/*
 * Created on May 9, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.bluejungle.destiny.inquirycenter.report.IReportSummaryResult;
import com.bluejungle.destiny.types.report_result.v1.SummaryResult;

/**
 * This is the implementation of the report summary record record. This class
 * wraps around the web service object returned by the query execution. It also
 * exposes values for minimum and maximum occurence count so that histograms can
 * be displayed on the UI.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/ReportSummaryResultImpl.java#1 $
 */

class ReportSummaryResultImpl implements IReportSummaryResult {

    private SummaryResult wsSummaryResult;
    private static final int MAX_DISPLAY_SIZE = 100;

    /**
     * Constructor
     * 
     * @param wsResult
     *            result object returned by the web service
     * @param min
     *            minimum occurence count
     * @param max
     *            maximum occurence count
     */
    public ReportSummaryResultImpl(SummaryResult wsResult) {
        if (wsResult == null) {
            throw new NullPointerException("wsResult cannot be null");
        }
        this.wsSummaryResult = wsResult;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportSummaryResult#getCount()
     */
    public long getCount() {
        return this.wsSummaryResult.getCount();
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportSummaryResult#getValue()
     */
    public Object getValue() {
        String value = this.wsSummaryResult.getValue();
        String result = null;
        try {
            result = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            result = value;
        }
        return result;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportSummaryResult#getDisplayValue()
     */
    public Object getDisplayValue() {
        //FIX ME - May need proper HTML encoding here
        String result = this.wsSummaryResult.getValue();
        if (result.length() > MAX_DISPLAY_SIZE){
            String temp = result.substring(0, MAX_DISPLAY_SIZE) +
                          " " +
                          result.substring(MAX_DISPLAY_SIZE, result.length());
            result = temp;
        }
        return result;
    }
}