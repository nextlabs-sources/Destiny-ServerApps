/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 5, 2015
 *
 */
package com.nextlabs.destiny.console.dao.impl;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;
import static org.apache.commons.lang3.StringUtils.isBlank;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.dao.TagLabelDao;
import com.nextlabs.destiny.console.enums.TagType;
import com.nextlabs.destiny.console.model.TagLabel;

/**
 *
 * Tag Label DAO implementation
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Repository
public class TagLabelDaoImpl extends GenericDaoImpl<TagLabel, Long>
        implements TagLabelDao {

    private static final Logger log = LoggerFactory
            .getLogger(TagLabelDaoImpl.class);

    @PersistenceContext(unitName = MGMT_UNIT)
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<TagLabel> findByType(TagType type) {

        TypedQuery<TagLabel> query = entityManager
                .createNamedQuery(TagLabel.FIND_BY_TYPE, TagLabel.class);
        query.setParameter("type", type);
        List<TagLabel> results = query.getResultList();

        log.debug("Labels found by given type [ Type:{}, No of records: {}]",
                type, results.size());
        return results;
    }

    @Override
    public List<TagLabel> findByKey(String key, TagType type) {

        TypedQuery<TagLabel> query = entityManager
                .createNamedQuery(TagLabel.FIND_BY_KEY, TagLabel.class);
        query.setParameter("key", key.toLowerCase());
        query.setParameter("type", type);
        List<TagLabel> results = query.getResultList();

        log.debug("Labels found by given key [ Key:{}, No of records: {}]",
                type, results.size());
        return results;
    }

    @Override
    public List<TagLabel> findByStartwith(String labelStartswith,
            TagType type) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<TagLabel> query = cb.createQuery(TagLabel.class);
        Root<TagLabel> entity = query.from(TagLabel.class);

        labelStartswith = isBlank(labelStartswith) ? ""
                : labelStartswith.toLowerCase();

        if (type != null) {
            query.where(cb.and(
                    cb.like(cb.lower(entity.get("label").as(String.class)),
                            labelStartswith + "%"),
                    cb.equal(entity.get("type"), type)));
        } else {
            query.where(cb.like(cb.lower(entity.get("label").as(String.class)),
                    labelStartswith + "%"));
        }
        query.orderBy(cb.asc(entity.get("label")));

        TypedQuery<TagLabel> dbQuery = entityManager.createQuery(query);
        List<TagLabel> results = dbQuery.getResultList();

        log.debug(
                "Labels found for given label or start with characters [ Starts with:{}, No of records: {}]",
                labelStartswith, results.size());
        return results;
    }

}
