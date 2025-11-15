package com.ovvium.mother.model;

import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.customer.Zone;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ZoneMother {

	public static final String MAIN_ROOM = "MAIN_ROOM";
	public static final String TERRACE = "TERRACE";

	public static Zone getSalonPrincipalZoneOfElBulli() {
		final Customer bulli = CustomerMother.getElBulliCustomer();
		return new Zone(bulli, MAIN_ROOM);
	}

	public static Zone getTerrazaZoneOfElBulli() {
		final Customer bulli = CustomerMother.getElBulliCustomer();
		return new Zone(bulli, TERRACE);
	}

}
