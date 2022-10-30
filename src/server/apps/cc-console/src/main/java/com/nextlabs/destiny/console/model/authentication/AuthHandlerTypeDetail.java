/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 1 Aug 2016
 *
 */
package com.nextlabs.destiny.console.model.authentication;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.nextlabs.destiny.console.enums.AuthHandlerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluejungle.framework.crypt.ReversibleEncryptor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextlabs.destiny.console.dto.authentication.AuthHandlerDetail;
import com.nextlabs.destiny.console.model.BaseModel;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * Authentication Handlers entity to manage various authentication modes
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@Entity
@Table(name = "AUTH_HANDLER_REGISTRY")
@NamedQuery(name = AuthHandlerTypeDetail.FIND_BY_TYPE, query = "SELECT t FROM AuthHandlerTypeDetail t WHERE t.type = :type ORDER BY t.type")
public class AuthHandlerTypeDetail extends BaseModel {
	
	private static final Logger log = LoggerFactory.getLogger(AuthHandlerTypeDetail.class);
	
	private static final long serialVersionUID = -5914450735285480456L;
	
	private static final ReversibleEncryptor encryptor = new ReversibleEncryptor();
	
	public static final String FIND_BY_TYPE = "authHandler.findByType";

	@org.springframework.data.annotation.Id
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "account_id")
	private String accountId;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "name")
	private String name;
	
	@Lob
	@Column(name = "config_data_json")
	private String configDataJson;
	
	@Lob
	@Column(name = "user_attrs_json")
	private String userAttrsJson;
	
	@Transient
	private Boolean inUse;

	@Transient
	private Map<String, MultipartFile> resources;

	/**
	 * Default Constructor
	 */
	public AuthHandlerTypeDetail(){
		
	}
	
	/**
	 * Constructor with basic arguments
	 * 
	 * @param id
	 * @param type
	 */
	public AuthHandlerTypeDetail(Long id, String type) {
		super();
		this.id = id;
		this.type = type;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getConfigDataJson() {
		return configDataJson;
	}

	public void setConfigDataJson(String configDataJson) {
		this.configDataJson = configDataJson;
	}

	public String getUserAttrsJson() {
		return userAttrsJson;
	}

	public void setUserAttrsJson(String userAttrsJson) {
		this.userAttrsJson = userAttrsJson;
	}

	public Boolean isInUse() {
		return inUse;
	}

	public void setInUse(Boolean inUse) {
		this.inUse = inUse;
	}

	public Map<String, MultipartFile> getResources() {
		if(resources == null) {
			resources = new HashMap<>();
		}

		return resources;
	}

	public void setResources(Map<String, MultipartFile> resources) {
		this.resources = resources;
	}

	public static AuthHandlerTypeDetail getHandlerFromDetail(AuthHandlerDetail handlerDetail) {
		ObjectMapper mapper = new ObjectMapper();

		AuthHandlerTypeDetail authHandler = new AuthHandlerTypeDetail(handlerDetail.getId(),
				handlerDetail.getType());
		authHandler.setName(handlerDetail.getName());
		authHandler.setLastUpdatedDate(handlerDetail.getLastUpdatedDate() == null ? null : new Date(handlerDetail.getLastUpdatedDate()));
		
		try {
			Map<String, String> configData = handlerDetail.getConfigData();
			if(configData.containsKey("password")) {
				configData.put("password", encryptor.encrypt(configData.get("password"))); // Encryption before saving to database
			}
			if(configData.containsKey("applicationKey")) {
				configData.put("applicationKey", encryptor.encrypt(configData.get("applicationKey"))); // Encryption before saving to database
			}
			authHandler.setConfigDataJson(mapper.writeValueAsString(configData));
			authHandler.setUserAttrsJson(mapper.writeValueAsString(AuthHandlerType.SAML2.toString().equals(handlerDetail.getType()) ?
							handlerDetail.getComplexUserAttributes() : handlerDetail.getUserAttributes()));
		} catch (JsonProcessingException e) {
			log.error("Error occurred during json data parsing {}", e.getMessage());
		}

		return authHandler;
	}
}
