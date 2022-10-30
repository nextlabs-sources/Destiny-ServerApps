package com.nextlabs.destiny.console.dto.policymgmt.policyworkflow;

import com.nextlabs.destiny.console.model.policyworkflow.WorkflowRequestComment;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Mohammed Sainal Shah
 * @since 2020.10
 */
@Document(indexName = "workflow_request_comments")
@Setting(settingPath = "/search_config/index-settings.json")
public class WorkflowRequestCommentLite implements Serializable {

    private static final long serialVersionUID = -6612632908043694213L;

    @ApiModelProperty(example = "87", position = 10)
    @Id
    @Field(type = FieldType.Long, store = true)
    private Long id;

    @ApiModelProperty("The ID of the entity workflow request.")
    @Field
    private Long workflowRequestId;

    @ApiModelProperty("The ID of the parent comment.")
    @Field
    private Long parentCommentId;

    @Transient
    private List<WorkflowRequestCommentLite> children;

    @ApiModelProperty(value = "The content of the workflow request comment.", example = "Sample comment 1")
    @Field
    private String content;

    @ApiModelProperty(
            value = "Indicates the date at which this workflow request comment was last modified. The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC(coordinated universal time). For example, November 15, 2019 10:48:49.296 AM will be written as 1573786129296.",
            example = "1573786129296")
    @MultiField(mainField = @Field(type = FieldType.Date, store = true), otherFields = {
            @InnerField(suffix = "untouched", type = FieldType.Date, store = true)})
    private long lastUpdatedDate;

    @ApiModelProperty(
            value = "Indicates the date at which this workflow request comment was created. The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC(coordinated universal time). For example, November 15, 2019 10:48:49.296 AM will be written as 1573786129296.",
            example = "1573786129296")
    @MultiField(mainField = @Field(type = FieldType.Date, store = true), otherFields = {
            @InnerField(suffix = "untouched", type = FieldType.Date, store = true)})
    private long createdDate;

    @ApiModelProperty(value = "ID of the user who created the workflow request comment.", example = "0")
    @Field
    private long ownerId;

    @ApiModelProperty(value = "Display name of the user who created the workflow request comment.", example = "user1")
    @Field
    private String ownerDisplayName;

    @ApiModelProperty(value = "ID of the user who last modified the workflow request comment.", example = "0")
    private long modifiedById;

    @ApiModelProperty(value = "Display name of the user who last modified the workflow request comment.", example = "user1")
    private String modifiedBy;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getWorkflowRequestId() {
        return workflowRequestId;
    }

    public void setWorkflowRequestId(Long workflowRequestId) {
        this.workflowRequestId = workflowRequestId;
    }

    public List<WorkflowRequestCommentLite> getChildren() {
        return children;
    }

    public void setChildren(List<WorkflowRequestCommentLite> children) {
        this.children = children;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public long getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(long lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
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

    public static WorkflowRequestCommentLite getLite(WorkflowRequestComment workflowRequestComment, ApplicationUserSearchRepository applicationUserSearchRepository) {
        WorkflowRequestCommentLite workflowRequestCommentLite = new WorkflowRequestCommentLite();
        workflowRequestCommentLite.setId(workflowRequestComment.getId());
        workflowRequestCommentLite.setWorkflowRequestId(workflowRequestComment.getWorkflowRequestId());
        workflowRequestCommentLite.setContent(workflowRequestComment.getContent());
        if (workflowRequestComment.getParentComment() != null) {
            workflowRequestCommentLite.setParentCommentId(workflowRequestComment.getParentComment().getId());
        }
        workflowRequestCommentLite.setChildren(workflowRequestComment.getChildren()
                .parallelStream()
                .map((WorkflowRequestComment comment) -> getLite(comment, applicationUserSearchRepository))
                .collect(Collectors.toList()));
        workflowRequestCommentLite.setOwnerId(workflowRequestComment.getOwnerId());
        workflowRequestCommentLite.setCreatedDate(workflowRequestComment.getCreatedDate().getTime());
        workflowRequestCommentLite.setModifiedById(workflowRequestComment.getLastUpdatedBy());
        workflowRequestCommentLite.setLastUpdatedDate(workflowRequestComment.getLastUpdatedDate().getTime());
        applicationUserSearchRepository.findById(workflowRequestCommentLite.getOwnerId())
                .ifPresent(owner -> workflowRequestCommentLite.setOwnerDisplayName(owner.getDisplayName()));
        applicationUserSearchRepository.findById(workflowRequestCommentLite.getModifiedById())
                .ifPresent(modifiedBy -> workflowRequestCommentLite.setModifiedBy(modifiedBy.getDisplayName()));
        return  workflowRequestCommentLite;
    }

}
