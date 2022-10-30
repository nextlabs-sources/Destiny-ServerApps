package com.bluejungle.destiny.mgmtconsole.agentstatus;

import com.bluejungle.destiny.mgmtconsole.shared.MgmtConsoleActionListenerBase;

/**
 * Base JSF Action Listener for the Status By Host View
 * 
 * @author sgoldstein
 */
public abstract class StatusByAgentActionListenerBase extends MgmtConsoleActionListenerBase {

    public static final String STATUS_BY_HOST_VIEW_BEAN_NAME_PARAM_NAME = "statusByAgentViewBeanName";

    /**
     * Retrive the User Groups View Bean as specified as a request parameter
     * with the name, {@see #STATUS_BY_HOST_VIEW_BEAN_NAME_PARAM_NAME}
     * 
     * @return the User Groups View Bean as specified as a request parameter
     *         with the name, {@see #STATUS_BY_HOST_VIEW_BEAN_NAME_PARAM_NAME}
     */
    public IStatusByAgentBean getStatusByAgentViewBean() {
        String statusByAgentViewBeaName = getRequestParameter(STATUS_BY_HOST_VIEW_BEAN_NAME_PARAM_NAME, null);

        if (statusByAgentViewBeaName == null) {
            throw new NullPointerException("Status by host view bean name parameter not found.");
        }

        IStatusByAgentBean statusByAgentViewBean = (IStatusByAgentBean) getManagedBeanByName(statusByAgentViewBeaName);
        if (statusByAgentViewBean == null) {
            throw new IllegalArgumentException("Status By Host View Bean instance with bean name, " + statusByAgentViewBeaName + ", not found");
        }
        return statusByAgentViewBean;
    }
}

