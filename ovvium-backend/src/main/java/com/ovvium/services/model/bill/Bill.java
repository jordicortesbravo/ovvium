package com.ovvium.services.model.bill;

import com.ovvium.services.model.customer.Employee;
import com.ovvium.services.model.customer.Location;
import com.ovvium.services.model.exception.OvviumDomainException;
import com.ovvium.services.model.exception.ResourceNotFoundException;
import com.ovvium.services.model.payment.InvoiceDate;
import com.ovvium.services.model.payment.PaymentOrderApp;
import com.ovvium.services.model.user.User;
import com.ovvium.services.transfer.command.order.OrderProductCommand;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import com.ovvium.services.util.util.basic.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.val;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ovvium.services.model.bill.BillStatus.CLOSED;
import static com.ovvium.services.model.bill.BillStatus.OPEN;
import static com.ovvium.services.model.exception.ErrorCode.ORDER_CANNOT_BE_ADDED_TO_CLOSED_BILL;
import static com.ovvium.services.util.ovvium.base.Preconditions.*;
import static java.lang.String.format;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Accessors(chain = true)
@NoArgsConstructor(access = PROTECTED)
public class Bill extends BaseEntity {

	@Type(type = "pg-uuid")
	private UUID customerId;

	@Type(type = "pg-uuid")
	private UUID invoiceDateId;

	@ManyToMany(cascade = ALL, fetch = LAZY)
	private Set<Location> locations = new HashSet<>();

	@Enumerated(STRING)
	private BillStatus billStatus = OPEN;

	@ManyToMany
	@OrderBy("name ASC")
	private Set<User> members = new TreeSet<>();

	@OrderBy("updated DESC")
	@OneToMany(cascade = ALL, fetch = LAZY)
	@JoinColumn(name = "bill_id")
	private Set<Order> orders = new HashSet<>();

	@ManyToOne(fetch = LAZY)
	private Employee employee;

	public Bill(InvoiceDate invoiceDate, User user, List<Location> locations) {
		this(invoiceDate, locations);
		members.add(user);
	}

	public Bill(InvoiceDate invoiceDate, List<Location> locations) {
		this.invoiceDateId = checkNotNull(invoiceDate, "Invoice Date cannot be null").getId();
		checkNotEmpty(locations, "Locations can't be empty.").forEach(location -> {
			if (this.customerId == null) {
				this.customerId = location.getCustomerId();
			}
			addLocation(location);
		});
	}

	public User getMember(UUID userId) {
		return members.stream()
				.filter(user -> user.getId().equals(userId))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException(String.format("User with id %s is not on bill %s", userId, getId())));
	}

	public Bill addMember(User user) {
		members.add(checkNotNull(user, "User can't be null"));
		return this;
	}

	public Bill removeMember(User user) {
		members.remove(checkNotNull(user, "User can't be null"));
		return this;
	}

	public Bill setEmployee(Employee employee) {
		this.employee = checkNotNull(employee, "Employee can't be null");
		return this;
	}

	public Optional<Employee> getEmployee() {
		return Optional.ofNullable(employee);
	}

	public boolean hasJoinedLocations() {
		return this.locations.size() > 1;
	}

	public Bill addLocation(Location location) {
		checkNotNull(location, "Location can't be null");
		check(location.getCustomerId().equals(this.customerId), "Location to add is not from this Bill's Customer");
		this.locations.add(location);
		return this;
	}

	public void setUpdated(Instant instant) {
		super.setUpdated(instant);
	}

	public Order createOrder(OrderProductCommand command) {
		check(!isClosed(), new OvviumDomainException(ORDER_CANNOT_BE_ADDED_TO_CLOSED_BILL, format("Bill is %s closed.", this.getId())));
		val order= Order.oneOfValues(command);
		this.orders.add(order);
		return order;
	}

	public Set<Order> getOrders(Set<UUID> orderIds) {
		checkNotNull(orderIds, "Order Ids cannot be null");
		val orderMap = getOrders().stream() //
				.collect(Collectors.toMap(Order::getId, Function.identity()));
		return orderIds.stream() //
				.map(id -> {
					check(orderMap.containsKey(id), new ResourceNotFoundException(format("Order Id %s not found for bill %s", id, this.getId())));
					return orderMap.get(id);
				}).collect(Collectors.toSet());
	}

	/**
	 * When an AdvancePayment is confirmed, Orders are PAID and they should be added to Bill
	 */
	public Bill addOrdersFrom(PaymentOrderApp paymentOrderApp){
		check(!isClosed(), new OvviumDomainException(ORDER_CANNOT_BE_ADDED_TO_CLOSED_BILL, format("Bill is %s closed.", this.getId())));
		this.orders.addAll(paymentOrderApp.getOrders());
		return this;
	}

	public Bill deleteOrder(UUID orderId) {
		Order order = Utils.first(this.getOrders(Collections.singleton(orderId)));
		order.delete();
		return this;
	}

	/**
	 * Move Bill content to this.
	 */
	public Bill joinTo(Bill bill) {
		this.orders.addAll(bill.orders);
		this.members.addAll(bill.members);
		this.locations.addAll(bill.locations);
		bill.members.clear();
		bill.orders.clear();
		bill.locations.clear();
		bill.close();
		return this;
	}

	public void close() {
		this.billStatus = CLOSED;
	}

	public boolean isOpen() {
		return billStatus == OPEN;
	}

	public boolean isClosed() {
		return billStatus == CLOSED;
	}

	public boolean isAllPaid() {
		return getOrders().stream().allMatch(Order::isPaid);
	}

	public boolean isFromAdvancePayment() {
		return this.locations.stream().anyMatch(Location::isAdvancePayment);
	}

}
