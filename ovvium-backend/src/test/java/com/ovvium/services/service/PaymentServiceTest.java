package com.ovvium.services.service;

import com.ovvium.mother.builder.*;
import com.ovvium.mother.model.*;
import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.bill.Order;
import com.ovvium.services.model.bill.OrderGroupChoice;
import com.ovvium.services.model.bill.ServiceTime;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.customer.CommissionConfig;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.customer.Location;
import com.ovvium.services.model.exception.ErrorCode;
import com.ovvium.services.model.exception.OvviumDomainException;
import com.ovvium.services.model.exception.ResourceNotFoundException;
import com.ovvium.services.model.payment.*;
import com.ovvium.services.model.user.User;
import com.ovvium.services.model.user.UserPciDetails;
import com.ovvium.services.repository.PaymentOrderAppCardRepository;
import com.ovvium.services.repository.PaymentOrderRepository;
import com.ovvium.services.repository.client.payment.PaymentClient;
import com.ovvium.services.repository.client.payment.dto.*;
import com.ovvium.services.repository.client.payment.exception.PaycometException;
import com.ovvium.services.repository.client.payment.ws.dto.SplitTransferWsResponse;
import com.ovvium.services.security.exception.NotAuthenticatedException;
import com.ovvium.services.service.impl.PaymentServiceImpl;
import com.ovvium.services.service.payment.BasicCommissionCalculator;
import com.ovvium.services.service.payment.CommissionCalculatorStrategyFactory;
import com.ovvium.services.transfer.command.payment.AdvancePaymentAppCardCommand;
import com.ovvium.services.transfer.command.payment.PaymentNotificationCommand;
import com.ovvium.services.util.util.basic.Utils;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import com.ovvium.services.web.controller.bff.v1.transfer.request.order.CreateOrderRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.payment.AddCardTokenRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.payment.PaymentAppCardRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.payment.PaymentInvoiceRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.payment.UserCardDataResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ovvium.mother.model.InvoiceMother.anyInvoice;
import static com.ovvium.mother.model.OrderMother.ORDER_PATATAS_BRAVAS_ID;
import static com.ovvium.mother.model.PaycometMother.*;
import static com.ovvium.mother.model.ProductMother.CERVEZA_ID;
import static com.ovvium.mother.model.ProductMother.getCervezaProduct;
import static com.ovvium.services.model.bill.BillStatus.CLOSED;
import static com.ovvium.services.model.bill.BillStatus.OPEN;
import static com.ovvium.services.model.bill.PaymentStatus.PAID;
import static com.ovvium.services.model.bill.PaymentStatus.PENDING;
import static com.ovvium.services.model.common.MoneyAmount.ZERO;
import static com.ovvium.services.model.exception.ErrorCode.PAYMENT_SPLIT_AMOUNT_IS_NOT_CORRECT;
import static com.ovvium.services.model.payment.CommissionStrategy.BASIC;
import static com.ovvium.services.model.payment.PaymentOrderStatus.CANCELLED;
import static com.ovvium.services.model.payment.PaymentOrderStatus.CONFIRMED;
import static com.ovvium.services.model.payment.PaymentType.APP_CARD;
import static com.ovvium.services.model.payment.PaymentType.CASH;
import static com.ovvium.services.model.user.PciProvider.PAYCOMET;
import static com.ovvium.services.util.util.basic.Utils.first;
import static com.ovvium.utils.MockitoUtils.mockRepository;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {

	private static final String ANY_TOKEN = "atoken";
	private static final double MINIMUM_COMMISSION = 0.2;
	private static final double TIPS_PERCENTAGE = 0.5;

	// SUT
	private PaymentService paymentService;

	private BillService billService;
	private UserService userService;
	private CustomerService customerService;
	private PaymentOrderRepository paymentOrderRepository;
	private PaymentOrderAppCardRepository paymentOrderAppCardRepository;
	private PaymentClient paymentClient;
	private EventPublisherService eventPublisherService;
	private MailService mailService;
	private LockService lockService;
	private InvoiceService invoiceService;
	private CommissionCalculatorStrategyFactory commissionCalculatorStrategyFactory;
	private ProductService productService;

	@Before
	public void setUp() {
		billService = mock(BillService.class);
		userService = mock(UserService.class);
		paymentClient = mock(PaymentClient.class);
		customerService = mock(CustomerService.class);
		paymentOrderRepository = mockRepository(PaymentOrderRepository.class);
		paymentOrderAppCardRepository = mockRepository(PaymentOrderAppCardRepository.class);
		eventPublisherService = mock(EventPublisherService.class);
		mailService = mock(MailService.class);
		lockService = mock(LockService.class);
		invoiceService = mock(InvoiceService.class);
		commissionCalculatorStrategyFactory = mock(CommissionCalculatorStrategyFactory.class);
		productService = mock(ProductService.class);

		paymentService = new PaymentServiceImpl(
				billService,
				customerService,
				userService,
				paymentOrderRepository,
				paymentOrderAppCardRepository,
				paymentClient,
				eventPublisherService,
				mailService,
				lockService,
				invoiceService,
				productService,
				commissionCalculatorStrategyFactory
		);
		ReflectionUtils.set(paymentService, "self", paymentService);
	}

	@Test
	public void given_empty_token_when_add_card_token_then_throw_exception() {
		AddCardTokenRequest emptyRequest = new AddCardTokenRequest();

		assertThatThrownBy(
				() -> paymentService.addCardToken(emptyRequest)
		).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Card Token cannot be blank");
	}

	@Test
	public void given_not_found_user_when_add_card_token_then_throw_exception() {
		AddCardTokenRequest anyRequest = new AddCardTokenRequest();
		anyRequest.setToken(ANY_TOKEN);

		when(userService.getAuthenticatedUser()).thenThrow(new NotAuthenticatedException());

		assertThatThrownBy(
				() -> paymentService.addCardToken(anyRequest)
		).isInstanceOf(NotAuthenticatedException.class)
				.hasMessage("User is not authenticated.");
	}

	@Test
	public void given_not_responding_paymentclient_when_add_card_token_then_throw_exception() {
		AddCardTokenRequest anyRequest = new AddCardTokenRequest();
		anyRequest.setToken(ANY_TOKEN);

		User currentUser = UserMother.getUserJorge();
		when(userService.getAuthenticatedUser()).thenReturn(currentUser);
		when(paymentClient.addUserToken(any())).thenThrow(new RuntimeException());

		assertThatThrownBy(
				() -> paymentService.addCardToken(anyRequest)
		).isInstanceOf(RuntimeException.class);
	}


	@Test
	public void given_correct_payment_client_response_when_add_card_token_then_add_user_pci_details_to_user() {
		AddCardTokenRequest anyRequest = new AddCardTokenRequest();
		anyRequest.setToken(ANY_TOKEN);

		User currentUser = UserMother.getUserJorge();
		when(userService.getAuthenticatedUser()).thenReturn(currentUser);
		AddUserTokenResponse response = new AddUserTokenResponse("a_user_id", "a_reference_token");
		when(paymentClient.addUserToken(any())).thenReturn(response);

		paymentService.addCardToken(anyRequest);

		verify(userService).save(currentUser);
		assertThat(currentUser.getPciDetails()).isNotEmpty();
		UserPciDetails pciDetail = first(currentUser.getPciDetails());
		assertThat(pciDetail.getPciProvider()).isEqualTo(PAYCOMET);
		assertThat(pciDetail.getProviderUserId()).isEqualTo(response.getUserId());
		assertThat(pciDetail.getProviderReferenceToken()).isEqualTo(response.getUserToken());
	}

	@Test
	public void given_correct_user_pci_details_id_when_remove_card_token_then_remove_user_pci_details_from_user_and_pci_provider() {
		// given
		final User currentUser = User.basicUser("Jorge", "jorge@email.com", "12345678");
		final UserPciDetails pciDetails = currentUser.addUserPciDetail("user-id", "user-token");

		when(userService.getAuthenticatedUser()).thenReturn(currentUser);
		RemoveUserTokenResponse response = new RemoveUserTokenResponse(1);
		when(paymentClient.removeUserToken(any())).thenReturn(response);

		// when
		paymentService.removeCardToken(pciDetails.getId());

		// then
		verify(userService).save(currentUser);
		assertThat(currentUser.getPciDetails()).isEmpty();

		final RemoveUserTokenRequest capturedRequest = captureRemoveUserTokenRequest();
		assertThat(capturedRequest.userId()).isEqualTo(pciDetails.getProviderUserId());
		assertThat(capturedRequest.userToken()).isEqualTo(pciDetails.getProviderReferenceToken());
	}

	@Test
	public void given_not_found_user_when_get_user_cards_then_throw_exception() {
		when(userService.getAuthenticatedUser()).thenThrow(new NotAuthenticatedException());

		assertThatThrownBy(
				() -> paymentService.getCardsOfCurrentUser()
		).isInstanceOf(NotAuthenticatedException.class)
				.hasMessage("User is not authenticated.");
	}

	@Test
	public void given_logged_user_when_get_user_cards_then_return_user_cards() {
		User currentUser = UserMother.getUserWithCardData();
		when(userService.getAuthenticatedUser()).thenReturn(currentUser);
		when(paymentClient.getInfoUser(any())).thenReturn(anyInfoUserResponse());

		final List<UserCardDataResponse> cardsOfCurrentUser = paymentService.getCardsOfCurrentUser();

		assertThat(cardsOfCurrentUser).isNotEmpty();
		assertThat(cardsOfCurrentUser.get(0).getPciDetailsId()).isEqualTo(Utils.first(currentUser.getPciDetails()).getId());
	}

	@Test
	public void given_empty_app_card_payment_request_when_create_app_card_payment_then_throw_exception() {
		final PaymentAppCardRequest appCardRequest = new PaymentAppCardRequest();

		assertThatThrownBy(
				() -> paymentService.pay(appCardRequest)
		).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("pciDetailsId must not be null");
	}

	@Test
	public void given_app_card_payment_request_with_one_order_when_create_app_card_payment_then_create_payment_order_and_close_bill() {
		final User user = UserMother.getUserWithCardData();
		final UserPciDetails pciDetails = Utils.first(user.getPciDetails());
		final Bill bill = BillMother.getOpenedBillWithOpenOrder();
		final Order order = Utils.first(bill.getOrders());
		mockCustomerWithBasicConfig(TIPS_PERCENTAGE);

		final PaymentAppCardRequest appCardRequest = new PaymentAppCardRequest()
				.setPciDetailsId(pciDetails.getId());
		appCardRequest.setOrderIds(singleton(order.getId()));

		mockAppCardPayment(user, bill, order);

		// when
		paymentService.pay(appCardRequest);

		final PaymentOrderApp paymentOrder = capturePaymentOrderAppCard();

		assertThat(paymentOrder.getOrders()).hasSize(1);
		assertThat(Utils.first(paymentOrder.getOrders()).getPaymentStatus()).isEqualTo(PAID);
		assertThat(paymentOrder.getOrders()).isEqualTo(bill.getOrders());
		assertThat(paymentOrder.getPaymentType()).isEqualTo(APP_CARD);
		assertThat(paymentOrder.getPurchaseTransactionDetails()).isNotEmpty();
		assertThat(paymentOrder.getPurchaseTransactionDetails().get().getAmount()).isEqualTo(order.getPrice());
		assertThat(paymentOrder.getTip()).isEmpty();
		assertThat(paymentOrder.getTotalAmount()).isEqualTo(order.getPrice());
		assertThat(paymentOrder.getBill().getBillStatus()).isEqualTo(CLOSED);
		assertThat(paymentOrder.getBill().getLocations()).isNotEmpty();
		assertThat(paymentOrder.getStatus()).isEqualTo(CONFIRMED);
	}

	@Test
	public void given_app_card_payment_request_with_multiple_order_when_create_app_card_payment_then_create_payment_order_and_not_close_bill() {
		final User user = UserMother.getUserWithCardData();
		final UserPciDetails pciDetails = Utils.first(user.getPciDetails());
		final Bill bill = BillMother.getOpenedBillWithMultipleOrders();
		final Order order = Utils.first(bill.getOrders());
		mockCustomerWithBasicConfig(TIPS_PERCENTAGE);

		final PaymentAppCardRequest appCardRequest = new PaymentAppCardRequest()
				.setPciDetailsId(pciDetails.getId());
		appCardRequest.setOrderIds(singleton(order.getId()));

		mockAppCardPayment(user, bill, order);

		// when
		paymentService.pay(appCardRequest);

		final PaymentOrderApp paymentOrder = capturePaymentOrderAppCard();

		assertThat(paymentOrder.getOrders()).hasSize(1);
		assertThat(paymentOrder.getOrders()).isNotEqualTo(bill.getOrders());
		assertThat(paymentOrder.getBill().getBillStatus()).isEqualTo(OPEN);
		assertThat(paymentOrder.getPaymentType()).isEqualTo(APP_CARD);
		assertThat(paymentOrder.getPurchaseTransactionDetails()).isNotEmpty();
		assertThat(paymentOrder.getPurchaseTransactionDetails().get().getAmount()).isEqualTo(order.getPrice());
		assertThat(paymentOrder.getTip()).isEmpty();
		assertThat(paymentOrder.getTotalAmount()).isEqualTo(order.getPrice());
		assertThat(paymentOrder.getSplitTransactionDetails()).isEmpty();
	}


	@Test
	public void given_app_card_payment_request_when_create_app_card_payment_then_call_payment_client_with_correct_request() {
		final User user = UserMother.getUserWithCardData();
		final UserPciDetails pciDetails = Utils.first(user.getPciDetails());
		final Bill bill = BillMother.getOpenedBillWithMultipleOrders();
		final Order order = Utils.first(bill.getOrders());
		mockCustomerWithBasicConfig(TIPS_PERCENTAGE);

		final PaymentAppCardRequest appCardRequest = new PaymentAppCardRequest()
				.setPciDetailsId(pciDetails.getId());
		appCardRequest.setOrderIds(singleton(order.getId()));

		mockAppCardPayment(user, bill, order);

		// when
		var response = paymentService.pay(appCardRequest);

		final PaymentOrderApp paymentOrder = capturePaymentOrderAppCard();
		final ExecutePurchaseRequest executePurchaseRequest = captureExecutePurchaseRequest();

		assertThat(executePurchaseRequest.amount()).isEqualTo(paymentOrder.getTotalAmount());
		assertThat(executePurchaseRequest.orderId()).isEqualTo(paymentOrder.getPciTransactionId());
		assertThat(executePurchaseRequest.orders()).isEqualTo(paymentOrder.getOrders());
		assertThat(executePurchaseRequest.userPciDetails().getProviderUserId()).isEqualTo(pciDetails.getProviderUserId());
		assertThat(executePurchaseRequest.userPciDetails().getProviderReferenceToken()).isEqualTo(pciDetails.getProviderReferenceToken());
		assertThat(response.getChallengeUrl()).isNull();
	}

	@Test
	public void given_app_card_payment_request_with_challenge_url_when_create_app_card_payment_then_return_challenge_url_and_not_pay_orders() {
		final User user = UserMother.getUserWithCardData();
		final UserPciDetails pciDetails = Utils.first(user.getPciDetails());
		final Bill bill = BillMother.getOpenedBillWithOpenOrder();
		final Order order = Utils.first(bill.getOrders());
		mockCustomerWithBasicConfig(TIPS_PERCENTAGE);

		final PaymentAppCardRequest appCardRequest = new PaymentAppCardRequest()
				.setPciDetailsId(pciDetails.getId());
		appCardRequest.setOrderIds(singleton(order.getId()));

		when(userService.getAuthenticatedUser()).thenReturn(user);
		when(billService.getCurrentBillOfUser(user.getId())).thenReturn(bill);
		when(paymentClient.executePurchase(any())).thenReturn(PaycometMother.executePurchaseWithChallengeUrlResponse(order.getPrice()));
		when(paymentClient.getInfoUser(any())).thenReturn(PaycometMother.anyInfoUserResponse());
		when(invoiceService.createInvoice(any())).thenReturn(anyInvoice());
		when(lockService.tryLock(any())).thenReturn(true);
		when(commissionCalculatorStrategyFactory.getStrategy(BASIC)).thenReturn(new BasicCommissionCalculator());

		// when
		var response = paymentService.pay(appCardRequest);

		final PaymentOrderApp paymentOrder = capturePaymentOrderAppCard();

		assertThat(paymentOrder.getOrders()).hasSize(1);
		assertThat(paymentOrder.getOrders()).isEqualTo(bill.getOrders());
		assertThat(paymentOrder.getPurchaseTransactionDetails()).isEmpty();
		assertThat(paymentOrder.getBill().getBillStatus()).isEqualTo(OPEN);
		assertThat(paymentOrder.getStatus()).isEqualTo(PaymentOrderStatus.PENDING);
		assertThat(response.getChallengeUrl()).isEqualTo(ANY_CHALLENGE_URL);
	}

	@Test
	public void given_executed_card_payment_request_when_create_app_card_payment_then_should_emit_paymentExecutedEvent() {
		final User user = UserMother.getUserWithCardData();
		final UserPciDetails pciDetails = Utils.first(user.getPciDetails());
		final Bill bill = BillMother.getOpenedBillWithMultipleOrders();
		final Order order = Utils.first(bill.getOrders());
		mockCustomerWithBasicConfig(TIPS_PERCENTAGE);

		final PaymentAppCardRequest appCardRequest = new PaymentAppCardRequest()
				.setPciDetailsId(pciDetails.getId());
		appCardRequest.setOrderIds(singleton(order.getId()));

		mockAppCardPayment(user, bill, order);

		// when
		paymentService.pay(appCardRequest);

		verify(eventPublisherService, times(1)).emit(any(PaymentExecutedEvent.class));
	}

	@Test
	public void given_wrong_order_id_on_card_payment_request_when_create_app_card_payment_then_throw_exception() {
		final User user = UserMother.getUserWithCardData();
		final UserPciDetails pciDetails = Utils.first(user.getPciDetails());
		final Bill bill = BillMother.getOpenedBillWithMultipleOrders();

		final PaymentAppCardRequest appCardRequest = new PaymentAppCardRequest()
				.setPciDetailsId(pciDetails.getId());
		UUID notFoundOrderId = UUID.randomUUID();
		appCardRequest.setOrderIds(singleton(notFoundOrderId));

		when(userService.getAuthenticatedUser()).thenReturn(user);
		when(billService.getCurrentBillOfUser(user.getId())).thenReturn(bill);
		when(lockService.tryLock(any())).thenReturn(true);

		// when
		assertThatThrownBy(
				() -> paymentService.pay(appCardRequest)
		).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Order Id " + notFoundOrderId + " not found for bill " + bill.getId());
	}

	@Test
	public void given_only_tip_on_card_payment_request_when_create_app_card_payment_then_create_payment_order_with_only_tip() {
		final User user = UserMother.getUserWithCardData();
		final UserPciDetails pciDetails = Utils.first(user.getPciDetails());
		final Bill bill = BillMother.getOpenedBillWithMultipleOrders();
		mockCustomerWithBasicConfig(TIPS_PERCENTAGE);

		double amount = 2d;
		final MoneyAmount tipAmount = MoneyAmount.ofDouble(amount);
		final PaymentAppCardRequest appCardRequest = new PaymentAppCardRequest()
				.setPciDetailsId(pciDetails.getId());
		appCardRequest.setTipAmount(amount);

		when(userService.getAuthenticatedUser()).thenReturn(user);
		when(billService.getCurrentBillOfUser(user.getId())).thenReturn(bill);
		when(paymentClient.executePurchase(any())).thenReturn(executePurchaseResponse(tipAmount));
		MoneyAmount splitAmount = tipAmount.subtract(MoneyAmount.ofDouble(1));
		when(paymentClient.splitTransfer(any())).thenReturn(PaycometMother.splitTransferResponse(splitAmount));
		when(paymentClient.getInfoUser(any())).thenReturn(PaycometMother.anyInfoUserResponse());
		when(invoiceService.createInvoice(any())).thenReturn(anyInvoice());
		when(commissionCalculatorStrategyFactory.getStrategy(BASIC)).thenReturn(new BasicCommissionCalculator());

		// when
		paymentService.pay(appCardRequest);

		final PaymentOrderApp paymentOrder = capturePaymentOrderAppCard();
		final ExecutePurchaseRequest executePurchaseRequest = captureExecutePurchaseRequest();

		assertThat(paymentOrder.getOrders()).isEmpty();
		assertThat(paymentOrder.getTip()).isNotEmpty();
		assertThat(paymentOrder.getTip().get().getAmount()).isEqualTo(tipAmount);
		assertThat(paymentOrder.getTotalAmount()).isEqualTo(tipAmount);
		assertThat(paymentOrder.getBill().getBillStatus()).isEqualTo(OPEN);
		assertThat(executePurchaseRequest.amount()).isEqualTo(tipAmount);
		assertThat(paymentOrder.getSplitTransactionDetails()).isEmpty();
	}

	// This is a weird use case when a user wants to give a Tip without any orders. The Bill will remain OPEN in this case.

	@Test
	public void given_app_card_payment_request_on_bill_with_no_orders_when_create_app_card_payment_then_create_payment_order_with_only_tip_and_not_closed_bill() {
		final User user = UserMother.getUserWithCardData();
		final UserPciDetails pciDetails = Utils.first(user.getPciDetails());
		final Bill bill = BillMother.getOpenedBillForElBulliLocation();
		mockCustomerWithBasicConfig(TIPS_PERCENTAGE);

		final PaymentAppCardRequest appCardRequest = new PaymentAppCardRequest()
				.setPciDetailsId(pciDetails.getId());
		appCardRequest.setTipAmount(2d);
		MoneyAmount tipAmount = MoneyAmount.ofDouble(appCardRequest.getTipAmount().get());

		when(userService.getAuthenticatedUser()).thenReturn(user);
		when(billService.getCurrentBillOfUser(user.getId())).thenReturn(bill);
		when(paymentClient.executePurchase(any())).thenReturn(executePurchaseResponse(tipAmount));
		when(paymentClient.getInfoUser(any())).thenReturn(PaycometMother.anyInfoUserResponse());
		when(invoiceService.createInvoice(any())).thenReturn(anyInvoice());
		when(commissionCalculatorStrategyFactory.getStrategy(BASIC)).thenReturn(new BasicCommissionCalculator());

		// when
		paymentService.pay(appCardRequest);

		final PaymentOrderApp paymentOrder = capturePaymentOrderAppCard();

		assertThat(paymentOrder.getOrders()).isEmpty();
		assertThat(paymentOrder.getTip()).isNotEmpty();
		assertThat(paymentOrder.getTip().get().getAmount()).isEqualTo(tipAmount);
		assertThat(paymentOrder.getTotalAmount()).isEqualTo(tipAmount);
		assertThat(paymentOrder.getBill().getBillStatus()).isEqualTo(OPEN);
	}
	@Test
	public void given_tip_on_paid_payment_order_when_execute_split_transfer_then_check_right_amount_split_transfer_to_customer() {
		// given
		final User user = UserMother.getUserWithCardData();
		final UserPciDetails pciDetails = Utils.first(user.getPciDetails());
		final Bill bill = BillMother.getOpenedBillWithMultipleOrders();
		double customPercentage = 0.3; // percentage to get from tips
		mockCustomerWithBasicConfig(customPercentage);

		final MoneyAmount tipAmount = MoneyAmount.ofDouble(10d);
		final PaymentAppCardRequest appCardRequest = new PaymentAppCardRequest()
				.setPciDetailsId(pciDetails.getId());
		appCardRequest.setTipAmount(tipAmount.getAmount().doubleValue());

		final MoneyAmount splitAmount = MoneyAmount.ofDouble(7d); // 10 * 0.3

		when(lockService.tryLock(any())).thenReturn(true);
		when(userService.getAuthenticatedUser()).thenReturn(user);
		when(billService.getCurrentBillOfUser(user.getId())).thenReturn(bill);
		when(paymentClient.executePurchase(any())).thenReturn(executePurchaseResponse(tipAmount));
		when(paymentClient.getInfoUser(any())).thenReturn(PaycometMother.anyInfoUserResponse());
		when(invoiceService.createInvoice(any())).thenReturn(anyInvoice());
		when(commissionCalculatorStrategyFactory.getStrategy(BASIC)).thenReturn(new BasicCommissionCalculator());

		// when
		paymentService.pay(appCardRequest);

		final PaymentOrderApp paymentOrder = capturePaymentOrderAppCard();

		// then
		assertThat(paymentOrder.getSplitCustomerAmount()).isEqualTo(splitAmount);
	}

	@Test
	public void given_orders_and_no_tip_on_card_payment_request_when_create_app_card_payment_then_check_right_amount_split_transfer_to_customer() {
		// given
		final User user = UserMother.getUserWithCardData();
		final UserPciDetails pciDetails = Utils.first(user.getPciDetails());
		final Bill bill = BillMother.getOpenedBillWithMultipleOrders();
		mockCustomerWithBasicConfig(TIPS_PERCENTAGE);

		Order order = bill.getOrders().stream()
				.filter(it -> it.getId().equals(ORDER_PATATAS_BRAVAS_ID))
				.findFirst().get();
		final MoneyAmount totalAmount = order.getPrice();

		final PaymentAppCardRequest appCardRequest = new PaymentAppCardRequest()
				.setPciDetailsId(pciDetails.getId());
		appCardRequest.setOrderIds(singleton(order.getId()));

		MoneyAmount splitAmount = MoneyAmount.ofDouble(4.75d);

		when(lockService.tryLock(any())).thenReturn(true);
		when(userService.getAuthenticatedUser()).thenReturn(user);
		when(billService.getCurrentBillOfUser(user.getId())).thenReturn(bill);
		when(paymentClient.executePurchase(any())).thenReturn(executePurchaseResponse(totalAmount));
		when(paymentClient.getInfoUser(any())).thenReturn(PaycometMother.anyInfoUserResponse());
		when(invoiceService.createInvoice(any())).thenReturn(anyInvoice());
		when(commissionCalculatorStrategyFactory.getStrategy(BASIC)).thenReturn(new BasicCommissionCalculator());

		// when
		paymentService.pay(appCardRequest);

		final PaymentOrderApp paymentOrder = capturePaymentOrderAppCard();
		assertThat(paymentOrder.getSplitCustomerAmount()).isEqualTo(splitAmount);
	}

	@Test
	public void given_total_amount_less_than_minimum_commission_on_card_payment_request_when_create_app_card_payment_then_should_throw_exception() {
		// given
		final User user = UserMother.getUserWithCardData();
		final UserPciDetails pciDetails = Utils.first(user.getPciDetails());
		final Bill bill = new BillBuilder()
				.setOrders(singletonList(
						new OrderBuilder().setProduct(new ProductItemBuilder().setPrice(MoneyAmount.ofDouble(0.10f)).build())
				)).build();

		final Order order = bill.getOrders().stream()
				.filter(it -> it.getId().equals(ORDER_PATATAS_BRAVAS_ID))
				.findFirst().get();
		mockCustomerWithBasicConfig(TIPS_PERCENTAGE);

		double amount = 0.05;
		final PaymentAppCardRequest appCardRequest = new PaymentAppCardRequest()
				.setPciDetailsId(pciDetails.getId());
		appCardRequest.setTipAmount(amount);
		appCardRequest.setOrderIds(singleton(order.getId()));

		when(userService.getAuthenticatedUser()).thenReturn(user);
		when(billService.getCurrentBillOfUser(user.getId())).thenReturn(bill);
		when(commissionCalculatorStrategyFactory.getStrategy(BASIC)).thenReturn(new BasicCommissionCalculator());
		when(paymentClient.getInfoUser(any())).thenReturn(PaycometMother.anyInfoUserResponse());

		when(lockService.tryLock(any())).thenReturn(true);
		verify(paymentClient, never()).executePurchase(any()); // importante, nunca debe llamarse en este caso!

		// when, then
		assertThatThrownBy(
				() -> paymentService.pay(appCardRequest)
		).isInstanceOf(OvviumDomainException.class)
				.hasMessage(PAYMENT_SPLIT_AMOUNT_IS_NOT_CORRECT.getMessage());
	}

	@Test
	public void given_total_amount_equal_to_minimum_commission_on_card_payment_request_when_create_app_card_payment_then_check_client_gets_zero_amount() {
		// given
		final User user = UserMother.getUserWithCardData();
		final UserPciDetails pciDetails = Utils.first(user.getPciDetails());
		final Bill bill = BillMother.getOpenedBillWithMultipleOrders();
		mockCustomerWithBasicConfig(TIPS_PERCENTAGE);

		final MoneyAmount tipAmount = MoneyAmount.ofDouble(MINIMUM_COMMISSION);
		final PaymentAppCardRequest appCardRequest = new PaymentAppCardRequest()
				.setPciDetailsId(pciDetails.getId());
		appCardRequest.setTipAmount(tipAmount.getAmount().doubleValue());

		MoneyAmount splitAmount = tipAmount.subtract(MoneyAmount.ofDouble(MINIMUM_COMMISSION));

		when(lockService.tryLock(any())).thenReturn(true);
		when(userService.getAuthenticatedUser()).thenReturn(user);
		when(billService.getCurrentBillOfUser(user.getId())).thenReturn(bill);
		when(paymentClient.executePurchase(any())).thenReturn(executePurchaseResponse(tipAmount));
		when(paymentClient.getInfoUser(any())).thenReturn(PaycometMother.anyInfoUserResponse());
		when(invoiceService.createInvoice(any())).thenReturn(anyInvoice());
		when(commissionCalculatorStrategyFactory.getStrategy(BASIC)).thenReturn(new BasicCommissionCalculator());

		// when
		paymentService.pay(appCardRequest);

		final PaymentOrderApp paymentOrder = capturePaymentOrderAppCard();
		assertThat(paymentOrder.getSplitCustomerAmount()).isEqualTo(splitAmount);
	}


	@Test
	public void given_exception_on_split_transfer_when_execute_split_transfer_then_mail_notification_error_should_be_sent() {
		// given
		when(paymentOrderAppCardRepository.getOrFail(any())).thenThrow( new ResourceNotFoundException("Not found"));

		// when
		assertThatThrownBy(
				() -> paymentService.executeSplitTransfer(UUID.randomUUID())
		).isInstanceOf(ResourceNotFoundException.class);

		// then
		verify(mailService, times(1)).notifyError(any(), any(ResourceNotFoundException.class));
	}

	@Test
	public void given_paycomet_exception_on_split_transfer_when_execute_split_transfer_then_mail_with_wsResponse_should_be_sent() {
		// given
		final PaymentOrderApp paymentOrder = new PaymentOrderAppCardBuilder()
				.setTipAmount(MoneyAmount.ofDouble(2))
				.setPurchaseTransactionDetails(new ProviderTransactionDetails("id", MoneyAmount.ofDouble(1)))
				.build();

		mockCustomerWithBasicConfig(TIPS_PERCENTAGE);

		when(paymentOrderAppCardRepository.getOrFail(paymentOrder.getId())).thenReturn(paymentOrder);
		when(paymentClient.splitTransfer(any())).thenThrow(
				new PaycometException("Error", new SplitTransferWsResponse().setTransferAuthCode("code")));
		when(commissionCalculatorStrategyFactory.getStrategy(BASIC)).thenReturn(new BasicCommissionCalculator());

		// when
		assertThatThrownBy(
				() -> paymentService.executeSplitTransfer(paymentOrder.getId())
		).isInstanceOf(PaycometException.class)
				.hasMessageContaining("code");

		// then
		verify(mailService, times(1)).notifyError(any(), any(PaycometException.class));
	}

	@Test
	public void given_payment_order_already_splitted_when_execute_split_transfer_then_should_throw_exception() {
		// given
		final PaymentOrderApp paymentOrder = new PaymentOrderAppCardBuilder()
				.setTipAmount(MoneyAmount.ofDouble(2))
				.setPurchaseTransactionDetails(new ProviderTransactionDetails("id", MoneyAmount.ofDouble(1)))
				.build();
		paymentOrder.setSplitTransactionDetails(new ProviderTransactionDetails("id", ZERO));

		mockCustomerWithBasicConfig(TIPS_PERCENTAGE);

		when(paymentOrderAppCardRepository.getOrFail(paymentOrder.getId())).thenReturn(paymentOrder);

		assertThatThrownBy(
				() -> paymentService.executeSplitTransfer(paymentOrder.getId())
		).isInstanceOf(OvviumDomainException.class)
				.hasMessage(ErrorCode.PAYMENT_ORDER_ALREADY_SPLIT.getMessage());

		verify(paymentClient, never()).splitTransfer(any());
	}

	@Test
	public void given_payment_request_with_orders_when_create_app_card_payment_then_should_lock_and_unlock_orders() {
		// given
		final User user = UserMother.getUserWithCardData();
		final UserPciDetails pciDetails = Utils.first(user.getPciDetails());
		final Bill bill = BillMother.getOpenedBillWithMultipleOrders();
		final Order order = Utils.first(bill.getOrders());
		mockCustomerWithBasicConfig(TIPS_PERCENTAGE);

		final PaymentAppCardRequest appCardRequest = new PaymentAppCardRequest()
				.setPciDetailsId(pciDetails.getId());
		appCardRequest.setOrderIds(singleton(order.getId()));

		when(userService.getAuthenticatedUser()).thenReturn(user);
		when(billService.getCurrentBillOfUser(user.getId())).thenReturn(bill);
		when(invoiceService.createInvoice(any())).thenReturn(anyInvoice());
		when(paymentClient.executePurchase(any())).thenReturn(executePurchaseResponse(order.getPrice()));
		when(paymentClient.getInfoUser(any())).thenReturn(PaycometMother.anyInfoUserResponse());
		when(lockService.tryLock(any())).thenReturn(true);
		when(commissionCalculatorStrategyFactory.getStrategy(BASIC)).thenReturn(new BasicCommissionCalculator());

		// when
		paymentService.pay(appCardRequest);
		//then
		verify(lockService).tryLock(order.getId().toString());
		verify(lockService).unlock(order.getId().toString());
	}

	@Test
	public void given_payment_request_with_orders_and_wrong_split_transfer_amount_when_create_app_card_payment_then_should_lock_and_unlock_orders() {
		// given
		final User user = UserMother.getUserWithCardData();
		final UserPciDetails pciDetails = Utils.first(user.getPciDetails());
		final Bill bill = new BillBuilder()
				.setOrders(singletonList(
						new OrderBuilder().setProduct(new ProductItemBuilder().setPrice(MoneyAmount.ofDouble(0.10f)).build())
				)).build();
		final Order order = Utils.first(bill.getOrders());
		mockCustomerWithBasicConfig(TIPS_PERCENTAGE);

		final PaymentAppCardRequest appCardRequest = new PaymentAppCardRequest()
				.setPciDetailsId(pciDetails.getId());
		appCardRequest.setOrderIds(singleton(order.getId()));

		when(userService.getAuthenticatedUser()).thenReturn(user);
		when(billService.getCurrentBillOfUser(user.getId())).thenReturn(bill);
		when(paymentClient.executePurchase(any())).thenReturn(executePurchaseResponse(order.getPrice()));
		when(paymentClient.getInfoUser(any())).thenReturn(PaycometMother.anyInfoUserResponse());
		when(lockService.tryLock(any())).thenReturn(true);
		when(commissionCalculatorStrategyFactory.getStrategy(BASIC)).thenReturn(new BasicCommissionCalculator());

		// when
		assertThatThrownBy(
				() -> paymentService.pay(appCardRequest)
		).isInstanceOf(OvviumDomainException.class)
				.hasMessage(PAYMENT_SPLIT_AMOUNT_IS_NOT_CORRECT.getMessage());
		//then
		verify(lockService).tryLock(order.getId().toString());
		verify(lockService).unlock(order.getId().toString());
	}

	@Test
	public void given_app_card_payment_request_with_one_order_when_create_app_card_payment_then_should_create_and_return_payment_order() {
		final User user = UserMother.getUserWithCardData();
		final UserPciDetails pciDetails = Utils.first(user.getPciDetails());
		final Bill bill = BillMother.getOpenedBillWithOpenOrder();
		final Order order = Utils.first(bill.getOrders());
		mockCustomerWithBasicConfig(TIPS_PERCENTAGE);

		final PaymentAppCardRequest appCardRequest = new PaymentAppCardRequest()
				.setPciDetailsId(pciDetails.getId());
		appCardRequest.setOrderIds(singleton(order.getId()));

		Invoice invoice = anyInvoice();
		when(invoiceService.createInvoice(any())).thenReturn(invoice);
		when(userService.getAuthenticatedUser()).thenReturn(user);
		when(billService.getCurrentBillOfUser(user.getId())).thenReturn(bill);
		when(paymentClient.executePurchase(any())).thenReturn(executePurchaseResponse(order.getPrice()));
		when(paymentClient.getInfoUser(any())).thenReturn(PaycometMother.anyInfoUserResponse());
		when(lockService.tryLock(any())).thenReturn(true);
		when(commissionCalculatorStrategyFactory.getStrategy(BASIC)).thenReturn(new BasicCommissionCalculator());

		// when
		ResourceIdResponse resourceIdResponse = paymentService.pay(appCardRequest);

		// then
		final PaymentOrderApp paymentOrder = capturePaymentOrderAppCard();
		verify(invoiceService, times(1)).createInvoice(any());
		assertThat(resourceIdResponse.getId()).isEqualTo(paymentOrder.getId());
	}

	@Test
	public void given_invoice_cash_payment_request_with_one_order_when_create_invoice_payment_then_create_payment_order_and_close_bill() {
		final Bill bill = BillMother.getOpenedBillWithOpenOrder();
		final Invoice invoice = new InvoiceBuilder()
				.setBill(bill)
				.buildDraft();
		final Order order = Utils.first(invoice.getOrders());

		final PaymentInvoiceRequest request = new PaymentInvoiceRequest()
				.setInvoiceId(invoice.getId())
				.setType(CASH);

		mockCashPayment(bill, invoice);

		// when
		ResourceIdResponse resourceIdResponse = paymentService.pay(request);

		final PaymentOrder paymentOrder = capturePaymentOrder();

		assertThat(paymentOrder.getOrders()).hasSize(1);
		assertThat(Utils.first(paymentOrder.getOrders()).getPaymentStatus()).isEqualTo(PAID);
		assertThat(paymentOrder.getOrders()).isEqualTo(bill.getOrders());
		assertThat(paymentOrder.getBill().getBillStatus()).isEqualTo(CLOSED);
		assertThat(paymentOrder.getPaymentType()).isEqualTo(request.getType());
		assertThat(paymentOrder.getTip()).isEmpty();
		assertThat(paymentOrder.getTotalAmount()).isEqualTo(order.getPrice());
		assertThat(resourceIdResponse.getId()).isEqualTo(invoice.getId());
	}

	@Test
	public void given_invoice_payment_request_with_multiple_order_when_create_app_card_payment_then_create_payment_order_and_not_close_bill() {
		final Bill bill = BillMother.getOpenedBillWithMultipleOrders();
		final Order order = Utils.first(bill.getOrders());
		final Invoice invoice = new InvoiceBuilder()
				.setBill(bill)
				.setOrderIds(Collections.singleton(order.getId()))
				.buildDraft();

		final PaymentInvoiceRequest request = new PaymentInvoiceRequest()
				.setInvoiceId(invoice.getId())
				.setType(CASH);

		mockCashPayment(bill, invoice);

		// when
		ResourceIdResponse resourceIdResponse = paymentService.pay(request);

		final PaymentOrder paymentOrder = capturePaymentOrder();

		assertThat(paymentOrder.getOrders()).hasSize(1);
		assertThat(Utils.first(paymentOrder.getOrders()).getPaymentStatus()).isEqualTo(PAID);
		assertThat(paymentOrder.getBill().getBillStatus()).isEqualTo(OPEN);
		assertThat(paymentOrder.getPaymentType()).isEqualTo(request.getType());
		assertThat(paymentOrder.getTip()).isEmpty();
		assertThat(paymentOrder.getTip()).isEmpty();
		assertThat(paymentOrder.getTotalAmount()).isEqualTo(order.getPrice());
		assertThat(resourceIdResponse.getId()).isEqualTo(invoice.getId());
	}

	@Test
	public void given_invoice_payment_request_with_orders_when_create_card_payment_then_should_lock_and_unlock_orders() {
		// given
		final Bill bill = BillMother.getOpenedBillWithMultipleOrders();
		final Order order = Utils.first(bill.getOrders());
		final Invoice invoice = new InvoiceBuilder()
				.setBill(bill)
				.setOrderIds(Collections.singleton(order.getId()))
				.buildDraft();

		final PaymentInvoiceRequest request = new PaymentInvoiceRequest()
				.setInvoiceId(invoice.getId())
				.setType(CASH);

		mockCashPayment(bill, invoice);

		// when
		paymentService.pay(request);
		//then
		verify(lockService).tryLock(order.getId().toString());
		verify(lockService).unlock(order.getId().toString());
	}

	@Test
	public void given_app_card_payment_request_with_one_order_group_choice_when_create_app_card_payment_then_create_payment_order_and_close_bill() {
		final User user = UserMother.getUserWithCardData();
		final UserPciDetails pciDetails = Utils.first(user.getPciDetails());
		final Bill bill = new BillBuilder()
				.setOrders(singletonList(new OrderBuilder()
						.setProduct(ProductMother.getMenuDiarioProduct())
						.setChoices(singleton(new OrderGroupChoice(ServiceTime.SOONER, ProductMother.getCervezaProduct())))))
				.build();
		final Order order = Utils.first(bill.getOrders());
		mockCustomerWithBasicConfig(TIPS_PERCENTAGE);

		final PaymentAppCardRequest appCardRequest = new PaymentAppCardRequest()
				.setPciDetailsId(pciDetails.getId());
		appCardRequest.setOrderIds(singleton(order.getId()));

		mockAppCardPayment(user, bill, order);

		// when
		paymentService.pay(appCardRequest);

		final PaymentOrderApp paymentOrder = capturePaymentOrderAppCard();

		assertThat(paymentOrder.getOrders()).hasSize(1);
		assertThat(Utils.first(paymentOrder.getOrders()).getPaymentStatus()).isEqualTo(PAID);
		assertThat(paymentOrder.getOrders()).isEqualTo(bill.getOrders());
	}

	@Test
	public void given_app_card_advance_payment_request_with_products_when_create_app_card_payment_then_create_new_bill_and_payment_order_and_not_close_bill() {
		var user = UserMother.getUserWithCardData();
		var customer = mockCustomerWithBasicConfig(TIPS_PERCENTAGE);
		var locations = List.of(new LocationBuilder().setAdvancePayment(true).setCustomer(customer).build());
		var billWithUser = getEmptyBillWithUser(user, locations);
		var command = createAdvancePaymentCommand(user, customer, locations);

		mockAdvancePayment(user, billWithUser, executePurchaseResponse(getCervezaProduct().getPrice()));

		// when
		paymentService.payAndOrder(command);

		var paymentOrder = capturePaymentOrderAppCard();

		assertThat(paymentOrder.getOrders()).hasSize(1);
		assertThat(Utils.first(paymentOrder.getOrders()).getPaymentStatus()).isEqualTo(PAID);
		assertThat(paymentOrder.getOrders()).isEqualTo(billWithUser.getOrders());
		assertThat(paymentOrder.getPaymentType()).isEqualTo(APP_CARD);
		assertThat(paymentOrder.getPurchaseTransactionDetails()).isNotEmpty();
		assertThat(paymentOrder.getTip()).isEmpty();
		assertThat(paymentOrder.getBill().getBillStatus()).isEqualTo(OPEN);
		assertThat(paymentOrder.getBill().getLocations()).isNotEmpty();
	}

	@Test
	public void given_app_card_advance_payment_request_with_products_and_existing_bill_when_create_app_card_payment_then_member_should_be_removed_from_bill() {
		var user = UserMother.getUserWithCardData();
		var customer = mockCustomerWithBasicConfig(TIPS_PERCENTAGE);
		var locations = List.of(new LocationBuilder().setAdvancePayment(true).setCustomer(customer).build());
		var billWithUser = getEmptyBillWithUser(user, locations);
		var command = createAdvancePaymentCommand(user, customer, locations);

		mockAdvancePayment(user, billWithUser,  executePurchaseResponse(getCervezaProduct().getPrice()));

		// when
		paymentService.payAndOrder(command);

		assertThat(billWithUser.getMembers()).doesNotContain(user);
	}

	@Test
	public void given_app_card_advance_payment_request_and_challenge_when_create_app_card_payment_then_create_new_bill_but_not_add_new_orders() {
		var user = UserMother.getUserWithCardData();
		var customer = mockCustomerWithBasicConfig(TIPS_PERCENTAGE);
		var locations = List.of(new LocationBuilder().setAdvancePayment(true).setCustomer(customer).build());
		var emptyBill = new BillBuilder().setLocations(locations).build();
		var command = createAdvancePaymentCommand(user, customer, locations);
		mockAdvancePayment(user, emptyBill, executePurchaseWithChallengeUrlResponse(getCervezaProduct().getPrice()));

		// when
		var response = paymentService.payAndOrder(command);

		var paymentOrder = capturePaymentOrderAppCard();

		assertThat(paymentOrder.getOrders()).hasSize(1);
		assertThat(paymentOrder.getBill()).isEqualTo(emptyBill);
		assertThat(Utils.first(paymentOrder.getOrders()).getPaymentStatus()).isEqualTo(PENDING);
		assertThat(emptyBill.getOrders()).isEmpty();
		assertThat(paymentOrder.getPurchaseTransactionDetails()).isEmpty();
		assertThat(response.getChallengeUrl()).isEqualTo(ANY_CHALLENGE_URL);
		verifyNoInteractions(invoiceService, eventPublisherService);
	}

	@Test
	public void given_app_card_advance_payment_for_joined_locations_with_at_least_one_advance_payment_when_create_app_card_payment_then_shold_work_successfully() {
		var user = UserMother.getUserWithCardData();
		var customer = mockCustomerWithBasicConfig(TIPS_PERCENTAGE);
		var locations = List.of(
				new LocationBuilder().setAdvancePayment(true).setCustomer(customer).build(),
				new LocationBuilder().setAdvancePayment(false).setCustomer(customer).withRandomId().build());
		var billWithUser = getEmptyBillWithUser(user, locations);
		var command = createAdvancePaymentCommand(user, customer, locations);

		mockAdvancePayment(user, billWithUser, executePurchaseResponse(getCervezaProduct().getPrice()));

		// when
		paymentService.payAndOrder(command);

		var paymentOrder = capturePaymentOrderAppCard();
		assertThat(paymentOrder.getOrders()).hasSize(1);
	}

	@Test
	public void given_payment_update_notification_when_update_payment_then_should_lock_and_unlock_orders() {
		var paymentOrderApp = PaymentOrderMother.anyAdvancedPaymentOrderAppCard();
		var command = aPaymentNotificationCommand(paymentOrderApp);
		when(lockService.tryLock(any())).thenReturn(true);
		when(invoiceService.createInvoice(any())).thenReturn(anyInvoice());

		// when
		paymentService.updatePaymentOnNotification(command);

		//then
		var orderId = Utils.first(paymentOrderApp.getOrders()).getId();
		verify(lockService).tryLock(orderId.toString());
		verify(lockService).unlock(orderId.toString());
	}

	@Test
	public void given_payment_update_notification_when_update_payment_then_should_check_for_errors_on_payment_client() {
		var paymentOrderApp = PaymentOrderMother.anyAdvancedPaymentOrderAppCard();
		var command = aPaymentNotificationCommand(paymentOrderApp);
		when(lockService.tryLock(any())).thenReturn(true);
		when(invoiceService.createInvoice(any())).thenReturn(anyInvoice());

		// when
		paymentService.updatePaymentOnNotification(command);

		//then
		verify(paymentClient).checkForErrors(new CheckClientErrorsRequest(command.error().toString(), null, false, null));
	}

	@Test
	public void given_error_on_payment_update_notification_when_update_payment_then_should_set_payment_order_as_cancelled() {
		var paymentOrderApp = PaymentOrderMother.anyAdvancedPaymentOrderAppCard();
		var command = aPaymentNotificationCommand(paymentOrderApp);
		when(lockService.tryLock(any())).thenReturn(true);
		doThrow(new UnsuccessfulPaymentClientException("error")).when(paymentClient).checkForErrors(any());

		// when
		paymentService.updatePaymentOnNotification(command);

		//then
		assertThat(paymentOrderApp.getStatus()).isEqualTo(CANCELLED);
		assertThat(paymentOrderApp.getOrders().stream()
				.map(Order::getPaymentStatus)
				.collect(Collectors.toUnmodifiableSet())).isNotEmpty().doesNotContain(PAID);
		verifyNoInteractions(invoiceService);
	}

	@Test
	public void given_successful_advance_payment_update_notification_when_update_payment_then_should_move_orders_to_bill() {
		var paymentOrderApp = PaymentOrderMother.anyAdvancedPaymentOrderAppCard();
		var command = aPaymentNotificationCommand(paymentOrderApp);
		when(lockService.tryLock(any())).thenReturn(true);
		when(invoiceService.createInvoice(any())).thenReturn(anyInvoice());

		// when
		paymentService.updatePaymentOnNotification(command);

		//then
		assertThat(paymentOrderApp.getStatus()).isEqualTo(CONFIRMED);
		assertThat(paymentOrderApp.getBill().getOrders()).containsAll(paymentOrderApp.getOrders());
		verify(invoiceService).createInvoice(paymentOrderApp);
	}

	@Test
	public void given_successful_payment_update_notification_when_update_payment_then_should_update_orders_as_paid() {
		var paymentOrderApp = PaymentOrderMother.anyPaymentOrderAppCard();
		var command = aPaymentNotificationCommand(paymentOrderApp);
		when(lockService.tryLock(any())).thenReturn(true);
		when(invoiceService.createInvoice(any())).thenReturn(anyInvoice());

		// when
		paymentService.updatePaymentOnNotification(command);

		//then
		assertThat(paymentOrderApp.getStatus()).isEqualTo(CONFIRMED);
		assertThat(paymentOrderApp.getPurchaseTransactionDetails()).isNotEmpty();
		assertThat(paymentOrderApp.getPurchaseTransactionDetails().get().getTransactionId()).isEqualTo(command.authCode());
		assertThat(paymentOrderApp.getOrders().stream()
				.map(Order::getPaymentStatus)
				.collect(Collectors.toUnmodifiableSet())).isNotEmpty().containsOnly(PAID);
		verify(invoiceService).createInvoice(paymentOrderApp);
	}

	private void mockAppCardPayment(User user, Bill bill, Order order) {
		when(userService.getAuthenticatedUser()).thenReturn(user);
		when(billService.getCurrentBillOfUser(user.getId())).thenReturn(bill);
		when(paymentClient.executePurchase(any())).thenReturn(executePurchaseResponse(order.getPrice()));
		when(paymentClient.getInfoUser(any())).thenReturn(PaycometMother.anyInfoUserResponse());
		when(invoiceService.createInvoice(any())).thenReturn(anyInvoice());
		when(lockService.tryLock(any())).thenReturn(true);
		when(commissionCalculatorStrategyFactory.getStrategy(BASIC)).thenReturn(new BasicCommissionCalculator());
	}

	private void mockCashPayment(Bill bill, Invoice invoice) {
		when(customerService.getCustomer(any())).thenReturn(CustomerMother.getElBulliCustomer());
		when(invoiceService.getInvoice(any())).thenReturn(invoice);
		when(billService.getBill(any())).thenReturn(bill);
		when(lockService.tryLock(any())).thenReturn(true);
	}

	private void mockAdvancePayment(User user, Bill billWithUser, ExecutePurchaseResponse purchaseResponse) {
		when(userService.getAuthenticatedUser()).thenReturn(user);
		when(billService.createOrJoin(any())).thenReturn(billWithUser);
		when(paymentClient.executePurchase(any())).thenReturn(purchaseResponse);
		when(paymentClient.getInfoUser(any())).thenReturn(PaycometMother.anyInfoUserResponse());
		when(invoiceService.createInvoice(any())).thenReturn(anyInvoice());
		when(commissionCalculatorStrategyFactory.getStrategy(BASIC)).thenReturn(new BasicCommissionCalculator());
		when(productService.getProduct(CERVEZA_ID)).thenReturn(ProductMother.getCervezaProduct());
	}

	private Bill getEmptyBillWithUser(User user, List<Location> locations) {
		return new BillBuilder()
				.setUser(user)
				.setLocations(locations)
				.build();
	}

	private PaymentNotificationCommand aPaymentNotificationCommand(PaymentOrderApp paymentOrderApp) {
		return new PaymentNotificationCommand(
				paymentOrderApp,
				"1",
				"1",
				0,
				null,
				200,
				"EUR"
		);
	}

	private AdvancePaymentAppCardCommand createAdvancePaymentCommand(User user, Customer customer, List<Location> locations) {
		UserPciDetails pciDetails = Utils.first(user.getPciDetails());
		return new AdvancePaymentAppCardCommand(
				customer,
				locations,
				user,
				pciDetails,
				List.of(new CreateOrderRequest().setProductId(CERVEZA_ID)),
				null
		);
	}

	private PaymentOrderApp capturePaymentOrderAppCard() {
		ArgumentCaptor<PaymentOrderApp> captor = ArgumentCaptor.forClass(PaymentOrderApp.class);
		verify(paymentOrderAppCardRepository, times(1)).save(captor.capture());
		return captor.getValue();
	}

	private PaymentOrder capturePaymentOrder() {
		ArgumentCaptor<PaymentOrder> captor = ArgumentCaptor.forClass(PaymentOrder.class);
		verify(paymentOrderRepository, times(1)).save(captor.capture());
		return captor.getValue();
	}

	private ExecutePurchaseRequest captureExecutePurchaseRequest() {
		ArgumentCaptor<ExecutePurchaseRequest> captor = ArgumentCaptor.forClass(ExecutePurchaseRequest.class);
		verify(paymentClient, times(1)).executePurchase(captor.capture());
		return captor.getValue();
	}

	private RemoveUserTokenRequest captureRemoveUserTokenRequest() {
		ArgumentCaptor<RemoveUserTokenRequest> captor = ArgumentCaptor.forClass(RemoveUserTokenRequest.class);
		verify(paymentClient, times(1)).removeUserToken(captor.capture());
		return captor.getValue();
	}


	private Customer mockCustomerWithBasicConfig(double tipsPercentage) {
		Customer customer = new CustomerBuilder()
				.setCommissionConfig(CommissionConfig.basic(tipsPercentage, MINIMUM_COMMISSION))
				.build();
		when(customerService.getCustomer(customer.getId())).thenReturn(customer);
		return customer;
	}

}
