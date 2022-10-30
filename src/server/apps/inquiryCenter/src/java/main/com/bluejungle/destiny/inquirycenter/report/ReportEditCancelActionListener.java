/*
 * Created on Aug 1, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

/**
 * Action invoked when a report edit is cancelled
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/ReportEditCancelActionListener.java#1 $
 */

public class ReportEditCancelActionListener extends ReportPageActionListenerBase {

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent actionEvent) throws AbortProcessingException {
        IMyReportsPageBean reportPageBean = (IMyReportsPageBean) getReportPageBean();
        reportPageBean.cancelSelectedReportEdit();
    }
}