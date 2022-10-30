/*
 * Created on Mar 24, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.service.BirtReportServiceFactory;
import org.eclipse.birt.report.service.BirtViewerReportService;
import org.eclipse.birt.report.service.ReportEngineService;
import org.eclipse.birt.report.service.api.IViewerReportService;
import org.eclipse.birt.report.utility.ParameterAccessor;

import com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoginMgr;
import com.bluejungle.destiny.container.dcc.DCCContextListener;
import com.bluejungle.destiny.inquirycenter.environment.InquiryCenterResourceLocators;
import com.bluejungle.destiny.server.shared.registration.ServerComponentType;
import com.bluejungle.destiny.webui.framework.loginmgr.remote.WebAppRemoteLoginMgr;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.HashMapConfiguration;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.LifestyleType;
import com.bluejungle.framework.environment.IResourceLocator;
import com.bluejungle.framework.environment.webapp.WebAppResourceLocatorImpl;
import com.nextlabs.destiny.configclient.ConfigClient;

/**
 * The Inquiry Center Context Listener is responsible for initialized the
 * Inquiry Center Application
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/InquiryCenterContextListener.java#3 $
 */

public class InquiryCenterContextListener extends DCCContextListener  {

    private static final String DAC_LOCATION_INIT_PARAM_NAME = "DACLocation";
    private static final String SECURE_SESSION_SERVICE_PATH_INFO = "services/SecureSessionService";
	private static final String CONTEXT_PARAMETER_CONFIG_PREFIX = "context.parameter.";
//    /**
//     * Name of the container component name
//     */
//    private static final String CONTAINER_COMP_NAME = "DCCContainerComponent";
    private final Log log = LogFactory.getLog(InquiryCenterContextListener.class);

    /**
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext servletContext = servletContextEvent.getServletContext();
		try {
			ConfigClient.init("reporter");
            String version = ConfigClient.get("application.version").toString();
            String build = ConfigClient.get("application.build").toString();
            if (StringUtils.isNotEmpty(build)) {
                version = String.format("%s-%s", version, build);
            }
            String serverName = ConfigClient.get("server.name").toString();
            servletContext.setAttribute("helpUrl", ConfigClient.get("help.url.reporter"));
            servletContext.setAttribute("helpUrlContentFormat", ConfigClient.get("help.url.contentFormat"));
            servletContext.setInitParameter("application.version", version);
            servletContext.setInitParameter("casServerUrlPrefix", ConfigClient.get("cas.service.url").toString());
            servletContext.setInitParameter("serverName", serverName);
            servletContext.setInitParameter("casServerLoginUrl", ConfigClient.get("cas.service.login").toString());
            servletContext.setInitParameter("casServerLogoutUrl", String.format("%s?service=", ConfigClient.get("cas.service.logout").toString()));
            servletContext.setInitParameter("unAuthAccessRedirectUrl", String.format("%s/reporter/logout", serverName));
            servletContext.setInitParameter("loginUrl", String.format("%s/reporter/", serverName));
		} catch (Exception e) {
			log.error("Error in loading system configurations", e);
			throw new RuntimeException(e);
		}
		initConfigurations(servletContext);
		ParameterAccessor.initParameters(servletContext);
		if (!ParameterAccessor.isOverWrite) {
			ParameterAccessor.isOverWrite = true;
		}
		IViewerReportService instance = new BirtViewerReportService(servletContext);
		BirtReportServiceFactory.init(instance);

		try {
			BirtReportServiceFactory.getReportService().setContext(
					servletContextEvent.getServletContext(), null);
		} catch (BirtException e) {
			// This error always printed but reporter still working. Change from log level from error to debug
		    log.debug(e.getMessage());
		}
		setupResourceLocator(servletContext);
		setupLoginManager(servletContext);
		super.contextInitialized(servletContextEvent);
	}

	private void initConfigurations(ServletContext servletContext) {
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
				"Reporter Context prepare Component Configuration");
		if (ctx == null) {
			throw new NullPointerException("Servlet context cannot be null");
		}
		
		return super.prepareComponentConfiguration(ctx);
	}

    /**
     * Returns the log object
     * 
     * @return the log object
     */
    protected Log getLog() {
        return this.log;
    }

    /**
     * @param dacLocation
     *            location of the DAC component
     * @return the URL of the secure service on the DAC component
     */
    private String getSecureSessionServiceEndpoint(String dacLocation) {
        if (dacLocation == null) {
            final String errMsg = "'" + DAC_LOCATION_INIT_PARAM_NAME + "'" + " cannot be null.";
            getLog().fatal(errMsg);
            throw new NullPointerException(errMsg);
        }

        StringBuffer resultToReturn = new StringBuffer(dacLocation);
        if (!dacLocation.endsWith("/")) {
            resultToReturn.append("/");
        }

        resultToReturn.append(SECURE_SESSION_SERVICE_PATH_INFO);
        return resultToReturn.toString();
    }

    /**
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent servletContext) {
        try{
            // When trying to destroy application, shutdown Platform and
            // ReportEngineService.
            Platform.shutdown( );
            ReportEngineService.shutdown( );
    
            // Reset initialized parameter
            ParameterAccessor.reset( );
        } catch(RuntimeException e){
            getLog().error("An error occured during destory.", e);
        } finally{
            super.contextDestroyed(servletContext);
        }
    }

    /**
     * Creates the login manager component
     * 
     * @param servletContext
     *            servlet context to use
     */
    private void setupLoginManager(ServletContext servletContext) {
        String dacLocation = servletContext.getInitParameter(DAC_LOCATION_INIT_PARAM_NAME);
        IComponentManager componentManager = ComponentManagerFactory.getComponentManager();
        HashMapConfiguration componentConfig = new HashMapConfiguration();
        componentConfig.setProperty(WebAppRemoteLoginMgr.SECURE_SESSION_SERVICE_ENDPOINT_PROP_NAME, getSecureSessionServiceEndpoint(dacLocation));
        ComponentInfo<ILoginMgr> componentInfo = 
            new ComponentInfo<ILoginMgr>(
                ILoginMgr.COMP_NAME, 
                WebAppRemoteLoginMgr.class, 
                ILoginMgr.class, 
                LifestyleType.SINGLETON_TYPE, 
                componentConfig);
        componentManager.registerComponent(componentInfo, true);
    }

    /**
     * Creates the resource locator component
     * 
     * @param servletContext
     *            servlet context
     */
    private void setupResourceLocator(ServletContext servletContext) {
        HashMapConfiguration webAppFileLocatorConfig = new HashMapConfiguration();
        webAppFileLocatorConfig.setProperty(WebAppResourceLocatorImpl.CONTAINER_CTX_CONFIG_PARAM, servletContext);
        ComponentInfo<IResourceLocator> webAppFileLocatorInfo = 
            new ComponentInfo<IResourceLocator>(
                InquiryCenterResourceLocators.WEB_APP_RESOURCE_LOCATOR_COMP_NAME, 
                WebAppResourceLocatorImpl.class, 
                IResourceLocator.class, 
                LifestyleType.SINGLETON_TYPE,
                webAppFileLocatorConfig);

        IComponentManager componentManager = ComponentManagerFactory.getComponentManager();
        componentManager.registerComponent(webAppFileLocatorInfo, true);
    }

    /**
     * @see com.bluejungle.destiny.container.dcc.DCCContextListener#getComponentType()
     */
    public ServerComponentType getComponentType() {
        return ServerComponentType.REPORTER;
    }

    @Override
    public String getTypeDisplayName() {
        return "Reporter";
    }
}
