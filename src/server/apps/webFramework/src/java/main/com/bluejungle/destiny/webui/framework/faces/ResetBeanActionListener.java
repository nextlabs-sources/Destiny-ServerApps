/*
 * Created on Jun 2, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.faces;

import java.util.StringTokenizer;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

/**
 * A JSF action listener which, when invoked, will reset a list of
 * IResetableBean instances. The list is specified as a list of managed bean
 * names using the request parameter with name,
 * {@see #BEANS_TO_RESET_PARAM_NAME}.
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/faces/ResetBeanActionListener.java#1 $
 */

public class ResetBeanActionListener extends ActionListenerBase {

    public static final String BEANS_TO_RESET_PARAM_NAME = "beansToReset";    
    public static final String BEANS_TO_RESET_PARAM_DELIMETER = ",";

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) throws AbortProcessingException {
        String beansToResetParamValue = getRequestParameter(BEANS_TO_RESET_PARAM_NAME, "");
        StringTokenizer beansToResetTokenizer = new StringTokenizer(beansToResetParamValue, BEANS_TO_RESET_PARAM_DELIMETER);
        while (beansToResetTokenizer.hasMoreTokens()) {
            String nextBeanToReset = beansToResetTokenizer.nextToken().trim();            
            Object nextBeanInstanceToReset = getManagedBeanByName(nextBeanToReset);
            if (!(nextBeanInstanceToReset instanceof IResetableBean)) {
                StringBuffer errorMessage = new StringBuffer("The bean with name, ");
                errorMessage.append(nextBeanToReset);
                errorMessage.append(", is not of type, ");
                errorMessage.append(IResetableBean.class.getName());
                errorMessage.append(".  It cannot be reset.");
                throw new IllegalArgumentException(errorMessage.toString());
            }
            
            ((IResetableBean)nextBeanInstanceToReset).reset();
        }
    }
}