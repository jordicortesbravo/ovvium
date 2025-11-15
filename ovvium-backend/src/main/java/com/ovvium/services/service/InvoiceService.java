package com.ovvium.services.service;

import com.ovvium.services.model.payment.Invoice;
import com.ovvium.services.model.payment.PaymentOrder;
import com.ovvium.services.web.controller.bff.v1.transfer.request.common.GetPageRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.invoice.CreateInvoiceDraftRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.invoice.CreateInvoiceOrdersRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.invoice.DeleteInvoiceOrdersRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.invoice.GetInvoiceCustomerPageRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.invoice.InvoicePageResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.invoice.InvoiceResponse;

import java.util.UUID;

public interface InvoiceService {

	Invoice createInvoice(PaymentOrder paymentOrder);

	InvoicePageResponse getInvoicesOfCustomer(GetInvoiceCustomerPageRequest getPageRequest);

	InvoiceResponse getInvoiceResponse(UUID invoiceId);

	Invoice getInvoice(UUID invoiceId);

	InvoicePageResponse getInvoicesOfCurrentUser(GetPageRequest request);

	ResourceIdResponse createInvoiceDraft(CreateInvoiceDraftRequest request);

	void addOrdersToInvoice(CreateInvoiceOrdersRequest request);

	void removeOrderFromInvoice(DeleteInvoiceOrdersRequest request);

}
