package com.nextlabs.destiny.console.dto.validation;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Violation {
    private final String fieldName;
    private final String message;

    public Violation(String message) {
        this.message = message;
        this.fieldName = null;
    }

    public Violation(String fieldName, String message) {
        this.fieldName = fieldName;
        this.message = message;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMessage() {
        return message;
    }
}
