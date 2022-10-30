/*
 * Created on Feb 2, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole;

import com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoginMgr;
import com.bluejungle.destiny.container.dcc.BaseDCCComponentImpl;
import com.bluejungle.destiny.container.dcc.IDCCContainer;
import com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.browsablehostpicker.DirectoryQueryBroker;
import com.bluejungle.destiny.server.shared.registration.ServerComponentType;
import com.bluejungle.destiny.webui.framework.loginmgr.remote.WebAppRemoteLoginMgr;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.HashMapConfiguration;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.comp.LifestyleType;
import com.bluejungle.framework.configuration.DestinyConfigurationStoreImpl;
import com.bluejungle.framework.configuration.DestinyRepository;
import com.bluejungle.framework.configuration.IDestinyConfigurationStore;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;

/**
 * This is the implementation class for the management console component.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/com/bluejungle
 *          /destiny/mgmtconsole/MgmtConsoleComponentImpl.java#1 $
 */

public class MgmtConsoleComponentImpl extends BaseDCCComponentImpl {

	private static final String SECURE_SESSION_SERVICE_PATH_INFO = "services/SecureSessionService";

	private static IHibernateRepository activityDataSrc;

	/**
	 * @see com.bluejungle.destiny.server.shared.registration.IRegisteredDCCComponent#getComponentType()
	 */
	public ServerComponentType getComponentType() {
		return ServerComponentType.MGMT_CONSOLE;
	}

	/**
	 * Initializes the Management console component. The initialization sets up
	 * the authentication manager.
	 */
	public void init() {
		super.init();
		final IDestinyConfigurationStore confStore = (IDestinyConfigurationStore) getManager()
				.getComponent(DestinyConfigurationStoreImpl.COMP_INFO);

		// Initialize the host-group cache:
		DirectoryQueryBroker broker = (DirectoryQueryBroker) getManager().getComponent(DirectoryQueryBroker.class);

		activityDataSrc = (IHibernateRepository) getManager()
				.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName());
		if (activityDataSrc == null) {
			throw new RuntimeException("Data source " + DestinyRepository.ACTIVITY_REPOSITORY
					+ " is not correctly setup for the DABS component.");
		}

		// HashMapConfiguration dataHolderonfig = new HashMapConfiguration();
		// dataHolderonfig.setProperty(ReportDataHolderManager.DATA_SOURCE_PARAM,
		// activityDataSrc);
		// getManager().getComponent(ReportDataHolderManager.class,
		// dataHolderonfig);

		// Initializes the login manager
		IConfiguration componentConfiguration = (IConfiguration) getManager()
				.getComponent(IDCCContainer.MAIN_COMPONENT_CONFIG_COMP_NAME);
		final String dmsLocation = (String) componentConfiguration.get(IDCCContainer.DMS_LOCATION_CONFIG_PARAM);
		initLoginManager(dmsLocation);
	}

	public static IHibernateRepository getActivityDataSource() {
		return activityDataSrc;
	}

	/**
	 * This function sets up the remote login manager component
	 * 
	 * @param dmsLocation
	 *            location of the DMS component
	 */
	private void initLoginManager(final String dmsLocation) {
		if (dmsLocation == null) {
			final String msg = "Error : no DMS location specified. Unable to intialize login manager";
			getLog().fatal(msg);
			throw new RuntimeException(msg);
		}
		HashMapConfiguration componentConfig = new HashMapConfiguration();
		String loginServiceLocation = dmsLocation;
		if (!loginServiceLocation.endsWith("/")) {
			loginServiceLocation = loginServiceLocation.concat("/");
		}
		loginServiceLocation = loginServiceLocation.concat(SECURE_SESSION_SERVICE_PATH_INFO);
		componentConfig.setProperty(WebAppRemoteLoginMgr.SECURE_SESSION_SERVICE_ENDPOINT_PROP_NAME,
				loginServiceLocation);
		ComponentInfo componentInfo = new ComponentInfo(ILoginMgr.COMP_NAME, WebAppRemoteLoginMgr.class.getName(),
				ILoginMgr.class.getName(), LifestyleType.SINGLETON_TYPE, componentConfig);
		// Prime the login manager (make sure it comes up)
		ILoginMgr loginMgr = (ILoginMgr) getManager().getComponent(componentInfo);
	}
}
