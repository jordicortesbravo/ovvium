package com.ovvium.services.model.exception;

public class ResourceNotFoundException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String s) {
		super(s);
	}
}
