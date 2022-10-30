/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 13, 2015
 *
 */
package com.nextlabs.destiny.console.dto.common;

import java.io.Serializable;

import com.nextlabs.destiny.console.enums.SearchFieldType;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Pattern;

/**
 *
 * DTO for Search criteria single field
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class SearchField implements Serializable {

    private static final long serialVersionUID = 5395093175396744474L;

    @ApiModelProperty(value = "Field name to search for. Available field names for search are listed in the API description.", example = "tags")
    private String field;

    @Pattern(regexp = "SINGLE|SINGLE_EXACT_MATCH|TEXT|DATE|MULTI|MULTI_EXACT_MATCH|NESTED|NESTED_MULTI")
    @ApiModelProperty(example = "SINGLE", value = "Type of the search field." +
            "\n<ul><li><strong>SINGLE</strong>: Match a single value at the beginning of the field.</li>"
            + "<li><strong>SINGLE_EXACT_MATCH</strong>: Match a single value anywhere in the field.</li>"
            + "<li><strong>TEXT</strong>: Match any of the provided list of values anywhere in the field.</li>"
            + "<li><strong>DATE</strong>: Match items between provided dates.</li>"
            + "<li><strong>MULTI</strong>: Match any of the provided list of values anywhere in the field.</li>"
            + "<li><strong>MULTI_EXACT_MATCH</strong>: Match any of the provided list of values anywhere in the field.</li>"
            + "<li><strong>NESTED</strong>: Match nested field (Single value) anywhere in the field.</li>"
            + "<li><strong>NESTED_MULTI</strong>: Match nested field (Any of the provided list of values) anywhere in the field.</li></ul>")
    private SearchFieldType type;

    @ApiModelProperty(value = "Nested field key.", example = "tags.key")
    private String nestedField;

    @ApiModelProperty(value = "Field Value.", example = "Sample Tag 1")
    private FieldValue value;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public SearchFieldType getType() {
        return type;
    }

    public void setType(SearchFieldType type) {
        this.type = type;
    }

    public FieldValue getValue() {
        return value;
    }

    public void setValue(FieldValue value) {
        this.value = value;
    }

    public String getNestedField() {
        return nestedField;
    }

    public void setNestedField(String nestedField) {
        this.nestedField = nestedField;
    }

    @Override
    public String toString() {
        return "SearchField [field=" + field + ", type=" + type
                + ", nestedField=" + nestedField + ", value=" + value + "]";
    }

}
