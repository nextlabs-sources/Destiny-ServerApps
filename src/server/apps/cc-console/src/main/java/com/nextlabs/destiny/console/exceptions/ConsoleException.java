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
 * Control Center Console system services exception class
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class ConsoleException extends Exception {
    private static final long serialVersionUID = -5055883361836861589L;

    private String statusCode;
    private String statusMsg;
    private String logMessage;

    public ConsoleException() {
        super();
    }

    public ConsoleException(String logMessage, Throwable cause) {
        super(logMessage, cause);
    }

    public ConsoleException(String statusCode, String statusMsg,
            String logMessage, Throwable cause) {
        super(logMessage, cause);
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
    }

    public ConsoleException(String message) {
        super(message);
    }

    public ConsoleException(Throwable cause) {
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
