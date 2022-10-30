/*
 * Created on Apr 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl;

import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.bluejungle.destiny.services.management.CommitFault;
import com.bluejungle.destiny.services.management.ComponentServiceStub;
import com.bluejungle.destiny.services.management.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.UnauthorizedCallerFault;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.container.dcc.IDCCContainer;
import com.bluejungle.destiny.mgmtconsole.status.IComponentStatusBean;
import com.bluejungle.destiny.server.shared.registration.ServerComponentType;
import com.bluejungle.destiny.services.management.types.Component;
import com.bluejungle.destiny.webui.framework.data.ProxyingDataModel;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IConfiguration;

/**
 * ComponentStatusBean is a concrete implementation of IComponentStatusBean
 * which retrieve component data from the DMS Component web service
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/ComponentStatusBean.java#1 $
 */
public class ComponentStatusBean implements IComponentStatusBean {

    private static final String COMPONENT_SERVICE_LOCATION_SERVLET_PATH = "/services/ComponentService";
    private static final Log LOG = LogFactory.getLog(ComponentStatusBean.class.getName());

    private ComponentServiceStub componentService;

    /**
     * Create an instance of ComponentStatusBean
     */
    public ComponentStatusBean() {
        super();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IComponentStatusBean#getComponentData()
     */
    public DataModel getComponentData() {
        Component[] components;
        try {
            components = retrieveComponents();
        } catch (ServiceNotReadyFault exception) {
            components = new Component[0];
            LOG.warn("Failed to retrieve component data from management server.  Server may not be fully initialized.", exception);
        } catch (UnauthorizedCallerFault exception) {
            components = new Component[0];
            LOG.warn("Failed to retrieve component data from management server.  Current user not authorized for management data access.", exception);
        } catch (RemoteException | CommitFault exception) {
            components = new Component[0];
            LOG.warn("Failed to retrieve component data from management server.", exception);
        }
        return new TranslatingDataModel(new DCSFFilteringDataModel(components));
    }

    /**
     * Retrieve the component data from the management server
     * 
     * @return the component data from the management server
     * @throws RemoteException
     * @throws UnauthorizedCallerFault
     * @throws ServiceNotReadyFault
     * @throws ServiceException
     */
    private Component[] retrieveComponents() throws RemoteException, CommitFault, UnauthorizedCallerFault, ServiceNotReadyFault {
        ComponentServiceStub componentService = getComponentService();
        return componentService.getComponents().getComp();
    }

    /**
     * Retrieve the Component Service interface.
     * 
     * Note - Protected for unit test purposes. Yes, not ideal, but it's also
     * better to test with a stub service rather than an actual service
     * 
     * @return the Component Service interface
     * @throws ServiceException
     *             if the component service interface could not be located
     */
    protected ComponentServiceStub getComponentService() throws AxisFault {
        if (this.componentService == null) {
            IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
            IConfiguration mainCompConfig = (IConfiguration) compMgr.getComponent(IDCCContainer.MAIN_COMPONENT_CONFIG_COMP_NAME);
            String location = mainCompConfig.get(IDCCContainer.DMS_LOCATION_CONFIG_PARAM);
            location += COMPONENT_SERVICE_LOCATION_SERVLET_PATH;

            this.componentService = new ComponentServiceStub(location);
        }

        return this.componentService;
    }

    /**
     * An implementation of the Faces DataModel interfaces. Provides
     * functionality for translating the Component[] instances retrieved from
     * the server to ComponentDataBean instances as the data is being interated
     * through.
     * 
     * @author sgoldstein
     */
    private class TranslatingDataModel extends ProxyingDataModel {
        private TranslatingDataModel(DataModel wrappedDataModel) {
            super(wrappedDataModel);
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
         */
        protected Object proxyRowData(Object rawData) {
            if (rawData == null) {
                throw new NullPointerException("rawData cannot be null.");
            }
            
            return new ComponentDataBean((Component) rawData);
        }
    }

    /**
     * A DataModel which filters out the DCSF components
     * 
     * @author sgoldstein
     */
    public class DCSFFilteringDataModel extends ListDataModel {

        /**
         * Create an instance of DCSFFilteringDataModel
         * 
         * @param components
         */
        public DCSFFilteringDataModel(Component[] components) {
            ArrayList<Component> filteredData = this.filterComponents(components);
            super.setWrappedData(filteredData);
        }

        /**
         * Return the specified component array as an ArrayList with the DCSF
         * components filtered out
         * 
         * Note than an ArrayList is chosen explicitly here in order to ensure
         * best performance when using the JSF Data Table tags
         * 
         * @param components
         *            the components to filter
         */
        private ArrayList<Component> filterComponents(Component[] components) {
            ArrayList<Component> componentsToReturn = new ArrayList<Component>(components.length);
            for (Component component : components) {
                if (!component.getType().equals(ServerComponentType.DCSF.getName())) {
                    componentsToReturn.add(component);
                }
            }
            
            return componentsToReturn;
        }
    }
}
