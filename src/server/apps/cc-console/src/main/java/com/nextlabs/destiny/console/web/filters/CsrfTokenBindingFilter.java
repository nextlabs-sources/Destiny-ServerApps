/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 23, 2016
 *
 */
package com.nextlabs.destiny.console.web.filters;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

/**
 * Binds a {@link org.springframework.security.web.csrf.CsrfToken} to the
 * {@link HttpServletResponse} headers if the Spring
 * {@link org.springframework.security.web.csrf.CsrfFilter} has placed one in
 * the {@link HttpServletRequest}.
 *
 * Based on the work found in: <a href=
 * "http://stackoverflow.com/questions/20862299/with-spring-security-3-2-0-release-how-can-i-get-the-csrf-token-in-a-page-that">
 * Stack Overflow</a>
 *
 * @author Amila Silva
 * @since 8.0
 */
public class CsrfTokenBindingFilter extends OncePerRequestFilter {
    protected static final String REQUEST_ATTRIBUTE_NAME = "_csrf";
    protected static final String RESPONSE_HEADER_NAME = "X-CSRF-HEADER";
    protected static final String RESPONSE_PARAM_NAME = "X-CSRF-PARAM";
    protected static final String RESPONSE_TOKEN_NAME = "X-CSRF-TOKEN";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, javax.servlet.FilterChain filterChain)
                    throws ServletException, IOException {
        CsrfToken csrf = (CsrfToken) request
                .getAttribute(REQUEST_ATTRIBUTE_NAME);

        if (csrf == null) {
            Cookie cookie = WebUtils.getCookie(request, "CSRF-TOKEN");
            if (cookie != null) {
                String token = cookie.getValue();
                response.setHeader(RESPONSE_TOKEN_NAME, token);

            }
        } else {
            response.setHeader(RESPONSE_HEADER_NAME, csrf.getHeaderName());
            response.setHeader(RESPONSE_PARAM_NAME, csrf.getParameterName());
            response.setHeader(RESPONSE_TOKEN_NAME, csrf.getToken());
        }
        filterChain.doFilter(request, response);
    }
}
