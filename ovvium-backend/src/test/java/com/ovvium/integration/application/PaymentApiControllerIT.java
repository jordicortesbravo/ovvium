package com.ovvium.integration.application;

import com.google.common.collect.Sets;
import com.ovvium.mother.model.OrderMother;
import com.ovvium.services.model.bill.Order;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.exception.ErrorCode;
import com.ovvium.services.model.payment.*;
import com.ovvium.services.repository.InvoiceRepository;
import com.ovvium.services.repository.OrderRepository;
import com.ovvium.services.repository.ProductRepository;
import com.ovvium.services.repository.client.payment.ws.dto.ExecutePurchaseWsResponse;
import com.ovvium.services.util.ovvium.exception.OvviumApiError;
import com.ovvium.services.util.util.basic.Utils;
import com.ovvium.services.web.controller.bff.v1.transfer.request.order.CreateOrderRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.payment.*;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.payment.PaymentOrderAppCardResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.payment.UserCardDataResponse;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import static com.ovvium.integration.DbDataConstants.*;
import static com.ovvium.mother.model.PaycometMother.*;
import static com.ovvium.mother.model.UserMother.USER_PCI_DETAILS_ID;
import static com.ovvium.services.model.bill.BillStatus.CLOSED;
import static com.ovvium.services.model.bill.BillStatus.OPEN;
import static com.ovvium.services.model.bill.PaymentStatus.PAID;
import static com.ovvium.utils.SpringMockMvcUtils.doGet;
import static com.ovvium.utils.SpringMockMvcUtils.doPost;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PaymentApiControllerIT extends AbstractApplicationIntegrationTest {

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Test
	public void given_logged_user_when_add_card_token_then_should_add_token_correctly() {
		String accessToken = loginUser(USER_1_EMAIL, USER_1_PASSWORD);

		AddCardTokenRequest request = new AddCardTokenRequest();
		request.setToken("cardtoken");

		when(paycometWsClient.addUserToken(any())).thenReturn(anyCorrectAddUserTokenWsResponse());
		when(paycometWsClient.getInfoUser(any())).thenReturn(anyCorrectInfoUserWsResponse());

		doPost(mockMvc, "/me/card-token", request, status().isCreated(), accessToken, ResourceIdResponse.class);

		UserCardDataResponse[] cardDataResponses = doGet(mockMvc,"/me/cards", accessToken, UserCardDataResponse[].class);
		assertThat(cardDataResponses).isNotEmpty();
		assertThat(cardDataResponses[0].getPciDetailsId()).isNotNull();
	}

	@Test
	public void given_not_logged_user_with_bill_when_pay_app_card_then_throw_exception() {
		PaymentRequest request = new PaymentAppCardRequest()
				.setPciDetailsId(USER_PCI_DETAILS_ID)
				.setOrderIds(Collections.singleton(OrderMother.ORDER_PATATAS_BRAVAS_ID))
				.setTipAmount(2d);

		OvviumApiError ovviumApiError =doPost(mockMvc,"/payments/app-card", request, status().isUnauthorized(), null, OvviumApiError.class);

		assertThat(ovviumApiError.getErrorCode()).isEqualTo(401);
	}

	@Test
	public void given_logged_user_with_bill_when_pay_app_card_then_should_execute_purchase_and_close_bill() {
		String accessToken = loginUser(USER_2_EMAIL, USER_2_PASSWORD);

		PaymentRequest request = new PaymentAppCardRequest()
				.setPciDetailsId(USER_2_PCI_DETAILS_ID)
				.setOrderIds(Sets.newHashSet(ORDER_1_BILL_1_ID, ORDER_2_BILL_1_ID))
				.setTipAmount(2d);

		MoneyAmount orderPrice = orderRepository.getOrFail(ORDER_1_BILL_1_ID).getPrice();
		when(paycometWsClient.executePurchase(any())).thenReturn(executePurchaseWsResponse(orderPrice));
		when(paycometWsClient.getInfoUser(any())).thenReturn(anyCorrectInfoUserWsResponse());

		UUID invoiceId = doPost(mockMvc, "/payments/app-card", request, status().isCreated(), accessToken, PaymentOrderAppCardResponse.class)
				.getInvoiceId();

		transactionalHelper.executeWithinTransaction(() -> {
			final PaymentOrder paymentOrder = invoiceRepository.getOrFail(invoiceId).getPaymentOrder().orElseThrow(() -> new IllegalStateException("Payment Order not found."));
			assertThat(paymentOrder.getBill().getOrders().stream().allMatch(Order::isPaid)).isTrue();
			assertThat(paymentOrder.getBill().getBillStatus()).isEqualTo(CLOSED);
			assertThat(paymentOrder.getOrders()).hasSize(2);
			assertThat(paymentOrder.as(PaymentOrderApp.class).getPurchaseTransactionDetails()).isNotEmpty();
		});
	}

	@Test
	public void given_multiple_requests_and_same_order_when_pay_app_card_then_should_throw_exception_on_locked_order() throws InterruptedException {
		final String accessToken = loginUser(USER_2_EMAIL, USER_2_PASSWORD);

		final PaymentRequest request = new PaymentAppCardRequest()
				.setPciDetailsId(USER_2_PCI_DETAILS_ID)
				.setOrderIds(Collections.singleton(ORDER_1_BILL_1_ID))
				.setTipAmount(2d);

		final MoneyAmount orderPrice = orderRepository.getOrFail(ORDER_1_BILL_1_ID).getPrice();

		final Semaphore semaphore = new Semaphore(1);

		when(paycometWsClient.getInfoUser(any())).thenReturn(anyCorrectInfoUserWsResponse());
		when(paycometWsClient.splitTransfer(any())).thenReturn(splitTransferWsResponse(orderPrice));

		// Thread A will create locks for Orders and sleep for 1s when executing purchase
		ExecutorService executorA = Executors.newSingleThreadExecutor();
		executorA.execute(() -> {
			when(paycometWsClient.executePurchase(any())).then((Answer<ExecutePurchaseWsResponse>) invocationOnMock -> {
				semaphore.release();
				Thread.sleep(2000);
				return executePurchaseWsResponse(orderPrice);
			});


			doPost(mockMvc, "/payments/app-card", request, status().isCreated(), accessToken, ResourceIdResponse.class);
		});

		// Main Thread will find Orders locked
		semaphore.acquire();
		Thread.sleep(500);
		OvviumApiError ovviumApiError = doPost(mockMvc, "/payments/app-card", request, status().is4xxClientError(), accessToken, OvviumApiError.class);

		await().untilAsserted(() ->
				assertThat(ovviumApiError.getErrorCode()).isEqualTo(ErrorCode.ORDER_IS_BEING_PAID.getErrorCode()));
	}

	@Test
	public void given_multiple_requests_on_different_orders_when_pay_app_card_then_should_pay_orders_correctly_on_same_bill() throws InterruptedException {
		final String accessToken = loginUser(USER_2_EMAIL, USER_2_PASSWORD);
		final MoneyAmount orderPrice = orderRepository.getOrFail(ORDER_1_BILL_1_ID).getPrice();

		final Semaphore semaphore = new Semaphore(1);

		// Thread A will create locks for Orders and sleep for 1s when executing purchase
		ExecutorService executorA = Executors.newSingleThreadExecutor();
		executorA.execute(() -> {
			final PaymentRequest request = new PaymentAppCardRequest()
					.setPciDetailsId(USER_2_PCI_DETAILS_ID)
					.setOrderIds(Collections.singleton(ORDER_1_BILL_1_ID))
					.setTipAmount(2d);

			when(paycometWsClient.getInfoUser(any())).thenReturn(anyCorrectInfoUserWsResponse());
			when(paycometWsClient.executePurchase(any())).then((Answer<ExecutePurchaseWsResponse>) invocationOnMock -> {
				semaphore.release();
				Thread.sleep(1000);
				return executePurchaseWsResponse(orderPrice);
			});

			doPost(mockMvc,"/payments/app-card", request, status().isCreated(), accessToken, ResourceIdResponse.class);
		});

		final PaymentRequest request = new PaymentAppCardRequest()
				.setPciDetailsId(USER_2_PCI_DETAILS_ID)
				.setOrderIds(Collections.singleton(ORDER_2_BILL_1_ID))
				.setTipAmount(2d);

		// Main Thread will find other Orders locked, but this is not blocking
		semaphore.acquire();
		Thread.sleep(500);
		doPost(mockMvc,"/payments/app-card", request, status().isCreated(), accessToken, ResourceIdResponse.class);

		await().untilAsserted(() -> {
			assertThat(orderRepository.getOrFail(ORDER_1_BILL_1_ID).getPaymentStatus()).isEqualTo(PAID);
			assertThat(orderRepository.getOrFail(ORDER_2_BILL_1_ID).getPaymentStatus()).isEqualTo(PAID);
		});
	}

	@Test
	public void given_logged_user_with_bill_when_pay_app_card_then_should_create_correct_invoice_number_on_invoice() {
		final String accessToken = loginUser(USER_2_EMAIL, USER_2_PASSWORD);

		final PaymentRequest request = new PaymentAppCardRequest()
				.setPciDetailsId(USER_2_PCI_DETAILS_ID)
				.setOrderIds(Sets.newHashSet(ORDER_1_BILL_1_ID, ORDER_2_BILL_1_ID))
				.setTipAmount(2d);

		Order order = orderRepository.getOrFail(ORDER_1_BILL_1_ID);
		final MoneyAmount orderPrice = order.getPrice();
		when(paycometWsClient.executePurchase(any())).thenReturn(executePurchaseWsResponse(orderPrice));
		when(paycometWsClient.getInfoUser(any())).thenReturn(anyCorrectInfoUserWsResponse());

		UUID invoiceId = doPost(mockMvc, "/payments/app-card", request, status().isCreated(), accessToken, PaymentOrderAppCardResponse.class)
				.getInvoiceId();

		final long invoiceNumber = invoiceRepository.getOrFail(invoiceId).getInvoiceNumber().asLong();
		final List<Long> invoiceNumbersOfCustomer = invoiceRepository.list().stream()
				.filter(invoice -> invoice.getCustomerId().equals(order.getProduct().getCustomer().getId()))
				.map(Invoice::getInvoiceNumber)
				.map(InvoiceNumber::asLong)
				.sorted()
				.collect(toList());
		assertThat(invoiceNumber).isEqualTo(Utils.last(invoiceNumbersOfCustomer));
	}

	@Test
	public void given_logged_customer_user_when_pay_cash_then_should_create_payment_order_and_close_bill() {
		final String accessToken = loginUser(CUSTOMER_2_USER_1_EMAIL, CUSTOMER_2_USER_1_PASSWORD);

		final PaymentInvoiceRequest request = new PaymentInvoiceRequest()
				.setInvoiceId(INVOICE_2_BILL_1_DRAFT);

		UUID invoiceId = doPost(mockMvc,"/payments/cash", request, status().isCreated(), accessToken, ResourceIdResponse.class).getId();

		transactionalHelper.executeWithinTransaction(() -> {
			final Invoice invoice = invoiceRepository.getOrFail(invoiceId);
			assertThat(invoice.getPaymentOrder()).isNotEmpty();
			final PaymentOrder paymentOrder = invoice.getPaymentOrder().orElseThrow(() -> new IllegalStateException("Payment Order not found."));
			assertThat(paymentOrder.getBill().getOrders().stream().allMatch(Order::isPaid)).isTrue();
			assertThat(paymentOrder.getBill().getBillStatus()).isEqualTo(CLOSED);
			assertThat(paymentOrder.getOrders()).hasSize(2);
		});
	}

	@Test
	public void given_logged_user_with_bill_when_pay_app_card_then_should_run_async_split_transfer_after_payment_executed() {
		final String accessToken = loginUser(USER_2_EMAIL, USER_2_PASSWORD);

		final PaymentRequest request = new PaymentAppCardRequest()
				.setPciDetailsId(USER_2_PCI_DETAILS_ID)
				.setOrderIds(Sets.newHashSet(ORDER_1_BILL_1_ID, ORDER_2_BILL_1_ID))
				.setTipAmount(2d);

		Order order = orderRepository.getOrFail(ORDER_1_BILL_1_ID);
		final MoneyAmount orderPrice = order.getPrice();
		when(paycometWsClient.executePurchase(any())).thenReturn(executePurchaseWsResponse(orderPrice));
		when(paycometWsClient.getInfoUser(any())).thenReturn(anyCorrectInfoUserWsResponse());
		when(paycometWsClient.splitTransfer(any())).thenReturn(splitTransferWsResponse(orderPrice));

		UUID invoiceId = doPost(mockMvc, "/payments/app-card", request, status().isCreated(), accessToken, PaymentOrderAppCardResponse.class)
				.getInvoiceId();

		await().pollInterval(Duration.ofSeconds(1)).untilAsserted(() -> {
			final Optional<PaymentOrder> paymentOrder = invoiceRepository.getOrFail(invoiceId).getPaymentOrder();
			assertThat(paymentOrder).isNotEmpty();
			assertThat(paymentOrder.get().as(PaymentOrderApp.class).getSplitTransactionDetails()).isNotEmpty();
		});
	}

	@Test
	public void given_logged_user_when_advance_pay_app_card_then_should_execute_purchase_and_split_and_keep_bill_open() {
		var accessToken = loginUser(CUSTOMER_2_USER_1_EMAIL, CUSTOMER_2_USER_1_PASSWORD);

		var request = new AdvancePaymentAppCardRequest()
				.setPciDetailsId(CUSTOMER_USER_1_PCI_DETAILS_ID)
				.setCustomerId(CUSTOMER_2_ID)
				.setLocationIds(Set.of(LOCATION_3_ADVANCE_PAYMENT_ID))
				.setOrders(List.of(new CreateOrderRequest()
						.setProductId(PRODUCT_1_ID)));
		request.setTipAmount(2d);

		var orderPrice = productRepository.getOrFail(PRODUCT_1_ID).getPrice();
		when(paycometWsClient.executePurchase(any())).thenReturn(executePurchaseWsResponse(orderPrice));
		when(paycometWsClient.getInfoUser(any())).thenReturn(anyCorrectInfoUserWsResponse());
		when(paycometWsClient.splitTransfer(any())).thenReturn(splitTransferWsResponse(orderPrice));

		var invoiceId = doPost(mockMvc, "/payments/advance-app-card", request, status().isCreated(), accessToken, PaymentOrderAppCardResponse.class)
				.getInvoiceId();

		await().pollInterval(Duration.ofSeconds(1)).untilAsserted(() ->
				transactionalHelper.executeWithinTransaction(() -> {
					var paymentOrder = invoiceRepository.getOrFail(invoiceId).getPaymentOrder().orElseThrow(() -> new IllegalStateException("Payment Order not found."));
					assertThat(paymentOrder.getBill().getOrders().stream().allMatch(Order::isPaid)).isTrue();
					assertThat(paymentOrder.getBill().getBillStatus()).isEqualTo(OPEN);
					assertThat(paymentOrder.getOrders()).hasSize(1);
					assertThat(paymentOrder.as(PaymentOrderApp.class).getPurchaseTransactionDetails()).isNotEmpty();
					assertThat(paymentOrder.as(PaymentOrderApp.class).getSplitTransactionDetails()).isNotEmpty();
				}));
	}

	@Test
	public void given_logged_user_with_pending_payment_order_when_get_payment_by_id_then_should_return_correct_payment_order_response() {
		final String accessToken = loginUser(USER_2_EMAIL, USER_2_PASSWORD);

		PaymentOrderAppCardResponse response = doGet(mockMvc,
				"/payments/" + PAYMENT_ORDER_PENDING_BILL_1_ID,
				accessToken,
				PaymentOrderAppCardResponse.class);

		assertThat(response.getId()).isEqualTo(PAYMENT_ORDER_PENDING_BILL_1_ID);
		assertThat(response.getStatus()).isEqualTo(PaymentOrderStatus.PENDING);
	}

}
