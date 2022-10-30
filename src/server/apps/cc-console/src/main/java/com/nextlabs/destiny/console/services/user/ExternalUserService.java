/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 20 Jul 2016
 *
 */
package com.nextlabs.destiny.console.services.user;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nextlabs.destiny.console.dto.authentication.AuthHandlerDetail;
import com.nextlabs.destiny.console.dto.authentication.activedirectory.AdSearchResponse;
import com.nextlabs.destiny.console.dto.common.ExternalUserDTO;
import com.nextlabs.destiny.console.dto.common.ExternalGroupDTO;
import com.nextlabs.destiny.console.exceptions.ConnectionFailedException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.ApplicationUser;

/**
 *
 * Service to manage the users from various external sources
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public interface ExternalUserService {

	/**
	 * Checks if the given external source is accessible
	 * 
	 * @param {@link
	 * 			AuthHandlerDetail}
	 * 
	 * @throws ConnectionFailedException
	 *             thrown at any exception
	 * 
	 */
    void checkConnection(AuthHandlerDetail handlerDTO);

	/**
	 * Gets all the users in the external source
	 * 
	 * @param authHandlerId
	 *            primary key of the Authentication Handler
	 * 
	 * @return Set of {@link ExternalUserDTO}
	 * @throws ConsoleException
	 *             thrown at any error
	 */
	AdSearchResponse getAllUsers(Long authHandlerId, String searchTxt, int pageSize) throws ConsoleException;

	/**
	 * Get all the user groups in the external source
	 * @param authHandlerId
	 * 				primary key of authentication handler
	 * @param searchTxt
	 * 				group name to search
	 * @param pageSize
	 * 				record page size
	 * @return List of {@link ExternalGroupDTO}
	 * @throws ConsoleException
	 * 				throw at any exception
	 */
	AdSearchResponse getAllGroups(Long authHandlerId, String searchTxt, int pageSize) throws ConsoleException;

	/**
	 * Get group details
	 * @param authHandlerId
	 * 				primary key of authentication handler
	 * @param groupId
	 * 				primary key of group in external environment
	 * @return ExternalGroupDTO contains all information of the group
	 * @throws ConsoleException
	 */
	ExternalGroupDTO getGroup(Long authHandlerId, String groupId) throws ConsoleException;

	/**
	 * Get list of application user ids for given authentication handler and groups
	 * @param authHandlerId
	 * @return
	 */
	List<ApplicationUser> getUserWithoutProvisionedGroups(Long authHandlerId) throws ConsoleException;

	/**
	 * Get all the user groups which has been provisioned, join with external source
	 * @param authHandlerId
	 * 				primary key of authentication handler
	 * @param searchTxt
	 * 				group name to search
	 * @param pageSize
	 * 				record page size
	 * @return List of {@link ExternalGroupDTO}
	 * @throws ConsoleException
	 * 				throw at any exception
	 */
	AdSearchResponse getAllProvisionedGroups(Long authHandlerId, String searchTxt, int pageSize) throws ConsoleException;

	/**
	 * Get all user group the user belongs to
	 * @param handlerDTO
	 * 			{@link AuthHandlerDetail} dto containing Authentication Handler
	 * @param username
	 * @return
	 * @throws ConsoleException
	 */
	Set<String> getUserGroups(AuthHandlerDetail handlerDTO, String username) throws ConsoleException;

	/**
	 * Gets all the external user attributes
	 * 
	 * @param authHandlerId
	 *            primary key of the Authentication Handler
	 * @return Set of External User Attributes
	 * @throws ConsoleException
	 *             thrown at any error
	 */
	Set<String> getAllUserAttributes(Long authHandlerId) throws ConsoleException;

	/**
	 * Gets all the external user attributes
	 * 
	 * @param {@link
	 * 			AuthHandlerDetail} dto containing Authentication Handler
	 *            details
	 * @return Set of External User Attributes
	 * @throws ConsoleException
	 *             thrown at any error
	 */
    Set<String> getAllUserAttributes(AuthHandlerDetail handlerDTO) throws ConsoleException;

	/**
	 * Fetches the external user properties, given the username and
	 * source/provider id
	 * 
	 * @param handlerDTO
	 *            primary key of the Authentication Handler details
	 * @param username
	 * @return map containing the user attributes and values
	 * @throws ConsoleException
	 *             thrown on any error
	 * 
	 */
	Map<String, String> getExternalUserAttributesByName(AuthHandlerDetail handlerDTO, String username) throws ConsoleException;

}
