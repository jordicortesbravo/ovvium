package com.ovvium.services.service;

import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.customer.Employee;
import com.ovvium.services.model.customer.Location;
import com.ovvium.services.model.customer.Zone;
import com.ovvium.services.util.ovvium.cache.CollectionWrapper;
import com.ovvium.services.web.controller.bff.v1.transfer.request.customer.*;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.customer.EmployeeLoginResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.customer.EmployeeResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.customer.TagResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.location.LocationResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.zone.ZoneResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerService {

	Customer createCustomer(CreateCustomerRequest request);

    void updateCustomer(UUID customerId, UpdateCustomerRequest request);

	Zone createZone(CreateZoneRequest request);

	Location createLocation(CreateLocationRequest request);

	void updateLocation(UpdateLocationRequest request);

	List<ZoneResponse> listZones(UUID customerId);

	CollectionWrapper<LocationResponse> listLocations(UUID customerId);

	Customer getCustomer(UUID customerId);

	Customer save(Customer customer);

	EmployeeLoginResponse loginEmployee(EmployeeLoginRequest request);

	ResourceIdResponse createEmployee(CreateEmployeeRequest request);

	Employee getEmployee(UUID customerId, UUID employeeId);

	List<EmployeeResponse> listEmployees(UUID customerId);

	TagResponse getTag(String tagId);

}
