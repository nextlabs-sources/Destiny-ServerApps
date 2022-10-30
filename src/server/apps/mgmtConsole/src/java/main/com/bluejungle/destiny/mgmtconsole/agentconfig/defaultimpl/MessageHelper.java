/*
 * Created on Apr 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.bluejungle.destiny.mgmtconsole.CommonConstants;
import com.bluejungle.destiny.webui.framework.message.MessageManager;

/**
 * MessageHelper is utilized to add user facing messages to the current request
 * context
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/MessageHelper.java#1 $
 */
public class MessageHelper {

    private static final String PROFILE_SAVE_SUCCESS_MSG = "agent_config_profile_save_success_message_detail";
    private static final String PROFILE_DELETE_SUCCESS_MSG = "agent_config_profile_delete_success_message_detail";
    private static final String SYSTEM_ERROR_SUMMARY_MSG = "system_error_message_summary";
    private static final String PROFILE_LOAD_FAILED_MSG = "agent_config_profile_load_error_message_detail";
    private static final String PROFILE_NAME_NOT_UNIQUE_ERROR_MSG = "agent_config_profile_name_not_unique_error_message_detail";
    private static final String PROFILE_SAVED_FAILED_ERROR_MSG = "agent_config_profile_save_failed_error_message_detail";
    private static final String PROFILE_DELETE_FAILED_ERROR_MSG = "agent_config_profile_delete_failed_error_message_detail";
    private static final String USER_UNAUTHORIZED_ERROR_MSG = "unauthorized_error_message_detail";

    /**
     * Add Profile Store Success Message
     */
    static void addProfileStoreSuccessMessage() {
        addSuccessMessage(PROFILE_SAVE_SUCCESS_MSG);
    }

    /**
     * Add Profile Deleted Success Message
     */
    static void addProfileDeleteSuccessMessage() {
        addSuccessMessage(PROFILE_DELETE_SUCCESS_MSG);
    }

    /**
     * Add Profile Load Error Message
     */
    static void addProfileLoadErrorMessage() {
        ResourceBundle commonBundle = getCommonBundle();
        ResourceBundle mgmtBundle = getMgmtConsoleBundle();

        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, commonBundle.getString(SYSTEM_ERROR_SUMMARY_MSG), mgmtBundle.getString(PROFILE_LOAD_FAILED_MSG));
        MessageManager.getInstance().addMessage(facesMessage);
    }

    /**
     * Add Unique Name Violation Message
     */
    static void addUniqueNameViolationMessage() {
        addErrorMessage(PROFILE_NAME_NOT_UNIQUE_ERROR_MSG);
    }

    /**
     * Add Profile Save Error Message
     */
    static void addProfileSaveErrorMessage() {
        addErrorMessage(PROFILE_SAVED_FAILED_ERROR_MSG);
    }

    /**
     * Add Profile Save Error Message
     */
    static void addProfileDeleteErrorMessage() {
        addErrorMessage(PROFILE_DELETE_FAILED_ERROR_MSG);
    }

    /**
     * Add a User Unauthorized Error Message
     */
    static void addUserUnauthorizedErrorMessage() {
        addErrorMessage(USER_UNAUTHORIZED_ERROR_MSG, getCommonBundle());
    }

    /**
     * Add a non-parameterized single line error message with the specified key
     * pointing to a message in the Management Console bundle
     */
    private static void addErrorMessage(String messageKey) {
        ResourceBundle mgmtBundle = getMgmtConsoleBundle();

        addErrorMessage(messageKey, mgmtBundle);
    }

    /**
     * Add a non-parameterized single line error message with the specified
     * bundle key from the specified bundle
     * 
     * @param messageKey
     * @param messageBundle
     */
    private static void addErrorMessage(String messageKey, ResourceBundle messageBundle) {
        FacesMessage facesMessage = new FacesMessage();
        facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
        String messageDetail = messageBundle.getString(messageKey);
        facesMessage.setDetail(messageDetail);
        MessageManager.getInstance().addMessage(facesMessage);
    }

    /**
     * Add a non-parameterized single line success message
     */
    private static void addSuccessMessage(String messageKey) {
        ResourceBundle mgmtBundle = getMgmtConsoleBundle();

        FacesMessage facesMessage = new FacesMessage();
        facesMessage.setSeverity(FacesMessage.SEVERITY_INFO);
        String messageDetail = mgmtBundle.getString(messageKey);
        facesMessage.setDetail(messageDetail);
        MessageManager.getInstance().addMessage(facesMessage);
    }

    /**
     * Retreive system common bundle
     * 
     * @return the system common bundle
     */
    private static ResourceBundle getCommonBundle() {
        Locale currentLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        return ResourceBundle.getBundle(com.bluejungle.destiny.appframework.CommonConstants.COMMON_BUNDLE_NAME, currentLocale);
    }

    /**
     * Retrieve the Management Console Bundle
     * 
     * @param currentLocale
     * @return
     */
    private static ResourceBundle getMgmtConsoleBundle() {
        Locale currentLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        return ResourceBundle.getBundle(CommonConstants.MGMT_CONSOLE_BUNDLE_NAME, currentLocale);
    }
}