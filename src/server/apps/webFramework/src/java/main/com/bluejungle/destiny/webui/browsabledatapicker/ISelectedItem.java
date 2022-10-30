/*
 * Created on May 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker;

/**
 * A representation of a Selected Item in the Browsable Data Picker view.
 * 
 * @author sgoldstein
 */
public interface ISelectedItem {

    /**
     * Retrieve the id of this selected item
     * 
     * @return the id of this selected item
     */
    public abstract String getId();

    /**
     * Retrieve the display value for this selected item
     * 
     * @return the display value for this selected item
     */
    public String getDisplayValue();

    /**
     * Retrieve the display value tool-tip associated with this selectable item
     * 
     * @return the display value tool-tip associated with this selectable item
     */
    //public String getDisplayValueToolTip();
}

