package com.nextlabs.destiny.console.dto.comparable;

import com.nextlabs.destiny.console.enums.DataType;
import com.nextlabs.destiny.console.model.policy.OperatorConfig;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Objects;

public class ComparableOperator
        implements Comparable<ComparableOperator> {

    private final DataType dataType;
    private final String key;
    private final String label;

    public ComparableOperator(OperatorConfig operatorConfig) {
        super();
        dataType = operatorConfig.getDataType();
        key = operatorConfig.getKey();
        label = operatorConfig.getLabel();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ComparableOperator)) return false;

        ComparableOperator that = (ComparableOperator) o;

        return new EqualsBuilder()
                .append(dataType, that.dataType)
                .append(key, that.key)
                .append(label, that.label)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataType, key, label);
    }

    @Override
    public int compareTo(ComparableOperator comparableOperator) {
        if(dataType != null && !dataType.equals(comparableOperator.dataType)) {
            return dataType.compareTo(comparableOperator.dataType);
        }
        if(key != null && !key.equals(comparableOperator.key)) {
            return key.compareTo(comparableOperator.key);
        }
        if(label != null && !label.equals(comparableOperator.label)) {
            return label.compareTo(comparableOperator.label);
        }

        return 0;
    }
}
