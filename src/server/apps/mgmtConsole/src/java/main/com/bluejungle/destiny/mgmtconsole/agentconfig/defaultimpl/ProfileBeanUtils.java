/*
 * Created on Apr 24, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import com.bluejungle.destiny.container.dcc.IDCCContainer;
import com.bluejungle.destiny.server.shared.registration.ServerComponentType;
import com.bluejungle.destiny.services.management.CommitFault;
import com.bluejungle.destiny.services.management.ComponentServiceStub;
import com.bluejungle.destiny.services.management.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.UnauthorizedCallerFault;
import com.bluejungle.destiny.services.management.types.Component;
import com.bluejungle.destiny.services.management.types.ComponentList;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IConfiguration;

import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.types.URI;
import org.apache.axis2.databinding.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.model.SelectItem;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility method for use within the agent configuration bean implementation
 * classes. Add methods with reluctance
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/ProfileBeanUtils.java#1 $
 */

class ProfileBeanUtils {

    static final String DEFAULT_ERROR_URL = "http://";

    private static final Log LOG = LogFactory.getLog(ProfileBeanUtils.class.getName());
    private static final String COMPONENT_SERVICE_LOCATION_SERVLET_PATH = "/services/ComponentService";
    private static ComponentServiceStub componentService;

    /**
     * Translate an Axis URI to a java.net.URL
     * 
     * @param location
     *            the URI to translate
     * @return the translated java.net.URL
     */
    static URL getURLFromAxisURI(URI location) {
        URL urlToReturn = null;
        try {
            urlToReturn = new URL(location.toString());
        } catch (MalformedURLException exception) {
            getLog().warn("Invalid URI found: " + location.toString(), exception);
            try {
                urlToReturn = new URL(DEFAULT_ERROR_URL);
            } catch (MalformedURLException neverHappenException) {
            }
        }

        return urlToReturn;
    }

    /**
     * Translate a java.net.URL to an Axis URI
     * 
     * @param location
     *            the URL to translate
     * @return the translate Axis URI
     */
    static URI getAxisURIFromURL(URL location) {
        URI uriToReturn = null;
        try {
            uriToReturn = new URI(location.toString());
        } catch (MalformedURIException exception) {
            getLog().warn("Validation failed.  Invalid URL set: " + location.toString(), exception);
            try {
                uriToReturn = new URI(DEFAULT_ERROR_URL);
            } catch (MalformedURIException neverHappenException) {
            }
        }

        return uriToReturn;
    }

    /**
     * Retrieve a List of SelectItem bean instances containing the DABS
     * component URLs
     * 
     * @return a List of SelectItem bean instances containing the DABS
     *         component URLs
     * @throws ServiceException 
     * @throws RemoteException 
     * @throws UnauthorizedCallerFault 
     * @throws ServiceNotReadyFault 
     */
    static List getDABSComponentsURLs() throws ServiceNotReadyFault, UnauthorizedCallerFault, CommitFault, RemoteException {
        List<SelectItem> listToReturn = new LinkedList<SelectItem>();
        
        ComponentServiceStub componentService = getComponentService();
        ComponentList dabsComponentList = componentService.getComponentsByType(ServerComponentType.DABS.getName());
        Component[] dabsComponents = dabsComponentList.getComp();
        if (dabsComponents != null) {
            for (Component dabsComponent : dabsComponents) {
                String nextDABSComponentURL = dabsComponent.getComponentURL();
                try {
                    SelectItem selectItemToAdd = new SelectItem(new URL(nextDABSComponentURL), nextDABSComponentURL);
                    listToReturn.add(selectItemToAdd);
                } catch (MalformedURLException exception) {
                    getLog().warn("DABS component registered with invalid URL, " + nextDABSComponentURL, exception);
                }
            }
        }
        
        return listToReturn;
    }

    /**
     * Retrieve the Component Service interface.
     * 
     * @return the Component Service interface
     * @throws ServiceException
     * @throws ServiceException
     *             if the component service interface could not be located
     */
    private static ComponentServiceStub getComponentService() throws AxisFault {
        if (componentService == null) {
            IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
            IConfiguration mainConfig = (IConfiguration) compMgr.getComponent(IDCCContainer.MAIN_COMPONENT_CONFIG_COMP_NAME);
            String location = (String) mainConfig.get(IDCCContainer.DMS_LOCATION_CONFIG_PARAM);
            location += COMPONENT_SERVICE_LOCATION_SERVLET_PATH;

            componentService = new ComponentServiceStub(location);
        }

        return componentService;
    }

    /**
     * @return
     */
    private static Log getLog() {
        return LOG;
    }
}
