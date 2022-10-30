/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 25, 2016
 *
 */
package com.nextlabs.destiny.console.dao.delegadmin.impl;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.dao.delegadmin.DelegateModelDao;
import com.nextlabs.destiny.console.dao.impl.GenericDaoImpl;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.model.delegadmin.DelegateModel;

/**
 *
 * DAO Implementation for Delegate model configuration
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Repository
public class DelegateModelDaoImpl extends GenericDaoImpl<DelegateModel, Long>
        implements DelegateModelDao {

    private static final Logger log = LoggerFactory
            .getLogger(DelegateModelDaoImpl.class);

    @PersistenceContext(unitName = MGMT_UNIT)
    private EntityManager entityManager;

    /*
     * (non-Javadoc)
     * @see
     * com.nextlabs.destiny.console.dao.impl.GenericDaoImpl#getEntityManager()
     */
    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public List<DelegateModel> findByType(PolicyModelType type) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<DelegateModel> query = cb
                .createQuery(DelegateModel.class);
        Root<DelegateModel> entity = query.from(DelegateModel.class);
        query.where(cb.equal(entity.get("type"), type));

        TypedQuery<DelegateModel> dbQuery = entityManager.createQuery(query);
        List<DelegateModel> results = dbQuery.getResultList();

        log.debug(
                "Delegate Models found by given type [ Type:{}, No of records: {}]",
                type, results.size());
        return results;
    }

    @Override
    public List<DelegateModel> findByTypes(PolicyModelType... types) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<DelegateModel> query = cb
                .createQuery(DelegateModel.class);
        Root<DelegateModel> entity = query.from(DelegateModel.class);
        Predicate[] predicates = new Predicate[types.length];
        for (int i = 0; i < types.length; i++) {
            predicates[i] = cb.equal(entity.get("type"), types[i]);
        }

        if (types.length > 0) {
            query.where(cb.or(predicates));
        }

        TypedQuery<DelegateModel> dbQuery = entityManager.createQuery(query);
        List<DelegateModel> results = dbQuery.getResultList();

        log.debug("Delegate Models found by given types [  No of records: {}]",
                results.size());
        return results;
    }
    
    @Override
    public DelegateModel findByShortName(String shortName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<DelegateModel> query = cb
                .createQuery(DelegateModel.class);
        Root<DelegateModel> entity = query.from(DelegateModel.class);

        query.where(cb.and(cb.equal(entity.get("shortName"), shortName),
                cb.notEqual(entity.get("status"), Status.DELETED)));

        TypedQuery<DelegateModel> dbQuery = entityManager.createQuery(query);
        List<DelegateModel> results = dbQuery.getResultList();

        log.debug(
                "Delegate Models found by given short name [ Type:{}, No of records: {}]",
                shortName, results.size());

        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

}
