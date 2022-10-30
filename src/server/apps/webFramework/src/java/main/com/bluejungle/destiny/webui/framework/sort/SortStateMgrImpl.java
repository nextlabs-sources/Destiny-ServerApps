/*
 * Created on May 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.sort;

/**
 * This is the sort state manager implementation class. This implementation
 * keeps track of the last sort state only, and compares the last known state to
 * the current state.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/sort/SortStateMgrImpl.java#1 $
 */

public class SortStateMgrImpl implements ISortStateMgr {

    private boolean currentAscending = false;
    private String currentSortFieldName;
    private boolean lastAscending = false;
    private String lastSortFieldName;
    private boolean isChanged = false;

    /**
     * This function performs the comparison and calculates if the value have
     * changed compared to the previous state.
     * 
     * @return true if the state has change, false otherwise
     */
    private boolean calculateStateChanged() {
        return ((this.currentAscending != this.lastAscending) || (this.currentSortFieldName != this.lastSortFieldName));
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.sort.ISortStateMgr#getSortFieldName()
     */
    public String getSortFieldName() {
        return this.currentSortFieldName;
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.sort.ISortStateMgr#isSortAscending()
     */
    public boolean isSortAscending() {
        return this.currentAscending;
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.sort.ISortStateMgr#isSortStateChanged()
     */
    public boolean isSortStateChanged() {
        return this.isChanged;
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.sort.ISortStateMgr#saveState()
     */
    public void saveState() {
        this.isChanged = calculateStateChanged();
        this.lastAscending = this.currentAscending;
        this.lastSortFieldName = this.currentSortFieldName;
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.sort.ISortStateMgr#setSortFieldName(java.lang.String)
     */
    public void setSortFieldName(String newName) {
        this.currentSortFieldName = newName;
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.sort.ISortStateMgr#setSortAscending(boolean)
     */
    public void setSortAscending(boolean ascending) {
        this.currentAscending = ascending;
    }
}