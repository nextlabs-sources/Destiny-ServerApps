/*
 * Created on Jun 20, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report;

import java.text.MessageFormat;
import java.text.StringCharacterIterator;
import java.text.CharacterIterator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import org.apache.commons.lang.StringEscapeUtils;

import com.bluejungle.destiny.inquirycenter.CommonConstants;
import com.bluejungle.destiny.webui.framework.faces.ActionListenerBase;
import com.bluejungle.destiny.webui.framework.message.MessageManager;

/**
 * Base Action Listener for the Reports views
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/branch/Destiny_D_PreBuiltReports/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/ReportPageActionListenerBase.java#1 $
 */

public class ReportPageActionListenerBase extends ActionListenerBase {

	public static final String REPORT_PAGE_BEAN_NAME_PARAM_NAME = "reportPageBeanName";

	/**
	 * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
	 */
	public void processAction(ActionEvent arg0) throws AbortProcessingException {
	}

	/**
	 * Retrieve the IReportPageBean associated with the invoking page. The bean
	 * is found through a request parameter with the name,
	 * {@see #REPORT_PAGE_BEAN_NAME_PARAM_NAME}, specifying the name of the
	 * bean
	 * 
	 * @return the IReportPageBean associated with the invoking page
	 */
	protected IReportPageBean getReportPageBean() {
		String reportPageBeanName = getRequestParameter(
				REPORT_PAGE_BEAN_NAME_PARAM_NAME, null);
		if (reportPageBeanName == null) {
			// TODO: Need to remove the following hack, this is put in because
			// the request parameter
			// reportPageBeanName somehow does not make it through to the action
			// listener classes
			// when using a4j:commandLink with d:dataTable
			reportPageBeanName = "myReportsBean";
		}

		if (reportPageBeanName == null) {
			throw new NullPointerException(
					"Report page bean name parameter not found.");
		}

		IReportPageBean reportPageBean = (IReportPageBean) getManagedBeanByName(reportPageBeanName);
		if (reportPageBean == null) {
			throw new IllegalArgumentException(
					"Report Page Bean instance with bean name, "
							+ reportPageBeanName + ", not found");
		}
		return reportPageBean;
	}

	/**
	 * Add a single line message
	 */
	private static void addMessage(String messageKey, Severity faceSeverity) {
		if (messageKey == null) {
			throw new NullPointerException("messageKey cannot be null.");
		}

		ResourceBundle inquiryCenter = getInquiryCenterBundle();

		FacesMessage facesMessage = new FacesMessage();
		facesMessage.setSeverity(faceSeverity);
		String messageDetail = inquiryCenter.getString(messageKey);
		facesMessage.setDetail(messageDetail);
		MessageManager.getInstance().addMessage(facesMessage);
	}

	/**
	 * Add a parameterized message
	 * 
	 * @param parameters
	 */
	private void addMessage(String messageKey, Object[] parameters,
			Severity faceSeverity) {
		if (messageKey == null) {
			throw new NullPointerException("messageKey cannot be null.");
		}

		if (parameters == null) {
			throw new NullPointerException("parameters cannot be null.");
		}

		String message = getInquiryCenterBundle().getString(messageKey);
		message = MessageFormat.format(message, parameters);

		FacesMessage facesMessage = new FacesMessage();
		facesMessage.setSeverity(faceSeverity);
		facesMessage.setDetail(StringEscapeUtils.escapeJavaScript(message));
		MessageManager.getInstance().addMessage(facesMessage);

	}

	/**
	 * Add a non-parameterized single line error message with the specified
	 * bundle key
	 * 
	 * @param messageKey
	 */
	protected static void addErrorMessage(String messageKey) {
		addMessage(messageKey, FacesMessage.SEVERITY_ERROR);
	}

	/**
	 * Add a parameterized error message
	 * 
	 * @param parameters
	 */
	protected void addErrorMessage(String messageKey, Object[] parameters) {
		addMessage(messageKey, parameters, FacesMessage.SEVERITY_ERROR);
	}

	/**
	 * Add a non-parameterized single line success message
	 */
	protected static void addSuccessMessage(String messageKey) {
		addMessage(messageKey, FacesMessage.SEVERITY_INFO);
	}

	/**
	 * Add a parameterized success message
	 * 
	 * @param parameters
	 */
	protected void addSuccessMessage(String messageKey, Object[] parameters) {
		addMessage(messageKey, parameters, FacesMessage.SEVERITY_INFO);
	}

	/**
	 * Retrieve the Inquiry Center Bundle
	 * 
	 * @param currentLocale
	 * @return the Inquiry Center Bundle
	 */
	private static ResourceBundle getInquiryCenterBundle() {
		Locale currentLocale = FacesContext.getCurrentInstance().getViewRoot()
				.getLocale();
		return ResourceBundle.getBundle(
				CommonConstants.INQUIRY_CENTER_BUNDLE_NAME, currentLocale);
	}
	public static String toDisableTags(String aText){
	    final StringBuilder result = new StringBuilder();
	    final StringCharacterIterator iterator = new StringCharacterIterator(aText);
	    char character =  iterator.current();
	    while (character != CharacterIterator.DONE ){
	      if (character == '<') {
	        result.append("&lt;");
	      }
	      else if (character == '>') {
	        result.append("&gt;");
	      }
	      else {
	        //the char is not a special one
	        //add it to the result as is
	        result.append(character);
	      }
	      character = iterator.next();
	    }
	    return result.toString();
	  }
}
