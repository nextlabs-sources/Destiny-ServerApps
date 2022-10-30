/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 9, 2015
 *
 */
package com.nextlabs.destiny.console.dto.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

/**
 *
 * All Console level API reponses handled by this response entity
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class ConsoleResponseEntity<T extends Object>
        extends ResponseEntity<T> {

    private ConsoleResponseEntity(HttpStatus statusCode) {
        super(statusCode);
    }

    private ConsoleResponseEntity(MultiValueMap<String, String> headers,
            HttpStatus statusCode) {
        super(headers, statusCode);
    }

    private ConsoleResponseEntity(T body, HttpStatus statusCode) {
        super(body, statusCode);
    }

    private ConsoleResponseEntity(T body, MultiValueMap<String, String> headers,
            HttpStatus statusCode) {
        super(body, headers, statusCode);
    }

    /**
     * @param statusCode
     */
    public static <T extends Object> ConsoleResponseEntity<T> get(
            HttpStatus statusCode) {
        return new ConsoleResponseEntity<>(statusCode);
    }

    /**
     * @param headers
     * @param statusCode
     */
    public static <T extends Object> ConsoleResponseEntity<T> get(
            MultiValueMap<String, String> headers, HttpStatus statusCode) {
        return new ConsoleResponseEntity<>(headers, statusCode);
    }

    /**
     * @param body
     * @param statusCode
     */
    public static <T extends Object> ConsoleResponseEntity<T> get(T body,
            HttpStatus statusCode) {
        return new ConsoleResponseEntity<>(body, statusCode);
    }

    /**
     * @param body
     * @param headers
     * @param statusCode
     */
    public static <T extends Object> ConsoleResponseEntity<T> get(T body,
            MultiValueMap<String, String> headers, HttpStatus statusCode) {
        return new ConsoleResponseEntity<>(body, headers, statusCode);
    }

    /**
     * Add new header value.
     * 
     * @param headerName
     *            headerName
     * @param headerValue
     *            headerValue
     * @return {@link HttpHeaders}
     * 
     */
    public HttpHeaders addHeader(String headerName, String headerValue) {
        HttpHeaders headers = this.getHeaders();
        headers.add(headerName, headerValue);

        return headers;
    }
}
