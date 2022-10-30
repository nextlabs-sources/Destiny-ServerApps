package com.nextlabs.destiny.console.dto.authentication;

public abstract class DelegatedProvider {

    protected static final String KEY_ENABLED = "enabled";
    protected static final String KEY_CLIENT_NAME = "client-name";

    protected DelegatedProvider() {
        super();
    }
}
