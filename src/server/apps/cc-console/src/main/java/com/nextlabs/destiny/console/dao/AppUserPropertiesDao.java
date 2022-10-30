/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on May 6, 2016
 *
 */
package com.nextlabs.destiny.console.dao;

import java.util.List;

import com.nextlabs.destiny.console.model.AppUserProperties;

/**
 *
 * DAO interface for Application User Properties
 *
 * @author aishwarya
 * @since   8.0
 *
 */
public interface AppUserPropertiesDao extends GenericDao<AppUserProperties, Long> {
    
    List<AppUserProperties> findByUserId(Long userId);
    
    List<AppUserProperties> findBySuperUserId(Long userId);

}
