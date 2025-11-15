package com.ovvium.services.web.filter;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.ovvium.services.security.ApiKeyAuthenticationFilter.API_KEY_HEADER;
import static com.ovvium.services.util.ovvium.string.OvviumStringUtils.randomUniqueString;

/**
 * Adds custom trace ids to log traces for every http request.
 */
@Component
@Order(1)
@WebFilter("/*")
public class MDCLoggerFilter extends OncePerRequestFilter {

	private static final String REQUEST_ID = "REQUEST_ID";
	private static final String API_KEY_ID = "API_KEY_ID";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
		try {
			MDC.put(REQUEST_ID, randomUniqueString());

			Optional.ofNullable(request.getHeader(API_KEY_HEADER))
					.map(it -> it.substring(0, 6))
					.ifPresent(it -> MDC.put(API_KEY_ID, it));

			filterChain.doFilter(request, httpServletResponse);
		} finally {
			MDC.clear();
		}
	}
}
