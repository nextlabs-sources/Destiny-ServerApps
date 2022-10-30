/*
 * Created on Feb 22, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import com.bluejungle.destiny.mgmtconsole.agentconfig.IActionTypeBean;
import com.bluejungle.destiny.mgmtconsole.agentconfig.IJournalingSettingsLevelBean;
import com.bluejungle.destiny.mgmtconsole.agentconfig.IPredefinedJournalingSettingsBean;
import com.bluejungle.destiny.services.management.types.ActionTypeDTO;
import com.bluejungle.destiny.services.management.types.ActionTypeDTOList;
import com.bluejungle.destiny.services.management.types.ActivityJournalingAuditLevelDTO;
import com.bluejungle.destiny.services.management.types.AgentTypeDTO;

import javax.faces.model.SelectItem;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Default implementation of {@link IPredefinedJournalingSettingsBean}
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/PredefinedJournalingSettingsBeanImpl.java#1 $
 */

public class PredefinedJournalingSettingsBeanImpl implements IPredefinedJournalingSettingsBean {

    private final List<IJournalingSettingsLevelBean> journalingSettingsLevelBeanList = new LinkedList<IJournalingSettingsLevelBean>();
    private final List<SelectItem> journalingSettingsLevelsSelectItems = new LinkedList<SelectItem>();
    private final ActionTypeDTOComparator actionTypeDTOComparator = new ActionTypeDTOComparator();
    /**
     * Create an instance of PredefinedJournalingSettingsBeanImpl
     * 
     * @param agentTypeDTO
     */
    public PredefinedJournalingSettingsBeanImpl(AgentTypeDTO agentTypeDTO) {
        if (agentTypeDTO == null) {
            throw new NullPointerException("agentTypeDTO cannot be null.");
        }
        
        SortedMap<ActivityJournalingAuditLevelDTO, List<IActionTypeBean>> journalingSettingsLevels = new TreeMap<ActivityJournalingAuditLevelDTO, List<IActionTypeBean>>(new Comparator<ActivityJournalingAuditLevelDTO>() {
            /**
             * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
             */
            public int compare(ActivityJournalingAuditLevelDTO levelOne, ActivityJournalingAuditLevelDTO levelTwo) {
                return levelTwo.getOrdinal().intValue() - levelOne.getOrdinal().intValue();
            }
            
        });
        
        ActionTypeDTOList actionTypeList = agentTypeDTO.getActions();
        ActionTypeDTO[] actionTypes = actionTypeList.getActionTypes();
        Arrays.sort(actionTypes, actionTypeDTOComparator);
        
        for (int i = 0; i < actionTypes.length; i++) {
            ActivityJournalingAuditLevelDTO level = actionTypes[i].getActivityJournalingAuditLevel();            
            List<IActionTypeBean> actionsForLevel = journalingSettingsLevels.get(level);
            if (actionsForLevel == null) {
                actionsForLevel = new LinkedList<IActionTypeBean>();
                journalingSettingsLevels.put(level, actionsForLevel);
            }
            IActionTypeBean newActionType = new ActionTypeBeanIml(actionTypes[i].getId(), actionTypes[i].getTitle());
            actionsForLevel.add(newActionType);
        }
        
        for (Map.Entry<ActivityJournalingAuditLevelDTO, List<IActionTypeBean>> nextJounrnalingSettingsLevelEntry : journalingSettingsLevels.entrySet()) {
            ActivityJournalingAuditLevelDTO level = nextJounrnalingSettingsLevelEntry.getKey();
            String levelTitle = level.getTitle();
            this.journalingSettingsLevelBeanList.add(new JournalingSettingsLevelBeanImpl(levelTitle, nextJounrnalingSettingsLevelEntry.getValue()));
            this.journalingSettingsLevelsSelectItems.add(new SelectItem(level.getId(), levelTitle));
        }
    }
    
    /**
     *	Only sort by the titles 
     */
    private class ActionTypeDTOComparator implements Comparator<ActionTypeDTO>{
		public int compare(ActionTypeDTO o1, ActionTypeDTO o2) {
			return o1.getTitle().compareTo(o2.getTitle());
		}
    }
    
    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IPredefinedJournalingSettingsBean#getPredefinedJournalingSettingsLevelsAsSelectItems()
     */
    public List<SelectItem> getPredefinedJournalingSettingsLevelsAsSelectItems() {
        return this.journalingSettingsLevelsSelectItems;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IPredefinedJournalingSettingsBean#getPredefinedJournalingSettingsLevels()
     */
    public List<IJournalingSettingsLevelBean> getPredefinedJournalingSettingsLevels() {
        return this.journalingSettingsLevelBeanList;
    }
}
