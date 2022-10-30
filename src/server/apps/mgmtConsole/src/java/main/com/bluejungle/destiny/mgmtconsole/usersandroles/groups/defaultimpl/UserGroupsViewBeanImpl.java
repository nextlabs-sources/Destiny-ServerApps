package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;

import com.bluejungle.destiny.framework.types.ID;
import com.bluejungle.destiny.services.management.types.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.framework.types.IDList;
import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean;
import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.NonUniqueUserGroupTitleException;
import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.UserGroupsViewException;
import com.bluejungle.destiny.services.policy.types.DefaultAccessAssignment;
import com.bluejungle.destiny.services.policy.types.DefaultAccessAssignmentList;
import com.bluejungle.destiny.webui.controls.UITabbedPane;
import com.bluejungle.destiny.webui.framework.data.ProxyingDataModel;
import com.bluejungle.destiny.webui.framework.faces.IResetableBean;
import com.bluejungle.destiny.webui.framework.faces.UIInputUtils;
import com.bluejungle.framework.comp.ComponentManagerFactory;

/**
 * Default implementation of the
 * {@see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IInternalUserGroupsViewBean}
 * 
 * @author sgoldstein
 */
public class UserGroupsViewBeanImpl implements IInternalUserGroupsViewBean, IResetableBean {

    private static final Log LOG = LogFactory.getLog(UserGroupsViewBeanImpl.class.getName());

    private static final String GENERAL_TAB_NAME = "generalTab";
    private static final String MEMBERS_TAB_NAME = "membersTab";
    private static final String DEFAULT_RIGHTS_TAB_NAME = "defaultRightsTab";

    private UserGroupReducedToGroupMenuItemTranslatingDataModel userGroupMenuItems;
    private String selectedUserGroupId = null;
    private IInternalNewUserGroupBean newUserGroup = null;
    private Map userGroupBeanCache = new HashMap();
    private UITabbedPane userGroupsViewTabbedPane;

    /**
     * Called to load user group data
     * 
     * @throws RemoteException
     */
    public void prerender() throws RemoteException, CommitFault, ServiceNotReadyFault, UnauthorizedCallerFault, UnknownEntryFault {
        // FIX ME - Currently throwing exceptions. Better to catch and add error
        // message to page
        if (this.userGroupsViewTabbedPane == null) {
            this.userGroupsViewTabbedPane = new UITabbedPane();
        }

        if (this.userGroupMenuItems == null) {
            loadUserGroupMenuItems();
            selectAndLoadFirstUserGroup();
        } else {
            loadSelectedUserGroupIfNecessary();
        }

    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupsViewBean#getUserGroupsViewTabbedPane()
     */
    public UITabbedPane getUserGroupsViewTabbedPane() {
        return this.userGroupsViewTabbedPane;
    }

    /**
     * 
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupsViewBean#getDefaultRightsTabName()
     */
    public String getDefaultRightsTabName() {
        return DEFAULT_RIGHTS_TAB_NAME;
    }

    /**
     * 
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupsViewBean#getGeneralTabName()
     */
    public String getGeneralTabName() {
        return GENERAL_TAB_NAME;
    }

    /**
     * 
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupsViewBean#getMembersTabName()
     */
    public String getMembersTabName() {
        return MEMBERS_TAB_NAME;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupsViewBean#setUserGroupsViewTabbedPane(com.bluejungle.destiny.webui.controls.UITabbedPane)
     */
    public void setUserGroupsViewTabbedPane(UITabbedPane tabbedPane) {
        if (tabbedPane == null) {
            throw new NullPointerException("tabbedPane cannot be null.");
        }

        this.userGroupsViewTabbedPane = tabbedPane;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupsViewBean#clearSelectedNewUserGroup()
     */
    public void clearSelectedNewUserGroup() {
        IUserGroupBean selectedUserGroupBean = getSelectedUserGroup();
        if (selectedUserGroupBean != null) {
            if (selectedUserGroupBean.isNew()) {
                // Do we need to clear it from menu? Let's try to rid ourselves
                // of this behavior. Talk to Andy
                this.newUserGroup = null;
                this.selectFirstUserGroup();
            }
        } else {
            // In an unstable state. Reset
            this.reset();
        }

    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupsViewBean#createAndSelectNewUserGroup()
     */
    public void createAndSelectNewUserGroup() {
        this.newUserGroup = new NewUserGroupBeanImpl();
        this.selectedUserGroupId = null;

        UIInputUtils.resetUIInput(FacesContext.getCurrentInstance());
        this.userGroupsViewTabbedPane.setSelectedTab(GENERAL_TAB_NAME);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupsViewBean#deleteSelectedUserGroup()
     */
    public void deleteSelectedUserGroup() throws UserGroupsViewException {
        IInternalUserGroupBean selectedGroup = (IInternalUserGroupBean) getSelectedUserGroup();
        if (!selectedGroup.isNew()) {
            IUserGroupServiceFacade userGroupServiceFacade = getUserGroupServiceFacade();
            try {
                UserGroupDTO userGroupToDelete = getSelectedUserGroupDTO();
                userGroupServiceFacade.deleteGroup(userGroupToDelete);
            } catch (ServiceNotReadyFault | UnknownEntryFault | CommitFault | UnauthorizedCallerFault | RemoteException exception) {
                throw new UserGroupsViewException(exception);
            } finally {
                this.reset();
            }
        } else {
            // Shouldn't happen, but handle it anyway
            this.clearSelectedNewUserGroup();
        }
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupsViewBean#getUserGroups()
     */
    public DataModel getUserGroups() {
        return this.userGroupMenuItems;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupsViewBean#getSelectedUserGroup()
     */
    public IUserGroupBean getSelectedUserGroup() {
        IUserGroupBean beanToReturn = null;

        if (this.selectedUserGroupId != null) {
            beanToReturn = (IUserGroupBean) this.userGroupBeanCache.get(this.selectedUserGroupId);
        } else if (this.newUserGroup != null) {
            beanToReturn = this.newUserGroup;
        } else {
            throw new IllegalStateException("A group has not been selected");
        }

        return beanToReturn;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupsViewBean#insertSelectedNewUserGroup()
     */
    public void insertSelectedNewUserGroup() throws UserGroupsViewException, NonUniqueUserGroupTitleException {
        if (this.newUserGroup == null) {
            throw new IllegalStateException("Insert called when new user group is null");
        }

        IUserGroupServiceFacade userGroupServiceFacade = getUserGroupServiceFacade();
        try {
            UserGroupDTO insertedUserGroup = userGroupServiceFacade.insertGroup(this.newUserGroup.getWrappedUserGroupInfo());
            this.resetAndSelectUserGroup(insertedUserGroup);
        } catch (UniqueConstraintViolationFault uniqueConstraintViolation) {
            throw new NonUniqueUserGroupTitleException();
        } catch (ServiceNotReadyFault | UnauthorizedCallerFault | RemoteException | CommitFault exception) {
            this.reset();
            throw new UserGroupsViewException(exception);
        }
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupsViewBean#saveSelectedUserGroup()
     */
    public void saveSelectedUserGroup() throws UserGroupsViewException, NonUniqueUserGroupTitleException {

        IInternalUserGroupBean selectedGroup = (IInternalUserGroupBean) getSelectedUserGroup();
        if (!selectedGroup.isNew()) {
            IUserGroupServiceFacade userGroupServiceFacade = getUserGroupServiceFacade();
            try {
                IInternalExistingUserGroupBean existingSelectedGroup = (IInternalExistingUserGroupBean) selectedGroup;
                UserGroupDTO wrappedUserGroupDTO = existingSelectedGroup.getWrappedUserGroupDTO();
                userGroupServiceFacade.updateGroup(wrappedUserGroupDTO);
                this.resetAndSelectUserGroup(wrappedUserGroupDTO);
            } catch (UniqueConstraintViolationFault uniqueConstraintViolation) {
                throw new NonUniqueUserGroupTitleException();
            } catch (ServiceNotReadyFault | UnauthorizedCallerFault | RemoteException | CommitFault exception) {
                this.reset();
                throw new UserGroupsViewException(exception);
            }
        } else {
            // Shouldn't happen, but handle it anyway
            this.insertSelectedNewUserGroup();
        }
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupsViewBean#setSelectedUserGroup(String)
     */
    public void setSelectedUserGroup(String userGroupId) {
        if (userGroupId == null) {
            throw new NullPointerException("groupId cannot be null.");
        }

        this.selectedUserGroupId = userGroupId;

        UIInputUtils.resetUIInput(FacesContext.getCurrentInstance());

    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupsViewBean#removeMembersFromSelectedUserGroup(java.util.Set)
     */
    public void removeMembersFromSelectedUserGroup(Set memberIds) throws UserGroupsViewException {
        if (memberIds == null) {
            throw new NullPointerException("memberIds cannot be null.");
        }

        UserGroupDTO selectedUserGroupDTO = getSelectedUserGroupDTO();
        ID[] usersToRemoveIds = new ID[memberIds.size()];
        Iterator memberIdIterator = memberIds.iterator();
        for (int i = 0; memberIdIterator.hasNext(); i++) {
            ID userIdToRemove = new ID();
            userIdToRemove.setID(BigInteger.valueOf((Long)memberIdIterator.next()));
            usersToRemoveIds[i] = userIdToRemove;
        }

        IDList usersToRemoveIDList = new IDList();
        usersToRemoveIDList.setIDList(usersToRemoveIds);

        IUserGroupServiceFacade userGroupServiceFacade = getUserGroupServiceFacade();
        try {
            userGroupServiceFacade.removeUsersFromUserGroup(selectedUserGroupDTO, usersToRemoveIDList);

            // Ideally, we wouldn't reset the entire bean state here, but it's
            // the easiest way at this point to refresh the selected user
            // group's state. Point of optimiztion for the future
            resetAndSelectUserGroup(selectedUserGroupDTO);
        } catch (ServiceNotReadyFault | UnauthorizedCallerFault | UnknownEntryFault | CommitFault | RemoteException exception) {
            this.reset();
            throw new UserGroupsViewException(exception);
        }
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupsViewBean#saveDefaultAccessRightsForSelectedUserGroup()
     */
    public void saveDefaultAccessRightsForSelectedUserGroup() throws UserGroupsViewException {
        IInternalExistingUserGroupBean selectedUserGroup = (IInternalExistingUserGroupBean) getSelectedUserGroup();
        UserGroupDTO selectedUserGroupDTO = selectedUserGroup.getWrappedUserGroupDTO();
        DefaultAccessAssignmentList defaultAccessAssignmentToSave = selectedUserGroup.getWrappedDefaultAccessAssignments();
        saveDefaultAccessRights(selectedUserGroupDTO, defaultAccessAssignmentToSave);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupsViewBean#removeDefaultAccessRightsPrincipalsFromSelectedUserGroup(java.util.Set)
     */
    public void removeDefaultAccessRightsPrincipalsFromSelectedUserGroup(Set principalsToRemove) throws UserGroupsViewException {
        if (principalsToRemove == null) {
            throw new NullPointerException("principalsToRemove cannot be null.");
        }

        IInternalExistingUserGroupBean selectedUserGroup = (IInternalExistingUserGroupBean) getSelectedUserGroup();
        UserGroupDTO selectedUserGroupDTO = selectedUserGroup.getWrappedUserGroupDTO();
        DefaultAccessAssignmentList currentDefaultAccessAssignments = selectedUserGroup.getWrappedDefaultAccessAssignments();

        ArrayList newDefaultAccessAssignments = new ArrayList();
        DefaultAccessAssignment[] defaultAccessAssignmentArray = currentDefaultAccessAssignments.getDefaultAccessAssignment();
        for (int i = 0; i < defaultAccessAssignmentArray.length; i++) {
            DefaultAccessAssignment nextCurrentAccessAssignment = defaultAccessAssignmentArray[i];
            BigInteger nextPricipalId = nextCurrentAccessAssignment.getPrinciapl().getID().getID();
            if (!principalsToRemove.contains(new Long(nextPricipalId.longValue()))) {
                newDefaultAccessAssignments.add(nextCurrentAccessAssignment);
            }
        }

        DefaultAccessAssignment[] newDefaultAccessAssignmentsArray = new DefaultAccessAssignment[newDefaultAccessAssignments.size()];
        newDefaultAccessAssignmentsArray = (DefaultAccessAssignment[]) newDefaultAccessAssignments.toArray(newDefaultAccessAssignmentsArray);
        DefaultAccessAssignmentList newDefaultAccessAssignmentsList = new DefaultAccessAssignmentList();
        newDefaultAccessAssignmentsList.setDefaultAccessAssignment(newDefaultAccessAssignmentsArray);

        saveDefaultAccessRights(selectedUserGroupDTO, newDefaultAccessAssignmentsList);

        try {
            // Ideally, we wouldn't reset the entire bean state here, but it's
            // the easiest way at this point to refresh the selected user
            // group's state. Point of optimiztion for the future
            this.resetAndSelectUserGroup(selectedUserGroupDTO);
        } catch (ServiceNotReadyFault | UnauthorizedCallerFault | RemoteException | CommitFault exception) {
            this.reset();
            throw new UserGroupsViewException(exception);
        }
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.faces.IResetableBean#reset()
     */
    public void reset() {
        this.newUserGroup = null;
        this.userGroupMenuItems = null;
        this.selectedUserGroupId = null;
        this.userGroupBeanCache.clear();
    }

    /**
     * Reset the cached data stored in this bean and set the user group with the
     * specified user id as the selected user group
     * 
     * @param userGroupId
     * @throws ServiceNotReadyFault
     * @throws UnauthorizedCallerFault
     * @throws RemoteException
     */
    public void resetAndSelectUserGroup(UserGroupDTO groupToSelect) throws ServiceNotReadyFault, UnauthorizedCallerFault, RemoteException, CommitFault {
        this.reset();
        this.loadUserGroupMenuItems();

        String userGroupId = groupToSelect.getId().toString();
        this.setSelectedUserGroup(userGroupId);
        addUserGroupToCache(groupToSelect);
    }

    /**
     * Save the specified default access rights for the specified user group
     * 
     * @param selectedUserGroupDTO
     * @param defaultAccessAssignmentToSave
     * @throws UserGroupsViewException
     */
    private void saveDefaultAccessRights(UserGroupDTO selectedUserGroupDTO, DefaultAccessAssignmentList defaultAccessAssignmentToSave) throws UserGroupsViewException {
        IUserGroupServiceFacade userGroupServiceFacade = getUserGroupServiceFacade();

        try {
            userGroupServiceFacade.setDefaultAccessAssignments(selectedUserGroupDTO, defaultAccessAssignmentToSave);
        } catch (ServiceNotReadyFault | UnauthorizedCallerFault | UnknownEntryFault | CommitFault | RemoteException exception) {
            this.reset();
            throw new UserGroupsViewException(exception);
        }
    }

    /**
     * Load the list of group menu items
     * 
     * @throws RemoteException
     * @throws UnauthorizedCallerFault
     * @throws CommitFault
     * @throws ServiceNotReadyFault
     */
    private void loadUserGroupMenuItems() throws ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException {
        IUserGroupServiceFacade userGroupServiceFacade = getUserGroupServiceFacade();
        UserGroupReducedList rawGroupMenuItems = userGroupServiceFacade.getAllGroups();
        UserGroupReduced[] rawGroupMenuItemsArray = rawGroupMenuItems.getUserGroupReduced();
        if (rawGroupMenuItemsArray == null) {
            rawGroupMenuItemsArray = new UserGroupReduced[0];
        }

        this.userGroupMenuItems = new UserGroupReducedToGroupMenuItemTranslatingDataModel(rawGroupMenuItemsArray);
    }

    /**
     * Select the first group in the group menu item list
     * 
     * @throws RemoteException
     */
    private void selectAndLoadFirstUserGroup() throws RemoteException, UnauthorizedCallerFault, CommitFault, UnknownEntryFault, ServiceNotReadyFault {
        selectFirstUserGroup();
        loadSelectedUserGroupIfNecessary();
    }

    /**
     * Select the first user group in the menu item list
     */
    private void selectFirstUserGroup() {
        if (!this.userGroupMenuItems.isEmpty()) {
            IInternalUserGroupMenuItemBean firstGroupMenuItem = (IInternalUserGroupMenuItemBean) this.userGroupMenuItems.getFirstMenuItem();
            this.setSelectedUserGroup(firstGroupMenuItem.getUserGroupId());
        } else {
            createAndSelectNewUserGroup();
        }
    }

    /**
     * Load the selected group into memory
     * 
     * @throws RemoteException
     */
    private void loadSelectedUserGroupIfNecessary() throws RemoteException, ServiceNotReadyFault, CommitFault, UnknownEntryFault, UnauthorizedCallerFault {
        if ((this.selectedUserGroupId != null) && (!this.userGroupBeanCache.containsKey(this.selectedUserGroupId))) {
            IInternalUserGroupMenuItemBean selectedMenuItem = this.userGroupMenuItems.getGroupMenuItem(selectedUserGroupId);
            loadUserGroupForUserGroupMenuItem(selectedMenuItem);
        }
    }

    /**
     * Load the group data associated with the specified group menu items
     * 
     * @param groupMenuItem
     *            the menu item for which to load group data
     * @throws RemoteException
     */
    private void loadUserGroupForUserGroupMenuItem(IInternalUserGroupMenuItemBean groupMenuItem) throws RemoteException, CommitFault, UnauthorizedCallerFault, UnknownEntryFault, ServiceNotReadyFault {
        IUserGroupServiceFacade groupServiceFacade = getUserGroupServiceFacade();
        UserGroupDTO loadedGroup = groupServiceFacade.getGroup(groupMenuItem.getWrappedUserGroupReduced());
        addUserGroupToCache(loadedGroup);
    }

    /**
     * Create a user group bean from the specified user group data and add it to
     * the user group cache
     * 
     * @param loadedGroup
     */
    private void addUserGroupToCache(UserGroupDTO loadedGroup) {
        ExistingUserGroupBeanImpl userGroupBean = new ExistingUserGroupBeanImpl(loadedGroup);
        this.userGroupBeanCache.put(userGroupBean.getUserGroupId(), userGroupBean);
    }

    /**
     * Retrieve the selected user group as the underlying user group dto. Will
     * throw an IllegalStateException if the selected user group is new (e.g.
     * does not have an underlying user group dto)
     * 
     * @return the selected user group as the underlying user group dto
     * @throws IllegalStateException
     *             if the selected user group is new
     */
    private UserGroupDTO getSelectedUserGroupDTO() throws IllegalStateException {
        IUserGroupBean selectedUserGroup = getSelectedUserGroup();
        if (selectedUserGroup.isNew()) {
            throw new IllegalStateException("Cannot add members to non-persistent (new) user group.");
        }

        UserGroupDTO selectedUserGroupDTO = ((IInternalExistingUserGroupBean) selectedUserGroup).getWrappedUserGroupDTO();
        return selectedUserGroupDTO;
    }

    /**
     * Retrieve the Group Service Facade
     * 
     * @return the Group Service Facade
     */
    private IUserGroupServiceFacade getUserGroupServiceFacade() {
        return (IUserGroupServiceFacade) ComponentManagerFactory.getComponentManager().getComponent(UserGroupServiceFacadeImpl.class);
    }

    /**
     * Retrieve a reference to a Log
     * 
     * @return a reference to a Log
     */
    private Log getLog() {
        return LOG;
    }

    /**
     * A data model which translates UserGroupReduced instances to
     * IInternalGroupItemMenuBean instances
     * 
     * @author sgoldstein
     */
    private class UserGroupReducedToGroupMenuItemTranslatingDataModel extends ProxyingDataModel {

        private Map viewedGroupMenuItems = new HashMap();

        private UserGroupReducedToGroupMenuItemTranslatingDataModel(UserGroupReduced[] rawGroupMenuItemsArray) {
            super(new ArrayDataModel(rawGroupMenuItemsArray));
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
         */
        protected Object proxyRowData(Object rawData) {
            if (rawData == null) {
                throw new NullPointerException("rawData cannot be null.");
            }

            UserGroupMenuItemBeanImpl groupMenuItemToReturn = new UserGroupMenuItemBeanImpl((UserGroupReduced) rawData);
            this.viewedGroupMenuItems.put(groupMenuItemToReturn.getUserGroupId(), groupMenuItemToReturn);

            return groupMenuItemToReturn;
        }

        /**
         * Retrieve the group menu item with the specified id. Note that this
         * group menu item must have already been viewed
         * 
         * @param groupID
         *            the id of the group associated with the viewed menu item
         * @return the viewed menu item associated with the group of the
         *         specified id
         */
        private IInternalUserGroupMenuItemBean getGroupMenuItem(String groupID) {
            if (!this.viewedGroupMenuItems.containsKey(groupID)) {
                throw new IllegalArgumentException("Group with ID, " + groupID + ", has not been viewed.");
            }

            return (IInternalUserGroupMenuItemBean) this.viewedGroupMenuItems.get(groupID);
        }

        /**
         * Determine if this data model is empty
         * 
         * @return true if empty; false otherwise
         */
        public boolean isEmpty() {
            return (this.getRowCount() <= 0);
        }

        /**
         * Retrieve the group menu item in the first row
         * 
         * @return the group menu item in the first row
         */
        public IInternalUserGroupMenuItemBean getFirstMenuItem() {
            this.setRowIndex(0);
            return (IInternalUserGroupMenuItemBean) this.getRowData();
        }

        /**
         * Retrieve the wrapped UserGroupReduced[] representing the groups in
         * the system
         * 
         * @return the wrapped UserGroupReduced[] representing the groups in the
         *         system
         */
        public UserGroupReduced[] getWrappedGroupSubjects() {
            return (UserGroupReduced[]) super.getWrappedDataModel().getWrappedData();
        }
    }

}
