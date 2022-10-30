/*
 * Created on Aug 3, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import java.util.Calendar;

import com.bluejungle.destiny.types.actions.v1.ActionList;
import com.bluejungle.destiny.types.actions.v1.ActionType;
import com.bluejungle.destiny.types.basic.v1.StringList;
import com.bluejungle.destiny.types.effects.v1.EffectList;
import com.bluejungle.destiny.types.effects.v1.EffectType;
import com.bluejungle.destiny.types.report.v1.Report;
import com.bluejungle.destiny.types.report.v1.ReportSortSpec;

/**
 * This is a simple utility class for the report executor
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/ReportExecutorUtil.java#1 $
 */

public class ReportExecutorUtil {

    /**
     * Clones a web service report and returns a new instance of the report
     * 
     * @param originalwsReport
     *            report to clone
     * @return
     */
    public static Report clone(final Report originalwsReport) {
        //If null report, clone is null as well
        if (originalwsReport == null) {
            return null;
        }

        final Report result = new Report();

        //title, description, shared
        result.setTitle(originalwsReport.getTitle());
        result.setDescription(originalwsReport.getDescription());
        result.setShared(originalwsReport.getShared());
        
        //action list
        final ActionList actionList = originalwsReport.getActions();
        if (actionList != null) {
            final ActionList newActions = new ActionList();
            newActions.setActions(actionList.getActions());
            result.setActions(newActions);
        }

        //Begin date
        final Calendar begDate = originalwsReport.getBeginDate();
        if (begDate != null) {
            final Calendar newBegDate = Calendar.getInstance();
            newBegDate.setTimeInMillis(begDate.getTimeInMillis());
            result.setBeginDate(newBegDate);
        }

        //End date
        final Calendar endDate = originalwsReport.getEndDate();
        if (endDate != null) {
            final Calendar newEndDate = Calendar.getInstance();
            newEndDate.setTimeInMillis(endDate.getTimeInMillis());
            result.setEndDate(newEndDate);
        }

        //effect list
        final EffectList effectList = originalwsReport.getEffects();
        if (effectList != null) {
            final EffectList newEffects = new EffectList();
            EffectType[] effects = effectList.getValues();
            if (effects != null) {
                int listSize = effects.length;
                if (listSize > 0) {
                    EffectType[] newEnforcementsArray = new EffectType[listSize];
                    System.arraycopy(effects, 0, newEnforcementsArray, 0, listSize);
                    newEffects.setValues(newEnforcementsArray);
                }
            }
            result.setEffects(newEffects);
        }

        //policy list
        final StringList policyList = originalwsReport.getPolicies();
        if (policyList != null) {
            final StringList newPolicyList = new StringList();
            String[] policies = policyList.getValues();
            if (policies != null) {
                int listSize = policies.length;
                if (listSize > 0) {
                    String[] newPolicies = new String[listSize];
                    System.arraycopy(policies, 0, newPolicies, 0, listSize);
                    newPolicyList.setValues(newPolicies);
                }
            }
            result.setPolicies(newPolicyList);
        }

        //resource list
        final StringList resourceList = originalwsReport.getResourceNames();
        if (resourceList != null) {
            final StringList newResourceList = new StringList();
            String[] resources = resourceList.getValues();
            if (resources != null) {
                int listSize = resources.length;
                if (listSize > 0) {
                    String[] newResources = new String[listSize];
                    System.arraycopy(resources, 0, newResources, 0, listSize);
                    newResourceList.setValues(newResources);
                }
            }
            result.setResourceNames(newResourceList);
        }

        //Sort specification
        final ReportSortSpec sortSpec = originalwsReport.getSortSpec();
        if (sortSpec != null) {
            final ReportSortSpec newSortSpec = new ReportSortSpec();
            newSortSpec.setDirection(sortSpec.getDirection());
            newSortSpec.setField(sortSpec.getField());
            result.setSortSpec(newSortSpec);
        }

        //Summary type
        result.setSummaryType(originalwsReport.getSummaryType());

        //Target data
        result.setTarget(originalwsReport.getTarget());

        //User list
        final StringList userList = originalwsReport.getUsers();
        if (userList != null) {
            final StringList newUserList = new StringList();
            String[] users = userList.getValues();
            if (users != null) {
                int listSize = users.length;
                if (listSize > 0) {
                    String[] newUsers = new String[listSize];
                    System.arraycopy(users, 0, newUsers, 0, listSize);
                    newUserList.setValues(newUsers);
                }
            }
            result.setUsers(newUserList);
        }
        
        //Logging level
        result.setLoggingLevel(originalwsReport.getLoggingLevel());

        return result;
    }
}