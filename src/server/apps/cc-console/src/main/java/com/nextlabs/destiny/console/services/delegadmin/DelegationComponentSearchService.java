/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 25, 2016
 *
 */
package com.nextlabs.destiny.console.services.delegadmin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nextlabs.destiny.console.dto.common.FacetResult;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SortField;
import com.nextlabs.destiny.console.dto.delegadmin.DelegationComponentLite;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;

/**
 *
 * Delegation component Search Criteria Service interface
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface DelegationComponentSearchService {
    /**
     * Find the delegation components by given criteria.
     * 
     * @param criteria
     *            search criteria
     * @return list of {@link DelegationComponentLite}
     * @throws ConsoleException
     *             throws at any error
     */
    Page<DelegationComponentLite> findByCriteria(SearchCriteria criteria)
            throws ConsoleException;

    /**
     * Find the delegation components by given list of Ids.
     * 
     * @param ids
     *            List of ids
     * @param sortFields
     *            List of sortFields
     * @param pageable
     *            pagination
     * @return list of {@link DelegationComponentLite}
     * @throws ConsoleException
     *             throws at any error
     */
    Page<DelegationComponentLite> findComponentsByIds(List<Long> ids,
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
     * @param groupType
     *            component group type
     * @param modelType
     *            model type
     * @return list of {@link DelegationComponentLite}
     * @throws ConsoleException
     */
    Page<DelegationComponentLite> findComponentsByGroupAndType(String groupType,
            String modelType, Pageable pageable) throws ConsoleException;

    /**
     * Re-Index all the components
     * 
     * @throws ConsoleException
     */
    void reIndexAllComponents() throws ConsoleException;
    
    /**
     * Re-index delegation component of given id
     * @param id Delegation component's id to be re-indexed
     * @throws ConsoleException
     */
    void reIndexComponent(Long id) throws ConsoleException;
    
    /**
     * Re-index a delegation component
     * @param entity delegation component to be re-indexed
     * @throws ConsoleException
     */
    void reIndexComponent(PolicyDevelopmentEntity entity) throws ConsoleException;
}
