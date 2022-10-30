/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 6, 2016
 *
 */
package com.nextlabs.destiny.console.dto.common;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 *
 * Application help content object
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Document(indexName = "console_help")
@Setting(settingPath = "/search_config/index-settings.json")
public class HelpContent implements Serializable {

    private static final long serialVersionUID = 4758779508464893492L;
    public static final String HELP_FILE_NAME_PREFIX = "helptext_";

    @Id
    @Field(type = FieldType.Long, store = true)
    private Long id;
    private String i18nLangCode;
    private String appName;
    private String module;
    private String sectionTitle;
    private String subSectionTitle;
    private String field;
    private String helpText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getI18nLangCode() {
        return i18nLangCode;
    }

    public void setI18nLangCode(String i18nLangCode) {
        this.i18nLangCode = i18nLangCode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public String getSubSectionTitle() {
        return subSectionTitle;
    }

    public void setSubSectionTitle(String subSectionTitle) {
        this.subSectionTitle = subSectionTitle;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    @Override
    public String toString() {
        return String.format("HelpContent []");
    }

}
