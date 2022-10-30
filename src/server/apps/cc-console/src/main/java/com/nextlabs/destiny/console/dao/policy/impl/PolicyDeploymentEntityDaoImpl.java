/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 19, 2016
 *
 */
package com.nextlabs.destiny.console.dao.policy.impl;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.dao.impl.GenericDaoImpl;
import com.nextlabs.destiny.console.dao.policy.PolicyDeploymentEntityDao;
import com.nextlabs.destiny.console.dao.policy.PolicyDeploymentRecordDao;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentEntity;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentRecord;

/**
 *
 * DAO Implementation for Policy Deployment Entity
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@Repository
public class PolicyDeploymentEntityDaoImpl
        extends GenericDaoImpl<PolicyDeploymentEntity, Long>
        implements PolicyDeploymentEntityDao {

    private static final Logger log = LoggerFactory
            .getLogger(PolicyDeploymentEntityDaoImpl.class);

    @PersistenceContext(unitName = MGMT_UNIT)
    private EntityManager entityManager;

    @Autowired
    private PolicyDeploymentRecordDao policyDeploymentRecordDao;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<PolicyDeploymentEntity> findByPolicyId(Long developmentId) {

        String queryStr = "SELECT e FROM PolicyDeploymentEntity e"
                + " WHERE e.developmentId = :developmentId"
                + " AND e.overrideCount = :ovrCount "
                + " ORDER BY e.activeTo DESC";

        String depRecordQueryStr = "SELECT r FROM "
                + " PolicyDeploymentRecord r  WHERE r.id = :depRecId";

        TypedQuery<PolicyDeploymentEntity> query = entityManager
                .createQuery(queryStr, PolicyDeploymentEntity.class);
        query.setParameter("developmentId", developmentId);
        query.setParameter("ovrCount", 0);

        List<PolicyDeploymentEntity> results = query.getResultList();
        PolicyDeploymentRecord record = null;
        if (!results.isEmpty()) {
            PolicyDeploymentEntity deployEntity = results.get(0);

            TypedQuery<PolicyDeploymentRecord> depRecQuery = entityManager
                    .createQuery(depRecordQueryStr,
                            PolicyDeploymentRecord.class);
            depRecQuery.setParameter("depRecId", deployEntity.getDepRecordId());

            List<PolicyDeploymentRecord> depResults = depRecQuery
                    .getResultList();
            record = depResults.get(0);
        }

        for (PolicyDeploymentEntity entity : results) {
            entity.setDeploymentRecord(record);
        }

        log.debug(
                "Policy deployement history for given policyId found, [ PolicyId :{}, No of revision :{}]",
                developmentId, results.size());
        return results;
    }

    public boolean isEntityDeployed(Long developmentId) {
        boolean isActive = false;

        String selectQuery = "SELECT e.ID FROM "
                + PolicyDeploymentEntity.DEPLOY_ENTITY + " e INNER JOIN "
                + PolicyDeploymentRecord.DEPLOY_RECORD
                + " r ON (e.DEP_RECORD_ID = r.ID) WHERE e.DEVELOPMENT_ID = :developmentId "
                + "AND e.OVERRIDE_CNT = :ovrCount AND e.ACTIVE_TO > :active_to AND r.ACTION_TYPE = :actionType";

        Query query = entityManager.createNativeQuery(selectQuery);
        query.setParameter("developmentId", developmentId);
        query.setParameter("ovrCount", 0);
        query.setParameter("active_to", System.currentTimeMillis());
        query.setParameter("actionType", "DE");

        @SuppressWarnings("rawtypes")
        List results = query.getResultList();
        if (!results.isEmpty()) {
            isActive = true;
        }

        log.debug("Policy Id and Active status [ PolicyId:{}, Active : {}]",
                developmentId, isActive);
        return isActive;
    }

    public PolicyDeploymentEntity findLastActiveRecord(Long developmentId, boolean deployed) {
        PolicyDeploymentEntity deployEntity = null;
        StringBuilder queryStr = new StringBuilder("SELECT e FROM PolicyDeploymentEntity e"
                + " WHERE e.developmentId = :developmentId AND e.overrideCount = :ovrCount AND e.activeTo > :activeTo");
        if (deployed) {
            queryStr.append(" AND e.activeFrom <= :activeFrom");
        }
        queryStr.append(" ORDER BY e.id DESC");

        TypedQuery<PolicyDeploymentEntity> query = entityManager
                .createQuery(queryStr.toString(), PolicyDeploymentEntity.class);
        query.setParameter("developmentId", developmentId);
        query.setParameter("ovrCount", 0);
        query.setParameter("activeTo", System.currentTimeMillis());
        if (deployed) {
            query.setParameter("activeFrom", System.currentTimeMillis());
        }

        List<PolicyDeploymentEntity> results = query.getResultList();
        if (!results.isEmpty()) {
            deployEntity = results.get(0);
        }
        return deployEntity;
    }

    @SuppressWarnings("unchecked")
    public PolicyDeploymentEntity findLastActiveRecordWithRevisionCount(Long developmentId, boolean deployed) {

        PolicyDeploymentEntity deployEntity = null;
        StringBuilder queryStr = new StringBuilder();
        queryStr.append(
                "SELECT l.DEPLOYEMENT_ID, l.DEVELOPMENT_ID,  l.ACTIVE_FROM, l.ACTIVE_TO, l.ACTION_TYPE, " +
                        "l.DEPLOY_ENT_COUNT, l.DEP_RECORD_ID ");
        queryStr.append(" FROM ( ");
        queryStr.append(
                " SELECT j.DEVELOPMENT_ID DEVELOPMENT_ID, ab.ID DEPLOYEMENT_ID, ab.ACTIVE_FROM ACTIVE_FROM , ab" +
                        ".ACTIVE_TO ACTIVE_TO, ab.DEP_RECORD_ID, cd.ACTION_TYPE ACTION_TYPE, j.DEPLOY_ENT_COUNT DEPLOY_ENT_COUNT ");
        queryStr.append(" FROM DEPLOYMENT_ENTITIES ab ");
        queryStr.append(" INNER JOIN DEPLOYMENT_RECORDS cd ");
        queryStr.append(" ON (cd.ID = ab.DEP_RECORD_ID) ");
        queryStr.append(" INNER JOIN ");
        queryStr.append(
                " (SELECT k.DEVELOPMENT_ID, COUNT(k.DEVELOPMENT_ID) DEPLOY_ENT_COUNT ");
        queryStr.append(" FROM ");
        queryStr.append(
                " (SELECT de.DEVELOPMENT_ID, dr.ACTION_TYPE ACTION_TYPE, dr.DEPLOYMENT_TYPE DEPLOYMENT_TYPE ");
        queryStr.append(" FROM DEPLOYMENT_ENTITIES de ");
        queryStr.append(" INNER JOIN DEPLOYMENT_RECORDS dr ");
        queryStr.append(" ON(dr.ID = de.DEP_RECORD_ID) ");
        queryStr.append(" WHERE de.OVERRIDE_CNT = :ovrCount ");
        queryStr.append(" ) k GROUP BY k.DEVELOPMENT_ID ");
        queryStr.append(
                " ) j ON (j.DEVELOPMENT_ID = ab.DEVELOPMENT_ID AND ab.OVERRIDE_CNT = :ovrCount) ) l WHERE l.ACTIVE_TO > :activeTo AND l.DEVELOPMENT_ID = :developmentId");
        if (deployed) {
            queryStr.append(" AND l.ACTIVE_FROM <= :activeFrom");
        }
        queryStr.append(" ORDER BY l.DEPLOYEMENT_ID DESC");

        Query query = entityManager.createNativeQuery(queryStr.toString());
        query.setParameter("ovrCount", 0);
        query.setParameter("activeTo", System.currentTimeMillis());
        query.setParameter("developmentId", developmentId);
        if (deployed) {
            query.setParameter("activeFrom", System.currentTimeMillis());
        }

        List<Object[]> results = query.getResultList();
        if (!results.isEmpty()) {
            Object[] row = results.get(0);
            deployEntity = new PolicyDeploymentEntity();
            deployEntity.setId(readLong(row[0]));
            deployEntity.setDevelopmentId(readLong(row[1]));
            deployEntity.setActiveFrom(readLong(row[2]));
            deployEntity.setActiveTo(readLong(row[3]));
            deployEntity.setActionType(readString(row[4]));
            deployEntity.setRevisionCount(readInt(row[5]));
            deployEntity.setDepRecordId(readLong(row[6]));
        }

        log.debug("Last active entity found by given policyId [ PolicyId:{}]",
                developmentId);
        return deployEntity;
    }

    private String readString(Object val) {
        if (val != null) {
            return (String) val;
        }
        return null;
    }

    private long readLong(Object val) {
        if (val != null) {
            if (val instanceof Integer) {
                return ((Integer) val).longValue();
            } else if (val instanceof BigDecimal){
                return ((BigDecimal) val).longValue();
            } else if (val instanceof BigInteger){
                return ((BigInteger) val).longValue();
            }
        }
        return 0L;
    }

	private int readInt(Object val) {
		if (val != null) {
			if (val instanceof Integer) {
				return ((Integer) val).intValue();
			} else if (val instanceof BigDecimal) {
				return ((BigDecimal) val).intValue();
			} else if (val instanceof BigInteger) {
				return ((BigInteger) val).intValue();
			}
		}
		return 0;
	}
}
