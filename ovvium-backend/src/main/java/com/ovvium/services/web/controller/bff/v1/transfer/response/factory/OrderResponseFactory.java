package com.ovvium.services.web.controller.bff.v1.transfer.response.factory;

import com.ovvium.services.model.bill.Order;
import com.ovvium.services.web.controller.bff.v1.transfer.response.order.OrderGroupChoiceResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.order.OrderResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderResponseFactory {

	private final ProductResponseFactory productResponseFactory;
	private final UserResponseFactory userResponseFactory;

	public OrderResponse create(Order order) {
		UserResponse userResponse = order.getUser().map(userResponseFactory::create).orElse(null);
		return new OrderResponse(order,
				userResponse,
				productResponseFactory.createSimple(order.getProduct()),
				order.getChoices().stream()
						.map(choice -> new OrderGroupChoiceResponse(choice, productResponseFactory.createSimple(choice.getProduct())))
						.collect(Collectors.toList()));
	}
}
