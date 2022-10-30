/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 24, 2016
 *
 */
package com.nextlabs.destiny.console.services.policy;

import java.util.List;
import java.util.Set;

import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.DeploymentDependency;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ValidationDetailDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;

/**
 *
 * Policy/Component validator Service interface
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface ValidatorService {

    /**
     * Validate the given policy and its contents
     * 
     * @param policyDTO
     * @return {@link ValidationDetailDTO}
     * @throws ConsoleException
     *             throws at any error
     */
    ValidationDetailDTO validate(PolicyDTO policyDTO) throws ConsoleException;

    /**
     * Validate the given component and its contents
     * 
     * @param componentDTO
     * @return {@link ValidationDetailDTO}
     * @throws ConsoleException
     *             throws at any error
     */
    ValidationDetailDTO validate(ComponentDTO componentDTO)
            throws ConsoleException;

    /**
     * Validates if an entity can be undeployed, given the Id
     * 
     * @param componentId
     * @return {@link ValidationDetailDTO}
     * @throws ConsoleException
     *             throws at any error
     */
    public List<ValidationDetailDTO> checkForReferences(List<Long> componentIds)
            throws ConsoleException;

    /**
     * Find dependencies of the policy with the given policy id.
     *
     * @param dependencies empty list to add dependencies
     * @param id           policy id
     * @param type         policy type
     * @param provided     true if the method is called with the provided policy id
     * @throws ConsoleException if an error occurred
     */
    void findDependenciesOfPolicy(Set<DeploymentDependency> dependencies, Long id, boolean provided)
            throws ConsoleException;

    /**
     * Find dependencies of the component with the given component id.
     *
     * @param dependencies empty list to add dependencies
     * @param id           component id
     * @param type         component type
     * @throws ConsoleException if an error occurred
     */
    void findDependenciesOfComponent(Set<DeploymentDependency> dependencies, Long id, boolean provided)
            throws ConsoleException;

}
