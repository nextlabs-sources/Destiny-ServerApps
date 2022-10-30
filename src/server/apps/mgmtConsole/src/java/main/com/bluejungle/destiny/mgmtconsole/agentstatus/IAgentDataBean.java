/*
 * Created on May 1, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentstatus;

import java.math.BigInteger;
import java.util.Calendar;

/**
 * @author safdar
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentstatus/IAgentDataBean.java#1 $
 */

public interface IAgentDataBean {

    /**
     * Retrieve the id of the associted agent
     * 
     * @return the id of the associated agent
     */
    public String getAgentId();

    /**
     * Retrieves the name of the host on which this agent is installed
     * 
     * @return name of host
     */
    public String getHostName();

    /**
     * Retrieves the type of this agent - 'FileServerAgent' or 'DesktopAgent'
     * 
     * @return type of agent
     */
    public String getType();

    /**
     * Returns the (localized) display value of the agent type
     * 
     * @return the (localized) display value of the agent type
     */
    public String getTypeDisplayValue();

    /**
     * Retrieves the time the last heartbeat was received from this agent
     * 
     * @return time of last heartbeat
     */
    public Calendar getLastHeartbeatTime();

    /**
     * Retrieves the time the last policy update took effect on this agent
     * 
     * @return time of policy update
     */
    public Calendar getLastPolicyUpdateTime();

    /**
     * Retrieves whether the policy updates that are in effect on this agent are
     * up-to-date.
     * 
     * @return up-to-date status of policies on agent
     */
    public boolean isPolicyUpToDate();

    /**
     * Retrieves whether the agent has sent a heartbeat in the last 24 hours or
     * not. true means agent has sent a heartbeat, false means agent is missing
     * for 24 hours
     * 
     * @return boolean
     */
    public boolean isMissingMoreThan24Hours();

    /**
     * Retrieves the name of the comm profile associated to this agent
     * 
     * @return comm profile name
     */
    public String getCommProfileName();

    /**
     * Retrieves the id of the comm profile associated with this agent
     * 
     * @return comm profile id
     */
    public BigInteger getCommProfileID();
}