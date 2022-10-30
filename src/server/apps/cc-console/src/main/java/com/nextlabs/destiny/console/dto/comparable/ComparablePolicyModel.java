package com.nextlabs.destiny.console.dto.comparable;

import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.model.policy.PolicyModel;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class ComparablePolicyModel
        implements Comparable<ComparablePolicyModel> {

    private final String name;
    private final String shortName;
    private final String description;
    private final PolicyModelType type;
    private final Set<ComparableAttribute> attributes;
    private final Set<ComparableAttribute> preSeededAttributes;
    private final Set<ComparableAction> actions;
    private final Set<ComparableObligationConfig> obligations;
    private final Set<ComparableTag> tags;

    public ComparablePolicyModel(PolicyModel policyModel) {
        super();

        name = policyModel.getName();
        shortName = policyModel.getShortName();
        description = policyModel.getDescription();
        type = policyModel.getType();
        attributes = new TreeSet<>();
        policyModel.getAttributes().forEach(attributeConfig
                -> attributes.add(new ComparableAttribute(attributeConfig)));
        preSeededAttributes = new TreeSet<>();
        policyModel.getExtraSubjectAttributes().forEach(preSeededAttributeConfig
                -> preSeededAttributes.add(new ComparableAttribute(preSeededAttributeConfig)));
        actions = new TreeSet<>();
        policyModel.getActions().forEach(actionConfig
                -> actions.add(new ComparableAction(actionConfig)));
        obligations = new TreeSet<>();
        policyModel.getObligations().forEach(obligationConfig
                -> obligations.add(new ComparableObligationConfig(obligationConfig)));
        tags = new TreeSet<>();
        policyModel.getTags().forEach(tagLabel
                -> tags.add(new ComparableTag(tagLabel)));
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ComparablePolicyModel)) return false;

        ComparablePolicyModel that = (ComparablePolicyModel) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(shortName, that.shortName)
                .append(description, that.description)
                .append(type, that.type)
                .append(attributes, that.attributes)
                .append(actions, that.actions)
                .append(obligations, that.obligations)
                .append(tags, that.tags)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, shortName, description, type, attributes,
                actions, obligations, tags);
    }

    @Override
    public int compareTo(ComparablePolicyModel comparablePolicyModel) {
        if(name != null && !name.equals(comparablePolicyModel.name)) {
            return name.compareTo(comparablePolicyModel.name);
        }
        if(shortName != null && !shortName.equals(comparablePolicyModel.shortName)) {
            return shortName.compareTo(comparablePolicyModel.shortName);
        }
        if(description != null && !description.equals(comparablePolicyModel.description)) {
            return description.compareTo(comparablePolicyModel.description);
        }
        if(type != null && !type.equals(comparablePolicyModel.type)) {
            return type.compareTo(comparablePolicyModel.type);
        }
        if(!attributes.equals(comparablePolicyModel.attributes)
            || !preSeededAttributes.equals(comparablePolicyModel.preSeededAttributes)
            || !actions.equals(comparablePolicyModel.actions)
            || !obligations.equals(comparablePolicyModel.obligations)
            || !tags.equals(comparablePolicyModel.tags)) {
            return 1;
        }

        return 0;
    }
}
