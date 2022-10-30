/*
 * Created on May 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.internal;

import java.util.Collection;

import javax.faces.model.DataModel;

import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList;

/**
 * Internal interface for a Selected Item list. Contains methods required for
 * internal use. These could be added to the extenal interface, but are kept
 * here to restrict access to the list to make system more robust. If
 * functionality is not needed, don't provide it at the expense of increasing
 * the possibility for bugs
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/browsabledatapicker/defaultimpl/internal/IInternalSelectedItemList.java#1 $
 */

public interface IInternalSelectedItemList extends ISelectedItemList {

    public void removeSelectedItem(String id);

    public void addAll(Collection selectedItemsToAdd);

    public DataModel getDataModel();

    /**
     * Clear the selected items
     */
    public void clear();
}