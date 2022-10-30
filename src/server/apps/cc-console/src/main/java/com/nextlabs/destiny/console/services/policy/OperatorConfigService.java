/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 30, 2015
 *
 */
package com.nextlabs.destiny.console.services.policy;

import java.util.List;

import com.nextlabs.destiny.console.enums.DataType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.policy.OperatorConfig;

/**
 *
 * Service Interface for Data Type and Operators Configuration
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public interface OperatorConfigService {

    /**
     * Saves or updates an Operator configuration in the System
     * 
     * @param {@link
     *            OperatorConfig}
     * @return Saved Operator Configuration entity
     * @throws ConsoleException
     *             thrown on any error
     * 
     */
    OperatorConfig save(OperatorConfig operatorConfig) throws ConsoleException;

    /**
     * Finds an Operator by id
     * 
     * @param id
     *            primary key of the entity
     * @return Operator Configuration entity
     * @throws ConsoleException
     *             thrown on any error
     * 
     */
    OperatorConfig findById(Long id) throws ConsoleException;

    /**
     * Lists all the Operators in the system
     * 
     * @return List of Operator Configuration entities
     * @throws ConsoleException
     *             thrown on any error
     * 
     */
    List<OperatorConfig> findAll() throws ConsoleException;

    /**
     * Load all data types available in the system
     * 
     * @return List of data types
     * @throws ConsoleException
     *             thrown on any error
     * 
     */
    List<DataType> findAllDataTypes() throws ConsoleException;

    /**
     * Finds all Operators associated with a DataType
     * 
     * @param type
     * @return List of Operator Configuration entity
     * @throws ConsoleException
     *             thrown on any error
     * 
     */
    List<OperatorConfig> findByDataType(DataType dataType)
            throws ConsoleException;

    /**
     * Removes an Operator by id
     * 
     * @param id
     *            primary key of the entity
     * @throws ConsoleException
     *             thrown on any error
     */
    void removeOperatorConfig(Long id) throws ConsoleException;
    
    /**
     * Finds an Operator by key and data type
     * 
     * @param key
     *            key of the entity
     * @param dataType
     * 			  dataType of the entity
     * @return Operator Configuration entity
     * @throws ConsoleException
     *             thrown on any error
     * 
     */
    OperatorConfig findByKeyAndDataType(String key, DataType dataType) 
    		throws ConsoleException;

}
