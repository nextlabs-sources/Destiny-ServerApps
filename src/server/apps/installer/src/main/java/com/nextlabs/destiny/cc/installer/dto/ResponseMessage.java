package com.nextlabs.destiny.cc.installer.dto;

/**
 * DTO for response message.
 *
 * @author Sachindra Dasun
 */
public class ResponseMessage {

    private String msg;

    public ResponseMessage(String msg) {
        this.msg = msg;
    }

    public ResponseMessage() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
