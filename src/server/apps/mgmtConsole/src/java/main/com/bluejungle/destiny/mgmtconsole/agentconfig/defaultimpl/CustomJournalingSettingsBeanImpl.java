/*
 * Created on Apr 22, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import com.bluejungle.destiny.mgmtconsole.agentconfig.ICustomJournalingSettingsBean;
import com.bluejungle.destiny.services.management.types.ActivityJournalingSettingsDTO;
import com.bluejungle.destiny.services.management.types.ActivityJournalingSettingsInfo;
import com.bluejungle.domain.types.ActionTypeDTO;
import com.bluejungle.domain.types.ActionTypeDTOList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implementation of the ICustomJournalingSettingsBean interface
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/CustomJournalingSettingsBeanImpl.java#1 $
 */

class CustomJournalingSettingsBeanImpl implements ICustomJournalingSettingsBean {

    private String name;
    private Map<String, Boolean> loggedActions;

    /**
     * Create an instance of CustomJournalingSettingsBeanImpl
     * 
     * @param settingsDTO
     * 
     */
    CustomJournalingSettingsBeanImpl(ActivityJournalingSettingsDTO settingsDTO) {
        if (settingsDTO == null) {
            throw new NullPointerException("settingsDTO cannot be null.");
        }

        this.name = settingsDTO.getName();
        ActionTypeDTOList loggedActionsListDTO = settingsDTO.getLoggedActivities();
        ActionTypeDTO[] loggedActionsArrayDTO = loggedActionsListDTO.getAction();
        this.loggedActions = new HashMap<String, Boolean>();
        if (loggedActionsArrayDTO != null) {
            for (int i = 0; i < loggedActionsArrayDTO.length; i++) {
                loggedActions.put(loggedActionsArrayDTO[i].getValue(), true);
            }
        }
    }

    /**
     * Create an instance of CustomJournalingSettingsBeanImpl
     * 
     */
    CustomJournalingSettingsBeanImpl(String name) {
        if (name == null) {
            throw new NullPointerException("name cannot be null.");
        }

        this.name = name;
        this.loggedActions = new HashMap<String, Boolean>();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.ICustomJournalingSettingsBean#getName()
     */
    public String getName() {
        return this.name;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.ICustomJournalingSettingsBean#getLoggedAction()
     */
    public Map<String, Boolean> getLoggedAction() {
        return this.loggedActions;
    }

    /**
     * Translate the current state to an ActivityJournalingSettingsDTO
     * 
     * @return the current state as an ActivityJournalingSettingsDTO
     */
    ActivityJournalingSettingsDTO getWrappedJournalingSettingsDTO() {
        ActionTypeDTOList actionsToLogList = buildLoggedActionList();
        ActivityJournalingSettingsDTO settingsDTO = new ActivityJournalingSettingsDTO();
        settingsDTO.setName(name);
        settingsDTO.setLoggedActivities(actionsToLogList);

        return settingsDTO;
    }

    /**
     * Translate the current state to an ActivityJournalingSettingsInfo
     * 
     * @return the current state as an ActivityJournalingSettingsInfo
     */
    ActivityJournalingSettingsInfo getWrappedJournalingSettingsInfo() {
        ActionTypeDTOList actionsToLogList = buildLoggedActionList();
        ActivityJournalingSettingsInfo settingsInfo = new ActivityJournalingSettingsInfo();
        settingsInfo.setLoggedActivities(actionsToLogList);

        return settingsInfo;
    }

    /**
     * Build the list of actions which will be logged by the agent
     * 
     * @return the list of actions which will be logged by the agent
     */
    private ActionTypeDTOList buildLoggedActionList() {
        ArrayList<ActionTypeDTO> actionsList = new ArrayList<ActionTypeDTO>(this.loggedActions.size());

        Iterator<Map.Entry<String, Boolean>> actionDTOIterator = this.loggedActions.entrySet().iterator();
        for (int i = 0; actionDTOIterator.hasNext(); i++) {
            Map.Entry<String, Boolean> nextAction = actionDTOIterator.next();
            if (nextAction.getValue().equals(Boolean.TRUE)) {
                actionsList.add(ActionTypeDTO.Factory.fromValue(nextAction.getKey()));
            }
        }

        ActionTypeDTOList actionsToLogList = new ActionTypeDTOList();
        actionsToLogList.setAction(actionsList.toArray(new ActionTypeDTO[actionsList.size()]));
        return actionsToLogList;
    }
}