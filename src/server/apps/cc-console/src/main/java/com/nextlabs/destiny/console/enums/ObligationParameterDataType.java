/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 14, 2016
 *
 */
package com.nextlabs.destiny.console.enums;

/**
 *
 * Obligation parameter data type enum.
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public enum ObligationParameterDataType {

    TEXT_SINGLE_ROW("oblig.param.data.type.text.single.row"), 
    TEXT_MULTIPLE_ROW("oblig.param.data.type.text.multiple.row"), 
    LIST("oblig.param.data.type.list");

    private String label;

    private ObligationParameterDataType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ObligationParameterDataType get(String name) {
        for (ObligationParameterDataType type : ObligationParameterDataType
                .values()) {
            if (type.name().equalsIgnoreCase(name))
                return type;
        }
        return null;
    }
}
