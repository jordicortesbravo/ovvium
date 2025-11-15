package com.ovvium.mother.model;

import com.ovvium.mother.builder.OrderBuilder;
import com.ovvium.services.model.bill.Order;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class OrderMother {

	public static final UUID ORDER_CERVEZA_ID = UUID.fromString("1d163804-cdbc-48ff-8146-b3e7ad9349b0");
	public static final UUID ORDER_PATATAS_BRAVAS_ID = UUID.fromString("395ebae0-b8ec-4ad6-9dd0-24dbe45953f3");

	public static Order getOrderOfCerveza() {
		return getOrderOfCervezaBuilder()
				.setId(ORDER_CERVEZA_ID)
				.setBill(BillMother.getOpenedBill().build())
				.setUser(UserMother.getUserJorge())
				.build();
	}

	public static OrderBuilder getOrderOfPatatasBravasBuilder() {
		return new OrderBuilder() //
				.setId(ORDER_PATATAS_BRAVAS_ID)
				.setProduct(ProductMother.getPatatasBravasProduct());
	}

	public static OrderBuilder getOrderOfCervezaBuilder() {
		return new OrderBuilder() //
				.setId(ORDER_CERVEZA_ID)
				.setProduct(ProductMother.getCervezaProduct());
	}

}
