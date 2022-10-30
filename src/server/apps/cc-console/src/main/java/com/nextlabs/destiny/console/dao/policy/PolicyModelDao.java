/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 14, 2016
 *
 */
package com.nextlabs.destiny.console.dao.policy;

import java.util.List;

import com.nextlabs.destiny.console.dao.GenericDao;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.model.policy.ActionConfig;
import com.nextlabs.destiny.console.model.policy.PolicyModel;

/**
 *
 * DAO interface for Policy model
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface PolicyModelDao extends GenericDao<PolicyModel, Long> {

    /**
     * Find a policy model type by given type
     * 
     * @param type
     *            {@link PolicyModelType}
     * @return collection of {@link PolicyModel}
     */
    List<PolicyModel> findByType(PolicyModelType type);

    /**
     * Find a policy model type by given types
     * 
     * @param type
     *            {@link PolicyModelType}
     * @return collection of {@link PolicyModel}
     */
    List<PolicyModel> findByTypes(PolicyModelType... types);
    
    /**
     * Find a policy model action by short code
     * 
     * @param actionShortCode
     * @return   {@link ActionConfig} 
     */
    ActionConfig findActionByShortCode(String actionShortCode);
    
	/**
	 * Find actionShortCode given model_short_name and action_short_name
	 * 
	 * @param actionSN
	 * @param modelSN
	 * @param deactivatedId
	 * 
	 * @return action_short_code
	 */
	String findActionAndModelByShortCode(String actionSN, String modelSN, Long deactivatedId);

}
