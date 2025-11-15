package com.ovvium.mother.model;

import com.ovvium.mother.builder.InvoiceDateBuilder;
import com.ovvium.services.model.payment.InvoiceDate;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.UUID;

@UtilityClass
public class InvoiceDateMother {

	public static final UUID INVOICE_DATE_ID = UUID.fromString("7fdbb80a-4cbf-40d2-b9de-4252072b30a7");

	public static InvoiceDate anyInvoiceDate(LocalDate date) {
		return new InvoiceDateBuilder().setDate(date).build();
	}

}
