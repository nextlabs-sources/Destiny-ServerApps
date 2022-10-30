/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 12, 2015
 *
 */
package com.nextlabs.destiny.console.enums;

/**
 *
 * Enum to maintain policy status
 * 
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public enum PolicyStatus {
    
    DRAFT("policy.mgmt.status.draft"),
    SAVED("policy.mgmt.status.saved"),
    APPROVED("policy.mgmt.status.approved"),
    SUBMITTED("policy.mgmt.status.submitted"),
    DEPLOYED("policy.mgmt.status.deployed"),
    DE_ACTIVATED("policy.mgmt.status.deactivated"),
	OBSOLETE("policy.mgmt.status.obsolete");
    
    private String key;

    private PolicyStatus(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static PolicyStatus get(String name) {
        for (PolicyStatus status : PolicyStatus.values()) {
            if (status.name().equalsIgnoreCase(name))
                return status;
        }
        return null;
    }

}
