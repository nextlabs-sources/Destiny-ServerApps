package com.nextlabs.destiny.console.dto.authentication;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.nextlabs.destiny.console.config.ReversibleTextEncryptor.*;
import static com.nextlabs.destiny.console.dto.authentication.AuthHandlerDetail.*;

/**
 * This class contains implementation of mapping from collected Azure configuration to PAC4J configuration
 * Refer to https://apereo.github.io/cas/5.3.x/installation/Configuration-Properties-Common.html#delegated-authentication-settings
 *
 * @author schok
 * @since 9.0
 */
public final class Azure
    extends Pac4jOidc {

    // In CAS 6.2, each Idp has it's own configuration key
    protected static final String AZURE_CONFIGURATION_KEY = "cas.authn.pac4j.oidc[%d].azure.%s";

    // Azure specific configuration key
    private static final String KEY_AZURE_TENANT = "tenant";

    // Fixed value for pac4j Azure configuration
    private static final String CALLBACK_URL_TYPE = "PATH_PARAMETER";
    private static final String TOKEN_EXPIRATION_ADVANCE = "0";
    private static final String MAX_CLOCK_SKEW = "60";
    private static final String PREFERRED_JWS_ALGORITHM = "RS256";
    private static final String SCOPE = "openid profile";
    private static final String RESPONSE_MODE = "query";
    private static final String RESPONSE_TYPE = "code";
    private static final String USER_PRINCIPAL_NAME = "unique_name";
    private static final String DEFAULT_TIMEOUT = "PT5S";

    // Microsoft Azure OpenID configuration discovery URI
    private static final String DISCOVERY_URI_TEMPLATE = "%s/%s/v2.0/.well-known/openid-configuration";
    // Logout URI
    private static final String LOGOUT_URI_TEMPLATE = "%s/%s/oauth2/logout?post_logout_redirect_uri=${server.name}/cas/logout";

    /**
     * Populate authentication handler information collected from UI to system configuration, in this case is PAC4J
     *
     * @param index PAC4J configuration index. Starts from 0
     * @param authenticationConfigMapping Authentication handler configuration
     * @param userAttributeMapping User attribute mapping
     * @return Mapped PAC4J configuration from authentication handler details
     */
    @Override
    public Map<String, String> getSystemConfigMap(int index, Map<String, String> authenticationConfigMapping, String userAttributeMapping) {
        // Use LinkedHashMap to maintain data input order
        Map<String, String> configMap = new LinkedHashMap<>();

        if(authenticationConfigMapping != null) {
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_ORDER), Integer.toString(index));
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_ENABLED), Boolean.TRUE.toString());
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_CLIENT_NAME), Pac4jOidcType.AZURE.toString().toLowerCase());
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_AZURE_TENANT), authenticationConfigMapping.get(TENANT_ID));
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_DISCOVERY_URI), String.format(DISCOVERY_URI_TEMPLATE, trimSlash(authenticationConfigMapping.get(AUTHORITY_URI)),
                    authenticationConfigMapping.get(TENANT_ID)));
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_LOGOUT_URL), String.format(LOGOUT_URI_TEMPLATE, trimSlash(authenticationConfigMapping.get(AUTHORITY_URI)),
                    authenticationConfigMapping.get(TENANT_ID)));
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_MAX_CLOCK_SKEW), MAX_CLOCK_SKEW);
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_PREFERRED_JWS_ALGORITHM), PREFERRED_JWS_ALGORITHM);
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_USE_NONCE), Boolean.TRUE.toString());
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_CALLBACK_URL_TYPE), CALLBACK_URL_TYPE);
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_SCOPE), SCOPE);
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_RESPONSE_MODE), RESPONSE_MODE);
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_RESPONSE_TYPE), RESPONSE_TYPE);
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_PRINCIPAL_ATTRIBUTE_ID), USER_PRINCIPAL_NAME);
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_APPLICATION_ID), authenticationConfigMapping.get(APPLICATION_ID));
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_APPLICATION_SECRET), ENCRYPTED_VALUE_PREFIX + authenticationConfigMapping.get(APPLICATION_KEY));
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_AUTO_REDIRECT), Boolean.FALSE.toString());
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_READ_TIMEOUT), DEFAULT_TIMEOUT);
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_CONNECT_TIMEOUT), DEFAULT_TIMEOUT);
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_EXPIRE_SESSION_WITH_TOKEN), Boolean.FALSE.toString());
            configMap.put(String.format(AZURE_CONFIGURATION_KEY, index, KEY_TOKEN_EXPIRATION_ADVANCE), TOKEN_EXPIRATION_ADVANCE);
        }

        return configMap;
    }
}
