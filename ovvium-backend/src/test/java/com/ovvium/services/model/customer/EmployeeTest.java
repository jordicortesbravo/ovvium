package com.ovvium.services.model.customer;

import com.ovvium.mother.model.CustomerMother;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EmployeeTest {

	@Test
	public void given_incorrect_non_numeric_access_code_when_new_employee_then_throw_exception() {
		final String accesscode = "aaaa";

		assertThatThrownBy(() ->
				new Employee(CustomerMother.getElBulliCustomer(), "Paco", accesscode)
		).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Access code must be a 4-length numeric code.");
	}


	@Test
	public void given_incorrect_length_access_code_when_new_employee_then_throw_exception() {
		final String accesscode = "12345";

		assertThatThrownBy(() ->
				new Employee(CustomerMother.getElBulliCustomer(), "Paco", accesscode)
		).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Access code must be a 4-length numeric code.");
	}


	@Test
	public void given_correct_access_code_when_new_employee_then_create_correct_employee() {
		final String accesscode = "1234";

		Employee paco = new Employee(CustomerMother.getElBulliCustomer(), "Paco", accesscode);

		assertThat(paco.getName()).isEqualTo("Paco");
		assertThat(paco.getAccessCode()).isEqualTo("1234");
	}
}