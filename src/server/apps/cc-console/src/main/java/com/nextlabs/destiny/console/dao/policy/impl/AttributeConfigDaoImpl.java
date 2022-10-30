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
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.dao.impl.GenericDaoImpl;
import com.nextlabs.destiny.console.dao.policy.AttributeConfigDao;
import com.nextlabs.destiny.console.dao.policy.OperatorConfigDao;
import com.nextlabs.destiny.console.enums.DataType;
import com.nextlabs.destiny.console.model.policy.AttributeConfig;
import com.nextlabs.destiny.console.model.policy.OperatorConfig;

/**
 *
 * DAO Implementation for Attribute Configuration
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Repository
public class AttributeConfigDaoImpl extends
        GenericDaoImpl<AttributeConfig, Long> implements AttributeConfigDao {

    private static final Logger log = LoggerFactory
            .getLogger(AttributeConfigDaoImpl.class);

    @PersistenceContext(unitName = MGMT_UNIT)
    private EntityManager entityManager;

    @Autowired
    private OperatorConfigDao operatorConfigDao;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<AttributeConfig> loadExternalSubjectAttributes(String type) {
        log.debug("Load external subject attributes for given type : {}", type);

        Map<DataType, Set<OperatorConfig>> operatorConfigs = operatorConfigDao
                .findOperatorConfigMap();

        String queryStr = "SELECT NAME, LABEL, TYPE from dict_type_fields where "
                + " PARENT_TYPE_ID = (SELECT ID FROM DICT_ELEMENT_TYPES WHERE NAME = :type ) AND DELETED = 'N'";

        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter("type", type);

        Set<AttributeConfig> attribConfigs = new TreeSet<>();
        List<Object[]> results = query.getResultList();

        for (Object[] row : results) {
            AttributeConfig config = new AttributeConfig();
            config.setShortName(String.valueOf(row[0]));
            config.setName(String.valueOf(row[1]));
            config.setDataType(DataType.fromAttrType(String.valueOf(row[2])));
            config.setOperatorConfigs(operatorConfigs.get(config.getDataType()));
            attribConfigs.add(config);
        }

        return attribConfigs;
    }

}
