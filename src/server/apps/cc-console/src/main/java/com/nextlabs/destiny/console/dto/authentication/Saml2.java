package com.nextlabs.destiny.console.dto.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.nextlabs.destiny.console.dto.authentication.AuthHandlerDetail.*;

public class Saml2
        extends Pac4jSaml2 {

    public static final String MULTI_VALUE_DELIMITER = "\\s*,\\s*";
    public static final String KEYSTORE_FILE = "saml2-keystore.p12";
    public static final String KEYSTORE_PASSWORD = "${key.store.password}";
    public static final String DEFAULT_KEYSTORE_ALIAS = "saml2";
    public static final String DEFAULT_PROVIDER_NAME = "NextLabs::ControlCenter";
    public static final String DEFAULT_SP_ENTITY_ID = "urn:mace:saml:pac4j.org";
    public static final String DEFAULT_MAX_AUTHENTICATION_LIFETIME = "3600";
    public static final String DEFAULT_CERTIFICATION_NAME_TO_APPEND = "";
    public static final String DEFAULT_DESTINATION_BINDING = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect";
    public static final String DEFAULT_NAME_ID_POLICY_FORMAT = "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified";
    public static final String SP_METADATA_FILE = "sp-metadata.xml";
    public static final String IDP_METADATA_FILE = "idp-metadata.xml";

    /**
     * Populate authentication handler information collected from UI to system configuration, in this case is PAC4J
     *
     * @param index PAC4J configuration index. Starts from 0
     * @param authenticationConfigMapping Authentication handler configuration
     * @param userAttributeMapping User attribute mapping
     * @return Mapped PAC4J configuration from authentication handler details
     */
    @Override
    public Map<String, String> getSystemConfigMap(int index, Map<String, String> authenticationConfigMapping, String userAttributeMapping)
                    throws JsonProcessingException {
        // Use LinkedHashMap to maintain data input order
        Map<String, String> configMap = new LinkedHashMap<>();

        if(authenticationConfigMapping != null) {
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_CLIENT_NAME), "saml2");
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_ENABLED), Boolean.TRUE.toString());
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_KEYSTORE_PATH), String.join("/", "${cc.home}", "server", "certificates", KEYSTORE_FILE));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_KEYSTORE_PASSWORD), KEYSTORE_PASSWORD);
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_PRIVATE_KEY_PASSWORD), KEYSTORE_PASSWORD);
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_KEYSTORE_ALIAS), authenticationConfigMapping.getOrDefault(KEYSTORE_ALIAS, DEFAULT_KEYSTORE_ALIAS));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_IDP_METADATA_PATH), String.join("/", "${cc.home}", "server", "configuration", IDP_METADATA_FILE));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_SP_METADATA_PATH), String.join("/", "${cc.home}", "server", "configuration", SP_METADATA_FILE));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_SP_ENTITY_ID), authenticationConfigMapping.getOrDefault(SP_ENTITY_ID, DEFAULT_SP_ENTITY_ID));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_MAX_AUTHENTICATION_LIFETIME), authenticationConfigMapping.getOrDefault(MAX_AUTHENTICATION_LIFETIME, DEFAULT_MAX_AUTHENTICATION_LIFETIME));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_DESTINATION_BINDING), authenticationConfigMapping.getOrDefault(DESTINATION_BINDING, DEFAULT_DESTINATION_BINDING));
            if(StringUtils.isNotEmpty(authenticationConfigMapping.get(AUTHENTICATION_CONTEXT_CLASS_REFERENCES))) {
                int counter = 0;
                for(String reference : authenticationConfigMapping.get(AUTHENTICATION_CONTEXT_CLASS_REFERENCES).split(MULTI_VALUE_DELIMITER)) {
                    configMap.put(String.format(CONFIGURATION_KEY, index,
                                    String.format(KEY_AUTHENTICATION_CONTEXT_CLASS_REF, counter++)), reference);
                }
            }
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_AUTHENTICATION_CONTEXT_COMPARISON_TYPE), authenticationConfigMapping.get(AUTHENTICATION_CONTEXT_COMPARISON_TYPE));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_FORCE_AUTHENTICATION), authenticationConfigMapping.get(FORCE_AUTHENTICATION));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_PASSIVE), authenticationConfigMapping.get(PASSIVE));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_PRINCIPAL_ID_ATTRIBUTE), authenticationConfigMapping.get(PRINCIPAL_ID_ATTRIBUTE));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_WANTS_ASSERTIONS_SIGNED), authenticationConfigMapping.get(WANTS_ASSERTIONS_SIGNED));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_WANTS_RESPONSE_SIGNED), authenticationConfigMapping.get(WANTS_RESPONSE_SIGNED));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_NAME_ID_POLICY_FORMAT), authenticationConfigMapping.getOrDefault(NAME_ID_POLICY_FORMAT, DEFAULT_NAME_ID_POLICY_FORMAT));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_ALL_SIGNATURE_VALIDATION_DISABLED), authenticationConfigMapping.get(ALL_SIGNATURE_VALIDATION_DISABLED));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_SIGN_SERVICE_PROVIDER_METADATA), authenticationConfigMapping.get(SIGN_SERVICE_PROVIDER_METADATA));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_USE_NAME_QUALIFIER), authenticationConfigMapping.get(USE_NAME_QUALIFIER));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_SIGN_AUTHN_REQUEST), authenticationConfigMapping.get(SIGN_AUTHENTICATION_REQUEST));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_SIGN_SERVICE_PROVIDER_LOGOUT_REQUEST), authenticationConfigMapping.get(SIGN_SERVICE_PROVIDER_LOGOUT_REQUEST));
            if(StringUtils.isNotEmpty(authenticationConfigMapping.get(SIGNATURE_ALGORITHMS))) {
                int counter = 0;
                for(String algorithm : authenticationConfigMapping.get(SIGNATURE_ALGORITHMS).split(MULTI_VALUE_DELIMITER)) {
                    configMap.put(String.format(CONFIGURATION_KEY, index,
                                    String.format(KEY_SIGNATURE_ALGORITHMS, counter++)), algorithm);
                }
            }
            if(StringUtils.isNotEmpty(authenticationConfigMapping.get(SIGNATURE_REFERENCE_DIGEST_METHODS))) {
                int counter = 0;
                for(String method : authenticationConfigMapping.get(SIGNATURE_REFERENCE_DIGEST_METHODS).split(MULTI_VALUE_DELIMITER)) {
                    configMap.put(String.format(CONFIGURATION_KEY, index,
                                    String.format(KEY_SIGNATURE_REFERENCE_DIGEST_METHODS, counter++)), method);
                }
            }
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_SIGNATURE_CANONICALIZATION_ALGORITHM), authenticationConfigMapping.get(SIGNATURE_CANONICALIZATION_ALGORITHM));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_ASSERTION_CONSUMER_SERVICE_INDEX), Integer.toString(0));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_ATTRIBUTE_CONSUMING_SERVICE_INDEX), Integer.toString(1));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_PROVIDER_NAME), authenticationConfigMapping.getOrDefault(PROVIDER_NAME, DEFAULT_PROVIDER_NAME));
            configMap.put(String.format(CONFIGURATION_KEY, index, KEY_NAME_ID_POLICY_ALLOW_CREATE), authenticationConfigMapping.get(NAME_ID_POLICY_ALLOW_CREATE).toUpperCase());

            if(userAttributeMapping != null && !userAttributeMapping.isEmpty()) {
                int attrCounter = 0;
                ObjectMapper mapper = new ObjectMapper();

                for(ComplexUserAttribute userAttribute : mapper.readValue(userAttributeMapping, ComplexUserAttribute[].class)) {
                    configMap.put(String.format(REQUESTED_ATTRIBUTE_KEY, index, attrCounter, KEY_ATTR_NAME), userAttribute.getName());
                    configMap.put(String.format(REQUESTED_ATTRIBUTE_KEY, index, attrCounter, KEY_ATTR_FRIENDLY_NAME), userAttribute.getFriendlyName());
                    configMap.put(String.format(REQUESTED_ATTRIBUTE_KEY, index, attrCounter, KEY_ATTR_NAME_FORMAT), userAttribute.getNameFormat());
                    configMap.put(String.format(REQUESTED_ATTRIBUTE_KEY, index, attrCounter, KEY_ATTR_REQUIRED), Boolean.toString(userAttribute.isRequired()));

                    configMap.put(String.format(MAPPED_ATTRIBUTE_KEY, index, attrCounter, KEY_MAPPED_NAME), userAttribute.getName());
                    configMap.put(String.format(MAPPED_ATTRIBUTE_KEY, index, attrCounter,
                                    KEY_MAPPED_TO), userAttribute.getMappedAs());

                    attrCounter++;
                }
            }
        }

        return configMap;
    }
}
