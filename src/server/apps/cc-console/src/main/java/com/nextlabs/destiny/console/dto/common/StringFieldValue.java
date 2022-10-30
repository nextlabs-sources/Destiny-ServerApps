/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Dec 21, 2015
 *
 */
package com.nextlabs.destiny.console.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;

/**
 *
 * Search Criteria string field value
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@ApiModel(description ="String value to search for", parent = FieldValue.class, value="String")
@JsonIgnoreProperties(ignoreUnknown = true)
public class StringFieldValue extends FieldValue {

    private Object value;

    /**
     * Default constructor
     */
    public StringFieldValue() {
        super();
    }

    /**
     *  constructor
     * 
     * @param value
     */
    public StringFieldValue(Object value) {
        super();
        this.value = value;
    }


    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("StringFieldValue [value=%s]", value);
    }

}
