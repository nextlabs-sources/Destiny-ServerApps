/*
 * Created on Feb 9, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status;

/**
 * Represents that number of agents registered of a given type
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/status/IAgentCountBean.java#1 $
 */

public interface IAgentCountBean {

    /**
     * Retrieve the agent type of the agents counted by this agent count bean
     * 
     * @return the agent type of the agents counted by this agent count bean
     */
    public IAgentTypeBean getAgentType();

    /**
     * Retrieve the number agents registered of the type of the agents counted
     * by this agent count bean
     * 
     * @return the number agents registered of the type of the agents counted by
     *         this agent count bean
     */
    public Long getNumRegistered();
}
