/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 8, 2016
 *
 */
package com.nextlabs.destiny.console.model;

import static com.nextlabs.destiny.console.utils.SecurityContextUtil.getCurrentUser;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.*;

import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.utils.JsonUtil;
import org.joda.time.LocalDateTime;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

/**
 *
 * Entity for Super Application User
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@Entity
@Table(name = "SUPER_APPLICATION_USER")
public class SuperApplicationUser implements Serializable {

    private static final long serialVersionUID = 3420932226723305164L;

    @Id
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password")
    private byte[] password;

    @Column(name = "displayName")
    private String displayName;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
            CascadeType.MERGE, CascadeType.REMOVE })
    @JoinColumn(name = "super_user_id")
    @Field(type = FieldType.Nested, store = true)
    private Set<AppUserProperties> properties;

    @Column(name = "last_logged_time")
    private Long loggedInTime;

    @Column(name = "hide_splash")
    private Boolean hideSplash = false;
    
    /* audit fields*/
    @Column(name = "last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    @MultiField(mainField = @Field(type = FieldType.Date, store = true), otherFields = {
            @InnerField(suffix = "untouched", type = FieldType.Date, store = true)})
    private Date lastUpdatedDate;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @MultiField(mainField = @Field(type = FieldType.Date, store = true), otherFields = {
            @InnerField(suffix = "untouched", type = FieldType.Date, store = true)})
    private Date createdDate;

    @Column(name = "created_by")
    @Field(type = FieldType.Long)
    private Long ownerId;

    @Column(name = "last_updated_by")
    @Field(type = FieldType.Long)
    private Long lastUpdatedBy;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "initlogin_done")
	private String initLoginDone = "N";;
    
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

    @PostLoad
    public void postLoad() {
        this.displayName = this.firstName
                + ((this.lastName != null) ? " " + this.lastName : "");
    }
    
    /**
     * Before entity persist
     * 
     */
    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now().toDate();
        this.lastUpdatedDate = LocalDateTime.now().toDate();
        this.ownerId = getCurrentUser().getUserId();
        this.lastUpdatedBy = getCurrentUser().getUserId();
    }

    /**
     * Before entity update
     * 
     */
    @PreUpdate
    public void preUpdate() {
        this.lastUpdatedDate = LocalDateTime.now().toDate();
        this.lastUpdatedBy = getCurrentUser().getUserId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<AppUserProperties> getProperties() {
        return properties;
    }

    public void setProperties(Set<AppUserProperties> properties) {
        this.properties = properties;
    }

    public Long getLoggedInTime() {
        return loggedInTime;
    }

    public void setLoggedInTime(Long loggedInTime) {
        this.loggedInTime = loggedInTime;
    }

    public Boolean isHideSplash() {
        return hideSplash;
    }

    public void setHideSplash(Boolean hideSplash) {
        this.hideSplash = hideSplash;
    }
    
    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getInitloginDone() {
		return initLoginDone;
	}

	public void setInitloginDone(String initLoginDone) {
		this.initLoginDone = initLoginDone;
	}
	
	public Integer getFailedLoginAttempts() {
		if(failedLoginAttempts == null) {
			failedLoginAttempts = 0;
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

	public List<PasswordHistory> getPasswordHistory() {
		return passwordHistory;
	}

	public void setPasswordHistory(List<PasswordHistory> passwordHistory) {
		this.passwordHistory = passwordHistory;
	}

    public Boolean getManualProvision() {
        return manualProvision;
    }

    public void setManualProvision(Boolean manualProvision) {
        this.manualProvision = manualProvision;
    }

    public String toAuditString()
            throws ConsoleException {
        try {
            Map<String, Object> audit = new LinkedHashMap<>();

            audit.put("User Type", "internal");
            audit.put("User Category", "ADMIN");
            audit.put("First Name", this.firstName);
            audit.put("Last Name", this.lastName);
            audit.put("Username", this.username);
            audit.put("Display Name", this.displayName);
            audit.put("Email", this.email);

            if(this.properties != null
                    && !this.properties.isEmpty()) {
                Map<String, String> userAttributes = new LinkedHashMap<>();

                for(AppUserProperties property : this.properties) {
                    userAttributes.put(property.getKey(), property.getValue());
                }

                audit.put("User Attributes", userAttributes);
            } else {
                audit.put("User Attributes", null);
            }

            audit.put("Domain ID", this.id);
            audit.put("Authentication Handler ID", 0);

            return JsonUtil.toJsonString(audit);
        } catch(Exception e) {
            throw new ConsoleException(e);
        }
    }
}
