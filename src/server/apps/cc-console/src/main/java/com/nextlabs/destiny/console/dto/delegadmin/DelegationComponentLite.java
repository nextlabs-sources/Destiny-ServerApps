/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 26, 2016
 *
 */
package com.nextlabs.destiny.console.dto.delegadmin;

import static com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus.getByKey;
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

import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.policy.visitors.PredicateData;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;

/**
 *
 * Light weight delegation component object for list views
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Document(indexName = "delegation_components")
@Setting(settingPath = "/search_config/index-settings.json")
public class DelegationComponentLite implements Serializable {

    private static final long serialVersionUID = -7443766580144343914L;

    @Id
    @Field(type = FieldType.Long, store = true)
    private Long id;

    @Column(name = "name", length = 264)
    @MultiField(mainField = @Field(type = FieldType.Text), otherFields = {
            @InnerField(suffix = "untouched", type = FieldType.Text, store = true, analyzer =
                    "case_insensitive_analyzer"),
            @InnerField(suffix = "na", type = FieldType.Keyword, store = true)})
    private String name;

    private String fullName;
    private String description;
    private String status;
    private Long modelId;
    private String modelType;

    @Field(type = FieldType.Keyword, store = true)
    private String group;

    @MultiField(mainField = @Field(type = FieldType.Date, store = true), otherFields = {
            @InnerField(suffix = "untouched", type = FieldType.Date, store = true)})
    private long lastUpdatedDate;
    private long createdDate;
    private long ownerId;
    private String ownerDisplayName;
    private long modifiedById;
    private String modifiedBy;
    private boolean hasIncludedIn;
    private boolean hasSubComponents;

    @Field(type = Nested, store = true)
    private PredicateData predicateData;

    @Field(type = Nested)
    private List<TagDTO> tags;
    
    private int version;

    /**
     * Transform {@link PolicyDevelopmentEntity} entity data to Component light
     * weight reference object
     * 
     * @param devEntity
     *            {@link PolicyDevelopmentEntity}
     * @param componentGroup
     *            Component group [RESOURCE, ACTION, SUBJECT]
     * @param modelTypeId
     * @param modelType
     *            policy model type
     * @param predicateData
     * @param appUserSearchRepo
     * @return {@link DelegationComponentLite}
     */
    public static DelegationComponentLite getLite(
            PolicyDevelopmentEntity devEntity, String componentGroup,
            Long modelTypeId, String modelType,
            PredicateData predicateData,
            ApplicationUserSearchRepository appUserSearchRepo) {

        String[] splits = devEntity.getTitle().split("/", -1);
        int length = splits.length;
        String name = (length < 2) ? "" : splits[length - 1];

        DelegationComponentLite dto = new DelegationComponentLite();
        dto.setId(devEntity.getId());
        dto.setFullName(devEntity.getTitle());
        dto.setName(name);
        dto.setDescription(devEntity.getDescription());
        dto.setLastUpdatedDate(devEntity.getLastUpdatedDate());
        dto.setCreatedDate(devEntity.getCreatedDate());
        dto.setGroup(componentGroup);
        dto.setModelId(modelTypeId);
        dto.setVersion(devEntity.getVersion());
        dto.setModelType(modelType);
        dto.setPredicateData(predicateData);
        dto.setStatus(getByKey(devEntity.getStatus()).name());

        Long ownerId = devEntity.getOwner();
        if (ownerId != null) {
            dto.setOwnerId(ownerId);
            dto.setOwnerDisplayName(getDisplayName(appUserSearchRepo, ownerId));
        }

        Long modifiedById = devEntity.getModifiedBy();
        if (modifiedById != null) {
            dto.setModifiedById(modifiedById);
            dto.setModifiedBy(getDisplayName(appUserSearchRepo, modifiedById));
        }

        // Tags
        addTags(devEntity, dto);
        return dto;
    }

    private static String getDisplayName(
            ApplicationUserSearchRepository appUserSearchRepository,
            Long ownerId) {
        return appUserSearchRepository.findById(ownerId)
                .map(ApplicationUser::getDisplayName)
                .orElse(StringUtils.EMPTY);
    }

    private static void addTags(PolicyDevelopmentEntity devEntity,
            DelegationComponentLite dto) {
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

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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

    public boolean isHasIncludedIn() {
        return hasIncludedIn;
    }

    public void setHasIncludedIn(boolean hasIncludedIn) {
        this.hasIncludedIn = hasIncludedIn;
    }

    public boolean isHasSubComponents() {
        return hasSubComponents;
    }

    public void setHasSubComponents(boolean hasSubComponents) {
        this.hasSubComponents = hasSubComponents;
    }

    public PredicateData getPredicateData() {
        return predicateData;
    }

    public void setPredicateData(PredicateData predicateData) {
        this.predicateData = predicateData;
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
