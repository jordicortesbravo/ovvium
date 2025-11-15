package com.ovvium.services.security.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

public final class InvalidTokenException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

	@Getter
	private Class<?> causedBy;

	public InvalidTokenException(String message) {
		super(message);
	}

	public InvalidTokenException(Class<?> causedBy, String message) {
		this(message);
		this.causedBy = causedBy;
	}
}
