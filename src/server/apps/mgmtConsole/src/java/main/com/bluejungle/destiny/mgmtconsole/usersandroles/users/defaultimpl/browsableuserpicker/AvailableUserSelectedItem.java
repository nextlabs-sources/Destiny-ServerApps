/*
 * Created on May 11, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.browsableuserpicker;

import com.bluejungle.destiny.services.policy.types.SubjectDTO;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem;

/**
 * An implementation of ISelectedItem for a Host
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/browsablehostpicker/HostSelectedItem.java#2 $
 */

public class AvailableUserSelectedItem implements ISelectedItem {
    
    private SubjectDTO wrappedUserDTO;

    /**
     * Create an instance of HostSelectedItem
     *  
     */
    public AvailableUserSelectedItem(SubjectDTO wrappedUserDTO) {
        if (wrappedUserDTO == null) {
            throw new NullPointerException("wrappedDTO cannot be null.");
        }

        this.wrappedUserDTO = wrappedUserDTO;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem#getId()
     */
    public String getId() {
        return this.wrappedUserDTO.getUniqueName();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem#getDisplayValue()
     */
    public String getDisplayValue() {
        return this.wrappedUserDTO.getName();
    }

    /**
     * Retrieve the wrapped AgentDTO
     * 
     * @return the wrapped AgentDTO
     */
    SubjectDTO getwrappedUser() {
        return this.wrappedUserDTO;
    }
}