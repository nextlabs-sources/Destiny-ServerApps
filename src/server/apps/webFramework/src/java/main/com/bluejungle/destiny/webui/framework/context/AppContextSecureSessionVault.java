/*
 * Created on May 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.context;

import com.bluejungle.destiny.appframework.appsecurity.axis.ISecureSessionVault;
import com.bluejungle.destiny.types.secure_session.v1.SecureSession;

/**
 * AppContextSecureSessionVault is an Implementation of the ISecureSessionVault
 * insterface which stores secure session instances in the AppContext
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/context/AppContextSecureSessionVault.java#2 $
 */

public class AppContextSecureSessionVault implements ISecureSessionVault {

    private static final String SECURE_SESSION_ATTR_NAME = "SecureSession";

    /**
     * @see com.bluejungle.destiny.appsecurity.axis.ISecureSessionVault#storeSecureSession(com.bluejungle.destiny.types.secure_session.v1.SecureSession)
     */
    public void storeSecureSession(SecureSession secureSession) {
        if (secureSession == null) {
            throw new NullPointerException("secureSession cannot be null.");
        }

        AppContext appContext = getAppContext();
        if (appContext != null) {
            appContext.setAttribute(SECURE_SESSION_ATTR_NAME, secureSession);
        }
    }

    /**
     * @see com.bluejungle.destiny.appsecurity.axis.ISecureSessionVault#getSecureSession()
     */
    public SecureSession getSecureSession() {
        SecureSession sessionToReturn = null;
        AppContext appContext = getAppContext();
        if (appContext != null) {
            sessionToReturn = (SecureSession) appContext.getAttribute(SECURE_SESSION_ATTR_NAME);
        }

        return sessionToReturn;
    }

    /**
     * @see com.bluejungle.destiny.appsecurity.axis.ISecureSessionVault#clearSecureSession()
     */
    public void clearSecureSession() {
        AppContext appContext = getAppContext();
        if (appContext != null) {
            appContext.setAttribute(SECURE_SESSION_ATTR_NAME, null);
        }
    }

    /**
     * Retrieve the current app context
     * 
     * @return the current app context
     */
    private AppContext getAppContext() {
        return AppContext.getContext();
    }
}