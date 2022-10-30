/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 7, 2015
 *
 */
package com.nextlabs.destiny.console.dto.common;

import static com.nextlabs.destiny.console.enums.Status.ACTIVE;

import com.nextlabs.destiny.console.model.Tag;
import com.nextlabs.destiny.console.model.TagLabel;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 *
 * DTO for Tag Label, reference entity {@link TagLabel}
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class TagDTO extends BaseDTO implements Tag, Comparable<TagDTO> {

    private static final long serialVersionUID = 5603404635030157154L;

    @NotBlank
    @Size(min = 1, max = 255)
    private String key;

    @Field(type = FieldType.Text, store = true, analyzer = "case_insensitive_analyzer")
    @NotBlank
    @Size(min = 1, max = 255)
    private String label;

    @NotBlank
    @Size(min = 1, max = 255)
    @Pattern(regexp = "COMPONENT_TAG|POLICY_MODEL_TAG|POLICY_TAG")
    private String type;

    @NotBlank
    @Size(min = 1, max = 255)
    private String status;

    public TagDTO() {
        super();
    }

    /**
     * Constructor
     *
     * @param key
     * @param label
     * @param type
     */
    public TagDTO(String key, String label, String type) {
        super();
        this.key = key;
        this.label = label;
        this.type = type;
        this.status = ACTIVE.name();
    }

    /**
     * Constructor
     * 
     * @param id
     * @param key
     * @param label
     * @param type
     */
    public TagDTO(Long id, String key, String label, String type) {
        this.setId(id);
        this.key = key;
        this.label = label;
        this.type = type;
        this.status = ACTIVE.name();
    }

    /**
     * Transform {@link TagLabel} entity data to DTO
     * 
     * @return {@link TagDTO}}
     */
    public static TagDTO getDTO(TagLabel tagLabel) {

        TagDTO tag = new TagDTO();
        tag.setId(tagLabel.getId());
        tag.setKey(tagLabel.getKey());
        tag.setLabel(tagLabel.getLabel());
        tag.setType(tagLabel.getType().name());
        tag.setStatus(tagLabel.getStatus().name());

        return tag;
    }

    @ApiModelProperty(value = "The display label of the tag.", position = 30, example = "Sample Tag")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @ApiModelProperty(value = "The unique key used to identify a tag. The value could be same as the label.",
                    position = 20,
                    example = "sample_tag")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @ApiModelProperty(value = "The tag type to apply.\n" +
                    "List of possible values:"
                    + "\n<ul>"
                    + "<li><strong>COMPONENT_TAG</strong>: Tags created with this type only available for component.</li>"
                    + "<li><strong>POLICY_MODEL_TAG</strong>: Tags created with this type only available for policy model.</li>"
                    + "<li><strong>POLICY_TAG</strong>: Tags created with this type only available for policy.</li>"
                    + "<li><strong>FOLDER_TAG</strong>: Tags created with this type only available for folder.</li>"
                    + "</ul>",
                    position = 40,
                    allowableValues = "POLICY_TAG, POLICY_MODEL_TAG, COMPONENT_TAG, FOLDER_TAG")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @ApiModelProperty(value = "The status of the tag.", 
                    position = 50,
                    allowableValues = "ACTIVE, IN_ACTIVE, DELETED",
                    example = "ACTIVE")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int compareTo(TagDTO o) {
        if (o == null)
            return -1;
        if (this.getId() == null || o.getId() == null) {
            return -1;
        }
        return this.getId().compareTo(o.getId());
    }

}
