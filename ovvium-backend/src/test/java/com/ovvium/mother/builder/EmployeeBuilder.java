package com.ovvium.mother.builder;

import com.ovvium.mother.model.CustomerMother;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.model.customer.Employee;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Setter
@Accessors(chain = true)
public class EmployeeBuilder {

	private UUID id = EmployeeMother.ANY_EMPLOYEE_ID;
	private Customer customer = CustomerMother.getCanRocaCustomer();
	private String accessCode = "0000";


	public Employee build() {
		Employee employee = new Employee(this.customer, "Paco", "0000");
		ReflectionUtils.set(employee, "id", id);
		return employee;
	}

}
