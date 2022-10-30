/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 19, 2016
 *
 */
package com.nextlabs.destiny.console.model.policy;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 *
 * Policy Deployment Entity
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@Entity
@Table(name = PolicyDeploymentEntity.DEPLOY_ENTITY)
public class PolicyDeploymentEntity implements Serializable {

    private static final long serialVersionUID = -7710673981877346875L;

    public static final String DEPLOY_ENTITY = "DEPLOYMENT_ENTITIES";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Version
    private int version;

    @Column(name = "development_id")
    private Long developmentId;

    @Column(name = "dep_record_id")
    private Long depRecordId;

    @Column(name = "name", length = 800, nullable = false)
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "pql", nullable = true)
    private String pql;

    @Lob
    @Column(name = "processedpql", nullable = true)
    private String processedPql;

    @Column(name = "override_cnt")
    private int overrideCount;

    @Column(name = "active_from")
    private long activeFrom;

    @Column(name = "active_to")
    private long activeTo;

    @Basic
    private Character hidden;

    @Column(name = "original_version", nullable = false)
    private int originalVersion;

    @Column(name = "last_modified", nullable = false)
    private Long lastModified;

    @Column(name = "modifier", nullable = true)
    private Long modifier;

    @Column(name = "submitter", nullable = true)
    private Long submitter;

    @Column(name = "submitted_time", nullable = true)
    private Long submittedTime;

    @Transient
    private String actionType;

    @Transient
    private int revisionCount;

    @Transient
    private PolicyDeploymentRecord policyDeploymentRecord;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Long getDevelopmentId() {
        return developmentId;
    }

    public void setDevelopmentId(Long developmentId) {
        this.developmentId = developmentId;
    }

    public Long getDepRecordId() {
        return depRecordId;
    }

    public void setDepRecordId(Long depRecordId) {
        this.depRecordId = depRecordId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPql() {
        return pql;
    }

    public void setPql(String pql) {
        this.pql = pql;
    }

    public String getProcessedPql() {
        return processedPql;
    }

    public void setProcessedPql(String processedPql) {
        this.processedPql = processedPql;
    }

    public int getOverrideCount() {
        return overrideCount;
    }

    public void setOverrideCount(int overrideCount) {
        this.overrideCount = overrideCount;
    }

    public long getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(long activeFrom) {
        this.activeFrom = activeFrom;
    }

    public long getActiveTo() {
        return activeTo;
    }

    public void setActiveTo(long activeTo) {
        this.activeTo = activeTo;
    }

    public Boolean getHidden() {
        if (hidden == null)
            return null;
        return hidden == 'Y' ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setHidden(Boolean hidden) {
        if (hidden == null) {
            this.hidden = null;
        } else {
            this.hidden = hidden == true ? 'Y' : 'N';
        }
    }

    public int getOriginalVersion() {
        return originalVersion;
    }

    public void setOriginalVersion(int originalVersion) {
        this.originalVersion = originalVersion;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public Long getSubmittedTime() {
        return submittedTime;
    }

    public void setSubmittedTime(Long submittedTime) {
        this.submittedTime = submittedTime;
    }

    public Long getModifier() {
        return modifier;
    }

    public void setModifier(Long modifier) {
        this.modifier = modifier;
    }

    public Long getSubmitter() {
        return submitter;
    }

    public void setSubmitter(Long submitter) {
        this.submitter = submitter;
    }

    public PolicyDeploymentRecord getDeploymentRecord() {
        return policyDeploymentRecord;
    }

    public void setDeploymentRecord(
            PolicyDeploymentRecord policyDeploymentRecord) {
        this.policyDeploymentRecord = policyDeploymentRecord;
    }

    public int getRevisionCount() {
        return revisionCount;
    }

    public void setRevisionCount(int revisionCount) {
        this.revisionCount = revisionCount;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}
