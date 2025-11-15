package com.ovvium.services.web.controller.bff.v1.transfer.response.customer;

import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import lombok.Getter;

import java.net.URI;
import java.util.Set;

@Getter
public final class CustomerResponse extends ResourceIdResponse {

	private final String name;
	private final String description;
	private final URI imageUrl;
	private final String address;
	private final String cif;
	private final String timeZone;
	private final Set<String> phones;

	public CustomerResponse(Customer customer, URI imageUri) {
		super(customer);
		this.name = customer.getName();
		this.description = customer.getDescription();
		this.imageUrl = imageUri;
		this.address = customer.getAddress();
		this.cif = customer.getCif();
		this.phones = customer.getPhones();
		this.timeZone = customer.getTimeZone().toString();
	}

}
