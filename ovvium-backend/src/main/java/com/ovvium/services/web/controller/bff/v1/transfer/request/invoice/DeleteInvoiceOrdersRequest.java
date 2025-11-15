package com.ovvium.services.web.controller.bff.v1.transfer.request.invoice;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class DeleteInvoiceOrdersRequest {

	@NotNull
	private UUID customerId;
	@NotNull
	private UUID invoiceId;
	@NotNull
	private UUID orderId;
}
