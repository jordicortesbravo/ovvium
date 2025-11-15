package com.ovvium.services.service;

import com.ovvium.mother.builder.InvoiceBuilder;
import com.ovvium.mother.builder.OrderBuilder;
import com.ovvium.mother.model.*;
import com.ovvium.mother.response.ResponseFactoryMother;
import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.bill.Order;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.exception.ResourceNotFoundException;
import com.ovvium.services.model.payment.Invoice;
import com.ovvium.services.model.payment.InvoiceDate;
import com.ovvium.services.model.payment.PaymentOrderApp;
import com.ovvium.services.repository.InvoiceRepository;
import com.ovvium.services.service.impl.InvoiceServiceImpl;
import com.ovvium.services.util.util.basic.Utils;
import com.ovvium.services.web.controller.bff.v1.transfer.request.invoice.CreateInvoiceDraftRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.invoice.CreateInvoiceOrdersRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.invoice.DeleteInvoiceOrdersRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ovvium.utils.MockitoUtils.mockRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InvoiceServiceTest {

	private InvoiceService invoiceService;
	private CustomerService customerService;
	private InvoiceDateService invoiceDateService;
	private InvoiceRepository invoiceRepository;
	private UserService userService;
	private BillService billService;
	private LockService lockService;

	@Before
	public void setUp() {
		customerService = mock(CustomerService.class);
		invoiceDateService = mock(InvoiceDateService.class);
		invoiceRepository = mockRepository(InvoiceRepository.class);
		userService = mock(UserService.class);
		billService = mock(BillService.class);
		lockService = mock(LockService.class);
		invoiceService = new InvoiceServiceImpl(customerService, billService, lockService, invoiceRepository, invoiceDateService, userService, ResponseFactoryMother.anInvoiceResponseFactory());
	}

	@Test
	public void given_payment_order_when_create_invoice_then_should_create_invoice_correctly() {
		PaymentOrderApp paymentOrder = PaymentOrderMother.anyPaymentOrderAppCard();
		InvoiceDate invoiceDate = InvoiceDateMother.anyInvoiceDate(LocalDate.now());

		Customer customer = CustomerMother.getElBulliCustomer();
		when(customerService.getCustomer(any())).thenReturn(customer);
		when(invoiceDateService.getCurrentInvoiceDate(customer)).thenReturn(invoiceDate);
		when(invoiceRepository.getLastInvoice(customer.getId())).thenReturn(Optional.empty());
		when(lockService.tryLock(anyString(), anyLong(), any())).thenReturn(true);

		invoiceService.createInvoice(paymentOrder);

		ArgumentCaptor<Invoice> captor = ArgumentCaptor.forClass(Invoice.class);
		verify(invoiceRepository, times(1)).save(captor.capture());
		Invoice capturedValue = captor.getValue();
		assertThat(capturedValue.getInvoiceNumber().asLong() ).isEqualTo(1);
		assertThat(capturedValue.getInvoiceDate()).isEqualTo(invoiceDate);
	}

	@Test
	public void given_payment_order_and_not_released_lock_on_wait_time_when_create_invoice_then_should_throw_exception() {
		PaymentOrderApp paymentOrder = PaymentOrderMother.anyPaymentOrderAppCard();
		InvoiceDate invoiceDate = InvoiceDateMother.anyInvoiceDate(LocalDate.now());

		Customer customer = CustomerMother.getElBulliCustomer();
		when(customerService.getCustomer(any())).thenReturn(customer);
		when(invoiceDateService.getCurrentInvoiceDate(customer)).thenReturn(invoiceDate);
		when(lockService.tryLock(anyString(), anyLong(), any())).thenReturn(false);

		assertThatThrownBy(() -> invoiceService.createInvoice(paymentOrder))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageStartingWith("Cannot acquire lock for requesting Invoice Number with key ");
	}

	@Test
	public void given_invoice_draft_request_for_bill_when_create_invoice_draft_then_should_create_invoice_draft_correctly() {
		Bill bill = BillMother.getOpenedBillWithMultipleOrders();
		InvoiceDate invoiceDate = InvoiceDateMother.anyInvoiceDate(LocalDate.now());

		Customer customer = CustomerMother.getElBulliCustomer();
		when(customerService.getCustomer(any())).thenReturn(customer);
		when(billService.getBill(any())).thenReturn(bill);
		when(invoiceDateService.getCurrentInvoiceDate(customer)).thenReturn(invoiceDate);
		when(invoiceRepository.getLastInvoice(customer.getId())).thenReturn(Optional.empty());
		when(lockService.tryLock(anyString(), anyLong(), any())).thenReturn(true);

		CreateInvoiceDraftRequest invoiceDraftRequest = new CreateInvoiceDraftRequest()
				.setCustomerId(customer.getId())
				.setBillId(bill.getId())
				.setOrderIds(bill.getOrders().stream().map(Order::getId).collect(Collectors.toSet()));
		invoiceService.createInvoiceDraft(invoiceDraftRequest);

		ArgumentCaptor<Invoice> captor = ArgumentCaptor.forClass(Invoice.class);
		verify(invoiceRepository, times(1)).save(captor.capture());
		Invoice capturedValue = captor.getValue();
		assertThat(capturedValue.getInvoiceNumber().asLong()).isEqualTo(1);
		assertThat(capturedValue.getInvoiceDate()).isEqualTo(invoiceDate);
		assertThat(capturedValue.isDraft()).isEqualTo(true);
	}

	@Test
	public void given_invoice_draft_and_add_order_request_when_add_order_to_invoice_then_should_add_order_correctly() {
		OrderBuilder orderBuilder = OrderMother.getOrderOfCervezaBuilder()
				.setId(UUID.randomUUID());
		Bill bill = BillMother.getOpenedBill()
				.setOrders(Collections.singletonList(orderBuilder))
				.build();
		Invoice invoice = new InvoiceBuilder()
				.setBill(bill)
				.buildDraft();
		Order order = Utils.first(bill.getOrders());

		Customer customer = CustomerMother.getElBulliCustomer();
		when(customerService.getCustomer(customer.getId())).thenReturn(customer);
		when(billService.getBill(bill.getId())).thenReturn(bill);
		when(invoiceRepository.getOrFail(invoice.getId())).thenReturn(invoice);

		CreateInvoiceOrdersRequest request = new CreateInvoiceOrdersRequest()
				.setCustomerId(customer.getId())
				.setBillId(bill.getId())
				.setInvoiceId(invoice.getId())
				.setOrderIds(Collections.singleton(order.getId()));
		invoiceService.addOrdersToInvoice(request);

		ArgumentCaptor<Invoice> captor = ArgumentCaptor.forClass(Invoice.class);
		verify(invoiceRepository, times(1)).save(captor.capture());
		Invoice capturedValue = captor.getValue();
		assertThat(capturedValue.getOrders()).hasSize(1);
		assertThat(capturedValue.getOrders()).contains(order);
	}

	@Test
	public void given_invoice_draft_and_bill_from_different_customer_when_add_order_to_invoice_then_should_throw_exception() {
		Bill bill = BillMother.getOpenedBill()
				.setCustomerId(UUID.randomUUID())
				.build();
		Invoice invoice = new InvoiceBuilder()
				.setBill(bill)
				.buildDraft();

		Customer customer = CustomerMother.getElBulliCustomer();
		when(customerService.getCustomer(customer.getId())).thenReturn(customer);
		when(invoiceRepository.getOrFail(invoice.getId())).thenReturn(invoice);

		CreateInvoiceOrdersRequest request = new CreateInvoiceOrdersRequest()
				.setCustomerId(customer.getId())
				.setBillId(bill.getId())
				.setInvoiceId(invoice.getId())
				.setOrderIds(Collections.singleton(UUID.randomUUID()));

		assertThatThrownBy(() -> invoiceService.addOrdersToInvoice(request))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Invoice is not from this Customer");
	}

	@Test
	public void given_invoice_draft_and_invoice_from_different_bill_when_add_order_to_invoice_then_should_throw_exception() {
		Bill bill = BillMother.getOpenedBill()
				.build();
		Invoice invoice = new InvoiceBuilder()
				.setBill(BillMother.getOpenedBillWithOpenOrder())
				.buildDraft();

		Customer customer = CustomerMother.getElBulliCustomer();
		when(customerService.getCustomer(customer.getId())).thenReturn(customer);
		when(invoiceRepository.getOrFail(invoice.getId())).thenReturn(invoice);
		when(billService.getBill(bill.getId())).thenReturn(bill);

		CreateInvoiceOrdersRequest request = new CreateInvoiceOrdersRequest()
				.setCustomerId(customer.getId())
				.setBillId(bill.getId())
				.setInvoiceId(invoice.getId())
				.setOrderIds(Collections.singleton(UUID.randomUUID()));

		assertThatThrownBy(() -> invoiceService.addOrdersToInvoice(request))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Invoice is not from this Bill");
	}

	@Test
	public void given_invoice_draft_and_remove_order_request_when_remove_order_from_invoice_then_should_remove_order_correctly() {
		OrderBuilder orderBuilder = OrderMother.getOrderOfCervezaBuilder()
				.setId(UUID.randomUUID());
		Bill bill = BillMother.getOpenedBill()
				.setOrders(Collections.singletonList(orderBuilder))
				.build();
		Invoice invoice = new InvoiceBuilder()
				.setBill(bill)
				.buildDraft();
		Order order = Utils.first(bill.getOrders());

		Customer customer = CustomerMother.getElBulliCustomer();
		when(customerService.getCustomer(customer.getId())).thenReturn(customer);
		when(billService.getBill(bill.getId())).thenReturn(bill);
		when(invoiceRepository.getOrFail(invoice.getId())).thenReturn(invoice);

		DeleteInvoiceOrdersRequest request = new DeleteInvoiceOrdersRequest()
				.setCustomerId(customer.getId())
				.setInvoiceId(invoice.getId())
				.setOrderId(order.getId());
		invoiceService.removeOrderFromInvoice(request);

		ArgumentCaptor<Invoice> captor = ArgumentCaptor.forClass(Invoice.class);
		verify(invoiceRepository, times(1)).save(captor.capture());
		Invoice capturedValue = captor.getValue();
		assertThat(capturedValue.getOrders()).isEmpty();
	}

	@Test
	public void given_invoice_draft_and_invoice_from_different_customer_when_remove_order_from_invoice_then_should_throw_exception() {
		Bill bill = BillMother.getOpenedBill()
				.setCustomerId(UUID.randomUUID())
				.build();
		Invoice invoice = new InvoiceBuilder()
				.setBill(bill)
				.buildDraft();

		Customer customer = CustomerMother.getElBulliCustomer();
		when(customerService.getCustomer(customer.getId())).thenReturn(customer);
		when(invoiceRepository.getOrFail(invoice.getId())).thenReturn(invoice);

		DeleteInvoiceOrdersRequest request = new DeleteInvoiceOrdersRequest()
				.setCustomerId(customer.getId())
				.setInvoiceId(invoice.getId())
				.setOrderId(UUID.randomUUID());

		assertThatThrownBy(() -> invoiceService.removeOrderFromInvoice(request))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Invoice is not from this Customer");
	}

}