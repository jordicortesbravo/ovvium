package com.ovvium.services.model.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MoneyAmountTest {

	@Test
	public void given_null_amount_when_create_moneyAmount_then_throw_exception() {
		assertThatThrownBy(() -> new MoneyAmount(null)) //
				.isExactlyInstanceOf(IllegalArgumentException.class)//
				.hasMessage("Amount can´t be null");
	}

	@Test
	public void given_negative_amount_when_create_moneyAmount_then_throw_exception() {
		assertThatThrownBy(() -> new MoneyAmount(BigDecimal.valueOf(-1))) //
				.isExactlyInstanceOf(IllegalArgumentException.class)//
				.hasMessage("Amount can´t be negative.");
	}

	@Test
	public void given_double_amount_when_create_moneyAmount_from_double_then_equals_same_amount() {
		MoneyAmount moneyAmount = new MoneyAmount(BigDecimal.valueOf(12.30d));
		MoneyAmount doubleAMount = MoneyAmount.ofDouble(12.30d);

		assertThat(moneyAmount).isEqualTo(doubleAMount);
	}

	@Test
	public void given_two_amount_prices_when_add_moneyAmount_then_equals_sum_amount() {
		MoneyAmount moneyAmount1 = new MoneyAmount(BigDecimal.valueOf(12.99d));
		MoneyAmount moneyAmount2 = new MoneyAmount(BigDecimal.valueOf(12.99d));

		MoneyAmount total = moneyAmount1.add(moneyAmount2);
		assertThat(total).isEqualTo(MoneyAmount.ofDouble(25.98d));
	}

	@Test
	public void given_various_amount_prices_when_add_moneyAmount_and_return_as_price_then_equals_correct_amount() {
		double amount = 12.99d;
		MoneyAmount total = IntStream.rangeClosed(1, 9999).boxed() //
				.map(i -> MoneyAmount.ofDouble(amount)) //
				.reduce(MoneyAmount.ZERO, MoneyAmount::add);

		assertThat(total.getAmount().doubleValue()).isEqualTo(129887.01);
		// Here is a precision loss because it uses the fraction digits of the Currency!
		assertThat(total).isEqualTo(new MoneyAmount(new BigDecimal("129887.01")));
	}

	@Test
	public void given_amount_double_price_when_return_as_price_then_equals_correct_amount() {
		MoneyAmount amount = MoneyAmount.ofDouble(11.30000d);

		assertThat(amount.asPrice()).isEqualTo("11.30");
		assertThat(amount.getAmount().doubleValue()).isEqualTo(11.30d);
	}

	@Test
	public void given_amount_integer_price_and_eur_currency_when_return_as_price_then_equals_correct_amount() {
		MoneyAmount amount = MoneyAmount.ofInteger(1250, "EUR");

		assertThat(amount.asPrice()).isEqualTo("12.50");
		assertThat(amount.getAmount().doubleValue()).isEqualTo(12.50d);
	}

	@Test
	public void given_amount_integer_price_and_yen_currency_when_return_as_price_then_equals_correct_amount() {
		MoneyAmount amount = MoneyAmount.ofInteger(1250, "JPY");

		assertThat(amount.asPrice()).isEqualTo("1250");
		assertThat(amount.getAmount().doubleValue()).isEqualTo(1250d);
	}

	@ParameterizedTest
	@CsvSource({"1,2,true", "2,2,true", "3,2,false"})
	public void given_amount_and_other_when_lessOrEqualThan_then_should_equal_result(double amountValue, double otherValue, boolean result) {
		MoneyAmount amount = MoneyAmount.ofDouble(amountValue);
		MoneyAmount other = MoneyAmount.ofDouble(otherValue);

		boolean lessOrEqualThan = amount.isLessOrEqualThan(other);

		assertThat(lessOrEqualThan).isEqualTo(result);
	}

	@ParameterizedTest
	@CsvSource({"1,2,false", "2,2,true", "3,2,true"})
	public void given_amount_and_other_when_greaterOrEqualThan_then_should_equal_result(double amountValue, double otherValue, boolean result) {
		MoneyAmount amount = MoneyAmount.ofDouble(amountValue);
		MoneyAmount other = MoneyAmount.ofDouble(otherValue);

		boolean greaterOrEqualThan = amount.isGreaterOrEqualThan(other);

		assertThat(greaterOrEqualThan).isEqualTo(result);
	}

	@Test
	public void given_one_amount_and_percentage_when_multiply_then_return_correct_amount() {
		MoneyAmount amount = MoneyAmount.ofDouble(2);

		MoneyAmount result = amount.multiply(0.5);

		assertThat(result).isEqualTo(MoneyAmount.ofDouble(1));
	}

	@Test
	public void given_second_amount_and_percentage_when_multiply_then_return_correct_amount() {
		MoneyAmount amount = MoneyAmount.ofDouble(137.50);

		MoneyAmount result = amount.multiply(0.5);

		assertThat(result).isEqualTo(MoneyAmount.ofDouble(68.75));
	}

	@Test
	public void given_amount_from_calculated_tax_when_calculate_back_and_return_as_double_then_equals_correct_amount() {
		double productPrice = 1.50;
		double basePrice = productPrice / 1.1; // 10% tax

		MoneyAmount amount = MoneyAmount.ofDouble(basePrice);

		MoneyAmount productAmount = amount.add(amount.multiply(0.1));

		assertThat(productAmount.asDouble()).isEqualTo(1.5);
		assertThat(productAmount.asInt()).isEqualTo(150);
		assertThat(productAmount.asPrice()).isEqualTo("1.50");
	}

	@Test
	public void given_amount_from_calculated_tax_when_calculate_back_and_return_as_double_then_equals_correct_amount_2() {
		double productPrice = 15.99;
		double basePrice = productPrice / 1.21; // 21% tax

		MoneyAmount amount = MoneyAmount.ofDouble(basePrice); // 13.2148760331

		MoneyAmount productAmount = amount.add(amount.multiply(0.21));

		assertThat(productAmount.asDouble()).isEqualTo(15.99);
		assertThat(productAmount.asInt()).isEqualTo(1599);
		assertThat(productAmount.asPrice()).isEqualTo("15.99");
	}

	@Test
	public void given_amount_with_extra_scale_when_add_amount_then_should_not_lose_scale() {
		MoneyAmount amount = MoneyAmount.ofDouble(1.23456d);
		MoneyAmount otherAmount = MoneyAmount.ofDouble(0.1111d);

		MoneyAmount total = amount.add(otherAmount);

		assertThat(total.getAmount()).isEqualTo(BigDecimal.valueOf(1.3457));
		assertThat(total.asDouble()).isEqualTo(1.35);
	}

}
