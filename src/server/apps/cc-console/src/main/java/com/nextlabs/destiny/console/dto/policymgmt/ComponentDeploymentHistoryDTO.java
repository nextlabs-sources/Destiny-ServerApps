/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 23, 2016
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
 * Deployment history DTO for Components
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class ComponentDeploymentHistoryDTO extends BaseDTO {
    private static final long serialVersionUID = 848864617781319582L;

    private String revision;
    private String name;
    private String description;
    private long activeFrom;
    private long activeTo;
    private ComponentDTO componentDetail;
    private long createdDate;
    private String createdBy;
    private String modifiedBy;
    private long lastUpdatedDate;
    private String submittedBy;
    private long submittedDate;

    /**
     * Transform {@link PolicyDeploymentEntity} entity data to Deployment
     * history
     * 
     * @param deployEntity
     *            {@link PolicyDeploymentEntity}
     * @return {@link ComponentDeploymentHistoryDTO}
     * @throws PQLException
     */
    public static ComponentDeploymentHistoryDTO getDTO(
            PolicyDeploymentEntity deployEntity, String revision,
            ApplicationUserSearchRepository appUserSearchRepository) {
        ComponentDeploymentHistoryDTO dto = new ComponentDeploymentHistoryDTO();
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

        return dto;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
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

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public ComponentDTO getComponentDetail() {
        return componentDetail;
    }

    public void setComponentDetail(ComponentDTO componentDetail) {
        this.componentDetail = componentDetail;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public long getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(long lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
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

}
