package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.datapicker;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;

import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean;
import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IInternalExistingUserGroupBean;
import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IInternalUserGroupsViewBean;
import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade;
import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.UserGroupServiceFacadeImpl;
import com.bluejungle.destiny.services.management.UserRoleServiceException;
import com.bluejungle.destiny.services.management.types.CommitFault;
import com.bluejungle.destiny.services.management.types.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.types.UnauthorizedCallerFault;
import com.bluejungle.destiny.services.management.types.UnknownEntryFault;
import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.services.management.types.UserGroupDTO;
import com.bluejungle.destiny.services.management.types.UserGroupReduced;
import com.bluejungle.destiny.services.management.types.UserGroupReducedList;
import com.bluejungle.destiny.services.policy.types.AccessList;
import com.bluejungle.destiny.services.policy.types.DefaultAccessAssignment;
import com.bluejungle.destiny.services.policy.types.DefaultAccessAssignmentList;
import com.bluejungle.destiny.services.policy.types.Principal;
import com.bluejungle.destiny.services.policy.types.PrincipalType;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.BaseSelectableItemSource;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.SelectableItemSourceException;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.DisablingItemDataModel;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.IDisableableSelectableItem;
import com.bluejungle.destiny.webui.framework.data.LinkingDataModel;
import com.bluejungle.destiny.webui.framework.data.MemorizingDataModel;
import com.bluejungle.destiny.webui.framework.data.ProxyingDataModel;
import com.bluejungle.framework.comp.ComponentManagerFactory;

/**
 * Selectable Item source for principals (user and user group reveiving access
 * rights)
 * 
 * @author sgoldstein
 */
public class PrincipalSelectableItemSourceImpl extends BaseSelectableItemSource {

    private static final String ITEM_NAME = "principals";

    private MemorizingDataModel memorizedDataModel;
    private IInternalUserGroupsViewBean userGroupsViewBean;
    private DefaultAccessAssignmentList currentAccessAssignments;

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.BaseSelectableItemSource#getItemName()
     */
    protected String getItemName() {
        return ITEM_NAME;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#getSelectableItems(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec,
     *      com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList)
     */
    public DataModel getSelectableItems(ISearchBucketSearchSpec searchSpec, ISelectedItemList selectedItems) throws SelectableItemSourceException {
        if (searchSpec == null) {
            throw new NullPointerException("searchSpec cannot be null.");
        }

        if (selectedItems == null) {
            throw new NullPointerException("selectedItems cannot be null.");
        }

        DataModel modelToReturn = null;

        try {
            UserDTO[] matchingUsersArray = UserGroupDataPickerUtils.getUsersForSearchBucketSearchSpec(searchSpec);

            IUserGroupServiceFacade userGroupServiceFacade = getUserGroupServiceFacade();
            UserGroupReducedList userGroupQueryResults = userGroupServiceFacade.getUserGroupsForSearchBucketSearchSpec(searchSpec);

            modelToReturn = buildSelectableItemsDataModel(matchingUsersArray, userGroupQueryResults, selectedItems);
        } catch (ServiceNotReadyFault | UnknownEntryFault | CommitFault | UnauthorizedCallerFault | RemoteException | UserRoleServiceException exception) {
            throw new SelectableItemSourceException(exception);
        }

        return modelToReturn;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#getSelectableItems(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec,
     *      com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList)
     */
    public DataModel getSelectableItems(IFreeFormSearchSpec searchSpec, ISelectedItemList selectedItems) throws SelectableItemSourceException {
        if (searchSpec == null) {
            throw new NullPointerException("searchSpec cannot be null.");
        }

        if (selectedItems == null) {
            throw new NullPointerException("selectedItems cannot be null.");
        }

        DataModel modelToReturn = null;

        try {
            UserDTO[] matchingUsersArray = UserGroupDataPickerUtils.getUsersForFreeFormSearchSpec(searchSpec);

            IUserGroupServiceFacade userGroupServiceFacade = getUserGroupServiceFacade();
            UserGroupReducedList userGroupQueryResults = userGroupServiceFacade.getUserGroupsForFreeFormSearchSpec(searchSpec);

            modelToReturn = buildSelectableItemsDataModel(matchingUsersArray, userGroupQueryResults, selectedItems);
        } catch (ServiceNotReadyFault | UnknownEntryFault | CommitFault | UnauthorizedCallerFault | RemoteException | UserRoleServiceException exception) {
            throw new SelectableItemSourceException(exception);
        }

        return modelToReturn;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#generateSelectedItems(java.lang.String)
     */
    public Set generateSelectedItems(String selectableItemId) throws SelectableItemSourceException {
        if (!this.memorizedDataModel.isDataItemMemorized(selectableItemId)) {
            throw new SelectableItemSourceException("Unknown id, " + selectableItemId);
        }

        ISelectedItem itemToReturn = null;
        ISelectableItem itemSelected = (ISelectableItem) this.memorizedDataModel.getMemorizedDataItem(selectableItemId);
        if (itemSelected instanceof UserPrincipalSelectableItemImpl) {
            UserPrincipalSelectableItemImpl itemSelectedTypeCasted = (UserPrincipalSelectableItemImpl) itemSelected;
            itemToReturn = new UserPrincipalSelectedItemImpl(itemSelectedTypeCasted.getWrappedUser());
        } else if (itemSelected instanceof UserGroupPrincipalSelectableItemImpl) {
            UserGroupPrincipalSelectableItemImpl itemSelectedTypeCasted = (UserGroupPrincipalSelectableItemImpl) itemSelected;
            itemToReturn = new UserGroupPrincipalSelectedItemImpl(itemSelectedTypeCasted.getWrappedUserGroup());
        } else {
            throw new IllegalStateException("Unknown selected item class: " + itemSelected.getClass());
        }

        return Collections.singleton(itemToReturn);
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#storeSelectedItems(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList)
     */
    public String storeSelectedItems(ISelectedItemList selectedItems) throws SelectableItemSourceException {
        if (selectedItems == null) {
            throw new NullPointerException("selectedItems cannot be null.");
        }

        try {
            DefaultAccessAssignmentList accessAssignmentsToSetList = buildAccessAssignmentListToSet(selectedItems);

            IUserGroupServiceFacade userGroupServiceFacade = getUserGroupServiceFacade();
            userGroupServiceFacade.setDefaultAccessAssignments(getCurrentlySelectedUserGroup(), accessAssignmentsToSetList);

            IInternalUserGroupsViewBean userGroupsViewBean = getUserGroupsViewBean();
            userGroupsViewBean.resetAndSelectUserGroup(getCurrentlySelectedUserGroup());
        } catch (RemoteException | ServiceNotReadyFault | UnauthorizedCallerFault | CommitFault | UnknownEntryFault exception) {
            throw new SelectableItemSourceException(exception);
        }

        return getReturnAction();
    }

    /**
     * @param selectedItems
     * @return
     * @throws RemoteException
     * @throws ServiceNotReadyFault
     * @throws UnknownEntryFault
     * @throws CommitFault
     * @throws UnauthorizedCallerFault
     */
    private DefaultAccessAssignmentList buildAccessAssignmentListToSet(ISelectedItemList selectedItems) throws RemoteException, ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault {
        if (selectedItems == null) {
            throw new NullPointerException("selectedItems cannot be null.");
        }

        int princpalsToAddSize = selectedItems.size();

        DefaultAccessAssignmentList currentAccessAssignmentsList = retrieveCurrentAccessAssignments();
        DefaultAccessAssignment[] currentAccessAssignments = currentAccessAssignmentsList.getDefaultAccessAssignment();

        DefaultAccessAssignment[] accessAssignmentsToSet;
        int startPos = 0;
        if (currentAccessAssignments == null) {
            accessAssignmentsToSet = new DefaultAccessAssignment[princpalsToAddSize];
        } else {
            int currentAccessAssignmentsArrayLength = currentAccessAssignments.length;
            accessAssignmentsToSet = new DefaultAccessAssignment[currentAccessAssignmentsArrayLength + princpalsToAddSize];
            System.arraycopy(currentAccessAssignments, 0, accessAssignmentsToSet, 0, currentAccessAssignmentsArrayLength);
            startPos = currentAccessAssignmentsArrayLength;
        }

        Iterator selectedItemIterator = selectedItems.iterator();
        for (int i = startPos; selectedItemIterator.hasNext(); i++) {
            ISelectedItem nextSelectedItem = (ISelectedItem) selectedItemIterator.next();
            Principal nextPrincipalToAdd = null;
            if (nextSelectedItem instanceof UserPrincipalSelectedItemImpl) {
                UserPrincipalSelectedItemImpl nextSelectedItemTypeCasted = (UserPrincipalSelectedItemImpl) nextSelectedItem;
                UserDTO wrappedUser = nextSelectedItemTypeCasted.getWrappedUser();
                nextPrincipalToAdd = new Principal();
                nextPrincipalToAdd.setID(wrappedUser.getId());
                nextPrincipalToAdd.setDisplayName(nextSelectedItem.getDisplayValue());
                nextPrincipalToAdd.setType(PrincipalType.USER);
            } else if (nextSelectedItem instanceof UserGroupPrincipalSelectedItemImpl) {
                UserGroupPrincipalSelectedItemImpl nextSelectedItemTypeCasted = (UserGroupPrincipalSelectedItemImpl) nextSelectedItem;
                UserGroupReduced wrappedUserGroup = nextSelectedItemTypeCasted.getWrappedUserGroup();
                nextPrincipalToAdd = new Principal();
                nextPrincipalToAdd.setID(wrappedUserGroup.getId());
                nextPrincipalToAdd.setDisplayName(nextSelectedItem.getDisplayValue());
                nextPrincipalToAdd.setType(PrincipalType.USER_GROUP);
            } else {
                throw new IllegalStateException("Unknown selected item class: " + nextSelectedItem.getClass());
            }

            accessAssignmentsToSet[i] = new DefaultAccessAssignment();
            accessAssignmentsToSet[i].setPrinciapl(nextPrincipalToAdd);
            accessAssignmentsToSet[i].setDefaultAccess(new AccessList());
        }

        DefaultAccessAssignmentList defaultAccessAssignmentList = new DefaultAccessAssignmentList();
        defaultAccessAssignmentList.setDefaultAccessAssignment(accessAssignmentsToSet);
        return defaultAccessAssignmentList;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#reset()
     */
    public void reset() {
        super.reset();

        this.currentAccessAssignments = null;
    }

    public void setUserGroupsViewBean(IInternalUserGroupsViewBean userGroupsViewBean) {
        if (userGroupsViewBean == null) {
            throw new NullPointerException("userGroupsViewBean cannot be null.");
        }

        this.userGroupsViewBean = userGroupsViewBean;
    }

    /**
     * @param matchingUsersArray
     * @param userGroupQueryResults
     * @param selectedItems
     * @return
     * @throws ServiceException
     * @throws RemoteException
     * @throws UnauthorizedCallerFault
     * @throws CommitFault
     * @throws UnknownEntryFault
     * @throws ServiceNotReadyFault
     */
    private DataModel buildSelectableItemsDataModel(UserDTO[] matchingUsersArray, UserGroupReducedList userGroupQueryResults, ISelectedItemList selectedItems) throws ServiceNotReadyFault, UnknownEntryFault, CommitFault,
            UnauthorizedCallerFault, RemoteException {
        if (matchingUsersArray == null) {
            throw new NullPointerException("matchingUsersArray cannot be null.");
        }

        if (userGroupQueryResults == null) {
            throw new NullPointerException("userGroupQueryResults cannot be null.");
        }

        if (selectedItems == null) {
            throw new NullPointerException("selectedItems cannot be null.");
        }

        DataModel userPrincipalDataModel = buildUserPrincipalSelectableItemsDatamodel(matchingUsersArray);
        DataModel userGroupPrincipalDataModel = buildUserGroupPrincipalSelectableItemsDataModels(userGroupQueryResults);
        LinkingDataModel userGroupAndUserPrincipalDataModel = new LinkingDataModel(userGroupPrincipalDataModel, userPrincipalDataModel);

        DefaultAccessAssignmentList defaultAccessAssigmentsList = retrieveCurrentAccessAssignments();
        DataModel disablingItemDataModel = new DisablingItemDataModelImpl(userGroupAndUserPrincipalDataModel, selectedItems, defaultAccessAssigmentsList);

        this.memorizedDataModel = new MemorizingDataModel(disablingItemDataModel);

        return this.memorizedDataModel;
    }

    /**
     * @param matchingUsersArray
     * @return
     */
    private DataModel buildUserPrincipalSelectableItemsDatamodel(UserDTO[] matchingUsersArray) {
        return new UserPrincipalDataModel(matchingUsersArray == null ? new UserDTO[] {} : matchingUsersArray);
    }

    /**
     * @param userGroupQueryResults
     * @return
     */
    private DataModel buildUserGroupPrincipalSelectableItemsDataModels(UserGroupReducedList userGroupQueryResults) {
        UserGroupReduced[] userGroupPrincipals = userGroupQueryResults.getUserGroupReduced();
        if (userGroupPrincipals == null) {
            userGroupPrincipals = new UserGroupReduced[0];
        }

        return new UserGroupPrincipalDataModel(userGroupPrincipals);
    }

    /**
     * @return
     */
    private UserGroupDTO getCurrentlySelectedUserGroup() {
        IUserGroupBean selectedUserGroup = getUserGroupsViewBean().getSelectedUserGroup();
        if (!(selectedUserGroup instanceof IInternalExistingUserGroupBean)) {
            throw new IllegalStateException("Users cannot be added to an unpersisted group");
        }

        return ((IInternalExistingUserGroupBean) selectedUserGroup).getWrappedUserGroupDTO();
    }

    private IInternalUserGroupsViewBean getUserGroupsViewBean() {
        return this.userGroupsViewBean;
    }

    /**
     * @return
     * @throws RemoteException
     * @throws ServiceNotReadyFault
     * @throws UnknownEntryFault
     * @throws CommitFault
     * @throws UnauthorizedCallerFault
     * @throws ServiceException
     */
    private DefaultAccessAssignmentList retrieveCurrentAccessAssignments() throws RemoteException, ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault {
        if (this.currentAccessAssignments == null) {
            UserGroupDTO selectedUserGroupDTO = getCurrentlySelectedUserGroup();

            IUserGroupServiceFacade userGroupServiceFacade = getUserGroupServiceFacade();
            this.currentAccessAssignments = userGroupServiceFacade.getDefaultAccessAssignments(selectedUserGroupDTO);
        }

        return this.currentAccessAssignments;
    }

    /**
     * Retrieve the Group Service Facade
     * 
     * @return
     */
    private IUserGroupServiceFacade getUserGroupServiceFacade() {
        return ComponentManagerFactory.getComponentManager().getComponent(UserGroupServiceFacadeImpl.class);
    }

    private class UserPrincipalDataModel extends ProxyingDataModel {

        /**
         * 
         * Create an instance of UserGroupPrincipalDataModel
         * 
         * @param userGroupPrincipals
         */
        private UserPrincipalDataModel(UserDTO[] userPrincipals) {
            super(new ArrayDataModel(userPrincipals));
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
         */
        protected Object proxyRowData(Object rawData) {
            return new UserPrincipalSelectableItemImpl((UserDTO) rawData);
        }
    }

    private class UserGroupPrincipalDataModel extends ProxyingDataModel {

        /**
         * 
         * Create an instance of UserGroupPrincipalDataModel
         * 
         * @param userGroupPrincipals
         */
        private UserGroupPrincipalDataModel(UserGroupReduced[] userGroupPrincipals) {
            super(new ArrayDataModel(userGroupPrincipals));
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
         */
        protected Object proxyRowData(Object rawData) {
            return new UserGroupPrincipalSelectableItemImpl((UserGroupReduced) rawData);
        }
    }

    public class DisablingItemDataModelImpl extends DisablingItemDataModel {

        private final ISelectedItemList selectedItems;
        private final Set principalsAlreadyAssigned;

        /**
         * Create an instance of DisablingDataModelImpl
         * 
         * @param wrappedDataModel
         * @param selectedItems
         * @param usersAlreadySelected
         */
        public DisablingItemDataModelImpl(DataModel wrappedDataModel, ISelectedItemList selectedItems, DefaultAccessAssignmentList currentAccessAssignments) {
            super(wrappedDataModel);

            if (selectedItems == null) {
                throw new NullPointerException("selectedItems cannot be null.");
            }

            if (currentAccessAssignments == null) {
                throw new NullPointerException("existingUsers cannot be null.");
            }

            this.selectedItems = selectedItems;
            this.principalsAlreadyAssigned = new HashSet();
            DefaultAccessAssignment[] defaultAccessAssigments = currentAccessAssignments.getDefaultAccessAssignment();
            if (defaultAccessAssigments != null) {
                for (int i = 0; i < defaultAccessAssigments.length; i++) {
                    this.principalsAlreadyAssigned.add(defaultAccessAssigments[i].getPrinciapl().getID());
                }
            }
        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.DisablingItemDataModel#shouldItemBeDisabled(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.IDisableableSelectableItem)
         */
        protected boolean shouldItemBeDisabled(IDisableableSelectableItem disableableItem) {
            if (disableableItem == null) {
                throw new NullPointerException("disableableItem cannot be null.");
            }

            boolean valueToReturn = true;

            String selectableItemId = disableableItem.getId();

            if (!this.selectedItems.containsSelectedItem(selectableItemId)) {
                if (disableableItem instanceof UserGroupPrincipalSelectableItemImpl) {
                    UserGroupPrincipalSelectableItemImpl userGroupPrincipalDisableableItem = (UserGroupPrincipalSelectableItemImpl) disableableItem;
                    if (!principalsAlreadyAssigned.contains(userGroupPrincipalDisableableItem.getWrappedUserGroup().getId())) {
                        valueToReturn = false;
                    }
                } else if (disableableItem instanceof UserPrincipalSelectableItemImpl) {
                    UserPrincipalSelectableItemImpl userPrincipalDisableableItem = (UserPrincipalSelectableItemImpl) disableableItem;
                    if (!principalsAlreadyAssigned.contains(userPrincipalDisableableItem.getWrappedUser().getId())) {
                        valueToReturn = false;
                    }
                }
            }

            return valueToReturn;
        }
    }
}
