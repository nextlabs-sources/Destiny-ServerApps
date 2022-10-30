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
 * Single value field DTO for all forms
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class SinglevalueFieldDTO implements Serializable {

    private static final long serialVersionUID = -2508445962470767671L;

    @ApiModelProperty(value = "Name of the search field.", example = "status")
    private String name;

    @ApiModelProperty(value = "Label of the search field displayed on UI.", example = "Status")
    private String label;

    public SinglevalueFieldDTO() {

    }

    private SinglevalueFieldDTO(String name, String label) {
        this.name = name;
        this.label = label;
    }

    /**
     * Create SinglevalueFieldDTO
     * 
     * @param name
     *            field name
     * @param label
     *            label to display
     * @return {@link SinglevalueFieldDTO}
     */
    public static SinglevalueFieldDTO create(String name, String label) {
        return new SinglevalueFieldDTO(name, label);
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

}
