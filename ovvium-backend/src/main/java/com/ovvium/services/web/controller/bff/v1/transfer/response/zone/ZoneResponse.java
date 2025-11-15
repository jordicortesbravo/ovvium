package com.ovvium.services.web.controller.bff.v1.transfer.response.zone;

import com.ovvium.services.model.customer.Zone;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import lombok.Getter;

@Getter
public final class ZoneResponse extends ResourceIdResponse {

	private final String name;

	public ZoneResponse(Zone zone) {
		super(zone);
		this.name = zone.getName();
	}


}
