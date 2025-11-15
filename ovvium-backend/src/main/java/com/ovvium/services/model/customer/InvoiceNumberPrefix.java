package com.ovvium.services.model.customer;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

import static com.ovvium.services.util.ovvium.base.Preconditions.checkMaxCharacters;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotBlank;
import static lombok.AccessLevel.PROTECTED;

/**
 * Prefix added to InvoiceNumber.
 */
@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public final class InvoiceNumberPrefix {

    private static final int MAX_LENGTH = 4;

    private String value;

    public InvoiceNumberPrefix(String code) {
        checkNotBlank(code, "InvoiceNumberPrefix cannot be blank");
        this.value = checkMaxCharacters(code.trim(), MAX_LENGTH, "InvoiceNumberPrefix max length is " + MAX_LENGTH).toUpperCase();
    }

}
