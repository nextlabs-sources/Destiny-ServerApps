/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 22, 2016
 *
 */
package com.nextlabs.destiny.console.services.impl;

import static com.nextlabs.destiny.console.model.ApplicationUser.JWT_PASSPHRASE;
import static com.nextlabs.destiny.console.model.ApplicationUser.USER_TYPE_INTERNAL;
import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.buildQuery;
import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.withSorts;

import javax.annotation.Resource;
import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nextlabs.destiny.console.config.properties.PasswordEnforcementProperties;
import com.nextlabs.destiny.console.config.root.PrincipalUser;
import com.nextlabs.destiny.console.dao.AppUserPropertiesDao;
import com.nextlabs.destiny.console.dao.ApplicationUserDao;
import com.nextlabs.destiny.console.dao.EntityAuditLogDao;
import com.nextlabs.destiny.console.dao.SuperApplicationUserDao;
import com.nextlabs.destiny.console.dto.authentication.AuthHandlerDetail;
import com.nextlabs.destiny.console.dto.authentication.activedirectory.AdSearchRequest;
import com.nextlabs.destiny.console.dto.authentication.activedirectory.AdSearchResponse;
import com.nextlabs.destiny.console.dto.common.ApplicationUserDTO;
import com.nextlabs.destiny.console.dto.common.ExternalGroupDTO;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.dto.policymgmt.ValidationDetailDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ValidationRecord;
import com.nextlabs.destiny.console.enums.AuditAction;
import com.nextlabs.destiny.console.enums.AuditableEntity;
import com.nextlabs.destiny.console.enums.AuthHandlerType;
import com.nextlabs.destiny.console.enums.DataType;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.enums.UserCategory;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.InvalidOperationException;
import com.nextlabs.destiny.console.exceptions.InvalidPasswordException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.exceptions.NotUniqueException;
import com.nextlabs.destiny.console.exceptions.PasswordHistoryException;
import com.nextlabs.destiny.console.init.ConfigurationDataLoader;
import com.nextlabs.destiny.console.model.AppUserProperties;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.PasswordHistory;
import com.nextlabs.destiny.console.model.ProvisionedUserGroup;
import com.nextlabs.destiny.console.model.SuperApplicationUser;
import com.nextlabs.destiny.console.repositories.ProvisionedUserGroupRepository;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.services.ApplicationUserService;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.authentication.AuthHandlerService;
import com.nextlabs.destiny.console.services.delegadmin.AccessControlService;
import com.nextlabs.destiny.console.services.delegadmin.DelegateModelService;
import com.nextlabs.destiny.console.services.user.ExternalUserService;
import com.nextlabs.destiny.console.utils.SecurityContextUtil;

/**
 * 
 * Implementation of ApplicationUserService
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class ApplicationUserServiceImpl implements ApplicationUserService {

    private static final Logger log = LoggerFactory
            .getLogger(ApplicationUserServiceImpl.class);
    
    private SimpleDateFormat usernameDateFormat = new SimpleDateFormat("yyMMddHHmmss");
    private SimpleDateFormat displayNameDateFormat = new SimpleDateFormat("dd MMM yyyy");

    private static String disallowManageAdministratorAccountKey = "disallow.manage.administrator.account";
    private static String disallowManageAdministratorAccountCodeKey = "disallow.manage.administrator.account.code";

    @Autowired
    private PasswordEnforcementProperties passwordEnforcementProperties;

    @Autowired
    private ApplicationUserDao applicationUserDao;
    
    @Autowired
    private SuperApplicationUserDao superUserDao;

    @Autowired
    private AppUserPropertiesDao appUserPropertiesDao;
	
    @Autowired
    @Qualifier("ADUsersService")
    private ExternalUserService ldapUserService;

    @Autowired
    @Qualifier("AzureUserService")
    private ExternalUserService azureUserService;

    @Autowired
    protected MessageBundleService msgBundle;

    @Resource
    private ApplicationUserSearchRepository appUserSearchRepository;

    @Autowired
    private DelegateModelService delegateModelService;
    
    @Autowired
    private ConfigurationDataLoader configDataLoader;

    @Autowired
    private EntityAuditLogDao entityAuditLogDao;
    
    @Autowired
    private AuthHandlerService authenticationHandlerService;

    @Autowired
    private AccessControlService accessControlService;

    @Autowired
    private ProvisionedUserGroupRepository userGroupRepository;

    @Autowired
    PasswordEncoder delegatingPasswordEncoder;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public ApplicationUser findByUsername(String username) {
        ApplicationUser user = searchForUserName(username);

        if (user != null) {
            log.info("User found for given username, [username : {}]",
                    username);
            List<AppUserProperties> userProperties = appUserPropertiesDao
                    .findByUserId(user.getId());
            Set<AppUserProperties> properties = new TreeSet<>(userProperties);
            user.setProperties(properties);
        }

        return user;
    }

    @Override
    public ApplicationUser findByUsernamePopulateDelegationPolicy(String username) throws ConsoleException {
        ApplicationUser user = findByUsername(username);
        if (ApplicationUser.USER_TYPE_IMPORTED.equals(user.getUserType())) {
            AuthHandlerDetail authHandler = AuthHandlerDetail.getDTO(authenticationHandlerService.findById(user.getAuthHandlerId()));
            Map<String, String> userAttributes = new LinkedHashMap<>();
            Map<String, Set<String>> multiValueAttributes = new HashMap<>();

            if(authHandler.getType().equals(AuthHandlerType.LDAP.name())) {
                userAttributes = ldapUserService.getExternalUserAttributesByName(authHandler, username);
                multiValueAttributes.put("groups", ldapUserService.getUserGroups(authHandler, username));
                updateUserBaseAttribute(user, userAttributes);
            } else if(authHandler.getType().equals(AuthHandlerType.OIDC.name())) {
                userAttributes = azureUserService.getExternalUserAttributesByName(authHandler, username);
                multiValueAttributes.put("groups", azureUserService.getUserGroups(authHandler, username));
                updateUserBaseAttribute(user, userAttributes);
            }
            if(!userAttributes.isEmpty()) {
                setExternalUserProperties(user, userAttributes);
            }
            if(!multiValueAttributes.isEmpty()) {
                user.setMultiValueProperties(multiValueAttributes);
            }
            userAttributes.remove(msgBundle.getText("attr.email.key"));
            userAttributes.remove(msgBundle.getText("attr.lastName.key"));
            userAttributes.remove(msgBundle.getText("attr.firstName.key"));
            userAttributes.remove(msgBundle.getText("attr.username.key"));
        }

        // load all permissions and data level tag conditions
        user = accessControlService.populateAllowedActionsAndTags(user);

        return user;
    }

    private ApplicationUser searchForUserName(String username) {
		return applicationUserDao.findByUsername(username);
	}

	private void updateUserBaseAttribute(ApplicationUser user, Map<String, String> userAttributes) {
        String firstName = userAttributes.get(msgBundle.getText("attr.firstName.key"));
        if(firstName == null || firstName.equals("null")) {
            firstName = " ";
        }
        if(!firstName.equals(user.getFirstName())) {
            user.setFirstName(firstName);
        }
        String lastName = userAttributes.get(msgBundle.getText("attr.lastName.key"));
        if(lastName == null || lastName.equals("null")) {
            lastName = " ";
        }
        if(!lastName.equals(user.getLastName())) {
            user.setLastName(lastName);
        }
        String displayName = userAttributes.get(msgBundle.getText("attr.displayName.key"));
        if(displayName == null || displayName.equals("null")) {
            displayName = String.format("%s %s", firstName, lastName);
        }
        if(StringUtils.isBlank(displayName)) {
            displayName = user.getUsername();
        }
        if(!displayName.equals(user.getDisplayName())) {
            user.setDisplayName(displayName);
        }

        if(userAttributes.containsKey(msgBundle.getText("attr.email.key"))) {
            String email = userAttributes.get(msgBundle.getText("attr.email.key"));
            user.setEmail(email);
        }
    }

    public List<GrantedAuthority> getAllAuthorities(ApplicationUser appUser) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (String allowedAction : appUser.getAllowedActions()) {
            authorities.add(new SimpleGrantedAuthority(allowedAction));
        }
        return authorities;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public ApplicationUser findById(Long id) throws ConsoleException {
        ApplicationUser user = applicationUserDao.findById(id);

        if (user == null) {
        	SuperApplicationUser superUser = superUserDao.findById(id);
            user = new ApplicationUser(superUser.getId(),
                    ApplicationUser.USER_TYPE_INTERNAL, UserCategory.ADMINISTRATOR.getCode(), superUser.getUsername(),
                    superUser.getFirstName(), superUser.getLastName());
            user.setLastUpdatedBy(superUser.getLastUpdatedBy());
            user.setLastUpdatedDate(superUser.getLastUpdatedDate());
            user.setEmail(superUser.getEmail());
            
        	List<AppUserProperties> userProperties = appUserPropertiesDao
                    .findBySuperUserId(superUser.getId());
            Set<AppUserProperties> properties = new TreeSet<>(userProperties);
            user.setProperties(properties);
        } else {
            log.info("User found for given id, [id : {}]", id);
            Long handlerId = user.getAuthHandlerId();
        	List<AppUserProperties> userProperties = appUserPropertiesDao.findByUserId(user.getId());
            Set<AppUserProperties> properties = new TreeSet<>(userProperties);
            user.setProperties(properties);
            
            if(ApplicationUser.USER_TYPE_IMPORTED.equals(user.getUserType())
            		&& handlerId != null) {
                AuthHandlerDetail authHandler = AuthHandlerDetail.getDTO(authenticationHandlerService.findById(user.getAuthHandlerId()));
                Map<String, String> externalUserAttrs = new LinkedHashMap<>();
                
                if (authHandler.getType().equals(AuthHandlerType.LDAP.name())){
                	externalUserAttrs = ldapUserService.getExternalUserAttributesByName(authHandler, user.getUsername());
                } else if(authHandler.getType().equals(AuthHandlerType.OIDC.name())) {
                	externalUserAttrs = azureUserService.getExternalUserAttributesByName(authHandler, user.getUsername());
                }

                if(!externalUserAttrs.isEmpty()) {
                    setExternalUserProperties(user, externalUserAttrs);
                }
                externalUserAttrs.remove(msgBundle.getText("attr.email.key"));
                externalUserAttrs.remove(msgBundle.getText("attr.lastName.key"));
                externalUserAttrs.remove(msgBundle.getText("attr.firstName.key"));
                externalUserAttrs.remove(msgBundle.getText("attr.username.key"));
            }
        }
        
        return user;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updatePassword(Long userId, String currentPassword, String newPassword)
            throws ConsoleException {
        ApplicationUser user = applicationUserDao.findById(userId);
        if (user != null) {
            if(!delegatingPasswordEncoder.matches(currentPassword, new String(user.getPassword()))) {
            	throw new InvalidPasswordException("invalid.old.password.code", "invalid.old.password.message");
            }
            
            if(isPasswordRepeated(newPassword, user.getPasswordHistory())) {
            	throw new PasswordHistoryException("enforce.password.history", "enforce.password.history.message");
            }

            PasswordHistory passwordHistory = new PasswordHistory();
            passwordHistory.setUserId(user.getId());
            passwordHistory.setPassword(user.getPassword());
            passwordHistory.setTimestamp(System.currentTimeMillis());

            // encode the new password with another random salt
            user.setPassword(delegatingPasswordEncoder.encode(newPassword).getBytes());
            user.getPasswordHistory().add(passwordHistory);
            applicationUserDao.update(user);
            entityAuditLogDao.addEntityAuditLog(AuditAction.CHANGE_PASSWORD, AuditableEntity.APPLICATION_USER.getCode(),
                    user.getId(), null, ApplicationUserDTO.getDTO(user).toAuditString());
            log.info(
                    "Application user's password updated successfully, [username :{}]",
                    user.getUsername());
        } else {
            SuperApplicationUser superUser = superUserDao.findById(userId);

            if (superUser.getUsername()
                    .equalsIgnoreCase(ApplicationUser.SUPER_USERNAME)) {
                if(!delegatingPasswordEncoder.matches(currentPassword, new String(superUser.getPassword()))) {
                	throw new InvalidPasswordException("invalid.old.password.code", "invalid.old.password.message");
                }
                
                if(isPasswordRepeated(newPassword, superUser.getPasswordHistory())) {
                	throw new PasswordHistoryException("enforce.password.history", "enforce.password.history.message");
                }
                
                PasswordHistory passwordHistory = new PasswordHistory();
                passwordHistory.setUserId(superUser.getId());
                passwordHistory.setPassword(superUser.getPassword());
                passwordHistory.setTimestamp(System.currentTimeMillis());

                // encode the new password with another random salt
                superUser.setPassword(delegatingPasswordEncoder.encode(newPassword).getBytes());
                superUser.getPasswordHistory().add(passwordHistory);
                superUserDao.update(superUser);

                entityAuditLogDao.addEntityAuditLog(AuditAction.CHANGE_PASSWORD, AuditableEntity.APPLICATION_USER.getCode(),
                        superUser.getId(), null, superUser.toAuditString());
                log.info(
                        "Super Application user password updated successfully, [username :{}]",
                        superUser.getUsername());
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.nextlabs.destiny.console.services.ApplicationUserService#
     * reIndexAllComponents()
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void reIndexAllUsers() throws ConsoleException {
        try {
        	appUserSearchRepository.deleteAll();
        	
            List<ApplicationUser> appUsers = applicationUserDao.findAllActive();
            List<SuperApplicationUser> superUsers = superUserDao.findAll();

            for (ApplicationUser user : appUsers) {
                List<AppUserProperties> userProperties = appUserPropertiesDao
                        .findByUserId(user.getId());
                Set<AppUserProperties> properties = new TreeSet<>(
                        userProperties);
                user.setProperties(properties);
                accessControlService.populateAllowedActionsAndTags(user);
                appUserSearchRepository.save(user);
            }

            for (SuperApplicationUser superUser : superUsers) {
                ApplicationUser user = new ApplicationUser();
                user.setId(superUser.getId());
                user.setUsername(superUser.getUsername());
                user.setFirstName(superUser.getFirstName());
                user.setLastName(superUser.getLastName());
                user.setUserType(USER_TYPE_INTERNAL);
                user.setUserCategory(UserCategory.ADMINISTRATOR.getCode());
                user.setDisplayName(superUser.getFirstName());
                user.setLastUpdatedDate(superUser.getLastUpdatedDate());
                user.setManualProvision(true);
                List<AppUserProperties> userProperties = appUserPropertiesDao
                        .findByUserId(superUser.getId());
                Set<AppUserProperties> properties = new TreeSet<>(
                        userProperties);
                user.setProperties(properties);
                accessControlService.populateAllowedActionsAndTags(user);
                appUserSearchRepository.save(user);
            }

            log.info(
                    "Application users re-indexing successfull, No of re-indexes :{}",
                    appUsers.size());
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in re-indexing application users", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ApplicationUser save(ApplicationUserDTO appUserDTO)
            throws ConsoleException {
    	ApplicationUser appUser = new ApplicationUser(null, 
				ApplicationUser.USER_TYPE_INTERNAL, UserCategory.getUserCategory(appUserDTO.getUserCategory()).getCode(), appUserDTO.getUsername(),
				appUserDTO.getFirstName(), appUserDTO.getLastName());

    	if(!isAllowedToManageUserCategory(appUser.getUserCategory())) {
            throw new InvalidOperationException(msgBundle.getText(disallowManageAdministratorAccountCodeKey),
            		msgBundle.getText(disallowManageAdministratorAccountKey));
    	}
    	
        String encPassword = delegatingPasswordEncoder.encode(appUserDTO.getPassword());
        appUser.setPassword(encPassword.getBytes());
        appUser.setProperties(appUserDTO.getProperties());
        appUser.setEmail(appUserDTO.getEmail());
        appUser.setInitLoginDone(UserCategory.API_ACCOUNT.getCode().equals(appUser.getUserCategory())? "Y" : "N");

        appUserDTO.getProperties().forEach(property -> {
            if(msgBundle.getText("attr.displayName.key").equals(property.getKey())) {
                appUser.setDisplayName(property.getValue());
            }
        });

        if (StringUtils.isNotEmpty(appUser.getEmail())) {
			checkUserEmailIsUnique(appUser.getEmail(), appUser.getUsername());
		}

        setJWTPassphrase(appUser);
        Long domainId = applicationUserDao.getLocalDomainId();
		appUser.setDomainId(domainId);
		appUser.setStatus(Status.ACTIVE);

		applicationUserDao.create(appUser);
		
		entityAuditLogDao.addEntityAuditLog(AuditAction.CREATE, AuditableEntity.APPLICATION_USER.getCode(), 
				appUser.getId(), null, ApplicationUserDTO.getDTO(appUser).toAuditString());
		
		if (appUser.getProperties() != null && 
				!appUser.getProperties().isEmpty()) {
			// get newly added user attributes
			Set<String> userAttributes = new LinkedHashSet<>();
			for (AppUserProperties userProperty : appUser.getProperties()) {
				userAttributes.add(userProperty.getKey());
			}
			delegateModelService.updateDAModelUserAttributes(userAttributes);
		}
		accessControlService.populateAllowedActionsAndTags(appUser);
		appUserSearchRepository.save(appUser);

		log.debug("Application User saved successfully, [ Id: {}]", appUser.getId());
      
        return appUser;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ApplicationUser modify(ApplicationUserDTO appUserDTO)
    		throws ConsoleException {
    	ApplicationUser appUser = findById(appUserDTO.getId());

    	if(appUser == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

    	// Check if able to modify stored user
    	if(!isAllowedToManageUserCategory(appUser.getUserCategory())) {
            throw new InvalidOperationException(msgBundle.getText(disallowManageAdministratorAccountCodeKey),
            		msgBundle.getText(disallowManageAdministratorAccountKey));
    	}
    	
    	// Check if promote user to administrator is possible
    	if(!isAllowedToManageUserCategory(appUserDTO.getUserCategory())) {
            throw new InvalidOperationException(msgBundle.getText(disallowManageAdministratorAccountCodeKey),
            		msgBundle.getText(disallowManageAdministratorAccountKey));
    	}
    	
		if (StringUtils.isNotEmpty(appUserDTO.getEmail())) {
			checkUserEmailIsUnique(appUserDTO.getEmail(), appUser.getUsername());
		}
    	
    	String snapshot = ApplicationUserDTO.getDTO(appUser).toAuditString();

    	// Only internal user's properties can be updated
    	if(USER_TYPE_INTERNAL.equals(appUser.getUserType())) {
            appUser.setFirstName(appUserDTO.getFirstName());
            if (StringUtils.isBlank(appUserDTO.getLastName())) {
                appUser.setLastName(" ");
            } else {
                appUser.setLastName(appUserDTO.getLastName());
            }

            appUserDTO.getProperties().forEach(property -> {
                if(msgBundle.getText("attr.displayName.key").equals(property.getKey())) {
                    appUser.setDisplayName(property.getValue());
                }
            });

            if (!StringUtils.isBlank(appUserDTO.getPassword())) {
                byte[] encPassword = delegatingPasswordEncoder.encode(appUserDTO.getPassword()).getBytes();
                appUser.setPassword(encPassword);
                appUser.setInitLoginDone(UserCategory.CONSOLE_USER.getCode().equals(appUser.getUserCategory()) ? "N" : "Y");
            }

            // Remove old record from database
            if (appUser.getProperties() != null) {
                for (AppUserProperties property : appUser.getProperties()) {
                    appUserPropertiesDao.delete(property);
                }
            }

            appUser.setProperties(appUserDTO.getProperties());
            appUser.setEmail(appUserDTO.getEmail());
            appUser.setLastUpdatedDate(LocalDateTime.now().toDate());
        }

		appUser.setUserCategory(appUserDTO.getUserCategory());
		if(UserCategory.API_ACCOUNT.getCode().equals(appUser.getUserCategory())) {
			appUser.setInitLoginDone("Y");
		}
        setJWTPassphrase(appUser);

		if (appUser.getUsername().equalsIgnoreCase(ApplicationUser.SUPER_USERNAME)) {
			modifySuperUser(appUser);
		} else {
			applicationUserDao.update(appUser);
		}
		
		entityAuditLogDao.addEntityAuditLog(AuditAction.UPDATE, AuditableEntity.APPLICATION_USER.getCode(), 
				appUser.getId(), snapshot, ApplicationUserDTO.getDTO(appUser).toAuditString());

        if (USER_TYPE_INTERNAL.equals(appUser.getUserType()) && appUser.getProperties() != null &&
                !appUser.getProperties().isEmpty()) {
            // get newly added user attributes
            Set<String> userAttributes = new LinkedHashSet<>();
            for (AppUserProperties userProperty : appUser.getProperties()) {
                userAttributes.add(userProperty.getKey());
            }
            delegateModelService.updateDAModelUserAttributes(userAttributes);
        }

        accessControlService.populateAllowedActionsAndTags(appUser);
		appUserSearchRepository.save(appUser);

		log.debug("Application User updated successfully, [ Id: {}]", appUser.getId());
        
        return appUser;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ValidationDetailDTO validateAndSave(List<ApplicationUserDTO> appUsersDTO)
			throws ConsoleException {
		ValidationDetailDTO validateDTO = new ValidationDetailDTO();

		try {
			for (ApplicationUserDTO appUserDTO : appUsersDTO) {
				ValidationRecord validateRecord = validateExternalUser(appUserDTO);
				
				if (StringUtils.isBlank(appUserDTO.getLastName())) {
					appUserDTO.setLastName(" ");
				}

				if (validateRecord.getMessages().isEmpty()) {
					ApplicationUser appUser = searchForUserName(appUserDTO.getUsername());
					if (null == appUser) {
						appUser = new ApplicationUser(null, 
								ApplicationUser.USER_TYPE_IMPORTED, UserCategory.CONSOLE_USER.getCode(),
								appUserDTO.getUsername(), 
								appUserDTO.getFirstName(), 
								appUserDTO.getLastName());
	
						appUser.setAuthHandlerId(appUserDTO.getAuthHandlerId());
						Long domainId = applicationUserDao.getLocalDomainId();
						appUser.setDomainId(domainId);
						appUser.setStatus(Status.ACTIVE);
						appUser.setLocked(false);
						appUser.setFailedLoginAttempts(0);
	
                        setJWTPassphrase(appUser);
						applicationUserDao.create(appUser);
						
						entityAuditLogDao.addEntityAuditLog(AuditAction.CREATE, AuditableEntity.APPLICATION_USER.getCode(), 
								appUser.getId(), null, ApplicationUserDTO.getDTO(appUser).toAuditString());
					} else {
						String snapshot = ApplicationUserDTO.getDTO(appUser).toAuditString();
						// same username found and authhandler is the same
						appUser.setFirstName(appUserDTO.getFirstName());
						appUser.setLastName(appUserDTO.getLastName());
						appUser.setAuthHandlerId(appUserDTO.getAuthHandlerId());
						Long domainId = applicationUserDao.getLocalDomainId();
						appUser.setDomainId(domainId);
						appUser.setStatus(Status.ACTIVE);
						appUser.setLocked(false);
						appUser.setFailedLoginAttempts(0);

                        setJWTPassphrase(appUser);
						applicationUserDao.update(appUser);
						
						entityAuditLogDao.addEntityAuditLog(AuditAction.UPDATE, AuditableEntity.APPLICATION_USER.getCode(),
								appUser.getId(), snapshot, ApplicationUserDTO.getDTO(appUser).toAuditString());
					}
					log.info("External User saved successfully, [ Id: {}]", appUser.getId());
				} else {
					validateDTO.getDetails().add(validateRecord);
				}
			}
			
			reIndexAllUsers();
		} catch (Exception e) {
			throw new ConsoleException("Error encountered while saving a User", e);
		}
		return validateDTO;
	}

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ValidationDetailDTO validateAndSaveGroup(List<ExternalGroupDTO> groupDTOList)
                    throws ConsoleException {
        ValidationDetailDTO validateDTO = new ValidationDetailDTO();

        try {
            for(ExternalGroupDTO groupDTO : groupDTOList) {
                if(userGroupRepository.findByAuthHandlerIdAndGroupId(groupDTO.getAuthHandlerId(), groupDTO.getExternalId()) == null) {
                    ProvisionedUserGroup userGroup = new ProvisionedUserGroup();
                    userGroup.setAuthHandlerId(groupDTO.getAuthHandlerId());
                    userGroup.setGroupId(groupDTO.getExternalId());
                    userGroup.setCreatedBy(SecurityContextUtil.getCurrentUser().getUserId());

                    userGroupRepository.save(userGroup);
                    entityAuditLogDao.addEntityAuditLog(AuditAction.CREATE, AuditableEntity.USER_GROUP.getCode(),
                                    userGroup.getId(), null, groupDTO.toAuditString());
                } else {
                    log.info("User group {} existed.", groupDTO.getExternalId());
                }
            }
        } catch (Exception e) {
            throw new ConsoleException("Error encountered while saving group", e);
        }

        return validateDTO;
    }

    private ValidationRecord validateExternalUser(ApplicationUserDTO appUser) {
		String username = appUser.getUsername();
		Pattern emailRegex = Pattern.compile(msgBundle.getText("email.pattern"));
		
		ValidationRecord validateRecord = new ValidationRecord();
		List<String> messages = new ArrayList<>();
		validateRecord.setCategory("User");
		validateRecord.setType("External User");
		validateRecord.setName(appUser.getUsername());	

		if (appUser.getAuthHandlerId() == null) {
			messages.add(
					msgBundle.getText("invalid.input.field.blank", "AuthHandlerId"));
		}
		validateRecord.setMsgCode(msgBundle.getText("invalid.input.field.pattern.code"));
		
		if (StringUtils.isNotEmpty(appUser.getEmail())) {
			if (!emailRegex.matcher(appUser.getEmail()).matches()) {
				messages.add(
						msgBundle.getText("invalid.input.field.pattern","Email", 
						msgBundle.getText("email.format.message")));
			}
			validateRecord.setMsgCode(msgBundle.getText(
					"invalid.input.field.pattern.code"));
			try{
				checkUserEmailIsUnique(appUser.getEmail(), 
						appUser.getUsername());
			} catch(NotUniqueException e){
				messages.add(msgBundle.getText(
						"server.error.user.email.not.unique"));
			}
		}
		
		ApplicationUser applicationUser = searchForUserName(username);
		// if same username exists and is imported from another AD
		if (applicationUser != null && !appUser.getAuthHandlerId().equals(applicationUser.getAuthHandlerId())) {
			messages.add(msgBundle.getText("duplicate.username.message"));
		}
		validateRecord.setMsgCode(msgBundle.getText("duplicate.username.code"));
		
		validateRecord.setMessages(messages);
		return validateRecord;
	}

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ApplicationUser remove(Long userId) 
    		throws ConsoleException {
        ApplicationUser appUser = applicationUserDao.findById(userId);
        
        if (appUser == null) {
            SuperApplicationUser superUser = superUserDao.findById(userId);
            if (superUser == null) {
                throw new NoDataFoundException(
                        msgBundle.getText("no.entity.found.delete.code"),
                        msgBundle.getText("no.entity.found.delete",
                                "Application User"));
            } else {
                throw new ConsoleException(msgBundle.getText(
                        "operation.not.allowed", "Super User", "delete"));
            }
        } else {
        	if(!isAllowedToManageUserCategory(appUser.getUserCategory())) {
                throw new InvalidOperationException(msgBundle.getText(disallowManageAdministratorAccountCodeKey),
                		msgBundle.getText(disallowManageAdministratorAccountKey));
        	}
        }
       	
        String snapshot = ApplicationUserDTO.getDTO(appUser).toAuditString();
        resetGAuthToken(appUser.getUsername());
        appUser.setStatus(Status.DELETED);
        appUser.setUsername(appendDeletedTimestamp(appUser.getUsername()));
        appUser.setLastName(appendDeletedDisplay(appUser.getLastName()));
        log.info("Display name: {}", appUser.getDisplayName());
        applicationUserDao.update(appUser);
        
        entityAuditLogDao.addEntityAuditLog(AuditAction.DELETE, AuditableEntity.APPLICATION_USER.getCode(), appUser.getId(), snapshot, null);

        appUserSearchRepository.deleteById(userId);
        return appUser;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ProvisionedUserGroup removeGroup(Long id, boolean removeMember)
                throws ConsoleException {
        Optional<ProvisionedUserGroup> group = userGroupRepository.findById(id);

        if (group.isPresent()) {
            ProvisionedUserGroup userGroup = group.get();
            ExternalGroupDTO externalGroupDTO = new ExternalGroupDTO();

            if(userGroup.getAuthHandlerId() > 0) {
                ExternalGroupDTO groupDetails = azureUserService.getGroup(userGroup.getAuthHandlerId(), userGroup.getGroupId());
                if (groupDetails != null) {
                    externalGroupDTO = groupDetails;
                    externalGroupDTO.setAuthHandlerId(userGroup.getAuthHandlerId());
                }
            } else {
                externalGroupDTO = ExternalGroupDTO.getDTO(userGroup);
            }

            entityAuditLogDao.addEntityAuditLog(AuditAction.DELETE, AuditableEntity.USER_GROUP.getCode(),
                            id, externalGroupDTO.toAuditString(), null);
            userGroupRepository.deleteById(id);

            if(removeMember) {
                List<ProvisionedUserGroup> deletedGroups = new ArrayList<>();
                deletedGroups.add(userGroup);

                deleteUserWithoutProvisionGroup(deletedGroups);
            }

            return userGroup;
        } else {
            throw new NoDataFoundException(
                msgBundle.getText("no.entity.found.delete.code"),
                msgBundle.getText("no.entity.found.delete",
                                "User Group"));
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<ApplicationUser> remove(List<Long> ids) throws ConsoleException {
        List<ApplicationUser> deletedUsers = new ArrayList<>(ids.size());
        for (Long id : ids) {
            deletedUsers.add(remove(id));
        }
        return deletedUsers;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<ProvisionedUserGroup> removeGroups(List<Long> ids) throws ConsoleException {
        List<ProvisionedUserGroup> deletedGroups = new ArrayList<>();
        for (Long id : ids) {
            deletedGroups.add(removeGroup(id, false));
        }

        deleteUserWithoutProvisionGroup(deletedGroups);

        return deletedGroups;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Page<ApplicationUser> findUserByCriteria(SearchCriteria criteria)
            throws ConsoleException {
        try {
            log.debug("Search Criteria :[{}]", criteria);
            PageRequest pageable = PageRequest.of(criteria.getPageNo(),
                    criteria.getPageSize());

            List<SearchField> searchFields = criteria.getFields();
            BoolQueryBuilder query = buildQuery(searchFields);

            if(!SecurityContextUtil.getCurrentUser().isSuperUser()) {
            	query.mustNot(QueryBuilders.matchQuery("userCategory", UserCategory.ADMINISTRATOR.getCode()));
            }

            Query searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(query).withPageable(pageable).build();

            searchQuery = withSorts(searchQuery, criteria.getSortFields());

            log.debug("User search query :{},", query);
            Page<ApplicationUser> appUsersListPage = appUserSearchRepository
                    .search(searchQuery);
            
            // Locked flag update by CAS is not sync to indexer, need to retrieve from database manually
            for(ApplicationUser appUser : appUsersListPage) {
            	ApplicationUser user = applicationUserDao.findById(appUser.getId());
            	
            	if(user != null) {
            		appUser.setLocked(user.isLocked());
            	}
            }
            
            log.info("Users list page :{}, No of elements :{}",
                    appUsersListPage, appUsersListPage.getNumberOfElements());
            return appUsersListPage;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find users by given criteria", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<ExternalGroupDTO> findUserGroupByCriteria(List<AdSearchRequest> criteria)
            throws ConsoleException {
        try {
            List<ExternalGroupDTO> provisionedUserGroups = new ArrayList<>();
            int pageSize = 0;

            for(AdSearchRequest criterion : criteria) {
                if(pageSize < criterion.getPageSize()) {
                    pageSize = criterion.getPageSize();
                }

                if(authenticationHandlerService.isType(criterion.getHandlerId(), AuthHandlerType.OIDC.name())) {
                    AdSearchResponse response = azureUserService.getAllProvisionedGroups(criterion.getHandlerId(), criterion.getSearchText(), criterion.getPageSize());
                    for(ExternalGroupDTO group : response.getAdGroups()) {
                        group.setAuthHandlerId(criterion.getHandlerId());
                        provisionedUserGroups.add(group);
                        if (provisionedUserGroups.size() == pageSize) {
                            return provisionedUserGroups;
                        }
                    }
                }
            }

            return provisionedUserGroups;
        } catch (Exception e) {
            throw new ConsoleException(
                            "Error encountered in find user groups by given criteria", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateLastLoggedIn(Long userId) {
        ApplicationUser user = applicationUserDao.findById(userId);
        if (user != null) {
            user.setLoggedInTime(System.currentTimeMillis());
            applicationUserDao.update(user);
            log.debug(
                    "Application user's last logged in time updated successfully, [username :{}]",
                    user.getUsername());
        } else {
            SuperApplicationUser superUser = superUserDao.findById(userId);
            if (superUser.getUsername()
                    .equalsIgnoreCase(ApplicationUser.SUPER_USERNAME)) {
                superUser.setLoggedInTime(System.currentTimeMillis());
                superUserDao.update(superUser);
                log.debug(
                        "Super Application user last logged in updated successfully, [username :{}]",
                        superUser.getUsername());
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateHideSplash(Long userId, boolean hideSplash) {
        ApplicationUser user = applicationUserDao.findById(userId);
        if (user != null) {
            user.setHideSplash(hideSplash);
            applicationUserDao.update(user);
            log.info(
                    "Application user's hide splash screen flag updated successfully, [username :{}]",
                    user.getUsername());
        } else {
            SuperApplicationUser superUser = superUserDao.findById(userId);
            if (superUser.getUsername()
                    .equalsIgnoreCase(ApplicationUser.SUPER_USERNAME)) {
                superUser.setHideSplash(hideSplash);
                superUserDao.update(superUser);
                log.info(
                        "Super Application user's hide splash screen flag updated successfully, [username :{}]",
                        superUser.getUsername());
            }
        }
    }

    private void modifySuperUser(ApplicationUser appUser)
            throws ConsoleException {
        log.info("Request came to modify super user");
        try {
            SuperApplicationUser superUser = superUserDao
                    .findById(appUser.getId());
            superUser.setFirstName(appUser.getFirstName());
            superUser.setLastName(appUser.getLastName());
            superUser.setHideSplash(appUser.isHideSplash());
            superUser.setLoggedInTime(appUser.getLoggedInTime());
            superUser.setProperties(appUser.getProperties());
            superUser.setEmail(appUser.getEmail());
            if (appUser.getPassword() != null && appUser.getPassword().length != 0) {
                superUser.setPassword(appUser.getPassword());
            }

            superUserDao.update(superUser);
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error occured while updating Super User details", e);
        }
    }
    
    @Override
    public void setExternalUserProperties(ApplicationUser appUser, Map<String, String> userAttributes) {
		Set<AppUserProperties> userProperties = new TreeSet<>();
		for (String propKey : userAttributes.keySet()) {
			if (propKey.equals(msgBundle.getText("attr.email.key"))){
				appUser.setEmail(userAttributes.get(propKey));
			}else{
				AppUserProperties property = new AppUserProperties();
				property.setUserId(appUser.getId());
				property.setKey(propKey);
				property.setValue(userAttributes.get(propKey));
				property.setDataType(DataType.STRING);
				userProperties.add(property);
			}
		}
		appUser.setProperties(userProperties);
	}

    @Override
    @Transactional
    public ApplicationUser unlock(Long userId) throws ConsoleException {
    	ApplicationUser user = applicationUserDao.findById(userId);

        if(user != null) {
            if(!isAllowedToManageUserCategory(user.getUserCategory())) {
                throw new InvalidOperationException(msgBundle.getText(disallowManageAdministratorAccountCodeKey),
                        msgBundle.getText(disallowManageAdministratorAccountKey));
            }
    	
    		user.setLocked(false);
    		user.setFailedLoginAttempts(0);
    		log.info("Application user account unlocked successfully, [username :{}]",
    				user.getUsername());
    		applicationUserDao.update(user);
            accessControlService.populateAllowedActionsAndTags(user);
    		appUserSearchRepository.save(user);
    	}
    	
    	return user;
    }
    
    private void checkUserEmailIsUnique(String email, String username) {

		ApplicationUser user = applicationUserDao.findByEmail(email);

		if (user != null 
				&& !(user.getUsername().equals(username))
				&& user.getStatus().equals(Status.ACTIVE)) {
			throw new NotUniqueException(
					msgBundle.getText("server.error.not.unique.code"),
					msgBundle.getText("server.error.user.email.not.unique",
							email));
		}
	}

    private boolean isPasswordRepeated(String newPassword, List<PasswordHistory> passwordHistory) {
        if (passwordEnforcementProperties.getHistory() > 0 && passwordHistory != null && !passwordHistory.isEmpty()) {
            for (int i = 0; (i < passwordEnforcementProperties.getHistory() - 1 && i < passwordHistory.size()); i++) {
                if (delegatingPasswordEncoder.matches(newPassword, new String(passwordHistory.get(i).getPassword()))) {
                    return true;
                }
            }
        }
        return false;
    }
	
	/**
	 * Only administrator user allowed to manage administrator account
	 * @param userCategory User account's category to manage
	 * @return true if current logged in user has the authority to manage this account category
	 */
	private boolean isAllowedToManageUserCategory(String userCategory) {
		if(UserCategory.ADMINISTRATOR.getCode().equals(userCategory)) {
			PrincipalUser currentUser = SecurityContextUtil.getCurrentUser();
			
			if(currentUser != null) {
				return currentUser.isSuperUser();
			}
			
			return false;
		}
		
		return true;
	}
	
	private String appendDeletedTimestamp(String username) {
		return username + "_" + usernameDateFormat.format(new Date());
	}
	
	private String appendDeletedDisplay(String displayName) {
		return displayName + " (deactivated " + displayNameDateFormat.format(new Date()) + ")";
	}

    /**
     * Set jwt_passphrase for API user account if it is not provided in the payload
     *
     * @param appUser
     */
	private void setJWTPassphrase(ApplicationUser appUser) {
        if(UserCategory.API_ACCOUNT.getCode().equals(appUser.getUserCategory())) {
            boolean passphraseProvided = false;

            for(AppUserProperties prop : appUser.getProperties()) {
                if(JWT_PASSPHRASE.equals(prop.getKey())) {
                    passphraseProvided = true;
                    break;
                }
            }

            if(!passphraseProvided) {
                try {
                    KeyGenerator randomGenerator = KeyGenerator.getInstance("AES");
                    randomGenerator.init(256);

                    AppUserProperties jwtPassphrase = new AppUserProperties();
                    jwtPassphrase.setKey(JWT_PASSPHRASE);
                    jwtPassphrase.setValue(Base64.getEncoder().encodeToString(randomGenerator.generateKey().getEncoded()));
                    appUser.getProperties().add(jwtPassphrase);
                } catch(NoSuchAlgorithmException err) {
                    // Ignore
                }
            }
        }
    }

    private void deleteUserWithoutProvisionGroup(List<ProvisionedUserGroup> groups)
            throws ConsoleException {
        Set<Long> processedAuthHandlerIds = new HashSet<>();

        for(ProvisionedUserGroup group : groups) {
            if(!processedAuthHandlerIds.contains(group.getAuthHandlerId())) {
                List<ApplicationUser> usersToRemove;
                if(authenticationHandlerService.isType(group.getAuthHandlerId(), AuthHandlerType.OIDC.name())) {
                    usersToRemove = azureUserService.getUserWithoutProvisionedGroups(group.getAuthHandlerId());
                } else {
                    usersToRemove = ldapUserService.getUserWithoutProvisionedGroups(group.getAuthHandlerId());
                }

                for(ApplicationUser user : usersToRemove) {
                    remove(user.getId());
                }

                processedAuthHandlerIds.add(group.getAuthHandlerId());
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void resetGAuthToken(String username) {
        applicationUserDao.resetGAuthTokenByUsername(username);
    }

}
