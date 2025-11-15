package com.ovvium.services.web.controller.bff.v1.transfer.response.factory;

import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.payment.Invoice;
import com.ovvium.services.web.controller.bff.v1.transfer.response.customer.CustomerResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.invoice.InvoiceResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvoiceResponseFactory {

	private final UserResponseFactory userResponseFactory;
	private final CustomerResponseFactory customerResponseFactory;

	public InvoiceResponse create(Invoice invoice, Customer customer) {
		UserResponse userResponse = invoice.getUser().map(userResponseFactory::create).orElse(null);
		CustomerResponse customerResponse = customerResponseFactory.create(customer);
		return new InvoiceResponse(invoice, customerResponse, userResponse);
	}
}
