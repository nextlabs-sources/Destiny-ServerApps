/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 26, 2016
 *
 */
package com.nextlabs.destiny.console.dto.delegadmin;

import static org.springframework.data.elasticsearch.annotations.FieldType.Nested;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;

/**
 * Delegation rule light search object
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Document(indexName = "delegation_rules")
@Setting(settingPath = "/search_config/index-settings.json")
public class DelegateRuleLite implements Serializable{

    private static final long serialVersionUID = 3637005317546242355L;

    @Id
    @Field(type = FieldType.Long, store = true)
    private Long id;

    @Column(name = "name", length = 264)
    @MultiField(mainField = @Field(type = FieldType.Text, store = true), otherFields = {
            @InnerField(fielddata = true, suffix = "untouched", type = FieldType.Text, store = true, analyzer =
                    "case_insensitive_analyzer"),
            @InnerField(suffix = "na", type = FieldType.Keyword, store = true)})
    private String name;
    private String rootFolder;

    @Field(type = FieldType.Keyword, store = true)
    private String fullName;
    private String description;
    private String status;

    @Field(type = FieldType.Keyword, store = true)
    private String effectType;

    @MultiField(mainField = @Field(type = FieldType.Date, store = true), otherFields = {
            @InnerField(suffix = "untouched", type = FieldType.Date, store = true)})
    private long lastUpdatedDate;
    private long createdDate;

    private long ownerId;
    private String ownerDisplayName;
    private long modifiedById;
    private String modifiedBy;

    @Field(type = Nested)
    private List<TagDTO> tags;
    
    private int version;

    /**
     * Transform {@link PolicyDevelopmentEntity} entity data to delegation rule
     * light weight reference object
     * 
     * @param devEntity
     *            {@link PolicyDevelopmentEntity}
     * @return {@link PolicyLite}
     */
    public static DelegateRuleLite getLite(PolicyDevelopmentEntity devEntity,
            ApplicationUserSearchRepository appUserSearchRepository) {
        DelegateRuleLite dto = new DelegateRuleLite();
        dto.setId(devEntity.getId());

        String fullName = devEntity.getTitle();
        String[] splits = fullName.split("/", -1);
        String name = splits[splits.length - 1];

        dto.setName(name);
        dto.setRootFolder(splits[0]);
        dto.setFullName(devEntity.getTitle());
        dto.setDescription(devEntity.getDescription());
        dto.setLastUpdatedDate(devEntity.getLastUpdatedDate());
        dto.setCreatedDate(devEntity.getCreatedDate());
        dto.setVersion(devEntity.getVersion());

        Long ownerId = devEntity.getOwner();
        if (ownerId != null) {
            dto.setOwnerId(ownerId);
            dto.setOwnerDisplayName(
                    getDisplayName(appUserSearchRepository, ownerId));
        }

        Long modifiedById = devEntity.getModifiedBy();
        if (modifiedById != null) {
            dto.setModifiedById(modifiedById);
            dto.setModifiedBy(
                    getDisplayName(appUserSearchRepository, modifiedById));
        }

        IDPolicy policy = devEntity.getPolicy();
        if (policy != null) {
            dto.setStatus(policy.getStatus().getName());
            dto.setEffectType(policy.getMainEffect().getName());

            // Tags
            addTags(dto, devEntity);
        }
        return dto;
    }

    private static String getDisplayName(
            ApplicationUserSearchRepository appUserSearchRepository,
            Long ownerId) {
        return appUserSearchRepository.findById(ownerId)
                .map(ApplicationUser::getDisplayName)
                .orElse(StringUtils.EMPTY);
    }

    private static void addTags(DelegateRuleLite dto,
            PolicyDevelopmentEntity devEntity) {
        for (TagLabel tag : devEntity.getTags()) {
            dto.getTags().add(TagDTO.getDTO(tag));
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEffectType() {
        return effectType;
    }

    public void setEffectType(String effectType) {
        this.effectType = effectType;
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

    public List<TagDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
