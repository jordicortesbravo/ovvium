package com.ovvium.services.service.impl;

import com.ovvium.services.app.config.properties.JwtProperties;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.customer.Employee;
import com.ovvium.services.model.exception.ErrorCode;
import com.ovvium.services.model.exception.OvviumDomainException;
import com.ovvium.services.model.user.SocialProvider;
import com.ovvium.services.model.user.User;
import com.ovvium.services.model.user.event.RecoveredPasswordEvent;
import com.ovvium.services.model.user.event.UserRegisteredEvent;
import com.ovvium.services.model.user.event.UserSocialLoggedEvent;
import com.ovvium.services.repository.CustomerRepository;
import com.ovvium.services.repository.UserRepository;
import com.ovvium.services.repository.client.social.FacebookClient;
import com.ovvium.services.repository.client.social.GoogleClient;
import com.ovvium.services.repository.client.social.SocialProfileDto;
import com.ovvium.services.security.AuthenticatedUser;
import com.ovvium.services.service.AccountService;
import com.ovvium.services.service.EventPublisherService;
import com.ovvium.services.util.ovvium.base.Preconditions;
import com.ovvium.services.util.ovvium.base.Validations;
import com.ovvium.services.web.controller.bff.v1.transfer.request.account.*;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.LoginResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.RecoverPasswordResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.RegisterResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.SessionResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.factory.AccountResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.ovvium.services.model.user.SocialProvider.APPLE;
import static com.ovvium.services.security.JwtUtil.*;
import static com.ovvium.services.security.exception.AccountError.*;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkIsPresent;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotBlank;
import static com.ovvium.services.util.ovvium.string.OvviumStringUtils.EMAIL_FAKE_DOMAIN;
import static com.ovvium.services.util.ovvium.string.OvviumStringUtils.createEmail;
import static java.lang.String.format;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

	private final JwtProperties jwtProperties;
	private final UserRepository userRepository;
	private final CustomerRepository customerRepository;
	private final FacebookClient facebookClient;
	private final GoogleClient googleClient;
	private final EventPublisherService eventPublisherService;
	private final AccountResponseFactory accountResponseFactory;

	@Override
	public RegisterResponse register(RegisterRequest request) {
		Preconditions.checkAllNotBlank("Email, name and password are required", //
				request.getEmail(), request.getName(), request.getPassword());
		if (userRepository.get(request.getEmail()).isPresent()) {
			throw new OvviumDomainException(ErrorCode.USER_ALREADY_EXISTS);
		}
		val user = User.basicUser(request.getName(), request.getEmail(), request.getPassword());
		save(user);
		eventPublisherService.emit(new UserRegisteredEvent(user));
		log.info("User {} registered successfully", user.getId());
		return new RegisterResponse(user.getId(), null);
	}

	@Override
	public RegisterResponse verify(VerifyUserRequest request) {
		Validations.validate(request);
		val user = userRepository.getOrFail(request.getUserId());
		user.verify(request.getActivationCode(), jwtProperties.getSecret());
		save(user);
		log.info("Verified user email for user {}", user.getId());
		return new RegisterResponse(user.getId(), generateTokens(user, null));
	}

	@Override
	@SneakyThrows
	public LoginResponse login(LoginRequest loginRequest) {
		checkNotBlank(loginRequest.getEmail(), "Email can't be empty", BadCredentialsException.class);
		checkNotBlank(loginRequest.getPassword(), "Password can't be empty", BadCredentialsException.class);
		val user = userRepository.get(loginRequest.getEmail())
				.orElseThrow(() -> new BadCredentialsException(INVALID_USER.name()));
		user.checkPassword(loginRequest.getPassword());
		SessionResponse sessionResponse = generateTokens(user, null);
		log.info("User {} logged in successfully", user.getId());
		return getLoginResponse(user, sessionResponse);
	}

	@Override
	public LoginResponse authorize(AuthorizeRequest request) {
		try {
			String email = request.getEmail().orElseThrow(() -> new IllegalArgumentException("Email is required"));
			userRepository.getOrFail(email);
		} catch (EntityNotFoundException | IllegalArgumentException e) {
			// Este caso hace referencia a que un usuario está intentando
			// logarse con una cuenta social, pero todavía no existe en el
			// sistema. En este caso, lo que haremos serà autoregistrarlo y
			// hacer login
			// FIXME Esto se puede mejorar con mejor uso de Optionals. Es confuso.
			return socialLogin(request);
		}
		if (request.getSocialProvider().isPresent()) {
			return socialLogin(request);
		}
		if (StringUtils.isNotBlank(request.getToken())) {
			// No hay que retornar el refreshToken y el accessToken ya que el
			// login viene de ellos y no se TIENEN que actualizar en ese momento
			AuthenticatedUser authenticatedUser = parseToken(request.getToken(), jwtProperties.getSecret());
			return accountResponseFactory.create(userRepository.getOrFail(authenticatedUser.getId()));
		}
		throw new BadCredentialsException(INVALID_TOKEN.name());
	}

	@Override
	public RecoverPasswordResponse recoverPassword(RecoverPasswordRequest recoverRequest) {
		User user = userRepository.getOrFail(recoverRequest.getEmail());
		eventPublisherService.emit(new RecoveredPasswordEvent(user));
		log.info("Recover password event for user {} was sent", user.getId());
		return accountResponseFactory.createRecoverPassword(user);
	}

	@Override
	public SessionResponse refreshAccessToken(String refreshToken) {
		val secret = jwtProperties.getSecret();
		// Si retorna un user en lugar de una excepción es que el refresh token
		// es correcto y por tanto podemos generar un accessToken
		val authUser = parseToken(refreshToken, secret);
		val user = userRepository.getOrFail(authUser.getId());
		val expires = Instant.now().plus(jwtProperties.getAccessDuration());
		val authenticatedUser = new AuthenticatedUser(user);
		val accessToken = generateAccessToken(authenticatedUser, secret, expires);
		log.info("Refreshed access token.");
		return new SessionResponse(refreshToken, accessToken, expires);
	}

	@Override
	public User getUser(UUID userId) {
		return userRepository.getOrFail(userId);
	}

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}

	@Override
	public SessionResponse generateTokens(User user, Employee employee) {
		val authenticatedUser = new AuthenticatedUser(user);
		Optional.ofNullable(employee).ifPresent(authenticatedUser::setEmployee);
		val secret = jwtProperties.getSecret();
		val refreshDuration = jwtProperties.getRefreshDuration();
		val accessDuration = jwtProperties.getAccessDuration();
		val now = Instant.now();
		val refreshToken = generateRefreshToken(authenticatedUser, secret, now.plus(refreshDuration));
		val expires = now.plus(accessDuration);
		val accessToken = generateAccessToken(authenticatedUser, secret, expires);
		return new SessionResponse(refreshToken, accessToken, expires);
	}

	@SneakyThrows
	private LoginResponse socialLogin(AuthorizeRequest request) {
		checkNotBlank(request.getToken(), "Token can't be empty", BadCredentialsException.class);
		val socialProvider = request.getSocialProvider()
				.orElseThrow(() -> new BadCredentialsException(SOCIAL_LOGIN_ERROR.name()));

		val socialProfileDto = getSocialProfileDto(request, socialProvider);

		val email = checkIsPresent(socialProfileDto.getEmail().or(request::getEmail), "Required email on Social Login");
		val name = checkIsPresent(Optional.ofNullable(socialProfileDto.getFullName().orElse(request.getName())), "Required name on Social Login");
		val id = checkIsPresent(socialProfileDto.getId(), "Required id on Social Login");
		if (socialProfileDto.getProfileImage().isEmpty()) {
			request.getProfileImage().map(URI::create).ifPresent(socialProfileDto::setProfileImage);
		}
		User user = save(
				userRepository.get(email)
						.orElseGet(() -> {
							if (socialProvider == APPLE) {
								return userRepository.getByAppleId(id)
										.orElseGet(() -> User.socialUser(name, email));
							} else {
								return User.socialUser(name, email);
							}
						})
						.setSocialProfile(socialProfileDto.getProvider(), email, id, name)
		);

		UserSocialLoggedEvent event = new UserSocialLoggedEvent(user);
		socialProfileDto.getProfileImage().ifPresent(event::setProfileImage);
		eventPublisherService.emit(event);
		log.info("{} Social login for user {} was successful", socialProvider, user.getId());
		return accountResponseFactory.create(user, generateTokens(user, null));
	}

	private SocialProfileDto getSocialProfileDto(AuthorizeRequest request, SocialProvider socialProvider) {
		SocialProfileDto dto;
		switch (socialProvider) {
			case FACEBOOK -> dto = facebookClient.getProfile(request.getToken());
			case GOOGLE -> dto = googleClient.getProfile(request.getToken());
			case APPLE -> {
				dto = new SocialProfileDto(APPLE);
				dto.setId(request.getId());
				dto.setEmail(request.getEmail().orElse(createEmail(request.getId(), EMAIL_FAKE_DOMAIN)));
				dto.setFullName(request.getName());
			}
			default -> throw new IllegalArgumentException("Social provider not managed: " + socialProvider);
		}
		return dto;
	}

	private LoginResponse getLoginResponse(User user, SessionResponse sessionResponse) {
		if (user.isCustomerAdmin()) {
			Customer customer = customerRepository.getCustomerByUser(user)
					.orElseThrow(() -> new IllegalStateException(format("User %s is not from a Customer.", user.getId())));
			return accountResponseFactory.create(user, customer, sessionResponse);
		}
		return accountResponseFactory.create(user, sessionResponse);
	}
}
