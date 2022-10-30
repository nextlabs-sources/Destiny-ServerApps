/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 28, 2016
 *
 */
package com.nextlabs.destiny.console.dto.common;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.nextlabs.destiny.console.dto.Auditable;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.AppUserProperties;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.utils.JsonUtil;

/**
 *
 *
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public class ApplicationUserDTO 
		extends BaseDTO 
		implements Auditable {

    private static final long serialVersionUID = -6421829093095055468L;

    private String userType;
    private String userCategory;
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String oldPassword;
    private String displayName;
    private Long domainId;
    private Long authHandlerId;
    private String authHandlerName;
    private String authHandlerProtocol;
    private String email;
    private boolean locked;
    private boolean manualProvision;
    private Long lastUpdatedDate;
    
    private Set<AppUserProperties> properties;

    public ApplicationUserDTO() {
        super();
    }

    public static ApplicationUserDTO getDTO(ApplicationUser appUser) {

        ApplicationUserDTO appUserDTO = new ApplicationUserDTO();
        appUserDTO.setId(appUser.getId());
        appUserDTO.setUserType(appUser.getUserType());
        appUserDTO.setUserCategory(appUser.getUserCategory());
        appUserDTO.setUsername(appUser.getUsername());
        appUserDTO.setFirstName(appUser.getFirstName());
        appUserDTO.setLastName(appUser.getLastName());
        appUserDTO.setDisplayName(appUser.getDisplayName());
        appUserDTO.setEmail(appUser.getEmail());
        appUserDTO.setProperties(appUser.getProperties());
        appUserDTO.setDomainId(appUser.getDomainId());
        appUserDTO.setAuthHandlerId(appUser.getAuthHandlerId());
        appUserDTO.setLocked(appUser.isLocked());
        appUserDTO.setManualProvision(appUser.isManualProvision());
        appUserDTO.setLastUpdatedDate(appUser.getLastUpdatedDate() == null ? 0L : appUser.getLastUpdatedDate().getTime());
        // group/role
        //last logged in
        return appUserDTO;
    }

    @Override
    public String toAuditString()
    		throws ConsoleException {
    	try {
	    	Map<String, Object> audit = new LinkedHashMap<>();
	    	
	    	audit.put("User Type", this.userType);
	    	audit.put("User Category", this.userCategory);
	    	audit.put("First Name", this.firstName);
	    	audit.put("Last Name", this.lastName);
	    	audit.put("Username", this.username);
	    	audit.put("Display Name", this.displayName);
	    	audit.put("Email", this.email);
	    	audit.put("Domain ID", this.domainId);
    		audit.put("Authentication Handler ID", this.authHandlerId);
	    	
	    	return JsonUtil.toJsonString(audit);
    	} catch(Exception e) {
    		throw new ConsoleException(e);
    	}
    }
    
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserCategory() {
		return userCategory;
	}

	public void setUserCategory(String userCategory) {
		this.userCategory = userCategory;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
   public Set<AppUserProperties> getProperties() {
        if (properties == null) {
            properties = new TreeSet<>();
        }
        return properties;
    }

    public void setProperties(Set<AppUserProperties> properties) {
        this.properties = properties;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

	public Long getAuthHandlerId() {
		return authHandlerId;
	}

	public void setAuthHandlerId(Long authHandlerId) {
		this.authHandlerId = authHandlerId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public Long getLastUpdatedDate() {
		return lastUpdatedDate;
	}
	
	public void setLastUpdatedDate(Long lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isManualProvision() {
        return manualProvision;
    }

    public void setManualProvision(boolean manualProvision) {
        this.manualProvision = manualProvision;
    }

    public String getAuthHandlerName() {
		return authHandlerName;
	}

	public void setAuthHandlerName(String authHandlerName) {
		this.authHandlerName = authHandlerName;
	}

	public String getAuthHandlerProtocol() {
        return this.authHandlerProtocol;
    }

    public void setAuthHandlerProtocol(String authHandlerProtocol) {
        this.authHandlerProtocol = authHandlerProtocol;
    }
}
