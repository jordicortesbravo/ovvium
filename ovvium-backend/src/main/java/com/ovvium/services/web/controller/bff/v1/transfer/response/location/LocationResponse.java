package com.ovvium.services.web.controller.bff.v1.transfer.response.location;

import com.ovvium.services.model.customer.Location;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import lombok.Getter;

@Getter
public final class LocationResponse extends ResourceIdResponse {

	private final String zone;
	private final String serialNumber;
	private final int position;
	private final String description;
	private final boolean advancePayment;

	public LocationResponse(Location location) {
		super(location);
		this.zone = location.getZone().getName();
		this.serialNumber = location.getSerialNumber().getValue();
		this.position = location.getPosition();
		this.description = location.getDescription();
		this.advancePayment = location.isAdvancePayment();
	}

}
