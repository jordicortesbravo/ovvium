package com.ovvium.services.util.ovvium.spring;

import com.ovvium.services.util.ovvium.optional.OptionalUtils;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Spring Request Utility class. Spring RequestContextListener bean needs to be registered.
 */
@UtilityClass
public class SpringRequestUtils {

	private static final UrlPathHelper URL_HELPER = new UrlPathHelper();
	private static final String FORWARDED_FOR_HEADER = "X-FORWARDED-FOR";

	/**
	 * Returns the original IP from the request if exists or empty otherwise.
	 */
	public static Optional<String> getOriginalIpAddress() {
		return getCurrentRequest()
				.map(rq ->
						OptionalUtils.ofBlankable(rq.getHeader(FORWARDED_FOR_HEADER))
								.orElse(rq.getRemoteAddr())
				);
	}

	/**
	 * Returns the current request.
	 */
	public static Optional<HttpServletRequest> getCurrentRequest() {
		val attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return Optional.ofNullable(attrs)
				.map(ServletRequestAttributes::getRequest);
	}

	public static Optional<String> getRequestPath() {
		return getCurrentRequest().map(URL_HELPER::getPathWithinApplication);
	}
}
