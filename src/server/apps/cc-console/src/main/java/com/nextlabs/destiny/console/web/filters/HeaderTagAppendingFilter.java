/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 22, 2016
 *
 */
package com.nextlabs.destiny.console.web.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * Web filter to append No cache tags and other meta tags to response header
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class HeaderTagAppendingFilter extends OncePerRequestFilter {

    private List<String> cacheAllowedTypes;
    /**
     * Constants related to no caching
     */
    private static final String EXPIRES = "Expires";
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String CACHE_CONTROL_ALLOW = "public, max-age=600";
    private static final String CACHE_CONTROL_RESTRICTED = "no-cache, no-store, must-revalidate";
    private static final String PRAGMA = "Pragma";
    private static final String HTTP_1_1 = "HTTP/1.1";
    private static final String HTTP_1_0 = "HTTP/1.0";
    private static final String NO_CACHE = "no-cache";
    private static final String ETAG = "ETag";

    @PostConstruct
    public void init() {
        cacheAllowedTypes = new ArrayList<>();
        cacheAllowedTypes.add("svg");
        cacheAllowedTypes.add("woff");
        cacheAllowedTypes.add("woff2");
        cacheAllowedTypes.add("ttf");
        cacheAllowedTypes.add("eot");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        response.addHeader("X-UA-Compatible", "IE=edge,chrome=1");
        String resourseType = getExtension(request.getRequestURI());
        if (resourseType != null && cacheAllowedTypes.contains(resourseType)) {
            response.addHeader(CACHE_CONTROL, CACHE_CONTROL_ALLOW);
            response.addHeader(PRAGMA, CACHE_CONTROL_ALLOW);
            response.addHeader(ETAG, String.valueOf(System.currentTimeMillis()));
        } else {
            if (request.getProtocol().compareTo(HTTP_1_0) == 0) {
                response.addHeader(PRAGMA, NO_CACHE);
            } else if (request.getProtocol().compareTo(HTTP_1_1) == 0) {
                response.addHeader(CACHE_CONTROL, CACHE_CONTROL_RESTRICTED);
            }
            response.addDateHeader(EXPIRES, 0);
        }

        filterChain.doFilter(request, response);
    }

    private String getExtension (String uri){
        if (StringUtils.isBlank(uri)){
            return null;
        }
        int extIndex = uri.lastIndexOf('.');
        if (extIndex < 0){
            return null;
        }

        // return extension excluding dot, if last character is not dot
        extIndex = extIndex + 1;
        return extIndex > uri.length()? "" : uri.substring(extIndex);
    }

}
