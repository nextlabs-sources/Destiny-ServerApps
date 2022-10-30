package com.nextlabs.destiny.inquirycenter.web.filter;

import java.io.IOException;
import java.security.Principal;
import java.sql.*;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bluejungle.framework.expressions.Multivalue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import net.sf.hibernate.HibernateException;
import org.json.JSONException;
import org.json.JSONObject;

import com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoggedInUser;
import com.bluejungle.destiny.container.shared.applicationusers.core.IApplicationUserManager;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.DBUtil;
import com.bluejungle.destiny.webui.framework.context.AppContext;
import com.bluejungle.framework.crypt.ReversibleEncryptor;
import com.bluejungle.framework.datastore.hibernate.HibernateUtils;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;
import com.bluejungle.framework.expressions.EvalValue;
import com.bluejungle.framework.utils.DynamicAttributes;
import com.bluejungle.pf.destiny.parser.PQLException;
import com.bluejungle.pf.domain.destiny.action.DAction;
import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.bluejungle.pf.domain.destiny.subject.SubjectType;
import com.bluejungle.pf.engine.destiny.EvaluationEngine;
import com.bluejungle.pf.engine.destiny.EvaluationRequest;
import com.bluejungle.pf.engine.destiny.EvaluationResult;
import com.bluejungle.pf.engine.destiny.PolicyEvaluationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextlabs.destiny.inquirycenter.JsonUtil;
import com.nextlabs.destiny.inquirycenter.authentication.Authority;
import com.nextlabs.destiny.inquirycenter.authentication.AuthorityFactory;
import com.nextlabs.destiny.inquirycenter.web.delegadmin.helper.AppUserSubject;
import com.nextlabs.destiny.inquirycenter.web.delegadmin.helper.DelegationRuleReferenceResolver;
import com.nextlabs.destiny.inquirycenter.web.delegadmin.helper.DelegationTargetResolver;
import com.nextlabs.destiny.inquirycenter.web.security.CsrfTokenBindingFilter;
import com.nextlabs.framework.ssl.ConfigurableSSLSocketFactory;
import com.nextlabs.destiny.inquirycenter.exception.ReportingException;
import com.nextlabs.destiny.inquirycenter.savedreport.dao.EntityAuditLogDAO;
import com.nextlabs.destiny.inquirycenter.savedreport.dao.EntityAuditLogDAOImpl;
import com.nextlabs.destiny.inquirycenter.savedreport.dao.SavedReportDAO;
import com.nextlabs.destiny.inquirycenter.savedreport.dao.SavedReportDAOImpl;
import com.nextlabs.destiny.inquirycenter.savedreport.dto.EntityAuditLogDO;
import com.bluejungle.destiny.inquirycenter.enumeration.*;
import com.fasterxml.jackson.core.JsonProcessingException;

import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;

/**
 * 
 * Filter to populate the application logged in user details
 * 
 * @author Amila Silva
 * @since 8.0
 */
public class ApplicationUserDetailsFilter implements Filter {

	private static Log log = LogFactory.getLog(ApplicationUserDetailsFilter.class);
	public final static String UN_AUTH_ACCESS_REDIRECT = "unAuthAccessRedirectUrl";
	public final static String APP_AUTHS_ATTR = "app_auths";

	private String appHomeRedirectUrl = "/#/home";
	private String unAuthAccessRedirectUrl = "./logout";

	private static final String REPORT_ADMINISTRATOR = "Report Administrator";
	private static final String BUSINESS_ANALYST = "Business Analyst";
	private static final String SYSTEM_ADMINISTRATOR = "System Administrator";
	private static final String POLICY_ADMINISTRATOR = "Policy Administrator";
	private static final String POLICY_ANALYST = "Policy Analyst";
	private static final String LOGIN_AUDITED = "login.audited";

	private static final String INSTALL_MODE_LEGACY = "OPL";
	public static final String MANAGE_REPORTER_ACTION = "MANAGE_REPORTER";
	private static final String VIEW_REPORTER_ACTION = "VIEW_REPORTER";
	private String installMode;
	
	private volatile EntityAuditLogDAO entityAuditLogDAO;

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.unAuthAccessRedirectUrl = config.getInitParameter(UN_AUTH_ACCESS_REDIRECT);
		if (this.unAuthAccessRedirectUrl == null) {
			this.unAuthAccessRedirectUrl = config.getServletContext().getInitParameter(UN_AUTH_ACCESS_REDIRECT);
		}
		this.installMode = System.getProperty("console.install.mode", "OPL");
	}

	@Override
	public void destroy() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) req;

		if (httpReq.getRequestURI().endsWith(".jsf") || httpReq.getRequestURI().endsWith("/")) {

			AppContext ctx = AppContext.getContext(httpReq);

			if (httpReq.getRemoteUser() != null && !ctx.isLoggedIn()) {
				Principal principal = httpReq.getUserPrincipal();
				String username = principal.getName();
				populateAuths(ctx, username, httpReq);
			}
		}

		// check user has permission to access the reporter
		boolean hasAccess = false;
		if (httpReq.getRemoteUser().equalsIgnoreCase(IApplicationUserManager.SUPER_USER_USERNAME)
				|| isAdminCategory(httpReq)) {
			hasAccess = true;
		} else {
			Set<String> auths = (Set<String>) AppContext.getContext(httpReq).getAttribute(APP_AUTHS_ATTR);
			if (auths == null) {
				// re-load the auths if not loaded
				AppContext ctx = AppContext.getContext(httpReq);
				if (httpReq.getRemoteUser() != null) {
					Principal principal = httpReq.getUserPrincipal();
					String username = principal.getName();
					populateAuths(ctx, username, httpReq);
				} else {
					HttpServletResponse httpRes = (HttpServletResponse) res;
					log.warn("User doesn't have access permission to reporter: [ User : " + httpReq.getRemoteUser() + "]");
					httpRes.sendRedirect(this.unAuthAccessRedirectUrl);
				}
				auths = (Set<String>) AppContext.getContext(httpReq).getAttribute(APP_AUTHS_ATTR);
			}

			if (auths != null) {
				for (String auth : auths) {
					if (auth.endsWith("_REPORTER") || auth.endsWith("_MONITOR")) {
						hasAccess = true;
						break;
					}
				}
			}
		}

		if (hasAccess) {
			if (httpReq.getSession().getAttribute(CsrfTokenBindingFilter.CSRF_TOKEN_ATTR) == null) {
				httpReq.getSession().setAttribute(CsrfTokenBindingFilter.CSRF_TOKEN_ATTR, UUID.randomUUID().toString());
			}
			Principal principal = httpReq.getUserPrincipal();
			String username = principal.getName();
			AppContext ctx = AppContext.getContext(httpReq);
			if(ctx.getAttribute(LOGIN_AUDITED) == null) {
				auditLogin(username, ctx); 
			}
			chain.doFilter(req, res);

		} else {
			HttpServletResponse httpRes = (HttpServletResponse) res;
			log.warn("User doesn't have access permission to reporter: [ User : " + httpReq.getRemoteUser() + "]");
			httpRes.sendRedirect(this.appHomeRedirectUrl);
		}
	}

	private void populateAuths(AppContext ctx, String username, HttpServletRequest httpReq) {
		ILoggedInUser loggedInUser = getAuthenticatedUser(username, ctx);
		log.debug("loggedInUser principal name = " + loggedInUser.getPrincipalName() + " and username = "
				+ loggedInUser.getUsername());
		ctx.setRemoteUser(loggedInUser);
		Set<String> auths = populateAllowedActions(loggedInUser, httpReq);
		ctx.setAttribute(APP_AUTHS_ATTR, auths);
	}

	public Set<String> populateAllowedActions(ILoggedInUser loggedInUser, HttpServletRequest httpReq) {
		Set<String> allowedActions = new TreeSet<String>();
		try {
			log.debug("Start evaluating delegation rules to get access controls for the user, [User :{"
					+ loggedInUser.getPrincipalName() + "}]");

			Set<String> allActions = loadAllActions();

			// grant all the permission to super user
			if (loggedInUser.getPrincipalName().equalsIgnoreCase(IApplicationUserManager.SUPER_USER_USERNAME)
					|| isAdminCategory(httpReq)) {
				return allActions;
			} else {
				if (INSTALL_MODE_LEGACY.equals(installMode)) {
					allowedActions = getReporterUsersAuths(loggedInUser.getPrincipalName());
				} else {
					getReporterUserAuthUsingDAPolicies(loggedInUser, allowedActions, allActions, httpReq);
				}
			}
		} catch (Exception e) {
			log.error("Error encountered in delegation rule evaluation for access control,", e);
		}
		return allowedActions;
	}

	private void getReporterUserAuthUsingDAPolicies(ILoggedInUser loggedInUser, Set<String> allowedActions,
			Set<String> allActions, HttpServletRequest httpReq) throws PQLException {
		List<IDPolicy> parsedRules = resolveRules();
		DelegationTargetResolver resolver = new DelegationTargetResolver(parsedRules);

		// Subject with attributes
		AppUserSubject subject = getSubjectWithAttributes(loggedInUser, httpReq);

		EvaluationEngine engine = null;

		for (String actionName : allActions) {
			EvaluationRequest evalRequest = new EvaluationRequest();
			evalRequest.setRequestId(System.nanoTime());
			evalRequest.setAction(DAction.getAction(actionName));
			evalRequest.setUser(subject);

			engine = new EvaluationEngine(resolver);
			evaluateRule(allowedActions, resolver, engine, actionName, evalRequest);
		}
	}

	private Set<String> loadAllActions() {
		Set<String> allActions = new TreeSet<String>();
		allActions.add(MANAGE_REPORTER_ACTION);
		allActions.add(VIEW_REPORTER_ACTION);
		return allActions;
	}

	private AppUserSubject getSubjectWithAttributes(ILoggedInUser loggedInUser, HttpServletRequest httpReq) {
		AppUserSubject subject = new AppUserSubject(loggedInUser.getUsername(), loggedInUser.getPrincipalName(),
				loggedInUser.getUsername(), loggedInUser.getPrincipalId(), SubjectType.USER, new DynamicAttributes());
		Map<String, String> propsMap = fetchAppUserProperties(loggedInUser.getPrincipalId());
		Set<String> groups = new HashSet<>();
		
		if (propsMap.get("userType").equals("imported")) {
			// take the properties from AD
			Long authHandlerId = Long.valueOf(propsMap.get("authHandlerId"));
			String username = propsMap.get("username");
			propsMap.putAll(fetchExternalUserProperties(authHandlerId, username));
			groups.addAll(fetchExternalUserGroups(authHandlerId, username));
		}
		
		for (Map.Entry<String, String> prop : propsMap.entrySet()) {
            if(prop.getKey() != null) {
                subject.setAttribute(prop.getKey().toLowerCase(), EvalValue.build(prop.getValue()));
            }
		}

		if(groups.size() > 0) {
			subject.setAttribute("groups", EvalValue.build(Multivalue.create(groups)));
		}

		return subject;
	}

	private List<IDPolicy> resolveRules() throws PQLException {
		List<String> policies = findEntitiesByType("DP");
		List<String> components = findEntitiesByType("DC");

		DelegationRuleReferenceResolver resolver = DelegationRuleReferenceResolver.create(policies, components);
		List<IDPolicy> parsedPolicies = resolver.resolve();
		return parsedPolicies;
	}

	private void evaluateRule(Set<String> allowedActions, DelegationTargetResolver resolver, EvaluationEngine engine,
			String actionName, EvaluationRequest evalRequest) {
        try {
            EvaluationResult evalResult = engine.evaluate(evalRequest);
            if (EvaluationResult.ALLOW.equals(evalResult.getEffectName())) {
                allowedActions.add(actionName);
            }
        } catch (PolicyEvaluationException e) {
            // Nothing to do. Just ignore policy
        }
	}

	private Map<String, String> fetchAppUserProperties(final Long userId) {
		Session session = null;
		Transaction t = null;
		String query = "";
		Map<String, String> propsMap = new HashMap<String, String>();
		try {
			query = "SELECT u.ID, u.USERNAME, u.FIRST_NAME, u.LAST_NAME, u.EMAIL, u.USER_TYPE, u.AUTH_HANDLER_ID, p.PROP_KEY, p.PROP_VALUE "
					+ "FROM APPLICATION_USER u left outer join APP_USER_PROPERTIES p on u.ID = p.USER_ID "
					+ "WHERE u.ID = ?";

			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			session = dataSource.getSession();
			t = session.beginTransaction();
			Connection conn = session.connection();
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setLong(1, userId);

			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {	
				propsMap.put("userType", rs.getString("USER_TYPE").trim());
				propsMap.put("authHandlerId", rs.getString("AUTH_HANDLER_ID"));
				propsMap.put("username", rs.getString("USERNAME").trim());
				propsMap.put("firstname", rs.getString("FIRST_NAME").trim());
				
				final String lastName = rs.getString("LAST_NAME");
				if (lastName != null){
					propsMap.put("lastname", lastName.trim());
				}
				final String email = rs.getString("EMAIL");
				if (email != null){
					propsMap.put("email", email.trim());
				}
								
				String key = rs.getString("PROP_KEY");
				final String value = rs.getString("PROP_VALUE");
				if (key != null){
					key = key.trim();
					propsMap.put(key, value);
				}
				
			}

			rs.close();
			stmt.close();
			t.commit();
		} catch (Exception e) {
			log.error("Error occurred during fetch application user properties", e);
		} finally {
			HibernateUtils.closeSession(session, log);
		}
		return propsMap;
	}

	private Set<String> getReporterUsersAuths(String username) {
		Set<String> allowedActions = new TreeSet<String>();

		Session session = null;
		Transaction t = null;
		String query = "";

		try {
			query = "SELECT name, pql FROM development_entities WHERE name in (?,?,?,?,?) "
					+ "AND type = ? AND hidden = ? AND appql is not null";

			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			session = dataSource.getSession();
			t = session.beginTransaction();
			Connection conn = session.connection();
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, REPORT_ADMINISTRATOR);
			stmt.setString(2, BUSINESS_ANALYST);
			stmt.setString(3, SYSTEM_ADMINISTRATOR);
			stmt.setString(4, POLICY_ADMINISTRATOR);
			stmt.setString(5, POLICY_ANALYST);
			stmt.setString(6, "CO");
			stmt.setString(7, "Y");

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String role = rs.getString("name").trim();
				String rolePql = rs.getString("pql");

				boolean isAdmin = (SYSTEM_ADMINISTRATOR.equals(role) || REPORT_ADMINISTRATOR.equals(role));
				if (isAdmin) {
					if (rolePql.indexOf(role) != -1) {
						int index = rolePql.indexOf('(');
						if (index != -1) {
							String usernamesSubStr = rolePql.substring(index);
							if (usernamesSubStr.contains("\"" + username + "\"")) {
								allowedActions.add(MANAGE_REPORTER_ACTION);
								allowedActions.add(VIEW_REPORTER_ACTION);
							}
						} else {
							int eqIndex = rolePql.lastIndexOf('=');
							if (eqIndex != -1) {
								String usernameSubString = rolePql.substring(eqIndex);
								if (usernameSubString.contains("\"" + username + "\"")) {
									allowedActions.add(MANAGE_REPORTER_ACTION);
									allowedActions.add(VIEW_REPORTER_ACTION);
								}
							}
						}
					}
				} else {
					if (rolePql.indexOf(role) != -1) {
						int index = rolePql.indexOf('(');
						if (index != -1) {
							String usernamesSubStr = rolePql.substring(index);
							if (usernamesSubStr.contains("\"" + username + "\"")) {
								allowedActions.add(VIEW_REPORTER_ACTION);
							}
						} else {
							int eqIndex = rolePql.lastIndexOf('=');
							if (eqIndex != -1) {
								String usernameSubString = rolePql.substring(eqIndex);
								if (usernameSubString.contains("\"" + username + "\"")) {
									allowedActions.add(VIEW_REPORTER_ACTION);
								}
							}
						}
					}
				}
			}

			rs.close();
			stmt.close();
			t.commit();
		} catch (Exception e) {
			log.error("Error occurred while fetching user roles", e);
		} finally {
			HibernateUtils.closeSession(session, log);
		}
		return allowedActions;
	}

	private List<String> findEntitiesByType(final String type) {
		Session session = null;
		Transaction t = null;
		String query = "";
		List<String> devEntities = new ArrayList<String>();
		try {
			query = "SELECT d.ID, d.NAME, d.DESCRIPTION, d.PQL, d.STATUS, d.TYPE"
					+ " FROM DEVELOPMENT_ENTITIES d WHERE d.TYPE = ? AND d.STATUS = ?";
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();

			session = dataSource.getSession();
			t = session.beginTransaction();
			Connection conn = session.connection();
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, type);
			stmt.setString(2, "DR");

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				final String pql = rs.getString("PQL");
				devEntities.add(pql);
			}
			rs.close();
			stmt.close();
			t.commit();
		} catch (Exception e) {
			log.error("Error occurred during delegation rule loading", e);
		} finally {
			HibernateUtils.closeSession(session, log);
		}
		return devEntities;
	}

	private ILoggedInUser getAuthenticatedUser(final String username, final AppContext ctx) {
		Session session = null;
		Transaction t = null;
		String query = "";
		ILoggedInUser loggedInUser = null;
		try {
			if (username.equalsIgnoreCase(IApplicationUserManager.SUPER_USER_USERNAME)) {
				query = "SELECT u.ID, u.USERNAME, u.FIRST_NAME, u.LAST_NAME, u.DOMAIN_ID, d.NAME  as DOMAIN, 'internal' as USER_TYPE, 'ADMIN' as USER_CATEGORY"
						+ " FROM SUPER_APPLICATION_USER u LEFT JOIN APPLICATION_USER_DOMAIN d ON ( d.ID = u.DOMAIN_ID)"
						+ "  WHERE LOWER(u.USERNAME) = ? ";
			} else {
				query = "SELECT u.ID, u.USERNAME, u.FIRST_NAME, u.LAST_NAME, u.DOMAIN_ID, d.NAME as DOMAIN, u.USER_TYPE, u.USER_CATEGORY"
						+ " FROM APPLICATION_USER u LEFT JOIN APPLICATION_USER_DOMAIN d ON ( d.ID = u.DOMAIN_ID)"
						+ " WHERE LOWER(u.USERNAME) = ? ";
			}
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();

			session = dataSource.getSession();
			t = session.beginTransaction();
			Connection conn = session.connection();
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, username.toLowerCase());

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				final Long userId = rs.getLong("ID");
				final String principalName = rs.getString("USERNAME").trim();
				final String firstName = rs.getString("FIRST_NAME");
				final String lastName = rs.getString("LAST_NAME");
				final String name = firstName + ((lastName != null) ? " " + lastName : "");
				final String domainName = rs.getString("DOMAIN");
				final String userType = rs.getString("USER_TYPE");
				ctx.setUserCategory(rs.getString("USER_CATEGORY"));

				loggedInUser = new ILoggedInUser() {

					@Override
					public Long getPrincipalId() {
						return userId;
					}

					@Override
					public String getUsername() {
						return name;
					}

					@Override
					public String getPrincipalName() {
						return (username.equalsIgnoreCase(IApplicationUserManager.SUPER_USER_USERNAME)) ? principalName
								: principalName + "@" + domainName;
					}

					@Override
					public boolean isPasswordModifiable() {
						if ("internal".equals(userType))
							return true;
						else
							return false;
					}
					
					@Override
					public String toString() {
						return "LoggedInUser [ User Id:" + this.getPrincipalId() + ", Principal name: "
								+ this.getPrincipalName() + "]";
					}
				};
			}

			rs.close();
			stmt.close();
			t.commit();
		} catch (Exception e) {
			log.error("Error occurred during fetch logged in user details", e);
		} finally {
			HibernateUtils.closeSession(session, log);
		}
		return loggedInUser;
	}
	
	
	@SuppressWarnings("unchecked")
	private Map<String, String> fetchExternalUserProperties(Long authHandlerId, String username) {
		log.info("UserProperties : [ authHandlerId = " + authHandlerId + ", username = " + username + "] ");

		Map<String, String> propsMap = new HashMap<String, String>();
		LdapContext ldapContext = null;

		try {
			Map<String, String> extSourceDetail = getExtSourceDetailsById(authHandlerId);
			ObjectMapper mapper = new ObjectMapper();

			if("LDAP".equals(extSourceDetail.get("TYPE"))) {
				HashMap<String, String> configData = mapper.readValue(extSourceDetail.get("CONFIG_DATA_JSON"), HashMap.class);
				log.debug("Config data map :" + configData);
				HashMap<String, String> userAttrs = mapper.readValue(extSourceDetail.get("USER_ATTRS_JSON"), HashMap.class);
				log.debug("UserAttrs data map :" + userAttrs);
				NamingEnumeration<SearchResult> resultsEnum = null;
				ldapContext = getLdapConnection(configData);
				SearchControls controls = getSearchControls(userAttrs, 100);
				resultsEnum = ldapContext.search(configData.get("baseDn"), userAttrs.get("username") + "=" + username,
						controls);
	
				if (resultsEnum.hasMore()) {
					Attributes attrs = resultsEnum.next().getAttributes();
					for (String attributeId : controls.getReturningAttributes()) {
						if (userAttrs.containsValue(attributeId)) {
							String internalAttr = getKeyFromValue(userAttrs, attributeId);
							propsMap.put(internalAttr,
									attrs.get(attributeId) == null ? "" : String.valueOf(attrs.get(attributeId).get()));
						}
					}
				}
				resultsEnum.close();
			} else if("OIDC".equals(extSourceDetail.get("TYPE"))) {
				HashMap<String, String> userAttrs = mapper.readValue(extSourceDetail.get("USER_ATTRS_JSON"), HashMap.class);
				log.debug("UserAttrs data map :" + userAttrs);
				Authority authority = AuthorityFactory.getAuthority(new JSONObject(extSourceDetail.get("CONFIG_DATA_JSON")));
				
				if(authority != null) {
					return authority.getUserAttributes(username, userAttrs);
				}
			}
		} catch (Exception e) {
			log.error("Error occured while getting AD user details", e);
		} finally {
			try {
				if (ldapContext != null)
					ldapContext.close();
			} catch (NamingException e) {
				log.warn("Error occured while closing LDAP connection", e);
			}
		}
		return propsMap;
	}

	@SuppressWarnings("unchecked")
	private Set<String> fetchExternalUserGroups(Long authHandlerId, String username) {
		log.info("UserGroups : [ authHandlerId = " + authHandlerId + ", username = " + username + "] ");

		try {
			Map<String, String> extSourceDetail = getExtSourceDetailsById(authHandlerId);

			if("OIDC".equals(extSourceDetail.get("TYPE"))) {
				Authority authority = AuthorityFactory.getAuthority(new JSONObject(extSourceDetail.get("CONFIG_DATA_JSON")));

				if(authority != null) {
					return authority.getUserGroups(username);
				}
			}
		} catch (Exception e) {
			log.error("Error occured while getting AD user details", e);
		}

		return new HashSet<>();
	}

	private Map<String, String> getExtSourceDetailsById(Long authHandlerId) {
		Map<String, String> authHandlerData = new HashMap<String, String>();
		Session session = null;
		try {
			String query = "SELECT ID, TYPE, CONFIG_DATA_JSON, USER_ATTRS_JSON FROM AUTH_HANDLER_REGISTRY WHERE ID = ?";

			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			IHibernateRepository.DbType dbType = dataSource.getDatabaseType();
			session = dataSource.getSession();
			Connection conn = session.connection();
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setLong(1, authHandlerId);

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				authHandlerData.put("TYPE", rs.getString("TYPE"));
				switch (dbType) {
					case ORACLE: {
						Clob configDataclob = rs.getClob("CONFIG_DATA_JSON");
						Clob userAttrclob = rs.getClob("USER_ATTRS_JSON");
						authHandlerData.put("CONFIG_DATA_JSON", configDataclob.getSubString(1, (int)configDataclob.length()));
						authHandlerData.put("USER_ATTRS_JSON", userAttrclob.getSubString(1, (int)userAttrclob.length()));
						break;
					}
					default: {
						authHandlerData.put("CONFIG_DATA_JSON", rs.getString("CONFIG_DATA_JSON"));
						authHandlerData.put("USER_ATTRS_JSON", rs.getString("USER_ATTRS_JSON"));
					}
				}
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			log.error("Error occurred during fetch application user properties", e);
		} finally {
			HibernateUtils.closeSession(session, log);
		}
		return authHandlerData;
	}
	
	private LdapContext getLdapConnection(Map<String,String> configData) throws Exception {

		String ldapURL = configData.get("ldapUrl");
		String ldapDomain = configData.get("ldapDomain");
		String username = configData.get("username");
		String password = configData.get("password");

		LdapContext ctx = null;
		Hashtable<String, String> env = new Hashtable<String, String>();

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		String securityPrincipal = username + "@" + ldapDomain;
		env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
		ReversibleEncryptor encryptor = new ReversibleEncryptor();
		env.put(Context.SECURITY_CREDENTIALS, encryptor.decrypt(password));
		env.put(Context.PROVIDER_URL, ldapURL);
		// This is LDAP via SSL
		if(isSSL(ldapURL)) {
			env.put(Context.SECURITY_PROTOCOL, "ssl");
			env.put("java.naming.ldap.ref.separator", ":");
			env.put("java.naming.ldap.factory.socket", ConfigurableSSLSocketFactory.class.getName());
		}
		// env.put(Context.REFERRAL,"follow");
		ctx = new InitialLdapContext(env, null);
		log.info("LDAP Connection Successful.");
		return ctx;
	}
	
	private SearchControls getSearchControls(Map<String,String> userAttrs, int pageSize) {
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchControls.setCountLimit(pageSize);
		searchControls.setTimeLimit(30000);
		Set<String> attrsIdsList = new HashSet<String>();
		String[] attrIDs = new String[userAttrs.size()];
		for (Entry<String, String> entry : userAttrs.entrySet()) {
			attrsIdsList.add(entry.getValue());
		}
		attrIDs = attrsIdsList.toArray(attrIDs);
		searchControls.setReturningAttributes(attrIDs);
		return searchControls;
	}
	
	private String getKeyFromValue(Map<String, String> attributesMap, String value) {

		for (String key : attributesMap.keySet()) {
			if (value.equalsIgnoreCase(attributesMap.get(key))) {
				return key;
			}
		}
		return null;
	}
	
	private boolean isSSL(String ldapURL) {
		if(ldapURL.toLowerCase().startsWith("ldaps://")) {
			return true;
		}
		
		return false;
	}
	

	private boolean isAdminCategory(HttpServletRequest httpReq) {
		AppContext ctx = AppContext.getContext(httpReq);
		
		if(ctx != null) {
			return "ADMIN".equals(ctx.getUserCategory());
		}
		
		return false;	
	}

	private void auditLogin(String username, AppContext ctx) {
		try {	
			ILoggedInUser loggedInUser = getAuthenticatedUser(username, ctx);
			Map<String, String> propsMap = getAuditMap(username);
			EntityAuditLogDO auditLog = new EntityAuditLogDO();
			auditLog.setAction(AuditAction.LOGIN.name());
			auditLog.setActor(propsMap.get("Display Name"));
			auditLog.setActorId(loggedInUser.getPrincipalId());
			auditLog.setEntityId(loggedInUser.getPrincipalId());
			auditLog.setEntityType(AuditableEntity.APPLICATION_USER.getCode());
			auditLog.setNewValue(JsonUtil.toJsonString(propsMap));
			
			getEntityAuditLogDAO().create(auditLog);
			ctx.setAttribute(LOGIN_AUDITED, "yes");
		} catch (SQLException | HibernateException | JsonProcessingException | JSONException e) {
			log.error("Error occurred in creating login audit logs.", e);
		}
	}

	public EntityAuditLogDAO getEntityAuditLogDAO() {
		if(entityAuditLogDAO == null) {
			synchronized(ApplicationUserDetailsFilter.class) {
				if(entityAuditLogDAO == null) {
					entityAuditLogDAO = new EntityAuditLogDAOImpl();
					log.info("Entity audit log DAO initialized.");
				}
			}
		}
		
		return entityAuditLogDAO;
	}
	
	private Map<String, String> getAuditMap(String username) {
		Session session = null;
		Transaction t = null;
		String query = "";
		Map<String, String> propsMap = new LinkedHashMap<>();
		Map<String, String> userAttributes = new LinkedHashMap<>();
		try {
			if (username.equalsIgnoreCase(IApplicationUserManager.SUPER_USER_USERNAME)) {
				query = "SELECT u.ID, u.USERNAME, u.DISPLAYNAME, u.LAST_NAME, u.DOMAIN_ID, d.NAME  as DOMAIN, 'internal' as USER_TYPE, 'ADMIN' as USER_CATEGORY"
						+ " FROM SUPER_APPLICATION_USER u LEFT JOIN APPLICATION_USER_DOMAIN d ON ( d.ID = u.DOMAIN_ID)"
						+ "  WHERE LOWER(u.USERNAME) = ? ";
			} else {
				query = "SELECT u.ID, u.USERNAME, u.DISPLAYNAME, u.LAST_NAME, u.DOMAIN_ID, d.NAME as DOMAIN, u.USER_TYPE, u.USER_CATEGORY"
						+ " FROM APPLICATION_USER u LEFT JOIN APPLICATION_USER_DOMAIN d ON ( d.ID = u.DOMAIN_ID)"
						+ " WHERE LOWER(u.USERNAME) = ? ";
			}

			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			session = dataSource.getSession();
			t = session.beginTransaction();
			Connection conn = session.connection();
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, username.toLowerCase());

			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {	
				propsMap.put("User Type", rs.getString("USER_TYPE"));
				propsMap.put("User Category", rs.getString("USER_CATEGORY"));
				propsMap.put("Display Name", rs.getString("DISPLAYNAME"));
				propsMap.put("username", rs.getString("USERNAME"));
			}

			rs.close();
			stmt.close();
			t.commit();
		} catch (Exception e) {
			log.error("Error occurred during fetch application user properties", e);
		} finally {
			HibernateUtils.closeSession(session, log);
		}
		return propsMap;
	}
}
