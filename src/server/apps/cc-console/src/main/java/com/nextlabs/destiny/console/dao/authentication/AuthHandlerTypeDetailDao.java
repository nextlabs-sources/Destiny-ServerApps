/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 1 Aug 2016
 *
 */
package com.nextlabs.destiny.console.dao.authentication;

import java.util.List;

import com.nextlabs.destiny.console.dao.GenericDao;
import com.nextlabs.destiny.console.dto.authentication.AuthHandlerDetail;
import com.nextlabs.destiny.console.model.authentication.AuthHandlerTypeDetail;

/**
 *
 * Authentication Handler Dao interface
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public interface AuthHandlerTypeDetailDao extends GenericDao<AuthHandlerTypeDetail, Long> {

	/**
	 * 
	 * Find the Authentication Handlers by type
	 * 
	 * @param type
	 * @return List of {@link AuthHandlerDetail}
	 */
	List<AuthHandlerTypeDetail> findByType(String type);
}
