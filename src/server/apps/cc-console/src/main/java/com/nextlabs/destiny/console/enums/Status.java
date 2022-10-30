/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 9, 2015
 *
 */
package com.nextlabs.destiny.console.enums;

/**
 *
 * Entity Status enum ACTIVE, IN-ACTIVE, DELETED
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public enum Status {

    ACTIVE, IN_ACTIVE, DELETED;

    public static Status get(String status) {
        for (Status s : Status.values()) {
            if (s.name().equals(status)) {
                return s;
            }
        }
        return ACTIVE;
    }
}
