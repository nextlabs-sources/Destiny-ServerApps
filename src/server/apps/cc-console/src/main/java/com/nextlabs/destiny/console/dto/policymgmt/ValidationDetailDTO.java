/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 24, 2016
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Policy and Component validation details holder
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class ValidationDetailDTO implements Serializable {

    private static final long serialVersionUID = 1261528724147457342L;

    private Long id;
    private boolean deployable = true;
    private List<ValidationRecord> details;
    private Map<String, String> warnings;
    private ArrayList<PushResultDTO> pushResults;

    public ValidationDetailDTO() {

    }

    /**
     * Constructor
     * 
     * @param id
     * 
     */
    public ValidationDetailDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean canDeploy(boolean isDeployed) {
        this.deployable &= isDeployed;
        return deployable;
    }

    public boolean isDeployable() {
        return deployable;
    }

    public void setDeployable(boolean deployable) {
        this.deployable = deployable;
    }

    public List<ValidationRecord> getDetails() {
        if (details == null) {
            details = new ArrayList<>();
        }
        return details;
    }

    public Map<String, String> getWarnings() {
        return warnings;
    }

    public void setWarnings(Map<String, String> warnings) {
        this.warnings = warnings;
    }

    public List<PushResultDTO> getPushResults() {
        if (pushResults == null) {
            pushResults = new ArrayList<>();
        }
        return pushResults;
    }

    public void setPushResults(List<PushResultDTO> pushResults) {
        this.pushResults = new ArrayList<>(pushResults);
    }


}
