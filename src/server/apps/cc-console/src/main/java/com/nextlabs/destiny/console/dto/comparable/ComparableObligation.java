package com.nextlabs.destiny.console.dto.comparable;

import com.nextlabs.destiny.console.dto.policymgmt.ObligationDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class ComparableObligation
        implements Comparable<ComparableObligation> {

    private final String name;
    private final Set<ComparableObligationParameter> parameters;

    public ComparableObligation(ObligationDTO obligationDTO) {
        super();
        name = obligationDTO.getName();
        parameters = new TreeSet<>();
        obligationDTO.getParams().entrySet().forEach(parameter
                -> parameters.add(new ComparableObligationParameter(parameter)));
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ComparableObligation)) return false;

        ComparableObligation that = (ComparableObligation) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(parameters, that.parameters)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parameters);
    }

    @Override
    public int compareTo(ComparableObligation comparableObligation) {
        if(name != null && !name.equals(comparableObligation.name)) {
            return name.compareTo(comparableObligation.name);
        }
        if(parameters.size() != comparableObligation.parameters.size()) {
            return parameters.size() - comparableObligation.parameters.size();
        }
        if(!parameters.equals(comparableObligation.parameters)) {
            return 1;
        }

        return 0;
    }
}
