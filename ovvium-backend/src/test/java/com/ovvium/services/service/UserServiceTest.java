package com.ovvium.services.service;

import com.ovvium.mother.model.BillMother;
import com.ovvium.mother.response.ResponseFactoryMother;
import com.ovvium.services.model.exception.ErrorCode;
import com.ovvium.services.model.exception.OvviumDomainException;
import com.ovvium.services.model.user.User;
import com.ovvium.services.model.user.event.UserPasswordChangedEvent;
import com.ovvium.services.repository.BillRepository;
import com.ovvium.services.repository.UserRepository;
import com.ovvium.services.repository.client.payment.PaymentClient;
import com.ovvium.services.security.AuthenticatedUser;
import com.ovvium.services.security.JwtAuthenticationToken;
import com.ovvium.services.service.impl.UserServiceImpl;
import com.ovvium.services.util.ovvium.domain.event.OvviumEvent;
import com.ovvium.services.web.controller.bff.v1.transfer.request.user.UpdateUserRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.user.UpdateUserRequest.ChangeUserPasswordRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static com.ovvium.services.util.ovvium.string.OvviumStringUtils.randomPassword;
import static com.ovvium.utils.MockitoUtils.mockRepository;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

	// SUT
	private UserService userService;

	private PictureService pictureService;
	private EventPublisherService eventPublisherService;
	private UserRepository userRepository;
	private BillRepository billRepository;
	private PaymentClient paymentClient;

	@Before
	public void setUp() throws Exception {
		pictureService = mock(PictureService.class);
		eventPublisherService = mock(EventPublisherService.class);
		userRepository = mockRepository(UserRepository.class);
		billRepository = mockRepository(BillRepository.class);
		paymentClient = mock(PaymentClient.class);
		userService = new UserServiceImpl(pictureService, userRepository, ResponseFactoryMother.anUserResponseFactory(), eventPublisherService, billRepository, paymentClient);
	}

	@Test
	public void given_update_user_change_password_when_update_password_should_change_user_password() {
		final User user = User.basicUser("Jorge", "jp@ovvium.com", "oldpassword");

		when(userRepository.getOrFail(user.getId())).thenReturn(user);

		userService.updateUser(new UpdateUserRequest()
				.setUserId(user.getId())
				.setPassword(new ChangeUserPasswordRequest()
						.setOldPassword("oldpassword")
						.setNewPassword("newpassword"))
		);

		assertThatCode(() -> {
			user.checkPassword("newpassword");
		}).doesNotThrowAnyException();
	}

	@Test
	public void given_update_user_change_password_when_update_password_should_emit_user_password_changed_event() {
		final User user = User.basicUser("Jorge", "jp@ovvium.com", "oldpassword");

		when(userRepository.getOrFail(user.getId())).thenReturn(user);

		userService.updateUser(new UpdateUserRequest()
				.setUserId(user.getId())
				.setPassword(new ChangeUserPasswordRequest()
						.setOldPassword("oldpassword")
						.setNewPassword("newpassword"))
		);

		final ArgumentCaptor<OvviumEvent> captor = ArgumentCaptor.forClass(OvviumEvent.class);
		verify(eventPublisherService, times(1)).emit(captor.capture());

		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue()).isExactlyInstanceOf(UserPasswordChangedEvent.class);
	}

	@Test
	public void given_user_when_remove_user_should_remove_user_tokens_with_payment_client() {
		final User user = User.basicUser("User", "user@email.com", randomPassword());
		user.addUserPciDetail("user-id", "user-token");

		SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(new AuthenticatedUser(user), null));
		when(userRepository.getOrFail(user.getId())).thenReturn(user);

		userService.removeCurrentUser();

		verify(paymentClient, times(1)).removeUserToken(any());
	}

	@Test
	public void given_user_with_pending_bill_when_remove_user_should_throw_exception() {
		final User user = User.basicUser("User", "user@email.com", randomPassword());

		SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(new AuthenticatedUser(user), null));
		when(userRepository.getOrFail(user.getId())).thenReturn(user);
		when(billRepository.getLastBillOfUser(user.getId())).thenReturn(Optional.of(BillMother.getOpenedBillWithOpenOrder()));

		assertThatThrownBy(() -> userService.removeCurrentUser())
				.isInstanceOf(OvviumDomainException.class)
				.hasMessage(ErrorCode.USER_NOT_REMOVABLE_BILL_OPEN.getMessage());
	}

	@Test
	public void given_user_when_remove_user_should_call_anonymize_method() {
		final User user = User.basicUser("User", "user@email.com", randomPassword());
		final User spiedUser = Mockito.spy(user);

		SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(new AuthenticatedUser(spiedUser), null));
		when(userRepository.getOrFail(user.getId())).thenReturn(spiedUser);

		userService.removeCurrentUser();

		verify(spiedUser, times(1)).anonymize();
	}
}