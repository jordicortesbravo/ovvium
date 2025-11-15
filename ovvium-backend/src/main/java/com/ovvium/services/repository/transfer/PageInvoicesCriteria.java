package com.ovvium.services.repository.transfer;

import lombok.Data;

import java.time.LocalDate;
import java.util.Optional;

@Data
public final class PageInvoicesCriteria {

	private final LocalDate invoiceDate;

	public Optional<LocalDate> getInvoiceDate() {
		return Optional.ofNullable(invoiceDate);
	}
}
