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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.dao.impl.GenericDaoImpl;
import com.nextlabs.destiny.console.dao.policy.PolicyModelDao;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.model.policy.ActionConfig;
import com.nextlabs.destiny.console.model.policy.PolicyModel;

/**
 *
 * DAO Implementation for Policy model configuration
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Repository
public class PolicyModelDaoImpl extends GenericDaoImpl<PolicyModel, Long>
        implements PolicyModelDao {

    private static final Logger log = LoggerFactory
            .getLogger(PolicyModelDaoImpl.class);

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

    public List<PolicyModel> findByType(PolicyModelType type) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<PolicyModel> query = cb.createQuery(PolicyModel.class);
        Root<PolicyModel> entity = query.from(PolicyModel.class);
        query.where(cb.equal(entity.get("type"), type));

        TypedQuery<PolicyModel> dbQuery = entityManager.createQuery(query);
        List<PolicyModel> results = dbQuery.getResultList();

        log.debug(
                "Policy Models found by given type [ Type:{}, No of records: {}]",
                type, results.size());
        return results;
    }

    @Override
    public List<PolicyModel> findByTypes(PolicyModelType... types) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<PolicyModel> query = cb.createQuery(PolicyModel.class);
        Root<PolicyModel> entity = query.from(PolicyModel.class);
        Predicate[] predicates = new Predicate[types.length];
        for (int i = 0; i < types.length; i++) {
            predicates[i] = cb.equal(entity.get("type"), types[i]);
        }

        if (types.length > 0) {
            query.where(cb.or(predicates));
        }

        TypedQuery<PolicyModel> dbQuery = entityManager.createQuery(query);
        List<PolicyModel> results = dbQuery.getResultList();

        log.debug("Policy Models found by given types [  No of records: {}]",
                results.size());
        return results;
    }
    
    @SuppressWarnings("unchecked")
	@Override
	public ActionConfig findActionByShortCode(String shortCode) {
		Query query = entityManager.createNativeQuery(
				"SELECT NAME, SHORT_CODE, SHORT_NAME FROM PM_ACTION_CONFIG WHERE LOWER(SHORT_CODE) = :shortCode ");
		query.setParameter("shortCode", shortCode);

		List<Object[]> results = query.getResultList();
		if (results.size() == 1) {
			Object[] row = results.get(0);
			ActionConfig actionConfig = new ActionConfig();
			actionConfig.setName(readString(row[0]));
			actionConfig.setShortName( readString(row[1]));
			actionConfig.setShortCode(readString(row[2]));
			log.info("In findActionByShortCode, result found = {}",
					actionConfig.getName());
			return actionConfig;			
		} else {
			return null;
		}

	}
    
    @SuppressWarnings("unchecked")
	@Override
    public String findActionAndModelByShortCode(String actionSN, String modelSN, Long deactivatedId){
    	long deactivatedModelId = 0;
    	if (deactivatedId != null){
    		deactivatedModelId = deactivatedId.longValue();
    	}
    	Query query = entityManager.createNativeQuery(
				"SELECT short_code FROM PM_ACTION_CONFIG act LEFT JOIN POLICY_MODEL p on (act.plcy_model_id = p.id) "
						+ "WHERE p.discriminator != :discriminator AND p.short_name = :modelSN "
						+ "AND act.short_name = :actionSN AND p.id = " + deactivatedModelId);
    	
		query.setParameter("discriminator", "DELEGATION");
		query.setParameter("modelSN", modelSN);
		query.setParameter("actionSN", actionSN);
		

		List<Object> results = query.getResultList();
		if (!results.isEmpty()) {
			Object row = results.get(0);
            return readString(row);
		} else {
			return null;
		}
    }
    
    
    private String readString(Object val) {
        if (val != null) {
            return (String) val;
        }
        return "";
    }

}
