/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 14, 2016
 *
 */
package com.nextlabs.destiny.console.dao.policy;

import java.util.Set;

import com.nextlabs.destiny.console.dao.GenericDao;
import com.nextlabs.destiny.console.model.policy.AttributeConfig;

/**
 * 
 * DAO interface for Attribute Configuration
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface AttributeConfigDao extends GenericDao<AttributeConfig, Long> {

    /**
     * Load external subject attributes
     * 
     * @return set of {@link AttributeConfig}
     */
    Set<AttributeConfig> loadExternalSubjectAttributes(String type);

}
