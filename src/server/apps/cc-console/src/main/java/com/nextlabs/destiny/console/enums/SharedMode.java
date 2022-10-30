/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 13, 2015
 *
 */
package com.nextlabs.destiny.console.enums;

/**
 *
 * Enum to maintain shared modes
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public enum SharedMode {

    PUBLIC("share.mode.public"), ONLY_ME("share.mode.onlyme"), USERS(
            "share.mode.users");

    private String key;

    private SharedMode(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static SharedMode get(String mode) {
        for (SharedMode s : SharedMode.values()) {
            if (s.name().equals(mode)) {
                return s;
            }
        }
        return PUBLIC;
    }

}
