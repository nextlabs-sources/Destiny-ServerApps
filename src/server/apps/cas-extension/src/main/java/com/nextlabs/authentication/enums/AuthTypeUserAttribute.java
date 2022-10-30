package com.nextlabs.authentication.enums;

public enum AuthTypeUserAttribute {
    USERNAME("username"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    EMAIL("email");

    private String key;

    AuthTypeUserAttribute(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
