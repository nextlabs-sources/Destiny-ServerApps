/*
 * Created on May 14, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.faces.model.SelectItem;

import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.bluejungle.destiny.types.actions.v1.ActionList;
import com.bluejungle.destiny.types.basic.v1.StringList;
import com.bluejungle.destiny.types.effects.v1.EffectList;
import com.bluejungle.destiny.types.effects.v1.EffectType;
import com.bluejungle.destiny.types.report.v1.Report;
import com.bluejungle.destiny.types.report.v1.ReportTargetType;
import com.bluejungle.destiny.webui.tags.BaseJSFTest;
import com.bluejungle.domain.action.ActionEnumType;

/**
 * This is the test class for the report data object.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/ReportDataObjectTest.java#1 $
 */

public class ReportDataObjectTest extends BaseJSFTest {

    private static final String INQUIRY_CENTER_BUNDLE_NAME = "InquiryCenterMessages";
    private static final String ANY_ACTION_BUNDLE_KEY = "reports_form_any_action_value";
    private static final String ANY_POLICY_BUNDLE_KEY = "reports_form_any_policy_value";
    private static final String ANY_USER_BUNDLE_KEY = "reports_form_any_user_value";
    private static final String QUICK_REPORT_NAME_BUNDLE_KEY = "my_reports_quick_report_title";
    private static final String DOCUMENT_ACTIVITY_BUNDLE_KEY = "my_reports_enum_target_data_activity";
    private static final String POLICY_ACTIVITY_BUNDLE_KEY = "my_reports_enum_target_data_policy";

    /**
     * This test verifies the basics about the report object class
     */
    public void testReportDataObjectClassBasics() {
        ReportImpl report = new ReportImpl();
        assertTrue("Report class should implement the correct interface", report instanceof IReport);
        boolean exThrown = false;
        try {
            report = new ReportImpl(null);
        } catch (NullPointerException e) {
            exThrown = true;
        }
        assertTrue("The report class cannot accept a null report object", exThrown);
    }

    /**
     * This test verifies that the correct key in the resource bundle is used if
     * there are no values to be displayed for a field.
     */
    public void testDefaultFieldDisplay() {
        ResourceBundle bundle = ResourceBundle.getBundle(INQUIRY_CENTER_BUNDLE_NAME);
        assertNotNull("The test should be setup to have this bundle", bundle);
        ReportImpl report = new ReportImpl();
        String anyAction = report.getActions();
        assertNotNull(anyAction);
        assertEquals("Default string should be displayed if no actions are specified", anyAction, bundle.getString(ANY_ACTION_BUNDLE_KEY));
        String anyPolicy = report.getPolicies();
        assertNotNull("Default string should be displayed if no policies are specified", anyPolicy);
        assertEquals(anyPolicy, bundle.getString(ANY_POLICY_BUNDLE_KEY));
        String anyUser = report.getUsers();
        assertNotNull(anyUser);
        assertEquals("Default string should be displayed if no users are specified", anyUser, bundle.getString(ANY_USER_BUNDLE_KEY));
    }

    /**
     * This test verifies that the target data field is localized properly
     */
    public void testReportDataObjectTargetDataLocalization() {
        ResourceBundle bundle = ResourceBundle.getBundle(INQUIRY_CENTER_BUNDLE_NAME);
        assertNotNull("The test should be setup to have this bundle", bundle);
        Report wsReport = new Report();
        //Set to tracking
        wsReport.setTarget(ReportTargetType.ActivityJournal);
        ReportImpl report = new ReportImpl(wsReport);
        String uiTargetData = report.getTargetDisplayName();
        assertEquals(uiTargetData, bundle.getString(DOCUMENT_ACTIVITY_BUNDLE_KEY));
        //Set to policy
        wsReport.setTarget(ReportTargetType.PolicyEvents);
        report = new ReportImpl(wsReport);
        uiTargetData = report.getTargetDisplayName();
        assertEquals(uiTargetData, bundle.getString(POLICY_ACTIVITY_BUNDLE_KEY));
    }

    /**
     * This test verifies that the "group by" Field is properly working
     */
    public void testReportDataObjectGroupByFieldChoices() {
        ResourceBundle bundle = ResourceBundle.getBundle(INQUIRY_CENTER_BUNDLE_NAME);
        assertNotNull("The test should be setup to have this bundle", bundle);
        Report wsReport = new Report();
        ReportImpl report = new ReportImpl(wsReport);
        List trackingChoices = report.getGroupByTrackingChoices();
        assertEquals("The number of choices should be correct", 4, trackingChoices.size());
        Iterator it = trackingChoices.iterator();
        Set choicesValue = new HashSet();
        while (it.hasNext()) {
            Object choice = it.next();
            assertTrue("Each item should be a select item", choice instanceof SelectItem);
            Object value = ((SelectItem) choice).getValue();
            assertTrue("Each item should have a String value", value instanceof String);
            assertFalse("Each item value should be unique", choicesValue.contains(value));
            choicesValue.add(value);
        }

        List policyChoices = report.getGroupByPolicyChoices();
        assertEquals("The number of choices should be correct", 5, policyChoices.size());
        it = trackingChoices.iterator();
        choicesValue = new HashSet();
        while (it.hasNext()) {
            Object choice = it.next();
            assertTrue("Each item should be a select item", choice instanceof SelectItem);
            Object value = ((SelectItem) choice).getValue();
            assertTrue("Each item should have a String value", value instanceof String);
            assertFalse("Each item value should be unique", choicesValue.contains(value));
            choicesValue.add(value);
        }
    }

    /**
     * This test verifies that the "group by" Field is properly working
     */
    public void testReportDataObjectTargetDataChoices() {
        ResourceBundle bundle = ResourceBundle.getBundle(INQUIRY_CENTER_BUNDLE_NAME);
        assertNotNull("The test should be setup to have this bundle", bundle);
        Report wsReport = new Report();
        ReportImpl report = new ReportImpl(wsReport);
        List choices = report.getTargetDataChoices();
        assertEquals("The number of choices should be correct", 2, choices.size());
        Iterator it = choices.iterator();
        Set choicesValue = new HashSet();
        while (it.hasNext()) {
            Object choice = it.next();
            assertTrue("Each item should be a select item", choice instanceof SelectItem);
            Object value = ((SelectItem) choice).getValue();
            assertTrue("Each item should have a String value", value instanceof String);
            assertFalse("Each item value should be unique", choicesValue.contains(value));
            choicesValue.add(value);
        }
    }

    /**
     * This tests that the target selections work
     *  
     */
    public void testReportDataObjectTargetSelection() {

    }

    /**
     * This tests that the enformcent choices are correct
     *  
     */
    public void testReportDataObjectEnforcementChoices() {
        ResourceBundle bundle = ResourceBundle.getBundle(INQUIRY_CENTER_BUNDLE_NAME);
        assertNotNull("The test should be setup to have this bundle", bundle);
        Report wsReport = new Report();
        ReportImpl report = new ReportImpl(wsReport);
        List choices = report.getEnforcementChoices();
        assertEquals("The number of choices should be correct", 3, choices.size());
        Iterator it = choices.iterator();
        Set choicesValue = new HashSet();
        while (it.hasNext()) {
            Object choice = it.next();
            assertTrue("Each item should be a select item", choice instanceof SelectItem);
            Object value = ((SelectItem) choice).getValue();
            assertTrue("Each item should have a String value", value instanceof String);
            assertFalse("Each item value should be unique", choicesValue.contains(value));
            choicesValue.add(value);
        }
    }

    /**
     * This tests that the enforcement selections from the UI work correctly
     *  
     */
    public void testReportDataObjectEnforcementSelection() {
        ResourceBundle bundle = ResourceBundle.getBundle(INQUIRY_CENTER_BUNDLE_NAME);
        assertNotNull("The test should be setup to have this bundle", bundle);
        Report wsReport = new Report();
        ReportImpl report = new ReportImpl(wsReport);

        // Get the selected enforcements and make sure none are selected:
        List selectedEnforcements = report.getEnforcementsAsList();
        assertEquals("There should be one default enforcement returned", 1, selectedEnforcements.size());
        assertEquals("The default enforcement should be allow", "allow", (String)selectedEnforcements.get(0));
        
        // Get the ui-selection labels so we can use them to set:
        List choices = report.getEnforcementChoices();
        Iterator it = choices.iterator();
        List selections = new ArrayList();
        while (it.hasNext()) {
            Object choice = it.next();
            String value = (String) ((SelectItem) choice).getValue();
            if (!value.equals("Both")){
                selections.add(value);
            }
        }

        // Set the ui-selection names:
        report.setEnforcementsAsList(selections);

        // Now get the selected enforcements and make sure they're the right
        // ones:
        selectedEnforcements = report.getEnforcementsAsList();
        assertEquals("Number of enforcements returned should match number set", selections.size(), selectedEnforcements.size());
        Iterator iter = selectedEnforcements.iterator();
        while (iter.hasNext()) {
            String selection = (String) iter.next();
            assertTrue("Selection name must exist in the selections that were set", selections.contains(selection));
        }
    }

    /**
     * This test verifies that the enforcement list is displayed properly
     */
    public void testReportDataObjectEnforcementListDisplay() {
        Report wsReport = new Report();
        EffectList effectList = new EffectList();
        EffectType[] effects = { EffectType.allow, EffectType.deny };
        effectList.setValues(effects);
        wsReport.setEffects(effectList);
        ReportImpl report = new ReportImpl(wsReport);
        String uiEnforcements = report.getEnforcements();
        assertNotNull("There should be some enforcement names displayed", uiEnforcements);
        assertEquals("Enforcement names should be displayed properly", "Allow/Monitor, Deny", uiEnforcements);
    }

    /**
     * Tests the action choices:
     */
    public void testReportDataObjectActionChoices() {
        ResourceBundle bundle = ResourceBundle.getBundle(INQUIRY_CENTER_BUNDLE_NAME);
        assertNotNull("The test should be setup to have this bundle", bundle);
        Report wsReport = new Report();
        ReportImpl report = new ReportImpl(wsReport);
        for (int i = 0; i < 2; i++) {
            List choices = (i == 0 ? report.getTrackingActionChoices() : report.getPolicyActionChoices());
            Iterator it = choices.iterator();
            Set choicesValue = new HashSet();
            String lastDisplayValue = "";
            while (it.hasNext()) {
                final Object choice = it.next();
                assertTrue("Each item should be a select item", choice instanceof SelectItem);
                final SelectItem item = (SelectItem) choice;
                final Object value = item.getValue();
                final String displayValue = item.getLabel();
                assertTrue("Each item should have a String value", value instanceof String);
                assertFalse("Each item value should be unique", choicesValue.contains(value));
                assertTrue("Items should be sorted in ascending order", lastDisplayValue.compareTo(displayValue) < 0);
                choicesValue.add(value);
                lastDisplayValue = displayValue;
            }
        }
    }
    
    /**
     * Tests the actual string of the tracking action choices
     * WARNING: if this breaks, please refer to ActionEnumTypeTest (under common/domain)
     */
    public void testReportDataObjectTrackingActionValues() {
        ResourceBundle bundle = ResourceBundle.getBundle(INQUIRY_CENTER_BUNDLE_NAME);
        assertNotNull("The test should be setup to have this bundle", bundle);
        Report wsReport = new Report();
        ReportImpl report = new ReportImpl(wsReport);
        Iterator choices = report.getTrackingActionChoices().iterator();
        assertEquals("There should be 34 tracking actions", 34, report.getTrackingActionChoices().size());
        assertEquals("The action should be Abnormal Enforcer Shutdown", "Abnormal Enforcer Shutdown", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Ask Question", "Ask Question", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Attach to Item", "Attach to Item", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Change Attributes", "Change Attributes", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Change File Permissions", "Change File Permissions", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Copy", "Copy / Embed File", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Create / Edit", "Create / Edit", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Delete", "Delete", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Email", "Email", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Enforcer Binary File Access", "Enforcer Binary File Access", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Enforcer Configuration File Access", "Enforcer Configuration File Access", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Enforcer Log File Access", "Enforcer Log File Access", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Enforcer Shutdown (normal)", "Enforcer Shutdown (normal)", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Enforcer Startup", "Enforcer Startup", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Export", "Export", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Instant Message", "Instant Message", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Join", "Join", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Meet", "Meet", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Move", "Move", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Open", "Open", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Policy Bundle Authentication Failed", "Policy Bundle Authentication Failed", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Policy Bundle Authentication Succeeded", "Policy Bundle Authentication Succeeded", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Policy Bundle File Access", "Policy Bundle File Access", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Presence", "Presence", ((SelectItem)choices.next()).getLabel());	
        assertEquals("The action should be Print", "Print", ((SelectItem)choices.next()).getLabel());	
        assertEquals("The action should be Record", "Record", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Rename", "Rename", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Run", "Run", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Share", "Share", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be User Login", "User Login", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be User Logout", "User Logout", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Video", "Video", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Voice", "Voice", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Voice Call / Video Call", "Voice Call / Video Call", ((SelectItem)choices.next()).getLabel());
    }
    
    /**
     * Tests the actual string of the policy action choices
     * WARNING: if this breaks, please refer to ActionEnumTypeTest (under common/domain)
     */
    public void testReportDataObjectPolicyActionValues() {
        ResourceBundle bundle = ResourceBundle.getBundle(INQUIRY_CENTER_BUNDLE_NAME);
        assertNotNull("The test should be setup to have this bundle", bundle);
        Report wsReport = new Report();
        ReportImpl report = new ReportImpl(wsReport);
        Iterator choices = report.getPolicyActionChoices().iterator();
        assertEquals("There should be 23 policy actions", 23, report.getPolicyActionChoices().size());
        assertEquals("The action should be Ask Question", "Ask Question", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Attach to Item", "Attach to Item", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Change Attributes", "Change Attributes", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Change File Permissions", "Change File Permissions", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Copy", "Copy / Embed File", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Create / Edit", "Create / Edit", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Delete", "Delete", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Email", "Email", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Export", "Export", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Instant Message", "Instant Message", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Join", "Join", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Meet", "Meet", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Move", "Move", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Open", "Open", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Presence", "Presence", ((SelectItem)choices.next()).getLabel());	
        assertEquals("The action should be Print", "Print", ((SelectItem)choices.next()).getLabel());	
        assertEquals("The action should be Record", "Record", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Rename", "Rename", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Run", "Run", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Share", "Share", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Video", "Video", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Voice", "Voice", ((SelectItem)choices.next()).getLabel());
        assertEquals("The action should be Voice Call / Video Call", "Voice Call / Video Call", ((SelectItem)choices.next()).getLabel());
    }

    /**
     * Tests that the action selections from UI work
     *  
     */
    public void testReportDataObjectActionSelections() {
        ResourceBundle bundle = ResourceBundle.getBundle(INQUIRY_CENTER_BUNDLE_NAME);
        assertNotNull("The test should be setup to have this bundle", bundle);
        Report wsReport = new Report();
        ReportImpl report = new ReportImpl(wsReport);

        // Get the selected enforcements and make sure none are selected:
        List selectedActions = report.getActionsAsList();
        assertTrue("No selected enforcements should be returned", ((selectedActions == null) || (selectedActions.size() == 0)));

        // Get the ui-selection labels so we can use them to set:
        List choices = report.getTrackingActionChoices();
        Iterator it = choices.iterator();
        List selections = new ArrayList();
        while (it.hasNext()) {
            Object choice = it.next();
            String value = (String) ((SelectItem) choice).getValue();
            selections.add(value);
        }

        // Set the ui-selection names:
        report.setActionsAsList(selections);

        // Now get the selected enforcements and make sure they're the right
        // ones:
        selectedActions = report.getActionsAsList();
        assertEquals("Number of actions returned should match number set", selections.size(), selectedActions.size());
        Iterator iter = selectedActions.iterator();
        while (iter.hasNext()) {
            String selection = (String) iter.next();
            assertTrue("Selection name must exist in the selections that were set", selections.contains(selection));
        }
    }

    /**
     * This test verifies that the action list is displayed properly
     */
    public void testReportDataObjectActionListDisplay() {
        Report wsReport = new Report();
        ActionList actionList = new ActionList();
        String[] actions = { ActionEnumType.ACTION_COPY.getName(), ActionEnumType.ACTION_OPEN.getName(), ActionEnumType.ACTION_PRINT.getName() };
        actionList.setActions(actions);
        wsReport.setActions(actionList);
        ReportImpl report = new ReportImpl(wsReport);
        String uiActions = report.getActions();
        assertNotNull("There should be some action names displayed", uiActions);
        assertEquals("Action names should be displayed properly", "Copy / Embed File, Open, Print", uiActions);
    }
    
    /**
     * Tests that the logging level selections from UI work
     *  
     */
    public void testReportDataObjectLoggingLevelSelections() {
        //TODO Noise Reduction Unit Test
        ResourceBundle bundle = ResourceBundle.getBundle(INQUIRY_CENTER_BUNDLE_NAME);
        assertNotNull("The test should be setup to have this bundle", bundle);
        Report wsReport = new Report();
        ReportImpl report = new ReportImpl(wsReport);

        // Get the selected enforcements and make sure none are selected:
        List selectedActions = report.getActionsAsList();
        assertTrue("No selected enforcements should be returned", ((selectedActions == null) || (selectedActions.size() == 0)));

        // Get the ui-selection labels so we can use them to set:
        List choices = report.getTrackingActionChoices();
        Iterator it = choices.iterator();
        List selections = new ArrayList();
        while (it.hasNext()) {
            Object choice = it.next();
            String value = (String) ((SelectItem) choice).getValue();
            selections.add(value);
        }

        // Set the ui-selection names:
        report.setActionsAsList(selections);

        // Now get the selected enforcements and make sure they're the right
        // ones:
        selectedActions = report.getActionsAsList();
        assertEquals("Number of actions returned should match number set", selections.size(), selectedActions.size());
        Iterator iter = selectedActions.iterator();
        while (iter.hasNext()) {
            String selection = (String) iter.next();
            assertTrue("Selection name must exist in the selections that were set", selections.contains(selection));
        }
    }

    /**
     * This test verifies that the logging level is displayed properly
     */
    public void testReportDataObjectLoggingLevelDisplay() {
        //TODO Noise Reduction Unit Test
        Report wsReport = new Report();
        ActionList actionList = new ActionList();
        String[] actions = { ActionEnumType.ACTION_COPY.getName(), ActionEnumType.ACTION_OPEN.getName(), ActionEnumType.ACTION_PRINT.getName() };
        actionList.setActions(actions);
        wsReport.setActions(actionList);
        ReportImpl report = new ReportImpl(wsReport);
        String uiActions = report.getActions();
        assertNotNull("There should be some action names displayed", uiActions);
        assertEquals("Action names should be displayed properly", "Copy / Embed File, Open, Print", uiActions);
    }

    /**
     * This test verifies that the begin and end date are displayed properly.
     */
    public void testReportDataObjectDateDisplay() {
        Calendar now = Calendar.getInstance();
        Calendar lastYear = Calendar.getInstance();
        lastYear.setTimeInMillis(now.getTimeInMillis());
        lastYear.add(Calendar.YEAR, -1);
        Report wsReport = new Report();
        wsReport.setBeginDate(lastYear);
        wsReport.setEndDate(now);
        ReportImpl report = new ReportImpl(wsReport);
        assertEquals("Begin date value should be correct", report.getBeginDate(), lastYear.getTime());
        assertEquals("End date value should be correct", report.getEndDate(), now.getTime());

        //Try the null values
        wsReport.setBeginDate(null);
        wsReport.setEndDate(null);
        report = new ReportImpl(wsReport);
        assertNotNull("Begin date value should NOT be null", report.getBeginDate());
        assertNotNull("End date value should NOT be null", report.getEndDate());
    }

    /**
     * This test verifies that when a date is set, the proper time is applied to
     * the date.
     */
    public void testReportDataObjectDateSettings() {
        Report wsReport = new Report();
        wsReport.setBeginDate(null);
        wsReport.setEndDate(null);
        ReportImpl report = new ReportImpl(wsReport);

        final Date now = new Date();
        //Sets the beginning and the end to the same time
        report.setBeginDate(now);
        report.setEndDate(now);
        assertNotNull("Begin date should not be null", report.getBeginDate());
        assertNotNull("End date should not be null", report.getEndDate());
        long realBeginDateTs = report.getBeginDate().getTime();
        long realEndDateTs = report.getEndDate().getTime();
        long expectedInterval = 3600 * 24 * 1000 - 1;
        assertEquals("The query should span for 24 hours", realBeginDateTs, realEndDateTs - expectedInterval);
        assertTrue("The begin date should be placed before the end date", report.getBeginDate().compareTo(report.getEndDate()) < 0);
        Calendar beginCal = Calendar.getInstance();
        beginCal.setTimeInMillis(realBeginDateTs);
        assertTrue("Begin date should start at the beginning of the day", now.compareTo(report.getBeginDate()) > 0);
        assertEquals("Begin date should start at the beginning of the day", 0, beginCal.get(Calendar.HOUR_OF_DAY));
        assertEquals("Begin date should start at the beginning of the day", 0, beginCal.get(Calendar.MILLISECOND));

        //Tries a gap of one day
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.setTimeInMillis(now.getTime());
        tomorrow.add(Calendar.DATE, 1);
        final Date tomorrowDate = new Date(tomorrow.getTimeInMillis());
        report.setBeginDate(now);
        report.setEndDate(tomorrowDate);
        assertNotNull("Begin date should not be null", report.getBeginDate());
        assertNotNull("End date should not be null", report.getEndDate());
        realBeginDateTs = report.getBeginDate().getTime();
        realEndDateTs = report.getEndDate().getTime();
        expectedInterval = 3600 * 48 * 1000 - 1;
        assertEquals("The query should span for 48 hours", realBeginDateTs, realEndDateTs - expectedInterval);
        assertTrue("The begin date should be placed before the end date", report.getBeginDate().compareTo(report.getEndDate()) < 0);
        beginCal = Calendar.getInstance();
        beginCal.setTimeInMillis(realBeginDateTs);
        assertTrue("Begin date should start at the beginning of the day", now.compareTo(report.getBeginDate()) > 0);
        assertEquals("Begin date should start at the beginning of the day", 0, beginCal.get(Calendar.HOUR_OF_DAY));
        assertEquals("Begin date should start at the beginning of the day", 0, beginCal.get(Calendar.MILLISECOND));
    }

    /**
     * This test verifies that the policy list is displayed properly
     */
    public void testReportDataObjectPolicyListDisplay() {
        Report wsReport = new Report();
        StringList policyList = new StringList();
        final String policy1 = "Policy1";
        final String policy2 = "Policy2";
        final String policy3 = "Policy3";
        String[] names = { policy2, policy1, policy3 };
        policyList.setValues(names);
        wsReport.setPolicies(policyList);

        ReportImpl report = new ReportImpl(wsReport);
        String uiPolicies = report.getPolicies();
        assertNotNull("There should be some policy names displayed", uiPolicies);
        assertEquals("Policy names should be displayed properly", "Policy2, Policy1, Policy3", uiPolicies);
    }

    /**
     * This test verifies that the policy list is displayed properly
     */
    public void testReportDataObjectUserListDisplay() {
        Report wsReport = new Report();
        StringList userList = new StringList();
        final String user1 = "User1";
        final String user2 = "User2";
        final String user3 = "User3";
        String[] names = { user2, user1, user3 };
        userList.setValues(names);
        wsReport.setUsers(userList);
        ReportImpl report = new ReportImpl(wsReport);
        String uiUsers = report.getUsers();
        assertNotNull("There should be some users names displayed", uiUsers);
        assertEquals("User names should be displayed properly", "User2, User1, User3", uiUsers);
    }

    /**
     * This test verifies that the policy list is displayed properly
     */
    public void testReportDataObjectResourceListDisplay() {
        Report wsReport = new Report();
        StringList resList = new StringList();
        final String res1 = "Res1";
        final String res2 = "Res2";
        String[] resNames = { res2, res1 };
        resList.setValues(resNames);
        wsReport.setResourceNames(resList);
        ReportImpl report = new ReportImpl(wsReport);
        String uiResources = report.getResources();
        assertNotNull("There should be some resource names displayed", uiResources);
        assertEquals("Resource names should be displayed properly", "Res2, Res1", uiResources);
    }

    /**
     * Verifies the functionality of the getTitle method
     */
    public void testReportDataObjectGetTitle() {
        ResourceBundle bundle = ResourceBundle.getBundle(INQUIRY_CENTER_BUNDLE_NAME);
        assertNotNull("The test should be setup to have this bundle", bundle);
        ReportImpl report = new ReportImpl();
        assertEquals("Report name should be equals to the assigned resource", bundle.getString(QUICK_REPORT_NAME_BUNDLE_KEY), report.getTitle());
    }

    /**
     * This test verifies that when the user switches between policy and
     * tracking, everything remains fine
     */
    public void testReportDataObjectPolicyTrackingSwich() {
        final Report wsReport = new Report();
        wsReport.setTarget(ReportTargetType.PolicyEvents);
        String[] policies = { "p1", "p2" };
        StringList policyStringList = new StringList(policies);
        wsReport.setPolicies(policyStringList);
        ReportImpl report = new ReportImpl(wsReport);
        String reportPolicies = report.getPolicies();
        assertEquals("Policy list should be returned properly", "p1, p2", reportPolicies);
        report.setTargetData("ActivityJournal");
        reportPolicies = report.getPolicies();
        assertEquals("Policy list should be returned properly", "p1, p2", reportPolicies);
        String[] actions = { ActionEnumType.ACTION_COPY.getName(), ActionEnumType.ACTION_DELETE.getName(), ActionEnumType.ACTION_ACCESS_AGENT_LOGS.getName() };
        wsReport.setActions(new ActionList(actions));
        List policyActionList = new ArrayList();
        policyActionList.add("COPY");
        policyActionList.add("DELETE");
        policyActionList.add("STOP_AGENT");
        report.setActionsAsList(policyActionList);
        String reportTrackingAction = report.getActions();
        assertEquals("Tracking actions should be correct", "Copy / Embed File, Delete, Enforcer Shutdown (normal)", reportTrackingAction);
        report.setTargetData("PolicyEvents");
    }
}
