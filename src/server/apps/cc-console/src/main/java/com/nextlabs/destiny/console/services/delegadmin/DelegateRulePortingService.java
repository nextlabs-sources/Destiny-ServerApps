package com.nextlabs.destiny.console.services.delegadmin;

/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on May 25, 2016
 *
 */

import java.util.List;

import com.nextlabs.destiny.console.dto.delegadmin.DelegateRuleLite;
import com.nextlabs.destiny.console.dto.delegadmin.porting.DelegationPortingDTO;
import com.nextlabs.destiny.console.exceptions.CircularReferenceException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;

/**
 *
 * Service to handle all export and import of delegation rules
 * 
 * This service will export all the related Policy models, components and its
 * sub components and sub policies
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public interface DelegateRulePortingService {

	/**
	 * Export the given rules and its components and delegation
	 * models
	 * 
	 * @param delegationRuleIds
	 * @return
	 * @throws ConsoleException
	 */
	String export(List<Long> daRuleIds) throws ConsoleException;

	/**
	 * Export all the policies and its components, sub-components and policy
	 * models
	 * 
	 * @param policyIds
	 * @return
	 * @throws ConsoleException
	 */
	String exportAll(List<DelegateRuleLite> daRuleLites) throws ConsoleException;

	/**
	 * Validate the given file and import the policy data.
	 * 
	 * @param bytes
	 * @return
	 * @throws ConsoleException
	 * @throws CircularReferenceException 
	 */
	DelegationPortingDTO validateAndImport(byte[] bytes) throws ConsoleException, CircularReferenceException;

}
