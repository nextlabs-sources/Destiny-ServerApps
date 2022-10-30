/*
 * Created on Mar 4, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole;

import java.util.HashSet;
import java.util.Set;

import com.bluejungle.destiny.container.dcc.BaseDCCComponentTestCase;
import com.bluejungle.framework.configuration.DestinyRepository;

/**
 * Base Test case for Management console components
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/BaseMgmtConsoleComponentTestCase.java#1 $
 */
public abstract class BaseMgmtConsoleComponentTestCase extends BaseDCCComponentTestCase {
    
    public static final String COMPONENT_NAME = "mgmtConsole";
    private static final String COMPONENT_RELATIVE_HOME_PATH = "/server/apps/";
    public static final Set REQUIRED_DATA_REPOSITORIES = new HashSet();    
    static {        
        REQUIRED_DATA_REPOSITORIES.add(DestinyRepository.ACTIVITY_REPOSITORY);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(BaseMgmtConsoleComponentTestCase.class);
    }
    
    /**
     * @see com.bluejungle.destiny.container.dcc.test.BaseDCCComponentTestCase#getComponentRelativeHomePath()
     */
    protected String getComponentRelativeHomePath() {
        return COMPONENT_RELATIVE_HOME_PATH;
    }
    
    /**
     * @see com.bluejungle.destiny.container.dcc.test.BaseDCCComponentTestCase#getComponentName()
     */
    protected String getComponentName() {
        return COMPONENT_NAME;
    }
    
    
    /**
     * @see com.bluejungle.destiny.container.dcc.test.BaseDCCComponentTestCase#getDataRepositories()
     */
    protected Set getDataRepositories() {
        return REQUIRED_DATA_REPOSITORIES;
    }
}
