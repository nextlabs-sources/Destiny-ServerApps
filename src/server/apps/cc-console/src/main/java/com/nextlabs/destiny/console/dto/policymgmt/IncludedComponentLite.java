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

import org.springframework.data.annotation.Transient;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * Included in Component model
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IncludedComponentLite implements Serializable {

    private static final long serialVersionUID = 9087933978967035597L;

    private Long id;
    private String componentFullName;
    private String name;
    private boolean deployed;
    private String status;
    private String policyModel;
    private long lastUpdatedDate;
    private boolean hasIncludedIn;
    private boolean hasSubComponents;

    @Transient
    private List<GrantedAuthority> authorities;

    public IncludedComponentLite() {

    }

    public IncludedComponentLite(Long id, String fullName) {
        this.id = id;
        this.componentFullName = fullName;
        String[] splits = fullName.split("/", -1);
        int length = splits.length;
        this.name = (length <= 1) ? "" : splits[length - 1];
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
                "IncludedComponentLite [id=%s, componentFullName=%s, name=%s]",
                id, componentFullName, name);
    }

}
