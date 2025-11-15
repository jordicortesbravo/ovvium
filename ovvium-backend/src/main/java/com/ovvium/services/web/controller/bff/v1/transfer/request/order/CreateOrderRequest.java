package com.ovvium.services.web.controller.bff.v1.transfer.request.order;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class CreateOrderRequest {

	private UUID userId;
	private UUID productId;
	private String serviceTime;
	private String notes;
	private List<OrderGroupChoiceRequest> groupChoices = emptyList();
	private List<UUID> selectedOptions = emptyList();

	public Optional<String> getNotes() {
		return Optional.ofNullable(notes);
	}

	public Optional<UUID> getUserId() {
		return Optional.ofNullable(userId);
	}

	public Optional<String> getServiceTime() {
		return Optional.ofNullable(serviceTime);
	}
}
