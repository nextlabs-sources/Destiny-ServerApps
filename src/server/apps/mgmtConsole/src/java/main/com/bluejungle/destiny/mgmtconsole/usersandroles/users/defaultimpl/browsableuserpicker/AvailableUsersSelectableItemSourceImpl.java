/*
 * Created on Jul 8, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.browsableuserpicker;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;

import com.bluejungle.destiny.mgmtconsole.usersandroles.users.UsersException;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IInternalUsersViewBean;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.UserServiceFacadeImpl;
import com.bluejungle.destiny.services.management.UserRoleServiceException;
import com.bluejungle.destiny.services.management.types.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.services.management.types.UserDTOList;
import com.bluejungle.destiny.services.policy.types.SubjectDTO;
import com.bluejungle.destiny.services.policy.types.SubjectDTOList;
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
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/users/defaultimpl/browsableuserpicker/AvailableUsersSelectableItemSourceImpl.java#6 $
 */

public class AvailableUsersSelectableItemSourceImpl extends BaseSelectableItemSource {

    private static final String ITEM_NAME = "availableUsers";

    private IInternalUsersViewBean usersViewBean;
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
        DataModel modelToReturn = null;

        IUserServiceFacade userServiceFacade = getUserServiceFacade();
        try {
            SubjectDTOList matchingUsersList = userServiceFacade.getAvailableUsersForSearchBucketSearchSpec(searchSpec);
            SubjectDTO[] matchingUsersArray = matchingUsersList.getSubjects();
            modelToReturn = buildSelectableItemsDataModel(selectedItems, matchingUsersArray);
        } catch (ServiceNotReadyFault | UserRoleServiceException | RemoteException exception) {
            throw new SelectableItemSourceException(exception);
        }

        return modelToReturn;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#getSelectableItems(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec,
     *      com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList)
     */
    public DataModel getSelectableItems(IFreeFormSearchSpec searchSpec, ISelectedItemList selectedItems) throws SelectableItemSourceException {
        DataModel modelToReturn = null;

        IUserServiceFacade userServiceFacade = getUserServiceFacade();
        try {
            SubjectDTOList matchingUsersList = userServiceFacade.getAvailableUsersForFreeFormSearchSpec(searchSpec);
            SubjectDTO[] matchingUsersArray = matchingUsersList.getSubjects();
            modelToReturn = buildSelectableItemsDataModel(selectedItems, matchingUsersArray);
        } catch (ServiceNotReadyFault | UserRoleServiceException | RemoteException exception) {
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

        AvailableUserSelectableItem itemSelected = (AvailableUserSelectableItem) this.memorizedDataModel.getMemorizedDataItem(selectableItemId);
        AvailableUserSelectedItem itemToReturn = new AvailableUserSelectedItem(itemSelected.getWrappedUser());

        return Collections.singleton(itemToReturn);
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#storeSelectedItems(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList)
     */
    public String storeSelectedItems(ISelectedItemList selectedItems) throws SelectableItemSourceException {
        if (selectedItems == null) {
            throw new NullPointerException("selectedItems cannot be null.");
        }

        SubjectDTO[] usersToImport = new SubjectDTO[selectedItems.size()];
        Iterator selectedUsersIterator = selectedItems.iterator();
        for (int i = 0; selectedUsersIterator.hasNext(); i++) {
            AvailableUserSelectedItem nextUserToImport = (AvailableUserSelectedItem) selectedUsersIterator.next();
            usersToImport[i] = nextUserToImport.getwrappedUser();
        }

        if (usersToImport.length > 0) {
            try {
                UserDTOList createdUsers = getUserServiceFacade().importAvailableUsers(usersToImport);
                UserDTO[] createdUsersArray = createdUsers.getUsers();
                IInternalUsersViewBean usersViewBean = getUsersViewBean();
                if (createdUsersArray.length > 0) {
                    try {
                        usersViewBean.resetAndSelectUser(createdUsersArray[0].getId().getID().longValue());
                    } catch (UsersException exception) {
                        // In this case, just do full reset. Hopefully, when the
                        // page reloads, everything will be okay
                        usersViewBean.reset();
                    }
                } else {
                    // Should never happen
                    usersViewBean.reset();
                }
            } catch (UserRoleServiceException | RemoteException exception) {
                throw new SelectableItemSourceException(exception);
            }
        }

        return getReturnAction();
    }

    /**
     * Set the users view bean associated with this available selectable item
     * source.
     * 
     * @param usersViewBean
     *            the users view bean associated with this host selectable item
     *            source.
     * 
     */
    public void setUsersViewBean(IInternalUsersViewBean usersViewBean) {
        if (usersViewBean == null) {
            throw new NullPointerException("usersViewBean cannot be null.");
        }

        this.usersViewBean = usersViewBean;
    }

    /**
     * Build an appropriate DataModel to provide the selectable item list given
     * the specified inputs
     * 
     * @param selectedItems
     * @param matchingUsers
     * @return the selectedable item data model
     * @throws SelectableItemSourceException
     * @throws ServiceException
     * @throws ServiceNotReadyFault
     * @throws RemoteException
     */
    private DataModel buildSelectableItemsDataModel(ISelectedItemList selectedItems, SubjectDTO[] matchingUsers) throws RemoteException, UserRoleServiceException {
        IUserServiceFacade userServiceFacade = getUserServiceFacade();

        DataModel usersDataModel = new AvailableUserSelectableItemsDataModel(matchingUsers);
        UserDTOList usersAlreadySelectedList = userServiceFacade.getAllUsers();
        UserDTO[] usersAlreadySelected = usersAlreadySelectedList.getUsers();
        if (usersAlreadySelected == null) {
            usersAlreadySelected = new UserDTO[0];
        }

        DataModel disablingDataModel = new DisablingItemDataModelImpl(usersDataModel, selectedItems, usersAlreadySelected);

        this.memorizedDataModel = new MemorizingDataModel(disablingDataModel);

        return this.memorizedDataModel;
    }

    /**
     * Get the users view bean associated with this available selectable item
     * source.
     */
    private IInternalUsersViewBean getUsersViewBean() {
        return this.usersViewBean;
    }

    /**
     * Retrieve the User Service Facade
     * 
     * @return the user service facade
     */
    private IUserServiceFacade getUserServiceFacade() {
        return (IUserServiceFacade) ComponentManagerFactory.getComponentManager().getComponent(UserServiceFacadeImpl.class);
    }

    private class AvailableUserSelectableItemsDataModel extends ProxyingDataModel {

        /**
         * Create an instance of HostSelectableItemsDataModel
         * 
         * @param wrappedDataModel
         */
        private AvailableUserSelectableItemsDataModel(SubjectDTO[] availableUsers) {
            super(new ArrayDataModel(availableUsers));
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
         */
        protected Object proxyRowData(Object rawData) {
            return new AvailableUserSelectableItem((SubjectDTO) rawData);
        }
    }

    public class DisablingItemDataModelImpl extends DisablingItemDataModel {

        private final ISelectedItemList selectedItems;
        private final Set usersAlreadyImported;

        /**
         * Create an instance of DisablingDataModelImpl
         * 
         * @param wrappedDataModel
         * @param selectedItems
         * @param usersAlreadySelected
         */
        public DisablingItemDataModelImpl(DataModel wrappedDataModel, ISelectedItemList selectedItems, SubjectDTO[] usersAlreadyImported) {
            super(wrappedDataModel);

            if (selectedItems == null) {
                throw new NullPointerException("selectedItems cannot be null.");
            }

            if (usersAlreadyImported == null) {
                throw new NullPointerException("existingUsers cannot be null.");
            }

            this.selectedItems = selectedItems;
            this.usersAlreadyImported = new HashSet();
            for (int i = 0; i < usersAlreadyImported.length; i++) {
                this.usersAlreadyImported.add(usersAlreadyImported[i].getUniqueName());
            }

        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.DisablingItemDataModel#shouldItemBeDisabled(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.IDisableableSelectableItem)
         */
        protected boolean shouldItemBeDisabled(IDisableableSelectableItem disableableItem) {
            AvailableUserSelectableItem availableUserSelectableItem = (AvailableUserSelectableItem) disableableItem;
            String selectableItemId = availableUserSelectableItem.getId();
            SubjectDTO wrappedDTO = availableUserSelectableItem.getWrappedUser();

            return ((this.selectedItems.containsSelectedItem(selectableItemId)) || (this.usersAlreadyImported.contains(wrappedDTO.getUniqueName())));
        }
    }
}
