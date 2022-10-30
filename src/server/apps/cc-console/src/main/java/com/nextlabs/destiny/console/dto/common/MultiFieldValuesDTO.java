/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 12, 2015
 *
 */
package com.nextlabs.destiny.console.dto.common;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 *
 * Multi value field values DTO for all forms
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class MultiFieldValuesDTO implements Serializable {

    private static final long serialVersionUID = -3736986379911481029L;

    @ApiModelProperty(value = "Name of the search field option.", example = "DRAFT")
    private String name;

    @ApiModelProperty(value = "Label of the search field option displayed on UI.", example = "Draft")
    private String label;

    @ApiModelProperty(value = "The order of display on UI.", example = "1")
    private String order;

    public MultiFieldValuesDTO() {

    }

    private MultiFieldValuesDTO(String name, String label) {
        this.name = name;
        this.label = label;
    }
    
    private MultiFieldValuesDTO(String name, String label, String order) {
        this.name = name;
        this.label = label;
        this.order = order;
    }

    /**
     * Create MultiFieldValuesDTO
     * 
     * @param name
     * @param label
     * @return {@link MultiFieldValuesDTO}
     */
    public static MultiFieldValuesDTO create(String name, String label) {
        return new MultiFieldValuesDTO(name, label);
    }
    
    public static MultiFieldValuesDTO create(String name, String label, String order) {
        return new MultiFieldValuesDTO(name, label, order);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

}
