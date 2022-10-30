package com.nextlabs.serverapps.common.exception;

public class InvalidCredentialException extends RuntimeException {

    private static final long serialVersionUID = -7003923479118391919L;

    private String statusCode;
    private String statusMsg;

    /**
     * Constructor
     *
     * @param statusCode
     * @param statusMsg
     */
    public InvalidCredentialException(String statusCode, String statusMsg){
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
    }

    public InvalidCredentialException(String statusMsg) {
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
