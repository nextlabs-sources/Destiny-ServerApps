/*
 * Created on Apr 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.bluejungle.destiny.container.dcc.IDCCContainer;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.IStatistic;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.IStatisticSet;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.SimpleStatistic;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.SimpleStatisticSet;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.TimedPullStatisticProvider;
import com.bluejungle.destiny.server.shared.registration.ServerComponentType;
import com.bluejungle.destiny.services.management.CommitFault;
import com.bluejungle.destiny.services.management.ComponentServiceStub;
import com.bluejungle.destiny.services.management.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.UnauthorizedCallerFault;
import com.bluejungle.destiny.services.management.types.Component;
import com.bluejungle.destiny.services.policy.PolicyEditorStub;
import com.bluejungle.destiny.services.policy.PolicyServiceFault;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.comp.IInitializable;
import org.apache.axis2.AxisFault;

/**
 * PolicyStatisicsProvider is a concrete implementation of the
 * IPolicyStatisticsProvider
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/PolicyStatisticsProvider.java#1 $
 */
public class PolicyStatisticsProvider extends TimedPullStatisticProvider implements IPolicyStatisticsProvider, IInitializable {

    private static final String POLICY_SERVICE_LOCATION_SERVLET_PATH = "/services/PolicyEditor";
    private static final String COMPONENT_SERVICE_LOCATION_SERVLET_PATH = "/services/ComponentService";
    private static final Long ERROR_STATISTIC_VALUE = new Long(-1);

    private PolicyEditorStub policyEditorService;
    private ComponentServiceStub componentService;

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.TimedPullStatisticProvider#pullStatistic()
     */
    public IStatisticSet pullStatistic() {
        SimpleStatisticSet statisticSetToReturn = new SimpleStatisticSet();

        try {
            PolicyEditorStub policyService = getPolicyEditorService();
            int numPolices = policyService.getNumDeployedPolicies();
            IStatistic policyCountStat = new SimpleStatistic(new Long(numPolices), Calendar.getInstance());
            statisticSetToReturn.setStatistic(POLICY_COUNT_STAT_KEY, policyCountStat);
        } catch (ServiceNotReadyFault | com.bluejungle.destiny.services.policy.ServiceNotReadyFault exception) {
            getLog().warn("Failed to retrieve policy statistics.  Server not ready", exception);
            populateErrorStatistics(statisticSetToReturn);
        } catch (UnauthorizedCallerFault exception) {
            getLog().warn("Failed to retrieve policy statistics.  User not authorized", exception);
            populateErrorStatistics(statisticSetToReturn);
        } catch (PolicyServiceFault | CommitFault | RemoteException exception) {
            getLog().warn("Failed to retrieve policy statistics", exception);
            populateErrorStatistics(statisticSetToReturn);
        }

        return statisticSetToReturn;
    }

    /**
     * @return
     */
    private void populateErrorStatistics(SimpleStatisticSet statisticSetToPopulate) {
        IStatistic errorStat = new SimpleStatistic(ERROR_STATISTIC_VALUE, Calendar.getInstance());

        statisticSetToPopulate.setStatistic(POLICY_COUNT_STAT_KEY, errorStat);
    }

    /**
     * Retrieve the PolicyEditor Service interface.
     * 
     * Note - Protected for unit test purposes. Yes, not ideal, but it's also
     * better to test with a stub service rather than an actual service
     * 
     * @return the PolicyEditor Service interface
     * @throws RemoteException
     * @throws UnauthorizedCallerFault
     * @throws ServiceNotReadyFault
     */
    protected PolicyEditorStub getPolicyEditorService() throws RemoteException, CommitFault, UnauthorizedCallerFault, ServiceNotReadyFault {
        if (this.policyEditorService == null) {
            String location = getPolicyServerURL();
            location += POLICY_SERVICE_LOCATION_SERVLET_PATH;

            this.policyEditorService = new PolicyEditorStub(location);
        }

        return this.policyEditorService;
    }

    private String getPolicyServerURL() throws ServiceNotReadyFault, UnauthorizedCallerFault, CommitFault, RemoteException {
        ComponentServiceStub componentService = getComponentService();
        Component[] policyServerComponents = componentService.getComponentsByType(ServerComponentType.DPS.getName()).getComp();
        if ((policyServerComponents == null) || (policyServerComponents.length < 1)) {
            /*
             * Couldn't find a policy server. Throw exception
             * ServiceNotReadyFault is not the ideal exception to throw
             * here, but it's somewhat appropriate
             */
            throw new ServiceNotReadyFault();
        }

        // Pick the first active one and return the load balancing url
        for(Component policyServerComponent : policyServerComponents){
        	if(policyServerComponent.getActive()){
        		 return policyServerComponent.getLoadBalancerURL();
        	}
        }
        
        //FIXME not ideal throw exception
        throw new ServiceNotReadyFault();
       
    }

    /**
     * Retrieve the Component Service interface.
     * 
     * Note - Protected for unit test purposes. Yes, not ideal, but it's also
     * better to test with a stub service rather than an actual service
     * 
     * @return the Component Service interface
     */
    protected ComponentServiceStub getComponentService() throws AxisFault {
        if (this.componentService == null) {
            IComponentManager compMgr = getManager();
            IConfiguration mainCompConfig = (IConfiguration) compMgr.getComponent(IDCCContainer.MAIN_COMPONENT_CONFIG_COMP_NAME);
            String location = (String) mainCompConfig.get(IDCCContainer.DMS_LOCATION_CONFIG_PARAM);
            location += COMPONENT_SERVICE_LOCATION_SERVLET_PATH;

            this.componentService = new ComponentServiceStub(location);
        }

        return this.componentService;
    }

}
