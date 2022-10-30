/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 7, 2016
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * DTO for Policy environment configurations
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class PolicyEnvironmentConfigDTO implements Serializable {

    private static final long serialVersionUID = -779217821949446903L;

    @ApiModelProperty(value = "Type of access. Value of remoteAccess for:\n" +
            "<ul>" +
            "<li><strong>Local</strong>: 0</li>" +
            "<li><strong>Remote</strong>: 1</li>", example = "-1")
    private int remoteAccess = -1; // 0- local/ 1- remote

    @ApiModelProperty(value = "Time since last heart beat of PDP, in seconds.", example = "2400")
    private int timeSinceLastHBSecs = -1;

    public int getRemoteAccess() {
        return remoteAccess;
    }

    public void setRemoteAccess(int remoteAccess) {
        this.remoteAccess = remoteAccess;
    }

    public int getTimeSinceLastHBSecs() {
        return timeSinceLastHBSecs;
    }

    public void setTimeSinceLastHBSecs(int timeSinceLastHBSecs) {
        this.timeSinceLastHBSecs = timeSinceLastHBSecs;
    }

    @Override
    public String toString() {
        return String.format(
                "PolicyEnvironmentConfigDTO [remoteAccess=%s, timeSinceLastHBSecs=%s]",
                remoteAccess, timeSinceLastHBSecs);
    }

}
