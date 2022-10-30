/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 26, 2016
 *
 */
package com.nextlabs.destiny.console.services.delegadmin;

import java.util.List;

import org.springframework.data.domain.Page;

import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.delegadmin.DelegateRuleLite;
import com.nextlabs.destiny.console.dto.delegadmin.DelegationRuleDTO;
import com.nextlabs.destiny.console.exceptions.CircularReferenceException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;

/**
 *
 * Delegation rule management service interface
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface DelegationRuleMgmtService {

    /**
     * Saves {@link DelegationRuleDTO}
     * 
     * @param ruleDTO
     *            {@link DelegationRuleDTO}
     * @return Saved {@link DelegationRuleDTO}
     * @throws ConsoleException
     *             thrown on any error
     * @throws CircularReferenceException 
     * 
     */
    DelegationRuleDTO save(DelegationRuleDTO ruleDTO) throws ConsoleException, CircularReferenceException;

    /**
     * modify {link DelegationRuleDTO}
     * 
     * @param ruleDTO
     *            {@link DelegationRuleDTO}
     * @return Saved {@link DelegationRuleDTO}
     * @throws ConsoleException
     *             thrown on any error
     * @throws CircularReferenceException 
     * 
     */
    DelegationRuleDTO modify(DelegationRuleDTO ruleDTO) throws ConsoleException, CircularReferenceException;

    /**
     * Finds a delegation rule details by id
     * 
     * @param id
     *            id
     * @return {@link DelegationRuleDTO} if found null thrown on any error
     * 
     */
    DelegationRuleDTO findById(Long id) throws ConsoleException;

    /**
     * Remove a delegation rule by id, related subject component also should get
     * deleted
     * 
     * @param id
     *            id
     */
    void remove(Long id) throws ConsoleException;

    /**
     * Bulk delete of policies by list of ids, related subject component also
     * should get deleted
     * 
     * @param ids
     *            list of ids
     */
    void remove(List<Long> ids) throws ConsoleException;

    /**
     * Re-Index all the delegation rules
     * 
     * @throws ConsoleException
     */
    void reIndexAllRules() throws ConsoleException;

    /**
     * Re-index delegation rule by its id
     * 
     * @param id Delegation rule's id to be re-indexed
     * @throws ConsoleException
     */
    void reIndexRule(Long id) throws ConsoleException;
    
    /**
     * Re-index a delegation rule
     * 
     * @param entity Delegation rule to be re-indexed
     * @throws ConsoleException
     */
    void reIndexRule(PolicyDevelopmentEntity entity) throws ConsoleException;
    
    /**
     * Find the delegation rules by given criteria.
     * 
     * @param criteria
     *            search criteria
     * @return list of {@link DelegateRuleLite}
     * @throws ConsoleException
     *             throws at any error
     */
    Page<DelegateRuleLite> findPolicyByCriteria(SearchCriteria criteria)
            throws ConsoleException;
    /**
     * Checks if a rule by given name already exists
     * 
     * @param ruleName
     * @return boolean
     * 			true/false
     * @throws ConsoleException
     *             thrown at any error
     */
    boolean isRuleExists(String ruleName) throws ConsoleException;

    /**
     * Returns list of Delegation Policies that match the given name
     * 
     * @param ruleName
     * @return
     */
	List<DelegateRuleLite> getDelegationPoliciesByName(String ruleName);
}
