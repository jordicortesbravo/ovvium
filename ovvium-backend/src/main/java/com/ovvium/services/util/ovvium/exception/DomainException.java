package com.ovvium.services.util.ovvium.exception;

import lombok.Getter;

@Getter
public abstract class DomainException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;
	
	private final int errorCode;
	private final String developerMessage;

	protected DomainException(String message, int errorCode, String developerMessage) {
		super(message);
		this.errorCode = errorCode;
		this.developerMessage = developerMessage;
	}

}
