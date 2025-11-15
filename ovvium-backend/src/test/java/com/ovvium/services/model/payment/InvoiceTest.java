package com.ovvium.services.model.payment;

import com.ovvium.mother.builder.PaymentOrderAppCardBuilder;
import com.ovvium.mother.model.BillMother;
import com.ovvium.mother.model.InvoiceDateMother;
import com.ovvium.mother.model.OrderMother;
import com.ovvium.mother.model.PaymentOrderMother;
import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.bill.Order;
import com.ovvium.services.model.bill.PaymentStatus;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.exception.ErrorCode;
import com.ovvium.services.model.exception.OvviumDomainException;
import org.junit.Test;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ovvium.mother.model.InvoiceMother.anyInvoiceNumber;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InvoiceTest {

	@Test
	public void given_payment_order_when_create_invoice_then_should_create_invoice_correctly() {
		final PaymentOrderApp paymentOrder = new PaymentOrderAppCardBuilder()
				.setTipAmount(MoneyAmount.ofDouble(2))
				.setOrders(singleton(OrderMother.getOrderOfCerveza()))
				.build();
		final InvoiceDate invoiceDate = InvoiceDateMother.anyInvoiceDate(LocalDate.now());
		Invoice invoice = Invoice.ofPaymentOrder(anyInvoiceNumber(), invoiceDate, paymentOrder);

		assertThat(invoice.getPaymentOrder()).isNotEmpty();
		assertThat(invoice.getInvoiceDate()).isEqualTo(invoiceDate);
		assertThat(invoice.getOrders()).isEqualTo(paymentOrder.getOrders());
		assertThat(invoice.getCustomerId()).isEqualTo(paymentOrder.getBill().getCustomerId());
		assertThat(invoice.getBillId()).isEqualTo(paymentOrder.getBill().getId());
		assertThat(invoice.getTotalAmount()).isEqualTo(paymentOrder.getTotalAmount());
		assertThat(invoice.getTipAmount()).isEqualTo(paymentOrder.getTip().get().getAmount());
		assertThat(invoice.getUser()).contains(paymentOrder.getPayer());
	}

	@Test
	public void given_payment_order_and_null_invoice_number_when_create_invoice_then_should_throw_exception() {
		final PaymentOrderApp paymentOrder = PaymentOrderMother.anyPaymentOrderAppCard();
		final InvoiceDate invoiceDate = InvoiceDateMother.anyInvoiceDate(LocalDate.now());

		assertThatThrownBy(
				() -> Invoice.ofPaymentOrder(null, invoiceDate, paymentOrder)
		).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("InvoiceNumber can't be null");
	}

	@Test
	public void given_invoice_with_orders_when_get_orders_should_not_allow_modify_internal_orders() {
		final PaymentOrderApp paymentOrder = new PaymentOrderAppCardBuilder()
				.setTipAmount(MoneyAmount.ofDouble(2))
				.setOrders(singleton(OrderMother.getOrderOfCerveza()))
				.build();
		final InvoiceDate invoiceDate = InvoiceDateMother.anyInvoiceDate(LocalDate.now());
		Invoice invoice = Invoice.ofPaymentOrder(anyInvoiceNumber(), invoiceDate, paymentOrder);

		assertThatThrownBy(() -> invoice.getOrders().add(OrderMother.getOrderOfCerveza()))
				.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	public void given_invoice_draft_with_orders_when_add_paid_order_should_throw_exception() {
		final Bill bill = BillMother.getOpenedBillWithOpenOrder();
		final InvoiceDate invoiceDate = InvoiceDateMother.anyInvoiceDate(LocalDate.now());
		Invoice invoice = Invoice.ofBill(anyInvoiceNumber(), invoiceDate, bill, bill.getOrders().stream().map(Order::getId).collect(Collectors.toSet()));

		final Order paidOrder = OrderMother.getOrderOfCervezaBuilder()
				.setPaymentStatus(PaymentStatus.PAID)
				.build();

		assertThatThrownBy(() -> invoice.addOrders(singleton(paidOrder)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Order " + paidOrder.getId() + " is already paid");
	}

	@Test
	public void given_invoice_payment_order_with_orders_when_add_order_should_throw_exception() {
		final PaymentOrderApp paymentOrder = new PaymentOrderAppCardBuilder()
				.setTipAmount(MoneyAmount.ofDouble(2))
				.setOrders(singleton(OrderMother.getOrderOfCerveza()))
				.build();
		final InvoiceDate invoiceDate = InvoiceDateMother.anyInvoiceDate(LocalDate.now());
		Invoice invoice = Invoice.ofPaymentOrder(anyInvoiceNumber(), invoiceDate, paymentOrder);
		final Order newOrder = OrderMother.getOrderOfCerveza();

		assertThatThrownBy(() -> invoice.addOrders(singleton(newOrder)))
				.isInstanceOf(OvviumDomainException.class)
				.hasMessage(ErrorCode.INVOICE_CANNOT_BE_MODIFIED.getMessage());
	}

	@Test
	public void given_invoice_draft_with_orders_when_add_new_order_should_add_order_correctly() {
		final Bill bill = BillMother.getOpenedBillWithOpenOrder();
		final InvoiceDate invoiceDate = InvoiceDateMother.anyInvoiceDate(LocalDate.now());
		Invoice invoice = Invoice.ofBill(anyInvoiceNumber(), invoiceDate, bill, bill.getOrders().stream().map(Order::getId).collect(Collectors.toSet()));

		final Order newOrder = OrderMother.getOrderOfCervezaBuilder()
				.setId(UUID.randomUUID())
				.build();

		invoice.addOrders(singleton(newOrder));

		assertThat(invoice.getOrders()).hasSize(2);
		assertThat(invoice.getOrders()).contains(newOrder);
	}

	@Test
	public void given_paid_invoice_with_orders_when_add_new_order_should_throw_exception() {
		final Bill bill = BillMother.getOpenedBillWithOpenOrder();
		final InvoiceDate invoiceDate = InvoiceDateMother.anyInvoiceDate(LocalDate.now());
		Invoice invoice = Invoice.ofBill(anyInvoiceNumber(), invoiceDate, bill, bill.getOrders().stream().map(Order::getId).collect(Collectors.toSet()));
		new PaymentOrder(invoice, bill, PaymentType.CASH);

		final Order newOrder = OrderMother.getOrderOfCervezaBuilder()
				.setId(UUID.randomUUID())
				.build();

		assertThatThrownBy(() -> invoice.addOrders(singleton(newOrder)))
				.isInstanceOf(OvviumDomainException.class)
				.hasMessage(ErrorCode.INVOICE_CANNOT_BE_MODIFIED.getMessage());
	}

	@Test
	public void given_paid_invoice_with_orders_when_remove_order_should_throw_exception() {
		final Bill bill = BillMother.getOpenedBillWithOpenOrder();
		final InvoiceDate invoiceDate = InvoiceDateMother.anyInvoiceDate(LocalDate.now());
		Invoice invoice = Invoice.ofBill(anyInvoiceNumber(), invoiceDate, bill, bill.getOrders().stream().map(Order::getId).collect(Collectors.toSet()));
		new PaymentOrder(invoice, bill, PaymentType.CASH);

		final Order order = OrderMother.getOrderOfCervezaBuilder()
				.setId(UUID.randomUUID())
				.build();

		assertThatThrownBy(() -> invoice.removeOrder(order.getId()))
				.isInstanceOf(OvviumDomainException.class)
				.hasMessage(ErrorCode.INVOICE_CANNOT_BE_MODIFIED.getMessage());
	}

}