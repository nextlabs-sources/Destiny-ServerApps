/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 22, 2016
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.bluejungle.pf.destiny.parser.PQLException;
import com.nextlabs.destiny.console.dto.common.BaseDTO;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentEntity;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;

/**
 *
 * Deployment history DTO for Policy
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class PolicyDeploymentHistoryDTO extends BaseDTO {

    private static final long serialVersionUID = 848864617781319582L;

    private String revision;
    private String name;
    private String description;
    private long activeFrom;
    private long activeTo;
    private PolicyDTO policyDetail;
    private long createdDate;
    private String createdBy;
    private String modifiedBy;
    private long lastUpdatedDate;
    private String submittedBy;
    private long submittedDate;
    private String actionType;

    /**
     * Transform {@link PolicyDeploymentEntity} entity data to Deployment
     * history
     * 
     * @param deployEntity
     *            {@link PolicyDeploymentEntity}
     * @return {@link PolicyDeploymentHistoryDTO}
     * @throws PQLException
     */
    public static PolicyDeploymentHistoryDTO getDTO(
            PolicyDeploymentEntity deployEntity, String revision,
            ApplicationUserSearchRepository appUserSearchRepository) {
        PolicyDeploymentHistoryDTO dto = new PolicyDeploymentHistoryDTO();
        dto.setId(deployEntity.getId());
        dto.setName(deployEntity.getName());
        dto.setActiveFrom(deployEntity.getActiveFrom());
        dto.setActiveTo(deployEntity.getActiveTo());
        dto.setRevision(revision);

        appUserSearchRepository.findById(deployEntity.getSubmitter())
                .ifPresent(applicationUser -> {
                    dto.setSubmittedBy(applicationUser.getDisplayName());
                    dto.setCreatedBy(applicationUser.getDisplayName());
                });
        dto.setCreatedDate(deployEntity.getSubmittedTime());
        dto.setSubmittedDate(deployEntity.getSubmittedTime());

        appUserSearchRepository.findById(deployEntity.getModifier())
                .ifPresent(applicationUser -> dto.setModifiedBy(applicationUser.getDisplayName()));
        dto.setLastUpdatedDate(deployEntity.getLastModified());

        if(deployEntity.getDeploymentRecord() != null) {
        	dto.setActionType(deployEntity.getDeploymentRecord().getActionType());
        }

        return dto;
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

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
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

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public PolicyDTO getPolicyDetail() {
        return policyDetail;
    }

    public void setPolicyDetail(PolicyDTO policyDetail) {
        this.policyDetail = policyDetail;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public long getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(long submittedDate) {
        this.submittedDate = submittedDate;
    }

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
}
