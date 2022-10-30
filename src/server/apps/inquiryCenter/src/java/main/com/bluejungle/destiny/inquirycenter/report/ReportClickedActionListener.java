/*
 * Created on Apr 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

/**
 * This action listener is invoked when a report is clicked in a report list
 * control.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/ReportClickedActionListener.java#1 $
 */

public class ReportClickedActionListener extends ReportPageActionListenerBase {

    private static final String REPORT_ID_REQUEST_PARAM = "reportId";    

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) throws AbortProcessingException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map requestParameters = facesContext.getExternalContext().getRequestParameterMap();

        //Finds the id of the selected report
        String reportId = (String) requestParameters.get(REPORT_ID_REQUEST_PARAM);
        if (reportId == null) {
            throw new IllegalStateException("Unable to find required request parameter, '" + REPORT_ID_REQUEST_PARAM + "'.");
        } 
        
        getReportPageBean().setSelectedReportId(new Long(reportId));
    }
}