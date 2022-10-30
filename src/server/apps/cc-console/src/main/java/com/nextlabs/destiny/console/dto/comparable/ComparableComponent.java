package com.nextlabs.destiny.console.dto.comparable;

import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class ComparableComponent
        implements Comparable<ComparableComponent> {

    private final String type;
    private final String name;
    private final String description;
    private final Set<String> actions;
    private final Set<ComparableCondition> conditions;
    private final Set<ComparableMemberCondition> memberConditions;
    private final Set<ComparableComponent> subComponents;
    private final String parentName;
    private final String folderPath;

    public ComparableComponent(ComponentDTO componentDTO) {
        super();
        type = componentDTO.getType();
        name = componentDTO.getName();
        parentName = componentDTO.getParentName();
        folderPath = componentDTO.getFolderPath();
        description = componentDTO.getDescription();
        actions = new TreeSet<>();
        actions.addAll(componentDTO.getActions());
        conditions = new TreeSet<>();
        componentDTO.getConditions().forEach(condition
                -> conditions.add(new ComparableCondition(condition)));
        memberConditions = new TreeSet<>();
        componentDTO.getMemberConditions().forEach(memberCondition
                -> memberConditions.add(new ComparableMemberCondition(memberCondition)));
        subComponents = new TreeSet<>();
        componentDTO.getSubComponents().forEach(subComponent
                -> subComponents.add(new ComparableComponent(subComponent)));
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ComparableComponent)) return false;

        ComparableComponent that = (ComparableComponent) o;

        return new EqualsBuilder()
                .append(type, that.type)
                .append(name, that.name)
                .append(parentName, that.parentName)
                .append(folderPath, that.folderPath)
                .append(description, that.description)
                .append(actions, that.actions)
                .append(conditions, that.conditions)
                .append(memberConditions, that.memberConditions)
                .append(subComponents, that.subComponents)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, parentName, folderPath, description, actions,
                conditions, memberConditions, subComponents);
    }

    @Override
    public int compareTo(ComparableComponent comparableComponent) {
        if(type != null && !type.equals(comparableComponent.type)) {
            return type.compareTo(comparableComponent.type);
        }
        if(name != null && !name.equals(comparableComponent.name)) {
            return name.compareTo(comparableComponent.name);
        }
        if(parentName != null && !parentName.equals(comparableComponent.parentName)) {
            return parentName.compareTo(comparableComponent.parentName);
        }
        if(folderPath != null && !folderPath.equals(comparableComponent.folderPath)) {
            return folderPath.compareTo(comparableComponent.folderPath);
        }
        if(description != null && !description.equals(comparableComponent.description)) {
            return description.compareTo(comparableComponent.description);
        }
        if(!actions.equals(comparableComponent.actions)
            || !conditions.equals(comparableComponent.conditions)
            || !memberConditions.equals(comparableComponent.memberConditions)
            || !subComponents.equals(comparableComponent.subComponents)) {
            return 1;
        }

        return 0;
    }
}
