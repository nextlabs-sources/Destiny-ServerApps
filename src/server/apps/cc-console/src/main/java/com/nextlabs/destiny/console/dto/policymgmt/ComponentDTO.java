/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 2, 2016
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import com.nextlabs.destiny.console.dto.Auditable;
import com.nextlabs.destiny.console.dto.Authorizable;
import com.nextlabs.destiny.console.dto.common.BaseDTO;
import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.utils.JsonUtil;

import io.swagger.annotations.ApiModelProperty;

/**
 * DTO for Component
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class ComponentDTO 
		extends BaseDTO
        implements Auditable, Authorizable {

    private static final long serialVersionUID = -3418190224314100508L;

    @ApiModelProperty(value = "The id of the folder to which this component belongs.", position = 30,
            example = "2")
    private Long folderId;

    @ApiModelProperty(value = "The name of the component.", position = 10, example = "Sample Component",
            required = true)
    private String name;

    @ApiModelProperty(value = "The description of the component.", position = 50,
            example = "This is a sample component")
    private String description;

    @ApiModelProperty(value = "Tags of the component.", position = 60)
    private Set<TagDTO> tags;

    @ApiModelProperty(value = "The type of the component.", allowableValues = "RESOURCE, ACTION, SUBJECT",
            example = "SUBJECT", position = 70)
    private String type;

    @ApiModelProperty(
            value = "The category can have the following values: "
                    + "\n<ul><li><strong>FOLDER</strong></li>"
                    + "<li><strong>POLICY</strong></li>"
                    + "<li><strong>COMPONENT</strong></li>"
                    + "<li><strong>DELEGATION_POLICY</strong></li>"
                    + "<li><strong>XACML_POLICY</strong></li>"
                    + "<li><strong>LOCATION</strong></li>"
                    + "<li><strong>DELEGATION_COMPONENT</strong></li></ul>",
            position = 80, example = "COMPONENT")
    private DevEntityType category;

    @ApiModelProperty(value = "Policy model of the component.", position = 90, example = "Printer")
    private PolicyModelDTO policyModel;

    @ApiModelProperty(value = "The list of short names of actions of the component. Applicable only for components of type ACTION.",
            position = 100, example = "[\"VIEW\",\"EDIT\"]")
    private List<String> actions;

    @ApiModelProperty(value = "The conditions of the component.", position = 110)
    private List<ComponentConditionDTO> conditions;

    @ApiModelProperty(value = "The member conditions of the component.", position = 120)
    private List<MemberCondition> memberConditions;

    @ApiModelProperty(value = "The sub components of the component. The sub components is of model ComponentDTO.", position = 130)
    private List<ComponentDTO> subComponents;

    @ApiModelProperty(value = "Indicates the current status of the component.", example = "DRAFT", required = true,
    allowableValues = "DRAFT, APPROVED, DELETED", position = 140)
    private String status;

    @ApiModelProperty(value = "The id of the parent component.", position = 150, example = "80")
    private Long parentId;

    @ApiModelProperty(value = "The parent component's name.", position = 160,
            example = "Parent Sample Component")
    private String parentName;

    @ApiModelProperty(value = "The scheduled deployed time of the component.\n" +
            "The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC (coordinated universal time). For example: November 15, 2019 10:48:49.296 AM is written as 1573786129296.",
            position = 170, example = "1573786129296")
    private long deploymentTime;

    @ApiModelProperty(value = "Indicates whether this component is deployed or not.", position = 180)
    private boolean deployed;

    @ApiModelProperty(value = "Indicates whether the the component is to be deployed or undeployed.\n" +
            "<ul>" +
            "<li><strong>DE</strong>: Deploy</li>" +
            "<li><strong>UN</strong>: Undeploy</li>" +
            "</ul>",
            allowableValues = "DE, UN", example = "DE", position = 190)
    private String actionType;

    @ApiModelProperty(value = "Number of times the component is deployed.", example = "1", position = 200)
    private int revisionCount;

    @ApiModelProperty(hidden = true)
    private long ownerId;

    @ApiModelProperty(hidden = true)
    private String ownerDisplayName;

    @ApiModelProperty(
            value = "Indicates the date at which this component was created. The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC (coordinated universal time). For example: November 15, 2019 10:48:49.296 AM is written as 1573786129296.",
            position = 210, example = "1573786129296")
    private long createdDate;

    @ApiModelProperty(hidden = true)
    private long modifiedById;

    @ApiModelProperty(hidden = true)
    private String modifiedBy;

    @ApiModelProperty(
            value = "Indicates the date at which this component was last modified. The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC (coordinated universal time). For example: November 15, 2019 10:48:49.296 AM is written as 1573786129296.",
            position = 220, example = "1573786129296")
    private long lastUpdatedDate;

    @ApiModelProperty(hidden = true)
    private boolean skipValidate;

    @ApiModelProperty(hidden = true)
    private boolean reIndexAllNow = true;

    @ApiModelProperty(hidden = true)
    private boolean hidden = false;

    @ApiModelProperty(value = "Access management permissions assigned to this component. Mainly used in Control Center Console UI.",
            position = 230)
    private List<GrantedAuthority> authorities;

    @ApiModelProperty(position = 240)
    private DeploymentRequestDTO deploymentRequest;

    @ApiModelProperty(position = 250, value = "The path of the folder to which this component belongs",
            example = "folder/sub-folder")
    private String folderPath;

    @ApiModelProperty(hidden = true)
    private boolean preCreated = false;

    @ApiModelProperty(position = 260, value = "Required only in modify request.", example = "2")
    private int version;

    @ApiModelProperty(value = "Current page number.", position = 270, example = "2")
    private int pageNo = 0;

    @ApiModelProperty(value = "Number of items in page.", position = 280, example = "20")
    private int pageSize = 20;

    /**
     * Default constructor
     * 
     */
    public ComponentDTO() {
    }

    /**
     * constructor
     * 
     */
    public ComponentDTO(Long id) {
        super();
        super.setId(id);
    }

    /**
     * constructor
     * 
     * @param name
     * @param description
     * @param status
     */
    public ComponentDTO(Long id, String name, String description,
            String status) {
        super();
        super.setId(id);
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String toAuditString() 
    		throws ConsoleException {
    	try {
    		Map<String, Object> audit = new LinkedHashMap<>();

            audit.put("Component Category", this.type);
    		if(this.policyModel != null
    				&& this.policyModel.getId() != null) {
    			Map<String, Object> policyModelDetails = new LinkedHashMap<>();
    			
                policyModelDetails.put("Component Type ID", this.policyModel.getId());
    			policyModelDetails.put("Name", this.policyModel.getName());
    			
                audit.put("Component Type", policyModelDetails);
    		}
    		audit.put("Display Name", this.name);
    		audit.put("Description", this.description);

    		if(this.tags != null
    				&& !this.tags.isEmpty()) {
    			List<String> tagNames = new ArrayList<>();
    			
    			for(TagDTO tag : this.tags) {
    				tagNames.add(tag.getLabel());
    			}

    			audit.put("Tags", StringUtils.join(tagNames, ", "));
    		} else {
    			audit.put("Tags", null);
    		}

    		if(this.conditions != null
    				&& !this.conditions.isEmpty()) {
    			List<Map<String, String>> conditionList = new ArrayList<>();

    			for(ComponentConditionDTO condition : this.conditions) {
    				Map<String, String> conditionDetails = new LinkedHashMap<>();
    				
    				conditionDetails.put("Attribute Short Name", condition.getAttribute());
    				conditionDetails.put("Operator", condition.getOperator());
    				conditionDetails.put("Value", condition.getRHSValue());
    				
    				conditionList.add(conditionDetails);
    			}

    			audit.put("Conditions", conditionList);
    		} else {
    			audit.put("Conditions", null);
    		}

    		if(this.memberConditions != null
                    && !this.memberConditions.isEmpty()) {
    			List<Map<String, String>> memberList = new ArrayList<>();

                for (MemberCondition memberCondition : this.memberConditions) {
                    Map<String, String> memberDetails = new LinkedHashMap<>();

                    memberDetails.put("Operator", memberCondition.getOperator().name());
                    memberDetails.put("Size", String.valueOf(memberCondition.getMembers().size()));

                    memberList.add(memberDetails);
                }

    			audit.put("Members", memberList);
    		} else {
    			audit.put("Members", null);
    		}

    		if(this.subComponents != null
    				&& !this.subComponents.isEmpty()) {
    			Map<String, Object> subComponentDetails = new LinkedHashMap<>();

    			for(ComponentDTO component : this.subComponents) {
    				if(component.getId() != null
    						&& component.getId() > 0) {
    					subComponentDetails.put("Component ID", component.getId());
    					subComponentDetails.put("Display Name", component.getName());
    					subComponentDetails.put("Description", component.getDescription());
    				}
    			}

    			audit.put("Include Sub-Components", subComponentDetails);
    		} else {
    			audit.put("Include Sub-Components", null);
    		}

            if (this.deploymentRequest != null) {
                audit.put("Deployment Request", ImmutableMap.of(
                        "Push", deploymentRequest.isPush(),
                        "Deployment Time", deploymentRequest.getDeploymentTime())
                );
            }
    		audit.put("Status", this.status);
            audit.put("Deployment Time", this.deploymentTime);
            audit.put("Deployed", this.deployed);
            audit.put("Folder Path", this.folderPath);

    		return JsonUtil.toJsonString(audit);
    	} catch(Exception e) {
    		throw new ConsoleException(e);
    	}
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<TagDTO> getTags() {
        if (tags == null) {
            tags = new TreeSet<>();
        }
        return tags;
    }

    public void setTags(Set<TagDTO> tags) {
        this.tags = tags;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PolicyModelDTO getPolicyModel() {
        return policyModel;
    }

    public void setPolicyModel(PolicyModelDTO policyModel) {
        this.policyModel = policyModel;
    }

    public List<ComponentConditionDTO> getConditions() {
        if (conditions == null) {
            conditions = new ArrayList<>();
        }
        return conditions;
    }

    public void setConditions(List<ComponentConditionDTO> conditions) {
        this.conditions = conditions;
    }

    public List<MemberCondition> getMemberConditions() {
        if (memberConditions == null) {
            memberConditions = new ArrayList<>();
        }
        return memberConditions;
    }

	public void setMemberConditions(List<MemberCondition> members) {
		this.memberConditions = members;
	}

	public List<ComponentDTO> getSubComponents() {
        if (subComponents == null) {
            subComponents = new ArrayList<>();
        }
        return subComponents;
    }

    public void setSubComponents(List<ComponentDTO> subComponents) {
        this.subComponents = subComponents;
    }

    public List<String> getActions() {
        if (actions == null) {
            actions = new ArrayList<>();
        }
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return this.parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public long getDeploymentTime() {
        return deploymentTime;
    }

    public void setDeploymentTime(long deploymentTime) {
        this.deploymentTime = deploymentTime;
    }

    public boolean isDeployed() {
        return deployed;
    }

    public void setDeployed(boolean deployed) {
        this.deployed = deployed;
    }

    @ApiModelProperty(value = "Indicates if a deployment is pending for the component.", position = 290)
    public boolean isDeploymentPending() {
        return this.deploymentTime > 0 && this.deploymentTime > Instant.now().toEpochMilli();
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        if (StringUtils.isNotEmpty(actionType) && actionType.equals("DE")) {
            this.deployed = true;
        }
        this.actionType = actionType;
    }

    public int getRevisionCount() {
        return revisionCount;
    }

    public void setRevisionCount(int revisionCount) {
        this.revisionCount = revisionCount;
    }

    public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
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

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
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

    public long getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(long lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public boolean isSkipValidate() {
        return skipValidate;
    }

    public void setSkipValidate(boolean skipValidate) {
        this.skipValidate = skipValidate;
    }

    public DevEntityType getCategory() {
        return category;
    }

    public void setCategory(DevEntityType category) {
        this.category = category;
    }

    public boolean isReIndexAllNow() {
        return reIndexAllNow;
    }

    public void setReIndexAllNow(boolean reIndexAllNow) {
        this.reIndexAllNow = reIndexAllNow;
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

    public DeploymentRequestDTO getDeploymentRequest() {
        if (deploymentRequest == null) {
            deploymentRequest = new DeploymentRequestDTO(this.id, DevEntityType.POLICY, false,
                    System.currentTimeMillis(), true);
        }
        return deploymentRequest;
    }

    public void setDeploymentRequest(DeploymentRequestDTO deploymentRequest) {
        this.deploymentRequest = deploymentRequest;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

	public boolean isPreCreated() {
		return preCreated;
	}

	public void setPreCreated(boolean preCreated) {
		this.preCreated = preCreated;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}
