/*
 * Created on May 11, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.browsableuserpicker;

import com.bluejungle.destiny.services.policy.types.SubjectDTO;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.BaseDisableableSelectableItem;
import com.bluejungle.destiny.webui.framework.data.IMemorizeableDataItem;

/**
 * Implementation of a Selectable Item for an available user
 * 
 * @author sgoldstein
 */

class AvailableUserSelectableItem extends BaseDisableableSelectableItem implements IMemorizeableDataItem {

    private SubjectDTO wrappedUserDTO;
    private String displayValue;
    
    /**
     * Create an instance of HostSelectableItem
     *  
     */
    AvailableUserSelectableItem(SubjectDTO wrappedUserDTO) {
        if (wrappedUserDTO == null) {
            throw new NullPointerException("wrappedUser cannot be null.");
        }

        this.wrappedUserDTO = wrappedUserDTO;
        this.displayValue = buildDisplayValue();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getId()
     */
    public String getId() {
        return this.wrappedUserDTO.getUniqueName();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getDisplayValue()
     */
    public String getDisplayValue() {
        return this.displayValue;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getDisplayValueToolTip()
     */
    public String getDisplayValueToolTip() {
        return this.wrappedUserDTO.getUniqueName().toLowerCase();
    }

    /**
     * Retrieve the wrapped User dto
     * 
     * @return the wrapped User dto
     */
    SubjectDTO getWrappedUser() {
        return this.wrappedUserDTO;
    }

    /**
     * Build the display value for this user selectable item
     */
    private String buildDisplayValue() {
        StringBuffer displayNameBuffer = new StringBuffer(this.wrappedUserDTO.getName());
        displayNameBuffer.append(" (");
        displayNameBuffer.append(this.wrappedUserDTO.getUniqueName().toLowerCase());
        displayNameBuffer.append(")");
        
        return displayNameBuffer.toString();
    }
}
