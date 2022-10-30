/*
 * Created on Jun 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl;

import java.util.ArrayList;
import java.util.List;

import com.bluejungle.destiny.webui.browsabledatapicker.IItemListColumnSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.IItemListDisplaySpec;

/**
 * Default implementation of the IItemListDisplaySpec interface
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/browsabledatapicker/defaultimpl/ItemListDisplaySpecImpl.java#1 $
 */

public class ItemListDisplaySpecImpl implements IItemListDisplaySpec {

    private List columnsSpec = new ArrayList();

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IItemListDisplaySpec#getColumnsSpec()
     */
    public List getColumnsSpec() {
        return this.columnsSpec;
    }

    /**
     * Add a column spec at the end of the column spec list
     * 
     * @param columnSpec
     *            the column spec to add
     */
    public void addColumnSpec(IItemListColumnSpec columnSpec) {
        if (columnSpec == null) {
            throw new NullPointerException("columnSpec cannot be null.");
        }
        
        this.columnsSpec.add(columnSpec);
    }
}