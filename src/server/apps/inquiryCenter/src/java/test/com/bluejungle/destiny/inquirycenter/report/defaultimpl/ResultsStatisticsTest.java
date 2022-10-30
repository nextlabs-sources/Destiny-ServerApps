/*
 * Created on May 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import com.bluejungle.destiny.inquirycenter.report.IResultsStatistics;
import com.bluejungle.destiny.inquirycenter.report.ISummaryResultsStatistics;
import com.bluejungle.framework.test.BaseDestinyTestCase;

/**
 * This is the test class for the result statistics
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/branch/Destiny_Beta2/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/ResultsStatisticsTest.java#1 $
 */

public class ResultsStatisticsTest extends BaseDestinyTestCase {

    /**
     * This test verifies the result statistics class
     */
    public void testResultStatisticsClassesBasics() {
        final long totalCount = 1000;
        final long availableCount = 10;
        ResultsStatisticsImpl stats = new ResultsStatisticsImpl(availableCount, totalCount);
        assertTrue(stats instanceof IResultsStatistics);
        assertEquals("Available row count shoult match", availableCount, stats.getAvailableRowCount());
        assertEquals("Total row count shoult match", totalCount, stats.getTotalRowCount());
    }

    /**
     * This test verifies the result statistics class
     */
    public void testResultSummaryStatisticsClassesBasics() {
        final long avaibleCount = 10;
        final long totalCount = 1000;
        final long minCount = 10;
        final long maxCount = 10;
        final long sumCount = 1045;
        SummaryResultsStatisticsImpl stats = new SummaryResultsStatisticsImpl(avaibleCount, totalCount, minCount, maxCount, sumCount);
        assertTrue(stats instanceof IResultsStatistics);
        assertTrue(stats instanceof ISummaryResultsStatistics);
        assertTrue(stats instanceof ResultsStatisticsImpl);
        assertEquals("Available row count should match", avaibleCount, stats.getAvailableRowCount());
        assertEquals("Total row count should match", totalCount, stats.getTotalRowCount());
        assertEquals("Min count should match", minCount, stats.getMinCount());
        assertEquals("Max count should match", maxCount, stats.getMaxCount());
        assertEquals("Max count should match", sumCount, stats.getTotalSummaryCount());
    }
}