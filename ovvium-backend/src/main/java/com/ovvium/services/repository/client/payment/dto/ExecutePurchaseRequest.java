package com.ovvium.services.repository.client.payment.dto;

import com.ovvium.services.model.bill.Order;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.user.User;
import com.ovvium.services.model.user.UserPciDetails;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;
import java.util.UUID;

@Data
@Accessors(fluent = true)
public class ExecutePurchaseRequest {

	private final UUID customerId;
	private final User user;
	private final UserPciDetails userPciDetails;
	private final MoneyAmount amount;
	private final UUID orderId;
	private final Set<Order> orders;

}
