/*
 * Created on Apr 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report;

import java.util.List;

import javax.faces.event.ActionEvent;

/**
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/IReportPageBean.java#1 $
 */
public interface IReportPageBean {

    /**
     * Returns the list of reports to display in the reports view
     * 
     * @return a list of report to display
     */
    public List getReportList();

    /**
     * Returns the currently selected report object
     * 
     * @return the currently selected report object
     */
    public IReport getSelectedReport();

    /**
     * This API is called when a report from the list is about to be executed
     * 
     * @param event
     *            action event triggered by the "execute report" control
     */
    public void onExecuteReport(ActionEvent event);

    /**
     * Sets the id of the selected report
     * 
     * @param id
     *            id of the new report that needs to be selected
     */
    public void setSelectedReportId(Long id);
}