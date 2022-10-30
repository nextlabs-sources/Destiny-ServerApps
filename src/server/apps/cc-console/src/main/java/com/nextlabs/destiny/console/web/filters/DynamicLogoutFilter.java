package com.nextlabs.destiny.console.web.filters;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nextlabs.destiny.console.enums.LogMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.nextlabs.destiny.console.config.root.PrincipalUser;
import com.nextlabs.destiny.console.enums.AuditAction;
import com.nextlabs.destiny.console.enums.AuditableEntity;
import com.nextlabs.destiny.console.services.EntityAuditLogService;
import com.nextlabs.destiny.console.utils.JsonUtil;
import com.nextlabs.destiny.console.utils.SecurityContextUtil;

public class DynamicLogoutFilter 
        extends LogoutFilter {
    
    private static final Logger log = LoggerFactory.getLogger(DynamicLogoutFilter.class);
    
    private final String casServerLogoutUrl;
    
    private final EntityAuditLogService entityAuditLogService;
    
	public DynamicLogoutFilter(String logoutSuccessUrl, LogoutHandler[] handlers,
					EntityAuditLogService entityAuditLogService) {
		super(logoutSuccessUrl, handlers);
		this.casServerLogoutUrl = logoutSuccessUrl;
		this.entityAuditLogService = entityAuditLogService;
		setLogoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"));
	}
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;
        
        if (httpReq.getRequestURI().contains("/logout")) {
            HttpSession session = httpReq.getSession();
            if (session != null) {
                PrincipalUser loggedInUser = SecurityContextUtil.getCurrentUser();
				session.removeAttribute("user");
				if (loggedInUser != null) {
					try {
						Map<String, Object> audit = new LinkedHashMap<>();

						log.info(LogMarker.AUTHENTICATION, "User logged out. [username={}]", loggedInUser.getUsername());
						audit.put("Message", loggedInUser.getUsername() + " has logged out successfully.");
						entityAuditLogService.addEntityAuditLog(AuditAction.LOGOUT,
								AuditableEntity.APPLICATION_USER.getCode(), loggedInUser.getUserId(), null,
								JsonUtil.toJsonString(audit));
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
                
                session.invalidate();
            }

            httpRes.sendRedirect(casServerLogoutUrl);
        } else {
            chain.doFilter(request, response);
        }
    }
}
