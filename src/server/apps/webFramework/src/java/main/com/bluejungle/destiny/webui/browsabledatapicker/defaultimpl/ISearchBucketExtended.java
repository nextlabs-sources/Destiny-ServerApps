/*
 * Created on May 10, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl;

import com.bluejungle.destiny.webui.browsabledatapicker.ISearchBucket;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/browsabledatapicker/defaultimpl/ISearchBucketExtended.java#1 $
 */

public interface ISearchBucketExtended extends ISearchBucket {

    /**
     * Retrieve a search spec associated with this search bucket. This search
     * spec describes the search required for obtaining selectable items which
     * are contained with this search bucket
     * 
     * @return a search spec associated with this search bucket
     */
    public ISearchBucketSearchSpec getSeachSpec(int maximumItemsToReturn);
}
