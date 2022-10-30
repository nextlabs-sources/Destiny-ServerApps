/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Dec 18, 2015
 *
 */
package com.nextlabs.destiny.console.dto.common;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * Free text search
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@ApiModel(description ="Text value to search for", parent = FieldValue.class, value="Text")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TextSearchValue extends FieldValue {

    private String value;
    private String[] fields;
    
    @ApiModelProperty(value = "Text string to be searched in policy")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return String.format("TextSearchValue [value=%s, fields=%s]", value,
                Arrays.toString(fields));
    }

}
