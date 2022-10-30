/* 
 * Created on May 10, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Set;

import com.bluejungle.destiny.framework.types.ID;
import com.bluejungle.destiny.services.management.BadArgumentFault;
import com.bluejungle.destiny.services.management.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.UnknownEntryFault;
import com.bluejungle.dictionary.IElement;
import com.bluejungle.destiny.container.dcc.IDCCContainer;
import com.bluejungle.destiny.framework.types.IDList;
import com.bluejungle.destiny.framework.types.RelationalOpDTO;
import com.bluejungle.destiny.services.management.AgentServiceStub;
import com.bluejungle.destiny.services.management.types.AgentDTOQueryField;
import com.bluejungle.destiny.services.management.types.AgentDTOQuerySpec;
import com.bluejungle.destiny.services.management.types.AgentDTOQueryTerm;
import com.bluejungle.destiny.services.management.types.AgentDTOQueryTermList;
import com.bluejungle.destiny.services.management.types.AgentDTOSortTerm;
import com.bluejungle.destiny.services.management.types.AgentDTOSortTermField;
import com.bluejungle.destiny.services.management.types.AgentDTOSortTermList;
import com.bluejungle.destiny.services.management.types.AgentQueryResultsDTO;
import com.bluejungle.destiny.services.management.types.ConcreteAgentDTOQueryTerm;
import com.bluejungle.destiny.services.management.types.ORCompositeAgentDTOQueryTerm;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketBean;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;
import com.bluejungle.domain.types.AgentTypeDTO;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.comp.IHasComponentInfo;
import com.bluejungle.framework.comp.LifestyleType;
import org.apache.axis2.AxisFault;

/**
 * A broker for performing Agent Queries. Provides a facade (maybe the class
 * name should be changed?) for running agent queries through the DMS Agent
 * Service. The methods contained are specific to the requirements of the Agent
 * Configuration view of the Management Console
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/AgentQueryBroker.java#2 $
 */
public class AgentQueryBroker implements IHasComponentInfo<AgentQueryBroker> {

    public static final String COMPONENT_NAME = AgentQueryBroker.class.getName();

    private static final ComponentInfo COMP_INFO = new ComponentInfo(COMPONENT_NAME, AgentQueryBroker.class.getName(), LifestyleType.SINGLETON_TYPE);
    private static final String AGENT_SERVICE_LOCATION_SERVLET_PATH = "/services/AgentService";

    private AgentServiceStub agentService;

    /**
     * @see com.bluejungle.framework.comp.IHasComponentInfo#getComponentInfo()
     */
    public ComponentInfo<AgentQueryBroker> getComponentInfo() {
        return COMP_INFO;
    }

    /**
     * Execute an Agent Query to retrieve all agents configured with the profile
     * associated with the specified profile id
     * 
     * @param profileId
     * @param agentType
     * @param searchFilter 
     * @return the matching agents
     * @throws ServiceException
     * @throws ServiceNotReadyFault
     * @throws RemoteException
     */
    public AgentQueryResultsDTO getAgentsForProfile(long profileId, String agentTypeId, String searchFilter, int maxResults) throws ServiceNotReadyFault, RemoteException, BadArgumentFault {

        if (agentTypeId == null) {
            throw new NullPointerException("profileId cannot be null.");
        }
        
        if (searchFilter == null) {
            throw new NullPointerException("searchFilter cannot be null.");
        }
        
        if (maxResults < 0) {
            throw new IllegalArgumentException("maxResults must be greated than 0.");
        }

        ConcreteAgentDTOQueryTerm commProfile = new ConcreteAgentDTOQueryTerm();
        commProfile.setAgentDTOQueryField(AgentDTOQueryField.COMM_PROFILE_ID);
        commProfile.setOperator(RelationalOpDTO.equals);
        commProfile.setValue(new Long(profileId));

        ConcreteAgentDTOQueryTerm host = new ConcreteAgentDTOQueryTerm();
        host.setAgentDTOQueryField(AgentDTOQueryField.HOST);
        host.setOperator(RelationalOpDTO.starts_with);
        host.setValue(searchFilter);

        AgentDTOQueryTerm[] agentQueryTerms = {commProfile, host};

        return runAgentQuery(agentQueryTerms, agentTypeId, maxResults);
    }

    /**
     * Execute an Agent Query to retrieve all agents running on the specified
     * hosts retrieved from the LDAP directory
     * 
     * @param ldapHosts
     * @return the matching agents
     * @throws ServiceException
     * @throws RemoteException
     * @throws ServiceNotReadyFault
     */
    public AgentQueryResultsDTO getAgentForLDAPHosts(Set ldapHosts, String agentTypeId) throws ServiceNotReadyFault, RemoteException, BadArgumentFault {
        if (ldapHosts == null) {
            throw new NullPointerException("ldapHosts cannot be null.");
        }

        AgentDTOQueryTerm[] agentQueryTerms = new AgentDTOQueryTerm[ldapHosts.size()];
        Iterator ldapHostIterator = ldapHosts.iterator();
        for (int i = 0; ldapHostIterator.hasNext(); i++) {
            IElement nextLDAPHost = (IElement) ldapHostIterator.next();
            //TODO: make sure host uniqueName map to dnsName
            String nextHostName = nextLDAPHost.getUniqueName();
            agentQueryTerms[i] = getHostNameEqualsQueryTerm(nextHostName);
        }

        AgentDTOQueryTermList ldapHostQueryTermList = new AgentDTOQueryTermList();
        ldapHostQueryTermList.setAgentDTOQueryTerms(agentQueryTerms);
        ORCompositeAgentDTOQueryTerm compositeQueryTerm = new ORCompositeAgentDTOQueryTerm();
        compositeQueryTerm.setAgentDTOQueryTerms(ldapHostQueryTermList);
        
        return runAgentQuery(new AgentDTOQueryTerm[]{compositeQueryTerm}, agentTypeId, 0);
    }
    
    private static final Character[] ALL_OTHER_CHARS =  new Character[] { 
        '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 
        '<', '>', '.', '[', ']', '{', '}', '(', ')', '&', };


    /**
     * Retrieve the agents for the specified search bucket search spec
     * 
     * @param searchSpec
     * @param agentType
     * @return the matchin agent
     * @throws ServiceNotReadyFault
     * @throws RemoteException
     * @throws ServiceException
     */
    public AgentQueryResultsDTO getAgentsForSearchBucketSearchSpec(ISearchBucketSearchSpec searchSpec, String agentTypeId) throws ServiceNotReadyFault, RemoteException, BadArgumentFault {
        Character[] searchBucketCharacters = searchSpec.getCharactersInBucket();
        //FIXME Not to hardcode the set, the other should be anything but not a to z.
        if (searchBucketCharacters.length == 1 && searchBucketCharacters[0] == ISearchBucketBean.NO_VALUE) {
            searchBucketCharacters = ALL_OTHER_CHARS;
        }
        AgentDTOQueryTerm[] agentQueryTerms = new AgentDTOQueryTerm[searchBucketCharacters.length];
        for (int i = 0; i < searchBucketCharacters.length; i++) {
            agentQueryTerms[i] = getHostNameStartsWithQueryTerm(String.valueOf(searchBucketCharacters[i]));
        }

        AgentDTOQueryTermList queryTermList = new AgentDTOQueryTermList();
        queryTermList.setAgentDTOQueryTerms(agentQueryTerms);
        ORCompositeAgentDTOQueryTerm compositeQueryTerm = new ORCompositeAgentDTOQueryTerm();
        compositeQueryTerm.setAgentDTOQueryTerms(queryTermList);
        
        return runAgentQuery(new AgentDTOQueryTerm[]{compositeQueryTerm}, agentTypeId, searchSpec.getMaximumResultsToReturn());
    }

    /**
     * Retrieve the agents for the specified free form search specification
     * 
     * @param freeFormSearchSpec
     * @param agentType
     * @return the matchin agents
     * @throws ServiceNotReadyFault
     * @throws RemoteException
     * @throws ServiceException
     */
    public AgentQueryResultsDTO getAgentsForFreeFormSearchSpec(IFreeFormSearchSpec freeFormSearchSpec, String agentTypeId) throws ServiceNotReadyFault, RemoteException, BadArgumentFault {
        AgentDTOQueryTerm[] agentQueryTerms = { getHostNameStartsWithQueryTerm(freeFormSearchSpec.getFreeFormSeachString()) };

        return runAgentQuery(agentQueryTerms, agentTypeId, freeFormSearchSpec.getMaximumResultsToReturn());
    }

    /**
     * Set the communication profile for the specified agents
     * 
     * @param agentIds
     * @param profileId
     * @throws ServiceNotReadyFault
     * @throws UnknownEntryFault
     * @throws RemoteException
     */
    public void setCommProfileForAgents(Set agentIds, long profileId) throws ServiceNotReadyFault, UnknownEntryFault, RemoteException {
        BigInteger[] agentIdArray = (BigInteger[]) agentIds.toArray(new BigInteger[agentIds.size()]);
        IDList agentIdList = new IDList();

        for(BigInteger agentId : agentIdArray) {
            ID id = new ID();
            id.setID(agentId);
            agentIdList.addIDList(id);
        }

        ID proId = new ID();
        proId.setID(BigInteger.valueOf(profileId));

        getAgentService().setCommProfileForAgents(agentIdList, proId);
    }

    /**
     * Retrieve an agent query term specifying that the name of the host on
     * which the agent is running starts with the provided value
     * 
     * @param value
     * @return an agent query term representing the expression (agent.hostname
     *         starts_with value)
     */
    private AgentDTOQueryTerm getHostNameStartsWithQueryTerm(String value) {
        return getHostNameQueryTerm(value, RelationalOpDTO.starts_with);
    }

    /**
     * Retrieve an agent query term specifying that the name of the host on
     * which the agent is running equals the provided value
     * 
     * @param value
     * @return an agent query term representing the expression (agent.hostname ==
     *         value)
     */
    private AgentDTOQueryTerm getHostNameEqualsQueryTerm(String value) {
        return getHostNameQueryTerm(value, RelationalOpDTO.equals);
    }

    /**
     * Retrieve an agent query term specifying that the name of the host on
     * which the agent is running is related to the specified value as described
     * by the specified operator
     * 
     * @param value
     * @return an agent query term representing the expression (agent.hostname
     *         operator value)
     */
    private AgentDTOQueryTerm getHostNameQueryTerm(Object value, RelationalOpDTO operator) {
        ConcreteAgentDTOQueryTerm agentDTOQueryTerm = new ConcreteAgentDTOQueryTerm();
        agentDTOQueryTerm.setAgentDTOQueryField(AgentDTOQueryField.HOST);
        agentDTOQueryTerm.setOperator(operator);
        agentDTOQueryTerm.setValue(value);

        return agentDTOQueryTerm;
    }

    /**
     * Run an agent query
     * 
     * @param wsAgentQueryTerms
     * @param maximumResults 
     * @return the matching agents
     * @throws RemoteException
     * @throws ServiceNotReadyFault
     * @throws ServiceException
     */
    private AgentQueryResultsDTO runAgentQuery(AgentDTOQueryTerm[] wsAgentQueryTerms, String agentTypeId, int maximumResults) throws ServiceNotReadyFault, RemoteException, BadArgumentFault {
		ConcreteAgentDTOQueryTerm wsTypeQueryTerm = new ConcreteAgentDTOQueryTerm();
        wsTypeQueryTerm.setAgentDTOQueryField(AgentDTOQueryField.TYPE);
        wsTypeQueryTerm.setOperator(RelationalOpDTO.equals);
        wsTypeQueryTerm.setValue(AgentTypeDTO.Factory.fromValue(agentTypeId));
        AgentServiceStub agentService = getAgentService();

        //ArrayCopy... not great
        int length = wsAgentQueryTerms.length;
        AgentDTOQueryTerm[] wsSearchSpecTerms = new AgentDTOQueryTerm[length + 1];
        System.arraycopy(wsAgentQueryTerms, 0, wsSearchSpecTerms, 0, length);
        wsSearchSpecTerms[length] = wsTypeQueryTerm;
        AgentDTOQueryTermList wsAgentQueryTermList = new AgentDTOQueryTermList();
        wsAgentQueryTermList.setAgentDTOQueryTerms(wsSearchSpecTerms);

        final AgentDTOSortTerm wsSortSpec = new AgentDTOSortTerm();
        wsSortSpec.setField(AgentDTOSortTermField.HOST);
        wsSortSpec.setAscending(true);
        final AgentDTOSortTermList wsSortTermList = new AgentDTOSortTermList();
        wsSortTermList.setAgentDTOSortTerms(new AgentDTOSortTerm[] { wsSortSpec });
        final AgentDTOQuerySpec wsQuerySpec = new AgentDTOQuerySpec();
        wsQuerySpec.setLimit(maximumResults);
        wsQuerySpec.setSearchSpec(wsAgentQueryTermList);
        wsQuerySpec.setSortSpec(wsSortTermList);
        return agentService.getAgents(wsQuerySpec);
    }

    /**
     * Retrieve the Agent Service interface.
     * 
     * Note - Protected for unit test purposes. Yes, not ideal, but it's also
     * better to test with a stub service rather than an actual service
     * 
     * @return the Agent Service interface
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
}
