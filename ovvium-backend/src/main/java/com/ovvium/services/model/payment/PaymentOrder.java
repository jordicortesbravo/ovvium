package com.ovvium.services.model.payment;

import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.bill.Order;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.exception.OvviumDomainException;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.ovvium.services.model.common.MoneyAmount.ZERO;
import static com.ovvium.services.model.exception.ErrorCode.INVOICE_ALREADY_PAID;
import static com.ovvium.services.model.exception.ErrorCode.ORDER_ALREADY_PAID;
import static com.ovvium.services.model.payment.PaymentType.CARD;
import static com.ovvium.services.model.payment.PaymentType.CASH;
import static com.ovvium.services.util.ovvium.base.Preconditions.check;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.InheritanceType.SINGLE_TABLE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "payment_order_type")
@DiscriminatorValue("POINT_OF_SALE")
@NoArgsConstructor(access = PROTECTED)
@Accessors(chain = true)
public class PaymentOrder extends BaseEntity {

	@ManyToOne
	private Bill bill;

	@ManyToMany(cascade = ALL)
	private Set<Order> orders = new HashSet<>();

	@Enumerated(STRING)
	private PaymentType paymentType;

	@Setter
	@ManyToOne(cascade = ALL)
	private Tip tip;

	@OneToOne(fetch = LAZY)
	@JoinColumn (name="invoice_surrogate_id")
	private Invoice invoice;

	PaymentOrder(Bill bill, PaymentType paymentType) {
		this.bill = checkNotNull(bill, "Bill cannot be null");
		this.paymentType = checkNotNull(paymentType, "Payment Type cannot be null");
	}

	public PaymentOrder(Invoice invoice, Bill bill, PaymentType paymentType) {
		this(bill, check(paymentType, asList(CARD, CASH).contains(paymentType), "Payment Type can be only CARD or CASH."));
		checkNotNull(invoice, "Invoice can't be null");
		check(invoice.isDraft(), new OvviumDomainException(INVOICE_ALREADY_PAID, "Invoice already paid " + invoice.getId()));
		invoice.setPaymentOrder(this);
	}

	PaymentOrder setInvoice(Invoice invoice){
		this.invoice = checkNotNull(invoice, "Invoice cannot be null");
		return this;
	}

	public Optional<Tip> getTip() {
		return Optional.ofNullable(tip);
	}

	public Optional<Invoice> getInvoice() {
		return Optional.ofNullable(invoice);
	}

	public MoneyAmount getTipAmount() {
		return getTip().map(Tip::getAmount).orElse(ZERO);
	}

	public PaymentOrder addOrders(Set<Order> orders) {
		checkNotNull(orders, "Orders cannot be null");
		checkOrdersStillPendingOnBill(orders);
		this.getOrders().addAll(orders);
		return this;
	}

	public MoneyAmount getTotalAmount() {
		MoneyAmount total = orders.stream()
				.map(Order::getPrice)
				.reduce(ZERO, MoneyAmount::add);
		if (getTip().isPresent()) {
			total = total.add(getTip().get().getAmount());
		}
		return total;
	}

	@SuppressWarnings("unchecked")
	public <T extends PaymentOrder> T as(Class<T> clazz) {
		if (clazz.isAssignableFrom(this.getClass())) {
			return (T) this;
		}
		throw new IllegalStateException("Class is not from type " + clazz.getSimpleName());
	}

	private void checkOrdersStillPendingOnBill(Set<Order> order) {
		val isPaid = order.stream().anyMatch(Order::isPaid);
		if (isPaid) {
			throw new OvviumDomainException(ORDER_ALREADY_PAID);
		}
	}
}
