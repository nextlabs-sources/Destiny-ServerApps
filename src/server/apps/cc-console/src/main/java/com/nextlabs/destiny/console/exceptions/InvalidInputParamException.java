/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 5, 2015
 *
 */
package com.nextlabs.destiny.console.exceptions;

/**
 *
 * Invalid input parameter related exceptions handles by this exception class
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class InvalidInputParamException extends RuntimeException {

    private static final long serialVersionUID = -4612257189692106770L;

    private String statusCode;
    private String statusMsg;

    /**
     * Constructor
     * 
     * @param statusCode
     * @param statusMsg
     */
    public InvalidInputParamException(String statusCode, String statusMsg) {
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

}
