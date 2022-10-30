/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on May 3, 2016
 *
 */
package com.nextlabs.destiny.console.services.delegadmin;

import java.util.List;

import com.nextlabs.destiny.console.dto.Authorizable;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentLite;
import com.nextlabs.destiny.console.dto.policymgmt.FolderDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyModelDTO;
import com.nextlabs.destiny.console.enums.ActionType;
import com.nextlabs.destiny.console.enums.AuthorizableType;
import com.nextlabs.destiny.console.enums.DelegationModelShortName;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.ApplicationUser;

/**
 * System wide access control is managed by this service
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface AccessControlService {

    /**
     * Populate all allowed actions and accessible tags for the given user.
     * 
     * @param user
     *            {@link ApplicationUser}
     * @throws ConsoleException
     *             throws at any error
     */
    ApplicationUser populateAllowedActionsAndTags(ApplicationUser user)
            throws ConsoleException;

    /**
     * Enforce the tag based access control to given policy
     * 
     * @param policy
     *            policy
     * @return {@link PolicyLite}
     */
    PolicyLite enforceTBAConPolicy(PolicyLite policy);

    /**
     * Enforce the tag based access control to each policy in given policy list
     * 
     * @param policies
     *            list of policies
     * @return list of {@link PolicyLite}
     */
    List<PolicyLite> enforceTBAConPolicies(List<PolicyLite> policies);

    /**
     * Enforce the tag based access control to each component
     * 
     * @param component
     *            component
     * @return {@link ComponentLite}
     */
    ComponentLite enforceTBAConComponent(ComponentLite component);

    /**
     * Enforce the tag based access control to each policy in given component
     * list
     * 
     * @param components
     *            list of components
     * @return list of {@link ComponentLite}
     */
    List<ComponentLite> enforceTBAConComponents(List<ComponentLite> components);

    /**
     * Enforce the tag based access control to given policy model
     * 
     * @param policyModel
     *            list of policy model
     * @return {@link PolicyModelDTO}
     */
    PolicyModelDTO enforceTBAConPolicyModel(PolicyModelDTO policyModel);

    /**
     * Enforce the tag based access control to each policy in given policy model
     * list
     * 
     * @param policyModel
     *            list of policy model
     * @return list of {@link PolicyModelDTO}
     */
    List<PolicyModelDTO> enforceTBAConPolicyModels(
            List<PolicyModelDTO> policyModel);

    /**
     * Enforce access control for a list of policy folders.
     *
     * @param folderDTOS folders to enforce access control
     * @return the folder list with access control
     */
    List<FolderDTO> enforceTBAConPolicyFolder(List<FolderDTO> folderDTOS);

    /**
     * Enforce access control for a list of policy folders.
     *
     * @param folderDTOS folders to enforce access control
     * @return the folder list with access control
     */
    List<FolderDTO> enforceTBAConComponentFolder(List<FolderDTO> folderDTOS);

    /**
     * Authorize to perform a specific action on an object with provided tags.
     *
     * @param actionType               type of the action
     * @param delegationModelShortName type of the object
     * @param tags                     tags owned by the object
     * @param tagValidation            true if required to check the tag availability
     * @throws ConsoleException                                           thrown if error occurred in accessing data
     * @throws com.nextlabs.destiny.console.exceptions.ForbiddenException thrown if the user is not authorized
     */
    void authorizeByTags(ActionType actionType,
                         DelegationModelShortName delegationModelShortName,
                         Authorizable authorizable,
                         boolean tagValidation) throws ConsoleException;

    /**
     * Check if the current user has the authority.
     *
     * @param authority        authority to check
     * @param actionType       type of the action
     * @param authorizableType type of the object
     * @throws com.nextlabs.destiny.console.exceptions.ForbiddenException thrown if the user does not has the authority
     */
    void checkAuthority(String authority, ActionType actionType, AuthorizableType authorizableType);

}
