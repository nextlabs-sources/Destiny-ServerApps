package com.nextlabs.authentication.handlers.authentication;

import javax.annotation.PostConstruct;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.FailedLoginException;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apereo.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.LdapAuthenticationHandler;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.hibernate.exception.GenericJDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.bluejungle.framework.crypt.ReversibleEncryptor;
import com.nextlabs.authentication.config.properties.FailedLoginProperties;
import com.nextlabs.authentication.dto.AuthTypeDetails;
import com.nextlabs.authentication.enums.AuthType;
import com.nextlabs.authentication.enums.AuthTypeConfigData;
import com.nextlabs.authentication.enums.LogMarker;
import com.nextlabs.authentication.enums.UserStatus;
import com.nextlabs.authentication.enums.UserType;
import com.nextlabs.authentication.repositories.ApplicationUserRepository;
import com.nextlabs.authentication.repositories.AuthHandlerRegistryRepository;
import com.nextlabs.authentication.util.LicenseCheckerUtil;

/**
 * <p>
 * NextLabs Custom Authentication Handler
 * <p>
 * Retrieves the list of registered authentication handlers Invokes the
 * appropriate authenticate method and returns the HandlerResponse
 *
 * @author aishwarya
 * @since 8.0
 */
public class CCAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

    private static final Logger logger = LoggerFactory.getLogger(CCAuthenticationHandler.class);
    private static final long REFRESH_INTERVAL_IN_SECONDS = 300;

    @Autowired
    private ReversibleEncryptor reversibleEncryptor;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private AuthHandlerRegistryRepository authHandlerRegistryRepository;

    @Autowired
    private FailedLoginProperties failedLoginProperties;

    @Value("${cc.home}")
    private String ccHome;

    private AbstractJdbcUsernamePasswordAuthenticationHandler primaryHandler;

    private List<AuthTypeDetails> registeredAuthHandlers;
    private final Map<Long, AuthTypeDetails> registeredAuthHandlersMap = new HashMap<>();

    private long lastRefreshedTime;

    public CCAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    @PostConstruct
    public void loadRegisteredAuthHandlers() {
        getAllRegisteredHandlers();
        lastRefreshedTime = System.currentTimeMillis();
        LicenseCheckerUtil.init(Paths.get(ccHome, "server", "license").toString());
    }

    @Override
    public AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(
                    UsernamePasswordCredential credential, String name)
            throws GeneralSecurityException {
        boolean isValidLicense = LicenseCheckerUtil.isValidLicense();
        if (!isValidLicense) {
            logger.warn(LogMarker.SYSTEM, "License expired. License renewal required.");
            throw new AccountExpiredException("License Expired");
        }

        AuthenticationHandlerExecutionResult authHandlerResult;

        try {
            authHandlerResult = primaryHandler.authenticate(credential);
        } catch (GenericJDBCException | AccountLockedException | AccountPasswordMustChangeException e) {
            throw e;
        } catch (Exception ex) {
            authHandlerResult = authViaAD(credential);
        }

        if (authHandlerResult != null) {
            if (failedLoginProperties.getAttempts() > 0) {
                resetFailedLoginAttempts(credential.getUsername());
            }
            logger.info(LogMarker.AUTHENTICATION, "User logged in. [username={}]", credential.getUsername());
            return authHandlerResult;
        } else {
            if (failedLoginProperties.getAttempts() > 0) {
                increaseFailedLoginAttempts(credential.getUsername());
            }

            logger.warn(LogMarker.AUTHENTICATION, "Invalid credential. [username={}]", credential.getUsername());
            throw new FailedLoginException("Authentication Failed");
        }
    }

    private AuthenticationHandlerExecutionResult authViaAD(UsernamePasswordCredential credential) {
        refreshRegisteredAuthHandlers();
        // if LDAP enabled, attempt LDAP Authentication
        if (registeredAuthHandlers.stream()
                .anyMatch(authTypeDetails -> AuthType.LDAP.name().equalsIgnoreCase(authTypeDetails.getType()))) {
            return applicationUserRepository.findByUsernameIgnoreCaseAndUserTypeAndStatus(credential.getUsername(),
                    UserType.IMPORTED.getType(), UserStatus.ACTIVE)
                    .map(applicationUser -> attemptLdapAuthentication(credential, new AuthHandlerFactory(),
                            registeredAuthHandlersMap.get(applicationUser.getAuthHandlerId()))).orElse(null);
        }
        return null;
    }

    /**
     * This method perform the LDAP authentication.
     *
     * @param credential         username and password
     * @param authHandlerFactory authentication handler factory
     * @param authTypeDetails    authentication type details
     * @return the result of the authentication
     */
    private AuthenticationHandlerExecutionResult attemptLdapAuthentication(UsernamePasswordCredential credential,
                                                                           AuthHandlerFactory authHandlerFactory,
                                                                           AuthTypeDetails authTypeDetails) {
        try {
            LdapAuthenticationHandler ldapAuthHandler = (LdapAuthenticationHandler) authHandlerFactory
                .createLDAPHandler(authTypeDetails, getServicesManager(), getPrincipalFactory(),
                        registeredAuthHandlers.size(), getPasswordPolicyHandlingStrategy());
            return ldapAuthHandler.authenticate(credential);
        } catch (Exception e) {
            logger.error("Exception while authenticating via AD", e);
            return null;
        }
    }

    /**
     * Refresh the list of authentication handlers on save/update in cc database
     * <p>
     * Should be invoked on receiving a notification from console app
     */
    private void refreshRegisteredAuthHandlers() {
        long diffInSeconds = (System.currentTimeMillis() - lastRefreshedTime) / 1000;
        if (diffInSeconds > REFRESH_INTERVAL_IN_SECONDS) {
            getAllRegisteredHandlers();
            lastRefreshedTime = System.currentTimeMillis();
        }
    }

    /**
     * Fetches all the registered authentication providers from cc database
     */
    private void getAllRegisteredHandlers() {
        registeredAuthHandlers = new ArrayList<>();
        registeredAuthHandlersMap.clear();
        authHandlerRegistryRepository.findByTypeIgnoreCase(AuthType.LDAP.name()).forEach(authHandlerRegistry -> {
            AuthTypeDetails authTypeDetails = new AuthTypeDetails();
            authTypeDetails.setId(authHandlerRegistry.getId());
            authTypeDetails.setType(authHandlerRegistry.getType());
            try {
                @SuppressWarnings("unchecked")
                Map<String, String> configData = new ObjectMapper().readValue(authHandlerRegistry.getConfigDataJson(), HashMap.class);
                configData.put(AuthTypeConfigData.PASSWORD.getKey(), reversibleEncryptor.decrypt(configData.get(AuthTypeConfigData.PASSWORD.getKey())));
                authTypeDetails.setConfigData(configData);

                Map<String, String> userAttributeMap = new ObjectMapper().readValue(authHandlerRegistry.getUserAttrsJson(), HashMap.class);
                authTypeDetails.setUserAttrMap(userAttributeMap);

                registeredAuthHandlers.add(authTypeDetails);
                registeredAuthHandlersMap.put(authTypeDetails.getId(), authTypeDetails);
            } catch (IOException e) {
                logger.error("Error occurred while parsing json", e);
            }
        });
    }

    /**
     * This will only reset application user account
     *
     * @param username account username to reset
     */
    private void resetFailedLoginAttempts(String username)
            throws GenericJDBCException {
        applicationUserRepository.findByUsernameIgnoreCaseAndStatus(username, UserStatus.ACTIVE)
            .ifPresent(applicationUser -> {
                applicationUser.setFailedLoginAttempts(0);
                applicationUserRepository.save(applicationUser);
            });
    }

    private void increaseFailedLoginAttempts(String username)
            throws GenericJDBCException {
        applicationUserRepository.findByUsernameIgnoreCaseAndStatus(username, UserStatus.ACTIVE)
            .ifPresent(applicationUser -> {
                applicationUser.setFailedLoginAttempts(applicationUser.getFailedLoginAttempts() + 1);
                if (applicationUser.getFailedLoginAttempts() >= failedLoginProperties.getAttempts()) {
                    logger.warn(LogMarker.AUTHENTICATION, "User account locked. [username={}, failedLoginAttempts={}]",
                            applicationUser.getUsername(), applicationUser.getFailedLoginAttempts());
                    applicationUser.setLocked(true);
                }
                applicationUserRepository.save(applicationUser);
            });
    }

    public void setPrimaryHandler(AbstractJdbcUsernamePasswordAuthenticationHandler primaryHandler) {
        this.primaryHandler = primaryHandler;
    }

}
