package com.ovvium.services.model.customer;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TagIdTest {

	@Test
	public void given_wrong_tag_id_format_when_new_tag_id_then_should_throw_exception() {
		String id = "aaaaa";

		assertThatThrownBy(() -> {
			new TagId(id);
		}).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("This id is not a TagId");
	}

	@Test
	public void given_wrong_tag_id_characters_when_new_tag_id_then_should_throw_exception() {
		String id = "123456789.";

		assertThatThrownBy(() -> {
			new TagId(id);
		}).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("This id is not a TagId");
	}

	@Test
	public void given_generate_random_tag_id_characters_when_new_tag_id_then_should_create_correct_tag_id() {
		TagId randomTagId = TagId.randomTagId();

		assertThatCode(() -> {
			new TagId(randomTagId.getValue());
		}).doesNotThrowAnyException();
	}
}