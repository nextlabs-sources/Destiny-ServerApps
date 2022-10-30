/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 5, 2016
 *
 */
package com.nextlabs.destiny.console.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nextlabs.destiny.console.enums.SavedSearchType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.SavedSearch;

/**
 * Saved search service interface
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface SavedSearchService {

    /**
     * Create or modify Policy search criteria
     * 
     * @param criteria
     * @return {@link SavedSearch}
     * @throws ConsoleException
     *             throws at any error
     */
    SavedSearch saveCriteria(SavedSearch criteria) throws ConsoleException;

    /**
     * Find policy search for given id
     * 
     * @param id
     * @return {@link SavedSearch}
     * @throws ConsoleException
     *             throws at any error
     */
    SavedSearch findById(Long id) throws ConsoleException;

    /**
     * Remove policy search criteria
     * 
     * @param id
     * @throws ConsoleException
     *             throws at any error
     */
    void removeCriteria(Long id) throws ConsoleException;

    /**
     * Re-Index all the saved policy criteria
     * 
     * @throws ConsoleException
     */
    void reIndexAllCriteria() throws ConsoleException;

    /**
     * Find the policy search criteria by name, wild card search applies
     * 
     * @param searchText
     *            searchText
     * @param type
     *            Saved search type
     * @param pageable
     *            pagination
     * @return List of {@link SavedSearch}
     * @throws ConsoleException
     *             throws at any error
     */
    Page<SavedSearch> findByNameOrDescriptionAndType(String searchText,
            SavedSearchType type, Pageable pageable) throws ConsoleException;

}
