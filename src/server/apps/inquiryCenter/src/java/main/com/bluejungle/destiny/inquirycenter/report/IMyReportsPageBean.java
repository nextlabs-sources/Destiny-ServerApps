/*
 * Created on Jun 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report;

import org.apache.axis2.AxisFault;

/**
 * IMyReportsPageBean is utilized by the display layer to obtain information
 * necessary to render the My Reports view
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/IMyReportsPageBean.java#1 $
 */
public interface IMyReportsPageBean extends IReportPageBean {

    /**
     * Deletes selected report
     */
    public void deleteSelectedReport() throws ReportViewException, AxisFault;

    /**
     * This API is called by the action listener when a report from the list is
     * edited and saved
     * 
     * @throws ReportViewException
     *             if the report update fails
     */
    public void updateSelectedReport() throws ReportViewException, AxisFault;

    /**
     * Cancel any changes made to the selected report
     *
     */
    public void cancelSelectedReportEdit();
    
    /**
     * Creates a new quick report
     */
    public void createNewQuickReport();

    /**
     * Insert the selected report into the database using the specified
     * insertion information
     * 
     * @throws ReportViewException
     *             if the report insertion fails
     */
    public void insertSelectedReport() throws ReportViewException, AxisFault;
}