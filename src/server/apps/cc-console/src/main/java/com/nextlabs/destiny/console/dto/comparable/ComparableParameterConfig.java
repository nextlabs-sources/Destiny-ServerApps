package com.nextlabs.destiny.console.dto.comparable;

import com.nextlabs.destiny.console.enums.ObligationParameterDataType;
import com.nextlabs.destiny.console.model.policy.ParameterConfig;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Objects;

public class ComparableParameterConfig
        implements Comparable<ComparableParameterConfig> {

    private final String name;
    private final String shortName;
    private final ObligationParameterDataType type;
    private final String defaultValue;
    private final String listValues;
    private final Boolean hidden;
    private final Boolean editable;
    private final Boolean mandatory;

    public ComparableParameterConfig(ParameterConfig parameterConfig) {
        super();
        name = parameterConfig.getName();
        shortName = parameterConfig.getShortName();
        type = parameterConfig.getType();
        defaultValue = parameterConfig.getDefaultValue();
        listValues = parameterConfig.getListValues();
        hidden = parameterConfig.isHidden();
        editable = parameterConfig.isEditable();
        mandatory = parameterConfig.isMandatory();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ComparableParameterConfig)) return false;

        ComparableParameterConfig that = (ComparableParameterConfig) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(shortName, that.shortName)
                .append(type, that.type)
                .append(defaultValue, that.defaultValue)
                .append(listValues, that.listValues)
                .append(hidden, that.hidden)
                .append(editable, that.editable)
                .append(mandatory, that.mandatory)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, shortName, type, defaultValue, listValues,
                hidden, editable, mandatory);
    }

    @Override
    public int compareTo(ComparableParameterConfig comparableParameterConfig) {
        if(name != null && !name.equals(comparableParameterConfig.name)) {
            return name.compareTo(comparableParameterConfig.name);
        }
        if(shortName != null && !shortName.equals(comparableParameterConfig.shortName)) {
            return shortName.compareTo(comparableParameterConfig.shortName);
        }
        if(type != null && !type.equals(comparableParameterConfig.type)) {
            return type.compareTo(comparableParameterConfig.type);
        }
        if(defaultValue != null && !defaultValue.equals(comparableParameterConfig.defaultValue)) {
            return defaultValue.compareTo(comparableParameterConfig.defaultValue);
        }
        if(listValues != null && !listValues.equals(comparableParameterConfig.listValues)) {
            return listValues.compareTo(comparableParameterConfig.listValues);
        }
        if(hidden != null && !hidden.equals(comparableParameterConfig.hidden)) {
            return hidden.compareTo(comparableParameterConfig.hidden);
        }
        if(editable != null && !editable.equals(comparableParameterConfig.editable)) {
            return editable.compareTo(comparableParameterConfig.editable);
        }
        if(mandatory != null && !mandatory.equals(comparableParameterConfig.mandatory)) {
            return mandatory.compareTo(comparableParameterConfig.mandatory);
        }

        return 0;
    }
}
