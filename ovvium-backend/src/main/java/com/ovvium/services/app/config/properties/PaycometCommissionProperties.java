package com.ovvium.services.app.config.properties;

import com.ovvium.services.model.customer.CommissionPair;
import com.ovvium.services.model.payment.CardCategory;
import com.ovvium.services.model.payment.CardLocation;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class PaycometCommissionProperties {

	@NotNull
	private Double splitCommissionPercentage;

	private Map<CardCategory, Map<CardLocation, CommissionPair>> config;

}
