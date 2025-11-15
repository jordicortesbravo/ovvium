package com.ovvium.services.web.controller.bff.v1;

import com.ovvium.services.service.CustomerService;
import com.ovvium.services.web.controller.bff.v1.transfer.request.customer.*;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.customer.CustomerResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.customer.EmployeeCodeResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.customer.EmployeeLoginResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.customer.EmployeeResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.factory.CustomerResponseFactory;
import com.ovvium.services.web.controller.bff.v1.transfer.response.location.LocationResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.zone.ZoneResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.ovvium.services.web.controller.bff.v1.WebConstants.BASE_URI_API_V1;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = BASE_URI_API_V1, produces = APPLICATION_JSON_VALUE)
public class CustomerApiController {

	private final CustomerService customerService;
	private final CustomerResponseFactory customerResponseFactory;

	@ResponseStatus(CREATED)
	@PostMapping("/customers")
	@PreAuthorize("hasRole('OVVIUM_ADMIN')")
	public ResourceIdResponse createCustomer(@RequestBody CreateCustomerRequest request) {
		return new ResourceIdResponse(customerService.createCustomer(request));
	}

	@ResponseStatus(OK)
	@PatchMapping("/customers/{customerId}")
	@PreAuthorize("hasRole('OVVIUM_ADMIN')")
	public void updateCustomer(@PathVariable UUID customerId, @RequestBody UpdateCustomerRequest request) {
		customerService.updateCustomer(customerId, request);
	}

	@ResponseStatus(CREATED)
	@PostMapping("/customers/{customerId}/zones")
	@PreAuthorize("hasRole('OVVIUM_ADMIN')")
	public ResourceIdResponse createZone(@PathVariable UUID customerId, @RequestBody CreateZoneRequest request) {
		request.setCustomerId(customerId);
		return new ResourceIdResponse(customerService.createZone(request));
	}

	@ResponseStatus(CREATED)
	@PostMapping("/customers/{customerId}/locations")
	@PreAuthorize("hasRole('OVVIUM_ADMIN')")
	public ResourceIdResponse createLocation(@PathVariable UUID customerId, @RequestBody CreateLocationRequest request) {
		request.setCustomerId(customerId);
		return new ResourceIdResponse(customerService.createLocation(request));
	}

	@ResponseStatus(OK)
	@PatchMapping("/customers/{customerId}/locations/{locationId}")
	@PreAuthorize("hasRole('OVVIUM_ADMIN')")
	public void updateLocation(@PathVariable UUID customerId, @PathVariable UUID locationId, @RequestBody UpdateLocationRequest request) {
		request.setCustomerId(customerId).setLocationId(locationId);
		customerService.updateLocation(request);
	}

	@ResponseStatus(OK)
	@GetMapping("/customers/{customerId}")
	@PreAuthorize("(hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER') and @auth.isFromCustomer(#customerId)) or hasRole('OVVIUM_ADMIN')")
	public CustomerResponse getCustomer(@PathVariable UUID customerId) {
		return customerResponseFactory.create(customerService.getCustomer(customerId));
	}

	@ResponseStatus(OK)
	@GetMapping("/customers/{customerId}/zones")
	@PreAuthorize("(hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER') and @auth.isFromCustomer(#customerId)) or hasRole('OVVIUM_ADMIN')")
	public List<ZoneResponse> listZones(@PathVariable UUID customerId) {
		return customerService.listZones(customerId);
	}

	@ResponseStatus(OK)
	@GetMapping("/customers/{customerId}/locations")
	@PreAuthorize("(hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER') and @auth.isFromCustomer(#customerId)) or hasRole('OVVIUM_ADMIN')")
	public List<LocationResponse> listLocations(@PathVariable UUID customerId) {
		return customerService.listLocations(customerId).toList();
	}

	@ResponseStatus(CREATED)
	@PostMapping("/customers/{customerId}/employees")
	@PreAuthorize("(hasAnyRole('CUSTOMERS_ADMIN') and @auth.isFromCustomer(#customerId)) or hasRole('OVVIUM_ADMIN')")
	public ResourceIdResponse createEmployee(@PathVariable UUID customerId, @RequestBody CreateEmployeeRequest request) {
		return customerService.createEmployee(request.setCustomerId(customerId));
	}

	@ResponseStatus(OK)
	@GetMapping("/customers/{customerId}/employees")
	@PreAuthorize("(hasAnyRole('CUSTOMERS_ADMIN') and @auth.isFromCustomer(#customerId)) or hasRole('OVVIUM_ADMIN')")
	public List<EmployeeResponse> getEmployees(@PathVariable UUID customerId) {
		return customerService.listEmployees(customerId);
	}

	// This is used to show AccessCode when Employee is created, and only admin should have privileges to see it
	// FIXME This can be improved using @JsonView annotations - filtering fields by role
	@ResponseStatus(OK)
	@GetMapping("/customers/{customerId}/employees/{employeeId}")
	@PreAuthorize("hasRole('CUSTOMERS_ADMIN') and @auth.isFromCustomer(#customerId)")
	public EmployeeCodeResponse getEmployeeCode(@PathVariable UUID customerId, @PathVariable UUID employeeId) {
		return new EmployeeCodeResponse(customerService.getEmployee(customerId, employeeId));
	}

	@ResponseStatus(OK)
	@PostMapping("/customers/{customerId}/employees/login")
	@PreAuthorize("hasAnyRole('CUSTOMERS_ADMIN', 'CUSTOMERS_USER') and @auth.isFromCustomer(#customerId)")
	public EmployeeLoginResponse loginEmployee(@PathVariable UUID customerId, @RequestBody EmployeeLoginRequest request) {
		return customerService.loginEmployee(request.setCustomerId(customerId));
	}


}
