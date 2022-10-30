/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 8, 2016
 *
 */
package com.nextlabs.destiny.console.dao.impl;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.dao.SuperApplicationUserDao;
import com.nextlabs.destiny.console.model.SuperApplicationUser;

/**
 *
 * DAO Implementation for Super Application User Entity
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@Repository
public class SuperApplicationUserDaoImpl
        extends GenericDaoImpl<SuperApplicationUser, Long>
        implements SuperApplicationUserDao {

    @PersistenceContext(unitName = MGMT_UNIT)
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

}
