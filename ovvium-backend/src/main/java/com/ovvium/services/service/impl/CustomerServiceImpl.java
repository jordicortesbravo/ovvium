package com.ovvium.services.service.impl;

import com.ovvium.services.app.config.properties.PaycometCommissionProperties;
import com.ovvium.services.model.customer.*;
import com.ovvium.services.model.exception.ResourceNotFoundException;
import com.ovvium.services.model.payment.CardCategory;
import com.ovvium.services.model.payment.CardLocation;
import com.ovvium.services.repository.CustomerRepository;
import com.ovvium.services.repository.LocationRepository;
import com.ovvium.services.repository.ZoneRepository;
import com.ovvium.services.service.AccountService;
import com.ovvium.services.service.CustomerService;
import com.ovvium.services.service.PictureService;
import com.ovvium.services.service.UserService;
import com.ovvium.services.util.ovvium.base.Validations;
import com.ovvium.services.util.ovvium.cache.CollectionWrapper;
import com.ovvium.services.util.util.basic.Utils;
import com.ovvium.services.util.util.string.StringUtils;
import com.ovvium.services.web.controller.bff.v1.transfer.request.customer.*;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.customer.EmployeeLoginResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.customer.EmployeeResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.customer.TagResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.factory.AccountResponseFactory;
import com.ovvium.services.web.controller.bff.v1.transfer.response.factory.TagResponseFactory;
import com.ovvium.services.web.controller.bff.v1.transfer.response.location.LocationResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.zone.ZoneResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ovvium.services.app.constant.Caches.*;
import static com.ovvium.services.util.ovvium.base.Preconditions.check;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

	private final UserService userService;
	private final CustomerRepository customerRepository;
	private final AccountService accountService;
	private final PictureService pictureService;
	private final PaycometCommissionProperties paycometCommissionProperties;
	private final ZoneRepository zoneRepository;
	private final LocationRepository locationRepository;
	private final AccountResponseFactory accountResponseFactory;
	private final TagResponseFactory tagResponseFactory;

	@Autowired
	private CustomerService self;

	@Override
	public Customer createCustomer(CreateCustomerRequest request) {
		Validations.validate(request);
		val user = userService.getUserOrFail(request.getUserId());
		userService.save(user.promoteToAdminUser());
		val customer = new Customer(
				user,
				request.getName(),
				request.getDescription(),
				request.getCif(),
				request.getAddress(),
				request.getPhones(),
				request.getPciSplitUserId(),
				createCommissionConfig(request.getCommissionConfig()),
				new InvoiceNumberPrefix(request.getInvoiceNumberPrefix())
		);
		request.getPictureId().map(pictureService::getOrFail).ifPresent(customer::setPicture);
		request.getLongitude().ifPresent(customer::setLongitude);
		request.getLatitude().ifPresent(customer::setLatitude);
		request.getWebsite().ifPresent(customer::setWebsite);
		request.getTimeZone().map(ZoneId::of).ifPresent(customer::setTimeZone);
		customerRepository.save(customer);
		log.info("Created new Customer: " + customer.getName());
		return customer;
	}

	@Override
	public void updateCustomer(UUID customerId, UpdateCustomerRequest request) {
		Validations.validate(request);
		val customer = customerRepository.getOrFail(customerId);
		request.getName().ifPresent(customer::setName);
		request.getDescription().ifPresent(customer::setDescription);
		request.getCif().ifPresent(customer::setCif);
		request.getAddress().ifPresent(customer::setAddress);
		request.getPciSplitUserId().ifPresent(customer::setPciSplitUserId);
		request.getCommissionConfig().map(this::createCommissionConfig).ifPresent(customer::setCommissionConfig);
		request.getPictureId().map(pictureService::getOrFail).ifPresent(customer::setPicture);
		request.getLongitude().ifPresent(customer::setLongitude);
		request.getLatitude().ifPresent(customer::setLatitude);
		request.getWebsite().ifPresent(customer::setWebsite);
		request.getTimeZone().map(ZoneId::of).ifPresent(customer::setTimeZone);
		request.getInvoiceNumberPrefix().map(InvoiceNumberPrefix::new).ifPresent(customer::setInvoiceNumberPrefix);
		if (!request.getPhones().isEmpty()) {
			customer.getPhones().clear();
			request.getPhones().forEach(customer::addPhone);
		}
		self.save(customer);
		log.info("Updated Customer: {} {}", customer.getId(), customer.getName());
	}

	@Override
	public Zone createZone(CreateZoneRequest request) {
		Validations.validate(request);
		val zone = new Zone(
				getCustomer(request.getCustomerId()),
				request.getName()
		);
		return zoneRepository.save(zone);
	}

	@Override
	@CacheEvict(value = LOCATIONS_BY_CUSTOMER, key = "#request.customerId")
	public Location createLocation(CreateLocationRequest request) {
		Validations.validate(request);
		val tagId = new TagId(request.getTagId());
		check(locationRepository.getByTagId(tagId).isEmpty(), "TagId already exists.");
		val serialNumber = new SerialNumber(request.getSerialNumber());
		check(locationRepository.getBySerialNumber(serialNumber).isEmpty(), "SerialNumber already exists.");
		val location = new Location(
				getCustomer(request.getCustomerId()),
				zoneRepository.getOrFail(request.getZoneId()),
				tagId,
				serialNumber,
				locationRepository.getLastPosition(request.getCustomerId(), request.getZoneId()) + 1
		)
				.setAdvancePayment(request.isAdvancePayment());
		locationRepository.save(location);
		return location;
	}

	@Override
	@Caching(evict = {
			@CacheEvict(value = LOCATIONS_BY_CUSTOMER, key = "#request.customerId"),
			@CacheEvict(value = LOCATIONS_BY_TAG, allEntries = true)
	})
	public void updateLocation(UpdateLocationRequest request) {
		//TODO If there´s any Bill on Location, reject any changes
		val location = Utils.first(
				customerRepository.getOrFail(request.getCustomerId())
						.getLocationsById(singleton(request.getLocationId()))
		);
		request.getTagId()
				.map(TagId::new)
				.ifPresent(tagId -> {
					check(locationRepository.getByTagId(tagId).isEmpty(), "TagId already exists.");
					location.setTagId(tagId);
				});
		request.getSerialNumber()
				.map(SerialNumber::new)
				.ifPresent(serialNumber -> {
					check(locationRepository.getBySerialNumber(serialNumber).isEmpty(), "SerialNumber already exists.");
					location.setSerialNumber(serialNumber);
				});
		request.getZoneId().map(zoneRepository::getOrFail).ifPresent(location::setZone);
		request.getDescription().ifPresent(location::setDescription);
		request.getPosition().ifPresent(location::setPosition);
		request.isAdvancePayment().ifPresent(location::setAdvancePayment);
		locationRepository.save(location);
	}

	@Override
	public List<ZoneResponse> listZones(UUID customerId) {
		return zoneRepository.listByCustomer(customerId).stream()
				.map(ZoneResponse::new)
				.collect(toList());
	}

	@Override
	@Cacheable(LOCATIONS_BY_CUSTOMER)
	public CollectionWrapper<LocationResponse> listLocations(UUID customerId) {
		Customer customer = getCustomer(customerId);
		return CollectionWrapper.of(
				customerId,
				customer.getLocations()
						.stream()
						.map(LocationResponse::new)
						.collect(toList())
		);
	}

	@Override
	public Customer getCustomer(UUID customerId) {
		return customerRepository.getOrFail(customerId);
	}

	@Override
	@Caching(evict = {
			@CacheEvict(value = BILLS_BY_CUSTOMER_OPEN, key = "#customer.id"),
			@CacheEvict(value = LOCATIONS_BY_CUSTOMER, key = "#customer.id")
	})
	public Customer save(Customer customer) {
		return customerRepository.save(customer);
	}

	@Override
	public ResourceIdResponse createEmployee(CreateEmployeeRequest request) {
		val customer = getCustomer(request.getCustomerId());
		val accessCode = generateAccessCode(customer);
		val employee = new Employee(customer, request.getName(), accessCode);
		customerRepository.save(customer);
		return new ResourceIdResponse(employee);
	}

	@Override
	public Employee getEmployee(UUID customerId, UUID employeeId) {
		val customer = getCustomer(customerId);
		return customer.getEmployee(employeeId);
	}

	@Override
	public List<EmployeeResponse> listEmployees(UUID customerId) {
		return getCustomer(customerId).getEmployees()
				.stream()
				.map(EmployeeResponse::new)
				.collect(toList());
	}

	@Override
	@Cacheable(LOCATIONS_BY_TAG)
	public TagResponse getTag(String tagId) {
		val location = locationRepository.getByTagId(new TagId(tagId))
				.orElseThrow(() -> new ResourceNotFoundException("Location tag not found for tagId " + tagId));
		val customer = getCustomer(location.getCustomerId());
		return tagResponseFactory.create(location, customer);
	}

	/**
	 * This will return a new accessToken so the current Customer User can impersonate an Employee.
	 * This is needed so the Waiters can have their own user under the Customer´s user.
	 */
	@Override
	public EmployeeLoginResponse loginEmployee(EmployeeLoginRequest request) {
		val currentUser = userService.getAuthenticatedUser();
		val customer = customerRepository.getOrFail(request.getCustomerId());
		val employee = customer.getEmployeeByCode(request.getAccessCode());
		return accountResponseFactory.create(
				currentUser,
				employee,
				accountService.generateTokens(currentUser, employee)
		);
	}

	private CommissionConfig createCommissionConfig(CreateCommissionConfigRequest commissionConfig) {
		return switch (commissionConfig.getStrategy()) {
			case BASIC -> CommissionConfig.basic(commissionConfig.getTipPercentage(), commissionConfig.getMinimumCommission());
			case BY_CARD_CATEGORY -> CommissionConfig.cardCategory(
					paycometCommissionProperties,
					commissionConfig.getTipPercentage(),
					toConfigMap(commissionConfig)
			);
		};
	}

	//TODO A ServiceFacade would help here with all of these transformations...
	private Map<CardCategory, Map<CardLocation, CommissionPair>> toConfigMap(CreateCommissionConfigRequest commissionConfig) {
		return commissionConfig.getConfig().entrySet()
				.stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().entrySet().stream().collect(Collectors.toMap(
						Map.Entry::getKey, cp -> {
							CreateCommissionPairRequest value = cp.getValue();
							return new CommissionPair(value.getBasePercentage(), value.getCommission());
						}
				))));
	}

	private String generateAccessCode(Customer customer) {
		// Generate an unique access code by Employee for this Customer.
		// Number of permutations with repetitions are 10000, should not be a problem to have this do-while here.
		String accessCode;
		do {
			accessCode = StringUtils.randomString(4, "0123456789");
		} while (customerRepository.existsAccessCode(customer, accessCode));
		return accessCode;
	}

}
