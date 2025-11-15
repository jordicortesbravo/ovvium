package com.ovvium.services.web.controller.bff.v1.transfer.request.payment;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Optional;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public abstract class PaymentRequest {

	private Double tipAmount;

	public Optional<Double> getTipAmount() {
		return Optional.ofNullable(tipAmount);
	}

}
