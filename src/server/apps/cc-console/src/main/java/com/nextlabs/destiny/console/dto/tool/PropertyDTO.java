/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 6, 2020
 *
 */
package com.nextlabs.destiny.console.dto.tool;

import com.nextlabs.destiny.console.dto.common.BaseDTO;
import com.nextlabs.destiny.console.enums.ElementFieldType;
import com.nextlabs.destiny.console.enums.ElementType;
import com.nextlabs.destiny.console.model.dictionary.Property;


public class PropertyDTO extends BaseDTO {

    private static final long serialVersionUID = -3814693256858901732L;

    private int version;
    private String name;
    private String type;
    private String label;
    private char deleted;
    private String mapping;
    private Long parentTypeId;
    private String parentName;
    private boolean isPreSeeded;

    public PropertyDTO() {
        super();
    }

    public PropertyDTO(Property property, String parentName) {
        this.id = property.getId();
        this.version = property.getVersion();
        this.name = property.getName();
        this.type = ElementFieldType.getLabelFromAttributeType(property.getType());
        this.label = property.getLabel();
        this.deleted = property.getDeleted();
        this.mapping = property.getMapping();
        this.parentTypeId = property.getParentTypeId();
        this.parentName = parentName;
        this.isPreSeeded = ElementType.valueOf(parentName.toUpperCase())
                .getPreSeededAttributes().contains(name);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
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

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public Long getParentTypeId() {
        return parentTypeId;
    }

    public void setParentTypeId(Long parentTypeId) {
        this.parentTypeId = parentTypeId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public boolean isPreSeeded() {
        return isPreSeeded;
    }

}
