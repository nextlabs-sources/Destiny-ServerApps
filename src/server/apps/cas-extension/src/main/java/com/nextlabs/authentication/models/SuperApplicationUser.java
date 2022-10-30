package com.nextlabs.authentication.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.StringJoiner;

/**
 * Entity for super application user.
 *
 * @author Sachindra Dasun
 */
@Entity
@Table(name = "SUPER_APPLICATION_USER")
public class SuperApplicationUser implements Serializable {

    private static final long serialVersionUID = 7411602190949633387L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "USERNAME", unique = true, length = 64, nullable = false)
    private String username;

    @Column(name = "FIRST_NAME", length = 64, nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", length = 64, nullable = false)
    private String lastName;

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

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "INITLOGIN_DONE", length = 1, columnDefinition = "CHAR")
    private String initLoginDone = "N";

    @Column(name = "FAILED_LOGIN_ATTEMPTS")
    private Integer failedLoginAttempts = 0;

    @Column(name = "LOCKED")
    private Boolean locked = false;

    @Column(name = "MANUAL_PROVISION")
    private Boolean manualProvision = true;

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

    public Boolean getManualProvision() {
        return manualProvision;
    }

    public void setManualProvision(Boolean manualProvision) {
        this.manualProvision = manualProvision;
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

    @Override
    public String toString() {
        return new StringJoiner(", ", SuperApplicationUser.class.getSimpleName() + "[", "]")
                .add("username='" + username + "'")
                .add("email='" + email + "'")
                .toString();
    }

}
