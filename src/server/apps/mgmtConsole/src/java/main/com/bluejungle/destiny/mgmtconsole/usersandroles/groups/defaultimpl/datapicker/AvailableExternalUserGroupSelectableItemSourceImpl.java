package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.datapicker;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;

import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IInternalUserGroupsViewBean;
import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade;
import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.UserGroupServiceFacadeImpl;
import com.bluejungle.destiny.services.management.types.CommitFault;
import com.bluejungle.destiny.services.management.types.ExternalUserGroup;
import com.bluejungle.destiny.services.management.types.ExternalUserGroupList;
import com.bluejungle.destiny.services.management.types.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.types.UnauthorizedCallerFault;
import com.bluejungle.destiny.services.management.types.UserGroupReduced;
import com.bluejungle.destiny.services.management.types.UserGroupReducedList;
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
import org.apache.axis2.databinding.utils.ConverterUtil;

/**
 * Selectable item source implementation for external user groups which can be linked to
 * create user groups in the system
 * 
 * @author sgoldstein
 */
public class AvailableExternalUserGroupSelectableItemSourceImpl extends BaseSelectableItemSource {

    private static final String ITEM_NAME = "availableExternalUserGroups";

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
        DataModel modelToReturn = null;

        IUserGroupServiceFacade userGroupServiceFacade = getUserGroupServiceFacade();
        try {
            ExternalUserGroupList externalUserGroupQueryResults = userGroupServiceFacade.getExternalGroupsForSearchBucketSearchSpec(searchSpec);
            modelToReturn = buildSelectableItemsDataModel(selectedItems, externalUserGroupQueryResults);
        } catch (RemoteException | ServiceNotReadyFault | CommitFault | UnauthorizedCallerFault exception) {
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

        IUserGroupServiceFacade userGroupServiceFacade = getUserGroupServiceFacade();
        ExternalUserGroupList externalUserGroupQueryResults;
        try {
            externalUserGroupQueryResults = userGroupServiceFacade.getExternalGroupsForFreeFormSearchSpec(searchSpec);
            modelToReturn = buildSelectableItemsDataModel(selectedItems, externalUserGroupQueryResults);
        } catch (RemoteException | ServiceNotReadyFault | CommitFault | UnauthorizedCallerFault exception) {
            throw new SelectableItemSourceException(exception);
        }

        return modelToReturn;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#generateSelectedItems(java.lang.String)
     */
    public Set generateSelectedItems(String selectableItemId) throws SelectableItemSourceException {
        if (selectableItemId == null) {
            throw new NullPointerException("selectableItemId cannot be null.");
        }

        if (!this.memorizedDataModel.isDataItemMemorized(selectableItemId)) {
            throw new SelectableItemSourceException("Unknown id, " + selectableItemId);
        }

        AvailableExternalUserGroupSelectableItemImpl itemSelected = (AvailableExternalUserGroupSelectableItemImpl) this.memorizedDataModel.getMemorizedDataItem(selectableItemId);
        AvailableExternalUserGroupSelectedItemImpl itemToReturn = new AvailableExternalUserGroupSelectedItemImpl(itemSelected.getWrappedExternalUserGroup());

        return Collections.singleton(itemToReturn);
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#storeSelectedItems(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList)
     */
    public String storeSelectedItems(ISelectedItemList selectedItems) throws SelectableItemSourceException {
        if (selectedItems == null) {
            throw new NullPointerException("selectedItems cannot be null.");
        }

        ExternalUserGroup[] externalUserGroups = new ExternalUserGroup[selectedItems.size()];
        Iterator selectedExternalUserGroupsIterator = selectedItems.iterator();
        for (int i = 0; selectedExternalUserGroupsIterator.hasNext(); i++) {
            AvailableExternalUserGroupSelectedItemImpl nextExternalUserGroupToImport = (AvailableExternalUserGroupSelectedItemImpl) selectedExternalUserGroupsIterator.next();
            externalUserGroups[i] = nextExternalUserGroupToImport.getWrappedExternalUserGroup();
        }

        ExternalUserGroupList externalUserGroupList = new ExternalUserGroupList();
        externalUserGroupList.setExternalUserGroup(externalUserGroups);

        try {
            getUserGroupServiceFacade().linkExternalGroups(externalUserGroupList);

            getUserGroupsViewBean().reset();
        } catch (ServiceNotReadyFault | RemoteException | CommitFault | UnauthorizedCallerFault exception) {
            throw new SelectableItemSourceException(exception);
        }

        return getReturnAction();
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
     * @param selectedItems
     * @param externalUserGroupQueryResults
     * @return
     * @throws RemoteException
     * @throws UnauthorizedCallerFault
     * @throws CommitFault
     * @throws ServiceNotReadyFault
     */
    private DataModel buildSelectableItemsDataModel(ISelectedItemList selectedItems, ExternalUserGroupList externalUserGroupData) throws ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException {
        IUserGroupServiceFacade userGroupServiceFacade = getUserGroupServiceFacade();

        ExternalUserGroup[] matchingExternalUserGroups = externalUserGroupData.getExternalUserGroup();
        if (matchingExternalUserGroups == null) {
            matchingExternalUserGroups = new ExternalUserGroup[0];
        }
        DataModel externalUserGroupDataModel = new ExternalUserGroupsSelectableItemDataModel(matchingExternalUserGroups);
        UserGroupReducedList userGroupsInSystemList = userGroupServiceFacade.getAllGroups();
        UserGroupReduced[] userGroupsInSystem = userGroupsInSystemList.getUserGroupReduced();
        if (userGroupsInSystem == null) {
            userGroupsInSystem = new UserGroupReduced[0];
        }

        DataModel disablingDataModel = new DisablingItemDataModelImpl(externalUserGroupDataModel, selectedItems, userGroupsInSystem);

        this.memorizedDataModel = new MemorizingDataModel(disablingDataModel);

        return this.memorizedDataModel;
    }

    /**
     * Retrieve the Group Service Facade
     * 
     * @return
     */
    private IUserGroupServiceFacade getUserGroupServiceFacade() {
        return (IUserGroupServiceFacade) ComponentManagerFactory.getComponentManager().getComponent(UserGroupServiceFacadeImpl.class);
    }

    /*
     * Was originally called AvailableExternalUserGroupsSelectableItemDataMode.  Was reduced to avoid Windows path length limit
     */
    private class ExternalUserGroupsSelectableItemDataModel extends ProxyingDataModel {

        /**
         * 
         * Create an instance of
         * AvailableExternalUserGroupsSelectableItemDataModel
         * 
         * @param availableExternalUserGroups
         */
        private ExternalUserGroupsSelectableItemDataModel(ExternalUserGroup[] availableExternalUserGroups) {
            super(new ArrayDataModel(availableExternalUserGroups));
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
         */
        protected Object proxyRowData(Object rawData) {
            return new AvailableExternalUserGroupSelectableItemImpl((ExternalUserGroup) rawData);
        }
    }

    public class DisablingItemDataModelImpl extends DisablingItemDataModel {

        private final ISelectedItemList selectedItems;
        private final Set userGroupsInSystem;

        /**
         * Create an instance of DisablingDataModelImpl
         * 
         * @param wrappedDataModel
         * @param selectedItems
         * @param usersAlreadySelected
         */
        public DisablingItemDataModelImpl(DataModel wrappedDataModel, ISelectedItemList selectedItems, UserGroupReduced[] userGroupsInSystem) {
            super(wrappedDataModel);

            if (selectedItems == null) {
                throw new NullPointerException("selectedItems cannot be null.");
            }

            if (userGroupsInSystem == null) {
                throw new NullPointerException("userGroupsInSystem cannot be null.");
            }

            this.selectedItems = selectedItems;
            this.userGroupsInSystem = new HashSet();
            for (int i = 0; i < userGroupsInSystem.length; i++) {
                if (userGroupsInSystem[i].getExternallyLinked()) {
                    this.userGroupsInSystem.add(UserGroupDataPickerUtils.getBase64EncodingOf(ConverterUtil.getStringFromDatahandler(userGroupsInSystem[i].getExternalId()).getBytes()));
                }
            }
        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.DisablingItemDataModel#shouldItemBeDisabled(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.IDisableableSelectableItem)
         */
        protected boolean shouldItemBeDisabled(IDisableableSelectableItem disableableItem) {
            AvailableExternalUserGroupSelectableItemImpl availableExternalUserGroupSelectableItem = (AvailableExternalUserGroupSelectableItemImpl) disableableItem;
            String selectableItemId = availableExternalUserGroupSelectableItem.getId();
            ExternalUserGroup wrappedExternalUserGroup = availableExternalUserGroupSelectableItem.getWrappedExternalUserGroup();

            return ((this.selectedItems.containsSelectedItem(selectableItemId)) || (this.userGroupsInSystem.contains(UserGroupDataPickerUtils.getBase64EncodingOf(ConverterUtil.getStringFromDatahandler(wrappedExternalUserGroup.getExternalId()).getBytes()))));
        }
    }
}

