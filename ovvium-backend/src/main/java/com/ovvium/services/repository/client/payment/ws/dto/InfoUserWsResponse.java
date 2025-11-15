package com.ovvium.services.repository.client.payment.ws.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@Data
@EqualsAndHashCode(callSuper=false)
@XmlType
@Accessors(chain = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class InfoUserWsResponse extends AbstractWsResponse {

	@XmlElement(name = "DS_MERCHANT_PAN")
	private String cardPan;

	@XmlElement(name = "DS_CARD_BRAND")
	private String cardBrand;

	@XmlElement(name = "DS_CARD_TYPE")
	private String cardType;

	@XmlElement(name = "DS_CARD_I_COUNTRY_ISO3")
	private String cardCountry;

	@XmlElement(name = "DS_EXPIRYDATE")
	private String cardExpiryDate;

	@XmlElement(name = "DS_CARD_HASH")
	private String cardHash;

	@XmlElement(name = "DS_CARD_CATEGORY")
	private String cardCategory;

	@XmlElement(name = "DS_SEPA_CARD")
	private Integer cardSepa;

}
