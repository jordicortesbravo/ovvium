package com.ovvium.mother.builder;

import com.ovvium.mother.model.BillMother;
import com.ovvium.mother.model.InvoiceDateMother;
import com.ovvium.mother.model.InvoiceMother;
import com.ovvium.mother.model.PaymentOrderMother;
import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.bill.Order;
import com.ovvium.services.model.payment.Invoice;
import com.ovvium.services.model.payment.InvoiceDate;
import com.ovvium.services.model.payment.InvoiceNumber;
import com.ovvium.services.model.payment.PaymentOrder;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ovvium.mother.model.InvoiceMother.INVOICE_ID;

@Setter
@Accessors(chain = true)
public class InvoiceBuilder {

	private UUID id = INVOICE_ID;
	private InvoiceDate invoiceDate = InvoiceDateMother.anyInvoiceDate(LocalDate.now());
	private Bill bill = BillMother.getOpenedBillWithMultipleOrders();
	private Set<UUID> orderIds = Collections.emptySet();
	private PaymentOrder paymentOrder = PaymentOrderMother.anyPaymentOrderAppCard();
	private InvoiceNumber invoiceNumber = InvoiceMother.anyInvoiceNumber();

	public Invoice buildDraft() {
		Set<UUID> invoiceOrderIds = orderIds.isEmpty() ? bill.getOrders().stream().map(Order::getId).collect(Collectors.toSet()) : orderIds;
		Invoice invoice = Invoice.ofBill(invoiceNumber, invoiceDate, bill, invoiceOrderIds);
		ReflectionUtils.set(invoice, "id", id);
		return invoice;
	}

	public Invoice build() {
		Invoice invoice = Invoice.ofPaymentOrder(invoiceNumber, invoiceDate, paymentOrder);
		ReflectionUtils.set(invoice, "id", id);
		return invoice;
	}

}
