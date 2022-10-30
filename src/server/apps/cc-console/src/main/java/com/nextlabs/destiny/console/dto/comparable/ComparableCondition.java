package com.nextlabs.destiny.console.dto.comparable;

import com.nextlabs.destiny.console.dto.policymgmt.ComponentConditionDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Objects;

public class ComparableCondition
        implements Comparable<ComparableCondition> {

    private final String attribute;
    private final String operator;
    private final String value;
    private final String rhsType;
    private final String rhsValue;

    public ComparableCondition(ComponentConditionDTO componentConditionDTO) {
        super();
        attribute = componentConditionDTO.getAttribute();
        operator = componentConditionDTO.getOperator();
        value = componentConditionDTO.getValue();
        rhsType = componentConditionDTO.getRhsType();
        rhsValue = componentConditionDTO.getRHSValue();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ComparableCondition)) return false;

        ComparableCondition that = (ComparableCondition) o;

        return new EqualsBuilder()
                .append(attribute, that.attribute)
                .append(operator, that.operator)
                .append(value, that.value)
                .append(rhsType, that.rhsType)
                .append(rhsValue, that.rhsValue)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(attribute, operator, value, rhsType, rhsValue);
    }

    @Override
    public int compareTo(ComparableCondition comparableCondition) {
        if(attribute!= null && !attribute.equals(comparableCondition.attribute)) {
            return attribute.compareTo(comparableCondition.attribute);
        }
        if(operator != null && !operator.equals(comparableCondition.operator)) {
            return operator.compareTo(comparableCondition.operator);
        }
        if(value != null && !value.equals(comparableCondition.value)) {
            return value.compareTo(comparableCondition.value);
        }
        if(rhsType != null && !rhsType.equals(comparableCondition.rhsType)) {
            return rhsType.compareTo(comparableCondition.rhsType);
        }
        if(rhsValue != null && !rhsValue.equals(comparableCondition.rhsValue)) {
            return rhsValue.compareTo(comparableCondition.rhsValue);
        }

        return 0;
    }
}
