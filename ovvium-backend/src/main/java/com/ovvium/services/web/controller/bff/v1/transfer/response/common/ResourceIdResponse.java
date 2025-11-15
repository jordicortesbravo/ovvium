package com.ovvium.services.web.controller.bff.v1.transfer.response.common;

import com.ovvium.services.util.common.domain.Identifiable;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class ResourceIdResponse implements Serializable {

	private final UUID id;

	public ResourceIdResponse(Identifiable<UUID> identifiable) {
		this.id = identifiable.getId();
	}
}
