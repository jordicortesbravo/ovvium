package com.ovvium.services.model.customer;

import com.ovvium.mother.builder.CustomerBuilder;
import com.ovvium.mother.builder.EmployeeBuilder;
import com.ovvium.mother.model.CustomerMother;
import com.ovvium.mother.model.UserMother;
import com.ovvium.services.model.exception.OvviumDomainException;
import com.ovvium.services.model.user.User;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import com.ovvium.services.util.util.xson.Xson;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

public class CustomerTest {

	@Test
	public void given_empty_split_code_when_create_customer_then_should_throw_exception() {
		User user = UserMother.getCustomerUserFAdria();

		assertThatThrownBy(() ->
				new Customer(
						user,
						"Name",
						"Description",
						"CIF",
						"Address",
						Collections.singleton("977897865"),
						"",
						CommissionConfig.cardCategory(0.5,0.2, 0.09),
						CustomerMother.anyInvoiceNumberPrefix()
				)
		).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Split id cannot be blank.");
	}

	@Test
	public void given_wrong_phone_format_when_create_customer_then_should_throw_exception() {
		final User user = UserMother.getCustomerUserFAdria();

		assertThatThrownBy(() ->
				new Customer(
						user,
						"Name",
						"Description",
						"CIF",
						"Address",
						Collections.singleton("1_1_233"),
						"split",
						CommissionConfig.cardCategory(0.5,0.2, 0.09),
						CustomerMother.anyInvoiceNumberPrefix()
				)
		).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Phone 1_1_233 is not a valid phone format.");
	}

	@Test
	public void given_employee_access_code_when_get_by_access_code_then_should_return_correct_employee_by_access_code() {
		final Customer customer = CustomerMother.getCanRocaCustomer();
		final Employee employee = new EmployeeBuilder()
				.setAccessCode("1235")
				.setCustomer(customer)
				.build();

		Employee employeeByCode = customer.getEmployeeByCode(employee.getAccessCode());

		assertThat(employeeByCode).isEqualTo(employee);
		assertThat(employeeByCode.getAccessCode()).isEqualTo(employee.getAccessCode());
	}

	@Test
	public void given_wrong_employee_access_code_when_get_by_access_code_then_should_throw_exception() {
		final Customer customer = CustomerMother.getCanRocaCustomer();

		assertThatThrownBy(() -> customer.getEmployeeByCode("0000"))
				.isInstanceOf(OvviumDomainException.class)
				.hasMessage("This employee code is not found on this Customer.");
	}

	@Test
	public void given_commission_config_when_create_customer_then_should_convert_field_to_json() {
		final Customer customer = new CustomerBuilder()
				.setCommissionConfig(CommissionConfig.cardCategory(0.5, 0.005, 0.9))
				.build();

		final String commissionConfig = (String) ReflectionUtils.get(customer, "commissionConfig");
		assertThat(commissionConfig).isNotBlank();
		assertThatCode(() -> Xson.create(commissionConfig).as(CommissionConfig.class))
				.doesNotThrowAnyException();
	}

}