/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide. Created on Feb 11, 2016
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ImmutableMap;
import com.nextlabs.destiny.console.dto.Auditable;
import com.nextlabs.destiny.console.dto.Authorizable;
import com.nextlabs.destiny.console.dto.common.AgentDTO;
import com.nextlabs.destiny.console.dto.common.BaseDTO;
import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.dto.policymgmt.policyworkflow.EntityWorkflowRequestDTO;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.utils.JsonUtil;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 *
 * DTO for Policy
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class PolicyDTO extends BaseDTO implements Auditable, Authorizable {

    private static final long serialVersionUID = 5121358474807911844L;
    public static final String POLICY_EXCEPTION_ATTR = "POLICY_EXCEPTION";
    public static final String POLICY_TRUE_ALLOW_ATTR = "TRUE_ALLOW";

    private Long folderId;
    @NotBlank
    @Size(min = 1, max = 255)
    private String name;
    private String fullName;
    private String description;
    @NotBlank
    @Pattern(regexp = "DRAFT|DEPLOYED")
    private String status;
    private DevEntityType category;
    @NotBlank
    @Pattern(regexp = "ALLOW|DENY")
    private String effectType;
    private Set<TagDTO> tags;

    private Long parentId;
    private String parentName;
    private boolean hasParent;
    private boolean hasSubPolicies;

    private List<PolicyComponent> subjectComponents;
    private boolean hasToSubjectComponents;
    private List<PolicyComponent> toSubjectComponents;
    private List<PolicyComponent> actionComponents;
    private List<PolicyComponent> fromResourceComponents;
    private boolean hasToResourceComponents;
    private List<PolicyComponent> toResourceComponents;

    private PolicyEnvironmentConfigDTO environmentConfig;
    private PolicyScheduleConfigDTO scheduleConfig;

    private String expression;

    private List<ObligationDTO> allowObligations;
    private List<ObligationDTO> denyObligations;

    private boolean subPolicy;
    private List<String> subPolicyRefs;
    private Set<String> attributes;

    private long deploymentTime;
    private boolean deployed;
    private String actionType;
    private int revisionCount;

    private long ownerId;
    private String ownerDisplayName;
    private long createdDate;

    private long modifiedById;
    private String modifiedBy;
    private long lastUpdatedDate;

    private boolean skipValidate;
    private boolean reIndexNow = true;

    @JsonIgnore
    private boolean skipIndexing;
    private boolean skipAddingTrueAllowAttribute;

    private int version;

    private List<GrantedAuthority> authorities;
    private boolean manualDeploy;
    private List<AgentDTO> deploymentTargets;
    private DeploymentRequestDTO deploymentRequest;
    private String folderPath;

    private EntityWorkflowRequestDTO activeWorkflowRequest;

    /**
     * Create Json string of this object for auditing purpose.
     */
    public String toAuditString() throws ConsoleException {
        try {
            Map<String, Object> audit = new LinkedHashMap<>();

            audit.put("Name", this.name);
            audit.put("Description", this.description);
            audit.put("Effect", this.effectType);

            if (this.tags != null && !this.tags.isEmpty()) {
                List<String> tagNames = new ArrayList<>();

                for (TagDTO tag : this.tags) {
                    tagNames.add(tag.getLabel());
                }

                audit.put("Tags", StringUtils.join(tagNames, ", "));
            } else {
                audit.put("Tags", null);
            }

            if (this.subjectComponents != null && !this.subjectComponents.isEmpty()) {
                List<Map<String, Object>> subjects = new ArrayList<>();

                for (PolicyComponent subjectComponent : this.subjectComponents) {
                    Map<String, Object> policyComponentDetails = new LinkedHashMap<>();
                    List<Map<String, Object>> componentDetailsList = new ArrayList<>();
                    policyComponentDetails.put("Operator", subjectComponent.getOperator().name());

                    for (ComponentDTO component : subjectComponent.getComponents()) {
                        Map<String, Object> componentDetails = new LinkedHashMap<>();

                        componentDetails.put("Component ID", component.getId());
                        componentDetails.put("Name", component.getName());

                        componentDetailsList.add(componentDetails);
                    }
                    policyComponentDetails.put("Subjects", componentDetailsList);
                    subjects.add(policyComponentDetails);
                }

                audit.put("Subject Components", subjects);
            } else {
                audit.put("Subject Components", null);
            }

            if (this.toSubjectComponents != null && !this.toSubjectComponents.isEmpty()) {
                List<Map<String, Object>> subjects = new ArrayList<>();

                for (PolicyComponent subjectComponent : this.toSubjectComponents) {
                    Map<String, Object> policyComponentDetails = new LinkedHashMap<>();
                    List<Map<String, Object>> componentDetailsList = new ArrayList<>();
                    policyComponentDetails.put("Operator", subjectComponent.getOperator().name());

                    for (ComponentDTO component : subjectComponent.getComponents()) {
                        Map<String, Object> componentDetails = new LinkedHashMap<>();

                        componentDetails.put("Component ID", component.getId());
                        componentDetails.put("Name", component.getName());

                        componentDetailsList.add(componentDetails);
                    }
                    policyComponentDetails.put("Subjects", componentDetailsList);
                    subjects.add(policyComponentDetails);
                }

                audit.put("To Subject Components", subjects);
            } else {
                audit.put("To Subject Components", null);
            }

            if (this.fromResourceComponents != null && !this.fromResourceComponents.isEmpty()) {
                List<Map<String, Object>> resources = new ArrayList<>();

                for (PolicyComponent resourceComponent : this.fromResourceComponents) {
                    Map<String, Object> policyComponentDetails = new LinkedHashMap<>();
                    List<Map<String, Object>> componentDetailsList = new ArrayList<>();
                    policyComponentDetails.put("Operator", resourceComponent.getOperator().name());

                    for (ComponentDTO component : resourceComponent.getComponents()) {
                        Map<String, Object> componentDetails = new LinkedHashMap<>();

                        componentDetails.put("Component ID", component.getId());
                        componentDetails.put("Name", component.getName());

                        componentDetailsList.add(componentDetails);
                    }
                    policyComponentDetails.put("Resources", componentDetailsList);
                    resources.add(policyComponentDetails);
                }

                audit.put("From Resource Components", resources);
            } else {
                audit.put("From Resource Components", null);
            }

            if (this.toResourceComponents != null && !this.toResourceComponents.isEmpty()) {
                List<Map<String, Object>> resources = new ArrayList<>();

                for (PolicyComponent resourceComponent : this.toResourceComponents) {
                    Map<String, Object> policyComponentDetails = new LinkedHashMap<>();
                    List<Map<String, Object>> componentDetailsList = new ArrayList<>();
                    policyComponentDetails.put("Operator", resourceComponent.getOperator().name());

                    for (ComponentDTO component : resourceComponent.getComponents()) {
                        Map<String, Object> componentDetails = new LinkedHashMap<>();

                        componentDetails.put("Component ID", component.getId());
                        componentDetails.put("Name", component.getName());

                        componentDetailsList.add(componentDetails);
                    }
                    policyComponentDetails.put("Resources", componentDetailsList);
                    resources.add(policyComponentDetails);
                }

                audit.put("To Resource Components", resources);
            } else {
                audit.put("To Resource Components", null);
            }

            if (this.actionComponents != null && !this.actionComponents.isEmpty()) {
                List<Map<String, Object>> actions = new ArrayList<>();

                for (PolicyComponent actionComponent : this.actionComponents) {
                    if (actionComponent.getComponents() == null
                                    || actionComponent.getComponents().isEmpty()) {
                        continue;
                    }

                    for (ComponentDTO component : actionComponent.getComponents()) {
                        Map<String, Object> actionDetails = new LinkedHashMap<>();

                        actionDetails.put("Component ID", component.getId());
                        actionDetails.put("Name", component.getName());

                        actions.add(actionDetails);
                    }
                }

                audit.put("Action Components", actions);
            } else {
                audit.put("Action Components", null);
            }

            if (this.allowObligations != null && !this.allowObligations.isEmpty()) {
                List<Map<String, Object>> obligations = new ArrayList<>();

                for (ObligationDTO obligation : this.allowObligations) {
                    Map<String, Object> obligationDetails = new LinkedHashMap<>();

                    obligationDetails.put("Component Type ID", obligation.getPolicyModelId());
                    obligationDetails.put("Name", obligation.getName());

                    if (obligation.getParams() != null && obligation.getParams().size() > 0) {
                        obligationDetails.put("Parameters", obligation.getParams());
                    }

                    obligations.add(obligationDetails);
                }

                audit.put("Obligations on Allow", obligations);
            } else {
                audit.put("Obligations on Allow", null);
            }

            if (this.denyObligations != null && !this.denyObligations.isEmpty()) {
                List<Map<String, Object>> obligations = new ArrayList<>();

                for (ObligationDTO obligation : this.denyObligations) {
                    Map<String, Object> obligationDetails = new LinkedHashMap<>();

                    obligationDetails.put("Component Type ID", obligation.getPolicyModelId());
                    obligationDetails.put("Name", obligation.getName());

                    if (obligation.getParams() != null && !obligation.getParams().isEmpty()) {
                        obligationDetails.put("Parameters", obligation.getParams());
                    }

                    obligations.add(obligationDetails);
                }

                audit.put("Obligations on Deny", obligations);
            } else {
                audit.put("Obligations on Deny", null);
            }

            if (this.scheduleConfig != null) {
                Map<String, Object> schedule = new LinkedHashMap<>();

                schedule.put("Start Date Time", this.scheduleConfig.getStartDateTime());
                schedule.put("End Date Time", this.scheduleConfig.getEndDateTime());
                schedule.put("Recurrence Start Time", this.scheduleConfig.getRecurrenceStartTime());
                schedule.put("Recurrence End Time", this.scheduleConfig.getRecurrenceEndTime());
                schedule.put("Recurrence Date of Month",
                                this.scheduleConfig.getRecurrenceDateOfMonth());
                schedule.put("Recurrence Day in Month",
                                this.scheduleConfig.getRecurrenceDayInMonth());
                schedule.put("Sunday", this.scheduleConfig.isSunday());
                schedule.put("Monday", this.scheduleConfig.isMonday());
                schedule.put("Tuesday", this.scheduleConfig.isTuesday());
                schedule.put("Wednesday", this.scheduleConfig.isWednesday());
                schedule.put("Thursday", this.scheduleConfig.isThursday());
                schedule.put("Friday", this.scheduleConfig.isFriday());
                schedule.put("Saturday", this.scheduleConfig.isSaturday());

                audit.put("Schedule", schedule);
            } else {
                audit.put("Schedule", null);
            }

            if (this.subPolicyRefs != null && !this.subPolicyRefs.isEmpty()) {
                audit.put("Sub Policies", StringUtils.join(this.subPolicyRefs, ", "));
            }

            if (this.attributes != null && !this.attributes.isEmpty()) {
                audit.put("Attributes", StringUtils.join(this.attributes, ", "));
            }

            audit.put("Manual Deploy", this.manualDeploy);
            if (manualDeploy) {
                List<Map<String, Object>> targets = this.deploymentTargets.stream()
                                .map(agent -> ImmutableMap.<String, Object>builder()
                                                .put("Agent ID", agent.getId())
                                                .put("Host", agent.getHost())
                                                .put("Type", agent.getType()).build())
                                .collect(Collectors.toList());
                audit.put("Deployment Targets", targets.isEmpty() ? null : targets);
            }

            if (this.deploymentRequest != null) {
                audit.put("Deployment Request", ImmutableMap.of("Push", deploymentRequest.isPush(),
                                "Deployment Time", deploymentRequest.getDeploymentTime()));
            }

            if (this.activeWorkflowRequest != null) {
                Map<String, Object> workflowRequestAudit = new HashMap<>();
                workflowRequestAudit.put("Status", this.activeWorkflowRequest.getStatus());
                workflowRequestAudit.put("Owner ID", this.activeWorkflowRequest.getOwnerId());
                workflowRequestAudit.put("Owner Display Name", this.activeWorkflowRequest.getOwnerDisplayName());
                List<Map<String, Object>> workflowRequestLevelAudit = this.activeWorkflowRequest.getWorkflowRequestLevels()
                        .parallelStream()
                        .map(workflowRequestLevelDTO -> ImmutableMap.<String, Object>builder()
                                .put("Workflow Level Name", workflowRequestLevelDTO.getWorkflowLevelName())
                                .put("Workflow Level Order", workflowRequestLevelDTO.getLevelOrder())
                                .put("Workflow Level Status", workflowRequestLevelDTO.getStatus())
                                .put("Approved By ID", Objects.toString(workflowRequestLevelDTO.getApprovedById(), ""))
                                .put("Approved By", Objects.toString(workflowRequestLevelDTO.getApprovedBy(), ""))
                                .put("Approved Date", Objects.toString(workflowRequestLevelDTO.getApprovedDate(), ""))
                                .put("Created Date", workflowRequestLevelDTO.getCreatedDate())
                                .put("Last Modified Date", workflowRequestLevelDTO.getLastUpdatedDate())
                                .build())
                        .collect(Collectors.toList());
                workflowRequestAudit.put("Workflow Levels", workflowRequestLevelAudit);
                workflowRequestAudit.put("Created Date", this.activeWorkflowRequest.getCreatedDate());
                workflowRequestAudit.put("Last Modified Date", this.activeWorkflowRequest.getLastUpdatedDate());
                audit.put("Active Workflow Request", workflowRequestAudit);
            }

            audit.put("Deployment Time", this.deploymentTime);
            audit.put("Deployed", this.deployed);
            audit.put("Folder Path", this.folderPath);

            return JsonUtil.toJsonString(audit);
        } catch (Exception e) {
            throw new ConsoleException(e);
        }
    }

    @ApiModelProperty(value = "The id of the folder to which this policy belongs.", position = 30,
                    example = "2")
    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    @ApiModelProperty(value = "The name of the policy.", position = 10, example = "Sample Policy",
                    required = true)
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(value = "The full name of the policy.", position = 20,
                    example = "ROOT_87/Sample Policy")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @ApiModelProperty(value = "The description of the policy.", position = 50,
                    example = "This is a sample policy")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ApiModelProperty(value = "Indicates the current status of the policy.", example = "DRAFT",position = 60,
            allowableValues = "DRAFT, APPROVED, DELETED", required = true)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @ApiModelProperty(
                    value = "The intended consequence of this policy, for example, Allow or Deny.",
                    position = 80, required = true)
    public String getEffectType() {
        return effectType;
    }

    public void setEffectType(String effectType) {
        this.effectType = effectType;
    }

    @ApiModelProperty(
                    value = "Indicates the date at which this policy was last modified. The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC(coordinated universal time). For example, November 15, 2019 10:48:49.296 AM will be written as 1573786129296.",
                    position = 310, example = "1573786129296")
    public long getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(long lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    @ApiModelProperty(
                    value = "Indicates the date at which this policy was created. The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC(coordinated universal time). For example, November 15, 2019 10:48:49.296 AM will be written as 1573786129296.",
                    position = 300, example = "1573786129296")
    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    @ApiModelProperty(value = "Indicates if this policy is a sub-policy of another policy.",
                    position = 100)
    public boolean isHasParent() {
        return hasParent;
    }

    public void setHasParent(boolean hasParent) {
        this.hasParent = hasParent;
    }

    @ApiModelProperty(value = "The ID of the parent policy.", position = 110, example = "80")
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @ApiModelProperty(value = "Parent policy's name.", position = 120,
                    example = "Parent Sample Policy")
    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    @ApiModelProperty(value = "Indicates if this policy has sub-policies.", position = 130)
    public boolean isHasSubPolicies() {
        return hasSubPolicies;
    }

    public void setHasSubPolicies(boolean hasSubPolicies) {
        this.hasSubPolicies = hasSubPolicies;
    }

    @ApiModelProperty(value = "List of tags associated with this policy.", position = 90)
    public Set<TagDTO> getTags() {
        if (tags == null) {
            tags = new TreeSet<>();
        }
        return tags;
    }

    public void setTags(Set<TagDTO> tags) {
        this.tags = tags;
    }

    @ApiModelProperty(value = "List of Subject components associated with this policy.",
                    position = 140)
    public List<PolicyComponent> getSubjectComponents() {
        if (subjectComponents == null) {
            subjectComponents = new ArrayList<>();
        }
        return subjectComponents;
    }

    public void setSubjectComponents(List<PolicyComponent> subjectComponents) {
        this.subjectComponents = subjectComponents;
    }

    @ApiModelProperty(value = "Indicates if the policy has a recipient subject.",
                    allowableValues = "true,false", position = 150)
    public boolean isHasToSubjectComponents() {
        return hasToSubjectComponents;
    }

    public void setHasToSubjectComponents(boolean hasToSubjectComponents) {
        this.hasToSubjectComponents = hasToSubjectComponents;
    }

    @ApiModelProperty(value = "List of recipient subject components associated with this policy.",
                    position = 160)
    public List<PolicyComponent> getToSubjectComponents() {
        if (toSubjectComponents == null) {
            toSubjectComponents = new ArrayList<>();
        }
        return toSubjectComponents;
    }

    public void setToSubjectComponents(List<PolicyComponent> toSubjectComponents) {
        this.toSubjectComponents = toSubjectComponents;
    }

    @ApiModelProperty(value = "List of actions associated with this policy.", position = 170)
    public List<PolicyComponent> getActionComponents() {
        if (actionComponents == null) {
            actionComponents = new ArrayList<>();
        }
        return actionComponents;
    }

    public void setActionComponents(List<PolicyComponent> actionComponents) {
        this.actionComponents = actionComponents;
    }

    @ApiModelProperty(value = "List of resource components associated with this policy.",
                    position = 180)
    public List<PolicyComponent> getFromResourceComponents() {
        if (fromResourceComponents == null) {
            fromResourceComponents = new ArrayList<>();
        }
        return fromResourceComponents;
    }

    public void setFromResourceComponents(List<PolicyComponent> fromResourceComponents) {
        this.fromResourceComponents = fromResourceComponents;
    }

    @ApiModelProperty(value = "Indicates if the policy has list of target resources.",
                    position = 190)
    public boolean isHasToResourceComponents() {
        return hasToResourceComponents;
    }

    public void setHasToResourceComponents(boolean hasToResourceComponents) {
        this.hasToResourceComponents = hasToResourceComponents;
    }

    @ApiModelProperty(
                    value = "List of target resource components(moved, renamed, or copied) associated with this policy.",
                    position = 200)
    public List<PolicyComponent> getToResourceComponents() {
        if (toResourceComponents == null) {
            toResourceComponents = new ArrayList<>();
        }
        return toResourceComponents;
    }

    public void setToResourceComponents(List<PolicyComponent> toResourceComponents) {
        this.toResourceComponents = toResourceComponents;
    }

    @ApiModelProperty(position = 210)
    public PolicyEnvironmentConfigDTO getEnvironmentConfig() {
        return environmentConfig;
    }

    public void setEnvironmentConfig(PolicyEnvironmentConfigDTO environmentConfig) {
        this.environmentConfig = environmentConfig;
    }

    @ApiModelProperty(value = "The frequency configuration at which the policy recurs.",
                    position = 220)
    public PolicyScheduleConfigDTO getScheduleConfig() {
        return scheduleConfig;
    }

    public void setScheduleConfig(PolicyScheduleConfigDTO scheduleConfig) {
        this.scheduleConfig = scheduleConfig;
    }

    @ApiModelProperty(value = "A condition expression that enhances policy evaluation.",
                    position = 230, 
                    example = "(resource.sample_resource.created_by != user.user_id AND resource.sample_resource.assigned_to != user.user_id)")
    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @ApiModelProperty(
                    value = "Tasks the PEP (Policy Enforcement Point) needs to perform if policy evaluation results in allowing access.",
                    position = 240)
    public List<ObligationDTO> getAllowObligations() {
        if (allowObligations == null) {
            allowObligations = new ArrayList<>();
        }
        return allowObligations;
    }

    public void setAllowObligations(List<ObligationDTO> allowObligations) {
        this.allowObligations = allowObligations;
    }

    @ApiModelProperty(
                    value = "Tasks the PEP (Policy Enforcement Point) needs to perform if policy evaluation results in denying access.",
                    position = 250)
    public List<ObligationDTO> getDenyObligations() {
        if (denyObligations == null) {
            denyObligations = new ArrayList<>();
        }
        return denyObligations;
    }

    public void setDenyObligations(List<ObligationDTO> denyObligations) {
        this.denyObligations = denyObligations;
    }

    @ApiModelProperty(value = "Indicates if this policy is a sub-policy of another policy.",
                    position = 130)
    public boolean isSubPolicy() {
        return subPolicy;
    }

    public void setSubPolicy(boolean subPolicy) {
        this.subPolicy = subPolicy;
    }

    @ApiModelProperty(position = 135)
    public List<String> getSubPolicyRefs() {
        if (subPolicyRefs == null) {
            subPolicyRefs = new ArrayList<>();
        }
        return subPolicyRefs;
    }

    public void setSubPolicyRefs(List<String> subPolicyRefs) {
        this.subPolicyRefs = subPolicyRefs;
    }

    @ApiModelProperty(value = "Indicates whether the policy is deployed or not.", position = 300)
    public boolean isDeployed() {
        return deployed;
    }

    @ApiModelProperty(position = 310)
    public long getDeploymentTime() {
        return deploymentTime;
    }

    public void setDeploymentTime(long deploymentTime) {
        this.deploymentTime = deploymentTime;
    }

    public void setDeployed(boolean deployed) {
        this.deployed = deployed;
    }

    @ApiModelProperty(
                    value = "Indicates whether the policy is scheduled for deployment on a future date and time.",
                    position = 320)
    public boolean isDeploymentPending() {
        return this.deploymentTime > 0 && this.deploymentTime > Instant.now().toEpochMilli();
    }

    @ApiModelProperty(value = "Indicates whether the the policy is to be deployed or undeployed.\n" +
            "<ul>" +
            "<li><strong>DE</strong>: Deploy</li>" +
            "<li><strong>UN</strong>: Undeploy</li>" +
            "</ul>",
            position = 325)
    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        if (StringUtils.isNotEmpty(actionType) && actionType.equals("DE")) {
            this.deployed = true;
        }
        this.actionType = actionType;
    }

    @ApiModelProperty(value = "Number of times the policy is deployed.", position = 340)
    public int getRevisionCount() {
        return revisionCount;
    }

    public void setRevisionCount(int revisionCount) {
        this.revisionCount = revisionCount;
    }

    @ApiModelProperty(position = 330, value = "ID of the user who created the policy.")
    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    @ApiModelProperty(position = 335, value = "Display name of the user who created the policy.")
    public String getOwnerDisplayName() {
        return ownerDisplayName;
    }

    public void setOwnerDisplayName(String ownerDisplayName) {
        this.ownerDisplayName = ownerDisplayName;
    }

    @ApiModelProperty(position = 345, value = "ID of the user who last modified the policy.")
    public long getModifiedById() {
        return modifiedById;
    }

    public void setModifiedById(long modifiedById) {
        this.modifiedById = modifiedById;
    }

    @ApiModelProperty(position = 350, value = "Display name of the user who last modified the policy.")
    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @ApiModelProperty(position = 355, hidden = true)
    public boolean isSkipValidate() {
        return skipValidate;
    }

    public void setSkipValidate(boolean skipValidate) {
        this.skipValidate = skipValidate;
    }

    @ApiModelProperty(position = 360, hidden = true)
    public boolean isSkipIndexing() {
        return skipIndexing;
    }

    public void setSkipIndexing(boolean skipIndexing) {
        this.skipIndexing = skipIndexing;
    }

    public boolean isSkipAddingTrueAllowAttribute() {
        return skipAddingTrueAllowAttribute;
    }

    public void setSkipAddingTrueAllowAttribute(boolean skipAddingTrueAllowAttribute) {
        this.skipAddingTrueAllowAttribute = skipAddingTrueAllowAttribute;
    }

    @ApiModelProperty(position = 280)
    public Set<String> getAttributes() {
        if (attributes == null) {
            attributes = new TreeSet<>();
        }
        return attributes;
    }

    public void setAttributes(Set<String> attributes) {
        this.attributes = attributes;
    }

    @ApiModelProperty(
                    value = "The category can have the following values: "
                            + "\n<ul><li><strong>FO</strong>: Folder</li>"
                            + "<li><strong>PO</strong>: Policy</li>"
                            + "<li><strong>CO</strong>: Component</li>"
                            + "<li><strong>DP</strong>: Delegation Policy</li>"
                            + "<li><strong>DC</strong>: Delegation Component</li></ul>",
                    position = 70, example = "PO")
    public DevEntityType getCategory() {
        return category;
    }

    public void setCategory(DevEntityType category) {
        this.category = category;
    }

    @ApiModelProperty(position = 365, hidden = true)
    public boolean isReIndexNow() {
        return reIndexNow;
    }

    public void setReIndexNow(boolean reIndexNow) {
        this.reIndexNow = reIndexNow;
    }

    @ApiModelProperty(position = 480, example = "2")
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @ApiModelProperty(position = 440, value = "Access management permissions assigned to this policy. Mainly used in Control Center Console UI.")
    public List<GrantedAuthority> getAuthorities() {
        if (this.authorities == null) {
            this.authorities = new ArrayList<>();
        }
        return authorities;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @ApiModelProperty(position = 450)
    public boolean isManualDeploy() {
        return manualDeploy;
    }

    public void setManualDeploy(boolean manualDeploy) {
        this.manualDeploy = manualDeploy;
    }

    @ApiModelProperty(position = 460)
    public List<AgentDTO> getDeploymentTargets() {
        if (deploymentTargets == null) {
            deploymentTargets = new ArrayList<>();
        }
        return deploymentTargets;
    }

    public void setDeploymentTargets(List<AgentDTO> deploymentTargets) {
        this.deploymentTargets = deploymentTargets;
    }

    @ApiModelProperty(position = 470)
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

    @ApiModelProperty(value = "The path of the folder to which this policy belongs.", position = 40,
                    example = "folder/sub-folder")
    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    @ApiModelProperty(position = 480)
    public EntityWorkflowRequestDTO getActiveWorkflowRequest() {
        return activeWorkflowRequest;
    }

    public void setActiveWorkflowRequest(EntityWorkflowRequestDTO activeWorkflowRequestDto) {
        this.activeWorkflowRequest = activeWorkflowRequestDto;
    }
}
