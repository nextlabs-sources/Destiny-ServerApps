/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 26, 2016
 *
 */
package com.nextlabs.destiny.console.dto.delegadmin;

import static com.nextlabs.destiny.console.enums.ObligationTagType.DELETE_TAG_FILTERS;
import static com.nextlabs.destiny.console.enums.ObligationTagType.DEPLOY_TAG_FILTERS;
import static com.nextlabs.destiny.console.enums.ObligationTagType.EDIT_TAG_FILTERS;
import static com.nextlabs.destiny.console.enums.ObligationTagType.INSERT_TAG_FILTERS;
import static com.nextlabs.destiny.console.enums.ObligationTagType.MOVE_TAG_FILTERS;
import static com.nextlabs.destiny.console.enums.ObligationTagType.VIEW_TAG_FILTERS;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import com.nextlabs.destiny.console.dto.common.BaseDTO;
import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ObligationDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyComponent;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.enums.DelegationModelShortName;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.enums.ObligationTagType;
import com.nextlabs.destiny.console.enums.Operator;
import com.nextlabs.destiny.console.exceptions.ConsoleException;

/**
 *
 * Delegation Rule DTO
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class DelegationRuleDTO extends BaseDTO {

    private static final long serialVersionUID = 7582969642748966784L;

    @NotBlank
    @Size(max = 255)
    @ApiModelProperty(value = "Delegation rule name",
            example = "Internal User Access")
    private String name;

    @ApiModelProperty(hidden = true)
    private String fullName;

    @Size(max = 4000)
    @ApiModelProperty(value = "Delegation rule description",
            example = "This delegation rule control internal user access")
    private String description;

    @Pattern(regexp = "DRAFT|DEPLOYED")
    @NotBlank
    @ApiModelProperty(value = "Current status of the delegation rule",
            example = "DRAFT")
    private String status;

    @NotBlank
    @ApiModelProperty(value = "Delegation rule type",
            example = "DP")
    private DevEntityType category;

    @NotBlank
    @Pattern(regexp = "allow|deny")
    @ApiModelProperty(value = "Delegation rule effect type",
            example = "allow")
    private String effectType;

    @ApiModelProperty(hidden = true)
    private Set<TagDTO> tags;

    @ApiModelProperty(hidden = true)
    private Long parentId;

    @ApiModelProperty(hidden = true)
    private boolean hasParent;

    @ApiModelProperty(hidden = true)
    private boolean hasSubPolicies;

    @ApiModelProperty(value = "The subject components of the delegation rule")
    private ComponentDTO subjectComponent;

    @ApiModelProperty(value = "The action components of the delegation rule")
    private List<PolicyComponent> actionComponents;

    @ApiModelProperty(value = "The resource components of the delegation rule")
    private List<PolicyComponent> resourceComponents;

    // parameters saved as yml simillar format
    @ApiModelProperty(value = "The obligations of the delegation rule")
    private List<ObligationDTO> obligations;

    @ApiModelProperty(value = "The attributes of the delegation rule")
    private Set<String> attributes;

    @ApiModelProperty(hidden = true)
    private long ownerId;

    @ApiModelProperty(hidden = true)
    private String ownerDisplayName;

    @ApiModelProperty(value = "Indicates the date at which this delegation rule was created. The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC (coordinated universal time). For example: November 15, 2019 10:48:49.296 AM is written as 1573786129296.",
            example = "1573786129296",
            notes = "This value is automatically populated by the system when creation of entity happens.")
    private long createdDate;

    @ApiModelProperty(hidden = true)
    private long modifiedById;

    @ApiModelProperty(hidden = true)
    private String modifiedBy;

    @ApiModelProperty(value = "Indicates the date at which this delegation rule was last modified. The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC (coordinated universal time). For example: November 15, 2019 10:48:49.296 AM is written as 1573786129296.",
            example = "1573786129296",
            notes = "This value is automatically populated by the system when modification of entity happens.")
    private long lastUpdatedDate;

    @ApiModelProperty(hidden = true)
    private int version;

    @ApiModelProperty(hidden = true)
    private boolean reIndexNow = false;

    /**
     * Convert {@link DelegationRuleDTO} to {@link PolicyDTO}
     * 
     * @param ruleDTO
     * @return
     * @throws ConsoleException
     */
    public static PolicyDTO getPolicyDTO(DelegationRuleDTO ruleDTO)
            throws ConsoleException {
        PolicyDTO policy = new PolicyDTO();
        policy.setId(ruleDTO.getId());
        policy.setName(ruleDTO.getName());
        policy.setFullName(ruleDTO.getFullName());
        policy.setDescription(ruleDTO.getDescription());
        policy.setStatus(ruleDTO.getStatus());
        policy.setCategory(DevEntityType.DELEGATION_POLICY);
        policy.setEffectType(ruleDTO.getEffectType());
        policy.setVersion(ruleDTO.getVersion());

        PolicyComponent subComponent = new PolicyComponent();
        subComponent.setOperator(Operator.IN);
        subComponent.getComponents().add(ruleDTO.getSubjectComponent());
        policy.getSubjectComponents().add(subComponent);

        policy.setActionComponents(ruleDTO.getActionComponents());
        policy.setFromResourceComponents(ruleDTO.getResourceComponents());

        // convert to yml similar format
        List<ObligationDTO> obligations = ruleDTO.getObligations();
        convertPQLFriendlyFilterParams(obligations);
        policy.setAllowObligations(obligations);

        policy.setCreatedDate(ruleDTO.getCreatedDate());
        policy.setOwnerId(ruleDTO.getOwnerId());
        policy.setOwnerDisplayName(ruleDTO.getOwnerDisplayName());
        policy.setModifiedById(ruleDTO.getModifiedById());
        policy.setModifiedBy(ruleDTO.getModifiedBy());
        policy.setLastUpdatedDate(ruleDTO.getLastUpdatedDate());
        policy.setReIndexNow(ruleDTO.isReIndexNow());

        return policy;
    }

    private static void convertPQLFriendlyFilterParams(
            List<ObligationDTO> obligations) throws ConsoleException {
        for (ObligationDTO obligation : obligations) {
            parseTagFilterPQLFriendly(obligation, INSERT_TAG_FILTERS);
            parseTagFilterPQLFriendly(obligation, VIEW_TAG_FILTERS);
            parseTagFilterPQLFriendly(obligation, EDIT_TAG_FILTERS);
            parseTagFilterPQLFriendly(obligation, DELETE_TAG_FILTERS);
            parseTagFilterPQLFriendly(obligation, DEPLOY_TAG_FILTERS);

            if(!DelegationModelShortName.POLICY_MODEL_ACCESS_TAGS.name().equals(obligation.getName())) {
            	parseTagFilterPQLFriendly(obligation, MOVE_TAG_FILTERS);
            }  
        }
    }

    private static void parseTagFilterPQLFriendly(ObligationDTO obligation,
            ObligationTagType tagFilterType) throws ConsoleException {
        String viewTagFilters = obligation.getParams()
                .get(tagFilterType.name());
        if(viewTagFilters != null) {
            String pqlFriendlyString = ObligationTagsFilter
                    .toPQLFriendlyFormat(viewTagFilters);
            obligation.getParams().put(tagFilterType.name(), pqlFriendlyString);
        }
    }


    public static DelegationRuleDTO getDTO(PolicyDTO policyDTO)
            throws ConsoleException {
        DelegationRuleDTO ruleDTO = new DelegationRuleDTO();
        ruleDTO.setId(policyDTO.getId());
        ruleDTO.setName(policyDTO.getName());
        ruleDTO.setFullName(policyDTO.getFullName());
        ruleDTO.setDescription(policyDTO.getDescription());
        ruleDTO.setStatus(policyDTO.getStatus());
        ruleDTO.setCategory(DevEntityType.DELEGATION_POLICY);
        ruleDTO.setEffectType(policyDTO.getEffectType());
        ruleDTO.setVersion(policyDTO.getVersion());

        ruleDTO.setActionComponents(policyDTO.getActionComponents());
        ruleDTO.setResourceComponents(policyDTO.getFromResourceComponents());

        List<ObligationDTO> obligations = policyDTO.getAllowObligations();
        convertPQLFriendlyToJSON(obligations);
        ruleDTO.setObligations(obligations);

        ruleDTO.setCreatedDate(policyDTO.getCreatedDate());
        ruleDTO.setOwnerId(policyDTO.getOwnerId());
        ruleDTO.setOwnerDisplayName(policyDTO.getOwnerDisplayName());
        ruleDTO.setModifiedById(policyDTO.getModifiedById());
        ruleDTO.setModifiedBy(policyDTO.getModifiedBy());
        ruleDTO.setLastUpdatedDate(policyDTO.getLastUpdatedDate());

        return ruleDTO;
    }

    private static void convertPQLFriendlyToJSON(
            List<ObligationDTO> obligations) throws ConsoleException {
        for (ObligationDTO obligation : obligations) {
        	getPQLFriendlyToJSON(obligation, INSERT_TAG_FILTERS);
            getPQLFriendlyToJSON(obligation, VIEW_TAG_FILTERS);
            getPQLFriendlyToJSON(obligation, EDIT_TAG_FILTERS);
            getPQLFriendlyToJSON(obligation, DELETE_TAG_FILTERS);
            getPQLFriendlyToJSON(obligation, DEPLOY_TAG_FILTERS);

            if(!DelegationModelShortName.POLICY_MODEL_ACCESS_TAGS.name().equals(obligation.getName())) {
            	getPQLFriendlyToJSON(obligation, MOVE_TAG_FILTERS);
            }
        }
    }

    private static void getPQLFriendlyToJSON(ObligationDTO obligation,
            ObligationTagType tagFilterType) throws ConsoleException {
        String filterTagsString = obligation.getParams()
                .get(tagFilterType.name());
        if(StringUtils.isEmpty(filterTagsString)) {
            return;
        }
        
        String jsonString = ObligationTagsFilter.toJSON(filterTagsString);
        obligation.getParams().put(tagFilterType.name(), jsonString);
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

    public DevEntityType getCategory() {
        return category;
    }

    public void setCategory(DevEntityType category) {
        this.category = category;
    }

    public String getEffectType() {
        return effectType;
    }

    public void setEffectType(String effectType) {
        this.effectType = effectType;
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    public ComponentDTO getSubjectComponent() {
        return subjectComponent;
    }

    public void setSubjectComponent(ComponentDTO subjectComponent) {
        this.subjectComponent = subjectComponent;
    }

    public List<PolicyComponent> getActionComponents() {
        if (actionComponents == null) {
            actionComponents = new ArrayList<>();
        }
        return actionComponents;
    }

    public void setActionComponents(List<PolicyComponent> actionComponents) {
        this.actionComponents = actionComponents;
    }

    public List<PolicyComponent> getResourceComponents() {
        if (resourceComponents == null) {
            resourceComponents = new ArrayList<>();
        }
        return resourceComponents;
    }

    public void setResourceComponents(
            List<PolicyComponent> resourceComponents) {
        this.resourceComponents = resourceComponents;
    }

    public Set<String> getAttributes() {
        if (attributes == null) {
            attributes = new TreeSet<>();
        }
        return attributes;
    }

    public void setAttributes(Set<String> attributes) {
        this.attributes = attributes;
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

    public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public List<ObligationDTO> getObligations() {
        if (obligations == null) {
            obligations = new ArrayList<>();
        }
        return obligations;
    }

    public void setObligations(List<ObligationDTO> obligations) {
        this.obligations = obligations;
    }

    public boolean isReIndexNow() {
        return reIndexNow;
    }

    public void setReIndexNow(boolean reIndexNow) {
        this.reIndexNow = reIndexNow;
    }

}
