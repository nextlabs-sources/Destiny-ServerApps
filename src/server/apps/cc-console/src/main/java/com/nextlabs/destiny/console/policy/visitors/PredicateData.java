/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 1, 2016
 *
 */
package com.nextlabs.destiny.console.policy.visitors;

import java.util.ArrayList;
import java.util.List;

import com.nextlabs.destiny.console.enums.Operator;
import io.swagger.annotations.ApiModelProperty;

/**
 * Predicate data model helps to fill-in the predicate details using visitor
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class PredicateData {

    @ApiModelProperty(hidden = true)
    private Operator operator;

    @ApiModelProperty(value = "List of IDs of member components.", example = "[1008, 1009]")
    private List<Long> referenceIds;
    private List<Attribute> attributes;

    @ApiModelProperty(value = "List of short names of the actions of the component. Applicable only for Action component group.",
            example = "[\"PRINT\", \"VIEW\"]")
    private List<String> actions;

    public void addReference(Long refereceId) {
        getReferenceIds().add(refereceId);
    }

    public void addAttribute(Attribute attribute) {
        getAttributes().add(attribute);
    }

    public void addAction(String action) {
        getActions().add(action);
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public List<Long> getReferenceIds() {
        if (referenceIds == null) {
            referenceIds = new ArrayList<>();
        }
        return referenceIds;
    }

    public List<Attribute> getAttributes() {
        if (attributes == null) {
            attributes = new ArrayList<>();
        }
        return attributes;
    }

    public List<String> getActions() {
        if (actions == null) {
            actions = new ArrayList<>();
        }
        return actions;
    }

}
