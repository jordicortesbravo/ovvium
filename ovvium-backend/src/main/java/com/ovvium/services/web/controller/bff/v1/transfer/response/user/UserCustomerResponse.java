package com.ovvium.services.web.controller.bff.v1.transfer.response.user;

import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.user.User;
import lombok.Getter;

import java.net.URI;
import java.util.UUID;

@Getter
public class UserCustomerResponse extends UserResponse {

	private final UUID customerId;
	private final String customerName;

	public UserCustomerResponse(User user, URI pictureUri, Customer customer) {
		super(user, pictureUri);
		this.customerId = customer.getId();
		this.customerName = customer.getName();
	}

}
