package com.nextlabs.authentication.services.impl;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextlabs.authentication.enums.AuthType;
import com.nextlabs.authentication.enums.UserAttributeKey;
import com.nextlabs.authentication.enums.UserStatus;
import com.nextlabs.authentication.handlers.authentication.AuthHandlerDetail;
import com.nextlabs.authentication.models.ApplicationUser;
import com.nextlabs.authentication.repositories.ApplicationUserRepository;
import com.nextlabs.authentication.repositories.AuthHandlerRegistryRepository;
import com.nextlabs.authentication.services.UserAttributeProviderService;

/**
 * Provides attributes of an LDAP user.
 *
 * @author Sachindra Dasun
 */
@Service
public class LdapUserAttributeProviderServiceImpl implements UserAttributeProviderService {

    private static final Logger logger = LoggerFactory.getLogger(LdapUserAttributeProviderServiceImpl.class);

    @Autowired
    private AuthHandlerRegistryRepository handlerRepository;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Override
    public Map<String, Set<String>> getAttributes(String username) {
        Map<String, Set<String>> userAttributes = new HashMap<>();
        ApplicationUser applicationUser = applicationUserRepository.findByUsernameIgnoreCaseAndStatus(username,
                UserStatus.ACTIVE).orElseThrow();
        userAttributes.put(UserAttributeKey.NEXTLABS_CC_USER_CATEGORY.getKey(),
                Set.of(applicationUser.getUserCategory().name()));
        LdapContext ldapContext = null;
        NamingEnumeration<SearchResult> resultsEnum = null;
        try {
            AuthHandlerDetail authHandlerDetail = new AuthHandlerDetail(handlerRepository
                    .findById(applicationUser.getAuthHandlerId()).orElseThrow());
            ldapContext = getLdapConnection(authHandlerDetail);

            SearchControls constraints = getSearchControls(authHandlerDetail);

            String usernameFilter = String.format("%s=%s",
                    authHandlerDetail.getUserAttributes().get(AuthHandlerDetail.USERNAME), username);
            resultsEnum = ldapContext.search(authHandlerDetail.getConfigData().get(AuthHandlerDetail.BASE_DN),
                    usernameFilter, constraints);

            Map<String, String> attributeMapping = authHandlerDetail.getUserAttributes();
            if (resultsEnum.hasMore()) {
                Attributes attributes = resultsEnum.next().getAttributes();
                for (String externalAttributeKey : constraints.getReturningAttributes()) {
                    if (attributeMapping.containsValue(externalAttributeKey)) {
                        String internalAttributeKey = getInternalAttributeKey(attributeMapping, externalAttributeKey);
                        Attribute attributeValue = attributes.get(externalAttributeKey);
                        userAttributes.put(internalAttributeKey,
                                Set.of(attributeValue == null ? "" : String.valueOf(attributeValue.get())));
                    }
                }
            }
            userAttributes.put(UserAttributeKey.NEXTLABS_CC_USER_ID.getKey(), Set.of(String.valueOf(applicationUser.getId())));
        } catch (NamingException | IOException e) {
            logger.error("Error in getting attributes", e);
        } finally {
            if (resultsEnum != null) {
                try {
                    resultsEnum.close();
                } catch (NamingException e) {
                    logger.error("Exception while close the ldap search result ", e);
                }
            }
            if (ldapContext != null) {
                try {
                    ldapContext.close();
                } catch (NamingException e) {
                    logger.error("Exception while close the ldapContext ", e);
                }
            }
        }
        return userAttributes;
    }

    @Override
    public String getAuthenticationMethod() {
        return AuthType.LDAP.name();
    }

    private LdapContext getLdapConnection(AuthHandlerDetail authHandlerDetail) throws NamingException, IOException {
        String ldapURL = authHandlerDetail.getConfigData().get(AuthHandlerDetail.LDAP_URL);
        String ldapDomain = authHandlerDetail.getConfigData().get(AuthHandlerDetail.LDAP_DOMAIN);
        String username = authHandlerDetail.getConfigData().get(AuthHandlerDetail.USERNAME);
        String password = authHandlerDetail.getConfigData().get(AuthHandlerDetail.PASSWORD);
        String securityPrincipal = username + "@" + ldapDomain;

        Hashtable<String, String> environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
        environment.put(Context.SECURITY_CREDENTIALS, password);
        environment.put(Context.PROVIDER_URL, ldapURL);
        environment.put(Context.REFERRAL, "follow");
        environment.put("com.sun.jndi.ldap.read.timeout",
                Integer.toString(Integer.parseInt(authHandlerDetail.getConfigData()
                        .getOrDefault(AuthHandlerDetail.CONNECTION_TIMEOUT, "30")) * 1000));
        // This is LDAP via SSL
        boolean isSecuredConnection = ldapURL.toLowerCase().startsWith("ldaps://");
        if (isSecuredConnection) {
            environment.put(Context.SECURITY_PROTOCOL, "ssl");
            environment.put("java.naming.ldap.ref.separator", ":");
            environment.put("java.naming.ldap.factory.socket", SSLSocketFactory.class.getName());
        }
        LdapContext ctx = new InitialLdapContext(environment, null);

        if (!isSecuredConnection
                && Boolean.parseBoolean(authHandlerDetail.getConfigData().get(AuthHandlerDetail.USE_STARTTLS))) {
            StartTlsResponse startTlsResponse = (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
            startTlsResponse.negotiate();
            if (logger.isDebugEnabled()) {
                logger.debug("StartTLS connection established successfully with LDAP server");
            }
        }
        logger.info("LDAP Connection Successful.");
        return ctx;
    }

    private SearchControls getSearchControls(AuthHandlerDetail authHandlerDetail) {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setCountLimit(100);
        searchControls.setTimeLimit(Integer.parseInt(authHandlerDetail.getConfigData()
                .getOrDefault(AuthHandlerDetail.CONNECTION_TIMEOUT, "30")) * 1000);
        Set<String> attrsIdsList = new HashSet<>();
        String[] attrIDs = new String[authHandlerDetail.getUserAttributes().size()];
        for (Map.Entry<String, String> entry : authHandlerDetail.getUserAttributes().entrySet()) {
            attrsIdsList.add(entry.getValue());
        }
        attrIDs = attrsIdsList.toArray(attrIDs);
        searchControls.setReturningAttributes(attrIDs);
        return searchControls;
    }

}
