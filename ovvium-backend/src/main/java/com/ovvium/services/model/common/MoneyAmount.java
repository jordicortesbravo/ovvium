package com.ovvium.services.model.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;

import static com.ovvium.services.util.ovvium.base.Preconditions.check;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotNull;
import static java.lang.Math.pow;
import static java.lang.String.format;
import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;
import static lombok.AccessLevel.PROTECTED;

/**
 * Money Value Object. Money should be represented as a BigDecimal along with
 * its Currency.<br/>
 * Now only EUR currency is implemented, so EUR is the default.<br/>
 * // FIXME This can be improved with a library or upgrading to Java 9 Money
 * API.
 */
@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
public final class MoneyAmount implements Comparable<MoneyAmount> {

	private static final int SCALE = 4;
	private static final RoundingMode DEFAULT_ROUND_MODE = HALF_UP;
	private final static MathContext DEFAULT_CONTEXT = new MathContext(10, DEFAULT_ROUND_MODE);
	private final static String EUR_CURRENCY = "EUR";

	public final static MoneyAmount ZERO = new MoneyAmount(java.math.BigDecimal.ZERO);

	@Column(precision = 19, scale = SCALE)
	private BigDecimal amount;

	private Currency currency;

	public MoneyAmount(BigDecimal amount, String currency) {
		BigDecimal value = checkNotNull(amount, "Amount can´t be null");
		if (isNegative(value)) {
			throw new IllegalArgumentException("Amount can´t be negative.");
		}
		this.currency = Currency.getInstance(currency);
		this.amount = value.setScale(SCALE, DEFAULT_ROUND_MODE);
	}

	public MoneyAmount(BigDecimal amount) {
		this(amount, EUR_CURRENCY);
	}

	public MoneyAmount add(MoneyAmount other) {
		checkSameCurrency(other);
		return new MoneyAmount(this.amount.add(other.amount, DEFAULT_CONTEXT));
	}

	public MoneyAmount subtract(MoneyAmount other) {
		checkSameCurrency(other);
		val result = this.amount.subtract(other.amount, DEFAULT_CONTEXT);
		return isNegative(result) ? MoneyAmount.ZERO : new MoneyAmount(result);
	}

	public MoneyAmount multiply(double value) {
		return new MoneyAmount(amount.multiply(valueOf(value), DEFAULT_CONTEXT), currency.getCurrencyCode());
	}

	public int asInt() {
		return Integer.parseInt(asPrice().replace(".", ""));
	}

	public static MoneyAmount ofDouble(double amount) {
		return new MoneyAmount(valueOf(amount));
	}

	public MoneyAmount withAmount(double amount) {
		return new MoneyAmount(valueOf(amount), this.currency.getCurrencyCode());
	}

	public static MoneyAmount ofInteger(int amount, String currency) {
		Currency moneyCurrency = Currency.getInstance(currency);
		double moneyAmount = amount;
		int fractionDigits = moneyCurrency.getDefaultFractionDigits();
		if (fractionDigits > 0) {
			moneyAmount = (double) amount / pow(10, fractionDigits);
		}
		return new MoneyAmount(valueOf(moneyAmount), currency);
	}

	public String asPrice() {
		return getCurrencyScaled().toString();
	}

	public double asDouble() {
		return getCurrencyScaled().doubleValue();
	}

	public boolean isGreaterOrEqualThan(MoneyAmount other) {
		checkSameCurrency(other);
		return compareTo(other) != -1;
	}

	public boolean isLessOrEqualThan(MoneyAmount other) {
		checkSameCurrency(other);
		return compareTo(other) != 1;
	}

	@Override
	public String toString() {
		return asPrice();
	}

	@Override
	public int compareTo(MoneyAmount val) {
		return amount.compareTo(val.amount);
	}

	private BigDecimal getCurrencyScaled() {
		return amount.setScale(currency.getDefaultFractionDigits(), DEFAULT_ROUND_MODE);
	}

	private void checkSameCurrency(MoneyAmount other) {
		val currencyCode = this.currency.getCurrencyCode();
		val otherCurrencyCode = other.currency.getCurrencyCode();
		check(currencyCode.equalsIgnoreCase(otherCurrencyCode),
				format("Currencies must be the same to perform this operation (%s, %s)", currencyCode, otherCurrencyCode));
	}

	private boolean isNegative(BigDecimal b) {
		return b.signum() == -1;
	}
}
