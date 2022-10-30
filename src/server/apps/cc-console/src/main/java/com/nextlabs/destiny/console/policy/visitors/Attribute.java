/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 1, 2016
 *
 */
package com.nextlabs.destiny.console.policy.visitors;

import io.swagger.annotations.ApiModelProperty;

/**
 * Attribute descriptor
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class Attribute {

    @ApiModelProperty(value = "Short name of the attribute.", required = true, example = "department")
    private String lhs;

    @ApiModelProperty(value = "The arithmetic comparator value for evaluation engine.\n"
            + "Possible values by data type of attribute:\n" +
            "DATE: <, >=\n" +
            "STRING: =, !=\n" +
            "NUMBER: =, !=, <, <=, >, >=\n" +
            "MULTIVAL: includes, equals_unordered, =, !=",
            allowableValues = "=, !=, <, <=, >, >=, includes, equals_unordered",
            example = "!=", required = true)
    private String operator;

    @ApiModelProperty(value = "The value of the condition. The value could be a constant or variable.\n" +
            "It could also be a list of values.\n" +
            "A variable value in a list is enclosed with ${}.\n" +
            "A list of values are comma separated and enclosed with [].\n" +
            "The values of a list are treated as strings and is surrounded by \"\".",
            example = "user.principalname, [\"Williams\",\"Adams\"], [\\\"${application.systemReference}\\\", \\\"${application.displayName}\\\", \\\"a constant\\\"]",
            required = true)
    private String rhs;

    public Attribute() {
    }

    public Attribute(String lhs, String operator, String rhs) {
        this.lhs = lhs;
        this.operator = operator;
        this.rhs = rhs;
    }

    public String getLhs() {
        return lhs;
    }

    public void setLhs(String lhs) {
        this.lhs = lhs;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getRhs() {
        return rhs;
    }

    public void setRhs(String rhs) {
        this.rhs = rhs;
    }

}
