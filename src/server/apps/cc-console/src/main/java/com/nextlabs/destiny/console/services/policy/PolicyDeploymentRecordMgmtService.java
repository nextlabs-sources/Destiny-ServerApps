/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 22, 2016
 *
 */
package com.nextlabs.destiny.console.services.policy;

import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentRecord;

/**
 *
 *
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public interface PolicyDeploymentRecordMgmtService {

    /**
     * Saves or updates an {@link PolicyDeploymentRecord}
     * 
     * @param policyModel
     *            {@link PolicyDeploymentRecord}
     * @return Saved {@link PolicyDeploymentRecord}
     * @throws ConsoleException
     *             thrown on any error
     * 
     */
    PolicyDeploymentRecord save(PolicyDeploymentRecord deploymentRecord)
            throws ConsoleException;

    /**
     * Finds a Policy Deployment Record by id
     * 
     * @param id
     *            id
     * @return {@link PolicyDeploymentRecord} if found null thrown on any error
     * 
     */
    PolicyDeploymentRecord findById(Long id) throws ConsoleException;

    /**
     * Removes a {@link PolicyDeploymentRecord}
     * 
     * @param policyDeploymentEntity
     *            {@link PolicyDeploymentRecord}
     * @throws ConsoleException
     *             thrown on any error
     * 
     */
    void remove(Long id) throws ConsoleException;
}
