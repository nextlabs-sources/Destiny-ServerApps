/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 5, 2015
 *
 */
package com.nextlabs.destiny.console.model;

import static com.nextlabs.destiny.console.utils.SecurityContextUtil.getCurrentUser;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import org.joda.time.LocalDateTime;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

/**
 *
 * Base model class for all the entities
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@MappedSuperclass
public abstract class BaseModel implements Serializable {

    private static final long serialVersionUID = 6281509266782825890L;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @MultiField(mainField = @Field(type = FieldType.Date, store = true), otherFields = {
                    @InnerField(suffix = "untouched", type = FieldType.Date, store = true)})
    @ApiModelProperty(value = "The date at which the record was created, in the UTC time format.",
            example = "2020-06-28T07:16:27.194+0000",
            position = 998)
    private Date createdDate;

    @Column(name = "last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    @MultiField(mainField = @Field(type = FieldType.Date, store = true), otherFields = {
            @InnerField(suffix = "untouched", type = FieldType.Date, store = true)})
    @ApiModelProperty(value = "The date at which the record was last updated, in the UTC time format.",
            example = "2020-07-01T07:37:33.393+0000",
            position = 999)
    private Date lastUpdatedDate;

    @Version
    @ApiModelProperty(position = 1000, example = "2")
    private int version;

    @Column(name = "created_by")
    @Field(type = FieldType.Long)
    @ApiModelProperty(hidden = true)
    private Long ownerId;

    @Column(name = "last_updated_by")
    @Field(type = FieldType.Long)
    @ApiModelProperty(hidden = true)
    private Long lastUpdatedBy;

    /**
     * Before entity persist
     * 
     */
    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now().toDate();
        this.lastUpdatedDate = LocalDateTime.now().toDate();
        this.ownerId = getCurrentUser().getUserId();
        this.lastUpdatedBy = getCurrentUser().getUserId();
    }

    /**
     * Before entity update
     * 
     */
    @PreUpdate
    public void preUpdate() {
        this.lastUpdatedDate = LocalDateTime.now().toDate();
        this.lastUpdatedBy = getCurrentUser().getUserId();
    }

    /**
     * Primary Key of the entity
     * 
     * @return primary key id
     */
    public abstract Long getId();

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

}
