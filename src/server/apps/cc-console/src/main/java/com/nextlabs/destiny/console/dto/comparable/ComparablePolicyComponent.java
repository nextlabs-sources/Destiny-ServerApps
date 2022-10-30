package com.nextlabs.destiny.console.dto.comparable;

import com.nextlabs.destiny.console.dto.policymgmt.PolicyComponent;
import com.nextlabs.destiny.console.enums.Operator;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class ComparablePolicyComponent
        implements Comparable<ComparablePolicyComponent> {

    private final Operator operator;
    private final Set<Long> components;

    public ComparablePolicyComponent(PolicyComponent policyComponent) {
        super();
        operator = policyComponent.getOperator();
        components = new TreeSet<>();
        policyComponent.getComponents().forEach(component
                -> components.add(component.getId()));
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ComparablePolicyComponent)) return false;

        ComparablePolicyComponent that = (ComparablePolicyComponent) o;

        return new EqualsBuilder()
                .append(operator, that.operator)
                .append(components, that.components)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, components);
    }

    @Override
    public int compareTo(ComparablePolicyComponent comparablePolicyComponent) {
        if(operator != null && !operator.equals(comparablePolicyComponent.operator)) {
            return operator.compareTo(comparablePolicyComponent.operator);
        }
        if(!components.equals(comparablePolicyComponent.components)) {
            return 1;
        }

        return 0;
    }
}
