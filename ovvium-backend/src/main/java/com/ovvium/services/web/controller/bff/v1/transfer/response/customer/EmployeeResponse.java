package com.ovvium.services.web.controller.bff.v1.transfer.response.customer;

import com.ovvium.services.model.customer.Employee;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import lombok.Getter;

@Getter
public final class EmployeeResponse extends ResourceIdResponse {

	private final String name;

	public EmployeeResponse(Employee employee) {
		super(employee);
		this.name = employee.getName();
	}
}
