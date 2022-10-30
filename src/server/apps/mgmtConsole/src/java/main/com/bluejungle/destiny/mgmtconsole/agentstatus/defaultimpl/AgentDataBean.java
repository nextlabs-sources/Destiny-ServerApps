/*
 * Created on May 2, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentstatus.defaultimpl;

import java.math.BigInteger;
import java.util.Calendar;

import com.bluejungle.destiny.mgmtconsole.agentstatus.IAgentDataBean;
import com.bluejungle.destiny.services.management.types.AgentDTO;
import com.bluejungle.destiny.services.management.types.AgentPolicyAssemblyStatusDTO;

/**
 * @author safdar
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentstatus/defaultimpl/AgentDataBean.java#1 $
 */

public class AgentDataBean implements IAgentDataBean {

    /**
     * 24 hours in milliseconds
     */
    private static final long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;

    /**
     * Agent data
     */
    private AgentDTO agentData;

    /**
     * Policy assembly status data
     */
    private AgentPolicyAssemblyStatusDTO policyAssemblyStatus;

    /**
     * Constructor
     * 
     * @param agentData
     */
    public AgentDataBean(AgentDTO agentData, AgentPolicyAssemblyStatusDTO policyStatus) {
        this.agentData = agentData;
        this.policyAssemblyStatus = policyStatus;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IAgentDataBean#getAgentId()
     */
    public String getAgentId() {
        return this.agentData.getId().toString();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IAgentDataBean#getHostName()
     */
    public String getHostName() {
        return this.agentData.getHost();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IAgentDataBean#getType()
     */
    public String getType() {
        return this.agentData.getType().getId();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IAgentDataBean#getTypeDisplayValue()
     */
    public String getTypeDisplayValue() {
        return this.agentData.getType().getTitle();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IAgentDataBean#getLastHeartbeatTime()
     */
    public Calendar getLastHeartbeatTime() {
        return this.agentData.getLastHeartbeat();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IAgentDataBean#getLastPolicyUpdateTime()
     */
    public Calendar getLastPolicyUpdateTime() {
        Calendar policyUpdateTime = null;
        AgentPolicyAssemblyStatusDTO policyStatus = this.agentData.getPolicyAssemblyStatus();
        if (policyStatus != null) {
            policyUpdateTime = policyStatus.getLastPolicyUpdate();
        }
        return policyUpdateTime;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IAgentDataBean#isPolicyUpToDate()
     */
    public boolean isPolicyUpToDate() {
        boolean isUpToDate = false;
        AgentPolicyAssemblyStatusDTO wsAgentPolicyStatus = this.agentData.getPolicyAssemblyStatus();
        if (wsAgentPolicyStatus != null) {
            final Calendar lastUpdated = wsAgentPolicyStatus.getLastPolicyUpdate();
            //Calendar can be null before any policy gets deployed
            if (lastUpdated != null) {
                isUpToDate = !lastUpdated.before(this.policyAssemblyStatus.getLastPolicyUpdate());
            }
        }
        return isUpToDate;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IAgentDataBean#isMissingMoreThan24Hours()
     */
    public boolean isMissingMoreThan24Hours() {
        Calendar lastHeartbeat = getLastHeartbeatTime();
        boolean missing = true;
        if (lastHeartbeat != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(cal.getTimeInMillis() - ONE_DAY_IN_MILLIS);
            // Return whether the last heartbeat was more than 24 hours ago:
            missing = lastHeartbeat.before(cal);
        }
        return missing;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IAgentDataBean#getCommProfileID()
     */
    public BigInteger getCommProfileID() {
        return this.agentData.getCommProfileID().getID();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentstatus.IAgentDataBean#getCommProfileName()
     */
    public String getCommProfileName() {
        return this.agentData.getCommProfileName();
    }
}
