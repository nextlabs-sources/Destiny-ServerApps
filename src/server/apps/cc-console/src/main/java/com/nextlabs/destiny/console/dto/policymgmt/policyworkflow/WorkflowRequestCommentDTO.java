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
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.model.policyworkflow.WorkflowRequestComment;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import io.swagger.annotations.ApiModelProperty;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for workflow request comments
 *
 *
 * @author Mohammed Sainal Shah
 * @since 2020.10
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class WorkflowRequestCommentDTO extends BaseDTO {

    private static final long serialVersionUID = 2884060285697270588L;

    @ApiModelProperty("The ID of the entity workflow request.")
    private Long workflowRequestId;

    @ApiModelProperty("The ID of the parent comment.")
    private Long parentCommentId;
    
    private List<WorkflowRequestCommentDTO> children;

    @ApiModelProperty(value = "The content of the workflow request comment.", example = "Sample comment 1")
    private String content;

    @ApiModelProperty(value = "ID of the user who created the workflow request comment.", example = "1")
    private long ownerId;

    @ApiModelProperty(value = "Display name of the user who created the workflow request comment.", example = "Stuart")
    private String ownerDisplayName;

    @ApiModelProperty(value = "ID of the user who last modified the workflow request comment.", example = "3")
    private long modifiedById;

    @ApiModelProperty(value = "Display name of the user who last modified the workflow request comment.", example = "John")
    private String modifiedBy;

    @ApiModelProperty(
            value = "Indicates the date at which this workflow request comment was created. The value is measured in milliseconds, " +
                    "between the current time and midnight, January 1, 1970 UTC(coordinated universal time). " +
                    "For example, November 15, 2019 10:48:49.296 AM will be written as 1573786129296.",
            example = "1573786129296")
    private long createdDate;

    @ApiModelProperty(
            value = "Indicates the date at which this workflow request comment was last modified. The value is measured in milliseconds, " +
                    "between the current time and midnight, January 1, 1970 UTC(coordinated universal time). " +
                    "For example, November 15, 2019 10:48:49.296 AM will be written as 1573786129296.",
            example = "1573786129296")
    private long lastUpdatedDate;

    public Long getWorkflowRequestId() {
        return workflowRequestId;
    }

    public void setWorkflowRequestId(Long workflowRequestId) {
        this.workflowRequestId = workflowRequestId;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public List<WorkflowRequestCommentDTO> getChildren() {
        return children;
    }

    public void setChildren(List<WorkflowRequestCommentDTO> children) {
        this.children = children;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public static WorkflowRequestCommentDTO getDTO(WorkflowRequestComment workflowRequestComment, ApplicationUserSearchRepository applicationUserSearchRepository) {
        WorkflowRequestCommentDTO workflowRequestCommentDTO = new WorkflowRequestCommentDTO();
        workflowRequestCommentDTO.setId(workflowRequestComment.getId());
        workflowRequestCommentDTO.setContent(workflowRequestComment.getContent());
        if (workflowRequestComment.getParentComment() != null) {
            workflowRequestCommentDTO.setParentCommentId(workflowRequestComment.getParentComment().getId());
        }
        workflowRequestCommentDTO.setChildren(workflowRequestComment.getChildren()
                .parallelStream()
                .map((WorkflowRequestComment comment) -> getDTO(comment, applicationUserSearchRepository))
                .collect(Collectors.toList()));
        workflowRequestCommentDTO.setOwnerId(workflowRequestComment.getOwnerId());
        workflowRequestCommentDTO.setCreatedDate(workflowRequestComment.getCreatedDate().getTime());
        workflowRequestCommentDTO.setModifiedById(workflowRequestComment.getLastUpdatedBy());
        workflowRequestCommentDTO.setLastUpdatedDate(workflowRequestComment.getLastUpdatedDate().getTime());
        return workflowRequestCommentDTO;
    }

    public static WorkflowRequestComment setEntityValues(WorkflowRequestCommentDTO workflowRequestCommentDTO,
                                                         WorkflowRequestComment workflowRequestComment,
                                                         WorkflowRequestComment parentComment) {
        workflowRequestComment.setContent(workflowRequestCommentDTO.getContent());
        workflowRequestComment.setWorkflowRequestId(workflowRequestCommentDTO.getWorkflowRequestId());
        if (parentComment != null) {
            workflowRequestComment.setParentComment(parentComment);
        }
        return workflowRequestComment;
    }

    public String toAuditString(PolicyLite policyLite) {
        JSONObject audit = new JSONObject();
        audit.put("Workflow Request ID", this.id);
        audit.put("Parent Comment ID", this.parentCommentId);
        audit.put("Content", this.content);
        audit.put("Policy ID", policyLite.getId());
        audit.put("Policy Name", policyLite.getName());
        audit.put("Policy Full Name", policyLite.getPolicyFullName());
        return audit.toString(2);
    }
}
