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
 * @author aishwarya
 * @since 8.0
 *
 */
public class NotUniqueException extends RuntimeException {

    private static final long serialVersionUID = -2866063156625621662L;

    private String statusCode;
    private String statusMsg;
       
    /**
     * Constructor
     * 
     * @param statusCode
     * @param statusMsg
     */
    public NotUniqueException(String statusCode, String statusMsg){
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
