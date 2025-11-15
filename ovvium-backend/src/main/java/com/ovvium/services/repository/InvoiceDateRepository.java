package com.ovvium.services.repository;

import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.payment.InvoiceDate;
import com.ovvium.services.util.common.domain.Page;
import com.ovvium.services.util.jpa.core.DefaultRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceDateRepository extends DefaultRepository<InvoiceDate, UUID> {

	Optional<InvoiceDate> getLastByCustomer(Customer customer);

	Page<InvoiceDate> pageByMonth(Customer customer, int month, int year);
}
