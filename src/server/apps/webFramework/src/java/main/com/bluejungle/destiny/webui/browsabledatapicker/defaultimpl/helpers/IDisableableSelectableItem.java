/*
 * Created on May 17, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers;

import com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem;

/**
 * @author safdar
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/browsabledatapicker/defaultimpl/helpers/IDisableableSelectableItem.java#1 $
 */

public interface IDisableableSelectableItem extends ISelectableItem {

    /**
     * This method should disable this item from being selectable. The expected
     * result is that the isSelectable() method should return false.
     */
    public void disable();
}