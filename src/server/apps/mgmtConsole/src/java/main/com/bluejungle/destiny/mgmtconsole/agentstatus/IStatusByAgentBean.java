/*
 * Created on May 1, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentstatus;

import com.bluejungle.destiny.services.management.BadArgumentFault;
import com.bluejungle.destiny.services.management.types.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.types.UnauthorizedCallerFault;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;


/**
 * This bean represents the agent status information by host. It exposes overal
 * system status info, as well as a list of agent status records depending on a
 * search filter.
 * 
 * @author safdar
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentstatus/IStatusByAgentBean.java#1 $
 */

public interface IStatusByAgentBean {

    /**
     * Retrieve a DataModel containing a list of AgentDataBean instances which
     * hold data for each registered agent
     * 
     * @return a DataModel containing a list of AgentDataBean instances which
     *         hold data for each registered agent
     */
    public DataModel getStatusByAgentData();

    /**
     * Retrieve a boolean representing the overall system status - true
     * indicates that there is no 'absconding' agent alert, while false
     * indicates that there is at least one 'absconding' agent.
     * 
     * @return
     */
    public boolean getOverallSystemStatus();

    /**
     * Returns the 'value' of the selected/active filter.
     * 
     * @return value of filter
     */
    public String getFilterValue();

    /**
     * Set the filter value
     * 
     * @param filterToSet
     *            the filter value to set
     */
    public void setFilterValue(String filterToSet);

    /**
     * Returns a collection of Selection items for the supported filters
     * 
     * @return selection items
     */
    public Collection getFilterSelections();

    /**
     * Processes the filter-change action performed on the filter dropdown.
     * Returns a new datamodel corresponding to that filter.
     * 
     * @param filterChangeEvent
     */
    public void filterChanged(ValueChangeEvent filterChangeEvent);

    /**
     * Invoked by the framework when the bean is about to be used to render UI
     * after receiving a request.
     * 
     * @throws ServiceNotReadyFault
     * @throws UnauthorizedCallerFault
     * @throws RemoteException
     */
    public void prerender() throws ServiceNotReadyFault, RemoteException, UnauthorizedCallerFault, com.bluejungle.destiny.services.management.ServiceNotReadyFault, BadArgumentFault;

    /**
     * Returns the name of the column on which the sort is currently active
     * 
     * @return currently sorted column
     */
    public String getCurrentSortColumnName();

    /**
     * Sets the sort column
     * 
     * @param logicalColumnName
     */
    public void setCurrentSortColumnName(String logicalColumnName);

    /**
     * Returns whether the currently sorted column is sorted in ascending order
     * 
     * @return is sorted in ascending order
     */
    public boolean getCurrentSortAscending();

    /**
     * Sets the sort order on the corresponding sort column name
     * 
     * @param ascending
     *            true or false
     */
    public void setCurrentSortAscending(boolean ascending);

    /**
     * Returns the logical name of the host column - for sorting purposes.
     * 
     * @return logical name of the host column
     */
    public String getHostColumnName();

    /**
     * Returns the logical name of the type column - for sorting purposes.
     * 
     * @return logical name of the type column
     */
    public String getTypeColumnName();

    /**
     * Returns the logical name of the last heartbeat column - for sorting
     * purposes.
     * 
     * @return logical name of the last heartbeat column
     */
    public String getLastHeartbeatColumnName();

    /**
     * Returns the logical name of the last policy update column - for sorting
     * purposes.
     * 
     * @return logical name of the last policy update column
     */
    public String getLastPolicyUpdateColumnName();

    /**
     * Returns the logical name of the profile column - for sorting purposes.
     * 
     * @return logical name of the profile column
     */
    public String getProfileColumnName();

    /**
     * Returns the logical name of the policy-up-to-date column - for sorting
     * purposes.
     * 
     * @return logical name of the policy-up-to-date column
     */
    public String getPolicyUptoDateColumnName();

    /**
     * Returns the logical name of the missing-in-last-24-hours column for
     * sorting purposes.
     * 
     * @return logical name of the missing-in-last-24-hours column
     */
    public String getMissingInLast24HoursColumnName();

    /**
     * Unregister the agent with the specified id
     * 
     * @param agentToUnregisterId
     *            the id of the agent to unregister
     * @throws StatusByAgentViewException
     */
    public void unregisterAgent(String agentToUnregisterId) throws StatusByAgentViewException, RemoteException;

    /**
     * Cancel any search which had previously been run
     */
    public void cancelSearch();

    /**
     * Set the search string.
     * 
     * @param SearchString
     *            the search string to set
     */
    public void setSearchString(String searchString);

    /**
     * Retrieve the current search string.
     * 
     * @return the current search string
     */
    public String getSearchString();

    /**
     * Determine if a search is currently active
     * 
     * @return true if a search is active; false otherwise
     */
    public boolean isSearchActive();

    /**
     * Returns a collection of options for max number of items for display
     * 
     * @return collection of max items for display options
     */
    public Collection getMaxSelectableItemsToDisplayOptions();

    /**
     * Returns the number of options for max number of items for display
     * 
     * @return number of options for max items for display
     */
    public int getMaxSelectableItemsToDisplay();

    /**
     * Sets the number of options for max items for display
     * 
     * @param maxSelectableItemsToDisplay
     */
    public void setMaxSelectableItemsToDisplay(int maxSelectableItemsToDisplay);

    /**
     * Determine if the current status list is filtered by only those agents
     * with warnings.
     * 
     * @return true if the list is filtered by only those agents with warnings;
     *         false otherwise
     */
    public boolean isFilteredByWarningsOnly();

    /**
     * Set whether or not current status list is filtered by only those agents
     * with warnings.
     * 
     * @param filteredByWarningsOnly
     *            true to filter the list by only those agents with warnings;
     *            false otherwise
     */
    public void setFilteredByWarningsOnly(boolean filteredByWarningsOnly);
}