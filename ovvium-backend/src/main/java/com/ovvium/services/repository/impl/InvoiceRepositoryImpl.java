package com.ovvium.services.repository.impl;

import com.mysema.query.types.expr.BooleanExpression;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.payment.Invoice;
import com.ovvium.services.model.payment.QInvoice;
import com.ovvium.services.model.payment.QPaymentOrderApp;
import com.ovvium.services.model.user.User;
import com.ovvium.services.repository.InvoiceRepository;
import com.ovvium.services.repository.transfer.PageInvoicesCriteria;
import com.ovvium.services.util.common.domain.Page;
import com.ovvium.services.util.common.domain.PageRequest;
import com.ovvium.services.util.jpa.core.JpaDefaultRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class InvoiceRepositoryImpl extends JpaDefaultRepository<Invoice, UUID> implements InvoiceRepository {

	private static final QInvoice qInvoice = QInvoice.invoice;

	@Override
	public Optional<Invoice> getLastInvoice(UUID customerId) {
		return first(qInvoice.customerId.eq(customerId), qInvoice.created.desc());
	}

	@Override
	public Page<Invoice> pageByUser(PageRequest pageRequest, User user) {
		return super.page(qInvoice.paymentOrder.as(QPaymentOrderApp.class).payer.eq(user), pageRequest);
	}

	@Override
	public Page<Invoice> pageByCustomer(PageRequest pageRequest, Customer customer, PageInvoicesCriteria criteria) {
		BooleanExpression predicate = qInvoice.customerId.eq(customer.getId());
		if (criteria.getInvoiceDate().isPresent()) {
			predicate = predicate.and(qInvoice.invoiceDate.date.eq(criteria.getInvoiceDate().get()));
		}
		return super.page(predicate, pageRequest);
	}
}
