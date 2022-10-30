package com.nextlabs.destiny.console.dto.authentication;

/**
 * Implementation of mapping from UI definition to PAC4J's SAML 2.0 implementation
 * This class contains the pac4j configuration key template which mapped from-to UI object
 * Refer to https://apereo.github.io/cas/5.3.x/installation/Configuration-Properties.html#saml2
 *
 * @author Chok Shah Neng
 * @since 2020.09
 */
public abstract class Pac4jSaml2
        extends DelegatedProvider
        implements IdProvider {

    protected static final String CONFIGURATION_KEY = "cas.authn.pac4j.saml[%d].%s";
    protected static final String REQUESTED_ATTRIBUTE_KEY = "cas.authn.pac4j.saml[%d].requested-attributes[%d].%s";
    protected static final String MAPPED_ATTRIBUTE_KEY = "cas.authn.pac4j.saml[%d].mapped-attributes[%d].%s";

    // These are mandatory configuration fields expected by PAC4J
    protected static final String KEY_KEYSTORE_PASSWORD = "keystore-password";
    protected static final String KEY_PRIVATE_KEY_PASSWORD = "private-key-password";
    protected static final String KEY_KEYSTORE_PATH = "keystore-path";
    protected static final String KEY_KEYSTORE_ALIAS = "keystore-alias";
    protected static final String KEY_SP_ENTITY_ID = "service-provider-entity-id";
    protected static final String KEY_SP_METADATA_PATH = "service-provider-metadata-path";
    protected static final String KEY_CERTIFICATE_NAME_TO_APPEND = "key-certificate-name-to-append";
    protected static final String KEY_MAX_AUTHENTICATION_LIFETIME = "maximum-authentication-lifetime";
    protected static final String KEY_DESTINATION_BINDING = "destination-binding";
    protected static final String KEY_IDP_METADATA_PATH = "identity-provider-metadata-path";
    protected static final String KEY_AUTHENTICATION_CONTEXT_CLASS_REF = "authn-context-class-ref[%d]";
    protected static final String KEY_AUTHENTICATION_CONTEXT_COMPARISON_TYPE = "authn-context-comparison-type";
    protected static final String KEY_NAME_ID_POLICY_FORMAT = "name-id-policy-format";
    protected static final String KEY_FORCE_AUTHENTICATION = "force-auth";
    protected static final String KEY_PASSIVE = "passive";
    protected static final String KEY_WANTS_ASSERTIONS_SIGNED = "wants-assertions-signed";
    protected static final String KEY_WANTS_RESPONSE_SIGNED = "wants-response-signed";
    protected static final String KEY_ALL_SIGNATURE_VALIDATION_DISABLED = "all-signature-validation-disabled";
    protected static final String KEY_SIGN_SERVICE_PROVIDER_METADATA = "sign-service-provider-metadata";
    protected static final String KEY_PRINCIPAL_ID_ATTRIBUTE = "principal-id-attribute";
    protected static final String KEY_USE_NAME_QUALIFIER = "use-name-qualifier";
    protected static final String KEY_ATTRIBUTE_CONSUMING_SERVICE_INDEX = "attribute-consuming-service-index";
    protected static final String KEY_ASSERTION_CONSUMER_SERVICE_INDEX = "assertion-consumer-service-index";
    protected static final String KEY_PROVIDER_NAME = "provider-name";
    protected static final String KEY_NAME_ID_POLICY_ALLOW_CREATE = "name-id-policy-allow-create";
    protected static final String KEY_MESSAGE_STORE_FACTORY = "message-store-factory";
    protected static final String KEY_SIGN_AUTHN_REQUEST = "sign-authn-request";
    protected static final String KEY_SIGN_SERVICE_PROVIDER_LOGOUT_REQUEST = "sign-service-provider-logout-request";
    protected static final String KEY_SIGNATURE_ALGORITHMS = "signature-algorithms[%d]";
    protected static final String KEY_SIGNATURE_REFERENCE_DIGEST_METHODS = "signature-reference-digest-methods[%d]";
    protected static final String KEY_SIGNATURE_CANONICALIZATION_ALGORITHM = "signature-canonicalization-algorithm";

    // These are requested attribute fields
    protected static final String KEY_ATTR_NAME = "name";
    protected static final String KEY_ATTR_FRIENDLY_NAME = "friendly-name";
    protected static final String KEY_ATTR_NAME_FORMAT = "name-format";
    protected static final String KEY_ATTR_REQUIRED = "required";

    // These are mapped attribute fields
    protected static final String KEY_MAPPED_NAME = "name";
    protected static final String KEY_MAPPED_TO = "mapped-to";
}
