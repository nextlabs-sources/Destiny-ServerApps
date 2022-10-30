/*
 * Created on Apr 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import com.bluejungle.destiny.webui.framework.faces.NewSelectedItemEvent;

/**
 * This action listener instance is invoked whenever the data in a list control
 * for reports is scrolled. This action listener sets the currently selected
 * item properly.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/ReportScrolledActionListener.java#1 $
 */

public class ReportScrolledActionListener extends ReportPageActionListenerBase implements ActionListener {

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) throws AbortProcessingException {
        if (event instanceof NewSelectedItemEvent) {
            NewSelectedItemEvent selectedItemEvent = (NewSelectedItemEvent) event;
            IReportPageBean reportPageBean = getReportPageBean();
            IReport report = (IReport) selectedItemEvent.getSelectedItem();
            reportPageBean.setSelectedReportId(report.getId());
        } else {
            throw new AbortProcessingException("Event is expected to be of type NewSelectedItemEvent.  Type was " + event.getClass().getName());
        }
    }
}