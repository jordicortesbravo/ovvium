package com.ovvium.services.service.impl;

import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.exception.ResourceNotFoundException;
import com.ovvium.services.model.payment.Invoice;
import com.ovvium.services.model.payment.InvoiceNumber;
import com.ovvium.services.model.payment.PaymentOrder;
import com.ovvium.services.repository.InvoiceRepository;
import com.ovvium.services.repository.transfer.PageInvoicesCriteria;
import com.ovvium.services.service.*;
import com.ovvium.services.util.common.domain.PageRequest;
import com.ovvium.services.util.common.domain.Pageable;
import com.ovvium.services.util.ovvium.base.Validations;
import com.ovvium.services.util.ovvium.spring.TransactionalUtils;
import com.ovvium.services.web.controller.bff.v1.transfer.request.common.GetPageRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.invoice.CreateInvoiceDraftRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.invoice.CreateInvoiceOrdersRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.invoice.DeleteInvoiceOrdersRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.invoice.GetInvoiceCustomerPageRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.factory.InvoiceResponseFactory;
import com.ovvium.services.web.controller.bff.v1.transfer.response.invoice.InvoicePageResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.invoice.InvoiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Function;

import static com.ovvium.services.util.common.domain.SimplePage.FIRST_PAGE;
import static com.ovvium.services.util.common.domain.Sort.desc;
import static com.ovvium.services.util.ovvium.base.Preconditions.check;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotNull;
import static com.ovvium.services.util.ovvium.domain.entity.TimestampedEntity.CREATED;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

	private final CustomerService customerService;
	private final BillService billService;
	private final LockService lockService;
	private final InvoiceRepository invoiceRepository;
	private final InvoiceDateService invoiceDateService;
	private final UserService userService;
	private final InvoiceResponseFactory invoiceResponseFactory;

	@Override
	public Invoice createInvoice(PaymentOrder paymentOrder) {
		val bill = paymentOrder.getBill();
		val customer = customerService.getCustomer(bill.getCustomerId());
		val invoiceDate = invoiceDateService.getCurrentInvoiceDate(customer);
		return createSafeInvoiceNumber(customer, (invoiceNumber) -> {
			Invoice invoice = Invoice.ofPaymentOrder(invoiceNumber, invoiceDate, paymentOrder);
			log.info("Created Invoice {} for Customer {}", invoice, customer);
			return invoiceRepository.save(invoice);
		});
	}

	@Override
	public InvoicePageResponse getInvoicesOfCustomer(GetInvoiceCustomerPageRequest request) {
		val customer = customerService.getCustomer(request.getCustomerId());
		// FIXME This should be one instead of zero
		val page = request.getPage().orElse(0);
		val size = request.getSize().orElse(20);
		val pageRequest = PageRequest.of(new Pageable(page, size, desc(CREATED)));
		val criteria = new PageInvoicesCriteria(request.getInvoiceDate().orElse(null));
		val invoicePage = invoiceRepository.pageByCustomer(pageRequest, customer, criteria);
		return new InvoicePageResponse(invoicePage,
				invoicePage.getContent().stream()
						.map(inv -> invoiceResponseFactory.create(inv, customer))
						.collect(toList())
		);
	}

	@Override
	public InvoiceResponse getInvoiceResponse(UUID invoiceId) {
		Invoice invoice = getInvoice(invoiceId);
		Customer customer = customerService.getCustomer(invoice.getCustomerId());
		return invoiceResponseFactory.create(invoice, customer);
	}

	@Override
	public Invoice getInvoice(UUID invoiceId) {
		return invoiceRepository.getOrFail(invoiceId);
	}

	@Override
	public InvoicePageResponse getInvoicesOfCurrentUser(GetPageRequest request) {
		val user = userService.getAuthenticatedUser();
		// FIXME This should be one instead of zero
		val page = request.getPage().orElse(FIRST_PAGE);
		val size = request.getSize().orElse(20);
		val pageRequest = PageRequest.of(new Pageable(page, size, desc(CREATED)));
		val invoicePage = invoiceRepository.pageByUser(pageRequest, user);
		val customersById = invoicePage.getContent().stream()
				.map(Invoice::getCustomerId)
				.distinct()
				.collect(toMap(identity(), customerService::getCustomer));
		return new InvoicePageResponse(invoicePage,
				invoicePage.getContent().stream()
						.map(inv -> invoiceResponseFactory.create(inv, customersById.get(inv.getCustomerId())))
						.collect(toList())
		);
	}

	@Override
	public ResourceIdResponse createInvoiceDraft(CreateInvoiceDraftRequest request) {
		val bill = billService.getBill(checkNotNull(request.getBillId(), "Bill id cannot be null"));
		val customer = customerService.getCustomer(bill.getCustomerId());
		val invoiceDate = invoiceDateService.getCurrentInvoiceDate(customer);
		val invoice = createSafeInvoiceNumber(customer, invoiceNumber ->
				invoiceRepository.save(Invoice.ofBill(invoiceNumber, invoiceDate, bill, request.getOrderIds()))
		);
		return new ResourceIdResponse(invoice);
	}

	@Override
	public void addOrdersToInvoice(CreateInvoiceOrdersRequest request) {
		Validations.validate(request);
		val invoice = invoiceRepository.getOrFail(request.getInvoiceId());
		check(invoice.getCustomerId().equals(request.getCustomerId()), new ResourceNotFoundException("Invoice is not from this Customer"));
		val bill = billService.getBill(request.getBillId());
		check(invoice.getBillId().equals(request.getBillId()), new ResourceNotFoundException("Invoice is not from this Bill"));
		invoice.addOrders(bill.getOrders(request.getOrderIds()));
		invoiceRepository.save(invoice);
	}

	@Override
	public void removeOrderFromInvoice(DeleteInvoiceOrdersRequest request) {
		Validations.validate(request);
		val invoice = invoiceRepository.getOrFail(request.getInvoiceId());
		check(invoice.getCustomerId().equals(request.getCustomerId()), new ResourceNotFoundException("Invoice is not from this Customer"));
		invoice.removeOrder(request.getOrderId());
		invoiceRepository.save(invoice);
	}

	private Invoice createSafeInvoiceNumber(Customer customer, Function<InvoiceNumber, Invoice> createFunction) {
		val lockKey = "invoice-number-" + customer.getId();
		tryInvoiceLockOrFail(lockKey);
		try {
			val invoiceNumber = invoiceRepository.getLastInvoice(customer.getId())
					.map(Invoice::getInvoiceNumber)
					.map(InvoiceNumber::asLong)
					.orElse(0L) + 1;
			return createFunction.apply(new InvoiceNumber(customer.getInvoiceNumberPrefix(), invoiceNumber));
		} finally {
			unlock(lockKey);
		}
	}

	private void tryInvoiceLockOrFail(String lockKey) {
		boolean tryLock = lockService.tryLock(lockKey, 30, SECONDS);
		if (!tryLock) {
			throw new IllegalStateException("Cannot acquire lock for requesting Invoice Number with key " + lockKey);
		}
	}

	private void unlock(String lockKey) {
		TransactionalUtils.executeAfterTransaction(() ->
				lockService.unlock(lockKey)
		);
	}
}
