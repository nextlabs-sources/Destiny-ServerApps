package com.nextlabs.destiny.console.model.policyworkflow;

import com.nextlabs.destiny.console.enums.WorkflowRequestLevelStatus;
import com.nextlabs.destiny.console.model.BaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Mohammed Sainal Shah
 * @since 2020.10
 */
@Entity
@Table(name = "WORKFLOW_REQUEST_LEVEL")
public class WorkflowRequestLevel extends BaseModel implements Serializable {

    private static final long serialVersionUID = 8840570286425755356L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "entity_workflow_request_id")
    private Long entityWorkflowRequestId;

    @OneToOne(optional = false)
    @JoinColumn(name = "level_id")
    @OrderBy("levelOrder ASC")
    private WorkflowLevel workflowLevel;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private WorkflowRequestLevelStatus status;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_time")
    private Date approvedTime;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntityWorkflowRequestId() {
        return entityWorkflowRequestId;
    }

    public void setEntityWorkflowRequestId(Long entityWorkflowRequestId) {
        this.entityWorkflowRequestId = entityWorkflowRequestId;
    }

    public WorkflowLevel getWorkflowLevel() {
        return workflowLevel;
    }

    public void setWorkflowLevel(WorkflowLevel workflowLevel) {
        this.workflowLevel = workflowLevel;
    }

    public WorkflowRequestLevelStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowRequestLevelStatus status) {
        this.status = status;
    }

    public Long getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Date getApprovedTime() {
        return approvedTime;
    }

    public void setApprovedTime(Date approvedTime) {
        this.approvedTime = approvedTime;
    }

    @Override
    public Long getId() {
        return id;
    }
}
