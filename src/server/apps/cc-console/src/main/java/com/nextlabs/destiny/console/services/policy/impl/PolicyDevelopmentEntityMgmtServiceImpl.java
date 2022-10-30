/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 3, 2016
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

import com.nextlabs.destiny.console.dao.policy.PolicyDevelopmentEntityDao;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.policy.PolicyDevelopmentEntityMgmtService;

/**
 *
 * Policy Development Entity management service implementation
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class PolicyDevelopmentEntityMgmtServiceImpl
        implements PolicyDevelopmentEntityMgmtService {

    private static final Logger log = LoggerFactory
            .getLogger(PolicyDevelopmentEntityMgmtServiceImpl.class);

    @Autowired
    private PolicyDevelopmentEntityDao policyDevelopmentDao;

    @Autowired
    protected MessageBundleService msgBundle;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PolicyDevelopmentEntity save(
            PolicyDevelopmentEntity developmentEntity) throws ConsoleException {
        try {
            if (developmentEntity.getId() == null) {
                developmentEntity.setStatus("DR");             
                Boolean bdefault = new Boolean(false);
                developmentEntity.setHidden(bdefault);
                developmentEntity.setHasDependencies('N');
                developmentEntity.setIsSubPolicy('N');
                policyDevelopmentDao.create(developmentEntity);
            } else {
                policyDevelopmentDao.update(developmentEntity);
            }
            log.debug("Development entity saved successfully, [ Id: {}]",
                    developmentEntity.getId());
        } catch (Exception ex) {
            throw new ConsoleException(
                    "Error occurred while saving Development entity config",
                    ex);
        }
        return developmentEntity;
    }

    @Override
    public PolicyDevelopmentEntity findById(Long id) throws ConsoleException {
        try {
            PolicyDevelopmentEntity devEntity = policyDevelopmentDao
                    .findById(id);

            if (devEntity == null) {
                log.debug("No Development entity found for the id : {} ", id);
            }
            return devEntity;
        } catch (Exception ex) {
            throw new ConsoleException(
                    "Error occured while finding an development entity by Id",
                    ex);
        }
    }

    
    @Override
    public List<PolicyDevelopmentEntity> findActiveEntitiesByType(String type)
            throws ConsoleException {
        try {
            List<PolicyDevelopmentEntity> devEntities = policyDevelopmentDao
                    .findActiveRecordsByType(type);

            if (devEntities.isEmpty()) {
                log.debug("No Development entity found for the type : {} ",
                        type);
            }
            return devEntities;
        } catch (Exception ex) {
            throw new ConsoleException(
                    "Error occured while finding an development entity by type",
                    ex);
        }
    }

    /* (non-Javadoc)
     * @see com.nextlabs.destiny.console.services.policy.PolicyDevelopmentEntityMgmtService#findByType(java.lang.String)
     */
    @Override
    public List<PolicyDevelopmentEntity> findByType(String type) throws ConsoleException {
        try {
            List<PolicyDevelopmentEntity> devEntities = policyDevelopmentDao
                    .findByType(type);

            if (devEntities.isEmpty()) {
                log.debug("No Development entity found for the type : {} ",
                        type);
            }
            return devEntities;
        } catch (Exception ex) {
            throw new ConsoleException(
                    "Error occured while finding an development entity by type",
                    ex);
        }
    }
    
}
