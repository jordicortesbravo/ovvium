package com.ovvium.services.model.user.apikey;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ApiKeyValueTest {

	@Test
	public void given_invalid_api_key_value_when_create_api_key_from_value_then_should_throw_exception() {
		String key = "wrongkeyvalue";

		assertThatThrownBy(() -> ApiKeyValue.ofKey(key))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Key length is not correct");
	}

	@Test
	public void given_valid_api_key_value_when_create_api_key_from_value_then_should_create_api_key() {
		String key = "329N44McG2t7KUTkXd6ixy1f9816yM6N";

		assertThatCode(() -> ApiKeyValue.ofKey(key))
				.doesNotThrowAnyException();
	}
}