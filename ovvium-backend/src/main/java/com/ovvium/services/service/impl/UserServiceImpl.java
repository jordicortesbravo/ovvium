package com.ovvium.services.service.impl;

import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.exception.OvviumDomainException;
import com.ovvium.services.model.user.User;
import com.ovvium.services.model.user.event.UserPasswordChangedEvent;
import com.ovvium.services.repository.BillRepository;
import com.ovvium.services.repository.UserRepository;
import com.ovvium.services.repository.client.payment.PaymentClient;
import com.ovvium.services.repository.client.payment.dto.RemoveUserTokenRequest;
import com.ovvium.services.security.JwtUtil;
import com.ovvium.services.service.EventPublisherService;
import com.ovvium.services.service.PictureService;
import com.ovvium.services.service.UserService;
import com.ovvium.services.web.controller.bff.v1.transfer.request.user.UpdateUserRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.factory.UserResponseFactory;
import com.ovvium.services.web.controller.bff.v1.transfer.response.user.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.ovvium.services.model.exception.ErrorCode.USER_NOT_REMOVABLE_BILL_OPEN;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final PictureService pictureService;
	private final UserRepository userRepository;
	private final UserResponseFactory userResponseFactory;
	private final EventPublisherService eventPublisherService;
	private final BillRepository billRepository;
	private final PaymentClient paymentClient;

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}

	@Override
	public User getUserOrFail(UUID userId) {
		return userRepository.getOrFail(userId);
	}

	@Override
	public void updateUser(UpdateUserRequest request) {
		val user = userRepository.getOrFail(request.getUserId());
		request.getName().ifPresent(user::setName);
		request.getAllergens().ifPresent(user::setAllergens);
		request.getFoodPreferences().ifPresent(user::setFoodPreferences);
		request.getPictureId().map(pictureService::getOrFail).ifPresent(user::setPicture);
		request.getPassword().ifPresent(pw -> changePassword(user, pw));
		save(user);
	}

	@Override
	public UserProfileResponse getCurrentUser() {
		return userResponseFactory.createUserProfile(getAuthenticatedUser());
	}

	@Override
	public void removeCurrentUser() {
		val user = getAuthenticatedUser();
		billRepository.getLastBillOfUser(user.getId())
				.filter(Bill::isOpen)
				.ifPresent((bill) -> {
					throw new OvviumDomainException(USER_NOT_REMOVABLE_BILL_OPEN);
				});
		user.getPciDetails().stream()
				.map(it -> new RemoveUserTokenRequest(user, it.getProviderUserId(), it.getProviderReferenceToken()))
				.forEach(paymentClient::removeUserToken);
		user.anonymize();
		save(user);
		log.info("User {} removed succesfully", user.getId());
	}

	/**
	 * Gets the Authenticated User. It takes the User DTO from SecurityContext and
	 * retrieves the full user from DB.
	 */
	@Override
	public User getAuthenticatedUser() {
		val currentUserDto = JwtUtil.getAuthenticatedUserOrFail();
		return userRepository.getOrFail(currentUserDto.getId());
	}

	private void changePassword(User user, UpdateUserRequest.ChangeUserPasswordRequest passwordRequest) {
		user.changePassword(passwordRequest.getOldPassword(), passwordRequest.getNewPassword());
		eventPublisherService.emit(new UserPasswordChangedEvent(user));
	}

}
