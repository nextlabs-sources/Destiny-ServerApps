/*
 * Created on May 9, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import com.bluejungle.destiny.inquirycenter.report.IResultsStatistics;

/**
 * @author ihanen
 * @version $Id:
 *          //depot/branch/Destiny_Beta2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/ResultsStatisticsImpl.java#1 $
 */

public class ResultsStatisticsImpl implements IResultsStatistics {

    private long availableRowCount;
    private long totalRowCount;

    /**
     * 
     * Constructor
     * 
     * @param newAvailableRowCount
     *            available row count
     * @param newTotalRowCount
     *            total number of rows in the result set
     */
    public ResultsStatisticsImpl(long newAvailableRowCount, long newTotalRowCount) {
        this.availableRowCount = newAvailableRowCount;
        this.totalRowCount = newTotalRowCount;

    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IResultsStatistics#getAvailableRowCount()
     */
    public long getAvailableRowCount() {
        return this.availableRowCount;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IResultsStatistics#getTotalRowCount()
     */
    public long getTotalRowCount() {
        return this.totalRowCount;
    }
}