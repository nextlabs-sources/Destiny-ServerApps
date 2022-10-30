/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 5, 2015
 *
 */
package com.nextlabs.destiny.console.dao.impl;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.dao.AuditLogDao;
import com.nextlabs.destiny.console.model.AuditLog;

/**
 *
 * DAO implementation for Audit Log
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Repository
public class AuditLogDaoImpl extends GenericDaoImpl<AuditLog, Long>
        implements AuditLogDao {

    private static final Logger log = LoggerFactory
            .getLogger(AuditLogDaoImpl.class);

    @PersistenceContext(unitName = MGMT_UNIT)
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<AuditLog> findByUser(Long userId) {
        TypedQuery<AuditLog> query = entityManager
                .createNamedQuery(AuditLog.FIND_BY_USER_ID, AuditLog.class);
        query.setParameter("ownerId", userId);
        query.setMaxResults(200);

        List<AuditLog> results = query.getResultList();
        log.debug(
                "Audit Logs found for given user [ User id:{}, No of records: {}]",
                userId, results.size());
        return results;
    }

    @Override
    public List<AuditLog> findByComponent(String componentName) {
        TypedQuery<AuditLog> query = entityManager
                .createNamedQuery(AuditLog.FIND_BY_COMPONENT, AuditLog.class);
        query.setParameter("component", componentName);
        query.setMaxResults(200);

        List<AuditLog> results = query.getResultList();

        log.debug(
                "Audit Logs found for given component [ Component:{}, No of records: {}]",
                componentName, results.size());
        return results;
    }

    @Override
    public List<AuditLog> findByLastXRecords(int rowCount) {
        TypedQuery<AuditLog> query = entityManager
                .createNamedQuery(AuditLog.FIND_LAST_X_RECORDS, AuditLog.class);
        query.setMaxResults(rowCount);

        List<AuditLog> results = query.getResultList();
        log.debug("Last saved Audit Logs founds [ No of records:{}]",
                results.size());
        return results;
    }

    @Override
    @Transactional
    public void clearAll() {
        entityManager.createQuery("DELETE FROM AuditLog")
                .executeUpdate();

        log.info("All audit logs were removed");
    }

}
