package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.datapicker;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;

import com.bluejungle.destiny.framework.types.ID;
import com.bluejungle.destiny.framework.types.IDList;
import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean;
import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IInternalExistingUserGroupBean;
import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IInternalUserGroupsViewBean;
import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade;
import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.UserGroupServiceFacadeImpl;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.UserServiceFacadeImpl;
import com.bluejungle.destiny.services.management.UserRoleServiceException;
import com.bluejungle.destiny.services.management.types.CommitFault;
import com.bluejungle.destiny.services.management.types.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.types.UnauthorizedCallerFault;
import com.bluejungle.destiny.services.management.types.UnknownEntryFault;
import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.services.management.types.UserDTOList;
import com.bluejungle.destiny.services.management.types.UserGroupDTO;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.BaseSelectableItemSource;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.SelectableItemSourceException;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.DisablingItemDataModel;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.IDisableableSelectableItem;
import com.bluejungle.destiny.webui.framework.data.MemorizingDataModel;
import com.bluejungle.destiny.webui.framework.data.ProxyingDataModel;
import com.bluejungle.framework.comp.ComponentManagerFactory;

/**
 * Selectable Item source for potential members for users group
 * 
 * @author sgoldstein
 */
public class PotentialMemberSelectableItemSourceImpl extends BaseSelectableItemSource {

    private static final String ITEM_NAME = "potentialUserGroupMembers";

    private IInternalUserGroupsViewBean userGroupsViewBean;
    private MemorizingDataModel memorizedDataModel;

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
            modelToReturn = buildSelectableItemsDataModel(selectedItems, matchingUsersArray);
        } catch (UserRoleServiceException | RemoteException | ServiceNotReadyFault | UnknownEntryFault | CommitFault | UnauthorizedCallerFault exception) {
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
            modelToReturn = buildSelectableItemsDataModel(selectedItems, matchingUsersArray);
        } catch (ServiceNotReadyFault | UserRoleServiceException | RemoteException | UnknownEntryFault | CommitFault | UnauthorizedCallerFault exception) {
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

        PotentialMemberSelectableItemImpl itemSelected = (PotentialMemberSelectableItemImpl) this.memorizedDataModel.getMemorizedDataItem(selectableItemId);
        PotentialMemberSelectedItemImpl itemToReturn = new PotentialMemberSelectedItemImpl(itemSelected.getWrappedUser());

        return Collections.singleton(itemToReturn);
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#storeSelectedItems(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList)
     */
    public String storeSelectedItems(ISelectedItemList selectedItems) throws SelectableItemSourceException {
        if (selectedItems == null) {
            throw new NullPointerException("selectedItems cannot be null.");
        }

        IUserGroupBean selectedUserGroup = getUserGroupsViewBean().getSelectedUserGroup();
        if (!(selectedUserGroup instanceof IInternalExistingUserGroupBean)) {
            throw new IllegalStateException("Users cannot be added to an unpersisted group");
        }

        UserGroupDTO selectedUserGroupDTO = ((IInternalExistingUserGroupBean) selectedUserGroup).getWrappedUserGroupDTO();

        IUserGroupServiceFacade userGroupServiceFacade = getUserGroupServiceFacade();

        ID[] usersToAddIds = new ID[selectedItems.size()];
        Iterator selectedUsersIterator = selectedItems.iterator();
        for (int i = 0; selectedUsersIterator.hasNext(); i++) {
            PotentialMemberSelectedItemImpl nextUserToAdd = (PotentialMemberSelectedItemImpl) selectedUsersIterator.next();
            usersToAddIds[i] = nextUserToAdd.getWrappedUser().getId();
        }

        IDList usersToAddIDList = new IDList();
        usersToAddIDList.setIDList(usersToAddIds);
        try {
            getUserGroupServiceFacade().addUsersToUserGroup(selectedUserGroupDTO, usersToAddIDList);
            getUserGroupsViewBean().resetAndSelectUserGroup(selectedUserGroupDTO);
        } catch (ServiceNotReadyFault | UnauthorizedCallerFault | CommitFault | UnknownEntryFault | RemoteException exception) {
            throw new SelectableItemSourceException(exception);
        }

        return getReturnAction();
    }

    /**
     * @param selectedItems
     * @param matchingUsersArray
     * @return
     * @throws RemoteException
     * @throws UnauthorizedCallerFault
     * @throws CommitFault
     * @throws UnknownEntryFault
     * @throws ServiceNotReadyFault
     */
    private DataModel buildSelectableItemsDataModel(ISelectedItemList selectedItems, UserDTO[] matchingUsers) throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException {
        IUserGroupServiceFacade userGroupServiceFacade = getUserGroupServiceFacade();

        DataModel usersDataModel = new PotentialMemberSelectableItemsDataModel(matchingUsers == null ? new UserDTO[] {} : matchingUsers);
        IUserGroupBean selectedUserGroup = getUserGroupsViewBean().getSelectedUserGroup();
        if (!(selectedUserGroup instanceof IInternalExistingUserGroupBean)) {
            throw new IllegalStateException("Users cannot be added to an unpersisted group");
        }

        UserGroupDTO selectedUserGroupDTO = ((IInternalExistingUserGroupBean) selectedUserGroup).getWrappedUserGroupDTO();
        UserDTOList usersAlreadyInGroupList = userGroupServiceFacade.getUsersInUserGroup(selectedUserGroupDTO);
        UserDTO[] usersAlreadyInGroup = usersAlreadyInGroupList.getUsers();
        if (usersAlreadyInGroup == null) {
            usersAlreadyInGroup = new UserDTO[0];
        }

        DataModel disablingDataModel = new DisablingItemDataModelImpl(usersDataModel, selectedItems, usersAlreadyInGroup);

        this.memorizedDataModel = new MemorizingDataModel(disablingDataModel);

        return this.memorizedDataModel;
    }

    public void setUserGroupsViewBean(IInternalUserGroupsViewBean userGroupsViewBean) {
        if (userGroupsViewBean == null) {
            throw new NullPointerException("userGroupsViewBean cannot be null.");
        }

        this.userGroupsViewBean = userGroupsViewBean;
    }

    private IInternalUserGroupsViewBean getUserGroupsViewBean() {
        return this.userGroupsViewBean;
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
     * Retrieve the Group Service Facade
     * 
     * @return
     */
    private IUserGroupServiceFacade getUserGroupServiceFacade() {
        return (IUserGroupServiceFacade) ComponentManagerFactory.getComponentManager().getComponent(UserGroupServiceFacadeImpl.class);
    }

    private class PotentialMemberSelectableItemsDataModel extends ProxyingDataModel {

        /**
         * Create an instance of HostSelectableItemsDataModel
         * 
         * @param wrappedDataModel
         */
        private PotentialMemberSelectableItemsDataModel(UserDTO[] potentialMembers) {
            super(new ArrayDataModel(potentialMembers));
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
         */
        protected Object proxyRowData(Object rawData) {
            return new PotentialMemberSelectableItemImpl((UserDTO) rawData);
        }
    }

    public class DisablingItemDataModelImpl extends DisablingItemDataModel {

        private final ISelectedItemList selectedItems;
        private final Set usersAlreadyInGroup;

        /**
         * Create an instance of DisablingDataModelImpl
         * 
         * @param wrappedDataModel
         * @param selectedItems
         * @param usersAlreadySelected
         */
        public DisablingItemDataModelImpl(DataModel wrappedDataModel, ISelectedItemList selectedItems, UserDTO[] usersAlreadyInGroup) {
            super(wrappedDataModel);

            if (selectedItems == null) {
                throw new NullPointerException("selectedItems cannot be null.");
            }

            if (usersAlreadyInGroup == null) {
                throw new NullPointerException("existingUsers cannot be null.");
            }

            this.selectedItems = selectedItems;
            this.usersAlreadyInGroup = new HashSet();
            for (int i = 0; i < usersAlreadyInGroup.length; i++) {
                this.usersAlreadyInGroup.add(usersAlreadyInGroup[i].getId());
            }

        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.DisablingItemDataModel#shouldItemBeDisabled(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.IDisableableSelectableItem)
         */
        protected boolean shouldItemBeDisabled(IDisableableSelectableItem disableableItem) {
            if (disableableItem == null) {
                throw new NullPointerException("disableableItem cannot be null.");
            }

            PotentialMemberSelectableItemImpl potentialMemberSelectableItem = (PotentialMemberSelectableItemImpl) disableableItem;
            String selectableItemId = potentialMemberSelectableItem.getId();
            UserDTO wrappedDTO = potentialMemberSelectableItem.getWrappedUser();

            return ((this.selectedItems.containsSelectedItem(selectableItemId)) || (this.usersAlreadyInGroup.contains(wrappedDTO.getId())));
        }
    }
}
