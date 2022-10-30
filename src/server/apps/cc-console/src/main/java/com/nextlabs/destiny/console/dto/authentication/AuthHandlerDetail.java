/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 1 Aug 2016
 *
 */
package com.nextlabs.destiny.console.dto.authentication;

import com.bluejungle.framework.crypt.ReversibleEncryptor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextlabs.destiny.console.enums.AuthHandlerType;
import com.nextlabs.destiny.console.model.authentication.AuthHandlerTypeDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthHandlerDetail implements Serializable {
	
	private static final Logger log = LoggerFactory.getLogger(AuthHandlerDetail.class);

	private static final long serialVersionUID = 8737817396287786343L;

	// Keys in configData
	public static final String ENABLED = "enabled";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String LDAP_URL = "ldapUrl";
	public static final String LDAP_DOMAIN = "ldapDomain";
	public static final String BASE_DN = "baseDn";
	public static final String USER_SEARCH_BASE = "userSearchBase";
	public static final String USE_STARTTLS = "useStartTLS";
	public static final String CONNECTION_TIMEOUT = "connectionTimeout";
	public static final String TENANT_ID = "tenantId";
	public static final String AUTHORITY_URI = "authorityUri";
	public static final String ATTRIBUTE_URI = "attributeUri";
	public static final String LOGOUT_SERVICE = "logoutService";
	public static final String APPLICATION_ID = "applicationId";
	public static final String APPLICATION_KEY = "applicationKey";
	public static final String SP_ENTITY_ID = "spEntityId";
	public static final String KEYSTORE_ALIAS = "keyStoreAlias";
	public static final String CERTIFICATE_NAME_TO_APPEND = "certificateNameToAppend";
	public static final String MAX_AUTHENTICATION_LIFETIME = "maximumAuthenticationLifetime";
	public static final String DESTINATION_BINDING = "destinationBinding";
	public static final String AUTHENTICATION_CONTEXT_CLASS_REFERENCES = "authenticationContextClassReferences";
	public static final String AUTHENTICATION_CONTEXT_COMPARISON_TYPE = "authenticationContextComparisonType";
	public static final String FORCE_AUTHENTICATION = "forceAuthentication";
	public static final String PASSIVE = "passive";
	public static final String PRINCIPAL_ID_ATTRIBUTE = "principalIdAttribute";
	public static final String WANTS_ASSERTIONS_SIGNED = "signAssertion";
	public static final String WANTS_RESPONSE_SIGNED = "signResponse";
	public static final String NAME_ID_POLICY_FORMAT = "nameIdPolicyFormat";
	public static final String ALL_SIGNATURE_VALIDATION_DISABLED = "disableAllSignatureValidation";
	public static final String SIGN_SERVICE_PROVIDER_METADATA = "signServiceProviderMetadata";
	public static final String USE_NAME_QUALIFIER = "useNameQualifier";
	public static final String SIGN_AUTHENTICATION_REQUEST = "signAuthenticationRequest";
	public static final String SIGN_SERVICE_PROVIDER_LOGOUT_REQUEST = "signServiceProviderLogoutRequest";
	public static final String SIGNATURE_ALGORITHMS = "signatureAlgorithms";
	public static final String SIGNATURE_REFERENCE_DIGEST_METHODS = "signatureReferenceDigestMethods";
	public static final String PROVIDER_NAME = "providerName";
	public static final String NAME_ID_POLICY_ALLOW_CREATE = "allowNameIdPolicyCreation";
	public static final String SIGNATURE_CANONICALIZATION_ALGORITHM = "signatureCanonicalizationAlgorithm";

	private static final ReversibleEncryptor encryptor = new ReversibleEncryptor();

	private Long id;
	private String type;
	private String name;
	private String accountId;
	private Map<String, String> configData;
	private Map<String,String> userAttributes;
	private List<ComplexUserAttribute> complexUserAttributes;
	private Long lastUpdatedDate;
	private Boolean inUse;
	private Map<String, MultipartFile> resources;

	/**
	 * Default constructor
	 */
	public AuthHandlerDetail() {

	}

	/**
	 * Overloaded constructor with type input parameter
	 * 
	 * @param type
	 */
	public AuthHandlerDetail(String type) {
		super();
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getConfigData() {
		return configData;
	}

	public void setConfigData(Map<String, String> configData) {
		this.configData = configData;
	}

	public Map<String, String> getUserAttributes() {
		return userAttributes;
	}

	public void setUserAttributes(Map<String, String> userAttributes) {
		this.userAttributes = userAttributes;
	}

	public List<ComplexUserAttribute> getComplexUserAttributes() {
		if(this.complexUserAttributes == null) {
			this.complexUserAttributes = new ArrayList<>();
		}

		return complexUserAttributes;
	}

	public void setComplexUserAttributes(List<ComplexUserAttribute> complexUserAttributes) {
		this.complexUserAttributes = complexUserAttributes;
	}

	public Map<String, MultipartFile> getResources() {
		if(this.resources == null)
			this.resources = new HashMap<>();

		return resources;
	}

	public void setResources(Map<String, MultipartFile> resources) {
		this.resources = resources;
	}

	public Long getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Long lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public Boolean isInUse() {
		return inUse;
	}

	public void setInUse(Boolean inUse) {
		this.inUse = inUse;
	}

	/**
	 * Convert database model object to UI model object
	 *
	 * @param authHandler Database model for authentication handler configuration
	 * @return UI model representing given database model
	 */
	@SuppressWarnings("unchecked")
	public static AuthHandlerDetail getDTO(AuthHandlerTypeDetail authHandler) {

		ObjectMapper mapper = new ObjectMapper();

		AuthHandlerDetail authHandlerDto = new AuthHandlerDetail();
		authHandlerDto.setId(authHandler.getId());
		authHandlerDto.setType(authHandler.getType());
		authHandlerDto.setAccountId(authHandler.getAccountId());
		authHandlerDto.setName(authHandler.getName());
		authHandlerDto.setLastUpdatedDate(authHandler.getLastUpdatedDate() == null ? 0L : authHandler.getLastUpdatedDate().getTime());
		authHandlerDto.setInUse(authHandler.isInUse());
		
		try {
			String configDataJson = authHandler.getConfigDataJson();
			if (configDataJson != null) {
				Map<String, String> configData;
				configData = mapper.readValue(configDataJson, HashMap.class);
				
				if(configData.containsKey(PASSWORD)) {
					configData.put(PASSWORD, encryptor.decrypt(configData.get(PASSWORD)));
				}
				
				if(configData.containsKey(APPLICATION_KEY)) {
					configData.put(APPLICATION_KEY, encryptor.decrypt(configData.get(APPLICATION_KEY)));
				}
				authHandlerDto.setConfigData(configData);
			}

			String userAttrsJson = authHandler.getUserAttrsJson();
			if (userAttrsJson != null) {
				if(AuthHandlerType.SAML2.toString().equals(authHandlerDto.getType())) {
					ComplexUserAttribute[] userAttributes = mapper.readValue(userAttrsJson, ComplexUserAttribute[].class);
					authHandlerDto.setComplexUserAttributes(Arrays.asList(userAttributes));
				} else {
					Map<String, String> userAttributes = mapper.readValue(userAttrsJson, HashMap.class);
					authHandlerDto.setUserAttributes(userAttributes);
				}
			}
		} catch (Exception ex) {
			log.error("Error occured during json data parsing", ex);
		}
		return authHandlerDto;
	}
}
