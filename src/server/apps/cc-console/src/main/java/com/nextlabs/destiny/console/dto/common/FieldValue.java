/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Dec 21, 2015
 *
 */
package com.nextlabs.destiny.console.dto.common;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * Search Field value base class
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@ApiModel(value = "FieldValue", subTypes = {StringFieldValue.class, DateFieldValue.class, TextSearchValue.class}, discriminator = "type")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @Type(name = "String", value = StringFieldValue.class),
        @Type(name = "Date", value = DateFieldValue.class),
        @Type(name = "Text", value = TextSearchValue.class),
})
public abstract class FieldValue {

    private String type;

    @ApiModelProperty(value = "The type of FieldValue.", allowableValues = "String, Date, Text", example = "String")
    public String getType() {
        return type;
    }

}
