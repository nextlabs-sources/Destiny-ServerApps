/*
 * Created on May 11, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.browsablehostpicker;

import java.util.HashSet;
import java.util.Set;

import javax.faces.model.DataModel;

import com.bluejungle.destiny.services.management.types.AgentDTO;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemPossibleStyleClassIds;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList;
import com.bluejungle.destiny.webui.framework.data.ProxyingDataModel;

/**
 * DisablingDataModel is aa ProxyingDataModel which is reponsible for disabling
 * Host Selectable Items if the Host is already in the Selected Item list or if
 * the Host is already assigned the profile which is currently being edited
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/browsablehostpicker/DisablingItemDataModel.java#2 $
 */

class DisablingItemDataModel extends ProxyingDataModel {

    private ISelectedItemList selectedItems;
    private Set agentsAlreadySelected;

    /**
     * Create an instance of DisablingItemDataModel
     * 
     * @param wrappedDataModel
     * @param selectedItems
     * @param agentsAlreadySelected
     */
    DisablingItemDataModel(DataModel wrappedDataModel, ISelectedItemList selectedItems, AgentDTO[] agentsAlreadySelected) {
        super(wrappedDataModel);

        if (selectedItems == null) {
            throw new NullPointerException("selectedItems cannot be null.");
        }

        if (agentsAlreadySelected == null) {
            throw new NullPointerException("agentsAlreadySelected cannot be null.");
        }

        this.selectedItems = selectedItems;

        this.agentsAlreadySelected = new HashSet();
        for (int i = 0; i < agentsAlreadySelected.length; i++) {
            this.agentsAlreadySelected.add(agentsAlreadySelected[i]);
        }
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
     */
    protected Object proxyRowData(Object rawData) {
        HostSelectableItem hostSelectableItem = (HostSelectableItem) rawData;
        String selectableItemId = hostSelectableItem.getId();
        AgentDTO wrappedDTO = hostSelectableItem.getWrappedAgent();
        if (this.selectedItems.containsSelectedItem(selectableItemId) || (this.agentsAlreadySelected.contains(wrappedDTO))) {
            hostSelectableItem.setStyleClassId(ISelectableItemPossibleStyleClassIds.DISABLED_STYLE_CLASS_ID);
            hostSelectableItem.setSelectable(false);
        }

        return rawData;
    }
}