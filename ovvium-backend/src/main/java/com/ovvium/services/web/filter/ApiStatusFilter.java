package com.ovvium.services.web.filter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
@WebFilter("/public/api/*")
public class ApiStatusFilter extends OncePerRequestFilter {


    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        if (isDisabled(request.getRequestURI())) {
            response.setStatus(HttpStatus.GONE.value());
            response.setHeader("x-api-status", "DISABLED");
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean isDisabled(String url) {
        return false;
    }
}