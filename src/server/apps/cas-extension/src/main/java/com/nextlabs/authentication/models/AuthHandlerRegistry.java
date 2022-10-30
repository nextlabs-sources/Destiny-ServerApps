package com.nextlabs.authentication.models;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * Entity for authentication handler registry.
 *
 * @author Sachindra Dasun
 */
@Entity
@Table(name = "AUTH_HANDLER_REGISTRY")
public class AuthHandlerRegistry implements Serializable {

    private static final long serialVersionUID = 6958702414880115169L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", unique = true, nullable = false)
    private long id;

    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "CREATED_BY")
    private long createdBy;

    @Column(name = "LAST_UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    @Column(name = "LAST_UPDATED_BY")
    private long lastUpdatedBy;

    @Column(name = "VERSION", nullable = false)
    private long version;

    @Column(name = "ACCOUNT_ID")
    private String accountId;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "CONFIG_DATA_JSON")
    private String configDataJson;

    @Column(name = "NAME")
    private String name;

    @Column(name = "TYPE")
    private String type;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "USER_ATTRS_JSON")
    private String userAttrsJson;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getConfigDataJson() {
        return configDataJson;
    }

    public void setConfigDataJson(String configDataJson) {
        this.configDataJson = configDataJson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserAttrsJson() {
        return userAttrsJson;
    }

    public void setUserAttrsJson(String userAttrsJson) {
        this.userAttrsJson = userAttrsJson;
    }

}
