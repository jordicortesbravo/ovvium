package com.ovvium.services.service.payment;

import com.ovvium.services.model.payment.CardCategory;
import lombok.Data;

@Data
public class CommissionCardDetails {

	private final CardCategory cardCategory;
	private final String isoCountry;

}
