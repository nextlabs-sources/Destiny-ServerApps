/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 18, 2016
 *
 */
package com.nextlabs.destiny.console.config.root;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nextlabs.destiny.console.dto.common.ApplicationUserDTO;
import com.nextlabs.destiny.console.enums.AuditAction;
import com.nextlabs.destiny.console.enums.AuditableEntity;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.services.ApplicationUserService;
import com.nextlabs.destiny.console.services.EntityAuditLogService;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.nextlabs.destiny.console.model.ApplicationUser.SUPER_USERNAME;

/**
 *
 * CAS authentication user details service to populate other attributes for the
 * {@link PrincipalUser}
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@SuppressWarnings("rawtypes")
@Component
public class CasAuthenticationUserDetailsService
        implements AuthenticationUserDetailsService {

    private static final Logger log = LoggerFactory
            .getLogger(CasAuthenticationUserDetailsService.class);

    @Autowired
    private ApplicationUserService applicationUserService;
    
    @Resource
    private ApplicationUserSearchRepository appUserSearchRepository;
    
    @Autowired
    protected MessageBundleService msgBundle;
    
    @Autowired
    private EntityAuditLogService entityAuditLogService;

    @Override
    public UserDetails loadUserDetails(Authentication token) {
        try {
            ApplicationUser appUser = applicationUserService.findByUsernamePopulateDelegationPolicy(token.getName());

            if(appUser == null) {
                throw new UsernameNotFoundException("Record not found for give username.");
            }
			applicationUserService.updateLastLoggedIn(appUser.getId());
            appUserSearchRepository.save(appUser);

            log.info(
                    "New user login - User details populated and Principal user created, [ User : {}]",
                    appUser.getUsername());

            ApplicationUserDTO applicationUserDTO = ApplicationUserDTO.getDTO(appUser);

            if(appUser.getUsername().equalsIgnoreCase(SUPER_USERNAME)) {
            	applicationUserDTO.setUserType(ApplicationUser.USER_TYPE_INTERNAL);
            }

        	Map<String, String> audit = new LinkedHashMap<>();
        	    	
        	audit.put("User Type", applicationUserDTO.getUserType());
        	audit.put("User Category", applicationUserDTO.getUserCategory());
        	audit.put("Display Name", applicationUserDTO.getDisplayName());
        	audit.put("Username", applicationUserDTO.getUsername());

            entityAuditLogService.addEntityAuditLog(AuditAction.LOGIN,
					AuditableEntity.APPLICATION_USER.getCode(), applicationUserDTO.getDisplayName(), applicationUserDTO.getId(), null,
					JsonUtil.toJsonString(audit));

            return new PrincipalUser(appUser.getId(), appUser.getFirstName(), appUser.getLastName(), appUser.getDisplayName(),
                    appUser.getUsername(), "*******", applicationUserDTO.getUserType(), appUser.getUserCategory(),
                    appUser.isSuperUser(), appUser.isHideSplash(), applicationUserService.getAllAuthorities(appUser));
        } catch (ConsoleException | JsonProcessingException e) {
            log.error("Error encountered in load user details,", e);
            throw new UsernameNotFoundException(
                    "Error encountered in search by username");
        }
    }
}
