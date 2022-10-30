/*
 * Created on May 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utilized by the display layer to select a User from the User list menu
 * 
 * @author pkeni
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/users/SelectUserActionListener.java#1 $
 */

public class SelectUserActionListener extends UsersViewActionListenerBase {

     public static final String SELECTED_USER_ID_PARAM_NAME = "selectedUserId";
     private static final Log LOG = LogFactory.getLog(SelectUserActionListener.class.getName());

     /**
      * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
      */
     public void processAction(ActionEvent event) throws AbortProcessingException {
         String selectedUserId = getRequestParameter(SELECTED_USER_ID_PARAM_NAME, null);

         if (selectedUserId == null) {
             throw new NullPointerException("Selected User id parameter not found.");
         }

         IUsersViewBean usersViewBean = getUsersViewBean();

         Long selectedUserIdAsLong = Long.valueOf(selectedUserId);
         usersViewBean.setSelectedUser(selectedUserIdAsLong.longValue());
     }

     /**
      * Retrieve a reference to a Log
      * 
      * @return a reference to a Log
      */
     private Log getLog() {
         return LOG;
     }
}
