package com.bluejungle.destiny.mgmtconsole.agentconfig;

import com.bluejungle.destiny.mgmtconsole.shared.MgmtConsoleActionListenerBase;

/**
 * Base JSF Action Listener for the Agent Configuration View
 * 
 * @author sgoldstein
 */
public abstract class AgentConfigurationViewActionListenerBase extends MgmtConsoleActionListenerBase {

    public static final String AGENT_CONFIGURATION_VIEW_BEAN_NAME_PARAM_NAME = "agentConfigurationViewBeanName";

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
