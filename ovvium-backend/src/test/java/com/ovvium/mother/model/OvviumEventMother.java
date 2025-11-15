package com.ovvium.mother.model;

import com.ovvium.services.util.ovvium.domain.event.AbstractOvviumEvent;
import com.ovvium.services.util.ovvium.domain.event.OvviumEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class OvviumEventMother {

	@Data
	@EqualsAndHashCode(callSuper = false)
	public static class AnyDto extends AbstractOvviumEvent {
		private final UUID id;
		private final List<UUID> otherIds;
	}

	public static OvviumEvent anyEvent() {
		return new OvviumEventMother.AnyDto(UUID.randomUUID(), Collections.singletonList(UUID.randomUUID()));
	}

}
