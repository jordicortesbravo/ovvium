package com.ovvium.mother.builder;

import com.ovvium.mother.model.CustomerMother;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.payment.InvoiceDate;
import com.ovvium.services.model.payment.InvoiceDateStatus;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.UUID;

import static com.ovvium.mother.model.InvoiceDateMother.INVOICE_DATE_ID;

@Setter
@Accessors(chain = true)
public class InvoiceDateBuilder {
	private UUID id = INVOICE_DATE_ID;
	private Customer customer = CustomerMother.getElBulliCustomer();
	private LocalDate date = LocalDate.now();
	private InvoiceDateStatus status = InvoiceDateStatus.OPEN;

	public InvoiceDate build() {
		InvoiceDate invoiceDate = new InvoiceDate(
				customer,
				date
		);
		ReflectionUtils.set(invoiceDate, "id", id);
		ReflectionUtils.set(invoiceDate, "status", status);
		return invoiceDate;
	}

}
