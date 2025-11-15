package com.ovvium.services.web.controller.bff.v1.transfer.request.customer;

import com.ovvium.services.model.payment.CardCategory;
import com.ovvium.services.model.payment.CardLocation;
import com.ovvium.services.model.payment.CommissionStrategy;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@Accessors(chain = true)
public class CreateCommissionConfigRequest {

	@NotNull
	private CommissionStrategy strategy;

	private double tipPercentage;

	private double minimumCommission;

	@NotEmpty
	private Map<CardCategory, Map<CardLocation, CreateCommissionPairRequest>> config;

}
