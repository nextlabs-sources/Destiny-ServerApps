/*
 * Created on May 11, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.browsablehostpicker;

import com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemPossibleStyleClassIds;
import com.bluejungle.dictionary.IMGroup;

/**
 * Implementation of ISelectableItem for a Host Group
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/browsablehostpicker/HostGroupSelectableItem.java#3 $
 */

class HostGroupSelectableItem implements ISelectableItem, Comparable {

    // ID's have a prefix to avoid conflict with host selectable item id's
    private static final String ID_PREFIX = "Host_Group_";

    private IMGroup wrappedGroup;
    private String id;

    /**
     * Create an instance of HostGroupSelectableItem
     * 
     * @param wrappedGroup
     */
    HostGroupSelectableItem(IMGroup wrappedGroup) {
        if (wrappedGroup == null) {
            throw new NullPointerException("wrappedGroup cannot be null.");
        }

        this.wrappedGroup = wrappedGroup;
        this.id = ID_PREFIX + this.wrappedGroup.getInternalKey().toString();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getId()
     */
    public String getId() {
        return this.id;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getStyleClassId()
     */
    public String getStyleClassId() {
        return ISelectableItemPossibleStyleClassIds.BOLD_STYLE_CLASS_ID;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getDisplayValue()
     */
    public String getDisplayValue() {
        return this.wrappedGroup.getDisplayName();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getDisplayValueToolTip()
     */
    public String getDisplayValueToolTip() {
        return this.wrappedGroup.getUniqueName();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#isSelectable()
     */
    public boolean isSelectable() {
        return true;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obectToCompare) {
        HostGroupSelectableItem hostGroupItemToCompare = (HostGroupSelectableItem) obectToCompare;

        return this.getDisplayValue().compareTo(hostGroupItemToCompare.getDisplayValue());
    }

    /**
     * Retrieve the wrapped LDAP Aggregate which this Host Group Selectable Item
     * represents
     * 
     * @return the wrapped LDAP Aggregate which this Host Group Selectable Item
     *         represents
     */
    IMGroup getWrappedGroup() {
        return this.wrappedGroup;
    }

}
