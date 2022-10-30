package com.bluejungle.destiny.webui.framework.filters;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.webui.framework.context.AppContext;

/**
 * 
 * Logout filter
 * 
 * @author Amila Silva
 * @since 8.0
 */
public class LogoutFilter implements Filter {

	private static Log log = LogFactory.getLog(LogoutFilter.class);

	private String loginUrl;
	private String casServerLogoutUrl;
	private String casServerLoginUrl;
	private static final String LOGIN_URL = "loginUrl";
	private static final String CAS_LOGIN_URL = "casServerLoginUrl";
	private static final String CAS_LOGOUT_URL = "casServerLogoutUrl";

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;
		if (httpReq.getRequestURI().contains("/logout") || httpReq.getRequestURI().contains("/login.jsf")) {
			log.debug("Logout request came " + httpReq.getRequestURI());
			AppContext appContext = AppContext.getContext(httpReq);
			if (appContext != null) {
				appContext.releaseContext(httpReq);
			}

			httpRes.sendRedirect(constructLogoutURL());
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.loginUrl = getInitParameter(config, LOGIN_URL);
		this.casServerLoginUrl = getInitParameter(config, CAS_LOGIN_URL);
		this.casServerLogoutUrl = getInitParameter(config, CAS_LOGOUT_URL);
		if (!casServerLoginUrl.endsWith("?service=")) {
			this.casServerLoginUrl = casServerLoginUrl + "?service=";
		}
	}

	private String constructLogoutURL() {
        try {
            return this.casServerLogoutUrl.concat(URLEncoder.encode(this.casServerLoginUrl.concat(this.loginUrl), "UTF-8"));
        } catch(UnsupportedEncodingException err) {
            log.error(err.getMessage(), err);
        }

        return this.casServerLogoutUrl + this.loginUrl;
    }

    private String getInitParameter(FilterConfig config, String parameterName) {
		String value = config.getInitParameter(parameterName);
		if(value == null) {
			value = config.getServletContext().getInitParameter(parameterName);
		}
		return value;
	}
}
