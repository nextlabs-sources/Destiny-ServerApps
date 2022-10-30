/*
 * Created on May 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import com.bluejungle.destiny.inquirycenter.report.IReportSummaryResult;
import com.bluejungle.destiny.types.report_result.v1.SummaryResult;
import com.bluejungle.destiny.webui.tags.BaseJSFTest;

/**
 * This is the report summary result test class
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/branch/Destiny_112/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/ReportSummaryResultTest.java#1 $
 */

public class ReportSummaryResultTest extends BaseJSFTest {

    /**
     * This test verifies the basic aspects of the class
     */
    public void testReportSummaryResultClassBasics() {
        boolean exThrown = false;
        try {
            ReportSummaryResultImpl result = new ReportSummaryResultImpl(null);
        } catch (NullPointerException e) {
            exThrown = true;
        }
        assertTrue("The ReportSummaryResultImpl constructor should not accept null parameter", exThrown);
        ReportSummaryResultImpl result = new ReportSummaryResultImpl(new SummaryResult());
        assertTrue("ReportSummaryResultImpl should implement the right interface", result instanceof IReportSummaryResult);
    }

    /**
     * This test verifies that the set / get properties are working
     */
    public void testReportSummaryResultProperties() {
        SummaryResult wsSummaryResult = new SummaryResult();
        final long count = 10876;
        wsSummaryResult.setCount(count);
        final String value = "myValue";
        wsSummaryResult.setValue(value);
        ReportSummaryResultImpl result = new ReportSummaryResultImpl(wsSummaryResult);
        assertEquals("Count property should match", count, result.getCount());
        assertEquals("Value property should match", value, result.getValue());
    }

    /**
     * This test verifies that the bean exposes API that return HTML encoded
     * values.
     */
    public void testReportSummaryResultEncodingForHTML() {
        SummaryResult wsSummaryResult = new SummaryResult();
        final long count = 10876;
        wsSummaryResult.setCount(count);
        final String value = "my value with space";
        wsSummaryResult.setValue(value);
        ReportSummaryResultImpl result = new ReportSummaryResultImpl(wsSummaryResult);
        assertEquals("my+value+with+space", result.getValue());
    }
}