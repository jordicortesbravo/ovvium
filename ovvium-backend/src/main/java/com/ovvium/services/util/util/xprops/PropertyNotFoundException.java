package com.ovvium.services.util.util.xprops;

public class PropertyNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PropertyNotFoundException(String key) {
		super(String.format("Required property %s not found", key));
	}
}
