/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 5, 2016
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;

/**
 *
 * Sub component Lite object to reference in {@link ComponentLite}
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubComponentLite implements Serializable {

    private static final long serialVersionUID = 9087933978967035597L;

    @ApiModelProperty(example = "88", position = 10)
    private Long id;

    @ApiModelProperty(position = 20, value = "The full name of the sub component.", example = "SUBJECT/Sample Sub Component")
    private String componentFullName;

    @ApiModelProperty(position = 30, value = "The name of the sub component.",
            example = "Sample Sub Component")
    private String name;

    @ApiModelProperty(position = 40, value = "Indicates whether this sub component is deployed or not.")
    private boolean deployed;

    @ApiModelProperty(position = 50, value = "Indicates the current status of the sub component.", example = "DRAFT",
            allowableValues = "DRAFT, APPROVED, DELETED", required = true)
    private String status;

    @ApiModelProperty(position = 90, value = "Policy model of the sub component.", example = "Printer")
    private String policyModel;

    @ApiModelProperty(
            value = "Indicates the date at which this sub component was last modified. The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC (coordinated universal time). For example: November 15, 2019 10:48:49.296 AM is written as 1573786129296.",
            position = 100, example = "1573786129296")
    private long lastUpdatedDate;

    @ApiModelProperty(hidden = true)
    private boolean hasIncludedIn;

    @ApiModelProperty(position = 110, value = "Indicates whether the component have sub components.")
    private boolean hasSubComponents;

    @ApiModelProperty(position = 120, value = "Access management permissions assigned to this component. Mainly used in Control Center Console UI.")
    @Transient
    private List<GrantedAuthority> authorities;

    public SubComponentLite() {

    }

    public SubComponentLite(PolicyDevelopmentEntity devEntity) {
        if(devEntity != null) {
            this.id = devEntity.getId();
            this.componentFullName = devEntity.getTitle();
            String[] splits = devEntity.getTitle().split("/", -1);
            this.name = splits[splits.length - 1];
        } else {
            this.id = -1L;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComponentFullName() {
        return componentFullName;
    }

    public void setComponentFullName(String componentFullName) {
        this.componentFullName = componentFullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDeployed() {
        return deployed;
    }

    public void setDeployed(boolean deployed) {
        this.deployed = deployed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(long lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public boolean isHasIncludedIn() {
        return hasIncludedIn;
    }

    public void setHasIncludedIn(boolean hasIncludedIn) {
        this.hasIncludedIn = hasIncludedIn;
    }

    public boolean isHasSubComponents() {
        return hasSubComponents;
    }

    public void setHasSubComponents(boolean hasSubComponents) {
        this.hasSubComponents = hasSubComponents;
    }

    public String getPolicyModel() {
        return policyModel;
    }

    public void setPolicyModel(String policyModel) {
        this.policyModel = policyModel;
    }

    public List<GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            authorities = new ArrayList<>();
        }
        return authorities;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String toString() {
        return String.format(
                "SubComponentLite [id=%s, componentFullName=%s, name=%s]", id,
                componentFullName, name);
    }

}
