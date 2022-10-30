/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 22, 2016
 *
 */
package com.nextlabs.destiny.console.dao.policy.impl;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.dao.impl.GenericDaoImpl;
import com.nextlabs.destiny.console.dao.policy.PolicyDeploymentRecordDao;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentRecord;

/**
 *
 *
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@Repository
public class PolicyDeploymentRecordDaoImpl
        extends GenericDaoImpl<PolicyDeploymentRecord, Long>
        implements PolicyDeploymentRecordDao {

    @PersistenceContext(unitName = MGMT_UNIT)
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
