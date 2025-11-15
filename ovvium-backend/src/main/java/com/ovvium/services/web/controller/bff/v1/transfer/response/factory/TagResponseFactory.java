package com.ovvium.services.web.controller.bff.v1.transfer.response.factory;

import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.customer.Location;
import com.ovvium.services.web.controller.bff.v1.transfer.response.customer.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagResponseFactory {

	private final CustomerResponseFactory customerResponseFactory;

	public TagResponse create(Location location, Customer customer) {
		return new TagResponse(customerResponseFactory.create(customer), location);
	}
}
