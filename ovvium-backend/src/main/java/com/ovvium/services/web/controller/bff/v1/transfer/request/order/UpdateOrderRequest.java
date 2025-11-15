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
public class UpdateOrderRequest {

	private UUID orderId;
	private String issueStatus;
	private String serviceTime;
	private String notes;
	private List<UpdateOrderGroupChoiceRequest> groupChoices = emptyList();
	private List<UUID> options = emptyList();

	public Optional<String> getIssueStatus() {
		return Optional.ofNullable(issueStatus);
	}

	public Optional<String> getServiceTime() {
		return Optional.ofNullable(serviceTime);
	}
	
	public Optional<String> getNotes() {
		return Optional.ofNullable(notes);
	}
}
