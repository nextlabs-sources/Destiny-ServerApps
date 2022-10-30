/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide. Created on Apr 14, 2020
 *
 */
package com.nextlabs.destiny.console.model.scim;

import com.bettercloud.scim2.common.messages.ErrorResponse;
import com.bettercloud.scim2.common.utils.StatusSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "method",
        visible = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulkResponseOperation {

    private String location;
    private BulkMethodType method;
    private String bulkId;

    @JsonSerialize(using = StatusSerializer.class)
    private int status;
    private ErrorResponse response;

    @JsonCreator
    public BulkResponseOperation(@JsonProperty(value = "location", required = true) String location,
                                 @JsonProperty(value = "method", required = true) BulkMethodType method,
                                 @JsonProperty(value = "bulkId", required = true) String bulkId,
                                 @JsonProperty(value = "status", required = true) int status,
                                 @JsonProperty(value = "response", required = true) ErrorResponse response) {
        this.location = location;
        this.method = method;
        this.bulkId = bulkId;
        this.status = status;
        this.response = response;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BulkMethodType getMethod() {
        return method;
    }

    public void setMethod(BulkMethodType method) {
        this.method = method;
    }

    public String getBulkId() {
        return bulkId;
    }

    public void setBulkId(String bulkId) {
        this.bulkId = bulkId;
    }

    public ErrorResponse getResponse() {
        return response;
    }

    public void setResponse(ErrorResponse response) {
        this.response = response;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BulkResponseOperation that = (BulkResponseOperation) o;
        if (getLocation() != null ? !getLocation().equals(that.getLocation())
                : that.getLocation() != null) {
            return false;
        } else if (getMethod() != null ? !getMethod().equals(that.getMethod())
                : that.getMethod() != null) {
            return false;
        } else if (getBulkId() != null ? !getBulkId().equals(that.getBulkId())
                : that.getBulkId() != null) {
            return false;
        } else if (getResponse() != null ? !getResponse().equals(that.getResponse())
                : that.getResponse() != null) {
            return false;
        }




        return true;
    }


    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getLocation() != null ? getLocation().hashCode() : 0);
        result = 31 * result + (getMethod() != null ? getMethod().hashCode() : 0);
        result = 31 * result + (getBulkId() != null ? getBulkId().hashCode() : 0);
        result = 31 * result + (getResponse() != null ? getResponse().hashCode() : 0);

        return result;
    }

}
