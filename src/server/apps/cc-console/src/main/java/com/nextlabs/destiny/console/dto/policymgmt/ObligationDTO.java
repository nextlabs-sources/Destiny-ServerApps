/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 16, 2016
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nextlabs.destiny.console.dto.common.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

/**
 * DTO for policy obligations
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class ObligationDTO extends BaseDTO {

    private static final long serialVersionUID = 6574182190598327873L;

    @ApiModelProperty(value = "Policy model id for this obligation.", example = "42662", position = 10)
    private Long policyModelId = 0L;

    @ApiModelProperty(value = "Name of the obligation.", example = "log", position = 20)
    private String name;

    @ApiModelProperty(value = "A String to String map of parameters for custom obligations. Keys of the map should be valid values " +
            "defined in Obligation section of Policy Model.")
    private Map<String, String> params;

    public Long getPolicyModelId() {
        return policyModelId;
    }

    public void setPolicyModelId(Long policyModelId) {
        this.policyModelId = policyModelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getParams() {
        if (params == null) {
            params = new LinkedHashMap<>(10);
        }
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

}
