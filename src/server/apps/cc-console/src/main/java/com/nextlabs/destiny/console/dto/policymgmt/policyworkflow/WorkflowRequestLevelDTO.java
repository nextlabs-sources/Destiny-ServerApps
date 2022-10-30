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
import com.nextlabs.destiny.console.enums.WorkflowRequestLevelStatus;
import com.nextlabs.destiny.console.model.policyworkflow.WorkflowRequestLevel;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import io.swagger.annotations.ApiModelProperty;

/**
 * DTO for workflow request levels
 *
 *
 * @author Mohammed Sainal Shah
 * @since 2020.10
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class WorkflowRequestLevelDTO extends BaseDTO {

    private static final long serialVersionUID = 2884060285697270588L;

    @ApiModelProperty(
            value = "The status of the workflow request level.", allowableValues = "PENDING, APPROVED, REQUESTED_AMENDMENT")
    private WorkflowRequestLevelStatus status;

    @ApiModelProperty(value = "The name of the workflow level.", example = "Level 1 Approval")
    private String workflowLevelName;

    @ApiModelProperty(value = "The description of the workflow level.", example = "Level 1 Approval description")
    private String workflowLevelDescription;

    @ApiModelProperty(value = "The rank of the level. The value is used to determine the order of the workflow level " +
            "when there are multiple workflow levels configured. The value starts at 1.", example = "1")
    private int levelOrder;

    @ApiModelProperty(value = "ID of the user who approved the workflow request level.", example = "3")
    private Long approvedById;

    @ApiModelProperty(value = "Display name of the user who approved the workflow request level.", example = "John")
    private String approvedBy;

    @ApiModelProperty(
            value = "Indicates the date at which this workflow request level was approved. The value is measured in milliseconds, " +
                    "between the current time and midnight, January 1, 1970 UTC(coordinated universal time). " +
                    "For example, November 15, 2019 10:48:49.296 AM will be written as 1573786129296.",
            example = "1573786129296")
    private long approvedDate;

    @ApiModelProperty(value = "ID of the user who created the workflow request level.", example = "1")
    private long ownerId;

    @ApiModelProperty(value = "Display name of the user who created the workflow request level.", example = "Stuart")
    private String ownerDisplayName;

    @ApiModelProperty(value = "ID of the user who last modified the workflow request level.", example = "3")
    private long modifiedById;

    @ApiModelProperty(value = "Display name of the user who last modified the workflow request level.", example = "John")
    private String modifiedBy;

    @ApiModelProperty(
            value = "Indicates the date at which this workflow request level was created. The value is measured in milliseconds, " +
                    "between the current time and midnight, January 1, 1970 UTC(coordinated universal time). " +
                    "For example, November 15, 2019 10:48:49.296 AM will be written as 1573786129296.",
            example = "1573786129296")
    private long createdDate;

    @ApiModelProperty(
            value = "Indicates the date at which this workflow request level was last modified. The value is measured in milliseconds, " +
                    "between the current time and midnight, January 1, 1970 UTC(coordinated universal time). " +
                    "For example, November 15, 2019 10:48:49.296 AM will be written as 1573786129296.",
            example = "1573786129296")
    private long lastUpdatedDate;

    public WorkflowRequestLevelStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowRequestLevelStatus status) {
        this.status = status;
    }

    public String getWorkflowLevelName() {
        return workflowLevelName;
    }

    public void setWorkflowLevelName(String workflowLevelName) {
        this.workflowLevelName = workflowLevelName;
    }

    public String getWorkflowLevelDescription() {
        return workflowLevelDescription;
    }

    public void setWorkflowLevelDescription(String workflowLevelDescription) {
        this.workflowLevelDescription = workflowLevelDescription;
    }

    public int getLevelOrder() {
        return levelOrder;
    }

    public void setLevelOrder(int levelOrder) {
        this.levelOrder = levelOrder;
    }

    public Long getApprovedById() {
        return approvedById;
    }

    public void setApprovedById(Long approvedById) {
        this.approvedById = approvedById;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public long getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(long approvedDate) {
        this.approvedDate = approvedDate;
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

    public static WorkflowRequestLevelDTO getDTO(WorkflowRequestLevel workflowRequestLevel, ApplicationUserSearchRepository applicationUserSearchRepository) {
        if (workflowRequestLevel == null) {
            return null;
        }
        WorkflowRequestLevelDTO workflowRequestLevelDTO = new WorkflowRequestLevelDTO();
        workflowRequestLevelDTO.setId(workflowRequestLevel.getId());
        workflowRequestLevelDTO.setStatus(workflowRequestLevel.getStatus());
        workflowRequestLevelDTO.setWorkflowLevelName(workflowRequestLevel.getWorkflowLevel().getName());
        workflowRequestLevelDTO.setWorkflowLevelDescription(workflowRequestLevel.getWorkflowLevel().getDescription());
        workflowRequestLevelDTO.setLevelOrder(workflowRequestLevel.getWorkflowLevel().getLevelOrder());
        if (workflowRequestLevel.getStatus() == WorkflowRequestLevelStatus.APPROVED) {
            workflowRequestLevelDTO.setApprovedById(workflowRequestLevel.getApprovedBy());
            workflowRequestLevelDTO.setApprovedDate(workflowRequestLevel.getApprovedTime().getTime());
        }
        workflowRequestLevelDTO.setOwnerId(workflowRequestLevel.getOwnerId());
        workflowRequestLevelDTO.setCreatedDate(workflowRequestLevel.getCreatedDate().getTime());
        workflowRequestLevelDTO.setModifiedById(workflowRequestLevel.getLastUpdatedBy());
        workflowRequestLevelDTO.setLastUpdatedDate(workflowRequestLevel.getLastUpdatedDate().getTime());
        applicationUserSearchRepository.findById(workflowRequestLevelDTO.getOwnerId())
                .ifPresent(owner -> workflowRequestLevelDTO.setOwnerDisplayName(owner.getDisplayName()));
        applicationUserSearchRepository.findById(workflowRequestLevelDTO.getModifiedById())
                .ifPresent(modifiedBy -> workflowRequestLevelDTO.setModifiedBy(modifiedBy.getDisplayName()));
        if (workflowRequestLevelDTO.getStatus() == WorkflowRequestLevelStatus.APPROVED) {
            applicationUserSearchRepository.findById(workflowRequestLevelDTO.getApprovedById())
                    .ifPresent(approvedBy -> workflowRequestLevelDTO.setApprovedBy(approvedBy.getDisplayName()));
        }
        return workflowRequestLevelDTO;
    }

}
