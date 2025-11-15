package com.ovvium.services.web.controller.bff.v1.transfer.request.customer;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Optional;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class UpdateLocationRequest {

	private UUID customerId;
	private UUID locationId;
	private UUID zoneId;
	private String tagId;
	private String serialNumber;
	private String description;
	private Integer position;
	private Boolean advancePayment;

	public Optional<UUID> getZoneId() {
		return Optional.ofNullable(zoneId);
	}

	public Optional<String> getTagId() {
		return Optional.ofNullable(tagId);
	}

	public Optional<String> getSerialNumber() {
		return Optional.ofNullable(serialNumber);
	}

	public Optional<String> getDescription() {
		return Optional.ofNullable(description);
	}

	public Optional<Integer> getPosition() {
		return Optional.ofNullable(position);
	}

	public Optional<Boolean> isAdvancePayment() {
		return Optional.ofNullable(advancePayment);
	}
}
