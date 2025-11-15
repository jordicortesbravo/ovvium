package com.ovvium.services.web.controller.bff.v1.transfer.request.invoice;

import com.ovvium.services.web.controller.bff.v1.transfer.request.common.GetPageRequest;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class GetInvoiceCustomerPageRequest extends GetPageRequest {

	private UUID customerId;
	private LocalDate invoiceDate;

	public GetInvoiceCustomerPageRequest(Integer page, Integer size, UUID customerId, String invoiceDate) {
		super(page, size);	
		this.customerId = customerId;
		this.invoiceDate = invoiceDate == null ? null : LocalDate.parse(invoiceDate);
	}

	public Optional<LocalDate> getInvoiceDate() {
		return Optional.ofNullable(invoiceDate);
	}
}
