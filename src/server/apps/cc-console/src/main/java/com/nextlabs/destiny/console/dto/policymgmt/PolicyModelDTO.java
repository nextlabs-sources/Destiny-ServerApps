/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 15, 2016
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import com.nextlabs.destiny.console.dto.Auditable;
import com.nextlabs.destiny.console.dto.common.BaseDTO;
import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.model.policy.ActionConfig;
import com.nextlabs.destiny.console.model.policy.AttributeConfig;
import com.nextlabs.destiny.console.model.policy.ObligationConfig;
import com.nextlabs.destiny.console.model.policy.OperatorConfig;
import com.nextlabs.destiny.console.model.policy.ParameterConfig;
import com.nextlabs.destiny.console.model.policy.PolicyModel;
import com.nextlabs.destiny.console.utils.JsonUtil;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 *
 * DTO for Policy model
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyModelDTO 
		extends BaseDTO 
		implements Auditable {

    private static final long serialVersionUID = -6617606360187203786L;

    @NotBlank
    @Size(min = 1, max = 255)
    @ApiModelProperty(value = "The name of the policy model.",
                    example = "Sample Policy Model")
    private String name;

    @NotBlank
    @Size(min = 1, max = 255)
    @ApiModelProperty(value = "Unique code/identifier of the policy model.",
                    example = "sample_policy_model",
                    notes = "This value is not modifiable once entity created.")
    private String shortName;
    @ApiModelProperty(value = "The description for the policy model.",
                    example = "This is a sample policy model")
    private String description;
    @NotBlank
    @Pattern(regexp = "SUBJECT|RESOURCE|DA_SUBJECT|DA_RESOURCE")
    @ApiModelProperty(value = "The type of policy model. This value is not modifiable after the policy model is created."
                    + "\n<ul><li><strong>SUBJECT</strong>: The subject component for policy.</li>"
                    + "<li><strong>RESOURCE</strong>: The resource component for policy.</li>"
                    + "<li><strong>DA_SUBJECT</strong>: The delegation subject component for the delegation policy.</li>"
                    + "<li><strong>DA_RESOURCE</strong>: The delegation resource component for the delegation policy.</li></ul>",
                    example = "RESOURCE")
    private String type;
    @ApiModelProperty(example = "Active")
    private String status;
    @ApiModelProperty(value = "The tags of the policy model grouping.")
    private Set<TagDTO> tags;
    @ApiModelProperty(value = "The attributes of the policy model.")
    private Set<AttributeConfig> attributes;
    @ApiModelProperty(value = "The actions of the policy model.")
    private Set<ActionConfig> actions;
    @ApiModelProperty(value = "The obligations of the policy model.")
    private Set<ObligationConfig> obligations;
    @ApiModelProperty(example = "[{\"authority\": \"VIEW_POLICY_MODEL\"}, {\"authority\": \"VIEW_POLICY_MODEL\"}]")
    private List<GrantedAuthority> authorities;
    @ApiModelProperty(value = "Indicates the date at which this policy model was last modified. The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC (coordinated universal time). For example: November 15, 2019 10:48:49.296 AM is written as 1573786129296.",
                    example = "1573786129296",
                    notes = "This value is automatically populated by the system when modification of entity happens.")
    private long lastUpdatedDate;
    @ApiModelProperty(value = "Indicates the date at which this policy model was created. The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC (coordinated universal time). For example: November 15, 2019 10:48:49.296 AM is written as 1573786129296.",
                    example = "1573786129296",
                    notes = "This value is automatically populated by the system when creation of entity happens.")

    private long createdDate;
    @ApiModelProperty(example = "null")
    private Long ownerId;
    @ApiModelProperty(example = "null")
    private String ownerDisplayName;
    @ApiModelProperty(example = "0")
    private Long modifiedById;
    @ApiModelProperty(example = "null")
    private String modifiedBy;
    @ApiModelProperty(example = "2", notes = "Required only in modify request.")
    private int version;

    /**
     * Transform {@link PolicyModel} entity data to light weight DTO
     * 
     * @return {@link PolicyModelDTO}
     */
    public static PolicyModelDTO getLiteDTO(PolicyModel model) {
        PolicyModelDTO dto = new PolicyModelDTO();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setShortName(model.getShortName());
        dto.setDescription(model.getDescription());
        dto.setType(model.getType().name());
        dto.setStatus(model.getStatus().name());
        dto.setOwnerId(model.getOwnerId());
        dto.setModifiedById(model.getLastUpdatedBy());
        dto.setLastUpdatedDate(model.getLastUpdatedDate().getTime());
        dto.setVersion(model.getVersion());

        for (TagLabel tag : model.getTags()) {
            dto.getTags().add(TagDTO.getDTO(tag));
        }

        return dto;
    }

    /**
     * Transform {@link PolicyModel} entity data to DTO
     * 
     * @return {@link PolicyModelDTO}
     */
    public static PolicyModelDTO getDTO(PolicyModel model) {
        PolicyModelDTO dto = new PolicyModelDTO();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setShortName(model.getShortName());
        dto.setDescription(model.getDescription());
        dto.setType(model.getType().name());
        dto.setStatus(model.getStatus().name());
        dto.setOwnerId(model.getOwnerId());
        dto.setModifiedById(model.getLastUpdatedBy());
        dto.setLastUpdatedDate(model.getLastUpdatedDate().getTime());
        dto.setVersion(model.getVersion());

        dto.setActions(model.getActions());
        dto.setAttributes(model.getAttributes());
        dto.setObligations(model.getObligations());

        for (TagLabel tag : model.getTags()) {
            dto.getTags().add(TagDTO.getDTO(tag));
        }

        return dto;
    }

    public String toAuditString() 
    		throws ConsoleException {
    	try {
	    	Map<String, Object> audit = new LinkedHashMap<>();

	    	audit.put("Type", this.type);
	    	audit.put("Name", this.name);
	    	audit.put("Short Name", this.shortName);
	    	audit.put("Description", this.description);
	    	audit.put("Status", this.status);

	    	if(this.tags != null
	    			&& !this.tags.isEmpty()) {
	    		List<String> modelTags = new ArrayList<>();
	    		
	    		for(TagDTO tag : this.tags) {
	    			modelTags.add(tag.getLabel());
	    		}
	    		
	    		audit.put("Tags", StringUtils.join(modelTags, ", "));
	    	} else {
	    		audit.put("Tags", null);
	    	}

	    	if(this.attributes != null
	    			&& !this.attributes.isEmpty()) {
	    		List<Map<String, String>> modelAttributes = new ArrayList<>();
	    		
	    		for(AttributeConfig attribute : this.attributes) {
	    			Map<String, String> modelAttribute = new LinkedHashMap<>();
	    			modelAttribute.put("Name", attribute.getName());
	    			modelAttribute.put("Short Name", attribute.getShortName());
	    			modelAttribute.put("Data Type", attribute.getDataType().name());
	    			List<String> operators = new ArrayList<>();
	    			for(OperatorConfig operator : attribute.getOperatorConfigs()) {
	    				operators.add(operator.getLabel());
	    			}
	    			modelAttribute.put("Operators", StringUtils.join(operators, ", "));
	    			
	    			modelAttributes.add(modelAttribute);
	    		}
	    		
	    		audit.put("Attributes", modelAttributes);
	    	} else {
	    		audit.put("Attributes", null);
	    	}

	    	if(this.actions != null
	    			&& !this.actions.isEmpty()) {
	    		List<Map<String, String>> modelActions = new ArrayList<>();
	    		
	    		for(ActionConfig action : this.actions) {
	    			Map<String, String> modelAction = new LinkedHashMap<>();
	    			modelAction.put("Name", action.getName());
	    			modelAction.put("Short Name", action.getShortName());
	    			
	    			modelActions.add(modelAction);
	    		}
	    		
	    		audit.put("Actions", modelActions);
	    	} else {
	    		audit.put("Actions", null);
	    	}

	    	if(this.obligations != null
	    			&& !this.obligations.isEmpty()) {
	    		List<Map<String, Object>> obligationsList = new ArrayList<>(); 
	    		
	    		for(ObligationConfig config : this.obligations) {
	    			Map<String, Object> obligationDetails = new LinkedHashMap<>();
	    			
	    			obligationDetails.put("Name", config.getName());
	    			obligationDetails.put("Short Name", config.getShortName());
	    			
	    			if(config.getParameters() != null
	    					&& !config.getParameters().isEmpty()) {
	    				List<Map<String, String>> obligationParameters = new ArrayList<>();
	    				
	    				for(ParameterConfig parameter : config.getParameters()) {
	    					Map<String, String> parameterDetails = new LinkedHashMap<>();
	    					
	    					parameterDetails.put("Name", parameter.getName());
	    					parameterDetails.put("Short Name", parameter.getShortName());
	    					parameterDetails.put("Type", parameter.getType().name());
	    					parameterDetails.put("List Values", parameter.getListValues());
	    					parameterDetails.put("Default Value", parameter.getDefaultValue());
	    					parameterDetails.put("Hidden", Boolean.toString(parameter.isHidden()));
	    					parameterDetails.put("Editable", Boolean.toString(parameter.isEditable()));
	    					parameterDetails.put("Mandatory", Boolean.toString(parameter.isMandatory()));
	    					
	    					obligationParameters.add(parameterDetails);
	    				}
	    				
	    				obligationDetails.put("Parameters", obligationParameters);
	    			} else {
	    				obligationDetails.put("Parameters", null);
	    			}
	    			
	    			obligationsList.add(obligationDetails);
	    		}
	    		
	    		audit.put("Obligations", obligationsList);
	    	} else {
	    		audit.put("Obligations", null);
	    	}

	    	audit.put("Status", this.status);

	    	return JsonUtil.toJsonString(audit);
    	} catch(Exception e) {
    		throw new ConsoleException(e);
    	}
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Set<AttributeConfig> getAttributes() {
        if (attributes == null) {
            attributes = new TreeSet<>();
        }
        return attributes;
    }

    public void setAttributes(Set<AttributeConfig> attributes) {
        this.attributes = attributes;
    }

    public Set<ActionConfig> getActions() {
        if (actions == null) {
            actions = new TreeSet<>();
        }
        return actions;
    }

    public void setActions(Set<ActionConfig> actions) {
        this.actions = actions;
    }

    public Set<ObligationConfig> getObligations() {
        if (obligations == null) {
            obligations = new TreeSet<>();
        }
        return obligations;
    }

    public void setObligations(Set<ObligationConfig> obligations) {
        this.obligations = obligations;
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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
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

    public String getOwnerDisplayName() {
        return ownerDisplayName;
    }

    public void setOwnerDisplayName(String ownerDisplayName) {
        this.ownerDisplayName = ownerDisplayName;
    }

    public Long getModifiedById() {
        return modifiedById;
    }

    public void setModifiedById(Long modifiedById) {
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
}
