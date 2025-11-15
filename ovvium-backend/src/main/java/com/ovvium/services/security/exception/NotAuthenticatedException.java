package com.ovvium.services.security.exception;

import org.springframework.security.core.AuthenticationException;

public final class NotAuthenticatedException extends AuthenticationException {

	public NotAuthenticatedException() {
		super("User is not authenticated.");
	}
}
