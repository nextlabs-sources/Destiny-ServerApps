/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 5, 2016
 *
 */
package com.nextlabs.destiny.console.enums;

/**
 *
 * Saved search types 
 *
 * @author Amila Silva
 * @since   8.0
 *
 */
public enum SavedSearchType {

    POLICY, COMPONENT, POLICY_MODEL_RESOURCE, POLICY_MODEL_SUBJECT, LOCATION, PROPERTY, ENROLLED_DATA;
    
    public static SavedSearchType get(String name) {
        for (SavedSearchType type : SavedSearchType.values()) {
            if (type.name().equalsIgnoreCase(name))
                return type;
        }
        return null;
    }

}
