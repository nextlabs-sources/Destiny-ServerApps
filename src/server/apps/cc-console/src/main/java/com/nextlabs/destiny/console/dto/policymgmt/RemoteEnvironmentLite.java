/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 26, 2015
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.remoteenv.RemoteEnvironment;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import javax.persistence.Column;
import java.io.Serializable;

/**
 *
 * Light weight remote env entity object for list views
 *
 * @author Mohammed Sainal Shah
 * @since 2020.08
 *
 */
@Document(indexName = "remote_env")
@Setting(settingPath = "/search_config/index-settings.json")
public class RemoteEnvironmentLite implements Serializable {

    private static final long serialVersionUID = -3109478968307531857L;

    @Id
    @Field(type = FieldType.Long, store = true)
    private Long id;

    @Column(name = "name", length = 264)
    @MultiField(mainField = @Field(type = FieldType.Text, store = true), otherFields = {
            @InnerField(fielddata = true, suffix = "untouched", type = FieldType.Text, store = true, analyzer =
                    "case_insensitive_analyzer")})
    private String name;

    @Field(type = FieldType.Keyword, store = true)
    private String lowercaseName;

    @Field(type = FieldType.Keyword, store = true)
    private String host;
    private String port;
    private String clientId;
    private String username;
    private boolean isActive;

    @MultiField(mainField = @Field(type = FieldType.Date, store = true), otherFields = {
            @InnerField(suffix = "untouched", type = FieldType.Date, store = true)})
    private long lastUpdatedDate;
    private long createdDate;

    private long ownerId;
    private String ownerDisplayName;
    private long modifiedById;
    private String modifiedBy;

    private int revisionCount;
    private int version;

    /**
     * Create PolicyLite from {@link PolicyDTO}
     * 
     * @param entity
     * @param appUserSearchRepository
     * @return {@link RemoteEnvironmentLite}
     */
    public static RemoteEnvironmentLite getLite(RemoteEnvironment entity,
                                                ApplicationUserSearchRepository appUserSearchRepository) {
        RemoteEnvironmentLite lite = new RemoteEnvironmentLite();
        lite.setId(entity.getId());

        lite.setName(entity.getName());
        lite.setHost(entity.getHost());
        lite.setPort(entity.getPort());
        lite.setUsername(entity.getUsername());
        lite.setClientId(entity.getClientId());
        lite.setLastUpdatedDate(entity.getLastUpdatedDate().getTime());
        lite.setCreatedDate(entity.getCreatedDate().getTime());
        lite.setRevisionCount(0);
        lite.setVersion(entity.getVersion());
        lite.setActive(entity.isActive());

        Long ownerId = entity.getOwnerId();
        if (ownerId != null) {
            lite.setOwnerId(ownerId);
            lite.setOwnerDisplayName(
                    getDisplayName(appUserSearchRepository, ownerId));
        }

        Long modifiedById = entity.getLastUpdatedBy();
        if (modifiedById != null) {
            lite.setModifiedById(modifiedById);
            lite.setModifiedBy(
                    getDisplayName(appUserSearchRepository, modifiedById));
        }
        return lite;
    }

    private static String getDisplayName(
            ApplicationUserSearchRepository appUserSearchRepository,
            Long ownerId) {
        return appUserSearchRepository.findById(ownerId)
                .map(ApplicationUser::getDisplayName)
                .orElse(StringUtils.EMPTY);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(long lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.setLowercaseName(name);
    }

    public String getLowercaseName() {
        return lowercaseName;
    }

    public void setLowercaseName(String lowercaseName) {
        if (lowercaseName != null) {
            this.lowercaseName = lowercaseName.toLowerCase();
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getRevisionCount() {
        return revisionCount;
    }

    public void setRevisionCount(int revisionCount) {
        this.revisionCount = revisionCount;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerDisplayName() {
        return ownerDisplayName;
    }

    public void setOwnerDisplayName(String ownerDisplayName) {
        this.ownerDisplayName = ownerDisplayName;
    }

    public long getModifiedById() {
        return modifiedById;
    }

    public void setModifiedById(long modifiedById) {
        this.modifiedById = modifiedById;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
    public String toString() {
        return String.format(
                "RemoteEnvironment [id=%s, name=%s, policyFullName=%s, isActive=%s, " +
                        "lastUpdatedDate=%s, createdDate=%s, revisionCount=%s]",
                id, name, host, isActive, lastUpdatedDate, createdDate, revisionCount);
    }

}
