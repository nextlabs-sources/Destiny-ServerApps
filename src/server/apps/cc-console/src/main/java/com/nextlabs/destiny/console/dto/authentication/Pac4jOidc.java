package com.nextlabs.destiny.console.dto.authentication;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of mapping from UI definition to PAC4J's OpenID Connect implementation
 * This class contains the pac4j configuration key template which mapped from-to UI object
 * Refer to https://apereo.github.io/cas/5.3.x/installation/Configuration-Properties.html#openid-connect-1
 *
 * @author schok
 * @since 9.0
 */
public abstract class Pac4jOidc
    implements IdProvider {

    protected static final String CONFIGURATION_KEY = "cas.authn.pac4j.oidc[%d].%s";

    // These are mandatory configuration fields expected by PAC4J
    protected static final String KEY_ORDER = "order";
    protected static final String KEY_ENABLED = "enabled";
    protected static final String KEY_CALLBACK_URL_TYPE = "callback-url-type";
    protected static final String KEY_DISCOVERY_URI = "discovery-uri";
    protected static final String KEY_LOGOUT_URL = "logout-url";
    protected static final String KEY_MAX_CLOCK_SKEW = "max-clock-skew";
    protected static final String KEY_SCOPE = "scope";
    protected static final String KEY_USE_NONCE = "use-nonce";
    protected static final String KEY_PRINCIPAL_ATTRIBUTE_ID = "principal-attribute-id";
    protected static final String KEY_RESPONSE_MODE = "response-mode";
    protected static final String KEY_RESPONSE_TYPE = "response-type";
    protected static final String KEY_APPLICATION_ID = "id";
    protected static final String KEY_APPLICATION_SECRET = "secret";
    protected static final String KEY_AUTO_REDIRECT = "auto-redirect";
    protected static final String KEY_READ_TIMEOUT = "read-timeout";
    protected static final String KEY_CONNECT_TIMEOUT = "connect-timeout";
    protected static final String KEY_EXPIRE_SESSION_WITH_TOKEN = "expire-session-with-token";
    protected static final String KEY_TOKEN_EXPIRATION_ADVANCE = "token-expiration-advance";

    // These are optional configuration fields expected by PAC4J
    protected static final String KEY_CLIENT_NAME = "client-name";
    protected static final String KEY_PREFERRED_JWS_ALGORITHM = "preferred-jws-algorithm";

    protected Map<String, String> configurations = new LinkedHashMap<>();

    /**
     * Trim the last slash value from URI
     * @param uri URI to be trimmed
     * @return uri without slash ended
     */
    protected String trimSlash(String uri) {
        if(uri != null
                && uri.endsWith("/")) {
            return trimSlash(uri.substring(0, uri.length()-1));
        }

        return uri;
    }
}
