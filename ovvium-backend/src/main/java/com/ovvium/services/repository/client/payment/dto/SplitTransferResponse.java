package com.ovvium.services.repository.client.payment.dto;

import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.repository.client.payment.ws.dto.SplitTransferWsResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SplitTransferResponse  {

	private Integer submerchantAmount;
	private String order;
	private String submerchantCurrency;
	private String transferAuthCode;
	private Integer response;

	public SplitTransferResponse(SplitTransferWsResponse response) {
		this.submerchantAmount = response.getSubmerchantAmount();
		this.order = response.getOrder();
		this.submerchantCurrency = response.getSubmerchantCurrency();
		this.transferAuthCode = response.getTransferAuthCode();
		this.response = response.getResponse();
	}

	public MoneyAmount getMoneyAmount() {
		return MoneyAmount.ofInteger(submerchantAmount, submerchantCurrency);
	}
}
