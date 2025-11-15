package com.ovvium.services.service.impl;

import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.exception.OvviumDomainException;
import com.ovvium.services.model.payment.InvoiceDate;
import com.ovvium.services.repository.BillRepository;
import com.ovvium.services.repository.InvoiceDateRepository;
import com.ovvium.services.service.CustomerService;
import com.ovvium.services.service.InvoiceDateService;
import com.ovvium.services.util.ovvium.base.Preconditions;
import com.ovvium.services.web.controller.bff.v1.transfer.request.invoice.CreateInvoiceDateRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.invoice.UpdateInvoiceDateRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.invoice.InvoiceDatePageResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.invoice.InvoiceDateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

import static com.ovvium.services.model.exception.ErrorCode.*;
import static com.ovvium.services.model.payment.InvoiceDateStatus.CLOSED;
import static com.ovvium.services.model.payment.InvoiceDateStatus.OPEN;
import static com.ovvium.services.util.ovvium.base.Preconditions.check;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InvoiceDateServiceImpl implements InvoiceDateService {

	private final CustomerService customerService;
	private final InvoiceDateRepository invoiceDateRepository;
	private final BillRepository billRepository;

	@Override
	public ResourceIdResponse createInvoiceDate(CreateInvoiceDateRequest request) {
		val customer = customerService.getCustomer(request.getCustomerId());
		invoiceDateRepository.getLastByCustomer(customer).ifPresent(
				id -> {
					check(!request.getDate().isBefore(id.getDate()), new OvviumDomainException(INVOICE_DATE_RECENT_EXISTS, "Last InvoiceDate " + id.getDate()));
					check(!id.getDate().equals(request.getDate()), new OvviumDomainException(INVOICE_DATE_EXISTS, "Invoice exists for date " + id.getDate()));
					check(id.getStatus() != OPEN, new OvviumDomainException(INVOICE_DATE_OPEN_EXISTS, "Invoice exists for date " + id.getDate()));
				}
		);
		val invoiceDate = invoiceDateRepository.save(new InvoiceDate(customer, request.getDate()));
		return new ResourceIdResponse(invoiceDate);
	}

	@Override
	public void updateInvoiceDate(UpdateInvoiceDateRequest request) {
		var invoiceDate = invoiceDateRepository.getOrFail(request.getInvoiceDateId());
		request.getStatus().ifPresent(status -> {
			switch (status) {
				case OPEN -> {
					var customer = customerService.getCustomer(request.getCustomerId());
					invoiceDateRepository.getLastByCustomer(customer)
							.ifPresent(id -> check(id.getStatus() == CLOSED, new OvviumDomainException(INVOICE_DATE_LAST_NOT_CLOSED, "Only last invoice date can be opened again.")));
					invoiceDate.open();
				}
				case CLOSED -> {
					check(!billRepository.existsOpenByInvoiceDate(invoiceDate), new OvviumDomainException(INVOICE_DATE_BILLS_OPEN));
					invoiceDate.close();
				}
			}
			log.info("Changed status of InvoiceDate {} to {}", invoiceDate, status);
		});
	}

	/**
	 * We assume this will return the current InvoiceDate of 'today', and is the PoS who will need to keep
	 * the current Invoice Date up to date by warning the Customer of not closed InvoiceDates.
	 */
	@Override
	public InvoiceDate getCurrentInvoiceDate(Customer customer) {
		return invoiceDateRepository.getLastByCustomer(customer)
				.filter(id -> id.getStatus() == OPEN)
				.orElseThrow(() -> new OvviumDomainException(INVOICE_DATE_NOT_OPENED));
	}

	@Override
	public InvoiceDateResponse getLastInvoiceDate(UUID customerId) {
		val customer = customerService.getCustomer(customerId);
		return invoiceDateRepository.getLastByCustomer(customer)
				.map(InvoiceDateResponse::new)
				.orElseThrow(() -> new OvviumDomainException(INVOICE_DATE_NOT_OPENED));
	}

	@Override
	public InvoiceDatePageResponse pageInvoiceDates(UUID customerId, int month, int year) {
		Preconditions.checkRange(month, 1, 12, "Month is not between 1-12");
		Preconditions.check(year, year > 0, "Year cannot be negative");
		val customer = customerService.getCustomer(customerId);
		val page = invoiceDateRepository.pageByMonth(customer, month, year);
		return new InvoiceDatePageResponse(page,
				page.getContent().stream()
						.map(InvoiceDateResponse::new)
						.collect(Collectors.toList()));
	}


}
