package com.ovvium.services.model.customer;

import lombok.Data;

import static com.ovvium.services.util.ovvium.base.Preconditions.check;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkRange;

@Data
public class CommissionPair {

	private double basePercentage;
	private double commission;

	public CommissionPair(double basePercentage, double commission) {
		this.basePercentage = checkRange(basePercentage, 0, 1, "Base Percentage must be between 0 and 1");
		this.commission = check(commission, commission >= 0, "Commission must be positive");
	}

}