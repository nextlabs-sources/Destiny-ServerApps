/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 19, 2016
 *
 */
package com.nextlabs.destiny.console.dao.policy.impl;

import com.nextlabs.destiny.console.dao.impl.GenericDaoImpl;
import com.nextlabs.destiny.console.dao.policy.XacmlPolicyDeploymentEntityDao;
import com.nextlabs.destiny.console.model.policy.XacmlPolicyDeploymentEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;

/**
 *
 * DAO Implementation for Xacml Policy Deployment Entity
 *
 * @author Mohammed Sainal Shah
 * @since 9.5
 *
 */
@Repository
public class XacmlPolicyDeploymentEntityDaoImpl
        extends GenericDaoImpl<XacmlPolicyDeploymentEntity, Long>
        implements XacmlPolicyDeploymentEntityDao {

    @PersistenceContext(unitName = MGMT_UNIT)
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
