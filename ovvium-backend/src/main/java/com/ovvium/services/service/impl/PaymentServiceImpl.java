package com.ovvium.services.service.impl;

import com.ovvium.services.model.bill.Order;
import com.ovvium.services.model.bill.ServiceTime;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.exception.OvviumDomainException;
import com.ovvium.services.model.payment.*;
import com.ovvium.services.model.user.User;
import com.ovvium.services.model.user.UserPciDetails;
import com.ovvium.services.repository.PaymentOrderAppCardRepository;
import com.ovvium.services.repository.PaymentOrderRepository;
import com.ovvium.services.repository.client.payment.PaymentClient;
import com.ovvium.services.repository.client.payment.dto.*;
import com.ovvium.services.service.*;
import com.ovvium.services.service.payment.CommissionCalculatorStrategyFactory;
import com.ovvium.services.service.payment.CommissionCardDetails;
import com.ovvium.services.transfer.command.bill.CreateOrJoinBillCommand;
import com.ovvium.services.transfer.command.order.OrderGroupChoicesCommand;
import com.ovvium.services.transfer.command.order.OrderProductCommand;
import com.ovvium.services.transfer.command.payment.AdvancePaymentAppCardCommand;
import com.ovvium.services.transfer.command.payment.PaymentNotificationCommand;
import com.ovvium.services.util.ovvium.base.Validations;
import com.ovvium.services.util.ovvium.optional.OptionalUtils;
import com.ovvium.services.util.ovvium.spring.TransactionalUtils;
import com.ovvium.services.web.controller.bff.v1.transfer.request.order.CreateOrderRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.payment.AddCardTokenRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.payment.PaymentAppCardRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.payment.PaymentInvoiceRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.payment.PaymentOrderAppCardResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.payment.UserCardDataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ovvium.services.app.constant.Caches.PAYMENT_ORDERS;
import static com.ovvium.services.model.exception.ErrorCode.*;
import static com.ovvium.services.model.user.PciProvider.PAYCOMET;
import static com.ovvium.services.util.ovvium.base.Preconditions.*;
import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

	private final BillService billService;
	private final CustomerService customerService;
	private final UserService userService;
	private final PaymentOrderRepository paymentOrderRepository;
	private final PaymentOrderAppCardRepository paymentOrderAppCardRepository;
	private final PaymentClient paymentClient;
	private final EventPublisherService eventPublisherService;
	private final MailService mailService;
	private final LockService lockService;
	private final InvoiceService invoiceService;
	private final ProductService productService;
	private final CommissionCalculatorStrategyFactory commissionCalculatorFactory;

	@Autowired
	private PaymentService self;

	@Override
	public PaymentOrderAppCardResponse pay(PaymentAppCardRequest request) {
		Validations.validate(request);
		val user = userService.getAuthenticatedUser();
		lockOrders(request.getOrderIds());
		try {
			val currentBill = billService.getCurrentBillOfUser(user.getId());
			val orders = currentBill.getOrders(request.getOrderIds());
			val pciDetails = user.getSinglePciDetails(request.getPciDetailsId());
			val paymentOrder = new PaymentOrderApp(currentBill, user, PAYCOMET);
			paymentOrder.addOrders(orders);
			request.getTipAmount()
					.map(MoneyAmount::ofDouble)
					.map(Tip::new)
					.ifPresent(paymentOrder::setTip);
			paymentOrder.setSplitCustomerAmount(calculateAmountToSplitTransfer(paymentOrder, user, pciDetails));
			val response = executePurchase(paymentOrder, user, pciDetails);
			if (response.getChallengeUrl().isEmpty()) {
				// Payment executed, no need to wait for notification
				confirmPaymentAndCreateInvoice(paymentOrder,
						response.getAuthCode(),
						response.getMoneyAmount());
				billService.save(paymentOrder.getBill()); // to refresh caches
			} else {
				log.info("PaymentOrder is Pending of confirmation: {}", paymentOrder.getId());
			}
			self.save(paymentOrder);
			return new PaymentOrderAppCardResponse(
					paymentOrder,
					response.getChallengeUrl().orElse(null)
			);
		} finally {
			unlockOrders(request.getOrderIds());
		}
	}

	@Override
	public ResourceIdResponse pay(PaymentInvoiceRequest request) {
		val invoice = invoiceService.getInvoice(checkNotNull(request.getInvoiceId(), "Invoice must not be null"));
		Set<Order> orders = invoice.getOrders();
		Set<UUID> orderIds = orders.stream().map(Order::getId).collect(toSet());
		lockOrders(orderIds);
		try {
			val bill = billService.getBill(invoice.getBillId());
			val paymentOrder = new PaymentOrder(invoice, bill, request.getType())
					.addOrders(orders);
			orders.forEach(Order::markAsPaid);
			if (bill.isAllPaid()) {
				bill.close();
			}
			billService.save(bill);
			self.save(paymentOrder);
			log.info("PaymentOrder {} executed, created invoice {}", paymentOrder.getId(), invoice.getId());
			return new ResourceIdResponse(invoice);
		} finally {
			unlockOrders(orderIds);
		}
	}

	@Override
	public PaymentOrderAppCardResponse payAndOrder(AdvancePaymentAppCardCommand command) {
		val user = command.currentUser();
		val bill = billService.createOrJoin(
				new CreateOrJoinBillCommand(user, command.customer(), command.locations())
			).removeMember(user); // user should not be added to Bill
		val paymentOrder = new PaymentOrderApp(bill, user, PAYCOMET)
				.addOrders(
						command.orders().stream()
								.map((req) -> createOrder(user, req))
								.collect(Collectors.toUnmodifiableSet())
				)
				.setTip(command.tip())
				.as(PaymentOrderApp.class);
		paymentOrder.setSplitCustomerAmount(calculateAmountToSplitTransfer(paymentOrder, user, command.userPciDetails()));
		val response = executePurchase(paymentOrder, user, command.userPciDetails());
		if (response.getChallengeUrl().isEmpty()) {
			confirmAdvancePayment(paymentOrder, response.getAuthCode(), response.getMoneyAmount());
		} else {
			log.info("Advance PaymentOrder is Pending of confirmation: {}", paymentOrder.getId());
		}
		self.save(paymentOrder);
		billService.save(bill);
		return new PaymentOrderAppCardResponse(
				paymentOrder,
				response.getChallengeUrl().orElse(null)
		);
	}

	@Override
	public void updatePaymentOnNotification(PaymentNotificationCommand command) {
		val paymentOrder = command.paymentOrderApp();
		val bill = paymentOrder.getBill();
		val orderIds = paymentOrder.getOrders().stream().map(Order::getId).collect(toSet());
		lockOrders(orderIds);
		try{
			log.info("Received notification for PciTransactionId {}", paymentOrder.getPciTransactionId());
			paymentClient.checkForErrors(new CheckClientErrorsRequest(command.getError().map(String::valueOf).orElse(null),
					null, false, null));
			val isAdvanced = paymentOrder.getBill().isFromAdvancePayment();
			if(isAdvanced) {
				confirmAdvancePayment(paymentOrder, command.authCode(), command.getAmount());
			} else {
				confirmPaymentAndCreateInvoice(paymentOrder, command.authCode(), command.getAmount());
			}
		} catch (UnsuccessfulPaymentClientException exc) {
			paymentOrder.cancelPayment();
			log.error("PaymentOrder '%s' Cancelled, notification returned error".formatted(paymentOrder.getId()), exc);
		} finally {
			self.save(paymentOrder);
			billService.save(bill);
			unlockOrders(orderIds);
		}
	}

	@Override
	public PaymentOrder getPaymentOrder(UUID id) {
		return paymentOrderRepository.getOrFail(id);
	}

	@Override
	public ResourceIdResponse addCardToken(AddCardTokenRequest request) {
		val token = checkNotBlank(request.getToken(), "Card Token cannot be blank");
		val currentUser = userService.getAuthenticatedUser();
		val tokenResponse = paymentClient.addUserToken(new AddUserTokenRequest(token, currentUser));
		val userPciDetails = currentUser.addUserPciDetail(tokenResponse.getUserId(), tokenResponse.getUserToken());
		userService.save(currentUser);
		return new ResourceIdResponse(userPciDetails);
	}

	@Override
	public void removeCardToken(UUID pciDetailsId) {
		val currentUser = userService.getAuthenticatedUser();
		val removedPciDetail = currentUser.removeUserPciDetail(pciDetailsId);
		val request = new RemoveUserTokenRequest(currentUser, removedPciDetail.getProviderUserId(), removedPciDetail.getProviderReferenceToken());
		paymentClient.removeUserToken(request);
		userService.save(currentUser);
	}

	@Override
	public List<UserCardDataResponse> getCardsOfCurrentUser() {
		val currentUser = userService.getAuthenticatedUser();
		return currentUser.getPciDetails().stream()
				.map(pciDetails -> getUserCardDataResponse(currentUser, pciDetails))
				.collect(Collectors.toList());
	}


	private Order createOrder(User user, CreateOrderRequest req) {
		return Order.oneOfValues(new OrderProductCommand(
				user,
				productService.getProduct(req.getProductId()),
				req.getServiceTime().map(ServiceTime::valueOf).orElse(null),
				req.getNotes().orElse(null),
				req.getGroupChoices().stream()
						.map(it -> new OrderGroupChoicesCommand(it.getProductId(), it.getNotes().orElse(null)))
						.collect(Collectors.toUnmodifiableList()),
				req.getSelectedOptions()
		));
	}

	private void confirmPaymentAndCreateInvoice(PaymentOrderApp paymentOrder, String authCode, MoneyAmount moneyAmount) {
		val bill = paymentOrder.getBill();
		paymentOrder.getOrders().forEach(Order::markAsPaid);
		if (!bill.isFromAdvancePayment() && isNotEmpty(bill.getOrders()) && bill.isAllPaid()) {
			bill.close();
		}
		paymentOrder.setPurchaseTransactionDetails(new ProviderTransactionDetails(authCode, moneyAmount));
		paymentOrder.confirmAsPaid();
		eventPublisherService.emit(new PaymentExecutedEvent(paymentOrder.getId()));
		val invoice = invoiceService.createInvoice(paymentOrder);
		log.info("PaymentOrderApp {} confirmed, created invoice {}", paymentOrder.getId(), invoice.getId());
	}

	private void confirmAdvancePayment(PaymentOrderApp paymentOrder, String authCode, MoneyAmount amount) {
		confirmPaymentAndCreateInvoice(paymentOrder, authCode, amount);
		paymentOrder.getBill().addOrdersFrom(paymentOrder); // Move orders to Bill cause payment is confirmed
	}

	private ExecutePurchaseResponse executePurchase(PaymentOrderApp paymentOrder, User user, UserPciDetails pciDetails) {
		return paymentClient.executePurchase(new ExecutePurchaseRequest(
				paymentOrder.getBill().getCustomerId(),
				user,
				pciDetails,
				paymentOrder.getTotalAmount(),
				paymentOrder.getPciTransactionId(),
				paymentOrder.getOrders()
		));
	}

	@Override
	public void executeSplitTransfer(UUID paymentOrderId) {
		try {
			val paymentOrder = paymentOrderAppCardRepository.getOrFail(paymentOrderId);
			check(paymentOrder.getSplitTransactionDetails().isEmpty(), new OvviumDomainException(PAYMENT_ORDER_ALREADY_SPLIT));

			val purchaseDetails = paymentOrder.getPurchaseTransactionDetails()
					.orElseThrow(() -> new OvviumDomainException(PAYMENT_ORDER_NOT_EXECUTED));
			val customer = getCustomer(paymentOrder);
			val response = paymentClient.splitTransfer(new SplitTransferRequest(
					customer,
					paymentOrder.getPciTransactionId(),
					purchaseDetails.getTransactionId(),
					customer.getPciSplitUserId(),
					paymentOrder.getSplitCustomerAmount()
			));
			paymentOrder.setSplitTransactionDetails(new ProviderTransactionDetails(response.getTransferAuthCode(), response.getMoneyAmount()));
			self.save(paymentOrder);
			log.info("Executed SplitTransfer successfully for PaymentOrder {}", paymentOrder.getId());
		} catch (Exception e) {
			mailService.notifyError(format("Error on split transfer on payment order Id %s", paymentOrderId), e);
			throw e;
		}
	}

	@Override
	@CacheEvict(value = PAYMENT_ORDERS, key = "#paymentOrder.id")
	public void save(PaymentOrder paymentOrder) {
		if (paymentOrder instanceof PaymentOrderApp paymentOrderApp) {
			paymentOrderAppCardRepository.save(paymentOrderApp);
		} else {
			paymentOrderRepository.save(paymentOrder);
		}
	}

	/**
	 * Calculate the amount to transfer to the Customer.
	 * We need to check the total amount is greater or equal than the commission, otherwise the provider would
	 * charge us.
	 */
	private MoneyAmount calculateAmountToSplitTransfer(PaymentOrderApp paymentOrder, User user, UserPciDetails pciDetails) {
		val totalAmount = paymentOrder.getTotalAmount();
		val customer = getCustomer(paymentOrder);
		val config = customer.getCommissionConfig();
		val calculator = commissionCalculatorFactory.getStrategy(config.getStrategy());
		val ovviumCommission = calculator.calculate(config, paymentOrder.getTipAmount(), getCommissionCardDetails(user, pciDetails));
		if (totalAmount.isGreaterOrEqualThan(ovviumCommission)) {
			return totalAmount.subtract(ovviumCommission);
		}
		throw new OvviumDomainException(PAYMENT_SPLIT_AMOUNT_IS_NOT_CORRECT);
	}

	private Customer getCustomer(PaymentOrder paymentOrder) {
		return customerService.getCustomer(paymentOrder.getBill().getCustomerId());
	}

	private CommissionCardDetails getCommissionCardDetails(User user, UserPciDetails pciDetails) {
		val details = paymentClient.getInfoUser(new InfoUserRequest(user, pciDetails));
		val isoCountry = OptionalUtils.ofBlankable(details.getCardCountry())
				.orElseGet(() -> {
					log.error("Iso Country is empty ");
					return "ESP";
				});
		return new CommissionCardDetails(CardCategory.safeGet(details.getCardCategory()), isoCountry.toUpperCase());
	}

	private UserCardDataResponse getUserCardDataResponse(User user, UserPciDetails pciDetails) {
		val userRequest = new InfoUserRequest(user, pciDetails);
		val userCardResponse = paymentClient.getInfoUser(userRequest);
		return new UserCardDataResponse(pciDetails.getId(), userCardResponse);
	}

	private synchronized void lockOrders(Set<UUID> orderIds) {
		orderIds.stream()
				.map(UUID::toString)
				.forEachOrdered(orderId -> {
					if (!lockService.tryLock(orderId)) {
						log.error("Order {} is locked by another thread", orderId);
						throw new OvviumDomainException(ORDER_IS_BEING_PAID);
					}
				});
	}

	private synchronized void unlockOrders(Set<UUID> orderIds) {
		TransactionalUtils.executeAfterTransaction(() ->
				orderIds.stream()
						.map(UUID::toString)
						.forEach(lockService::unlock));
	}

}
