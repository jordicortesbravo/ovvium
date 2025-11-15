package com.ovvium.services.security.exception;

import org.springframework.security.core.AuthenticationException;

public final class InvalidApiKeyException extends AuthenticationException {

	public InvalidApiKeyException() {
		super("Invalid Api Key");
	}
}
