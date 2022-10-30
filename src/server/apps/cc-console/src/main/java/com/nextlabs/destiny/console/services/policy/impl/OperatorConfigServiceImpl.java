/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 30, 2015
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

import com.nextlabs.destiny.console.dao.policy.OperatorConfigDao;
import com.nextlabs.destiny.console.enums.DataType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.model.policy.OperatorConfig;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.policy.OperatorConfigService;

/**
 *
 * Service Implementation for Data Type and Operators Configuration
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@Service
public class OperatorConfigServiceImpl implements OperatorConfigService {

    private static final Logger log = LoggerFactory
            .getLogger(OperatorConfigServiceImpl.class);

    @Autowired
    private OperatorConfigDao configDao;

    @Autowired
    protected MessageBundleService msgBundle;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public OperatorConfig save(OperatorConfig operatorConfig)
            throws ConsoleException {

        try {
            if (operatorConfig.getId() == null) {
                configDao.create(operatorConfig);
            } else {
                configDao.update(operatorConfig);
            }
            log.debug("Operator config saved successfully, [ Id: {}]",
                    operatorConfig.getId());
        } catch (Exception ex) {
            throw new ConsoleException(
                    "Error occurred while saving Operator config", ex);
        }
        return operatorConfig;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public OperatorConfig findById(Long id) throws ConsoleException {
        try {
            OperatorConfig operatorConfig = configDao.findById(id);

            if (operatorConfig == null) {
                log.debug("No Operator found for the id : {} ", id);
            }
            return operatorConfig;
        } catch (Exception ex) {
            throw new ConsoleException(
                    "Error occured while finding an operator by Id", ex);
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<OperatorConfig> findAll() throws ConsoleException {
        try {
            return configDao.findAll();
        } catch (Exception ex) {
            throw new ConsoleException(
                    "Error occured while listing all operators ", ex);
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<OperatorConfig> findByDataType(DataType dataType)
            throws ConsoleException {
        try {
            List<OperatorConfig> operators = configDao.findByDataType(dataType);

            log.debug("No of operators found for the type  = "
                    + operators.size());

            return operators;
        } catch (Exception ex) {
            throw new ConsoleException(
                    "Error occured while finding operators by dataType ", ex);
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<DataType> findAllDataTypes() throws ConsoleException {
        try {
            List<DataType> dataTypes = configDao.findAllDataTypes();

            log.debug("No of data types found :" + dataTypes.size());

            return dataTypes;
        } catch (Exception ex) {
            throw new ConsoleException(
                    "Error occured while finding all dataTypes ", ex);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void removeOperatorConfig(Long id) throws ConsoleException {
        OperatorConfig operator = configDao.findById(id);

        if (operator == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.entity.found.delete.code"),
                    msgBundle.getText("no.entity.found.delete", "Operator"));
        } else {
            try {
                configDao.delete(operator);
            } catch (Exception ex) {
                throw new ConsoleException(
                        "Error occured while deleting an operator ", ex);
            }
        }

    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public OperatorConfig findByKeyAndDataType(String key, DataType dataType) 
    		throws ConsoleException {
    	
    	try {
    		OperatorConfig operator =  configDao.findByKeyAndDataType
    				(key, dataType);

            if (operator == null) {
                log.debug("No Operator found for the key : {} ", key);
            }
            return operator;
        } catch (Exception ex) {
            throw new ConsoleException(
                    "Error occured while finding an operator by key "
                    + "and Data Type", ex);
        }
    	
    }

}
