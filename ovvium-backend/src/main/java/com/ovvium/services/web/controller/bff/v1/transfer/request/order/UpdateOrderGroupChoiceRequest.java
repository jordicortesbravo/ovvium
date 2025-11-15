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
public class UpdateOrderGroupChoiceRequest {

	private UUID orderGroupChoiceId;
	private UUID productId;
	private String issueStatus;
	private String notes;
	private List<UUID> options = emptyList();

	public Optional<String> getNotes() {
		return Optional.ofNullable(notes);
	}

	public Optional<String> getIssueStatus() {
		return Optional.ofNullable(issueStatus);
	}

	public Optional<UUID> getProductId() {
		return Optional.ofNullable(productId);
	}


}
