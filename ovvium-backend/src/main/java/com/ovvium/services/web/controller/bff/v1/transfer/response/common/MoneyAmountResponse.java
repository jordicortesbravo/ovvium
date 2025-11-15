package com.ovvium.services.web.controller.bff.v1.transfer.response.common;

import com.ovvium.services.model.common.MoneyAmount;
import lombok.Getter;

import java.io.Serializable;

@Getter
public final class MoneyAmountResponse implements Serializable {

	private final double amount;
	private final String currency;

	public MoneyAmountResponse(MoneyAmount amount) {
		this.amount = amount.asDouble();
		this.currency = amount.getCurrency().getCurrencyCode();
	}
}
