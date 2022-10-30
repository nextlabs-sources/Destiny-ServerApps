package com.nextlabs.authentication.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.filter.GenericFilterBean;

import com.nextlabs.authentication.config.properties.XFrameProperties;

/**
 *
 * Setting X-Frame-Options header in the response to prevent the application for
 * being embedded in any website.
 * 
 * DENY : The page cannot be displayed in a frame, regardless of the site
 * attempting to do so.
 * 
 * SAMEORIGIN : The page can only be displayed in a frame on the same origin as
 * the page itself.
 * 
 * ALLOW-FROM uri : The page can only be displayed in a frame on the specified
 * origin.
 *
 * @author Moushumi Seal
 *
 */
@Component
public class XFrameFilter extends GenericFilterBean {
	private static final Logger log = LoggerFactory.getLogger(XFrameFilter.class);

	protected static final String X_FRAME_OPTIONS_HEADER = "X-Frame-Options";
	protected static final String ALLOW_FROM = "ALLOW-FROM";
	protected static final String DENY = "DENY";

	@Autowired
	private XFrameProperties xFrameProperties;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletResponse res = (HttpServletResponse) response;
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());
		
		String xFrameOption = xFrameProperties.getOptions();
		String allowedOrigins = xFrameProperties.getAllowedOrigins();
		
		if (ALLOW_FROM.equalsIgnoreCase(xFrameOption)) {
			if(!StringUtils.isEmpty(allowedOrigins)) {
				xFrameOption += " " + allowedOrigins;
			} else {
				log.debug(" Allowed origin not provided, default to DENY ");
				xFrameOption = DENY; // Allowed origin not provided, default to DENY.
			}
		}
		res.setHeader(X_FRAME_OPTIONS_HEADER, xFrameOption);
		log.debug(" X-Frame-Options header set to : {}", res.getHeader(X_FRAME_OPTIONS_HEADER));
		filterChain.doFilter(request, res);
	}
}
