/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 29, 2016
 *
 */
package com.nextlabs.destiny.console.dto.delegadmin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nextlabs.destiny.console.exceptions.ConsoleException;

/**
 *
 * Obligation tag filter model for delegation rule obligations
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class ObligationTagsFilter implements Serializable {

    private static final Logger log = LoggerFactory
            .getLogger(ObligationTagsFilter.class);

    private static final long serialVersionUID = 7484771525328611837L;

    private List<TagsFilter> tagsFilters;

    public List<TagsFilter> getTagsFilters() {
        if (tagsFilters == null) {
            tagsFilters = new ArrayList<>();
        }
        return tagsFilters;
    }

    public void setTagsFilters(List<TagsFilter> tagsFilters) {
        this.tagsFilters = tagsFilters;
    }

    /**
     * Convert {@link ObligationTagsFilter} json string to PQL friendly yml
     * similar format
     * 
     * @param jsonString
     * @return pql friendly string
     * @throws ConsoleException
     *             throws at any error
     */
    public static String toPQLFriendlyFormat(String jsonString)
            throws ConsoleException {
        ObjectMapper jsonMapper = new ObjectMapper();
        try {
            ObligationTagsFilter obligTagsFilter = jsonMapper
                    .readValue(jsonString, ObligationTagsFilter.class);

            ObjectMapper ymlMapper = new ObjectMapper(new YAMLFactory());
            String ymlString = ymlMapper.writeValueAsString(obligTagsFilter);

            String pqlFriendlyString = ymlString.replaceAll("\n", "~")
                    .replaceAll("\"", "'");
            log.debug("PQL Friendly Delegation obligation string :{}",
                    pqlFriendlyString);

            return pqlFriendlyString;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in converting to pql friendly string",
                    e);
        }
    }

    /**
     * Convert PQL friendly YML to JSON
     * 
     * @param pqlFriendlyString
     * @return pql friendly string
     * @throws ConsoleException
     *             throws at any error
     */
    public static String toJSON(String pqlFriendlyString)
            throws ConsoleException {
        try {
            ObjectMapper jsonMapper = new ObjectMapper();
            ObjectMapper ymlMapper = new ObjectMapper(new YAMLFactory());

            String ymlString = pqlFriendlyString.replaceAll("~", "\n")
                    .replaceAll("'", "\"");
            ObligationTagsFilter obliTagsFilter = ymlMapper.readValue(ymlString,
                    ObligationTagsFilter.class);

            String jsonString = jsonMapper.writeValueAsString(obliTagsFilter);
            log.debug("JSON from PQL Friendly Delegation obligation string :{}",
                    jsonString);

            return jsonString;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in converting pql friendly string to json",
                    e);
        }
    }

    /**
     * Convert PQL friendly YML to {@link ObligationTagsFilter},
     * 
     * @param pqlFriendlyString
     *            pql friendly string
     * @return {@link ObligationTagsFilter}
     * @throws ConsoleException
     *             throws at any error
     */
    public static ObligationTagsFilter parsePQLFriendly(
            String pqlFriendlyString) throws ConsoleException {
        try {
            ObjectMapper ymlMapper = new ObjectMapper(new YAMLFactory());

            String ymlString = pqlFriendlyString.replaceAll("~", "\n")
                    .replaceAll("'", "\"");
            return ymlMapper.readValue(ymlString, ObligationTagsFilter.class);
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in converting pql friendly string to object",
                    e);
        }
    }

    /**
     * Parse PQL friendly YML to {@link ObligationTagsFilter}
     * 
     * @param pqlFriendlyString
     * @return {@link ObligationTagsFilter}
     * @throws ConsoleException
     *             throws at any error
     */
    public static ObligationTagsFilter parse(String pqlFriendlyString)
            throws ConsoleException {
        try {
            ObjectMapper ymlMapper = new ObjectMapper(new YAMLFactory());
            String ymlString = pqlFriendlyString.replaceAll("~", "\n")
                    .replaceAll("'", "\"");

            return ymlMapper.readValue(ymlString, ObligationTagsFilter.class);
        } catch (Exception e) {
            throw new ConsoleException("Error encountered in parse", e);
        }
    }

    @Override
    public String toString() {
        return String.format("ObligationTagsFilter [tagsFilters=%s]",
                tagsFilters);
    }

}
