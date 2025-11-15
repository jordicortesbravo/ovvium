package com.ovvium.services.model.payment;

import com.ovvium.services.model.customer.InvoiceNumberPrefix;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

import static com.ovvium.services.util.ovvium.base.Preconditions.check;
import static java.lang.String.format;
import static lombok.AccessLevel.PROTECTED;

/**
 * Sequential number to identify uniquely an Invoice by Customer.
 */
@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public final class InvoiceNumber {

    private static final String OVVIUM_CODE = "OVV";
    public static final String SEPARATOR = "-";

    private String value;

    public InvoiceNumber(InvoiceNumberPrefix prefix, long number) {
        check(number, number > 0, "Invoice number must be positive");
        this.value = format("%s%s%s%d", OVVIUM_CODE, prefix.getValue(), SEPARATOR, number);
    }

    public long asLong() {
        return Long.parseLong(value.split(SEPARATOR)[1]);
    }

}
