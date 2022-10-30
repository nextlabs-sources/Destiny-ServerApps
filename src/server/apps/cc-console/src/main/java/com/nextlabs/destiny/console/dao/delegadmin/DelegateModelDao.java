/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 25, 2016
 *
 */
package com.nextlabs.destiny.console.dao.delegadmin;

import java.util.List;

import com.nextlabs.destiny.console.dao.GenericDao;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.model.delegadmin.DelegateModel;

/**
 *
 * DAO interface for Delegation model
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface DelegateModelDao extends GenericDao<DelegateModel, Long> {

    /**
     * Find a delegation model type by given type
     * 
     * @param type
     *            {@link DelegateModel}
     * @return collection of {@link DelegateModel}
     */
    List<DelegateModel> findByType(PolicyModelType type);

    /**
     * Find a delegation model type by given types
     * 
     * @param type
     *            {@link PolicyModelType}
     * @return collection of {@link DelegateModel}
     */
    List<DelegateModel> findByTypes(PolicyModelType... types);
    
    /**
     * Finds delegation models type by given type
     * 
     * @param type
     *            {@link DelegateModel}
     * @return collection of {@link DelegateModel}
     */
     DelegateModel findByShortName(String shortName);
}
