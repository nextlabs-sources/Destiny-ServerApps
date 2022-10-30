/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 16, 2016
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt.policyworkflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nextlabs.destiny.console.dto.common.BaseDTO;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.enums.EntityWorkflowRequestStatus;
import com.nextlabs.destiny.console.model.policyworkflow.EntityWorkflowRequest;
import com.nextlabs.destiny.console.model.policyworkflow.WorkflowRequestLevel;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for entity workflow requests
 *
 *
 * @author Mohammmed Sainal Shah
 * @since 2020.10
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class EntityWorkflowRequestDTO extends BaseDTO {

    private static final long serialVersionUID = 2884060285697270588L;

    @ApiModelProperty(
            value = "The entity type can have the following values: "
                    + "\n<ul><li><strong>FO</strong>: Folder</li>"
                    + "<li><strong>PO</strong>: Policy</li>"
                    + "<li><strong>CO</strong>: Component</li>"
                    + "<li><strong>DP</strong>: Delegation Policy</li>"
                    + "<li><strong>DC</strong>: Delegation Component</li></ul>",
            example = "PO")
    private DevEntityType devEntityType;

    @ApiModelProperty(
            value = "The status of the workflow request.")
    private EntityWorkflowRequestStatus status;

    private List<WorkflowRequestLevelDTO> workflowRequestLevels;

    private WorkflowRequestLevelDTO activeWorkflowRequestLevel;

    @ApiModelProperty(value = "ID of the user who created the workflow request.", example = "1")
    private long ownerId;

    @ApiModelProperty(value = "Display name of the user who created the workflow request.", example = "Stuart")
    private String ownerDisplayName;

    @ApiModelProperty(value = "ID of the user who last modified the workflow request.", example = "3")
    private long modifiedById;

    @ApiModelProperty(value = "Display name of the user who last modified the workflow request.", example = "John")
    private String modifiedBy;

    @ApiModelProperty(
            value = "Indicates the date at which this workflow request was created. The value is measured in milliseconds, " +
                    "between the current time and midnight, January 1, 1970 UTC(coordinated universal time). " +
                    "For example, November 15, 2019 10:48:49.296 AM will be written as 1573786129296.",
            example = "1573786129296")
    private long createdDate;

    @ApiModelProperty(
            value = "Indicates the date at which this workflow request was last modified. The value is measured in milliseconds, " +
                    "between the current time and midnight, January 1, 1970 UTC(coordinated universal time). " +
                    "For example, November 15, 2019 10:48:49.296 AM will be written as 1573786129296.",
            example = "1573786129296")
    private long lastUpdatedDate;
    
    public DevEntityType getDevEntityType() {
        return devEntityType;
    }

    public void setDevEntityType(DevEntityType devEntityType) {
        this.devEntityType = devEntityType;
    }

    public EntityWorkflowRequestStatus getStatus() {
        return status;
    }

    public void setStatus(EntityWorkflowRequestStatus status) {
        this.status = status;
    }

    public List<WorkflowRequestLevelDTO> getWorkflowRequestLevels() {
        if (workflowRequestLevels == null) {
            workflowRequestLevels = new ArrayList<>();
        }
        return workflowRequestLevels;
    }

    public void setWorkflowRequestLevels(List<WorkflowRequestLevelDTO> workflowRequestLevels) {
        this.workflowRequestLevels = workflowRequestLevels;
    }

    public WorkflowRequestLevelDTO getActiveWorkflowRequestLevel() {
        return activeWorkflowRequestLevel;
    }

    public void setActiveWorkflowRequestLevel(WorkflowRequestLevelDTO activeWorkflowRequestLevel) {
        this.activeWorkflowRequestLevel = activeWorkflowRequestLevel;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerDisplayName() {
        return ownerDisplayName;
    }

    public void setOwnerDisplayName(String ownerDisplayName) {
        this.ownerDisplayName = ownerDisplayName;
    }

    public long getModifiedById() {
        return modifiedById;
    }

    public void setModifiedById(long modifiedById) {
        this.modifiedById = modifiedById;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(long lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public static EntityWorkflowRequestDTO getDTO(EntityWorkflowRequest entityWorkflowRequest, ApplicationUserSearchRepository applicationUserSearchRepository) {
        if (entityWorkflowRequest == null) {
            return null;
        }
        EntityWorkflowRequestDTO entityWorkflowRequestDTO = new EntityWorkflowRequestDTO();
        entityWorkflowRequestDTO.setId(entityWorkflowRequest.getId());
        entityWorkflowRequestDTO.setDevEntityType(entityWorkflowRequest.getDevEntityType());
        entityWorkflowRequestDTO.setStatus(entityWorkflowRequest.getStatus());
        entityWorkflowRequestDTO.setWorkflowRequestLevels(
                entityWorkflowRequest.getRequestLevels()
                .parallelStream()
                .map((WorkflowRequestLevel workflowRequestLevel) -> WorkflowRequestLevelDTO.getDTO(workflowRequestLevel, applicationUserSearchRepository))
                .collect(Collectors.toList()));
        entityWorkflowRequestDTO.setActiveWorkflowRequestLevel(WorkflowRequestLevelDTO
                .getDTO(entityWorkflowRequest.getActiveWorkflowRequestLevel(), applicationUserSearchRepository));
        entityWorkflowRequestDTO.setOwnerId(entityWorkflowRequest.getOwnerId());
        entityWorkflowRequestDTO.setCreatedDate(entityWorkflowRequest.getCreatedDate().getTime());
        entityWorkflowRequestDTO.setModifiedById(entityWorkflowRequest.getLastUpdatedBy());
        entityWorkflowRequestDTO.setLastUpdatedDate(entityWorkflowRequest.getLastUpdatedDate().getTime());
        applicationUserSearchRepository.findById(entityWorkflowRequestDTO.getOwnerId())
                .ifPresent(owner -> entityWorkflowRequestDTO.setOwnerDisplayName(owner.getDisplayName()));
        applicationUserSearchRepository.findById(entityWorkflowRequestDTO.getModifiedById())
                .ifPresent(modifiedBy -> entityWorkflowRequestDTO.setModifiedBy(modifiedBy.getDisplayName()));
        return entityWorkflowRequestDTO;
    }
}
