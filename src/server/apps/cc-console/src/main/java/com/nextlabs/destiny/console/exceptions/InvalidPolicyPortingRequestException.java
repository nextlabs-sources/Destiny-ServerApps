package com.nextlabs.destiny.console.exceptions;

public class InvalidPolicyPortingRequestException extends RuntimeException {	
	
    private static final long serialVersionUID = -423285129168309680L;
	private String statusCode;
    private String statusMsg;

    /**
     * Constructor
     * 
     * @param statusCode
     * @param statusMsg
     */
    public InvalidPolicyPortingRequestException(String statusCode, String statusMsg) {
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
