/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 11, 2015
 *
 */
package com.nextlabs.destiny.console.dto.common;

/**
 *
 * Simple Response DTO with a model
 *
 * @param <T>
 * @author Amila Silva
 * @since 8.0
 *
 */
public class SimpleResponseDTO<T> extends ResponseDTO {

    private static final long serialVersionUID = -3350896899045985685L;

    private T data;

    /**
     * Simple Response DTO Constructor
     * 
     * @param statusCode
     * @param message
     */
    private SimpleResponseDTO(String statusCode, String message, T data) {
        super(statusCode, message);
        this.data = data;
    }

    @SuppressWarnings("rawtypes")
    public static <T extends Object> SimpleResponseDTO create(
            String statusCode, String message, T data) {
        return new SimpleResponseDTO<T>(statusCode, message, data);
    }

    public static <T> SimpleResponseDTO<T> createWithType(String statusCode, String message, T data) {
        return new SimpleResponseDTO<>(statusCode, message, data);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
