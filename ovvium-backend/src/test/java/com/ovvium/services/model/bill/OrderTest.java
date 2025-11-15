package com.ovvium.services.model.bill;

import com.ovvium.mother.builder.OrderBuilder;
import com.ovvium.mother.model.ProductMother;
import com.ovvium.mother.model.UserMother;
import com.ovvium.services.transfer.command.order.OrderGroupChoicesCommand;
import com.ovvium.services.transfer.command.order.OrderProductCommand;
import com.ovvium.services.util.util.basic.Utils;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ovvium.mother.model.ProductMother.*;
import static com.ovvium.services.model.bill.ServiceTime.SOONER;
import static com.ovvium.services.util.ovvium.domain.DomainStatus.DELETED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OrderTest {

	@Test
	public void given_order_pending_when_delete_then_should_delete_correctly() {
		Order order = new OrderBuilder()
				.setPaymentStatus(PaymentStatus.PENDING)
				.setIssueStatus(IssueStatus.PENDING)
				.build();

		order.delete();

		assertThat(order.getStatus()).isEqualTo(DELETED);
	}

	@Test
	public void given_order_different_to_issue_pending_when_delete_then_should_delete_order() {
		Order order = new OrderBuilder()
				.setPaymentStatus(PaymentStatus.PENDING)
				.setIssueStatus(IssueStatus.PREPARING)
				.build();

		order.delete();

		assertThat(order.getStatus()).isEqualTo(DELETED);
	}

	@Test
	public void given_order_different_to_payment_pending_when_delete_then_should_delete_order() {
		Order order = new OrderBuilder()
				.setPaymentStatus(PaymentStatus.PAID)
				.setIssueStatus(IssueStatus.PENDING)
				.build();

		order.delete();

		assertThat(order.getStatus()).isEqualTo(DELETED);
	}

	@Test
	public void given_order_with_order_group_choice_different_to_payment_pending_when_delete_then_should_delete_order_group_choice() {
		Order order = new OrderBuilder()
				.setProduct(ProductMother.getMenuDiarioProduct())
				.setChoices(Collections.singleton(new OrderGroupChoice(SOONER, getCervezaProduct())))
				.build();

		order.delete();

		assertThat(order.getStatus()).isEqualTo(DELETED);
		assertThat(Utils.first(order.getChoices()).getStatus()).isEqualTo(DELETED);
	}

	@Test
	public void given_single_product_and_user_when_create_order_from_values_then_should_create_order_properly() {
		var user = UserMother.getUserJorge();
		var cervezaProduct = getCervezaProduct();
		Order order = Order.oneOfValues(new OrderProductCommand(
				user,
				cervezaProduct,
				SOONER,
				"Notes",
				null,
				Collections.emptyList()
		));

		assertThat(order.getUser()).contains(user);
		assertThat(order.getProduct()).isEqualTo(cervezaProduct);
		assertThat(order.getServiceTime()).isEqualTo(SOONER);
		assertThat(order.getNotes()).contains("Notes");
	}

	@Test
	public void given_product_group_and_choices_when_create_order_from_values_then_should_create_order_properly() {
		var user = UserMother.getUserJorge();
		var menu = ProductMother.getMenuDiarioProduct();
		Order order = Order.oneOfValues(new OrderProductCommand(
				user,
				menu,
				SOONER,
				"Notes",
				List.of(new OrderGroupChoicesCommand(getCervezaProduct().getId(), "Notes")),
				Collections.emptyList()
		));

		assertThat(order.getUser()).contains(user);
		assertThat(order.getProduct()).isEqualTo(menu);
		assertThat(order.getChoices()).isNotEmpty();
		assertThat(order.getServiceTime()).isEqualTo(SOONER);
		assertThat(order.getNotes()).contains("Notes");
	}

	@Test
	public void given_product_group_and_wrong_choice_when_create_order_from_values_then_should_throw_exception() {
		var menu = ProductMother.getMenuDiarioProduct();
		var bravasProduct = getPatatasBravasProduct();
		var groupChoices = List.of(new OrderGroupChoicesCommand(bravasProduct.getId(), "Notes"));

		assertThatThrownBy(() ->
				Order.oneOfValues(new OrderProductCommand(
						null,
						menu,
						SOONER,
						"Notes",
						groupChoices,
						Collections.emptyList()
				))
		).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Product " + bravasProduct.getId() + " not found for this ProductGroup");

	}

	@Test
	public void given_single_product_with_options_and_user_when_create_order_from_values_then_should_create_order_properly() {
		var user = UserMother.getUserJorge();
		var coffeeProduct = getCoffeeProduct();
		var irishCoffeeOption = getIrishCoffeeOption();
		Order order = Order.oneOfValues(new OrderProductCommand(
				user,
				coffeeProduct,
				SOONER,
				"Notes",
				Collections.emptyList(),
				Collections.singletonList(ProductMother.IRISH_COFFEE_OPTION_ID)
		));

		assertThat(order.getUser()).contains(user);
		assertThat(order.getProduct()).isEqualTo(coffeeProduct);
		assertThat(order.getServiceTime()).isEqualTo(SOONER);
		assertThat(order.getNotes()).contains("Notes");
		assertThat(Utils.first(order.getSelectedOptions()).getTitle().getDefaultValue()).isEqualTo(irishCoffeeOption.getTitle().getDefaultValue());
		assertThat(order.getBasePrice()).isEqualTo(coffeeProduct.getBasePrice().add(irishCoffeeOption.getBasePrice()));
		assertThat(order.getPrice()).isEqualTo(coffeeProduct.getPrice().add(irishCoffeeOption.getPrice()));
	}
}