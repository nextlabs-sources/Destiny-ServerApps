/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 14, 2016
 *
 */
package com.nextlabs.destiny.console.model.policy;

import static org.springframework.data.elasticsearch.annotations.FieldType.Nested;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

import com.nextlabs.destiny.console.dto.Authorizable;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.model.BaseModel;
import com.nextlabs.destiny.console.model.TagLabel;

/**
 *
 * Basic building block of a policy.
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Entity
@Table(name = "POLICY_MODEL")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "POLICY")
@Document(indexName = "policy_models")
@Setting(settingPath = "/search_config/index-settings.json")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyModel extends BaseModel implements Authorizable {

    private static final long serialVersionUID = -5030773544599207480L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @org.springframework.data.annotation.Id
    @Field(type = FieldType.Long, store = true)
    private Long id;

    @Column(name = "name", length = 264)
    @MultiField(mainField = @Field(type = FieldType.Text), otherFields = {
            @InnerField(fielddata = true, suffix = "untouched", type = FieldType.Text, store = true, analyzer =
                    "case_insensitive_analyzer")})
    private String name;

    @Transient
    @Field(type = FieldType.Keyword, store = true)
    private String lowercase_name;

    @Column(name = "short_name", length = 50)
    @MultiField(mainField = @Field(type = FieldType.Text), otherFields = {
            @InnerField(suffix = "untouched", type = FieldType.Text, store = true, fielddata = true, analyzer =
                    "case_insensitive_analyzer")})
    private String shortName;

    @Transient
    @Field(type = FieldType.Keyword, store = true)
    private String lowercase_shortName;

    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @MultiField(mainField = @Field(type = FieldType.Keyword), otherFields = {
            @InnerField(suffix = "untouched", type = FieldType.Text, store = true, fielddata = true, analyzer =
                    "case_insensitive_analyzer")})
    private PolicyModelType type;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "POLICY_MODEL_TAGS", joinColumns = @JoinColumn(name = "plcy_model_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @Field(type = Nested)
    private Set<TagLabel> tags;

    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST,
            CascadeType.MERGE, CascadeType.REMOVE })
    @JoinColumn(name = "plcy_model_id")
    private Set<AttributeConfig> attributes;

    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST,
            CascadeType.MERGE, CascadeType.REMOVE })
    @JoinColumn(name = "plcy_model_id")
    private Set<ActionConfig> actions;

    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST,
            CascadeType.MERGE, CascadeType.REMOVE })
    @JoinColumn(name = "plcy_model_id")
    private Set<ObligationConfig> obligations;

    @Transient
    private Set<AttributeConfig> extraSubjectAttributes;

    /**
     * Constructor
     */
    public PolicyModel() {
    }

    /**
     * Constructor with values
     * 
     * @param id
     * @param name
     * @param shortName
     * @param description
     * @param type
     * @param status
     */
    public PolicyModel(Long id, String name, String shortName,
            String description, PolicyModelType type, Status status) {
        this.id = id;
        this.name = name;
        this.setLowercase_name(name);
        this.shortName = shortName;
        this.setLowercase_shortName(shortName);
        this.description = description;
        this.type = type;
        this.status = status;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ApiModelProperty(value = "The name of the policy model.",
            example = "Sample Policy Model")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.setLowercase_name(name);
    }

    @ApiModelProperty(value = "Name of the policy model in lowercase.",
            example = "sample policy model")
    public String getLowercase_name() {
        return lowercase_name;
    }

    public void setLowercase_name(String lowercase_name) {
        if (lowercase_name != null) {
            this.lowercase_name = lowercase_name.toLowerCase();
        }
    }

    @ApiModelProperty(value = "Unique code/identifier of the policy model.",
            example = "samplePolicyModel",
            notes = "This value is not modifiable once entity created.")
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
        this.setLowercase_shortName(shortName);
    }

    @ApiModelProperty(value = "Short name of the policy model in lowercase.",
            example = "samplepolicymodel")
    public String getLowercase_shortName() {
        return lowercase_shortName;
    }

    public void setLowercase_shortName(String lowercase_shortName) {
        if (lowercase_shortName != null) {
            this.lowercase_shortName = lowercase_shortName.toLowerCase();
        }
    }

    @ApiModelProperty(value = "The description for the policy model.",
            example = "This is a sample policy model")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ApiModelProperty(value = "The type of the policy model. This value is not modifiable once policy model is created."
            + "\n<ul><li><strong>SUBJECT</strong>: Subject component for policy.</li>"
            + "<li><strong>RESOURCE</strong>: Resource component for policy.</li>"
            + "<li><strong>DA_SUBJECT</strong>: Delegation subject component for delegation policy.</li>"
            + "<li><strong>DA_RESOURCE</strong>: Delegation resource component for delegation policy.</li></ul>",
            example = "RESOURCE")
    public PolicyModelType getType() {
        return type;
    }

    public void setType(PolicyModelType type) {
        this.type = type;
    }

    @ApiModelProperty(value = "The tags of the policy model grouping.")
    public Set<TagLabel> getTags() {
        if (tags == null) {
            tags = new TreeSet<>();
        }
        return tags;
    }

    @ApiModelProperty(hidden = true)
    @Override
    public Long getFolderId() {
        return null;
    }

    public void setTags(Set<TagLabel> tags) {
        this.tags = tags;
    }

    @ApiModelProperty(value = "The attributes of the policy model.")
    public Set<AttributeConfig> getAttributes() {
        if (attributes == null) {
            attributes = new TreeSet<>();
        }
        return attributes;
    }

    public void setAttributes(Set<AttributeConfig> attributes) {
        this.attributes = attributes;
    }

    @ApiModelProperty(value = "The actions of the policy model.", example = "[\"OPEN\", \"PRINT\"]")
    public Set<ActionConfig> getActions() {
        if (actions == null) {
            actions = new TreeSet<>();
        }
        return actions;
    }

    public void setActions(Set<ActionConfig> actions) {
        this.actions = actions;
    }

    @ApiModelProperty(value = "The obligations of the policy model.")
    public Set<ObligationConfig> getObligations() {
        if (obligations == null) {
            obligations = new TreeSet<>();
        }
        return obligations;
    }

    public void setObligations(Set<ObligationConfig> obligations) {
        this.obligations = obligations;
    }

    @ApiModelProperty(hidden = true)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @ApiModelProperty(value = "Extra subject attributes based on enrollment data")
    public Set<AttributeConfig> getExtraSubjectAttributes() {
        if (extraSubjectAttributes == null) {
            extraSubjectAttributes = new TreeSet<>();
        }
        return extraSubjectAttributes;
    }

    public void setExtraSubjectAttributes(Set<AttributeConfig> extraSubjectAttributes) {
        this.extraSubjectAttributes = extraSubjectAttributes;
    }
}
