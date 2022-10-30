/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 14, 2016
 *
 */
package com.nextlabs.destiny.console.dao.policy.impl;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.dao.impl.GenericDaoImpl;
import com.nextlabs.destiny.console.dao.policy.ActionConfigDao;
import com.nextlabs.destiny.console.model.policy.ActionConfig;


/**
 *
 * DAO Implementation for Action configuration
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Repository
public class ActionConfigDaoImpl extends GenericDaoImpl<ActionConfig, Long>
        implements ActionConfigDao {

    private static final Logger log = LoggerFactory
            .getLogger(ActionConfigDaoImpl.class);

    @PersistenceContext(unitName = MGMT_UNIT)
    private EntityManager entityManager;

    @Override
    public String getLatestShortCode() {
        String currentVal = null;

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<ActionConfig> query = cb.createQuery(ActionConfig.class);
        Root<ActionConfig> entity = query.from(ActionConfig.class);
        query.where(cb.isNotNull(entity.get("shortCode")));
        query.orderBy(cb.desc(entity.get("shortCode")));

        TypedQuery<ActionConfig> dbQuery = entityManager.createQuery(query);
        List<ActionConfig> results = dbQuery.getResultList();

        if (!results.isEmpty()) {
            ActionConfig actionConfig = results.get(0);
            currentVal = actionConfig.getShortCode();
        }

        log.debug("Current short_code value = {}", currentVal);
        return currentVal;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

}
