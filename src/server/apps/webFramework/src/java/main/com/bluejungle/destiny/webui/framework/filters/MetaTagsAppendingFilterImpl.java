package com.bluejungle.destiny.webui.framework.filters;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * Meta tag filter to append IE compatible meta tags to header.
 * 
 * @author Amila Silva
 * @since 8.0
 */
public class MetaTagsAppendingFilterImpl implements Filter {

	/**
	 * Constants related to no caching
	 */
	private static final String EXPIRES = "Expires";
	private static final String CACHE_CONTROL = "Cache-Control";
	private static final String BLOCK_CACHING = "no-cache, no-store, must-revalidate";
	private static final String ALLOW_CACHING = "private, no-cache";
	private static final String HTTP_1_1 = "HTTP/1.1";
	private static final String NO_CACHE = "no-cache";
	private static final String PRAGMA = "Pragma";
	private static final String HTTP_1_0 = "HTTP/1.0";

	private List<String> cacheAllowedTypes;

	/**
	 * Filter destruction
	 */
	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		if (response instanceof HttpServletResponse) {
			setIECompatibilityModeMetaTags(httpReq, (HttpServletResponse) response);
			setCacheControl(httpReq, (HttpServletResponse) response);
		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		cacheAllowedTypes = new ArrayList<>();
		cacheAllowedTypes.add("js");
		cacheAllowedTypes.add("css");
		cacheAllowedTypes.add("gif");
		cacheAllowedTypes.add("jpg");
		cacheAllowedTypes.add("woff");
		cacheAllowedTypes.add("eot");
		cacheAllowedTypes.add("png");
	}

	/**
	 * Set header to force IE to not mess up document mode
	 * 
	 * @param httpReq
	 * @param httpResp
	 */
	private void setIECompatibilityModeMetaTags(HttpServletRequest httpReq, HttpServletResponse httpResp) {
		httpResp.setHeader("X-UA-Compatible", "IE=edge,chrome=1");
	}

	/**
	 * Sets the HTTP response not cacheable
	 * 
	 * @param httpReq
	 *            http request
	 * @param httpResp
	 *            http response
	 */
	private void setCacheControl(HttpServletRequest httpReq, HttpServletResponse httpResp) {

		// disable caching for HTTP/1.0
		if (httpReq.getProtocol().compareTo(HTTP_1_0) == 0) {
			httpResp.setHeader(PRAGMA, NO_CACHE);
			return;
		}

		String resourseType = getExtension(httpReq.getRequestURI());
		if (httpReq.getProtocol().compareTo(HTTP_1_1) == 0) {
			if (resourseType != null && cacheAllowedTypes.contains(resourseType)) {
				httpResp.setHeader(CACHE_CONTROL, ALLOW_CACHING);
			} else {
				// disable caching if request endpoint is not in CACHE_ALLOWED_TYPES
				httpResp.setHeader(CACHE_CONTROL, BLOCK_CACHING);
				httpResp.setDateHeader(EXPIRES, 0);
			}
		}
	}

	private String getExtension (String uri){
		if (StringUtils.isBlank(uri)){
			return null;
		}
		int extIndex = uri.lastIndexOf('.');
		if (extIndex < 0){
			return null;
		}

		// return extension excluding dot, if last character is not dot
		extIndex = extIndex + 1;
		return extIndex > uri.length()? "" : uri.substring(extIndex);
	}

}
