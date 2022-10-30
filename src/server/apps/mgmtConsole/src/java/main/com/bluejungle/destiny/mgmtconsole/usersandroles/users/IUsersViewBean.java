/*
 * Created on May 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users;

import javax.faces.model.DataModel;

/**
 * The Users View bean is utilized by the display layer to retrieve information
 * necessary to render the users administration view within the management
 * console
 * 
 * @author pkeni
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/users/IUsersViewBean.java#4 $
 */

public interface IUsersViewBean {

    String CREATE_USER_ACTION = "createUser";

    /**
     * Retrieve the users to display. The list contains all users defined within
     * the system
     * 
     * @return a DataModel interface to the list of all users in the system
     */
    public DataModel getUsers();

    /**
     * Retrieve the currently selected user
     * 
     * @return the currently selected user
     */
    public IUserBean getSelectedUser();

    /**
     * Set the user with the specified id to be the currently selected user
     * 
     * @param selectedUserId
     *            the id of the user to select
     */
    public void setSelectedUser(long selectedUserId);

    /**
     * Determine if a user is currently selected. A user will not be selected if
     * the new user button is pressed or no users currently exist in the system
     * 
     * @return true if a user is selected; false otherwise
     */
    public boolean isUserSelected();

    /**
     * Save changes to the selected user
     * 
     * @throws UsersException
     *             if an error occurs while saving the selected user
     */
    public void saveSelectedUser() throws UsersException;

    /**
     * Delete the selected user
     * 
     * @throws UsersException
     *             if an error occurs while deleting the selected user
     */
    public void deleteSelectedUser() throws UsersException;

    /**
     * Adds an empty user as the currently-selected user
     * 
     * @throws UsersException
     * 
     */
    public void createUser() throws UsersException;

    /**
     * Determine if local user creation is allowed
     * 
     * @return true if allowed; false otherwise
     */
    public boolean isLocalUserCreationAllowed();
}
