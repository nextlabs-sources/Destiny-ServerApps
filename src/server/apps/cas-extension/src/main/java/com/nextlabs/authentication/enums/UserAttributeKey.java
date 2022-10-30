package com.nextlabs.authentication.enums;

/**
 * User attribute key types.
 *
 * @author Sachindra Dasun
 */
public enum UserAttributeKey {
    DISPLAY_NAME("displayName"),
    EMAIL("email"),
    FIRST_NAME("firstName"),
    NEXTLABS_CC_USER_ID("nextlabsCcUserId"),
    LAST_NAME("lastName"),
    NEXTLABS_CC_USER_CATEGORY("nextlabsCcUserCategory"),
    USERNAME("username");

    private String key;

    UserAttributeKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
