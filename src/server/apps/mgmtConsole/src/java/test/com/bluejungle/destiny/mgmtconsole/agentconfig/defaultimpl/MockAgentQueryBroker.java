/*
 * Created on May 13, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import java.rmi.RemoteException;
import java.util.Set;

import javax.xml.rpc.ServiceException;

import org.apache.axis.types.NonNegativeInteger;

import com.bluejungle.destiny.framework.types.ServiceNotReadyFault;
import com.bluejungle.destiny.framework.types.UnknownEntryFault;
import com.bluejungle.destiny.services.management.types.AgentDTO;
import com.bluejungle.destiny.services.management.types.AgentDTOList;
import com.bluejungle.destiny.services.management.types.AgentPolicyAssemblyStatusDTO;
import com.bluejungle.destiny.services.management.types.AgentQueryResultsDTO;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;
import com.bluejungle.domain.agenttype.AgentTypeEnumType;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/MockAgentQueryBroker.java#1 $
 */

public class MockAgentQueryBroker extends AgentQueryBroker {
    
    private AgentDTO[] agents = new AgentDTO[0];
    private boolean getAgentsForProfileInvoked = false;

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.AgentQueryBroker#getAgentForLDAPHosts(java.util.Set,
     *      com.bluejungle.domain.agenttype.AgentTypeEnumType)
     */
    public AgentQueryResultsDTO getAgentForLDAPHosts(Set ldapHosts, AgentTypeEnumType agentType) throws ServiceNotReadyFault, RemoteException, ServiceException {
        return new AgentQueryResultsDTO(new AgentDTOList(agents), new AgentPolicyAssemblyStatusDTO());
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.AgentQueryBroker#getAgentsForFreeFormSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec,
     *      com.bluejungle.domain.agenttype.AgentTypeEnumType)
     */
    public AgentQueryResultsDTO getAgentsForFreeFormSearchSpec(IFreeFormSearchSpec freeFormSearchSpec, AgentTypeEnumType agentType) throws ServiceNotReadyFault, RemoteException, ServiceException {
        return new AgentQueryResultsDTO(new AgentDTOList(agents), new AgentPolicyAssemblyStatusDTO());
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.AgentQueryBroker#getAgentsForProfile(long, java.lang.String, java.lang.String, int)
     */
    public AgentQueryResultsDTO getAgentsForProfile(long profileId, String agentType, String searchFilter, int maxResults) throws ServiceException, ServiceNotReadyFault, RemoteException {
        this.getAgentsForProfileInvoked = true;
        return new AgentQueryResultsDTO(new AgentDTOList(agents), new AgentPolicyAssemblyStatusDTO());
    }

    /**
     * Determine whether or not getAgentsForProfile was invoked
     * 
     * @return true if getAgentsForProfile was invoked, false otherwise;
     */
    public boolean wasGetAgentsForProfileInvoked() {
        boolean valueToReturn = this.getAgentsForProfileInvoked ;
        this.getAgentsForProfileInvoked = false;
        return valueToReturn;
    }
    
    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.AgentQueryBroker#getAgentsForSearchBucketSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec,
     *      com.bluejungle.domain.agenttype.AgentTypeEnumType)
     */
    public AgentQueryResultsDTO getAgentsForSearchBucketSearchSpec(ISearchBucketSearchSpec searchSpec, AgentTypeEnumType agentType) throws ServiceNotReadyFault, RemoteException, ServiceException {
        return new AgentQueryResultsDTO(new AgentDTOList(agents), new AgentPolicyAssemblyStatusDTO());
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.AgentQueryBroker#setCommProfileForAgents(java.util.Set,
     *      long)
     */
    public void setCommProfileForAgents(Set agentIds, long profileId) throws ServiceNotReadyFault, UnknownEntryFault, RemoteException, ServiceException {

    }

    /**
     * @param agents
     */
    public void setAgents(AgentDTO[] agents) {
        this.agents = agents;
    }
}
