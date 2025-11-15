package com.ovvium.services.web.controller.bff.v1.transfer.response.invoice;

import com.ovvium.services.model.payment.InvoiceDate;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import lombok.Getter;

@Getter
public final class InvoiceDateResponse extends ResourceIdResponse {

	private final String date;
	private final String status;

	public InvoiceDateResponse(InvoiceDate invoiceDate) {
		super(invoiceDate);
		this.date = invoiceDate.getDate().toString();
		this.status = invoiceDate.getStatus().toString();
	}

}
