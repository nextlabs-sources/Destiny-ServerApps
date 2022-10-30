/*
 * Created on May 20, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;

import com.bluejungle.destiny.inquirycenter.report.defaultimpl.MyReportsPageBeanImpl;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.ReportNavigatorBeanImpl;

/**
 * Action listener which is invoked when a new report is created
 * 
 * @author safdar
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/NewReportActionListener.java#2 $
 */
public class NewReportActionListener extends ReportPageActionListenerBase {

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(javax.faces.event.ActionEvent event) throws AbortProcessingException {
        
        ((IMyReportsPageBean)getReportPageBean()).createNewQuickReport();
        
        FacesContext fc = FacesContext.getCurrentInstance();
        ReportNavigatorBeanImpl myBean = (ReportNavigatorBeanImpl) fc
                       .getApplication().createValueBinding(
                                       "#{reportNavigatorBean}").getValue(fc);
        if (myBean.isHasMultipleReportsPages()) {
            ((MyReportsPageBeanImpl)getReportPageBean()).setReportDefinitionSelected(true);
            ((MyReportsPageBeanImpl)getReportPageBean()).setReportResultsSelected(false);
            ((MyReportsPageBeanImpl)getReportPageBean()).setRedirectPage(
                    "/reports/myReportsExecuteContent.jspf");

        }  else {
            ((IMyReportsPageBean)getReportPageBean()).createNewQuickReport();
            ((MyReportsPageBeanImpl)getReportPageBean()).reset();
        }
        
        /*
         * Currently, there's no way in JSF to clear state.  Therefore, create a new UIViewRoot for the page
         */
        FacesContext.getCurrentInstance().getViewRoot();
    }
}