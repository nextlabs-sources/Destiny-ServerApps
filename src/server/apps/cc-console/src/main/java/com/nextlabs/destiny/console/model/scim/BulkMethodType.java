/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 14, 2020
 *
 */
package com.nextlabs.destiny.console.model.scim;


/**
 * This enumeration defines the set of possible method types that may be used for SCIM 2 bulk
 * operations.
 */
public enum BulkMethodType {

    POST("post"), PUT("put"), PATCH("patch"), DELETE("delete");

    private String type;

    BulkMethodType(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
