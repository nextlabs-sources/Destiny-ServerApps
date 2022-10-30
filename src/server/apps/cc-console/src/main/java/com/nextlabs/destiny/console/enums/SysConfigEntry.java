package com.nextlabs.destiny.console.enums;

public enum SysConfigEntry {

    KEY_STORE_PASSWORD(SysConfigScope.APPLICATION, "key.store.password"),
    TRUST_STORE_PASSWORD(SysConfigScope.APPLICATION, "trust.store.password");

    private SysConfigScope scope;

    private String key;

    SysConfigEntry(SysConfigScope scope, String key) {
        this.scope = scope;
        this.key = key;
    }

    public SysConfigScope getScope() {
        return scope;
    }

    public String getKey() {
        return key;
    }
}
