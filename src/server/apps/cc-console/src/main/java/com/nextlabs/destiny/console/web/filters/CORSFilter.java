/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 28, 2016
 *
 */
package com.nextlabs.destiny.console.web.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nextlabs.destiny.console.config.properties.CorsProperties;

/**
 *
 * Enabling CORS support - Access-Control-Allow-Origin
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class CORSFilter extends OncePerRequestFilter {

	protected static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	protected static final String ORIGIN = "Origin";
	
	@Autowired
	private CorsProperties corsProperties;
	
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
    	
    	if(!StringUtils.isEmpty(corsProperties.getAllowedOrigins())) {
    		Set<String> allowOriginSet= new HashSet<>(Arrays.asList(corsProperties.getAllowedOrigins().split(",")));
    		if(allowOriginSet.contains(request.getHeader(ORIGIN))) {
    			response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, request.getHeader(ORIGIN));
    		}
    	}
        response.setHeader("Access-Control-Allow-Credentials", "true");

        if (request.getHeader("Access-Control-Request-Method") != null
                && "OPTIONS".equals(request.getMethod())) {

            // CORS "pre-flight" request
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type");
            response.addHeader("Access-Control-Max-Age", "1");
        }

        filterChain.doFilter(request, response);
    }

}
