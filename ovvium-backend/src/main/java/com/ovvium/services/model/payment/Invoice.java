package com.ovvium.services.model.payment;

import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.bill.Order;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.exception.OvviumDomainException;
import com.ovvium.services.model.exception.ResourceNotFoundException;
import com.ovvium.services.model.user.User;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.val;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.*;

import static com.ovvium.services.model.common.MoneyAmount.ZERO;
import static com.ovvium.services.model.exception.ErrorCode.INVOICE_CANNOT_BE_MODIFIED;
import static com.ovvium.services.util.ovvium.base.Preconditions.check;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Accessors(chain = true)
public class Invoice extends BaseEntity {

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "invoice_number"))
	private InvoiceNumber invoiceNumber;

	@Type(type = "pg-uuid")
	private UUID customerId;

	@Type(type = "pg-uuid")
	private UUID billId;

	@ManyToOne
	private InvoiceDate invoiceDate;

	@OneToMany
	@JoinColumn(name = "invoice_id")
	private Set<Order> orders = new HashSet<>();

	@OneToOne(mappedBy = "invoice", fetch = LAZY)
	private PaymentOrder paymentOrder;

	private Invoice(InvoiceNumber invoiceNumber, InvoiceDate date, Bill bill) {
		this.invoiceNumber = checkNotNull(invoiceNumber, "InvoiceNumber can't be null");
		this.invoiceDate = checkNotNull(date, "InvoiceDate can't be null");
		this.billId = checkNotNull(bill, "Bill can't be null").getId();
		this.customerId = bill.getCustomerId();
	}

	public static Invoice ofPaymentOrder(InvoiceNumber invoiceNumber, InvoiceDate date, PaymentOrder paymentOrder) {
		Invoice invoice = new Invoice(invoiceNumber, date, checkNotNull(paymentOrder, "PaymentOrder can't be null").getBill());
		invoice.setPaymentOrder(paymentOrder);
		invoice.orders = new HashSet<>(paymentOrder.getOrders());
		return invoice;
	}

	public static Invoice ofBill(InvoiceNumber invoiceNumber, InvoiceDate date, Bill bill, Set<UUID> ordersIds) {
		Invoice invoice = new Invoice(invoiceNumber, date, checkNotNull(bill, "Bill can't be null"));
		addOrders(invoice, bill.getOrders(ordersIds));
		return invoice;
	}

	public Invoice setPaymentOrder(PaymentOrder paymentOrder) {
		this.paymentOrder = checkNotNull(paymentOrder, "PaymentOrder cannot be null");
		paymentOrder.setInvoice(this);
		return this;
	}

	public Set<Order> getOrders() {
		return Collections.unmodifiableSet(orders);
	}

	public Optional<PaymentOrder> getPaymentOrder() {
		return Optional.ofNullable(paymentOrder);
	}

	public Optional<User> getUser() {
		val po = getPaymentOrder()
				.filter(it -> PaymentOrderApp.class.isAssignableFrom(it.getClass()))
				.map(it -> (PaymentOrderApp) it);
		return po.map(PaymentOrderApp::getPayer);
	}

	public MoneyAmount getTotalBaseAmount() {
		return this.orders.stream()
				.map(Order::getBasePrice)
				.reduce(ZERO, MoneyAmount::add);
	}

	public MoneyAmount getTotalAmount() {
		return getPaymentOrder()
				.map(PaymentOrder::getTotalAmount)
				.orElse(getTotalOrdersAmount());
	}

	public MoneyAmount getTipAmount() {
		return getPaymentOrder()
				.flatMap(PaymentOrder::getTip)
				.map(Tip::getAmount)
				.orElse(ZERO);
	}

	public boolean isDraft() {
		return getPaymentOrder().isEmpty();
	}

	private MoneyAmount getTotalOrdersAmount() {
		return this.orders.stream()
				.map(Order::getPrice)
				.reduce(ZERO, MoneyAmount::add);
	}

	public void addOrders(Set<Order> orders) {
		check(this.isDraft(), new OvviumDomainException(INVOICE_CANNOT_BE_MODIFIED));
		addOrders(this, orders);
	}

	public void removeOrder(UUID orderId) {
		check(this.isDraft(), new OvviumDomainException(INVOICE_CANNOT_BE_MODIFIED));
		val order = orders.stream()
				.filter(o -> o.getId().equals(orderId))
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException(format("Order %s not found for Invoice %s", orderId, this.getId())));
		this.orders.remove(order);
	}

	private static void addOrders(Invoice invoice, Set<Order> newOrders) {
		newOrders.stream().
				map(order -> check(order, !order.isPaid(), "Order " + order.getId() + " is already paid"))
				.forEach(ord -> invoice.orders.add(ord));
	}
}
