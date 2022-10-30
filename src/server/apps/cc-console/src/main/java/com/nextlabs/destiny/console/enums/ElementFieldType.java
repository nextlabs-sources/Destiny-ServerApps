/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide. Created on Mar 17, 2020
 *
 */
package com.nextlabs.destiny.console.enums;

/**
 * Enum to maintain the data types. An extension of com.nextlabs.destiny.console.enums.DataType
 */
public enum ElementFieldType {
    STRING("ST", "String", "String"), CS_STRING("CS", "CS-String",
            "String"), STRING_ARRAY("SA", "Multi-String", "String"), NUMBER("NM",
                    "Number",
                    "Number"), NUMBER_ARRAY("NA", "Multi-Long",
                            "NumArray"), DATE("DT", "Date", "Date");

    private String attributeType;
    private String label;
    private String mappingBase;

    private ElementFieldType(String attributeType, String label, String mappingBase) {
        this.attributeType = attributeType;
        this.label = label;
        this.mappingBase = mappingBase;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public String getLabel() {
        return label;
    }

    public String getMappingBase() {
        return mappingBase;
    }

    public static String getLabelFromAttributeType(String attributeType) {
        switch (attributeType) {
            case "ST":
                return STRING.getLabel();
            case "CS":
                return CS_STRING.getLabel();
            case "SA":
                return STRING_ARRAY.getLabel();
            case "NM":
                return NUMBER.getLabel();
            case "NA":
                return NUMBER_ARRAY.getLabel();
            case "DT":
                return DATE.getLabel();
            default:
                return STRING.getLabel();
                
        }
    }

    public static String getAttributeTypeFromLabel(String label) {
        for (ElementFieldType dataType : ElementFieldType.values()) {
            if (dataType.getLabel().equalsIgnoreCase(label))
                return dataType.getAttributeType();
        }
        return null;
    }

    public static ElementFieldType getAttributeFromType(String attributeType) {
        for (ElementFieldType dataType : ElementFieldType.values()) {
            if (dataType.getAttributeType().equalsIgnoreCase(attributeType))
                return dataType;
        }
        return null;
    }

    public static String getMappingBaseFromLabel(String label) {
        for (ElementFieldType dataType : ElementFieldType.values()) {
            if (dataType.getLabel().equalsIgnoreCase(label))
                return dataType.getMappingBase();
        }
        return null;
    }

}
