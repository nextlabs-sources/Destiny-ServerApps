/*
 * Created on Apr 21, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.report;

import com.bluejungle.destiny.interfaces.report.v1.ComponentLookupServiceStub;
import com.bluejungle.destiny.types.basic.v1.SortDirection;
import com.bluejungle.destiny.types.policies.v1.Policy;
import com.bluejungle.destiny.types.policies.v1.PolicyQueryFieldName;
import com.bluejungle.destiny.types.policies.v1.PolicyQuerySpec;
import com.bluejungle.destiny.types.policies.v1.PolicyQueryTerm;
import com.bluejungle.destiny.types.policies.v1.PolicyQueryTermList;
import com.bluejungle.destiny.types.policies.v1.PolicySortFieldName;
import com.bluejungle.destiny.types.policies.v1.PolicySortTerm;
import com.bluejungle.destiny.types.policies.v1.PolicySortTermList;
import com.bluejungle.destiny.types.users.v1.User;
import com.bluejungle.destiny.types.users.v1.UserQueryFieldName;
import com.bluejungle.destiny.types.users.v1.UserQuerySpec;
import com.bluejungle.destiny.types.users.v1.UserQueryTerm;
import com.bluejungle.destiny.types.users.v1.UserQueryTermList;
import com.bluejungle.destiny.types.users.v1.UserSortFieldName;
import com.bluejungle.destiny.types.users.v1.UserSortTerm;
import com.bluejungle.destiny.types.users.v1.UserSortTermList;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.configuration.DestinyRepository;
import com.bluejungle.framework.datastore.hibernate.HibernateUtils;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;
import com.nextlabs.destiny.inquirycenter.report.lookup.dao.DataLookUpDao;
import com.nextlabs.destiny.inquirycenter.report.lookup.dao.DataLookUpDaoImpl;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.types.PositiveInteger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * <p>
 * ResourceLookupService
 * </p>
 * 
 * 
 * @author Amila Silva
 * 
 */
public class ResourceLookupService {

	private static final String COMPONENT_LOOKUP_SERVICE_LOCATION_SERVLET_PATH = "/services/ComponentLookupService";

	private ComponentLookupServiceStub componentLookupService;

	private DataLookUpDao lookUpDao = new DataLookUpDaoImpl();

	private String dacLocation;
	private HttpServletRequest request;

	static final Log LOG = LogFactory.getLog(ResourceLookupService.class.getName());

	IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
	/**
	 * <p>
	 * lookup the available Policies according to given criteria.
	 * </p>
	 * 
	 * @param searchText
	 *            search key
	 * @param maxNoResults
	 *            no of maximum results expected
	 * @return List of users
	 * @throws ReportLookupException
	 *             throws in any error
	 */

	public List<Policy> getPoliciesSearch(String searchText, int maxNoResults) throws ReportLookupException {
		List<Policy> policies = new ArrayList<Policy>();

		try {

			searchText = (searchText == null || searchText.isEmpty()) ? "" : searchText;
			maxNoResults = (maxNoResults > -1) ? maxNoResults : Integer.MAX_VALUE;

			policies = getDataLookUpDao().lookUpPolicies();

			/*
			 * PolicyList policyList = this.getComponentLookupIF().getPolicies(
			 * getPolicyQuerySpec(searchText, maxNoResults)); if
			 * (policyList.getPolicies() != null) { policies =
			 * Arrays.asList(policyList.getPolicies()); } else { LOG.info(
			 * "No policies found for given criteria"); }
			 */

		} /*
			 * catch (ServiceNotReadyFault e) { LOG.error("Service Not Ready",
			 * e); throw new ReportLookupException(e);
			 * 
			 * } catch (AccessDeniedFault e) { // Session timeout - logout the
			 * user if (request != null)
			 * AppContext.getContext(request).releaseContext(); LOG.error(
			 * "Access Denied ", e); throw new ReportLookupException(e);
			 * 
			 * }
			 */catch (Exception e) {
			LOG.error("Error encountered in policy search", e);
			throw new ReportLookupException(e);
		}
		return policies;
	}

	public List getUsersPaginated(int offset, int pageLimit, String searchName, String order) {
		List users = null;
		Session session = null;
		try {
			String selectClause = "User.displayName, User.firstName, User.lastName";
			session = ((IHibernateRepository) ComponentManagerFactory.getComponentManager()
					.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName())).getSession();
			users = getUserQuery(session, selectClause, searchName, offset, pageLimit, order).list();
		} catch (HibernateException e) {
			LOG.debug("Error while user lookup\n{}", e);
		} finally {
			HibernateUtils.closeSession(session, LOG);
		}
		return users;
	}

	public int getUserRecordCount(String searchName){
		Integer count = null;
		Session session = null;
		try {
			String selectClause = "count(User.displayName)";
			session = ((IHibernateRepository) ComponentManagerFactory.getComponentManager()
					.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName())).getSession();

			count = (Integer) getUserQuery(session, selectClause, searchName, 0, -1, null).list().get(0);
		} catch (HibernateException e) {
			LOG.debug("Error while user lookup\n{}", e);
		} finally {
			HibernateUtils.closeSession(session, LOG);
		}
		return count;
	}

	private Query getUserQuery(Session session, String selectClause, String searchName, int offset, int pageLimit, String order)
			throws HibernateException {

		StringBuilder queryString = new StringBuilder();
		queryString.append("select ");
		queryString.append(selectClause);
		queryString.append(" from UserDO User" +
				" where User.originalId <> :unknownId and User.timeRelation.activeFrom <= :activeFrom" +
				" and User.timeRelation.activeTo > :activeTo");
		if (StringUtils.isNotBlank(searchName)) {
			queryString.append(" and lower(User.displayName) like :searchName");
		}
		if (StringUtils.isNotBlank(order)) {
			queryString.append(" order by User.displayName " + order);
		}
		Query query = session.createQuery(queryString.toString());

		long asOfTime = Calendar.getInstance().getTimeInMillis();
		query.setLong("unknownId", -1L);
		query.setLong("activeFrom", asOfTime);
		query.setLong("activeTo", asOfTime);
		if (StringUtils.isNotBlank(searchName)) {
			query.setString("searchName", "%" + searchName.toLowerCase() + "%");
		}
		if (pageLimit > 0) {
			query.setMaxResults(pageLimit);
			query.setFirstResult(offset);
		}
		return query;
	}

	public List getPoliciesPaginated(int offset, int pageLimit, String searchName, String order) {
		List policies = null;
		Session session = null;
		try {
			String selectClause = "Policy.name, Policy.fullName";
			session = ((IHibernateRepository) ComponentManagerFactory.getComponentManager()
					.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName())).getSession();
			policies = getPolicyQuery(session, selectClause, searchName, offset, pageLimit, order).list();
		} catch (HibernateException e) {
			LOG.debug("Error while user lookup\n{}", e);
		} finally {
			HibernateUtils.closeSession(session, LOG);
		}
		return policies;
	}

	public int getPolicyRecordCount(String searchName){
		Integer count = null;
		Session session = null;
		try {
			String selectClause = "count(Policy.name)";
			session = ((IHibernateRepository) ComponentManagerFactory.getComponentManager()
					.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName())).getSession();

			count = (Integer) getPolicyQuery(session, selectClause, searchName, 0, -1, null).list().get(0);
		} catch (HibernateException e) {
			LOG.debug("Error while user lookup\n{}", e);
		} finally {
			HibernateUtils.closeSession(session, LOG);
		}
		return count;
	}

	private Query getPolicyQuery(Session session, String selectClause, String searchName, int offset, int pageLimit, String order)
			throws HibernateException {
		StringBuilder queryString = new StringBuilder();
		queryString.append("select ");
		queryString.append(selectClause);
		queryString.append(" from PolicyDO Policy");
		if (StringUtils.isNotBlank(searchName)) {
			queryString.append(" where lower(Policy.name) like :searchName");
		}
		if (StringUtils.isNotBlank(order)) {
			queryString.append(" order by Policy.name " + order);
		}
		Query query = session.createQuery(queryString.toString());
		if (StringUtils.isNotBlank(searchName)) {
			query.setString("searchName", "%" + searchName.toLowerCase() + "%");
		}
		if (pageLimit > 0) {
			query.setMaxResults(pageLimit);
			query.setFirstResult(offset);
		}
		return query;
	}

	/**
	 *
	 * <p>
	 * lookup the available Users according to given criteria.
	 * </p>
	 *
	 * @param searchText
	 *            search key
	 * @param maxNoResults
	 *            no of maximum results expected
	 * @return List of users
	 * @throws ReportLookupException
	 *             throws in any error
	 */

	public List<User> getUsersSearch(String searchText, int maxNoResults) throws ReportLookupException {
		List<User> users = new ArrayList<User>();

		try {
			searchText = (searchText == null || searchText.isEmpty()) ? "" : searchText;
			maxNoResults = (maxNoResults > -1) ? maxNoResults : Integer.MAX_VALUE;

			users = getDataLookUpDao().lookUpUsers();

			/*
			 * UserList userList = this.getComponentLookupIF().getUsers(
			 * getUserQuerySpec(searchText, maxNoResults));
			 * 
			 * if (userList.getUsers() != null) { users =
			 * Arrays.asList(userList.getUsers()); } else { LOG.info(
			 * "No users found for given criteria"); }
			 */

			/*
			 * } catch (ServiceNotReadyFault e) { LOG.error("Service Not Ready",
			 * e); throw new ReportLookupException(e);
			 * 
			 * } catch (AccessDeniedFault e) { // Session timeout - logout the
			 * user if (request != null)
			 * AppContext.getContext(request).releaseContext(); //
			 * FacesContext.getCurrentInstance().renderResponse(); LOG.error(
			 * "Access Denied ", e); throw new ReportLookupException(e);
			 */

		} catch (Exception e) {
			LOG.error("Error encountered in user search", e);
			throw new ReportLookupException(e);
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
	 * Set by the faces-config
	 * 
	 * @param location
	 */
	public void setDataLocation(String location) {
		this.dacLocation = location;
	}

	public String getDacLocation() {
		if (dacLocation == null) {
			dacLocation = "DACLocation";
		}

		return dacLocation;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * Returns the component lookup interface object
	 * 
	 * @return the component lookup interface object
	 * @throws AxisFault
	 */
	private ComponentLookupServiceStub getComponentLookup() throws AxisFault {
		if (this.componentLookupService == null) {
			 // Get the Component Lookup service:
			 String serviceLocation = getDacLocation() + COMPONENT_LOOKUP_SERVICE_LOCATION_SERVLET_PATH;
			 this.componentLookupService = new ComponentLookupServiceStub(serviceLocation);
		}
		return this.componentLookupService;
	}

	private DataLookUpDao getDataLookUpDao() {
		if (lookUpDao == null) {
			lookUpDao = new DataLookUpDaoImpl();
		}
		return lookUpDao;
	}

}
