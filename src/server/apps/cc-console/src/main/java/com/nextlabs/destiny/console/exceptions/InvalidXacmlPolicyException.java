/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 14, 2016
 *
 */
package com.nextlabs.destiny.console.exceptions;

/**
 * This exception will be thrown by the API if the entity name is not unique
 *
 * @author Mohammed Sainal Shah
 * @since 9.5
 *
 */
public class InvalidXacmlPolicyException extends RuntimeException {

    private static final long serialVersionUID = -2866063156625621662L;

    private final String statusCode;
    private final String statusMsg;

    /**
     * Constructor
     *
     * @param statusCode
     * @param statusMsg
     */
    public InvalidXacmlPolicyException(String statusCode, String statusMsg){
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
    }
    
    public String getStatusCode() {
        return statusCode;
    }
    public String getStatusMsg() {
        return statusMsg;
    }

}
