/*
 * Created on Jan 23, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.loginmgr;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.webui.framework.context.AppContext;

/**
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/container/dac/framework/com/bluejungle/destiny/server/dac/framework/LoginFilterImpl.java#1 $
 */

public class LoginFilterImpl implements Filter {

    /**
     * Constants related to no caching
     */
    private static final String EXPIRES = "Expires";
    private static final String CACHE_CONTROL = "Cache-Control";
	private static final String CACHE_CONTROL_HEADER_VALUE = "no-cache, no-store, must-revalidate";
    private static final String HTTP_1_1 = "HTTP/1.1";
    private static final String NO_CACHE = "no-cache";
    private static final String PRAGMA = "Pragma";
    private static final String HTTP_1_0 = "HTTP/1.0";

    /**
     * Logging
     */
    private static final Log LOG = LogFactory.getLog(LoginFilterImpl.class.getName());

    /**
     * Redirection constants
     */
    private static final String REDIRECT_PATH_PARAM_NAME = "RedirectPath";
    private static final String DEFAULT_REDIRECT_PATH = "/login/login.jsf";
    private static final String PASS_THROUGH_PATHS_PARAM_NAME = "PassThroughPaths";
    private static final String PASS_THROUGH_PATHS_DELIMITER = ",";

    private String redirectPath;
    private Set passThroughPathPatterns;

    /**
     * Add a pass through pattern from the specified url patterns
     * 
     * @param urlPattern
     *            the url pattern from which to create a pass through pattern
     * @throws PatternSyntaxException
     *             if the urlPattern is of an invalid format (java regexp)
     */
    private void addPassThroughURL(String urlPattern) throws PatternSyntaxException {
        Pattern nextPathPattern = Pattern.compile(urlPattern);
        this.passThroughPathPatterns.add(nextPathPattern);
        if (getLog().isDebugEnabled()) {
            getLog().debug("Pass through path, " + urlPattern + "added.");
        }
    }

    /**
     * Filter destruction
     */
    public void destroy() {
    }

    /**
     * This is the main filtering function. If the page does not belong to the
     * list of protected items (and if the user is not currrenly logged in),
     * then the login page should be displayed.
     * 
     * @param chain
     *            filter chain
     * @param req
     *            HTTP request object
     * @param res
     *            HTTP response object
     * @throws IOException
     *             if filter fails
     * @throws ServletException
     *             if filter fails
     */
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) req;

        AppContext ctx = AppContext.getContext(httpReq);

        String uri = getResourcePath(httpReq);
        if (!isPassThroughURL(uri) && !ctx.isLoggedIn()) {
            if (resp instanceof HttpServletResponse) {
                setIECompatibilityModeMetaTags(httpReq,(HttpServletResponse) resp);
                String contextPath = ((HttpServletRequest) req).getContextPath();
                ((HttpServletResponse) resp).sendRedirect(contextPath + this.redirectPath);
            } else {
                RequestDispatcher rd = req.getRequestDispatcher(this.redirectPath);
                rd.forward(req, resp);
            }
        } else {
            if (resp instanceof HttpServletResponse) {
                setIECompatibilityModeMetaTags(httpReq,(HttpServletResponse) resp);
                setNotCacheable(httpReq, (HttpServletResponse) resp);
            }
            chain.doFilter(req, resp);
        }
    }

    /**
     * Returns the log object
     * 
     * @return the log object
     */
    private Log getLog() {
        return LOG;
    }

    /**
     * Returns the relative path of the resource within the application context
     * 
     * @param req
     *            HTTP request object
     * @return the relative path of the resource within the application context
     */
    private String getResourcePath(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String path = req.getContextPath();

        //Extracts the resource path
        String resourcePath = uri.substring(path.length());
        return (resourcePath);
    }

    /**
     * Filter initialization
     * 
     * @param config
     *            filter configuration
     * @throws ServletException
     *             if init fails
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        this.redirectPath = filterConfig.getInitParameter(REDIRECT_PATH_PARAM_NAME);
        if (this.redirectPath != null) {
            this.redirectPath = this.redirectPath.trim();
            if (!this.redirectPath.startsWith("/")) {
                this.redirectPath = "/" + this.redirectPath;
            }
        } else {
            this.redirectPath = DEFAULT_REDIRECT_PATH;
        }

        initializePassThroughPathPatterns(filterConfig);
    }

    /**
     * Initialize the pass through patterns from the appropriate initialization
     * parameter
     * 
     * @param filterConfig
     *            the ServletContext of the current web applications
     */
    private void initializePassThroughPathPatterns(FilterConfig filterConfig) {
        this.passThroughPathPatterns = new HashSet();

        String passThroughPathsInitParam = filterConfig.getInitParameter(PASS_THROUGH_PATHS_PARAM_NAME);
        if (passThroughPathsInitParam != null) {
            //Now, add the excluded paths to the set within compiled patterns
            StringTokenizer tokenizer = new StringTokenizer(passThroughPathsInitParam, PASS_THROUGH_PATHS_DELIMITER);
            while (tokenizer.hasMoreTokens()) {
                String nextPath = tokenizer.nextToken().trim();
                try {
                    addPassThroughURL(nextPath);
                } catch (PatternSyntaxException exception) {
                    getLog().warn("Invalid pass through path, " + nextPath + ".  Invalid format.", exception);
                }
            }
        }
    }

    /**
     * Returns whether a resource is protected by the filter
     * 
     * @param uri
     *            URI of the resource
     * @return true of the URL is protected by the filter, false otherwise
     */
    private boolean isPassThroughURL(String uri) {
        boolean result = false;
        Iterator passThroughPathPatternIterator = this.passThroughPathPatterns.iterator();
        while ((passThroughPathPatternIterator.hasNext()) && (!result)) {
            Pattern nextPattern = (Pattern) passThroughPathPatternIterator.next();
            Matcher nextMatcher = nextPattern.matcher(uri);
            result |= nextMatcher.matches();
        }

        return result;
    }
    
    /**
     * Set header to force IE to not mess up document mode
     * @param httpReq
     * @param httpResp
     */
    private void setIECompatibilityModeMetaTags(HttpServletRequest httpReq, HttpServletResponse httpResp)
    {
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
    private void setNotCacheable(HttpServletRequest httpReq, HttpServletResponse httpResp) {
        if (httpReq.getProtocol().compareTo(HTTP_1_0) == 0) {
            httpResp.setHeader(PRAGMA, NO_CACHE);
        } else if (httpReq.getProtocol().compareTo(HTTP_1_1) == 0) {
            httpResp.setHeader(CACHE_CONTROL, CACHE_CONTROL_HEADER_VALUE);
        }
        httpResp.setDateHeader(EXPIRES, 0);
    }
}