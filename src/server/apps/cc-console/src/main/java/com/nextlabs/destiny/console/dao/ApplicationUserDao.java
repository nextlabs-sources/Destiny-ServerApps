/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 22, 2016
 *
 */
package com.nextlabs.destiny.console.dao;

import java.util.List;

import com.nextlabs.destiny.console.model.ApplicationUser;

/**
 *
 * DAO interface for Application user
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface ApplicationUserDao extends GenericDao<ApplicationUser, Long> {

    ApplicationUser findByUsername(String username);
    
    List<ApplicationUser> findAllActive();
    
    Long getLocalDomainId();
    
    List<String> findAllImportedByHandlerId(Long authHandlerId);
     
    ApplicationUser findByEmail(String email);
    
    List<ApplicationUser> findAllActiveOrOtherHandler(Long authHandlerId);

    List<String> findAllGroupUsers(Long authHandlerId);
    
    Long getActiveUserCountByHandlerId(Long authHandlerId);
    
    void resetGAuthTokenByUsername(String username);
}
