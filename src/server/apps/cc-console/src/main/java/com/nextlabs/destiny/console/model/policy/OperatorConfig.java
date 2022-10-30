/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 30, 2015
 *
 */
package com.nextlabs.destiny.console.model.policy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.nextlabs.destiny.console.enums.DataType;
import com.nextlabs.destiny.console.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * Entity to manage operators and associated data types
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@Entity
@Table(name = "OPERATOR_CONFIG")
@NamedQuery(name = OperatorConfig.FIND_BY_DATA_TYPE, query = "SELECT o FROM OperatorConfig o WHERE o.dataType = :dataType ORDER BY o.label ")
public class OperatorConfig extends BaseModel
        implements Comparable<OperatorConfig> {

    private static final long serialVersionUID = -6115267310641236986L;

    public static final String FIND_BY_DATA_TYPE = "operatorConfig.findByDataType";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(hidden = true)
    private Long id;

    @Column(name = "operator_key", length = 255)
    @ApiModelProperty(value = "The arithmetic comparator value for the evaluation engine.\n"
                    + "Possible values by data type:\n" +
                    "DATE: <, >=\n" +
                    "STRING: =, !=\n" +
                    "NUMBER: =, !=, <, <=, >, >=\n" +
                    "MULTIVAL: includes, equals_unordered, =, !=",
                    allowableValues = "=, !=, <, <=, >, >=, includes, equals_unordered",
                    example = "!=",
                    required = true)
    private String key;

    @Column(name = "label", length = 255)
    @ApiModelProperty(value = "The display value of the evaluation comparator.\n"
                    + "Possible values by data type:\n" +
                    "DATE: before, on or after\n" +
                    "STRING: is, is not\n" +
                    "NUMBER: =, !=, <, <=, >, >=\n" +
                    "MULTIVAL: includes, equals_unordered, =, !=",
                    allowableValues = "before, on or after, is, is not, =, !=, <, <=, >, >=, includes, exactly matches",
                    example = "is not",
                    required = true)
    private String label;

    @Column(name = "data_type")
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(value = "The data type of evaluation comparator.")
    private DataType dataType;

    /**
     * Default Constructor
     */
    public OperatorConfig() {

    }

    /**
     * Constructor with basic input params
     * 
     * @param id
     * @param key
     * @param label
     * @param dataType
     */
    public OperatorConfig(Long id, String key, String label,
            DataType dataType) {
        this.id = id;
        this.key = key;
        this.label = label;
        this.dataType = dataType;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OperatorConfig other = (OperatorConfig) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(OperatorConfig o) {
        if (o == null || this.id == null || o.id == null)
            return -1;
        return this.id.compareTo(o.id);
    }

}
