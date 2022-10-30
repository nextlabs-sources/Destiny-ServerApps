/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 12, 2015
 *
 */
package com.nextlabs.destiny.console.model;

import static com.nextlabs.destiny.console.enums.SharedMode.PUBLIC;
import static com.nextlabs.destiny.console.enums.Status.ACTIVE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.enums.SavedSearchType;
import com.nextlabs.destiny.console.enums.SharedMode;
import com.nextlabs.destiny.console.enums.Status;

/**
 *
 * Entity for Saved Policy Search
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Entity
@Table(name = "SAVED_SEARCH")
@NamedQuery(name = SavedSearch.FIND_BY_NAME, query = "SELECT c FROM SavedSearch c WHERE lower(c.name) like :name "
                + "AND c.status != 'DELETED' AND c.type = :type ORDER BY c.name")
@Document(indexName = "saved_searches")
@Setting(settingPath = "/search_config/index-settings.json")
public class SavedSearch extends BaseModel {

    private static final long serialVersionUID = -8751084280122385610L;

    public static final String FIND_BY_NAME = "savedSearch.fingByName";

    @org.springframework.data.annotation.Id
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Field(type = FieldType.Long, store = true)
    private Long id;

    @Column(name = "name")
    private String name;

    @Field(type = FieldType.Keyword, store = true)
    private String lowercase_name;

    @Column(name = "description")
    private String desc;

    @Column(name = "criteria_json", length = 4000)
    @Field(type = FieldType.Text, store = true)
    private String criteria;

    @Column(name = "shared_mode")
    @Enumerated(EnumType.STRING)
    private SharedMode sharedMode = PUBLIC;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status = ACTIVE;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @Field(type = FieldType.Keyword, store = true)
    private SavedSearchType type;

    public SavedSearch() {
    }

    /**
     * Constructor
     * 
     * @param id
     * @param name
     *            name or title
     * @param desc
     *            description
     * @param criteria
     *            criteria in json format
     * @param status
     *            status
     * @param sharedMode
     *            sharing mode
     * @param userIds
     *            list of shared user ids
     * @param type
     *            saved search type
     */
    public SavedSearch(Long id, String name, String desc, String criteria,
            Status status, SharedMode sharedMode, List<String> userIds,
            SavedSearchType type) {
        super();
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.criteria = criteria;
        this.status = status;
        this.sharedMode = sharedMode;
        this.type = type;
    }

    /**
     * Get Search criteria model from criteria json
     * 
     * @return {@link SearchCriteria}
     * @throws IOException
     */
    public SearchCriteria criteriaModel() throws IOException {
        return new ObjectMapper().readValue(this.criteria,
                SearchCriteria.class);
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.setLowercase_name(name.toLowerCase());
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public SharedMode getSharedMode() {
        return sharedMode;
    }

    public void setSharedMode(SharedMode sharedMode) {
        this.sharedMode = sharedMode;
    }

    public List<String> getUserIds() {
        return new ArrayList<>();
    }

    public void setUserIds(List<String> userIds) {
        // TODO
    }

    public SavedSearchType getType() {
        return type;
    }

    public void setType(SavedSearchType type) {
        this.type = type;
    }

    public String getLowercase_name() {
        return lowercase_name;
    }

    public void setLowercase_name(String lowercase_name) {
        this.lowercase_name = lowercase_name;
    }
    
}
