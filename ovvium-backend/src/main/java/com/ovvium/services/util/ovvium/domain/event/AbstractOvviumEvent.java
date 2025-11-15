package com.ovvium.services.util.ovvium.domain.event;

import lombok.Data;

import java.util.Optional;
import java.util.UUID;

@Data
public abstract class AbstractOvviumEvent implements OvviumEvent {

	private UUID requestUserId;
	private String requestPath;

	@Override
	public Optional<UUID> getRequestUserId() {
		return Optional.ofNullable(requestUserId);
	}

	@Override
	public Optional<String> getRequestPath() {
		return Optional.ofNullable(requestPath);
	}

}
