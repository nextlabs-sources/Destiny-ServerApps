/*
 * Created on Mar 16, 2010
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2010 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.IConfigurable;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.comp.IHasComponentInfo;
import com.bluejungle.framework.comp.LifestyleType;
import com.bluejungle.framework.comp.PropertyKey;

/**
 * @author hchan
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/context/AppContextFactory.java#1 $
 */

public class AppContextFactory implements IConfigurable, IHasComponentInfo<AppContextFactory>{
    public static final PropertyKey<Constructor<? extends AppContext>> APP_CONTEXT_CONSTRUCTOR_KEY =
            new PropertyKey<Constructor<? extends AppContext>>("AppContext constructor");
    
    private static final Constructor<? extends AppContext> DEFAULT_APP_CONTEXT_CONSTRUCTOR;
    static {
        try {
            DEFAULT_APP_CONTEXT_CONSTRUCTOR = AppContextImpl.class.getConstructor(HttpServletRequest.class);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    
    private static final ComponentInfo<AppContextFactory> COMP_INFO = 
        new ComponentInfo<AppContextFactory>(AppContextFactory.class, LifestyleType.SINGLETON_TYPE);
    
    private Constructor<? extends AppContext> appContextConstructor = DEFAULT_APP_CONTEXT_CONSTRUCTOR;
    private IConfiguration configuration;
    
    public AppContext getAppContext(HttpServletRequest httpReq){
    	// This getSession call was flagged in Veracode scans.  Our mitigation of this is in WebLoginMgrImpl.java
    	// in the performLogin() method.  Here we get a session but don't create one.
    	
        HttpSession httpSession = httpReq.getSession(false); // Don't create a new session
        if (httpSession == null) {
        	throw new RuntimeException("AppContextFactory failed to get a valid session.");
        }
        
        AppContext ctx = (AppContext) httpSession.getAttribute(AppContext.APP_CONTEXT_SESSION_ATTR);
        if (ctx == null) {
            try {
                ctx = appContextConstructor.newInstance(httpReq); 
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            httpSession.setAttribute(AppContext.APP_CONTEXT_SESSION_ATTR, ctx);
        }
        return ctx;
    }

    public IConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(IConfiguration config) {
        if (config != null) {
            Constructor<? extends AppContext> constructor = config.get(APP_CONTEXT_CONSTRUCTOR_KEY);
            if (constructor != null) {
                this.appContextConstructor = constructor;
            }
        }
        this.configuration = config;
    }

    public ComponentInfo<AppContextFactory> getComponentInfo() {
        return COMP_INFO;
    }
}
