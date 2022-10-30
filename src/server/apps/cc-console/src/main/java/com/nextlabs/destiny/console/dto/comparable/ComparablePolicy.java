package com.nextlabs.destiny.console.dto.comparable;

import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.dto.policymgmt.porting.Node;
import com.nextlabs.destiny.console.enums.DevEntityType;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class ComparablePolicy
        implements Comparable<ComparablePolicy> {

    private final String folderPath;
    private final String name;
    private final String description;
    private final DevEntityType category;
    private final String effectType;
    private final String parentName;
    private final String expression;
    private Integer connectionType;
    private Integer sinceLastHeartbeat;

    private final Set<ComparableTag> tags;
    private final Set<ComparablePolicyComponent> subjects;
    private final Set<ComparablePolicyComponent> toSubjects;
    private final Set<ComparablePolicyComponent> actions;
    private final Set<ComparablePolicyComponent> resources;
    private final Set<ComparablePolicyComponent> toResources;

    private ComparableEffectiveDuration effectiveDuration;

    private final Set<ComparableObligation> allows;
    private final Set<ComparableObligation> denies;

    private final Set<String> subPolicies;

    public ComparablePolicy(PolicyDTO policyDTO, Node policyNode) {
        super();
        folderPath = policyDTO.getFolderPath();
        name = policyDTO.getName();
        description = policyDTO.getDescription();
        category = policyDTO.getCategory();
        effectType = policyDTO.getEffectType();
        parentName = policyDTO.getParentName();
        expression = policyDTO.getExpression();
        if(policyDTO.getScheduleConfig() != null) {
            effectiveDuration = new ComparableEffectiveDuration(policyDTO.getScheduleConfig());
        }
        if(policyDTO.getEnvironmentConfig() != null) {
            connectionType = policyDTO.getEnvironmentConfig().getRemoteAccess();
            sinceLastHeartbeat = policyDTO.getEnvironmentConfig().getTimeSinceLastHBSecs();
        }
        tags = new TreeSet<>();
        policyDTO.getTags().forEach(tag
                -> tags.add(new ComparableTag(tag)));
        subjects = new TreeSet<>();
        policyDTO.getSubjectComponents().forEach(subject
                -> subjects.add(new ComparablePolicyComponent(subject)));
        toSubjects = new TreeSet<>();
        policyDTO.getToSubjectComponents().forEach(toSubject
                -> toSubjects.add(new ComparablePolicyComponent(toSubject)));
        actions = new TreeSet<>();
        policyDTO.getActionComponents().forEach(action
                -> actions.add(new ComparablePolicyComponent(action)));
        resources = new TreeSet<>();
        policyDTO.getFromResourceComponents().forEach(resource
                -> resources.add(new ComparablePolicyComponent(resource)));
        toResources = new TreeSet<>();
        policyDTO.getToResourceComponents().forEach(toResource
                -> toResources.add(new ComparablePolicyComponent(toResource)));
        allows = new TreeSet<>();
        policyDTO.getAllowObligations().forEach(allow
                -> allows.add(new ComparableObligation(allow)));
        denies = new TreeSet<>();
        policyDTO.getDenyObligations().forEach(deny
                -> denies.add(new ComparableObligation(deny)));
        subPolicies = new TreeSet<>();
        if(policyNode != null) {
            policyNode.getChildren().forEach(subPolicyNode
                -> subPolicies.add(subPolicyNode.getData().getName()));
        } else {
            policyDTO.getSubPolicyRefs().forEach(reference
                -> subPolicies.add(reference.substring(reference.lastIndexOf("/") + 1)));
        }
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ComparablePolicy)) return false;

        ComparablePolicy that = (ComparablePolicy) o;

        return new EqualsBuilder()
                .append(folderPath, that.folderPath)
                .append(name, that.name)
                .append(description, that.description)
                .append(category, that.category)
                .append(effectType, that.effectType)
                .append(parentName, that.parentName)
                .append(expression, that.expression)
                .append(connectionType, that.connectionType)
                .append(sinceLastHeartbeat, that.sinceLastHeartbeat)
                .append(effectiveDuration, that.effectiveDuration)
                .append(tags, that.tags)
                .append(subjects, that.subjects)
                .append(toSubjects, that.toSubjects)
                .append(actions, that.actions)
                .append(resources, that.resources)
                .append(toResources, that.toResources)
                .append(allows, that.allows)
                .append(denies, that.denies)
                .append(subPolicies, that.subPolicies)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(folderPath, name, description, category, effectType, parentName,
                expression, connectionType, sinceLastHeartbeat, effectiveDuration, tags, subjects,
                toSubjects, actions, resources, toResources, allows, denies, subPolicies);
    }

    @Override
    public int compareTo(ComparablePolicy comparablePolicy) {
        if(folderPath != null && !folderPath.equals(comparablePolicy.folderPath)) {
            return folderPath.compareTo(comparablePolicy.folderPath);
        }
        if(name != null && !name.equals(comparablePolicy.name)) {
            return name.compareTo(comparablePolicy.name);
        }
        if(description != null && !description.equals(comparablePolicy.description)) {
            return description.compareTo(comparablePolicy.description);
        }
        if(category != null && !category.equals(comparablePolicy.category)) {
            return category.compareTo(comparablePolicy.category);
        }
        if(effectType != null && !effectType.equals(comparablePolicy.effectType)) {
            return effectType.compareTo(comparablePolicy.effectType);
        }
        if(parentName != null && !parentName.equals(comparablePolicy.parentName)) {
            return parentName.compareTo(comparablePolicy.parentName);
        }
        if(expression != null && !expression.equals(comparablePolicy.expression)) {
            return expression.compareTo(comparablePolicy.expression);
        }
        if(connectionType != null && !connectionType.equals(comparablePolicy.connectionType)) {
            return connectionType.compareTo(comparablePolicy.connectionType);
        }
        if(sinceLastHeartbeat != null && !sinceLastHeartbeat.equals(comparablePolicy.sinceLastHeartbeat)) {
            return sinceLastHeartbeat.compareTo(comparablePolicy.sinceLastHeartbeat);
        }
        if(effectiveDuration != null && !effectiveDuration.equals(comparablePolicy.effectiveDuration)) {
            return effectiveDuration.compareTo(comparablePolicy.effectiveDuration);
        }

        if(!tags.equals(comparablePolicy.tags)
            || !subjects.equals(comparablePolicy.subjects)
            || !toSubjects.equals(comparablePolicy.toSubjects)
            || !actions.equals(comparablePolicy.actions)
            || !resources.equals(comparablePolicy.resources)
            || !toResources.equals(comparablePolicy.toResources)
            || !allows.equals(comparablePolicy.allows)
            || !denies.equals(comparablePolicy.denies)
            || !subPolicies.equals(comparablePolicy.subPolicies)) {
            return 1;
        }

        return 0;
    }
}
