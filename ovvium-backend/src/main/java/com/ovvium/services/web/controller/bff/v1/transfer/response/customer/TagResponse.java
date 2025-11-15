package com.ovvium.services.web.controller.bff.v1.transfer.response.customer;

import com.ovvium.services.model.customer.Location;
import com.ovvium.services.web.controller.bff.v1.transfer.response.location.LocationResponse;
import lombok.Getter;

@Getter
public final class TagResponse {

	private final CustomerResponse customer;
	private final LocationResponse location;

	public TagResponse(CustomerResponse customerResponse, Location location) {
		this.customer = customerResponse;
		this.location = new LocationResponse(location);
	}
}
