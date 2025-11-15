package com.ovvium.services.service.payment;

import com.ovvium.services.app.config.properties.PaycometCommissionProperties;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.customer.CommissionConfig;
import com.ovvium.services.model.customer.CommissionPair;
import com.ovvium.services.model.payment.CardCategory;
import com.ovvium.services.model.payment.CardLocation;
import com.ovvium.services.util.util.container.Maps;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.ovvium.services.model.payment.CardCategory.BUSINESS;
import static com.ovvium.services.model.payment.CardCategory.CONSUMER;
import static com.ovvium.services.model.payment.CardLocation.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CardCategoryCommissionCalculatorTest {

	private CardCategoryCommissionCalculator calculator;

	private PaycometCommissionProperties paymentProperties;

	@Before
	public void setUp() {
		paymentProperties = mock(PaycometCommissionProperties.class);
		calculator = new CardCategoryCommissionCalculator(paymentProperties);
	}

	@Test
	public void given_tip_amount_greater_than_provider_commission_when_calculate_card_category_commission_then_should_return_tip_commission() {
		CommissionConfig commissionConfig = CommissionConfig.cardCategory(0.5, 0.005, 0.09);
		MoneyAmount tipAmount = MoneyAmount.ofDouble(4);

		when(paymentProperties.getSplitCommissionPercentage()).thenReturn(0.0015);

		MoneyAmount commission = calculator.calculate(commissionConfig, tipAmount, new CommissionCardDetails(BUSINESS, "ESP"));

		assertThat(commission.asDouble()).isEqualTo(2);
	}

	@Test
	public void given_no_tip_amount_when_calculate_card_category_commission_then_should_return_provider_commission() {
		CommissionConfig commissionConfig = CommissionConfig.cardCategory(0.5, 0.005, 0.09);
		MoneyAmount tipAmount = MoneyAmount.ZERO;

		when(paymentProperties.getSplitCommissionPercentage()).thenReturn(0.0015);

		MoneyAmount commission = calculator.calculate(commissionConfig, tipAmount, new CommissionCardDetails(BUSINESS, "ESP"));

		assertThat(commission.asDouble()).isEqualTo(0.09);
	}

	@Test
	public void given_tip_amount_less_than_provider_commission_when_calculate_card_category_commission_then_should_return_provider_commission() {
		CommissionConfig commissionConfig = CommissionConfig.cardCategory(0.5, 0.005, 0.09);
		MoneyAmount tipAmount = MoneyAmount.ofDouble(0.10);

		when(paymentProperties.getSplitCommissionPercentage()).thenReturn(0.0015);

		MoneyAmount commission = calculator.calculate(commissionConfig, tipAmount, new CommissionCardDetails(BUSINESS, "ESP"));

		assertThat(commission.asDouble()).isEqualTo(0.09);
	}

	@Test
	public void given_tip_amount_equals_than_provider_commission_when_calculate_card_category_commission_then_should_return_tip_commission() {
		CommissionConfig commissionConfig = CommissionConfig.cardCategory(0.5, 0.005, 0.09);
		MoneyAmount tipAmount = MoneyAmount.ofDouble(0.10);

		when(paymentProperties.getSplitCommissionPercentage()).thenReturn(0.0015);

		MoneyAmount commission = calculator.calculate(commissionConfig, tipAmount, new CommissionCardDetails(BUSINESS, "ESP"));

		assertThat(commission.asDouble()).isEqualTo(0.09);
	}

	@Test
	public void given_iso_country_not_on_country_config_when_calculate_card_category_commission_then_should_return_not_european_country_location_commission() {
		CommissionConfig commissionConfig = CommissionConfig.cardCategory(paymentProperties, 0.5, new HashMap<CardCategory, Map<CardLocation, CommissionPair>>() {{
			put(BUSINESS, Maps.map(CardLocation.class, CommissionPair.class)
					.with(EUROPEAN, new CommissionPair(0.5, 0.1))
					.with(EUROPEAN_NO_EEA, new CommissionPair(0.5, 0.1))
					.with(NATIONAL, new CommissionPair(0.5, 0.1))
					.with(NOT_EUROPEAN, new CommissionPair(0.5, 0.8))
			);
			put(CONSUMER, Maps.map(CardLocation.class, CommissionPair.class)
					.with(EUROPEAN, new CommissionPair(0.5, 0.1))
					.with(EUROPEAN_NO_EEA, new CommissionPair(0.5, 0.1))
					.with(NATIONAL, new CommissionPair(0.5, 0.1))
					.with(NOT_EUROPEAN, new CommissionPair(0.5, 0.8))
			);
		}});
		MoneyAmount tipAmount = MoneyAmount.ZERO;

		when(paymentProperties.getSplitCommissionPercentage()).thenReturn(0.0015);

		MoneyAmount commission = calculator.calculate(commissionConfig, tipAmount, new CommissionCardDetails(BUSINESS, "XXX"));

		assertThat(commission.asDouble()).isEqualTo(0.8);
	}

}