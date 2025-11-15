package com.ovvium.mother.model;

import com.ovvium.services.model.customer.*;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class LocationMother {

	public static final int FREE_BILL_FIRST_POSITION_ID = 1;
	public static final int FREE_BILL_SECOND_POSITION_ID = 2;
	public static final int FREE_BILL_THIRD_POSITION_ID = 3;
	public static final int ADVANCE_PAYMENT_LOCATION_POSITION_ID = 3;
	public static final UUID FIRST_LOCATION_ID = UUID.fromString("7471f2a5-5cef-4ee1-97b4-33bc25916ac0");
	public static final UUID SECOND_LOCATION_ID = UUID.fromString("4a063805-e2ff-4748-a425-3f7833832308");
	public static final UUID THIRD_LOCATION_ID = UUID.fromString("56fadd3a-a70a-4eb3-84d9-571858f56473");
	public static final UUID ADVANCE_PAYMENT_LOCATION_ID = UUID.fromString("56fadd3a-a70a-4eb3-84d9-571858f56473");
	public static final TagId FIRST_LOCATION_TAG_ID = new TagId("abcdef1234");
	public static final TagId SECOND_LOCATION_TAG_ID = new TagId("abcdef1235");
	public static final TagId THIRD_LOCATION_TAG_ID = new TagId("abcdef1236");
	public static final TagId ADVANCE_PAYMENT_LOCATION_TAG_ID = new TagId("abcdef1237");
	public static final TagId CUSTOMER_LOCATION_TAG_ID = new TagId("abcdef1237");
	public static final SerialNumber FIRST_LOCATION_SN = new SerialNumber("0001");
	public static final SerialNumber SECOND_LOCATION_SN = new SerialNumber("0002");
	public static final SerialNumber THIRD_LOCATION_SN = new SerialNumber("0003");
	public static final SerialNumber CUSTOMER_LOCATION_SN = new SerialNumber("0004");
	public static final SerialNumber ADVANCE_PAYMENT_LOCATION_SN = new SerialNumber("0004");

	public static Location getElBulliFirstFreeLocation() {
		Zone zone = ZoneMother.getTerrazaZoneOfElBulli();
		Location location = new Location(CustomerMother.getElBulliCustomer(), zone, FIRST_LOCATION_TAG_ID, FIRST_LOCATION_SN, FREE_BILL_FIRST_POSITION_ID);
		ReflectionUtils.set(location, "id", FIRST_LOCATION_ID);
		return location;
	}

	public static Location getElBulliAdvancePaymentLocation() {
		Zone zone = ZoneMother.getTerrazaZoneOfElBulli();
		Location location = new Location(CustomerMother.getElBulliCustomer(), zone, ADVANCE_PAYMENT_LOCATION_TAG_ID, ADVANCE_PAYMENT_LOCATION_SN, ADVANCE_PAYMENT_LOCATION_POSITION_ID);
		location.setAdvancePayment(true);
		ReflectionUtils.set(location, "id", ADVANCE_PAYMENT_LOCATION_ID);
		return location;
	}

	public static Location getElBulliSecondFreeLocation() {
		Zone zone = ZoneMother.getSalonPrincipalZoneOfElBulli();
		Location location = new Location(CustomerMother.getElBulliCustomer(), zone, SECOND_LOCATION_TAG_ID, SECOND_LOCATION_SN, FREE_BILL_SECOND_POSITION_ID);
		ReflectionUtils.set(location, "id", SECOND_LOCATION_ID);
		return location;
	}

	public static Location getElBulliThirdFreeLocation() {
		Zone zone = ZoneMother.getSalonPrincipalZoneOfElBulli();
		Location location = new Location(CustomerMother.getElBulliCustomer(), zone, THIRD_LOCATION_TAG_ID, THIRD_LOCATION_SN, FREE_BILL_THIRD_POSITION_ID);
		ReflectionUtils.set(location, "id", THIRD_LOCATION_ID);
		return location;
	}

	public static Location getLocationOfCustomer(Customer customer) {
		int size = customer.getLocations().size();
		return new Location(customer, ZoneMother.getSalonPrincipalZoneOfElBulli(), CUSTOMER_LOCATION_TAG_ID, CUSTOMER_LOCATION_SN, size + 1);
	}
}
