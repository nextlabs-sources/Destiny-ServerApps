/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 25, 2016
 *
 */
package com.nextlabs.destiny.console.enums;

/**
 *
 * Development entity category type
 *
 * @author Amila Silva
 * @since 8.0
 */
public enum DevEntityType {

    FOLDER("FO"),
    POLICY("PO"),
    XACML_POLICY("XP"),
    COMPONENT("CO"),
    DELEGATION_POLICY("DP"),
    DELEGATION_COMPONENT("DC"),
    LOCATION("LC");

    private String key;

    private DevEntityType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return this.key;
    }

    public static DevEntityType getByKey(String key) {
        for (DevEntityType type : DevEntityType.values()) {
            if (type.getKey().equalsIgnoreCase(key))
                return type;
        }
        return null;
    }

}
