package com.nextlabs.authentication.enums;

/**
 * Enum for auth type config data keys.
 *
 * @author Sachindra Dasun
 */
public enum AuthTypeConfigData {

    LDAP_URL("ldapUrl"),
    USE_START_TLS("useStartTLS"),
    BASE_DN("baseDn"),
    LDAP_DOMAIN("ldapDomain"),
    USER_PRINCIPAL_FILTER("userPrincipalFilter"),
    USERNAME("username"),
    PASSWORD("password");

    private String key;

    AuthTypeConfigData(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
