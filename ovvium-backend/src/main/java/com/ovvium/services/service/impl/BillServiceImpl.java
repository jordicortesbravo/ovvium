package com.ovvium.services.service.impl;

import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.bill.IssueStatus;
import com.ovvium.services.model.bill.Order;
import com.ovvium.services.model.bill.ServiceTime;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.customer.Location;
import com.ovvium.services.model.exception.OvviumDomainException;
import com.ovvium.services.model.exception.ResourceNotFoundException;
import com.ovvium.services.model.product.ProductGroup;
import com.ovvium.services.model.user.User;
import com.ovvium.services.repository.BillRepository;
import com.ovvium.services.repository.transfer.ListBillsCriteria;
import com.ovvium.services.security.AuthenticatedUser;
import com.ovvium.services.security.JwtUtil;
import com.ovvium.services.service.*;
import com.ovvium.services.transfer.command.bill.CreateOrJoinBillCommand;
import com.ovvium.services.transfer.command.order.OrderGroupChoicesCommand;
import com.ovvium.services.transfer.command.order.OrderProductCommand;
import com.ovvium.services.util.ovvium.cache.CollectionWrapper;
import com.ovvium.services.web.controller.bff.v1.transfer.request.bill.JoinBillAndLocationsRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.bill.UpdateBillRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.order.CreateOrderRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.order.UpdateOrderRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.bill.BillResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.factory.BillResponseFactory;
import com.ovvium.services.web.controller.bff.v1.transfer.response.order.OrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.ovvium.services.app.constant.Caches.BILLS;
import static com.ovvium.services.app.constant.Caches.BILLS_BY_CUSTOMER_OPEN;
import static com.ovvium.services.model.bill.BillStatus.OPEN;
import static com.ovvium.services.model.exception.ErrorCode.BILL_ALREADY_OPENED_FOR_LOCATION;
import static com.ovvium.services.model.exception.ErrorCode.BILL_ALREADY_OPENED_FOR_USER;
import static com.ovvium.services.util.ovvium.base.Preconditions.check;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotNull;
import static com.ovvium.services.util.util.basic.Utils.first;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

	private final CustomerService customerService;
	private final ProductService productService;
	private final InvoiceDateService invoiceDateService;
	private final UserService userService;
	private final BillRepository billRepository;
	private final EventPublisherService eventPublisherService;
	private final BillResponseFactory billResponseFactory;

	// We cannot invalidate a list on Spring Cache, so we need a distributed map here
	private final Map<UUID, UUID> billsByUserCache;

	@Autowired
	private BillService self;

	/**
	 * Creates a new Bill for an User in a Location  or joins to an existing bill if it
	 * already exists. If User is not present, it creates a bill without members (an Employee is creating the Bill)
	 */
	@Override
	public Bill createOrJoin(CreateOrJoinBillCommand command) {
		val locations = command.locations();
		val invoiceDate = invoiceDateService.getCurrentInvoiceDate(command.customer());
		if (command.getUser().isPresent()) {
			val user = command.getUser().get();
			checkUserHasClosedBills(user);
			Bill billWithUser = billRepository.getOpenByLocation(first(locations).getId())
					.map(bill -> bill.addMember(user))
					.orElseGet(() -> new Bill(invoiceDate, user, locations));
			log.info("User {} joined Bill {} for Customer {}", user, billWithUser, billWithUser.getCustomerId());
			return self.save(billWithUser);
		} else {
			check(!billRepository.existsOpenByLocations(locations), new OvviumDomainException(BILL_ALREADY_OPENED_FOR_LOCATION));
			Bill bill = new Bill(invoiceDate, locations);
			addEmployeeIfPresent(command.customer(), bill);
			log.info("Employee created Bill {} for Customer {}", bill, bill.getCustomerId());
			return self.save(bill);
		}
	}

	/**
	 * Moves content from one Bill to another when joining tables (Locations).
	 */
	@Override
	public Bill joinBillAndLocations(JoinBillAndLocationsRequest request) {
		Customer customer = customerService.getCustomer(checkNotNull(request.getCustomerId(), "Customer id can´t be null"));
		Bill destinationBill = getBill(checkNotNull(request.getDestinationBillId(), "Bill id can't be null"));
		List<Location> locations = customer.getLocationsById(request.getLocationIds());
		val sourceBillIds = new HashSet<UUID>();
		for (val location : locations) {
			billRepository.getOpenByLocation(location.getId())
					.ifPresentOrElse(
							originalBill -> {
								// Si intentamos hacer join consigo mismo o ya hemos hecho join previamente, lo ignoramos
								if (!originalBill.equals(destinationBill) && originalBill.isOpen()) {
									destinationBill.joinTo(originalBill);
									self.save(originalBill);
									sourceBillIds.add(originalBill.getId());
								}
							},
							() -> destinationBill.addLocation(location)
					);
		}
		log.info("Bills joined. Destination Bill {}, Source Bill {}, Source Location Ids {}", destinationBill.getId(), sourceBillIds, request.getLocationIds());
		return self.save(destinationBill);
	}

	/**
	 * Updates bills fields, including orders for this bill.
	 */
	@Override
	public void updateBill(UpdateBillRequest updateBillRequest) {
		val bill = getBill(updateBillRequest.getBillId());
		updateOrders(bill, updateBillRequest.getOrders());
		updateBillRequest.getEmployeeId()
				.map(id -> customerService.getEmployee(updateBillRequest.getCustomerId(), id))
				.ifPresent(bill::setEmployee);
		self.save(bill);
	}

	@Override
	public void closeBill(Bill bill) {
		bill.close();
		self.save(bill);
		log.info("Bill {} of Customer {} was manually Closed", bill.getId(), bill.getCustomerId());
	}

	@Override
	public ResourceIdResponse addOrder(UUID billId, CreateOrderRequest createOrderRequest) {
		return first(addOrders(billId, singletonList(createOrderRequest)));
	}

	@Override
	public List<ResourceIdResponse> addOrdersToCurrentBill(List<CreateOrderRequest> createOrdersRequest) {
		val user = JwtUtil.getAuthenticatedUserOrFail();
		val bill = self.getCurrentBillOfUser();
		createOrdersRequest.forEach(orderRequest -> orderRequest.setUserId(user.getId()));
		return addOrders(bill.getId(), createOrdersRequest);
	}

	@Override
	public List<ResourceIdResponse> addOrders(UUID billId, List<CreateOrderRequest> createOrdersRequest) {
		val bill = getBill(billId);
		val idResponses = createOrdersRequest.stream()
				.map(orderRequest -> addOrderToBill(bill, orderRequest))
				.map(ResourceIdResponse::new)
				.collect(toList());
		self.save(bill);
		return idResponses;
	}

	@Override
	public List<OrderResponse> getOrders(UUID billId, String issueStatus) {
		val bill = self.getBillResponse(billId);
		return bill.getOrders().stream()//
				.filter(o -> Optional.ofNullable(issueStatus)
						.map(IssueStatus::valueOf)
						.map(status -> o.getIssueStatus().equals(status))
						.orElse(true))
				.collect(toList());
	}

	@Override
	public OrderResponse getOrder(UUID billId, UUID orderId) {
		return self.getBillResponse(billId)
				.getOrders()
				.stream()
				.filter(it -> it.getId().equals(orderId))
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Order not found for Bill"));
	}

	@Override
	public void deleteOrder(UUID billId, UUID orderId) {
		val bill = getBill(billId);
		bill.deleteOrder(orderId);
		self.save(bill);
	}

	@Override
	public Bill getBill(UUID billId) {
		return billRepository.getOrFail(billId);
	}

	@Override
	@Cacheable(BILLS)
	public BillResponse getBillResponse(UUID billId) {
		return billResponseFactory.create(getBill(billId));
	}

	@Override
	@Cacheable(BILLS_BY_CUSTOMER_OPEN)
	public CollectionWrapper<BillResponse> 	listOpenBills(UUID customerId) {
		val customer = customerService.getCustomer(customerId);
		val billsByCustomer = billRepository.listBills(customer, new ListBillsCriteria(OPEN));
		return CollectionWrapper.of(
				customerId,
				billsByCustomer.stream()
						.map(billResponseFactory::create)
						.collect(toList())
		);
	}

	/**
	 * Returns the current Bill of an User.
	 * This will return always the last Bill of the user, so it can return a Closed or Opened Bill.
	 */
	@Override
	public Bill getCurrentBillOfUser(UUID userId) {
		return billRepository.getLastBillOfUser(userId)
				.orElseThrow(() -> new ResourceNotFoundException(format("Open Bill not found for user %s", userId)));
	}

	@Override
	public BillResponse getCurrentBillOfUser() {
		val userId = userService.getAuthenticatedUser().getId();
		val billResponse = Optional.ofNullable(billsByUserCache.get(userId))
				.map(self::getBillResponse)
				.orElseGet(() -> billResponseFactory.create(getCurrentBillOfUser(userId)));
		billsByUserCache.put(userId, billResponse.getId());
		return billResponse;
	}

	@Override
	@Caching(evict = {
			@CacheEvict(value = BILLS, key = "#bill.id"),
			@CacheEvict(value = BILLS_BY_CUSTOMER_OPEN, key = "#bill.getCustomerId()"),
	})
	public Bill save(Bill bill) {
		// When we save the bill to save the orders, currently there is no way to update the Bill update date
		bill.setUpdated(Instant.now());
		removeFromCache(bill.getMembers());
		return billRepository.save(bill);
	}

	private Order addOrderToBill(Bill bill, CreateOrderRequest request) {
		val product = productService.getProduct(request.getProductId());
		return bill.createOrder(new OrderProductCommand(
				request.getUserId().map(bill::getMember).orElse(null),
				product,
				request.getServiceTime().map(ServiceTime::valueOf).orElse(null),
				request.getNotes().orElse(null),
				request.getGroupChoices().stream()
						.map(it -> new OrderGroupChoicesCommand(it.getProductId(), it.getNotes().orElse(null)))
						.collect(Collectors.toUnmodifiableList()),
				request.getSelectedOptions()
		));
	}

	private void updateOrders(Bill bill, List<UpdateOrderRequest> updateOrderRequests) {
		val requestOrderIds = updateOrderRequests.stream()
				.map(UpdateOrderRequest::getOrderId)
				.collect(Collectors.toSet());
		val ordersById = bill.getOrders(requestOrderIds).stream() //
				.collect(toMap(Order::getId, identity()));
		updateOrderRequests.forEach(order -> {
			val billOrder = ordersById.get(order.getOrderId());
			order.getIssueStatus().map(IssueStatus::valueOf).ifPresent(billOrder::setIssueStatus);
			order.getServiceTime().map(ServiceTime::valueOf).ifPresent(billOrder::setServiceTime);
			order.getNotes().ifPresent(billOrder::setNotes);
			order.getGroupChoices().forEach(gc -> {
				val billOrderChoice = billOrder.getChoice(gc.getOrderGroupChoiceId());
				gc.getIssueStatus().map(IssueStatus::valueOf).ifPresent(billOrderChoice::setIssueStatus);
				gc.getNotes().ifPresent(billOrderChoice::setNotes);
				gc.getProductId()
						.map(id-> {
							val pair = billOrder.getProduct().as(ProductGroup.class).getProductItem(id);
							check(pair.getSecond() == billOrderChoice.getServiceTime(), "Product "+ id +" is not configured for this ServiceTime");
							return pair.getFirst();
						})
						.ifPresent(billOrderChoice::setProduct);
			});
			billOrder.clearSelectedOptions();
			billOrder.getProduct().getOptionGroups().forEach(group ->
				group.getOptions()
						.stream()
						.filter(o -> order.getOptions().contains(o.getId()))
						.forEach(billOrder::addSelectedOption)
			);
		});
	}

	private void addEmployeeIfPresent(Customer customer, Bill bill) {
		JwtUtil.getAuthenticatedUserOrFail().getEmployeeUser()
				.map(AuthenticatedUser.EmployeeUser::getId)
				.map(customer::getEmployee)
				.ifPresent(bill::setEmployee);
	}

	private void checkUserHasClosedBills(User user) {
		Boolean isLastBillClosed = billRepository.getLastBillOfUser(user.getId()).map(Bill::isClosed).orElse(true);
		check(isLastBillClosed, new OvviumDomainException(BILL_ALREADY_OPENED_FOR_USER));
	}

	private void removeFromCache(Set<User> members) {
		for (User member : members) {
			billsByUserCache.remove(member.getId());
		}
	}

}
