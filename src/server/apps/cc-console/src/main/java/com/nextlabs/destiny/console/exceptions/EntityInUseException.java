/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 27, 2016
 *
 */
package com.nextlabs.destiny.console.exceptions;

/**
 * In case of no model found for the given query will throw this exception from
 * API
 *
 * @author Aishwarya
 * @since 8.0
 *
 */
public class EntityInUseException extends RuntimeException {

    private static final long serialVersionUID = -662685983827167096L;

    private String statusCode;
    private String statusMsg;

    /**
     * Constructor
     * 
     * @param statusCode
     * @param statusMsg
     */
    public EntityInUseException(String statusCode, String statusMsg) {
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
