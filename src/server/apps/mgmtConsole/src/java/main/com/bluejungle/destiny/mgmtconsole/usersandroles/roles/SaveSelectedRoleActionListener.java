/*
 * Created on May 20, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.roles;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The SaveSelectedRoleActionListener is invoked by the display layer to save
 * changes to the selected role
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/roles/SaveSelectedRoleActionListener.java#3 $
 */

public class SaveSelectedRoleActionListener extends RolesViewActionListenerBase {

    private static final Log LOG = LogFactory.getLog(SaveSelectedRoleActionListener.class.getName());
    
    private static final String ROLE_SAVE_SUCCESS_MSG = "users_and_roles_roles_save_success_message_detail";
    private static final String ROLE_SAVED_FAILED_ERROR_MSG = "users_and_roles_roles_save_failed_error_message_detail";

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) throws AbortProcessingException {
        IRolesViewBean roleBean = getRolesViewBean();
        IRoleBean selectedRoleBean = roleBean.getSelectedRole();
        
        // when using jstl and jsp together, the selectBooleanCheckBox is not updated on the backing bean
        // I need to update on the action listener.
        // This is the only way I can think
        UIComponent parent = ((HtmlCommandLink)event.getSource()).getParent();
        List<UIComponent> children  = parent.getChildren();
        for(UIComponent child : children){
            if (child instanceof HtmlSelectBooleanCheckbox) {
                String id = child.getId();
                
                id = trimSuffixes(id, "_true", "_false");
                ApplicationResourceBean appResourceBean = selectedRoleBean.getResourceById(id);
                if (appResourceBean != null) {
                    HtmlSelectBooleanCheckbox checkBox = (HtmlSelectBooleanCheckbox)child;
                    appResourceBean.setAccessible((Boolean)checkBox.getValue());
                }
            }
        }
        
        try {
            roleBean.saveSelectedRole();
            addSuccessMessage(ROLE_SAVE_SUCCESS_MSG);
        } catch (RolesException exception) {
            addErrorMessage(ROLE_SAVED_FAILED_ERROR_MSG);
            getLog().error("Failed to save role changes", exception);
        }
    }
    
    /**
     * return the suffix, stop at the first found
     * @param input
     * @param suffixes all possible suffix
     * @return
     */
    private String trimSuffixes(String input, String... suffixes) {
        for (String suffix : suffixes) {
            if (input.endsWith(suffix)) {
                return input.substring(0, input.length() - suffix.length());
            }
        }
        return input;
    }
    
    /**
     * Retrieve a Logger
     * @return a Logger
     */
    private Log getLog() {
        return LOG;
    }
}