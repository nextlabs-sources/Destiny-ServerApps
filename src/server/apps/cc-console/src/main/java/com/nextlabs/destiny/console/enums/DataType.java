/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Dec 2, 2015
 *
 */
package com.nextlabs.destiny.console.enums;

import io.swagger.annotations.ApiModelProperty;

/**
 *
 * Enum to maintain the data types
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public enum DataType {

    @ApiModelProperty(value = "STRING stands for alphanumeric value.")
    STRING("data.type.string"),
    @ApiModelProperty(value = "NUMBER stands for numeric value.")
    NUMBER("data.type.number"),
    @ApiModelProperty(value = "DATE stands for datetime value.")
    DATE("data.type.date"),
//    @ApiModelProperty(value = "BOOLEAN stands for true/false value.")
//    BOOLEAN("data.type.boolean"),
//    @ApiModelProperty(value = "COLLECTION stands for array values.")
//    COLLECTION("data.type.collection"),
    @ApiModelProperty(value = "MULTIVAL stands for array values.")
    MULTIVAL("data.type.multival");

    public static final String ATTR_TYPE_ST = "ST";
    public static final String ATTR_TYPE_CS = "CS";
    public static final String ATTR_TYPE_SA = "SA";
    public static final String ATTR_TYPE_NM = "NM";
    public static final String ATTR_TYPE_DT = "DT";
    public static final String ATTR_TYPE_NA = "NA";
    public static final String ATTR_TYPE_LS = "LS";

    private String key;

    private DataType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static DataType get(String name) {
        for (DataType dataType : DataType.values()) {
            if (dataType.name().equalsIgnoreCase(name))
                return dataType;
        }
        return null;
    }

    public static DataType fromAttrType(String attrType) {
        if (ATTR_TYPE_NM.equals(attrType)) {
            return DataType.NUMBER;
        } else if (ATTR_TYPE_DT.equals(attrType)) {
            return DataType.DATE;
        } else if (ATTR_TYPE_SA.equals(attrType)
                || ATTR_TYPE_NA.equalsIgnoreCase(attrType)) {
            return DataType.MULTIVAL;
        } else {
            return DataType.STRING;
        }
    }
}
