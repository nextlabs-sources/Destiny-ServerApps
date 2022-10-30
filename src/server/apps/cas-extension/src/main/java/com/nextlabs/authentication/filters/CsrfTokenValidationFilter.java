package com.nextlabs.authentication.filters;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nextlabs.authentication.config.properties.CorsProperties;

/**
 * This filter performs CSRF token validation.
 *
 * @author Sachindra Dasun
 */
@Component
public class CsrfTokenValidationFilter extends OncePerRequestFilter {
    
    protected static final Map<String, Set<String>> excludeCsrfTokenUri = new HashMap<>();
    protected static final Set<String> excludeOrigins = new HashSet<>();

    @Autowired
    private CorsProperties corsProperties;

    @PostConstruct
    public void init() {
        // POST methods to be excluded from CSRF token check
        Set<String> excludedPostUris = new HashSet<>();
        excludedPostUris.add("/cas/token");
        excludedPostUris.add("/cas/v1/tickets");
        excludedPostUris.add("/cas/account/modifyPassword");

        //Cas accessToken endpoints
        excludedPostUris.add("/cas/oidc/accessToken");
        excludedPostUris.add("/cas/auth/accessToken");
        excludeCsrfTokenUri.put("POST", excludedPostUris);

        // GET methods to be excluded from CSRF token check
        Set<String> excludedGetUris = new HashSet<>();
        excludedGetUris.add("*");
        excludeCsrfTokenUri.put("GET", excludedGetUris);

        if(StringUtils.isNotEmpty(corsProperties.getAllowedOrigins())) {
            excludeOrigins.addAll(Arrays.asList(corsProperties.getAllowedOrigins().split("\\s*,\\s*")));
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (checkCsrfToken(request)) {
            HttpSession httpSession = request.getSession(false);
            if (httpSession != null) {
                Object csrfToken = httpSession.getAttribute(CsrfTokenGeneratingFilter.CSRF_TOKEN_ATTR);
                if (csrfToken != null) {
                    String csrfTokenRequest = request.getHeader(CsrfTokenGeneratingFilter.CSRF_TOKEN_HEADER);
                    if (csrfTokenRequest == null) {
                        csrfTokenRequest = request.getParameter(CsrfTokenGeneratingFilter.CSRF_TOKEN_ATTR);
                    }
                    if (!csrfToken.equals(csrfTokenRequest)) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                        return;
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Determine if request need to be check for CSRF token
     * @param request Service request
     * @return true if CSRF token need to be check
     */
    private boolean checkCsrfToken(HttpServletRequest request) {
        Set<String> excludedUris = excludeCsrfTokenUri.get(request.getMethod());
        boolean check = excludedUris == null
                        || !(excludedUris.contains("*")
                        || excludedUris.stream().anyMatch(url -> request.getRequestURI().startsWith(url)));

        return check && !excludeOrigins.contains(request.getHeader("origin"));
    }
}
