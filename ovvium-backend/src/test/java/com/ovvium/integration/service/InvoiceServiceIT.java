package com.ovvium.integration.service;

import com.ovvium.integration.AbstractIntegrationTest;
import com.ovvium.integration.config.ServiceTestConfig;
import com.ovvium.services.app.config.BaseConfig;
import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.bill.Order;
import com.ovvium.services.model.payment.Invoice;
import com.ovvium.services.model.payment.InvoiceNumber;
import com.ovvium.services.model.payment.PaymentOrderApp;
import com.ovvium.services.model.user.PciProvider;
import com.ovvium.services.repository.BillRepository;
import com.ovvium.services.repository.InvoiceRepository;
import com.ovvium.services.repository.PaymentOrderAppCardRepository;
import com.ovvium.services.service.InvoiceService;
import com.ovvium.services.service.event.EventConsumerService;
import com.ovvium.services.util.util.basic.Utils;
import com.ovvium.services.web.controller.bff.v1.transfer.request.invoice.CreateInvoiceDraftRequest;
import com.ovvium.utils.TransactionalHelper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ovvium.integration.DbDataConstants.BILL_1_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@ContextConfiguration(classes = {BaseConfig.class, ServiceTestConfig.class})
@Sql(scripts = "/init_data.sql", executionPhase = BEFORE_TEST_METHOD)
public class InvoiceServiceIT extends AbstractIntegrationTest {

	@MockBean
	private EventConsumerService eventConsumerService;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private PaymentOrderAppCardRepository paymentOrderRepository;

	@Autowired
	private BillRepository billRepository;


	@Autowired
	private TransactionalHelper transactionalHelper;

	@Test(timeout = 60000)
	public void given_multiple_calls_to_create_invoice_when_create_invoice_then_should_get_unique_and_correlative_invoice_number() throws Exception {
		int threads = 10;
		ExecutorService executorService = Executors.newFixedThreadPool(threads);
		long beforeCount = invoiceRepository.count();

		// when
		IntStream.range(0, threads)
				.forEach((i) -> executorService.execute(() ->
						transactionalHelper.executeWithinTransaction(() -> {
							var bill = billRepository.getOrFail(BILL_1_ID);
							var user = Utils.first(bill.getMembers());
							var paymentOrder = new PaymentOrderApp(bill, user, PciProvider.PAYCOMET);
							paymentOrderRepository.save(paymentOrder);
							invoiceService.createInvoice(paymentOrder);
						})
				));
		executorService.shutdown();
		executorService.awaitTermination(60, TimeUnit.SECONDS);

		List<Long> list = invoiceRepository.list().stream().map(Invoice::getInvoiceNumber).map(InvoiceNumber::asLong).collect(Collectors.toList());
		assertThat(list).doesNotHaveDuplicates();
		assertThat(list).isSorted();
		assertThat(list).hasSize((int) beforeCount + threads);
	}

	@Test(timeout = 60000)
	public void given_multiple_calls_to_create_invoice_draft_when_create_invoice_draft_then_should_get_unique_and_correlative_invoice_number() throws Exception {
		int threads = 10;
		ExecutorService executorService = Executors.newFixedThreadPool(threads);
		long beforeCount = invoiceRepository.count();
		// when
		IntStream.range(0, threads)
				.forEach((i) -> executorService.execute(() -> transactionalHelper.executeWithinTransaction(() -> {
							Bill billWithOrders = billRepository.getOrFail(BILL_1_ID);
							invoiceService.createInvoiceDraft(new CreateInvoiceDraftRequest()
									.setBillId(billWithOrders.getId())
									.setCustomerId(billWithOrders.getCustomerId())
									.setOrderIds(billWithOrders.getOrders().stream()
											.filter(order -> !order.isPaid())
											.map(Order::getId).collect(Collectors.toSet())));
						})
				));
		executorService.shutdown();
		executorService.awaitTermination(60, TimeUnit.SECONDS);

		List<Long> list = invoiceRepository.list().stream().map(Invoice::getInvoiceNumber).map(InvoiceNumber::asLong).collect(Collectors.toList());
		assertThat(list).doesNotHaveDuplicates();
		assertThat(list).isSorted();
		assertThat(list).hasSize((int) beforeCount + threads);
	}
}
