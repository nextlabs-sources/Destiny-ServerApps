/*
 * Created on Apr 8, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl;

import com.bluejungle.destiny.mgmtconsole.CommonConstants;
import com.bluejungle.destiny.framework.types.ServiceNotReadyFault;
import com.bluejungle.destiny.framework.types.UnauthorizedCallerFault;
import com.bluejungle.destiny.server.shared.registration.ServerComponentType;
import com.bluejungle.destiny.services.management.ComponentServiceIF;
import com.bluejungle.destiny.services.management.types.Component;
import com.bluejungle.destiny.services.management.types.ComponentHeartbeatInfo;
import com.bluejungle.destiny.services.management.types.ComponentHeartbeatUpdate;
import com.bluejungle.destiny.services.management.types.ComponentList;
import com.bluejungle.destiny.services.management.types.DCCRegistrationInformation;
import com.bluejungle.destiny.services.management.types.DCCRegistrationStatus;
import com.bluejungle.destiny.services.management.types.RegistrationFailedException;

import org.apache.axis.types.URI;

import javax.faces.model.DataModel;
import javax.xml.rpc.ServiceException;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import junit.framework.TestCase;

/**
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/ComponentStatusBeanTest.java#2 $
 */

public class ComponentStatusBeanTest extends TestCase {

	private static final Map<ServerComponentType, String> TYPE_TO_DISPLAY_TYPE_MAP = new HashMap<ServerComponentType, String>();
	
    private ComponentStatusBean beanToTest;
    private ComponentList testList;
    private Component[] testComponents;

	static {
		ResourceBundle bundle = ResourceBundle.getBundle(CommonConstants.MGMT_CONSOLE_BUNDLE_NAME);
        TYPE_TO_DISPLAY_TYPE_MAP.put(ServerComponentType.DABS, bundle.getString("server_component_type_dabs"));
        TYPE_TO_DISPLAY_TYPE_MAP.put(ServerComponentType.DAC, bundle.getString("server_component_type_dac"));
        TYPE_TO_DISPLAY_TYPE_MAP.put(ServerComponentType.DCSF, bundle.getString("server_component_type_dcsf"));
        TYPE_TO_DISPLAY_TYPE_MAP.put(ServerComponentType.DEM, bundle.getString("server_component_type_dem"));
        TYPE_TO_DISPLAY_TYPE_MAP.put(ServerComponentType.DMS, bundle.getString("server_component_type_dms"));
        TYPE_TO_DISPLAY_TYPE_MAP.put(ServerComponentType.DPS, bundle.getString("server_component_type_dps"));
        TYPE_TO_DISPLAY_TYPE_MAP.put(ServerComponentType.MGMT_CONSOLE, bundle.getString("server_component_type_mgmt"));
        TYPE_TO_DISPLAY_TYPE_MAP.put(ServerComponentType.REPORTER, bundle.getString("server_component_type_reporter"));
	}
	
    public static void main(String[] args) {
        junit.textui.TestRunner.run(ComponentStatusBeanTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        testComponents = new Component[3];

        Component dtoOne = new Component();
        dtoOne.setName("One");
        dtoOne.setType(ServerComponentType.DABS.getName());
        dtoOne.setLastHeartbeat(123456789);
        dtoOne.setComponentURL("http://www.one.com:80");
        testComponents[0] = dtoOne;

        Component dtoTwo = new Component();
        dtoTwo.setName("Two");
        dtoTwo.setType(ServerComponentType.DAC.getName());
        dtoTwo.setLastHeartbeat(55);
        dtoTwo.setComponentURL("http://www.two.com:80");
        testComponents[1] = dtoTwo;

        Component dtoThree = new Component();
        dtoThree.setName("Three (DCSF)");
        dtoThree.setType(ServerComponentType.DCSF.getName());
        dtoThree.setLastHeartbeat(55);
        dtoThree.setComponentURL("http://www.three.com:80");
        testComponents[2] = dtoThree;
        
        testList = new ComponentList();
        testList.setComp(testComponents);

        ComponentServiceIF componentService = new TestComponentServiceInterface(testList);
        beanToTest = new ExtendedComponentStatusBean(componentService);
    }

//    public void testGetComponentData() {
//        DataModel modelToCheck = beanToTest.getComponentData();
//        assertEquals("testGetComponentData - Ensure retrieved data row count is as expected", 2, modelToCheck.getRowCount());

//        modelToCheck.setRowIndex(0);
//        assertTrue("testGetComponentData - Ensure row 1 is available.", modelToCheck.isRowAvailable());
//        assertEquals("testGetComponentData - Ensure row 1 index is correct.", 0, modelToCheck.getRowIndex());
//        ComponentDataBean rowOneData = (ComponentDataBean) modelToCheck.getRowData();
//        assertEquals("testGetComponentData - Ensure row 1 component data name is as expected", testComponents[0].getName(), rowOneData.getComponentName());
//        assertEquals("testGetComponentData - Ensure row 1 component data type is as expected", TYPE_TO_DISPLAY_TYPE_MAP.get(testComponents[0].getType()), rowOneData.getComponentType());
//        assertEquals("testGetComponentData - Ensure row 1 component data last heart beat is as expected", testComponents[0].getLastHeartbeat(), rowOneData.getComponentLastHeartbeatTime().getTimeInMillis());
//        assertEquals("testGetComponentData - Ensure row 1 component data host name and port is as expected", testComponents[0].getComponentURL(), "http://" + rowOneData.getComponentHostName() + ":" + rowOneData.getComponentPort());

//        modelToCheck.setRowIndex(1);
//        assertTrue("testGetComponentData - Ensure row 2 is available.", modelToCheck.isRowAvailable());
//        assertEquals("testGetComponentData - Ensure row 2 index is correct.", 1, modelToCheck.getRowIndex());
//        ComponentDataBean rowTwoData = (ComponentDataBean) modelToCheck.getRowData();
//        assertEquals("testGetComponentData - Ensure row 2 component data name is as expected", testComponents[1].getName(), rowTwoData.getComponentName());
//        assertEquals("testGetComponentData - Ensure row 2 component data type is as expected", TYPE_TO_DISPLAY_TYPE_MAP.get(testComponents[1].getType()), rowTwoData.getComponentType());
//        assertEquals("testGetComponentData - Ensure row 2 component data last heart beat is as expected", testComponents[1].getLastHeartbeat(), rowTwoData.getComponentLastHeartbeatTime().getTimeInMillis());
//        assertEquals("testGetComponentData - Ensure row 2 component data host name and port is as expected", testComponents[1].getComponentURL(), "http://" + rowTwoData.getComponentHostName() + ":" + rowTwoData.getComponentPort());

//        modelToCheck.setRowIndex(2);
//        assertFalse("testGetComponentData - Ensure only two rows were available (DCSF was filtered)", modelToCheck.isRowAvailable());
//    }

    private class ExtendedComponentStatusBean extends ComponentStatusBean {

        private ComponentServiceIF componentService;

        public ExtendedComponentStatusBean(ComponentServiceIF componentService) {
            super();
            this.componentService = componentService;
        }

        /**
         * @see com.bluejungle.destiny.mgmtconsole.status.defaultimpl.ComponentStatusBean#getComponentService()
         */
        protected ComponentServiceIF getComponentServiceInterface() throws ServiceException {
            return this.componentService;
        }
    }

    private class TestComponentServiceInterface implements ComponentServiceIF {

        private ComponentList testList;

        public TestComponentServiceInterface(ComponentList testList) {
            super();
            this.testList = testList;
        }

        /**
         * @see com.bluejungle.destiny.services.management.ComponentServiceIF#checkUpdates(com.bluejungle.destiny.services.management.types.ComponentHeartbeatInfo)
         */
        public ComponentHeartbeatUpdate checkUpdates(ComponentHeartbeatInfo heartBeat) throws RemoteException, ServiceNotReadyFault, UnauthorizedCallerFault {
            return null;
        }

        /**
         * @see com.bluejungle.destiny.services.management.ComponentServiceIF#registerComponent(com.bluejungle.destiny.services.management.types.DCCRegistrationInformation)
         */
        public DCCRegistrationStatus registerComponent(DCCRegistrationInformation regData) throws RemoteException, ServiceNotReadyFault, UnauthorizedCallerFault, RegistrationFailedException {
            return null;
        }

        /**
         * @see com.bluejungle.destiny.services.management.ComponentServiceIF#unregisterComponent(com.bluejungle.destiny.services.management.types.DCCRegistrationInformation)
         */
        public void unregisterComponent(DCCRegistrationInformation unregInfo) throws RemoteException, ServiceNotReadyFault, UnauthorizedCallerFault, RegistrationFailedException {
        }

        /**
         * @see com.bluejungle.destiny.services.management.ComponentServiceIF#getComponents()
         */
        public ComponentList getComponents() throws RemoteException, ServiceNotReadyFault, UnauthorizedCallerFault {
            return this.testList;
        }

        /**
         * @see com.bluejungle.destiny.services.management.ComponentServiceIF#getComponents()
         */
        public ComponentList getComponentsByType(String type) throws RemoteException, ServiceNotReadyFault, UnauthorizedCallerFault {
            return null;
        }

        /**
         * @see com.bluejungle.destiny.services.management.ComponentServiceIF#registerEvent(java.lang.String,
         *      org.apache.axis.types.URI)
         */
        public void registerEvent(String eventName, URI callback) throws RemoteException, ServiceNotReadyFault, UnauthorizedCallerFault, RegistrationFailedException {
        }

        /**
         * @see com.bluejungle.destiny.services.management.ComponentServiceIF#unregisterEvent(java.lang.String,
         *      org.apache.axis.types.URI)
         */
        public void unregisterEvent(String eventName, URI callback) throws RemoteException, ServiceNotReadyFault, UnauthorizedCallerFault, RegistrationFailedException {
        }

    }
}
