/*
 * Created on Sep 19, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.datapicker;

import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem;

/**
 * Base Selected Item implementation for users
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/groups/defaultimpl/datapicker/BaseUserSelectedItemImpl.java#1 $
 */

abstract class BaseUserSelectedItemImpl implements ISelectedItem {

    private UserDTO wrappedUser;
    private String displayName;

    BaseUserSelectedItemImpl(UserDTO userDTO) {
        if (userDTO == null) {
            throw new NullPointerException("wrappedUser cannot be null.");
        }

        this.wrappedUser = userDTO;
        StringBuffer displayNameBuffer = new StringBuffer(this.wrappedUser.getLastName());
        displayNameBuffer.append(", ");
        displayNameBuffer.append(this.wrappedUser.getFirstName());
        this.displayName = displayNameBuffer.toString();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem#getId()
     */
    public String getId() {
        return this.wrappedUser.getId().toString();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem#getDisplayValue()
     */
    public String getDisplayValue() {
        return this.displayName;
    }

    /**
     * @return
     */
    public UserDTO getWrappedUser() {
        return this.wrappedUser;
    }
}