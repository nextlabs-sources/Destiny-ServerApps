package com.nextlabs.authentication.handlers.authentication;

import com.nextlabs.authentication.config.properties.LdapSslProperties;
import com.nextlabs.authentication.dto.AuthTypeDetails;
import com.nextlabs.authentication.enums.AuthTypeConfigData;
import com.nextlabs.authentication.enums.AuthTypeUserAttribute;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.AuthenticationPasswordPolicyHandlingStrategy;
import org.apereo.cas.authentication.LdapAuthenticationHandler;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.ldaptive.BindConnectionInitializer;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.Credential;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.ReturnAttributes;
import org.ldaptive.auth.AuthenticationResponseHandler;
import org.ldaptive.auth.Authenticator;
import org.ldaptive.auth.SearchDnResolver;
import org.ldaptive.auth.SearchEntryResolver;
import org.ldaptive.auth.SimpleBindAuthenticationHandler;
import org.ldaptive.auth.ext.ActiveDirectoryAuthenticationResponseHandler;
import org.ldaptive.ssl.KeyStoreCredentialConfig;
import org.ldaptive.ssl.SslConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.time.Duration;
import java.util.Map;

/**
 * Creates Authentication Handlers given the details
 *
 * @author aishwarya
 * @since 8.0
 */
class AuthHandlerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthHandlerFactory.class);
    private static final String FILE_RESOURCE_PATTERN = "file:%s";

    @Autowired
    private LdapSslProperties ldapSslProperties;

    AbstractUsernamePasswordAuthenticationHandler createLDAPHandler(AuthTypeDetails authTypeDetails, ServicesManager servicesManager,
                                                                    PrincipalFactory factory, int order, AuthenticationPasswordPolicyHandlingStrategy strategy) {
        Map<String, String> configData = authTypeDetails.getConfigData();

        // LDAP SSL configuration
        SslConfig sslConfig = new SslConfig();
        KeyStoreCredentialConfig credentialConfig = new KeyStoreCredentialConfig();
        boolean useSSL = isSSL(configData.get(AuthTypeConfigData.LDAP_URL.getKey()));
        if (useSSL) {
            try {
                if (StringUtils.isNotEmpty(ldapSslProperties.getKeyStoreFile())) {
                    credentialConfig.setKeyStore(String.format(FILE_RESOURCE_PATTERN, ldapSslProperties.getKeyStoreFile()));
                    if (StringUtils.isNotEmpty(ldapSslProperties.getKeyStorePassword())) {
                        credentialConfig.setKeyStorePassword(ldapSslProperties.getKeyStorePassword());
                    }
                    if (StringUtils.isNotEmpty(ldapSslProperties.getKeyStoreType())) {
                        credentialConfig.setKeyStoreType(ldapSslProperties.getKeyStoreType());
                    }
                }
                if (StringUtils.isNotEmpty(ldapSslProperties.getTrustStoreFile())) {
                    credentialConfig.setTrustStore(String.format(FILE_RESOURCE_PATTERN, ldapSslProperties.getTrustStoreFile()));
                    if (StringUtils.isNotEmpty(ldapSslProperties.getTrustStorePassword())) {
                        String decryptedPassword = ldapSslProperties.getTrustStorePassword();
                        credentialConfig.setTrustStorePassword(decryptedPassword);
                        sslConfig.setTrustManagers(getTrustManagers(ldapSslProperties.getTrustStoreFile(), decryptedPassword));
                    }
                    if (StringUtils.isNotEmpty(ldapSslProperties.getTrustStoreType())) {
                        credentialConfig.setTrustStoreType(ldapSslProperties.getTrustStoreType());
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error in configuring LDAP SSL connection", e);
            }
        }
        sslConfig.setCredentialConfig(credentialConfig);

        // LDAP Connection Configuration
        ConnectionConfig connectionConfig = ConnectionConfig.builder()
                        .url(configData.get(AuthTypeConfigData.LDAP_URL.getKey()))
                        .connectTimeout(Duration.ofMillis(3000))
                        .useStartTLS(!useSSL && Boolean.valueOf(configData.getOrDefault(AuthTypeConfigData.USE_START_TLS.getKey(), "false")))
                        .connectionInitializers(new BindConnectionInitializer(
                                String.format("%s@%s", configData.get(AuthTypeConfigData.USERNAME.getKey()), configData.get(AuthTypeConfigData.LDAP_DOMAIN.getKey())),
                                new Credential(configData.get(AuthTypeConfigData.PASSWORD.getKey()))))
                        .sslConfig(sslConfig)
                        .build();

        // get authentication Handler
        SimpleBindAuthenticationHandler authHandler = new SimpleBindAuthenticationHandler(new DefaultConnectionFactory(connectionConfig));

        // use a search dn resolver
        SearchDnResolver dnResolver = SearchDnResolver.builder()
                .factory(new DefaultConnectionFactory(connectionConfig))
                .dn(configData.get(AuthTypeConfigData.BASE_DN.getKey()))
                .filter(String.format("(%s={user})", authTypeDetails.getUserAttrMap().get(AuthTypeUserAttribute.USERNAME.getKey())))
                .subtreeSearch(true)
                .build();

        // initialize the LDAP Authenticator
        Authenticator authenticator = new Authenticator(dnResolver, authHandler);

        // authentication response handlers :: set authenticator response
        // handlers
        AuthenticationResponseHandler authResponseHandler = new ActiveDirectoryAuthenticationResponseHandler();
        authenticator.setResponseHandlers(authResponseHandler);

        // search entry resolver :: set authenticator entry resolver
        SearchEntryResolver entryResolver = new SearchEntryResolver();
        entryResolver.setBaseDn(configData.get(AuthTypeConfigData.BASE_DN.getKey()));
        entryResolver.setUserFilter(configData.get(AuthTypeConfigData.USER_PRINCIPAL_FILTER.getKey()));
        entryResolver.setSubtreeSearch(true);
        authenticator.setEntryResolver(entryResolver);
        authenticator.setReturnAttributes(ReturnAttributes.ALL_USER.value());

        return new LdapAuthenticationHandler(authTypeDetails.getConfigData().getOrDefault("name", "LDAP_" + order),
                servicesManager, factory, order, authenticator, strategy);
    }

    private boolean isSSL(String ldapURL) {
        return ldapURL.toLowerCase().startsWith("ldaps://");
    }

    private TrustManager[] getTrustManagers(String storeFile, String password)
            throws IOException, GeneralSecurityException {
        KeyStore keyStore = KeyStore.getInstance(ldapSslProperties.getTrustStoreType());
        try (FileInputStream fin = new FileInputStream(storeFile)) {
            keyStore.load(fin, password.toCharArray());
        }
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        return trustManagerFactory.getTrustManagers();
    }

}
