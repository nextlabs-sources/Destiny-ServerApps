/*
 * Created on May 10, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.browsablehostpicker;

import com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean;
import com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentTypeBean;
import com.bluejungle.destiny.mgmtconsole.agentconfig.IExistingProfileBean;
import com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean;
import com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.AgentConfigurationBeanImpl;
import com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.AgentQueryBroker;
import com.bluejungle.destiny.services.management.BadArgumentFault;
import com.bluejungle.destiny.services.management.CommitFault;
import com.bluejungle.destiny.services.management.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.UnauthorizedCallerFault;
import com.bluejungle.destiny.services.management.UnknownEntryFault;
import com.bluejungle.destiny.services.management.types.AgentDTO;
import com.bluejungle.destiny.services.management.types.AgentQueryResultsDTO;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.BaseSelectableItemSource;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.SelectableItemSourceException;
import com.bluejungle.destiny.webui.framework.data.LinkingDataModel;
import com.bluejungle.destiny.webui.framework.data.ProxyingDataModel;
import com.bluejungle.dictionary.DictionaryException;
import com.bluejungle.dictionary.IElementField;
import com.bluejungle.dictionary.IElementType;
import com.bluejungle.dictionary.IMElement;
import com.bluejungle.dictionary.IMGroup;
import com.bluejungle.domain.enrollment.ComputerReservedFieldEnumType;
import com.bluejungle.framework.comp.ComponentManagerFactory;

import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * HostSelectableItermSourceImpl is an implementation of ISelectableItemSource
 * for providing the Host data to implement the Host Browse page
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/browsablehostpicker/HostSelectableItemSourceImpl.java#1 $
 */

public class HostSelectableItemSourceImpl extends BaseSelectableItemSource {

    private static final String ITEM_NAME = "hosts";
    private static final String EMPTY_HOST_SEARCH_FILTER = "";

    private IAgentConfigurationBean agentConfigurationBean;
    private Map<String, ISelectableItem> viewedSelectableItems = new HashMap<String, ISelectableItem>();
    private AgentDTO[] agentsAlreadySelected;

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#getSelectableItems(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec,
     *      com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList)
     */
    public DataModel getSelectableItems(ISearchBucketSearchSpec searchSpec, ISelectedItemList selectedItems) throws SelectableItemSourceException {
        DataModel modelToReturn = null;

        try {
            DirectoryQueryBroker directoryBroker = getDirectoryQueryBroker();
            Set<IMGroup> hostGroups = directoryBroker.getAllHostGroups();
            ISelectableItem[] processedGroups = processGroups(searchSpec, hostGroups);

            int maximumResultsToReturn = searchSpec.getMaximumResultsToReturn();
            AgentDTO[] matchingAgents = null;
            if (processedGroups.length >= maximumResultsToReturn) {
                matchingAgents = new AgentDTO[0];
            } else {
                AgentQueryBroker agentQueryBroker = getAgentQueryBroker();
                ISearchBucketSearchSpec limitingSearchBucketSearchSpec = new LimitingSearchBucketSearchSpec(searchSpec, maximumResultsToReturn - processedGroups.length);
                AgentQueryResultsDTO agentsForBucket = agentQueryBroker.getAgentsForSearchBucketSearchSpec(limitingSearchBucketSearchSpec, getAgentType().getAgentTypeId());
                matchingAgents = agentsForBucket.getAgentList().getAgents();
                if (matchingAgents == null) {
                    // Must be a way for Axis to return empty array rather than null
                    matchingAgents = new AgentDTO[0];
                }
            }

            modelToReturn = buildSelectableItemsDataModel(selectedItems, matchingAgents, processedGroups);
        } catch (BadArgumentFault | ServiceNotReadyFault | RemoteException | DictionaryException exception) {
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

        try {
            DirectoryQueryBroker directoryBroker = getDirectoryQueryBroker();
            Set<IMGroup> hostGroups = directoryBroker.getAllHostGroups();
            ISelectableItem[] processedGroups = processGroups(searchSpec, hostGroups);

            int maximumResultsToReturn = searchSpec.getMaximumResultsToReturn();
            AgentDTO[] matchingAgents = null;
            if (processedGroups.length >= maximumResultsToReturn) {
                matchingAgents = new AgentDTO[0];
            } else {
                AgentQueryResultsDTO agentsForBucket = getAgentQueryBroker().getAgentsForFreeFormSearchSpec(searchSpec, getAgentType().getAgentTypeId());
                matchingAgents = agentsForBucket.getAgentList().getAgents();
                if (matchingAgents == null) {
                    // Must be a way for Axis to return empty array rather than
                    // null
                    matchingAgents = new AgentDTO[0];
                }
            }

            modelToReturn = buildSelectableItemsDataModel(selectedItems, matchingAgents, processedGroups);
        } catch (ServiceNotReadyFault | DictionaryException | RemoteException | BadArgumentFault exception) {
            throw new SelectableItemSourceException(exception);
        }

        return modelToReturn;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#generateSelectedItems(java.lang.String)
     */
    public Set<HostSelectedItem> generateSelectedItems(String selectableItemId) throws SelectableItemSourceException {
        if (selectableItemId == null) {
            throw new NullPointerException("selectableItemId cannot be null.");
        }

        if (!this.viewedSelectableItems.containsKey(selectableItemId)) {
            throw new IllegalArgumentException("Unknown selectable item id, " + selectableItemId);
        }

        Set<HostSelectedItem> selectedItemsToReturn = null;

        ISelectableItem selectableItem = this.viewedSelectableItems.get(selectableItemId);
        if (selectableItem instanceof HostSelectableItem) {
            selectedItemsToReturn = generateSelectedItemsForHost((HostSelectableItem) selectableItem);
        } else if (selectableItem instanceof HostGroupSelectableItem) {
            selectedItemsToReturn = generateSelectedItemsForHostGroup((HostGroupSelectableItem) selectableItem);
        } else {
            throw new IllegalStateException("Selected item of unknown origin, " + selectableItem.getDisplayValue());
        }

        return selectedItemsToReturn;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#storeSelectedItems(com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItemList)
     */
    public String storeSelectedItems(ISelectedItemList selectedItems) throws SelectableItemSourceException {
        if (selectedItems == null) {
            throw new NullPointerException("selectedItems cannot be null.");
        }

        IProfileBean selectedProfile = this.agentConfigurationBean.getSelectedProfile();
        if (!(selectedProfile instanceof IExistingProfileBean)) {
            throw new SelectableItemSourceException("Updating on hosts for non-existing profile is not supported.");
        }

        long profileId = ((IExistingProfileBean) selectedProfile).getProfileId();

        Set<BigInteger> agentIds = new HashSet<BigInteger>();
        Iterator<HostSelectedItem> selectedItemIterator = selectedItems.iterator();
        while (selectedItemIterator.hasNext()) {
            HostSelectedItem nextSelectedHost = selectedItemIterator.next();
            long nextAgentId = nextSelectedHost.getWrappedAgent().getId().getID().longValue();
            agentIds.add(BigInteger.valueOf(nextAgentId));
        }

        if (!agentIds.isEmpty()) {
            try {
                getAgentQueryBroker().setCommProfileForAgents(agentIds, profileId);

                // FIX ME - Create internal interface
                ((AgentConfigurationBeanImpl) this.agentConfigurationBean).resetAndSelectProfile(getAgentType(), new Long(profileId));
            } catch (ServiceNotReadyFault | UnknownEntryFault | UnauthorizedCallerFault | CommitFault | RemoteException exception) {
                throw new SelectableItemSourceException(exception);
            }
        }

        return getReturnAction();
    }

    /**
     * Set the agent configuration bean associated with this host selectable
     * item source.
     * 
     * @param agentConfigurationBean
     *            the agent configuration bean associated with this host
     *            selectable item source.
     * 
     */
    public void setAgentConfigurationBean(IAgentConfigurationBean agentConfigurationBean) {
        if (agentConfigurationBean == null) {
            throw new NullPointerException("agentConfigurationBean cannot be null.");
        }

        this.agentConfigurationBean = agentConfigurationBean;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#reset()
     */
    public void reset() {
        super.reset();

        this.agentsAlreadySelected = null;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.BaseSelectableItemSource#getItemName()
     */
    protected String getItemName() {
        return ITEM_NAME;
    }

    /**
     * Process the groups retrieved from dictionary and provide an associated
     * array of ISelectableItem (i.e. an array a Host Groups which can be
     * selected in the Host Browsable Data Picker). The maximum size of the
     * array will be the maximum number of search results specified in the
     * provided search spec
     * 
     * @param enumGroups
     * @param structuralGroups
     * @return an array of ISelectableItem instances representing the Host
     *         Groups which can be selected
     */
    private ISelectableItem[] processGroups(ISearchBucketSearchSpec searchBucketSpec, Set<IMGroup> groups) {
        Character[] searchCharacters = searchBucketSpec.getCharactersInBucket();
        String[] searchCriteria = new String[searchCharacters.length];
        for (int i = 0; i < searchCharacters.length; i++) {
            searchCriteria[i] = String.valueOf(searchCharacters[i]);
        }

        return processGroups(searchCriteria, searchBucketSpec.getMaximumResultsToReturn(), groups);
    }

    /**
     * Process the groups retrieved from dictionary and provide an associated
     * array of ISelectableItem (i.e. an array a Host Groups which can be
     * selected in the Host Browsable Data Picker). The maximum size of the
     * array will be the maximum number of search results specified in the
     * provided search spec
     * 
     * @param groups
     * @return an array of ISelectableItem instances representing the Host
     *         Groups which can be selected
     */
    private ISelectableItem[] processGroups(IFreeFormSearchSpec freeFormSearchSpec, Set<IMGroup> groups) {
        String[] searchCriteria = { freeFormSearchSpec.getFreeFormSeachString() };

        return processGroups(searchCriteria, freeFormSearchSpec.getMaximumResultsToReturn(), groups);
    }

    /**
     * Process the groups retrieved from dictionary and provide an associated
     * array of ISelectableItem (i.e. an array a Host Groups which can be
     * selected in the Host Browsable Data Picker)
     * 
     * @param groups
     * @param maximumGroupsToProcess
     *            the maximum number of results to return in the result array
     * 
     * @return an array of ISelectableItem instances representing the Host
     *         Groups which can be selected
     */
    private ISelectableItem[] processGroups(String[] searchCriteria, int maximumGroupsToProcess, Set<IMGroup> groups) {
        SortedSet<ISelectableItem> processedGroupsSet = new TreeSet<ISelectableItem>();

        StringBuffer searchRegExp = new StringBuffer();
        for (int i = 0; i < searchCriteria.length; i++) {
            if (i != 0) {
                searchRegExp.append("|");
            }
            searchRegExp.append("(");
            for (int j = 0; j < searchCriteria[i].length(); j++) {
                char nextChar = searchCriteria[i].charAt(j);
                if (nextChar == '*') {
                    searchRegExp.append(".*");
                } else {
                    searchRegExp.append("(");
                    searchRegExp.append(Character.toUpperCase(nextChar));
                    searchRegExp.append("|");
                    searchRegExp.append(Character.toLowerCase(nextChar));
                    searchRegExp.append(")");
                }
            }
            searchRegExp.append(".*)");
        }

        Pattern searchPattern = Pattern.compile(searchRegExp.toString());

        addMatchingGroups(groups, processedGroupsSet, searchPattern, maximumGroupsToProcess);

        // Translating to an array here is okay, because the DataModel will
        // require an indexable Collection
        return processedGroupsSet.toArray(new ISelectableItem[processedGroupsSet.size()]);
    }

    /**
     * Iterator through the specified groups and add all that that mathc the
     * specified pattern to the provided SortedSet
     * 
     * @param groups
     *            the groups to search
     * @param processedGroupsSet
     *            the set to contain the matching groups
     * @param searchPattern
     *            the search pattern
     * @param maxResults
     *            the maximum number of results to return
     */
    private void addMatchingGroups(Set<IMGroup> groups, SortedSet<ISelectableItem> processedGroupsSet, Pattern searchPattern, int maxResults) {
        Iterator<IMGroup> groupIterator = groups.iterator();
        for (int i = 0; ((i < maxResults) && (groupIterator.hasNext())); i++) {
            IMGroup nextGroup = groupIterator.next();
            String groupDisplayName = nextGroup.getDisplayName();
            if (searchPattern.matcher(groupDisplayName).matches()) {
                ISelectableItem hostGroupSelectableItem = new HostGroupSelectableItem(nextGroup);
                processedGroupsSet.add(hostGroupSelectableItem);
            }
        }
    }

    /**
     * Build an appropriate DataModel to provide the selectable item list given
     * the specified inputs
     * 
     * @param selectedItems
     * @param matchingAgents
     * @param processedGroups
     * @return
     * @throws SelectableItemSourceException
     * @throws ServiceNotReadyFault
     * @throws RemoteException
     */
    private DataModel buildSelectableItemsDataModel(ISelectedItemList selectedItems, AgentDTO[] matchingAgents, ISelectableItem[] processedGroups) throws SelectableItemSourceException, ServiceNotReadyFault, RemoteException, BadArgumentFault {
        DataModel groupsDataModel = new ArrayDataModel(processedGroups == null ? new ISelectableItem[] {} : processedGroups);
        DataModel hostsDataModel = new HostSelectableItemsDataModel(matchingAgents == null ? new AgentDTO[] {} : matchingAgents);

        AgentDTO[] agentsAlreadySelected = retrieveAgentsAssignedSelectedProfile();
        if (agentsAlreadySelected == null) {
            agentsAlreadySelected = new AgentDTO[0];
        }
        DataModel disablingDataModel = new DisablingItemDataModel(hostsDataModel, selectedItems, agentsAlreadySelected);

        LinkingDataModel linkingDataModel = new LinkingDataModel(groupsDataModel, disablingDataModel);
        return new MemorizingDataModel(linkingDataModel);
    }

    /**
     * Retrieve the agents already assigned to the current profile
     * 
     * @param agentQueryBroker
     * @return the agents already assigned to the current profile
     * @throws SelectableItemSourceException
     * @throws ServiceException
     * @throws ServiceNotReadyFault
     * @throws RemoteException
     */
    private AgentDTO[] retrieveAgentsAssignedSelectedProfile() throws SelectableItemSourceException, ServiceNotReadyFault, RemoteException, BadArgumentFault {
        if (this.agentsAlreadySelected == null) {
            AgentQueryBroker agentQueryBroker = getAgentQueryBroker();

            IProfileBean selectedProfile = this.agentConfigurationBean.getSelectedProfile();
            if (!(selectedProfile instanceof IExistingProfileBean)) {
                throw new SelectableItemSourceException("Updating on hosts for non-existing profile is not supported.");
            }

            long profileId = ((IExistingProfileBean) selectedProfile).getProfileId();
            AgentQueryResultsDTO queryResults = agentQueryBroker.getAgentsForProfile(profileId, getAgentType().getAgentTypeId(), EMPTY_HOST_SEARCH_FILTER, 0);
            this.agentsAlreadySelected = queryResults.getAgentList().getAgents();
        }

        return this.agentsAlreadySelected;
    }

    /**
     * Generate the Selected Items Set for the specified HostSelectableItem
     * 
     * @param selectedItemsToReturn
     * @param selectableItem
     */
    private Set<HostSelectedItem> generateSelectedItemsForHost(HostSelectableItem selectableItem) {
        if (selectableItem == null) {
            throw new NullPointerException("selectableItem cannot be null.");
        }

        Set<HostSelectedItem> selectedItemsToReturn = new HashSet<HostSelectedItem>();
        AgentDTO wrappedAgentDTO = selectableItem.getWrappedAgent();
        selectedItemsToReturn.add(new HostSelectedItem(wrappedAgentDTO));
        return selectedItemsToReturn;
    }

    /**
     * Generate the Selected Items Set for the specified HostGroupSelectableItem
     * 
     * @param selectableItem
     * @return
     * @throws SelectableItemSourceException
     * @throws ServiceException
     * @throws RemoteException
     * @throws ServiceNotReadyFault
     */
    private Set<HostSelectedItem> generateSelectedItemsForHostGroup(HostGroupSelectableItem selectableItem) throws SelectableItemSourceException {
        if (selectableItem == null) {
            throw new NullPointerException("selectableItem cannot be null.");
        }

        Set<HostSelectedItem> selectedItemsToReturn = new HashSet<HostSelectedItem>();

        // Retrieve the hosts for the host group
        IMGroup group = selectableItem.getWrappedGroup();
        Set<IMElement> hosts = null;

        try {

            hosts = getDirectoryQueryBroker().getHostsForGroup(group);
            if (hosts.isEmpty()) {
                return selectedItemsToReturn;
            }
            
            Set<IMElement> filteredHosts = new HashSet<IMElement>();

            // Retrieve the agents already selected for this profile and the
            // associated hostnames
            AgentDTO[] agentsAlreadySelected = retrieveAgentsAssignedSelectedProfile();
            if (agentsAlreadySelected != null) {
                Set<String> hostsAlreadySelected = new HashSet<String>();
                for (int i = 0; i < agentsAlreadySelected.length; i++) {
                    hostsAlreadySelected.add(agentsAlreadySelected[i].getHost().toLowerCase());
                }

                // Filter out the hosts already selected
                Iterator<IMElement> hostIterator = hosts.iterator();
                while (hostIterator.hasNext()) {
                    IMElement nextHost = hostIterator.next();
                    IElementType type = nextHost.getType();
                    IElementField field = type.getField(ComputerReservedFieldEnumType.DNS_NAME.getName());
                    String nextHostName = (String) nextHost.getValue(field);
                    if (nextHostName != null && !hostsAlreadySelected.contains(nextHostName.toLowerCase())) {
                        filteredHosts.add(nextHost);
                    }
                }
            } else {
                // No hosts to filter
                filteredHosts = hosts;
            }

            // Retrieve the hosts to selected in the data picker
            AgentQueryResultsDTO agentQueryResults = getAgentQueryBroker().getAgentForLDAPHosts(filteredHosts, getAgentType().getAgentTypeId());
            AgentDTO[] matchingAgents = agentQueryResults.getAgentList().getAgents();
            if (matchingAgents != null) {
                for (AgentDTO matchingAgent : matchingAgents) {
                    selectedItemsToReturn.add(new HostSelectedItem(matchingAgent));
                }
            }
        } catch (ServiceNotReadyFault | DictionaryException | BadArgumentFault | RemoteException exception) {
            throw new SelectableItemSourceException(exception);
        }

        return selectedItemsToReturn;
    }

    /**
     * Retrieve the AgentType associated with this HostSelectableItemSource
     * 
     * @return the AgentType associated with this HostSelectableItemSource
     */
    private IAgentTypeBean getAgentType() {
        return this.agentConfigurationBean.getAgentType();
    }

    /**
     * Retrieve the AgentQueryBroker
     * 
     * @return the AgentQueryBroker
     */
    private AgentQueryBroker getAgentQueryBroker() {
        return ComponentManagerFactory.getComponentManager().getComponent(AgentQueryBroker.class);
    }

    /**
     * Retrieve the DirectoryQueryBroker
     * 
     * @return the DirectoryQueryBroker
     */
    private DirectoryQueryBroker getDirectoryQueryBroker() {
        return ComponentManagerFactory.getComponentManager().getComponent(DirectoryQueryBroker.class);
    }

    private class HostSelectableItemsDataModel extends ProxyingDataModel {

        /**
         * Create an instance of HostSelectableItemsDataModel
         * 
         * @param wrappedDataModel
         */
        private HostSelectableItemsDataModel(AgentDTO[] agents) {
            super(new ArrayDataModel(agents));
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
         */
        protected Object proxyRowData(Object rawData) {
            return new HostSelectableItem((AgentDTO) rawData);
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
            if (getRowIndex() == 0) {
                // A bit of a hack to keep memory use in check
                HostSelectableItemSourceImpl.this.viewedSelectableItems.clear();
            }

            ISelectableItem selectableItem = (ISelectableItem) rawData;

            // Store a reference to the returned items in a Map for later
            // retrieval ({@see generateSelectedItems})
            HostSelectableItemSourceImpl.this.viewedSelectableItems.put(selectableItem.getId(), selectableItem);

            return rawData;
        }
    }

    private class LimitingSearchBucketSearchSpec implements ISearchBucketSearchSpec {

        private ISearchBucketSearchSpec wrappedSearchSpec;
        private int maximumResults;

        /**
         * Create an instance of LimitingSearchBucketSearchSpec
         * 
         * @param wrappedSearchSpec
         * @param maximumResults
         */
        public LimitingSearchBucketSearchSpec(ISearchBucketSearchSpec wrappedSearchSpec, int maximumResults) {
            super();
            this.wrappedSearchSpec = wrappedSearchSpec;
            this.maximumResults = maximumResults;
        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec#getCharactersInBucket()
         */
        public Character[] getCharactersInBucket() {
            return this.wrappedSearchSpec.getCharactersInBucket();
        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSearchSpec#getMaximumResultsToReturn()
         */
        public int getMaximumResultsToReturn() {
            return this.maximumResults;
        }
    }

    private class LimitingFreeFormSearchSpec implements IFreeFormSearchSpec {
        private IFreeFormSearchSpec wrappedSearchSpec;
        private int maximumResults;
                
        /**
         * Create an instance of LimitingFreeFormSearchSpec
         * @param wrappedSearchSpec
         * @param maximumResults
         */
        public LimitingFreeFormSearchSpec(IFreeFormSearchSpec wrappedSearchSpec, int maximumResults) {
            super();
            this.wrappedSearchSpec = wrappedSearchSpec;
            this.maximumResults = maximumResults;
        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec#getFreeFormSeachString()
         */
        public String getFreeFormSeachString() {
            return this.wrappedSearchSpec.getFreeFormSeachString();
        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSearchSpec#getMaximumResultsToReturn()
         */
        public int getMaximumResultsToReturn() {
            return this.maximumResults;
        }        
    }
}
