/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 1 Aug 2016
 *
 */
package com.nextlabs.destiny.console.services.authentication;

import java.util.List;

import com.nextlabs.destiny.console.dto.authentication.AuthHandlerDetail;
import com.nextlabs.destiny.console.enums.AuthHandlerType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.InvalidInputParamException;
import com.nextlabs.destiny.console.model.authentication.AuthHandlerTypeDetail;

/**
 *
 * Authentication Handlers Registration Service interface
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public interface AuthHandlerService {

	/**
	 * Create or Update the Authentication Handler in the system
	 * 
	 * @param {@link
	 * 			AuthHandlerTypeDetail}
	 * @param {@link AuthHandlerDetail}
	 * @return Saved AuthHandlerTypeDetail entity
	 * @throws ConsoleException
	 *             thrown at any error
	 */
	AuthHandlerTypeDetail saveHandler(AuthHandlerTypeDetail authHandler, AuthHandlerDetail handlerDetail) throws
					ConsoleException;

	/**
	 * Find Authentication Handler by Id
	 * 
	 * @param id
	 *            primary key of the entity
	 * @return AuthHandlerTypeDetail entity
	 * @throws ConsoleException
	 *             thrown at any error
	 */
	AuthHandlerTypeDetail findById(Long id) throws ConsoleException;

    /**
     * Check if authentication handler is a certain type
     * @param id
     *              primary key of the entity
     * @param type
     *              type to check for
     * @return true if value match with given string
     * @throws ConsoleException
     */
    boolean isType(Long id, String type) throws ConsoleException;

	/**
	 * Find Authentication Handler by Type
	 * 
	 * @param type
	 * @return AuthHandlerTypeDetail entity
	 * @throws ConsoleException
	 *             thrown at any error
	 */
	List<AuthHandlerTypeDetail> findByType(String type) throws ConsoleException;

	/**
	 * Remove Authentication Handler by Id
	 * 
	 * @param id
	 *            primary key of the entity
	 * @throws ConsoleException
	 *             thrown at any error
	 */
	boolean removeHandler(Long id) throws ConsoleException;

	/**
	 * Verify if connection to Authentication source is successful
	 * 
	 * @param {@link
	 * 			AuthHandlerDetail}
	 * @return true if connection is successful, else false
	 * @throws ConsoleException
	 *             thrown at any error
	 */
	void checkHandlerConnection(AuthHandlerDetail authHandler) throws ConsoleException;

	/**
	 * List all the registered Authentication Handlers
	 * 
	 * @return collection of AuthHandlerTypeDetail
	 * @throws ConsoleException
	 *             thrown at any error
	 */
	List<AuthHandlerTypeDetail> findAllAuthHandlers() throws ConsoleException;
	
	/**
	 * Find external authority by account id
	 * @param accountId
	 * @return
	 * @throws ConsoleException
	 */
	boolean isSameAuthorityExisted(AuthHandlerType authHandlerType, String accountId, Long otherThanThis) throws ConsoleException;

}
