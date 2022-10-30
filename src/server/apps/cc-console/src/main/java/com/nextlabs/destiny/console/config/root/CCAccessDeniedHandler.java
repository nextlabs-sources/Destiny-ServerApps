package com.nextlabs.destiny.console.config.root;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.stereotype.Component;

import com.nextlabs.destiny.console.enums.LogMarker;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.utils.SecurityContextUtil;

/**
 * @author Sachindra Dasun
 */
@Component
public class CCAccessDeniedHandler extends AccessDeniedHandlerImpl {

    private static final Logger log = LoggerFactory.getLogger(CCAccessDeniedHandler.class);

    @Autowired
    private MessageBundleService msgBundle;

    @Value("${app.service.home}")
    private String appServiceHome;

    @Value("${cas.service.login}")
    private String casServiceLogin;

    @Override
    public void handle(HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse,
                       AccessDeniedException e) throws IOException, ServletException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = null;
        if (securityContext != null) {
            authentication = securityContext.getAuthentication();
        }
        // Same validation from ApiRequestAuthenticationFilter
        if (authentication == null) {
            if (httpServletRequest.getRequestURI().contains("/api/")
                    || httpServletRequest.getRequestURI().contains("/scim/")) {
                httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                String responseMsg = String.format("{\"statusCode\":\"%s\",\"message\":\"%s\"}",
                        msgBundle.getText("server.request.not.authenticated.code"),
                        msgBundle.getText("server.request.not.authenticated"));
                httpServletResponse.getWriter().println(responseMsg);
                httpServletResponse.getWriter().flush();
                log.warn(LogMarker.SECURITY, "Not authenticated access. [resource=[method={}, url={}], remoteHost={}]",
                        httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), httpServletRequest.getRemoteHost());
                log.debug("Caused by:", e);
            } else if (httpServletRequest.getRequestURI().endsWith("/logout")) {
                httpServletResponse.sendRedirect(appServiceHome);
            }
        } else {
            log.warn(LogMarker.SECURITY, "Not authorized access. [resource=[method={}, url={}], userInfo={}, remoteHost={}]",
                    httpServletRequest.getMethod(), httpServletRequest.getRequestURL(), SecurityContextUtil.getUserInfo(), httpServletRequest.getRemoteHost());
            log.debug("Caused by:", e);
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            // User get access denied to non-api call, this must be an API user account
            if (!(httpServletRequest.getRequestURI().contains("/api/")
                    || httpServletRequest.getRequestURI().contains("/scim/"))) {
                httpServletRequest.getSession().invalidate();
                httpServletResponse.sendRedirect(casServiceLogin);
                return;
            }

            super.handle(httpServletRequest, httpServletResponse, e);
        }
    }
}
