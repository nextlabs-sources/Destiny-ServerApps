/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 22, 2016
 *
 */
package com.nextlabs.destiny.console.services;

import java.util.List;
import java.util.Map;

import com.nextlabs.destiny.console.dto.authentication.activedirectory.AdSearchRequest;
import com.nextlabs.destiny.console.dto.common.ExternalGroupDTO;
import com.nextlabs.destiny.console.model.ProvisionedUserGroup;
import org.springframework.data.domain.Page;

import com.nextlabs.destiny.console.dto.common.ApplicationUserDTO;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.policymgmt.ValidationDetailDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.ApplicationUser;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * Application User Service to manage the application users
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface ApplicationUserService {

    /**
     * Find application users by username
     * 
     * @param username
     *            username
     * @return {@link ApplicationUser}
     * @throws ConsoleException
     */
    ApplicationUser findByUsername(String username) throws ConsoleException;

    /**
     * Find application users by username and populate delegation policy
     *
     * @param username
     *            username
     * @return {@link ApplicationUser}
     * @throws ConsoleException
     */
    ApplicationUser findByUsernamePopulateDelegationPolicy(String username) throws ConsoleException;

    /**
     * Load all authorities
     *
     * @param appUser
     *            ApplicationUser
     * @return {@link ApplicationUser}
     * @throws ConsoleException
     */
    List<GrantedAuthority> getAllAuthorities(ApplicationUser appUser);

    /**
     * Find application users by id
     * 
     * @param id
     *            id
     * @return {@link ApplicationUser}
     * @throws ConsoleException
     */
    ApplicationUser findById(Long id) throws ConsoleException;

    /**
     * update password of the given user
     * 
     * @param userId
     *            id of the user
     * @param password
     *            new password to change
     * 
     * @throws ConsoleException
     */
    void updatePassword(Long userId, String currentPassword, String password) throws ConsoleException;

    /**
     * Re-Index all the application users
     * 
     * @throws ConsoleException
     */
    void reIndexAllUsers() throws ConsoleException;

    /**
     * Saves a new application user
     * 
     * @param {@link
     *            ApplicationUserDTO}
     * @return {@link ApplicationUserDTO}
     * @throws ConsoleException
     */
    ApplicationUser save(ApplicationUserDTO appUserDTO) throws ConsoleException;

    /**
     * Modify existing application user
     * 
     * @param appUserDTO {@link ApplicationUserDTO}
     * @return {@link ApplicationUserDTO}
     * @throws ConsoleException
     */
    ApplicationUser modify(ApplicationUserDTO appUserDTO) throws ConsoleException;
    
    /**
     * removes an application user
     * 
     * @param userId
     *            id of the user
     * @return 
     * @throws ConsoleException
     */
    ApplicationUser remove(Long userId) throws ConsoleException;

    /**
     * removes an user group
     *
     * @param groupId
     *          if of the user group
     * @param removeMembers
     *          indicate removal of group members is needed
     * @return
     * @throws ConsoleException
     */
    ProvisionedUserGroup removeGroup(Long groupId, boolean removeMembers) throws ConsoleException;

    /**
     * removes application users by ids
     * 
     * @param userIds
     *            id of the user
     * @return 
     * @throws ConsoleException
     */
    List<ApplicationUser> remove(List<Long> userIds) throws ConsoleException;

    /**
     * removes collection of user groups
     *
     * @param groupIds
     *          collection of user group ids
     * @return
     * @throws ConsoleException
     */
    List<ProvisionedUserGroup> removeGroups(List<Long> groupIds) throws ConsoleException;

    /**
     * Finds applications users by criteria
     * 
     * @param criteria
     * @return
     * @throws ConsoleException
     */
    Page<ApplicationUser> findUserByCriteria(SearchCriteria criteria)
            throws ConsoleException;

    /**
     * Find user group by criteria
     * @param criteria
     * @return
     * @throws ConsoleException
     */
    List<ExternalGroupDTO> findUserGroupByCriteria(List<AdSearchRequest> criteria)
            throws ConsoleException;

    /**
     * Updates last login time of the currently logged in user
     * 
     * @param userId
     * @throws ConsoleException
     */
    void updateLastLoggedIn(Long userId) throws ConsoleException;

    /**
     * Updates splash screen hide flag
     * 
     * @param userId
     * @param hide
     * @throws ConsoleException
     */
    void updateHideSplash(Long userId, boolean hide)
            throws ConsoleException;

    /**
     * Save and validate external users
     * 
     * @param appUsersDTO
     * @return
     * @throws ConsoleException
     */
    ValidationDetailDTO validateAndSave(List<ApplicationUserDTO> appUsersDTO)
			throws ConsoleException;

    /**
     * Save and validate external users
     *
     * @param groupDTOS
     * @return
     * @throws ConsoleException
     */
    ValidationDetailDTO validateAndSaveGroup(List<ExternalGroupDTO> groupDTOS)
                    throws ConsoleException;

    /**
     * Maps external user properties with internal attributes
     * 
     * @param appUser
     * @param userAttributes
     */
    void setExternalUserProperties(ApplicationUser appUser, Map<String, String> userAttributes);
    
    /**
     * Unlock an application user account
     * 
     * @param userId
     * @return true if user account found and unlocked successfully
     * @throws ConsoleException
     */
    ApplicationUser unlock(Long userId) throws ConsoleException;
    
    /**
     * removes the Google Authenticator Token for an application user
     * 
     * @param username   username of the user
     * @throws ConsoleException
     */
    void resetGAuthToken(String username) throws ConsoleException;

}
