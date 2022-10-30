/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 14, 2016
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
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.nextlabs.destiny.console.enums.ObligationParameterDataType;
import com.nextlabs.destiny.console.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * Obligation parameter configuration entity
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Entity
@Table(name = "PM_PARAMETER_CONFIG")
public class ParameterConfig extends BaseModel
        implements Comparable<ParameterConfig> {

    private static final long serialVersionUID = -8436226604581544074L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(hidden = true)
    private Long id;

    @Column(name = "name", length = 255)
    @ApiModelProperty(value = "The name of the obligation parameter.",
                    example = "Assigned To",
                    required = true)
    private String name;

    @Column(name = "short_name", length = 50)
    @ApiModelProperty(value = "The unique code/identifier of the obligation parameter.",
                    example = "assigned_to",
                    required = true)
    private String shortName;

    @Column(name = "data_type")
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(value = "The obligation data type of the parameter.",
                    required = true)
    private ObligationParameterDataType type;

    @Column(name = "default_value", length = 1000)
    @ApiModelProperty(value = "The default value to be populated when the obligation is used.",
                    example = "${mail.assigned_to}",
                    required = true)
    private String defaultValue;

    @Column(name = "list_values", length = 1000)
    @Size(min = 0, max = 1000)
    @ApiModelProperty(value = "The allowed values of the parameter when type equals to <i>List</i>.\nValues should be comma separated.",
                    example = "Apple, Orange, Pear")
    private String listValues;

    @Column(name = "is_hidden")
    @ApiModelProperty(value = "The flag to indicate whether the parameter is visible to users when the obligation is enabled.",
                    example = "false",
                    required = true,
                    allowableValues = "true, false")
    private boolean hidden;

    @Column(name = "is_editable")
    @ApiModelProperty(value = "The flag to indicate whether the parameter is editable by users when the obligation is enabled.",
                    example = "true",
                    required = true,
                    allowableValues = "true, false")
    private boolean editable;

    @Column(name = "is_mandatory")
    @ApiModelProperty(value = "The flag to indicate whether the parameter value is mandatory when the obligation is enabled.",
                    example = "true",
                    required = true,
                    allowableValues = "true, false")
    private boolean mandatory;

	@Column(name = "sort_order")
    @ApiModelProperty(value = "The display position of the parameter in the obligation's parameter list.",
                    required = true,
                    notes = "This is automatically assigned based on the order of parameter added.",
                    example = "0")
	private int sortOrder;
	
    @Override
    public Long getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public ObligationParameterDataType getType() {
        return type;
    }

    public void setType(ObligationParameterDataType type) {
        this.type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getListValues() {
        return listValues;
    }

    public void setListValues(String listValues) {
        this.listValues = listValues;
    }

    /**
	 * @return the sortOrder
	 */
	public int getSortOrder() {
		return sortOrder;
	}

	/**
	 * @param sortOrder the sortOrder to set
	 */
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
    public int compareTo(ParameterConfig o) {
        if (o == null || this.id == null || o.id == null)
            return -1;
        return this.id.compareTo(o.id);
    }

}
