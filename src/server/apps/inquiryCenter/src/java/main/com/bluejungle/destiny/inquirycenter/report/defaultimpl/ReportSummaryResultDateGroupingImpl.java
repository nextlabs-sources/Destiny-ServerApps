/*
 * Created on May 10, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import java.util.Date;

import com.bluejungle.destiny.types.report_result.v1.SummaryResult;

/**
 * This class is a specific implementation for the display of results grouped by
 * days. This class allows displaying the day number instead of the
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/ReportSummaryResultDateGroupingImpl.java#1 $
 */

public class ReportSummaryResultDateGroupingImpl extends ReportSummaryResultImpl {

    /**
     * Constructor
     * 
     * @param wsResult
     */
    public ReportSummaryResultDateGroupingImpl(SummaryResult wsResult) {
        super(wsResult);
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportSummaryResult#getValue()
     */
    public Object getValue() {
        String sValue = (String) super.getValue();
        long millis = (new Long(sValue)).longValue();
        return new Long(millis);
    }

    /**
     * Returns a date object
     * 
     * @see com.bluejungle.destiny.inquirycenter.report.IReportSummaryResult#getValue()
     */
    public Object getDisplayValue() {
        String sValue = (String) super.getValue();
        long millis = (new Long(sValue)).longValue();
        Date dValue = new Date(millis);
        return dValue;
    }
}