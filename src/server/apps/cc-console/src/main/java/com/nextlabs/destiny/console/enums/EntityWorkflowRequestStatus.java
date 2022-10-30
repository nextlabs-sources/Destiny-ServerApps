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
 * Workflow Request Level Status
 *
 * @author Mohammed Sainal Shah
 * @since 2020.10
 */
public enum EntityWorkflowRequestStatus {

    PENDING("PE"),
    APPROVED("AP"),
    DEPLOYED("DE"),
    CANCELLED("CA");

    private final String key;

    EntityWorkflowRequestStatus(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return this.key;
    }

    public static EntityWorkflowRequestStatus getByKey(String key) {
        for (EntityWorkflowRequestStatus type : EntityWorkflowRequestStatus.values()) {
            if (type.getKey().equalsIgnoreCase(key))
                return type;
        }
        return null;
    }

}
