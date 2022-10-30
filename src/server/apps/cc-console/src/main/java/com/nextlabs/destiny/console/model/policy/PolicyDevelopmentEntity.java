/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 25, 2015
 *
 */
package com.nextlabs.destiny.console.model.policy;

import static com.nextlabs.destiny.console.utils.SecurityContextUtil.getCurrentUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.bluejungle.pf.destiny.parser.IHasPQL;
import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.nextlabs.destiny.console.dto.Authorizable;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.model.policyworkflow.EntityWorkflowRequest;

/**
 *
 * Policy Development Entity class
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Entity
@Table(name = PolicyDevelopmentEntity.DEV_ENTITY_TABLE)
@NamedQuery(name = PolicyDevelopmentEntity.FIND_BY_TYPE, query = "SELECT e FROM PolicyDevelopmentEntity e WHERE e.type = :type")
@NamedQuery(name = PolicyDevelopmentEntity.FIND_ACTIVE_BY_TYPE, query = "SELECT e FROM PolicyDevelopmentEntity e WHERE e.hidden = :hidden AND e.status != :status AND e.type = :type")
public class PolicyDevelopmentEntity implements Authorizable, Serializable, IHasPQL {

    private static final long serialVersionUID = -5691140347178815912L;

    public static final String DEV_ENTITY_TABLE = "DEVELOPMENT_ENTITIES";
    public static final String FIND_BY_TYPE = "policy.findByType";
    public static final String FIND_ACTIVE_BY_TYPE = "policy.findActiveByType";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Version
    private int version;

    @Column(name = "last_updated")
    private Long lastUpdatedDate;

    @Column(name = "created")
    private Long createdDate;

    @Column(name = "name", length = 1000, nullable = false)
    private String title;

    @Lob
    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "appql", nullable = false)
    private String apPql;

    @Lob
    @Column(name = "pql", nullable = false)
    private String pql;

    @Lob
    @Column(name = "approvedpql", nullable = false)
    private String approvedPql;

    @Column(name = "status", columnDefinition = "char(2)")
    private String status;

    @Column(name = "type", columnDefinition = "char(2)")
    private String type;

    // TODO REMOVE LATER - DUPLICATE COLUMNS
    @Column(name = "owner", nullable = false)
    private Long owner;

    @Column(name = "modifier")
    private Long modifiedBy;

    @Column(name = "last_modified")
    private Long lastModified;

    @Column(name = "submitter", nullable = true)
    private Long submitter;

    @Column(name = "submitted_time", nullable = true)
    private Long submittedTime;

    @Basic
    private Character hidden;

    @Column(name = "is_sub_policy")
    private Character isSubPolicy;

    @Column(name = "has_dependencies")
    private Character hasDependencies;

    @Lob
    @Column(name = "extended_desc")
    private String extendedDescription;

    @Column(name = "FOLDER_ID")
    private Long folderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "FOLDER_ID", insertable = false, updatable = false)
    private Folder folder;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST,
            CascadeType.MERGE })
    @JoinTable(name = "DEVELOPMENT_ENTITIES_TAGS", joinColumns = @JoinColumn(name = "dev_entity_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<TagLabel> tags;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "development_id")
    private List<EntityWorkflowRequest> entityWorkflowRequests = new ArrayList<>();

    @Transient
    private EntityWorkflowRequest activeWorkflowRequest;

    @Transient
    private IDPolicy policy;

    @Transient
    private String actionType;

    @Transient
    private long deploymentTime;

    @Transient
    private int revisionCount;

    @PrePersist
    public void prePersist() {
        this.createdDate = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
        this.owner = getCurrentUser().getUserId();
        this.modifiedBy = getCurrentUser().getUserId();
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModified = System.currentTimeMillis();
        this.modifiedBy = getCurrentUser().getUserId();
    }

    public String getNameFromTitle() {
        String[] splits = this.getTitle().split("/", -1);
        int length = splits.length;
        return (length < 2) ? "" : splits[length - 1];
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApPql() {
        return apPql;
    }

    public void setApPql(String apPql) {
        this.apPql = apPql;
    }

    public String getPql() {
        return pql;
    }

    public void setPql(String pql) {
        this.pql = pql;
    }

    public String getApprovedPql() {
        return approvedPql;
    }

    public void setApprovedPql(String approvedpql) {
        this.approvedPql = approvedpql;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public Long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public int getVersion() {
        return version;
    }

    public Long getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Long lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public IDPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(IDPolicy policy) {
        this.policy = policy;
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

    public Boolean getIsSubPolicy() {
        if (isSubPolicy == null)
            return null;
        return isSubPolicy == 'Y' ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setIsSubPolicy(Boolean isSubPolicy) {
        if (isSubPolicy == null) {
            this.isSubPolicy = null;
        } else {
            this.isSubPolicy = isSubPolicy == true ? 'Y' : 'N';
        }
    }

    public Boolean getHasDependencies() {
        if (hasDependencies == null)
            return null;
        return hasDependencies == 'Y' ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setHasDependencies(Boolean hasDependencies) {
        if (hasDependencies == null) {
            this.hasDependencies = null;
        } else {
            this.hasDependencies = hasDependencies == true ? 'Y' : 'N';
        }
    }

    public String getExtendedDescription() {
        return extendedDescription;
    }

    public void setExtendedDescription(String extendedDescription) {
        this.extendedDescription = extendedDescription;
    }

    public Set<TagLabel> getTags() {
        if (tags == null) {
            tags = new TreeSet<>();
        }
        return tags;
    }

    public void setTags(Set<TagLabel> tags) {
        this.tags = tags;
    }

    public List<EntityWorkflowRequest> getEntityWorkflowRequests() {
        return entityWorkflowRequests;
    }

    public void setEntityWorkflowRequests(List<EntityWorkflowRequest> entityWorkflowRequests) {
        this.entityWorkflowRequests = entityWorkflowRequests;
    }

    public EntityWorkflowRequest getActiveWorkflowRequest() {
        return activeWorkflowRequest;
    }

    public void setActiveWorkflowRequest(EntityWorkflowRequest activeWorkflowRequest) {
        this.activeWorkflowRequest = activeWorkflowRequest;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public long getDeploymentTime() {
        return deploymentTime;
    }

    public void setDeploymentTime(long deploymentTime) {
        this.deploymentTime = deploymentTime;
    }

    public int getRevisionCount() {
        return revisionCount;
    }

    public void setRevisionCount(int revisionCount) {
        this.revisionCount = revisionCount;
    }

    public void setIsSubPolicy(Character isSubPolicy) {
        this.isSubPolicy = isSubPolicy;
    }

    public void setHasDependencies(Character hasDependencies) {
        this.hasDependencies = hasDependencies;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public Long getSubmitter() {
        return submitter;
    }

    public void setSubmitter(Long submitter) {
        this.submitter = submitter;
    }

    public Long getSubmittedTime() {
        return submittedTime;
    }

    public void setSubmittedTime(Long submittedTime) {
        this.submittedTime = submittedTime;
    }

    @Override
    public String toString() {
        return String.format("PolicyDevelopmentEntity [id=%s, title=%s]", id,
                title);
    }

}
