/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on May 6, 2016
 *
 */
package com.nextlabs.destiny.console.dao.impl;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.dao.AppUserPropertiesDao;
import com.nextlabs.destiny.console.model.AppUserProperties;

/**
 *
 * Implementation class for AppUserPropertiesDAO
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@Repository
public class AppUserPropertiesDaoImpl extends
        GenericDaoImpl<AppUserProperties, Long>implements AppUserPropertiesDao {

    private static final Logger log = LoggerFactory
            .getLogger(AppUserPropertiesDaoImpl.class);

    @PersistenceContext(unitName = MGMT_UNIT)
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<AppUserProperties> findByUserId(Long userId) {
        TypedQuery<AppUserProperties> query = entityManager.createNamedQuery(
                AppUserProperties.FIND_BY_USER_ID, AppUserProperties.class);
        query.setParameter("userId", userId);
        List<AppUserProperties> results = query.getResultList();

        log.debug(
                "Properties found for given user [ UserId:{}, No of records: {}]",
                userId, results.size());
        return results;
    }
    
    @Override
    public List<AppUserProperties> findBySuperUserId(Long superUserId) {
        TypedQuery<AppUserProperties> query = entityManager.createNamedQuery(
                AppUserProperties.FIND_BY_SUPER_USER_ID, AppUserProperties.class);
        query.setParameter("superUserId", superUserId);
        List<AppUserProperties> results = query.getResultList();

        log.debug(
                "Properties found for super user [ UserId:{}, No of records: {}]",
                superUserId, results.size());
        return results;
    }
}
