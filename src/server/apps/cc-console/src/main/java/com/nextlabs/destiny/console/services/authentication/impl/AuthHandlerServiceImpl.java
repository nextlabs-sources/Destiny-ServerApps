/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 1 Aug 2016
 *
 */
package com.nextlabs.destiny.console.services.authentication.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.nextlabs.destiny.console.config.ReversibleTextEncryptor;
import com.nextlabs.destiny.console.dao.ApplicationUserDao;
import com.nextlabs.destiny.console.dao.authentication.AuthHandlerTypeDetailDao;
import com.nextlabs.destiny.console.dto.authentication.AuthHandlerDetail;
import com.nextlabs.destiny.console.dto.authentication.ComplexUserAttribute;
import com.nextlabs.destiny.console.dto.authentication.IdProvider;
import com.nextlabs.destiny.console.dto.authentication.IdProviderFactory;
import com.nextlabs.destiny.console.dto.authentication.Pac4jOidcType;
import com.nextlabs.destiny.console.enums.AuthHandlerType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.InvalidInputParamException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.ProvisionedUserGroup;
import com.nextlabs.destiny.console.model.authentication.AuthHandlerTypeDetail;
import com.nextlabs.destiny.console.model.configuration.SysConfig;
import com.nextlabs.destiny.console.repositories.ProvisionedUserGroupRepository;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.SysConfigService;
import com.nextlabs.destiny.console.services.authentication.AuthHandlerService;
import com.nextlabs.destiny.console.services.authentication.DelegationResourceService;
import com.nextlabs.destiny.console.services.delegadmin.DelegateModelService;
import com.nextlabs.destiny.console.services.user.ExternalUserService;
import com.nextlabs.destiny.console.utils.SecurityContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.nextlabs.destiny.console.dto.authentication.AuthHandlerDetail.*;

/**
 *
 * Service implementation for Authentication Handlers Registration 
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@Service
public class AuthHandlerServiceImpl implements AuthHandlerService {

	private static final Logger log = LoggerFactory.getLogger(AuthHandlerServiceImpl.class);

	@Autowired
	private AuthHandlerTypeDetailDao authHandlerDao;
	
	@Autowired
	private ApplicationUserDao appUserDao;
	
	@Autowired
	private DelegateModelService delegateModelService;

	@Autowired
	protected MessageBundleService msgBundle;

	@Autowired
	private SysConfigService sysConfigService;

	@Resource
    private ApplicationUserSearchRepository appUserSearchRepository;
	
	@Autowired
	@Qualifier("ADUsersService")
	private ExternalUserService adUserService;
	
	@Autowired
	@Qualifier("AzureUserService")
	private ExternalUserService azureUserService;

	@Autowired
	@Qualifier("OidcDelegationResourceService")
	private DelegationResourceService oidcDelegationResourceService;

	@Autowired
	@Qualifier("Saml2DelegationResourceService")
	private DelegationResourceService saml2DelegationResourceService;

	@Autowired
	private ProvisionedUserGroupRepository userGroupRepository;

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public AuthHandlerTypeDetail saveHandler(AuthHandlerTypeDetail authHandler, AuthHandlerDetail handlerDetail)
			throws ConsoleException {
		ObjectMapper mapper =  new ObjectMapper();
		try {
			if (authHandler.getId() == null) {
				authHandlerDao.create(authHandler);
			} else {
				authHandler.setLastUpdatedDate(new Date());
				authHandler.setLastUpdatedBy(SecurityContextUtil.getCurrentUser().getUserId());
				authHandlerDao.update(authHandler);
			}
			if (authHandler.getUserAttrsJson() != null) {
				// get external user attributes
				Set<String> extUserProps = new TreeSet<>();
				if (AuthHandlerType.SAML2.toString().equals(authHandler.getType())) {
					for (ComplexUserAttribute prop : mapper
									.readValue(authHandler.getUserAttrsJson(), ComplexUserAttribute[].class)) {
						extUserProps.add(prop.getMappedAs());
					}
				} else {
					extUserProps.addAll(
									mapper.readValue(authHandler.getUserAttrsJson(), HashMap.class).keySet());
				}

				if (!extUserProps.isEmpty()) {
					delegateModelService.updateDAModelUserAttributes(extUserProps);
				}
			}

			updateIdProviderProperties(authHandler.getAccountId(), authHandler.getType());
			updateIdProviderResources(handlerDetail);
			log.debug("Authentication Handler saved successfully, [ Id: {}]", authHandler.getId());
		} catch (InvalidInputParamException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new ConsoleException("Error occurred while saving a handler", ex);
		}
		return authHandler;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public AuthHandlerTypeDetail findById(Long id) throws ConsoleException {
		AuthHandlerTypeDetail authHandler;
		try {
			authHandler = authHandlerDao.findById(id);
			
			if (authHandler == null) {
				log.info("Auth Handler not found for id :{}", id);
				throw new NoDataFoundException(
						msgBundle.getText("no.data.found.code"),
						msgBundle.getText("no.data.found"));
			}
			authHandler.setInUse((appUserDao.getActiveUserCountByHandlerId(id) > 0));
		} catch (Exception ex) {
			throw new ConsoleException("Error encountered while fetching auth handler by id",
					ex);
		}
		return authHandler;
	}

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public boolean isType(Long id, String type) throws ConsoleException {
        try {
            AuthHandlerTypeDetail authHandler = authHandlerDao.findById(id);
            
            if(authHandler != null) {
                return authHandler.getType().equals(type);
            }
        } catch (Exception ex) {
            throw new ConsoleException(ex.getMessage(),    ex);
        }
        
        return false;
    }

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<AuthHandlerTypeDetail> findByType(String type) 
			throws ConsoleException {
		try {
			List<AuthHandlerTypeDetail> authHandlers = authHandlerDao.
					findByType(type);
			
			for(AuthHandlerTypeDetail authHandler : authHandlers) {
				authHandler.setInUse((appUserDao.getActiveUserCountByHandlerId(authHandler.getId()) > 0));
			}
			
			log.debug("Auth Handlers found successfully by Type, [ Type: {}]", 
					type);

			return authHandlers;

		} catch (Exception ex) {
			throw new ConsoleException("Error occurred while finding auth handler by type",
					ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean removeHandler(Long id) throws ConsoleException {
		try {
			AuthHandlerTypeDetail authHandler = authHandlerDao.findById(id);
			if (authHandler != null) {
				if (isDeleteAllowed(id)){
					List<ProvisionedUserGroup> userGroups = userGroupRepository.findByAuthHandlerId(id);
					for(ProvisionedUserGroup userGroup : userGroups) {
						userGroupRepository.deleteById(userGroup.getId());
					}

					authHandlerDao.delete(authHandler);
					updateIdProviderProperties(authHandler.getAccountId(), authHandler.getType());
					cleanUpIdProviderResources(authHandler);
					return true;
				}else{
					return false;
				}		
			} else {
				throw new NoDataFoundException(
						msgBundle.getText("no.entity.found.delete.code"),
						msgBundle.getText("no.entity.found.delete", "Authentication Handler"));
			}
		} catch (Exception ex) {
			throw new ConsoleException("Error occurred while removing auth handler",
					ex);
		}
	}
	
	public boolean isDeleteAllowed(Long authHandlerId){
		boolean canDelete = false;
		PageRequest pageable = PageRequest.of(0, 1);
		Page<ApplicationUser> importedUsersPage = appUserSearchRepository.
				findByAuthHandlerId(authHandlerId, pageable);
		if (importedUsersPage != null) {
			List<ApplicationUser> importedUsers = importedUsersPage.getContent();
			if (importedUsers.isEmpty()){
				canDelete = true;
			}
		}
		return canDelete;
	}

	@Override
	public void checkHandlerConnection(AuthHandlerDetail authHandler) {
		String type = authHandler.getType();

		if (AuthHandlerType.LDAP.name().equals(type)) {
			verifyLDAPConnection(authHandler);
		} else if (AuthHandlerType.OIDC.name().equals(type)) {
			verifyOidcAuthorityConnection(authHandler);
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<AuthHandlerTypeDetail> findAllAuthHandlers() 
			throws ConsoleException {
		try {
			List<AuthHandlerTypeDetail> authHandlers = authHandlerDao.
					findAll();
			for(AuthHandlerTypeDetail authHandler : authHandlers) {
				authHandler.setInUse((appUserDao.getActiveUserCountByHandlerId(authHandler.getId()) > 0));
			}
			return authHandlers;
		} catch (Exception ex) {
			throw new ConsoleException("Error encountered while fetching all auth handlers",
					ex);
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean isSameAuthorityExisted(AuthHandlerType authHandlerType, String accountId, Long otherThanThis)
			throws ConsoleException {
		try {
			List<AuthHandlerTypeDetail> authHandlers = authHandlerDao.findByType(authHandlerType.name());
			for(AuthHandlerTypeDetail authHandler : authHandlers) {
				if(authHandler.getAccountId().equals(accountId)) {
					AuthHandlerDetail handler = AuthHandlerDetail.getDTO(authHandler);
					if(Boolean.valueOf(handler.getConfigData().get(ENABLED))
							&& handler.getId().longValue() != otherThanThis.longValue()) {
						return true;
					}
				}
			}
			
			return false;
		} catch (Exception ex) {
			throw new ConsoleException("Error encountered while fetching all auth handlers",
					ex);
		}
	}
	
	private void verifyLDAPConnection(AuthHandlerDetail authHandlerDto) {

			log.info("LDAP details from DTO are = url = {}, baseDn = {}", 
					authHandlerDto.getConfigData().get(LDAP_URL),
					authHandlerDto.getConfigData().get(BASE_DN));

			adUserService.checkConnection(authHandlerDto);
	}
	
	private void verifyOidcAuthorityConnection(AuthHandlerDetail authHandlerDto) {
		if(Pac4jOidcType.AZURE.toString().equals(authHandlerDto.getAccountId())) {
			azureUserService.checkConnection(authHandlerDto);
		}
	}

	/**
	 * Update Id Provider's configuration into SYS_CONFIG table.
	 * As of 9.0, only handler type OIDC.AZURE is required to perform update into SYS_CONFIG
	 *
	 * @param accountId Authentication handler's account identifier
	 * @param type Authentication handler protocol type
	 */
	@SuppressWarnings("unchecked")
	private void updateIdProviderProperties(String accountId, String type)
			throws ConsoleException {
		IdProvider idProvider = IdProviderFactory.getProvider(type, accountId);
		if (idProvider != null) {
			try {
				log.info(String.format("Main Group: [%s] ;Type: [%s]", IdProviderFactory.ID_PROVIDER, type.toLowerCase()));
				sysConfigService.deleteAll(sysConfigService.findByMainGroupAndSubGroup(
								IdProviderFactory.ID_PROVIDER, type.toLowerCase()));

				log.info("Number of config left: {}", sysConfigService.findByMainGroupAndSubGroup(
								IdProviderFactory.ID_PROVIDER, type.toLowerCase()).size());
				List<AuthHandlerTypeDetail> authenticationHandlers = authHandlerDao.findByType(type);
				ObjectMapper mapper = new ObjectMapper();
				int configIndex = 0;

				for (AuthHandlerTypeDetail handler : authenticationHandlers) {
					Map<String, String> configData = mapper.readValue(handler.getConfigDataJson(), HashMap.class);

					if (Boolean.valueOf(configData.get(ENABLED))) {
						Map<String, String> systemConfigMapping =
										idProvider.getSystemConfigMap(configIndex, configData,
														handler.getUserAttrsJson());
						sysConfigService.saveAll(convertToSysConfig(type, systemConfigMapping));

						configIndex++;
					}
				}
			} catch(JsonProcessingException e) {
				throw new ConsoleException(e.getMessage(), e);
			}
		}
	}

	private void updateIdProviderResources(AuthHandlerDetail handlerDetail)
			throws Exception {
		if(AuthHandlerType.OIDC.toString().equals(handlerDetail.getType())) {
			oidcDelegationResourceService.configure(handlerDetail);
		} else if(AuthHandlerType.SAML2.toString().equals(handlerDetail.getType())) {
			saml2DelegationResourceService.configure(handlerDetail);
		}
	}

	private void cleanUpIdProviderResources(AuthHandlerTypeDetail authHandler)
			throws ConsoleException {
		if(AuthHandlerType.OIDC.toString().equals(authHandler.getType())) {
			oidcDelegationResourceService.cleanUp();
		} else if(AuthHandlerType.SAML2.toString().equals(authHandler.getType())) {
			saml2DelegationResourceService.cleanUp();
		}
	}

	/**
	 * Convert key value pair of authentication handler into SysConfig model object
	 *
	 * @param systemConfigMapping Authentication handler mapped to system configuration object
	 * @return List of SysConfig object for a authentication handler
	 */
	private List<SysConfig> convertToSysConfig(String type, Map<String, String> systemConfigMapping) {
		List<SysConfig> sysConfigs = new ArrayList<>();
		long displayOrder = 0;

		for(Map.Entry<String, String> config : systemConfigMapping.entrySet()) {
			SysConfig sysConfig = new SysConfig();

			sysConfig.setApplication(com.nextlabs.destiny.console.enums.Service.CAS.toString().toLowerCase());
			sysConfig.setConfigKey(config.getKey());
			sysConfig.setValue(config.getValue());
			sysConfig.setDefaultValue(config.getValue());
			sysConfig.setMainGroup(IdProviderFactory.ID_PROVIDER);
			sysConfig.setSubGroup(type.toLowerCase());
			sysConfig.setConfigOrder(displayOrder);
			sysConfig.setHidden(true);
			sysConfig.setReadOnly(true);
			sysConfig.setUi(false);
			sysConfig.setRestartRequired(false);
			if(config.getValue() != null
					&& config.getValue().startsWith(ReversibleTextEncryptor.ENCRYPTED_VALUE_PREFIX)) {
				sysConfig.setEncrypted(true);
				sysConfig.setDataType("password");
				sysConfig.setFieldType("password");
				sysConfig.setValueFormat(ReversibleTextEncryptor.CIPHER_VALUE_FORMAT);
			} else {
				sysConfig.setEncrypted(false);
				sysConfig.setDataType("text");
				sysConfig.setFieldType("text");
			}
			sysConfig.setRequired(true);
			sysConfig.setModifiedBy(-1);
			sysConfig.setModifiedOn(new Date());
			sysConfig.setMainGroupOrder(99);
			sysConfig.setSubGroupOrder(99);
			sysConfig.setAdvanced(true);

			sysConfigs.add(sysConfig);
			displayOrder++;
		}

		return sysConfigs;
	}
}
