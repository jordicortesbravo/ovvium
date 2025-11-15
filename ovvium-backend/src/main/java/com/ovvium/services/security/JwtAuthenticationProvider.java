package com.ovvium.services.security;


import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import static com.ovvium.services.security.JwtUtil.parseToken;

@RequiredArgsConstructor
public class JwtAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	private final String jwtSecret;

	@Override
	public boolean supports(Class<?> authentication) {
		return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String token = ((JwtAuthenticationToken) authentication).getToken();
		return new JwtAuthenticationToken(parseToken(token, jwtSecret), token);
	}

	@Override
	protected UserDetails retrieveUser(String s, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
		// retrieve user should never be called because authenticate is overriden
		throw new NotImplementedException("Not implemented");
	}

}