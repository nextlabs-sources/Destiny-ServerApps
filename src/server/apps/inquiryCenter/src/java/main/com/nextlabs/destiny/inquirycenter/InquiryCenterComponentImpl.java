/*
 * Created on Apr 17, 2008
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter;

import java.lang.reflect.Constructor;

import javax.mail.Address;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.container.dcc.BaseDCCComponentImpl;
import com.bluejungle.destiny.server.shared.configuration.IReporterComponentConfigurationDO;
import com.bluejungle.destiny.server.shared.registration.ServerComponentType;
import com.bluejungle.destiny.webui.framework.context.AppContextFactory;
import com.bluejungle.framework.comp.HashMapConfiguration;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.configuration.DestinyConfigurationStoreImpl;
import com.bluejungle.framework.configuration.DestinyRepository;
import com.bluejungle.framework.configuration.IDestinyConfigurationStore;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;
import com.bluejungle.framework.utils.IMailHelper;
import com.bluejungle.framework.utils.MailHelper;
import com.nextlabs.destiny.container.shared.customapps.CustomAppDataManager;
import com.nextlabs.destiny.container.shared.inquirymgr.ReportDataHolderManager;
import com.nextlabs.destiny.inquirycenter.context.ReportAppContext;
import com.nextlabs.destiny.inquirycenter.customapps.ExternalReportAppManager;
import com.nextlabs.destiny.inquirycenter.monitor.service.MonitorExecutionManager;
import com.nextlabs.framework.messaging.IMessageHandler;
import com.nextlabs.framework.messaging.IMessageHandlerManager;
import com.nextlabs.framework.messaging.handlers.EmailMessageHandler;
import com.nextlabs.framework.messaging.impl.IEmailMessageHandlerConfig;
import com.nextlabs.framework.messaging.impl.MessageHandlerManagerImpl;
import com.nextlabs.report.datagen.ReportDataManagerFactory;


/**
 * @author rlin
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/nextlabs/destiny/inquirycenter/InquiryCenterComponentImpl.java#1 $
 */

public class InquiryCenterComponentImpl extends BaseDCCComponentImpl {

	private static final Log LOG = LogFactory.getLog(InquiryCenterComponentImpl.class);
	
    public void init() {
        setComponentType(ServerComponentType.REPORTER);
        super.init();

        IComponentManager compMgr = getManager();
        final IDestinyConfigurationStore configMgr = compMgr.getComponent(DestinyConfigurationStoreImpl.COMP_INFO);
        final IReporterComponentConfigurationDO reporterCfg =
            (IReporterComponentConfigurationDO) configMgr
                        .retrieveComponentConfiguration(ServerComponentType.REPORTER.getName());
        
        IHibernateRepository activityDataSrc = (IHibernateRepository) compMgr.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName());
        
        if (activityDataSrc == null) {
            throw new RuntimeException("Data source " + DestinyRepository.ACTIVITY_REPOSITORY + " is not correctly setup for the DABS component.");
        }
        
        HashMapConfiguration reportDataHolderonfig = new HashMapConfiguration();
        reportDataHolderonfig.setProperty(ReportDataHolderManager.DATA_SOURCE_PARAM, activityDataSrc);
        compMgr.getComponent(ReportDataHolderManager.class, reportDataHolderonfig);
        
        // Initialize Mail Helper -- Make sure the configuration is ready before
        // instantiating. Copied from DABSComponentImpl

        IMessageHandlerManager messageHandlerManager = compMgr.getComponent(MessageHandlerManagerImpl.class);
        IMessageHandler messageHandler =
                messageHandlerManager.getMessageHandler(EmailMessageHandler.DEFAULT_HANDLER_NAME);


        if (messageHandler != null) {
            String mailServerHost   = messageHandler.getProperty(IEmailMessageHandlerConfig.SERVER);
            Integer mailServerPort  = messageHandler.getProperty(IEmailMessageHandlerConfig.PORT);
            String username         = messageHandler.getProperty(IEmailMessageHandlerConfig.USER);
            String password         = messageHandler.getProperty(IEmailMessageHandlerConfig.PASSWORD);
            Address from            = messageHandler.getProperty(IEmailMessageHandlerConfig.DEFAULT_FROM);

            HashMapConfiguration mailConfiguration = new HashMapConfiguration();
            mailConfiguration.setProperty(IMailHelper.SERVER_CFG_KEY, mailServerHost);
            mailConfiguration.setProperty(IMailHelper.PORT_CFG_KEY, mailServerPort);
            mailConfiguration.setProperty(IMailHelper.USER_CFG_KEY, username);
            mailConfiguration.setProperty(IMailHelper.PASSWORD_CFG_KEY, password);
            mailConfiguration.setProperty(IMailHelper.HEADER_FROM_CFG_KEY, from.toString());
            MailHelper.COMP_INFO.overrideConfiguration(mailConfiguration);
            compMgr.registerComponent(MailHelper.COMP_INFO, true);
        } else {
            getLog().warn("Mail server was not configured. Alert Notification Emails will not be delivered.");
        }

        HashMapConfiguration monitorExecutionConfig = new HashMapConfiguration();
        
        java.util.Properties props = reporterCfg.getProperties();
        
        monitorExecutionConfig.setProperty(MonitorExecutionManager.MONITOR_EXECUTION_INTERVAL, 
        		props.getProperty(MonitorExecutionManager.MONITOR_EXECUTION_INTERVAL));
        
        compMgr.getComponent(MonitorExecutionManager.class, monitorExecutionConfig);
        
        initCustomApp();
    }
    
    protected void initCustomApp(){
        IComponentManager compMgr = getManager();
        
        compMgr.getComponent(CustomAppDataManager.class);
        compMgr.getComponent(ExternalReportAppManager.class);
        
        HashMapConfiguration contextFactoryConfig = new HashMapConfiguration();
        Constructor<ReportAppContext> constructor;
        try {
            constructor = ReportAppContext.class.getConstructor(HttpServletRequest.class);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        contextFactoryConfig.setProperty(AppContextFactory.APP_CONTEXT_CONSTRUCTOR_KEY, constructor);
        compMgr.getComponent(AppContextFactory.class, contextFactoryConfig);
        
        ReportDataManagerFactory.setServerMode();
    }
}
