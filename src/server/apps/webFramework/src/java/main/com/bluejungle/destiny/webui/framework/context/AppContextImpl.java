/*
 * Created on Feb 14, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.context;

import com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoggedInUser;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * This is the application context implementation class. The application context is created for
 * every user session and maintains some useful information accessible by other
 * components within the application framework.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/context/AppContextImpl.java#1 $
 */
public class AppContextImpl extends AppContext {
    
    private Map attributes = new HashMap();
    private ILoggedInUser remoteUser;
    private String userCategory;
        
    public AppContextImpl(HttpServletRequest request) {
        super(request);
    }

    /**
     * Returns whether the current user is logged in
     * 
     * @return true if the current user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return (getRemoteUser() != null);
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.context.AppContext#setRemoteUser(java.lang.String)
     */
    public void setRemoteUser(ILoggedInUser remoteUser) {
        if (remoteUser == null) {
            throw new NullPointerException("remoteUser cannot be null.");
        }
        
        this.remoteUser = remoteUser;
    }
    
    /**
     * @see com.bluejungle.destiny.webui.framework.context.AppContext#getRemoteUser()
     */
    public ILoggedInUser getRemoteUser() {
        return this.remoteUser;
    }
    
    /**
     * @see com.bluejungle.destiny.webui.framework.context.AppContext#setUserCategory(java.lang.String)
     */
    public void setUserCategory(String userCategory) {
    	this.userCategory = userCategory;
    }
    
    /**
     * @see com.bluejungle.destiny.webui.framework.context.AppContext#getUserCategory()
     */
    public String getUserCategory() {
    	return this.userCategory;
    }
    
    /**
     * @see com.bluejungle.destiny.webui.framework.context.AppContext#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String attributeName, Object attributeValue) {
        if (attributeName == null) {
            throw new NullPointerException("attributeName cannot be null.");
        }
        
        this.attributes.put(attributeName, attributeValue);
    }
    
    /**
     * @see com.bluejungle.destiny.webui.framework.context.AppContext#getAttribute(java.lang.String, java.lang.Object)
     */
    public Object getAttribute(String attributeName, Object defaultValue) {
        if (attributeName == null) {
            throw new NullPointerException("attributeName cannot be null.");
        }
        
        Object attributeValueToReturn = this.attributes.get(attributeName);
        if (attributeValueToReturn == null) {
            attributeValueToReturn = defaultValue;
        }
        
        return attributeValueToReturn;
    }
    
    /**
     * @see com.bluejungle.destiny.webui.framework.context.AppContext#getAttribute(java.lang.String)
     */
    public Object getAttribute(String attributeName) {
        return this.getAttribute(attributeName, null);
    }
    
    
    /**
     * @see com.bluejungle.destiny.webui.framework.context.AppContext#releaseContext()
     */
    public void releaseContext() {
        super.releaseContext();
        this.attributes = null;
        this.remoteUser = null;
    }

    public void releaseContext(HttpServletRequest httpServletRequest) {
        super.releaseContext(httpServletRequest);
        this.attributes = null;
        this.remoteUser = null;
    }
}