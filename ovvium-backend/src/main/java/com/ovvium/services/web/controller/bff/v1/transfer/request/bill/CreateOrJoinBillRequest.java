package com.ovvium.services.web.controller.bff.v1.transfer.request.bill;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class CreateOrJoinBillRequest {

	private UUID userId;
	private UUID customerId;
	private Set<UUID> locationIds;

	public Optional<UUID> getUserId() {
		return Optional.ofNullable(userId);
	}

}
