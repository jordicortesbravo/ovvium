package com.ovvium.services.web.controller.bff.v1.transfer.request.customer;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class CreateLocationRequest {

	@NotNull
	private UUID customerId;
	@NotNull
	private UUID zoneId;
	@NotBlank
	private String tagId;
	@NotBlank
	private String serialNumber;

	private boolean advancePayment;
}
