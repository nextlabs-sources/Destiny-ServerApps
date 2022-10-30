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
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.GrantedAuthority;

import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;

/**
 *
 * Sub Policy Lite object to reference in {@link PolicyLite}
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class SubPolicyLite implements Serializable {

    private static final long serialVersionUID = 9087933978967035597L;

    @ApiModelProperty(example = "88", position = 10)
    private Long id;

    @ApiModelProperty(value = "The full name of the sub policy.", position = 20,
            example = "ROOT_87/Sample Sub Policy")
    private String policyFullName;

    @ApiModelProperty(value = "The name of the sub policy.", position = 30,
            example = "Sample Sub Policy")
    private String name;

    @ApiModelProperty(position = 40, value = "Access management permissions assigned to this policy. Mainly used in Control Center Console UI.")
    @Transient
    private List<GrantedAuthority> authorities;

    public SubPolicyLite() {

    }

    public SubPolicyLite(PolicyDevelopmentEntity devEntity, String fullName) {
        this.id = (devEntity != null) ? devEntity.getId() : -1;
        this.policyFullName = fullName;
        String[] splits = fullName.split("/", -1);
        this.name = splits[splits.length - 1];
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

    public List<GrantedAuthority> getAuthorities() {
        if (this.authorities == null) {
            this.authorities = new ArrayList<>();
        }
        return authorities;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String toString() {
        return String.format(
                "SubPolicyLite [id=%s, policyFullName=%s, name=%s]", id,
                policyFullName, name);
    }

}
