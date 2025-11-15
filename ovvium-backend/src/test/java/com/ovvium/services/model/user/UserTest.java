package com.ovvium.services.model.user;

import com.ovvium.services.model.exception.ErrorCode;
import com.ovvium.services.model.exception.OvviumDomainException;
import org.junit.Test;
import org.springframework.security.authentication.BadCredentialsException;

import static com.ovvium.services.util.ovvium.string.OvviumStringUtils.randomPassword;
import static org.assertj.core.api.Assertions.*;

public class UserTest {

	private static final String A_USER_ID = "a_user_id";
	private static final String A_REFERENCE_TOKEN = "a_reference_token";

	@Test
	public void given_new_user_name_when_create_then_should_normalize_name() {
		User user = User.basicUser(" Gandalf  El  Blanco ", "jp@ovvium.com", "password123");

		assertThat(user.getName()).isEqualTo("Gandalf El Blanco");
	}

	@Test
	public void given_new_user_name_when_get_first_name_and_surnames_then_should_return_correct_values() {
		User user = User.basicUser(" Gandalf  El  Blanco ", "jp@ovvium.com", "password123");

		assertThat(user.getFirstName()).isEqualTo("Gandalf");
		assertThat(user.getSurnames()).isEqualTo("El Blanco");
	}

	@Test
	public void given_new_user_name_without_surnames_when_get_first_name_and_surnames_then_should_return_correct_values() {
		User user = User.basicUser(" Gandalf ", "jp@ovvium.com", "password123");

		assertThat(user.getFirstName()).isEqualTo("Gandalf");
		assertThat(user.getSurnames()).isEqualTo("");
	}

	@Test
	public void given_new_user_pci_details_when_add_pci_detail_then_must_create_new_user_pci_details() {
		User userJorge = User.basicUser("Jorge", "jp@ovvium.com", "password123");

		UserPciDetails userPciDetails = userJorge.addUserPciDetail(A_USER_ID, A_REFERENCE_TOKEN);

		assertThat(userJorge.getPciDetails()).hasSize(1);
		assertThat(userJorge.getPciDetails()).contains(userPciDetails);
	}

	@Test
	public void given_basic_user_data_and_empty_password_when_create_user_should_throw_exception() {
		assertThatThrownBy(() -> User.basicUser("Name", "jp@ovvium.com", ""))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Password can't be blank");
	}

	@Test
	public void given_customer_user_data_and_empty_password_when_create_user_should_throw_exception() {
		assertThatThrownBy(() -> User.adminCustomerUser("Name", "jp@ovvium.com", ""))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Password can't be blank");
	}

	@Test
	public void given_user_wrong_old_password_and_new_password_when_change_user_password_should_throw_exception() {
		User user = User.basicUser("Name", "jp@gmail.com", "oldpassword");

		assertThatThrownBy(() ->
				user.changePassword("wrongpassword", "newpassword")
		).isInstanceOf(BadCredentialsException.class)
				.hasMessage("INVALID_PASSWORD");
	}

	@Test
	public void given_user_correct_old_password_and_new_password_when_change_user_password_should_set_new_password() {
		String oldPassword = "oldpassword";
		String newPassword = "newpassword";
		User user = User.basicUser("Name", "jp@gmail.com", oldPassword);

		user.changePassword(oldPassword, newPassword);

		assertThatCode(() ->
				user.checkPassword(newPassword)
		).doesNotThrowAnyException();
	}

	@Test
	public void given_user_not_strong_password_when_change_user_password_should_throw_exception() {
		String oldPassword = "oldpassword";
		String newPassword = "short";
		User user = User.basicUser("Name", "jp@gmail.com", oldPassword);

		assertThatThrownBy(() ->
				user.changePassword(oldPassword, newPassword)
		).isInstanceOf(OvviumDomainException.class)
				.hasMessage(ErrorCode.PASSWORD_TOO_SHORT.getMessage());
	}

	@Test
	public void given_user_when_generate_user_password_then_check_is_set_correctly() {
		User user = User.basicUser("Name", "jp@gmail.com", "oldpassword");

		String newPassword = user.generatePassword();

		assertThatCode(() ->
				user.checkPassword(newPassword)
		).doesNotThrowAnyException();
	}

	@Test
	public void given_social_user_when_check_user_password_should_throw_exception() {
		User user = User.socialUser("Name", "jp@gmail.com");

		assertThatThrownBy(() ->
				user.checkPassword("anypassword")
		).isInstanceOf(IllegalStateException.class)
				.hasMessage("Missing password.");
	}

	@Test
	public void given_user_when_anonymize_then_should_anonyize_data_correctly() {
		String password = randomPassword();
		User user = User.basicUser("User", "user@email.com", password);
		user.addUserPciDetail("user", "token");

		user.anonymize();

		assertThat(user.getName()).isEqualTo("UNKNOWN");
		assertThat(user.getPciDetails()).isEmpty();
		assertThat(user.getEmail()).isNotEqualTo("user@email.com");
		assertThat(user.isEnabled()).isFalse();
		assertThatThrownBy(() -> {
			user.checkPassword(password);
		}).isInstanceOf(BadCredentialsException.class);
		assertThat(user.getAllergens()).isEmpty();
		assertThat(user.getFoodPreferences()).isEmpty();
		assertThat(user.getFacebookProfile()).isNull();
		assertThat(user.getPicture()).isEmpty();
	}

}