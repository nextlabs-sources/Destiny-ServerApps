/*
 * Created on May 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl;

import java.util.Iterator;

/**
 * An interface to a list of ISelectedItem instances. All implementations must
 * allow indexed lookup by id in constant time (or as close as possible as
 * allowed by the hashcode() for String)
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/browsabledatapicker/ISelectedItemList.java#2 $
 */
public interface ISelectedItemList {

    /**
     * Determine if this list contained the Selected Item associated with the
     * specified id.
     * 
     * @param id
     *            the id of the associated Selected Item to query
     * @return true if the Selected Item exists within this list; false
     *         otherwise
     */
    public boolean containsSelectedItem(String id);

    /**
     * Retrieve an iterator to iterate over the contained selected items
     * 
     * @return an iterator to iterate over the contained selected items
     */
    public Iterator iterator();

    /**
     * Retrieve the size of the Selected Item list
     * 
     * @return the size of the selected item list
     */
    public int size();
}