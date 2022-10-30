/*
 * Created on Jul 19, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;


import com.bluejungle.destiny.appframework.CommonConstants;
import com.bluejungle.destiny.webui.framework.message.MessageManager;
import com.nextlabs.destiny.container.shared.inquirymgr.SharedLib;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.ReportDataManager;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This is a simple utility class helping out with the messages that have to be
 * displayed on the UI.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/MessageUtil.java#1 $
 */
public class MessageUtil {

    /**
     * Name of the inquiry center resource bundle
     */
    private static final String INQUIRY_CENTER_BUNDLE_NAME = "InquiryCenterMessages";

    
    /**
     * Builds a message to be displayed to the end user
     * 
     * @param bundle
     *            resource bundle to use
     * @param bundleKey
     *            message key to use
     * @param severity
     *            severitu of the message
     * @param args
     *            message arguments
     */
    public static void addMessage(ResourceBundle bundle, String bundleKey, Severity severity, Object[] args) {
        FacesMessage facesMessage = new FacesMessage();
        facesMessage.setSeverity(severity);
        String messageToDisplay = bundle.getString(bundleKey);
        if (args != null) {
            messageToDisplay = MessageFormat.format(messageToDisplay, args);
        }
        facesMessage.setDetail(messageToDisplay);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }

    /**
     * Returns the common resource bundle
     * 
     * @return the common resource bundle
     */
    public static ResourceBundle getCommonResourceBundle() {
        Locale currentLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        return ResourceBundle.getBundle(CommonConstants.COMMON_BUNDLE_NAME, currentLocale);
    }

    /**
     * Returns the inquiry center resource bundle.
     * 
     * @return the inquiry center resource bundle.
     */
    public static ResourceBundle getInquiryCenterResourceBundle() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        ResourceBundle inquiryCenterBundle = ResourceBundle.getBundle(INQUIRY_CENTER_BUNDLE_NAME, locale);
        return inquiryCenterBundle;
    }
    
    private static Log LOG = LogFactory.getLog(MessageUtil.class.getName());

    /** 
     * Utility method reused by all reports to check the report query time
     * range and display warning message if needed
     * 
     * @param beginTimestamp The 'From' date in timestamp format
     * @param endTimestamp The 'To' date in timestamp format
     * @param forPolicyActivity true if the report is for policy activity logs
     */
    public static void checkReportQueryRange(
        Timestamp beginTimestamp, Timestamp endTimestamp, 
        boolean forPolicyActivity)  {

        String msg = null;
        try {
            Timestamp earliestTime = 
                SharedLib.getEarliestReportDate(forPolicyActivity);
            if (earliestTime == null) {
                ResourceBundle bundle = 
                    MessageUtil.getInquiryCenterResourceBundle();
                 msg = bundle.getString("report_date_range_error");
                 FacesMessage message = new FacesMessage();
                 message.setSeverity(FacesMessage.SEVERITY_ERROR);
                 message.setDetail(msg);
                 MessageManager.getInstance().addMessage(message);
            } else if (endTimestamp.before(beginTimestamp) || 
                beginTimestamp.before(earliestTime)) {
                
                ResourceBundle bundle = 
                    MessageUtil.getInquiryCenterResourceBundle();
                msg = bundle.getString("report_date_range_warning");
                SimpleDateFormat format = new SimpleDateFormat();
                String formattedWarningMsg = MessageFormat.format(
                        msg, format.format(earliestTime));
                FacesMessage message = new FacesMessage();
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setDetail(formattedWarningMsg);
                MessageManager.getInstance().addMessage(message);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("MessageUtil.checkReportQueryRange:Date range is correct");
                }
            }
        } catch (Exception ex) {
            if (LOG.isErrorEnabled()) {
                LOG.error("MessageUtil.checkReportQueryRange: Exception while checking report time range ", ex);
            }
        }
    }
}