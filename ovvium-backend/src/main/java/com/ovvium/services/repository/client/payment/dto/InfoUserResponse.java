package com.ovvium.services.repository.client.payment.dto;

import com.ovvium.services.repository.client.payment.ws.dto.InfoUserWsResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InfoUserResponse {

	private final String cardPan;
	private final String cardBrand;
	private final String cardType;
	private final String cardCountry;
	private final String cardExpiryDate;
	private final String cardHash;
	private final String cardCategory;
	private final Integer cardSepa;

	public InfoUserResponse(InfoUserWsResponse response) {
		this.cardPan = response.getCardPan();
		this.cardBrand = response.getCardBrand();
		this.cardType = response.getCardType();
		this.cardCountry = response.getCardCountry();
		this.cardExpiryDate = response.getCardExpiryDate();
		this.cardHash = response.getCardHash();
		this.cardCategory = response.getCardCategory();
		this.cardSepa = response.getCardSepa();
	}

}
