package com.ovvium.services.repository.client.payment.dto;

import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.customer.Customer;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(fluent = true)
public class SplitTransferRequest {

	private final Customer customer;
	private final UUID order;
	private final String authCode;
	private final String submerchantSplitId;
	private final MoneyAmount submerchantAmount;

}
