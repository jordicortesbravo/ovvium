package com.ovvium.services.model.customer;

import com.ovvium.mother.model.CustomerMother;
import com.ovvium.mother.model.LocationMother;
import com.ovvium.mother.model.ZoneMother;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LocationTest {

	@Test
	public void given_wrong_position_for_location_when_create_location_then_should_throw_exception() {
		Zone zone = ZoneMother.getSalonPrincipalZoneOfElBulli();
		Customer customer = CustomerMother.getElBulliCustomer();
		int wrongPosition = -1;
		assertThatThrownBy(() -> new Location(customer, zone, TagId.randomTagId(), new SerialNumber("0001"), wrongPosition))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Position can't be negative");
	}

	@Test
	public void given_customer_and_zone_for_other_customer_when_create_location_then_should_throw_exception() {
		Zone zone = ZoneMother.getSalonPrincipalZoneOfElBulli();
		Customer customer = CustomerMother.getCanRocaCustomer();

		assertThatThrownBy(() -> new Location(customer, zone, TagId.randomTagId(), new SerialNumber("0001"), 0))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Zone must be from the same customer, but was " + zone.getCustomerId());
	}

	@Test
	public void given_location_and_long_description_when_set_description_then_should_throw_exception() {
		Location location = LocationMother.getElBulliFirstFreeLocation();

		assertThatThrownBy(() -> location.setDescription("A very long description for a Location"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Description max length is 20");
	}

	@Test
	public void given_location_and_null_serial_number_when_set_serial_number_then_should_throw_exception() {
		Zone zone = ZoneMother.getSalonPrincipalZoneOfElBulli();
		Customer customer = CustomerMother.getElBulliCustomer();
		assertThatThrownBy(() -> new Location(customer, zone, TagId.randomTagId(), null, 1))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("SerialNumber cannot be null");
	}
}