package com.nextlabs.destiny.console.dto.comparable;

import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Map;
import java.util.Objects;

public class ComparableObligationParameter
        implements Comparable<ComparableObligationParameter> {

    private final String key;
    private final String value;

    public ComparableObligationParameter(Map.Entry<String, String> entry) {
        super();
        key = entry.getKey();
        value = entry.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ComparableObligationParameter)) return false;

        ComparableObligationParameter that = (ComparableObligationParameter) o;

        return new EqualsBuilder()
                .append(key, that.key)
                .append(value, that.value)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public int compareTo(ComparableObligationParameter obligationParameter) {
        if(key != null && !key.equals(obligationParameter.key)) {
            return key.compareTo(obligationParameter.key);
        }
        if(value != null && !value.equals(obligationParameter.value)) {
            return value.compareTo(obligationParameter.value);
        }

        return 0;
    }
}
