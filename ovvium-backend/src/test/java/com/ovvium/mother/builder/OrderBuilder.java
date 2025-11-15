package com.ovvium.mother.builder;

import com.ovvium.mother.model.BillMother;
import com.ovvium.mother.model.OrderMother;
import com.ovvium.mother.model.ProductMother;
import com.ovvium.mother.model.UserMother;
import com.ovvium.services.model.bill.*;
import com.ovvium.services.model.product.Product;
import com.ovvium.services.model.user.User;
import com.ovvium.services.transfer.command.order.OrderGroupChoicesCommand;
import com.ovvium.services.transfer.command.order.OrderProductCommand;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ovvium.services.model.bill.PaymentStatus.PENDING;

@Setter
@Accessors(chain = true)
public class OrderBuilder {

	private UUID id = OrderMother.ORDER_PATATAS_BRAVAS_ID;
	private User user = UserMother.getUserWithCardData();
	private Product product = ProductMother.getPatatasBravasProduct();
	private PaymentStatus paymentStatus = PENDING;
	private IssueStatus issueStatus = IssueStatus.PENDING;
	private ServiceTime serviceTime = ServiceTime.SOONER;
	private Set<OrderGroupChoice> choices = Collections.emptySet();
	private Bill bill = BillMother.getOpenedBill().setOrders(Collections.singletonList(this)).build();

	public Order build() {
		var order = bill.createOrder(
				new OrderProductCommand(
						user,
						product,
						serviceTime,
						null,
						choices.stream()
								.map(it -> new OrderGroupChoicesCommand(it.getProduct().getId(), null))
								.collect(Collectors.toList()),
						Collections.emptyList()
				)
		);
		order.setUser(user);
		order.setIssueStatus(issueStatus);
		ReflectionUtils.set(order, "id", id);
		ReflectionUtils.set(order, "paymentStatus", paymentStatus);
		return order;
	}
}
