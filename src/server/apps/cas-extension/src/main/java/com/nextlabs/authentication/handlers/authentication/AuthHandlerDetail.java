package com.nextlabs.authentication.handlers.authentication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.bluejungle.framework.crypt.ReversibleEncryptor;
import com.nextlabs.authentication.enums.AuthType;
import com.nextlabs.authentication.models.AuthHandlerRegistry;

/**
 * Utility class to parse JSON config data in authentication handler.
 *
 * @author Sachindra Dasun
 */
public class AuthHandlerDetail implements Serializable {
	
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String LDAP_URL = "ldapUrl";
	public static final String LDAP_DOMAIN = "ldapDomain";
	public static final String BASE_DN = "baseDn";
	public static final String USE_STARTTLS = "useStartTLS";
	public static final String CONNECTION_TIMEOUT = "connectionTimeout";
	public static final String APPLICATION_KEY = "applicationKey";

	private static final ReversibleEncryptor encryptor = new ReversibleEncryptor();

	private String type;
	private Map<String, String> configData;
	private Map<String,String> userAttributes;
	private List<ComplexUserAttribute> complexUserAttributes;

	public AuthHandlerDetail(AuthHandlerRegistry authHandler) throws JsonProcessingException {
		type = authHandler.getType();
		ObjectMapper mapper = new ObjectMapper();
		String configDataJson = authHandler.getConfigDataJson();
		if (configDataJson != null) {
			configData = mapper.readValue(configDataJson, new TypeReference<>() {
			});
			if (configData.containsKey(PASSWORD)) {
				configData.put(PASSWORD, encryptor.decrypt(configData.get(PASSWORD)));
			}
			if (configData.containsKey(APPLICATION_KEY)) {
				configData.put(APPLICATION_KEY, encryptor.decrypt(configData.get(APPLICATION_KEY)));
			}
		}
		String userAttrsJson = authHandler.getUserAttrsJson();
		if (userAttrsJson != null) {
			if (AuthType.SAML2.toString().equals(type)) {
				ComplexUserAttribute[] userAttributes = mapper.readValue(userAttrsJson, ComplexUserAttribute[].class);
				complexUserAttributes = Arrays.asList(userAttributes);
			} else {
				userAttributes = mapper.readValue(userAttrsJson, new TypeReference<>() {
				});
			}
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
}
