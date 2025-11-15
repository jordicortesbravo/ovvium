package com.ovvium.mother.builder;

import com.ovvium.mother.model.CustomerMother;
import com.ovvium.mother.model.LocationMother;
import com.ovvium.mother.model.ZoneMother;
import com.ovvium.services.model.customer.*;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Setter
@Accessors(chain = true)
public class LocationBuilder {

	private UUID id = LocationMother.FIRST_LOCATION_ID;
	private Customer customer = CustomerMother.getElBulliCustomer();
	private Zone zone = ZoneMother.getSalonPrincipalZoneOfElBulli();
	private TagId tagId = LocationMother.FIRST_LOCATION_TAG_ID;
	private SerialNumber serialNumber = LocationMother.FIRST_LOCATION_SN;
	private boolean advancePayment;

	public LocationBuilder withRandomId(){
		this.id = UUID.randomUUID();
		return this;
	}

	public Location build() {
		Location location = new Location(customer, zone, tagId, serialNumber, 1);
		ReflectionUtils.set(location, "id", id);
		location.setAdvancePayment(advancePayment);
		return location;
	}

}
