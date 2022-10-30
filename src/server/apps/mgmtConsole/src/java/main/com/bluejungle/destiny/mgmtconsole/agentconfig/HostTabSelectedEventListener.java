/*
 * Created on Apr 18, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig;

import com.bluejungle.destiny.webui.controls.SelectedTabChangeEvent;
import com.bluejungle.destiny.webui.controls.SelectedTabChangeEventListener;
import com.bluejungle.destiny.webui.controls.UITab;
import com.bluejungle.destiny.webui.framework.faces.FacesListenerBase;

import javax.faces.event.AbortProcessingException;

/**
 * This FacesListener is responsible for listening for tab selection in the
 * agent config view. It's main purpose is to inform the
 * AgentConfigurationViewBean whether or not the host tab is selected
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/branch/Destiny_Beta4_Stable/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/HostTabSelectedEventListener.java#2 $
 */

public class HostTabSelectedEventListener extends FacesListenerBase implements SelectedTabChangeEventListener {

    public static final String AGENT_CONFIGURATION_VIEW_BEAN_NAME_PARAM_NAME = "agentConfigurationViewBeanName";

    /**
     * @see com.bluejungle.destiny.webui.controls.SelectedTabChangeEventListener#processSelectedTabChange(com.bluejungle.destiny.webui.controls.SelectedTabChangeEvent)
     */
    public void processSelectedTabChange(SelectedTabChangeEvent event) throws AbortProcessingException {
        IAgentConfigurationBean agentConfigurationBean = getAgentConfigurationViewBean();

        UITab selectedTab = event.getSelectedTab();
        String selectedTabName = selectedTab.getName();
        if (selectedTabName.equals(agentConfigurationBean.getHostsTabName())) {
            agentConfigurationBean.setHostTabSelected(true);
        } else {
            agentConfigurationBean.setHostTabSelected(false);
        }
    }

    /**
     * Retrive the Agent Configuration Vuew Bean as specified as a request
     * parameter with the name,
     * {@see #AGENT_CONFIGURATION_VIEW_BEAN_NAME_PARAM_NAME}
     * 
     * @return the Agent Configuration View Bean as specified as a request
     *         parameter with the name,
     *         {@see #AGENT_CONFIGURATION_VIEW_BEAN_NAME_PARAM_NAME}
     */
    public IAgentConfigurationBean getAgentConfigurationViewBean() {
        String agentConfigurationsViewBeaName = getRequestParameter(AGENT_CONFIGURATION_VIEW_BEAN_NAME_PARAM_NAME, null);

        if (agentConfigurationsViewBeaName == null) {
            throw new NullPointerException("Agent Configuration view bean name parameter not found.");
        }

        IAgentConfigurationBean agentConfigurationsViewBean = (IAgentConfigurationBean) getManagedBeanByName(agentConfigurationsViewBeaName);
        if (agentConfigurationsViewBean == null) {
            throw new IllegalArgumentException("Agent Configuration View Bean instance with bean name, " + agentConfigurationsViewBeaName + ", not found");
        }
        return agentConfigurationsViewBean;
    }
}
