package com.nextlabs.destiny.console.dto.policymgmt.policyvalidator;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public class Status implements java.io.Serializable {
    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    private java.lang.String statusMessage;

    private Map<String, String> statusCode;

    /**
     * Returns the value of property "statusMessage". 
     * 
     */
    public java.lang.String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Updates the value of property "statusMessage". 
     */
    public void setStatusMessage(java.lang.String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * Returns the value of property "statusCode". 
     * @return 
     * 
     */
    public Map<String, String> getStatusCode() {
        return statusCode;
    }

    /**
     * Updates the value of property "statusCode". 
     */
    public void setStatusCode(Map<String, String> statusCode) {
        this.statusCode = statusCode;
    }

}
