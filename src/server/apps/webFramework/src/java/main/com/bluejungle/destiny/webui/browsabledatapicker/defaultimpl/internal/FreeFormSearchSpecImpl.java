/*
 * Created on May 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.internal;

import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;

/**
 * Default implementation of the IFreeFormSearchSpec interface
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/browsabledatapicker/defaultimpl/FreeFormSearchSpecImpl.java#2 $
 */

public class FreeFormSearchSpecImpl implements IFreeFormSearchSpec {

    private String freeFormSearchString;
    private int maximumResultsToDisplay = 0;
    
    /**
     * Create an instance of FreeFormSearchSpecImpl
     * 
     * @param freeFormSearchString
     */
    public FreeFormSearchSpecImpl(String freeFormSearchString, int maximumResultsToDisplay) {
        this.freeFormSearchString = freeFormSearchString;
        this.maximumResultsToDisplay = maximumResultsToDisplay;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec#getFreeFormSeachString()
     */
    public String getFreeFormSeachString() {
        return this.freeFormSearchString;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSearchSpec#getMaximumResultsToReturn()
     */
    public int getMaximumResultsToReturn() {
        return this.maximumResultsToDisplay;
    }   
}