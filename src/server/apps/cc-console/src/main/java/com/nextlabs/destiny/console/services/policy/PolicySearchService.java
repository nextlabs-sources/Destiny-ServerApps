/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 12, 2015
 *
 */
package com.nextlabs.destiny.console.services.policy;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.nextlabs.destiny.console.dto.common.FacetResult;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SortField;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.exceptions.ConsoleException;

/**
 *
 * Policy Search Service interface
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface PolicySearchService {

    /**
     * Find the policies by given criteria.
     * 
     * @param criteria
     *            search criteria
     * @return list of {@link PolicyLite}
     * @throws ConsoleException
     *             throws at any error
     */
    Page<PolicyLite> findPolicyByCriteria(SearchCriteria criteria)
            throws ConsoleException;

    /**
     * Find the policies by given list of Ids.
     * 
     * @param ids
     *            List of ids
     * @param sortFields
     *            List of sortFields
     * @param pageable
     *            pagination
     * @return list of {@link PolicyLite}
     * @throws ConsoleException
     *             throws at any error
     */
    Page<PolicyLite> findPolicyByIds(List<Long> ids, List<SortField> sortFields,
            PageRequest pageable) throws ConsoleException;

    /**
     * Find the policy tree for given root id
     * 
     * @param id
     *            policy id
     * @return list of {@link PolicyLite}
     * @throws ConsoleException
     *             throws at any error
     */
    PolicyLite findPolicyTree(Long id) throws ConsoleException;

    /**
     * Find the policy tree for given root
     * 
     * @param rootFolder
     *            policy tree root
     * @return list of {@link PolicyLite}
     * @throws ConsoleException
     *             throws at any error
     */
    PolicyLite findPolicyTree(String rootFolder) throws ConsoleException;

    /**
     * Find the sub policy list for the given policy id
     *
     * @param parentPolicyId
     *            policy tree root
     * @return list of {@link PolicyLite}
     * @throws ConsoleException
     *             throws at any error
     */
    List<PolicyLite> findSubPolicy(Long parentPolicyId)
            throws ConsoleException;

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
     * Aggregate all policies by tags. Top X tag count that were used in
     * policies
     * 
     * @param dataSize
     *            expected data, top x number
     * @return {@link FacetResult}
     * @throws ConsoleException
     *             throws at any error
     */
    FacetResult aggregatedPoliciesByTags(int dataSize) throws ConsoleException;

    /**
     * Re-Index all the policies
     * 
     * @throws ConsoleException
     */
    void reIndexAllPolicies() throws ConsoleException;

    /**
     * @param policyDTO
     * @throws ConsoleException 
     */
    void reIndexPolicies(PolicyDTO... policyDTO) throws ConsoleException;

    /**
     * Re-index policies by folder
     * @param folderId  folder id to find policies
     * @throws ConsoleException if error occurred
     */
    void reIndexPoliciesByFolder(Long folderId) throws ConsoleException;

}
