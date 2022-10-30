package com.bluejungle.destiny.mgmtconsole.usersandroles.groups;

import javax.faces.event.ActionEvent;

/**
 * JSF Action Listener used to create a new user group
 * 
 * @author sgoldstein
 */
public class CreateNewUserGroupActionListener extends UserGroupsViewActionListenerBase {

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) {
        getUserGroupsViewBean().createAndSelectNewUserGroup();
    }
}

