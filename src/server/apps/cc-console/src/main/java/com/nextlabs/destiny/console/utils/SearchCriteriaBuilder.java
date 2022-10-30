/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on May 5, 2016
 *
 */
package com.nextlabs.destiny.console.utils;

import java.util.List;

import com.nextlabs.destiny.console.dto.common.DateFieldValue;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.dto.common.StringFieldValue;
import com.nextlabs.destiny.console.enums.SearchFieldType;

/**
 *
 * Search criteria builder to build search criteria for search
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class SearchCriteriaBuilder {

    private SearchCriteria criteria;

    public static SearchCriteriaBuilder create() {
        return new SearchCriteriaBuilder(new SearchCriteria());
    }

    private SearchCriteriaBuilder(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    /**
     * Add Single string search field to search criteria
     * 
     * @param fieldName
     *            fieldName
     * @param value
     *            search value
     * @return {@link SearchCriteriaBuilder}
     */
    public SearchCriteriaBuilder addSingleField(String fieldName,
            String value) {
        SearchField field = new SearchField();
        field.setField(fieldName);
        field.setType(SearchFieldType.SINGLE);

        field.setValue(getStringFieldValue(value));
        this.criteria.getFields().add(field);
        return this;
    }

    /**
     * Add Single exact match search field to search criteria
     *
     * @param fieldName fieldName
     * @param value     search value
     * @return {@link SearchCriteriaBuilder}
     */
    public SearchCriteriaBuilder addSingleExactMatchField(String fieldName, String value) {
        SearchField field = new SearchField();
        field.setField(fieldName);
        field.setType(SearchFieldType.SINGLE_EXACT_MATCH);
        field.setValue(getStringFieldValue(value));
        this.criteria.getFields().add(field);
        return this;
    }

    /**
     * Add multi value search field to search criteria
     * 
     * @param fieldName
     *            fieldName
     * @param values
     *            search values
     * @return {@link SearchCriteriaBuilder}
     */
    public SearchCriteriaBuilder addMultiValueField(String fieldName,
            List<String> values) {
        SearchField field = new SearchField();
        field.setField(fieldName);
        field.setType(SearchFieldType.MULTI);

        field.setValue(getMultiStringFieldValue(values));
        this.criteria.getFields().add(field);
        return this;
    }

    /**
     * Add date search field to search criteria
     * 
     * @param fieldName
     * @param fromDate
     * @param toDate
     * @return {@link SearchCriteriaBuilder}
     */
    public SearchCriteriaBuilder addDateField(String fieldName, long fromDate,
            long toDate) {
        SearchField field = new SearchField();
        field.setField(fieldName);
        field.setType(SearchFieldType.DATE);

        DateFieldValue fieldValue = new DateFieldValue();
        fieldValue.setFromDate(fromDate);
        fieldValue.setToDate(toDate);
        field.setValue(fieldValue);
        this.criteria.getFields().add(field);
        return this;
    }

    /**
     * Add nested single value search field to search criteria
     * 
     * @param fieldName
     * @param nestedPath
     * @param value
     * @return
     */
    public SearchCriteriaBuilder addNestedField(String fieldName,
            String nestedPath, String value) {
        SearchField field = new SearchField();
        field.setField(fieldName);
        field.setNestedField(nestedPath);
        field.setType(SearchFieldType.NESTED);

        field.setValue(getStringFieldValue(value));
        this.criteria.getFields().add(field);
        return this;
    }

    /**
     * Add multi nested value search field to search criteria
     * 
     * @param fieldName
     * @param nestedPath
     * @param values
     * @return
     */
    public SearchCriteriaBuilder addNestedMultiField(String fieldName,
            String nestedPath, List<String> values) {
        SearchField field = new SearchField();
        field.setField(fieldName);
        field.setNestedField(nestedPath);
        field.setType(SearchFieldType.NESTED);

        field.setValue(getMultiStringFieldValue(values));
        this.criteria.getFields().add(field);
        return this;
    }

    private StringFieldValue getStringFieldValue(String value) {
        StringFieldValue fieldValue = new StringFieldValue();
        fieldValue.setValue(value);
        return fieldValue;
    }

    private StringFieldValue getMultiStringFieldValue(List<String> value) {
        StringFieldValue fieldValue = new StringFieldValue();
        fieldValue.setValue(value);
        return fieldValue;
    }

    public SearchCriteria getCriteria() {
        return criteria;
    }

}
