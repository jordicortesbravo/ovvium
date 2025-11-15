package com.ovvium.services.web.filter;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@Component
@Profile("local")
@WebFilter("/*")
public class StatsFilter implements Filter {

	private static final Gson gson = new Gson();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// empty
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		long time = System.currentTimeMillis();
		try {
			chain.doFilter(req, resp);
		} finally {
			time = System.currentTimeMillis() - time;
			log.info("{} {} {}: {} ms ", ((HttpServletRequest) req).getMethod(), ((HttpServletRequest) req).getRequestURI(), gson.toJson(req.getParameterMap()), time);
		}
	}

	@Override
	public void destroy() {
		// empty
	}
}