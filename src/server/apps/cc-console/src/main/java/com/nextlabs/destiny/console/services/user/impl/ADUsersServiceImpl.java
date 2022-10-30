/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 2 Aug 2016
 *
 */
package com.nextlabs.destiny.console.services.user.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.SizeLimitExceededException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InvalidSearchFilterException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.SortControl;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.SSLSocketFactory;

import com.nextlabs.destiny.console.dto.common.ExternalGroupDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextlabs.destiny.console.dao.ApplicationUserDao;
import com.nextlabs.destiny.console.dao.authentication.AuthHandlerTypeDetailDao;
import com.nextlabs.destiny.console.dto.authentication.AuthHandlerDetail;
import com.nextlabs.destiny.console.dto.authentication.activedirectory.AdSearchResponse;
import com.nextlabs.destiny.console.dto.common.ExternalUserDTO;
import com.nextlabs.destiny.console.enums.AuthHandlerType;
import com.nextlabs.destiny.console.exceptions.ConnectionFailedException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.init.ConfigurationDataLoader;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.authentication.AuthHandlerTypeDetail;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.user.ExternalUserService;

/**
 * External Users Service implementation to fetch details of Active Directory
 * Users
 *
 * 
 * @author aishwarya
 * @since 8.0
 *
 */
@Service("ADUsersService")
public class ADUsersServiceImpl implements ExternalUserService {

	private static final Logger log = LoggerFactory.getLogger(ADUsersServiceImpl.class);

	@Autowired
	protected MessageBundleService msgBundle;

	@Autowired
	private AuthHandlerTypeDetailDao authHandlerDao;

	@Autowired
	private ApplicationUserDao applicationUserDao;
	
	@Autowired
	private ConfigurationDataLoader configDataLoader;
	
	private static ConfigurationDataLoader configDataLoaderMirror;
	
	@PostConstruct
	public void manualWiring() {
		configDataLoaderMirror = this.configDataLoader;
	}
	
	public static ConfigurationDataLoader getConfigurationDataLoader() {
		return configDataLoaderMirror;
	}
	
	@Override
	public Map<String, String> getExternalUserAttributesByName(AuthHandlerDetail configDetails, String username)
			throws ConsoleException {
        Map<String, String> userDetailsMap = new HashMap<>();
		LdapContext ldapContext = null;
		NamingEnumeration<SearchResult> resultsEnum = null;

		try {
			ldapContext = getLdapConnection(configDetails);

			SearchControls constraints = getSearchControls(configDetails, 100, true);

			resultsEnum = ldapContext.search(configDetails.getConfigData().get(AuthHandlerDetail.BASE_DN),
					configDetails.getUserAttributes().get(AuthHandlerDetail.USERNAME) + "=" + username, constraints);

			Map<String, String> mappedAttrs = getMappedAttributes(configDetails);

			if (resultsEnum.hasMore()) {
				Attributes attrs = resultsEnum.next().getAttributes();
				for (String attributeId : constraints.getReturningAttributes()) {
					if (mappedAttrs.containsValue(attributeId)) {
						String internalAttr = getKeyFromValue(mappedAttrs, attributeId);
						userDetailsMap.put(internalAttr,
								attrs.get(attributeId) == null ? "" : String.valueOf(attrs.get(attributeId).get()));
					}
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new ConsoleException("Error occurred while getting AD user details");
		} finally {
			if(resultsEnum != null) {
				try {
					resultsEnum.close();
				} catch (NamingException e) {
					log.error("Exception while close the ldap search result ", e);
				}
			}
			
			if(ldapContext != null) {
				try {
					ldapContext.close();
				} catch (NamingException e) {
					log.error("Exception while close the ldapContext ", e);
				}
			}
		}
		return userDetailsMap;
	}

	@Override
    public void checkConnection(AuthHandlerDetail handler) {
		LdapContext ldapContext = null;
		try {
			ldapContext = getLdapConnection(handler);
			getSearchUsers(handler, ldapContext, handler.getConfigData().get(AuthHandlerDetail.BASE_DN), "", 5, new TreeSet<>(), false);
		} catch (AuthenticationException ex) {
			log.error("LDAP Authentication failed", ex.getMessage());
			throw new ConnectionFailedException(msgBundle.getText("auth.handler.auth.failed.code"),
					msgBundle.getText("auth.handler.auth.failed", "LDAP"));
		} catch (InvalidSearchFilterException invalidSearchFilter) {
			throw new ConnectionFailedException(msgBundle.getText("auth.handler.invalid.search.filter.code"),
					msgBundle.getText("auth.handler.invalid.search.filter"));	
	    }catch (Exception ex) {
			log.error("LDAP Connection failed", ex);
			throw new ConnectionFailedException(msgBundle.getText("auth.handler.verify.failed.code"),
					msgBundle.getText("auth.handler.verify.failed", "LDAP"));
		} finally {
			if(ldapContext != null) {
				try {
					ldapContext.close();
				} catch (NamingException e) {
					log.error("Exception while close the ldapContext ", e);
				}
			}
		}
	}

	@Override
	public AdSearchResponse getAllUsers(Long authHandlerId, String searchTxt, int pageSize) throws ConsoleException {
		LdapContext ldapContext = null;
		try {
			String searchText = (StringUtils.isEmpty(searchTxt)) ? "*" : searchTxt + "*";
			AuthHandlerDetail configDetails = getConfigDetails(authHandlerId);
			ldapContext = getLdapConnection(configDetails);

			return getSearchUsers(configDetails, ldapContext, configDetails.getConfigData().get(AuthHandlerDetail.BASE_DN), searchText, pageSize,
							getAlreadyExistingUsers(configDetails.getId()), true);
		} catch (InvalidSearchFilterException invalidSearchFilter) {
			throw new ConsoleException(msgBundle.getText("auth.handler.invalid.search.filter.code"),
					msgBundle.getText("auth.handler.invalid.search.filter"), "Invalid search filter", invalidSearchFilter);	
		} catch (Exception e) {
			throw new ConsoleException("Error occurred while fetching external users");
		} finally {
			if (ldapContext != null) {
				try {
					ldapContext.close();
				} catch (NamingException e) {
					log.error("Exception while close the ldapContext ", e);
				}
			}
		}
	}

	@Override
	public AdSearchResponse getAllGroups(Long authHandlerId, String searchTxt, int pageSize) {
		return new AdSearchResponse();
	}

	@Override
	public ExternalGroupDTO getGroup(Long authHandlerId, String groupId)
			throws ConsoleException {
		return null;
	}

	@Override
	public AdSearchResponse getAllProvisionedGroups(Long authHandlerId, String searchTxt, int pageSize) {
		return new AdSearchResponse();
	}

	@Override
	public Set<String> getUserGroups(AuthHandlerDetail authHandlerDetail, String username) {
		return new HashSet<>();
	}

	@Override
	public List<ApplicationUser> getUserWithoutProvisionedGroups(Long authHandlerId) {
		return new ArrayList<>();
	}

	private AdSearchResponse getSearchUsers(AuthHandlerDetail configDetails, LdapContext ctx, String searchBase,
			String searchTxt, int pageSize, Set<String> existingUsers, boolean returnAttribs) throws Exception {
		Pattern usernameRegex = Pattern.compile(msgBundle.getText("username.pattern"));
		String firstNameKey = configDetails.getUserAttributes().get(msgBundle.getText("attr.firstName.key"));
		String usernameKey = configDetails.getUserAttributes().get(msgBundle.getText("attr.username.key"));
		Map<String, String> mappedAttrs = getMappedAttributes(configDetails);
		
		if (returnAttribs) {
			ctx.setRequestControls(getLDAPControls(configDetails));
		}
		String userSearchBase = configDetails.getConfigData().get(AuthHandlerDetail.USER_SEARCH_BASE);
		String searchFilter = "(&" + userSearchBase + "(|(sAMAccountName=" + searchTxt + ")(name=" + searchTxt + ")))";

		int rowCount = (pageSize + existingUsers.size() + 2);
		NamingEnumeration<?> namingEnum = ctx.search(searchBase, searchFilter,
				getSearchControls(configDetails, rowCount, returnAttribs));

		return populateExternalUsers(configDetails, existingUsers, usernameRegex, firstNameKey,
				usernameKey, mappedAttrs, namingEnum, pageSize, rowCount);
	}

	private AdSearchResponse populateExternalUsers(AuthHandlerDetail configDetails,
												   Set<String> alreadyExistingUsernames, Pattern usernameRegex, String firstNameKey, String usernameKey,
												   Map<String, String> mappedAttrs, NamingEnumeration<?> namingEnum, int pageSize, int rowCount) throws NamingException {
		AdSearchResponse response = new AdSearchResponse();

		int itemCount = 0;
		int i = 0;
		while (namingEnum != null && i < rowCount && namingEnum.hasMore()) {
			SearchResult result = (SearchResult) namingEnum.next();
			i++;
			Attributes attrs = result.getAttributes();
			String username = attrs.get(usernameKey) == null ? "" : String.valueOf(attrs.get(usernameKey).get());
			String firstName = attrs.get(firstNameKey) == null ? "" : String.valueOf(attrs.get(firstNameKey).get());
			String lastName = " ";
			if (StringUtils.isEmpty(username) || StringUtils.isEmpty(firstName)
					|| alreadyExistingUsernames.contains(username) || !usernameRegex.matcher(username).matches()) {
				continue;
			}

			ExternalUserDTO extUser = new ExternalUserDTO();
			extUser.setExternalId(username);
			extUser.setFirstName(firstName);
			extUser.setUsername(username);
			extUser.setUserType(ApplicationUser.USER_TYPE_IMPORTED);
			extUser.setAuthHandlerId(configDetails.getId());

			// set lastName if mapped
			if (configDetails.getUserAttributes().containsKey(msgBundle.getText("attr.lastName.key"))) {
				String lastNameKey = configDetails.getUserAttributes().get(msgBundle.getText("attr.lastName.key"));
				mappedAttrs.remove(lastNameKey);
				lastName = attrs.get(lastNameKey) == null ? "" : String.valueOf(attrs.get(lastNameKey).get());
				extUser.setLastName(lastName);
			}
			// set email if mapped
			if (configDetails.getUserAttributes().containsKey(msgBundle.getText("attr.email.key"))) {
				String emailKey = configDetails.getUserAttributes().get(msgBundle.getText("attr.email.key"));
				mappedAttrs.remove(emailKey);
				extUser.setEmail(attrs.get(emailKey) == null ? "" : String.valueOf(attrs.get(emailKey).get()));
			}
			// set displayName if mapped
			if (configDetails.getUserAttributes().containsKey(msgBundle.getText("attr.displayName.key"))) {
				String displayNameKey = configDetails.getUserAttributes().get(msgBundle.getText("attr.displayName.key"));
				mappedAttrs.remove(displayNameKey);
				String displayName = attrs.get(displayNameKey) == null ? String.format("%s %s", firstName, lastName) : String.valueOf(attrs.get(displayNameKey).get());
				extUser.setDisplayName(displayName);
			}

			Map<String, String> properties = new HashMap<>();
			for (String internalAttr : mappedAttrs.keySet()) {
				if(internalAttr.equals(msgBundle.getText("attr.username.key"))) {
					continue;
				}
				String externalAttr = mappedAttrs.get(internalAttr);
				properties.put(internalAttr,
						attrs.get(externalAttr) == null ? "" : String.valueOf(attrs.get(externalAttr).get()));
			}
			extUser.setAttributes(properties);
			response.getAdUsers().add(extUser);

			itemCount++;
			if (pageSize <= itemCount) {
				try {
					response.setHasMoreResults(namingEnum.hasMore());
				} catch (SizeLimitExceededException e) {
					response.setHasMoreResults(true);
				}
				break;
			}
		}
		
		namingEnum.close();
		return response;
	}

	private Control[] getLDAPControls(AuthHandlerDetail configDetails)
					throws IOException {
		// sort the results by user name
		SortControl sortControl = new SortControl(configDetails.getUserAttributes().get(AuthHandlerDetail.USERNAME), Control.CRITICAL);
		Control[] reqControls = new Control[1];
		reqControls[0] = sortControl;
		return reqControls;
	}

	private Map<String, String> getMappedAttributes(AuthHandlerDetail configDetails) {
        Map<String, String> mappedAttrs = new HashMap<>();
		mappedAttrs.putAll(configDetails.getUserAttributes());
		return mappedAttrs;
	}

	/**
	 * username of all active users and those imported from another auth handler
	 * can not be imported
	 * 
	 * @param authHandlerId
	 * @return
	 */
    private Set<String> getAlreadyExistingUsers(Long authHandlerId) {
		Set<String> existingAppUsers = new TreeSet<>();
		List<ApplicationUser> appUsers = applicationUserDao.findAllActiveOrOtherHandler(authHandlerId);
		for (ApplicationUser appUser : appUsers) {
			existingAppUsers.add(appUser.getUsername());
		}
		return existingAppUsers;
	}

	@Override
	public Set<String> getAllUserAttributes(Long authHandlerId) {
		return getAllUserAttributes(getConfigDetails(authHandlerId));
	}

	@Override
	public Set<String> getAllUserAttributes(AuthHandlerDetail handlerDTO) {
		LdapContext ldapContext = null;
		try {
			Map<String, String> configData = handlerDTO.getConfigData();
			ldapContext = getLdapConnection(handlerDTO);
            return getLDAPUserAttributes(ldapContext, String.valueOf(configData.get(AuthHandlerDetail.BASE_DN)), handlerDTO);
		} catch (Exception e) {
			log.error("Error while fetching all user attributes for auth handler", e);
			throw new ConnectionFailedException(msgBundle.getText("auth.handler.verify.failed.code"),
					msgBundle.getText("auth.handler.verify.failed", AuthHandlerType.LDAP.name()));
		} finally {
			if (ldapContext != null) {
				try {
					ldapContext.close();
				} catch (NamingException e) {
					log.error("Exception while close the ldapContext ", e);
				}
			}
		}
	}

	public Set<String> getLDAPUserAttributes(LdapContext context, String searchBase, AuthHandlerDetail handlerDTO) throws Exception {
		Set<String> attributes = new TreeSet<>();
		int count = 0;

		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		constraints.setTimeLimit(Integer.parseInt(handlerDTO.getConfigData()
						.getOrDefault(AuthHandlerDetail.CONNECTION_TIMEOUT, "30")) * 1000);
		NamingEnumeration<?> usersEnum = context.search(searchBase, "(objectclass=user)", constraints);

		while (usersEnum.hasMore()) {
			SearchResult result = (SearchResult) usersEnum.next();
			count++;
			Attributes attrs = result.getAttributes();
			NamingEnumeration<String> attributesEnum = attrs.getIDs();
			while (attributesEnum.hasMore()) {
				String attrName = attributesEnum.next();
				attributes.add(attrName);
			}
			attributesEnum.close();
			if (count > 0) {
				break;
			}
		}
		usersEnum.close();

		return attributes;
	}

    private AuthHandlerDetail getConfigDetails(Long authHandlerId) {
		AuthHandlerTypeDetail authHandler = authHandlerDao.findById(authHandlerId);
		return AuthHandlerDetail.getDTO(authHandler);
	}

	private SearchControls getSearchControls(AuthHandlerDetail configDetails, int pageSize, boolean returnAttribs) {
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchControls.setCountLimit(pageSize);
		searchControls.setTimeLimit(Integer.parseInt(configDetails.getConfigData()
						.getOrDefault(AuthHandlerDetail.CONNECTION_TIMEOUT, "30")) * 1000);

		if (returnAttribs) {
			Set<String> attrsIdsList = new HashSet<>();
			String[] attrIDs = new String[configDetails.getUserAttributes().size()];
			for (Entry<String, String> entry : configDetails.getUserAttributes().entrySet()) {
				attrsIdsList.add(entry.getValue());
			}
			attrIDs = attrsIdsList.toArray(attrIDs);
			searchControls.setReturningAttributes(attrIDs);
		}
		return searchControls;
	}
	
	// Change to synchronized so System properties of key store and trust store can be restore
	private LdapContext getLdapConnection(AuthHandlerDetail configDetails) throws Exception {
		String ldapURL = configDetails.getConfigData().get(AuthHandlerDetail.LDAP_URL);
		String ldapDomain = configDetails.getConfigData().get(AuthHandlerDetail.LDAP_DOMAIN);
		String username = configDetails.getConfigData().get(AuthHandlerDetail.USERNAME);
		String password = configDetails.getConfigData().get(AuthHandlerDetail.PASSWORD);
		String securityPrincipal = username + "@" + ldapDomain;

		LdapContext ctx;
        Hashtable<String, String> env = new Hashtable<>();

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
		env.put(Context.SECURITY_CREDENTIALS, password);
		env.put(Context.PROVIDER_URL, ldapURL);
		env.put(Context.REFERRAL, "follow");
		env.put("com.sun.jndi.ldap.read.timeout",
						Integer.toString(Integer.parseInt(configDetails.getConfigData()
										.getOrDefault(AuthHandlerDetail.CONNECTION_TIMEOUT, "30")) * 1000));
		// This is LDAP via SSL
		boolean isSecuredConnection = isSSL(ldapURL);
		if(isSecuredConnection) {
			env.put(Context.SECURITY_PROTOCOL, "ssl");
			env.put("java.naming.ldap.ref.separator", ":");
			env.put("java.naming.ldap.factory.socket", SSLSocketFactory.class.getName());
		}
		ctx = new InitialLdapContext(env, null);

		if(!isSecuredConnection
				&& Boolean.valueOf(configDetails.getConfigData().get(AuthHandlerDetail.USE_STARTTLS))) {
			StartTlsResponse startTlsResponse = (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
			startTlsResponse.negotiate();
			if (log.isDebugEnabled()) {
				log.debug("StartTLS connection established successfully with LDAP server");
			}
		}

		log.info("LDAP Connection Successful.");
		return ctx;
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
		return ldapURL.toLowerCase().startsWith("ldaps://");
	}
}
