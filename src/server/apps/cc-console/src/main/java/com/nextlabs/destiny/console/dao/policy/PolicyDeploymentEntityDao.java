/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 19, 2016
 *
 */
package com.nextlabs.destiny.console.dao.policy;

import java.util.List;

import com.nextlabs.destiny.console.dao.GenericDao;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentEntity;

/**
 *
 * DAO Interface for Policy Deployment Entity
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public interface PolicyDeploymentEntityDao
        extends GenericDao<PolicyDeploymentEntity, Long> {

    /**
     * <p>
     * Find policies by given policyId/developmentId.
     * 
     * @param developmentId
     * @return list of {@link PolicyDeploymentEntity}
     * 
     *         </p>
     */
    List<PolicyDeploymentEntity> findByPolicyId(Long developmentId);

    /**
     * <p>
     * Checks is a deployed entity is active, given the policyId/developmentId
     * 
     * @param developmentId
     * @return list of {@link PolicyDeploymentEntity}
     * 
     *         </p>
     */
    boolean isEntityDeployed(Long developmentId);

    /**
     * Gets the last active record, given the policyId/developmentId
     *
     * @param developmentId development id
     * @param deployed      true if need to find already deployed records
     * @return list of {@link PolicyDeploymentEntity}
     */
    PolicyDeploymentEntity findLastActiveRecord(Long developmentId, boolean deployed);

    /**
     * Find the latest record with revision count, from Native query
     *
     * @param developmentId development id
     * @param deployed      true if need to find already deployed records
     * @return {@link PolicyDeploymentEntity}
     */
    PolicyDeploymentEntity findLastActiveRecordWithRevisionCount(Long developmentId, boolean deployed);

}
