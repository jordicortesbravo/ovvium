package com.ovvium.services.model.product;

import com.ovvium.mother.model.CustomerMother;
import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.customer.Customer;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CategoryTest {

	@Test
	public void given_wrong_order_for_category_when_create_category_then_should_throw_exception() {
		Customer customer = CustomerMother.getElBulliCustomer();
		int wrongOrder = -1;

		assertThatThrownBy(() -> {
			new Category(customer, new MultiLangString("Name"), wrongOrder);
		}).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Order cannot be negative");
	}

}