/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 2, 2016
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import static com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper.CONSTANT;
import static com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper.RESOURCE_GROUP;
import static com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper.RESOURCE_TYPE_PREFIX;
import static com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper.SUBJECT_GROUP;
import static com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper.USER_TYPE_PREFIX;
import static com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper.HOST_TYPE_PREFIX;
import static com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper.APPLICATION_TYPE_PREFIX;
import static com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper.VARIABLE_VALUE_PREFIX;
import static com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper.VARIABLE_VALUE_SUFFIX;
import static com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper.MULTI_VALUE_PREFIX;
import static com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper.MULTI_VALUE_SUFFIX;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

/**
 * DTO for Component Conditions
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComponentConditionDTO implements Serializable {

    private static final long serialVersionUID = 2462911693693670554L;
    private static final String MULTIVALUE_SPLIT_PATTERN = "\\s*,\\s*";

    @ApiModelProperty(value = "Short name of the attribute.", required = true, example = "department")
    private String attribute;

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
            "A variable value is enclosed with ${}.\n" +
            "A list of values are comma separated and enclosed with [].\n" +
            "A constant is surrounded by \"\".",
            example = "${user.principalname}, [\"Williams\",\"Adams\"]", required = true)
    private String value;

    @ApiModelProperty(value = "The type of value of condition.\n" +
            "If the condition value is a variable and is attribute of subject or resource, possible values for rhsType are SUBJECT or RESOURCE.\n" +
            "If the condition value is a strong constant, value of rhsType is CONSTANT.",
            allowableValues = "SUBJECT, RESOURCE, CONSTANT")
    private String rhsType;

    public ComponentConditionDTO() {
    }

    public ComponentConditionDTO(String attribute, String operator,
            String value) {
        this.attribute = attribute;
        this.operator = operator;
        value = extractMultiValues(value);
        this.value = value;
        if (value != null &&
                        (value.startsWith(USER_TYPE_PREFIX)
                                        || value.startsWith(HOST_TYPE_PREFIX)
                                        || value.startsWith(APPLICATION_TYPE_PREFIX))) {
            this.rhsType = SUBJECT_GROUP;
        } else if (value != null && value.startsWith(RESOURCE_TYPE_PREFIX)) {
            this.rhsType = RESOURCE_GROUP;
        } else {
            this.rhsType = CONSTANT;
        }
    }

    public ComponentConditionDTO(String attribute, String operator,
            String value, boolean withVariablePrefix) {
        this.attribute = attribute;
        this.operator = operator;
        value = extractMultiValues(value);
        this.value = value;
        if (value != null &&
                        (value.startsWith(USER_TYPE_PREFIX)
                                        || value.startsWith(HOST_TYPE_PREFIX)
                                        || value.startsWith(APPLICATION_TYPE_PREFIX))) {
            this.rhsType = SUBJECT_GROUP;
            if (withVariablePrefix) {
                this.value = VARIABLE_VALUE_PREFIX + value
                        + VARIABLE_VALUE_SUFFIX;
            }
        } else if (value != null && value.startsWith(RESOURCE_TYPE_PREFIX)) {
            this.rhsType = RESOURCE_GROUP;
            if (withVariablePrefix) {
                this.value = VARIABLE_VALUE_PREFIX + value
                        + VARIABLE_VALUE_SUFFIX;
            }
        } else {
            this.rhsType = CONSTANT;
        }
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @ApiModelProperty(value = "The condition value without enclosing ${}.\n" +
            "For Constant RHS type, rhsvalue would be same as condition value attribute.",
            example = "user.principalname")
    public String getRHSValue() {
        if (value != null) {
            value = extractMultiValues(value);
            if (value.startsWith(VARIABLE_VALUE_PREFIX) && value.endsWith(VARIABLE_VALUE_SUFFIX)) {
                value = value.substring(2, value.length() - 1);
            }
        }
        return value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRhsType() {
        if (this.getRHSValue().startsWith(USER_TYPE_PREFIX) || this.getRHSValue().startsWith(HOST_TYPE_PREFIX)
        		|| this.getRHSValue().startsWith(APPLICATION_TYPE_PREFIX)) {
            this.rhsType = SUBJECT_GROUP;
        } else if (this.getRHSValue().startsWith(RESOURCE_TYPE_PREFIX)) {
            this.rhsType = RESOURCE_GROUP;
        } else {
            this.rhsType = CONSTANT;
        }
        return rhsType;
    }

    public void setRhsType(String rhsType) {
        this.rhsType = rhsType;
    }

    private String extractMultiValues(String value) {
        if(value != null &&
                value.startsWith(MULTI_VALUE_PREFIX) && value.endsWith(MULTI_VALUE_SUFFIX)) {
            String elementsContent = value.substring(1, value.length() - 1);
            String[] elements = elementsContent.split(MULTIVALUE_SPLIT_PATTERN);

            if (elements.length == 1) {
                String extractedValue;
                if(elements[0].startsWith("\"") && elements[0].endsWith("\"")) {
                    extractedValue = elements[0].substring(1, elements[0].length() - 1).trim();
                } else {
                    extractedValue = elements[0].trim();
                }

                if((extractedValue.startsWith(USER_TYPE_PREFIX)
                        || extractedValue.startsWith(HOST_TYPE_PREFIX)
                        || extractedValue.startsWith(RESOURCE_TYPE_PREFIX))
                    || ((extractedValue.startsWith(VARIABLE_VALUE_PREFIX + USER_TYPE_PREFIX)
                        || extractedValue.startsWith(VARIABLE_VALUE_PREFIX + HOST_TYPE_PREFIX)
                        || extractedValue.startsWith(VARIABLE_VALUE_PREFIX + RESOURCE_TYPE_PREFIX))
                        && extractedValue.endsWith(VARIABLE_VALUE_SUFFIX))) {
                    return extractedValue;
                }
            }
        }

        return value;
    }
}
