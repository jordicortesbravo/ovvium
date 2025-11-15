package com.ovvium.mother.model;

import com.ovvium.mother.builder.PaymentOrderAppCardBuilder;
import com.ovvium.services.model.bill.Order;
import com.ovvium.services.model.bill.ServiceTime;
import com.ovvium.services.model.payment.PaymentOrderApp;
import com.ovvium.services.transfer.command.order.OrderProductCommand;
import lombok.experimental.UtilityClass;

import java.util.Collections;

@UtilityClass
public class PaymentOrderMother {

	public static PaymentOrderApp anyPaymentOrderAppCard() {
		var user = UserMother.getUserWithCardData();
		var bill = BillMother.getOpenedBillWithMultipleOrders();
		return new PaymentOrderAppCardBuilder()
				.setBill(bill)
				.setUser(user)
				.setOrders(bill.getOrders())
				.build();
	}

	public static PaymentOrderApp anyAdvancedPaymentOrderAppCard() {
		var user = UserMother.getUserWithCardData();
		var bill = BillMother.getEmptyBillAtLocation(LocationMother.getElBulliAdvancePaymentLocation()).build();
		return new PaymentOrderAppCardBuilder()
				.setBill(bill)
				.setUser(user)
				.setOrders(Collections.singleton(Order.oneOfValues(new OrderProductCommand(
						user,
						ProductMother.getCervezaProduct(),
						ServiceTime.SOONER,
						null,
						Collections.emptyList(),
						Collections.emptyList()
				))))
				.build();
	}

}
