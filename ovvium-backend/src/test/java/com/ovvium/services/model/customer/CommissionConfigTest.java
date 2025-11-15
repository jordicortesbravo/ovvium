package com.ovvium.services.model.customer;

import com.ovvium.services.app.config.properties.PaycometCommissionProperties;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.payment.CardCategory;
import com.ovvium.services.model.payment.CardLocation;
import com.ovvium.services.util.util.xson.Xson;
import org.junit.Test;

import java.util.Map;

import static com.ovvium.services.model.payment.CardCategory.BUSINESS;
import static com.ovvium.services.model.payment.CardCategory.CONSUMER;
import static com.ovvium.services.model.payment.CardLocation.*;
import static org.assertj.core.api.Assertions.*;

public class CommissionConfigTest {

	private final static PaycometCommissionProperties paycometCommissionProperties = Xson.ofResource("paycomet/paycomet_commissions-test.json").as(PaycometCommissionProperties.class);

	@Test
	public void given_commission_minimum_properties_and_missing_category_when_create_card_category_commission_then_should_throw_exception() {

		final Map<CardCategory, Map<CardLocation, CommissionPair>> commissionMap = Map.of(BUSINESS, Map.of(EUROPEAN, new CommissionPair(1.0, 0.0)));

		assertThatThrownBy(() -> CommissionConfig.cardCategory(paycometCommissionProperties, 0.5, commissionMap))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Configuration does not contain all Card Category values" );

	}

	@Test
	public void given_commission_minimum_properties_and_missing_location_when_create_card_category_commission_then_should_throw_exception() {

		final Map<CardCategory, Map<CardLocation, CommissionPair>> commissionMap = Map.of(
				BUSINESS, Map.of(EUROPEAN, new CommissionPair(1.0, 0.0)),
				CONSUMER, Map.of(EUROPEAN, new CommissionPair(1.0, 0.0))
		);

		assertThatThrownBy(() -> CommissionConfig.cardCategory(paycometCommissionProperties, 0.5, commissionMap))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Configuration does not contain all Card Location values" );

	}

	@Test
	public void given_commission_minimum_properties_and_less_base_percentage_when_create_card_category_commission_then_should_throw_exception() {

		final Map<CardCategory, Map<CardLocation, CommissionPair>> commissionMap = getCommissionMap(0.0, 1.0);

		assertThatThrownBy(() -> CommissionConfig.cardCategory(paycometCommissionProperties, 0.5, commissionMap))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Base Percentage 0.0 is lower than Paycomet base percentage minimum" );

	}

	@Test
	public void given_commission_minimum_properties_and_less_commissions_when_create_card_category_commission_then_should_throw_exception() {

		final Map<CardCategory, Map<CardLocation, CommissionPair>> commissionMap = getCommissionMap(1.0, 0.0);

		assertThatThrownBy(() -> CommissionConfig.cardCategory(paycometCommissionProperties, 0.5, commissionMap))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Commission 0.0 is lower than Paycomet commission minimum" );

	}

	@Test
	public void given_commission_minimum_properties_and_correct_commission_when_create_card_category_commission_then_should_not_throw_exception() {

		final Map<CardCategory, Map<CardLocation, CommissionPair>> commissionMap = getCommissionMap(1.0, 1.0);

		assertThatCode(() -> CommissionConfig.cardCategory(paycometCommissionProperties, 0.5, commissionMap))
				.doesNotThrowAnyException();

	}

	@Test
	public void given_commission_config_when_get_max_minimum_commission_then_should_return_maximum_value() {
		final Map<CardLocation, CommissionPair> map = Map.of(
				EUROPEAN, new CommissionPair(0.5, 0.1),
				EUROPEAN_NO_EEA, new CommissionPair(0.5, 0.2),
				NATIONAL, new CommissionPair(0.5, 0.3),
				NOT_EUROPEAN, new CommissionPair(0.5, 0.4)
		);
		Map<CardCategory, Map<CardLocation, CommissionPair>> commissiongConfigMap = Map.of(
				BUSINESS, map,
				CONSUMER, map
		);
		MoneyAmount maxMinimumCommission = CommissionConfig.cardCategory(paycometCommissionProperties, 0.5, commissiongConfigMap)
				.getMaxMinimumCommission();

		assertThat(maxMinimumCommission).isEqualTo(MoneyAmount.ofDouble(0.4));
	}

	private Map<CardCategory, Map<CardLocation, CommissionPair>> getCommissionMap(double basePercentage, double commission) {
		final Map<CardLocation, CommissionPair> map = Map.of(
				EUROPEAN, new CommissionPair(basePercentage, commission),
				EUROPEAN_NO_EEA, new CommissionPair(basePercentage, commission),
				NATIONAL, new CommissionPair(basePercentage, commission),
				NOT_EUROPEAN, new CommissionPair(basePercentage, commission)
		);
		return Map.of(
				BUSINESS, map,
				CONSUMER, map
		);
	}
}