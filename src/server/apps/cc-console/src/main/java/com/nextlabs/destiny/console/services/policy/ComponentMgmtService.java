/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 3, 2016
 *
 */
package com.nextlabs.destiny.console.services.policy;

import java.util.List;
import java.util.Set;

import com.bluejungle.pf.destiny.lib.LeafObject;
import com.bluejungle.pf.destiny.services.PolicyEditorException;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDeploymentHistoryDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentLite;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentPreviewDTO;
import com.nextlabs.destiny.console.dto.policymgmt.DeploymentDependency;
import com.nextlabs.destiny.console.dto.policymgmt.DeploymentRequestDTO;
import com.nextlabs.destiny.console.dto.policymgmt.DeploymentResponseDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ValidationDetailDTO;
import com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus;
import com.nextlabs.destiny.console.exceptions.CircularReferenceException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentEntity;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;

/**
 * Policy component management service
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface ComponentMgmtService {

    enum CheckCircularRefs { YES, NO };
    
    /**
     * Saves {@link componentDTO}
     * 
     * @param componentDTO
     *            {@link componentDTO}
     * @return Saved {@link componentDTO}
     * @throws ConsoleException
     *             thrown on any error
     * @throws CircularReferenceException
     *
     * @note equivalent to save(componentDTO, DO_CIRCULAR_REFERENCES_CHECK)
     */
    ComponentDTO save(ComponentDTO componentDTO) throws ConsoleException, CircularReferenceException;
    
    /**
     * Saves {@link componentDTO}
     * 
     * @param componentDTO
     *            {@link componentDTO}
     * @param checkCircularReferences
     * @return Saved {@link componentDTO}
     * @throws ConsoleException
     *             thrown on any error
     * @throws CircularReferenceException
     *
     */
    ComponentDTO save(ComponentDTO componentDTO, CheckCircularRefs checkCircularReferences) throws ConsoleException, CircularReferenceException;

    /**
     * modifies an {@link componentDTO}
     * 
     * @param componentDTO
     *            {@link componentDTO}
     * @return Saved {@link componentDTO}
     * @throws ConsoleException
     *             thrown on any error
     * @throws CircularReferenceException
     *
     * @note equivalent to modify(componentDTO, DO_CIRCULAR_REFERENCES_CHECK)
     */
    ComponentDTO modify(ComponentDTO componentDTO) throws ConsoleException, CircularReferenceException;
    
    /**
     * modifies an {@link componentDTO}
     * 
     * @param componentDTO
     *            {@link componentDTO}
     * @param checkCircularReferences
     * @return Saved {@link componentDTO}
     * @throws ConsoleException
     *             thrown on any error
     * @throws CircularReferenceException
     *
     */
    ComponentDTO modify(ComponentDTO componentDTO, CheckCircularRefs checkCircularReferences) throws ConsoleException, CircularReferenceException;

    /**
     * Add new subcomponent an {@link componentDTO}
     * 
     * @param componentDTO
     *            {@link componentDTO}
     * @return Saved {@link componentDTO}
     * @throws ConsoleException
     *             thrown on any error
     * @throws CircularReferenceException
     *
     */
    ComponentDTO addSubComponent(ComponentDTO componentDTO)
            throws ConsoleException, CircularReferenceException;

    /**
     * Add new subcomponent an {@link componentDTO}
     * 
     * @param componentDTO
     *            {@link componentDTO}
     * @param chckCircularReferences
     * @return Saved {@link componentDTO}
     * @throws ConsoleException
     *             thrown on any error
     * @throws CircularReferenceException
     *
     */
    ComponentDTO addSubComponent(ComponentDTO componentDTO, CheckCircularRefs checkCircularReferences)
            throws ConsoleException, CircularReferenceException;

    /**
     * Update status of component an {@link componentDTO}
     * 
     * @param policyModel
     *            {@link componentDTO}
     * @return Saved {@link componentDTO}
     * @throws ConsoleException
     *             thrown on any error
     * 
     */
    ComponentDTO updateStatus(Long componentId,
            PolicyDevelopmentStatus devStatus) throws ConsoleException;

    /**
     * Finds a Policy component by id
     * 
     * @param id
     *            id
     * @return {@link componentDTO} if found null thrown on any error
     * 
     */
    ComponentDTO findById(Long id) throws ConsoleException;

    /**
     * Finds an active Policy component by id
     * 
     * @param id
     *            id
     * @return {@link componentDTO} if found null thrown on any error
     * 
     */
    ComponentDTO findActiveById(Long id) throws ConsoleException;

    /**
     * Clone Policy component Entity by id
     * 
     * @param id
     *            id
     * @return {@link ComponentDTO} if found otherwise null
     * @throws ConsoleException
     *             thrown on any error
     * 
     */
    ComponentDTO clone(Long id) throws ConsoleException;

    /**
     * Remove a Policy component by id
     * 
     * @param id
     *            id
     * @return {@link ComponentDTO} information of removed component
     * @throws CircularReferenceException
     */
    ComponentDTO remove(Long id) throws ConsoleException;

    /**
     * Bulk delete components by ids
     * 
     * @param ids
     *            list of ids
     * @throws CircularReferenceException
     */
    List<ComponentDTO> remove(List<Long> ids) throws ConsoleException;

    /**
     * Deploy a component.
     *
     * @param deploymentRequest component deployment request
     */
    void deploy(DeploymentRequestDTO deploymentRequest) throws ConsoleException;

    /**
     * Deploy multiple components.
     *
     * @param deploymentRequests list of component deployment requests
     */
    List<DeploymentResponseDTO> deploy(List<DeploymentRequestDTO> deploymentRequests) throws ConsoleException;

    /**
     * Save and Deploy the component
     * 
     * @param componentDTO
     *            component details
     * @throws ConsoleException
     *             throws at any error
     * @throws CircularReferenceException
     *
     * @note equivalent to saveAndDeploy(componentDTO, DO_CIRCULAR_REFERENCES_CHECK)
     */
    DeploymentResponseDTO saveAndDeploy(ComponentDTO componentDTO) throws ConsoleException, CircularReferenceException;
    
    /**
     * Save and Deploy the component
     * 
     * @param componentDTO
     *            component details
     * @param circularReferencesCheck
     * @throws ConsoleException
     *             throws at any error
     * @throws CircularReferenceException
     */
    DeploymentResponseDTO saveAndDeploy(ComponentDTO componentDTO, CheckCircularRefs circularReferencesCheck) throws ConsoleException, CircularReferenceException;

    /**
     * Validate and Deploy the component and its sub-components if not deployed
     *
     * @param deploymentRequests list of component deployment requests
     * @throws ConsoleException throws at any error
     */
    List<ValidationDetailDTO> validateAndDeploy(List<DeploymentRequestDTO> deploymentRequests)
            throws ConsoleException;

    /**
     * Un-deploy a component.
     *
     * @param devEntity component to un-deploy
     */
    void unDeploy(PolicyDevelopmentEntity devEntity) throws ConsoleException;

    /**
     * un-deploy components by given list of ids
     * 
     * @param ids
     *            list of ids
     */
    void unDeploy(List<Long> ids) throws ConsoleException;

    /**
     * Get component deployment history by component by id
     * 
     * @param id
     *            id
     * @return collection of {@link PolicyDeploymentEntity} if found otherwise
     *         null
     * @throws ConsoleException
     *             thrown at any error
     * 
     */
    List<PolicyDeploymentEntity> deploymentHistory(Long id)
            throws ConsoleException;

    /**
     * View the revision details for given deployment entity id
     * 
     * 
     * @param id
     *            deployment entity id
     * @return {@link ComponentDeploymentHistoryDTO}
     * @throws ConsoleException
     *             thrown at any error
     */
    ComponentDeploymentHistoryDTO viewRevision(Long revisionId)
            throws ConsoleException;

    /**
     * Revert to given component revision id
     * 
     * @param revisionId
     * @return {@link ComponentDTO}
     * @throws ConsoleException
     *             thrown at any error
     */
    ComponentDTO revertToVersion(Long revisionId) throws ConsoleException;

    /**
     * Get the cloned unique name for the given component name and type
     * 
     * @param title
     *            title of the existing component
     * @param type
     *            type of the component
     * @return new unique name of the component
     * @throws ConsoleException
     *             thrown at any error
     */
    String getClonedName(String title, String type) throws ConsoleException;

    /**
     * This method will check the existence of the component for the given name
     * 
     * @param name
     *            name of the component
     * @param type
     *            type of the component
     * @return boolean true if exists
     * @throws ConsoleException
     *             thrown at any error
     */
    boolean isComponentExists(String name, String type) throws ConsoleException;
    
    /**
     * This method find the components for the given name and type
     * 
     * @param name
     *            name of the component
     * @param type
     *            type of the component
     * @return boolean true if exists
     */
    List<ComponentLite> getComponentsByNameAndGroup(String name, String type);

    /**
     * Find the list of deployment dependencies for the given list of component ids.
     *
     * @param ids component id list to find dependencies
     * @return list of deployment dependencies
     * @throws ConsoleException if error occurred
     */
    Set<DeploymentDependency> findDependencies(List<Long> ids) throws ConsoleException;


    /**
     * Enforce Tag based access control on {@link ComponentLite}. This will
     * method will enforce all the related reference access control in
     * Included-in components and sub components
     * 
     * @param lite
     *            {@link ComponentLite}
     * @return {@link ComponentLite} with TBAC
     */
    ComponentLite enforceTBAC(ComponentLite lite);

    /**
     * This method returns the preview of a component
     *
     * @param {@link componentDTO}
     * @return List of {@link LeafObject}}
     * @throws PolicyEditorException
     * @throws ConsoleException
     */
    ComponentPreviewDTO getComponentPreview(ComponentDTO componentDTO) throws PolicyEditorException, ConsoleException;

    void move(Long destinationFolderId, List<Long> ids) throws ConsoleException;

}
