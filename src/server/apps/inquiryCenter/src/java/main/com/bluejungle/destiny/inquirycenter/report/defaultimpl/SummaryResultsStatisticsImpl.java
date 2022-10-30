/*
 * Created on May 9, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import com.bluejungle.destiny.inquirycenter.report.ISummaryResultsStatistics;

/**
 * This class is the implementation for the statistics related to the report
 * summary results. This class is instanciated by the report executor and
 * contains statistics about the report execution.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/branch/Destiny_Beta2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/SummaryResultsStatisticsImpl.java#1 $
 */

public class SummaryResultsStatisticsImpl extends ResultsStatisticsImpl implements ISummaryResultsStatistics {

    private long maxCount;
    private long minCount;
    private long totalSummaryCount;

    /**
     * Constructor
     * 
     * @param availableRowCount
     *            total number of rows available for display
     * @param totalRowCount
     *            total number of rows returned by the query
     * @param minCount
     *            minimum value occurence count
     * @param maxCount
     *            maximum value occurent count
     * @param totalSummaryCount
     *            total count accross all value occurences
     */
    public SummaryResultsStatisticsImpl(long availableRowCount, long totalRowCount, long minCount, long maxCount, long totalSummaryCount) {
        super(availableRowCount, totalRowCount);
        this.maxCount = maxCount;
        this.minCount = minCount;
        this.totalSummaryCount = totalSummaryCount;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.ISummaryResultsStatistics#getMaxCount()
     */
    public long getMaxCount() {
        return this.maxCount;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.ISummaryResultsStatistics#getMinCount()
     */
    public long getMinCount() {
        return this.minCount;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.ISummaryResultsStatistics#getTotalSummaryCount()
     */
    public long getTotalSummaryCount() {
        return this.totalSummaryCount;
    }

}