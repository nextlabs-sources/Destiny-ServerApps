/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 19, 2016
 *
 */
package com.nextlabs.destiny.console.services.policy;

import java.util.List;

import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentEntity;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentRecord;

/**
 *
 * Policy Deployment Entity Management Service
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public interface PolicyDeploymentEntityMgmtService {

    /**
     * Saves a {@link PolicyDeploymentEntity}
     * 
     * @param policyDeploymentEntity
     *            {@link PolicyDeploymentEntity}
     * @param policyDeploymentRecord
     *            {@link PolicyDeploymentRecord}
     * @return Saved {@link PolicyDeploymentEntity}
     * @throws ConsoleException
     *             thrown on any error
     * 
     */
    PolicyDeploymentEntity save(PolicyDeploymentEntity deployEntity)
            throws ConsoleException;

    /**
     * Removes a {@link PolicyDeploymentEntity}
     * 
     * @param policyDeploymentEntity
     *            {@link PolicyDeploymentEntity}
     * @param policyDeploymentRecord
     *            {@link PolicyDeploymentRecord}
     * @throws ConsoleException
     *             thrown on any error
     * 
     */
    void remove(Long id) throws ConsoleException;

    /**
     * Finds a Policy Deployment Entity by Policy Id
     * 
     * @param developmentId
     *            developmentId
     * @return {@link PolicyDeploymentEntity} if found null thrown on any error
     * 
     */
    List<PolicyDeploymentEntity> findByPolicyId(Long developmentId)
            throws ConsoleException;

    /**
     * Finds a Policy Deployment Entity by Id
     * 
     * @param id
     *            id
     * @return {@link PolicyDeploymentEntity} if found null thrown on any error
     * 
     */
    PolicyDeploymentEntity findById(Long id) throws ConsoleException;

    /**
     * Get last active record given the policy Id
     * 
     * @param developmentId
     *            developmentId
     * @return {@link PolicyDeploymentEntity} if found null thrown on any error
     * 
     */
    PolicyDeploymentEntity getLastActiveRecord(Long developmentId)
            throws ConsoleException;

    /**
     * Get last active record with revision count for a given entity, Uses a
     * native query and detached from the session context
     *
     * @param developmentId development id
     * @param deployed      deployed true if need to find already deployed records
     * @return {@link PolicyDeploymentEntity}
     * @throws ConsoleException throws at any error
     */
    PolicyDeploymentEntity findLastActiveRecordWithRevisionCount(Long developmentId, boolean deployed)
            throws ConsoleException;

    /**
     * Checks if a given record is active
     * 
     * @param developmentId
     * @return
     * @throws ConsoleException
     */
    boolean isEntityDeployed(Long developmentId) throws ConsoleException;

}
