/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 16 Aug 2016
 *
 */
package com.nextlabs.destiny.console.dto.common;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public class ExternalUserDTO implements Comparable<ExternalUserDTO> {

	private String externalId;
	private String username;
	private String userType;
	private String firstName;
	private String lastName;
	private String displayName;
	private String email;
	private Long authHandlerId;

	private Map<String, String> attributes;

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Long getAuthHandlerId() {
		return authHandlerId;
	}

	public void setAuthHandlerId(Long authHandlerId) {
		this.authHandlerId = authHandlerId;
	}

	public Map<String, String> getAttributes() {
		if (attributes == null) {
			attributes = new HashMap<>();
		}
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	
	@Override
	public int compareTo(ExternalUserDTO o) {
		if (o == null)
			return -1;
		if (this.getUsername() == null || o.getUsername() == null) {
			return -1;
		}
		return this.getUsername().compareTo(o.getUsername());
	}
}
