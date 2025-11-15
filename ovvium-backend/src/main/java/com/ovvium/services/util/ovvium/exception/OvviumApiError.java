package com.ovvium.services.util.ovvium.exception;

import lombok.Data;

@Data
public class OvviumApiError {

	private final int code;
	private final String message;
	private final int errorCode;
	private final String developerMessage;

	private OvviumApiError(int status, Exception exception, int errorCode, String developerMessage) {
		this.errorCode = errorCode;
		this.developerMessage = developerMessage;
		this.code = status;
		this.message = exception.getMessage();
	}

	public static OvviumApiError of(int status, Exception exc) {
		if (exc instanceof DomainException) {
			DomainException domainException = (DomainException) exc;
			return new OvviumApiError(status, exc, domainException.getErrorCode(),
					domainException.getDeveloperMessage());
		}
		return new OvviumApiError(status, exc, status, null);
	}

}