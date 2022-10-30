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
import javax.persistence.Table;

/**
 *
 * Deployment Record Entity
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@Entity
@Table(name = PolicyDeploymentRecord.DEPLOY_RECORD)
public class PolicyDeploymentRecord implements Serializable {

    private static final long serialVersionUID = 794375939109185124L;

    public static final String DEPLOY_RECORD = "DEPLOYMENT_RECORDS";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "action_type", columnDefinition = "char(2)")
    private String actionType;

    @Column(name = "deployment_type", columnDefinition = "char(2)")
    private String deploymentType;

    @Column(name = "as_of", nullable = false)
    private Long asOf;

    @Column(name = "when_requested", nullable = false)
    private Long whenRequested;

    @Column(name = "when_cancelled", nullable = true)
    private Long whenCancelled;

    @Basic
    private Character hidden;

    @Column(name = "deployer", nullable = true)
    private Long deployer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDeploymentType() {
        return deploymentType;
    }

    public void setDeploymentType(String deploymentType) {
        this.deploymentType = deploymentType;
    }

    public Long getAsOf() {
        return asOf;
    }

    public void setAsOf(Long asOf) {
        this.asOf = asOf;
    }

    public Long getWhenRequested() {
        return whenRequested;
    }

    public void setWhenRequested(Long whenRequested) {
        this.whenRequested = whenRequested;
    }

    public Long getWhenCancelled() {
        return whenCancelled;
    }

    public void setWhenCancelled(Long whenCancelled) {
        this.whenCancelled = whenCancelled;
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

    public Long getDeployer() {
        return deployer;
    }

    public void setDeployer(Long deployer) {
        this.deployer = deployer;
    }

}
