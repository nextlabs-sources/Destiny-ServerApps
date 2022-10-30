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
public enum WorkflowRequestLevelStatus {

    PENDING("policy.mgmt.workflow.status.pending"),
    APPROVED("policy.mgmt.workflow.status.approved"),
    REQUESTED_AMENDMENT("policy.mgmt.workflow.status.returned");

    private final String key;

    WorkflowRequestLevelStatus(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return this.key;
    }

    public static WorkflowRequestLevelStatus getByKey(String key) {
        for (WorkflowRequestLevelStatus type : WorkflowRequestLevelStatus.values()) {
            if (type.getKey().equalsIgnoreCase(key))
                return type;
        }
        return null;
    }

}
