package com.nextlabs.authentication.models;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import com.nextlabs.authentication.enums.UserCategory;
import com.nextlabs.authentication.enums.UserStatus;

/**
 * Entity for application user.
 *
 * @author Sachindra Dasun
 */
@Entity
@Table(name = "APPLICATION_USER")
public class ApplicationUser implements Serializable {

    private static final long serialVersionUID = -4152274969845036639L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Version
    private Integer version;

    @Column(name = "USER_TYPE", nullable = false)
    private String userType;

    @Column(name = "USER_CATEGORY")
    @Enumerated(EnumType.STRING)
    private UserCategory userCategory = UserCategory.CONSOLE;

    @Column(name = "USERNAME", unique = true, length = 255, nullable = false)
    private String username;

    @Column(name = "FIRST_NAME", length = 255, nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", length = 255, nullable = false)
    private String lastName;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "DOMAIN_ID", nullable = false)
    private Long domainId;

    @Column(name = "PRIMARY_GROUP_ID")
    private Long primaryGroupId;

    @Column(name = "PASSWORD", nullable = false)
    private byte[] password;

    @Column(name = "DISPLAYNAME")
    private String displayName;

    @Column(name = "HIDE_SPLASH")
    private Boolean hideSplash;

    @Column(name = "LAST_LOGGED_TIME")
    private Long lastLoggedTime;

    @Column(name = "AUTH_HANDLER_ID")
    private Long authHandlerId;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "INITLOGIN_DONE", length = 1, columnDefinition = "CHAR")
    private String initLoginDone = "N";

    @Column(name = "FAILED_LOGIN_ATTEMPTS")
    private Integer failedLoginAttempts = 0;

    @Column(name = "LOCKED")
    private Boolean locked = false;

    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "LAST_UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    @Column(name = "LAST_UPDATED_BY")
    private Long lastUpdatedBy;

    @Column(name = "MANUAL_PROVISION")
    private Boolean manualProvision = true;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "USER_ID")
    private Set<AppUserProperties> properties;

    @Transient
    private Map<String, Set<String>> multiValueProperties = new HashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public UserCategory getUserCategory() {
        return userCategory;
    }

    public void setUserCategory(UserCategory userCategory) {
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

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public Long getPrimaryGroupId() {
        return primaryGroupId;
    }

    public void setPrimaryGroupId(Long primaryGroupId) {
        this.primaryGroupId = primaryGroupId;
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

    public Boolean getHideSplash() {
        return hideSplash;
    }

    public void setHideSplash(Boolean hideSplash) {
        this.hideSplash = hideSplash;
    }

    public Long getLastLoggedTime() {
        return lastLoggedTime;
    }

    public void setLastLoggedTime(Long lastLoggedTime) {
        this.lastLoggedTime = lastLoggedTime;
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

    public String getInitLoginDone() {
        return initLoginDone;
    }

    public void setInitLoginDone(String initLoginDone) {
        this.initLoginDone = initLoginDone;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Boolean getManualProvision() {
        return manualProvision;
    }

    public void setManualProvision(Boolean manualProvision) {
        this.manualProvision = manualProvision;
    }

    public Set<AppUserProperties> getProperties() {
        if (properties == null) {
            properties = new HashSet<>();
        }
        return properties;
    }

    public void setProperties(Set<AppUserProperties> properties) {
        this.properties = properties;
    }

    public Map<String, Set<String>> getMultiValueProperties() {
        if (multiValueProperties == null) {
            multiValueProperties = new HashMap<>();
        }
        return multiValueProperties;
    }

    public void setMultiValueProperties(Map<String, Set<String>> multiValueProperties) {
        this.multiValueProperties = multiValueProperties;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ApplicationUser.class.getSimpleName() + "[", "]")
                .add("username='" + username + "'")
                .add("email='" + email + "'")
                .toString();
    }

}
