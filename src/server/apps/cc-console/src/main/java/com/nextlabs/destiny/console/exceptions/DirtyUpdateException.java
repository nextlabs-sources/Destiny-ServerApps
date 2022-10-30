/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 17, 2016
 *
 */
package com.nextlabs.destiny.console.exceptions;

/**
 * This exception will be thrown if an attempt is made to update the obsolete
 * data
 *
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public class DirtyUpdateException extends RuntimeException {

    private static final long serialVersionUID = 1729491511182946111L;

    private String statusCode;
    private String statusMsg;

    /**
     * Constructor
     * 
     * @param statusCode
     * @param statusMsg
     */
    public DirtyUpdateException(String statusCode, String statusMsg) {
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
