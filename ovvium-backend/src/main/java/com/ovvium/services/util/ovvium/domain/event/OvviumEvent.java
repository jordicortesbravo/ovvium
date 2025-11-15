package com.ovvium.services.util.ovvium.domain.event;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

public interface OvviumEvent extends Serializable {

	Optional<UUID> getRequestUserId();

	Optional<String> getRequestPath();

	void setRequestUserId(UUID requestUserId);

	void setRequestPath(String requestPath);
}
