/*
 * Created on May 9, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report;

/**
 * The report summary result interface is implemented by each report summary
 * result object. It contains the values for a summary result record.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/IReportSummaryResult.java#1 $
 */

public interface IReportSummaryResult {

    /**
     * Returns the number of occurences of the value
     * 
     * @return the number of occurences of the value
     */
    public long getCount();

    /**
     * Returns the value for the current summary result. The value depends on
     * the grouping type (can be a data, a username, a policy name, etc.) and is
     * for internal use.
     * 
     * @return the value (in its internal form) for the current summary result.
     */
    public Object getValue();

    /**
     * Returns the value for the current summary result. The value depends on
     * the grouping type (can be a data, a username, a policy name, etc.)
     * 
     * @return the value for the current summary result.
     */
    public Object getDisplayValue();
}