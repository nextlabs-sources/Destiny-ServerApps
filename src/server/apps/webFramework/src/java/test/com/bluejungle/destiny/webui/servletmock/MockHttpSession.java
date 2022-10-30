/*
 * Created on Mar 31, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.servletmock;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * Mock Http Session. Can be used in place of MockObjects package http session
 * when the expected values logic causes conflicts with production code business
 * logic (e.g. get/set attributes)
 * 
 * Note - Not yet fully implemented
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/servletmock/MockHttpSession.java#1 $
 */
public class MockHttpSession implements HttpSession {

    private Map attributes = new HashMap();

    /**
     * Constructor
     *  
     */
    public MockHttpSession() {
        super();
    }

    /**
     * @see javax.servlet.http.HttpSession#getCreationTime()
     */
    public long getCreationTime() {
        return 0;
    }

    /**
     * @see javax.servlet.http.HttpSession#getId()
     */
    public String getId() {
        return null;
    }

    /**
     * @see javax.servlet.http.HttpSession#getLastAccessedTime()
     */
    public long getLastAccessedTime() {
        return 0;
    }

    /**
     * @see javax.servlet.http.HttpSession#getServletContext()
     */
    public ServletContext getServletContext() {
        return null;
    }

    /**
     * @see javax.servlet.http.HttpSession#setMaxInactiveInterval(int)
     */
    public void setMaxInactiveInterval(int arg0) {
    }

    /**
     * @see javax.servlet.http.HttpSession#getMaxInactiveInterval()
     */
    public int getMaxInactiveInterval() {
        return 0;
    }

    /**
     * @see javax.servlet.http.HttpSession#getSessionContext()
     */
    public HttpSessionContext getSessionContext() {
        return null;
    }

    /**
     * @see javax.servlet.http.HttpSession#getAttribute(java.lang.String)
     */
    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    /**
     * @see javax.servlet.http.HttpSession#getValue(java.lang.String)
     */
    public Object getValue(String arg0) {
        return null;
    }

    /**
     * @see javax.servlet.http.HttpSession#getAttributeNames()
     */
    public Enumeration getAttributeNames() {
        return null;
    }

    /**
     * @see javax.servlet.http.HttpSession#getValueNames()
     */
    public String[] getValueNames() {
        return null;
    }

    /**
     * @see javax.servlet.http.HttpSession#setAttribute(java.lang.String,
     *      java.lang.Object)
     */
    public void setAttribute(String name, Object value) {
        this.attributes.put(name, value);
    }

    /**
     * @see javax.servlet.http.HttpSession#putValue(java.lang.String,
     *      java.lang.Object)
     */
    public void putValue(String arg0, Object arg1) {
    }

    /**
     * @see javax.servlet.http.HttpSession#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String arg0) {
    }

    /**
     * @see javax.servlet.http.HttpSession#removeValue(java.lang.String)
     */
    public void removeValue(String arg0) {
    }

    /**
     * @see javax.servlet.http.HttpSession#invalidate()
     */
    public void invalidate() {
    }

    /**
     * @see javax.servlet.http.HttpSession#isNew()
     */
    public boolean isNew() {
        return false;
    }

}