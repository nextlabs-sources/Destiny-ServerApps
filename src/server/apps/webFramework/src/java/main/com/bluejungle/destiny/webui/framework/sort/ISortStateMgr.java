/*
 * Created on May 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.sort;

/**
 * The sort state manager allows tracking the state of the sort in a list
 * control. It provides information about the current sorting state, and can
 * tell whether the state has changed between two <code>saveState</code>
 * calls. Typically, the caller will set a sort specification, save it with
 * <code>saveState</code>, and call the <code>isSortStateChanged</code> API
 * to see if anything changed the state of the sort since last time it was
 * saved.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/sort/ISortStateMgr.java#1 $
 */

public interface ISortStateMgr {

    /**
     * Returns the name of the currently sorted field, or null if no field is
     * currently sorted
     * 
     * @return the name of the currently sorted field
     */
    public String getSortFieldName();

    /**
     * Returns if the currently sorted field is sorted in ascending or
     * descending order
     * 
     * @return true if the field is sorted in ascending order, false otherwise
     */
    public boolean isSortAscending();

    /**
     * Returns true if the sort state has changed since the last call to
     * <code>saveState</code>.
     * 
     * @return true if the sort state has changed, false otherwise
     */
    public boolean isSortStateChanged();

    /**
     * Saves the current sort state. If the current sort state has changed since
     * the last time the <code>saveState</code> function was called, then the
     * <code>isSortStateChanged</code> function will return true.
     *  
     */
    public void saveState();

    /**
     * Sets the name of the field to sort on
     * 
     * @param newName
     *            field name to set
     */
    public void setSortFieldName(String newName);

    /**
     * Sets the sorting direction (ascending or descending)
     * 
     * @param ascending
     *            true if the sort should be ascending, false if the sorting
     *            should be descending.
     */
    public void setSortAscending(boolean ascending);
}