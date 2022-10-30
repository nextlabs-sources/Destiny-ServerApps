/*
 * Created on Jun 24, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2018 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author schok
 */
package com.nextlabs.destiny.console.exceptions;

/**
 * Exception when detected invalid/disallow operation request
 *
 */
public class InvalidOperationException 
		extends RuntimeException {

	private static final long serialVersionUID = 8303714898229570148L;
	private String statusCode;
    private String statusMsg;
    
    /**
     * Control Center's standard exception constructor
     * 
     * @param statusCode Status code for front end to determine work flow after received response
     * @param statusMsg Message which describes the details of exception 
     */
    public InvalidOperationException(String statusCode, String statusMsg) {
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
    }
    
    /**
     * Control Center + Exception's standard constructor
     * 
     * @param statusCode Status code for front end to determine work flow after received response
     * @param statusMsg Message which describes the details of exception
     * @param logMessage The detail message
     * @param cause The cause
     */
    public InvalidOperationException(String statusCode, String statusMsg,
            String logMessage, Throwable cause) {
        super(logMessage, cause);
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
