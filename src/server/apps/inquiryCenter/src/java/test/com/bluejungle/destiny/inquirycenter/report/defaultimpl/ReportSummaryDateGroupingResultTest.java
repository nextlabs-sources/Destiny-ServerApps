/*
 * Created on May 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import java.util.Date;

import com.bluejungle.destiny.inquirycenter.report.IReportSummaryResult;
import com.bluejungle.destiny.types.report_result.v1.SummaryResult;
import com.bluejungle.destiny.webui.tags.BaseJSFTest;

/**
 * This is the report summary result date grouping test class
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/ReportSummaryDateGroupingResultTest.java#1 $
 */

public class ReportSummaryDateGroupingResultTest extends BaseJSFTest {

    /**
     * This test verifies the basic aspects of the class
     */
    public void testReportSummaryDateGroupingResultClassBasics() {
        boolean exThrown = false;
        try {
            ReportSummaryResultDateGroupingImpl result = new ReportSummaryResultDateGroupingImpl(null);
        } catch (NullPointerException e) {
            exThrown = true;
        }
        assertTrue("The ReportSummaryResultDateGroupingImpl constructor should not accept null parameter", exThrown);
        ReportSummaryResultDateGroupingImpl result = new ReportSummaryResultDateGroupingImpl(new SummaryResult());
        assertTrue("ReportSummaryResultDateGroupingImpl should implement the right interface", result instanceof IReportSummaryResult);
        assertTrue("ReportSummaryResultDateGroupingImpl should extend the right class", result instanceof ReportSummaryResultImpl);
    }

    /**
     * This test verifies that the set / get properties are working
     */
    public void ReportSummaryDateGroupingResultProperties() {
        SummaryResult wsSummaryResult = new SummaryResult();
        final long count = 10876;
        wsSummaryResult.setCount(count);
        final Date now = new Date();
        final String value = (new Long(now.getTime())).toString();
        wsSummaryResult.setValue(value);
        ReportSummaryResultDateGroupingImpl result = new ReportSummaryResultDateGroupingImpl(wsSummaryResult);
        assertEquals("Count property should match", count, result.getCount());
        assertTrue("Date Value property should match", result.getValue() instanceof Date);
        assertEquals("Date Value property should match", now, result.getValue());
    }
}