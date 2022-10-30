/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 5, 2015
 *
 */
package com.nextlabs.destiny.console.enums;

/**
 *
 * Tag type enums
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public enum TagType {

    POLICY_TAG, POLICY_MODEL_TAG, COMPONENT_TAG, FOLDER_TAG;

    public static TagType getType(String type) {
        for (TagType tagType : TagType.values()) {
            if (tagType.name().equals(type)) {
                return tagType;
            }
        }
        return null;
    }
}
