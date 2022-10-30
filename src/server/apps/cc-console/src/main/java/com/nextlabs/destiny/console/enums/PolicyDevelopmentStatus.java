/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 25, 2015
 *
 */
package com.nextlabs.destiny.console.enums;

import com.bluejungle.pf.destiny.lifecycle.DevelopmentStatus;

/**
 *
 * enum to hold Policy Developement Status
 *
 * @author Amila Silva
 * @since   8.0
 *
 */
public enum PolicyDevelopmentStatus {
    
    NEW("NE", DevelopmentStatus.NEW, "policy.dev.status.ne"), 
    EMPTY("EM", DevelopmentStatus.EMPTY, "policy.dev.status.em"), 
    DRAFT("DR", DevelopmentStatus.DRAFT, "policy.dev.status.dr"), 
    APPROVED("AP", DevelopmentStatus.APPROVED, "policy.dev.status.ap"),
    DELETED("DE", DevelopmentStatus.DELETED, "policy.dev.status.de"),
    OBSOLETE("OB", DevelopmentStatus.OBSOLETE, "policy.dev.status.ob"),
    ILLEGAL("##", "policy.dev.status.il");
    
    
    private String key;
    private DevelopmentStatus devValue;
    private String label;
    
    /**
     * Private constructor
     * 
     * @param label
     * @param key
     */
    private PolicyDevelopmentStatus(String key, String label) {
        this.key = key;
        this.label = label;
    }
    
    /**
     * Private constructor
     * 
     * @param label
     * @param key
     */
    private PolicyDevelopmentStatus(String key, DevelopmentStatus devValue, String label) {
        this.key = key;
        this.devValue = devValue;
        this.label = label;
    }
    
    public static PolicyDevelopmentStatus get(String name) {
        for (PolicyDevelopmentStatus status : PolicyDevelopmentStatus.values()) {
            if (status.name().equalsIgnoreCase(name))
                return status;
        }
        return null;
    }
    
    public static PolicyDevelopmentStatus getByKey(String key) {
        for (PolicyDevelopmentStatus status : PolicyDevelopmentStatus.values()) {
            if (status.getKey().equalsIgnoreCase(key))
                return status;
        }
        return null;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public DevelopmentStatus getDevValue() {
        return devValue;
    }
    
    @Override
    public String toString() {
        return this.label;
    }
    
}
