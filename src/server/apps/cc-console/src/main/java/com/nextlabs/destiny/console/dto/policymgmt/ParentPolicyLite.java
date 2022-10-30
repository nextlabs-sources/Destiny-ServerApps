/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Dec 29, 2015
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * Parent Policy Reference
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class ParentPolicyLite implements Serializable {

    private static final long serialVersionUID = 9087933978967035597L;

    @ApiModelProperty(example = "86", position = 10)
    private Long id;

    @ApiModelProperty(value = "The full name of the parent policy.", position = 20,
            example = "ROOT_87/Sample Parent Policy")
    private String policyFullName;

    @ApiModelProperty(value = "The name of the parent policy.", position = 30,
            example = "Sample Parent Policy")
    private String name;

    @ApiModelProperty(hidden = true)
    private boolean parent;

    @ApiModelProperty(position = 50, value = "Access management permissions assigned to this policy. Mainly used in Control Center Console UI.")
    @Transient
    private List<GrantedAuthority> authorities;

    public ParentPolicyLite() {

    }

    public ParentPolicyLite(String fullName) {
        String[] splits = fullName.split("/", -1);
        int length = splits.length;
        this.name = (length <= 2) ? "" : splits[length - 2];

        if (StringUtils.isNotEmpty(this.name)) {
            this.policyFullName = fullName.substring(0,
                    fullName.lastIndexOf('/'));
            this.parent = true;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPolicyFullName() {
        return policyFullName;
    }

    public void setPolicyFullName(String policyFullName) {
        this.policyFullName = policyFullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isParent() {
        return parent;
    }

    public void setParent(boolean parent) {
        this.parent = parent;
    }

    public List<GrantedAuthority> getAuthorities() {
        if (this.authorities == null) {
            this.authorities = new ArrayList<>();
        }
        return authorities;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

}
