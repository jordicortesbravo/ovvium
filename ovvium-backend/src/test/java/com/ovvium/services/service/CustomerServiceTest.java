package com.ovvium.services.service;

import com.ovvium.mother.builder.EmployeeBuilder;
import com.ovvium.mother.model.CustomerMother;
import com.ovvium.mother.model.LocationMother;
import com.ovvium.mother.model.UserMother;
import com.ovvium.mother.model.ZoneMother;
import com.ovvium.services.app.config.properties.PaycometCommissionProperties;
import com.ovvium.services.model.customer.*;
import com.ovvium.services.model.user.User;
import com.ovvium.services.repository.CustomerRepository;
import com.ovvium.services.repository.LocationRepository;
import com.ovvium.services.repository.ZoneRepository;
import com.ovvium.services.service.impl.CustomerServiceImpl;
import com.ovvium.services.web.controller.bff.v1.transfer.request.customer.CreateLocationRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.customer.EmployeeLoginRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.customer.UpdateLocationRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.SessionResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.customer.EmployeeLoginResponse;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Optional;

import static com.ovvium.mother.response.ResponseFactoryMother.aTagResponseFactory;
import static com.ovvium.mother.response.ResponseFactoryMother.anAccountResponseFactory;
import static com.ovvium.utils.MockitoUtils.mockRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class CustomerServiceTest {

	private CustomerService customerService;

	private UserService userService;
	private AccountService accountService;
	private PictureService pictureService;
	private CustomerRepository customerRepository;
	private ZoneRepository zoneRepository;
	private LocationRepository locationRepository;
	private PaycometCommissionProperties paymentProperties;

	@Before
	public void setUp() {
		userService = mock(UserService.class);
		accountService = mock(AccountService.class);
		pictureService = mock(PictureService.class);
		customerRepository = mockRepository(CustomerRepository.class);
		zoneRepository = mockRepository(ZoneRepository.class);
		paymentProperties = mock(PaycometCommissionProperties.class);
		locationRepository = mockRepository(LocationRepository.class);
		customerService = new CustomerServiceImpl(
				userService,
				customerRepository,
				accountService,
				pictureService,
				paymentProperties,
				zoneRepository,
				locationRepository,
				anAccountResponseFactory(),
				aTagResponseFactory()
		);
	}

	@Test
	public void given_employee_login_request_when_login_employee_then_should_return_new_access_token_with_employee() {
		User user = UserMother.getCustomerUserFAdria();
		Customer customer = CustomerMother.getCanRocaCustomer();
		Employee employee = new EmployeeBuilder()
				.setAccessCode("1234")
				.setCustomer(customer)
				.build();

		SessionResponse sessionResponse = new SessionResponse(
				"refresh", "access", Instant.now()
		);
		when(userService.getAuthenticatedUser()).thenReturn(user);
		when(customerRepository.getOrFail(customer.getId())).thenReturn(customer);
		when(accountService.generateTokens(user, employee)).thenReturn(sessionResponse);

		EmployeeLoginResponse loginResponse = customerService.loginEmployee(new EmployeeLoginRequest()
				.setAccessCode(employee.getAccessCode())
				.setCustomerId(customer.getId()));

		verify(accountService, times(1)).generateTokens(user, employee);
		assertThat(loginResponse.getEmployee().getName()).isEqualTo(employee.getName());
		assertThat(loginResponse.getUser().getId()).isEqualTo(user.getId());
		assertThat(loginResponse.getSession()).isEqualTo(sessionResponse);
	}

	@Test
	public void given_create_location_request_and_existing_tag_id_when_create_location_then_should_throw_exception() {
		Customer customer = CustomerMother.getElBulliCustomer();
		Zone zone = ZoneMother.getSalonPrincipalZoneOfElBulli();
		CreateLocationRequest request = new CreateLocationRequest()
				.setCustomerId(customer.getId())
				.setZoneId(zone.getId())
				.setTagId(TagId.randomTagId().getValue())
				.setSerialNumber("0001");
		when(customerRepository.getOrFail(customer.getId())).thenReturn(customer);
		when(zoneRepository.getOrFail(zone.getId())).thenReturn(zone);
		when(locationRepository.getByTagId(any())).thenReturn(Optional.of(LocationMother.getElBulliFirstFreeLocation()));

		assertThatThrownBy(() -> customerService.createLocation(request))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("TagId already exists.");
	}

	@Test
	public void given_create_location_request_and_existing_serial_number_when_create_location_then_should_throw_exception() {
		Customer customer = CustomerMother.getElBulliCustomer();
		Zone zone = ZoneMother.getSalonPrincipalZoneOfElBulli();
		CreateLocationRequest request = new CreateLocationRequest()
				.setCustomerId(customer.getId())
				.setZoneId(zone.getId())
				.setTagId(TagId.randomTagId().getValue())
				.setSerialNumber("0001");
		when(customerRepository.getOrFail(customer.getId())).thenReturn(customer);
		when(zoneRepository.getOrFail(zone.getId())).thenReturn(zone);
		when(locationRepository.getByTagId(any())).thenReturn(Optional.empty());
		when(locationRepository.getBySerialNumber(any())).thenReturn(Optional.of(LocationMother.getElBulliFirstFreeLocation()));

		assertThatThrownBy(() -> customerService.createLocation(request))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("SerialNumber already exists.");
	}

	@Test
	public void given_update_location_request_and_existing_tag_id_when_update_location_then_should_throw_exception() {
		Customer customer = CustomerMother.getElBulliCustomer();
		Zone zone = ZoneMother.getSalonPrincipalZoneOfElBulli();
		Location location = LocationMother.getLocationOfCustomer(customer);
		UpdateLocationRequest request = new UpdateLocationRequest()
				.setCustomerId(customer.getId())
				.setLocationId(location.getId())
				.setZoneId(zone.getId())
				.setTagId(TagId.randomTagId().getValue())
				.setSerialNumber("0001");
		when(customerRepository.getOrFail(customer.getId())).thenReturn(customer);
		when(locationRepository.getOrFail(location.getId())).thenReturn(location);
		when(locationRepository.getByTagId(any())).thenReturn(Optional.of(LocationMother.getElBulliFirstFreeLocation()));

		assertThatThrownBy(() -> customerService.updateLocation(request))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("TagId already exists.");
	}

	@Test
	public void given_update_location_request_and_existing_serial_number_when_update_location_then_should_throw_exception() {
		Customer customer = CustomerMother.getElBulliCustomer();
		Zone zone = ZoneMother.getSalonPrincipalZoneOfElBulli();
		Location location = LocationMother.getLocationOfCustomer(customer);
		UpdateLocationRequest request = new UpdateLocationRequest()
				.setCustomerId(customer.getId())
				.setLocationId(location.getId())
				.setZoneId(zone.getId())
				.setTagId(TagId.randomTagId().getValue())
				.setSerialNumber("0001");
		when(customerRepository.getOrFail(customer.getId())).thenReturn(customer);
		when(locationRepository.getOrFail(location.getId())).thenReturn(location);
		when(locationRepository.getByTagId(any())).thenReturn(Optional.empty());
		when(locationRepository.getBySerialNumber(any())).thenReturn(Optional.of(LocationMother.getElBulliFirstFreeLocation()));

		assertThatThrownBy(() -> customerService.updateLocation(request))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("SerialNumber already exists.");
	}
}