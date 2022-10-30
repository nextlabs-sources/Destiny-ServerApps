/*
 * Created on Feb 14, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.context;

import com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoggedInUser;
import com.bluejungle.framework.comp.ComponentManagerFactory;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * This is the application context.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/context/AppContext.java#3 $
 */
public abstract class AppContext {

    static final String APP_CONTEXT_SESSION_ATTR = "destiny.AppContextImpl";

    /**
     * Retrieve the application context. The application context is located in
     * the session scope. If it is not here, a new instance gets created. The
     * creation of the application context is manual so that every application
     * does not have to declare a managed bean for the application contect.
     * 
     * @return the application context instance. Note that this method will
     *         return null when not in the context of a JSF request. This is
     *         done to workaround an issue involving the Secure Session when
     *         sending heartbeats to dms. Please see
     * @see AppContextSecureSessionVault}
     */
    public static AppContext getContext() {
        AppContext contextToReturn = null;

        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            HttpServletRequest httpReq = (HttpServletRequest) context.getExternalContext().getRequest();
            contextToReturn = (getContext(httpReq));
        }
		return contextToReturn;
    }

    /**
     * Returns the application Context instance. Typically, only filters will
     * call this API, since they have a direct access to the HTTP servlet
     * request object. Other callers will call the getInstance() API since the
     * JSF context can give us access to the request object.
     * 
     * @param httpReq
     *            http request object
     * @return the application context instance
     */
    public static AppContext getContext(HttpServletRequest httpReq) {
        HttpSession httpSession = httpReq.getSession();
		AppContext ctx = null;
		if (httpSession != null) {
			ctx = (AppContext) httpSession.getAttribute(APP_CONTEXT_SESSION_ATTR);
		}
        if (httpSession == null || ctx == null) {
            AppContextFactory appContextFactory =
                    ComponentManagerFactory.getComponentManager().getComponent(AppContextFactory.class);
            ctx = appContextFactory.getAppContext(httpReq);
            httpSession.setAttribute(APP_CONTEXT_SESSION_ATTR, ctx);			
        }
        return ctx;
    }

    public AppContext(HttpServletRequest request) {
    }
    
    /**
     * Returns whether the current user is logged in
     * 
     * @return true of the user if logged in, false otherwise
     */
    public abstract boolean isLoggedIn();

    /**
     * FIX ME - This method below shouldn't be part of this interface. It should
     * be further hidden to prevent misuse. Not sure of a good way to do this,
     * though
     */
    /**
     * Set the currently logged in user
     * 
     * @param authenticatedUser
     *            the current logged in user
     */
    public abstract void setRemoteUser(ILoggedInUser authenticatedUser);

    /**
     * Retrieve the currently logged in user
     * 
     * @return the currently logged in user
     * @throws IllegalStateException
     *             if there is no user currently logged in (@see isLoggedIn)
     */
    public abstract ILoggedInUser getRemoteUser();

    /**
     * Set the category of currently logged in user
     * @param userCategory ADMIN or CONSOLE or API
     */
    public abstract void setUserCategory(String userCategory);
    
    /**
     * Get category of currently logged in user
     */
    public abstract String getUserCategory();
    
    /**
     * Set an application context attribute
     * 
     * @param attributeName
     *            the attribute name
     * @param attributeValue
     *            the attribute value
     */
    public abstract void setAttribute(String attributeName, Object attributeValue);

    /**
     * Retrieve an application context attribute by name
     * 
     * @param attributeName
     *            the name of the attribute to retrieve
     * @param defaultValue
     *            a default value to return, if the attribute doesn't exist
     * @return the attribute value associated with the specified name or the
     *         specified default value if the given attribute doesn't exist
     */
    public abstract Object getAttribute(String attributeName, Object defaultValue);

    /**
     * Retrieve an application context attribute by name. Equivalent to
     * getAttribute(attributeName, null);
     * 
     * @param attributeName
     *            the name of the attribute to retrieve
     * @return the attribute value associated with the specified name or null if
     *         the given attribute doesn't exist
     */
    public abstract Object getAttribute(String attributeName);

    /**
     * Release the current context. Flushes all context information and
     * invalidates the users session
     */
    public void releaseContext() {
        releaseContext((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
    }

    public void releaseContext(HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        httpSession.removeAttribute(APP_CONTEXT_SESSION_ATTR);
        httpSession.invalidate();
    }
}