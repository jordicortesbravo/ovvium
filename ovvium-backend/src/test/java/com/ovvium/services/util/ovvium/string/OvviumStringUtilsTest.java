package com.ovvium.services.util.ovvium.string;

import com.ovvium.services.model.user.User;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OvviumStringUtilsTest {

	@Test
	public void given_password_when_generate_random_password_has_minimum_length() {
		String randomPassword = OvviumStringUtils.randomPassword();

		assertThat(randomPassword).hasSize(User.MINIMUM_PASSWORD);
	}
}