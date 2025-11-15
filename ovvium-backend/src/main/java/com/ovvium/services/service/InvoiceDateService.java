package com.ovvium.services.service;

import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.payment.InvoiceDate;
import com.ovvium.services.web.controller.bff.v1.transfer.request.invoice.CreateInvoiceDateRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.invoice.UpdateInvoiceDateRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.invoice.InvoiceDatePageResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.invoice.InvoiceDateResponse;

import java.util.UUID;

public interface InvoiceDateService {

	ResourceIdResponse createInvoiceDate(CreateInvoiceDateRequest request);

	void updateInvoiceDate(UpdateInvoiceDateRequest setInvoiceDateId);

	InvoiceDate getCurrentInvoiceDate(Customer customer);

	InvoiceDateResponse getLastInvoiceDate(UUID customerId);

	InvoiceDatePageResponse pageInvoiceDates(UUID customerId, int month, int year);

}
