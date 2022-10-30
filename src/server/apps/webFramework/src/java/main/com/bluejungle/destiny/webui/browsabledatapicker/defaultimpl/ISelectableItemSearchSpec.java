/*
 * Created on Mar 17, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl;

/**
 * Base interface for selectable item search spec instances
 * 
 * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec
 * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/browsabledatapicker/defaultimpl/ISelectableItemSearchSpec.java#1 $
 */

public interface ISelectableItemSearchSpec {

    /**
     * Retrieve the parameter specifying the maximum number of results to return
     * 
     * @return the parameter specifying the maximum number of results to return
     */
    public int getMaximumResultsToReturn();
}
