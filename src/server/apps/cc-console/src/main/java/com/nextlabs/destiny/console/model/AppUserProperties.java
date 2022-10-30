/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on May 5, 2016
 *
 */
package com.nextlabs.destiny.console.model;

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

/**
 *
 * Entity for Application User Properties
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@Entity
@Table(name = "APP_USER_PROPERTIES")
@NamedQuery(name = AppUserProperties.FIND_BY_USER_ID, query = "SELECT p FROM AppUserProperties p WHERE p.userId = :userId ORDER BY p.key")
@NamedQuery(name = AppUserProperties.FIND_BY_SUPER_USER_ID, query = "SELECT p FROM AppUserProperties p WHERE p.superUserId = :superUserId ORDER BY p.key")
public class AppUserProperties extends BaseModel
        implements Comparable<AppUserProperties> {

    private static final long serialVersionUID = 2990227898398363761L;

    public static final String FIND_BY_USER_ID = "appUserProperties.findByUserId";
    public static final String FIND_BY_SUPER_USER_ID = "appUserProperties.findBySuperUserId";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "super_user_id")
    private Long superUserId;

    @Column(name = "prop_key")
    private String key;

    @Column(name = "prop_value")
    private String value;

    @Column(name = "data_type")
    @Enumerated(EnumType.STRING)
    private DataType dataType = DataType.STRING;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSuperUserId() {
		return superUserId;
	}

	public void setSuperUserId(Long superUserId) {
		this.superUserId = superUserId;
	}

	public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public int compareTo(AppUserProperties o) {
        if (o == null || this.id == null)
            return -1;
        return this.id.compareTo(o.id);
    }

}
