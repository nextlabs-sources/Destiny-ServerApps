/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 11, 2016
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nextlabs.destiny.console.enums.Operator;

/**
 * Holder for assigned components in given policy
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class PolicyComponent implements Serializable {

    private static final long serialVersionUID = -6931552668710433696L;
    
    private Operator operator;
    private List<ComponentDTO> components;

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public List<ComponentDTO> getComponents() {
        if (components == null) {
            components = new ArrayList<>();
        }
        return components;
    }

    public void setComponents(List<ComponentDTO> components) {
        this.components = components;
    }

}
