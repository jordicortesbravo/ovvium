package com.ovvium.services.repository.client.payment.ws.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@Data
@EqualsAndHashCode(callSuper = false)
@XmlType
@Accessors(fluent = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class RemoveUserTokenWsResponse extends AbstractWsResponse {

	@XmlElement(name = "DS_RESPONSE")
	private Integer response;

}
