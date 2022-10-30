/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 28, 2016
 *
 */
package com.nextlabs.destiny.console.web.filters;

import com.nextlabs.destiny.console.enums.LogMarker;
import com.nextlabs.destiny.console.services.impl.HttpResponseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * API request authentication filter will validate the api requests has
 * authenticated token in request header or as a cookie
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class ApiRequestAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory
            .getLogger(ApiRequestAuthenticationFilter.class);
    @Autowired
    private HttpResponseServiceImpl httpResponseService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();

        if ((request.getRequestURI().contains("/api/") || request.getRequestURI().contains("/scim/"))
                && auth == null) {
            httpResponseService.respondUnAuthenticated(request, response);
            log.warn(LogMarker.SECURITY, "Not authenticated access. [resource=[method={}, url={}], remoteHost={}]",
                    request.getMethod(), request.getRequestURI(), request.getRemoteHost());
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
