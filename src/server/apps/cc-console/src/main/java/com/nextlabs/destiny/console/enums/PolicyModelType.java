/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 14, 2016
 *
 */
package com.nextlabs.destiny.console.enums;

import io.swagger.annotations.ApiModelProperty;

/**
 * Policy model type enum
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public enum PolicyModelType {

    @ApiModelProperty(value = "SUBJECT stands for policy subject.")
    SUBJECT("policy.model.subject.type"),
    @ApiModelProperty(value = "RESOURCE stands for policy resource.")
    RESOURCE("policy.model.resource.type"),
    @ApiModelProperty(value = "DA_SUBJECT stands for delegation policy subject.")
    DA_SUBJECT("policy.model.da.subject.type"),
    @ApiModelProperty(value = "DA_RESOURCE stands for delegation policy resource.")
    DA_RESOURCE("policy.model.da.resource.type");

    private String label;

    private PolicyModelType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static PolicyModelType get(String name) {
        for (PolicyModelType type : PolicyModelType.values()) {
            if (type.name().equalsIgnoreCase(name))
                return type;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
