/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 14, 2016
 *
 */
package com.nextlabs.destiny.console.services.policy;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.nextlabs.destiny.console.dto.common.FacetResult;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SortField;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.exceptions.CircularReferenceException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.policy.AttributeConfig;
import com.nextlabs.destiny.console.model.policy.PolicyModel;

/**
 *
 * Service interface for Policy Model
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface PolicyModelService {

    /**
     * Saves or updates an {@link PolicyModel}
     * 
     * @param policyModel
     *            {@link PolicyModel}
     * @param assigned
     *            tags
     * @param createAction
     * 			create default action components, or skip 
     * @return Saved {@link PolicyModel}
     * @throws ConsoleException
     *             thrown on any error
     * @throws CircularReferenceException 
     * 
     */
    PolicyModel save(PolicyModel policyModel, List<Long> tagIds, boolean createAction)
            throws ConsoleException, CircularReferenceException;

    /**
     * Saves or updates an {@link PolicyModel}
     * 
     * @param policyModel
     *            {@link PolicyModel}
     * @param createAction
     * 			create default action components, or skip 
     * @return Saved {@link PolicyModel}
     * @throws ConsoleException
     *             thrown on any error
     * @throws CircularReferenceException 
     * 
     */
    PolicyModel save(PolicyModel policyModel, boolean createAction) throws ConsoleException, CircularReferenceException;

    /**
     * Finds a Policy Model by id
     * 
     * @param id
     *            id
     * @return {@link PolicyModel} if found null thrown on any error
     * 
     */
    PolicyModel findById(Long id) throws ConsoleException;

    /**
     * Finds an active Policy Model by id
     * 
     * @param id
     *            id
     * @return {@link PolicyModel} if found null thrown on any error
     * 
     */
    PolicyModel findActivePolicyModelById(Long id) throws ConsoleException;

    /**
     * Clone Policy Model by id
     * 
     * @param id
     *            id
     * @param checkUniqueName 
     *            enforce policy model unique name check           
     * @param skipShortName
     *            skip cloning the shortname
     * @return {@link PolicyModel} if found otherwise null
     * @throws ConsoleException
     *             thrown on any error
     * @throws CircularReferenceException 
     * 
     */
	PolicyModel clone(Long id, boolean skipShortName, boolean checkUniqueName) 
			throws ConsoleException, CircularReferenceException;

    /**
     * Clone the given policy model
     * 
     * @param policyModel
     *            {@link PolicyModel} 
     * @param checkUniqueName 
     *            enforce policy model unique name check           
     * @return {@link PolicyModel} if found otherwise null
     * @throws ConsoleException
     *             thrown on any error
     * @throws CircularReferenceException 
     * 
     */
	PolicyModel clone(PolicyModel policyModel, boolean skipShortName, boolean checkUniqueName)
			throws ConsoleException, CircularReferenceException;

    /**
     * Load extra subject attributes
     * 
     * @param type
     *            type of the subject attribute
     * @return set of {@link AttributeConfig}
     * @throws ConsoleException
     */
    Set<AttributeConfig> loadExtraSubjectAttributes(String type)
            throws ConsoleException;

    /**
     * Finds {@link PolicyModel} by given criteria
     * 
     * @param criteria
     *            search criteria
     * @param skipDAFilter
     *            skip DA filtering
     * @return page of {@link PolicyModel}
     * @throws ConsoleException
     *             thrown on any error
     * 
     */
    Page<PolicyModel> findByCriteria(SearchCriteria criteria,
            boolean skipDAFilter) throws ConsoleException;

    /**
     * Finds PolicyModels by given list of ids
     * 
     * @param ids
     *            list of policy model ids
     * @param sortFields
     *            data set sort field
     * @param pageable
     *            page request data
     * @return page of {@link PolicyModel}
     * @throws ConsoleException
     *             thrown on any error
     * 
     */
    Page<PolicyModel> findByIds(List<Long> ids, List<SortField> sortFields,
            PageRequest pageable) throws ConsoleException;

    /**
     * Find Policy models by type
     * 
     * @param type
     *            {@link PolicyModelType}
     * @param pageable
     *            page request data
     * @return page of {@link PolicyModel}
     * @throws ConsoleException
     */
    Page<PolicyModel> findPolicyModelsByType(PolicyModelType type,
            PageRequest pageable) throws ConsoleException;

    /**
     * Find faceted terms by given criteria
     * 
     * @param criteria
     *            search criteria with facet field
     * @return {@link FacetResult}
     * @throws ConsoleException
     *             throws at any error
     */
    FacetResult findFacetByCriteria(SearchCriteria criteria)
            throws ConsoleException;

    /**
     * Remove an Policy Model
     * 
     * @param id
     *            id of PolicyModel
     * @throws ConsoleException
     *             thrown on any error
     */
    boolean remove(Long id) throws ConsoleException;

    /**
     * Bulk delete function on policy model
     * 
     * @param ids
     *            list of ids to be removed
     * @throws ConsoleException
     */
    List<String> remove(List<Long> ids) throws ConsoleException;

    /**
     * Re-index all policy models
     * 
     * @throws ConsoleException
     */
    void reIndexAllModels() throws ConsoleException;

    /**
     * Finds Policy models by type from database
     * 
     * @param type
     *            {@link PolicyModelType}
     * @param pageable
     *            page request data
     * @return page of {@link PolicyModel}
     * @throws ConsoleException
     */
    List<PolicyModel> findModelsByType(PolicyModelType type)
            throws ConsoleException;

}
