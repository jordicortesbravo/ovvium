package com.ovvium.services.model.payment;

import com.ovvium.mother.model.BillMother;
import com.ovvium.mother.model.InvoiceMother;
import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.exception.OvviumDomainException;
import org.junit.Test;

import static com.ovvium.services.model.payment.PaymentType.APP_CARD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PaymentOrderTest {

	@Test
	public void given_invoice_and_bill_when_create_payment_order_then_should_create_payment_order_correctly() {
		Invoice invoice = InvoiceMother.anyInvoiceDraft();
		Bill bill = BillMother.getOpenedBillWithOpenOrder();

		PaymentOrder paymentOrder = new PaymentOrder(invoice, bill, PaymentType.CARD);

		assertThat(paymentOrder.getBill()).isEqualTo(bill);
	}

	@Test
	public void given_invoice_and_bill_when_create_payment_order_then_should_set_payment_order_to_invoice() {
		Invoice invoice = InvoiceMother.anyInvoiceDraft();
		Bill bill = BillMother.getOpenedBillWithOpenOrder();

		PaymentOrder paymentOrder = new PaymentOrder(invoice, bill, PaymentType.CARD);

		assertThat(invoice.getPaymentOrder()).contains(paymentOrder);
	}

	@Test
	public void given_already_paid_invoice_and_bill_when_create_payment_order_then_should_throw_exception() {
		Invoice invoice = InvoiceMother.anyInvoice();
		Bill bill = BillMother.getOpenedBillWithOpenOrder();

		assertThatThrownBy(() -> new PaymentOrder(invoice, bill, PaymentType.CARD))
				.isInstanceOf(OvviumDomainException.class)
				.hasMessage("This Invoice has been already paid.");
	}

	@Test
	public void given_invoice_and_wrong_payment_type_when_create_payment_order_then_should_throw_exception() {
		Invoice invoice = InvoiceMother.anyInvoice();
		Bill bill = BillMother.getOpenedBillWithOpenOrder();

		assertThatThrownBy(() -> new PaymentOrder(invoice, bill, APP_CARD))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Payment Type can be only CARD or CASH.");
	}


}
