package com.ovvium.services.service.payment;

import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.customer.CommissionConfig;
import com.ovvium.services.model.payment.CommissionStrategy;

public interface CommissionCalculatorStrategy {

	MoneyAmount calculate(CommissionConfig config, MoneyAmount tipAmount, CommissionCardDetails cardDetails);

	CommissionStrategy getType();
}
