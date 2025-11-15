package com.ovvium.services.web.controller.bff.v1.transfer.response.customer;

import com.ovvium.services.model.customer.Employee;
import com.ovvium.services.web.controller.bff.v1.transfer.response.common.ResourceIdResponse;
import lombok.Getter;

@Getter
public final class EmployeeCodeResponse extends ResourceIdResponse {

	private final String name;
	private final String accessCode;

	public EmployeeCodeResponse(Employee employee) {
		super(employee);
		this.name = employee.getName();
		this.accessCode = employee.getAccessCode();
	}

}
