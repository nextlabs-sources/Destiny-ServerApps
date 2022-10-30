/*
 * Created on May 17, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import com.bluejungle.destiny.interfaces.report.v1.AccessDeniedFault;
import com.bluejungle.destiny.interfaces.report.v1.ComponentLookupIF;
import com.bluejungle.destiny.interfaces.report.v1.ComponentLookupServiceStub;
import com.bluejungle.destiny.interfaces.report.v1.ServiceNotReadyFault;
import com.bluejungle.destiny.types.basic.v1.SortDirection;
import com.bluejungle.destiny.types.hosts.v1.Host;
import com.bluejungle.destiny.types.hosts.v1.HostList;
import com.bluejungle.destiny.types.hosts.v1.HostQueryFieldName;
import com.bluejungle.destiny.types.hosts.v1.HostQuerySpec;
import com.bluejungle.destiny.types.hosts.v1.HostQueryTerm;
import com.bluejungle.destiny.types.hosts.v1.HostQueryTermList;
import com.bluejungle.destiny.types.hosts.v1.HostSortFieldName;
import com.bluejungle.destiny.types.hosts.v1.HostSortTerm;
import com.bluejungle.destiny.types.hosts.v1.HostSortTermList;
import com.bluejungle.destiny.types.policies.v1.Policy;
import com.bluejungle.destiny.types.policies.v1.PolicyList;
import com.bluejungle.destiny.types.policies.v1.PolicyQueryFieldName;
import com.bluejungle.destiny.types.policies.v1.PolicyQuerySpec;
import com.bluejungle.destiny.types.policies.v1.PolicyQueryTerm;
import com.bluejungle.destiny.types.policies.v1.PolicyQueryTermList;
import com.bluejungle.destiny.types.policies.v1.PolicySortFieldName;
import com.bluejungle.destiny.types.policies.v1.PolicySortTerm;
import com.bluejungle.destiny.types.policies.v1.PolicySortTermList;
import com.bluejungle.destiny.types.resources.v1.ResourceClassQueryFieldName;
import com.bluejungle.destiny.types.resources.v1.ResourceClassQuerySpec;
import com.bluejungle.destiny.types.resources.v1.ResourceClassQueryTerm;
import com.bluejungle.destiny.types.resources.v1.ResourceClassQueryTermList;
import com.bluejungle.destiny.types.resources.v1.ResourceClassSortFieldName;
import com.bluejungle.destiny.types.resources.v1.ResourceClassSortTerm;
import com.bluejungle.destiny.types.resources.v1.ResourceClassSortTermList;
import com.bluejungle.destiny.types.users.v1.User;
import com.bluejungle.destiny.types.users.v1.UserClass;
import com.bluejungle.destiny.types.users.v1.UserClassList;
import com.bluejungle.destiny.types.users.v1.UserClassQueryFieldName;
import com.bluejungle.destiny.types.users.v1.UserClassQuerySpec;
import com.bluejungle.destiny.types.users.v1.UserClassQueryTerm;
import com.bluejungle.destiny.types.users.v1.UserClassQueryTermList;
import com.bluejungle.destiny.types.users.v1.UserClassSortFieldName;
import com.bluejungle.destiny.types.users.v1.UserClassSortTerm;
import com.bluejungle.destiny.types.users.v1.UserClassSortTermList;
import com.bluejungle.destiny.types.users.v1.UserList;
import com.bluejungle.destiny.types.users.v1.UserQueryFieldName;
import com.bluejungle.destiny.types.users.v1.UserQuerySpec;
import com.bluejungle.destiny.types.users.v1.UserQueryTerm;
import com.bluejungle.destiny.types.users.v1.UserQueryTermList;
import com.bluejungle.destiny.types.users.v1.UserSortFieldName;
import com.bluejungle.destiny.types.users.v1.UserSortTerm;
import com.bluejungle.destiny.types.users.v1.UserSortTermList;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketBean;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;
import com.bluejungle.destiny.webui.framework.context.AppContext;
import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.types.PositiveInteger;

/**
 * This is a bean-like class that is initialized and setup by the JSF framework -
 * based on the faces-config file. It needs to be providd with the location of
 * the DAC server at init time.
 * 
 * @author safdar
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/ReportComponentQueryBroker.java#1 $
 */
public class ReportComponentQueryBroker {

    private static final String COMPONENT_LOOKUP_SERVICE_LOCATION_SERVLET_PATH = "/services/ComponentLookupService";

    private ComponentLookupServiceStub componentLookupService;

    private String dacLocation;

    /**
     * Constructor
     *  
     */
    public ReportComponentQueryBroker() {
        super();
    }

    /**
     * Returns a list of matching policies
     * 
     * @param freeFormSearchSpec
     *            list search expression
     * @return a list of matching policies
     * @throws ReportComponentLookupException
     *             if the search fails
     */
    public PolicyList getPoliciesForFreeFormSearchSpec(IFreeFormSearchSpec freeFormSearchSpec) throws ReportComponentLookupException {
        PolicyList policies = null;
        try {
            policies = this.getComponentLookupService().getPolicies(getPolicyQuerySpec(freeFormSearchSpec.getFreeFormSeachString(), freeFormSearchSpec.getMaximumResultsToReturn()));
        } catch (ServiceNotReadyFault | RemoteException e) {
            throw new ReportComponentLookupException(e);
        } catch (AccessDeniedFault e) {
            //Session timeout - logout the user
            AppContext.getContext().releaseContext();
            FacesContext.getCurrentInstance().renderResponse();
            throw new ReportComponentLookupException(e);
        }
        return policies;
    }

    /**
     * Returns a list of policy
     * 
     * @param searchBucketSearchSpec
     *            search expression
     * @return a list of matching policies
     * @throws ReportComponentLookupException
     *             if the search fails
     */
    public PolicyList getPoliciesForSearchBucketSearchSpec(ISearchBucketSearchSpec searchBucketSearchSpec) throws ReportComponentLookupException {
        PolicyList policies = null;
        try {
            Character[] charsToSearchFor = searchBucketSearchSpec.getCharactersInBucket();
            if (charsToSearchFor.length == 1 && charsToSearchFor[0] == ISearchBucketBean.NO_VALUE) {
                charsToSearchFor = ALL_OTHER_CHARS;
            }
            
            List allPoliciesList = new ArrayList();
            for (int i = 0; i < charsToSearchFor.length; i++) {
                PolicyList intermediatePolicies = this.getComponentLookupService().getPolicies(getPolicyQuerySpec(charsToSearchFor[i].toString(), searchBucketSearchSpec.getMaximumResultsToReturn()));
                Policy[] intermediatePolicyArray = intermediatePolicies.getPolicies();
                if (intermediatePolicyArray != null) {
                    int size = searchBucketSearchSpec.getMaximumResultsToReturn();
                    if (intermediatePolicyArray.length < size){
                        size = intermediatePolicyArray.length;
                    }
                    for (int j = 0; j < size; j++) {
                        if (allPoliciesList.size() < searchBucketSearchSpec.getMaximumResultsToReturn()){
                            allPoliciesList.add(intermediatePolicyArray[j]);
                        }
                    }
                }
            }
           
            Policy[] allPoliciesArray = new Policy[allPoliciesList.size()];         
            allPoliciesList.toArray(allPoliciesArray);
            policies = new PolicyList();
            policies.setPolicies(allPoliciesArray);
        } catch (ServiceNotReadyFault | RemoteException e) {
            throw new ReportComponentLookupException(e);
        } catch (AccessDeniedFault e) {
            //Session timeout - logout the user
            AppContext.getContext().releaseContext();
            FacesContext.getCurrentInstance().renderResponse();
            throw new ReportComponentLookupException(e);
        }
        return policies;
    }

    /**
     * Returns a policy query specification based on a search expression. Policy
     * sorting is based on policy name.
     * 
     * @param searchExpr
     *            search expression
     * @param maxNbResults
     *            maximum number of results to return
     * @return a policy query specification.
     */
    private PolicyQuerySpec getPolicyQuerySpec(String searchExpr, int maxNbResults) {
        PolicyQueryTerm policyQueryTerm = new PolicyQueryTerm();
        policyQueryTerm.setFieldName(PolicyQueryFieldName.Name);
        policyQueryTerm.setExpression(searchExpr + "*");
        PolicyQueryTermList policyQueryTermList = new PolicyQueryTermList();
        policyQueryTermList.setTerms(new PolicyQueryTerm[] { policyQueryTerm });
        PolicySortTerm policySortTerm = new PolicySortTerm();
        policySortTerm.setFieldName(PolicySortFieldName.Name);
        policySortTerm.setDirection(SortDirection.Ascending);
        PolicySortTermList policySortTermList = new PolicySortTermList();
        policySortTermList.setTerms(new PolicySortTerm[] { policySortTerm });
        final PolicyQuerySpec result = new PolicyQuerySpec();
        result.setSearchSpec(policyQueryTermList);
        result.setSortSpec(policySortTermList);
        result.setLimit(new PositiveInteger((new Integer(maxNbResults)).toString()));
        return result;
    }

    /**
     * Returns a list of resource classes
     * 
     * @param freeFormSearchSpec
     *            search expression
     * @return a list of matching resource classes
     * @throws ReportComponentLookupException
     *             if the search failed.
     */
/* NOTE: disabled per Bug 4286 */
//    public ResourceClassList getResourceClassesForFreeFormSearchSpec(IFreeFormSearchSpec freeFormSearchSpec) throws ReportComponentLookupException {
//        ResourceClassList resourceClasses = null;
//        try {
//            resourceClasses = this.getComponentLookupIF().getResourceClasses(getResourceClassQuerySpec(freeFormSearchSpec.getFreeFormSeachString(), freeFormSearchSpec.getMaximumResultsToReturn()));
//        } catch (ServiceNotReadyFault e) {
//            throw new ReportComponentLookupException(e);
//        } catch (AccessDeniedFault e) {
//            //Session timeout - logout the user
//            AppContext.getContext().releaseContext();
//            FacesContext.getCurrentInstance().renderResponse();
//            throw new ReportComponentLookupException(e);
//        } catch (RemoteException e) {
//            throw new ReportComponentLookupException(e);
//        } catch (ServiceException e) {
//            throw new ReportComponentLookupException(e);
//        }
//        return resourceClasses;
//    }

    /**
     * Returns a list of resource classes
     * 
     * @param searchBucketSearchSpec
     *            search expression
     * @return a list of matching resource classes
     * @throws ReportComponentLookupException
     *             if the search failed.
     */
    /* NOTE: disabled per Bug 4286 */
//    public ResourceClassList getResourceClassesForSearchBucketSearchSpec(ISearchBucketSearchSpec searchBucketSearchSpec) throws ReportComponentLookupException {
//        ResourceClassList resourceClasses = null;
//        try {
//            Character[] charsToSearchFor = searchBucketSearchSpec.getCharactersInBucket();
//            List allResourceClassesList = new ArrayList();
//            for (int i = 0; i < charsToSearchFor.length; i++) {
//                ResourceClassList intermediateResourceClasses = this.getComponentLookupIF().getResourceClasses(getResourceClassQuerySpec(charsToSearchFor[i].toString(), searchBucketSearchSpec.getMaximumResultsToReturn()));
//                ResourceClass[] intermediateResourceClassArray = intermediateResourceClasses.getClasses();
//                if (intermediateResourceClassArray != null) {
//                    for (int j = 0; j < intermediateResourceClassArray.length; j++) {
//                        if (allResourceClassesList.size() < searchBucketSearchSpec.getMaximumResultsToReturn()){
//                            allResourceClassesList.add(intermediateResourceClassArray[j]);
//                        }
//                    }
//                }
//            }
//
//            ResourceClass[] allResourceClassesArray = new ResourceClass[allResourceClassesList.size()];
//            allResourceClassesList.toArray(allResourceClassesArray);
//            resourceClasses = new ResourceClassList(allResourceClassesArray);
//        } catch (ServiceNotReadyFault e) {
//            throw new ReportComponentLookupException(e);
//        } catch (AccessDeniedFault e) {
//            //Session timeout - logout the user
//            AppContext.getContext().releaseContext();
//            FacesContext.getCurrentInstance().renderResponse();
//            throw new ReportComponentLookupException(e);
//        } catch (RemoteException e) {
//            throw new ReportComponentLookupException(e);
//        } catch (ServiceException e) {
//            throw new ReportComponentLookupException(e);
//        }
//        return resourceClasses;
//    }

    /**
     * Returns the query specification for the resource class. Resource classes
     * are sorted by name ascending.
     * 
     * @param searchExpr
     *            search expression
     * @param maxNbResults
     *            maximum number of results to return
     * @return a query specification for resource class, sorted by name
     *         ascending.
     */
    private ResourceClassQuerySpec getResourceClassQuerySpec(String searchExpr, int maxNbResults) {
        ResourceClassQueryTerm resourceClassQueryTerm = new ResourceClassQueryTerm();
        resourceClassQueryTerm.setFieldName(ResourceClassQueryFieldName.Name);
        resourceClassQueryTerm.setExpression(searchExpr + "*");
        ResourceClassSortTerm resourceClassSortTerm = new ResourceClassSortTerm();
        resourceClassSortTerm.setDirection(SortDirection.Ascending);
        resourceClassSortTerm.setFieldName(ResourceClassSortFieldName.Name);
        final ResourceClassQuerySpec result = new ResourceClassQuerySpec();

        ResourceClassQueryTermList resourceClassQueryTermList = new ResourceClassQueryTermList();
        resourceClassQueryTermList.setTerms(new ResourceClassQueryTerm[] { resourceClassQueryTerm });
        result.setSearchSpec(resourceClassQueryTermList);

        ResourceClassSortTermList resourceClassSortTermList = new ResourceClassSortTermList();
        resourceClassSortTermList.setTerms(new ResourceClassSortTerm[] { resourceClassSortTerm });
        result.setSortSpec(resourceClassSortTermList);
        result.setLimit(new PositiveInteger((new Integer(maxNbResults)).toString()));
        return result;
    }

    /**
     * 
     * @param freeFormSearchSpec
     * @return
     * @throws ReportComponentLookupException
     */
    public UserList getUsersForFreeFormSearchSpec(IFreeFormSearchSpec freeFormSearchSpec) throws ReportComponentLookupException {
        UserList users = null;
        try {
            users = this.getComponentLookupService().getUsers(getUserQuerySpec(freeFormSearchSpec.getFreeFormSeachString(), freeFormSearchSpec.getMaximumResultsToReturn()));
        } catch (ServiceNotReadyFault e) {
            throw new ReportComponentLookupException(e);
        } catch (AccessDeniedFault e) {
            //Session timeout - logout the user
            AppContext.getContext().releaseContext();
            FacesContext.getCurrentInstance().renderResponse();
            throw new ReportComponentLookupException(e);
        } catch (RemoteException e) {
            throw new ReportComponentLookupException(e);
        }
        return users;
    }
    
    private static final Character[] ALL_OTHER_CHARS =	new Character[] { 
					'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 
					'<', '>', '.', '[', ']', '{', '}', '(', ')', '&', };

    /**
     * Returns the list of users based on the search bucket query specification.
     * 
     * @param searchBucketSearchSpec
     * @return
     * @throws ReportComponentLookupException
     */
    public UserList getUsersForSearchBucketSearchSpec(ISearchBucketSearchSpec searchBucketSearchSpec)
			throws ReportComponentLookupException {
        UserList users = null;
        try {
            Character[] charsToSearchFor = searchBucketSearchSpec.getCharactersInBucket();
    
            //FIXME bug fix for 7881
            // the other bucket is not perfect since it doesn't cover all chars other than A-Z
            // however, the api doesn't provide CaseInsensitveUnlike search. It will be hard to 
            // cover all the cases on server side
            // The other approach to solve this bug is "All" - "A-Z". However, the dataset maybe 
            // very large, and you don't want to limit the resultset since the data maybe incorrect. 
            // Due to poor performance, this approach is not taken. 
            List<User> allUsersList = new ArrayList<User>();
            if (charsToSearchFor.length == 1 && charsToSearchFor[0] == ISearchBucketBean.NO_VALUE) {
            	charsToSearchFor = ALL_OTHER_CHARS;
            }
            for (int i = 0; i < charsToSearchFor.length; i++) {
                UserList intermediateUsers = this.getComponentLookupService().getUsers(
                		getUserQuerySpec(charsToSearchFor[i].toString(),
								searchBucketSearchSpec.getMaximumResultsToReturn()));
                User[] intermediateUserArray = intermediateUsers.getUsers();
                if (intermediateUserArray != null) {
                    for (int j = 0; j < intermediateUserArray.length; j++) {
                        if (allUsersList.size() < searchBucketSearchSpec.getMaximumResultsToReturn()){
                            allUsersList.add(intermediateUserArray[j]);
                        }
                    }
                }
            }

            User[] allUsersArray = new User[allUsersList.size()];
            allUsersList.toArray(allUsersArray);
            users = new UserList();
            users.setUsers(allUsersArray);
        } catch (ServiceNotReadyFault e) {
            throw new ReportComponentLookupException(e);
        } catch (AccessDeniedFault e) {
            //Session timeout - logout the user
            AppContext.getContext().releaseContext();
            FacesContext.getCurrentInstance().renderResponse();
            throw new ReportComponentLookupException(e);
        } catch (RemoteException e) {
            throw new ReportComponentLookupException(e);
        }
        return users;
    }

    /**
     * Returns the user query specification given a search string
     * 
     * @param searchExpr
     *            search string to use. This search string is applied on the
     *            user last name.
     * @param maxNbResults
     *            maximum number of results
     * @return a user query spec searching the the search expression on the last
     *         name, and sorting by last name and first name ascending.
     */
    private UserQuerySpec getUserQuerySpec(final String searchExpr, int maxNbResults) {
    	final UserQueryTerm userQueryTerm = new UserQueryTerm();
        userQueryTerm.setFieldName(UserQueryFieldName.lastName);
        userQueryTerm.setExpression(searchExpr + "*");
        UserQueryTermList userQueryTermList = new UserQueryTermList();
        userQueryTermList.setTerms(new UserQueryTerm[] { userQueryTerm });
        final UserSortTerm userLastNameSort = new UserSortTerm();
        userLastNameSort.setDirection(SortDirection.Ascending);
        userLastNameSort.setFieldName(UserSortFieldName.lastName);
        final UserSortTerm userFirstNameSort = new UserSortTerm();
        userFirstNameSort.setDirection(SortDirection.Ascending);
        userFirstNameSort.setFieldName(UserSortFieldName.firstName);
        UserSortTermList sortTermList = new UserSortTermList();
        sortTermList.setTerms(new UserSortTerm[] { userLastNameSort, userFirstNameSort });
        final UserQuerySpec result = new UserQuerySpec();
        result.setSearchSpec(userQueryTermList);
        result.setSortSpec(sortTermList);
        result.setLimit(new PositiveInteger((new Integer(maxNbResults)).toString()));
        return result;
    }

    /**
     * Returns the matches user classes
     * 
     * @param freeFormSearchSpec
     *            search specification
     * @return a list of user classes matching the search specification
     * @throws ReportComponentLookupException
     *             if the search failed.
     */
    public UserClassList getUserClassesForFreeFormSearchSpec(IFreeFormSearchSpec freeFormSearchSpec) throws ReportComponentLookupException {
        UserClassList userClasses = null;
        try {
            userClasses = this.getComponentLookupService().getUserClasses(getUserClassQuerySpec(freeFormSearchSpec.getFreeFormSeachString(), freeFormSearchSpec.getMaximumResultsToReturn()));
        } catch (ServiceNotReadyFault e) {
            throw new ReportComponentLookupException(e);
        } catch (AccessDeniedFault e) {
            //Session timeout - logout the user
            AppContext.getContext().releaseContext();
            FacesContext.getCurrentInstance().renderResponse();
            throw new ReportComponentLookupException(e);
        } catch (RemoteException e) {
            throw new ReportComponentLookupException(e);
        }
        return userClasses;
    }

    /**
     * Returns the user class for the search specification
     * 
     * @param searchBucketSearchSpec
     *            search specification
     * @return a list of matching user classes
     * @throws ReportComponentLookupException
     *             if the search failed.
     */
    public UserClassList getUserClassesForSearchBucketSearchSpec(ISearchBucketSearchSpec searchBucketSearchSpec) throws ReportComponentLookupException {
        UserClassList userClasses = null;
        try {
            Character[] charsToSearchFor = searchBucketSearchSpec.getCharactersInBucket();
            if (charsToSearchFor.length == 1 && charsToSearchFor[0] == ISearchBucketBean.NO_VALUE) {
                charsToSearchFor = ALL_OTHER_CHARS;
            }
            List allUserClasssList = new ArrayList();
            for (int i = 0; i < charsToSearchFor.length; i++) {
                UserClassList intermediateUserClasss = this.getComponentLookupService().getUserClasses(getUserClassQuerySpec(charsToSearchFor[i].toString(), searchBucketSearchSpec.getMaximumResultsToReturn()));
                UserClass[] intermediateUserClassArray = intermediateUserClasss.getClasses();
                if (intermediateUserClassArray != null) {
                    for (int j = 0; j < intermediateUserClassArray.length; j++) {
                        if (allUserClasssList.size() < searchBucketSearchSpec.getMaximumResultsToReturn()){
                            allUserClasssList.add(intermediateUserClassArray[j]);
                        }
                    }
                }
            }

            UserClass[] allUserClasssArray = new UserClass[allUserClasssList.size()];
            allUserClasssList.toArray(allUserClasssArray);
            userClasses = new UserClassList();
            userClasses.setClasses(allUserClasssArray);
        } catch (ServiceNotReadyFault e) {
            throw new ReportComponentLookupException(e);
        } catch (AccessDeniedFault e) {
            //Session timeout - logout the user
            AppContext.getContext().releaseContext();
            FacesContext.getCurrentInstance().renderResponse();
            throw new ReportComponentLookupException(e);
        } catch (RemoteException e) {
            throw new ReportComponentLookupException(e);
        }
        return userClasses;
    }

    /**
     * Returns the user class quety specification given a search string
     * 
     * @param searchExpr
     *            search expression to use. The search expression is applied to
     *            the resource class display name.
     * @param maxNbResults
     *            maximum number of results to return
     * @return a user class query spec filtering by display name and sorting on
     *         the display name in ascending order.
     */
    private UserClassQuerySpec getUserClassQuerySpec(final String searchExpr, int maxNbResults) {
        UserClassQueryTerm userClassQueryTerm = new UserClassQueryTerm();
        userClassQueryTerm.setFieldName(UserClassQueryFieldName.displayName);
        userClassQueryTerm.setExpression(searchExpr + "*");
        UserClassQueryTermList userClassQueryTermList = new UserClassQueryTermList();
        userClassQueryTermList.setTerms(new UserClassQueryTerm[] { userClassQueryTerm });
        UserClassSortTerm userClassSortTerm = new UserClassSortTerm();
        userClassSortTerm.setDirection(SortDirection.Ascending);
        userClassSortTerm.setFieldName(UserClassSortFieldName.displayName);

        UserClassSortTermList userClassSortTermList = new UserClassSortTermList();
        userClassSortTermList.setTerms(new UserClassSortTerm[] { userClassSortTerm });

        UserClassQuerySpec userClassQuerySpec = new UserClassQuerySpec();
        userClassQuerySpec.setSearchSpec(userClassQueryTermList);
        userClassQuerySpec.setSortSpec(userClassSortTermList);

        return userClassQuerySpec;
    }

    /**
     * Returns a list of hosts
     * 
     * @param freeFormSearchSpec
     *            search specification
     * @return a list of matching hosts
     * @throws ReportComponentLookupException
     *             if the search failed.
     */
    public HostList getHostsForFreeFormSearchSpec(IFreeFormSearchSpec freeFormSearchSpec) throws ReportComponentLookupException {
        HostList hosts = null;
        try {
            hosts = this.getComponentLookupService().getHosts(getHostQuerySpec(freeFormSearchSpec.getFreeFormSeachString(), freeFormSearchSpec.getMaximumResultsToReturn()));
        } catch (ServiceNotReadyFault e) {
            throw new ReportComponentLookupException(e);
        } catch (AccessDeniedFault e) {
            //Session timeout - logout the user
            AppContext.getContext().releaseContext();
            FacesContext.getCurrentInstance().renderResponse();
            throw new ReportComponentLookupException(e);
        } catch (RemoteException e) {
            throw new ReportComponentLookupException(e);
        }
        return hosts;
    }

    /**
     * Returns a list of hosts
     * 
     * @param searchBucketSearchSpec
     *            search specification
     * @return a list of matchin hosts
     * @throws ReportComponentLookupException
     *             if the search failed.
     */
    public HostList getHostsForSearchBucketSearchSpec(ISearchBucketSearchSpec searchBucketSearchSpec) throws ReportComponentLookupException {
        HostList hosts = null;
        try {
            Character[] charsToSearchFor = searchBucketSearchSpec.getCharactersInBucket();
            if (charsToSearchFor.length == 1 && charsToSearchFor[0] == ISearchBucketBean.NO_VALUE) {
                charsToSearchFor = ALL_OTHER_CHARS;
            }
            List allHostsList = new ArrayList();
            for (int i = 0; i < charsToSearchFor.length; i++) {
                HostList intermediateHosts = this.getComponentLookupService().getHosts(getHostQuerySpec(charsToSearchFor[i].toString(), searchBucketSearchSpec.getMaximumResultsToReturn()));
                Host[] intermediateHostArray = intermediateHosts.getHosts();
                if (intermediateHostArray != null) {
                    for (int j = 0; j < intermediateHostArray.length; j++) {
                        allHostsList.add(intermediateHostArray[j]);
                    }
                }
            }

            Host[] allHostsArray = new Host[allHostsList.size()];
            allHostsList.toArray(allHostsArray);
            hosts = new HostList();
            hosts.setHosts(allHostsArray);
        } catch (ServiceNotReadyFault e) {
            throw new ReportComponentLookupException(e);
        } catch (AccessDeniedFault e) {
            //Session timeout - logout the user
            AppContext.getContext().releaseContext();
            FacesContext.getCurrentInstance().renderResponse();
            throw new ReportComponentLookupException(e);
        } catch (RemoteException e) {
            throw new ReportComponentLookupException(e);
        }
        return hosts;
    }

    /**
     * Returns the host query specification. Hosts are ordered by name
     * ascending.
     * 
     * @param searchExpr
     *            search expression.
     * @param maxNbResults
     *            maximum number of results to return
     * @return the host query specification.
     */
    private HostQuerySpec getHostQuerySpec(String searchExpr, int maxNbResults) {
        HostQueryTerm hostQueryTerm = new HostQueryTerm();
        hostQueryTerm.setFieldName(HostQueryFieldName.name);
        hostQueryTerm.setExpression(searchExpr + "*");
        HostSortTerm hostSortTerm = new HostSortTerm();
        hostSortTerm.setDirection(SortDirection.Ascending);
        hostSortTerm.setFieldName(HostSortFieldName.name);

        HostQueryTermList hostQueryTermList = new HostQueryTermList();
        hostQueryTermList.setTerms(new HostQueryTerm[] { hostQueryTerm });

        HostSortTermList hostSortTermList = new HostSortTermList();
        hostSortTermList.setTerms(new HostSortTerm[] { hostSortTerm });

        HostQuerySpec hostQuerySpec = new HostQuerySpec();
        hostQuerySpec.setSearchSpec(hostQueryTermList);
        hostQuerySpec.setSortSpec(hostSortTermList);
        hostQuerySpec.setLimit(new PositiveInteger(String.valueOf(maxNbResults)));

        return hostQuerySpec;
    }

    /**
     * Set by the faces-config
     * 
     * @param location
     */
    public void setDataLocation(String location) {
        this.dacLocation = location;
    }

    /**
     * Returns the component lookup interface object
     * 
     * @return the component lookup interface object
     * @throws AxisFault
     */
    private ComponentLookupServiceStub getComponentLookupService() throws AxisFault {
        if (this.componentLookupService == null) {
            // Get the Component Lookup service:
            String serviceLocation = this.dacLocation + COMPONENT_LOOKUP_SERVICE_LOCATION_SERVLET_PATH;
            this.componentLookupService = new ComponentLookupServiceStub(serviceLocation);
        }
        return this.componentLookupService;
    }
}
