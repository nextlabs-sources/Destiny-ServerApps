/*
 * Created on May 17, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.user;

import com.bluejungle.destiny.framework.types.ServiceNotReadyFault;
import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.BaseReportSelectableItemSource;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentLookupException;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentQueryBroker;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportDataPickerUtil;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.UserComponentEntityResolver;
import com.bluejungle.destiny.types.users.v1.User;
import com.bluejungle.destiny.types.users.v1.UserClass;
import com.bluejungle.destiny.types.users.v1.UserClassList;
import com.bluejungle.destiny.types.users.v1.UserList;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.SelectableItemSourceException;
import com.bluejungle.destiny.webui.framework.data.LinkingDataModel;
import com.bluejungle.destiny.webui.framework.data.ProxyingDataModel;

import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * @author safdar
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/SelectableUserComponentItemSourceImpl.java#1 $
 */

public class SelectableUserComponentItemSourceImpl extends BaseReportSelectableItemSource {

    private static final String ITEM_NAME = "users";

    /**
     * @param selectedItems
     * @param matchingUsers
     * @param matchingUserClasses
     * @return
     * @throws SelectableItemSourceException
     * @throws ServiceNotReadyFault
     * @throws RemoteException
     */
    private DataModel buildSelectableItemsDataModel(ISelectedItemList selectedItems, UserList matchingUsers, UserClassList matchingUserClasses) {
        DataModel usersDataModel = new SelectableUserItemsDataModel(matchingUsers == null || matchingUsers.getUsers() == null ? new User[] {} : matchingUsers.getUsers());
        DataModel userClassesDataModel = new SelectableUserClassItemsDataModel(matchingUserClasses == null || matchingUserClasses.getClasses() == null ? new UserClass[] {} : matchingUserClasses.getClasses());

        // Calculate the user/user-classes that are already selected, and
        // disable them:
        IReport reportBean = getCurrentReport();
        String existingSelections = reportBean.getUsers();
        UserComponentEntityResolver userComponentEntityResolver = new UserComponentEntityResolver(existingSelections);
        String[] alreadySelectedUserIds = userComponentEntityResolver.getQualifiedUsers();
        String[] alreadySelectedUserClassIds = userComponentEntityResolver.getQualifiedUserClasses();

        // Create the disabling data model:
        DataModel disablingUserDataModel = new UserDisablingDataModel(usersDataModel, selectedItems, alreadySelectedUserIds);
        DataModel disablingUserClassesDataModel = new UserClassDisablingDataModel(userClassesDataModel, selectedItems, alreadySelectedUserClassIds);

        LinkingDataModel linkingDataModel = new LinkingDataModel(disablingUserDataModel, disablingUserClassesDataModel);
        return new MemorizingDataModel(linkingDataModel);
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.BaseSelectableItemSource#getItemName()
     */
    protected String getItemName() {
        return ITEM_NAME;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#getSelectableItems(com.bluejungle.destiny.webui.browsabledatapicker.ISearchBucketSearchSpec,
     *      com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItemList)
     */
    public DataModel getSelectableItems(ISearchBucketSearchSpec searchSpec, ISelectedItemList selectedItems) throws SelectableItemSourceException {
        DataModel modelToReturn = null;

        try {
            ReportComponentQueryBroker componentQueryBroker = getReportComponentQueryBroker();
            UserList users = componentQueryBroker.getUsersForSearchBucketSearchSpec(searchSpec);  
            User[] usersArray = users.getUsers();
            int numberUsersRetrieved = (usersArray == null) ? 0 : usersArray.length;
            
            UserClassList userClasses = new UserClassList(); 
           /*
            * This is commented out since we have taken out the search-by-group 
            * feature when generating reports and hence should not display the
            * groups in the picker list. The server-side logic is still
            * intact, so if, in the future, we want to add this feature, 
            * we should have a separate UI component for selecting groups, 
            * and the UI should allow either search by group or by user 
            * so that the query is separate. The main reason of taking this out 
            * was that the query had to include either user or group that 
            * resulted in multiple joins resulting
            * in very inefficient query generation time. 
             if (numberUsersRetrieved < searchSpec.getMaximumResultsToReturn()){
                UserClassList tempUserClasses = componentQueryBroker.getUserClassesForSearchBucketSearchSpec(searchSpec);
                List userClassList = new ArrayList();
                if (tempUserClasses != null && tempUserClasses.getClasses() != null){
                    int limit = (searchSpec.getMaximumResultsToReturn() - numberUsersRetrieved) > tempUserClasses.getClasses().length ?
                            	tempUserClasses.getClasses().length :
                            	searchSpec.getMaximumResultsToReturn() - numberUsersRetrieved;
                    for (int i = 0; i < limit; i++){
                        userClassList.add(tempUserClasses.getClasses(i));
                    }
                }
                UserClass[] allUserClassesArray = new UserClass[userClassList.size()];
                userClassList.toArray(allUserClassesArray);
                userClasses = new UserClassList(allUserClassesArray);
            }            */            
            
            modelToReturn = buildSelectableItemsDataModel(selectedItems, users, userClasses);
        } catch (ReportComponentLookupException exception) {
            throw new SelectableItemSourceException(exception);
        }
        return modelToReturn;
    }

    /**
     * @throws SelectableItemSourceException
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#getSelectableItems(com.bluejungle.destiny.webui.browsabledatapicker.IFreeFormSearchSpec,
     *      com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItemList)
     */
    public DataModel getSelectableItems(IFreeFormSearchSpec searchSpec, ISelectedItemList selectedItems) throws SelectableItemSourceException {
        DataModel modelToReturn = null;

        try {
            ReportComponentQueryBroker componentQueryBroker = getReportComponentQueryBroker();
            UserList users = componentQueryBroker.getUsersForFreeFormSearchSpec(searchSpec);
            User[] usersArray = users.getUsers();
            int numberUsersRetrieved = (usersArray == null) ? 0 : usersArray.length;
            
            UserClassList userClasses = new UserClassList();
            /*
             * don't return groups on searching too, search by group is not possible.
            if (numberUsersRetrieved < searchSpec.getMaximumResultsToReturn()){
                UserClassList tempUserClasses = componentQueryBroker.getUserClassesForFreeFormSearchSpec(searchSpec);
                List userClassList = new ArrayList();
                if (tempUserClasses != null && tempUserClasses.getClasses() != null){
                    int limit = (searchSpec.getMaximumResultsToReturn() - numberUsersRetrieved) > tempUserClasses.getClasses().length ?
                            	tempUserClasses.getClasses().length :
                            	searchSpec.getMaximumResultsToReturn() - numberUsersRetrieved;
                    for (int i = 0; i < limit; i++){
                        userClassList.add(tempUserClasses.getClasses(i));
                    }
                }
                UserClass[] allUserClassesArray = new UserClass[userClassList.size()];
                userClassList.toArray(allUserClassesArray);
                userClasses = new UserClassList(allUserClassesArray);
            }
            */

            modelToReturn = buildSelectableItemsDataModel(selectedItems, users, userClasses);
        } catch (ReportComponentLookupException exception) {
            throw new SelectableItemSourceException(exception);
        }

        return modelToReturn;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#storeSelectedItems(com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItemList)
     */
    public String storeSelectedItems(ISelectedItemList selectedItems) throws SelectableItemSourceException {
        IReport report = getCurrentReport();
        report.setUsers(ReportDataPickerUtil.createInputFieldSelection(selectedItems, report.getUsers()));
        return getReturnAction();
    }

    private class SelectableUserClassItemsDataModel extends ProxyingDataModel {

        /**
         * Create an instance of SelectableUserItemsDataModel
         * 
         * @param wrappedDataModel
         */
        private SelectableUserClassItemsDataModel(UserClass[] userClasses) {
            super(new ArrayDataModel(userClasses));
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
         */
        protected Object proxyRowData(Object rawData) {
            return new SelectableUserClassItem((UserClass) rawData);
        }
    }

    private class SelectableUserItemsDataModel extends ProxyingDataModel {

        /**
         * Create an instance of SelectableUserItemsDataModel
         * 
         * @param wrappedDataModel
         */
        private SelectableUserItemsDataModel(User[] users) {
            super(new ArrayDataModel(users));
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
         */
        protected Object proxyRowData(Object rawData) {
            return new SelectableUserItem((User) rawData);
        }
    }

    private class MemorizingDataModel extends ProxyingDataModel {

        /**
         * Create an instance of MemorizingDataModel
         * 
         * @param wrappedDataModel
         */
        private MemorizingDataModel(DataModel wrappedDataModel) {
            super(wrappedDataModel);
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
         */
        protected Object proxyRowData(Object rawData) {
            Map viewedSelectItems = SelectableUserComponentItemSourceImpl.this.getViewedSelectableItems();
            if (getRowIndex() == 0) {
                // A bit of a hack to keep memory use in check
                viewedSelectItems.clear();
            }
            ISelectableItem selectableItem = (ISelectableItem) rawData;
            viewedSelectItems.put(selectableItem.getId(), selectableItem);
            return selectableItem;
        }
    }
}
