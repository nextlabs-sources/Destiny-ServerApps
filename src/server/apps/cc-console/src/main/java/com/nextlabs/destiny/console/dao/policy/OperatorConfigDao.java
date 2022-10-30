/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 30, 2015
 *
 */
package com.nextlabs.destiny.console.dao.policy;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nextlabs.destiny.console.dao.GenericDao;
import com.nextlabs.destiny.console.enums.DataType;
import com.nextlabs.destiny.console.model.policy.OperatorConfig;

/**
 *
 * DAO interface for Data Type and Operators Configuration
 *
 * @author Amila Silva
 * @author aishwarya
 * @since 8.0
 *
 */
public interface OperatorConfigDao extends GenericDao<OperatorConfig, Long> {

    /**
     * 
     * Finds the list of operators for a given dataType
     * 
     * @param dataType
     * @return List of {@link OperatorConfig}
     * 
     */
    List<OperatorConfig> findByDataType(DataType dataType);

    /**
     * Group all the available operators by data type
     * 
     * @param dataType
     * @return List of {@link OperatorConfig}
     * 
     */
    Map<DataType, Set<OperatorConfig>> findOperatorConfigMap();

    /**
     * 
     * Finds the list of available data types
     * 
     * @return List of data types
     * 
     */
    List<DataType> findAllDataTypes();
    
    /**
     * 
     * Finds the operators for a given key and dataType 
     * 
     * @param dataType
     * @return List of {@link OperatorConfig}
     * 
     */
    OperatorConfig findByKeyAndDataType(String key, DataType dataType);

}
