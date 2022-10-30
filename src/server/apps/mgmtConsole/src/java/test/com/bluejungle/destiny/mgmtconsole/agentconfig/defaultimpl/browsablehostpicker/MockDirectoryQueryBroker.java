/*
 * Created on May 13, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.browsablehostpicker;

import java.util.Set;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/browsablehostpicker/MockDirectoryQueryBroker.java#1 $
 */

public class MockDirectoryQueryBroker extends DirectoryQueryBroker {
    private Set enumGroups;
    private Set structuralGroups;

	public void init() {}
	
    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.browsablehostpicker.DirectoryQueryBroker#getEnumGroups()
     */
    Set getEnumGroups() {
        return this.enumGroups;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.browsablehostpicker.DirectoryQueryBroker#getStructuralGroups()
     */
    Set getStructuralGroups() {
        return this.structuralGroups;
    }
    /**
     * @param set
     */
    public void setEnumGroups(Set enumGroups) {
        this.enumGroups = enumGroups;
    }
    
    /**
     * @param set
     */
    public void setStructuralGroups(Set structuralGroups) {
        this.structuralGroups = structuralGroups;
    }
}
