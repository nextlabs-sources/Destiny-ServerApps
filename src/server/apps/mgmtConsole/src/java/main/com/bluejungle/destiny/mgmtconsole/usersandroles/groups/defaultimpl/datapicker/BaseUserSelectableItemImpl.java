/*
 * Created on Sep 19, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.datapicker;

import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.BaseDisableableSelectableItem;
import com.bluejungle.destiny.webui.framework.data.IMemorizeableDataItem;

/**
 * Base selectable item for user objects
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/groups/defaultimpl/datapicker/BaseUserSelectableItemImpl.java#1 $
 */

abstract class BaseUserSelectableItemImpl extends BaseDisableableSelectableItem implements IMemorizeableDataItem {

    private UserDTO wrappedUser;
    private String displayName;

    /**
     * Create an instance of BaseUserSelectableItemImpl
     * 
     * @param userDTO
     */
    public BaseUserSelectableItemImpl(UserDTO userDTO) {
        if (userDTO == null) {
            throw new NullPointerException("wrappedUser cannot be null.");
        }

        this.wrappedUser = userDTO;
        this.displayName = buildDisplayValue();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getId()
     */
    public String getId() {
        return this.wrappedUser.getId().toString();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getDisplayValue()
     */
    public String getDisplayValue() {
        return this.displayName;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getDisplayValueToolTip()
     */
    public String getDisplayValueToolTip() {
        return this.wrappedUser.getUniqueName().toLowerCase();
    }

    /**
     * @return
     */
    public UserDTO getWrappedUser() {
        return this.wrappedUser;
    }

    /**
     * Build the display value for this user selectable item
     */
    private String buildDisplayValue() {
        StringBuffer displayNameBuffer = new StringBuffer(this.wrappedUser.getLastName());
        displayNameBuffer.append(", ");
        displayNameBuffer.append(this.wrappedUser.getFirstName());
        displayNameBuffer.append(" (");
        displayNameBuffer.append(this.wrappedUser.getUniqueName().toLowerCase());
        displayNameBuffer.append(")");
        
        return displayNameBuffer.toString();
    }
}
