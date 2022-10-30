package com.nextlabs.destiny.console.dto.policymgmt;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author Sachindra Dasun
 */
public class PushResultDTO implements Serializable {
    private static final long serialVersionUID = 8153236356817201828L;

    @ApiModelProperty(value = "The URL of DPS component.", example = "https://cc-prod-01:8443/dps")
    private String dpsUrl;

    @ApiModelProperty(value = "The push status.")
    private boolean success;

    @ApiModelProperty(value = "The push result message.", example = "Push Successful")
    private String message;

    public String getDpsUrl() {
        return dpsUrl;
    }

    public void setDpsUrl(String dpsUrl) {
        this.dpsUrl = dpsUrl;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
