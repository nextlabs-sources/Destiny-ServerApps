/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 22, 2016
 *
 */
package com.nextlabs.destiny.console.services.policy.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nextlabs.destiny.console.dao.policy.PolicyDeploymentRecordDao;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentRecord;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.policy.PolicyDeploymentRecordMgmtService;

/**
 *
 *
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@Service
public class PolicyDeploymentRecordMgmtServiceImpl
        implements PolicyDeploymentRecordMgmtService {

    private static final Logger log = LoggerFactory
            .getLogger(PolicyDeploymentRecordMgmtServiceImpl.class);

    @Autowired
    private PolicyDeploymentRecordDao deployRecordDao;
    
    @Autowired
    protected MessageBundleService msgBundle;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PolicyDeploymentRecord save(PolicyDeploymentRecord deploymentRecord)
            throws ConsoleException {

        try {
            if (deploymentRecord.getId() == null) {
                deployRecordDao.create(deploymentRecord);
            } else {
                deployRecordDao.update(deploymentRecord);
            }

            log.debug("Deployment Record saved successfully, [ Id: {}]",
                    deploymentRecord.getId());
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while saving a Deployment Entity", e);
        }

        return deploymentRecord;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PolicyDeploymentRecord findById(Long id) throws ConsoleException {
        try {
            PolicyDeploymentRecord deploymentRecord = deployRecordDao.findById(id);

            if (deploymentRecord == null) {
                log.info("Policy Deployment Record not found for id :{}", id);
            }

            return deploymentRecord;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while find a Deployment Record by id",
                    e);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void remove(Long id) throws ConsoleException{
        try {
            PolicyDeploymentRecord deploymentRecord = deployRecordDao.findById(id);
            if (deploymentRecord != null) {
                deployRecordDao.delete(deploymentRecord);
            } else {
                throw new NoDataFoundException(
                        msgBundle.getText("no.entity.found.delete.code"),
                        msgBundle.getText("no.entity.found.delete",
                                "Policy Deployment Record"));
            }

        } catch (Exception e) {
            throw new ConsoleException(String.format(
                    "Error encountered while removing a Deployment Entity [ Id : %s ]",
                    id), e);
        }
    }

}
