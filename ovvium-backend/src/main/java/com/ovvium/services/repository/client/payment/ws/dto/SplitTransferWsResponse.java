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
public class SplitTransferWsResponse extends AbstractWsResponse {

	@XmlElement(name = "DS_SUBMERCHANT_AMOUNT")
	private Integer submerchantAmount;

	@XmlElement(name = "DS_MERCHANT_ORDER")
	private String order;

	@XmlElement(name = "DS_SUBMERCHANT_CURRENCY")
	private String submerchantCurrency;

	@XmlElement(name = "DS_MERCHANT_TRANSFER_AUTHCODE")
	private String transferAuthCode;

	@XmlElement(name = "DS_RESPONSE")
	private Integer response;

}
