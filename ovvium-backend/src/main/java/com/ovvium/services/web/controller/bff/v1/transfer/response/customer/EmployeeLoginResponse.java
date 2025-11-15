package com.ovvium.services.web.controller.bff.v1.transfer.response.customer;

import com.ovvium.services.model.customer.Employee;
import com.ovvium.services.web.controller.bff.v1.transfer.response.account.SessionResponse;
import com.ovvium.services.web.controller.bff.v1.transfer.response.user.UserResponse;
import lombok.Getter;

@Getter
public final class EmployeeLoginResponse {

	private final EmployeeResponse employee;
	private final UserResponse user;
	private final SessionResponse session;

	public EmployeeLoginResponse(UserResponse userResponse, Employee employee, SessionResponse session) {
		this.user = userResponse;
		this.session = session;
		this.employee = new EmployeeResponse(employee);
	}

}
