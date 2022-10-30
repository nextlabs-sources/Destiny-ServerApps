/*
 * Created on Sep 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.user;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.appframework.CommonConstants;
import com.bluejungle.destiny.webui.framework.faces.ActionListenerBase;
import com.bluejungle.framework.utils.PasswordUtils;

/**
 * @author sasha
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/user/ChangePasswordActionListener.java#1 $:
 */

public class ChangePasswordActionListener extends ActionListenerBase {
    
    public static final String CHANGE_PASSWORD_BEAN_NAME_PARAM_NAME = "changePasswordBeanName";
    public static final String INVALID_OLD_PASSWORD_MSG = "user_change_password_invalid_old_password";
    public static final String SAME_PASSWORD_MSG = "user_change_password_same_password";
    public static final String REPEATED_PASSWORD_MSG = "user_change_password_repeated_password";
    public static final String GENERAL_ERROR_MSG = "user_change_password_general_error";
    public static final String SUCCESS_MSG = "user_change_password_success";
    
    private static final Log LOG = LogFactory.getLog(ChangePasswordActionListener.class.getName());

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) throws AbortProcessingException {
        String changePasswordBeanName = getRequestParameter(CHANGE_PASSWORD_BEAN_NAME_PARAM_NAME, null);
        if (changePasswordBeanName == null) {
            throw new NullPointerException("changePasswordBeanName parameter not found.");
        }
        IChangePasswordBean changePasswordBean = (IChangePasswordBean) getManagedBeanByName(changePasswordBeanName);
        if (changePasswordBean == null) {
            throw new IllegalArgumentException("IChangePasswordBean instance with bean name " + changePasswordBeanName + " not found.");
        }

        /*
         * HACK: the following validation should be done by the class UIPassword, as part of a JSF component
         */
        
        // Validation - making sure the password was supplied
        if (((changePasswordBean.getNewPassword() == null) ||
             (changePasswordBean.getNewPassword().toString().length() == 0)) &&
            ((changePasswordBean.getNewConfirmPassword() == null) ||
             (changePasswordBean.getNewConfirmPassword().toString().length() == 0))) {
            FacesMessage message = new FacesMessage(geCommonBundle().getString("password_empty_summary"), geCommonBundle().getString("password_empty_detail"));
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        
        // Validation - making sure the new password and the confirm pass are equal
        if (!changePasswordBean.getNewPassword().equals(changePasswordBean.getNewConfirmPassword())) {
            FacesMessage message = new FacesMessage(geCommonBundle().getString("confirmed_password_mismatch_summary"), geCommonBundle().getString("confirmed_password_mismatch_detail"));
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        
        // Validation - checking the password content
        if ((!changePasswordBean.getNewPassword().equals("")) && (!PasswordUtils.isValidPasswordDefault(changePasswordBean.getNewPassword()))) {
            FacesMessage message = new FacesMessage(geCommonBundle().getString("password_content_invalid_summary"), geCommonBundle().getString("password_content_invalid_detail"));
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        
        // Validation - checking if user entered same password as current password
        if(changePasswordBean.getNewPassword().equals(changePasswordBean.getOldPassword()))  {
            FacesMessage message = new FacesMessage(geCommonBundle().getString("password_content_invalid_summary"), geCommonBundle().getString(SAME_PASSWORD_MSG));
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        
        try {
            changePasswordBean.changePassword();
        } catch (InvalidPasswordException e) {
            addErrorMessage(INVALID_OLD_PASSWORD_MSG);
            LOG.error(e);
            return;
        } catch (PasswordHistoryException e) {
        	addErrorMessage(REPEATED_PASSWORD_MSG);
            LOG.error(e);
            return;
        } catch (Exception e) {
            addErrorMessage(GENERAL_ERROR_MSG);
            LOG.error(e);
            return;
        }
        addSuccessMessage(SUCCESS_MSG);
    }
    
    /**
     * Add a non-parameterized single line error message with the specified
     * bundle key 
     * 
     * @param messageKey
     */
    protected static void addErrorMessage(String messageKey) {
        if (messageKey == null) {
            throw new NullPointerException("messageKey cannot be null.");
        }
        
        FacesMessage facesMessage = new FacesMessage();
        facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
        
        String messageDetail = geCommonBundle().getString(messageKey);
        facesMessage.setDetail(messageDetail);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }
    
    /**
     * Add a non-parameterized single line success message
     */
    protected static void addSuccessMessage(String messageKey) {
        if (messageKey == null) {
            throw new NullPointerException("messageKey cannot be null.");
        }
        
        ResourceBundle inquiryCenter = geCommonBundle();

        FacesMessage facesMessage = new FacesMessage();
        facesMessage.setSeverity(FacesMessage.SEVERITY_INFO);
        String messageDetail = inquiryCenter.getString(messageKey);
        facesMessage.setDetail(messageDetail);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }
    
    /**
     * Retrieve the Inquiry Center Bundle
     * 
     * @param currentLocale
     * @return the Inquiry Center Bundle
     */
    private static ResourceBundle geCommonBundle() {
        Locale currentLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        return ResourceBundle.getBundle(CommonConstants.COMMON_BUNDLE_NAME, currentLocale);
    }    
}
