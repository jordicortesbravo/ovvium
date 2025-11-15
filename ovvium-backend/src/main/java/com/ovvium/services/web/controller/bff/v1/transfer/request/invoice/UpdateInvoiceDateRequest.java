package com.ovvium.services.web.controller.bff.v1.transfer.request.invoice;

import com.ovvium.services.model.payment.InvoiceDateStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Optional;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class UpdateInvoiceDateRequest {

	private UUID invoiceDateId;
	private UUID customerId;
	private InvoiceDateStatus status;


	public Optional<InvoiceDateStatus> getStatus() {
		return Optional.ofNullable(status);
	}
}
