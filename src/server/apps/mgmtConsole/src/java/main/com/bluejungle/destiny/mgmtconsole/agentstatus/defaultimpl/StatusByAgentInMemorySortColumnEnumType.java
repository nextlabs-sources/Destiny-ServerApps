/*
 * Created on May 13, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentstatus.defaultimpl;

import com.bluejungle.framework.patterns.EnumBase;

/**
 * @author safdar
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentstatus/defaultimpl/StatusByAgentInMemorySortColumnEnumType.java#1 $
 */

public class StatusByAgentInMemorySortColumnEnumType extends EnumBase {

    public static final StatusByAgentInMemorySortColumnEnumType POLICY_UP_TO_DATE_COLUMN = new StatusByAgentInMemorySortColumnEnumType("POLICY_UP_TO_DATE");
    public static final StatusByAgentInMemorySortColumnEnumType MISSING_IN_LAST_24_HOURS_COLUMN = new StatusByAgentInMemorySortColumnEnumType("MISSING_IN_LAST_24_HOURS");

    /**
     * Constructor
     * 
     * @param arg0
     */
    public StatusByAgentInMemorySortColumnEnumType(String name) {
        super(name);
    }

    /**
     * Retrieves an enum by name
     * 
     * @param name
     * @return
     */
    public static StatusByAgentInMemorySortColumnEnumType getByName(String name) {
        return EnumBase.getElement(name, StatusByAgentInMemorySortColumnEnumType.class);
    }
}
