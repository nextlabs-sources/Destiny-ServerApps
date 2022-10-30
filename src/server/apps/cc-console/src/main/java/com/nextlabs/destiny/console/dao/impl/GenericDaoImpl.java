/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 5, 2015
 *
 */
package com.nextlabs.destiny.console.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.nextlabs.destiny.console.dao.GenericDao;

/**
 *
 * Generic DAO implementation
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public abstract class GenericDaoImpl<E extends Object, PK extends Serializable>
        implements GenericDao<E, PK> {

    private final Class<E> persistentClass;

    /**
     * <p>
     * Default constructor.
     * </p>
     **/
    @SuppressWarnings("unchecked")
    public GenericDaoImpl() {
        this.persistentClass = (Class<E>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * <p>
     * Get for entityManager (injected by Spring configuration).
     * </p>
     * 
     * @return anEntityManager the entity manager for this Dao
     **/
    public abstract EntityManager getEntityManager();

    @Override
    public E create(E newInstance) {
        getEntityManager().persist(newInstance);
        return newInstance;
    }

    @Override
    public E update(E inTransientObject) {
        getEntityManager().merge(inTransientObject);
        return inTransientObject;
    }

    @Override
    public void delete(E persistentObject) {
        getEntityManager().remove(persistentObject);
    }

    @Override
    public E findById(PK id) {
        return getEntityManager().find(persistentClass, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<E> findAll() {
        Query query = getEntityManager()
                .createQuery("FROM " + persistentClass.getName());
        return query.getResultList();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public long countAll() {
        CriteriaBuilder criteriaBuilder = getEntityManager()
                .getCriteriaBuilder();
        CriteriaQuery query = criteriaBuilder.createQuery();
        Root<Entity> root = query.from(persistentClass);
        Predicate whereClause = criteriaBuilder
                .equal(criteriaBuilder.literal(1), 1);
        query.select(criteriaBuilder.count(root));
        query.where(whereClause);

        TypedQuery<Long> q = getEntityManager().createQuery(query);

        return q.getSingleResult();
    }
}
