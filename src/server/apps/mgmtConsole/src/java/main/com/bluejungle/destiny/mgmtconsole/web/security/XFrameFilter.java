package com.bluejungle.destiny.mgmtconsole.web.security;

import com.nextlabs.destiny.configclient.Config;
import com.nextlabs.destiny.configclient.ConfigClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
public class XFrameFilter implements Filter {

	private final Logger logger = LoggerFactory.getLogger(XFrameFilter.class);

	protected static final String X_FRAME_OPTIONS_HEADER = "X-Frame-Options";
	protected static final String ALLOW_FROM = "ALLOW-FROM";
	protected static final String DENY = "DENY";

	private static final Config xFrameOptionsConfig =  ConfigClient.get("security.xframe.options", "DENY");
	private static final Config xFrameAllowedOriginsConfig =  ConfigClient.get("security.xframe.allowedOrigins", "");

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		String xFrameOption = xFrameOptionsConfig.toString();
		String xFrameAllowedOrigin = xFrameAllowedOriginsConfig.toString();

		
		if (ALLOW_FROM.equalsIgnoreCase(xFrameOption)) {
			if (!xFrameAllowedOrigin.equals("")) {
				xFrameOption += " " + xFrameAllowedOrigin;
			} else {
				logger.debug("Allowed origin not provided, default to DENY");
				xFrameOption = DENY; // Allowed origin not provided, default to DENY.
			}
		}
		
		res.setHeader(X_FRAME_OPTIONS_HEADER, xFrameOption);
		logger.debug(" X-Frame-Options header set to : {}", res.getHeader(X_FRAME_OPTIONS_HEADER));
		filterChain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	@Override
	public void destroy() {}
}
