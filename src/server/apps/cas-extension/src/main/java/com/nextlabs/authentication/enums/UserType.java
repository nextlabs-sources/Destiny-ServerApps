package com.nextlabs.authentication.enums;

/**
 * Enum for application user type.
 *
 * @author Sachindra Dasun
 */
public enum UserType {

    IMPORTED("imported"), INTERNAL("internal");

    private String type;

    UserType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
