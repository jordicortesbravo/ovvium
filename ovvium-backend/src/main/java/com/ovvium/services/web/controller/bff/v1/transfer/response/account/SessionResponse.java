package com.ovvium.services.web.controller.bff.v1.transfer.response.account;

import lombok.Data;

import java.time.Instant;

import static java.time.ZoneOffset.UTC;

@Data
public final class SessionResponse {

	private final String refreshToken;
	private final String accessToken;
	private final String loggedUntil;

	public SessionResponse(String refreshToken, String accessToken, Instant loggedUntil) {
		this.refreshToken = refreshToken;
		this.accessToken = accessToken;
		this.loggedUntil = loggedUntil.atZone(UTC).toString();
	}
}
