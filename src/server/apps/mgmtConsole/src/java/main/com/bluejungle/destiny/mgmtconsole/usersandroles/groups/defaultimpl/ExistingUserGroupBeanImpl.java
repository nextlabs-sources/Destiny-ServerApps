package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import java.rmi.RemoteException;

import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;

import com.bluejungle.destiny.services.management.types.CommitFault;
import com.bluejungle.destiny.services.management.types.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.types.UnauthorizedCallerFault;
import com.bluejungle.destiny.services.management.types.UnknownEntryFault;
import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.services.management.types.UserDTOList;
import com.bluejungle.destiny.services.management.types.UserGroupDTO;
import com.bluejungle.destiny.services.policy.types.DefaultAccessAssignment;
import com.bluejungle.destiny.services.policy.types.DefaultAccessAssignmentList;
import com.bluejungle.destiny.webui.framework.data.ProxyingDataModel;
import com.bluejungle.framework.comp.ComponentManagerFactory;

/**
 * Default implementation of the
 * {@see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IInternalExistingUserGroupBean}
 * 
 * @author sgoldstein
 */
public class ExistingUserGroupBeanImpl implements IInternalExistingUserGroupBean {

    private UserGroupDTO wrappedUserGroupDTO;
    private DataModel userGroupMembers;
    private DefaultAccessAssignmentDataModel defaultAccessAssignments;

    /**
     * Create an instance of GroupBeanImpl
     * 
     * @param userGroupDTO
     */
    public ExistingUserGroupBeanImpl(UserGroupDTO userGroupDTO) {
        if (userGroupDTO == null) {
            throw new NullPointerException("loadedGroup cannot be null.");
        }

        this.wrappedUserGroupDTO = userGroupDTO;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean#getDefaultAccessAssignments()
     */
    public DataModel getDefaultAccessAssignments() {
        if (this.defaultAccessAssignments == null) {

            try {
                // FIX ME - Need to load is proper place (before page is
                // displayed) and then properly handle exceptions
                loadDefaultAccessAssignments();
            } catch (RemoteException | ServiceNotReadyFault | UnknownEntryFault | CommitFault | UnauthorizedCallerFault exception) {
                // FIX ME - Need to load is proper place (before page is
                // displayed) and then properly handle exceptions
                this.defaultAccessAssignments = new DefaultAccessAssignmentDataModel(new DefaultAccessAssignment[0]);
            }
        }

        return this.defaultAccessAssignments;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean#getUserGroupId()
     */
    public String getUserGroupId() {
        return this.wrappedUserGroupDTO.getId().toString();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean#getUserGroupTitle()
     */
    public String getUserGroupTitle() {
        return this.wrappedUserGroupDTO.getTitle();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean#getUserGroupDescription()
     */
    public String getUserGroupDescription() {
        return this.wrappedUserGroupDTO.getDescription();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean#getMembers()
     */
    public DataModel getMembers() {
        if (this.userGroupMembers == null) {
            try {
                // FIX ME - Need to load is proper place (before page is
                // displayed) and then properly handle exceptions
                loadUserGroupMembers();
            } catch (RemoteException | CommitFault | UnauthorizedCallerFault | UnknownEntryFault | ServiceNotReadyFault exception) {
                // FIX ME - Need to load is proper place (before page is
                // displayed) and then properly handle exceptions
                this.userGroupMembers = new UserGroupMembersDataModel(new UserDTO[0]);
            }
        }

        return this.userGroupMembers;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean#isExternallyManaged()
     */
    public boolean isExternallyManaged() {
        return this.wrappedUserGroupDTO.getExternallyLinked();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean#setUserGroupTitle(java.lang.String)
     */
    public void setUserGroupTitle(String titleToSet) {
        if (titleToSet == null) {
            throw new NullPointerException("titleToSet cannot be null.");
        }

        this.wrappedUserGroupDTO.setTitle(titleToSet);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean#getUserGroupDescription()
     */
    public void setUserGroupDescription(String description) {
        if (description == null) {
            throw new NullPointerException("description cannot be null.");
        }

        this.wrappedUserGroupDTO.setDescription(description);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean#getUserGroupQualifiedExternalName()
     */
    public String getUserGroupQualifiedExternalName() {
        if (!isExternallyManaged()) {
            throw new UnsupportedOperationException("Groups which are not externally managed do not have external names");
        }

        return this.wrappedUserGroupDTO.getQualifiedExternalName();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean#isNew()
     */
    public boolean isNew() {
        return false;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IInternalExistingUserGroupBean#getWrappedUserGroupDTO()
     */
    public UserGroupDTO getWrappedUserGroupDTO() {
        return this.wrappedUserGroupDTO;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IInternalExistingUserGroupBean#getWrappedDefaultAccessAssignments()
     */
    public DefaultAccessAssignmentList getWrappedDefaultAccessAssignments() {
        return this.defaultAccessAssignments.getWrappedDefaultAccessAssignments();
    }

    /**
     * Load the members of this user group
     * 
     * @throws ServiceException
     * @throws RemoteException
     * @throws UnauthorizedCallerFault
     * @throws CommitFault
     * @throws UnknownEntryFault
     * @throws ServiceNotReadyFault
     * 
     */
    private void loadUserGroupMembers() throws CommitFault, UnauthorizedCallerFault, UnknownEntryFault, RemoteException, ServiceNotReadyFault {
        IUserGroupServiceFacade userGroupServiceFacade = getUserGroupServiceFacade();
        UserDTOList usersInGroupList = userGroupServiceFacade.getUsersInUserGroup(getWrappedUserGroupDTO());
        UserDTO[] usersInGroup = usersInGroupList.getUsers();
        if (usersInGroup == null) {
            usersInGroup = new UserDTO[0];
        }

        this.userGroupMembers = new UserGroupMembersDataModel(usersInGroup);
    }

    /**
     * Load the default access assignments for this user group
     * 
     * @throws ServiceException
     * @throws RemoteException
     * @throws UnauthorizedCallerFault
     * @throws CommitFault
     * @throws UnknownEntryFault
     * @throws ServiceNotReadyFault
     * 
     */
    private void loadDefaultAccessAssignments() throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException {
        IUserGroupServiceFacade userGroupServiceFacade = getUserGroupServiceFacade();
        DefaultAccessAssignmentList defaultAccessAssignmentsList = userGroupServiceFacade.getDefaultAccessAssignments(getWrappedUserGroupDTO());
        DefaultAccessAssignment[] defaultAccessAssigments = defaultAccessAssignmentsList.getDefaultAccessAssignment();
        if (defaultAccessAssigments == null) {
            defaultAccessAssigments = new DefaultAccessAssignment[0];
        }

        this.defaultAccessAssignments = new DefaultAccessAssignmentDataModel(defaultAccessAssigments);
    }

    /**
     * Retrieve the Group Service Facade
     * 
     * @return
     */
    private IUserGroupServiceFacade getUserGroupServiceFacade() {
        return (IUserGroupServiceFacade) ComponentManagerFactory.getComponentManager().getComponent(UserGroupServiceFacadeImpl.class);
    }

    private class DefaultAccessAssignmentDataModel extends ProxyingDataModel {

        /**
         * Create an instance of DefaultAccessAssignmentDataModel
         * 
         * @param wrappedDataModel
         */
        public DefaultAccessAssignmentDataModel(DefaultAccessAssignment[] defaultAccessAssignments) {
            super(new ArrayDataModel(defaultAccessAssignments));
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
         */
        protected Object proxyRowData(Object rawData) {
            return new DefaultAccessAssignmentBeanImpl((DefaultAccessAssignment) rawData);
        }

        /**
         * Retrieve the wrapped default access assignments
         * 
         * @return the wrapped default access assignments
         */
        private DefaultAccessAssignmentList getWrappedDefaultAccessAssignments() {
            DefaultAccessAssignment[] defaultAccessAssignments = new DefaultAccessAssignment[this.getRowCount()];
            for (int i=0; i<this.getRowCount(); i++) {
                this.setRowIndex(i);
                IInternalDefaultAccessAssignmentBean nextDefaultAccessAssignmentBean = (IInternalDefaultAccessAssignmentBean) this.getRowData();
                defaultAccessAssignments[i] = nextDefaultAccessAssignmentBean.getWrappedDefaultAccessAssignment();
            }

            DefaultAccessAssignmentList assignmentList = new DefaultAccessAssignmentList();
            assignmentList.setDefaultAccessAssignment(defaultAccessAssignments);

            return assignmentList;
        }
    }

    private class UserGroupMembersDataModel extends ProxyingDataModel {

        /**
         * Create an instance of DefaultAccessAssignmentDataModel
         * 
         * @param wrappedDataModel
         */
        public UserGroupMembersDataModel(UserDTO[] userGroupMembers) {
            super(new ArrayDataModel(userGroupMembers));
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
         */
        protected Object proxyRowData(Object rawData) {
            return new MemberBeanImpl((UserDTO) rawData);
        }
    }
}
