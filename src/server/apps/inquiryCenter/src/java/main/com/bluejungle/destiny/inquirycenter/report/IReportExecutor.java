/*
 * Created on May 3, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report;

import javax.faces.model.DataModel;

import com.bluejungle.destiny.types.report.v1.Report;

/**
 * Report execution bean interface
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/IReportExecutor.java#1 $
 */

public interface IReportExecutor {

    /**
     * Re-execute a report, based on a former definition
     * 
     * @return the outcome for the result page based on the report definition
     */
    public String executeReport();
    
    /**
     * Retrieves the log detail of the current row 
     * 
     * @return the log detail of the current row
     */
    public String getLogDetail();

    /**
     * Returns the report that is being executed
     * 
     * @return the report that is being executed
     */
    public IReport getReport();

    /**
     * Returns the name of the report that is being executed. The name could be
     * null in the case of an "ad hoc" execution
     * 
     * @return name of the report currently executed
     */
    public String getReportName();

    /**
     * Returns the list of results produced by the report execution. Based on
     * the report definition, these results can be either detail or summary
     * results.
     * 
     * @return a data model object wrapping around the results.
     */
    public DataModel getResults();

    /**
     * Returns the result statistics about the current report
     * 
     * @return the result statistics about the current report
     */
    public IResultsStatistics getResultsStatistics();

    /**
     * Sets the location of the back end web service to retrieve the data
     * 
     * @param location
     *            location of the web service
     */
    public void setDataLocation(String location);

    /**
     * Sets the new report that should be executed
     * 
     * @param wsReport
     *            report to execute.
     * @param uiName
     *            name of the report to execute
     */
    public void setReportToExecute(Report wsReport, String uiName);
    
    public void setMode(int i);
    
    public int getMode();
}
