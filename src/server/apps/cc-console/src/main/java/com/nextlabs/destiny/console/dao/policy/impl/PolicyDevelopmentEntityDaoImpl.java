/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 25, 2015
 *
 */
package com.nextlabs.destiny.console.dao.policy.impl;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;
import static com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus.DELETED;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.nextlabs.destiny.console.enums.EntityWorkflowRequestStatus;
import com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus;
import com.nextlabs.destiny.console.enums.WorkflowRequestLevelStatus;
import com.nextlabs.destiny.console.model.policyworkflow.EntityWorkflowRequest;
import com.nextlabs.destiny.console.model.policyworkflow.WorkflowRequestLevel;
import com.nextlabs.destiny.console.repositories.EntityWorkflowRequestRepository;
import com.nextlabs.destiny.console.repositories.WorkflowRequestLevelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.dao.impl.GenericDaoImpl;
import com.nextlabs.destiny.console.dao.policy.PolicyDeploymentEntityDao;
import com.nextlabs.destiny.console.dao.policy.PolicyDeploymentRecordDao;
import com.nextlabs.destiny.console.dao.policy.PolicyDevelopmentEntityDao;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentEntity;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentRecord;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;

/**
 * DAO implementation for Policy Development Entity
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Repository
public class PolicyDevelopmentEntityDaoImpl
        extends GenericDaoImpl<PolicyDevelopmentEntity, Long>
        implements PolicyDevelopmentEntityDao {

    private static final Logger log = LoggerFactory
            .getLogger(PolicyDevelopmentEntityDaoImpl.class);

    @PersistenceContext(unitName = MGMT_UNIT)
    private EntityManager entityManager;

    private PolicyDeploymentEntityDao deploymentEntityDao;
    private PolicyDeploymentRecordDao deploymentRecordDao;
    private EntityWorkflowRequestRepository entityWorkflowRequestRepository;
    private WorkflowRequestLevelRepository workflowRequestLevelRepository;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public PolicyDevelopmentEntity findById(Long id) {
        PolicyDevelopmentEntity entity = super.findById(id);
        if (entity != null) {
            int revisionCount = deploymentEntityDao
                    .findByPolicyId(entity.getId()).size();
            if (revisionCount > 0) {
                entity.setRevisionCount(revisionCount);

                PolicyDeploymentEntity deployEntity = deploymentEntityDao
                        .findLastActiveRecord(entity.getId(), true);
                if (deployEntity != null && deployEntity.getDeploymentRecord() != null) {
                    entity.setActionType(deployEntity.getDeploymentRecord().getActionType());
                }
                PolicyDeploymentEntity pendingDeploymentEntity = deploymentEntityDao
                        .findLastActiveRecord(entity.getId(), false);
                if (pendingDeploymentEntity != null) {
                    PolicyDeploymentRecord deploymentRecord =
                            deploymentRecordDao.findById(pendingDeploymentEntity.getDepRecordId());
                    if (deploymentRecord != null && deploymentRecord.getAsOf() != null) {
                        entity.setDeploymentTime(deploymentRecord.getAsOf());
                    }
                }
            }
            populateWorkflowRequest(entity);
        }
        return entity;
    }

    @Override
    public List<PolicyDevelopmentEntity> findByType(String type) {
        List<PolicyDevelopmentEntity> entities;
        TypedQuery<PolicyDevelopmentEntity> query = entityManager
                .createNamedQuery(PolicyDevelopmentEntity.FIND_BY_TYPE,
                        PolicyDevelopmentEntity.class);
        query.setParameter("type", type);
        entities = query.getResultList();
        populateWorkflowRequest(entities.toArray(new PolicyDevelopmentEntity[0]));

        log.debug(
                "Policy Development entities found by given type [ Type:{}, No of records: {}]",
                type, entities.size());
        return entities;
    }

    @Override
    public List<PolicyDevelopmentEntity> findActiveRecordsByType(String type) {
        List<PolicyDevelopmentEntity> entities;
        TypedQuery<PolicyDevelopmentEntity> query = entityManager
                .createNamedQuery(PolicyDevelopmentEntity.FIND_ACTIVE_BY_TYPE,
                        PolicyDevelopmentEntity.class);

        query.setParameter("hidden", 'N');
        query.setParameter("type", type);
        query.setParameter("status", DELETED.getKey());
        entities = query.getResultList();
        populateWorkflowRequest(entities.toArray(new PolicyDevelopmentEntity[0]));

        log.debug(
                "Policy Development entities found by given type [ Type:{}, No of records: {}]",
                type, entities.size());
        return entities;
    }

    @Override
    public PolicyDevelopmentEntity findActiveByName(String name) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<PolicyDevelopmentEntity> query = cb
                .createQuery(PolicyDevelopmentEntity.class);
        Root<PolicyDevelopmentEntity> entity = query
                .from(PolicyDevelopmentEntity.class);

        query.where(cb.and(cb.equal(entity.get("title"), name),
                cb.notEqual(entity.get("status"), "DE")));

        TypedQuery<PolicyDevelopmentEntity> dbQuery = entityManager
                .createQuery(query);
        List<PolicyDevelopmentEntity> results = dbQuery.getResultList();
        populateWorkflowRequest(results.toArray(new PolicyDevelopmentEntity[0]));

        log.debug(
                "Entities found by given name and status [ Name :{}, No of records: {}]",
                name, results.size());

        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    private void populateWorkflowRequest(PolicyDevelopmentEntity ... policyDevelopmentEntities) {
        for (PolicyDevelopmentEntity entity: policyDevelopmentEntities) {
            if (entity.getStatus().equals(PolicyDevelopmentStatus.DRAFT.getKey())) {
                EntityWorkflowRequest entityWorkflowRequest = entityWorkflowRequestRepository.findActiveEntityWorkflowRequest(entity.getId());
                if (entityWorkflowRequest != null) {
                    WorkflowRequestLevel workflowRequestLevel;
                    if (entityWorkflowRequest.getStatus() == EntityWorkflowRequestStatus.APPROVED) {
                        workflowRequestLevel = workflowRequestLevelRepository
                                .findByEntityWorkflowRequestIdAndStatusIn(entityWorkflowRequest.getId(),
                                WorkflowRequestLevelStatus.APPROVED);
                    } else {
                        workflowRequestLevel = workflowRequestLevelRepository
                                .findByEntityWorkflowRequestIdAndStatusIn(entityWorkflowRequest.getId(),
                                        WorkflowRequestLevelStatus.PENDING,
                                        WorkflowRequestLevelStatus.REQUESTED_AMENDMENT);
                    }
                    entityWorkflowRequest.setActiveWorkflowRequestLevel(workflowRequestLevel);
                    entity.setActiveWorkflowRequest(entityWorkflowRequest);
                }
            } else if (entity.getStatus().equals(PolicyDevelopmentStatus.APPROVED.getKey())) {
                entity.setActiveWorkflowRequest(null);
            }
        }
    }

    public Boolean getHiddenChar(String hidden) {
        if (hidden == null) {
            return false;
        } else {
            return hidden.equals("Y");
        }
    }

    @Autowired
    public void setEntityWorkflowRequestRepository(EntityWorkflowRequestRepository entityWorkflowRequestRepository) {
        this.entityWorkflowRequestRepository = entityWorkflowRequestRepository;
    }

    @Autowired
    public void setWorkflowRequestLevelRepository(WorkflowRequestLevelRepository workflowRequestLevelRepository) {
        this.workflowRequestLevelRepository = workflowRequestLevelRepository;
    }

    @Autowired
    public void setDeploymentEntityDao(PolicyDeploymentEntityDao deploymentEntityDao) {
        this.deploymentEntityDao = deploymentEntityDao;
    }

    @Autowired
    public void setDeploymentRecordDao(PolicyDeploymentRecordDao deploymentRecordDao) {
        this.deploymentRecordDao = deploymentRecordDao;
    }
}
