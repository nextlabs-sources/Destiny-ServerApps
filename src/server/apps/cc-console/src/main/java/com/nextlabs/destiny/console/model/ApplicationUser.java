/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 22, 2016
 *
 */
package com.nextlabs.destiny.console.model;

import com.nextlabs.destiny.console.enums.DelegationModelShortName;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.enums.UserCategory;
import com.nextlabs.destiny.console.model.delegadmin.AccessibleTags;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * Entity for Application user
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Entity
@Table(name = "APPLICATION_USER")
@NamedQuery(name = ApplicationUser.FIND_BY_USERNAME, query = "SELECT a FROM ApplicationUser a WHERE a.username = :username")
@Document(indexName = "app_users")
@Setting(settingPath = "/search_config/index-settings.json")
public class ApplicationUser extends BaseModel implements Serializable {

    private static final long serialVersionUID = 3420932226723305164L;

    public static final String SUPER_USERNAME = "Administrator";
    public static final String FIND_BY_USERNAME = "applicationUser.findByUsername";

    public static final String USER_TYPE_INTERNAL = "internal";
    public static final String USER_TYPE_IMPORTED = "imported";
    public static final String JWT_PASSPHRASE = "jwt_passphrase";

    @org.springframework.data.annotation.Id
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Field(type = FieldType.Long, store = true)
    private Long id;

    @Column(name = "user_type")
    private String userType;
    
    @Column(name = "user_category")
    @MultiField(mainField = @Field(type = FieldType.Text), otherFields = {
            @InnerField(fielddata = true, suffix = "untouched", type = FieldType.Text, store = true, analyzer =
                    "case_insensitive_analyzer")})
    private String userCategory = "CONSOLE";

    @Column(name = "username")
    @MultiField(mainField = @Field(type = FieldType.Text), otherFields = {
            @InnerField(suffix = "untouched", type = FieldType.Text, store = true, analyzer =
                    "case_insensitive_analyzer")})
    private String username;

    @Column(name = "first_name")
    @MultiField(mainField = @Field(type = FieldType.Text), otherFields = {
            @InnerField(suffix = "untouched", type = FieldType.Text, store = true, analyzer = "case_insensitive_analyzer")})
    private String firstName;

    @Column(name = "last_name")
    @MultiField(mainField = @Field(type = FieldType.Text), otherFields = {
            @InnerField(suffix = "untouched", type = FieldType.Text, store = true, analyzer = "case_insensitive_analyzer")})
    private String lastName;

    @Column(name = "password")
    private byte[] password;

    @Column(name = "displayName")
    @MultiField(mainField = @Field(type = FieldType.Text), otherFields = {
            @InnerField(fielddata = true, suffix = "untouched", type = FieldType.Text, store = true, analyzer =
                    "case_insensitive_analyzer")})
    private String displayName;

    @Column(name = "domain_id")
    private Long domainId;

    @Column(name = "last_logged_time")
    @Field(type = FieldType.Date, store = true)
    private Long loggedInTime;

    @Column(name = "hide_splash")
    @Field(type = FieldType.Boolean, store = true)
    private Boolean hideSplash = false;

    @Transient
    @Field(type = FieldType.Keyword, store = true)
    private Set<String> allowedActions;

    @Transient
    @Field(type = FieldType.Object, store = true)
    private Map<String, AccessibleTags> accessibleTagsMap;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
            CascadeType.MERGE, CascadeType.REMOVE })
    @JoinColumn(name = "user_id")
    @Field(type = FieldType.Nested, store = true)
    @OrderBy("id ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<AppUserProperties> properties;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
    
    @Column(name = "auth_handler_id")
    @MultiField(mainField = @Field(type = FieldType.Text), otherFields = {
            @InnerField(suffix = "untouched", type = FieldType.Text, store = true, analyzer = "case_insensitive_analyzer")})
    private Long authHandlerId;
    
    @Column(name = "email")
    private String email;
    
    @Transient
    @Field(type = FieldType.Boolean, store = true)
    private Map<String, String> authHandler;
    
    @Column(name = "initlogin_done")
    private String initLoginDone = "N";
    
    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;
    
    @Column(name = "locked")
    private Boolean locked = false;

    @Column(name = "manual_provision")
    @Field(type = FieldType.Boolean)
    private Boolean manualProvision = true;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "USER_ID")
    @OrderBy("TIMESTAMP DESC")
    private List<PasswordHistory> passwordHistory = new ArrayList<>();

    @Transient
    private Map<String, Set<String>> multiValueProperties = new HashMap<>();

    @PreUpdate
    @PrePersist
    @PostLoad
    public void postLoad() {
        if(StringUtils.isBlank(this.displayName)) {
            this.displayName = this.firstName
                    + ((this.lastName != null) ? " " + this.lastName : "");
        }
        if(null == authHandlerId) 
        	authHandlerId = 0L;
    }

    /**
     * Default Constructor
     */
    public ApplicationUser() {

    }

    /**
     * Overloaded Constructor with parameters
     * 
     * @param id
     * @param userType
     * @param userCategory
     * @param username
     * @param firstName
     * @param lastName
     * 
     */
    public ApplicationUser(Long id, String userType, String userCategory, String username,
            String firstName, String lastName) {
        this.id = id;
        this.userType = userType;
        this.userCategory = userCategory;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Check has access to given action
     * 
     * @param action
     *            action
     * @return true if has access otherwise false
     */
    public boolean hasAccess(String action) {
        return getAllowedActions().contains(action.toUpperCase());
    }

    /**
     * Get policy accessible tags
     * 
     * @return {@link AccessibleTags}
     */
    public AccessibleTags getPolicyAccessibleTags() {
        return getAccessibleTagsMap()
                .get(DelegationModelShortName.POLICY_ACCESS_TAGS.name());
    }

    /**
     * Get policy model accessible tags
     * 
     * @return {@link AccessibleTags}
     */
    public AccessibleTags getPolicyModelAccessibleTags() {
        return getAccessibleTagsMap()
                .get(DelegationModelShortName.POLICY_MODEL_ACCESS_TAGS.name());
    }

    /**
     * Get component accessible tags
     * 
     * @return {@link AccessibleTags}
     */
    public AccessibleTags getComponentAccessibleTags() {
        return getAccessibleTagsMap()
                .get(DelegationModelShortName.COMPONENT_ACCESS_TAGS.name());
    }

    /**
     * Get policy folder accessible tags
     *
     * @return {@link AccessibleTags}
     */
    public AccessibleTags getPolicyFolderAccessibleTags() {
        return getAccessibleTagsMap()
                .get(DelegationModelShortName.POLICY_FOLDER_ACCESS_TAGS.name());
    }

    /**
     * Get component folder accessible tags
     *
     * @return {@link AccessibleTags}
     */
    public AccessibleTags getComponentFolderAccessibleTags() {
        return getAccessibleTagsMap()
                .get(DelegationModelShortName.COMPONENT_FOLDER_ACCESS_TAGS.name());
    }

    /**
     * Check user has access to any of the given actions
     *
     * @param actions
     * @return true if has access otherwise false
     */
    public boolean hasAnyAccess(String... actions) {
        for (String action : actions) {
            if (getAllowedActions().contains(action.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getLoggedInTime() {
        return loggedInTime;
    }

    public void setLoggedInTime(Long loggedInTime) {
        this.loggedInTime = loggedInTime;
    }

    public Set<String> getAllowedActions() {
        if (allowedActions == null) {
            allowedActions = new TreeSet<>();
        }
        return allowedActions;
    }

    public void setAllowedActions(Set<String> allowedActions) {
        this.allowedActions = allowedActions;
    }

    public Map<String, AccessibleTags> getAccessibleTagsMap() {
        if (accessibleTagsMap == null) {
            this.accessibleTagsMap = new HashMap<>();
        }
        return accessibleTagsMap;
    }

    public void setAccessibleTagsMap(
            Map<String, AccessibleTags> accessibleTagsMap) {
        this.accessibleTagsMap = accessibleTagsMap;
    }

	public Set<AppUserProperties> getProperties() {
		if(this.properties == null) {
			this.properties = new TreeSet<>();
		}
		
		return properties;
    }

	public void setProperties(Set<AppUserProperties> properties) {
        this.properties = properties;
    }

    public Long getDomainId() {
        return domainId;
    }

	public void setProperty(String key, String value) {
		boolean exist = false;
		Set<AppUserProperties> props = getProperties();
		
		for(AppUserProperties prop : props) {
			if(key.equals(prop.getKey())) {
				prop.setValue(value);
				exist = true;
			}
		}
		
		if(!exist) {
			AppUserProperties property = new AppUserProperties();
			property.setKey(key);
			property.setValue(value);
			props.add(property);
		}
	}
	
	public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getAuthHandlerId() {
		return authHandlerId;
	}

	public void setAuthHandlerId(Long authHandlerId) {
		this.authHandlerId = authHandlerId;
	}

	public Boolean isHideSplash() {
        return hideSplash;
    }

    public void setHideSplash(Boolean hideSplash) {
        this.hideSplash = hideSplash;
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the authHandler
	 */
	public Map<String, String> getAuthHandler() {
		return authHandler;
	}

	/**
	 * @param authHandler the authHandler to set
	 */
	public void setAuthHandler(Map<String, String> authHandler) {
		this.authHandler = authHandler;
	}

	public String getInitLoginDone() {
		return initLoginDone;
	}

	public void setInitLoginDone(String initLoginDone) {
		this.initLoginDone = initLoginDone;
	}

	public Integer getFailedLoginAttempts() {
		if(failedLoginAttempts == null) {
			failedLoginAttempts = Integer.valueOf(0);
		}
		
		return failedLoginAttempts;
	}

	public void setFailedLoginAttempts(Integer failedLoginAttempts) {
		this.failedLoginAttempts = failedLoginAttempts;
	}

	public Boolean isLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

    public Boolean isManualProvision() {
        return manualProvision;
    }

    public void setManualProvision(Boolean manualProvision) {
        this.manualProvision = manualProvision;
    }

    public List<PasswordHistory> getPasswordHistory() {
		return passwordHistory;
	}

	public void setPasswordHistory(List<PasswordHistory> passwordHistory) {
		this.passwordHistory = passwordHistory;
	}

    public Map<String, Set<String>> getMultiValueProperties() {
	    if(multiValueProperties == null) {
	        multiValueProperties = new HashMap<>();
        }

        return multiValueProperties;
    }

    public void setMultiValueProperties(Map<String, Set<String>> multiValueProperties) {
        this.multiValueProperties = multiValueProperties;
    }

    public String getPropertyValue(String propertyKey) {
		if(this.properties != null) {
			for(AppUserProperties property : properties) {
				if(property.getKey().equals(propertyKey)) {
					return property.getValue();
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Determine if this user account is super user
	 *
	 * @return true if username equals to Administrator or user's category is UserCategory.ADMINISTRATOR
	 * @see com.nextlabs.destiny.console.enums.UserCategory
	 * 
	 */
	public boolean isSuperUser() {
		return (this.username.equalsIgnoreCase(SUPER_USERNAME)
                || UserCategory.ADMINISTRATOR.getCode().equals(this.userCategory));
	}

    @Override
    public boolean equals(Object user) {
        if (user instanceof ApplicationUser) {
            ApplicationUser that = (ApplicationUser) user;
            return that.getUsername().equalsIgnoreCase(this.getUsername());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
                    .append(username)
                    .toHashCode();
    }
}
