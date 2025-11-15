package com.ovvium.services.web.handler;

import com.ovvium.services.model.exception.ResourceNotFoundException;
import com.ovvium.services.util.ovvium.exception.DomainException;
import com.ovvium.services.util.ovvium.exception.OvviumApiError;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<OvviumApiError> exceptionHandler(Exception ex, HttpServletRequest request) {
		return createApiError(ex, request, INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({ResourceNotFoundException.class, EntityNotFoundException.class})
	public ResponseEntity<OvviumApiError> notFound(Exception ex, HttpServletRequest request) {
		return createApiError(ex, request, NOT_FOUND);
	}

	@ExceptionHandler({DomainException.class, IllegalArgumentException.class})
	public ResponseEntity<OvviumApiError> badClientRequest(Exception ex, HttpServletRequest request) {
		return createApiError(ex, request, BAD_REQUEST);
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<OvviumApiError> conflict(IllegalStateException ex, HttpServletRequest request) {
		return createApiError(ex, request, CONFLICT);
	}

	@ExceptionHandler({BadCredentialsException.class})
	public ResponseEntity<OvviumApiError> loginError(BadCredentialsException ex,
													 HttpServletRequest request) {
		return createApiError(ex, request, UNAUTHORIZED);
	}

	@ExceptionHandler({AuthenticationException.class, AccessDeniedException.class})
	public ResponseEntity<OvviumApiError> notLoggedUser(Exception ex, HttpServletRequest request) {
		return createApiError(ex, request, FORBIDDEN);
	}

	// Log Spring Mapping errors
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
		val entity = super.handleExceptionInternal(ex, body, headers, status, request);
		logError(ex, ((ServletWebRequest) request).getRequest().getRequestURI(), status);
		return entity;
	}

	private ResponseEntity<OvviumApiError> createApiError(Exception ex, HttpServletRequest request, HttpStatus status) {
		logError(ex, request.getRequestURI(), status);
		return new ResponseEntity<>(OvviumApiError.of(status.value(), ex), status);
	}

	private void logError(Exception ex, String path, HttpStatus status) {
		if (status.is5xxServerError()) {
			log.error(format("Error occurred processing %s", path), ex);
		} else {
			log.warn(format("Error occurred processing %s: %s", path, ExceptionUtils.getRootCauseMessage(ex)));
		}
	}

}
