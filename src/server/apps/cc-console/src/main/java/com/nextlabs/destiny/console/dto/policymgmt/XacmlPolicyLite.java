/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 26, 2015
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.services.policy.impl.PolicyMgmtServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

import javax.persistence.Column;
import java.io.Serializable;

/**
 *
 * Light weight policy entity object for list views
 *
 * @author Mohammed Sainal Shah
 * @since 9.5
 *
 */
@Document(indexName = "xacml_policies")
@Setting(settingPath = "/search_config/index-settings.json")
public class XacmlPolicyLite implements Serializable {

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
    private String policyFullName;
    private String description;
    private String status;

    @MultiField(mainField = @Field(type = FieldType.Date, store = true), otherFields = {
            @InnerField(suffix = "untouched", type = FieldType.Date, store = true)})
    private long lastUpdatedDate;
    private long createdDate;

    private long ownerId;
    private String ownerDisplayName;
    private long modifiedById;
    private String modifiedBy;
    private String documentType;

    private int revisionCount;
    private int version;

    /**
     * Create PolicyLite from {@link PolicyDTO}
     * 
     * @param entity
     * @param appUserSearchRepository
     * @return {@link XacmlPolicyLite}
     */
    public static XacmlPolicyLite getLite(PolicyDevelopmentEntity entity,
                                          ApplicationUserSearchRepository appUserSearchRepository) {
        XacmlPolicyLite lite = new XacmlPolicyLite();
        lite.setId(entity.getId());

        String fullName = entity.getTitle();
        String[] splits = fullName.split("/", -1);
        String name = splits[splits.length - 1];

        lite.setName(name);
        lite.setPolicyFullName(fullName);
        lite.setDescription(entity.getDescription());
        lite.setLastUpdatedDate(entity.getLastUpdatedDate());
        lite.setCreatedDate(entity.getCreatedDate());
        lite.setRevisionCount(0);
        lite.setVersion(entity.getVersion());
        lite.setStatus(entity.getStatus());

        Long ownerId = entity.getOwner();
        if (ownerId != null) {
            lite.setOwnerId(ownerId);
            lite.setOwnerDisplayName(
                    getDisplayName(appUserSearchRepository, ownerId));
        }

        Long modifiedById = entity.getModifiedBy();
        if (modifiedById != null) {
            lite.setModifiedById(modifiedById);
            lite.setModifiedBy(
                    getDisplayName(appUserSearchRepository, modifiedById));
        }
        if (entity.getTitle().startsWith(PolicyMgmtServiceImpl.XACML_POLICY_SET_ROOT)){
            lite.setDocumentType("XACML POLICY SET");
        } else if (entity.getTitle().startsWith(PolicyMgmtServiceImpl.XACML_POLICY_ROOT)){
            lite.setDocumentType("XACML POLICY");
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

    public String getPolicyFullName() {
        return policyFullName;
    }

    public void setPolicyFullName(String policyFullName) {
        this.policyFullName = policyFullName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
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
                "XacmlPolicyLite [id=%s, name=%s, policyFullName=%s, description=%s, status=%s, " +
                        "lastUpdatedDate=%s, createdDate=%s, revisionCount=%s]",
                id, name, policyFullName, description, status, lastUpdatedDate, createdDate, revisionCount);
    }

}
