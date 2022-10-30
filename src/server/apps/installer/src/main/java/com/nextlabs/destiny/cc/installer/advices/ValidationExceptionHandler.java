package com.nextlabs.destiny.cc.installer.advices;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Handler for installer validation exceptions.
 *
 * @author Sachindra Dasun
 */
@ControllerAdvice
public class ValidationExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {
        return new ResponseEntity<>(Collections.singletonMap("errors",
                e.getBindingResult().getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField,
                                error -> Objects.toString(error.getDefaultMessage(), "")))),
                headers, status);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e,
                                                                        WebRequest request) {
        return new ResponseEntity<>(Collections.singletonMap("errors", getErrors(e)),
                new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    public static Map<String, List<String>> getErrors(ConstraintViolationException e) {
        Map<String, List<String>> errors = new TreeMap<>();
        e.getConstraintViolations().forEach(constraintViolation -> {
            String field = determineField(constraintViolation.getPropertyPath().toString());
            errors.computeIfAbsent(field, key -> new ArrayList<>()).add(constraintViolation.getMessage());
        });
        return errors;
    }

    private static String determineField(String path) {
        int elementIndex = path.indexOf(".<");
        return (elementIndex >= 0 ? path.substring(0, elementIndex) : path);
    }

}
