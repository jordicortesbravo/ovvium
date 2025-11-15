package com.ovvium.services.service.payment;

import com.ovvium.services.model.payment.CommissionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Component
public class CommissionCalculatorStrategyFactory {

	private final Map<CommissionStrategy, CommissionCalculatorStrategy> strategyMap;
	private final BasicCommissionCalculator basicCommissionCalculator;

	@Autowired
	public CommissionCalculatorStrategyFactory(List<CommissionCalculatorStrategy> strategies, BasicCommissionCalculator basicCommissionCalculator) {
		this.strategyMap = strategies.stream().collect(toMap(CommissionCalculatorStrategy::getType, identity()));
		this.basicCommissionCalculator = basicCommissionCalculator;
	}

	public CommissionCalculatorStrategy getStrategy(CommissionStrategy strategy) {
		return strategyMap.getOrDefault(strategy, basicCommissionCalculator);
	}

}
