/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 3, 2016
 *
 */
package com.nextlabs.destiny.console.services.policy.impl;

import static com.bluejungle.pf.domain.epicenter.common.SpecType.RESOURCE;
import static com.nextlabs.destiny.console.enums.AuditLogComponent.COMPONENT_MGMT;
import static com.nextlabs.destiny.console.enums.DevEntityType.COMPONENT;
import static com.nextlabs.destiny.console.enums.DevEntityType.POLICY;
import static com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus.APPROVED;
import static com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus.DELETED;
import static com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus.OBSOLETE;
import static com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus.getByKey;
import static com.nextlabs.destiny.console.utils.SecurityContextUtil.getCurrentUser;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bluejungle.framework.expressions.BooleanOp;
import com.bluejungle.framework.expressions.CompositePredicate;
import com.bluejungle.framework.expressions.IPredicate;
import com.bluejungle.pf.destiny.lib.LeafObject;
import com.bluejungle.pf.destiny.lib.LeafObjectSearchSpec;
import com.bluejungle.pf.destiny.lib.LeafObjectType;
import com.bluejungle.pf.destiny.lifecycle.EntityType;
import com.bluejungle.pf.destiny.services.PolicyEditorException;
import com.bluejungle.pf.domain.epicenter.common.SpecType;
import com.nextlabs.destiny.console.dao.EntityAuditLogDao;
import com.nextlabs.destiny.console.delegadmin.actions.DelegationModelActions;
import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDeploymentHistoryDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentLite;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentPreviewDTO;
import com.nextlabs.destiny.console.dto.policymgmt.DeploymentDependency;
import com.nextlabs.destiny.console.dto.policymgmt.DeploymentRequestDTO;
import com.nextlabs.destiny.console.dto.policymgmt.DeploymentResponseDTO;
import com.nextlabs.destiny.console.dto.policymgmt.IncludedComponentLite;
import com.nextlabs.destiny.console.dto.policymgmt.MemberCondition;
import com.nextlabs.destiny.console.dto.policymgmt.MemberDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ObligationDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyComponent;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyModelDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PushResultDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SubComponentLite;
import com.nextlabs.destiny.console.dto.policymgmt.ValidationDetailDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ValidationRecord;
import com.nextlabs.destiny.console.enums.ActionType;
import com.nextlabs.destiny.console.enums.AuditAction;
import com.nextlabs.destiny.console.enums.AuditableEntity;
import com.nextlabs.destiny.console.enums.AuthorizableType;
import com.nextlabs.destiny.console.enums.DelegationModelShortName;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.enums.Operator;
import com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.enums.PolicyStatus;
import com.nextlabs.destiny.console.exceptions.CircularReferenceException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.DirtyUpdateException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.exceptions.NotUniqueException;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.model.policy.ComponentExtDescription;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentEntity;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.model.policy.PolicyModel;
import com.nextlabs.destiny.console.policy.handlers.PolicyLifeCycleHandler;
import com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper;
import com.nextlabs.destiny.console.repositories.FolderRepository;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.search.repositories.ComponentSearchRepository;
import com.nextlabs.destiny.console.services.AuditLogService;
import com.nextlabs.destiny.console.services.DPSProxyService;
import com.nextlabs.destiny.console.services.MemberService;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.TagLabelService;
import com.nextlabs.destiny.console.services.delegadmin.AccessControlService;
import com.nextlabs.destiny.console.services.policy.ComponentMgmtService;
import com.nextlabs.destiny.console.services.policy.ComponentSearchService;
import com.nextlabs.destiny.console.services.policy.PolicyDeploymentEntityMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyDevelopmentEntityMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyModelService;
import com.nextlabs.destiny.console.services.policy.ValidatorService;
import com.nextlabs.destiny.console.utils.JavaBeanCopier;

/**
 * Policy Component management service implementation
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class ComponentMgmtServiceImpl implements ComponentMgmtService {

    private static final Logger log = LoggerFactory
            .getLogger(ComponentMgmtServiceImpl.class);

    public static final int MAX_ALLOWED_LENGTH = 247;

    @Autowired
    private PolicyDevelopmentEntityMgmtService devEntityMgmtService;

    @Autowired
    private PolicyDeploymentEntityMgmtService deploymentMgmtService;

    @Autowired
    private PolicyModelService policyModelService;

    @Autowired
    private PolicyLifeCycleHandler policyLifeCycleHandler;

    @Autowired
    private ComponentSearchService componentSearchService;

    @Resource
    private ComponentSearchRepository componentSearchRepository;

    @Autowired
    private MemberService memberService;

    @Resource
    private ApplicationUserSearchRepository appUserSearchRepository;

    @Autowired
    private MessageBundleService msgBundle;

    @Autowired
    private TagLabelService tagLabelService;

    @Autowired
    private ValidatorService validatorService;

    @Autowired
    private PolicyMgmtService policyMgmtService;

    @Autowired
    private AccessControlService accessControlService;

    @Autowired
    private AuditLogService auditService;

    @Autowired
    private EntityAuditLogDao entityAuditLogDao;

    @Autowired
    private DPSProxyService dpsProxyService;

    @Autowired
    private FolderRepository folderRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ComponentDTO save(ComponentDTO componentDTO) throws ConsoleException, CircularReferenceException {
        return save(componentDTO, CheckCircularRefs.YES);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ComponentDTO save(ComponentDTO componentDTO, CheckCircularRefs checkCircularReferences)
            throws ConsoleException, CircularReferenceException {
        try {
            log.debug("Component save has began");

            if (componentDTO != null) {
                PolicyDevelopmentEntity devEntity = saveComponent(componentDTO, checkCircularReferences);
                componentDTO.setId(devEntity.getId());

                log.info("Component [{}] saved successfully, [Component Id :{}]", devEntity.getTitle(),
                        devEntity.getId());

                if (!componentDTO.isHidden()) {
                    auditService.save(COMPONENT_MGMT.name(), "audit.new.component",
                            componentDTO.getName());
                }
            }
            return componentDTO;

        } catch (JsonProcessingException ex) {
            log.error("Error occurred in save new component", ex);
            throw new ConsoleException("Error occurred in save new component",
                    ex);
        }
    }

    private PolicyDevelopmentEntity saveComponent(ComponentDTO componentDTO, CheckCircularRefs checkCircularReferences)
            throws JsonProcessingException, ConsoleException, CircularReferenceException {

        if (DevEntityType.DELEGATION_COMPONENT.equals(componentDTO.getCategory())) {
            accessControlService.checkAuthority(DelegationModelActions.MANAGE_DELEGATED_ADMIN, ActionType.MANAGE,
                    AuthorizableType.DELEGATION_COMPONENT);
        } else {
            accessControlService.authorizeByTags(ActionType.INSERT,
                    DelegationModelShortName.COMPONENT_ACCESS_TAGS,
                    componentDTO,
                    true);
        }

        if (checkCircularReferences == CheckCircularRefs.YES) {
            detectCircularReferences(componentDTO);
        }

        PolicyDevelopmentEntity devEntity = new PolicyDevelopmentEntity();
        devEntity.setFolderId(componentDTO.getFolderId());
        devEntity.setTitle(componentDTO.getType().toUpperCase() + "/"
                + componentDTO.getName());
        devEntity.setDescription(componentDTO.getDescription());
        devEntity.setHidden(Boolean.FALSE);
        devEntity.setType(componentDTO.getCategory().getKey()); // CO

        PolicyDevelopmentStatus policyStatus = PolicyDevelopmentStatus
                .get(componentDTO.getStatus());
        devEntity.setStatus(policyStatus.getKey());

        devEntity.setCreatedDate(System.currentTimeMillis());
        devEntity.setLastUpdatedDate(System.currentTimeMillis());
        devEntity.setPql(newPQL(-1L, devEntity.getTitle()));
        devEntity.setApPql("  ");
        devEntity.setOwner(getCurrentUser().getUserId());

        String extendedDescStr = getExtentedDescAsString(componentDTO);
        devEntity.setExtendedDescription(extendedDescStr);

        // Add Tags
        addTags(componentDTO, devEntity);
        devEntity = devEntityMgmtService.save(devEntity);

        PolicyModel policyModel = getPolicyModel(componentDTO);
        // No policy model found, getPolicyModel(ComponetDTO may return null)
        if (policyModel != null) {
            componentDTO.getPolicyModel().setName(policyModel.getName());
            componentDTO.getPolicyModel().setVersion(policyModel.getVersion());
        }

        String pql = getComponentPQL(componentDTO, devEntity, policyModel);
        devEntity.setPql(pql);
        devEntityMgmtService.save(devEntity);
        componentDTO.setId(devEntity.getId());
        componentSearchService.reIndexComponents(componentDTO);
        entityAuditLogDao.addEntityAuditLog(AuditAction.CREATE, getEntityType(componentDTO.getType()),
                devEntity.getId(), null, componentDTO.toAuditString());
        return devEntity;
    }

    private String getComponentPQL(ComponentDTO componentDTO,
                                   PolicyDevelopmentEntity devEntity, PolicyModel policyModel) {
        return ComponentPQLHelper.create().getPQL(devEntity.getId(),
                devEntity.getTitle(), devEntity.getDescription(),
                SpecType.RESOURCE, componentDTO, policyModel,
                PolicyModelType.get(componentDTO.getType()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ComponentDTO modify(ComponentDTO componentDTO)
            throws ConsoleException, CircularReferenceException {
        return modify(componentDTO, CheckCircularRefs.YES);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ComponentDTO modify(ComponentDTO componentDTO, CheckCircularRefs checkCircularReferences)
            throws ConsoleException, CircularReferenceException {
        try {
            log.debug("Component modify has began");
            PolicyDevelopmentEntity devEntity = modifyComponent(componentDTO, checkCircularReferences);

            componentDTO.setId(devEntity.getId());
            log.info("Component modified successfully, [Component Id :{}]",
                    devEntity.getId());

            return componentDTO;
        } catch (JsonProcessingException ex) {
            log.error("Error occurred in save new component", ex);
            throw new ConsoleException("Error occurred in update component",
                    ex);
        }
    }

    private PolicyDevelopmentEntity modifyComponent(ComponentDTO componentDTO, CheckCircularRefs checkCircularReferences)
            throws ConsoleException, JsonProcessingException, CircularReferenceException {

        if (checkCircularReferences == CheckCircularRefs.YES) {
            detectCircularReferences(componentDTO);
        }

        PolicyDevelopmentEntity devEntity = devEntityMgmtService
                .findById(componentDTO.getId());

        if (DevEntityType.DELEGATION_COMPONENT.equals(componentDTO.getCategory())) {
            accessControlService.checkAuthority(DelegationModelActions.MANAGE_DELEGATED_ADMIN, ActionType.MANAGE,
                    AuthorizableType.DELEGATION_COMPONENT);
        } else {
            accessControlService.authorizeByTags(ActionType.EDIT,
                    DelegationModelShortName.COMPONENT_ACCESS_TAGS,
                    devEntity,
                    false);
            accessControlService.authorizeByTags(ActionType.EDIT,
                    DelegationModelShortName.COMPONENT_ACCESS_TAGS,
                    componentDTO,
                    true);
        }

        if (componentDTO.getVersion() != -1 && componentDTO.getVersion() < devEntity.getVersion()) {
            log.info("DirtyUpdateException occurred for component named = {}, id = {}", componentDTO.getName(), componentDTO.getId());
            throw new DirtyUpdateException(msgBundle.getText("server.error.dirty.update.code"),
                    msgBundle.getText("server.error.dirty.update"));
        }

        ComponentDTO beforeUpdate = findById(componentDTO.getId());
        PolicyModel policyModelBeforeUpdate = getPolicyModel(beforeUpdate);
        // No policy model found, getPolicyModel(ComponetDTO may return null)
        if (policyModelBeforeUpdate != null) {
            beforeUpdate.getPolicyModel().setName(policyModelBeforeUpdate.getName());
            beforeUpdate.getPolicyModel().setVersion(policyModelBeforeUpdate.getVersion());
        }
        PolicyDevelopmentStatus policyStatus = PolicyDevelopmentStatus
                .get(componentDTO.getStatus());

        devEntity.setTitle(componentDTO.getType().toUpperCase() + "/"
                + componentDTO.getName());
        devEntity.setFolderId(componentDTO.getFolderId());
        devEntity.setDescription(componentDTO.getDescription());
        devEntity.setHidden(Boolean.FALSE);
        devEntity.setType(componentDTO.getCategory().getKey());
        devEntity.setStatus(policyStatus.getKey());
        devEntity.setLastUpdatedDate(System.currentTimeMillis());
        devEntity.setApPql("  ");
        devEntity.setOwner(getCurrentUser().getUserId());

        String extendedDescStr = getExtentedDescAsString(componentDTO);
        devEntity.setExtendedDescription(extendedDescStr);

        // Add Tags
        addTags(componentDTO, devEntity);

        PolicyModel policyModel = getPolicyModel(componentDTO);
        // No policy model found, getPolicyModel(ComponetDTO may return null)
        if (policyModel != null) {
            componentDTO.getPolicyModel().setName(policyModel.getName());
            componentDTO.getPolicyModel().setVersion(policyModel.getVersion());
        }

        String pql = getComponentPQL(componentDTO, devEntity, policyModel);
        devEntity.setPql(pql);
        devEntityMgmtService.save(devEntity);
        componentDTO.setId(devEntity.getId());
        componentSearchService.reIndexComponents(componentDTO);
        ComponentDTO componentDTOCopy = SerializationUtils.clone(componentDTO);
        componentDTOCopy.setVersion(devEntity.getVersion());
        entityAuditLogDao.addEntityAuditLog(PolicyDevelopmentStatus.DELETED.equals(policyStatus) ? AuditAction.DELETE : AuditAction.UPDATE,
                getEntityType(componentDTO.getType()), devEntity.getId(),
                beforeUpdate.toAuditString(), PolicyDevelopmentStatus.DELETED.equals(policyStatus) ? null : componentDTOCopy.toAuditString());

        return devEntity;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ComponentDTO addSubComponent(ComponentDTO componentDTO)
            throws ConsoleException, CircularReferenceException {
        return addSubComponent(componentDTO, CheckCircularRefs.YES);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ComponentDTO addSubComponent(ComponentDTO componentDTO, CheckCircularRefs checkCircularReferences)
            throws ConsoleException, CircularReferenceException {
        log.debug("Add sub component save has began");

        if (checkCircularReferences == CheckCircularRefs.YES) {
            detectCircularReferences(componentDTO);
        }

        ComponentDTO subComponent = save(componentDTO);
        ComponentDTO parentComponent = findById(componentDTO.getParentId());

        MemberCondition memberCondition = new MemberCondition();
        memberCondition.setOperator(Operator.IN);
        memberCondition.getMembers().add(new MemberDTO(subComponent.getId(), subComponent.getType()));
        parentComponent.getMemberConditions().add(memberCondition);

        modify(parentComponent);

        subComponent.setParentName(parentComponent.getName());

        log.info("SubComponent added successfully, [Component Id :{}]", subComponent.getId());
        auditService.save(COMPONENT_MGMT.name(), "audit.new.sub.component", componentDTO.getName());

        return subComponent;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ComponentDTO updateStatus(Long componentId,
                                     PolicyDevelopmentStatus devStatus) throws ConsoleException {
        try {
            log.debug("Component modify has began");

            PolicyDevelopmentEntity devEntity = devEntityMgmtService
                    .findById(componentId);

            devEntity.setLastUpdatedDate(System.currentTimeMillis());
            devEntity.setStatus(devStatus.getKey());

            ComponentPQLHelper pqlHelper = ComponentPQLHelper.create();
            ComponentDTO componentDTO = pqlHelper.fromPQL(devEntity.getPql());
            componentDTO.setStatus(devStatus.getDevValue().getName());
            populateExtendedDesc(devEntity.getExtendedDescription(),
                    componentDTO);
            PolicyModel policyModel = getPolicyModel(componentDTO);

            String updatedPql = pqlHelper.getPQL(devEntity.getId(),
                    devEntity.getTitle(), devEntity.getDescription(),
                    SpecType.RESOURCE, componentDTO, policyModel,
                    PolicyModelType.get(componentDTO.getType()));

            devEntity.setPql(updatedPql);
            devEntityMgmtService.save(devEntity);

            componentDTO.setId(devEntity.getId());
            if (!devEntity.getType()
                    .equals(DevEntityType.DELEGATION_COMPONENT.getKey())) {
                componentSearchService.reIndexComponents(componentDTO);
            }

            log.info(
                    "Component status updated successfully, [Component Id :{}, Status :{}]",
                    devEntity.getId(), devStatus.getLabel());
            return componentDTO;
        } catch (Exception ex) {
            throw new ConsoleException(
                    "Error occurred in component status update", ex);
        }
    }

    private PolicyModel getPolicyModel(ComponentDTO componentDTO)
            throws ConsoleException {
        PolicyModel policyModel = null;
        if (componentDTO.getPolicyModel() != null
                && componentDTO.getPolicyModel().getId() != null
                && componentDTO.getPolicyModel().getId() != 0L) {
            policyModel = policyModelService.findById(componentDTO.getPolicyModel().getId());
            if (policyModel != null) {
                policyModel.setExtraSubjectAttributes(policyModelService.loadExtraSubjectAttributes(policyModel.getShortName()));
            }
        }
        return policyModel;
    }

    private void addTags(ComponentDTO componentDTO,
                         PolicyDevelopmentEntity devEntity) throws ConsoleException {
        devEntity.getTags().clear();
        for (TagDTO tagDTO : componentDTO.getTags()) {
            TagLabel tag = tagLabelService.findById(tagDTO.getId());
            if (tag != null) {
                devEntity.getTags().add(tag);
            }
        }
    }

    private String getExtentedDescAsString(ComponentDTO componentDTO)
            throws JsonProcessingException {
        ComponentExtDescription extendedDesc = new ComponentExtDescription();
        if (componentDTO != null) {
            if (componentDTO.getPolicyModel() != null
                    && componentDTO.getPolicyModel().getId() != null) {
                extendedDesc.setPolicyModelId(componentDTO.getPolicyModel().getId());
            }
            extendedDesc.setPreCreated(componentDTO.isPreCreated());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(extendedDesc);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public ComponentDTO findById(Long id) throws ConsoleException {
        try {
            PolicyDevelopmentEntity devEntity = devEntityMgmtService.findById(id);
            if (devEntity == null) {
                log.info("No component found for given id: {} ", id);
                return null;
            }

            String pql = devEntity.getPql();
            ComponentDTO component = ComponentPQLHelper.create().fromPQL(pql);
            component.setId(devEntity.getId());
            component.setFolderId(devEntity.getFolderId());
            component.setFolderPath(devEntity.getFolder() == null ? null : devEntity.getFolder().getFolderPath());
            component.setVersion(devEntity.getVersion());
            component.setActionType(devEntity.getActionType());
            component.setDeploymentTime(devEntity.getDeploymentTime());
            component.setRevisionCount(devEntity.getRevisionCount());
            component.setStatus(getByKey(devEntity.getStatus()).name());
            component.setCategory(DevEntityType.getByKey(devEntity.getType()));

            populateExtendedDesc(devEntity.getExtendedDescription(), component);
            populateTags(devEntity, component);
            populateMemberDetails(component);
            component.setCreatedDate(devEntity.getCreatedDate());
            component.setOwnerId(devEntity.getOwner());
            appUserSearchRepository.findById(devEntity.getOwner())
                    .ifPresent(owner -> component.setOwnerDisplayName(owner.getDisplayName()));

            component.setLastUpdatedDate(devEntity.getLastUpdatedDate());
            if (devEntity.getModifiedBy() != null) {
                component.setModifiedById(devEntity.getModifiedBy());
                appUserSearchRepository.findById(devEntity.getModifiedBy())
                        .ifPresent(modifiedBy -> component.setModifiedBy(modifiedBy.getDisplayName()));
            }

            return component;
        } catch (Exception ex) {
            throw new ConsoleException("Error occurred in find component by id",
                    ex);
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public ComponentDTO findActiveById(Long id) throws ConsoleException {
        ComponentDTO component = findById(id);
        if (component != null
                && DELETED.getKey().equals(component.getStatus())) {
            log.debug("No Policy found or trying access deleted component, [ Id:{}, Is deleted:{} ]",
                            id, true);
            return null;
        }

        return component;
    }

    private void populateMemberDetails(ComponentDTO componentDTO) {
        for (MemberCondition membersCondition : componentDTO.getMemberConditions()) {
            for (MemberDTO member : membersCondition.getMembers()) {
                if (ComponentPQLHelper.MEMBER_GROUP.equals(member.getType())) {
                    List<MemberDTO> memberFound = memberService.findMemberById(LeafObjectType.forName(member.getMemberType()), member.getId().toString());
                    if (memberFound != null && !memberFound.isEmpty()) {
                        member.setName(memberFound.get(0).getName());
                        member.setUniqueName(memberFound.get(0).getUniqueName());
                        member.setDomainName(memberFound.get(0).getDomainName());
                    } else {
                        member.setName("Entry not found - " + Math.abs(member.getId()));
                        member.setNotFound(true);
                    }
                } else {
                    ComponentLite lite = componentSearchRepository.findById(member.getId()).orElse(null);
                    if (lite != null) {
                        member.setType(lite.getGroup());
                        member.setName(lite.getName());
                        member.setDescription(lite.getDescription());
                        member.setStatus(lite.getStatus());
                    } else {
                        member.setName("Entry not found - " + Math.abs(member.getId()));
                        member.setNotFound(true);
                    }
                }
            }
        }
    }

    private void populateTags(PolicyDevelopmentEntity devEntity,
                              ComponentDTO component) {
        for (TagLabel tag : devEntity.getTags()) {
            component.getTags().add(TagDTO.getDTO(tag));
        }
    }

    private void populateExtendedDesc(String extendedDesc,
                                      ComponentDTO component)
            throws IOException {
        if (StringUtils.isNotEmpty(extendedDesc)) {
            ObjectMapper objectMapper = new ObjectMapper();
            ComponentExtDescription componentDesc = objectMapper
                    .readValue(extendedDesc, ComponentExtDescription.class);

            PolicyModelDTO policyModel = new PolicyModelDTO();
            policyModel.setId(componentDesc.getPolicyModelId());
            component.setPolicyModel(policyModel);
            component.setPreCreated(componentDesc.isPreCreated());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ComponentDTO remove(Long id) throws ConsoleException {
        PolicyDevelopmentEntity devEntity = devEntityMgmtService.findById(id);
        if (devEntity != null) {

            accessControlService.authorizeByTags(ActionType.DELETE,
                    DelegationModelShortName.COMPONENT_ACCESS_TAGS,
                    devEntity,
                    false);

            if (PolicyStatus.APPROVED.name().equals(getByKey(devEntity.getStatus()).name())) {
                accessControlService.authorizeByTags(ActionType.DEPLOY,
                        DelegationModelShortName.COMPONENT_ACCESS_TAGS,
                        devEntity,
                        false);
            }

            Long componentId = devEntity.getId();
            ComponentDTO componentDTO = findById(componentId);

            PolicyModel policyModel = getPolicyModel(componentDTO);
            // No policy model found, getPolicyModel(ComponetDTO may return null)
            if (policyModel != null) {
                componentDTO.getPolicyModel().setName(policyModel.getName());
                componentDTO.getPolicyModel().setVersion(policyModel.getVersion());
            }

            Map<String, List<Long>> references = getAllReferences(componentId);
            if (!references.isEmpty()) {
                removeComponentReference(references, componentId);
            }
            policyLifeCycleHandler.unDeployEntity(devEntity, DELETED);
            componentSearchRepository.deleteById(id);

            auditService.save(COMPONENT_MGMT.name(), "audit.delete.component",
                    devEntity.getNameFromTitle());

            entityAuditLogDao.addEntityAuditLog(AuditAction.DELETE,
                    getEntityType(componentDTO.getType()), componentId, componentDTO.toAuditString(), null);

            return componentDTO;
        } else {
            throw new NoDataFoundException(
                    msgBundle.getText("no.entity.found.delete.code"),
                    msgBundle.getText("no.entity.found.delete", "Component"));
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<ComponentDTO> remove(List<Long> ids) throws ConsoleException {
        List<PolicyDevelopmentEntity> authorizedDevEntities = new ArrayList<>();
        List<ComponentDTO> removedComponents = new ArrayList<>();

        // Components will be removed only if the user has permission to remove all requested components.
        for (Long id : ids) {
            PolicyDevelopmentEntity devEntity = devEntityMgmtService.findById(id);
            if (devEntity != null) {
                accessControlService.authorizeByTags(ActionType.DELETE,
                        DelegationModelShortName.COMPONENT_ACCESS_TAGS,
                        devEntity,
                        false);

                if (PolicyStatus.APPROVED.name().equals(getByKey(devEntity.getStatus()).name())) {
                    accessControlService.authorizeByTags(ActionType.DEPLOY,
                            DelegationModelShortName.COMPONENT_ACCESS_TAGS,
                            devEntity,
                            false);
                }

                authorizedDevEntities.add(devEntity);
            }
        }

        for (PolicyDevelopmentEntity devEntity : authorizedDevEntities) {
            Long componentId = devEntity.getId();
            ComponentDTO componentDTO = findById(componentId);
            Map<String, List<Long>> references = getAllReferences(
                    componentId);
            if (!references.isEmpty()) {
                removeComponentReference(references, componentId);
            }
            policyLifeCycleHandler.unDeployEntity(devEntity, DELETED);

            removedComponents.add(componentDTO);
            auditService.save(COMPONENT_MGMT.name(),
                    "audit.delete.component", devEntity.getNameFromTitle());

            entityAuditLogDao.addEntityAuditLog(AuditAction.DELETE,
                    getEntityType(componentDTO.getType()), componentId, componentDTO.toAuditString(), null);
        }

        for (Long id : ids) {
            componentSearchRepository.deleteById(id);
        }

        return removedComponents;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public DeploymentResponseDTO saveAndDeploy(ComponentDTO componentDTO) throws ConsoleException, CircularReferenceException {
        return saveAndDeploy(componentDTO, CheckCircularRefs.YES);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public DeploymentResponseDTO saveAndDeploy(ComponentDTO componentDTO, CheckCircularRefs checkCircularReferences)
            throws ConsoleException, CircularReferenceException {
        PolicyDevelopmentEntity entity;
        DeploymentResponseDTO responseDTO;
        try {
            if (componentDTO.getId() != null) {
                entity = modifyComponent(componentDTO, checkCircularReferences);
                log.info("Component details updated, Component Id:{}",
                        componentDTO.getId());
            } else {
                if (componentDTO.getParentId() != null) {
                    ComponentDTO subComponent = addSubComponent(componentDTO);
                    componentDTO.getDeploymentRequest().setId(subComponent.getId());
                    entity = devEntityMgmtService.findById(componentDTO.getId());
                } else {
                    entity = saveComponent(componentDTO, checkCircularReferences);
                    componentDTO.setId(entity.getId());
                    componentDTO.getDeploymentRequest().setId(entity.getId());
                }
                log.info("New Component details saved, Component Id:{}",
                        entity.getId());
            }
            List<DeploymentRequestDTO> deploymentRequests = new ArrayList<>();
            deploymentRequests.add(componentDTO.getDeploymentRequest());
            deploy(deploymentRequests);
            responseDTO = new DeploymentResponseDTO(entity.getId());
            log.info("Component deployment triggered, Component Id:{}",
                    entity.getId());

            if (componentDTO.getDeploymentRequest().isPush()) {
                // Schedule time should be the server time.
                List<PushResultDTO> pushResults = dpsProxyService.schedulePush(new Date());
                responseDTO.setPushResults(pushResults);
                log.info("Component update push requested, [Component Id:{}]", entity.getId());
            }
        } catch (JsonProcessingException e) {
            throw new ConsoleException(
                    "Error encountered in Component save and deployement", e);
        }
        return responseDTO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<ValidationDetailDTO> validateAndDeploy(List<DeploymentRequestDTO> deploymentRequests)
            throws ConsoleException {
        Map<Long, ComponentDTO> authorizedComponents = new HashMap<>();

        // Components will be deployed only if the user has permission to deploy all requested components.
        for (DeploymentRequestDTO deploymentRequestDTO : deploymentRequests) {
            ComponentDTO component = findById(deploymentRequestDTO.getId());
            if (component != null) {
                accessControlService.authorizeByTags(ActionType.DEPLOY,
                        DelegationModelShortName.COMPONENT_ACCESS_TAGS,
                        component,
                        false);
                authorizedComponents.put(component.getId(), component);
            }
        }

        List<ValidationDetailDTO> validateDTOList = new ArrayList<>();
        boolean push = false;
        try {
            for (DeploymentRequestDTO deploymentRequest : deploymentRequests) {
                ComponentDTO component = authorizedComponents.get(deploymentRequest.getId());
                ValidationDetailDTO validationDetails = validatorService
                        .validate(component);
                Set<DeploymentRequestDTO> toBeDeployedSet = new TreeSet<>();
                if (validationDetails.isDeployable()) {
                    for (ValidationRecord record : validationDetails
                            .getDetails()) {
                        if (!record.isDeployed()) {
                            toBeDeployedSet.add(new DeploymentRequestDTO(record.getId(), DevEntityType.COMPONENT,
                                    false, deploymentRequest.getDeploymentTime(),
                                    false));
                        }
                    }
                    toBeDeployedSet.add(new DeploymentRequestDTO(deploymentRequest.getId(), DevEntityType.COMPONENT,
                            false, deploymentRequest.getDeploymentTime(), false));
                    deploy(new ArrayList<>(toBeDeployedSet));
                    push = push || deploymentRequest.isPush();
                }
                validateDTOList.add(validationDetails);
                componentSearchService.reIndexComponents(component);
            }

            if (push) {
                // Schedule time should be the server time.
                List<PushResultDTO> pushResults = dpsProxyService.schedulePush(new Date());
                validateDTOList.forEach(validationDetailDTO -> validationDetailDTO.setPushResults(pushResults));
            }

            if (log.isInfoEnabled()) {
                log.info(
                        "Component and its sub components have been validated and deployed{}successfully, [ Component Ids: {}]",
                        push ? " (push) " : " ",
                        validateDTOList.stream()
                                .map(validationDetail -> String.valueOf(validationDetail.getId()))
                                .collect(Collectors.joining(",")));
            }
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in component validation and deployement",
                    e);
        }
        return validateDTOList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deploy(DeploymentRequestDTO deploymentRequest) throws ConsoleException {
        try {
            PolicyDevelopmentEntity devEntity = devEntityMgmtService.findById(deploymentRequest.getId());
            ComponentDTO beforeUpdate = findById(deploymentRequest.getId());
            PolicyModel policyModelBeforeUpdate = getPolicyModel(beforeUpdate);
            // No policy model found, getPolicyModel(ComponetDTO may return null)
            if (policyModelBeforeUpdate != null) {
                beforeUpdate.getPolicyModel().setName(policyModelBeforeUpdate.getName());
                beforeUpdate.getPolicyModel().setVersion(policyModelBeforeUpdate.getVersion());
            }
            policyLifeCycleHandler.deployEntity(devEntity, deploymentRequest.getDeploymentTime());

            ComponentDTO componentDTO = findById(devEntity.getId());
            componentDTO.setStatus(APPROVED.name());
            PolicyModel policyModel = getPolicyModel(componentDTO);
            // No policy model found, getPolicyModel(ComponetDTO may return null)
            if (policyModel != null) {
                componentDTO.getPolicyModel().setName(policyModel.getName());
                componentDTO.getPolicyModel().setVersion(policyModel.getVersion());
            }

            componentSearchService.reIndexComponents(componentDTO);
            entityAuditLogDao.addEntityAuditLog(AuditAction.DEPLOY,
                    getEntityType(componentDTO.getType()), componentDTO.getId(),
                    beforeUpdate.toAuditString(), componentDTO.toAuditString());

            auditService.save(COMPONENT_MGMT.name(), "audit.deploy.component",
                    devEntity.getNameFromTitle());
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in component deployement", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<DeploymentResponseDTO> deploy(List<DeploymentRequestDTO> deploymentRequests) throws
            ConsoleException {

        List<DeploymentRequestDTO> dependencyDeploymentRequests = new ArrayList<>();
        for (DeploymentRequestDTO deploymentRequest : deploymentRequests) {
            if (deploymentRequest.isDeployDependencies()) {
                dependencyDeploymentRequests.addAll(findDependencies(ImmutableList.of(deploymentRequest.getId())).stream()
                        .filter(dependency -> !dependency.isOptional())
                        .map(dependency -> new DeploymentRequestDTO(dependency.getId(),
                                dependency.getType(), deploymentRequest.isPush(),
                                deploymentRequest.getDeploymentTime(), false))
                        .collect(Collectors.toList()));
            }
        }
        deploymentRequests.addAll(dependencyDeploymentRequests);
        deploymentRequests = deploymentRequests.stream().distinct().collect(Collectors.toList());

        // Components will be deployed only if the user has permission to deploy all requested components.
        for (DeploymentRequestDTO deploymentRequestDTO : deploymentRequests) {
            PolicyDevelopmentEntity devEntity = devEntityMgmtService.findById(deploymentRequestDTO.getId());
            if (devEntity != null) {
                accessControlService.authorizeByTags(ActionType.DEPLOY,
                        DelegationModelShortName.COMPONENT_ACCESS_TAGS,
                        devEntity,
                        false);
            }
        }

        boolean push = false;
        List<DeploymentResponseDTO> deploymentResponses = new ArrayList<>();
        for (DeploymentRequestDTO deploymentRequest : deploymentRequests) {
            deploy(deploymentRequest);
            push = push || deploymentRequest.isPush();
            deploymentResponses.add(new DeploymentResponseDTO(deploymentRequest.getId()));
        }

        if (push) {
            List<PushResultDTO> pushResults = dpsProxyService.schedulePush(new Date());
            deploymentResponses.forEach(deploymentResponse -> deploymentResponse.setPushResults(pushResults));
            if (log.isInfoEnabled()) {
                log.info("Component update push requested, [Component Ids:{}]",
                        deploymentResponses.stream()
                                .map(deploymentResponse -> String.valueOf(deploymentResponse.getId()))
                                .collect(Collectors.joining(",")));
            }
        }

        return deploymentResponses;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void unDeploy(PolicyDevelopmentEntity devEntity) throws ConsoleException {
        try {
            ComponentDTO beforeUpdate = findById(devEntity.getId());
            PolicyModel policyModelBeforeUpdate = getPolicyModel(beforeUpdate);
            // No policy model found, getPolicyModel(ComponetDTO may return null)
            if (policyModelBeforeUpdate != null) {
                beforeUpdate.getPolicyModel().setName(policyModelBeforeUpdate.getName());
                beforeUpdate.getPolicyModel().setVersion(policyModelBeforeUpdate.getVersion());
            }

            policyLifeCycleHandler.unDeployEntity(devEntity, OBSOLETE);

            ComponentDTO componentDTO = findById(devEntity.getId());

            componentDTO.setStatus(OBSOLETE.name());
            PolicyModel policyModel = getPolicyModel(componentDTO);
            // No policy model found, getPolicyModel(ComponetDTO may return null)
            if (policyModel != null) {
                componentDTO.getPolicyModel().setName(policyModel.getName());
                componentDTO.getPolicyModel().setVersion(policyModel.getVersion());
            }
            entityAuditLogDao.addEntityAuditLog(AuditAction.UNDEPLOY,
                    getEntityType(componentDTO.getType()), componentDTO.getId(),
                    beforeUpdate.toAuditString(), componentDTO.toAuditString());
            componentSearchService.reIndexComponents(componentDTO);

            auditService.save(COMPONENT_MGMT.name(),
                    "audit.deactivated.component",
                    devEntity.getNameFromTitle());
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in component un-deployement", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void unDeploy(List<Long> ids) throws ConsoleException {
        List<PolicyDevelopmentEntity> authorizedDevEntities = new ArrayList<>();

        // Components will be un-deployed only if the user has permission to deploy all requested components.
        for (Long id : ids) {
            PolicyDevelopmentEntity devEntity = devEntityMgmtService.findById(id);
            if (devEntity != null) {
                accessControlService.authorizeByTags(ActionType.DEPLOY,
                        DelegationModelShortName.COMPONENT_ACCESS_TAGS,
                        devEntity,
                        false);
                authorizedDevEntities.add(devEntity);
            }
        }

        for (PolicyDevelopmentEntity devEntity : authorizedDevEntities) {
            unDeploy(devEntity);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ComponentDTO clone(Long id) throws ConsoleException {
        try {
            PolicyDevelopmentEntity devEntity = devEntityMgmtService
                    .findById(id);
            PolicyDevelopmentEntity clone = null;
            if (devEntity != null) {

                accessControlService.authorizeByTags(ActionType.INSERT,
                        DelegationModelShortName.COMPONENT_ACCESS_TAGS,
                        devEntity,
                        false);

                clone = new PolicyDevelopmentEntity();
                clone.setFolderId(devEntity.getFolderId());
                String group = "";
                String entityTitle = devEntity.getTitle();
                int index = entityTitle.indexOf('/');
                if (index != -1) {
                    group = entityTitle.substring(0, index);
                }
                String clonedTitle = getClonedName(devEntity.getTitle(), group);
                clone.setTitle(clonedTitle);
                clone.setDescription(devEntity.getDescription());
                clone.setApPql(devEntity.getApPql());
                clone.setHidden(devEntity.getHidden());
                clone.setType(devEntity.getType());
                clone.setStatus(devEntity.getStatus());
                clone.setCreatedDate(System.currentTimeMillis());
                clone.setLastUpdatedDate(System.currentTimeMillis());
                clone.setPql(devEntity.getPql());
                clone.setApPql(devEntity.getApPql());
                clone.setOwner(getCurrentUser().getUserId());
                clone.setExtendedDescription(
                        devEntity.getExtendedDescription());

                for (TagLabel tag : devEntity.getTags()) {
                    TagLabel tagLbl = tagLabelService.findById(tag.getId());
                    clone.getTags().add(tagLbl);
                }

                clone = devEntityMgmtService.save(clone);

                ComponentPQLHelper pqlHelper = new ComponentPQLHelper();
                ComponentDTO componentDTO = pqlHelper
                        .fromPQL(devEntity.getPql());
                populateExtendedDesc(devEntity.getExtendedDescription(),
                        componentDTO);

                PolicyModel policyModel = null;
                if (componentDTO.getPolicyModel() != null) {
                    policyModel = policyModelService
                            .findById(componentDTO.getPolicyModel().getId());
                }
                String updatedPQL = pqlHelper.getPQL(clone.getId(),
                        clone.getTitle(), clone.getDescription(), RESOURCE,
                        componentDTO, policyModel);
                clone.setPql(updatedPQL);
                clone = devEntityMgmtService.save(clone);

                componentDTO.setId(clone.getId());
                componentDTO.setFolderId(clone.getFolderId());
                componentDTO.setFolderPath(clone.getFolder() == null ? null : clone.getFolder().getFolderPath());
                componentDTO.setName(clone.getTitle());
                log.info("Component cloned successfully, [ ID: {}]", id);

                componentSearchService.reIndexComponents(componentDTO);
                auditService.save(COMPONENT_MGMT.name(),
                        "audit.cloned.component", devEntity.getNameFromTitle(),
                        componentDTO.getName());

                entityAuditLogDao.addEntityAuditLog(AuditAction.CREATE, getEntityType(componentDTO.getType()),
                        clone.getId(), null, componentDTO.toAuditString());

                return componentDTO;
            }
        } catch (Exception e) {
            throw new ConsoleException("Error occurred in clone component id",
                    e);
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<PolicyDeploymentEntity> deploymentHistory(Long id)
            throws ConsoleException {
        List<PolicyDeploymentEntity> policyDepoymentDetails = deploymentMgmtService
                .findByPolicyId(id);

        log.info(
                "Component deployment details loaded for [Component id :{}, No of records :{}]",
                id, policyDepoymentDetails.size());

        return policyDepoymentDetails;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ComponentDeploymentHistoryDTO viewRevision(Long revisionId)
            throws ConsoleException {
        try {
            PolicyDeploymentEntity historyEntity = deploymentMgmtService
                    .findById(revisionId);

            ComponentDeploymentHistoryDTO revisionDTO = ComponentDeploymentHistoryDTO
                    .getDTO(historyEntity, "", appUserSearchRepository);
            ComponentDTO component = ComponentPQLHelper.create()
                    .fromPQL(historyEntity.getPql());
            populateMemberDetails(component);
            componentSearchRepository.findById(historyEntity.getDevelopmentId())
                    .ifPresent(componentLite -> {
                        for (TagDTO tag : componentLite.getTags()) {
                            component.getTags().add(tag);
                        }
                    });
            revisionDTO.setComponentDetail(component);

            log.info("View component revision loaded for component id :{},",
                    component.getId());

            return revisionDTO;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error occurred in load revision details by id", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ComponentDTO revertToVersion(Long revisionId)
            throws ConsoleException {
        try {
            PolicyDeploymentEntity historyEntity = deploymentMgmtService
                    .findById(revisionId);

            PolicyDevelopmentEntity devEntity = devEntityMgmtService
                    .findById(historyEntity.getDevelopmentId());

            accessControlService.authorizeByTags(ActionType.EDIT,
                    DelegationModelShortName.COMPONENT_ACCESS_TAGS,
                    devEntity,
                    false);

            devEntity.setTitle(historyEntity.getName());
            devEntity.setDescription(historyEntity.getDescription());
            devEntity.setStatus(PolicyDevelopmentStatus.DRAFT.getKey());
            devEntity.setLastModified(System.currentTimeMillis());
            devEntity.setLastUpdatedDate(System.currentTimeMillis());

            ComponentPQLHelper pqlHelper = ComponentPQLHelper.create();
            ComponentDTO componentDTO = pqlHelper
                    .fromPQL(historyEntity.getPql());
            componentDTO.setStatus(PolicyDevelopmentStatus.DRAFT.name());
            populateExtendedDesc(devEntity.getExtendedDescription(),
                    componentDTO);

            String updatedPql = null;
            PolicyModel policyModel = null;

            if (componentDTO.getPolicyModel() != null && componentDTO.getPolicyModel().getId() != 0) {
                policyModel = policyModelService
                        .findById(componentDTO.getPolicyModel().getId());

                updatedPql = pqlHelper.getPQL(devEntity.getId(),
                        devEntity.getTitle(), devEntity.getDescription(),
                        SpecType.RESOURCE, componentDTO, policyModel,
                        policyModel.getType());
            } else {
                updatedPql = pqlHelper.getPQL(devEntity.getId(),
                        devEntity.getTitle(), devEntity.getDescription(),
                        SpecType.RESOURCE, componentDTO, policyModel,
                        PolicyModelType.RESOURCE);
            }

            devEntity.setPql(updatedPql);

            devEntityMgmtService.save(devEntity);
            componentSearchService.reIndexComponents(componentDTO);

            auditService.save(COMPONENT_MGMT.name(), "audit.reverted.component",
                    devEntity.getNameFromTitle(), String.valueOf(revisionId));

            log.info(
                    "Component details reverted to old revision[ Component Id :{}, Revision :{}",
                    devEntity.getId(), revisionId);

            ComponentDTO dto = new ComponentDTO();
            dto.setId(devEntity.getId());
            return dto;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in revert to given version,", e);
        }
    }

    public ComponentLite enforceTBAC(ComponentLite lite) {
        lite = accessControlService.enforceTBAConComponent(lite);

        // Add sub component level access control
        for (SubComponentLite subComponent : lite.getSubComponents()) {
            componentSearchRepository.findById(subComponent.getId())
                    .ifPresent(component -> {
                        accessControlService.enforceTBAConComponent(component);
                        subComponent.setAuthorities(component.getAuthorities());
                    });
        }

        // add included in component's access control
        for (IncludedComponentLite includedComponent : lite
                .getIncludedInComponents()) {
            componentSearchRepository.findById(includedComponent.getId())
                    .ifPresent(component -> {
                        accessControlService.enforceTBAConComponent(component);
                        includedComponent.setAuthorities(component.getAuthorities());
                    });
        }
        return lite;
    }

    @Override
    public boolean isComponentExists(String name, String type)
            throws ConsoleException {
        try {
            checkNameIsUnique(name, type);
            return false;
        } catch (NotUniqueException e) {
            return true;
        }
    }

    public String newPQL(Long id, String name) {
        EntityType type = EntityType.COMPONENT;
        StringBuffer res = new StringBuffer(128);
        res.append("ID ");
        res.append(id);
        res.append(" STATUS NEW ");
        res.append(type.emptyPql(name));
        return res.toString();
    }

    private void checkNameIsUnique(String name, String group) {
        List<ComponentLite> components = getComponentsByNameAndGroup(name, group);
        if (!components.isEmpty()) {
            throw new NotUniqueException(
                    msgBundle.getText("server.error.not.unique.code"),
                    msgBundle.getText("server.error.component.name.not.unique",
                            name));
        }
    }

    @Override
    public List<ComponentLite> getComponentsByNameAndGroup(String name, String group) {
        MatchAllQueryBuilder query = QueryBuilders.matchAllQuery();
        BoolQueryBuilder filter = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("lowercase_name", name.toLowerCase()))
                .must(QueryBuilders.termQuery("group", group));

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(query).withFilter(filter)
                .withPageable(PageRequest.of(0, 100000)).build();

        Page<ComponentLite> compPage = componentSearchRepository
                .search(searchQuery);
        return compPage.getContent();
    }

    @Override
    public Set<DeploymentDependency> findDependencies(List<Long> ids) throws ConsoleException {
        Set<DeploymentDependency> dependencies = new HashSet<>();
        for (Long id : ids) {
            validatorService.findDependenciesOfComponent(dependencies, id, true);
        }
        dependencies.forEach(dependency -> {
            if (ids.contains(dependency.getId())) {
                dependency.setProvided(true);
                dependency.setOptional(false);
            }
        });
        return dependencies;
    }

    public String getClonedName(String title, String type) {
        String cloneTitle = title;
        int index = cloneTitle.indexOf('/');
        if (index != -1) {
            String rootFolder = cloneTitle.substring(0, index);
            String titleName = cloneTitle.substring(index + 1);
            if (titleName.length() >= MAX_ALLOWED_LENGTH) {
                cloneTitle = getCloneNameForLongTitle(rootFolder, titleName);
            }
        }
        while (true) {
            try {
                cloneTitle = JavaBeanCopier.clonedLabelSuffix(cloneTitle);
                String cloneName = getComponentName(cloneTitle);
                checkNameIsUnique(cloneName, type);
                break;
            } catch (NotUniqueException ce) {
                continue;
            }
        }
        return cloneTitle;
    }

    private String getCloneNameForLongTitle(String rootFolder, String titleName) {
        int extraChars = titleName.length() - MAX_ALLOWED_LENGTH;
        titleName = titleName.substring(0, (titleName.length() - extraChars));
        return rootFolder + "/" + titleName;
    }

    private String getComponentName(String cloneTitle) {
        int index = cloneTitle.lastIndexOf('/');
        return cloneTitle.substring(index + 1);
    }

    /**
     * Returns a list of components and/or policies where the given component is
     * referenced
     *
     * @param componentId
     * @return
     * @throws ConsoleException
     */
    private Map<String, List<Long>> getAllReferences(Long componentId)
            throws ConsoleException {

        log.debug("In getAllReferences method, componentId = {} ", componentId);
        Map<String, List<Long>> referencesMap = new HashMap<>();

        // specify any large value for page size to find all
        int pageSize = 100000;

        // get all components
        PageRequest compPageable = PageRequest.of(0, pageSize);
        Page<ComponentLite> compPage = componentSearchRepository
                .findAll(compPageable);
        List<ComponentLite> allcomponents = compPage.getContent();

        // check for references in components
        Set<Long> compReferences = new TreeSet<>();
        getComponentReferences(componentId, compReferences, allcomponents);
        List<Long> referenceIds = new ArrayList<>(compReferences);
        referencesMap.put(COMPONENT.getKey(), referenceIds);

        // get all policies
        List<PolicyDevelopmentEntity> policies = devEntityMgmtService
                .findActiveEntitiesByType(POLICY.getKey());

        // check for references in policies
        Set<Long> polReferences = new TreeSet<>();
        getPolicyReferences(componentId, polReferences, policies);

        List<Long> policyReferences = new ArrayList<>(polReferences);
        referencesMap.put(POLICY.getKey(), policyReferences);

        return referencesMap;
    }

    private void removeComponentReference(Map<String, List<Long>> referencesMap,
                                          Long componentId) throws ConsoleException {

        List<Long> compReferenceIds = referencesMap.get(COMPONENT.getKey());
        List<Long> policyReferenceIds = referencesMap.get(POLICY.getKey());
        log.debug(
                "No of reference policies = {}, No of reference components = {} ",
                policyReferenceIds.size(), compReferenceIds.size());

        List<DeploymentRequestDTO> deploymentRequests = new ArrayList<>();
        List<ComponentDTO> authorizedComponents = new ArrayList<>();
        for (Long referenceId : compReferenceIds) {
            ComponentDTO componentDTO = findById(referenceId);
            if (DevEntityType.DELEGATION_COMPONENT.equals(componentDTO.getCategory())) {
                accessControlService.checkAuthority(DelegationModelActions.MANAGE_DELEGATED_ADMIN, ActionType.MANAGE,
                        AuthorizableType.DELEGATION_COMPONENT);
            } else {
                accessControlService.checkAuthority(DelegationModelActions.EDIT_COMPONENT, ActionType.EDIT,
                        AuthorizableType.COMPONENT);
                accessControlService.authorizeByTags(ActionType.EDIT,
                        DelegationModelShortName.COMPONENT_ACCESS_TAGS,
                        componentDTO,
                        false);
                if (PolicyStatus.APPROVED.name().equals(componentDTO.getStatus())) {
                    accessControlService.checkAuthority(DelegationModelActions.DEPLOY_COMPONENT, ActionType.DEPLOY,
                            AuthorizableType.COMPONENT);
                    accessControlService.authorizeByTags(ActionType.DEPLOY,
                            DelegationModelShortName.COMPONENT_ACCESS_TAGS,
                            componentDTO,
                            false);
                }
            }
            authorizedComponents.add(componentDTO);
        }

        for (ComponentDTO componentDTO : authorizedComponents) {
            try {
                // de-reference deleted component
                deReferenceComponent(componentId, componentDTO);

                // update
                modifyComponent(componentDTO, CheckCircularRefs.NO);
                log.info("Component modified successfully, = {}",
                        componentDTO.getId());

                if ((PolicyStatus.APPROVED.name()).equals(componentDTO.getStatus())) {
                    deploymentRequests.add(new DeploymentRequestDTO(componentDTO.getId(), DevEntityType.COMPONENT));
                }
            } catch (JsonProcessingException | CircularReferenceException ex) {
                continue;
            }
        }

        List<PolicyDTO> authorizedPolicies = new ArrayList<>();
        for (Long referenceId : policyReferenceIds) {
            PolicyDTO policyDTO = policyMgmtService.findById(referenceId);
            if (DevEntityType.DELEGATION_POLICY.equals(policyDTO.getCategory())) {
                accessControlService.checkAuthority(DelegationModelActions.MANAGE_DELEGATED_ADMIN, ActionType.MANAGE,
                        AuthorizableType.DELEGATION_POLICY);
            } else {
                accessControlService.checkAuthority(DelegationModelActions.EDIT_POLICY, ActionType.EDIT,
                        AuthorizableType.POLICY);
                accessControlService.authorizeByTags(ActionType.EDIT,
                        DelegationModelShortName.POLICY_ACCESS_TAGS,
                        policyDTO,
                        true);
                if (PolicyStatus.APPROVED.name().equals(policyDTO.getStatus())) {
                    accessControlService.checkAuthority(DelegationModelActions.DEPLOY_POLICY, ActionType.DEPLOY,
                            AuthorizableType.POLICY);
                    accessControlService.authorizeByTags(ActionType.DEPLOY,
                            DelegationModelShortName.POLICY_ACCESS_TAGS,
                            policyDTO,
                            false);
                }
            }
            authorizedPolicies.add(policyDTO);
        }
        // repeat the process for policies
        for (PolicyDTO policyDTO : authorizedPolicies) {
            log.info("Component to be dereferenced from Policy, PolicyId = {}",
                    policyDTO.getId());
            // de-reference deleted component and update entity

            List<PolicyComponent> actionComponents = deReferencePolicy(
                    componentId, policyDTO,
                    policyDTO.getActionComponents());
            policyDTO.setActionComponents(actionComponents);

            List<PolicyComponent> subjectComponents = deReferencePolicy(
                    componentId, policyDTO,
                    policyDTO.getSubjectComponents());
            policyDTO.setSubjectComponents(subjectComponents);

            List<PolicyComponent> toSubjectComponents = deReferencePolicy(
                    componentId, policyDTO,
                    policyDTO.getToSubjectComponents());
            policyDTO.setToSubjectComponents(toSubjectComponents);

            List<PolicyComponent> fromResourceComponents = deReferencePolicy(
                    componentId, policyDTO,
                    policyDTO.getFromResourceComponents());
            policyDTO.setFromResourceComponents(fromResourceComponents);

            List<PolicyComponent> toResourceComponents = deReferencePolicy(
                    componentId, policyDTO,
                    policyDTO.getToResourceComponents());
            policyDTO.setToResourceComponents(toResourceComponents);

            eliminateObligations(policyDTO);

            // update
            policyDTO.setSkipIndexing(true);
            policyMgmtService.modify(policyDTO);

            if ((PolicyStatus.APPROVED.name()).equals(policyDTO.getStatus())) {
                deploymentRequests.add(new DeploymentRequestDTO(policyDTO.getId(), DevEntityType.POLICY));
            }

        }
        policyMgmtService.deploy(deploymentRequests, false);
    }

    private void getComponentReferences(Long componentId,
                                        Set<Long> compReferences, List<ComponentLite> allcomponents) {
        for (ComponentLite acomponent : allcomponents) {
            List<SubComponentLite> subComponents = acomponent
                    .getSubComponents();
            for (SubComponentLite subComponent : subComponents) {
                if (componentId.compareTo(subComponent.getId()) == 0) {
                    log.info("Reference found in component, Id = {} ",
                            acomponent.getId());
                    compReferences.add(acomponent.getId());
                }
            }
        }
    }

    private void getPolicyReferences(Long componentId, Set<Long> polReferences,
                                     List<PolicyDevelopmentEntity> policies) {

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
                    checkPoliciesForReference(componentId, polReferences,
                            policyDTO.getActionComponents(), policyId);
                    checkPoliciesForReference(componentId, polReferences,
                            policyDTO.getSubjectComponents(), policyId);
                    checkPoliciesForReference(componentId, polReferences,
                            policyDTO.getToSubjectComponents(), policyId);
                    checkPoliciesForReference(componentId, polReferences,
                            policyDTO.getFromResourceComponents(), policyId);
                    checkPoliciesForReference(componentId, polReferences,
                            policyDTO.getToResourceComponents(), policyId);
                }
            }

        }
    }

    /**
     * @param componentId
     * @param polReferences
     * @param policyComponents
     */
    private void checkPoliciesForReference(Long componentId,
                                           Set<Long> polReferences, List<PolicyComponent> policyComponents,
                                           Long refPolicyId) {
        for (PolicyComponent policyComponent : policyComponents) {
            List<ComponentDTO> components = policyComponent.getComponents();
            for (ComponentDTO component : components) {
                if (componentId.compareTo(component.getId()) == 0) {
                    log.info("Reference found in policy, PolicyId = {} ",
                            refPolicyId);
                    polReferences.add(refPolicyId);
                }
            }
        }
    }

    /**
     * Dereference component from policies
     *
     * @param componentId
     * @param policyDTO
     * @param policyComponents
     */
    private List<PolicyComponent> deReferencePolicy(Long componentId,
                                                    PolicyDTO policyDTO, List<PolicyComponent> policyComponents) {
        int removeIndex = -1;
        for (PolicyComponent policyComponent : policyComponents) {
            List<ComponentDTO> components = policyComponent.getComponents();
            for (int i = 0; i < components.size(); i++) {
                ComponentDTO comp = components.get(i);
                if (comp.getId().compareTo(componentId) == 0) {
                    removeIndex = i;
                }
            }
            if (removeIndex != -1) {
                components.remove(removeIndex);
            }
            policyComponent.setComponents(components);
        }
        return policyComponents;
    }

    /**
     * Dereference component from other components
     *
     * @param componentId
     * @param componentDTO
     */
    private void deReferenceComponent(Long componentId,
                                      ComponentDTO componentDTO) {
        for (MemberCondition memberCondition : componentDTO.getMemberConditions()) {
            memberCondition.getMembers().removeIf(member -> (!ComponentPQLHelper.MEMBER_GROUP.equals(member.getType())
                    && member.getId().equals(componentId)));
        }
        componentDTO.getMemberConditions().removeIf(memberCondition -> memberCondition.getMembers().isEmpty());
    }

    /**
     * Detect cycles in what should be a directed acylic component graph, starting from a given node
     *
     * @param component the starting compoent
     */
    private void detectCircularReferences(ComponentDTO component) throws ConsoleException, CircularReferenceException {
        if (component == null) {
            return;
        }

        try {
            detectCircularReferences(component.getId(), component, new HashSet<Long>(), new HashSet<Long>());
        } catch (CircularReferenceException e) {
            throw new CircularReferenceException(msgBundle.getText("component.circular.reference.error.code"),
                    msgBundle.getText("component.circular.reference.error.message"));
        } catch (ConsoleException e) {
            throw new ConsoleException("Exception in component " + component.getName(), e);
        }
    }

    /**
     * Detect loops in what should be a directed acyclic component
     * graph
     *
     * @param rootId    the original root node id (see previous function)
     * @param component the current node to investigate
     * @param found     the list of all nodes found so far in ancestor chain
     * @param processed the list of all nodes processed
     *                  <p>
     *                  Recursive function to find loops. There are complications. A
     *                  node can appear multiple times without there being a loop. What
     *                  it can't do is appear multiple times in the parent chain
     *                  (parent, parent of parent, etc).  We store the nodes found so
     *                  far by that path in "found". "processed" is all nodes we've
     *                  completely processed so far. If we find a node that's in that
     *                  list we can skip the node, because it's already been processed.
     *                  <p>
     *                  The main complication is that this is called when we save or
     *                  modify a component, but it's done before the save happens. That
     *                  means findById won't return the right value if we give it the
     *                  id of the component we are saving.
     *                  <p>
     *                  It's probably true that, since we are saving component X, any
     *                  loop that has been created must contain X (X could have a loop
     *                  further down, but that would have been created when saving its
     *                  children at some point, so we'd have detected it then), so we
     *                  probably don't need the set of "found" ids. Probably. I'm not
     *                  taking any chances.
     */
    private void detectCircularReferences(Long rootId, ComponentDTO component, Set<Long> found, Set<Long> processed) throws ConsoleException, CircularReferenceException {
        if (component == null) {
            throw new NullPointerException("component");
        }

        for (MemberCondition memberCondition : component.getMemberConditions()) {
            for (MemberDTO memberDTO : memberCondition.getMembers()) {
                if (!ComponentPQLHelper.MEMBER_GROUP.equals(memberDTO.getType())) {
                    Long childId = memberDTO.getId();

                    if (childId.equals(rootId)) {
                        throw new CircularReferenceException(memberDTO.getName());
                    }

                    // The parent component has an incomplete version of the child component. Get the real one
                    ComponentDTO realChildComponent = findById(childId);

                    if (processed.contains(childId)) {
                        continue;
                    }

                    if (found.contains(childId)) {
                        throw new CircularReferenceException(realChildComponent.getName());
                    }

                    found.add(childId);
                    detectCircularReferences(rootId, realChildComponent, found, processed);
                    found.remove(childId);
                    processed.add(childId);
                }
            }
        }
    }

    private String getEntityType(String type) {
        if (type != null) {
            if (type.toUpperCase().startsWith(ComponentPQLHelper.SUBJECT_GROUP)) {
                return AuditableEntity.SUBJECT.getCode();
            } else if (type.toUpperCase().startsWith(ComponentPQLHelper.RESOURCE_GROUP)) {
                return AuditableEntity.RESOURCE.getCode();
            } else if (type.toUpperCase().startsWith(ComponentPQLHelper.ACTION_GROUP)) {
                return AuditableEntity.ACTION.getCode();
            } else if (type.toUpperCase().startsWith("APPLICATION")) {
                return AuditableEntity.APPLICATION.getCode();
            }
        }

        return "";
    }

    public ComponentPreviewDTO getComponentPreview(ComponentDTO componentDTO)
            throws PolicyEditorException, ConsoleException {
        log.debug("about to fetch enrolled subjects based on conditions and members added");
        ComponentPreviewDTO componentPreviewDTO = new ComponentPreviewDTO();
        LeafObjectSearchSpec leafObjectSearchSpec;
        CompositePredicate predicates;
        PolicyModel policyModel = getPolicyModel(componentDTO);
        if(policyModel != null) {
            ComponentPQLHelper pqlHelper = ComponentPQLHelper.create();
            List<IPredicate> conditionPredicates = pqlHelper.getExpressions(componentDTO,
                            PolicyModelType.get(componentDTO.getType().toUpperCase()),
                            componentDTO.getPolicyModel().getShortName().toUpperCase(), policyModel);

            IPredicate memberPredicates = pqlHelper.getMemberConditionsPredicate(componentDTO.getMemberConditions());

            predicates = new CompositePredicate(BooleanOp.AND, conditionPredicates);
            predicates = new CompositePredicate(BooleanOp.AND, predicates, memberPredicates);

            int startIndex = componentDTO.getPageNo() * componentDTO.getPageSize();
            leafObjectSearchSpec = new LeafObjectSearchSpec(
                            LeafObjectType.forName(policyModel.getShortName().toUpperCase()),
                            predicates, null, startIndex, componentDTO.getPageSize());

            componentPreviewDTO.setEnrolledSubjects(dpsProxyService.getPolicyEditorClient().runLeafObjectQuery(leafObjectSearchSpec));
            componentPreviewDTO.setTotalEnrolledSubjects(
                            getTotalCountOfEnrolledSubjects(predicates, policyModel));
        }

        return componentPreviewDTO;
    }

    private int getTotalCountOfEnrolledSubjects(CompositePredicate predicates, PolicyModel policyModel)
            throws PolicyEditorException {
        LeafObjectSearchSpec leafObjectSearchSpec;
        List<LeafObject> enrolledSubjects = new ArrayList<>();
        leafObjectSearchSpec = new LeafObjectSearchSpec(
                LeafObjectType.forName(policyModel.getShortName().toUpperCase()), predicates, null, 65535);
        enrolledSubjects.addAll(dpsProxyService.getPolicyEditorClient().runLeafObjectQuery(leafObjectSearchSpec));
        return enrolledSubjects.size();
    }

    /**
     * Move list of components to another folder.
     *
     * @param destinationFolderId destination folder id
     * @param ids                 list of component ids
     * @throws ConsoleException if an error occurred
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void move(Long destinationFolderId, List<Long> ids) throws ConsoleException {
        List<PolicyDevelopmentEntity> policyDeploymentEntities = new ArrayList<>();
        for (Long id : ids) {
            PolicyDevelopmentEntity policyDevelopmentEntity = devEntityMgmtService.findById(id);
            accessControlService.authorizeByTags(ActionType.MOVE, DelegationModelShortName.COMPONENT_ACCESS_TAGS,
                    policyDevelopmentEntity, false);
            policyDevelopmentEntity.setFolderId(destinationFolderId);
            accessControlService.authorizeByTags(ActionType.INSERT, DelegationModelShortName.COMPONENT_ACCESS_TAGS,
                    policyDevelopmentEntity, false);
            policyDeploymentEntities.add(policyDevelopmentEntity);
        }
        for (PolicyDevelopmentEntity policyDevelopmentEntity : policyDeploymentEntities) {
            ComponentDTO componentDTO = findById(policyDevelopmentEntity.getId());
            String componentJsonBefore = componentDTO == null ? "" : componentDTO.toAuditString();
            devEntityMgmtService.save(policyDevelopmentEntity);
            if (componentDTO != null) {
                if(destinationFolderId != null) {
                    folderRepository.findById(destinationFolderId)
                            .ifPresent(folder -> componentDTO.setFolderPath(folder.getFolderPath()));
                }
                entityAuditLogDao.addEntityAuditLog(AuditAction.MOVE,
                        getEntityType(componentDTO.getType()),
                        componentDTO.getId(), componentJsonBefore,
                        componentDTO.toAuditString());
            }
            componentSearchService.reIndexComponents(findById(policyDevelopmentEntity.getId()));
        }
    }

    /**
     * When component is removed from a policy, all corresponding obligations should be removed as well
     * @param policyDTO Policy to examine
     */
    private void eliminateObligations(PolicyDTO policyDTO) {
        Set<Long> policyModelIds = new HashSet<>();

        for (PolicyComponent component : policyDTO.getSubjectComponents()) {
            for (ComponentDTO componentDTO : component.getComponents()) {
                if (componentDTO.getPolicyModel() != null) {
                    policyModelIds.add(componentDTO.getPolicyModel().getId());
                }
            }
        }

        for (PolicyComponent component : policyDTO.getToSubjectComponents()) {
            for (ComponentDTO componentDTO : component.getComponents()) {
                if (componentDTO.getPolicyModel() != null) {
                    policyModelIds.add(componentDTO.getPolicyModel().getId());
                }
            }
        }

        for (PolicyComponent component : policyDTO.getActionComponents()) {
            for (ComponentDTO componentDTO : component.getComponents()) {
                if (componentDTO.getPolicyModel() != null) {
                    policyModelIds.add(componentDTO.getPolicyModel().getId());
                }
            }
        }

        for (PolicyComponent component : policyDTO.getFromResourceComponents()) {
            for (ComponentDTO componentDTO : component.getComponents()) {
                if (componentDTO.getPolicyModel() != null) {
                    policyModelIds.add(componentDTO.getPolicyModel().getId());
                }
            }
        }

        for (PolicyComponent component : policyDTO.getToResourceComponents()) {
            for (ComponentDTO componentDTO : component.getComponents()) {
                if (componentDTO.getPolicyModel() != null) {
                    policyModelIds.add(componentDTO.getPolicyModel().getId());
                }
            }
        }

        Iterator<ObligationDTO> allowObligationDTOIterator = policyDTO.getAllowObligations().iterator();
        while(allowObligationDTOIterator.hasNext()) {
            ObligationDTO obligationDTO = allowObligationDTOIterator.next();
            if(!policyModelIds.contains(obligationDTO.getPolicyModelId())) {
                allowObligationDTOIterator.remove();
            }
        }

        Iterator<ObligationDTO> denyObligationDTOIterator = policyDTO.getDenyObligations().iterator();
        while(denyObligationDTOIterator.hasNext()) {
            ObligationDTO obligationDTO = denyObligationDTOIterator.next();
            if(!policyModelIds.contains(obligationDTO.getPolicyModelId())) {
                denyObligationDTOIterator.remove();
            }
        }
    }
}
