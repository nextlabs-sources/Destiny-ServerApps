/*
 * Created on Apr 8, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl;

import java.util.Calendar;

import com.bluejungle.destiny.server.shared.registration.ServerComponentType;
import com.bluejungle.destiny.services.management.types.Component;

import junit.framework.TestCase;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/ComponentDataBeanTest.java#1 $
 */

public class ComponentDataBeanTest extends TestCase {
    private static final String TEST_COMPONENT_NAME = "foo_comp";
    private static final ServerComponentType TEST_COMPONENT_TYPE = ServerComponentType.DABS;
    private static final String TEST_COMPONENT_HOST = "foo_host";
    private static final int TEST_COMPONENT_PORT = 55;
    private static final Calendar TEST_COMPONENT_LAST_HEARTBEAT_TIME = Calendar.getInstance();
    private static final int TEST_COMPONENT_HEART_BEAT_RATE = 3600;
    
    private Component testDTO;
    private ComponentDataBean dataBeanToTest;
        
    public static void main(String[] args) {
        junit.textui.TestRunner.run(ComponentDataBeanTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        testDTO = new Component();
        testDTO.setName(TEST_COMPONENT_NAME);
        testDTO.setType(TEST_COMPONENT_TYPE.getName());
        testDTO.setComponentURL("http://" + TEST_COMPONENT_HOST + ":" + TEST_COMPONENT_PORT);
        testDTO.setLastHeartbeat(TEST_COMPONENT_LAST_HEARTBEAT_TIME.getTimeInMillis());
        testDTO.setHeartbeatRate(TEST_COMPONENT_HEART_BEAT_RATE);
        testDTO.setActive(true);
        dataBeanToTest = new ComponentDataBean(testDTO);
    }

    public void testGetComponentName() {
        assertEquals("testGetComponentName - Ensure component name is as expected.", TEST_COMPONENT_NAME, dataBeanToTest.getComponentName());
    }

    public void testGetComponentType() {
        assertEquals("testGetComponentType- Ensure component type is as expected.", "ICENet Server", dataBeanToTest.getComponentType());
    }

    public void testGetHostName() {
        assertEquals("testGetHostName - Ensure component host is as expected.", TEST_COMPONENT_HOST, dataBeanToTest.getComponentHostName());
    }

    public void testGetPort() {
        assertEquals("testGetPort - Ensure component port is as expected.", TEST_COMPONENT_PORT, dataBeanToTest.getComponentPort());
    }

    public void testGetLastHeartbeatTime() {
        assertEquals("testGetLastHeartbeatTime - Ensure component last heart beat time is as expected.", TEST_COMPONENT_LAST_HEARTBEAT_TIME, dataBeanToTest.getComponentLastHeartbeatTime());
    }

    public void testGetExpectedHeartbeatTime() {
        Calendar expectedExpectedHeartBeatRate = (Calendar) TEST_COMPONENT_LAST_HEARTBEAT_TIME.clone();
        expectedExpectedHeartBeatRate.add(Calendar.SECOND, TEST_COMPONENT_HEART_BEAT_RATE);
        assertEquals("testGetExpectedHeartbeatTime - Ensure component expected heart beat time is as expected.", expectedExpectedHeartBeatRate, dataBeanToTest.getComponentExpectedHeartbeatTime());
    }
    
    public void testIsActive() {
        assertTrue("testIsActive - Ensure component active is as expected.", dataBeanToTest.isActive());        
    }
}
