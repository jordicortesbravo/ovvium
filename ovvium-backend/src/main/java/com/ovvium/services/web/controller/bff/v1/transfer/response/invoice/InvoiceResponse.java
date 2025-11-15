package com.ovvium.services.web.controller.bff.v1.transfer.response.invoice;

import com.ovvium.services.model.payment.Invoice;
import com.ovvium.services.model.payment.PaymentOrder;
import com.ovvium.services.model.payment.PaymentType;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.MoneyAmountResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.customer.CustomerResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.user.UserResponse;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public final class InvoiceResponse extends ResourceIdResponse {

	private final String invoiceNumber;
	private final CustomerResponse customer;
	private final UserResponse user;
	private final PaymentType paymentType;
	private final List<OrderInvoiceResponse> orders;
	private final MoneyAmountResponse tipAmount;
	private final MoneyAmountResponse totalAmount;
	private final MoneyAmountResponse totalBaseAmount;
	private final boolean draft;
	private final String invoiceDate;
	private final String creationDate;

	public InvoiceResponse(Invoice invoice, CustomerResponse customerResponse, UserResponse userResponse) {
		super(invoice);
		this.invoiceNumber = invoice.getInvoiceNumber().getValue();
		this.customer = customerResponse;
		this.user = userResponse;
		this.paymentType = invoice.getPaymentOrder().map(PaymentOrder::getPaymentType).orElse(null);
		this.orders = invoice.getOrders().stream().map(OrderInvoiceResponse::new).collect(Collectors.toList());
		this.tipAmount = new MoneyAmountResponse(invoice.getTipAmount());
		this.totalAmount = new MoneyAmountResponse(invoice.getTotalAmount());
		this.totalBaseAmount = new MoneyAmountResponse(invoice.getTotalBaseAmount());
		this.draft = invoice.isDraft();
		this.creationDate = invoice.getCreated().toString();
		this.invoiceDate = invoice.getInvoiceDate().getDate().toString();
	}

}
