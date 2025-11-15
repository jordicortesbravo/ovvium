package com.ovvium.services.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	private AuthenticatedUser user;
	private String token;

	public JwtAuthenticationToken(AuthenticatedUser user, String token) {
		super(user.getAuthorities());
		this.user = user;
		this.token = token;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return "N/A";
	}

	@Override
	public AuthenticatedUser getPrincipal() {
		return user;
	}

	public String getToken() {
		return token;
	}
}