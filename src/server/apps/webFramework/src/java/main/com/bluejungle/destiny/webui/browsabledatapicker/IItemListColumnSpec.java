/*
 * Created on Jun 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker;

/**
 * An ItemListColumnSpec describes the content of a particular column in an item
 * list
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/browsabledatapicker/IItemListColumnSpec.java#1 $
 */

public interface IItemListColumnSpec {

    /**
     * The column header
     * 
     * @return the column header
     */
    public String getColumnHeader();

    /**
     * The id of a displayable property of an item to display. See
     * {@see IEnhancedSelectedItem}.
     * 
     * @return the id of a displayable property of an item to display.
     */
    public String getColumnDisplayablePropertyId();
}