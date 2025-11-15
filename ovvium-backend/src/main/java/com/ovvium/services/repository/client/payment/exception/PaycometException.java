package com.ovvium.services.repository.client.payment.exception;

import com.ovvium.services.model.payment.UnsuccessfulPaymentClientException;

public class PaycometException extends UnsuccessfulPaymentClientException {

	private static final long serialVersionUID = 1L;

	public PaycometException(int errorId, String message, Object response) {
		this(String.format("Error ID %d: %s", errorId, message), response);
	}

	public PaycometException(String message, Object response) {
		super(String.format("%s with response %s", message, response));
	}
}
