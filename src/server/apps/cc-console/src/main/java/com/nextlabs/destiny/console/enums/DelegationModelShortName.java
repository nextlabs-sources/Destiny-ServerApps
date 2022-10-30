/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on May 4, 2016
 *
 */
package com.nextlabs.destiny.console.enums;

/**
 *
 * Enum to hold the delegation models short names
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public enum DelegationModelShortName {

    PS_POLICY,
    PS_COMPONENT,
    PS_POLICY_MODEL,
    POLICY_FOLDER,
    COMPONENT_FOLDER,
    ADMINISTRATOR,
    REPORTER,
    MONITOR,
    DELEGATED_ADMIN,
    DA_USER,
    POLICY_ACCESS_TAGS,
    COMPONENT_ACCESS_TAGS,
    POLICY_MODEL_ACCESS_TAGS,
    REPORTER_ACCESS_TAGS,
    USERS_ROLES_ACCESS_TAGS,
    POLICY_ENF_CONFIG_ACCESS_TAGS,
    MONITOR_ACCESS_TAGS,
    DELGATED_ADMIN_ACCESS_TAGS,
    POLICY_FOLDER_ACCESS_TAGS,
    COMPONENT_FOLDER_ACCESS_TAGS,
    TAG_MANAGEMENT,
    AUTH_MANAGEMENT,
    TOOLS,
    POLICY_VALIDATOR,
    ENROLLMENT_MANAGEMENT,
    SYS_CONFIG,
    LOG_CONFIG,
    SECURE_STORE,
    XACML_POLICY_UPLOADER,
    ENVIRONMENT_CONFIGURATION,
    POLICY_WORKFLOW,
    PDP_PLUGIN,
    POLICY_CONTROLLER,
    ICENET;

    public static DelegationModelShortName get(String name) {
        for (DelegationModelShortName op : DelegationModelShortName.values()) {
            if (op.name().equalsIgnoreCase(name))
                return op;
        }
        return null;
    }

}
