package com.bluejungle.destiny.mgmtconsole.usersandroles.groups;

import java.util.Set;

import javax.faces.model.DataModel;

import com.bluejungle.destiny.webui.controls.UITabbedPane;

public interface IUserGroupsViewBean {

    /**
     * Retrieve the tabbed pane component for the user groups view
     * 
     * @return the tabbed pane component for the user groups view
     */
    public UITabbedPane getUserGroupsViewTabbedPane();

    /**
     * Retrieve the name of the Default Rights tab.
     * 
     * @return the Default Rights tab name.
     */
    public String getDefaultRightsTabName();

    /**
     * Retrieve the name of the General tab
     * 
     * @return the general tab name.
     */
    public String getGeneralTabName();

    /**
     * Retrieve the name of the members tab.
     * 
     * @return the members tab name.
     */
    public String getMembersTabName();

    /**
     * Retrieve the tabbed pane component for the user groups view
     * 
     * @return the tabbed pane component for the user groups view
     */
    public void setUserGroupsViewTabbedPane(UITabbedPane tabbedPane);

    /**
     * Retrieve a data model of all user groups in the system
     * 
     * @return a data model of all user groups in the system as
     *         IUserGroupMenuItemBean instances
     * @throws UserGroupsViewException
     *             if an error occurs while retrieving the groups
     */
    public DataModel getUserGroups() throws UserGroupsViewException;

    /**
     * Remove the members specified by the provided IDs from the selected user
     * group
     * 
     * @param memberIds
     *            the IDs of the members to remove
     * @throws UserGroupsViewException
     *             if an error occurs while removing the members
     */
    public void removeMembersFromSelectedUserGroup(Set memberIds) throws UserGroupsViewException;

    /**
     * Delete the selected user group
     * 
     * @throws UserGroupsViewException
     *             if an error occurs while deleting the selected user group
     */
    public void deleteSelectedUserGroup() throws UserGroupsViewException;

    /**
     * Set the user group specified by the provided ID as the selected user
     * group in the user group view
     * 
     * @param selectedUserGroupId
     *            the ID of the group to select
     */
    public void setSelectedUserGroup(String selectedUserGroupId);

    /**
     * Retrieve the currently selected user group as a IUserGroupBean instance
     * 
     * @return the currently selected user group as a IUserGroupBean instance
     */
    public IUserGroupBean getSelectedUserGroup();

    /**
     * Save changes to the currently selected group
     * 
     * @throws UserGroupsViewException
     *             if an error occurs while saving the selected group
     * @throws NonUniqueUserGroupTitleException
     *             if the title set on the selected user group is not unique
     */
    public void saveSelectedUserGroup() throws UserGroupsViewException, NonUniqueUserGroupTitleException;

    /**
     * Invoke to create a IUserGroupBean instance for a new user group and
     * select it in the user groups view.
     */
    public void createAndSelectNewUserGroup();

    /**
     * Insert a newly created user group. {@see #createAndSelectNewUserGroup()}
     * must be called first to create a new user group
     * 
     * @throws UserGroupsViewException
     *             if an error occurs while inserting the new user group
     * @throws NonUniqueUserGroupTitleException
     *             if the title set on the selected user group is not unique
     */
    public void insertSelectedNewUserGroup() throws UserGroupsViewException, NonUniqueUserGroupTitleException;

    /**
     * Called to clear a newly created user group without inserting it if
     * {@see #createAndSelectNewUserGroup()}has been called
     */
    public void clearSelectedNewUserGroup();

    /**
     * Save changes to the default access rights for the currently selected
     * group
     * 
     * @throws UserGroupsViewException
     *             if an error occurs whle save the default access right
     */
    public void saveDefaultAccessRightsForSelectedUserGroup() throws UserGroupsViewException;

    /**
     * Remove the specified users and groups from the default access rights list
     * for the current group
     * 
     * @param principalsToRemove
     *            the principals to remove
     */
    public void removeDefaultAccessRightsPrincipalsFromSelectedUserGroup(Set principalsToRemove) throws UserGroupsViewException;
}
