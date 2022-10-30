package com.nextlabs.destiny.console.dto.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * DTO to return values with very light weight mode.
 *
 * @author Sachindra Dasun
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LiteDTO implements Serializable, Comparable<LiteDTO> {

    public static final String SHORT_NAME = "shortName";
    public static final String TYPE = "type";
    public static final String DATA_TYPE = "dataType";
    public static final String ATTRIBUTES = "attributes";
    public static final String DATA = "data";
    public static final String KEY = "key";
    public static final String LABEL = "label";
    public static final String OPERATOR_CONFIGS = "operatorConfigs";
    public static final String POLICY_MODEL_ID = "policy_model_id";
    public static final String POLICY_MODEL_NAME = "policy_model_name";
    public static final String OBLIGATIONS = "obligations";
    public static final String LAST_UPDATED_DATE = "lastUpdatedDate";
    public static final String VALUE = "value";
    public static final String DEFAULT_VALUE = "defaultValue";
    public static final String ACTIONS = "actions";
    public static final String PARAMETERS = "parameters";
    public static final String EDITABLE = "editable";
    public static final String MANDATORY = "mandatory";
    public static final String LIST_VALUES = "listValues";
    public static final String HIDDEN = "hidden";
    public static final String SORT_ORDER = "sortOrder";
    public static final String REG_EX_PATTERN = "regExPattern";

    private static final long serialVersionUID = -6970993953532979774L;
    private Long id;
    private String name;
    private HashMap<String, Object> properties;
    private boolean empty;

    public LiteDTO() {
    }

    public LiteDTO(Long id, String name) {
        this.id = id;
        this.name = name;
        properties = new HashMap<>();
    }

    public LiteDTO put(String name, Object value) {
        properties.put(name, value);
        return this;
    }

    public Long getId() {
        return id;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public int compareTo(LiteDTO o) {
        if (o == null) {
            return 0;
        }
        return this.getName().compareToIgnoreCase(o.getName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
