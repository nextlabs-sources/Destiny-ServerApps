/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 5, 2015
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

import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.enums.TagType;

/**
 * Tag entity to manage system wide tags
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Entity
@Table(name = "TAG_LABELS")
@NamedQuery(name = TagLabel.FIND_BY_TYPE, query = "SELECT t FROM TagLabel t WHERE t.type = :type ORDER BY t.label")
@NamedQuery(name = TagLabel.FIND_BY_KEY, query = "SELECT t FROM TagLabel t WHERE lower(t.key) = :key AND t.type = :type ORDER BY t.label")
@Document(indexName = "tags")
@Setting(settingPath = "/search_config/index-settings.json")
public class TagLabel extends BaseModel implements Tag, Comparable<TagLabel> {

    private static final long serialVersionUID = 8616085717267805787L;

    public static final String FIND_BY_TYPE = "tag.findByType";
    public static final String FIND_BY_STARTS_WITH = "tag.findByStartswith";
    public static final String FIND_BY_KEY = "tag.findByKey";
    public static final String ALL_TAGS_KEY = "all_tags";
    public static final String ALL_FOLDERS_KEY = "all_folders";

    @org.springframework.data.annotation.Id
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Field(type = FieldType.Long, store = true)
    private Long id;

    @Column(name = "tag_key")
    @Field(type = FieldType.Keyword, store = true)
    private String key;

    @Column(name = "label")
    @Field(type = FieldType.Text, fielddata = true)
    private String label;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TagType type;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "hidden")
    @Field(type = FieldType.Boolean, store = true)
    private boolean hidden = false;

    /**
     * Default constructor
     */
    public TagLabel() {

    }

    /**
     * TagLabel constructor with basic in parameters
     * 
     * @param id
     * @param key
     * @param label
     * @param type
     */
    public TagLabel(Long id, String key, String label, TagType type,
            Status status) {
        super();
        this.id = id;
        this.key = key.toLowerCase().replaceAll("[^\\w]+","_");
        this.label = label;
        this.type = type;
        this.status = status;
        this.hidden = false;
    }

    /**
     * TagLabel constructor with basic in parameters
     *
     * @param key
     * @param label
     * @param type
     */
    public TagLabel(String key, String label, TagType type, Status status) {
        super();
        this.key = key.toLowerCase().replaceAll("[^\\w]+","_");
        this.label = label;
        this.type = type;
        this.status = status;
        this.hidden = false;
    }

    @ApiModelProperty(value = "The id of the tag.", example = "28", position = 10)
    @Override
    public Long getId() {
        return this.id;
    }

    @ApiModelProperty(value = "The display label of the tag", position = 20, example = "Sample Tag")
    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @ApiModelProperty(value = "The type of the tag."
            + "\n<ul>"
            + "<li><strong>COMPONENT_TAG</strong>: Tags created with this type only available for component.</li>"
            + "<li><strong>POLICY_MODEL_TAG</strong>: Tags created with this type only available for policy model.</li>"
            + "<li><li><strong>POLICY_TAG</strong>: Tags created with this type only available for policy.</li>"
            + "</ul>",
            position = 30,
            allowableValues = "POLICY_TAG, POLICY_MODEL_TAG, COMPONENT_TAG, FOLDER_TAG")
    public TagType getType() {
        return this.type;
    }

    public void setType(TagType type) {
        this.type = type;
    }

    @ApiModelProperty(value = "The unique key used to identify a tag. The value could be same as the label.",
            position = 50,
            example = "sample_tag")
    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @ApiModelProperty(value = "The status of the tag.",
            position = 60,
            allowableValues = "ACTIVE, IN_ACTIVE, DELETED",
            example = "ACTIVE")
    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @ApiModelProperty(hidden = true)
    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
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
        TagLabel other = (TagLabel) obj;
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

    @Override
    public int compareTo(TagLabel obj) {
        if (obj == null)
            return -1;
        return this.id.compareTo(obj.getId());
    }

}
