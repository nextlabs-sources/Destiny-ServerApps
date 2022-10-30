/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 1 Aug 2016
 *
 */
package com.nextlabs.destiny.console.controllers.authentication;

import com.bluejungle.framework.crypt.ReversibleEncryptor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.controllers.AbstractRestController;
import com.nextlabs.destiny.console.dto.authentication.AuthHandlerDetail;
import com.nextlabs.destiny.console.dto.authentication.ComplexUserAttribute;
import com.nextlabs.destiny.console.dto.authentication.activedirectory.AdSearchRequest;
import com.nextlabs.destiny.console.dto.authentication.activedirectory.AdSearchResponse;
import com.nextlabs.destiny.console.dto.common.CollectionDataResponseDTO;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.enums.AuthHandlerType;
import com.nextlabs.destiny.console.exceptions.ConnectionFailedException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.EntityInUseException;
import com.nextlabs.destiny.console.exceptions.InvalidInputParamException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.exceptions.NotUniqueException;
import com.nextlabs.destiny.console.model.authentication.AuthHandlerTypeDetail;
import com.nextlabs.destiny.console.services.FileResourceService;
import com.nextlabs.destiny.console.services.SysConfigService;
import com.nextlabs.destiny.console.services.authentication.AuthHandlerService;
import com.nextlabs.destiny.console.services.user.ExternalUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * REST Controller for Authentication Handlers
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("/auth/handlers")
public class AuthHandlerController extends AbstractRestController {

	private static final Logger log = LoggerFactory.getLogger(AuthHandlerController.class);
	
	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private AuthHandlerService authHandlerService;
	
	@Autowired
	@Qualifier("ADUsersService")
	private ExternalUserService ldapUserService;
	
	@Autowired
	@Qualifier("AzureUserService")
	private ExternalUserService azureUserService;

	@Autowired
	private SysConfigService sysConfigService;

	@Autowired
	private FileResourceService fileResourceService;

	@SuppressWarnings({ "rawtypes" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value = "/add")
	public ConsoleResponseEntity add(@RequestBody AuthHandlerDetail authHandlerDto)
			throws ConsoleException {
		return addHandler(authHandlerDto);
	}

	@SuppressWarnings({ "rawtypes" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value = "/addWithAttachment", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ConsoleResponseEntity addWithAttachment(
				@RequestPart("config") AuthHandlerDetail authHandlerDto,
				@RequestPart("loginImage") MultipartFile loginImage,
				@RequestPart("idpMetadata") MultipartFile idpMetadata)
			throws ConsoleException {

		validations.assertNotNull(loginImage, "login image");
		validations.assertNotNull(idpMetadata, "idp metadata");

		authHandlerDto.getResources().put("loginImage", loginImage);
		authHandlerDto.getResources().put("idpMetadata", idpMetadata);

		return addHandler(authHandlerDto);
	}

	private ConsoleResponseEntity addHandler(AuthHandlerDetail authHandlerDto)
			throws ConsoleException {
		log.debug("Request came to add new auth handler"); 
        validations.assertNotBlank(authHandlerDto.getType(), "type");   
        validations.assertNotNull(authHandlerDto.getConfigData(), 
        		"configData");

		checkMandatoryAttributes(authHandlerDto);
		AuthHandlerType authHandlerType = AuthHandlerType.get(authHandlerDto.getType());
		if((AuthHandlerType.OIDC.equals(authHandlerType)
						|| AuthHandlerType.SAML2.equals(authHandlerType))
				&& Boolean.valueOf(authHandlerDto.getConfigData().get("enabled"))
				&& authHandlerService.isSameAuthorityExisted(authHandlerType, authHandlerDto.getAccountId(), 0L)) {
			throw new NotUniqueException(msgBundle.getText("duplicate.authority.code"),
                    msgBundle.getText("duplicate.authority.message"));
		}

		if(AuthHandlerType.SAML2.equals(authHandlerType)) {
			Set<String> nameSet = new HashSet<>();
			Set<String> friendlyNameSet = new HashSet<>();
			Set<String> mapAsNameSet = new HashSet<>();

			for(ComplexUserAttribute attr : authHandlerDto.getComplexUserAttributes()) {
				if(nameSet.contains(attr.getName())) {
					throw new InvalidInputParamException(msgBundle.getText("invalid.input.field.duplicated.code"),
							msgBundle.getText("invalid.input.field.duplicated", "name", attr.getName()));
				} else {
					nameSet.add(attr.getName());
				}
				if(friendlyNameSet.contains(attr.getFriendlyName())) {
					throw new InvalidInputParamException(msgBundle.getText("invalid.input.field.duplicated.code"),
							msgBundle.getText("invalid.input.field.duplicated", "friendly name", attr.getFriendlyName()));
				} else {
					friendlyNameSet.add(attr.getFriendlyName());
				}
				if(mapAsNameSet.contains(attr.getMappedAs())) {
					throw new InvalidInputParamException(msgBundle.getText("invalid.input.field.duplicated.code"),
							msgBundle.getText("invalid.input.field.duplicated", "map to", attr.getMappedAs()));
				} else {
					mapAsNameSet.add(attr.getMappedAs());
				}
			}
		}

        AuthHandlerTypeDetail authHandler = AuthHandlerTypeDetail.getHandlerFromDetail(
        		authHandlerDto);
        authHandler.setAccountId(authHandlerDto.getAccountId());
        authHandler = authHandlerService.saveHandler(authHandler, authHandlerDto);
        notifyCAS();

        SimpleResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), authHandler.getId());

        log.info("New handler saved successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
	}

	@SuppressWarnings({ "rawtypes" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PutMapping( value = "/modify")
	public ConsoleResponseEntity<SimpleResponseDTO> modify(
					@RequestBody AuthHandlerDetail authHandlerDto)
			throws ConsoleException, JsonProcessingException {
		return modifyHandler(authHandlerDto);
	}

	@SuppressWarnings({ "rawtypes" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping( value = "/modifyWithAttachment", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ConsoleResponseEntity<SimpleResponseDTO>  modifyWithAttachment(
				@RequestPart("config") @Valid AuthHandlerDetail authHandlerDto,
				@RequestPart(value = "loginImage", required = false) MultipartFile loginImage,
				@RequestPart(value = "idpMetadata", required = false) MultipartFile idpMetadata)
			throws ConsoleException, JsonProcessingException {
		if(loginImage != null)
			authHandlerDto.getResources().put("loginImage", loginImage);

		if(idpMetadata != null)
			authHandlerDto.getResources().put("idpMetadata", idpMetadata);

		return modifyHandler(authHandlerDto);
	}

	private ConsoleResponseEntity<SimpleResponseDTO> modifyHandler(AuthHandlerDetail authHandlerDto)
			throws ConsoleException, JsonProcessingException {
		log.debug("Request came to modify auth handler");
		validations.assertNotZero(authHandlerDto.getId(), "id");
        validations.assertNotBlank(authHandlerDto.getType(), "type");
        Map<String, String> configData = authHandlerDto.getConfigData();
		validations.assertNotNull(configData, "configData");

		checkMandatoryAttributes(authHandlerDto);
		AuthHandlerType authHandlerType = AuthHandlerType.get(authHandlerDto.getType());
		if((AuthHandlerType.OIDC.equals(authHandlerType)
						|| AuthHandlerType.SAML2.equals(authHandlerType))
				&& Boolean.valueOf(authHandlerDto.getConfigData().get("enabled"))
				&& authHandlerService.isSameAuthorityExisted(authHandlerType, authHandlerDto.getAccountId(), authHandlerDto.getId())) {
			throw new NotUniqueException(msgBundle.getText("duplicate.authority.code"),
                    msgBundle.getText("duplicate.authority.message"));
		}

		if(AuthHandlerType.SAML2.equals(authHandlerType)) {
			Set<String> nameSet = new HashSet<>();
			Set<String> friendlyNameSet = new HashSet<>();
			Set<String> mapAsNameSet = new HashSet<>();

			for(ComplexUserAttribute attr : authHandlerDto.getComplexUserAttributes()) {
				if(nameSet.contains(attr.getName())) {
					throw new InvalidInputParamException(msgBundle.getText("invalid.input.field.duplicated.code"),
							msgBundle.getText("invalid.input.field.duplicated", "name", attr.getName()));
				} else {
					nameSet.add(attr.getName());
				}
				if(friendlyNameSet.contains(attr.getFriendlyName())) {
					throw new InvalidInputParamException(msgBundle.getText("invalid.input.field.duplicated.code"),
							msgBundle.getText("invalid.input.field.duplicated", "friendly name", attr.getFriendlyName()));
				} else {
					friendlyNameSet.add(attr.getFriendlyName());
				}
				if(mapAsNameSet.contains(attr.getMappedAs())) {
					throw new InvalidInputParamException(msgBundle.getText("invalid.input.field.duplicated.code"),
							msgBundle.getText("invalid.input.field.duplicated", "map to", attr.getMappedAs()));
				} else {
					mapAsNameSet.add(attr.getMappedAs());
				}
			}
		}

		AuthHandlerTypeDetail authHandler = authHandlerService.findById(authHandlerDto.getId());
		if (authHandler == null) {
			throw new NoDataFoundException(
							msgBundle.getText("no.data.found.code"),
							msgBundle.getText("no.data.found"));
		}
		authHandler.setResources(authHandlerDto.getResources());
        authHandler.setName(authHandlerDto.getName());
        authHandler.setType(authHandlerDto.getType());
        authHandler.setAccountId(authHandlerDto.getAccountId());

        ReversibleEncryptor encryptor = new ReversibleEncryptor();
        
        if(configData.containsKey("password")) {
        	configData.put("password", encryptor.encrypt(configData.get("password")));
        }
        
        if(configData.containsKey("applicationKey")) {
        	configData.put("applicationKey", encryptor.encrypt(configData.get("applicationKey")));
        }
        
		authHandler.setConfigDataJson(mapper.writeValueAsString(configData));
        if(AuthHandlerType.SAML2 == AuthHandlerType.get(authHandlerDto.getType())) {
			authHandler.setUserAttrsJson(mapper.writeValueAsString(authHandlerDto.getComplexUserAttributes()));
		} else {
			authHandler.setUserAttrsJson(mapper.writeValueAsString(authHandlerDto.getUserAttributes()));
		}

        authHandler = authHandlerService.saveHandler(authHandler, authHandlerDto);
		notifyCAS();
        
        SimpleResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.modified.code"),
                msgBundle.getText("success.data.modified"), authHandler.getId());

        log.info("Auth handler modified successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value = "/verify")
	public ResponseDTO checkHandlerConnection(@RequestBody AuthHandlerDetail 
			authHandlerDto) throws ConsoleException {

		try {
			authHandlerService.checkHandlerConnection(authHandlerDto);
			return ResponseDTO.create(
					msgBundle.getText("success.auth.handler.verify.code"),
					msgBundle.getText("success.auth.handler.verify"));
		} catch (ConnectionFailedException ex) {
			return ResponseDTO.create(
					msgBundle.getText("auth.handler.verify.failed.code"),
					ex.getStatusMsg());
		} catch (Exception ex) {
			return ResponseDTO.create(
					msgBundle.getText("auth.handler.verify.failed.code"),
					ex.getMessage());
		}
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping(value = "/remove/{id}")
	public ConsoleResponseEntity<ResponseDTO> removeHandler(
			@PathVariable("id") Long id) throws ConsoleException {

		log.debug("Request came to remove a handler");
		validations.assertNotZero(id, "id");

        AuthHandlerTypeDetail authHandler = authHandlerService.findById(id);
		if (authHandler == null) {
			throw new NoDataFoundException(
							msgBundle.getText("no.data.found.code"),
							msgBundle.getText("no.data.found"));
		}

		if(authHandler.isInUse()) {
        	throw new EntityInUseException(msgBundle.getText("operation.not.allowed.code"), msgBundle.getText("entity.in.use"));
        }
		
		boolean isDeleted = authHandlerService.removeHandler(id);
		notifyCAS();

		ResponseDTO response = null;
		if (isDeleted){
			response = ResponseDTO.create(
					msgBundle.getText("success.data.deleted.code"),
					msgBundle.getText("success.data.deleted"));
			log.info("Handler removed successfully and response sent");
		}
		 else {
	            response = ResponseDTO.create(
	                    msgBundle.getText("operation.not.allowed.code"),
	                    msgBundle.getText("operation.not.allowed",
	                            "Handler" ,"Delete"));
	            log.info(
	                    "Auth Handler not removed as imported users exist");
	        }
		return ConsoleResponseEntity.get(response, HttpStatus.OK);
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = "/{id}")
	public ConsoleResponseEntity<ResponseDTO> getHandlerById(
			@PathVariable("id") Long id) throws ConsoleException {

		log.debug("Request came find handler by id, [id: {}]", id);
		validations.assertNotZero(id, "id");

		AuthHandlerTypeDetail authHandler = authHandlerService.findById(id);
		if (authHandler == null) {
			throw new NoDataFoundException(
					msgBundle.getText("no.data.found.code"), 
					msgBundle.getText("no.data.found"));
		}

		AuthHandlerDetail authHandlerDto = AuthHandlerDetail.getDTO(authHandler);

		ResponseDTO response = SimpleResponseDTO.create(
				msgBundle.getText("success.data.found.code"),
				msgBundle.getText("success.data.found"), authHandlerDto);

		log.info("Requested handler details found and response sent");
		return ConsoleResponseEntity.get(response, HttpStatus.OK);
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = "/list/{type}")
	public ConsoleResponseEntity<CollectionDataResponseDTO> findHandlerByType( 
			@PathVariable("type") String handlerType,
			@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
	        @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) 
			throws ConsoleException {

		log.debug("Request came to find handlers by type, [ Type:{} ]", 
				handlerType);

        List<AuthHandlerTypeDetail> authHandlersList = authHandlerService.
        		findByType(handlerType);
        
        if (authHandlersList.isEmpty()) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        List<AuthHandlerDetail> handlerDTOs = new ArrayList<>(
        		authHandlersList.size());
        for (AuthHandlerTypeDetail authHandlers : authHandlersList) {
        	AuthHandlerDetail handlerDTO = AuthHandlerDetail.getDTO(authHandlers);
        	handlerDTOs.add(handlerDTO);
        }

        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"));
        response.setData(handlerDTOs);
        response.setPageSize(pageSize);
        response.setPageNo(pageNo);
        response.setTotalPages(handlerDTOs.size()/pageSize);
        response.setTotalNoOfRecords(handlerDTOs.size());

        log.info(
                "Requested handler details found and response sent, [No of records :{}]",
                handlerDTOs.size());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = "/list")
	public ConsoleResponseEntity<CollectionDataResponseDTO> handlersSearch()
			throws ConsoleException {

		log.debug("Request came to handlers search");
		List<AuthHandlerTypeDetail> authHandlersList = authHandlerService.
				findAllAuthHandlers();

		if (authHandlersList.isEmpty()) {
			throw new NoDataFoundException(
					msgBundle.getText("no.data.found.code"), 
					msgBundle.getText("no.data.found"));
		}

		List<AuthHandlerDetail> handlerDTOs = new ArrayList<>(authHandlersList.size());
		for (AuthHandlerTypeDetail authHandlers : authHandlersList) {
			AuthHandlerDetail handlerDTO = AuthHandlerDetail.getDTO(authHandlers);
			handlerDTOs.add(handlerDTO);
		}

		CollectionDataResponseDTO response = CollectionDataResponseDTO
				.create(msgBundle.getText("success.data.found.code"), 
						msgBundle.getText("success.data.found"));
		response.setData(handlerDTOs);
		response.setPageSize(handlerDTOs.size());
		response.setPageNo(1);
		response.setTotalPages(1);
		response.setTotalNoOfRecords(handlerDTOs.size());

		log.info("Requested handler details found and response sent, [No of records :{}]",
				handlerDTOs.size());
		 return ConsoleResponseEntity.get(response, HttpStatus.OK);
	}
	
	@SuppressWarnings("rawtypes")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value = "/listExtUsers")
	public ConsoleResponseEntity<SimpleResponseDTO> getExternalUsers(
			@RequestBody AdSearchRequest searchRequest)
			throws ConsoleException {

		log.debug("Request came to list all external users , [ HandlerId:{} ]", 
				searchRequest.getHandlerId());
		
		AdSearchResponse adResponse;

		if(authHandlerService.isType(searchRequest.getHandlerId(), AuthHandlerType.OIDC.name())) {
			adResponse = azureUserService.getAllUsers(searchRequest.getHandlerId(), searchRequest.getSearchText(), searchRequest.getPageSize());
		} else {
			adResponse = ldapUserService.getAllUsers(searchRequest.getHandlerId(), searchRequest.getSearchText(), searchRequest.getPageSize());
		}
	
		if (adResponse.getAdUsers().isEmpty())
			throw new NoDataFoundException(
					msgBundle.getText("no.data.found.code"),
					msgBundle.getText("no.data.found"));
		
		SimpleResponseDTO response = SimpleResponseDTO.create(
	                msgBundle.getText("success.data.found.code"),
	                msgBundle.getText("success.data.found"), adResponse);

		log.info("External users found and response sent, [No of records :{}, Has more data : {}]", 
				adResponse.getAdUsers().size(), adResponse.isHasMoreResults());
		return ConsoleResponseEntity.get(response, HttpStatus.OK);
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value = "/listExtGroups")
	public ConsoleResponseEntity<SimpleResponseDTO> getExternalGroups(
					@RequestBody AdSearchRequest searchRequest)
					throws ConsoleException {

		log.debug("Request came to list all external groups , [ HandlerId:{} ]",
						searchRequest.getHandlerId());

		AdSearchResponse adResponse;

		if(authHandlerService.isType(searchRequest.getHandlerId(), AuthHandlerType.OIDC.name())) {
			adResponse = azureUserService.getAllGroups(searchRequest.getHandlerId(), searchRequest.getSearchText(), searchRequest.getPageSize());
		} else {
			adResponse = ldapUserService.getAllGroups(searchRequest.getHandlerId(), searchRequest.getSearchText(), searchRequest.getPageSize());
		}

		if (adResponse.getAdGroups().isEmpty())
			throw new NoDataFoundException(
							msgBundle.getText("no.data.found.code"),
							msgBundle.getText("no.data.found"));

		SimpleResponseDTO response = SimpleResponseDTO.create(
						msgBundle.getText("success.data.found.code"),
						msgBundle.getText("success.data.found"), adResponse);

		log.info("External groups found and response sent, [No of records :{}, Has more data : {}]",
						adResponse.getAdGroups().size(), adResponse.isHasMoreResults());
		return ConsoleResponseEntity.get(response, HttpStatus.OK);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = "/listExtUserProperties/{authHandlerId}")
	public ConsoleResponseEntity<CollectionDataResponseDTO> getExternalUserPropertiesByHandlerId(
			@PathVariable("authHandlerId") Long authHandlerId)  
	        throws ConsoleException {

		log.debug("Request came to fetch user attributes by handlerID , [ HandlerId:{} ]",
				authHandlerId);

		Set<String> extUserProps = ldapUserService.getAllUserAttributes(authHandlerId);
		
		if (extUserProps.isEmpty()) {
			throw new NoDataFoundException(
					msgBundle.getText("no.data.found.code"), 
					msgBundle.getText("no.data.found"));
		}

		CollectionDataResponseDTO response = CollectionDataResponseDTO
				.create(msgBundle.getText("success.data.found.code"), 
						msgBundle.getText("success.data.found"));
		response.setData(extUserProps);
		response.setPageSize(extUserProps.size());
		response.setPageNo(1);
		response.setTotalPages(1);
		response.setTotalNoOfRecords(extUserProps.size());

		log.info("External Users found and response sent, [No of records :{}]",
				extUserProps.size());
		return ConsoleResponseEntity.get(response, HttpStatus.OK);
	}	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value = "/listExtUserProperties")
	public ConsoleResponseEntity<CollectionDataResponseDTO> getExternalUserProperties(
			@RequestBody AuthHandlerDetail authHandlerDto) 
					throws ConsoleException {

		log.debug("Request came to fetch user attributes by handler, [ Handler:{} ]",
				authHandlerDto.getName());
		try {
			Set<String> extUserProps = ldapUserService.getAllUserAttributes(authHandlerDto);
	
			if (extUserProps.isEmpty()) {
				throw new NoDataFoundException(
						msgBundle.getText("no.data.found.code"), 
						msgBundle.getText("no.data.found"));
			}
	
			CollectionDataResponseDTO response = CollectionDataResponseDTO
					.create(msgBundle.getText("success.data.found.code"), 
							msgBundle.getText("success.data.found"));
			
			response.setData(extUserProps);
			response.setPageSize(extUserProps.size());
			response.setPageNo(1);
			response.setTotalPages(1);
			response.setTotalNoOfRecords(extUserProps.size());
	
			log.info("External Users found and response sent, [No of records :{}]", 
					extUserProps.size());
			return ConsoleResponseEntity.get(response, HttpStatus.OK);
		} catch (ConnectionFailedException ex) {
			CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
					msgBundle.getText("auth.handler.verify.failed.code"),
					msgBundle.getText("auth.handler.verify.failed",
							authHandlerDto.getType()));
			return ConsoleResponseEntity.get(response, HttpStatus.OK);
		}
	}

	@GetMapping("downloadFileResource/{resourceKey}")
	public ConsoleResponseEntity<ResponseDTO> downloadFileResource(@PathVariable("resourceKey") String resourceKey,
					HttpServletResponse response) {
		validations.assertNotBlank(resourceKey, "resourceKey");

		ResponseDTO responseDTO;
		try {
			String fileName = fileResourceService.exportResource("LoginConfig","cas", "saml2", resourceKey);

			responseDTO = SimpleResponseDTO.create(
							msgBundle.getText("success.file.export.code"),
							msgBundle.getText("success.file.export"), fileName);

			response.setHeader("Content-Disposition", "attachment");
		} catch (ConsoleException e) {
			responseDTO = ResponseDTO.create(e.getStatusCode(), e.getStatusMsg());
		}

		return ConsoleResponseEntity.get(responseDTO, HttpStatus.OK);
	}

	@Override
	public Logger getLog() {
		return log;
	}
	
	/**
	 * Check if authentication source configuration section has been modified
	 * 
	 * @param originalRecord Data from the database, which to be modified
	 * @param modifiedRecord Data from user, which contains modified field(s)
	 * @return true if configuration section has been modified
	 * 
 	 * @since 9.0
 	 */
	private boolean isConfigurationModified(AuthHandlerDetail originalRecord, AuthHandlerDetail modifiedRecord) {
		if(!originalRecord.getType().equals(modifiedRecord.getType()))
			return true;
		
		if(!originalRecord.getName().equals(modifiedRecord.getName()))
			return true;
		
		if(!originalRecord.getAccountId().equals(modifiedRecord.getAccountId()))
			return true;
		
		if(!originalRecord.getConfigData().keySet().equals(modifiedRecord.getConfigData().keySet()))
			return true;
		
		for(String key : originalRecord.getConfigData().keySet()) {
			if(!originalRecord.getConfigData().get(key).equals(modifiedRecord.getConfigData().get(key))) {
				return true;
			}
		}
		
		return false;
	}

	private void notifyCAS() {
		Set<String> applications = new HashSet<>();
		applications.add(com.nextlabs.destiny.console.enums.Service.CAS.toString().toLowerCase());
		sysConfigService.sendConfigRefreshRequest(applications);
	}

	private void checkMandatoryAttributes(AuthHandlerDetail handlerDetail) {
		boolean usernameProvided = false;
		boolean firstNameProvided = false;

		if(!handlerDetail.getComplexUserAttributes().isEmpty()) {
			for(ComplexUserAttribute userAttribute : handlerDetail.getComplexUserAttributes()) {
				if(userAttribute.getMappedAs().equals(msgBundle.getText("attr.username.key"))) {
					usernameProvided = true;
				} else if(userAttribute.getMappedAs().equals(msgBundle.getText("attr.firstName.label"))) {
					firstNameProvided = true;
				}
			}
		} else {
			validations.assertNotNull(handlerDetail.getUserAttributes(),"userAttributes");

			Map<String, String> userAttributes = handlerDetail.getUserAttributes();
			usernameProvided = userAttributes.containsKey(msgBundle.getText("attr.username.key"));
			firstNameProvided = userAttributes.containsKey(msgBundle.getText("attr.firstName.label"));
		}

		if(!usernameProvided) {
			throw new InvalidInputParamException(
							msgBundle.getText("invalid.input.field.required.code"),
							msgBundle.getText("invalid.input.field.required","username"));
		}

		if(!firstNameProvided) {
			throw new InvalidInputParamException(
							msgBundle.getText("invalid.input.field.required.code"),
							msgBundle.getText("invalid.input.field.required","firstName"));
		}
	}
}
