/*
 * Created on Apr 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report;

import java.util.Date;
import java.util.List;

import com.bluejungle.destiny.inquirycenter.enumeration.ReportGroupByType;
import com.bluejungle.destiny.types.report.v1.ReportSummaryType;

/**
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/IReport.java#1 $
 */

public interface IReport {

    /**
     * Returns the report description
     * 
     * @return the report description
     */
    public String getDescription();

    /**
     * Returns the report id
     * 
     * @return the report id
     */
    public Long getId();

    /**
     * Sets the report description
     * 
     * @param newDescription
     *            description to set
     */
    public void setDescription(String newDescription);

    /**
     * Sets the report name
     * 
     * @param newName
     *            name to be set
     */
    public void setTitle(String newTitle);
    
    /**
     * Returns whether this report is a shared report
     * (this is deprecated)
     * @return
     */
    public boolean isShared();

    /**
     * Returns whether this report is a shared report
     * 
     * @return
     */
    public boolean getShared();
    
    /**
     * Returns whether this report is owned by the current user
     * 
     * @return whether this report is owned by the current user
     */
    public boolean getOwned();
    
    /**
     * Sets whether this report should be shared
     *  
     */
    public void setShared(boolean shared);
    
    /**
     * Returns the expression containing actions for the report
     * 
     * @return the action expression
     */
    public String getActions();
    
    /**
     * Returns a list of the selected actions
     * 
     * @return a list of the selected actions
     */
    public List getActionsAsList();

    /**
     * Return the date at which the report should start looking for data
     * 
     * @return the start date for the report
     */
    public Date getBeginDate();

    /**
     * Returns the end date for the report execution
     * 
     * @return the end date
     */
    public Date getEndDate();

    /**
     * Returns the enforcements selected for the report as a string expression
     * 
     * @return the report enforcements
     */
    public String getEnforcements();

    /**
     * Returns the enforcements selected for the report
     * 
     * @return the report enforcements
     */
    public List getEnforcementsAsList();

    /**
     * Returns a list of enforcement options available to the UI
     * 
     * @return enforcement options
     */
    public List getEnforcementChoices();

    /**
     * Returns the report group by value
     * 
     * @return the report group by value
     */
    public String getGroupBy();

    /**
     * Returns the list of available group by choices for policy target data
     * 
     * @return the list of available group by choices for policy target data
     */
    public List getGroupByPolicyChoices();

    /**
     * Returns the group by type
     * 
     * @return the group by type
     */
    public ReportGroupByType getGroupByType();

    /**
     * Returns the list of available group by choices for tracking target data
     * 
     * @return the list of available group by choices for tracking target data
     */
    public List getGroupByTrackingChoices();

    /**
     * Returns the expression containing the policies to query on
     * 
     * @return the expression containing the policies to query on
     */
    public String getPolicies();

    /**
     * Returns a list of choices for the policy actions
     * 
     * @return list of action choices
     */
    public List getPolicyActionChoices();

    /**
     * Returns the expression containing the resources to query on
     * 
     * @return the expression containing the resources to query on
     */
    public String getResources();

    /**
     * Returns the display value for the report target data
     * 
     * @return the display value for the report target data
     */
    public String getTargetData();

    /**
     * Returns the display text for the report target data
     * 
     * @return display text
     */
    public String getTargetDisplayName();

    /**
     * Returns the list of possible target data choices
     * 
     * @return the list of possible target data choices
     */
    public List getTargetDataChoices();

    /**
     * Returns the report name
     * 
     * @return the report name
     */
    public String getTitle();
    
    public String getProcessedTitle();

    /**
     * Returns a list of choices for the tracking actions
     * 
     * @return list of action choices
     */
    public List getTrackingActionChoices();

    /**
     * Returns the expression containing the users (or user groups) to query on
     * 
     * @return the expression containing the users (or user groups) to query on
     */
    public String getUsers();
    
    /**
     * Returns the logging level to query on
     * 
     * @return the logging to query on
     */
    public int getLoggingLevel();
    
    /**
     * Returns the list of possible logging level choices
     * 
     * @return the list of possible logging level choices
     */
    public List getLoggingLevelChoices();
    
    /**
     * Returns the last execution time of the report
     * 
     * @return the last execution time of the report
     */
    public Date getRunTime();
    
    public ReportSummaryType getSummaryType();

    public String getSelectedUsers();
    
    public String getSelectedResources();
    
    public String getSelectedPolicies();
    
    /**
     * Returns true if the policy activity target data is currently selected
     * 
     * @return true if the policy activity target data is currently selected
     */
    public boolean isPolicyActivitySelected();

    /**
     * Returns true if the tracking target data is currently selected
     * 
     * @return true if the tracking target data is currently selected
     */
    public boolean isTrackingActivitySelected();
    
    /**
     * Sets the action expression for tracking
     * 
     * @param selectedActions
     *            action expression to set
     */
    public void setActionsAsList(List selectedActions);

    /**
     * Sets the begin date for the report execution
     * 
     * @param newBeginDate
     *            new begin date
     */
    public void setBeginDate(Date newBeginDate);

    /**
     * Sets the end date for the report execution
     * 
     * @param newEndDate
     *            new end date
     */
    public void setEndDate(Date newEndDate);

    /**
     * Sets the selected enforcements on the report
     * 
     * @param selected
     *            enforcements
     */
    public void setEnforcementsAsList(List enforcements);

    /**
     * Sets the report result grouping
     * 
     * @param groupBy
     *            grouping type
     */
    public void setGroupBy(String groupBy);

    /**
     * Sets the policy expression to query on
     * 
     * @param policyExpr
     *            policy expression to set
     */
    public void setPolicies(String policyExpr);

    /**
     * Sets the resource expression to query on
     * 
     * @param resourceExpr
     *            resource expression to set
     */
    public void setResources(String resourceExpr);

    /**
     * Sets the new target data
     * 
     * @param targetData
     *            new target data
     */
    public void setTargetData(String targetData);

    /**
     * Sets the expression representing the list of users
     * 
     * @param usersExpr
     *            expression to set
     */
    public void setUsers(String usersExpr);
    
    /**
     * Sets the logging level
     * 
     * @param level
     */
    public void setLoggingLevel(int level);
    
    /**
     * Sets the last execution time of the report
     * 
     * @param runTime
     */
    public void setRunTime(Date runTime);
}