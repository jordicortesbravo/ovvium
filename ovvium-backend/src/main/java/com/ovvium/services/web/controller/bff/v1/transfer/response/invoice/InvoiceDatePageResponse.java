package com.ovvium.services.web.controller.bff.v1.transfer.response.invoice;

import com.ovvium.services.model.payment.InvoiceDate;
import com.ovvium.services.util.common.domain.Page;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.AbstractPageResponse;
import lombok.Getter;

import java.util.List;

@Getter
public final class InvoiceDatePageResponse extends AbstractPageResponse<InvoiceDateResponse> {

	public InvoiceDatePageResponse(Page<InvoiceDate> page, List<InvoiceDateResponse> invoiceResponses) {
		super(page, invoiceResponses);
	}

}
