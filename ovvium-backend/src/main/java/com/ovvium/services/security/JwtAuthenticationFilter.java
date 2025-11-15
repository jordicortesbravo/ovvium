package com.ovvium.services.security;

import lombok.val;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.ovvium.services.security.JwtUtil.getAuthorizationToken;
import static com.ovvium.services.security.JwtUtil.parseToken;


public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private final String jwtSecret;

	public JwtAuthenticationFilter(String jwtSecret) {
		super("/**");
		this.jwtSecret = jwtSecret;
	}

	@Override
	protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
		return true;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		val token = getAuthorizationToken(request);
		return getAuthenticationManager().authenticate(new JwtAuthenticationToken(parseToken(token, jwtSecret), token));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		super.successfulAuthentication(request, response, chain, authResult);

		// Since the authentication is in a HTTP header, after success we need to continue the request
		chain.doFilter(request, response);
	}
}