package com.ovvium.services.repository.client.payment.ws.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@Data
@XmlType
@Accessors(chain = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class SplitTransferWsRequest {

	@XmlElement(name = "DS_MERCHANT_MERCHANTCODE")
	private String merchantCode;

	@XmlElement(name = "DS_MERCHANT_TERMINAL")
	private String terminal;

	@XmlElement(name = "DS_MERCHANT_ORDER")
	private String order;

	@XmlElement(name = "DS_MERCHANT_AUTHCODE")
	private String authCode;

	@XmlElement(name = "DS_SUBMERCHANT_TERMINAL_SPLITID")
	private String submerchantSplitId;

	@XmlElement(name = "DS_SUBMERCHANT_AMOUNT")
	private String submerchantAmount;

	@XmlElement(name = "DS_SUBMERCHANT_CURRENCY")
	private String submerchantCurrency;

	@XmlElement(name = "DS_MERCHANT_MERCHANTSIGNATURE")
	private String signature;

}
