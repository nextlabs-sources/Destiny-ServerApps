/*
 * Created on Feb 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.loginmgr;

/**
 * This is the interface for the web login manager. In the web case, the
 * credentials are retrieved from a bean in the request scope and passed to the
 * login manager component for verification.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/loginmgr/IWebLoginMgr.java#1 $
 */

public interface IWebLoginMgr {

    public static final String LOGIN_SUCCESS = "Success";
    public static final String LOGIN_FAILURE = "Failure";

    /**
     * Returns whether a set of credentials is valid for login.
     * 
     * @return a status string telling LOGIN_SUCCESS if login succeeded, or
     *         LOGIN_FAILURE otherwise.
     */
    public String performLogin();
    
    /**
     * Clear all login context information
     */
    public String performLogout();
}