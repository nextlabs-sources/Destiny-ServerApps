/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 19, 2016
 *
 */
package com.nextlabs.destiny.console.services.policy.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nextlabs.destiny.console.dao.policy.PolicyDeploymentEntityDao;
import com.nextlabs.destiny.console.dao.policy.PolicyDeploymentRecordDao;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentEntity;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.policy.PolicyDeploymentEntityMgmtService;

/**
 *
 * Policy Deployment Service Implementation
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@Service
public class PolicyDeploymentEntityMgmtServiceImpl
        implements PolicyDeploymentEntityMgmtService {

    private static final Logger log = LoggerFactory
            .getLogger(PolicyDeploymentEntityMgmtServiceImpl.class);

    @Autowired
    private PolicyDeploymentEntityDao deployEntityDao;
    
    @Autowired
    private PolicyDeploymentRecordDao deploymentRecordDao;
    
    @Autowired
    protected MessageBundleService msgBundle;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PolicyDeploymentEntity save(PolicyDeploymentEntity deployEntity)
            throws ConsoleException {
        try {
            if (deployEntity.getId() == null) {
                deployEntityDao.create(deployEntity);
            } else {
                deployEntityDao.update(deployEntity);
            }

            log.debug("Deployment Entity saved successfully, [ Id: {}]",
                    deployEntity.getId());
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while saving a Deployment Entity", e);
        }

        return deployEntity;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PolicyDeploymentEntity findById(Long id) throws ConsoleException {
        try {
            PolicyDeploymentEntity deployEntity = deployEntityDao.findById(id);

            if (deployEntity == null) {
                log.info("Policy Deployment Entity not found for id :{}", id);
            }

            return deployEntity;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while find a Deployment Entity by id",
                    e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void remove(Long id) throws ConsoleException {
        try {
            PolicyDeploymentEntity deployEntity = deployEntityDao.findById(id);
            if (deployEntity != null) {
                deployEntityDao.delete(deployEntity);
            } else {
                throw new NoDataFoundException(
                        msgBundle.getText("no.entity.found.delete.code"),
                        msgBundle.getText("no.entity.found.delete",
                                "Tag Label"));
            }

        } catch (Exception e) {
            throw new ConsoleException(String.format(
                    "Error encountered while removing a Deployment Entity [ Id : %s ]",
                    id), e);
        }

    }

    @Override
    public List<PolicyDeploymentEntity> findByPolicyId(Long developmentId)
            throws ConsoleException {
        try {
            List<PolicyDeploymentEntity> deployEntities = deployEntityDao
                    .findByPolicyId(developmentId);
            
            for(PolicyDeploymentEntity deployEntity : deployEntities) {
            	deployEntity.setDeploymentRecord(deploymentRecordDao.findById(deployEntity.getDepRecordId()));
            }
            
            return deployEntities;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while fetching a Deployment by policy id",
                    e);
        }
    }

    @Override
    public boolean isEntityDeployed(Long developmentId)
            throws ConsoleException {
        boolean isActive = false;
        try {
            isActive = deployEntityDao.isEntityDeployed(developmentId);
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while checking status of Deployed Entity ",
                    e);
        }

        return isActive;
    }

    @Override
    public PolicyDeploymentEntity getLastActiveRecord(Long developmentId)
            throws ConsoleException {
        try {
            return deployEntityDao.findLastActiveRecord(developmentId, false);
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while fetching a Deployment by policy id",
                    e);
        }
    }

    @Override
    public PolicyDeploymentEntity findLastActiveRecordWithRevisionCount(Long developmentId, boolean deployed)
            throws ConsoleException {
        try {
            return deployEntityDao.findLastActiveRecordWithRevisionCount(developmentId, deployed);
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in fetching last active deployement record by policy id",
                    e);
        }
    }

}
