package com.ovvium.services.repository.client.payment.ws.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

@Data
@XmlType
@Accessors(chain = true)
@XmlAccessorType(FIELD)
public class RemoveUserTokenWsRequest {

	@XmlElement(name = "DS_MERCHANT_MERCHANTCODE")
	private String merchantCode;

	@XmlElement(name = "DS_MERCHANT_TERMINAL")
	private String terminal;

	@XmlElement(name = "DS_IDUSER")
	private String userId;

	@XmlElement(name = "DS_TOKEN_USER")
	private String userToken;

	@XmlElement(name = "DS_MERCHANT_MERCHANTSIGNATURE")
	private String signature;

	@XmlElement(name = "DS_ORIGINAL_IP")
	private String originalIp;

}
