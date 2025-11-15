package com.ovvium.mother.model;

import com.ovvium.mother.builder.CustomerBuilder;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.customer.InvoiceNumberPrefix;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class CustomerMother {

	public static final UUID EL_BULLI_CUSTOMER_ID = UUID.fromString("80c769b6-f104-47c8-a5d4-37999c276c29");
	public static final UUID CAN_ROCA_CUSTOMER_ID = UUID.fromString("06bcb006-89a7-445d-b0b0-2cd64c802697");

	public static Customer getElBulliCustomer() {
		Customer elBulli = new CustomerBuilder().build();
		ReflectionUtils.set(elBulli, "id", EL_BULLI_CUSTOMER_ID);
		return elBulli;
	}

	public static Customer getCanRocaCustomer() {
		Customer canRoca = new CustomerBuilder()
				.build();
		ReflectionUtils.set(canRoca, "id", CAN_ROCA_CUSTOMER_ID);
		return canRoca;
	}

	public static InvoiceNumberPrefix anyInvoiceNumberPrefix() {
		return new InvoiceNumberPrefix("TEST");
	}



}
