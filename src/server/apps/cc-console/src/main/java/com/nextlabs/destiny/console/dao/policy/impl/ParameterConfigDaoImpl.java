/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 14, 2016
 *
 */
package com.nextlabs.destiny.console.dao.policy.impl;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.dao.impl.GenericDaoImpl;
import com.nextlabs.destiny.console.dao.policy.ParameterConfigDao;
import com.nextlabs.destiny.console.model.policy.ParameterConfig;

/**
 *
 * DAO Implementation for Parameter configuration
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Repository
public class ParameterConfigDaoImpl extends
        GenericDaoImpl<ParameterConfig, Long>implements ParameterConfigDao {
    
    @PersistenceContext(unitName = MGMT_UNIT)
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
