/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 8 Sep 2016
 *
 */
package com.nextlabs.destiny.console.enums;

/**
 *
 *
 *
 * @author aishwarya
 * @since   8.0
 *
 */
public enum AuthHandlerType {

	LDAP("auth.handler.type.ldap"), 
    DB("auth.handler.type.db"),
    OAUTH20("auth.handler.type.oauth2"),
    OIDC("auth.handler.type.oidc"),
    SAML2("auth.handler.type.saml2");

    private String key;

    private AuthHandlerType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static AuthHandlerType get(String name) {
        for (AuthHandlerType type : AuthHandlerType.values()) {
            if (type.name().equalsIgnoreCase(name))
                return type;
        }
        return null;
    }
}
