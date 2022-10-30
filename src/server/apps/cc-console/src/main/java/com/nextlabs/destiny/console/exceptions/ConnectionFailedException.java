/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 3 Aug 2016
 *
 */
package com.nextlabs.destiny.console.exceptions;

/**
 *
 * This exception will be thrown by the API if the connection to external source fails
 *
 * @author aishwarya
 * @since   8.0
 *
 */
public class ConnectionFailedException extends RuntimeException {
	
	private static final long serialVersionUID = -7003923479118391909L;
	
	private String statusCode;
    private String statusMsg;
       
    /**
     * Constructor
     * 
     * @param statusCode
     * @param statusMsg
     */
    public ConnectionFailedException(String statusCode, String statusMsg){
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
