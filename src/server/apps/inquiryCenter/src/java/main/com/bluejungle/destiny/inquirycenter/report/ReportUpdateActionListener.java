/*
 * Created on May 16, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report;

import org.apache.axis2.AxisFault;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

/**
 * This action listener is invoked when a report record is supposed to be
 * deleted.
 * 
 * @author safdar
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/ReportUpdateActionListener.java#1 $
 */

public class ReportUpdateActionListener extends ReportPageActionListenerBase {

    private static final String REPORT_UPDATE_SUCCESS_BUNDLE_KEY = "reports_update_report_success";
    private static final String REPORT_UPDATE_ERROR_BUNDLE_KEY = "reports_update_report_error";
    
    private static final String REPORT_INSERTION_FAILED_MSG = "reports_insert_report_failed";
    private static final String REPORT_INSERTION_SUCCESS_MSG = "reports_insert_report_success";
    
    private static final String REPORT_ID_REQUEST_PARAM = "reportId";

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent actionEvent) throws AbortProcessingException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map requestParameters = facesContext.getExternalContext().getRequestParameterMap();

        //Extract the id of the report to delete
        String reportId = (String) requestParameters.get(REPORT_ID_REQUEST_PARAM);

        IMyReportsPageBean reportPageBean = (IMyReportsPageBean) getReportPageBean();
        try {
            //First try to insert the report
            reportPageBean.insertSelectedReport();
            addSuccessMessage(REPORT_INSERTION_SUCCESS_MSG, new Object[] { reportPageBean.getSelectedReport().getTitle() });
        } catch (ReportViewException | AxisFault exception1) {
            try {
                IReport reportToUpdate = (IReport) reportPageBean.getSelectedReport();
                try {
                    reportToUpdate.getId();
                } catch (NullPointerException nullIdException){
                    addErrorMessage(REPORT_INSERTION_FAILED_MSG, new String[]{exception1.getMessage()});
                    throw new AbortProcessingException("Failed to insert report", exception1);
                }    

                // FIX ME - Is there a better way to handle the following?  Does changing to a GET form solve this problem?
                // If the customer has selected a different record,
                // and then navigates back to the edit/new page, then we will be
                // posting the edit/new data to the newly selected report -
                // which is wrong. This checks that condition:
//                if (!reportToUpdate.getId().toString().equals(reportId)) {
                    // We need to refresh the selected report to drop all
                    // unapplicable data as the the operation is in the
                    // wrong
                    // selected record context:
//                    reportPageBean.setSelectedReportId(new Long(reportId));
//                }

                String nameToUpdate = reportToUpdate.getTitle();
                reportPageBean.updateSelectedReport();

                Object[] successMessageArgs = {nameToUpdate}; 
                addSuccessMessage(REPORT_UPDATE_SUCCESS_BUNDLE_KEY, successMessageArgs); 
            } catch (ReportViewException | AxisFault exception2) {
            	reportPageBean.cancelSelectedReportEdit();
                addErrorMessage(REPORT_UPDATE_ERROR_BUNDLE_KEY, new String[]{exception2.getMessage()});
                throw new AbortProcessingException("Failed to update report", exception2);
            } 
        }

    }
}