/*
 * Created on May 12, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentstatus.defaultimpl;

import com.bluejungle.destiny.services.management.types.AgentDTOSortTermField;
import com.bluejungle.framework.patterns.EnumBase;

/**
 * @author safdar
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentstatus/defaultimpl/StatusByAgentSortColumnEnumType.java#1 $
 */

public class StatusByAgentSortColumnEnumType extends EnumBase {

    public static final StatusByAgentSortColumnEnumType HOST_COLUMN = new StatusByAgentSortColumnEnumType(AgentDTOSortTermField.HOST);
    public static final StatusByAgentSortColumnEnumType TYPE_COLUMN = new StatusByAgentSortColumnEnumType(AgentDTOSortTermField.TYPE);
    public static final StatusByAgentSortColumnEnumType LAST_HEARTBEAT_COLUMN = new StatusByAgentSortColumnEnumType(AgentDTOSortTermField.LAST_HEARTBEAT);
    public static final StatusByAgentSortColumnEnumType LAST_POLICY_UPDATE_COLUMN = new StatusByAgentSortColumnEnumType(AgentDTOSortTermField.LAST_POLICY_UPDATE);
    public static final StatusByAgentSortColumnEnumType PROFILE_COLUMN = new StatusByAgentSortColumnEnumType(AgentDTOSortTermField.PROFILE);

    private AgentDTOSortTermField sortSpecField;

    /**
     * Constructor
     * 
     * @param web-service-enum-type
     */
    public StatusByAgentSortColumnEnumType(AgentDTOSortTermField sortField) {
        super(sortField.getValue());
        this.sortSpecField = sortField;
    }

    /**
     * Returns the logical column name of the enum
     * 
     * @return logical column name
     */
    public String getLogicalName() {
        return super.getName();
    }

    /**
     * Returns the web-service enum type for this enum type
     * 
     * @return
     */
    public AgentDTOSortTermField getWSField() {
        return this.sortSpecField;
    }

    /**
     * Looks up a sort column enum type given the name
     * 
     * @param name
     * @return sort column enum
     */
    public static StatusByAgentSortColumnEnumType getByName(String name) {
        return EnumBase.getElement(name, StatusByAgentSortColumnEnumType.class);
    }

    /**
     * Returns whether an enum exists with the given name
     * 
     * @param name
     * @return
     */
    public static boolean doesExistByName(String name) {
        return existsElement(name, StatusByAgentSortColumnEnumType.class);
    }
}
