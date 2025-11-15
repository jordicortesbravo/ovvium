package com.ovvium.services.repository;

import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.payment.Invoice;
import com.ovvium.services.model.user.User;
import com.ovvium.services.repository.transfer.PageInvoicesCriteria;
import com.ovvium.services.util.common.domain.Page;
import com.ovvium.services.util.common.domain.PageRequest;
import com.ovvium.services.util.jpa.core.DefaultRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends DefaultRepository<Invoice, UUID> {

	Optional<Invoice> getLastInvoice(UUID customerId);

	Page<Invoice> pageByUser(PageRequest pageRequest, User user);

	Page<Invoice> pageByCustomer(PageRequest pageRequest, Customer customer, PageInvoicesCriteria criteria);
}
