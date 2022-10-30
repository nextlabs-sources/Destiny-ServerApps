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
import java.util.Enumeration;

/**
 * This filter performs CSRF token validation.
 *
 * @author Sachindra Dasun
 */
public class CsrfTokenValidationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        if (!httpServletRequest.getRequestURI().endsWith(".jsf") || !"GET".equals(httpServletRequest.getMethod())) {
            HttpSession httpSession = httpServletRequest.getSession(false);
            if (httpSession != null) {
                Object csrfToken = httpSession.getAttribute(CsrfTokenBindingFilter.CSRF_TOKEN_ATTR);
                if (csrfToken != null) {
                    String csrfTokenRequest = httpServletRequest.getHeader(CsrfTokenBindingFilter.CSRF_TOKEN_HEADER);
                    if (csrfTokenRequest == null) {
                        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();
                        while (parameterNames.hasMoreElements()) {
                            String parameterName = parameterNames.nextElement();
                            if (parameterName.endsWith(CsrfTokenBindingFilter.CSRF_TOKEN_ATTR)) {
                                csrfTokenRequest = httpServletRequest.getParameter(parameterName);
                                break;
                            }
                        }
                    }
                    if (!csrfToken.equals(csrfTokenRequest)) {
                        ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_FORBIDDEN);
                        return;
                    }
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
