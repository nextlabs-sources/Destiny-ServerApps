/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 28, 2016
 *
 */
package com.nextlabs.destiny.console.exceptions;

/**
 * 
 * This exception will be throws by the API if the user enters an invalid
 * password
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public class InvalidPasswordException extends RuntimeException {

    private static final long serialVersionUID = 2441364447934044255L;

    private String statusCode;
    private String statusMsg;

    /**
     * Constructor
     * 
     * @param statusCode
     * @param statusMsg
     */
    public InvalidPasswordException(String statusCode, String statusMsg) {
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
