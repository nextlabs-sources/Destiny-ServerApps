package com.nextlabs.destiny.console.model.policyworkflow;

import com.nextlabs.destiny.console.model.BaseModel;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mohammed Sainal Shah
 * @since 2020.10
 */
@Entity
@Table(name = "WORKFLOW_REQUEST_COMMENT")
public class WorkflowRequestComment extends BaseModel implements Serializable {

    private static final long serialVersionUID = 6863296569353659252L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "ENTITY_WORKFLOW_REQUEST_ID")
    private Long workflowRequestId;

    @Column(name = "PARENT_COMMENT_ID", insertable = false, updatable = false)
    private Long parentCommentId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "PARENT_COMMENT_ID")
    private WorkflowRequestComment parentComment;

    @OneToMany(mappedBy = "parentComment", fetch = FetchType.EAGER)
    private List<WorkflowRequestComment> children = new ArrayList<>();

    @Column(name = "CONTENT")
    private String content;

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Long getWorkflowRequestId() {
        return workflowRequestId;
    }

    public void setWorkflowRequestId(Long workflowRequestId) {
        this.workflowRequestId = workflowRequestId;
    }

    public WorkflowRequestComment getParentComment() {
        return parentComment;
    }

    public void setParentComment(WorkflowRequestComment parentComment) {
        this.parentComment = parentComment;
    }

    public List<WorkflowRequestComment> getChildren() {
        return children;
    }

    public void setChildren(List<WorkflowRequestComment> children) {
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
}
