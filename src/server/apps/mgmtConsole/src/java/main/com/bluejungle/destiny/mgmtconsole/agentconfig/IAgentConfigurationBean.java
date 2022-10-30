/*
 * Created on Apr 19, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig;

import javax.faces.model.DataModel;

import java.rmi.RemoteException;
import java.util.List;

/**
 * DEVELOPER NOTE: Don't use this class as a pattern. Prefer ActionListeners
 * over Actions and put them in a seperate class rather than on the bean
 */

/**
 * AgentConfiguratioBean contains information/actions used by the display layer
 * to present the agent configuration page of the management console
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/IAgentConfigurationBean.java#2 $
 */
public interface IAgentConfigurationBean {

    /**
     * Clear the host search string
     */
    public static final String EMPTY_HOST_SEARCH_STRING = "";

    /**
     * Retrieve the list of profiles of the associated agent type in the system
     * 
     * @return the list of profiles of the associated agent type in the system
     */
    public DataModel getProfiles();

    /**
     * Retrieve the profile which is the currently selected item in the
     * configuration page menu
     * 
     * @return the profile which is the currently selected item
     */
    public IProfileBean getSelectedProfile();

    /**
     * Retrieve the IDs of all of the possible agent types
     */
    public List<IAgentTypeBean> getAgentTypes();

    /**
     * Retrieve the type of agent for which the contained configuration
     * information is associted
     * 
     * @return the agent type for which the contained configuration information
     *         is assocaited
     */
    public IAgentTypeBean getAgentType();

    /**
     * Set the type of agent for this the contained configuration information is
     * associated
     * 
     * @param agentTypeId
     *            the id of the agent type to set
     */
    public void setAgentType(String agentTypeId);

    /**
     * Retrieve the name of the settigns tab
     * 
     * @return the name of the settigns tab
     */
    public String getSettingsTabName();

    /**
     * Retrieve the name of the hosts tab
     * 
     * @return the name of the hosts tab
     */
    public String getHostsTabName();

    /**
     * Retrieve the list of items to display in the maximum hosts to display
     * drop down box
     * 
     * @return the list of items to display in the maximum hosts to display drop
     *         down box
     */
    public List getMaxHostsToDisplayOptions();

    /**
     * Retrieve the setting for the maximum number of hosts to display
     * 
     * @return the maximum number of hosts to display
     */
    public int getMaxHostsToDisplay();

    /**
     * Set the maximum number of hosts to display
     * 
     * @param maxHostsToDisplay
     *            the maximum number of hosts to display
     */
    public void setMaxHostsToDisplay(int maxHostsToDisplay);

    /**
     * Retrieve the search string being used to filter hosts on the host list
     * tab
     * 
     * @return the search string being used to filter hosts on the host list tab
     */
    public String getHostSearchString();

    /**
     * Set the search string being used to filter hosts on the host list tab. To
     * clear the search, set the search string to
     * {@see IAgentConfigurationBean#EMPTY_HOST_SEARCH_STRING}
     */
    public void setHostSearchString(String hostSearchString);

    /**
     * Specify whether or not the Host tab is currently selected
     * 
     * @param selected
     *            true to indicate that the host tab is selected; false
     *            otherwise
     */
    public void setHostTabSelected(boolean selected);

    /**
     * *************************Actions/ Action Listeners**********************
     */

    /**
     * An ActionListener method invoked when a profile is selected from the
     * profile menu
     * 
     * @param actionEvent
     */
    public String handleSelectProfileActionEvent();

    /**
     * Action invoked when the Add new profile button is clicked
     * 
     * @return an action associated with a configured navigation rule which
     *         points to the create new agent configuration page
     */
    public String addNewProfile();

    /**
     * Action invoked when the process for adding a new profile is cancelled
     * 
     * @return an action associated with a configured navigation rule which
     *         points to the agent configuration main page
     */
    public String cancelAddNewProfile();

    /**
     * Action invoked to save the selected profile. If the profile already
     * exists, update are submitted. Otherwise, a new profile is creatd
     * 
     * @return an action associated with a configured navigation rule which
     *         points to the agent configuration main page
     */
    public String saveSelectedProfile();

    /**
     * Action invoked to delete the selected profile
     */
    public String deleteSelectedProfile() throws RemoteException;
}