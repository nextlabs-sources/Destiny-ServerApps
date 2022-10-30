package com.nextlabs.authentication.filters;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * This filter adds meta tags to responses
 *
 * @author Mohammed Sainal Shah
 */
@Component
public class MetaTagsAppendingFilter extends OncePerRequestFilter {

    private static final String REQUIRE_AUTH_HEADER = "REQUIRE_AUTH";
    private static final String LOGIN_PATH = "/cas/login";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // This header is used in reporter to support HTTP 302 redirection in ajax responses
        // See https://stackoverflow.com/questions/199099/how-to-manage-a-redirect-request-after-a-jquery-ajax-call
        if (LOGIN_PATH.equals(request.getRequestURI())){
            response.addHeader(REQUIRE_AUTH_HEADER, String.valueOf(true));
        }
        filterChain.doFilter(request, response);
    }

}
