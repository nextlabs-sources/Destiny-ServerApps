/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 14, 2016
 *
 */
package com.nextlabs.destiny.console.model.policy;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import com.nextlabs.destiny.console.enums.DataType;
import com.nextlabs.destiny.console.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * Attribute entity class
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Entity
@Table(name = "PM_ATTRIBUTE_CONFIG")
public class AttributeConfig extends BaseModel
        implements Comparable<AttributeConfig> {

    private static final long serialVersionUID = 5771123954671179124L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(hidden = true)
    private Long id;

    @Column(name = "name", length = 255)
    @Size(min = 1, max = 255)
    @ApiModelProperty(value = "The display name of the attribute.",
                    example = "Document category",
                    required = true)
    private String name;

    @Column(name = "short_name", length = 255)
    @Size(min = 1, max = 255)
    @ApiModelProperty(value = "The code/unique identifier of the attribute.",
                    example = "doc_cat",
                    required = true)
    private String shortName;

    @Column(name = "data_type")
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(value = "The data type of the attribute.",
                    example = "STRING",
                    required = true)
    private DataType dataType;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "PM_ATTRIB_CONFIG_OPER_CONFIG", joinColumns = @JoinColumn(name = "attribute_id") , inverseJoinColumns = @JoinColumn(name = "operator_id") )
    @ApiModelProperty(value = "The collection of evaluation operators allowed for this attribute.",
                    notes = "Minimally one evaluation operator.",
                    required = true)
    private Set<OperatorConfig> operatorConfigs;

    @Column(name = "reg_ex_pattern", length = 255)
    @ApiModelProperty(hidden = true)
    private String regExPattern;

    @ManyToOne
    @JoinColumn(name = "policy_model_id")
    @ApiModelProperty(hidden = true)
    private PolicyModel policyModel;

	@Transient
    @ApiModelProperty(hidden = true)
    private Boolean isReferenced;
	
	@Column(name = "sort_order")
    @ApiModelProperty(value = "The display position of this attribute in the policy model attributes list.",
                    required = true,
                    notes = "This is automatically assigned based on the order of attribute added.",
                    example = "0")
	private int sortOrder;
    
    @Override
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Set<OperatorConfig> getOperatorConfigs() {
        if (operatorConfigs == null) {
            operatorConfigs = new TreeSet<>();
        }
        return operatorConfigs;
    }

    public void setOperatorConfigs(Set<OperatorConfig> operatorConfigs) {
        this.operatorConfigs = operatorConfigs;
    }

    public String getRegExPattern() {
        return regExPattern;
    }

    public void setRegExPattern(String regExPattern) {
        this.regExPattern = regExPattern;
    }

    public Boolean getIsReferenced() {
		return isReferenced;
	}

	public void setIsReferenced(Boolean isReferenced) {
		this.isReferenced = isReferenced;
	}

	public PolicyModel getPolicyModel() {
        return policyModel;
    }

    public void setPolicyModel(PolicyModel policyModel) {
        this.policyModel = policyModel;
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
    public int compareTo(AttributeConfig o) {
        if (o == null || this.id == null || o.id == null)
            return -1;

        return this.id.compareTo(o.id);
    }

}
