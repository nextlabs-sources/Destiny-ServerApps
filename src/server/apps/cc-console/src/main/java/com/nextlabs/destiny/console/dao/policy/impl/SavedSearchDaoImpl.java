/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 12, 2015
 *
 */
package com.nextlabs.destiny.console.dao.policy.impl;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.dao.impl.GenericDaoImpl;
import com.nextlabs.destiny.console.dao.policy.SavedSearchDao;
import com.nextlabs.destiny.console.enums.SavedSearchType;
import com.nextlabs.destiny.console.model.SavedSearch;

/**
 *
 * DAO interface for Policy search criteria
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Repository
public class SavedSearchDaoImpl extends GenericDaoImpl<SavedSearch, Long>
        implements SavedSearchDao {

    private static final Logger log = LoggerFactory
            .getLogger(SavedSearchDaoImpl.class);

    @PersistenceContext(unitName = MGMT_UNIT)
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<SavedSearch> findByStartwith(String nameStartswith,
            SavedSearchType type) {

        nameStartswith = StringUtils.isBlank(nameStartswith) ? "%"
                : nameStartswith.toLowerCase() + "%";
        TypedQuery<SavedSearch> query = entityManager
                .createNamedQuery(SavedSearch.FIND_BY_NAME, SavedSearch.class);
        query.setParameter("name", nameStartswith);
        query.setParameter("type", type);
        List<SavedSearch> results = query.getResultList();

        log.debug(
                "Saved search criterias found for names starts with: {}, Type : {}, No of records: {}",
                nameStartswith, type, results.size());
        return results;
    }

}
