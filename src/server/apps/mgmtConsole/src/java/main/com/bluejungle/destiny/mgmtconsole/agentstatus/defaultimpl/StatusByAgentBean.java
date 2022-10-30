/*
 * Created on May 2, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentstatus.defaultimpl;

import com.bluejungle.destiny.container.dcc.IDCCContainer;
import com.bluejungle.destiny.framework.types.ID;
import com.bluejungle.destiny.framework.types.RelationalOpDTO;
import com.bluejungle.destiny.mgmtconsole.CommonConstants;
import com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean;
import com.bluejungle.destiny.mgmtconsole.agentstatus.StatusByAgentViewException;
import com.bluejungle.destiny.services.management.AgentServiceStub;
import com.bluejungle.destiny.services.management.BadArgumentFault;
import com.bluejungle.destiny.services.management.CommitFault;
import com.bluejungle.destiny.services.management.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.UnauthorizedCallerFault;
import com.bluejungle.destiny.services.management.UnknownEntryFault;
import com.bluejungle.destiny.services.management.types.AgentDTO;
import com.bluejungle.destiny.services.management.types.AgentDTOQueryField;
import com.bluejungle.destiny.services.management.types.AgentDTOQuerySpec;
import com.bluejungle.destiny.services.management.types.AgentDTOQueryTerm;
import com.bluejungle.destiny.services.management.types.AgentDTOQueryTermList;
import com.bluejungle.destiny.services.management.types.AgentDTOSortTerm;
import com.bluejungle.destiny.services.management.types.AgentDTOSortTermList;
import com.bluejungle.destiny.services.management.types.AgentPolicyAssemblyStatusDTO;
import com.bluejungle.destiny.services.management.types.AgentQueryResultsDTO;
import com.bluejungle.destiny.services.management.types.AgentTypeDTOList;
import com.bluejungle.destiny.services.management.types.ConcreteAgentDTOQueryTerm;
import com.bluejungle.destiny.webui.framework.faces.CommonSelectItemResourceLists;
import com.bluejungle.destiny.webui.framework.sort.ISortStateMgr;
import com.bluejungle.destiny.webui.framework.sort.SortStateMgrImpl;
import com.bluejungle.domain.types.AgentTypeDTO;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IConfiguration;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author safdar
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentstatus/defaultimpl/StatusByAgentBean.java#1 $
 */

public class StatusByAgentBean implements IStatusByAgentBean {

    /*
     * Constants:
     */
    private static final String AGENT_SERVICE_LOCATION_SERVLET_PATH = "/services/AgentService";
    private static final long OVERALL_STATUS_INTERVAL = 120000; // 2 minutes
    private static final String STAR_CHAR = "*";
    private static final String ALL_AGENTS_FILTER_TITLE_BUNDLE_KEY = "status_by_host_all_agents_filter";
    private static final String AGENT_TYPE_FILTER_TITLE_BUNDLE_KEY = "status_by_host_agent_type_filter_label";
    private static final String ALL_AGENTS_FILTER_ID = "com.nextlabs.all.agents.filter.id";
    
    /**
     * Pre-defined query terms
     */
    private static ConcreteAgentDTOQueryTerm REGISTERED_QUERY_TERM;
    private static ConcreteAgentDTOQueryTerm NOT_ONLINE_QUERY_TERM;

    static {
        REGISTERED_QUERY_TERM = new ConcreteAgentDTOQueryTerm();
        REGISTERED_QUERY_TERM.setAgentDTOQueryField(AgentDTOQueryField.REGISTERED);
        REGISTERED_QUERY_TERM.setOperator(RelationalOpDTO.equals);
        REGISTERED_QUERY_TERM.setValue(new Boolean(true));

        NOT_ONLINE_QUERY_TERM = new ConcreteAgentDTOQueryTerm();
        NOT_ONLINE_QUERY_TERM.setAgentDTOQueryField(AgentDTOQueryField.ONLINE);
        NOT_ONLINE_QUERY_TERM.setOperator(RelationalOpDTO.equals);
        NOT_ONLINE_QUERY_TERM.setValue(new Boolean(false));
    }

    /**
     * Log object
     */
    private static final Log LOG = LogFactory.getLog(StatusByAgentBean.class);

    /**
     * Private variables:
     */
    private AgentServiceStub agentService;
    private String activeFilterId;
    private DataModel agentDataModel;
    private boolean retrieveAgents;
    private long lastOverallStatusTime = 0;
    private ISortStateMgr sortStateMgr;
    private String searchString;
    private int maxSelectableItemsToDisplay = -1;
    private boolean overallSystemStatus = true;
    private boolean filteredByWarningsOnly;

    private final Collection<SelectItem> filterSelections;

    /**
     * Constructor
     */
    public StatusByAgentBean() throws RemoteException, ServiceNotReadyFault {
        super();
        this.sortStateMgr = new SortStateMgrImpl();
        this.sortStateMgr.setSortAscending(true);
        this.sortStateMgr.setSortFieldName(StatusByAgentSortColumnEnumType.HOST_COLUMN.getLogicalName());
        this.activeFilterId = ALL_AGENTS_FILTER_ID;
        this.filteredByWarningsOnly = true;
        this.retrieveAgents = true;

        // Populates the filter selections
        this.filterSelections = new ArrayList<SelectItem>();
        this.filterSelections.add(new SelectItem(ALL_AGENTS_FILTER_ID, getLocalizedMessageBundleValue(ALL_AGENTS_FILTER_TITLE_BUNDLE_KEY)));

        String filterByAgentTypeLabel = getLocalizedMessageBundleValue(AGENT_TYPE_FILTER_TITLE_BUNDLE_KEY);
        AgentServiceStub agentService = getAgentService();
        AgentTypeDTOList agentTypes = agentService.getAgentTypes();
        com.bluejungle.destiny.services.management.types.AgentTypeDTO[] agentTypesArray = agentTypes.getAgentTypes();
        for (int i = 0; i < agentTypesArray.length; i++) {
            com.bluejungle.destiny.services.management.types.AgentTypeDTO nextAgentType = agentTypesArray[i];
            String nextFilterTitle = MessageFormat.format(filterByAgentTypeLabel, new Object[]{nextAgentType.getTitle()});
            this.filterSelections.add(new SelectItem(nextAgentType.getId(), nextFilterTitle));
        }        
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#getStatusByAgentData()
     */
    public DataModel getStatusByAgentData() {
        return this.agentDataModel;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#getOverallSystemStatus()
     */
    public boolean getOverallSystemStatus() {
        final long nowMs = (new Date()).getTime();
        if (nowMs - this.lastOverallStatusTime > OVERALL_STATUS_INTERVAL) {
            // Refresh the overall status
            try {
                final AgentServiceStub agentService = getAgentService();
                AgentDTOQuerySpec querySpec = new AgentDTOQuerySpec();
                ConcreteAgentDTOQueryTerm term = new ConcreteAgentDTOQueryTerm();
                term.setAgentDTOQueryField(AgentDTOQueryField.ONLINE);
                term.setOperator(RelationalOpDTO.equals);
                term.setValue(new Boolean(false));
                querySpec.setLimit(1);

                AgentDTOQueryTermList agentDTOQueryTermList = new AgentDTOQueryTermList();
                agentDTOQueryTermList.setAgentDTOQueryTerms(new AgentDTOQueryTerm[] {term});
                querySpec.setSearchSpec(agentDTOQueryTermList);

                AgentDTOSortTermList agentDTOSortTermList = new AgentDTOSortTermList();
                agentDTOSortTermList.setAgentDTOSortTerms(new AgentDTOSortTerm[] {});
                querySpec.setSortSpec(agentDTOSortTermList);

                AgentQueryResultsDTO wsResult = agentService.getAgents(querySpec);
                if (wsResult != null) {
                    AgentDTO[] agentList = wsResult.getAgentList().getAgents();
                    if (agentList == null) {
                        // No results
                        setOverallSystemStatus(true);
                    } else {
                        // This will probably always mean false, but we should be robust
                        setOverallSystemStatus(agentList.length == 0);
                    }
                } else {
                    setOverallSystemStatus(false);
                }
                setOverallSystemStatus(false);
            } catch (BadArgumentFault | ServiceNotReadyFault | RemoteException e) {
                getLog().warn(e);
                setOverallSystemStatus(false);
            } finally {
                this.lastOverallStatusTime = nowMs;
            }
        }
        return this.overallSystemStatus;
    }

    /**
     * Read the list of all agents from the AgentService.
     * 
     * @throws RemoteException
     *             if remote call fails
     * @throws ServiceException
     *             if remote call fails
     */
    private void retrieveAgents() throws RemoteException, com.bluejungle.destiny.services.management.ServiceNotReadyFault, BadArgumentFault {

        AgentServiceStub agentService = getAgentService();

        // Create an agent query based on UI Actions and sort spec (TODO):
        final AgentDTOQueryTermList wsSearchSpec = getServerSearchTerms();
        final AgentDTOSortTermList wsSortSpec = new AgentDTOSortTermList();
        wsSortSpec.setAgentDTOSortTerms(new AgentDTOSortTerm[] { getServerSortTerm() });

        // Execute the query:
        AgentDTOQuerySpec wsQuerySpec = new AgentDTOQuerySpec();
        wsQuerySpec.setSearchSpec(wsSearchSpec);
        wsQuerySpec.setSortSpec(wsSortSpec);
        wsQuerySpec.setLimit(this.maxSelectableItemsToDisplay);
        AgentQueryResultsDTO wsAgentResults = agentService.getAgents(wsQuerySpec);
        AgentDTO[] wsAgents = wsAgentResults.getAgentList().getAgents();
        final AgentDataBean[] agents;
        if (wsAgents != null) {
            agents = new AgentDataBean[wsAgents.length];
            AgentPolicyAssemblyStatusDTO wsPolicyStatus = wsAgentResults.getPolicyStatus();
            for (int i = 0; i < wsAgents.length; i++) {
                AgentDataBean agentBean = new AgentDataBean(wsAgents[i], wsPolicyStatus);
                agents[i] = agentBean;
            }
        } else {
            agents = new AgentDataBean[0];
        }
        this.agentDataModel = new ArrayDataModel(agents);
        this.retrieveAgents = false;
    }

    /**
     * Returns the server search terms based on the current filter state and the
     * search box.
     * 
     * @return a fully populated list of agent query terms.
     */
    private AgentDTOQueryTermList getServerSearchTerms() {        
        ArrayList<AgentDTOQueryTerm> queryTerms = new ArrayList<AgentDTOQueryTerm>();
        queryTerms.add(REGISTERED_QUERY_TERM);
        if (!this.activeFilterId.equals(ALL_AGENTS_FILTER_ID)) {
            ConcreteAgentDTOQueryTerm termToAdd = new ConcreteAgentDTOQueryTerm();
            termToAdd.setAgentDTOQueryField(AgentDTOQueryField.TYPE);
            termToAdd.setOperator(RelationalOpDTO.equals);
            termToAdd.setValue(AgentTypeDTO.Factory.fromValue(this.activeFilterId));

            queryTerms.add(termToAdd);
        }

        if (isFilteredByWarningsOnly()) {
            queryTerms.add(NOT_ONLINE_QUERY_TERM);
        }
        
        if (isSearchActive()) {
            String searchStringToUse = getSearchString();
            if (!searchStringToUse.endsWith(STAR_CHAR)) {
                searchStringToUse += STAR_CHAR;
            }
            ConcreteAgentDTOQueryTerm termToAdd = new ConcreteAgentDTOQueryTerm();
            termToAdd.setAgentDTOQueryField(AgentDTOQueryField.HOST);
            termToAdd.setOperator(RelationalOpDTO.starts_with);
            termToAdd.setValue(searchStringToUse);

            queryTerms.add(termToAdd);
        }
        
        AgentDTOQueryTerm[] queryTermsArray = queryTerms.toArray(new AgentDTOQueryTerm[queryTerms.size()]);
        AgentDTOQueryTermList agentDTOQueryTermList = new AgentDTOQueryTermList();
        agentDTOQueryTermList.setAgentDTOQueryTerms(queryTermsArray);

        return agentDTOQueryTermList;
    }

    /**
     * Generates a sort spec based on the sort specifications from the UI
     * 
     * @return sort spec
     */
    private AgentDTOSortTerm getServerSortTerm() {
        AgentDTOSortTerm sortSpec = null;
        String sortFieldName = this.sortStateMgr.getSortFieldName();

        if ("POLICY_UP_TO_DATE".equals(sortFieldName)) {
            sortFieldName = StatusByAgentSortColumnEnumType.LAST_POLICY_UPDATE_COLUMN.getName();
        } else if ("MISSING_IN_LAST_24_HOURS".equals(sortFieldName)) {
            sortFieldName = StatusByAgentSortColumnEnumType.LAST_HEARTBEAT_COLUMN.getName();
        } else if ("PROFILE".equals(sortFieldName)) {
            sortFieldName = StatusByAgentSortColumnEnumType.PROFILE_COLUMN.getName();
        }
        if (StatusByAgentSortColumnEnumType.doesExistByName(sortFieldName)) {
            sortSpec = new AgentDTOSortTerm();
            sortSpec.setAscending(this.sortStateMgr.isSortAscending());
            sortSpec.setField(StatusByAgentSortColumnEnumType.getByName(sortFieldName).getWSField());
        } else {
            getLog().warn("Unknown sort column name : " + sortFieldName);
        }
        return sortSpec;
    }

    /**
     * Used to indicate that the current sort spec is the same state as the new
     * datamodel that was created.
     */
    private void saveSortSpec() {
        this.sortStateMgr.saveState();
    }

    /**
     * Retrieve the Agent Service interface.
     * 
     * Note - Protected for unit test purposes. Yes, not ideal, but it's also
     * better to test with a stub service rather than an actual service
     * 
     * @return the Agent Service interface
     * @throws ServiceException
     *             if the agent service interface could not be located
     */
    protected AgentServiceStub getAgentService() throws AxisFault {
        if (this.agentService == null) {
            IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
            IConfiguration mainCompConfig = (IConfiguration) compMgr.getComponent(IDCCContainer.MAIN_COMPONENT_CONFIG_COMP_NAME);
            String location = (String) mainCompConfig.get(IDCCContainer.DMS_LOCATION_CONFIG_PARAM);
            location += AGENT_SERVICE_LOCATION_SERVLET_PATH;

            this.agentService = new AgentServiceStub(location);
        }

        return this.agentService;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#filterChanged(javax.faces.event.ValueChangeEvent)
     */
    public void filterChanged(ValueChangeEvent filterChangeEvent) {
        this.retrieveAgents = true;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#getFilterValue()
     */
    public String getFilterValue() {
        return this.activeFilterId;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#setFilterValue(java.lang.String)
     */
    public void setFilterValue(String filterToSet) {
        if (filterToSet == null) {
            throw new NullPointerException("filterToSet cannot be null.");
        }

        this.activeFilterId = filterToSet;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#getFilterSelections()
     */
    public Collection<SelectItem> getFilterSelections() {
        return this.filterSelections;
    }

    /**
     * Returns the localized value refered to by the 'key' in the mgmt_console
     * message bundle
     * 
     * @return localized string
     */
    private String getLocalizedMessageBundleValue(String key) {
        Locale currentLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        ResourceBundle bundle = ResourceBundle.getBundle(CommonConstants.MGMT_CONSOLE_BUNDLE_NAME, currentLocale);
        return bundle.getString(key);
    }

    /**
     * Returns the log object
     * 
     * @return the log object
     */
    protected Log getLog() {
        return LOG;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#prerender()
     */
    public void prerender() throws RemoteException, ServiceNotReadyFault, BadArgumentFault {
        if (this.maxSelectableItemsToDisplay == -1) {
            SelectItem firstMaximumSelectableItemsMenuOption = (SelectItem) getMaxSelectableItemsToDisplayOptions().iterator().next();
            this.maxSelectableItemsToDisplay = ((Integer) firstMaximumSelectableItemsMenuOption.getValue()).intValue();
        }

        // Retrieve agents only when required:
        if (this.retrieveAgents) {
            retrieveAgents();
            saveSortSpec();
        } else {
            // We assume the next request by default will require us to retrieve
            // agents. It will be disabled again only if a filter selection is
            // changed, in which case we don't want to retrieve agents.
            this.retrieveAgents = true;
        }
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#getMaxSelectableItemsToDisplayOptions()
     */
    public Collection getMaxSelectableItemsToDisplayOptions() {
        Locale currentLocal = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        return CommonSelectItemResourceLists.MAX_UI_ELEMENT_LIST_SIZE_SELECT_ITEMS.getSelectItemResources(currentLocal);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#getMaxSelectableItemsToDisplay()
     */
    public int getMaxSelectableItemsToDisplay() {
        return this.maxSelectableItemsToDisplay;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#setMaxSelectableItemsToDisplay(int)
     */
    public void setMaxSelectableItemsToDisplay(int maxSelectableItemsToDisplay) {
        this.maxSelectableItemsToDisplay = maxSelectableItemsToDisplay;
        this.retrieveAgents = true;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#getCurrentSortColumnName()
     */
    public String getCurrentSortColumnName() {
        return this.sortStateMgr.getSortFieldName();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#getHostColumnName()
     */
    public String getHostColumnName() {
        return StatusByAgentSortColumnEnumType.HOST_COLUMN.getLogicalName();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#getLastHeartbeatColumnName()
     */
    public String getLastHeartbeatColumnName() {
        return StatusByAgentSortColumnEnumType.LAST_HEARTBEAT_COLUMN.getLogicalName();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#getLastPolicyUpdateColumnName()
     */
    public String getLastPolicyUpdateColumnName() {
        return StatusByAgentSortColumnEnumType.LAST_POLICY_UPDATE_COLUMN.getLogicalName();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#getTypeColumnName()
     */
    public String getTypeColumnName() {
        return StatusByAgentSortColumnEnumType.TYPE_COLUMN.getLogicalName();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#getProfileColumnName()
     */
    public String getProfileColumnName() {
        return StatusByAgentSortColumnEnumType.PROFILE_COLUMN.getLogicalName();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#isCurrentSortAscending()
     */
    public boolean getCurrentSortAscending() {
        return this.sortStateMgr.isSortAscending();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#setCurrentSortAscending(boolean)
     */
    public void setCurrentSortAscending(boolean ascending) {
        this.sortStateMgr.setSortAscending(ascending);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#setCurrentSortColumnName(java.lang.String)
     */
    public void setCurrentSortColumnName(String logicalColumnName) {
        this.sortStateMgr.setSortFieldName(logicalColumnName);
        this.retrieveAgents = true;
    }

    /**
     * Sets the overall system status flag
     * 
     * @param newStatus
     *            new status to flag
     */
    protected void setOverallSystemStatus(boolean newStatus) {
        this.overallSystemStatus = newStatus;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#getMissingInLast24HoursColumnName()
     */
    public String getMissingInLast24HoursColumnName() {
        return StatusByAgentInMemorySortColumnEnumType.MISSING_IN_LAST_24_HOURS_COLUMN.getName();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#getPolicyUptoDateColumnName()
     */
    public String getPolicyUptoDateColumnName() {
        return StatusByAgentInMemorySortColumnEnumType.POLICY_UP_TO_DATE_COLUMN.getName();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#unregisterAgent(java.lang.String)
     */
    public void unregisterAgent(String agentToUnregisterId) throws StatusByAgentViewException, RemoteException {
        if (agentToUnregisterId == null) {
            throw new NullPointerException("agentToUnregisterId cannot be null.");
        }

        try {
            AgentServiceStub agentService = getAgentService();
            ID agentId = new ID();
            agentId.setID(new BigInteger(agentToUnregisterId));

            agentService.unregisterAgent(agentId);
            this.retrieveAgents();
        } catch (UnknownEntryFault | CommitFault | UnauthorizedCallerFault | ServiceNotReadyFault | BadArgumentFault | AxisFault e) {
            throw new StatusByAgentViewException("Failed to unregister agent", e);
        }
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#cancelSearch()
     */
    public void cancelSearch() {
        this.searchString = null;
        this.retrieveAgents = true;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#getSearchString()
     */
    public String getSearchString() {
        return (this.searchString != null) ? this.searchString : "";
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#isSearchActive()
     */
    public boolean isSearchActive() {
        return (this.searchString != null);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#setSearchString(java.lang.String)
     */
    public void setSearchString(String searchString) {
        if (searchString == null) {
            throw new NullPointerException("freeFormSearchString cannot be null.");
        }

        if (!searchString.equals("")) {
            this.searchString = searchString;
            this.retrieveAgents = true;
        }
    }
 
    /**
     * 
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#isFilteredByWarningsOnly()
     */
    public boolean isFilteredByWarningsOnly() {
        return this.filteredByWarningsOnly;
    }

    /**
     * 
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IStatusByAgentBean#setFilteredByWarningsOnly(boolean)
     */
    public void setFilteredByWarningsOnly(boolean filteredByWarningsOnly) {
        this.filteredByWarningsOnly = filteredByWarningsOnly;
    }
}
