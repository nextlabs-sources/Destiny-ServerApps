/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 14, 2016
 *
 */
package com.nextlabs.destiny.console.dao.policy;

import com.nextlabs.destiny.console.dao.GenericDao;
import com.nextlabs.destiny.console.model.policy.ActionConfig;

/**
 *
 * DAO Interface for Action Configurations
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface ActionConfigDao extends GenericDao<ActionConfig, Long> {

    String getLatestShortCode();

}
