package com.ovvium.services.model.payment;

import com.ovvium.services.model.customer.InvoiceNumberPrefix;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InvoiceNumberTest {


    @Test
    public void given_invoice_number_prefix_and_number_when_create_invoice_number_should_create_invoice_number_correctly() {
        final InvoiceNumberPrefix prefix = new InvoiceNumberPrefix("TEST");

        final InvoiceNumber number = new InvoiceNumber(prefix, 1);

        assertThat(number.getValue()).isEqualTo("OVVTEST-1");
    }


    @Test
    public void given_invoice_number_prefix_and_not_valid_number_when_create_invoice_number_should_throw_exception() {
        final InvoiceNumberPrefix prefix = new InvoiceNumberPrefix("TEST");

        assertThatThrownBy(() ->
                new InvoiceNumber(prefix, 0)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invoice number must be positive");
    }
}