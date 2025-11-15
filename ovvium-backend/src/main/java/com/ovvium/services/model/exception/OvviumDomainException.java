package com.ovvium.services.model.exception;

import com.ovvium.services.util.ovvium.exception.DomainException;

public final class OvviumDomainException extends DomainException {

	private static final long serialVersionUID = 1L;

	public OvviumDomainException(ErrorCode errorCode) {
		super(errorCode.getMessage(), errorCode.getErrorCode(), null);
	}

	public OvviumDomainException(ErrorCode errorCode, String developerMessage) {
		super(errorCode.getMessage(), errorCode.getErrorCode(), developerMessage);
	}
}
