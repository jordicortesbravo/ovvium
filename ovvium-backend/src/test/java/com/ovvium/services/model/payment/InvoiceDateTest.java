package com.ovvium.services.model.payment;

import com.ovvium.mother.model.CustomerMother;
import com.ovvium.services.model.customer.Customer;
import org.junit.Test;

import java.time.LocalDate;

import static com.ovvium.services.model.payment.InvoiceDateStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InvoiceDateTest {

	@Test
	public void given_date_when_create_invoice_date_then_create_correctly() {
		LocalDate date = LocalDate.now();
		Customer customer = CustomerMother.getElBulliCustomer();

		InvoiceDate invoiceDate = new InvoiceDate(customer, date);

		assertThat(invoiceDate.getDate()).isEqualTo(date);
		assertThat(invoiceDate.getStatus()).isEqualTo(OPEN);
	}

	@Test
	public void given_null_date_when_create_invoice_date_then_should_throw_exception() {
		Customer customer = CustomerMother.getElBulliCustomer();
		assertThatThrownBy(() -> new InvoiceDate(customer, null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Date cannot be null");
	}
}