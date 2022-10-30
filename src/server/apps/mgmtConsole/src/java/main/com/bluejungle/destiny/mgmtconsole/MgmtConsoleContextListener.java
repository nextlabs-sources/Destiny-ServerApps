/*
 * Created on Feb 14, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.container.dcc.DCCContextListener;
import com.bluejungle.destiny.mgmtconsole.environment.MgmtConsoleResourceLocators;
import com.bluejungle.destiny.server.shared.registration.ServerComponentType;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.HashMapConfiguration;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.LifestyleType;
import com.bluejungle.framework.environment.IResourceLocator;
import com.bluejungle.framework.environment.webapp.WebAppResourceLocatorImpl;
import com.nextlabs.destiny.configclient.ConfigClient;

/**
 * This is the management console context listener class.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/MgmtConsoleContextListener.java#1 $
 */

public class MgmtConsoleContextListener extends DCCContextListener {

	private final Log LOG = LogFactory.getLog(MgmtConsoleContextListener.class);
	private static final String CONTEXT_PARAMETER_CONFIG_PREFIX = "context.parameter.";

    /**
     * @see com.bluejungle.destiny.container.dcc.DCCContextListener#getComponentType()
     */
    public ServerComponentType getComponentType() {
        return ServerComponentType.MGMT_CONSOLE;
    }
    
    @Override
    public String getTypeDisplayName() {
        return "Administrator";
    }


    /**
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent servletContextEvent) {
		try {
			ConfigClient.init("administrator");
			ServletContext servletContext = servletContextEvent.getServletContext();
			String version = ConfigClient.get("application.version").toString();
			String build = ConfigClient.get("application.build").toString();
			if (StringUtils.isNotEmpty(build)) {
				version = String.format("%s-%s", version, build);
			}
			String serverName = ConfigClient.get("server.name").toString();
			servletContext.setAttribute("helpUrl", ConfigClient.get("help.url.administrator"));
			servletContext.setAttribute("helpUrlContentFormat", ConfigClient.get("help.url.contentFormat"));
			servletContext.setInitParameter("application.version", version);
			servletContext.setInitParameter("casServerUrlPrefix", ConfigClient.get("cas.service.url").toString());
			servletContext.setInitParameter("serverName", serverName);
			servletContext.setInitParameter("casServerLoginUrl", ConfigClient.get("cas.service.login").toString());
			servletContext.setInitParameter("casServerLogoutUrl", String.format("%s?service=", ConfigClient.get("cas.service.logout").toString()));
			servletContext.setInitParameter("unAuthAccessRedirectUrl", String.format("%s/administrator/logout", serverName));
			servletContext.setInitParameter("loginUrl", String.format("%s/administrator/", serverName));
		} catch (Exception e) {
			LOG.error("Error in loading system configurations", e);
			throw new RuntimeException(e);
		}
        initConfigurations(servletContextEvent);
        super.contextInitialized(servletContextEvent);
        setupResourceLocator(servletContextEvent.getServletContext());
    }

	private void initConfigurations(ServletContextEvent servletContextEvent) {
		ServletContext servletContext = servletContextEvent.getServletContext();
		ConfigClient.getAll(CONTEXT_PARAMETER_CONFIG_PREFIX).forEach(config -> {
			String parameterName = config.getKey().replace(CONTEXT_PARAMETER_CONFIG_PREFIX, "");
			if (servletContext.getInitParameter(parameterName) == null) {
				servletContext.setInitParameter(parameterName, config.toString());
			}
		});
	}

	@Override
	protected HashMapConfiguration prepareComponentConfiguration(
			ServletContext ctx) {
		
		getLog().info(
				"Mgmt Context prepare Component Configuration");
		
		if (ctx == null) {
			throw new NullPointerException("Servlet context cannot be null");
		}
		
		return super.prepareComponentConfiguration(ctx);
	}

    /**
     * This function sets up the resource locator
     * 
     * @param servletContext
     *            servlet context to use
     */
    private void setupResourceLocator(ServletContext servletContext) {
        HashMapConfiguration webAppFileLocatorConfig = new HashMapConfiguration();
        webAppFileLocatorConfig.setProperty(WebAppResourceLocatorImpl.CONTAINER_CTX_CONFIG_PARAM, servletContext);
        ComponentInfo webAppFileLocatorInfo = new ComponentInfo(MgmtConsoleResourceLocators.WEB_APP_RESOURCE_LOCATOR_COMP_NAME, WebAppResourceLocatorImpl.class.getName(), IResourceLocator.class.getName(), LifestyleType.SINGLETON_TYPE,
                webAppFileLocatorConfig);
        IComponentManager componentManager = ComponentManagerFactory.getComponentManager();
        componentManager.registerComponent(webAppFileLocatorInfo, true);
    }

    
}