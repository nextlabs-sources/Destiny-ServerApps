/*
 * Created on Aug 23, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users;

import javax.faces.component.ActionSource;
import javax.faces.el.MethodBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.mgmtconsole.shared.MgmtConsoleActionListenerBase;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IInternalUsersViewBean;
import com.bluejungle.destiny.webui.framework.faces.ConstantMethodBinding;

/**
 * @author sasha
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/users/SaveCreatedUserActionListener.java#1 $:
 */

public class SaveCreatedUserActionListener extends MgmtConsoleActionListenerBase {
    
    public static final String USER_CREATION_BEAN_NAME_PARAM_NAME = "userCreationBeanName";
    public static final String USER_VIEW_BEAN_NAME_PARAM_NAME = "userViewBeanName";    
    
    private static final Log LOG = LogFactory.getLog(SaveCreatedUserActionListener.class.getName());
    
    private static final String DUPLICATE_LOGIN_MSG = "users_and_roles_user_creation_save_duplicate_login_message_detail";
    private static final String PASSWORD_MISMATCH_MSG = "users_and_roles_user_creation_save_password_mismatch_message_detail";
    private static final String GENERAL_ERROR_MSG = "users_and_roles_user_creation_save_failed_message_detail";

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent actionEvent) throws AbortProcessingException {
        String userCreationBeanName = getRequestParameter(USER_CREATION_BEAN_NAME_PARAM_NAME, null);
        if (userCreationBeanName == null) {
            throw new NullPointerException("User creation bean name parameter not found.");
        }
        
        IUserCreationBean userCreationBean = (IUserCreationBean) getManagedBeanByName(userCreationBeanName);
        if (userCreationBean == null) {
            throw new IllegalArgumentException("User Creation Bean instance with bean name " + userCreationBeanName + " not found.");
        }
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("called processAction.  User Creation Bean: " + userCreationBean);
        }
        
        String userViewBeanName = getRequestParameter(USER_VIEW_BEAN_NAME_PARAM_NAME, null);
        if (userViewBeanName == null) {
            throw new NullPointerException("User view bean name parameter not found.");
        }
        
        IInternalUsersViewBean userViewBean = (IInternalUsersViewBean) getManagedBeanByName(userViewBeanName);
        if (userViewBean == null) {
            throw new NullPointerException("User view bean instance with bean name " + userViewBeanName + " not found.");
        }

        ActionSource actionSource = (ActionSource) actionEvent.getComponent();
        MethodBinding actionMethodBinding = null;
        if (!userCreationBean.getPassword().equals(userCreationBean.getConfirmPassword())) {
            actionMethodBinding = null;
            addErrorMessage(PASSWORD_MISMATCH_MSG);
        } else {
            actionMethodBinding = new ConstantMethodBinding("usersAndRolesUsers");
        }
        
        try {
            userCreationBean.saveCreatedUser();
            userViewBean.reset();
        } catch (DuplicateUserException due) {
            actionMethodBinding = null;
            addErrorMessage(DUPLICATE_LOGIN_MSG);
            LOG.error(due);
        } catch (Exception e) {
            actionMethodBinding = null;            
            addErrorMessage(GENERAL_ERROR_MSG);
            LOG.error(e);
        }
        actionSource.setAction(actionMethodBinding);         
    }
}
