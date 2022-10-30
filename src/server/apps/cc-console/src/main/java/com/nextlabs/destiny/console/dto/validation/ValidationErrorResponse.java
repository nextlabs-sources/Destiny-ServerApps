package com.nextlabs.destiny.console.dto.validation;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ValidationErrorResponse {

    private final List<Violation> globalErrors = new ArrayList<>();
    private final List<Violation> fieldErrors = new ArrayList<>();

    public List<Violation> getGlobalErrors() {
        return globalErrors;
    }

    public List<Violation> getFieldErrors() {
        return fieldErrors;
    }
}
