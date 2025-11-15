package com.ovvium.services.repository.impl;

import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.payment.InvoiceDate;
import com.ovvium.services.model.payment.QInvoiceDate;
import com.ovvium.services.repository.InvoiceDateRepository;
import com.ovvium.services.util.common.domain.Page;
import com.ovvium.services.util.jpa.core.JpaDefaultRepository;
import lombok.val;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;
import java.util.UUID;

import static com.ovvium.services.util.common.domain.SimplePage.FIRST_PAGE;

@Repository
public class InvoiceDateRepositoryImpl extends JpaDefaultRepository<InvoiceDate, UUID> implements InvoiceDateRepository {

	private static final QInvoiceDate qInvoiceDate = QInvoiceDate.invoiceDate;

	@Override
	public Optional<InvoiceDate> getLastByCustomer(Customer customer) {
		return first(qInvoiceDate.customerId.eq(customer.getId()), qInvoiceDate.date.desc());
	}

	@Override
	public Page<InvoiceDate> pageByMonth(Customer customer, int month, int year) {
		val firstDayOfMonth = LocalDate.parse(String.format("01/%d/%d", month, year), DateTimeFormatter.ofPattern("dd/M/yyyy"));
		val lastDayOfMOnth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());
		return page(
				query(qInvoiceDate.customerId.eq(customer.getId())
						.and(qInvoiceDate.date.goe(firstDayOfMonth).and(qInvoiceDate.date.loe(lastDayOfMOnth)))),
				FIRST_PAGE,
				lastDayOfMOnth.lengthOfMonth()
		);
	}
}
