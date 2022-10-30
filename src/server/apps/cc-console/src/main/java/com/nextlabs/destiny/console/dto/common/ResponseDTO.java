/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 9, 2015
 *
 */
package com.nextlabs.destiny.console.dto.common;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

/**
 *
 * Base Response DTO for all the Console API reponses
 *
 * @author Amila Silva
 * 
 * @since 8.0
 *
 */
public class ResponseDTO implements Serializable {

    private static final long serialVersionUID = 548918052137779706L;

    private String statusCode;
    private String message;

    /**
     * Constructor
     * 
     * @param statusCode
     * @param message
     */
    protected ResponseDTO(String statusCode, String message) {
        super();
        this.statusCode = statusCode;
        this.message = message;
    }

    public static ResponseDTO create(String statusCode, String message) {
        return new ResponseDTO(statusCode, message);
    }

    @ApiModelProperty(value = "The status code of the response.", example = "1000")
    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    @ApiModelProperty(value = "The description of the status code of the response.", example = "Operation completed successfully")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
