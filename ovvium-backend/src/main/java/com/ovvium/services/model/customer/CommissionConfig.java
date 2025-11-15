package com.ovvium.services.model.customer;

import com.ovvium.services.app.config.properties.PaycometCommissionProperties;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.payment.CardCategory;
import com.ovvium.services.model.payment.CardLocation;
import com.ovvium.services.model.payment.CommissionStrategy;
import com.ovvium.services.util.util.xson.Xson;
import lombok.Data;
import lombok.Setter;
import lombok.val;

import java.util.*;
import java.util.stream.Stream;

import static com.ovvium.services.model.payment.CommissionStrategy.BASIC;
import static com.ovvium.services.model.payment.CommissionStrategy.BY_CARD_CATEGORY;
import static com.ovvium.services.util.ovvium.base.Preconditions.*;
import static lombok.AccessLevel.PROTECTED;

@Data
public class CommissionConfig {

	private CommissionStrategy strategy;

	private double tipPercentage;

	private double minimumCommission;

	@Setter(PROTECTED)
	private Map<CardCategory, Map<CardLocation, CommissionPair>> config = new HashMap<>();

	private CommissionConfig(CommissionStrategy strategy, double tipPercentage) {
		this.strategy = checkNotNull(strategy, "Strategy can't be null");
		this.tipPercentage = checkRange(tipPercentage, 0, 1, "Tip Percentage must be between 0 and 1");
	}

	public String toJson() {
		return Xson.of(this).writer().setSerializeNulls(false).toString();
	}

	public MoneyAmount getMaxMinimumCommission() {
		return config.values().stream()
				.map(Map::values)
				.flatMap(Collection::stream)
				.map(CommissionPair::getCommission)
				.max(Double::compareTo)
				.map(MoneyAmount::ofDouble)
		.orElse(MoneyAmount.ofDouble(minimumCommission));
	}

	public static CommissionConfig of(String configJson) {
		return Xson.create(checkNotBlank(configJson, "Json cannot be blank")).as(CommissionConfig.class);
	}

	public static CommissionConfig basic(double tipPercentage, double minimumCommission) {
		val commission = new CommissionConfig(BASIC, tipPercentage);
		commission.minimumCommission = check(minimumCommission, minimumCommission >= 0, "Minimum Commission must be positive");
		return commission;
	}

	public static CommissionConfig cardCategory(double tipPercentage, double basePercentage, double commissionAmount) {
		val commission = new CommissionConfig(BY_CARD_CATEGORY, tipPercentage);
		Stream.of(CardCategory.values()).forEach((it) ->
				commission.config.put(it, new HashMap<>() {{
					Arrays.stream(CardLocation.values()).forEach(cl ->
							this.put(cl, new CommissionPair(basePercentage, commissionAmount))
					);
				}}));
		return commission;
	}

	public static CommissionConfig cardCategory(PaycometCommissionProperties minimumCommissions, double tipPercentage, Map<CardCategory, Map<CardLocation, CommissionPair>> config) {
		checkAllValuesConfigured(config);
		checkMinimumCommissions(minimumCommissions, config);
		val commission = new CommissionConfig(BY_CARD_CATEGORY, tipPercentage);
		commission.config = new HashMap<>(config);
		return commission;
	}

	private static void checkAllValuesConfigured(Map<CardCategory, Map<CardLocation, CommissionPair>> config) {
		check(config.keySet().containsAll(EnumSet.allOf(CardCategory.class)), "Configuration does not contain all Card Category values");
		config.forEach((k, v) -> check(v.keySet().containsAll(EnumSet.allOf(CardLocation.class)), "Configuration does not contain all Card Location values"));
	}

	private static void checkMinimumCommissions(PaycometCommissionProperties minimumCommissions, Map<CardCategory, Map<CardLocation, CommissionPair>> config) {
		minimumCommissions.getConfig().forEach((k, v) ->
				Optional.ofNullable(config.get(k))
						.ifPresentOrElse(pairMap -> pairMap.forEach((l, p) ->
								Optional.ofNullable(v.get(l))
										.ifPresentOrElse(minimumPair -> {
											check(p.getBasePercentage() >= minimumPair.getBasePercentage(), String.format("Base Percentage %s is lower than Paycomet base percentage minimum", p.getBasePercentage()));
											check(p.getCommission() >= minimumPair.getCommission(), String.format("Commission %s is lower than Paycomet commission minimum", p.getCommission()));
										}, () -> {
											throw new IllegalArgumentException(l + " location was missing on config.");
										})), () -> {
							throw new IllegalArgumentException(k + " category was missing on config.");
						})
		);
	}
}
