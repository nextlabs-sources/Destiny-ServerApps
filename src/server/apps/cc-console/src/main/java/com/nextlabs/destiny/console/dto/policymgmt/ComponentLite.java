/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 5, 2016
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import static com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus.getByKey;
import static org.springframework.data.elasticsearch.annotations.FieldType.Nested;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.security.core.GrantedAuthority;

import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.policy.visitors.PredicateData;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;

/**
 *
 * Light weight component model object for list views
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Document(indexName = "components")
@Setting(settingPath = "/search_config/index-settings.json")
public class ComponentLite implements Serializable, Comparable<ComponentLite> {

    private static final long serialVersionUID = -138690953135106605L;
    public static final String GROUP_RESOURCE = "RESOURCE";
    public static final String GROUP_ACTION = "ACTION";
    public static final String GROUP_FIELD = "group";
    public static final String MODEL_TYPE_FIELD = "modelType";

    @ApiModelProperty(example = "87", position = 10)
    @Id
    @Field(type = FieldType.Long, store = true)
    private Long id;

    @ApiModelProperty(position = 20, value = "The id of the folder to which this component belongs.",
            example = "2")
    @Field(type = FieldType.Keyword)
    private long folderId = -1;

    @ApiModelProperty(position = 30, value = "The path of the folder to which this component belongs",
            example = "folder/sub-folder")
    private String folderPath;

    @ApiModelProperty(position = 40, value = "The name of the component.", example = "Sample Component",
            required = true)
    @Column(name = "name", length = 264)
    @MultiField(mainField = @Field(type = FieldType.Text), otherFields = {
            @InnerField(fielddata = true, suffix = "untouched", type = FieldType.Text, store = true, analyzer =
                    "case_insensitive_analyzer")})
    private String name;

    @ApiModelProperty(position = 50, value = "The name of the component in lowercase.", example = "sample component")
    @Field(type = FieldType.Keyword, store = true)
    private String lowercase_name;

    @ApiModelProperty(position = 60, value = "The full name of the component.", example = "SUBJECT/Sample Component")
    private String fullName;

    @ApiModelProperty(position = 70, value = "The description of the component.",
            example = "This is a sample component")
    private String description;

    @ApiModelProperty(position = 80, value = "Indicates the current status of the component.", example = "DRAFT",
            allowableValues = "DRAFT, APPROVED, DELETED", required = true)
    private String status;

    @ApiModelProperty(position = 90, value = "The ID of the component type.", example = "183")
    private Long modelId;

    @ApiModelProperty(position = 100, value = "The name of the component type.", example = "Host")
    private String modelType;

    @ApiModelProperty(position = 110, value = "The group of the component.", example = "SUBJECT",
            allowableValues = "SUBJECT, RESOURCE, ACTION")
    @Field(type = FieldType.Keyword, store = true)
    private String group;

    @ApiModelProperty(position = 120,
            value = "Indicates the date at which this component was last modified. The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC (coordinated universal time). For example: November 15, 2019 10:48:49.296 AM is written as 1573786129296.",
            example = "1573786129296")
    @MultiField(mainField = @Field(type = FieldType.Date, store = true), otherFields = {
            @InnerField(suffix = "untouched", type = FieldType.Date, store = true)})
    private long lastUpdatedDate;

    @ApiModelProperty(
            value = "Indicates the date at which this component was created. The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC (coordinated universal time). For example: November 15, 2019 10:48:49.296 AM is written as 1573786129296.",
            position = 130, example = "1573786129296")
    private long createdDate;

    @ApiModelProperty(position = 140, value = "ID of the user who created the component.", example = "0")
    private long ownerId;

    @ApiModelProperty(position = 150, value = "Display name of the user who created the component.", example = "user1")
    private String ownerDisplayName;

    @ApiModelProperty(position = 160, value = "ID of the user who last modified the component.", example = "0")
    private long modifiedById;

    @ApiModelProperty(position = 170, value = "Display name of the user who last modified the component.", example = "user1")
    private String modifiedBy;

    @ApiModelProperty(hidden = true)
    private boolean hasIncludedIn;

    @ApiModelProperty(position = 190, value = "Indicates whether the component have sub components.")
    private boolean hasSubComponents;

    @Field(type = Nested, store = true)
    private PredicateData predicateData;

    @ApiModelProperty(position = 200, value = "Tags of the component.")
    @Field(type = Nested)
    private List<TagDTO> tags;

    @ApiModelProperty(hidden = true)
    @Field(type = Nested)
    private List<IncludedComponentLite> includedInComponents;

    @ApiModelProperty(position = 220)
    @Field(type = Nested)
    private List<SubComponentLite> subComponents;

    @ApiModelProperty(
            value = "Indicates the datetime of last deployment.\n" +
                    "The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC (coordinated universal time). For example: November 15, 2019 10:48:49.296 AM is written as 1573786129296.",
            position = 230, example = "1573786129296")
    private long deploymentTime;

    @ApiModelProperty(position = 180, value = "Indicates whether this component is deployed or not.")
    private boolean deployed;

    @ApiModelProperty(value = "Indicates the last action performed on this component.\n" +
            "<ul>" +
            "<li><strong>DE</strong>: Deployed</li>" +
            "<li><strong>N/A</strong>: Undeployed</li>" +
            "</ul>",
            example = "DE",
            position = 260)
    private String actionType;

    @ApiModelProperty(value = "Number of times the component is deployed.", position = 270)
    private int revisionCount;

    @ApiModelProperty(hidden = true)
    private boolean empty;

    @ApiModelProperty(hidden = true)
    private boolean isPreCreated;

    @ApiModelProperty(position = 300, example = "2")
    private int version;

    @ApiModelProperty(position = 310, value = "Access management permissions assigned to this component. Mainly used in Control Center Console UI.")
    @Transient
    private List<GrantedAuthority> authorities;

    /**
     * Transform {@link PolicyDevelopmentEntity} entity data to Component light
     * weight reference object
     *
     * @param devEntity         {@link PolicyDevelopmentEntity}
     * @param componentGroup    Component group [RESOURCE, ACTION, SUBJECT]
     * @param modelTypeId
     * @param modelType         policy model type
     * @param componentIdMap
     * @param predicateData
     * @param appUserSearchRepo
     * @return {@link ComponentLite}
     */
    public static ComponentLite getLite(PolicyDevelopmentEntity devEntity,
            String componentGroup, Long modelTypeId, String modelType,
            Map<Long, PolicyDevelopmentEntity> componentIdMap,
            PredicateData predicateData,
            ApplicationUserSearchRepository appUserSearchRepo) {

        String[] splits = devEntity.getTitle().split("/", -1);
        int length = splits.length;
        String name = (length < 2) ? "" : splits[length - 1];

        ComponentLite dto = new ComponentLite();
        dto.setId(devEntity.getId());
        dto.setFolderId(devEntity.getFolderId() == null ? -1 : devEntity.getFolderId());
        dto.setFolderPath(devEntity.getFolder() == null ? null : devEntity.getFolder().getFolderPath());
        dto.setFullName(devEntity.getTitle());
        dto.setName(name);
        dto.setDescription(devEntity.getDescription());
        dto.setLastUpdatedDate(devEntity.getLastUpdatedDate());
        dto.setCreatedDate(devEntity.getCreatedDate());
        dto.setGroup(componentGroup);
        dto.setModelId(modelTypeId);
        dto.setModelType(modelType);
        dto.setPredicateData(predicateData);
        dto.setStatus(getByKey(devEntity.getStatus()).name());
        dto.setActionType("N/A");
        dto.setRevisionCount(0);
        dto.setVersion(devEntity.getVersion());

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

        // Sub-Components
        addSubComponents(componentIdMap, predicateData, dto);

        // Tags
        addTags(devEntity, dto);

        return dto;
    }

    /**
     * Create ComponentLite from {@link PolicyDTO}
     *
     * @param componentDTO
     * @param appUserSearchRepo
     * @return {@link PolicyLite}
     */
    public static ComponentLite getLite(ComponentDTO componentDTO,
            ApplicationUserSearchRepository appUserSearchRepo) {

        ComponentLite lite = new ComponentLite();
        lite.setId(componentDTO.getId());
        lite.setFolderId(componentDTO.getFolderId() == null ? -1 : componentDTO.getFolderId());
        lite.setFolderPath(componentDTO.getFolderPath());
        lite.setFullName(componentDTO.getName());
        lite.setName(componentDTO.getName());
        lite.setDescription(componentDTO.getDescription());
        lite.setLastUpdatedDate(componentDTO.getLastUpdatedDate());
        lite.setCreatedDate(componentDTO.getCreatedDate());
        lite.setGroup(componentDTO.getCategory().name());
        if (componentDTO.getPolicyModel() != null) {
            lite.setModelId(componentDTO.getPolicyModel().getId());
            lite.setModelType(componentDTO.getPolicyModel().getName());
        }

        lite.setStatus(componentDTO.getStatus());
        lite.setActionType("N/A");
        lite.setRevisionCount(0);
        lite.setVersion(componentDTO.getVersion());
        lite.setTags(new ArrayList<>(componentDTO.getTags()));

        Long ownerId = componentDTO.getOwnerId();
        if (ownerId != null) {
            lite.setOwnerId(ownerId);
            lite.setOwnerDisplayName(
                    getDisplayName(appUserSearchRepo, ownerId));
        }

        Long modifiedById = componentDTO.getModifiedById();
        if (modifiedById != null) {
            lite.setModifiedById(modifiedById);
            lite.setModifiedBy(getDisplayName(appUserSearchRepo, modifiedById));
        }

        lite.setPreCreated(componentDTO.isPreCreated());
        return lite;
    }

    private static String getDisplayName(
            ApplicationUserSearchRepository appUserSearchRepository,
            Long ownerId) {
        return appUserSearchRepository.findById(ownerId)
                .map(ApplicationUser::getDisplayName)
                .orElse(StringUtils.EMPTY);
    }

    private static void addTags(PolicyDevelopmentEntity devEntity,
            ComponentLite dto) {
        for (TagLabel tag : devEntity.getTags()) {
            dto.getTags().add(TagDTO.getDTO(tag));
        }
    }

    private static void addSubComponents(
            Map<Long, PolicyDevelopmentEntity> componentIdMap,
            PredicateData predicateData, ComponentLite dto) {
        for (Long refId : predicateData.getReferenceIds()) {
            PolicyDevelopmentEntity refComponent = componentIdMap.get(refId);
            if (refComponent != null) {
                dto.hasSubComponents = true;
                dto.getSubComponents().add(new SubComponentLite(refComponent));
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getFolderId() {
        return folderId;
    }

    public void setFolderId(long folderId) {
        this.folderId = folderId;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.setLowercase_name(name);
    }

    public String getLowercase_name() {
        return lowercase_name;
    }

    public void setLowercase_name(String lowercase_name) {
        if (lowercase_name != null) {
            this.lowercase_name = lowercase_name.toLowerCase();
        }
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

    public long getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(long lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
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

    public List<TagDTO> getTags() {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }

    public List<IncludedComponentLite> getIncludedInComponents() {
        if (includedInComponents == null) {
            includedInComponents = new ArrayList<>();
        }
        return includedInComponents;
    }

    public void setIncludedInComponents(
            List<IncludedComponentLite> includedInComponents) {
        this.includedInComponents = includedInComponents;
    }

    public List<SubComponentLite> getSubComponents() {
        if (subComponents == null) {
            subComponents = new ArrayList<>();
        }
        return subComponents;
    }

    public void setSubComponents(List<SubComponentLite> subComponents) {
        this.subComponents = subComponents;
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

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public PredicateData getPredicateData() {
        return predicateData;
    }

    public void setPredicateData(PredicateData predicateData) {
        if (this.group.equalsIgnoreCase("ACTION")) {
            if (predicateData == null || predicateData.getActions().isEmpty())
                this.empty = true;
        } else {
            if (predicateData == null
                    || predicateData.getAttributes().isEmpty())
                this.empty = true;
        }
        this.predicateData = predicateData;
    }

    public long getDeploymentTime() {
        return deploymentTime;
    }

    public void setDeploymentTime(long deploymentTime) {
        this.deploymentTime = deploymentTime;
    }

    public boolean isDeployed() {
        return deployed || (this.deploymentTime > 0 && this.deploymentTime < Instant.now().toEpochMilli());
    }

    public void setDeployed(boolean deployed) {
        this.deployed = deployed;
    }

    public boolean isDeploymentPending() {
        return this.deploymentTime > 0 && this.deploymentTime > Instant.now().toEpochMilli();
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        if (StringUtils.isNotEmpty(actionType) && actionType.equals("DE")) {
            this.deployed = true;
        } else if (StringUtils.isNotEmpty(actionType)
                && actionType.equals("UN")) {
            this.deployed = false;
        }
        this.actionType = actionType;
    }

    public int getRevisionCount() {
        return revisionCount;
    }

    public void setRevisionCount(int revisionCount) {
        this.revisionCount = revisionCount;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
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

    public List<GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            authorities = new ArrayList<>();
        }
        return authorities;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public boolean isPreCreated() {
		return isPreCreated;
	}

	public void setPreCreated(boolean isPreCreated) {
		this.isPreCreated = isPreCreated;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
    public int compareTo(ComponentLite o) {
        if (this.id == null || o.id == null)
            return -1;
        return this.id.compareTo(o.getId());
    }

    @Override
    public String toString() {
        return String.format(
                "ComponentLite [id=%s, name=%s, fullName=%s, description=%s, status=%s, modelId=%s, modelType=%s, " +
                        "group=%s, lastUpdatedDate=%s, createdDate=%s, hasIncludedIn=%s, hasSubComponents=%s, " +
                        "predicateData=%s, tags=%s, includedInComponents=%s, subComponents=%s, deploymentTime=%s, " +
                        "deployed=%s, actionType=%s, revisionCount=%s]",
                id, name, fullName, description, status, modelId, modelType,
                group, lastUpdatedDate, createdDate, hasIncludedIn,
                hasSubComponents, predicateData, tags, includedInComponents,
                subComponents, deploymentTime, deployed, actionType, revisionCount);
    }

}
