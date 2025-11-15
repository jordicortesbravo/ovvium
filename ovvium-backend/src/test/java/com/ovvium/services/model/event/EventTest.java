package com.ovvium.services.model.event;

import com.ovvium.mother.model.OvviumEventMother;
import com.ovvium.services.util.ovvium.domain.event.OvviumEvent;
import org.junit.Test;

import static com.ovvium.mother.model.OvviumEventMother.anyEvent;
import static com.ovvium.mother.model.UserMother.USER_JORGE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EventTest {

	private final static String ANY_REQUEST = "/customers";

	@Test
	public void given_null_dto_when_create_event_then_throw_exception() {
		assertThatThrownBy(
				() -> new Event(null)
		).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Event object cannot be null");
	}

	@Test
	public void given_dto_when_create_event_then_create_event_with_correct_fields() {
		final OvviumEvent anyDto = anyEvent();
		anyDto.setRequestPath(ANY_REQUEST);
		anyDto.setRequestUserId(USER_JORGE_ID);

		Event event = new Event(anyDto);

		assertThat(event.getPath()).contains(ANY_REQUEST);
		assertThat(event.getUserId()).contains(USER_JORGE_ID);
		assertThat(((OvviumEventMother.AnyDto) event.asOvviumEvent()).getId()).isEqualTo(((OvviumEventMother.AnyDto) anyDto).getId());
	}

}