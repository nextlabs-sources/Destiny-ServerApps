/*
 * Created on Apr 25, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import com.bluejungle.destiny.services.management.CommitFault;
import com.bluejungle.destiny.services.management.ProfileServiceStub;
import com.bluejungle.destiny.services.management.ServiceNotReadyFault;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.container.dcc.IDCCContainer;
import com.bluejungle.destiny.services.management.ProfileServiceIF;
import com.bluejungle.destiny.services.management.types.ActivityJournalingSettingsDTO;
import com.bluejungle.destiny.services.management.types.ActivityJournalingSettingsDTOList;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.exceptions.SingleErrorBlueJungleException;

/**
 * ActivityJournalingSettingsHelper is used to retrieve named persisted
 * journaling settings from the management server
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/ActivityJournalingSettingsHelper.java#1 $
 */

class ActivityJournalingSettingsHelper {

    private static final ActivityJournalingSettingsHelper SINGLETON_INSTANCE = new ActivityJournalingSettingsHelper();
    private static final Log LOG = LogFactory.getLog(ActivityJournalingSettingsHelper.class.getName());
    private static final String PROFILE_SERVICE_LOCATION_SERVLET_PATH = "/services/ProfileService";

    private Map<String, Map<String, ActivityJournalingSettingsDTO>> activityJournalingSettings = new HashMap();
    private ProfileServiceStub profileService;

    /**
     * Create an instance of ActivityJournalingSettingsHelper
     *  
     */
    private ActivityJournalingSettingsHelper() {
        super();
    }

    /**
     * Retrieve a reference to the ActivityJournalingSettingsHelper
     */
    static ActivityJournalingSettingsHelper getInstance() {
        return SINGLETON_INSTANCE;
    }

    /**
     * Retrieve an Activity Journaling Setting by name
     * 
     * @param assignedJournalName
     *            the name of the journaling setting to retrieve
     * @param assignedJournalName 
     * @return the activity journaling setting assocaited with the provided name
     *         or null if the setting does not exist
     * @throws ActivityJournalingSettingsException
     */
    ActivityJournalingSettingsDTO getActivityJournalingSettings(String agentTypeId, String assignedJournalName) throws ActivityJournalingSettingsException {
        if (agentTypeId == null) {
            throw new NullPointerException("agentTypeId cannot be null.");
        }
        
        if (assignedJournalName == null) {
            throw new NullPointerException("assignedJournalName cannot be null.");
        }

        ActivityJournalingSettingsDTO settingsToReturn = null;

        try {
            settingsToReturn = (ActivityJournalingSettingsDTO) this.getActivityJournalingSettingsMap(agentTypeId).get(assignedJournalName);
        } catch (ServiceNotReadyFault | CommitFault | RemoteException exception) {
            throw new ActivityJournalingSettingsException(exception);
        }

        return settingsToReturn;
    }

    /**
     * Retrieve a Map of the the persisted activity journaling settings
     * 
     * @return a Map of the the persisted activity journaling settings
     * @throws RemoteException
     * @throws ServiceNotReadyFault
     * @throws ServiceException
     */
    private Map getActivityJournalingSettingsMap(String agentTypeId) throws ServiceNotReadyFault, RemoteException, CommitFault {
        if (agentTypeId == null) {
            throw new NullPointerException("agentTypeId cannot be null.");
        }
        
        if (!this.activityJournalingSettings.containsKey(agentTypeId)) {
            ProfileServiceStub profileService = getProfileService();
            ActivityJournalingSettingsDTOList settingsList = profileService.getActivityJournalingSettings(agentTypeId);
            ActivityJournalingSettingsDTO[] settings = settingsList.getActivityJournalingSettings();
            Map<String, ActivityJournalingSettingsDTO> settingsForAgentType = new HashMap<String, ActivityJournalingSettingsDTO>();
            if (settings != null) {
                for (int i = 0; i < settings.length; i++) {
                    settingsForAgentType.put(settings[i].getName(), settings[i]);
                }
            }
            
            this.activityJournalingSettings.put(agentTypeId, settingsForAgentType);
        }

        return this.activityJournalingSettings.get(agentTypeId);
    }

    /**
     * Retrieve the Profile Service interface.
     * 
     * Note - Protected for unit test purposes. Yes, not ideal, but it's also
     * better to test with a stub service rather than an actual service
     * 
     * @return the Profile Service interface
     */
    protected ProfileServiceStub getProfileService() throws AxisFault {
        if(this.profileService == null) {
            IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
            IConfiguration mainCompConfig = (IConfiguration) compMgr.getComponent(IDCCContainer.MAIN_COMPONENT_CONFIG_COMP_NAME);
            String location = (String) mainCompConfig.get(IDCCContainer.DMS_LOCATION_CONFIG_PARAM);
            location += PROFILE_SERVICE_LOCATION_SERVLET_PATH;

            this.profileService = new ProfileServiceStub(location);
        }

        return this.profileService;
    }

    /**
     * @author sgoldstein
     */
    class ActivityJournalingSettingsException extends SingleErrorBlueJungleException {

        /**
         * Create an instance of ActivityJournalingSettingsException
         *  
         */
        public ActivityJournalingSettingsException() {
            super();
        }

        /**
         * Create an instance of ActivityJournalingSettingsException
         * 
         * @param cause
         */
        public ActivityJournalingSettingsException(Throwable cause) {
            super(cause);
        }

    }
}
