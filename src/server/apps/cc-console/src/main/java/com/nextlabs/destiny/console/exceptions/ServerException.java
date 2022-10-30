/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jul 29, 2020
 *
 */
package com.nextlabs.destiny.console.exceptions;


public class ServerException extends Exception {

    private static final long serialVersionUID = 4983315487407732470L;
    private String statusCode;
    private String statusMsg;
    private String logMessage;

    public ServerException() {
        super();
    }

    public ServerException(String logMessage, Throwable cause) {
        super(logMessage, cause);
    }

    public ServerException(String statusCode, String statusMsg,
            String logMessage, Throwable cause) {
        super(logMessage, cause);
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
    }

    public ServerException(String message) {
        super(message);
    }

    public ServerException(Throwable cause) {
        super(cause);
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
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
