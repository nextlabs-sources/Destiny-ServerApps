package com.bluejungle.destiny.mgmtconsole.web.security;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * This filter is adding CSRF headers to the response.
 *
 * @author Sachindra Dasun
 */
public class CsrfTokenBindingFilter implements Filter {

    public final static String CSRF_TOKEN_ATTR = "csrfToken";
    public final static String CSRF_TOKEN_HEADER = "X-CSRF-TOKEN";

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        httpServletResponse.setHeader("X-CSRF-HEADER", CSRF_TOKEN_HEADER);
        HttpSession httpSession = httpServletRequest.getSession(false);
        if (httpSession != null) {
            Object csrfToken = httpSession.getAttribute(CSRF_TOKEN_ATTR);
            if (csrfToken != null) {
                httpServletResponse.setHeader(CSRF_TOKEN_HEADER, csrfToken.toString());
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
