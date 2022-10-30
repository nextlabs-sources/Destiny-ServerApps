/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 6, 2016
 *
 */
package com.nextlabs.destiny.console.services;

import org.springframework.data.domain.Page;

import com.nextlabs.destiny.console.dto.common.HelpContent;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.exceptions.ConsoleException;

/**
 *
 * Help content search service interface.
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface HelpContentSearchService {

    /**
     * Upload the help content from local directory into search index
     * 
     * @throws ConsoleException
     *             throws at any exception
     */
    void uploadHelpContent() throws ConsoleException;

    /**
     * Search help content by given criteria
     * 
     * @param criteria
     *            search criteria
     * @return {@link Page<HelpContent>}
     * @throws ConsoleException
     *             throws at any exception
     */
    Page<HelpContent> findHelpByCriteria(SearchCriteria criteria)
            throws ConsoleException;

    /**
     * Return the resource URL for requested resource name.
     *
     * @param name resource to look for resource URL
     * @return resource URL
     */
    String getResourceUrl(String name);

}
