/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 24, 2016
 *
 */
package com.nextlabs.destiny.console.services.policy.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentLite;
import com.nextlabs.destiny.console.dto.policymgmt.DeploymentDependency;
import com.nextlabs.destiny.console.dto.policymgmt.MemberCondition;
import com.nextlabs.destiny.console.dto.policymgmt.MemberDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyComponent;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.dto.policymgmt.SubComponentLite;
import com.nextlabs.destiny.console.dto.policymgmt.SubPolicyLite;
import com.nextlabs.destiny.console.dto.policymgmt.ValidationDetailDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ValidationRecord;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.enums.PolicyStatus;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper;
import com.nextlabs.destiny.console.search.repositories.ComponentSearchRepository;
import com.nextlabs.destiny.console.search.repositories.PolicySearchRepository;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.policy.ComponentMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyDevelopmentEntityMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyMgmtService;
import com.nextlabs.destiny.console.services.policy.ValidatorService;

/**
 * Policy/Component validator Service implementation
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class ValidatorServiceImpl implements ValidatorService {

    private static final Logger log = LoggerFactory
            .getLogger(ValidatorServiceImpl.class);

    @Autowired
    private ComponentMgmtService componentMgmtService;

    @Resource
    private ComponentSearchRepository componentSearchRepository;

    @Autowired
    private PolicyMgmtService policyMgmtService;

    @Autowired
    private PolicyDevelopmentEntityMgmtService devEntityMgmtService;
    
    @Resource
    private PolicySearchRepository policySearchRepository;

    @Autowired
    private MessageBundleService msgBundle;

    @Override
    public ValidationDetailDTO validate(PolicyDTO policyDTO)
            throws ConsoleException {

        ValidationDetailDTO validateDTO = new ValidationDetailDTO(
                policyDTO.getId());

        boolean isEmpty = isPolicyEmpty(policyDTO);
        if (isEmpty) {
            // Validation Record
            ValidationRecord record = new ValidationRecord(policyDTO.getId(),
                    policyDTO.getName(), policyDTO.isDeployed(), "policy");
            record.setMsgCode(msgBundle.getText("warning.entity.empty.code"));
            record.setCategory("policy");
            record.addMessage(msgBundle.getText("policy.is.empty"));

            // Validation Details DTO
            validateDTO.canDeploy(false);
            validateDTO.getDetails().add(record);

            Map<String, String> warningMap = new HashMap<>();
            warningMap.put(msgBundle.getText("warning.entity.empty.code"),
                    msgBundle.getText("warning.entity.empty",
                            policyDTO.getName()));
            validateDTO.setWarnings(warningMap);

        } else {
            validatePolicyInternal(policyDTO, validateDTO);
        }

        log.info("Policy validated successfully, [Policy Id:{}, Results :{}]",
                policyDTO.getId(), validateDTO.isDeployable());

        return validateDTO;
    }

    private void validatePolicyInternal(PolicyDTO policyDTO,
            ValidationDetailDTO validationDTO) throws ConsoleException {

        validateReferenceComponents(validationDTO,
                policyDTO.getSubjectComponents());
        validateReferenceComponents(validationDTO,
                policyDTO.getToSubjectComponents());
        validateReferenceComponents(validationDTO,
                policyDTO.getActionComponents());
        validateReferenceComponents(validationDTO,
                policyDTO.getFromResourceComponents());
        validateReferenceComponents(validationDTO,
                policyDTO.getToResourceComponents());

        // sub-policy
        //TODO : check how to escape slash in elasticsearch
    }

    private void validateReferenceComponents(ValidationDetailDTO validateDTO,
            List<PolicyComponent> policyComponents) throws ConsoleException {
        for (PolicyComponent plcyComponent : policyComponents) {
            for (ComponentDTO component : plcyComponent.getComponents()) {
                ComponentLite comp = componentSearchRepository.findById(component.getId()).orElse(null);
                boolean isRefEmpty = isComponentEmpty(component.getId(), new HashSet<Long>());
                if (isRefEmpty) {
                    // Validation Record
                    String compName = null;
                    String compGroup = null;
                    if (comp != null) {
                        compName = comp.getName();
                        compGroup = comp.getGroup();
                    }
                    ValidationRecord record = new ValidationRecord(
                            component.getId(), compName, component.isDeployed(),
                            "component");
                    record.setMsgCode(
                            msgBundle.getText("warning.entity.empty.code"));
                    record.setCategory(compGroup);
                    record.addMessage(msgBundle.getText("component.is.empty"));

                    // Validation DTO
                    validateDTO.canDeploy(false);
                    validateDTO.getDetails().add(record);

                    Map<String, String> warningMap = new HashMap<>();
                    warningMap.put(
                            msgBundle.getText(
                                    "warning.entity.component.empty.code"),
                            msgBundle
                                    .getText("warning.entity.component.empty"));
                    validateDTO.setWarnings(warningMap);

                } else {
                    validateComponent(validateDTO, component.getId(), new HashSet<Long>());
                }
            }
        }
    }

    @Override
    public ValidationDetailDTO validate(ComponentDTO componentDTO)
            throws ConsoleException {

        ValidationDetailDTO validateDTO = new ValidationDetailDTO(
                componentDTO.getId());

        boolean isEmpty = false;

        if (componentDTO.getId() == null) {
            isEmpty = isNewComponentEmpty(componentDTO);
            if (isEmpty) {
                ValidationRecord record = new ValidationRecord(
                        componentDTO.getId(), componentDTO.getName(),
                        componentDTO.isDeployed(), "component");

                // Validation Record
                record.setMsgCode(
                        msgBundle.getText("warning.entity.empty.code"));
                record.addMessage(msgBundle.getText("component.is.empty"));

                // Validation DTO
                validateDTO.canDeploy(false);
                validateDTO.getDetails().add(record);

                Map<String, String> warningMap = new HashMap<>();
                warningMap.put(msgBundle.getText("warning.entity.empty.code"),
                        msgBundle.getText("warning.entity.empty",
                                componentDTO.getName()));
                validateDTO.setWarnings(warningMap);

            }
        } else {
            isEmpty = isComponentEmpty(componentDTO.getId(), new HashSet<Long>());
            if (isEmpty) {
                ValidationRecord record = new ValidationRecord(
                        componentDTO.getId(), componentDTO.getName(),
                        componentDTO.isDeployed(), "component");

                // Validation Record
                record.setMsgCode(
                        msgBundle.getText("warning.entity.empty.code"));
                record.addMessage(msgBundle.getText("component.is.empty"));

                // Validation DTO
                validateDTO.canDeploy(false);
                validateDTO.getDetails().add(record);

                Map<String, String> warningMap = new HashMap<>();
                warningMap.put(msgBundle.getText("warning.entity.empty.code"),
                        msgBundle.getText("warning.entity.empty",
                                componentDTO.getName()));
                validateDTO.setWarnings(warningMap);

            } else {
                ComponentLite comp = componentSearchRepository.findById(componentDTO.getId()).orElse(null);
                if (comp != null) {
                    for (SubComponentLite subComponent : comp
                            .getSubComponents()) {
                        validateReferenceComponents(validateDTO,
                                subComponent.getId());
                        validateComponent(validateDTO, subComponent.getId(), new HashSet<Long>());
                    }
                }
            }
        }
        log.info(
                "Component validated successfully, [Component Id:{}, Results :{}]",
                componentDTO.getId(), validateDTO.isDeployable());
        return validateDTO;
    }

    private void validateReferenceComponents(ValidationDetailDTO validateDTO,
            Long componentId) throws ConsoleException {

        ComponentLite component = componentSearchRepository.findById(componentId).orElse(null);
        if (component != null) {
            boolean isRefEmpty = isComponentEmpty(component.getId(), new HashSet<Long>());
            if (isRefEmpty) {

                // Validation Record
                ValidationRecord record = new ValidationRecord(
                        component.getId(), component.getName(),
                        component.isDeployed(), "component");
                record.setMsgCode(
                        msgBundle.getText("warning.entity.empty.code"));
                record.setCategory(component.getGroup());
                record.addMessage(msgBundle.getText("component.is.empty"));

                // Validation DTO
                validateDTO.canDeploy(false);
                validateDTO.getDetails().add(record);

                Map<String, String> warningMap = new HashMap<>();
                warningMap.put(
                        msgBundle
                                .getText("warning.entity.component.empty.code"),
                        msgBundle.getText("warning.entity.component.empty"));
                validateDTO.setWarnings(warningMap);

            } else {
                validateComponent(validateDTO, component.getId(), new HashSet<Long>());
            }
        }
    }

    private void validateComponent(ValidationDetailDTO validateDTO, Long id, Set<Long> validatedComponents)
            throws ConsoleException {

        ComponentLite comp = componentSearchRepository.findById(id).orElse(null);
        if (comp != null) {
            ValidationRecord record = new ValidationRecord(comp.getId(),
                    comp.getName(), comp.isDeployed(), "component");

            if (!comp.isDeployed()) {
                record.addMessage(
                        msgBundle.getText("component.not.yet.deployed"));
                record.setMsgCode(msgBundle
                        .getText("warning.entity.component.not.deployed.code"));
                record.setCategory(comp.getGroup());
            }

            if (!record.getMessages().isEmpty()) {
                validateDTO.canDeploy(false);
                validateDTO.getDetails().add(record);

                Map<String, String> warningMap = new HashMap<>();
                warningMap.put(
                        msgBundle.getText(
                                "warning.entity.component.not.deployed.code"),
                        msgBundle.getText(
                                "warning.entity.component.not.deployed"));
                validateDTO.setWarnings(warningMap);
            }
            
            validatedComponents.add(comp.getId());

            for (SubComponentLite subComponent : comp.getSubComponents()) {
                if(!validatedComponents.contains(subComponent.getId())) {
                    validateComponent(validateDTO, subComponent.getId(), validatedComponents);
                } else {
                    log.debug("component [%d] already validated, skip to prevent cyclic problem", subComponent.getId());
                }
            }

        } else {
            // it goes here only for deleted components
            getFromDAO(validateDTO, id);
        }
    }

    /**
     * Checks if a given policy is empty
     * 
     * @param policyDTO
     * @return
     */
    private boolean isPolicyEmpty(PolicyDTO policyDTO) {

        boolean isEmpty = false;

        if ((policyDTO.getActionComponents() != null
                && policyDTO.getActionComponents().isEmpty())
                && (policyDTO.getSubjectComponents() != null
                        && policyDTO.getSubjectComponents().isEmpty())
                && (policyDTO.getToSubjectComponents() != null
                        && policyDTO.getToSubjectComponents().isEmpty())
                && (policyDTO.getFromResourceComponents() != null
                        && policyDTO.getFromResourceComponents().isEmpty())
                && (policyDTO.getToResourceComponents() != null
                        && policyDTO.getToResourceComponents().isEmpty())
                && (policyDTO.getExpression() == null
                        || "".equals(policyDTO.getExpression())))
            isEmpty = true;
        return isEmpty;
    }

    /**
     * Checks is a given component is empty
     * 
     * @param id
     * @return
     * @throws ConsoleException
     */
    private boolean isComponentEmpty(Long id, Set<Long> validatedComponents) {

        boolean isCompEmpty = false;
        ComponentLite comp = componentSearchRepository.findById(id).orElse(null);
        if (comp != null) {
            validatedComponents.add(id);
            isCompEmpty = comp.isEmpty();
            if (isCompEmpty) {
                for (SubComponentLite subComponent : comp.getSubComponents()) {
                    if(!validatedComponents.contains(comp.getId()))
                        isCompEmpty = isComponentEmpty(subComponent.getId(), validatedComponents);
                }
            }
        }
        return isCompEmpty;
    }

    private boolean isNewComponentEmpty(ComponentDTO componentDTO)
            throws ConsoleException {

        boolean isCompEmpty = false;

        if (componentDTO.getActions().isEmpty()
                && componentDTO.getConditions().isEmpty()) {
            isCompEmpty = true;
        }

        if (isCompEmpty) {
            List<ComponentDTO> subComponents = new ArrayList<>();
            for (MemberCondition memberCondition : componentDTO.getMemberConditions()) {
                for (MemberDTO member : memberCondition.getMembers()) {
                    if (!ComponentPQLHelper.MEMBER_GROUP.equals(member.getType())) {
                        ComponentDTO subComponent = componentMgmtService.findById(member.getId());
                        if (subComponent != null) {
                            subComponents.add(subComponent);
                        }
                    }
                }
            }
            for (ComponentDTO subComponent : subComponents) {
                isNewComponentEmpty(subComponent);
            }
        }
        return isCompEmpty;
    }

    private void getFromDAO(ValidationDetailDTO validateDTO, Long id)
            throws ConsoleException {
        ComponentDTO compDTO = componentMgmtService.findById(id);
        if (compDTO == null) {
            return;
        }

        ValidationRecord record = new ValidationRecord(compDTO.getId(),
                compDTO.getName(), compDTO.isDeployed(), "component");

        validateDTO.canDeploy(compDTO.isDeployed());
        if (!compDTO.isDeployed()) {
            record.addMessage(msgBundle.getText("component.not.yet.deployed"));
            validateDTO.canDeploy(false);
        } else if (compDTO.getActions().isEmpty()
                || compDTO.getConditions().isEmpty()
                || compDTO.getMemberConditions().isEmpty()) {

            record.addMessage(msgBundle.getText("component.is.empty"));
            validateDTO.canDeploy(false);
        }

        if (!record.getMessages().isEmpty()) {
            validateDTO.getDetails().add(record);
        }
    }
    
    public List<ValidationDetailDTO> checkForReferences(List<Long> componentIds)
            throws ConsoleException {

        List<ValidationDetailDTO> validateDTOs = new ArrayList<>();
        for (Long componentId : componentIds) {
            ComponentLite compToDelete = componentSearchRepository.findById(componentId).orElse(null);
            if(compToDelete == null) {
                return validateDTOs;
            }

            boolean isReferenced = false;
            ValidationRecord record = new ValidationRecord(compToDelete.getId(),
                    compToDelete.getName(), compToDelete.isDeployed(),
                    "component");
            ValidationDetailDTO validateDTO = new ValidationDetailDTO(
                    componentId);

            // specify any large value for page size to find all
            int pageSize = 100000;

            // get all components
            PageRequest compPageable = PageRequest.of(0, pageSize);
            Page<ComponentLite> compPage = componentSearchRepository
                    .findAll(compPageable);
            List<ComponentLite> components = compPage.getContent();

            // check for references in components
            for (ComponentLite comp : components) {
                List<SubComponentLite> subComponents = comp.getSubComponents();
                for (SubComponentLite subComponent : subComponents) {
                    if (componentId.compareTo(subComponent.getId()) == 0) {
                        log.info(
                                "Component being deleted is referenced by other components or policies");
                        isReferenced = true;
                        break;
                    }
                }
            }

            if (!isReferenced) {
                // get all active policies
                List<PolicyDevelopmentEntity> policies = devEntityMgmtService
                        .findActiveEntitiesByType("PO");
                // check for references in policies
                isReferenced = checkPoliciesForReference(policies, componentId);
            }

            if (isReferenced) {
                record.addMessage(msgBundle.getText("entity.in.use"));
                record.setMsgCode(
                        msgBundle.getText("warning.entity.in.use.code"));
                record.setCategory(compToDelete.getGroup());
            }

            if (!record.getMessages().isEmpty()) {
                validateDTO.canDeploy(false);
                validateDTO.getDetails().add(record);

                Map<String, String> warningMap = new HashMap<>();
                warningMap.put(msgBundle.getText("warning.entity.in.use.code"),
                        msgBundle.getText("warning.entity.in.use"));
                validateDTO.setWarnings(warningMap);
            }

            validateDTOs.add(validateDTO);
        }
        return validateDTOs;
    }
    
    private boolean checkPoliciesForReference(
            List<PolicyDevelopmentEntity> policies, Long componentId) {

        boolean isReferenced = false;

        for (PolicyDevelopmentEntity policy : policies) {
            if (policy != null && policy.getId() != null) {
                Long policyId = policy.getId();
                PolicyDTO policyDTO = null;
                try {
                    policyDTO = policyMgmtService.findById(policyId);
                } catch (ConsoleException ex) {
                    continue;
                }

                if (policyDTO != null) {

                    if (checkPolicyCompForReference(componentId,
                            policyDTO.getActionComponents())) {
                        return true;
                    } else if (checkPolicyCompForReference(componentId,
                            policyDTO.getSubjectComponents())) {
                        return true;
                    } else if (checkPolicyCompForReference(componentId,
                            policyDTO.getToSubjectComponents())) {
                        return true;
                    } else if (checkPolicyCompForReference(componentId,
                            policyDTO.getFromResourceComponents())) {
                        return true;
                    } else {
                        return checkPolicyCompForReference(componentId,
                                policyDTO.getToResourceComponents());
                    }

                }
            }

        }
        return isReferenced;
    }

    private boolean checkPolicyCompForReference(Long componentId,
            List<PolicyComponent> policyComponents) {
        boolean isReferenced = false;
        for (PolicyComponent policyComponent : policyComponents) {
            List<ComponentDTO> components = policyComponent.getComponents();
            for (ComponentDTO component : components) {
                if (componentId.compareTo(component.getId()) == 0) {
                    isReferenced = true;
                    break;
                }
            }
        }
        return isReferenced;
    }

    @Override
    public void findDependenciesOfPolicy(Set<DeploymentDependency> dependencies, Long id, boolean provided)
            throws ConsoleException {
        PolicyDTO policy = policyMgmtService.findById(id);
        if (policy != null) {
            if (provided && policy.getParentId() != null) {
                PolicyDTO parentPolicy = policy;
                do {
                    PolicyDTO currentParent = policyMgmtService.findById(parentPolicy.getParentId());
                    if (currentParent == null) {
                        break;
                    }
                    parentPolicy = currentParent;
                } while (parentPolicy.getParentId() != null);
                if (parentPolicy != null) {
                    id = parentPolicy.getId();
                    policy = parentPolicy;
                }
            }
            if ((provided && id.equals(policy.getId())) || !PolicyStatus.APPROVED.name().equals(policy.getStatus())
                    || policy.isDeploymentPending()) {
                String policyName = policy.getFullName();
                if (StringUtils.isNotEmpty(policyName)) {
                    policyName = policyName.substring(policyName.indexOf('/') + 1);
                }
                dependencies.add(new DeploymentDependency(policy.getId(), DevEntityType.POLICY, policyName, policy.getFolderPath(),
                        policy.isDeployed(), false, policy.getParentId() != null));
            }
            for (List<PolicyComponent> policyComponents : Arrays.asList(policy.getSubjectComponents(),
                    policy.getToSubjectComponents(), policy.getFromResourceComponents(),
                    policy.getToResourceComponents(), policy.getActionComponents())) {
                for (PolicyComponent policyComponent : policyComponents) {
                    for (ComponentDTO component : policyComponent.getComponents()) {
                        findDependenciesOfComponent(dependencies, component.getId(), false);
                    }
                }
            }
            PolicyLite policyLite = policySearchRepository.findById(id).orElse(null);
            if (policyLite != null) {
                for (SubPolicyLite subPolicyLite : policyLite.getSubPolicies()) {
                    if (dependencies.stream().noneMatch(dependency -> dependency.getId() == subPolicyLite.getId())) {
                        findDependenciesOfPolicy(dependencies, subPolicyLite.getId(), false);
                    }
                }
            }
        }
    }

    @Override
    public void findDependenciesOfComponent(Set<DeploymentDependency> dependencies, Long id, boolean provided)
            throws ConsoleException {
        ComponentDTO component = componentMgmtService.findById(id);
        if (component != null) {
            if (provided || !PolicyStatus.APPROVED.name().equals(component.getStatus())
                    || component.isDeploymentPending()) {
                DeploymentDependency dependency = new DeploymentDependency(component.getId(), DevEntityType.COMPONENT,
                        component.getName(), component.getFolderPath(), component.isDeployed(), false,
                        false);
                dependency.setGroup(component.getType());
                dependencies.add(dependency);
            }
            for (MemberCondition memberCondition: component.getMemberConditions()) {
                for(MemberDTO member : memberCondition.getMembers()) {
                    if (!ComponentPQLHelper.MEMBER_GROUP.equals(member.getType())
                            && dependencies.stream().noneMatch(dependency -> dependency.getId() == member.getId())) {
                        findDependenciesOfComponent(dependencies, member.getId(), false);
                    }
                }
            }
        }
    }
}
