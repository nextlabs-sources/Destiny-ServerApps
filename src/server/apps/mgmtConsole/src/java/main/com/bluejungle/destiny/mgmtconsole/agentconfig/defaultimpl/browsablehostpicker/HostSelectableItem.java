/*
 * Created on May 11, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.browsablehostpicker;

import com.bluejungle.destiny.services.management.types.AgentDTO;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemPossibleStyleClassIds;

/**
 * Implementation of a Selectable Item for a host
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/browsablehostpicker/HostSelectableItem.java#1 $
 */

class HostSelectableItem implements ISelectableItem {

    private AgentDTO wrappedAgentDTO;
    private String styleClassId;
    private boolean isSelectable;

    /**
     * Create an instance of HostSelectableItem
     *  
     */
    HostSelectableItem(AgentDTO wrappedAgentDTO) {
        if (wrappedAgentDTO == null) {
            throw new NullPointerException("wrappedAgent cannot be null.");
        }

        this.wrappedAgentDTO = wrappedAgentDTO;
        this.styleClassId = ISelectableItemPossibleStyleClassIds.DEFAULT;
        this.isSelectable = true;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getId()
     */
    public String getId() {
        return this.wrappedAgentDTO.getId().toString();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getStyleClassId()
     */
    public String getStyleClassId() {
        return this.styleClassId;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getDisplayValue()
     */
    public String getDisplayValue() {
        return this.wrappedAgentDTO.getHost();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getDisplayValueToolTip()
     */
    public String getDisplayValueToolTip() {
        return getDisplayValue();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#isSelectable()
     */
    public boolean isSelectable() {
        return this.isSelectable;
    }

    /**
     * @param disabled_style_class_id
     */
    void setStyleClassId(String styleClassId) {
        if (styleClassId == null) {
            throw new NullPointerException("styleClassId cannot be null.");
        }

        this.styleClassId = styleClassId;
    }

    /**
     * Retrieve the wrapped AgentDTO
     * 
     * @return the wrapped AgentDTO
     */
    AgentDTO getWrappedAgent() {
        return this.wrappedAgentDTO;
    }

    /**
     * Set this host selectable item to be selectable or not selectable
     * 
     * @param isSelectable
     *            true to be selectable; false otherwise
     */
    void setSelectable(boolean isSelectable) {
        this.isSelectable = isSelectable;
    }
}