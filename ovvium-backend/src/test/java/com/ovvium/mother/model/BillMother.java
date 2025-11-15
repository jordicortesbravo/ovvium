package com.ovvium.mother.model;

import com.ovvium.mother.builder.BillBuilder;
import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.customer.Location;
import com.ovvium.services.model.user.User;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.UUID;

import static com.ovvium.mother.model.LocationMother.getElBulliFirstFreeLocation;
import static com.ovvium.mother.model.LocationMother.getElBulliThirdFreeLocation;
import static java.util.Collections.singletonList;

@UtilityClass
public class BillMother {

	public static final UUID OPENED_BILL_BULLI_ID = UUID.fromString("d946127c-bc28-4e68-880f-da1b627c9e05");
	public static final UUID OPENED_BILL_BULLI_ID_WITH_MEMBER = UUID.fromString("da2e011b-f8dd-4d2b-915a-1bd14366f84e");
	public static final UUID OPENED_BILL_BULLI_ID_WITH_OPEN_ORDERS = UUID
			.fromString("7825054a-4d63-4428-a319-fc4d0382038e");
	public static final UUID OPENED_BILL_BULLI_ID_WITH_MULTIPLE_ORDERS = UUID
			.fromString("486c24b1-41bc-4acf-a9a0-676f0d334d25");

	public static Bill getOpenedBillForElBulliLocation() {
		Bill bill = new Bill(
				InvoiceDateMother.anyInvoiceDate(LocalDate.now()),
				singletonList(getElBulliFirstFreeLocation()));
		ReflectionUtils.set(bill, "id", OPENED_BILL_BULLI_ID);
		return bill;
	}

	public static Bill getOpenedBillForElBulliLocationWithMember() {
		Bill bill = new Bill(
				InvoiceDateMother.anyInvoiceDate(LocalDate.now()),
				singletonList(getElBulliThirdFreeLocation()));
		ReflectionUtils.set(bill, "id", OPENED_BILL_BULLI_ID_WITH_MEMBER);
		bill.addMember(UserMother.getUserJorge());
		return bill;
	}

	public static Bill getOpenedBillWithOpenOrder() {
		Bill bill = new Bill(
				InvoiceDateMother.anyInvoiceDate(LocalDate.now()),
				singletonList(getElBulliFirstFreeLocation()));
		ReflectionUtils.set(bill, "id", OPENED_BILL_BULLI_ID_WITH_OPEN_ORDERS);
		User userJorge = UserMother.getUserJorge();
		bill.addMember(userJorge);
		OrderMother.getOrderOfPatatasBravasBuilder() //
				.setBill(bill) //
				.setUser(userJorge) //
				.build();
		return bill;
	}

	public static Bill getOpenedBillWithMultipleOrders() {
		Bill bill = new Bill(
				InvoiceDateMother.anyInvoiceDate(LocalDate.now()),
				singletonList(getElBulliFirstFreeLocation()));
		ReflectionUtils.set(bill, "id", OPENED_BILL_BULLI_ID_WITH_MULTIPLE_ORDERS);
		User userJorge = UserMother.getUserJorge();
		bill.addMember(userJorge);
		OrderMother.getOrderOfPatatasBravasBuilder() //
				.setBill(bill) //
				.setUser(userJorge) //
				.build();
		OrderMother.getOrderOfCervezaBuilder() //
				.setBill(bill) //
				.setUser(userJorge) //
				.build();
		return bill;
	}

	public static BillBuilder getOpenedBill() {
		return new BillBuilder() //
				.setLocations(singletonList(getElBulliFirstFreeLocation())) //
				.setUser(UserMother.getUserJorge());
	}

	public static BillBuilder getEmptyBillAtLocation(Location location) {
		return new BillBuilder() //
				.setLocations(singletonList(location));
	}

}
