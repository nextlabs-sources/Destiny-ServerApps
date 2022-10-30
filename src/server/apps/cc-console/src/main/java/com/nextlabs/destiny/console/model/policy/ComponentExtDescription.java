/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 10, 2016
 *
 */
package com.nextlabs.destiny.console.model.policy;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * Component related extended description model
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComponentExtDescription implements Serializable {

    private static final long serialVersionUID = 1530320474152269736L;

    private long policyModelId;
    private boolean preCreated = false;

	public long getPolicyModelId() {
        return policyModelId;
    }

    public void setPolicyModelId(long policyModelId) {
        this.policyModelId = policyModelId;
    }
    
    public boolean isPreCreated() {
		return preCreated;
	}

	public void setPreCreated(boolean preCreated) {
		this.preCreated = preCreated;
	}

}
