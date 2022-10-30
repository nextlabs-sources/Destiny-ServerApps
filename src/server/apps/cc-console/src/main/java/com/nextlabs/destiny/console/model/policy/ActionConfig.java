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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import com.nextlabs.destiny.console.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * Policy action configuration entity
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Entity
@Table(name = "PM_ACTION_CONFIG")
public class ActionConfig extends BaseModel
        implements Comparable<ActionConfig> {

    private static final long serialVersionUID = -223620266873031641L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(example = "21")
    private Long id;

    @Column(name = "name", length = 255)
    @ApiModelProperty(value = "The name of the action.\nThis should be an expressive verb.",
                    required =  true,
                    example = "Read File")
    @Size(min = 1, max = 255)
    private String name;

    @Column(name = "short_name", length = 255)
    @ApiModelProperty(value = "The code/unique identifier of the action.",
                    required = true,
                    example = "READ_FILE")
    @Size(min = 1, max = 255)
    private String shortName;
    
    @Column(name = "short_code", length = 2)
    @ApiModelProperty(value = "The internal code assigned by the system for reporting purpose.", hidden = true)
    private String shortCode;

	@Transient
    @ApiModelProperty(hidden = true)
    private Boolean isReferenced;

	@Column(name = "sort_order")
    @ApiModelProperty(value = "The display position of this action in the policy model actions list.",
                    required = true,
                    notes = "This is automatically assigned based on the order of action added.",
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

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public Boolean getIsReferenced() {
		return isReferenced;
	}

	public void setIsReferenced(Boolean isReferenced) {
		this.isReferenced = isReferenced;
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
    public int compareTo(ActionConfig o) {
        if (o == null || this.id == null || o.id == null)
            return -1;
        return this.id.compareTo(o.id);
    }
}
