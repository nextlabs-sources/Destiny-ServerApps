/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 13, 2015
 *
 */
package com.nextlabs.destiny.console.dto.common;

import io.swagger.annotations.ApiModelProperty;

/**
 *
 * DTO for Search criteria sort field
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class SortField {

    public static final String ASC = "ASC";
    public static final String DESC = "DESC";

    @ApiModelProperty(value = "Key of the field to be sorted.", example = "lastUpdatedDate", notes = "Allowed values are same as those for criteria.fields.field")
    private String field;

    @ApiModelProperty(value = "Sort order.", allowableValues = "ASC, DESC")
    private String order;

    public SortField() {

    }

    public SortField(String field, String order) {
        this.field = field;
        this.order = order;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return String.format("SortField [field=%s, order=%s]", field, order);
    }

}
