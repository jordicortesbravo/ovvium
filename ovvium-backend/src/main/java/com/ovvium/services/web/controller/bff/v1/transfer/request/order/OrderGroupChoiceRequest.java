package com.ovvium.services.web.controller.bff.v1.transfer.request.order;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Optional;
import java.util.UUID;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class OrderGroupChoiceRequest {

	private UUID productId;
	private String notes;

	public Optional<String> getNotes() {
		return Optional.ofNullable(notes);
	}
}
