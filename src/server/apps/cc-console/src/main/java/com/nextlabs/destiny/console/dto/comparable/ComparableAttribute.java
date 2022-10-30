package com.nextlabs.destiny.console.dto.comparable;

import com.nextlabs.destiny.console.enums.DataType;
import com.nextlabs.destiny.console.model.policy.AttributeConfig;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class ComparableAttribute
        implements Comparable<ComparableAttribute> {

    private final String name;
    private final String shortName;
    private final DataType dataType;
    private final Set<ComparableOperator> operators;

    public ComparableAttribute(AttributeConfig attributeConfig) {
        super();
        name = attributeConfig.getName();
        shortName = attributeConfig.getShortName();
        dataType = attributeConfig.getDataType();
        operators = new TreeSet<>();
        attributeConfig.getOperatorConfigs().forEach(operatorConfig
                -> operators.add(new ComparableOperator(operatorConfig)));
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ComparableAttribute)) return false;

        ComparableAttribute that = (ComparableAttribute) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(shortName, that.shortName)
                .append(dataType, that.dataType)
                .append(operators, that.operators)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, shortName, dataType, operators);
    }

    @Override
    public int compareTo(ComparableAttribute comparableAttribute) {
        if(name != null && !name.equals(comparableAttribute.name)) {
            return name.compareTo(comparableAttribute.name);
        }
        if(shortName != null && !shortName.equals(comparableAttribute.shortName)) {
            return shortName.compareTo(comparableAttribute.shortName);
        }
        if(dataType != null && !dataType.equals(comparableAttribute.dataType)) {
            return dataType.compareTo(comparableAttribute.dataType);
        }
        if(!operators.equals(comparableAttribute.operators)) {
            return 1;
        }

        return 0;
    }
}
