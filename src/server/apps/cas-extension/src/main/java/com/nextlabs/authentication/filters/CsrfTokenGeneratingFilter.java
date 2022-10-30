package com.nextlabs.authentication.filters;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nextlabs.authentication.services.CsrfProtectionService;

/**
 * This filter is generating the CSRF token.
 *
 * @author Sachindra Dasun
 */
@Component
public class CsrfTokenGeneratingFilter extends OncePerRequestFilter {

    public static final String CSRF_TOKEN_ATTR = "csrfToken";
    public static final String CSRF_TOKEN_HEADER = "X-CSRF-TOKEN";

    @Autowired
    @Qualifier("csrfProtectionService")
    private CsrfProtectionService csrfProtectionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        csrfProtectionService.getCsrfToken();
        filterChain.doFilter(request, response);
    }

}
