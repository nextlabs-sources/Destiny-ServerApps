/*
 * Created on May 19, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;

import com.bluejungle.destiny.framework.types.ID;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.DuplicateUserException;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUserBean;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.UsersException;
import com.bluejungle.destiny.services.management.UserRoleServiceException;
import com.bluejungle.destiny.services.management.types.AuthenticationModeEnumDTO;
import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.services.management.types.UserDTOList;
import com.bluejungle.destiny.services.management.types.UserManagementMetadata;
import com.bluejungle.destiny.services.policy.types.DMSUserData;
import com.bluejungle.destiny.services.policy.types.SubjectDTO;
import com.bluejungle.destiny.webui.framework.data.ProxyingDataModel;
import com.bluejungle.destiny.webui.framework.faces.IResetableBean;
import com.bluejungle.destiny.webui.framework.faces.UIInputUtils;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.domain.IHasId;
import com.bluejungle.pf.domain.destiny.subject.SubjectType;

/**
 * Default implementation of the
 * {@see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IInternalUsersViewBean}
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/users/defaultimpl/UsersViewBeanImpl.java#9 $
 */

public class UsersViewBeanImpl implements IInternalUsersViewBean, IResetableBean {

    private SubjectToUserMenuItemTranslatingDataModel userMenuItems;
    private Long selectedUserId;
    private Map userBeanCache = new HashMap();
    private boolean localUserCreationAllowed = false;

    /**
     * Performs actions necessary before render takes place
     * 
     * @throws ServiceException
     * @throws RemoteException
     * @throws UsersException
     */
    public void prerender() throws RemoteException, UsersException, UserRoleServiceException {
        // FIX ME - Currently throwing exceptions. Better to catch and add error
        // message to page

        if (userMenuItems == null) {
            loadUserMenuItems();
            selectAndLoadFirstUser();

            IUserServiceFacade serviceFacade = getUserServiceFacade();
            UserManagementMetadata userManagementMetadata = serviceFacade.getUserManagementMetadata();
            localUserCreationAllowed = (userManagementMetadata.getAuthenticationMode() != AuthenticationModeEnumDTO.REMOTE);
        } else {
            loadSelectedUserIfNecessary();
        }
        return;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUsersViewBean#getUsers()
     */
    public DataModel getUsers() {
        return this.userMenuItems;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUsersViewBean#getSelectedUser()
     */
    public IUserBean getSelectedUser() {
        return (IUserBean) this.userBeanCache.get(this.selectedUserId);
    }

    
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUsersViewBean#isUserSelected()
     */
    public boolean isUserSelected() {
        return this.selectedUserId.equals(IHasId.UNKNOWN_ID);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUsersViewBean#setSelectedUser(long)
     */
    public void setSelectedUser(long selectedUserId) {
        this.selectedUserId = new Long(selectedUserId);
        UIInputUtils.resetUIInput(FacesContext.getCurrentInstance());
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUsersViewBean#saveSelectedUser()
     */
    public void saveSelectedUser() throws UsersException {
        IInternalUserBean selectedUser = (IInternalUserBean) this.userBeanCache.get(this.selectedUserId);
        try {
            selectedUser.save();
            this.resetAndSelectUser(selectedUser.getUserId());
        } catch (RemoteException | UserRoleServiceException exception) {
            this.reset();
            throw new UsersException("Failed to update user with id, " + selectedUser.getUserId(), exception);
        }
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUsersViewBean#deleteSelectedUser()
     */
    public void deleteSelectedUser() throws UsersException {
        IInternalUserBean selectedUser = (IInternalUserBean) this.userBeanCache.get(this.selectedUserId);
        SubjectDTO userSubject = selectedUser.getWrappedUserDTO();
        try {
            getUserServiceFacade().deleteUser(userSubject);
        } catch (RemoteException | UserRoleServiceException exception) {
            throw new UsersException("Failed to update user with id, " + userSubject.getId(), exception);
        } finally {
            this.reset();
        }
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IInternalUsersViewBean#reset()
     */
    public void reset() {
        userMenuItems = null;
        selectedUserId = null;
        userBeanCache.clear();
    }

    /**
     * @throws UsersException
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IInternalUsersViewBean#resetAndSelectUser(long)
     */
    public void resetAndSelectUser(long selectedUserId) throws RemoteException, UsersException, UserRoleServiceException {
        this.reset();

        this.loadUserMenuItems();
        this.setSelectedUser(selectedUserId);

        IUserServiceFacade userServiceFacade = getUserServiceFacade();

        // Not exactly correct here. The corresponding menu item will have a
        // different user dto instance. Though, this shouldn't be a major issue,
        // since we just loaded the menu items. We're also forced to make a few
        // round trips, here, to the server. However, this shouldn't be a huge
        // problem given the usage characteristics of the app
        UserDTO user = userServiceFacade.getUser(new Long(selectedUserId));
        DMSUserData userData = userServiceFacade.getUserData(user);
        addUserToUserCache(user, userData);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUsersViewBean#createUser()
     */
    public void createUser() throws UsersException {
        this.selectedUserId = IHasId.UNKNOWN_ID;
        UIInputUtils.resetUIInput(FacesContext.getCurrentInstance());

        UserDTO userDTO = new UserDTO();
        userDTO.setType(SubjectType.USER.getName());
        ID unknownId = new ID();
        unknownId.setID(new BigInteger(IHasId.UNKNOWN_ID.toString()));

        userDTO.setId(unknownId);
        userDTO.setLocal(true);
        DMSUserData userData = new DMSUserData();
        IInternalUserBean userBean = new UserBeanImpl(userDTO, userData) {

            public void save() throws DuplicateUserException, RemoteException {
                userSubject = getUserServiceFacade().createUser(userSubject, getWrappedDMSUserData());
            }

        };

        userBeanCache.put(IHasId.UNKNOWN_ID, userBean);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUsersViewBean#isLocalUserCreationAllowed()
     */
    public boolean isLocalUserCreationAllowed() {
        return this.localUserCreationAllowed;
    }

    /**
     * Load the list of user menu items
     * 
     * @throws RemoteException
     * @throws ServiceException
     */
    private void loadUserMenuItems() throws RemoteException, UserRoleServiceException {
        IUserServiceFacade userServiceFaces = getUserServiceFacade();
        UserDTOList rawUserMenuItems = userServiceFaces.getAllUsers();
        UserDTO[] rawUserMenuItemsArray = rawUserMenuItems.getUsers();
        if (rawUserMenuItemsArray == null) {
            rawUserMenuItemsArray = new UserDTO[0];
        }

        this.userMenuItems = new SubjectToUserMenuItemTranslatingDataModel(rawUserMenuItemsArray);
    }

    /**
     * Select the first user in the user menu item list
     * 
     * @throws ServiceException
     * @throws RemoteException
     */
    private void selectAndLoadFirstUser() throws RemoteException, UsersException, UserRoleServiceException {
        if (this.userMenuItems.getRowCount() > 0) {
            IInternalUserMenuItemBean firstUserMenuItem = (IInternalUserMenuItemBean) this.userMenuItems.getFirstMenuItem();
            this.selectedUserId = new Long(firstUserMenuItem.getUserId());
            UIInputUtils.resetUIInput(FacesContext.getCurrentInstance());

            loadUserForUserMenuItem(firstUserMenuItem);
        } else {
            this.createUser();
        }
    }

    /**
     * Load the selected user into memory
     * 
     * @throws ServiceException
     * @throws RemoteException
     */
    private void loadSelectedUserIfNecessary() throws RemoteException, UsersException, UserRoleServiceException {
        if (this.userMenuItems.getRowCount() > 0) {
            if (this.selectedUserId == null) {
                selectAndLoadFirstUser();
            }

            if (!this.userBeanCache.containsKey(this.selectedUserId)) {
                IInternalUserMenuItemBean selectedMenuItem = this.userMenuItems.getUserMenuItem(selectedUserId);
                loadUserForUserMenuItem(selectedMenuItem);
            }
        }
    }

    /**
     * Load the user data associated with the specified user menu items
     * 
     * @param userMenuItem
     *            the menu item for which to load user data
     * @throws ServiceException
     * @throws RemoteException
     * @throws UsersException
     * @throws
     */
    private void loadUserForUserMenuItem(IInternalUserMenuItemBean userMenuItem) throws RemoteException, UsersException, UserRoleServiceException {
        if (userMenuItem == null) {
            throw new NullPointerException("userMenuItem cannot be null.");
        }

        UserDTO userSubject = userMenuItem.getWrappedSubjectDTO();
        DMSUserData userData = getUserServiceFacade().getUserData(userSubject);
        addUserToUserCache(userSubject, userData);
    }

    /**
     * Add the specified User to the user cache
     * 
     * @param userSubject
     * @param userData
     * @throws UsersException
     */
    private void addUserToUserCache(UserDTO userSubject, DMSUserData userData) throws UsersException {
        IInternalUserBean selectedUser = new UserBeanImpl(userSubject, userData);
        this.userBeanCache.put(this.selectedUserId, selectedUser);
    }

    /**
     * Retrieve the User Service Facade
     * 
     * @return the user service facade
     */
    private IUserServiceFacade getUserServiceFacade() {
        return (IUserServiceFacade) ComponentManagerFactory.getComponentManager().getComponent(UserServiceFacadeImpl.class);
    }

    /**
     * A data model which translates SubjectDTO instances to
     * IInternalUserItemMenuBean instances
     * 
     * @author sgoldstein
     */
    private class SubjectToUserMenuItemTranslatingDataModel extends ProxyingDataModel {

        private Map viewedUserMenuItems = new HashMap();

        private SubjectToUserMenuItemTranslatingDataModel(SubjectDTO[] userList) {
            super(new ArrayDataModel(userList));
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
         */
        protected Object proxyRowData(Object rawData) {
            if (rawData == null) {
                throw new NullPointerException("rawData cannot be null.");
            }

            UserMenuItemBeanImpl userMenuItemToReturn = new UserMenuItemBeanImpl((UserDTO) rawData);
            this.viewedUserMenuItems.put(new Long(userMenuItemToReturn.getUserId()), userMenuItemToReturn);

            return userMenuItemToReturn;
        }

        /**
         * Retrieve the user menu item with the specified id. Note that this
         * user menu item must have already been viewed
         * 
         * @param userID
         *            the id of the user associated with the viewed menu item
         * @return the viewed menu item associated with the user of the
         *         specified id
         */
        private IInternalUserMenuItemBean getUserMenuItem(Long userID) {
            if (!this.viewedUserMenuItems.containsKey(userID)) {
                throw new IllegalArgumentException("User with ID, " + userID + ", has not been viewed.");
            }

            return (IInternalUserMenuItemBean) this.viewedUserMenuItems.get(userID);
        }

        /**
         * Retrieve the user menu item in the first row
         * 
         * @return the user menu item in the first row
         */
        public IInternalUserMenuItemBean getFirstMenuItem() {
            this.setRowIndex(0);
            return (IInternalUserMenuItemBean) this.getRowData();
        }

        /**
         * Retrieve the wrapped SubjectDTO[] representing the users in the
         * system
         * 
         * @return the wrapped SubjectDTO[] representing the users in the system
         */
        public SubjectDTO[] getWrappedUserSubjects() {
            return (SubjectDTO[]) super.getWrappedDataModel().getWrappedData();
        }
    }

    /**
     * Default implementation of the IInternalUserMenuItemBean
     * 
     * @author sgoldstein
     */
    private class UserMenuItemBeanImpl implements IInternalUserMenuItemBean {

        private UserDTO userDTO;

        /**
         * Create an instance of UserMenuItemBeanImpl
         * 
         * @param userDTO
         */
        public UserMenuItemBeanImpl(UserDTO userDTO) {
            if (userDTO == null) {
                throw new NullPointerException("userDTO cannot be null.");
            }

            this.userDTO = userDTO;
        }

        /**
         * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUserMenuItemBean#getUserId()
         */
        public long getUserId() {
            return this.userDTO.getId().getID().longValue();
        }

        /**
         * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUserMenuItemBean#getUserTitle()
         */
        public String getUserTitle() {
            return this.userDTO.getName();
        }

        /**
         * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUserMenuItemBean#getUserTitleToolTip()
         */
        public String getUserTitleToolTip() {
            return userDTO.getUniqueName().toLowerCase();
        }

        /**
         * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IInternalUserMenuItemBean#getWrappedSubjectDTO()
         */
        public UserDTO getWrappedSubjectDTO() {
            return this.userDTO;
        }
    }

}
