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
public class ExecutePurchaseWsRequest {

	@XmlElement(name="DS_MERCHANT_MERCHANTCODE")
	private String merchantCode;
	
	@XmlElement(name="DS_MERCHANT_TERMINAL")
	private String terminal;

	@XmlElement(name="DS_IDUSER")
	private String userId;
	
	@XmlElement(name="DS_TOKEN_USER")
	private String userToken;
	
	@XmlElement(name="DS_MERCHANT_AMOUNT")
	private String amount;
	
	@XmlElement(name="DS_MERCHANT_ORDER")
	private String order;

	@XmlElement(name="DS_MERCHANT_CURRENCY")
	private String currency;
	
	@XmlElement(name="DS_MERCHANT_PRODUCTDESCRIPTION")
	private String productDescription;
	
	@XmlElement(name="DS_MERCHANT_OWNER")
	private String owner;
	
	@XmlElement(name="DS_MERCHANT_SCORING")
	private Integer scoring;
	
	@XmlElement(name="DS_MERCHANT_DATA")
	private String merchantData;
	
	@XmlElement(name="DS_MERCHANT_MERCHANTDESCRIPTOR")
	private String descriptor;
	
	@XmlElement(name="DS_MERCHANT_SCA_EXCEPTION")
	private String scaException;
	
	@XmlElement(name="DS_MERCHANT_TRX_TYPE")
	private String trxType;
	
	@XmlElement(name="DS_ESCROW_TARGETS")
	private String escrowTargets;
	
	@XmlElement(name="DS_USER_INTERACTION")
	private Integer userInteraction;

	@XmlElement(name = "DS_MERCHANT_MERCHANTSIGNATURE")
	private String signature;

	@XmlElement(name = "DS_ORIGINAL_IP")
	private String originalIp;
}
