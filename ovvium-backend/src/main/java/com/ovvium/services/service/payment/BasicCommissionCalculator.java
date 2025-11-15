package com.ovvium.services.service.payment;

import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.customer.CommissionConfig;
import com.ovvium.services.model.payment.CommissionStrategy;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

import static com.ovvium.services.model.common.MoneyAmount.ZERO;
import static com.ovvium.services.model.payment.CommissionStrategy.BASIC;
import static java.math.BigDecimal.valueOf;

/**
 * Basic Commission calculator.
 * Get Tip amount calculated from the tip percentage, and check it is always greater than the minimum commission.
 * Otherwise, return the minimum commission to charge.
 * This strategy is a bit dangerous as we don't know exactly which commission will the provider charge us.
 */
@Component
@RequiredArgsConstructor
public class BasicCommissionCalculator implements CommissionCalculatorStrategy {

	@Override
	public MoneyAmount calculate(CommissionConfig config, MoneyAmount tipAmount, CommissionCardDetails cardDetails) {
		val minimumCommission = new MoneyAmount(valueOf(config.getMinimumCommission()), tipAmount.getCurrency().getCurrencyCode());
		return getTipComissionOrMinimum(tipAmount, config.getTipPercentage(), minimumCommission);
	}

	@Override
	public CommissionStrategy getType() {
		return BASIC;
	}

	private MoneyAmount getTipComissionOrMinimum(MoneyAmount tipAmount, double tipPercentage, MoneyAmount minimumCommission) {
		if (tipAmount.equals(ZERO)) {
			return minimumCommission;
		}
		val tipCommission = tipAmount.multiply(tipPercentage);
		return tipCommission.isGreaterOrEqualThan(minimumCommission) ? tipCommission : minimumCommission;
	}

}
