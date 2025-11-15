package com.ovvium.services.security.exception;

import org.springframework.security.core.AuthenticationException;

public final class ExpiredTokenException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

	public ExpiredTokenException(String message) {
		super(message);
	}
}
