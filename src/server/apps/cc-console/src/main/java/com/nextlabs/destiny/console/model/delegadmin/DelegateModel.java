/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 22, 2016
 *
 */
package com.nextlabs.destiny.console.model.delegadmin;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;

import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.model.policy.PolicyModel;

/**
 * Delegation administrator basic building block
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Entity
@Table(name = "POLICY_MODEL")
@DiscriminatorValue(value = "DELEGATION")
@Document(indexName = "delegate_models")
@Setting(settingPath = "/search_config/index-settings.json")
public class DelegateModel extends PolicyModel {

    private static final long serialVersionUID = 9054625079348845538L;

    /**
     * Default constructor
     * 
     */
    public DelegateModel() {
        super();
    }

    public DelegateModel(Long id, String name, String shortName,
            String description, PolicyModelType type, Status status) {
        this.setId(id);
        this.setName(name);
        this.setShortName(shortName);
        this.setDescription(description);
        this.setType(type);
        this.setStatus(status);
    }

    public DelegateModel(PolicyModel policyModel) {
        this.setId(policyModel.getId());
        this.setName(policyModel.getName());
        this.setShortName(policyModel.getShortName());
        this.setDescription(policyModel.getDescription());
        this.setType(policyModel.getType());
        this.setStatus(policyModel.getStatus());
        this.setTags(policyModel.getTags());
        this.setActions(policyModel.getActions());
        this.setAttributes(policyModel.getAttributes());
        this.setObligations(policyModel.getObligations());
        this.setOwnerId(policyModel.getOwnerId());
        this.setCreatedDate(policyModel.getCreatedDate());
        this.setLastUpdatedBy(policyModel.getLastUpdatedBy());
        this.setLastUpdatedDate(policyModel.getLastUpdatedDate());
        this.setVersion(policyModel.getVersion());
    }
}
