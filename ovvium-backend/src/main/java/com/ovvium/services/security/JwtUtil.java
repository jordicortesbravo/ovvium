package com.ovvium.services.security;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.ovvium.services.security.exception.ExpiredTokenException;
import com.ovvium.services.security.exception.InvalidTokenException;
import com.ovvium.services.security.exception.NotAuthenticatedException;
import com.ovvium.services.util.ovvium.base.Preconditions;
import com.ovvium.services.util.security.AuthWrapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static com.ovvium.services.security.exception.AccountError.*;
import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static org.apache.commons.lang.StringUtils.join;

@UtilityClass
public class JwtUtil {

	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String EMAIL = "email";
	private static final String ROLES = "roles";
	private static final String EMPLOYEE_USER = "employeeUser";
	private static final String EXPIRATION = "expiration";
	private static final String TOKEN_TYPE = "tokenType";

	private static final Gson gson = new Gson();
	private static final SignatureAlgorithm SIGNATURE_ALGORITHM = HS512;

	private enum TokenType {
		REFRESH, ACCESS
	}

	public static AuthenticatedUser parseToken(String token, String secret) {
		try {
			Claims body = Jwts.parser()
					.setSigningKey(parseBase64Binary(secret))
					.parseClaimsJws(token)
					.getBody();

			if (body.get(EXPIRATION) == null) {
				throw new InvalidTokenException(INVALID_TOKEN.name());
			}
			val expiration = Instant.parse((String) body.get(EXPIRATION));
			if (expiration.isBefore(Instant.now())) {
				throw new ExpiredTokenException(EXPIRED_TOKEN.name());
			}
			return new AuthenticatedUser(
					UUID.fromString(get(body, ID)),
					get(body, NAME),
					get(body, EMAIL),
					Sets.newHashSet((get(body, ROLES)).split(";")),
					Optional.ofNullable(body.get(EMPLOYEE_USER))
							.map(Object::toString)
							.map(it -> gson.fromJson(it, AuthenticatedUser.EmployeeUser.class))
							.orElse(null)
			);
		} catch (JwtException | ClassCastException e) {
			throw new InvalidTokenException(e.getClass(), INVALID_TOKEN.name());
		}
	}

	public static String generateRefreshToken(AuthenticatedUser user, String secret, Instant expiration) {
		return generateToken(user, secret, expiration, TokenType.REFRESH);
	}

	public static String generateAccessToken(AuthenticatedUser user, String secret, Instant expiration) {
		return generateToken(user, secret, expiration, TokenType.ACCESS);
	}

	public static String getAuthorizationToken(HttpServletRequest request) {
		String requestHeader = request.getHeader("Authorization");
		try {
			Preconditions.checkNotBlank(requestHeader, "Token header can't be null");
			if (!requestHeader.startsWith("Bearer ")) {
				throw new PreAuthenticatedCredentialsNotFoundException(INVALID_TOKEN.name());
			}
			return requestHeader.substring(7);
		} catch (Exception e) {
			throw new InvalidTokenException(TOKEN_NOT_PRESENT.name());
		}
	}

	public static AuthenticatedUser getAuthenticatedUserOrFail() {
		return new AuthWrapper().getPrincipal(AuthenticatedUser.class)
				.orElseThrow(NotAuthenticatedException::new);
	}

	private static String generateToken(AuthenticatedUser user, String secret, Instant expiration, TokenType tokenType) {
		Claims claims = Jwts.claims();
		claims.put(ID, user.getId());
		claims.put(EMAIL, user.getEmail());
		claims.put(NAME, user.getName());
		claims.put(ROLES, join(user.getRoles(), ";"));
		claims.put(EXPIRATION, expiration.toString());
		claims.put(TOKEN_TYPE, tokenType.name());
		user.getEmployeeUser().ifPresent(it -> claims.put(EMPLOYEE_USER, gson.toJson(it)));

		Key signingKey = new SecretKeySpec(DatatypeConverter.parseBase64Binary(secret), SIGNATURE_ALGORITHM.getJcaName());

		return Jwts.builder()
				.setId(user.getId().toString())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setClaims(claims)
				.signWith(SIGNATURE_ALGORITHM, signingKey)
				.compact();
	}

	private static String get(Claims body, String key) {
		return (String) body.get(key);
	}
}