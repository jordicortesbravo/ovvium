package com.ovvium.services.web.controller.bff.v1.transfer.request.invoice;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

import static java.util.Collections.emptySet;

@Data
@Accessors(chain = true)
public class CreateInvoiceOrdersRequest {

	@NotNull
	private UUID customerId;
	@NotNull
	private UUID invoiceId;
	@NotNull
	private UUID billId;
	@NotEmpty
	private Set<UUID> orderIds = emptySet();
}
