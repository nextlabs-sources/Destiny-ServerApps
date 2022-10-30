/*
 * Created on May 23, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers;

import java.util.HashMap;
import java.util.Map;

import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.UserComponentEntityResolver;
import com.bluejungle.destiny.types.actions.v1.ActionList;
import com.bluejungle.destiny.types.hosts.v1.HostList;
import com.bluejungle.destiny.types.policies.v1.Policy;
import com.bluejungle.destiny.types.policies.v1.PolicyList;
import com.bluejungle.destiny.types.resources.v1.ResourceClassList;
import com.bluejungle.destiny.types.users.v1.User;
import com.bluejungle.destiny.types.users.v1.UserClass;
import com.bluejungle.destiny.types.users.v1.UserClassList;
import com.bluejungle.destiny.types.users.v1.UserList;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;

/**
 * @author safdar
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/MockReportComponentQueryBroker.java#1 $
 */

public class MockReportComponentQueryBroker extends ReportComponentQueryBroker {

    public static final String TEST_FOLDER_NAME = "/Folder1";
    public static final String TEST_POLICY_NAME = "TestPolicy1";
    public static final String TEST_FULL_POLICY_NAME = TEST_FOLDER_NAME + "/" + TEST_POLICY_NAME;
    public static final String TEST_USER_NAME = "First1Last1@test.com";
    public static final String TEST_USER_CLASS_NAME = "UserClass1";

    /*
     * Mock Policies
     */
    private static final PolicyList POLICIES_FOR_TEST;
    static {
        POLICIES_FOR_TEST = new PolicyList(new Policy[] { new Policy(TEST_POLICY_NAME, TEST_FOLDER_NAME), new Policy("TestPolicy2", TEST_FOLDER_NAME), new Policy("TestPolicy3", TEST_FOLDER_NAME), new Policy("TestPolicy4", TEST_FOLDER_NAME) });
    }

    /*
     * Mock Users
     */
    private static final UserList USERS_FOR_TEST;
    static {
        USERS_FOR_TEST = new UserList(new User[] { new User("First1", "Last1", "First1Last1@test.com"), new User("First2", "Last2", "First2Last2@test.com"), new User("First3", "Last3", "First3Last3@test.com"),
                new User("First4", "Last4", "First4Last4@test.com"), new User("First5", "Last5", "First5Last5@test.com") });
    }

    /*
     * Mock User Classes
     */
    private static final UserClassList USER_CLASSES_FOR_TEST;
    static {
        USER_CLASSES_FOR_TEST = new UserClassList(new UserClass[] { new UserClass("UserClassDisplay1", "UserClass1", "EnrollmentType1"), new UserClass("UserClassDisplay2", "UserClass2", "EnrollmentType2"), new UserClass("UserClassDisplay3", "UserClass3", "EnrollmentType3"),
                new UserClass("UserClassDisplay4", "UserClass4", "EnrollmentType4") });
    }

    /**
     * For JUNit testing
     * 
     * @return the number of policy components in this mock broker
     */
    public int getPolicyComponentCount() {
        return POLICIES_FOR_TEST.getPolicies().length;
    }

    /**
     * For JUnit testing
     * 
     * @return a map
     */
    public Map getPolicyComponentMap() {
        Map policyMap = new HashMap();
        Policy[] policies = POLICIES_FOR_TEST.getPolicies();
        for (int i = 0; i < policies.length; i++) {
            Policy policy = policies[i];
            String id = policy.getFolderName() + "/" + policy.getName();
            policyMap.put(id, policy);
        }
        return policyMap;
    }

    /**
     * For JUNit testing
     * 
     * @return the number of resource components in this mock broker
     */
    public int getResourceComponentCount() {
        return 0;
    }

    /**
     * For JUNit testing
     * 
     * @return the number of resource class components in this mock broker
     */
    public int getResourceClassComponentCount() {
        return 0;
    }

    /**
     * For JUNit testing
     * 
     * @return the number of user components in this mock broker
     */
    public int getUserComponentCount() {
        return USERS_FOR_TEST.getUsers().length;
    }

    /**
     * For JUnit testing
     * 
     * @return a map
     */
    public Map getUserMap() {
        Map userMap = new HashMap();
        User[] users = USERS_FOR_TEST.getUsers();
        for (int i = 0; i < users.length; i++) {
            User user = users[i];
            userMap.put(UserComponentEntityResolver.createUserQualification(user.getDisplayName()), user);
        }
        return userMap;
    }

    /**
     * For JUNit testing
     * 
     * @return the number of user class components in this mock broker
     */
    public int getUserClassComponentCount() {
        return USER_CLASSES_FOR_TEST.getClasses().length;
    }

    /**
     * For JUnit testing
     * 
     * @return a map
     */
    public Map getUserClassMap() {
        Map userClassMap = new HashMap();
        UserClass[] userClasses = USER_CLASSES_FOR_TEST.getClasses();
        for (int i = 0; i < userClasses.length; i++) {
            UserClass userClass = userClasses[i];
            userClassMap.put(UserComponentEntityResolver.createUserClassQualification(userClass.getName()), userClass);
        }
        return userClassMap;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentQueryBroker#getActionsForFreeFormSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec)
     */
    public ActionList getActionsForFreeFormSearchSpec(IFreeFormSearchSpec freeFormSearchSpec) throws ReportComponentLookupException {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentQueryBroker#getActionsForSearchBucketSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec)
     */
    public ActionList getActionsForSearchBucketSearchSpec(ISearchBucketSearchSpec searchBucketSearchSpec) throws ReportComponentLookupException {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentQueryBroker#getHostsForFreeFormSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec)
     */
    public HostList getHostsForFreeFormSearchSpec(IFreeFormSearchSpec freeFormSearchSpec) throws ReportComponentLookupException {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentQueryBroker#getHostsForSearchBucketSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec)
     */
    public HostList getHostsForSearchBucketSearchSpec(ISearchBucketSearchSpec searchBucketSearchSpec) throws ReportComponentLookupException {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentQueryBroker#getPoliciesForFreeFormSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec)
     */
    public PolicyList getPoliciesForFreeFormSearchSpec(IFreeFormSearchSpec freeFormSearchSpec) throws ReportComponentLookupException {
        return POLICIES_FOR_TEST;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentQueryBroker#getPoliciesForSearchBucketSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec)
     */
    public PolicyList getPoliciesForSearchBucketSearchSpec(ISearchBucketSearchSpec searchBucketSearchSpec) throws ReportComponentLookupException {
        return POLICIES_FOR_TEST;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentQueryBroker#getResourceClassesForFreeFormSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec)
     */
    public ResourceClassList getResourceClassesForFreeFormSearchSpec(IFreeFormSearchSpec freeFormSearchSpec) throws ReportComponentLookupException {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentQueryBroker#getResourceClassesForSearchBucketSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec)
     */
    public ResourceClassList getResourceClassesForSearchBucketSearchSpec(ISearchBucketSearchSpec searchBucketSearchSpec) throws ReportComponentLookupException {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentQueryBroker#getUserClassesForFreeFormSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec)
     */
    public UserClassList getUserClassesForFreeFormSearchSpec(IFreeFormSearchSpec freeFormSearchSpec) throws ReportComponentLookupException {
        return USER_CLASSES_FOR_TEST;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentQueryBroker#getUserClassesForSearchBucketSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec)
     */
    public UserClassList getUserClassesForSearchBucketSearchSpec(ISearchBucketSearchSpec searchBucketSearchSpec) throws ReportComponentLookupException {
        return USER_CLASSES_FOR_TEST;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentQueryBroker#getUsersForFreeFormSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec)
     */
    public UserList getUsersForFreeFormSearchSpec(IFreeFormSearchSpec freeFormSearchSpec) throws ReportComponentLookupException {
        return USERS_FOR_TEST;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentQueryBroker#getUsersForSearchBucketSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec)
     */
    public UserList getUsersForSearchBucketSearchSpec(ISearchBucketSearchSpec searchBucketSearchSpec) throws ReportComponentLookupException {
        return USERS_FOR_TEST;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentQueryBroker#setDataLocation(java.lang.String)
     */
    public void setDataLocation(String location) {
        super.setDataLocation(location);
    }
}
