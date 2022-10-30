/*
 * Created on May 9, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report;

/**
 * This interface exposes statistics for the results that have been fetched by
 * the report executor. Typically, a query has "totalRowCount" number of
 * matching rows, but it may only have "availableRowCount" rows available for
 * display.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/branch/Destiny_Beta2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/IResultsStatistics.java#1 $
 */

public interface IResultsStatistics {

    /**
     * Returns the number of rows that can be displayed in the query results.
     * 
     * @return the number of rows that can be displayed in the query results.
     */
    public long getAvailableRowCount();

    /**
     * Returns the total number of matching rows
     * 
     * @return the total number of matching rows
     */
    public long getTotalRowCount();
}