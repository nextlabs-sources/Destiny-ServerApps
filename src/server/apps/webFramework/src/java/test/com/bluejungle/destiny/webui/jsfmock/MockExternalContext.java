/*
 * Created on Mar 16, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.jsfmock;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;

/**
 * This is a dummy external context object
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/jsfmock/MockExternalContext.java#1 $
 */

public class MockExternalContext extends ExternalContext {

    private String requestContextPath;
    private Map applicationMap = new HashMap();
    private Map requestMap = new HashMap();
    private Map requestParamMap = new HashMap();
    private Map sessionMap = new HashMap();
    private Object request;

    /**
     * Constructor
     * 
     * @param contextPath
     *            path to the application context
     */
    public MockExternalContext(String contextPath) {
        super();
        this.requestContextPath = contextPath;
    }

    /**
     * @see javax.faces.context.ExternalContext#dispatch(java.lang.String)
     */
    public void dispatch(String arg0) throws IOException {
    }

    /**
     * @see javax.faces.context.ExternalContext#encodeActionURL(java.lang.String)
     */
    public String encodeActionURL(String arg0) {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#encodeNamespace(java.lang.String)
     */
    public String encodeNamespace(String arg0) {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#encodeResourceURL(java.lang.String)
     */
    public String encodeResourceURL(String arg0) {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getApplicationMap()
     */
    public Map getApplicationMap() {
        return this.applicationMap;
    }

    /**
     * @see javax.faces.context.ExternalContext#getAuthType()
     */
    public String getAuthType() {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getContext()
     */
    public Object getContext() {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getInitParameter(java.lang.String)
     */
    public String getInitParameter(String arg0) {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getInitParameterMap()
     */
    public Map getInitParameterMap() {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRemoteUser()
     */
    public String getRemoteUser() {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequest()
     */
    public Object getRequest() {
        return this.request;
    }

    /**
     * Set the request
     * 
     * @param request
     *            the request to set
     */
    public void setRequest(Object request) {
        this.request = request;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestContextPath()
     */
    public String getRequestContextPath() {
        return this.requestContextPath;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestCookieMap()
     */
    public Map getRequestCookieMap() {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestHeaderMap()
     */
    public Map getRequestHeaderMap() {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestHeaderValuesMap()
     */
    public Map getRequestHeaderValuesMap() {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestLocale()
     */
    public Locale getRequestLocale() {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestLocales()
     */
    public Iterator getRequestLocales() {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestMap()
     */
    public Map getRequestMap() {
        return this.requestMap;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestParameterMap()
     */
    public Map getRequestParameterMap() {
        return this.requestParamMap;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestParameterNames()
     */
    public Iterator getRequestParameterNames() {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestParameterValuesMap()
     */
    public Map getRequestParameterValuesMap() {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestPathInfo()
     */
    public String getRequestPathInfo() {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestServletPath()
     */
    public String getRequestServletPath() {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getResource(java.lang.String)
     */
    public URL getResource(String arg0) throws MalformedURLException {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getResourceAsStream(java.lang.String)
     */
    public InputStream getResourceAsStream(String arg0) {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getResourcePaths(java.lang.String)
     */
    public Set getResourcePaths(String arg0) {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getResponse()
     */
    public Object getResponse() {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getSession(boolean)
     */
    public Object getSession(boolean arg0) {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#getSessionMap()
     */
    public Map getSessionMap() {
        return this.sessionMap;
    }

    /**
     * @see javax.faces.context.ExternalContext#getUserPrincipal()
     */
    public Principal getUserPrincipal() {
        return null;
    }

    /**
     * @see javax.faces.context.ExternalContext#isUserInRole(java.lang.String)
     */
    public boolean isUserInRole(String arg0) {
        return false;
    }

    /**
     * @see javax.faces.context.ExternalContext#log(java.lang.String)
     */
    public void log(String arg0) {
    }

    /**
     * @see javax.faces.context.ExternalContext#log(java.lang.String,
     *      java.lang.Throwable)
     */
    public void log(String arg0, Throwable arg1) {
    }

    /**
     * @see javax.faces.context.ExternalContext#redirect(java.lang.String)
     */
    public void redirect(String arg0) throws IOException {
    }

    /**
     * Sets the request parameter map
     * 
     * @param newMap
     *            new map to set
     */
    public void setRequestParameterMap(Map newMap) {
        this.requestParamMap = newMap;
    }
}
