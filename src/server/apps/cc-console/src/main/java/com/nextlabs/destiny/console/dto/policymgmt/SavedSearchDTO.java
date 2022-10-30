/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 12, 2015
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import static com.nextlabs.destiny.console.enums.SharedMode.PUBLIC;
import static com.nextlabs.destiny.console.enums.Status.ACTIVE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextlabs.destiny.console.dto.common.BaseDTO;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.SavedSearch;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * DTO for Saved Policy Search
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class SavedSearchDTO extends BaseDTO {

    private static final long serialVersionUID = 2324460583492772910L;

    @ApiModelProperty(value = "Name of the saved search.", example = "Sample search 1")
    private String name;

    @ApiModelProperty(value = "Description of the saved search.", example = "Example saved search")
    private String desc;

    private SearchCriteria criteria;

    @ApiModelProperty(value = "Status of the saved search.", example = "ACTIVE")
    private String status = ACTIVE.name();

    @ApiModelProperty(value = "Share mode of the saved search.", example = "PUBLIC")
    private String sharedMode = PUBLIC.name();

    @ApiModelProperty(hidden = true)
    private List<String> userIds;

    @ApiModelProperty(value = "Type of the search.", allowableValues = "POLICY, COMPONENT, POLICY_MODEL_RESOURCE, POLICY_MODEL_SUBJECT, LOCATION, PROPERTY",
            example = "POLICY")
    private String type;

    @JsonIgnore
    private String criteriaJson;

    /**
     * Transform {@link SavedSearch} entity data to DTO
     * 
     * @return {@link SavedSearchDTO}}
     * @throws ConsoleException
     */
    public static SavedSearchDTO getDTO(SavedSearch criteria)
            throws ConsoleException {
        try {
            SavedSearchDTO dto = new SavedSearchDTO();
            dto.setId(criteria.getId());
            dto.setName(criteria.getName());
            dto.setDesc(criteria.getDesc());
            dto.setCriteria(criteria.criteriaModel());
            dto.setStatus(criteria.getStatus().name());
            dto.setSharedMode(criteria.getSharedMode().name());
            dto.setUserIds(criteria.getUserIds());
            dto.setType(criteria.getType().name());
            return dto;
        } catch (IOException e) {
            throw new ConsoleException(
                    "Error occurred in Policy criteria Json processing", e);
        }
    }

    public String getCriteriaJson() throws ConsoleException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.criteriaJson = mapper.writeValueAsString(getCriteria());
        } catch (JsonProcessingException e) {
            throw new ConsoleException(
                    "Error occurred in saved Policy criteria Json processing",
                    e);
        }
        return criteriaJson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public SearchCriteria getCriteria() {
        return criteria;
    }

    public void setCriteria(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    public String getSharedMode() {
        return sharedMode;
    }

    public void setSharedMode(String sharedMode) {
        this.sharedMode = sharedMode;
    }

    public List<String> getUserIds() {
        if (userIds == null) {
            userIds = new ArrayList<>();
        }
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
