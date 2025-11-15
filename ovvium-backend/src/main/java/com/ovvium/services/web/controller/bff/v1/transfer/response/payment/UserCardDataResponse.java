package com.ovvium.services.web.controller.bff.v1.transfer.response.payment;

import com.ovvium.services.repository.client.payment.dto.InfoUserResponse;
import lombok.Data;

import java.util.UUID;

@Data
public final class UserCardDataResponse {

	private final UUID pciDetailsId;
	private final String pan;
	private final String brand;
	private final String type;
	private final String country;
	private final String expiryDate;
	private final String hash;
	private final String category;
	private final Integer sepa;

	public UserCardDataResponse(UUID pciDetailsId, InfoUserResponse response) {
		this.pciDetailsId = pciDetailsId;
		this.pan = response.getCardPan();
		this.brand = response.getCardBrand();
		this.type = response.getCardType();
		this.country = response.getCardCountry();
		this.expiryDate = response.getCardExpiryDate();
		this.hash = response.getCardHash();
		this.category = response.getCardCategory();
		this.sepa = response.getCardSepa();
	}

}
