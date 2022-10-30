/*
 * Created on Mar 13, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.data;

/**
 * This is the interface implemented by all the sortable data objects. It allows
 * the components or event listeners to interact with them in a uniform way.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/data/ISortableDataList.java#1 $
 */

public interface ISortableDataList extends IDataList {

    /**
     * Returns true if the default sort for the field is ascending, false
     * otherwise
     * 
     * @param fieldName
     *            name of the sortable field
     * @return true if the default sort is ascending, false otherwise
     */
    public boolean getDefaultSortAscending(String fieldName);

    /**
     * Returns true if the current sort is ascending, false otherwise.
     * 
     * @return true if the current sort is ascending, false otherwise.
     */
    public boolean getSortAscending();

    /**
     * Returns the name of the sorted field (if any) or null if no fields are
     * sorted.
     * 
     * @return the name of the sorted field (if any) or null if no fields are
     *         sorted.
     */
    public String getSortFieldName();

    /**
     * Sets the new sort direction.
     * 
     * @param newSort
     *            true if new sort should be ascending, false otherwise.
     */
    public void setSortAscending(boolean newSort);

    /**
     * Sets the name of the field to sort
     * 
     * @param newSortFieldName
     *            name of the field to sort
     */
    public void setSortFieldName(String newSortFieldName);
}