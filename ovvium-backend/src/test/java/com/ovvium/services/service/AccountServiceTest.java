package com.ovvium.services.service;

import com.ovvium.mother.builder.EmployeeMother;
import com.ovvium.mother.model.UserMother;
import com.ovvium.mother.response.ResponseFactoryMother;
import com.ovvium.services.app.config.properties.JwtProperties;
import com.ovvium.services.model.customer.Employee;
import com.ovvium.services.model.user.User;
import com.ovvium.services.repository.CustomerRepository;
import com.ovvium.services.repository.UserRepository;
import com.ovvium.services.repository.client.social.FacebookClient;
import com.ovvium.services.repository.client.social.GoogleClient;
import com.ovvium.services.security.AuthenticatedUser;
import com.ovvium.services.security.JwtUtil;
import com.ovvium.services.security.exception.ExpiredTokenException;
import com.ovvium.services.service.impl.AccountServiceImpl;
import com.ovvium.services.web.controller.bff.v1.transfer.request.account.LoginRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.LoginResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.SessionResponse;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.Optional;

import static com.ovvium.services.util.ovvium.string.OvviumStringUtils.randomPassword;
import static com.ovvium.utils.MockitoUtils.mockRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountServiceTest {

	// SUT
	private AccountService accountService;

	private JwtProperties jwtProperties;
	private UserRepository userRepository;

	@Before
	public void setUp() {
		jwtProperties = mock(JwtProperties.class);
		userRepository = mockRepository(UserRepository.class);
		accountService = new AccountServiceImpl(
				jwtProperties,
				userRepository,
				mockRepository(CustomerRepository.class),
				mock(FacebookClient.class),
				mock(GoogleClient.class),
				mock(EventPublisherService.class),
				ResponseFactoryMother.anAccountResponseFactory()
		);
	}

	@Test
	public void given_not_enabled_user_credentials_when_login_then_should_generate_tokens() {
		var password = randomPassword();
		var email = "test@ovvium.com";
		var user = User.basicUser("test", email, password);

		mockJwtProperties();
		when(userRepository.get(email)).thenReturn(Optional.of(user));

		LoginResponse loginResponse = accountService.login(new LoginRequest().setEmail(email).setPassword(password));

		var session = loginResponse.getSession();
		assertThat(session).isNotNull();
		assertThat(session.getAccessToken()).isNotNull();
	}

	@Test
	public void given_user_and_employee_when_generate_tokens_then_should_generate_token_with_employee_data() {
		final User user = UserMother.getCustomerUserFAdria();
		final Employee employee = EmployeeMother.getAnyEmployee();

		mockJwtProperties();

		SessionResponse sessionResponse = accountService.generateTokens(user, employee);

		final AuthenticatedUser authenticatedUser = JwtUtil.parseToken(sessionResponse.getAccessToken(), "secret");
		assertThat(authenticatedUser.getEmployeeUser()).isNotEmpty();

		final AuthenticatedUser.EmployeeUser employeeUser = authenticatedUser.getEmployeeUser().get();
		assertThat(employeeUser.getId()).isEqualTo(employee.getId());
		assertThat(employeeUser.getName()).isEqualTo(employee.getName());
		assertThat(authenticatedUser.getRoles()).isEqualTo(employee.getRoles());
	}

	@Test
	public void given_user_and_not_expired_tokens_when_generate_tokens_then_should_return_authenticated_user() {
		final User user = UserMother.getCustomerUserFAdria();

		mockJwtProperties();

		SessionResponse sessionResponse = accountService.generateTokens(user, null);

		final AuthenticatedUser authenticatedUser = JwtUtil.parseToken(sessionResponse.getAccessToken(), "secret");
		assertThat(authenticatedUser.getId()).isEqualTo(user.getId());
	}

	@Test
	public void given_user_and_expired_refresh_token_when_refresh_token_then_should_throw_exception() throws InterruptedException {
		final User user = UserMother.getCustomerUserFAdria();
		when(jwtProperties.getSecret()).thenReturn("secret");
		when(jwtProperties.getAccessDuration()).thenReturn(Duration.ofDays(0));
		when(jwtProperties.getRefreshDuration()).thenReturn(Duration.ofDays(0));
		when(userRepository.getOrFail(user.getId())).thenReturn(user);
		final String refreshToken = accountService.generateTokens(user, null).getRefreshToken();
		Thread.sleep(200);

		assertThatThrownBy(() -> accountService.refreshAccessToken(refreshToken))
				.isInstanceOf(ExpiredTokenException.class);
	}

	@Test
	public void given_refresh_token_when_refresh_token_then_should_return_same_refresh_token() throws InterruptedException {
		final User user = UserMother.getCustomerUserFAdria();
		when(jwtProperties.getSecret()).thenReturn("secret");
		when(jwtProperties.getAccessDuration()).thenReturn(Duration.ofDays(0));
		when(jwtProperties.getRefreshDuration()).thenReturn(Duration.ofDays(1));
		when(userRepository.getOrFail(user.getId())).thenReturn(user);
		SessionResponse tokens = accountService.generateTokens(user, null);
		final String accessToken = tokens.getAccessToken();
		final String refreshToken = tokens.getRefreshToken();
		Thread.sleep(100);

		SessionResponse sessionResponse = accountService.refreshAccessToken(refreshToken);

		assertThat(sessionResponse.getAccessToken()).isNotEqualTo(accessToken);
		assertThat(sessionResponse.getRefreshToken()).isEqualTo(refreshToken);
		assertThat(sessionResponse.getLoggedUntil()).isNotEqualTo(tokens.getLoggedUntil());
	}

	private void mockJwtProperties() {
		when(jwtProperties.getSecret()).thenReturn("secret");
		when(jwtProperties.getAccessDuration()).thenReturn(Duration.ofDays(1));
		when(jwtProperties.getRefreshDuration()).thenReturn(Duration.ofDays(30));
	}
}