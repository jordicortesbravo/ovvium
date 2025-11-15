package com.ovvium.services.web.controller.bff.v1.transfer.response.invoice;

import com.ovvium.services.model.payment.Invoice;
import com.ovvium.services.util.common.domain.Page;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.AbstractPageResponse;
import lombok.Getter;

import java.util.List;

@Getter
public final class InvoicePageResponse extends AbstractPageResponse<InvoiceResponse> {

	public InvoicePageResponse(Page<Invoice> page, List<InvoiceResponse> invoiceResponses) {
		super(page, invoiceResponses);
	}

}
