/*
 * Created on May 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker;

import java.util.Map;

/**
 * An IEnhancedSelectedItem is an extension of ISelectedItem that provides
 * properties about the selected item. These properties can be used to display
 * an enhanced selected item list with multiple columns. Please see the Agent
 * Configuration Browse Hosts implementation for an example
 * 
 * @author sgoldstein
 */
public abstract interface IEnhancedSelectedItem extends ISelectedItem {

    /**
     * Retrieve the list of properties to display in the selected items list for
     * this selected item
     * 
     * @return the list of properties to display in the selected items list for
     *         this selected item
     */
    public Map getDisplayableProperties();
}

