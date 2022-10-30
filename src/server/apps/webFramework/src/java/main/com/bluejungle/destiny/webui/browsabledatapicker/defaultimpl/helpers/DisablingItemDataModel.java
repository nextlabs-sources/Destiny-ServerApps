/*
 * Created on May 17, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers;

import javax.faces.model.DataModel;

import com.bluejungle.destiny.webui.framework.data.ProxyingDataModel;

/**
 * This is an abstract class that supports disabling of selectable items.
 * 
 * @author safdar
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/browsabledatapicker/defaultimpl/helpers/DisablingItemDataModel.java#1 $
 */

public abstract class DisablingItemDataModel extends ProxyingDataModel {

    /**
     * Constructor
     * 
     * @param wrappedDataModel
     */
    public DisablingItemDataModel(DataModel wrappedDataModel) {
        super(wrappedDataModel);
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
     */
    protected Object proxyRowData(Object rawData) {
        IDisableableSelectableItem disableableSelectableItem = (IDisableableSelectableItem) rawData;
        if (shouldItemBeDisabled(disableableSelectableItem)) {
            disableableSelectableItem.disable();
        }
        return (Object) disableableSelectableItem;
    }

    /**
     * This method should be overridden by subclasses of this class to implement
     * the logic necessary to check if the given disableable item should be
     * disabled.
     * 
     * @param disableableItem
     * @return
     */
    protected abstract boolean shouldItemBeDisabled(IDisableableSelectableItem disableableItem);
}