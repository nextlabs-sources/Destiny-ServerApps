package com.nextlabs.destiny.console.model.policyworkflow;

import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.enums.EntityWorkflowRequestStatus;
import com.nextlabs.destiny.console.model.BaseModel;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mohammed Sainal Shah
 * @since 2020.10
 */
@Entity
@Table(name = "ENTITY_WORKFLOW_REQUEST")
public class EntityWorkflowRequest extends BaseModel implements Serializable {

    private static final long serialVersionUID = 5269127708751961447L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    private DevEntityType devEntityType;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EntityWorkflowRequestStatus status;

    @Column(name = "old_pql")
    private String oldPql;

    @Column(name = "updated_pql")
    private String updatedPQL;

    @Column(name = "development_id")
    private Long developmentId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "entity_workflow_request_id")
    private List<WorkflowRequestLevel> requestLevels = new ArrayList<>();

    @Transient
    private WorkflowRequestLevel activeWorkflowRequestLevel;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "entity_workflow_request_id")
    private List<WorkflowRequestComment> requestComments = new ArrayList<>();

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getOldPql() {
        return oldPql;
    }

    public void setOldPql(String oldPql) {
        this.oldPql = oldPql;
    }

    public String getUpdatedPQL() {
        return updatedPQL;
    }

    public void setUpdatedPQL(String updatedPQL) {
        this.updatedPQL = updatedPQL;
    }

    public Long getDevelopmentId() {
        return developmentId;
    }

    public void setDevelopmentId(Long developmentId) {
        this.developmentId = developmentId;
    }

    public List<WorkflowRequestLevel> getRequestLevels() {
        return requestLevels;
    }

    public void setRequestLevels(List<WorkflowRequestLevel> requestLevels) {
        this.requestLevels = requestLevels;
    }

    public WorkflowRequestLevel getActiveWorkflowRequestLevel() {
        return activeWorkflowRequestLevel;
    }

    public void setActiveWorkflowRequestLevel(WorkflowRequestLevel activeWorkflowRequestLevel) {
        this.activeWorkflowRequestLevel = activeWorkflowRequestLevel;
    }

    public List<WorkflowRequestComment> getRequestComments() {
        return requestComments;
    }

    public void setRequestComments(List<WorkflowRequestComment> requestComments) {
        this.requestComments = requestComments;
    }

    @Override
    public Long getId() {
        return id;
    }
}
