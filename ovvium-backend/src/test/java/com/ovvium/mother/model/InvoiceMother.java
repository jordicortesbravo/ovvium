package com.ovvium.mother.model;

import com.ovvium.mother.builder.InvoiceBuilder;
import com.ovvium.services.model.customer.InvoiceNumberPrefix;
import com.ovvium.services.model.payment.Invoice;
import com.ovvium.services.model.payment.InvoiceNumber;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class InvoiceMother {

	public final static UUID INVOICE_ID = UUID.fromString("8a381d87-613d-45e0-a2fe-2d5537fb0fc3");

	public static Invoice anyInvoice() {
		return new InvoiceBuilder().build();
	}

	public static Invoice anyInvoiceDraft() {
		return new InvoiceBuilder().buildDraft();
	}

	public static InvoiceNumber anyInvoiceNumber() {
		return new InvoiceNumber(new InvoiceNumberPrefix("TEST"), 1);
	}

}
