/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 3, 2016
 *
 */
package com.nextlabs.destiny.console.services.policy;

import java.util.List;

import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;

/**
 * Policy Development Entity management service
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface PolicyDevelopmentEntityMgmtService {

    /**
     * Saves or updates an {@link PolicyDevelopmentEntity}
     * 
     * @param policyModel
     *            {@link PolicyDevelopmentEntity}
     * @return Saved {@link PolicyDevelopmentEntity}
     * @throws ConsoleException
     *             thrown on any error
     * 
     */
    PolicyDevelopmentEntity save(PolicyDevelopmentEntity developmentEntity)
            throws ConsoleException;

    /**
     * Finds a Policy Development Entity by id
     * 
     * @param id
     *            id
     * @return {@link PolicyDevelopmentEntity} if found null thrown on any error
     * 
     */
    PolicyDevelopmentEntity findById(Long id) throws ConsoleException;
    
    /**
     * Finds a Policy Development Entity by type
     * 
     * @param type
     *            type
     * @return {@link PolicyDevelopmentEntity} if found null thrown on any error
     * 
     */
    List<PolicyDevelopmentEntity> findActiveEntitiesByType(String type) throws ConsoleException;

    /**
     * @param key
     * @return
     * @throws ConsoleException 
     */
    List<PolicyDevelopmentEntity> findByType(String key) throws ConsoleException;

}
