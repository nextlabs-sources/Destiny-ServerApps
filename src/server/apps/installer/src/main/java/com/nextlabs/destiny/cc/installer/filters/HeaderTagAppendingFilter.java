/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 22, 2016
 *
 */
package com.nextlabs.destiny.cc.installer.filters;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * Web filter to append No cache tags and other meta tags to response header
 *
 * @author Mohammed Sainal Shah
 * @since 9.5
 *
 */
@Component
@Order(1)
public class HeaderTagAppendingFilter extends OncePerRequestFilter {

    private List<String> cacheAllowedTypes;
    /**
     * Constants related to no caching
     */
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String CACHE_CONTROL_HEADER_VALUE = "public, max-age=600";
    private static final String PRAGMA = "Pragma";
    private static final String E_TAG = "ETag";

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

        response.addHeader("X-UA-Compatible", "IE=11");

        String resourseType = getExtension(request.getRequestURI());
        if (resourseType != null && cacheAllowedTypes.contains(resourseType)) {
            response.addHeader(CACHE_CONTROL, CACHE_CONTROL_HEADER_VALUE);
            response.addHeader(PRAGMA, CACHE_CONTROL_HEADER_VALUE);
            response.addHeader(E_TAG, String.valueOf(System.currentTimeMillis()));
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
