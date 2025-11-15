package com.ovvium.services.web.controller.bff.v1.transfer.response.invoice;

import com.ovvium.services.model.bill.Order;
import com.ovvium.services.model.bill.PaymentStatus;
import com.ovvium.services.transfer.response.common.MultiLangStringResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.MoneyAmountResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import lombok.Getter;
import lombok.val;

@Getter
public final class OrderInvoiceResponse extends ResourceIdResponse {

	private final PaymentStatus paymentStatus;
	private final MoneyAmountResponse price;
	private final MoneyAmountResponse basePrice;
	private final double tax;
	private final MultiLangStringResponse productName;

	public OrderInvoiceResponse(Order order) {
		super(order);
		val product = order.getProduct();
		this.productName = new MultiLangStringResponse(product.getName());
		this.price = new MoneyAmountResponse(order.getPrice());
		this.basePrice = new MoneyAmountResponse(product.getBasePrice());
		this.tax = product.getTax();
		this.paymentStatus = order.getPaymentStatus();
	}

}
