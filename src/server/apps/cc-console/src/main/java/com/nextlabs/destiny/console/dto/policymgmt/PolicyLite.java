/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 26, 2015
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import static org.springframework.data.elasticsearch.annotations.FieldType.Nested;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nextlabs.destiny.console.dto.policymgmt.policyworkflow.EntityWorkflowRequestDTO;
import com.nextlabs.destiny.console.model.policyworkflow.EntityWorkflowRequest;
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

import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.bluejungle.pf.domain.epicenter.exceptions.IPolicyExceptions;
import com.bluejungle.pf.domain.epicenter.exceptions.IPolicyReference;
import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;

/**
 *
 * Light weight policy entity object for list views
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Document(indexName = "policies")
@Setting(settingPath = "/search_config/index-settings.json")
public class PolicyLite implements Serializable {

    private static final long serialVersionUID = -3109478968307531857L;

    @ApiModelProperty(example = "87", position = 10)
    @Id
    @Field(type = FieldType.Long, store = true)
    private Long id;

    @ApiModelProperty(value = "The id of the folder to which this policy belongs.", position = 20,
            example = "2")
    @Field(type = FieldType.Keyword)
    private long folderId = -1;

    @ApiModelProperty(value = "The path of the folder to which this policy belongs.", position = 30,
            example = "folder/sub-folder")
    private String folderPath;

    @ApiModelProperty(value = "The name of the policy.", position = 40,
            example = "Sample Policy")
    @Column(name = "name", length = 264)
    @MultiField(mainField = @Field(type = FieldType.Text, store = true), otherFields = {
            @InnerField(fielddata = true, suffix = "untouched", type = FieldType.Text, store = true, analyzer =
                    "case_insensitive_analyzer")})
    private String name;

    @ApiModelProperty(value = "The name of the policy in lowercase.", position = 50,
            example = "sample policy")
    @Field(type = FieldType.Keyword, store = true)
    private String lowercase_name;

    private String rootFolder;

    @ApiModelProperty(value = "The full name of the policy.", position = 60,
            example = "ROOT_87/Sample Policy")
    @Field(type = FieldType.Keyword, store = true)
    private String policyFullName;

    @ApiModelProperty(value = "The description of the policy.", position = 70,
            example = "This is a sample policy")
    private String description;

    @ApiModelProperty(value = "Indicates the current status of the policy.", example = "DRAFT",position = 80,
            allowableValues = "DRAFT, APPROVED, DELETED", required = true)
    private String status;

    @ApiModelProperty(
            value = "The intended consequence of this policy, for example, Allow or Deny.",
            position = 90, required = true)
    @Field(type = FieldType.Keyword, store = true)
    private String effectType;

    @ApiModelProperty(
            value = "Indicates the date at which this policy was last modified. The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC(coordinated universal time). For example, November 15, 2019 10:48:49.296 AM will be written as 1573786129296.",
            position = 100, example = "1573786129296")
    @MultiField(mainField = @Field(type = FieldType.Date, store = true), otherFields = {
            @InnerField(suffix = "untouched", type = FieldType.Date, store = true)})
    private long lastUpdatedDate;

    @ApiModelProperty(
            value = "Indicates the date at which this policy was created. The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC(coordinated universal time). For example, November 15, 2019 10:48:49.296 AM will be written as 1573786129296.",
            position = 110, example = "1573786129296")
    private long createdDate;

    @ApiModelProperty(value = "Indicates if this policy is a sub-policy of another policy.",
            position = 120)
    private boolean hasParent;

    @ApiModelProperty(value = "Indicates if this policy has sub-policies.", position = 130)
    private boolean hasSubPolicies;

    @ApiModelProperty(position = 140, value = "ID of the user who created the policy.", example = "0")
    private long ownerId;

    @ApiModelProperty(position = 150, value = "Display name of the user who created the policy.", example = "user1")
    private String ownerDisplayName;

    @ApiModelProperty(position = 160, value = "ID of the user who last modified the policy.", example = "0")
    private long modifiedById;

    @ApiModelProperty(position = 170, value = "Display name of the user who last modified the policy.", example = "user1")
    private String modifiedBy;

    @ApiModelProperty(position = 180, value = "Tags associated with this policy.")
    @Field(type = Nested)
    private List<TagDTO> tags;

    @ApiModelProperty(position = 190, value = "Number of tags associated with this policy.")
    private int noOfTags;

    @ApiModelProperty(position = 200)
    private ParentPolicyLite parentPolicy;

    @ApiModelProperty(position = 210)
    @Field(type = Nested)
    private List<SubPolicyLite> subPolicies;

    @ApiModelProperty(hidden = true)
    @Transient
    private List<PolicyLite> childNodes;

    @ApiModelProperty(position = 230, value = "Access management permissions assigned to this policy. Mainly used in Control Center Console UI.")
    @Transient
    private List<GrantedAuthority> authorities;

    @ApiModelProperty(position = 240)
    private boolean manualDeploy;

    @ApiModelProperty(
            value = "Indicates the date at which this policy was last deployed. The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC(coordinated universal time). For example, November 15, 2019 10:48:49.296 AM will be written as 1573786129296.",
            position = 250, example = "1573786129296")
    private long deploymentTime;

    @ApiModelProperty(value = "Indicates whether the policy is/ was deployed.")
    private boolean deployed;

    @ApiModelProperty(value = "Indicates the last action performed on this policy.\n" +
            "<ul>" +
            "<li><strong>DE</strong>: Deployed</li>" +
            "<li><strong>N/A</strong>: Undeployed</li>" +
            "</ul>",
            example = "DE",
            position = 260)
    private String actionType;

    @ApiModelProperty(value = "The ID of the active policy workflow. This value being null means workflow is not created.", position = 270)
    private Long activeWorkflowId;

    @ApiModelProperty(value = "The entity workflow request status.", allowableValues = "PENDING, DEPLOYED, APPROVED, CANCELLED", position = 280)
    private String activeEntityWorkflowRequestStatus;

    @ApiModelProperty(value = "The workflow request level status.", allowableValues = "PENDING, REQUESTED_AMENDMENT, APPROVED", position = 280)
    private String activeWorkflowRequestLevelStatus;

    @ApiModelProperty(value = "Number of times the policy is deployed.", position = 11)
    private int revisionCount;

    @ApiModelProperty(position = 12, example = "2")
    private int version;

    /**
     * Transform {@link PolicyDevelopmentEntity} entity data to Policy light
     * weight reference object
     * 
     * @param devEntity
     *            {@link PolicyDevelopmentEntity}
     * @param policyNameIdMap
     *            map of Policy name to Id
     * @return {@link PolicyLite}
     */
    public static PolicyLite getLite(PolicyDevelopmentEntity devEntity,
            Map<String, PolicyDevelopmentEntity> policyNameIdMap,
            ApplicationUserSearchRepository appUserSearchRepository) {
        PolicyLite dto = new PolicyLite();
        dto.setId(devEntity.getId());
        dto.setFolderId(devEntity.getFolderId() == null ? -1 : devEntity.getFolderId());
        dto.setFolderPath(devEntity.getFolder() == null ? null : devEntity.getFolder().getFolderPath());

        String fullName = devEntity.getTitle();
        String[] splits = fullName.split("/", -1);
        String name = splits[splits.length - 1];

        dto.setName(name);
        dto.setRootFolder(splits[0]);
        dto.setPolicyFullName(devEntity.getTitle());
        dto.setDescription(devEntity.getDescription());
        dto.setLastUpdatedDate(devEntity.getLastUpdatedDate());
        dto.setCreatedDate(devEntity.getCreatedDate());
        dto.setActionType("N/A");
        dto.setRevisionCount(0);
        dto.setVersion(devEntity.getVersion());

        EntityWorkflowRequest activeEntityWorkflowRequest = devEntity.getActiveWorkflowRequest();
        if (activeEntityWorkflowRequest != null) {
            dto.setActiveWorkflowId(activeEntityWorkflowRequest.getId());
            dto.setActiveEntityWorkflowRequestStatus(activeEntityWorkflowRequest.getStatus().name());
            if (activeEntityWorkflowRequest.getActiveWorkflowRequestLevel() != null) {
                dto.setActiveWorkflowRequestLevelStatus(activeEntityWorkflowRequest.getActiveWorkflowRequestLevel().getStatus().name());
            }
        }

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

            // Parent Policy
            addParentPolicyDetails(devEntity, policyNameIdMap, dto);

            // Sub-policies
            addSubPolicies(policyNameIdMap, dto, policy);

            // Tags
            addTags(dto, devEntity);
            dto.setManualDeploy(policy.getDeploymentTarget() != null);
            dto.setNoOfTags(dto.getTags().size());
        }
        return dto;
    }

    /**
     * Create PolicyLite from {@link PolicyDTO}
     * 
     * @param policyDTO
     * @param appUserSearchRepository
     * @return {@link PolicyLite}
     */
    public static PolicyLite getLite(PolicyDTO policyDTO,
            ApplicationUserSearchRepository appUserSearchRepository) {
        PolicyLite lite = new PolicyLite();
        lite.setId(policyDTO.getId());
        lite.setFolderId(policyDTO.getFolderId() == null ? -1 : policyDTO.getFolderId());
        lite.setFolderPath(policyDTO.getFolderPath());

        String fullName = policyDTO.getFullName();
        String[] splits = fullName.split("/", -1);

        lite.setName(policyDTO.getName());
        lite.setRootFolder(splits[0]);
        lite.setPolicyFullName(policyDTO.getFullName());
        lite.setDescription(policyDTO.getDescription());
        lite.setLastUpdatedDate(policyDTO.getLastUpdatedDate());
        lite.setCreatedDate(policyDTO.getCreatedDate());
        lite.setActionType("N/A");
        lite.setRevisionCount(0);
        lite.setVersion(policyDTO.getVersion());
        lite.setStatus(policyDTO.getStatus());
        lite.setEffectType(policyDTO.getEffectType());
        lite.setHasParent(policyDTO.isHasParent());
        lite.setHasSubPolicies(policyDTO.isHasSubPolicies());
        lite.setTags(new ArrayList<>(policyDTO.getTags()));
        lite.setNoOfTags(lite.getTags().size());
        lite.setManualDeploy(policyDTO.isManualDeploy());

        EntityWorkflowRequestDTO activeEntityWorkflowRequest = policyDTO.getActiveWorkflowRequest();
        if (activeEntityWorkflowRequest != null) {
            lite.setActiveWorkflowId(activeEntityWorkflowRequest.getId());
            lite.setActiveEntityWorkflowRequestStatus(activeEntityWorkflowRequest.getStatus().name());
            if (activeEntityWorkflowRequest.getActiveWorkflowRequestLevel() != null) {
                lite.setActiveWorkflowRequestLevelStatus(activeEntityWorkflowRequest.getActiveWorkflowRequestLevel().getStatus().name());
            }
        }

        // TODO : Add sub policies and parents

        Long ownerId = policyDTO.getOwnerId();
        if (ownerId != null) {
            lite.setOwnerId(ownerId);
            lite.setOwnerDisplayName(
                    getDisplayName(appUserSearchRepository, ownerId));
        }

        Long modifiedById = policyDTO.getModifiedById();
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

    private static void addTags(PolicyLite dto,
            PolicyDevelopmentEntity devEntity) {
        for (TagLabel tag : devEntity.getTags()) {
            dto.getTags().add(TagDTO.getDTO(tag));
        }
    }

    private static void addSubPolicies(
            Map<String, PolicyDevelopmentEntity> policyNameIdMap,
            PolicyLite dto, IDPolicy policy) {
        IPolicyExceptions policyExceptions = policy.getPolicyExceptions();
        for (IPolicyReference policyRef : policyExceptions.getPolicies()) {
            String refName = policyRef.getReferencedName();
            PolicyDevelopmentEntity devEntity = policyNameIdMap.get(refName);
            if (devEntity == null)
                continue;
            SubPolicyLite subPolicyLite = new SubPolicyLite(devEntity, refName);
            dto.getSubPolicies().add(subPolicyLite);
        }
        dto.setHasSubPolicies(!dto.getSubPolicies().isEmpty());
    }

    private static void addParentPolicyDetails(
            PolicyDevelopmentEntity devEntity,
            Map<String, PolicyDevelopmentEntity> policyNameIdMap,
            PolicyLite dto) {
        ParentPolicyLite parentPolicy = new ParentPolicyLite(
                devEntity.getTitle());
        if (parentPolicy.isParent()) {
            PolicyDevelopmentEntity parent = policyNameIdMap
                    .get(parentPolicy.getPolicyFullName());
            if (parent != null) {
                parentPolicy.setId(parent.getId());
                dto.setParentPolicy(parentPolicy);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(String rootFolder) {
        this.rootFolder = rootFolder;
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

    public boolean isHasParent() {
        return hasParent;
    }

    public void setHasParent(boolean hasParent) {
        this.hasParent = hasParent;
    }

    public boolean isHasSubPolicies() {
        return hasSubPolicies;
    }

    public void setHasSubPolicies(boolean hasSubPolicies) {
        this.hasSubPolicies = hasSubPolicies;
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

    public ParentPolicyLite getParentPolicy() {
        return parentPolicy;
    }

    public void setParentPolicy(ParentPolicyLite parentPolicy) {
        this.parentPolicy = parentPolicy;
        if (parentPolicy != null) {
            hasParent = true;
        }
    }

    public List<SubPolicyLite> getSubPolicies() {
        if (this.subPolicies == null) {
            this.subPolicies = new ArrayList<>();
        }
        return subPolicies;
    }

    public void setSubPolicies(List<SubPolicyLite> subPolicies) {
        this.subPolicies = subPolicies;
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

    public String getPolicyFullName() {
        return policyFullName;
    }

    public void setPolicyFullName(String policyFullName) {
        this.policyFullName = policyFullName;
    }

    public List<PolicyLite> getChildNodes() {
        if (this.childNodes == null) {
            this.childNodes = new ArrayList<>();
        }
        return childNodes;
    }

    public void setChildNodes(List<PolicyLite> childNodes) {
        this.childNodes = childNodes;
    }

    public boolean isManualDeploy() {
        return manualDeploy;
    }

    public void setManualDeploy(boolean manualDeploy) {
        this.manualDeploy = manualDeploy;
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

    public boolean isDeploymentPending() {
        return this.deploymentTime > 0 && this.deploymentTime > Instant.now().toEpochMilli();
    }

    public void setDeployed(boolean deployed) {
        this.deployed = deployed;
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
        if (this.authorities == null) {
            this.authorities = new ArrayList<>();
        }
        return authorities;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public int getNoOfTags() {
        return noOfTags;
    }

    public void setNoOfTags(int noOfTags) {
        this.noOfTags = noOfTags;
    }

    public Long getActiveWorkflowId() {
        return activeWorkflowId;
    }

    public void setActiveWorkflowId(Long activeWorkflowId) {
        this.activeWorkflowId = activeWorkflowId;
    }

    public String getActiveEntityWorkflowRequestStatus() {
        return activeEntityWorkflowRequestStatus;
    }

    public void setActiveEntityWorkflowRequestStatus(String activeEntityWorkflowRequestStatus) {
        this.activeEntityWorkflowRequestStatus = activeEntityWorkflowRequestStatus;
    }

    public String getActiveWorkflowRequestLevelStatus() {
        return activeWorkflowRequestLevelStatus;
    }

    public void setActiveWorkflowRequestLevelStatus(String activeWorkflowRequestLevelStatus) {
        this.activeWorkflowRequestLevelStatus = activeWorkflowRequestLevelStatus;
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
                "PolicyLite [id=%s, name=%s, rootFolder=%s, policyFullName=%s, description=%s, status=%s, " +
                        "effectType=%s, lastUpdatedDate=%s, createdDate=%s, hasParent=%s, hasSubPolicies=%s, tags=%s," +
                        " parentPolicy=%s, subPolicies=%s, childNodes=%s, manualDeploy=%s, deploymentTime=%s, " +
                        "deployed=%s, actionType=%s, revisionCount=%s]",
                id, name, rootFolder, policyFullName, description, status,
                effectType, lastUpdatedDate, createdDate, hasParent,
                hasSubPolicies, tags, parentPolicy, subPolicies, childNodes,
                manualDeploy, deploymentTime, deployed, actionType, revisionCount);
    }

}
