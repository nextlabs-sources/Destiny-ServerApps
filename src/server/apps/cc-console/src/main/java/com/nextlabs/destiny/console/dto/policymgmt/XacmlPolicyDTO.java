/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide. Created on Feb 11, 2016
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nextlabs.destiny.console.dto.Auditable;
import com.nextlabs.destiny.console.dto.common.BaseDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.utils.JsonUtil;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * DTO for Xacml Policy
 *
 * @author Mohammed Sainal Shah
 * @since 9.5
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class XacmlPolicyDTO extends BaseDTO implements Auditable {

    private static final long serialVersionUID = 5121358474807911845L;

    private String policyName;
    private String policyFullName;

    private String documentType;
    private String description;
    private String xml;
    private long ownerId;
    private String ownerDisplayName;
    private Date createdDate;
    private long modifiedById;
    private String modifiedBy;
    private Date lastUpdatedDate;
    private int version;

    /**
     * Create Json string of this object for auditing purpose.
     */
    public String toAuditString() throws ConsoleException {
        try {
            Map<String, Object> audit = new LinkedHashMap<>();

            audit.put("Filename", this.policyName);
            audit.put("Description", this.description);
            audit.put("Owner ID", this.ownerId);
            audit.put("Owner Display Name", this.ownerDisplayName);
            audit.put("Modified ID", this.modifiedById);
            audit.put("Modified By", this.modifiedBy);
            audit.put("Created Date", this.createdDate);
            audit.put("Last Updated Date", this.lastUpdatedDate);
            audit.put("Version", this.version);

            return JsonUtil.toJsonString(audit);
        } catch (Exception e) {
            throw new ConsoleException(e);
        }
    }

    @ApiModelProperty(value = "The name of the XACML policy", position = 10, example = "Sample Policy")
    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
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

    @ApiModelProperty(value = "The description of the XACML policy", position = 10, example = "Sample Policy denying all L1 employees access to data center")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    @ApiModelProperty(
                    value = "Indicates the date at which this policy was last modified.",
                    position = 310, example = "November 15, 2019 10:48:49.296")
    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    @ApiModelProperty(
                    value = "Indicates the date at which this policy was created.",
                    position = 300, example = "November 15, 2019 10:48:49.296")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @ApiModelProperty(position = 330, hidden = true)
    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    @ApiModelProperty(position = 335, hidden = true)
    public String getOwnerDisplayName() {
        return ownerDisplayName;
    }

    public void setOwnerDisplayName(String ownerDisplayName) {
        this.ownerDisplayName = ownerDisplayName;
    }

    @ApiModelProperty(position = 345, hidden = true)
    public long getModifiedById() {
        return modifiedById;
    }

    public void setModifiedById(long modifiedById) {
        this.modifiedById = modifiedById;
    }

    @ApiModelProperty(position = 350, hidden = true)
    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @ApiModelProperty(position = 480, example = "2")
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
