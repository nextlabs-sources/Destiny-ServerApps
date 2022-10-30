/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 15, 2016
 *
 */
package com.nextlabs.destiny.console.dto.common;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * Facet Term data
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacetTerm implements Serializable {

    private static final long serialVersionUID = -4995804494280569680L;

    private String term;
    private int count;

    /**
     * Constructor
     */
    public FacetTerm() {
    }

    /**
     * Constructor
     * 
     * @param term
     *            term value
     * @param count
     *            count
     */
    private FacetTerm(String term, int count) {
        this.term = term;
        this.count = count;
    }

    /**
     * Create new Facet Term
     * 
     * @param term
     * @param count
     * @return {@link FacetTerm}
     */
    public static FacetTerm create(String term, int count) {
        return new FacetTerm(term, count);
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
