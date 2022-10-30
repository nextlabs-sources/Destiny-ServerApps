/*
 * Created on Apr 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report;

import com.bluejungle.destiny.inquirycenter.report.defaultimpl.ReportNavigatorBeanImpl;
import org.apache.axis2.AxisFault;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

/**
 * This action listener is invoked when a report record is supposed to be
 * deleted.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/ReportDeleteActionListener.java#1 $
 */

public class ReportDeleteActionListener extends ReportPageActionListenerBase {

    private static final String REPORT_DELETE_SUCCESS_BUNDLE_KEY = "reports_delete_report_success";
    private static final String REPORT_DELETE_ERROR_BUNDLE_KEY = "reports_delete_report_error";
    private static final String MY_REPORTS_EXECUTE_VIEW_ACTION = "myReportsExecute";
    private static final String MY_REPORTS_VIEW_ACTION = "myReports";
    private static final String MULTI_REPORTS_VIEW_ACTION = "multiReports";

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent actionEvent)
            throws AbortProcessingException {
        IMyReportsPageBean reportPageBean = (IMyReportsPageBean) getReportPageBean();
        IReport currentSelectedReport = reportPageBean.getSelectedReport();
        String nameToDelete = currentSelectedReport.getTitle();
        
        try {
            reportPageBean.deleteSelectedReport();
        } catch (ReportViewException | AxisFault exception) {
            addErrorMessage(REPORT_DELETE_ERROR_BUNDLE_KEY, new String[] { exception.getMessage() });
            throw new AbortProcessingException(exception);
        }
        
        Object[] successMessageArgs = new Object[]{ nameToDelete };
        addSuccessMessage(REPORT_DELETE_SUCCESS_BUNDLE_KEY, successMessageArgs);
        
        FacesContext fc = FacesContext.getCurrentInstance();
        ReportNavigatorBeanImpl myBean = (ReportNavigatorBeanImpl) fc.getApplication()
                .createValueBinding("#{reportNavigatorBean}").getValue(fc);
        
        final String responseAction;
        
        if (myBean.isHasMultipleReportsPages()) {
            // the multiReports view can handle the "execute" view too
            // if (reportPageBean.getReportList().isEmpty()) {
            responseAction = MULTI_REPORTS_VIEW_ACTION;
            // } else {
            //    responseAction = MULTI_EXECUTE_REPORTS_VIEW_ACTION;
            //}
        } else {
            if (reportPageBean.getReportList().isEmpty()) {
                responseAction = MY_REPORTS_VIEW_ACTION;
            }else{
                responseAction = MY_REPORTS_EXECUTE_VIEW_ACTION;
            }
        }
        super.setResponseAction(responseAction, actionEvent);
    }
}
