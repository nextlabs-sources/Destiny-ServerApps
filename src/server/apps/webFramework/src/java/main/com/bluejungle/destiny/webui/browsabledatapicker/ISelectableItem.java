/*
 * Created on May 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker;

/**
 * ISelectableItem represents an individual item which may be chosen within the
 * browsable data picker view. Note that this isn't necessarily the same as the
 * associated selected items. For instance, a selectable item may be a group
 * which is expanded into its children when selected
 * 
 * @author sgoldstein
 */
public interface ISelectableItem {

    /**
     * Retrieve the id associated with this selectable item
     * 
     * @return the id associated with this selectable item
     */
    public String getId();

    /**
     * Retrieve the style class id associated with this selectable item. This id
     * can be used by the display layer to display the selectable item in a
     * particular css style
     * 
     * @return the style class id associated with this selectable item
     */
    public String getStyleClassId();

    /**
     * Retrieve the display value associated with this selectable item
     * 
     * @return the display value associated with this selectable item
     */
    public String getDisplayValue();

    /**
     * Retrieve the display value tool-tip associated with this selectable item
     * 
     * @return the display value tool-tip associated with this selectable item
     */
    public String getDisplayValueToolTip();

    /**
     * Determine if this Selectable item is actually Selectable. Used for
     * displaying selectable items as disabled links
     * 
     * @return true if selectable; false otherwise
     */
    public boolean isSelectable();
}

