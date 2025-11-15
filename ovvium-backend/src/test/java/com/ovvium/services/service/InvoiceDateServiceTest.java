package com.ovvium.services.service;

import com.ovvium.mother.builder.InvoiceDateBuilder;
import com.ovvium.mother.model.CustomerMother;
import com.ovvium.mother.model.InvoiceDateMother;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.exception.OvviumDomainException;
import com.ovvium.services.model.payment.InvoiceDate;
import com.ovvium.services.repository.BillRepository;
import com.ovvium.services.repository.InvoiceDateRepository;
import com.ovvium.services.service.impl.InvoiceDateServiceImpl;
import com.ovvium.services.web.controller.bff.v1.transfer.request.invoice.CreateInvoiceDateRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.invoice.UpdateInvoiceDateRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static com.ovvium.mother.model.InvoiceDateMother.anyInvoiceDate;
import static com.ovvium.services.model.payment.InvoiceDateStatus.CLOSED;
import static com.ovvium.utils.MockitoUtils.mockRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InvoiceDateServiceTest {

	// SUT
	private InvoiceDateService invoiceDateService;

	private CustomerService customerService;
	private InvoiceDateRepository invoiceDateRepository;
	private BillRepository billRepository;

	@Before
	public void setUp() {
		customerService = mock(CustomerService.class);
		invoiceDateRepository = mockRepository(InvoiceDateRepository.class);
		billRepository = mockRepository(BillRepository.class);
		invoiceDateService = new InvoiceDateServiceImpl(customerService, invoiceDateRepository, billRepository);
	}

	@Test
	public void given_not_found_customer_when_create_invoice_date_then_should_throw_exception() {
		final CreateInvoiceDateRequest request = new CreateInvoiceDateRequest()
				.setCustomerId(UUID.randomUUID());

		when(customerService.getCustomer(any())).thenThrow(new EntityNotFoundException("Not found"));

		assertThatThrownBy(
				() -> invoiceDateService.createInvoiceDate(request)
		).isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	public void given_date_and_existing_invoice_date_when_create_invoice_date_then_should_throw_exception() {
		final CreateInvoiceDateRequest request = new CreateInvoiceDateRequest()
				.setCustomerId(UUID.randomUUID())
				.setDate(LocalDate.now());

		Customer customer = CustomerMother.getElBulliCustomer();
		when(customerService.getCustomer(any())).thenReturn(customer);
		when(invoiceDateRepository.getLastByCustomer(customer)).thenReturn(Optional.of(anyInvoiceDate(request.getDate())));

		assertThatThrownBy(
				() -> invoiceDateService.createInvoiceDate(request)
		).isInstanceOf(OvviumDomainException.class)
				.hasMessage("Invoice Date for this date already exists.");
	}

	@Test
	public void given_past_date_and_future_invoice_date_exists_when_create_invoice_date_then_should_throw_exception() {
		final CreateInvoiceDateRequest request = new CreateInvoiceDateRequest()
				.setCustomerId(UUID.randomUUID())
				.setDate(LocalDate.now().minusDays(1));

		Customer customer = CustomerMother.getElBulliCustomer();
		when(customerService.getCustomer(any())).thenReturn(customer);
		when(invoiceDateRepository.getLastByCustomer(customer)).thenReturn(Optional.of(InvoiceDateMother.anyInvoiceDate(LocalDate.now())));

		assertThatThrownBy(
				() -> invoiceDateService.createInvoiceDate(request)
		).isInstanceOf(OvviumDomainException.class)
				.hasMessage("It already exists a more recent Invoice Date.");
	}

	@Test
	public void given_invoice_date_update_close_request_and_open_bill_exists_when_update_invoice_date_then_should_throw_exception() {
		Customer customer = CustomerMother.getElBulliCustomer();
		InvoiceDate invoiceDate = InvoiceDateMother.anyInvoiceDate(LocalDate.now());
		final UpdateInvoiceDateRequest request = new UpdateInvoiceDateRequest()
				.setCustomerId(customer.getId())
				.setInvoiceDateId(invoiceDate.getId())
				.setStatus(CLOSED);

		when(customerService.getCustomer(any())).thenReturn(customer);
		when(invoiceDateRepository.getOrFail(invoiceDate.getId())).thenReturn(invoiceDate);
		when(billRepository.existsOpenByInvoiceDate(invoiceDate)).thenReturn(true);

		assertThatThrownBy(
				() -> invoiceDateService.updateInvoiceDate(request)
		).isInstanceOf(OvviumDomainException.class)
				.hasMessage("Bills still open for Invoice Date, cannot close this invoice date.");
	}

	@Test
	public void given_current_open_invoice_date_when_create_invoice_date_then_should_throw_exception() {
		final CreateInvoiceDateRequest request = new CreateInvoiceDateRequest()
				.setCustomerId(UUID.randomUUID())
				.setDate(LocalDate.now());

		Customer customer = CustomerMother.getElBulliCustomer();
		when(customerService.getCustomer(any())).thenReturn(customer);
		when(invoiceDateRepository.getLastByCustomer(customer)).thenReturn(Optional.of(InvoiceDateMother.anyInvoiceDate(LocalDate.now().minusDays(1))));

		assertThatThrownBy(
				() -> invoiceDateService.createInvoiceDate(request)
		).isInstanceOf(OvviumDomainException.class)
				.hasMessage("Invoice Date for another date is not closed.");
	}

	@Test
	public void given_last_closed_invoice_date_when_create_invoice_date_then_should_create_new_invoice() {
		final CreateInvoiceDateRequest request = new CreateInvoiceDateRequest()
				.setCustomerId(UUID.randomUUID())
				.setDate(LocalDate.now());
		final InvoiceDate invoiceDate = new InvoiceDateBuilder()
				.setStatus(CLOSED)
				.setDate(LocalDate.now().minusDays(1))
				.build();

		Customer customer = CustomerMother.getElBulliCustomer();
		when(customerService.getCustomer(any())).thenReturn(customer);
		when(invoiceDateRepository.getLastByCustomer(customer)).thenReturn(Optional.of(invoiceDate));

		invoiceDateService.createInvoiceDate(request);

		final ArgumentCaptor<InvoiceDate> captor = ArgumentCaptor.forClass(InvoiceDate.class);
		verify(invoiceDateRepository, times(1)).save(captor.capture());
		final InvoiceDate capturedValue = captor.getValue();
		assertThat(capturedValue.getDate()).isEqualTo(request.getDate());
	}

}