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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;

import com.nextlabs.destiny.console.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * Policy obligation configuration entity
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Entity
@Table(name = "PM_OBLIGATION_CONFIG")
public class ObligationConfig extends BaseModel
        implements Comparable<ObligationConfig> {

    private static final long serialVersionUID = 628293327511202510L;

    public static final String RUN_MODE_PEP = "PEP";
    public static final String RUN_MODE_PDP = "PDP";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(hidden = true)
    private Long id;

    @Column(name = "name", length = 255)
    @ApiModelProperty(value = "The name of the obligation.",
                    example = "Policy Violation Notification",
                    required = true)
    private String name;

    @Column(name = "short_name", length = 50)
    @ApiModelProperty(value = "The unique code/ identifier of the obligation.",
                    example = "notify_violation",
                    required = true)
    private String shortName;

    @Column(name = "run_at", length = 255)
    @Pattern(regexp = "PEP|PDP")
    @ApiModelProperty(value = "",
                    required = true,
                    allowableValues = "PEP, PDP")
    private String runAt = RUN_MODE_PEP;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "obligation_id")
    private Set<ParameterConfig> parameters;

    @Transient
    @ApiModelProperty(hidden = true)
    private Boolean isReferenced;

	@Column(name = "sort_order")
    @ApiModelProperty(value = "The display position of this obligation in the policy model obligations list.",
                    required = true,
                    notes = "This is automatically assigned based on the order of obligation added.",
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

    public String getRunAt() {
        return runAt;
    }

    public void setRunAt(String runAt) {
        this.runAt = runAt;
    }

    public Set<ParameterConfig> getParameters() {
        if (parameters == null) {
            parameters = new TreeSet<>();
        }
        return parameters;
    }

    public void setParameters(Set<ParameterConfig> parameters) {
        this.parameters = parameters;
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
    public int compareTo(ObligationConfig o) {
        if (o == null || this.id == null || o.id == null)
            return -1;
        return this.id.compareTo(o.id);
    }

}
