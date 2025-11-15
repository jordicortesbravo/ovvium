package com.ovvium.services.service.payment;

import com.ovvium.services.app.config.properties.PaycometCommissionProperties;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.customer.CommissionConfig;
import com.ovvium.services.model.customer.CommissionPair;
import com.ovvium.services.model.payment.CardLocation;
import com.ovvium.services.model.payment.CommissionStrategy;
import com.ovvium.services.util.util.io.IOUtils;
import com.ovvium.services.util.util.xson.Xson;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.ovvium.services.model.payment.CardLocation.NOT_EUROPEAN;
import static com.ovvium.services.model.payment.CommissionStrategy.BY_CARD_CATEGORY;
import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class CardCategoryCommissionCalculator implements CommissionCalculatorStrategy {

	private final PaycometCommissionProperties paycometCommissionProperties;
	private final Map<String, CardLocation> countryLocationByIsoCode;

	@Autowired
	public CardCategoryCommissionCalculator(PaycometCommissionProperties paymentProperties) {
		this.paycometCommissionProperties = paymentProperties;
		this.countryLocationByIsoCode = getCardLocationMap();
	}

	@Override
	public CommissionStrategy getType() {
		return BY_CARD_CATEGORY;
	}

	/**
	 * Calculate the commission to get from the Tip.
	 * Calculate the commission will charge us Paycomet from this Tip.
	 * If our commission is greater than the provider´s, charge our commission to the Customer, otherwise charge the provider commission.
	 */
	@Override
	public MoneyAmount calculate(CommissionConfig commissionConfig, MoneyAmount tipAmount, CommissionCardDetails cardDetails) {
		val config = commissionConfig.getConfig();
		val cardLocation = Optional.ofNullable(countryLocationByIsoCode.get(cardDetails.getIsoCountry()))
				.orElse(NOT_EUROPEAN);
		val commissionPairMap = config.get(cardDetails.getCardCategory());
		val commissionPair = commissionPairMap.get(cardLocation);
		val tipCommission = tipAmount.multiply(commissionConfig.getTipPercentage());
		val providerCommission = calculateProviderCommission(commissionPair, tipCommission);
		if (tipCommission.isGreaterOrEqualThan(providerCommission)) {
			return tipCommission;
		}
		return providerCommission;
	}

	/**
	 * This calculates the commission that the provider will charge us. For example, if the tip is an amount of 2:
	 *  2 - ( 0.5% + 0.09 + 0.15%) = 1.897
	 *  Where 0.15% is a commission for Split.
	 */
	private MoneyAmount calculateProviderCommission(CommissionPair commissionPair, MoneyAmount ovviumTipCommission) {
		val totalPercentage = commissionPair.getBasePercentage() + paycometCommissionProperties.getSplitCommissionPercentage();
		val providerComPercentageResult = ovviumTipCommission.multiply(totalPercentage);
		val providerAdquiryCommission = new MoneyAmount(BigDecimal.valueOf(commissionPair.getCommission()), ovviumTipCommission.getCurrency().getCurrencyCode());
		return providerAdquiryCommission.add(providerComPercentageResult);
	}

	private Map<String, CardLocation> getCardLocationMap() {
		Map<String, CardLocation> map = new HashMap<>();
		Xson.create(IOUtils.getResourceAsString("paycomet/paycomet_countries_mapping.json", UTF_8)).asMap().forEach((key, value) -> {
			val cl = CardLocation.valueOf(key);
			value.asList(String.class).forEach(country -> map.put(country, cl));
		});
		return map;
	}
}
