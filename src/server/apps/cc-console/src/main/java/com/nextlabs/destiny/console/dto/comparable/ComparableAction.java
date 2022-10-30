package com.nextlabs.destiny.console.dto.comparable;

import com.nextlabs.destiny.console.model.policy.ActionConfig;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Objects;

public class ComparableAction
        implements Comparable<ComparableAction> {

    private final String name;
    private final String shortName;

    public ComparableAction(ActionConfig actionConfig) {
        super();
        name = actionConfig.getName();
        shortName = actionConfig.getShortName();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ComparableAction)) return false;

        ComparableAction that = (ComparableAction) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(shortName, that.shortName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, shortName);
    }

    @Override
    public int compareTo(ComparableAction comparableAction) {
        if(name != null && !name.equals(comparableAction.name)) {
            return name.compareTo(comparableAction.name);
        }
        if(shortName != null && !shortName.equals(comparableAction.shortName)) {
            return shortName.compareTo(comparableAction.shortName);
        }

        return 0;
    }
}
