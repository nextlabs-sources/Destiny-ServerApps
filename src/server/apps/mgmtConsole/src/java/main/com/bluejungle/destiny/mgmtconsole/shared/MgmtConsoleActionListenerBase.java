/*
 * Created on Aug 24, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.shared;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.bluejungle.destiny.mgmtconsole.CommonConstants;
import com.bluejungle.destiny.webui.framework.faces.ActionListenerBase;
import com.bluejungle.destiny.webui.framework.message.MessageManager;

/**
 * @author sasha
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/shared/MgmtConsoleActionListenerBase.java#1 $:
 */

public abstract class MgmtConsoleActionListenerBase extends ActionListenerBase {

    /**
     * Add a non-parameterized single line error message with the specified
     * bundle key 
     * 
     * @param messageKey
     */
    protected void addErrorMessage(String messageKey) {
        FacesMessage facesMessage = new FacesMessage();
        facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
        
        String messageDetail = getMgmtConsoleBundle().getString(messageKey);
        facesMessage.setDetail(messageDetail);
        MessageManager.getInstance().addMessage(facesMessage);
    }

    /**
     * Add a non-parameterized single line success message
     */
    protected void addSuccessMessage(String messageKey) {
        ResourceBundle mgmtBundle = getMgmtConsoleBundle();
    
        FacesMessage facesMessage = new FacesMessage();
        facesMessage.setSeverity(FacesMessage.SEVERITY_INFO);
        String messageDetail = mgmtBundle.getString(messageKey);
        facesMessage.setDetail(messageDetail);
        MessageManager.getInstance().addMessage(facesMessage);
    }

    /**
     * Retrieve the Management Console Bundle
     * 
     * @param currentLocale
     * @return
     */
    protected ResourceBundle getMgmtConsoleBundle() {
        Locale currentLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        return ResourceBundle.getBundle(CommonConstants.MGMT_CONSOLE_BUNDLE_NAME, currentLocale);
    }

}
