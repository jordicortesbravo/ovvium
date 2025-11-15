package com.ovvium.services.security;

import com.ovvium.services.model.user.apikey.ApiKeyValue;
import com.ovvium.services.repository.ApiKeyRepository;
import com.ovvium.services.security.dto.ApiKeyDto;
import com.ovvium.services.security.exception.InvalidApiKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpMethod.OPTIONS;

@Slf4j
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

	public static final String API_KEY_HEADER = "X-API-Key";

	private final Map<String, ApiKeyDto> apiKeysByKey;
	private final ApiKeyRepository apiKeyRepository;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		// OPTIONS requests should be excluded of authentication checks by definition
		if (!request.getMethod().equals(OPTIONS.name())) {
			String header = request.getHeader(API_KEY_HEADER);
			Optional.ofNullable(header)
					.map(ApiKeyValue::ofKey)
					.ifPresentOrElse(
							this::checkValidApiKey,
							() -> {
								throw new IllegalArgumentException("Missing Api Key Header.");
							}
					);
		}
		chain.doFilter(request, response);
	}

	private void checkValidApiKey(ApiKeyValue apiKeyValue) {
		lazyloadApiKeys();
		if (!apiKeysByKey.containsKey(apiKeyValue.getKey())) {
			throw new InvalidApiKeyException();
		}
	}

	private void lazyloadApiKeys() {
		if (apiKeysByKey.isEmpty()) {
			this.apiKeyRepository.list()
					.forEach(it -> apiKeysByKey.putIfAbsent(it.getKey().getKey(), new ApiKeyDto(it)));
		}
	}
}