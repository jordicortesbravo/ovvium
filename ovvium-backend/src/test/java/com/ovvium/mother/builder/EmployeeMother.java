package com.ovvium.mother.builder;

import com.ovvium.services.model.customer.Employee;

import java.util.UUID;

public class EmployeeMother {

	public static final UUID ANY_EMPLOYEE_ID = UUID.fromString("8ace638e-2e27-4676-8d74-1aaa2a1b7a3a");

	public static Employee getAnyEmployee() {
		return new EmployeeBuilder().build();
	}

}
