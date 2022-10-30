/*
 * Created on Sep 17, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers;

import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemPossibleStyleClassIds;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/browsabledatapicker/defaultimpl/helpers/BaseDisableableSelectableItem.java#1 $
 */

public abstract class BaseDisableableSelectableItem implements IDisableableSelectableItem {

    private String styleClassId;
    private boolean isSelectable;

    /**
     * 
     * Create an instance of BaseDisableableSelectableItem
     *
     */
    public BaseDisableableSelectableItem() {
       this(ISelectableItemPossibleStyleClassIds.DEFAULT); 
    }
    
    /**
     * Create an instance of BaseDisableableSelectableItem
     * @param enabledStyleClassId
     */
    public BaseDisableableSelectableItem(String enabledStyleClassId) {
        if (enabledStyleClassId == null) {
            throw new NullPointerException("enabledStyleClassId cannot be null.");
        }
        
        this.styleClassId = enabledStyleClassId;
        this.isSelectable = true;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getStyleClassId()
     */
    public String getStyleClassId() {
        return this.styleClassId;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#isSelectable()
     */
    public boolean isSelectable() {
        return this.isSelectable;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.IDisableableSelectableItem#disable()
     */
    public void disable() {
        setStyleClassId(ISelectableItemPossibleStyleClassIds.DISABLED_STYLE_CLASS_ID);
        setSelectable(false);
    }

    /**
     * @param disabled_style_class_id
     */
    private void setStyleClassId(String styleClassId) {
        if (styleClassId == null) {
            throw new NullPointerException("styleClassId cannot be null.");
        }
    
        this.styleClassId = styleClassId;
    }

    /**
     * Set this host selectable item to be selectable or not selectable
     * 
     * @param isSelectable
     *            true to be selectable; false otherwise
     */
    private void setSelectable(boolean isSelectable) {
        this.isSelectable = isSelectable;
    }
}
