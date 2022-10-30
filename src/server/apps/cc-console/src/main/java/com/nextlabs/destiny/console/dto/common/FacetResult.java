/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 12, 2016
 *
 */
package com.nextlabs.destiny.console.dto.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * Facet request mapping data will be populated here.
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacetResult implements Serializable {

    private static final long serialVersionUID = -7975666134329000499L;

    private String facetField;
    private List<FacetTerm> terms;

    /**
     * Constructor
     */
    public FacetResult() {
    }

    /**
     * Constructor
     * 
     * @param facetField
     * @param termMap
     */
    public FacetResult(String facetField) {
        this.facetField = facetField;
    }

    public String getFacetField() {
        return facetField;
    }

    public void setFacetField(String facetField) {
        this.facetField = facetField;
    }

    public List<FacetTerm> getTerms() {
        if (terms == null) {
            terms = new ArrayList<>();
        }
        return terms;
    }

    public void setTerms(List<FacetTerm> terms) {
        this.terms = terms;
    }

    @Override
    public String toString() {
        return String.format("FacetResult [facetField=%s, terms=%s]",
                facetField, terms);
    }

}
