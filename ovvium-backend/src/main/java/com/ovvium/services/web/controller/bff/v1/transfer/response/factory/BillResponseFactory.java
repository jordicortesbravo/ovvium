package com.ovvium.services.web.controller.bff.v1.transfer.response.factory;

import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.service.CustomerService;
import com.ovvium.services.web.controller.bff.v1.transfer.response.bill.BillResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.order.OrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BillResponseFactory {

	// FIXME Esto no deberia ir aqui
	private final CustomerService customerService;
	private final OrderResponseFactory orderResponseFactory;
	private final UserResponseFactory userResponseFactory;
	private final CustomerResponseFactory customerResponseFactory;

	public BillResponse create(Bill bill) {
		val orders = getOrderResponses(bill);
		Customer customer = customerService.getCustomer(bill.getCustomerId());
		val customerResponse = customerResponseFactory.create(customer);
		val members = bill.getMembers().stream().map(userResponseFactory::create).collect(Collectors.toList());
		return new BillResponse(bill, customerResponse, members, orders);
	}

	private List<OrderResponse> getOrderResponses(Bill bill) {
		return bill.getOrders().stream() //
				.map(orderResponseFactory::create) //
				.collect(Collectors.toList());
	}
}
