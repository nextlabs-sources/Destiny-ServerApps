/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 11, 2016
 *
 */
package com.nextlabs.destiny.console.services.policy;

import java.util.List;
import java.util.Set;

import com.nextlabs.destiny.console.dto.policymgmt.DeploymentDependency;
import com.nextlabs.destiny.console.dto.policymgmt.DeploymentRequestDTO;
import com.nextlabs.destiny.console.dto.policymgmt.DeploymentResponseDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.dto.policymgmt.XacmlPolicyDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDeploymentHistoryDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.dto.policymgmt.ValidationDetailDTO;
import com.nextlabs.destiny.console.dto.policymgmt.XacmlPolicyLite;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentEntity;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * Policy management service interface
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface PolicyMgmtService {

    /**
     * Saves {@link PolicyDTO}
     * 
     * @param policyDTO
     *            {@link PolicyDTO}
     * @return Saved {@link PolicyDTO}
     * @throws ConsoleException
     *             thrown on any error
     * 
     */
    PolicyDTO save(PolicyDTO policyDTO) throws ConsoleException;

    /**
     * Saves {@link XacmlPolicyDTO}
     *
     * @param policyDTO
     *            {@link XacmlPolicyDTO}
     * @return Saved {@link PolicyDevelopmentEntity}
     * @throws ConsoleException
     *             thrown on any error
     *
     */
    PolicyDevelopmentEntity saveXacmlPolicy(XacmlPolicyDTO policyDTO) throws ConsoleException;

    /**
     * Saves {@link XacmlPolicyDTO}
     *
     * @param xacmlFile
     *            {@link XacmlPolicyDTO}
     * @return Saved {@link XacmlPolicyDTO}
     * @throws ConsoleException
     *             thrown on any error
     *
     */
    PolicyDevelopmentEntity importXacmlPolicyAndDeploy(MultipartFile xacmlFile) throws ConsoleException;

    /**
     * modify {link PolicyDTO}
     * 
     * @param policyDTO
     *            {@link PolicyDTO}
     * @return Saved {@link PolicyDTO}
     * @throws ConsoleException
     *             thrown on any error
     * 
     */
    PolicyDTO modify(PolicyDTO policyDTO) throws ConsoleException;
    /**
     * modifyXacmlPolicy {link XacmlPolicyDTO}
     *
     * @param policyDTO
     *            {@link PolicyDTO}
     * @return Saved {@link PolicyDevelopmentEntity}
     * @throws ConsoleException
     *             thrown on any error
     *
     */
    PolicyDevelopmentEntity modifyXacmlPolicy(XacmlPolicyDTO policyDTO) throws ConsoleException;

    /**
     * Finds a Policy details by id
     * 
     * @param id
     *            id
     * @return {@link PolicyDTO} if found null thrown on any error
     * 
     */
    PolicyDTO findById(Long id) throws ConsoleException;

    /**
     * Finds a XACMLPolicy details by id
     *
     * @param devEntity
     *            devEntity
     * @return {@link PolicyDTO} if found null thrown on any error
     *
     */
    XacmlPolicyDTO getXacmlDTO(PolicyDevelopmentEntity devEntity) throws ConsoleException;

    /**
     * Finds an active Policy details by id
     * 
     * @param id
     *            id
     * @return {@link PolicyDTO} if found null thrown on any error
     * 
     */
    PolicyDTO findActiveById(Long id) throws ConsoleException;

    /**
     * Add new sub policy {@link PolicyDTO}
     * 
     * @param policyDTO
     *            {@link PolicyDTO}
     * @return Saved {@link PolicyDTO}
     * @throws ConsoleException
     *             thrown on any error
     * 
     */
    PolicyDTO addSubPolicy(PolicyDTO policyDTO) throws ConsoleException;

    /**
     * Clone Policy by id
     * 
     * @param id
     *            id
     * @return {@link PolicyDTO} if found otherwise null
     * @throws ConsoleException
     *             thrown on any error
     * 
     */
    PolicyDTO clone(Long id) throws ConsoleException;

    /**
     * Get policy deployment history by policy by id
     * 
     * @param id
     *            id
     * @return collection of {@link PolicyDeploymentEntity} if found otherwise
     *         null
     * @throws ConsoleException
     *             thrown on any error
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
     * @return {@link PolicyDeploymentHistoryDTO}
     * @throws ConsoleException
     */
    PolicyDeploymentHistoryDTO viewRevision(Long revisionId)
            throws ConsoleException;

    /**
     * Revert to given policy revision id
     * 
     * @param revisionId
     * @return {@link PolicyDTO}
     * @throws ConsoleException
     */
    PolicyDTO revertToVersion(Long revisionId) throws ConsoleException;

    /**
     * Bulk delete of policies by list of ids, when removing a policy it should
     * delete the underneath whole tree and remove the reference from the parent
     * policy.
     * 
     * @param ids list of ids
     * @param reIndex true if required to re-index policies
     * @return {@link PolicyDTO} information of removed policy for logging purpose
     */
    List<PolicyDTO> remove(List<Long> ids, boolean reIndex) throws ConsoleException;

    /**
     * Bulk delete of xacml policies by list of ids
     *
     * @param ids list of ids
     */
     void removeXacmlPolicy(List<Long> ids) throws ConsoleException;

    /**
     * Deploy a Policy by id and deployment time.
     *
     * @param deploymentRequest policy deployment request
     */
    void deploy(DeploymentRequestDTO deploymentRequest) throws ConsoleException;

    /**
     * Deploy a Xacml Policy by id and deployment time.
     *
     * @param deploymentRequest policy deployment request
     */
    void xacmlPolicyDeploy(DeploymentRequestDTO deploymentRequest) throws ConsoleException;

    /**
     * Deploy a multiple policies by given list of ids and deployment times.
     *
     * @param deploymentRequests list of policy deployment requests
     * @param reIndex true if required to re-index after deployment
     */
    List<DeploymentResponseDTO> deploy(List<DeploymentRequestDTO> deploymentRequests, boolean reIndex) throws ConsoleException;

    /**
     * Save and deploy a given policy
     * 
     * @param policyDTO
     * @throws ConsoleException
     */
    DeploymentResponseDTO saveAndDeploy(PolicyDTO policyDTO) throws ConsoleException;

    /**
     * Save and deploy a sub policy
     * 
     * @param policyDTO
     * @throws ConsoleException
     */
    Long saveAndDeploySubPolicy(PolicyDTO policyDTO) throws ConsoleException;

    /**
     * Validate and Deploy the policy and its reference components if not
     * deployed
     *
     * @param deploymentRequests list of policy deployment requests
     * @throws ConsoleException throws at any error
     */
    List<ValidationDetailDTO> validateAndDeploy(List<DeploymentRequestDTO> deploymentRequests)
            throws ConsoleException;

    /**
     * Un-deploy a Policy.
     *
     * @param devEntity policy to un-deploy
     * @throws ConsoleException throws at any error
     */
    void unDeploy(PolicyDevelopmentEntity devEntity) throws ConsoleException;

    /**
     * un-deploy a multiple policies by given list of ids
     * 
     * @param ids
     *            list of ids
     * @throws ConsoleException
     *             throws at any error
     */
    void unDeploy(List<Long> ids) throws ConsoleException;

    /**
     * Get the cloned unique name for the given policy name
     * 
     * @param policyName
     *            policyName of the existing component
     * @return new unique name of the Policy
     * @throws ConsoleException
     *             thrown at any error
     */
    String getClonedName(String policyName) throws ConsoleException;

    /**
     * This method will check the existence of the policy for the given name
     * 
     * @param policyName
     *            name of the policy
     *            
     * @return boolean true if exists
     * @throws ConsoleException
     *             thrown at any error
     */
    boolean isPolicyExists(String policyName) throws ConsoleException;
    
    /**
     * find the policy for the given name
     * 
     * @param policyName
     *            name of the policy
     *            
     * @return boolean true if exists
     * @throws ConsoleException
     *             thrown at any error
     */
    List<PolicyLite> getPolicyByName(String policyName);

    /**
     * find the xacml policy for the given name
     *
     * @param policyName
     *            name of the policy
     *
     * @return boolean true if exists
     * @throws ConsoleException
     *             thrown at any error
     */
    List<XacmlPolicyLite> getXacmlPolicyByName(String policyName);

    /**
     * Find the list of deployment dependencies for the given list of policy ids.
     *
     * @param ids policy id list to find dependencies
     * @return list of deployment dependencies
     * @throws ConsoleException if an error occurred during the validation
     */
    Set<DeploymentDependency> findDependencies(List<Long> ids) throws ConsoleException;

    /**
     * Enforce Tag based access control on {@link PolicyLite}. This will method
     * will enforce all the related reference access control including sub
     * policies
     * 
     * @param lite
     *            {@link PolicyLite}
     * @return {@link PolicyLite} with TBAC
     */
    PolicyLite enforceTBAC(PolicyLite lite);

    void move(Long destinationFolderId, List<Long> ids) throws ConsoleException;

    /**
     * Recurse through sub policies, returns true if any one of the sub
     * policy is not in approved state.
     *
     * @param parentPolicyId
     */
    boolean isSubPolicyNotApproved(Long parentPolicyId) throws ConsoleException;
}
