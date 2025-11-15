package com.ovvium.services.web.controller.bff.v1.transfer.request.customer;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class CreateZoneRequest {

	@NotNull
	private UUID customerId;
	@NotEmpty
	private String name;
}
