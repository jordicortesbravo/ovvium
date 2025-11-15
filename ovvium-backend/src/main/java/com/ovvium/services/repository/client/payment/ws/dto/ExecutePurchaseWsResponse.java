package com.ovvium.services.repository.client.payment.ws.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper=false)
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class ExecutePurchaseWsResponse extends AbstractWsResponse {

	@XmlElement(name = "DS_MERCHANT_AMOUNT")
	private Integer amount;

	@XmlElement(name = "DS_MERCHANT_ORDER")
	private String order;

	@XmlElement(name = "DS_MERCHANT_CURRENCY")
	private String currency;
	
	@XmlElement(name = "DS_MERCHANT_AUTHCODE")
	private String authCode;
	
	@XmlElement(name = "DS_MERCHANT_CARDCOUNTRY")
	private Integer cardCountry;
	
	@XmlElement(name = "DS_RESPONSE")
	private Integer response;
	
	@XmlElement(name = "DS_CHALLENGE_URL")
	private String challengeUrl;

	public Optional<String> getChallengeUrl() {
		return Optional.ofNullable(challengeUrl);
	}
}
