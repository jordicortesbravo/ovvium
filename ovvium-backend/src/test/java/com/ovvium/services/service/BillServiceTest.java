package com.ovvium.services.service;

import com.google.common.collect.Sets;
import com.ovvium.mother.builder.BillBuilder;
import com.ovvium.mother.builder.EmployeeBuilder;
import com.ovvium.mother.builder.OrderBuilder;
import com.ovvium.mother.model.*;
import com.ovvium.mother.response.ResponseFactoryMother;
import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.bill.Order;
import com.ovvium.services.model.bill.OrderGroupChoice;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.customer.Employee;
import com.ovvium.services.model.customer.Location;
import com.ovvium.services.model.exception.OvviumDomainException;
import com.ovvium.services.model.exception.ResourceNotFoundException;
import com.ovvium.services.model.product.Product;
import com.ovvium.services.model.product.ProductGroup;
import com.ovvium.services.model.product.ProductItem;
import com.ovvium.services.model.user.User;
import com.ovvium.services.repository.AverageRatingRepository;
import com.ovvium.services.repository.BillRepository;
import com.ovvium.services.security.AuthenticatedUser;
import com.ovvium.services.security.JwtAuthenticationToken;
import com.ovvium.services.service.impl.BillServiceImpl;
import com.ovvium.services.transfer.command.bill.CreateOrJoinBillCommand;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import com.ovvium.services.util.util.basic.Utils;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import com.ovvium.services.web.controller.bff.v1.transfer.request.bill.JoinBillAndLocationsRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.order.CreateOrderRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.order.OrderGroupChoiceRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.factory.BillResponseFactory;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.ovvium.mother.model.CustomerMother.getElBulliCustomer;
import static com.ovvium.mother.model.InvoiceDateMother.anyInvoiceDate;
import static com.ovvium.mother.model.ProductMother.CERVEZA_ID;
import static com.ovvium.mother.model.ProductMother.getCervezaProduct;
import static com.ovvium.services.model.bill.BillStatus.CLOSED;
import static com.ovvium.services.model.bill.ServiceTime.SOONER;
import static com.ovvium.services.model.exception.ErrorCode.BILL_ALREADY_OPENED_FOR_USER;
import static com.ovvium.services.util.ovvium.domain.DomainStatus.DELETED;
import static com.ovvium.services.util.util.basic.Utils.first;
import static com.ovvium.services.util.util.basic.Utils.set;
import static com.ovvium.utils.MockitoUtils.mockRepository;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class BillServiceTest {

	// SUT
	private BillService billService;

	private CustomerService customerService;
	private ProductService productService;
	private InvoiceDateService invoiceDateService;
	private UserService userService;
	private BillRepository billRepository;
	private EventPublisherService eventPublisherService;
	private Map<UUID, UUID> billsByUserCache;

	@Before
	public void init() {
		SecurityContextHolder.getContext().setAuthentication(null);
        customerService = mock(CustomerService.class);
        productService = mock(ProductService.class);
        userService = mock(UserService.class);
        billRepository = mockRepository(BillRepository.class);
        eventPublisherService = mock(EventPublisherService.class);
        invoiceDateService = mock(InvoiceDateService.class);
        billsByUserCache = mock(Map.class);

        BillResponseFactory billResponseFactory = ResponseFactoryMother.aBillResponseFactory(customerService, mockRepository(AverageRatingRepository.class));

        billService = new BillServiceImpl(customerService, productService, invoiceDateService, userService,
                billRepository, eventPublisherService, billResponseFactory, billsByUserCache);
        ReflectionUtils.set(billService, "self", billService);
    }

	@Test
	public void createBillRequest_withOpenedLocation_shouldThrowException() {
		// given
		Customer customer = getElBulliCustomer();
		Location elBulliLocation = LocationMother.getLocationOfCustomer(customer);
		when(customerService.getCustomer(customer.getId())).thenReturn(customer);
		when(billRepository.existsOpenByLocations(Collections.singletonList(elBulliLocation))).thenReturn(true);

		// when, then
		Assertions.assertThatThrownBy(() -> {
			billService.createOrJoin(new CreateOrJoinBillCommand(
					null,
					customer,
					List.of(elBulliLocation)
			));
		}).isInstanceOf(OvviumDomainException.class).hasMessage("There is already an opened bill for this location.");
	}

	@Test
	public void createBillRequest_withUserAndLocation_mustCreateBillWithMemberInLocation() {
		// given
		Customer customer = getElBulliCustomer();
		Location elBulliLocation = LocationMother.getLocationOfCustomer(customer);
		when(customerService.getCustomer(customer.getId())).thenReturn(customer);

		User user = UserMother.getUserJorge();
		when(userService.getUserOrFail(UserMother.USER_JORGE_ID)).thenReturn(user);
		when(invoiceDateService.getCurrentInvoiceDate(customer))
				.thenReturn(anyInvoiceDate(LocalDate.now()));

		// when
		billService.createOrJoin(new CreateOrJoinBillCommand(user, customer, List.of(elBulliLocation)));

		// then
		Bill bill = captureBill();
		assertThat(bill.getLocations()).first().isEqualTo(elBulliLocation);
		assertThat(bill.getMembers()).contains(user);
	}

	@Test
	public void createBillRequest_withUserAndLocation_withExistingBill_mustAddMemberToBill() {
		// given
		Customer customer = getElBulliCustomer();
		Location elBulliLocation = LocationMother.getLocationOfCustomer(customer);
		when(customerService.getCustomer(customer.getId())).thenReturn(customer);

		Bill bill = new BillBuilder().setUser(UserMother.getUserJorge()).setLocations(singletonList(elBulliLocation))
				.build();
		when(billRepository.getOpenByLocation(elBulliLocation.getId())).thenReturn(Optional.of(bill));

		User user = UserMother.getUserJordi();
		when(userService.getUserOrFail(UserMother.USER_JORDI_ID)).thenReturn(user);

		// when
		billService.createOrJoin(new CreateOrJoinBillCommand(user, customer, List.of(elBulliLocation)));

		// then
		Bill capturedBill = captureBill();
		assertThat(capturedBill.getLocations()).first().isEqualTo(elBulliLocation);
		assertThat(capturedBill.getMembers()) //
				.contains(user) //
				.hasSize(2);
	}

	@Test
	public void given_join_bill_request_of_user_when_user_has_opened_bills_then_should_throw_exception() {
		// given
		Customer customer = getElBulliCustomer();
		Location elBulliLocation = LocationMother.getLocationOfCustomer(customer);
		when(customerService.getCustomer(customer.getId())).thenReturn(customer);

		Bill bill = new BillBuilder().setUser(UserMother.getUserJorge()).setLocations(singletonList(elBulliLocation))
				.build();
		when(billRepository.getOpenByLocation(elBulliLocation.getId())).thenReturn(Optional.of(bill));

		User user = UserMother.getUserJordi();
		when(userService.getUserOrFail(UserMother.USER_JORDI_ID)).thenReturn(user);
		when(billRepository.getLastBillOfUser(user.getId()))
				.thenReturn(Optional.of(BillMother.getOpenedBillWithOpenOrder()));

		// when, then
		assertThatThrownBy(() -> billService.createOrJoin(
				new CreateOrJoinBillCommand(user, customer, List.of(elBulliLocation))
		)).isInstanceOf(OvviumDomainException.class)
		.hasMessage(BILL_ALREADY_OPENED_FOR_USER.getMessage());
	}

	@Test
	public void given_employee_creating_bill_when_createOrJoinToBill_then_should_create_bill_without_members_and_employee() {
		// given
		Customer customer = getElBulliCustomer();
		Location elBulliLocation = LocationMother.getLocationOfCustomer(customer);
		User adminUser = UserMother.getCustomerUserFAdria();

		Employee employee = new EmployeeBuilder().setCustomer(customer).build();
		AuthenticatedUser authenticatedUser = new AuthenticatedUser(adminUser);
		authenticatedUser.setEmployee(employee);

		SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(authenticatedUser, "token"));

		when(customerService.getCustomer(elBulliLocation.getCustomerId())).thenReturn(customer);
		when(invoiceDateService.getCurrentInvoiceDate(customer))
				.thenReturn(anyInvoiceDate(LocalDate.now()));
		when(billRepository.existsOpenByLocations(Collections.singletonList(elBulliLocation))).thenReturn(false);

		// when
		billService.createOrJoin(
				new CreateOrJoinBillCommand(null, customer, List.of(elBulliLocation))
		); //

		// then
		Bill bill = captureBill();
		assertThat(bill.getLocations()).first().isEqualTo(elBulliLocation);
		assertThat(bill.getMembers()).isEmpty();
		assertThat(bill.getEmployee()).contains(employee);
	}

	@Test
	public void createBill_withMoreThanOneLocation_ShoulAddLocationsToBill() {
		// given
		Customer customer = getElBulliCustomer();
		Location firstFreeLocation = LocationMother.getLocationOfCustomer(customer);
		Location secondFreeLocation = LocationMother.getLocationOfCustomer(customer);

		User adminUser = UserMother.getCustomerUserFAdria();
		Employee employee = new EmployeeBuilder().setCustomer(customer).build();
		AuthenticatedUser authenticatedUser = new AuthenticatedUser(adminUser);
		authenticatedUser.setEmployee(employee);

		SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(authenticatedUser, "token"));

		when(customerService.getCustomer(customer.getId())).thenReturn(customer);
		when(invoiceDateService.getCurrentInvoiceDate(customer))
				.thenReturn(anyInvoiceDate(LocalDate.now()));

		// when
		Set<UUID> locationIds = set(firstFreeLocation.getId(), secondFreeLocation.getId());
		billService.createOrJoin(
				new CreateOrJoinBillCommand(null, customer, List.of(firstFreeLocation, secondFreeLocation))
		); //

		// then
		Bill bill = captureBill();
		assertThat(bill.getLocations()).hasSize(2);
	}

	@Test
	public void given_customer_location_when_joinBillAndLocations_then_add_location_to_bill() {
		// given
		Bill bill = BillMother.getOpenedBillForElBulliLocation();
		Customer customer = getElBulliCustomer();
		Location locationOfCustomer = LocationMother.getLocationOfCustomer(customer);
		when(customerService.getCustomer(customer.getId())).thenReturn(customer);
		when(billRepository.getOrFail(bill.getId())).thenReturn(bill);
		when(billRepository.getOpenByLocation(first(bill.getLocations()).getId())).thenReturn(Optional.of(bill));

		// when
		billService.joinBillAndLocations( //
				new JoinBillAndLocationsRequest().setCustomerId(customer.getId())//
						.setDestinationBillId(bill.getId()) //
						.setLocationIds(Sets.newHashSet(locationOfCustomer.getId())) //
		);

		// then
		Bill savedBill = captureBill();
		assertThat(savedBill.getLocations()).hasSize(2);
	}

	@Test
	public void given_two_bills_and_free_customer_location_when_joinBillAndLocations_then_all_locations_in_bill() {
		// given
		Bill bill1 = BillMother.getOpenedBillForElBulliLocation();
		Bill bill2 = BillMother.getOpenedBillForElBulliLocationWithMember();
		Customer customer = getElBulliCustomer();
		Location secondFreeLocation = LocationMother.getElBulliSecondFreeLocation();
		List<Location> locations = new ArrayList<>(bill2.getLocations());
		locations.addAll(bill1.getLocations());
		locations.add(secondFreeLocation);
		ReflectionUtils.set(customer, "locations", locations);
		when(customerService.getCustomer(customer.getId())).thenReturn(customer);
		when(billRepository.getOrFail(bill1.getId())).thenReturn(bill1);
		when(billRepository.getOrFail(bill2.getId())).thenReturn(bill2);
		when(billRepository.getOpenByLocation(first(bill1.getLocations()).getId())).thenReturn(Optional.of(bill1));
		when(billRepository.getOpenByLocation(first(bill2.getLocations()).getId())).thenReturn(Optional.of(bill2));

		// when

		billService.joinBillAndLocations( //
				new JoinBillAndLocationsRequest().setCustomerId(customer.getId())//
						.setDestinationBillId(bill2.getId()) //
						.setLocationIds(customer.getLocations().stream().map(BaseEntity::getId).collect(toSet())) //
		);

		// then
		Bill savedBill = captureBill();
		assertThat(savedBill.getLocations()).hasSize(3);
	}

	@Test
	public void given_not_customer_location_when_joinBillAndLocations_then_throw_exception() {
		// given
		Bill bill = BillMother.getOpenedBillForElBulliLocation();
		Customer customer = getElBulliCustomer();
		UUID randomLocationId = UUID.randomUUID();

		when(customerService.getCustomer(customer.getId())).thenReturn(customer);
		when(billRepository.getOrFail(bill.getId())).thenReturn(bill);

		// when, then
		assertThatThrownBy(() -> billService.joinBillAndLocations( //
				new JoinBillAndLocationsRequest().setCustomerId(customer.getId())//
						.setDestinationBillId(bill.getId()) //
						.setLocationIds(Sets.newHashSet(randomLocationId)) //
		)).isInstanceOf(IllegalArgumentException.class).hasMessage(
				String.format("Location with id %s is not from Customer %s", randomLocationId, customer.getId()));
	}

	@Test
	public void given_customer_location_with_existing_bills_when_joinBillAndLocations_then_add_location_to_bill() {
		// given
		Bill bill = BillMother.getOpenedBillForElBulliLocationWithMember();

		Customer customer = getElBulliCustomer();
		Location secondFreeLocation = LocationMother.getLocationOfCustomer(customer);

		Bill secondBill = new BillBuilder().setLocations(singletonList(secondFreeLocation))
				.setUser(UserMother.getUserJordi())
				.setOrders(singletonList(new OrderBuilder().setProduct(ProductMother.getCervezaProduct()))).build();

		when(customerService.getCustomer(customer.getId())).thenReturn(customer);
		when(billRepository.getOrFail(bill.getId())).thenReturn(bill);
		when(billRepository.getOpenByLocation(secondFreeLocation.getId())).thenReturn(Optional.ofNullable(secondBill));

		// when
		billService.joinBillAndLocations( //
				new JoinBillAndLocationsRequest().setCustomerId(customer.getId())//
						.setDestinationBillId(bill.getId()) //
						.setLocationIds(Sets.newHashSet(secondFreeLocation.getId())) //
		);

		// then
		Bill savedBill = captureBill();
		assertThat(savedBill.getLocations()).hasSize(2);
		assertThat(savedBill.getMembers()).hasSize(2);
		assertThat(savedBill.getOrders()).hasSize(1);
		assertThat(savedBill.getOrders()).hasSize(1);
		assertThat(secondBill.getOrders()).isEmpty();
		assertThat(secondBill.getMembers()).isEmpty();
		assertThat(secondBill.getLocations()).isEmpty();
		assertThat(secondBill.getBillStatus()).isEqualTo(CLOSED);
	}

	@Test
	public void addOrderToBill_withUser_mustAddOrdersToBill() {
		// given
		Bill bill = BillMother.getOpenedBillForElBulliLocationWithMember();
		User user = UserMother.getUserJorge();
		Product product = ProductMother.getPatatasBravasProduct();
		when(billRepository.getOrFail(bill.getId())).thenReturn(bill);
		when(productService.getProduct(product.getId())).thenReturn(product);
		when(userService.getUserOrFail(user.getId())).thenReturn(user);

		// when
		billService.addOrder(bill.getId(), new CreateOrderRequest()//
				.setProductId(product.getId()) //
				.setUserId(user.getId()));

		// then
		Bill savedBill = captureBill();
		assertThat(savedBill.getOrders()).hasSize(1);

		Order savedOrder = Utils.first(savedBill.getOrders());
		assertNotNull(savedOrder.getUser());
		assertThat(savedOrder.getUser()).contains(user);
	}

	@Test
	public void addOrderToBill_withoutUser_mustAddOrdersToBill() {
		// given
		Bill bill = BillMother.getOpenedBillForElBulliLocation();
		User user = UserMother.getUserJordi();
		Product product = ProductMother.getPatatasBravasProduct();
		when(billRepository.getOrFail(bill.getId())).thenReturn(bill);
		when(productService.getProduct(product.getId())).thenReturn(product);
		when(userService.getUserOrFail(user.getId())).thenReturn(user);

		// when
        billService.addOrder(bill.getId(), new CreateOrderRequest()//
                .setProductId(product.getId()));

        // then
        Bill savedBill = captureBill();
        assertThat(savedBill.getOrders()).hasSize(1);
        Order savedOrder = Utils.first(savedBill.getOrders());
        assertThat(savedOrder.getUser()).isEmpty();
    }

    @Test
    public void given_bill_when_close_bill_then_should_close_bill_only() {
        Bill bill = new BillBuilder()
                .setOrders(singletonList(new OrderBuilder()
                        .setProduct(ProductMother.getMenuDiarioProduct())
                        .setChoices(Collections.singleton(new OrderGroupChoice(SOONER, ProductMother.getCervezaProduct())))))
                .build();
        when(billRepository.getOrFail(bill.getId())).thenReturn(bill);

        billService.closeBill(bill);

        assertThat(bill.getBillStatus()).isEqualTo(CLOSED);
        assertThat(bill.getOrders().stream()
                .map(Order::getStatus)
                .collect(Collectors.toList())).doesNotContain(DELETED);
        assertThat(bill.getOrders().stream()
                .flatMap(it -> it.getChoices().stream())
                .map(OrderGroupChoice::getStatus)
                .collect(Collectors.toList())).doesNotContain(DELETED);
    }

	@Test
	public void given_not_found_user_id_when_get_open_bill_of_user_then_throw_exception() {
		// given
		UUID randomUUID = UUID.randomUUID();
		when(billRepository.getLastBillOfUser(randomUUID)).thenReturn(Optional.empty());

		// when then
		assertThatThrownBy(() -> billService.getCurrentBillOfUser(randomUUID))
				.isInstanceOf(ResourceNotFoundException.class).hasMessage("Open Bill not found for user " + randomUUID);
	}

	@Test
	public void given_bill_with_user_and_empty_cache_when_getCurrentBillOfUser_then_should_get_bill_and_update_cache() {
		final User user = UserMother.getUserJorge();
		final Bill bill = new BillBuilder().setUser(user).build();

		when(billsByUserCache.get(user.getId())).thenReturn(null);
		when(billRepository.getLastBillOfUser(user.getId())).thenReturn(Optional.of(bill));
		when(userService.getAuthenticatedUser()).thenReturn(user);
		when(customerService.getCustomer(bill.getCustomerId())).thenReturn(getElBulliCustomer());

		billService.getCurrentBillOfUser();

		verify(billRepository, times(1)).getLastBillOfUser(user.getId());
		verify(billsByUserCache, times(1)).put(user.getId(), bill.getId());
	}

	@Test
	public void given_bill_with_user_and_user_on_cache_when_getCurrentBillOfUser_then_should_get_bill_id_from_cache() {
		final User user = UserMother.getUserJorge();
		final Bill bill = new BillBuilder().setUser(user).build();

		when(billsByUserCache.get(user.getId())).thenReturn(bill.getId());
		when(billRepository.getOrFail(bill.getId())).thenReturn(bill);
		when(userService.getAuthenticatedUser()).thenReturn(user);
		when(customerService.getCustomer(bill.getCustomerId())).thenReturn(getElBulliCustomer());

		billService.getCurrentBillOfUser();

		verify(billRepository, times(1)).getOrFail(bill.getId());
		verify(billRepository, never()).getLastBillOfUser(user.getId());
		verify(billsByUserCache, times(1)).put(user.getId(), bill.getId());
	}

	@Test
	public void given_bill_with_user_and_user_on_cache_when_save_bill_then_should_remove_bill_from_cache() {
		final User user = UserMother.getUserJorge();
		final Bill bill = new BillBuilder().setUser(user).build();

		billService.save(bill);

		verify(billsByUserCache, times(1)).remove(user.getId());
	}

	@Test
	public void given_add_order_request_for_current_user_when_addOrdersToCurrentBill_then_should_add_order_with_user_to_bill() {
		// given
		Bill bill = BillMother.getOpenedBillForElBulliLocationWithMember();
		User user = UserMother.getUserJorge();
		SecurityContextHolder.getContext()
				.setAuthentication(new JwtAuthenticationToken(new AuthenticatedUser(user), "token"));

		Product product = ProductMother.getPatatasBravasProduct();
		when(billsByUserCache.get(user.getId())).thenReturn(bill.getId());
		when(billRepository.getOrFail(bill.getId())).thenReturn(bill);
		when(productService.getProduct(product.getId())).thenReturn(product);
		when(customerService.getCustomer(CustomerMother.EL_BULLI_CUSTOMER_ID))
				.thenReturn(getElBulliCustomer());
		when(userService.getAuthenticatedUser()).thenReturn(user);

		// when
		CreateOrderRequest orderRequest = new CreateOrderRequest()//
				.setProductId(product.getId()) //
				.setUserId(user.getId());
		billService.addOrdersToCurrentBill(asList(orderRequest, orderRequest));

		// then
		Bill savedBill = captureBill();
		assertThat(savedBill.getOrders()).hasSize(2);

		Order savedOrder = Utils.first(savedBill.getOrders());
		assertNotNull(savedOrder.getUser());
		assertThat(savedOrder.getUser()).contains(user);
	}

	@Test
	public void given_multiple_add_order_request_when_addOrders_then_should_add_orders_to_bill() {
		// given
		Bill bill = BillMother.getOpenedBillForElBulliLocationWithMember();
		User user = UserMother.getUserJorge();

		Product product = ProductMother.getPatatasBravasProduct();
		when(billRepository.getOrFail(bill.getId())).thenReturn(bill);
		when(productService.getProduct(product.getId())).thenReturn(product);
		when(userService.getAuthenticatedUser()).thenReturn(user);

		// when
		CreateOrderRequest orderRequest = new CreateOrderRequest()//
				.setProductId(product.getId()) //
				.setUserId(user.getId());
		billService.addOrders(bill.getId(), asList(orderRequest, orderRequest, orderRequest));

		// then
		Bill savedBill = captureBill();
		assertThat(savedBill.getOrders()).hasSize(3);

		Order savedOrder = Utils.first(savedBill.getOrders());
		assertNotNull(savedOrder.getUser());
		assertThat(savedOrder.getUser()).contains(user);
	}

	@Test
	public void given_add_order_of_product_group_when_addOrders_then_should_add_order_group_choice_to_order() {
		// given
		Bill bill = BillMother.getOpenedBillForElBulliLocationWithMember();
		User user = UserMother.getUserJorge();

		ProductGroup product = ProductMother.getMenuDiarioProduct();
		when(billRepository.getOrFail(bill.getId())).thenReturn(bill);
		when(productService.getProduct(product.getId())).thenReturn(product);
		ProductItem cervezaProduct = getCervezaProduct();
		when(productService.getProduct(CERVEZA_ID)).thenReturn(cervezaProduct);
		when(userService.getAuthenticatedUser()).thenReturn(user);

		// when
		CreateOrderRequest orderRequest = new CreateOrderRequest()//
				.setProductId(product.getId()) //
				.setUserId(user.getId())
				.setGroupChoices(singletonList(new OrderGroupChoiceRequest()
						.setProductId(CERVEZA_ID)
						.setNotes("Notes"))
				);
		billService.addOrders(bill.getId(), singletonList(orderRequest));

		// then
		Bill savedBill = captureBill();
		assertThat(savedBill.getOrders()).hasSize(1);
		Order savedOrder = Utils.first(savedBill.getOrders());
		assertThat(savedOrder.getProduct()).isEqualTo(product);
		assertThat(savedOrder.getChoices()).hasSize(1);
		assertThat(Utils.first(savedOrder.getChoices()).getProduct()).isEqualTo(cervezaProduct);
	}

	private Bill captureBill() {
		ArgumentCaptor<Bill> captor = ArgumentCaptor.forClass(Bill.class);
		verify(billRepository, atLeastOnce()).save(captor.capture());

		return captor.getValue();
	}

}