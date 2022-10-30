/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 25, 2015
 *
 */
package com.nextlabs.destiny.console.dao.policy;

import java.util.List;

import com.nextlabs.destiny.console.dao.GenericDao;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;

/**
 *
 * DAO interface for Policy Development Entity
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface PolicyDevelopmentEntityDao
        extends GenericDao<PolicyDevelopmentEntity, Long> {

    /**
     * <p>
     * Create new entity
     * </p>
     */
    PolicyDevelopmentEntity create(PolicyDevelopmentEntity entity);

    /**
     * <p>
     * Find entity by id
     * </p>
     */
    PolicyDevelopmentEntity findById(Long entity);

    /**
     * Find policies by given type.
     * 
     * @param type
     * @return list of {@link PolicyDevelopmentEntity}
     */
    List<PolicyDevelopmentEntity> findByType(String type);

    /**
     * Find all active policies by given type.
     * 
     * @param type
     * @return list of {@link PolicyDevelopmentEntity}
     */
    List<PolicyDevelopmentEntity> findActiveRecordsByType(String type);
    
    /**
     * Finds an active entity by name
     * 
     * @param name
     * @return {@link PolicyDevelopmentEntity}
     */
    PolicyDevelopmentEntity findActiveByName(String name);

}