/*
 * Created on May 23, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers;

import com.bluejungle.destiny.inquirycenter.enumeration.ReportGroupByType;
import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.bluejungle.destiny.types.report.v1.ReportSummaryType;

import java.util.Date;
import java.util.List;

/**
 * @author safdar
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/MockReportImpl.java#1 $
 */

public class MockReportImpl implements IReport {

    private String policies = "";
    private String users = "";

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getDescription()
     */
    public String getDescription() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getId()
     */
    public Long getId() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getOwned()
     */
    public boolean getOwned() {
        return false;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#isShared()
     */
    public boolean isShared() {
        return false;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#setDescription(java.lang.String)
     */
    public void setDescription(String newDescription) {
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#setShared(boolean)
     */
    public void setShared(boolean shared) {
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#setTitle(java.lang.String)
     */
    public void setTitle(String newTitle) {
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getTitle()
     */
    public String getTitle() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getActions()
     */
    public String getActions() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getActionsAsList()
     */
    public List getActionsAsList() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getBeginDate()
     */
    public Date getBeginDate() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getEndDate()
     */
    public Date getEndDate() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getEnforcements()
     */
    public String getEnforcements() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getEnforcementsAsList()
     */
    public List getEnforcementsAsList() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getEnforcementChoices()
     */
    public List getEnforcementChoices() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getObligationChoices()
     */
    public List getObligationChoices() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getObligations()
     */
    public List getObligations() {
        return null;
    }
    
    public int getLoggingLevel(){
        return 0;
    }
    
    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getLoggingLevelChoices()
     */
    public List getLoggingLevelChoices() {
        return null;
    }
    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#setObligations(java.util.List)
     */
    public void setObligations(List obligations) {
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getGroupBy()
     */
    public String getGroupBy() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getGroupByPolicyChoices()
     */
    public List getGroupByPolicyChoices() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getGroupByType()
     */
    public ReportGroupByType getGroupByType() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getGroupByTrackingChoices()
     */
    public List getGroupByTrackingChoices() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getPolicies()
     */
    public String getPolicies() {
        return this.policies;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getPolicyActionChoices()
     */
    public List getPolicyActionChoices() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getResources()
     */
    public String getResources() {
        return "";
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getResourceClasses()
     */
    public String getResourceClasses() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getResourceNames()
     */
    public String getResourceNames() {
        return "Resource1, Resource2, Resource3";
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getTargetData()
     */
    public String getTargetData() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getTargetDisplayName()
     */
    public String getTargetDisplayName() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getTargetDataChoices()
     */
    public List getTargetDataChoices() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getUsers()
     */
    public String getUsers() {
        return this.users;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#isPolicyActivitySelected()
     */
    public boolean isPolicyActivitySelected() {
        return false;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#isTrackingActivitySelected()
     */
    public boolean isTrackingActivitySelected() {
        return false;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#getTrackingActionChoices()
     */
    public List getTrackingActionChoices() {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#setActionsAsList(java.util.List)
     */
    public void setActionsAsList(List newList) {
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#setBeginDate(java.util.Date)
     */
    public void setBeginDate(Date newBeginDate) {
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#setEndDate(java.util.Date)
     */
    public void setEndDate(Date newEndDate) {
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#setEnforcementsAsList(java.util.List)
     */
    public void setEnforcementsAsList(List enforcements) {
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#setGroupBy(java.lang.String)
     */
    public void setGroupBy(String groupBy) {
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#setPolicies(java.lang.String)
     */
    public void setPolicies(String policyExpr) {
        this.policies = policyExpr;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#setResources(java.lang.String)
     */
    public void setResources(String resourceExpr) {
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#setResourceNames(java.lang.String)
     */
    public void setResourceNames(String resourceNames) {
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#setResourceClasses(java.lang.String)
     */
    public void setResourceClasses(String resourceClasses) {
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#setTargetData(java.lang.String)
     */
    public void setTargetData(String targetData) {
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReport#setUsers(java.lang.String)
     */
    public void setUsers(String usersExpr) {
        this.users = usersExpr;
    }

    public void setLoggingLevel(int level) {
    }

    public Date getRunTime() {
        return null;
    }

    public void setRunTime(Date runTime) {
    }

    public ReportSummaryType getSummaryType() {
        return null;
    }

    public boolean getShared() {
        return false;
    }

    public String getSelectedPolicies() {
        return null;
    }

    public String getSelectedResources() {
        return null;
    }

    public String getSelectedUsers() {
        return null;
    }

    public String getProcessedTitle() {
        return null;
    }
}