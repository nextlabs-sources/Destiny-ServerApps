/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 30, 2015
 *
 */
package com.nextlabs.destiny.console.dao.policy.impl;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.nextlabs.destiny.console.dao.policy.OperatorConfigDao;
import com.nextlabs.destiny.console.enums.DataType;
import com.nextlabs.destiny.console.model.policy.OperatorConfig;

/**
 *
 * DAO Implementation for Data Type and Operators Configuration
 * 
 * @author aishwarya
 * @since 8.0
 *
 */
@Repository
public class OperatorConfigDaoImpl extends GenericDaoImpl<OperatorConfig, Long>
        implements OperatorConfigDao {

    private static final Logger log = LoggerFactory
            .getLogger(OperatorConfigDaoImpl.class);

    @PersistenceContext(unitName = MGMT_UNIT)
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<OperatorConfig> findByDataType(DataType dataType) {
        TypedQuery<OperatorConfig> query = entityManager.createNamedQuery(
                OperatorConfig.FIND_BY_DATA_TYPE, OperatorConfig.class);
        query.setParameter("dataType", dataType);
        List<OperatorConfig> results = query.getResultList();

        log.debug("Operators found by given type [ Type:{}, No of operators: {}]",
                dataType, results.size());

        return results;
    }

    @Override
    public Map<DataType, Set<OperatorConfig>> findOperatorConfigMap() {
        Map<DataType, Set<OperatorConfig>> operatorConfigMap = new EnumMap<>(DataType.class);

        for (DataType dataType : DataType.values()) {
            List<OperatorConfig> operatorsList = findByDataType(dataType);

            Set<OperatorConfig> operatorsSet = new HashSet<>(
                    operatorsList);
            operatorConfigMap.put(dataType, operatorsSet);
        }

        log.debug("Size of operatorConfig Map {}", operatorConfigMap.size());
        return operatorConfigMap;
    }

    @Override
    public List<DataType> findAllDataTypes() {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Object> query = criteriaBuilder.createQuery();
        Root<OperatorConfig> from = query.from(OperatorConfig.class);
        query.select(from.get("dataType")).groupBy(from.get("dataType"));
        query.distinct(true);

        List<Object> results = entityManager.createQuery(query).getResultList();

        List<DataType> dataTypes = new ArrayList<>();
        for (Object obj : results) {
            dataTypes.add((DataType) obj);
        }

        return dataTypes;
    }
    
    @Override
    public  /**
     * 
     * Finds the operators for a given key and dataType 
     * 
     * @param dataType
     * @return List of {@link OperatorConfig}
     * 
     */
    OperatorConfig findByKeyAndDataType(String key, DataType dataType){
    	 
    	 CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    	 
         CriteriaQuery<OperatorConfig> query = cb.createQuery(
        		 OperatorConfig.class);
         Root<OperatorConfig> entity = query.from(
        		 OperatorConfig.class);

         query.where(cb.and(cb.equal(entity.get("key"), key),
                 cb.equal(entity.get("dataType"), dataType)));

         TypedQuery<OperatorConfig> dbQuery = entityManager
                 .createQuery(query);
         List<OperatorConfig> results = dbQuery.getResultList();

         log.debug(
                 "Entities found by given key and dataType [ Key :{}, DataType; {}, "
                 + "No of records: {}]",key, dataType, results.size());

         if (results.isEmpty()) {
             return null;
         } else {
             return results.get(0);
         }
    }

}
