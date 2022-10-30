/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 5, 2016
 *
 */
package com.nextlabs.destiny.console.services.policy;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nextlabs.destiny.console.dto.common.FacetResult;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SortField;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentLite;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.exceptions.ConsoleException;

/**
 *
 * Component Search Criteria Service interface
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface ComponentSearchService {

    /**
     * Find the components by given criteria.
     *
     * @param criteria     search criteria
     * @param skipDAFilter skip DA filtering
     * @return list of {@link PolicyLite}
     * @throws ConsoleException throws at any error
     */
    Page<ComponentLite> findByCriteria(SearchCriteria criteria, boolean skipDAFilter)
            throws ConsoleException;

    /**
     * Find the components by given list of Ids.
     * 
     * @param ids
     *            List of ids
     * @param sortFields
     *            List of sortFields
     * @param pageable
     *            pagination
     * @return list of {@link ComponentLite}
     * @throws ConsoleException
     *             throws at any error
     */
    Page<ComponentLite> findComponentsByIds(List<Long> ids,
            List<SortField> sortFields, PageRequest pageable)
            throws ConsoleException;

    /**
     * Find faceted terms by given criteria
     * 
     * @param criteria
     *            search criteria
     * @return {@link FacetResult}
     * @throws ConsoleException
     */
    FacetResult findFacetByCriteria(SearchCriteria criteria)
            throws ConsoleException;

    /**
     * Find components by given group type one of SUBJECT, ACTION, RESOURCE
     *
     * @param groupType    component group type
     * @param modelType    model type
     * @param skipDAFilter skip DA filtering
     * @return list of {@link ComponentLite}
     * @throws ConsoleException
     */
    Page<ComponentLite> findComponentsByGroupAndType(String groupType,
                                                     String modelType, Pageable pageable, boolean skipDAFilter)
            throws ConsoleException;

    /**
     * Re-Index all the components
     * 
     * @throws ConsoleException
     */
    void reIndexAllComponents() throws ConsoleException;

    /**
     * @param componentDTOs
     * @throws ConsoleException 
     */
    void reIndexComponents(ComponentDTO... componentDTOs) throws ConsoleException;

    /**
     * Re-index components by folder
     * @param folderId  folder id to find components
     * @throws ConsoleException if error occurred
     */
    void reIndexComponentsByFolder(Long folderId) throws ConsoleException;
}
