package com.bluejungle.destiny.mgmtconsole.usersandroles.groups;

import javax.faces.event.ActionEvent;

/**
 * JSF Action listener user to clear a new user group bean instead of persisted
 * it
 * 
 * @author sgoldstein
 */
public class CancelGroupAddActionListener extends UserGroupsViewActionListenerBase {

    public void processAction(ActionEvent event) {
        getUserGroupsViewBean().clearSelectedNewUserGroup();
    }
}

