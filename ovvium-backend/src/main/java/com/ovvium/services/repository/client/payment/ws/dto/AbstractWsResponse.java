package com.ovvium.services.repository.client.payment.ws.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@Data
@XmlType
@Accessors(fluent = true)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractWsResponse {

	@XmlElement(name = "DS_ERROR_ID")
	private String errorId;


}
