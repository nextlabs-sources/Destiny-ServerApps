/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 14, 2020
 *
 */
package com.nextlabs.destiny.console.model.dictionary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.persistence.Version;

import com.nextlabs.destiny.console.dto.tool.PropertyDTO;
import com.nextlabs.destiny.console.enums.ElementFieldType;

/**
 * Dictionary Type Fields entity class Named ElementField as same name is used in destiny-base to
 * refer to properties.
 * 
 * @author Sneha Tilak
 * @since 9.5
 */

@Entity
@Table(name = Property.ELEMENT_FIELD_TABLE)
@SecondaryTable(name = "DICT_ELEMENT_TYPES")
public class Property {

    public static final String ELEMENT_FIELD_TABLE = "DICT_TYPE_FIELDS";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Version
    private int version;

    // short name
    private String name;

    @Column(name = "name_upper")
    private String upperName;

    private String type;

    private String label;

    private char deleted;

    private String mapping;

    @Column(name = "parent_type_id")
    private Long parentTypeId;

    public Property() {

    }

    public Property(PropertyDTO propDTO) {
        this.id = propDTO.getId();
        this.version = propDTO.getVersion();
        this.name = propDTO.getName();
        this.upperName = this.name.toUpperCase();
        this.type = ElementFieldType.getAttributeTypeFromLabel(propDTO.getType());
        this.label = propDTO.getLabel();
        this.deleted = propDTO.getDeleted();
        this.mapping = propDTO.getMapping();
        this.parentTypeId = propDTO.getParentTypeId();
    }

    public Long getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public char getDeleted() {
        return deleted;
    }

    public void setDeleted(char deleted) {
        this.deleted = deleted;
    }

    public String getMapping() {
        return mapping;
    }

    public Long getParentTypeId() {
        return parentTypeId;
    }
}
