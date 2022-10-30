/*
 * Created on Feb 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.loginmgr;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.appframework.CommonConstants;
import com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoggedInUser;
import com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoginInfo;
import com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoginMgr;
import com.bluejungle.destiny.appframework.appsecurity.loginmgr.LoginException;
import com.bluejungle.destiny.webui.framework.context.AppContext;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;

/**
 * This is the web login manager implementation. The web login manager extends
 * the regular web login manager, because it needs to extract the credential
 * information from the request scope. Then, it needs to returns specific values
 * so that the UI framework can navigate the user to the correct page, or take
 * the proper decision.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/loginmgr/WebLoginMgrImpl.java#1 $
 */

public class WebLoginMgrImpl implements IWebLoginMgr {

    private static final String LOGIN_ACTION = "login";
    private static final String LOGIN_FAILED_SUMMARY_MSG = "login_failed_summary";
    private static final String LOGIN_FAILED_DETAIL_MSG = "login_failed_detail";
    private static final String DEFAULT_LOGIN_INFO_BINDING = "#{loginInfo}";
    private static final Log LOG = LogFactory.getLog(WebLoginMgrImpl.class.getName());
    private String loginInfoBinding;

    /**
     * Constructor.
     */
    public WebLoginMgrImpl() {
        super();
    }

    /**
     * @param context
     */
    private void addLoginFailedMessage(FacesContext context) {
        ResourceBundle bundle = ResourceBundle.getBundle(CommonConstants.COMMON_BUNDLE_NAME, context.getViewRoot().getLocale());
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString(LOGIN_FAILED_SUMMARY_MSG), bundle.getString(LOGIN_FAILED_DETAIL_MSG)));
    }

    /**
     * Returns the application context associated with this user session.
     * 
     * @return the application context associated with this user session.
     */
    private AppContext getApplicationContext() {
        AppContext appContext = AppContext.getContext();
        return (appContext);
    }

    /**
     * Returns the log object
     * 
     * @return the log object
     */
    protected Log getLog() {
        return LOG;
    }

    /**
     * Returns the binding expression for the login information. If none is
     * specified, the default is used.
     * 
     * @return binding expression for the login information
     */
    private String getLoginInfoBinding() {
        String result = this.loginInfoBinding;
        if (result == null) {
            result = DEFAULT_LOGIN_INFO_BINDING;
        }
        return (result);
    }

    /**
     * Performs the login operation. This function looks for the login manager
     * component. If the login manager component is not ready yet, it means that
     * the application component is not ready yet. In this case, the login will
     * fail.
     */
    public String performLogin() {
        String result = LOGIN_FAILURE;
        ILoginMgr loginMgr = null;

        FacesContext context = FacesContext.getCurrentInstance();
        ValueBinding binding = context.getApplication().createValueBinding(getLoginInfoBinding());
        try {
            IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
            loginMgr = (ILoginMgr) compMgr.getComponent(ILoginMgr.COMP_NAME);

            try {
                ILoginInfo loginInfo = (ILoginInfo) binding.getValue(context);
                ILoggedInUser authenticatedUser = loginMgr.login(loginInfo);
                result = LOGIN_SUCCESS;

                // We want to avoid Session Fixation.  CWE-384. For a description of the issue, See:
                // 			 http://cwe.mitre.org/data/definitions/384.html 
                // See also: http://blog.whitehatsec.com/tag/session-fixation/
                //           https://www.owasp.org/index.php/Session_Management_Cheat_Sheet
                //           http://www.acrossecurity.com/papers/session_fixation.pdf
                
                // Get old session and save attributes from it.
                HttpServletRequest httpReq = (HttpServletRequest) context.getExternalContext().getRequest();
                HttpSession oldSession = httpReq.getSession();
                Map<String, Object> oldAttribs = new HashMap<String, Object>();
                Enumeration<?> e = oldSession.getAttributeNames();
                while (e != null && e.hasMoreElements()) {
                	String name = (String) e.nextElement();
                	Object value = oldSession.getAttribute(name);
                	oldAttribs.put(name, value);
                }
                oldSession.invalidate();
                
                // Create a new session, which will have a new session ID.
                HttpSession newSession = httpReq.getSession(); // will create a new one, if one doesn't exist.
                
                // Save the old attributes to the new session.    
                for (Map.Entry<String, Object> mapEntry : oldAttribs.entrySet()) {
                	newSession.setAttribute(mapEntry.getKey(), mapEntry.getValue());                 
                }                 
                                
                AppContext dacContext = getApplicationContext();
                dacContext.setRemoteUser(authenticatedUser);
                if (getLog().isInfoEnabled()) {
                    getLog().info("Login for user '" + loginInfo.getUserName() + "' succeeded.");
                }
            } catch (LoginException exception) {
                getLog().error("Login attempt failed.", exception);
                /*
                 * FIX ME - Not sure if this is the appropriate action. Do we
                 * need another message here or an error page?
                 */
                addLoginFailedMessage(context);

            } catch (ClassCastException e) {
                //We are given a wrong login info bean
                getLog().error("Unable to retrieve login info from the context", e);

                /*
                 * FIX ME - Not sure if this is the appropriate action. Do we
                 * need another message here or an error page?
                 */
                addLoginFailedMessage(context);
            }
        } catch (RuntimeException e) {
            //If the login manager is not ready, then the login will fail
            // gracefully.
            getLog().info("Login request dropped : login manager is not ready yet");

            /*
             * FIX ME - Not sure if this is the appropriate action. Do we need
             * another message here or an error page?
             */
            addLoginFailedMessage(context);
        }
        return result;
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.loginmgr.IWebLoginMgr#performLogout()
     */
    public String performLogout() {
        return LOGIN_ACTION;
    }

    /**
     * This function is called before the login page is rendered. It checks
     * whether there is already an active session. The only way to come to the
     * login page during an active session is to do it purposely. Therefore, if
     * such a thing happens, the current session is finished and the login page
     * is displayed.
     */
    public void prerender() {
        if (getApplicationContext().isLoggedIn()) {
            getLog().debug("User already logged in and requested the login page. Logging the user out.");
            performLogout();
        }
    }

    /**
     * Sets the binding expression for the login manager. This expression allows
     * the web login manager to retrieve the login info from the context.
     * Typically, users should not have to change this, but this can give more
     * flexibility.
     * 
     * @param loginInfoBinding
     *            binding expression.
     */
    public void setLoginInfoBinding(String loginInfoBinding) {
        this.loginInfoBinding = loginInfoBinding;
    }
}