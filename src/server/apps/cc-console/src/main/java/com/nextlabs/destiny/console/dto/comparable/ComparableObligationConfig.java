package com.nextlabs.destiny.console.dto.comparable;

import com.nextlabs.destiny.console.model.policy.ObligationConfig;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class ComparableObligationConfig
        implements Comparable<ComparableObligationConfig> {

    private final String name;
    private final String shortName;
    private final String runAt;
    private final Set<ComparableParameterConfig> parameters;

    public ComparableObligationConfig(ObligationConfig obligationConfig) {
        super();
        name = obligationConfig.getName();
        shortName = obligationConfig.getShortName();
        runAt = obligationConfig.getRunAt();
        parameters = new TreeSet<>();
        obligationConfig.getParameters().forEach(parameter
                -> parameters.add(new ComparableParameterConfig(parameter)));
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ComparableObligationConfig)) return false;

        ComparableObligationConfig that = (ComparableObligationConfig) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(shortName, that.shortName)
                .append(runAt, that.runAt)
                .append(parameters, that.parameters)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, shortName, runAt, parameters);
    }

    @Override
    public int compareTo(ComparableObligationConfig comparableObligation) {
        if(name != null && !name.equals(comparableObligation.name)) {
            return name.compareTo(comparableObligation.name);
        }
        if(shortName != null && !shortName.equals(comparableObligation.shortName)) {
            return shortName.compareTo(comparableObligation.shortName);
        }
        if(runAt != null && !runAt.equals(comparableObligation.runAt)) {
            return runAt.compareTo(comparableObligation.runAt);
        }
        if(!parameters.equals(comparableObligation.parameters)) {
            return 1;
        }

        return 0;
    }
}
