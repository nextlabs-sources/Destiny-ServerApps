/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 28, 2016
 *
 */
package com.nextlabs.destiny.console.controllers;

import com.nextlabs.destiny.console.AuditLogger;
import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.config.root.PrincipalUser;
import com.nextlabs.destiny.console.dao.authentication.AuthHandlerTypeDetailDao;
import com.nextlabs.destiny.console.dto.authentication.activedirectory.AdSearchRequest;
import com.nextlabs.destiny.console.dto.common.ApplicationUserDTO;
import com.nextlabs.destiny.console.dto.common.CollectionDataResponseDTO;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ExternalGroupDTO;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.common.StringFieldValue;
import com.nextlabs.destiny.console.dto.policymgmt.SearchCriteriaDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ValidationDetailDTO;
import com.nextlabs.destiny.console.enums.AuthHandlerType;
import com.nextlabs.destiny.console.enums.LogMarker;
import com.nextlabs.destiny.console.enums.UserCategory;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.InvalidPasswordException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.exceptions.NotUniqueException;
import com.nextlabs.destiny.console.exceptions.PasswordHistoryException;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.ProvisionedUserGroup;
import com.nextlabs.destiny.console.model.authentication.AuthHandlerTypeDetail;
import com.nextlabs.destiny.console.services.ApplicationUserService;
import com.nextlabs.destiny.console.utils.SecurityContextUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.nextlabs.destiny.console.utils.SecurityContextUtil.getCurrentUser;

/**
 *
 * REST Controller for Application User Management
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("/profile/user")
public class ApplicationUserController extends AbstractRestController {

    private static final Logger log = LoggerFactory
            .getLogger(ApplicationUserController.class);

    @Autowired
    private ApplicationUserService appUserService;

	@Autowired
	private AuthHandlerTypeDetailDao authHandlerDao;
	
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/changePassword")
    public ConsoleResponseEntity<ResponseDTO> changePassword(
            @RequestBody ApplicationUserDTO appUserDTO)
            throws ConsoleException {

        log.debug("Request came to change password");
        validations.assertNotBlank(appUserDTO.getPassword(), "password");
        validations.assertNotBlank(appUserDTO.getOldPassword(), "oldPassword");

        String password = appUserDTO.getPassword();
        String currentPassword = appUserDTO.getOldPassword();

        validations.assertNotSame("password", "current password", password,
                currentPassword);
        validations.assertValidPassword(password);

        Long userId = getCurrentUser().getUserId();
        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.modified.code"),
                msgBundle.getText("success.data.modified"));
        try {
        	appUserService.updatePassword(userId, currentPassword, appUserDTO.getPassword());
            log.info(LogMarker.AUTHENTICATION, "User account password changed. [username={}]", trimUsername(getCurrentUser().getUsername()));
            AuditLogger.log("Application user modified, [username :{}] by {}", trimUsername(getCurrentUser().getUsername()), trimUsername(getCurrentUser().getUsername()));
        } catch(InvalidPasswordException err) {
        	response = ResponseDTO.create(msgBundle.getText(err.getStatusCode()), msgBundle.getText(err.getStatusMsg()));
            log.error(LogMarker.AUTHENTICATION, "User change password failed. [username={}, cause=Old password mismatch]", trimUsername(getCurrentUser().getUsername()));
        } catch(PasswordHistoryException err) {
        	response = ResponseDTO.create(msgBundle.getText(err.getStatusCode()), msgBundle.getText(err.getStatusMsg()));
            log.error(LogMarker.AUTHENTICATION, "User change password failed. [username={}, cause=Does not comply to password history requirement]", trimUsername(getCurrentUser().getUsername()));
        }
        
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/currentUser")
    public ConsoleResponseEntity<ResponseDTO> getPrincipalUser() {

        log.debug("Request came find get current user details");

        PrincipalUser currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), currentUser);

        log.info("Current user details attached to response and sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}")
    public ConsoleResponseEntity<ResponseDTO> getById(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came find user by id, [id: {}]", id);
        validations.assertNotZero(id, "id");

        ApplicationUser user = appUserService.findById(id);
        if (user == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        ApplicationUserDTO appUserDTO = ApplicationUserDTO.getDTO(user);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), appUserDTO);

        log.info("Requested user details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/details/{username}")
    public ConsoleResponseEntity<ResponseDTO> getByName(
            @PathVariable("username") String username) throws ConsoleException {

        log.debug("Request came to find user by username, [username: {}]", username);
        validations.assertNotNull(username, "username");

        ApplicationUser user = appUserService.findByUsername(username);
        if (user == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        ApplicationUserDTO appUserDTO = ApplicationUserDTO.getDTO(user);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), appUserDTO);

        log.info("Requested user details details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * API to create a ControlCenter's internal user account
     * @param appUserDTO User account information
     * @return Create user account's response
     * @throws ConsoleException Any encountered exception
     */
    @SuppressWarnings({ "rawtypes" })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/add")
    @ApiOperation(value = "Creates and adds a new application user.",
	notes="Returns a success message along with the new user's id, when the user has been successfully saved.")
    public ConsoleResponseEntity<SimpleResponseDTO> addUser(
            @RequestBody ApplicationUserDTO appUserDTO)
            throws ConsoleException {

        log.debug("Request came to add new user");
        validations.assertNotBlank(appUserDTO.getUsername(), "username");
        validations.assertNotBlank(appUserDTO.getPassword(), "password");
        validations.assertMatches(appUserDTO.getUsername(),
                Pattern.compile(msgBundle.getText("username.pattern")),
                "Username", msgBundle.getText("username.format.message"));  
        validations.assertValidPassword(appUserDTO.getPassword());
        
        // Added in v9.0
        // For backward compatibility, this is an optional field
        // If this is not passed in by the caller, default the value to Console User account
        if(StringUtils.isEmpty(appUserDTO.getUserCategory())) {
        	appUserDTO.setUserCategory(UserCategory.CONSOLE_USER.getCode());
        }
        
    	if (StringUtils.isBlank(appUserDTO.getLastName())){
    		appUserDTO.setLastName(" ");
    	}

        if ( appUserService.findByUsername(appUserDTO.getUsername()) != null) {
            throw new NotUniqueException(
                    msgBundle.getText("duplicate.username.code"),
                    msgBundle.getText("duplicate.username.message"));
        }
        
		ApplicationUser appUser = appUserService.save(appUserDTO);

        SimpleResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), appUser.getId());

        log.info(LogMarker.AUDIT, "User account created. [userAccount={}, actionBy={}]", SecurityContextUtil.getUserInfo(appUser), SecurityContextUtil.getUserInfo());
        AuditLogger.log("Application user created, [username :{}, name :{} {}] by {}", trimUsername(appUser.getUsername()), appUser.getFirstName(),
                appUser.getLastName(), trimUsername(getCurrentUser().getUsername()));
        
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }
    
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value = "/importUsers")
	public ConsoleResponseEntity<ResponseDTO> importUsers(
			@RequestBody List<ApplicationUserDTO> appUserDTOs) throws ConsoleException {

		log.debug("Request came to bulk add new users");
		validations.assertNotNull(appUserDTOs, "appUserDTOs");
		validations.assertNotZero(Long.valueOf(appUserDTOs.size()), "Ids");
		
		ValidationDetailDTO validateDTO = appUserService.validateAndSave(appUserDTOs);
		List<ValidationDetailDTO> validateDTOList = new ArrayList<>();
		validateDTOList.add(validateDTO);

		ResponseDTO response;
		if (validateDTO.getDetails().isEmpty()) {
			 response = SimpleResponseDTO.create(
	                    msgBundle.getText("success.data.saved.code"),
	                    msgBundle.getText("success.data.saved"));
			log.info("External user(s) saved successfully and response sent");
		} else { 
			response = SimpleResponseDTO.create(
					msgBundle.getText("user.import.failed.code"),
					msgBundle.getText("user.import.failed"),validateDTOList);
			log.info("One or more user not imported");
		}

		log.info(LogMarker.AUDIT, "User account imported. [actionBy={}]",
                SecurityContextUtil.getUserInfo());
        AuditLogger.log("External users imported,  [ No of imported users :{}] by {}", appUserDTOs.size() -
		        validateDTO.getDetails().size(), trimUsername(getCurrentUser().getUsername()));
		return ConsoleResponseEntity.get(response, HttpStatus.OK);
	}

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/group/import")
    public ConsoleResponseEntity<ResponseDTO> importGroups(
                    @RequestBody List<ExternalGroupDTO> groupDTOs) throws ConsoleException {

        log.debug("Request came to bulk add new users");
        validations.assertNotNull(groupDTOs, "groupDTOs");
        validations.assertNotZero(Long.valueOf(groupDTOs.size()), "Ids");

        ValidationDetailDTO validateDTO = appUserService.validateAndSaveGroup(groupDTOs);

        List<ValidationDetailDTO> validateDTOList = new ArrayList<>();
        validateDTOList.add(validateDTO);

        boolean allSaved;
        if (validateDTO.getDetails().isEmpty()) {
            allSaved = true;
        } else {
            allSaved = false;
        }

        ResponseDTO response;
        if (allSaved) {
            response = SimpleResponseDTO.create(
                            msgBundle.getText("success.data.saved.code"),
                            msgBundle.getText("success.data.saved"));
            log.info("External group(s) saved successfully and response sent");
        } else {
            response = SimpleResponseDTO.create(
                            msgBundle.getText("group.import.failed.code"),
                            msgBundle.getText("group.import.failed"),validateDTOList);
            log.info("One or more group not imported");
        }

        log.info(LogMarker.AUDIT, "User group imported. [actionBy={}]",
                        SecurityContextUtil.getUserInfo());
        AuditLogger.log("External groups imported,  [ No of imported groups :{}] by {}", groupDTOs.size() -
                        validateDTO.getDetails().size(), trimUsername(getCurrentUser().getUsername()));
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @SuppressWarnings({ "rawtypes" })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/modify")
    public ConsoleResponseEntity<SimpleResponseDTO> modifyUser(
            @RequestBody ApplicationUserDTO appUserDTO)
            throws ConsoleException {

        log.debug("Request came to modify the user");
        validations.assertNotZero(appUserDTO.getId(), "id");
        validations.assertNotBlank(appUserDTO.getUsername(), "username"); 
        
    	if (!StringUtils.isBlank(appUserDTO.getPassword())){
    		validations.assertValidPassword(appUserDTO.getPassword());
    	}

        ApplicationUser appUser = appUserService.modify(appUserDTO);

        SimpleResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.modified.code"),
                msgBundle.getText("success.data.modified"), appUser.getId());

        log.info(LogMarker.AUDIT, "User account modified. [userAccount={}, actionBy={}]", SecurityContextUtil.getUserInfo(appUser), SecurityContextUtil.getUserInfo());
        AuditLogger.log("Application user modified,  [username :{}, name :{} {}] by {}", trimUsername(appUser.getUsername()),
                appUser.getFirstName(), appUser.getLastName(), trimUsername(getCurrentUser().getUsername()));
        
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/splashDisplay/{hideSplash}")
    public ConsoleResponseEntity<ResponseDTO> splashDisplay(
            @PathVariable("hideSplash") boolean hideSplash)
            throws ConsoleException {

        log.debug("Request came to update the user with hide splash");
        PrincipalUser currentUser = SecurityContextUtil.getCurrentUser();
        Long userId = currentUser.getUserId();

        appUserService.updateHideSplash(userId, hideSplash);
        currentUser.setHideSplash(hideSplash);

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.modified.code"),
                msgBundle.getText("success.data.modified"));

        log.info("User modified successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/remove/{id}")
    public ConsoleResponseEntity<ResponseDTO> removeUser(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to remove a user");
        validations.assertNotZero(id, "id");

        ApplicationUser userDeleted = appUserService.remove(id);

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.deleted.code"),
                msgBundle.getText("success.data.deleted"));

        log.info(LogMarker.AUDIT, "User account removed. [userAccount={}, actionBy={}]", SecurityContextUtil.getUserInfo(userDeleted), SecurityContextUtil.getUserInfo());
        AuditLogger.log("Application user deleted,  [username :{}, name :{} {}] by {}", trimUsername(userDeleted.getUsername()), userDeleted.getFirstName(),
                userDeleted.getLastName(), trimUsername(getCurrentUser().getUsername()));
        
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "group/remove/{id}")
    public ConsoleResponseEntity<ResponseDTO> removeGroup(
                    @PathVariable("id") Long id) throws ConsoleException {
        log.debug("Request came to remove a user group");
        validations.assertNotZero(id, "id");

        appUserService.removeGroup(id, true);

        ResponseDTO response = ResponseDTO.create(
                        msgBundle.getText("success.data.deleted.code"),
                        msgBundle.getText("success.data.deleted"));


        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/bulkDelete")
    public ConsoleResponseEntity<ResponseDTO> removeUsers(
            @RequestBody List<Long> ids) throws ConsoleException {

        log.debug("Request came to bulk delete Components");

        validations.assertNotNull(ids, "Ids");
        List<ApplicationUser> usersDeleted = appUserService.remove(ids);

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.deleted.code"),
                msgBundle.getText("success.data.deleted"));

        List<String> userNames = new ArrayList<>(usersDeleted.size());
        for(ApplicationUser user : usersDeleted) {
            userNames.add(trimUsername(user.getUsername()));
        }
        log.info(LogMarker.AUDIT, "User accounts removed. [userAccountList=[{}], actionBy={}]", SecurityContextUtil.getUserInfo(usersDeleted), SecurityContextUtil.getUserInfo());
        AuditLogger.log("Application user deleted,  [username :{}] by {}", userNames, trimUsername(getCurrentUser().getUsername()));

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "group/bulkDelete")
    public ConsoleResponseEntity<ResponseDTO> removeGroups(
                    @RequestBody List<Long> ids) throws ConsoleException {

        log.debug("Request came to bulk delete User Groups");

        validations.assertNotNull(ids, "Ids");
        List<ProvisionedUserGroup> groupsDeleted = appUserService.removeGroups(ids);

        ResponseDTO response = ResponseDTO.create(
                        msgBundle.getText("success.data.deleted.code"),
                        msgBundle.getText("success.data.deleted"));

        List<String> groupNames = new ArrayList<>(groupsDeleted.size());
        for(ProvisionedUserGroup group : groupsDeleted) {
            groupNames.add(group.getGroupId());
        }
        log.info(LogMarker.AUDIT, "User groups removed. [userGroupList=[{}], actionBy={}]", groupNames, SecurityContextUtil.getUserInfo());
        AuditLogger.log("User group deleted,  [groupIds :{}] by {}", groupNames, trimUsername(getCurrentUser().getUsername()));

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/unlockUser")
    public ConsoleResponseEntity<ResponseDTO> unlockUser(
            @RequestBody Long id) throws ConsoleException {
        log.debug("Request came to unlock a user account");
        validations.assertNotZero(id, "id");

        ApplicationUser userUnlocked = appUserService.unlock(id);

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.modified.code"),
                msgBundle.getText("success.data.modified"));

        log.info(LogMarker.AUDIT, "User account unlocked. [username={}, actionBy={}]", userUnlocked.getUsername(), SecurityContextUtil.getUserInfo());
        AuditLogger.log("Application user unlocked,  [username :{}, name :{} {}] by {}", trimUsername(userUnlocked.getUsername()), userUnlocked.getFirstName(),
                userUnlocked.getLastName(), trimUsername(getCurrentUser().getUsername()));
        
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * User search handled by this method. This will return the user list
     * according to given search criteria
     * 
     * @param criteriaDTO
     * @return List of policies to display
     * @throws ConsoleException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/list")
    public ConsoleResponseEntity<CollectionDataResponseDTO> usersSearch(
            @RequestBody SearchCriteriaDTO criteriaDTO)
            throws ConsoleException {

        log.debug("Request came to users search");
        validations.assertNotNull(criteriaDTO.getCriteria(), "criteria");
        SearchCriteria criteria = criteriaDTO.getCriteria();

        long startTime = System.currentTimeMillis();
        Page<ApplicationUser> appUsersPage = appUserService.findUserByCriteria(criteria);

        long processingTime = System.currentTimeMillis() - startTime;

        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"));
        List<ApplicationUser> userList = appUsersPage.getContent();
        List<ApplicationUserDTO> userDTOList = new ArrayList<>();
        Map<Long, AuthHandlerTypeDetail>  authHandlerDetailMap = new HashMap<>();
        
        for(ApplicationUser user : userList) {
        	ApplicationUserDTO userDTO = ApplicationUserDTO.getDTO(user);
            userDTO.setAuthHandlerProtocol("DB");       // Default to DB
        	if(null != user.getAuthHandlerId() && user.getAuthHandlerId() > 0) {
        		AuthHandlerTypeDetail authHandler = authHandlerDetailMap.get(user.getAuthHandlerId());
        		if(null == authHandler) {
        			authHandler = authHandlerDao.findById(user.getAuthHandlerId());
					authHandlerDetailMap.put(user.getAuthHandlerId(), authHandler);
                }

        		if(authHandler != null) {
                    userDTO.setAuthHandlerId(authHandler.getId());
                    userDTO.setAuthHandlerName(authHandler.getName());
                    userDTO.setAuthHandlerProtocol(authHandler.getType());
                }
            }
        	
        	userDTOList.add(userDTO);
        }
		
		response.setData(userDTOList);
        response.setPageNo(criteria.getPageNo());
        response.setPageSize(criteria.getPageSize());
        response.setTotalPages(appUsersPage.getTotalPages());
        response.setTotalNoOfRecords(appUsersPage.getTotalElements());

        log.info(
                "User search has been completed, Search handled in {} milis, Total no of records : {}",
                processingTime, appUsersPage.getNumberOfElements());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "group/list")
    public ConsoleResponseEntity<CollectionDataResponseDTO> listGroup(
                    @RequestBody SearchCriteriaDTO criteriaDTO)
                    throws ConsoleException {
        validations.assertNotNull(criteriaDTO, "criteria");
        List<Long> authHandlerIds = new ArrayList<>();
        String searchText = null;
        for(SearchField searchField : criteriaDTO.getCriteria().getFields()) {
            if("authHandlerId".equalsIgnoreCase(searchField.getField())) {
                StringFieldValue fieldValue = (StringFieldValue)searchField.getValue();
                List<String> values = (List<String>) fieldValue.getValue();
                for(String authHandlerId : values) {
                    authHandlerIds.add(Long.parseLong(authHandlerId));
                }
            } else if("displayName".equalsIgnoreCase(searchField.getField())) {
                searchText = (String)((StringFieldValue)searchField.getValue()).getValue();
            }
        }

        List<AdSearchRequest> groupSearch = new ArrayList<>();
        if(!authHandlerIds.isEmpty()) {
            for (Long authHandlerId : authHandlerIds) {
                AdSearchRequest searchRequest = new AdSearchRequest();
                searchRequest.setHandlerId(authHandlerId);
                searchRequest.setSearchText(searchText);
                searchRequest.setPageSize(criteriaDTO.getCriteria().getPageSize());

                groupSearch.add(searchRequest);
            }
        } else {
            List<AuthHandlerTypeDetail> authHandlers = authHandlerDao.findByType(AuthHandlerType.OIDC.name());
            for(AuthHandlerTypeDetail authHandler : authHandlers) {
                AdSearchRequest searchRequest = new AdSearchRequest();
                searchRequest.setHandlerId(authHandler.getId());
                searchRequest.setSearchText(searchText);
                searchRequest.setPageSize(criteriaDTO.getCriteria().getPageSize());

                groupSearch.add(searchRequest);
            }
        }

        List<ExternalGroupDTO> userGroupDTOList = appUserService.findUserGroupByCriteria(groupSearch);

        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                        msgBundle.getText("success.data.loaded.code"),
                        msgBundle.getText("success.data.loaded"));

        response.setData(userGroupDTOList);
        response.setTotalPages(1);
        response.setTotalNoOfRecords(userGroupDTOList.size());

        log.info("User group search has been completed. Total no of records : {}",
                        response.getTotalNoOfRecords());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping(value = "/reset/mfa/token/{username}")
	public ConsoleResponseEntity<ResponseDTO> resetGAuthToken(@PathVariable("username") String username) throws ConsoleException {

		log.info("Request came to reset the Google Authentication Token for the user : {}", username);
		validations.assertNotNull(username, "username");

		appUserService.resetGAuthToken(username);

		ResponseDTO response = ResponseDTO.create(msgBundle.getText("success.data.deleted.code"),
				msgBundle.getText("success.data.deleted"));

		log.info("Google Authentication token deleted successfully");

        AuditLogger.log("Google Authenticator Token deleted,  [username :{}] by {}",
					trimUsername(username),
					trimUsername(getCurrentUser().getUsername()));
		return ConsoleResponseEntity.get(response, HttpStatus.OK);

	}

    @Override
    public Logger getLog() {
        return log;
    }
    
    private String trimUsername(String username) {
    	if(username != null && username.indexOf('@') > -1) {
    		return username.substring(0, username.lastIndexOf('@'));
    	}
    	
    	return username;
    }
}
