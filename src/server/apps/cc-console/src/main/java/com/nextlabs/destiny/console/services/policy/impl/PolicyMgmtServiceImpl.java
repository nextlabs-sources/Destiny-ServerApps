/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 11, 2016
 *
 */
package com.nextlabs.destiny.console.services.policy.impl;

import com.bluejungle.pf.destiny.lifecycle.EntityType;
import com.bluejungle.pf.destiny.parser.DomainObjectBuilder;
import com.bluejungle.pf.destiny.parser.PQLException;
import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.bluejungle.pf.domain.epicenter.exceptions.IPolicyReference;
import com.google.common.collect.ImmutableList;
import com.nextlabs.destiny.console.config.properties.PolicyWorkflowProperties;
import com.nextlabs.destiny.console.dao.EntityAuditLogDao;
import com.nextlabs.destiny.console.delegadmin.actions.DelegationModelActions;
import com.nextlabs.destiny.console.dto.common.AgentDTO;
import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentLite;
import com.nextlabs.destiny.console.dto.policymgmt.DeploymentDependency;
import com.nextlabs.destiny.console.dto.policymgmt.DeploymentRequestDTO;
import com.nextlabs.destiny.console.dto.policymgmt.DeploymentResponseDTO;
import com.nextlabs.destiny.console.dto.policymgmt.MemberCondition;
import com.nextlabs.destiny.console.dto.policymgmt.MemberDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ParentPolicyLite;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyComponent;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDeploymentHistoryDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.dto.policymgmt.PushResultDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SubPolicyLite;
import com.nextlabs.destiny.console.dto.policymgmt.ValidationDetailDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ValidationRecord;
import com.nextlabs.destiny.console.dto.policymgmt.XacmlPolicyDTO;
import com.nextlabs.destiny.console.dto.policymgmt.XacmlPolicyLite;
import com.nextlabs.destiny.console.dto.policymgmt.policyworkflow.EntityWorkflowRequestDTO;
import com.nextlabs.destiny.console.enums.ActionType;
import com.nextlabs.destiny.console.enums.AuditAction;
import com.nextlabs.destiny.console.enums.AuditableEntity;
import com.nextlabs.destiny.console.enums.AuthorizableType;
import com.nextlabs.destiny.console.enums.DelegationModelShortName;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.enums.EntityWorkflowRequestStatus;
import com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus;
import com.nextlabs.destiny.console.enums.PolicyEffect;
import com.nextlabs.destiny.console.enums.PolicyStatus;
import com.nextlabs.destiny.console.enums.WorkflowRequestLevelStatus;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.DirtyUpdateException;
import com.nextlabs.destiny.console.exceptions.InvalidXacmlPolicyException;
import com.nextlabs.destiny.console.exceptions.NotUniqueException;
import com.nextlabs.destiny.console.model.Agent;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentEntity;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.policy.handlers.PolicyLifeCycleHandler;
import com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper;
import com.nextlabs.destiny.console.policy.pql.helpers.PolicyPQLHelper;
import com.nextlabs.destiny.console.repositories.FolderRepository;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.search.repositories.ComponentSearchRepository;
import com.nextlabs.destiny.console.search.repositories.PolicySearchRepository;
import com.nextlabs.destiny.console.search.repositories.XacmlPolicySearchRepository;
import com.nextlabs.destiny.console.services.AgentSearchService;
import com.nextlabs.destiny.console.services.AuditLogService;
import com.nextlabs.destiny.console.services.DPSProxyService;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.TagLabelService;
import com.nextlabs.destiny.console.services.delegadmin.AccessControlService;
import com.nextlabs.destiny.console.services.policy.ComponentMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyDeploymentEntityMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyDevelopmentEntityMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicySearchService;
import com.nextlabs.destiny.console.services.policy.ValidatorService;
import com.nextlabs.destiny.console.services.policy.XacmlPolicySearchService;
import com.nextlabs.destiny.console.utils.JavaBeanCopier;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
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
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.nextlabs.destiny.console.enums.AuditLogComponent.POLICY_MGMT;
import static com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus.APPROVED;
import static com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus.DRAFT;
import static com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus.DELETED;
import static com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus.OBSOLETE;
import static com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus.getByKey;
import static com.nextlabs.destiny.console.utils.SecurityContextUtil.getCurrentUser;

/**
 * Policy management service implementation
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class PolicyMgmtServiceImpl implements PolicyMgmtService {

    private static final Logger log = LoggerFactory
            .getLogger(PolicyMgmtServiceImpl.class);

    public static final String POLICY_ROOT = "ROOT";
    public static final String XACML_POLICY_ROOT = "XACML_POLICY";
    public static final String XACML_POLICY_SET_ROOT = "XACML_POLICY_SET";
    private static final String XACML_POLICY_ID_XPATH = "/Policy/@PolicyId";
    private static final String XACML_POLICY_DESC_XPATH = "/Policy/Description/text()";
    private static final String XACML_POLICY_SET_ID_XPATH = "/PolicySet/@PolicySetId";
    private static final String XACML_POLICY_SET_DESC_XPATH = "/PolicySet/Description/text()";

    public static final int MAX_ALLOWED_LENGTH = 247;

    @Autowired
    private PolicyDevelopmentEntityMgmtService devEntityMgmtService;

    @Autowired
    private PolicySearchService policySearchService;

    @Autowired
    private XacmlPolicySearchService xacmlPolicySearchService;

    @Autowired
    private PolicyDeploymentEntityMgmtService deploymentMgmtService;

    @Resource
    private PolicySearchRepository policySearchRepository;

    @Resource
    private XacmlPolicySearchRepository xacmlPolicySearchRepository;

    @Autowired
    private PolicyLifeCycleHandler policyLifeCycleHandler;

    @Autowired
    private ComponentMgmtService componentMgmtService;

    @Autowired
    private AccessControlService accessControlService;

    @Resource
    private ComponentSearchRepository componentSearchRepository;

    @Resource
    private ApplicationUserSearchRepository appUserSearchRepository;

    @Autowired
    private MessageBundleService msgBundle;

    @Autowired
    private TagLabelService tagLabelService;

    @Autowired
    private AgentSearchService agentSearchService;

    @Autowired
    private ValidatorService validatorService;

    @Autowired
    private AuditLogService auditService;
    
    @Autowired
    private EntityAuditLogDao entityAuditLogDao;

    @Autowired
    private DPSProxyService dpsProxyService;

    @Autowired
    private FolderRepository folderRepository;

    private PolicyWorkflowProperties policyWorkflowProperties;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PolicyDTO save(PolicyDTO policyDTO) throws ConsoleException {
        log.debug("Policy save has began");
        PolicyDevelopmentEntity devEntity = savePolicy(policyDTO);

        auditService.save(POLICY_MGMT.name(), "audit.new.policy",
                policyDTO.getName());

        log.info("New policy saved successfully, [Policy Id :{}]",
                devEntity.getId());
        return policyDTO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PolicyDevelopmentEntity saveXacmlPolicy(XacmlPolicyDTO policyDTO) throws ConsoleException {
        log.debug("Xacml Policy save has began");

        PolicyDevelopmentEntity policyDevelopmentEntity = new PolicyDevelopmentEntity();
        policyDevelopmentEntity.setDescription(policyDTO.getDescription());
        policyDevelopmentEntity.setPql(policyDTO.getXml());
        policyDevelopmentEntity.setTitle(policyDTO.getDocumentType() + "/" + policyDTO.getPolicyName());
        policyDevelopmentEntity.setType(DevEntityType.XACML_POLICY.getKey());
        policyDevelopmentEntity.setOwner(getCurrentUser().getUserId());
        policyDevelopmentEntity.setCreatedDate(System.currentTimeMillis());
        policyDevelopmentEntity.setLastUpdatedDate(System.currentTimeMillis());
        policyDevelopmentEntity.setHidden(Boolean.FALSE);
        policyDevelopmentEntity = devEntityMgmtService.save(policyDevelopmentEntity);

        String policyNamePrefix = policyDTO.getDocumentType() + "_" + policyDevelopmentEntity.getId();
        policyDevelopmentEntity.setTitle(policyNamePrefix + "/" + policyDTO.getPolicyName());
        policyDevelopmentEntity.setStatus(APPROVED.getKey());
        policyDevelopmentEntity = devEntityMgmtService.save(policyDevelopmentEntity);

        xacmlPolicySearchService.reIndexXacmlPolicy(policyDevelopmentEntity);
        auditService.save(POLICY_MGMT.name(), "audit.new.xacml.policy",
                policyDevelopmentEntity.getTitle());

        log.info("New Xacml policy/ policy set saved successfully, [Policy Id :{}]",
                policyDevelopmentEntity.getId());
        return policyDevelopmentEntity;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PolicyDevelopmentEntity importXacmlPolicyAndDeploy(MultipartFile xacmlFile) throws ConsoleException {

        PolicyDevelopmentEntity policyDevelopmentEntity = null;
        String xml;
        String documentType = "";
        String policyName = "";
        String policyDesc = "";

        try {
            xml = IOUtils.toString(xacmlFile.getInputStream(), StandardCharsets.UTF_8);
            String policyId = getXPathValue(xml, XACML_POLICY_ID_XPATH);
            String policySetId = getXPathValue(xml, XACML_POLICY_SET_ID_XPATH);
            if(StringUtils.isNotBlank(policyId)){
                documentType = XACML_POLICY_ROOT;
                policyName = policyId;
                policyDesc = getXPathValue(xml, XACML_POLICY_DESC_XPATH).trim();
            } else if (StringUtils.isNotBlank(policySetId)) {
                documentType = XACML_POLICY_SET_ROOT;
                policyName = policySetId;
                policyDesc = getXPathValue(xml, XACML_POLICY_SET_DESC_XPATH).trim();
            }
        } catch (Exception e){
            throw new InvalidXacmlPolicyException(msgBundle.getText("server.error.code"),
                    msgBundle.getText("server.error.xacml.policy.parse.error"));
        }
        if (policyName.indexOf(' ') >= 0) {
            throw new InvalidXacmlPolicyException(msgBundle.getText("server.error.code"),
                    msgBundle.getText("server.error.xacml.space.in.policy.id"));
        }
        if (StringUtils.isBlank(policyName)) {
            throw new InvalidXacmlPolicyException(msgBundle.getText("server.error.code"),
                    msgBundle.getText("server.error.xacml.policy.id.missing"));
        }
        try {
            XacmlPolicyDTO xacmlPolicyDTO = new XacmlPolicyDTO();
            xacmlPolicyDTO.setPolicyName(policyName);
            xacmlPolicyDTO.setDescription(policyDesc);
            xacmlPolicyDTO.setXml(xml);
            xacmlPolicyDTO.setDocumentType(documentType);
            List<XacmlPolicyLite> xacmlPolicies = getXacmlPolicyByName(policyName);
            if (!xacmlPolicies.isEmpty()){
                xacmlPolicyDTO.setId(xacmlPolicies.get(0).getId());
                policyDevelopmentEntity = modifyXacmlPolicy(xacmlPolicyDTO);
            } else {
                policyDevelopmentEntity = saveXacmlPolicy(xacmlPolicyDTO);
            }
            xacmlPolicyDeploy(new DeploymentRequestDTO(policyDevelopmentEntity.getId(),
                    DevEntityType.XACML_POLICY, true, -1, false));
        } catch (Exception e) {
            throw new ConsoleException("Error occurred while saving Xacml policies", e);
        }
        return policyDevelopmentEntity;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void removeXacmlPolicy(List<Long> ids) throws ConsoleException {
        try {
            log.debug("Xacml Policy delete has began");
            for (long id: ids){
                PolicyDevelopmentEntity policyDevelopmentEntity = devEntityMgmtService.findById(id);

                XacmlPolicyDTO snapshot = getXacmlDTO(policyDevelopmentEntity);

                policyLifeCycleHandler.unDeployEntity(policyDevelopmentEntity, DELETED);
                XacmlPolicyDTO policyDTO = getXacmlDTO(policyDevelopmentEntity);
                xacmlPolicySearchRepository.deleteById(policyDevelopmentEntity.getId());

                entityAuditLogDao.addEntityAuditLog(AuditAction.DELETE,
                        AuditableEntity.XACML_POLICY.getCode(),
                        policyDTO.getId(), snapshot.toAuditString(),
                        null);

                auditService.save(POLICY_MGMT.name(), "audit.delete.xacml.policy",
                        policyDevelopmentEntity.getTitle());
            }

            log.info("Xacml policies deleted successfully, [Count :{}]",
                    ids.size());
        } catch (Exception e) {
            throw new ConsoleException("Error occurred while deleting Xacml policies", e);
        }
    }

    private PolicyDevelopmentEntity savePolicy(PolicyDTO policyDTO)
            throws ConsoleException {

        if (DevEntityType.DELEGATION_POLICY.equals(policyDTO.getCategory())) {
            accessControlService.checkAuthority(DelegationModelActions.MANAGE_DELEGATED_ADMIN, ActionType.MANAGE,
                    AuthorizableType.DELEGATION_POLICY);
        } else {
            accessControlService.authorizeByTags(ActionType.INSERT,
                    DelegationModelShortName.POLICY_ACCESS_TAGS,
                    policyDTO,
                    true);
            if (policyDTO.getId() == null) {
                checkPolicyNameIsUnique(policyDTO.getName());
            }
        }

        PolicyLite parentPolicy = (policyDTO.getParentId() != null)
                ? policySearchRepository.findById(policyDTO.getParentId()).orElse(null)
                : null;
        String parentPath = POLICY_ROOT;
        if (parentPolicy != null) {
            parentPath = parentPolicy.getPolicyFullName();
        }

        PolicyDevelopmentEntity devEntity = new PolicyDevelopmentEntity();
        devEntity.setFolderId(policyDTO.getFolderId());
        devEntity.setTitle(parentPath + "/" + policyDTO.getName());
        devEntity.setDescription(policyDTO.getDescription());
        devEntity.setHidden(Boolean.FALSE);
        devEntity.setType(policyDTO.getCategory().getKey());

        PolicyDevelopmentStatus policyStatus = PolicyDevelopmentStatus
                .get(policyDTO.getStatus());
        devEntity.setStatus(policyStatus.getKey());

        devEntity.setCreatedDate(System.currentTimeMillis());
        devEntity.setLastUpdatedDate(System.currentTimeMillis());
        devEntity.setPql(newPQL(-1L, devEntity.getTitle()));
        devEntity.setApPql(" ");
        devEntity.setOwner(getCurrentUser().getUserId());

        // if has parent treated as a sub policy
        if (policyDTO.getParentId() != null) {
            policyDTO.getAttributes().add(PolicyDTO.POLICY_EXCEPTION_ATTR);
        }

        // Add Tags
        addTags(policyDTO, devEntity);

        addTrueAllowAttributeOnlyIfAllowPolicy(policyDTO);

        devEntity = devEntityMgmtService.save(devEntity);

        if (parentPolicy == null) {
            parentPath = POLICY_ROOT + "_" + devEntity.getId();
            devEntity.setTitle(parentPath + "/" + policyDTO.getName());
        }

        String pql = PolicyPQLHelper.create().getPQL(devEntity.getId(),
                devEntity.getTitle(), devEntity.getDescription(), policyDTO);
        devEntity.setPql(pql);

        devEntityMgmtService.save(devEntity);
        policyDTO.setId(devEntity.getId());
        policyDTO.setFullName(devEntity.getTitle());
        populateComponentInfo(policyDTO);
        
        policySearchService.reIndexPolicies(policyDTO);

        entityAuditLogDao.addEntityAuditLog(AuditAction.CREATE, 
        		DevEntityType.DELEGATION_POLICY.equals(policyDTO.getCategory()) ? AuditableEntity.DELEGATE_ADMIN.getCode() : AuditableEntity.POLICY.getCode(), 
        		devEntity.getId(), null, policyDTO.toAuditString());
        
        return devEntity;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PolicyDTO modify(PolicyDTO policyDTO) throws ConsoleException {
        log.debug("Policy modify has began");

        PolicyDevelopmentEntity devEntity = modifyPolicy(policyDTO);

        log.info("Policy modified successfully, [Policy Id :{}]",
                devEntity.getId());

        return policyDTO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PolicyDevelopmentEntity modifyXacmlPolicy(XacmlPolicyDTO policyDTO) throws ConsoleException {
        log.debug("Xacml Policy modify has began");

        PolicyDevelopmentEntity policyDevelopmentEntity = devEntityMgmtService
                .findById(policyDTO.getId());

        policyDevelopmentEntity.setDescription(policyDTO.getDescription());
        policyDevelopmentEntity.setPql(policyDTO.getXml());
        String policyNamePrefix = policyDTO.getDocumentType() + "_" + policyDevelopmentEntity.getId();
        policyDevelopmentEntity.setTitle(policyNamePrefix + "/" + policyDTO.getPolicyName());
        policyDevelopmentEntity.setLastUpdatedDate(System.currentTimeMillis());
        policyDevelopmentEntity.setStatus(APPROVED.getKey());
        policyDevelopmentEntity = devEntityMgmtService.save(policyDevelopmentEntity);

        xacmlPolicySearchService.reIndexXacmlPolicy(policyDevelopmentEntity);
        auditService.save(POLICY_MGMT.name(), "audit.modify.xacml.policy",
                policyDevelopmentEntity.getTitle());

        log.info("Xacml Policy modified successfully, [Policy Id :{}]",
                policyDevelopmentEntity.getId());

        return policyDevelopmentEntity;
    }

    private PolicyDevelopmentEntity modifyPolicy(PolicyDTO policyDTO)
            throws ConsoleException {
        PolicyDTO snapshot = findById(policyDTO.getId());

        if (DevEntityType.DELEGATION_POLICY.equals(policyDTO.getCategory())) {
            accessControlService.checkAuthority(DelegationModelActions.MANAGE_DELEGATED_ADMIN, ActionType.MANAGE,
                    AuthorizableType.DELEGATION_POLICY);
        } else {
            accessControlService.authorizeByTags(ActionType.EDIT,
                    DelegationModelShortName.POLICY_ACCESS_TAGS,
                    snapshot,
                    false);

            accessControlService.authorizeByTags(ActionType.EDIT,
                    DelegationModelShortName.POLICY_ACCESS_TAGS,
                    policyDTO,
                    true);
            
          //duplicate name check
        	checkPolicyNameIsUniqueOnUpdate(policyDTO.getName(), 
        			policyDTO.getId());
        }

        PolicyLite policyLite = policySearchRepository.findById(policyDTO.getId()).orElse(null);
   		
        PolicyDevelopmentEntity devEntity = devEntityMgmtService
                .findById(policyDTO.getId());
        
        if (policyDTO.getVersion() != -1 && policyDTO.getVersion() < devEntity.getVersion()) {
			throw new DirtyUpdateException(
					msgBundle.getText("server.error.dirty.update.code"),
					msgBundle.getText("server.error.dirty.update"));
		}
        populateComponentInfo(snapshot);
        PolicyDevelopmentStatus policyStatus = PolicyDevelopmentStatus
                .get(policyDTO.getStatus());
    
		if (policyDTO.getFullName() != null) {
			devEntity.setTitle(policyDTO.getFullName());
		} else {
			String fullName = devEntity.getTitle();
            String path = fullName.substring(0, fullName.lastIndexOf('/'));
			devEntity.setTitle(path + "/" + policyDTO.getName());
		}
		devEntity.setFolderId(policyDTO.getFolderId());
        devEntity.setDescription(policyDTO.getDescription());
        devEntity.setHidden(Boolean.FALSE);
        devEntity.setType(policyDTO.getCategory().getKey());
        devEntity.setStatus(policyStatus.getKey());
        devEntity.setLastUpdatedDate(System.currentTimeMillis());
        devEntity.setApPql("  ");
        devEntity.setOwner(getCurrentUser().getUserId());

        // if has parent treated as a sub policy
		if (policyDTO.getParentId() != null || (policyLite != null && policyLite.isHasParent())) {
			policyDTO.getAttributes().add(PolicyDTO.POLICY_EXCEPTION_ATTR);
			updateParentSubPolicyReferences(policyDTO, policyLite);
		}

        // Add Tags
        addTags(policyDTO, devEntity);

        addTrueAllowAttributeOnlyIfAllowPolicy(policyDTO);

        // retain existing sub-policies, if any
        String pql = devEntity.getPql();
        if (policyDTO.getSubPolicyRefs().isEmpty()) {
            populateExistingSubPolicies(devEntity.getTitle(), pql, policyDTO, policyLite);
        }

        pql = PolicyPQLHelper.create().getPQL(devEntity.getId(),
                devEntity.getTitle(), devEntity.getDescription(), policyDTO);
        devEntity.setPql(pql);
        devEntityMgmtService.save(devEntity);
        populateComponentInfo(policyDTO);
        policyDTO.setVersion(devEntity.getVersion());
        AuditAction action;
        if(PolicyDevelopmentStatus.APPROVED.equals(policyStatus) && !policyStatus.getKey().equals(snapshot.getStatus())) {
        	action = AuditAction.DEPLOY;
        } else if(PolicyDevelopmentStatus.DELETED.equals(policyStatus)) {
        	action = AuditAction.DELETE;
        } else {
        	action = AuditAction.UPDATE;
        }
        
        policySearchService.reIndexPolicies(policyDTO);
        entityAuditLogDao.addEntityAuditLog(action, 
        		DevEntityType.DELEGATION_POLICY.equals(policyDTO.getCategory()) ? AuditableEntity.DELEGATE_ADMIN.getCode() : AuditableEntity.POLICY.getCode(), 
        		policyDTO.getId(), snapshot.toAuditString(), 
        		PolicyDevelopmentStatus.DELETED.equals(policyStatus) ? null : policyDTO.toAuditString());
        
        return devEntity;      
    }

	/**
	 * Updates parent policy (pql) if sub policy is renamed
	 * 
	 * @param policyDTO
	 * @param policyLite
	 * @throws ConsoleException
	 */
	private void updateParentSubPolicyReferences(PolicyDTO policyDTO, PolicyLite policyLite) throws ConsoleException {
		Long parentId = policyLite.getParentPolicy().getId();
        PolicyLite parentLite = policySearchRepository.findById(parentId).orElse(null);
        if(parentLite == null) {
            return;
        }
		for (SubPolicyLite subPolicyLite : parentLite.getSubPolicies()) {
			if (subPolicyLite.getId().compareTo(policyDTO.getId()) == 0) {
				if (subPolicyLite.getName().equals(policyDTO.getName()))
					continue;
				else {
					PolicyDTO parentDTO = findById(parentId);
					String subPolicyFullName = subPolicyLite.getPolicyFullName();
                    String policyPath =
                            subPolicyFullName.substring(0, subPolicyFullName.lastIndexOf('/'));
					String subPolicyRef = policyPath + "/" + policyDTO.getName();

					List<String> subPolicyRefs = parentDTO.getSubPolicyRefs();
					if (subPolicyRefs.contains(subPolicyFullName)) {
						subPolicyRefs.remove(subPolicyFullName);
					}
					subPolicyRefs.add(subPolicyRef);
					modify(parentDTO);
				}
			}
		}
	}

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PolicyDTO findById(Long id) throws ConsoleException {
        try {
            PolicyDevelopmentEntity devEntity = devEntityMgmtService
                    .findById(id);
            if (devEntity == null) {
                log.info("No Policy found for given id: {} ", id);
                return null;
            }

            String pql = devEntity.getPql();
            PolicyDTO policy = PolicyPQLHelper.create().fromPQL(pql);
            policy.setFolderId(devEntity.getFolderId());
            policy.setFolderPath(devEntity.getFolder() == null ? null : devEntity.getFolder().getFolderPath());
            policy.setVersion(devEntity.getVersion());
            policy.setActionType(devEntity.getActionType());
            policy.setDeploymentTime(devEntity.getDeploymentTime());
            policy.setRevisionCount(devEntity.getRevisionCount());
            policy.setStatus(getByKey(devEntity.getStatus()).name());
            policy.setCategory(DevEntityType.getByKey(devEntity.getType()));
            policy.setCreatedDate(devEntity.getCreatedDate());
            policy.setOwnerId(devEntity.getOwner());
            appUserSearchRepository.findById(devEntity.getOwner())
                    .ifPresent(owner -> policy.setOwnerDisplayName(owner.getDisplayName()));
            policySearchRepository.findById(id).ifPresent(lite -> {
                if (lite.isHasParent()) {
                    policy.setHasParent(true);
                    policy.setParentId(lite.getParentPolicy().getId());
                }
            });

            policy.setActiveWorkflowRequest(EntityWorkflowRequestDTO.getDTO(devEntity.getActiveWorkflowRequest(), appUserSearchRepository));
            policy.setLastUpdatedDate(devEntity.getLastUpdatedDate());
            if (devEntity.getModifiedBy() != null) {
                policy.setModifiedById(devEntity.getModifiedBy());
                appUserSearchRepository.findById(devEntity.getModifiedBy())
                        .ifPresent(modifiedBy -> policy.setModifiedBy(modifiedBy.getDisplayName()));
            }

            populateTags(devEntity, policy);
            populateDeploymentTargets(policy);

            return policy;
        } catch (Exception ex) {
            throw new ConsoleException("Error occurred in find policy by id",
                    ex);
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public XacmlPolicyDTO getXacmlDTO(PolicyDevelopmentEntity devEntity) throws ConsoleException {
        XacmlPolicyDTO policy = new XacmlPolicyDTO();

        policy.setId(devEntity.getId());

        String fullName = devEntity.getTitle();
        String[] splits = fullName.split("/", -1);
        String name = splits[splits.length - 1];

        policy.setPolicyName(name);
        policy.setDescription(devEntity.getDescription());
        policy.setCreatedDate(new Date(devEntity.getCreatedDate()));
        policy.setLastUpdatedDate(new Date(devEntity.getLastUpdatedDate()));
        policy.setVersion(devEntity.getVersion());

        Long ownerId = devEntity.getOwner();
        if (ownerId != null) {
            policy.setOwnerId(ownerId);
            policy.setOwnerDisplayName(appUserSearchRepository.findById(ownerId)
                    .map(ApplicationUser::getDisplayName)
                    .orElse(StringUtils.EMPTY));
        }

        Long modifiedById = devEntity.getModifiedBy();
        if (modifiedById != null) {
            policy.setModifiedById(modifiedById);
            policy.setModifiedBy(appUserSearchRepository.findById(modifiedById)
                    .map(ApplicationUser::getDisplayName)
                    .orElse(StringUtils.EMPTY));
        }

        return policy;
    }

    /**
     * replace all occurence with pattern and replace the specific group with replaceWith
     * @param source
     * @param regex
     * @param groupToReplace
     * @param replacement
     * @return
     */
    private String replaceGroup(String source, String regex, int groupToReplace,
            String replacement) {
        Matcher m = Pattern.compile(regex).matcher(source);
        if (!m.find())
            return source;
        String replaced = new StringBuilder(source).replace(m.start(groupToReplace),
                m.end(groupToReplace), replacement).toString();
        return replaceGroup(replaced, regex, groupToReplace, replacement);
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PolicyDTO findActiveById(Long id) throws ConsoleException {
        PolicyDTO policy = findById(id);
        if (policy != null
                && DELETED.getKey().equals(policy.getStatus())) {
            log.info("No Policy found or trying access deleted policy, [ Id:{}, Deleted:{} ]",
                            id, true);
            return null;
        }

        return policy;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PolicyDTO addSubPolicy(PolicyDTO policyDTO) throws ConsoleException {
        Long parentId = policyDTO.getParentId();
        PolicyDTO newPolicy = save(policyDTO);

        PolicyDTO parentPolicy = findById(parentId);
        parentPolicy.getSubPolicyRefs().add(newPolicy.getFullName());
        parentPolicy.setReIndexNow(policyDTO.isReIndexNow());
        modify(parentPolicy);
        newPolicy.setParentName(parentPolicy.getName());
      
        auditService.save(POLICY_MGMT.name(), "audit.new.sub.policy",
                policyDTO.getName());
        log.info(
                "New subpolicy created. [ Parent id :{}, New sub policy id :{}]",
                parentId, newPolicy.getId());
        return newPolicy;
    }

    /*
     * Clone a policy by name
     *
     * @param fullName - the complete path (e.g. Root_37/Policy A/Sub-Policy 3) of the policy
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PolicyDTO clone(String fullName) throws ConsoleException {
        return clone(fullName, null);
    }

    /*
     * Clone a policy by name, specifying the parent path. The parent
     * path should only be used for sub-policies
     *
     * @param fullName - the complete path (e.g. Root_37/Policy A/Sub-Policy 3) of the policy
     */
    private PolicyDTO clone(String fullName, String parentPath) throws ConsoleException {
        return clone(getPolicyByFullName(fullName).getId(), parentPath);
    }
    
    /*
     * Clone a policy by id
     *
     * @param id the id of the policy to clone
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PolicyDTO clone(Long id) throws ConsoleException {
        return clone(id, null);
    }

    /*
     * Clone a policy by id, specifying a parent path. The parent path
     * should only be used for sub-policies (TODO - check this)
     *
     * @param id the id of the policy to clone
     * @param path the parent path. If null, generate a new path based
     *             on the cloned id (this is the case for top-level
     *             policies. Sub-policies must specify the parent's path)
     *
     * Note: Sub-policies should not be cloned except as part of
     * cloning the parent. There is no easy way to check this,
     * although checking to see if the policy is a sub-policy but the
     * parent path is not set and flagging that as an error is a good
     * start
     */
    private PolicyDTO clone(Long id, String parentPath) throws ConsoleException {
        try {
            PolicyDevelopmentEntity devEntity = devEntityMgmtService.findById(id);
            PolicyDevelopmentEntity clone;
            if (devEntity != null) {

                accessControlService.authorizeByTags(ActionType.INSERT,
                        DelegationModelShortName.POLICY_ACCESS_TAGS,
                        devEntity,
                        false);

                clone = new PolicyDevelopmentEntity();
                clone.setCreatedDate(System.currentTimeMillis());
                clone.setLastUpdatedDate(System.currentTimeMillis());
                String title = devEntity.getTitle() + "_TEMP";
                clone.setTitle(title);
                clone.setDescription(devEntity.getDescription());
                clone.setApPql(devEntity.getApPql());
                clone.setPql(devEntity.getPql());
                clone.setStatus(DRAFT.getKey());
                clone.setType(devEntity.getType());
                clone.setSubmitter(devEntity.getSubmitter());
                clone.setSubmittedTime(devEntity.getSubmittedTime());
                clone.setFolderId(devEntity.getFolderId());
                clone.setFolder(devEntity.getFolder());
                clone.setTags(new HashSet<>());
                for (TagLabel tag : devEntity.getTags()) {
                    TagLabel tagLbl = tagLabelService.findById(tag.getId());
                    clone.getTags().add(tagLbl);
                }

                clone = devEntityMgmtService.save(clone);

                if (parentPath == null) {
                    parentPath = POLICY_ROOT + "_" + clone.getId();
                }
                title = getClonedName(devEntity.getTitle());
                String name = getPolicyName(title);

                // This does rely on the correct path being passed in...
                clone.setTitle(parentPath + "/" + name);

                PolicyPQLHelper pqlHelper = PolicyPQLHelper.create();
                PolicyDTO policyDTO = pqlHelper.fromPQL(clone.getPql());
                policyDTO.setFolderId(clone.getFolderId());
                policyDTO.setName(name);
                policyDTO.setFullName(clone.getTitle());
                policyDTO.setDescription(clone.getDescription());
                if (clone.getStatus().equals(APPROVED.getKey())) {
                    policyDTO.setStatus("DEPLOYED");
                } else {
                    policyDTO.setStatus("DRAFT");
                }
                policyDTO.setFolderPath(clone.getFolder() == null ? null : clone.getFolder().getFolderPath());
                policyDTO.setId(clone.getId());

                populateDeploymentTargets(policyDTO);

                // Clone the sub-policies.
                List<String> subPolicies = pqlHelper.fromPQL(devEntity.getPql()).getSubPolicyRefs();
                ArrayList<String> newSubPolicyRefs = new ArrayList<>();
                for (String subPolicy : subPolicies) {
                    PolicyDTO clonedSubPolicy = clone(subPolicy, clone.getTitle());
                    newSubPolicyRefs.add(clonedSubPolicy.getFullName());
                }
                policyDTO.setSubPolicyRefs(newSubPolicyRefs);
                
                String clonedPql = pqlHelper.getPQL(clone.getId(), clone.getTitle(), clone.getDescription(), policyDTO);
                clone.setPql(clonedPql);
                clone = devEntityMgmtService.save(clone);

                policySearchService.reIndexPolicies(policyDTO);
                auditService.save(POLICY_MGMT.name(), "audit.cloned.policy",
                        devEntity.getNameFromTitle(), policyDTO.getName());
                populateComponentInfo(policyDTO);
                entityAuditLogDao.addEntityAuditLog(AuditAction.CREATE, 
                		DevEntityType.DELEGATION_POLICY.equals(policyDTO.getCategory()) ? AuditableEntity.DELEGATE_ADMIN.getCode() : AuditableEntity.POLICY.getCode(), 
                		clone.getId(), null, policyDTO.toAuditString());
                
                log.info("Policy cloned successfully, [Cloned Policy id: {}]",
                        id);
                return policyDTO;
            }
        } catch (PQLException e) {
            throw new ConsoleException(
                    "Error occurred in cloning the given policy", e);
        }

        return null;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<PolicyDeploymentEntity> deploymentHistory(Long id)
            throws ConsoleException {
        List<PolicyDeploymentEntity> policyDepoymentDetails = deploymentMgmtService
                .findByPolicyId(id);

        log.info(
                "Policy deployment details loaded for policy id :{}, No of records :{}",
                id, policyDepoymentDetails.size());

        return policyDepoymentDetails;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PolicyDeploymentHistoryDTO viewRevision(Long revisionId)
            throws ConsoleException {
        try {
            PolicyDeploymentEntity historyEntity = deploymentMgmtService
                    .findById(revisionId);

            PolicyDeploymentHistoryDTO revisionDTO = PolicyDeploymentHistoryDTO
                    .getDTO(historyEntity, "", appUserSearchRepository);
            PolicyDTO policyDto = PolicyPQLHelper.create()
                    .fromPQL(historyEntity.getPql());

            populateDeploymentTargets(policyDto);

            populateRefComponents(policyDto.getActionComponents());
            populateRefComponents(policyDto.getSubjectComponents());
            populateRefComponents(policyDto.getToSubjectComponents());
            populateRefComponents(policyDto.getFromResourceComponents());
            populateRefComponents(policyDto.getToResourceComponents());

            revisionDTO.setPolicyDetail(policyDto);

            log.info("View policy revision loaded for policy id :{},",
                    policyDto.getId());

            return revisionDTO;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error occurred in load revision details by id", e);
        }
    }

    private void populateRefComponents(List<PolicyComponent> policyComponents)
            throws ConsoleException {
        for (PolicyComponent plcyComponent : policyComponents) {
            for (ComponentDTO component : plcyComponent.getComponents()) {
                ComponentLite comp = componentSearchRepository
                        .findById(component.getId()).orElse(null);
                if (comp != null) {
                    component.setName(comp.getName());
                    component.setStatus(comp.getStatus());
                    component.setVersion(comp.getVersion());
                } else {
                    // named the deleted components
                    ComponentDTO compDTO = componentMgmtService
                            .findById(component.getId());
                    component.setName(compDTO.getName());
                    component.setStatus(compDTO.getStatus());
                }
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PolicyDTO revertToVersion(Long revisionId) throws ConsoleException {

        try {
            PolicyDeploymentEntity historyEntity = deploymentMgmtService
                    .findById(revisionId);

            PolicyDevelopmentEntity devEntity = devEntityMgmtService
                    .findById(historyEntity.getDevelopmentId());

            accessControlService.authorizeByTags(ActionType.EDIT,
                    DelegationModelShortName.POLICY_ACCESS_TAGS,
                    devEntity,
                    false);

            devEntity.setTitle(historyEntity.getName());
            devEntity.setDescription(historyEntity.getDescription());
            devEntity.setStatus(DRAFT.getKey());
            devEntity.setLastModified(System.currentTimeMillis());
            devEntity.setLastUpdatedDate(System.currentTimeMillis());

            PolicyPQLHelper pqlHelper = PolicyPQLHelper.create();
            PolicyDTO policyDTO = pqlHelper.fromPQL(historyEntity.getPql());
            policyDTO.setFolderId(devEntity.getFolderId());
            policyDTO.setStatus(DRAFT.name());

            String updatedPql = pqlHelper.getPQL(devEntity.getId(),
                    devEntity.getTitle(), devEntity.getDescription(),
                    policyDTO);
            devEntity.setPql(updatedPql);

            devEntityMgmtService.save(devEntity);

            auditService.save(POLICY_MGMT.name(), "audit.reverted.policy",
                    devEntity.getNameFromTitle(), String.valueOf(revisionId));
            log.info(
                    "Policy details reverted to old revision[ Policy Id :{}, Revision :{}",
                    devEntity.getId(), revisionId);

            PolicyDTO dto = new PolicyDTO();
            dto.setId(devEntity.getId());
            
            policySearchRepository.deleteById(historyEntity.getId());
            policySearchService.reIndexPolicies(dto);

            return dto;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in revert to given version,", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PolicyDTO removeAndDereference(PolicyDevelopmentEntity devEntity) throws ConsoleException {
        log.debug("Removing and de-referencing policy, id: {}", devEntity.getId());
        PolicyDTO policyDTO = findById(devEntity.getId());

        accessControlService.authorizeByTags(ActionType.DELETE,
                DelegationModelShortName.POLICY_ACCESS_TAGS,
                policyDTO, false);

        List<Long> subPoliciesIds = getSubPoliciesIdList(devEntity.getId());
        log.debug("No of sub-policies :{}", subPoliciesIds.size());
        remove(subPoliciesIds, false);
        policyLifeCycleHandler.unDeployEntity(devEntity, DELETED);
        auditService.save(POLICY_MGMT.name(), "audit.delete.policy",
                devEntity.getNameFromTitle());
        populateComponentInfo(policyDTO);
        entityAuditLogDao.addEntityAuditLog(AuditAction.DELETE,
                DevEntityType.DELEGATION_POLICY.equals(policyDTO.getCategory()) ? AuditableEntity.DELEGATE_ADMIN.getCode() : AuditableEntity.POLICY.getCode(),
                devEntity.getId(), policyDTO.toAuditString(), null);

     // de-reference from parent
        deReference(devEntity.getId());
        policySearchRepository.deleteById(devEntity.getId());

        log.debug("Successfully removed and de-referenced policy, id: {}", devEntity.getId());
        return policyDTO;
    }

    public void deReference(Long id) throws ConsoleException {
        PolicyLite policyLite = policySearchRepository.findById(id).orElse(null);
        if (policyLite == null || policyLite.getParentPolicy() == null) {
            return;
        }
        Long parentPolicyId = policyLite.getParentPolicy().getId();
        PolicyDevelopmentEntity parentEntity = devEntityMgmtService
                .findById(parentPolicyId);
        if (parentEntity != null) {
            try {
                PolicyPQLHelper pqlHelper = PolicyPQLHelper.create();
                PolicyDTO policyDto = pqlHelper.fromPQL(parentEntity.getPql());
                int index = 0;
                for (String subPolicyRef : policyDto.getSubPolicyRefs()) {
                    if (subPolicyRef.equals(policyLite.getPolicyFullName())) {
                        policyDto.getSubPolicyRefs().remove(index);
                        break;
                    }
                    index++;
                }

                String pql = pqlHelper.getPQL(parentEntity.getId(),
                        parentEntity.getTitle(), parentEntity.getDescription(),
                        policyDto);
                parentEntity.setPql(pql);
                devEntityMgmtService.save(parentEntity);
                /*
                 * If current state is deployed, after de-referencing should
                 * re-deploy.
                 */
                if (APPROVED.name().equals(parentEntity.getStatus())
                        && deploymentMgmtService
                                .isEntityDeployed(parentPolicyId)) {
                    policyLifeCycleHandler.unDeployEntity(parentEntity);
                    policyLifeCycleHandler.deployEntity(parentEntity);
                }
                
                policySearchService.reIndexPolicies(policyDto);
                log.info(
                        "Policy parent's sub policy references updated successfully, [ Parent Id:{}]",
                        parentEntity.getId());
            } catch (PQLException e) {
                throw new ConsoleException(
                        "Error encountered in processing parant policy PQL", e);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<PolicyDTO> remove(List<Long> ids, boolean reIndex) throws ConsoleException {
        List<PolicyDevelopmentEntity> authorizedDevEntities = new ArrayList<>();
        List<PolicyDTO> removedEntities = new ArrayList<>();

        for (Long id : ids) {
            PolicyDevelopmentEntity devEntity = devEntityMgmtService.findById(id);
            if (devEntity != null) {
                accessControlService.authorizeByTags(ActionType.DELETE,
                        DelegationModelShortName.POLICY_ACCESS_TAGS,
                        devEntity,
                        false);
                if (PolicyStatus.APPROVED.name().equals(getByKey(devEntity.getStatus()).name())) {
                    accessControlService.authorizeByTags(ActionType.DEPLOY,
                            DelegationModelShortName.POLICY_ACCESS_TAGS,
                            devEntity,
                            false);
                }
                authorizedDevEntities.add(devEntity);
            }
        }

        for (PolicyDevelopmentEntity devEntity : authorizedDevEntities) {
            removedEntities.add(removeAndDereference(devEntity));
        }

        return removedEntities;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deploy(DeploymentRequestDTO deploymentRequest) throws ConsoleException {
        try {
            long policyId = deploymentRequest.getId();
            PolicyDTO snapshot = findById(policyId);
            PolicyDevelopmentEntity devEntity = devEntityMgmtService
                    .findById(policyId);
            populateComponentInfo(snapshot);
            policyLifeCycleHandler.deployEntity(devEntity, deploymentRequest.getDeploymentTime());
            PolicyDTO policyDTO = findById(policyId);
            populateComponentInfo(policyDTO);
            devEntity.setApprovedPql(devEntity.getPql());
            devEntityMgmtService.save(devEntity);
            
            policySearchService.reIndexPolicies(policyDTO);

            entityAuditLogDao.addEntityAuditLog(AuditAction.DEPLOY, 
            		DevEntityType.DELEGATION_POLICY.equals(policyDTO.getCategory()) ? AuditableEntity.DELEGATE_ADMIN.getCode() : AuditableEntity.POLICY.getCode(), 
            		policyDTO.getId(), snapshot.toAuditString(), 
            		policyDTO.toAuditString());

            auditService.save(POLICY_MGMT.name(), "audit.deploy.policy",
                    devEntity.getNameFromTitle());
        } catch (Exception e) {
            String message = StringUtils.isEmpty(e.getMessage()) ?
                    "Error encountered in policy deployment" :
                    e.getMessage();
            throw new ConsoleException(message, e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void xacmlPolicyDeploy(DeploymentRequestDTO deploymentRequest) throws ConsoleException {
        try {
            long policyId = deploymentRequest.getId();

            PolicyDevelopmentEntity devEntity = devEntityMgmtService
                    .findById(policyId);

            XacmlPolicyDTO snapshot = getXacmlDTO(devEntity);

            policyLifeCycleHandler.deployEntity(devEntity, deploymentRequest.getDeploymentTime());
            XacmlPolicyDTO policyDTO = getXacmlDTO(devEntity);
            xacmlPolicySearchService.reIndexXacmlPolicy(devEntity);

            entityAuditLogDao.addEntityAuditLog(AuditAction.DEPLOY,
                    AuditableEntity.XACML_POLICY.getCode(),
                    policyDTO.getId(), snapshot.toAuditString(),
                    policyDTO.toAuditString());

            auditService.save(POLICY_MGMT.name(), "audit.deploy.xacml.policy",
                    devEntity.getNameFromTitle());

        } catch (Exception e) {
            String message = StringUtils.isEmpty(e.getMessage()) ?
                    "Error encountered in xacml policy deployment" :
                    e.getMessage();
            throw new ConsoleException(message, e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<DeploymentResponseDTO> deploy(List<DeploymentRequestDTO> deploymentRequests, boolean reIndex) throws ConsoleException {

        List<DeploymentRequestDTO> dependencyDeploymentRequests = new ArrayList<>();
        for (DeploymentRequestDTO deploymentRequest : deploymentRequests) {
            if(policyWorkflowProperties.isWorkflowEnabled() && isSubPolicyNotApproved(deploymentRequest.getId())) {
                throw new ConsoleException("All the policies/ sub policies have to be approved to deploy.");
            }
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

        for (DeploymentRequestDTO deploymentRequest : deploymentRequests) {
            PolicyDevelopmentEntity devEntity = devEntityMgmtService.findById(deploymentRequest.getId());
            if (devEntity != null) {
                if (DevEntityType.COMPONENT.equals(deploymentRequest.getType())) {
                    accessControlService.checkAuthority(DelegationModelActions.DEPLOY_COMPONENT, ActionType.DEPLOY,
                            AuthorizableType.COMPONENT);
                }
                accessControlService.authorizeByTags(ActionType.DEPLOY,
                        DevEntityType.COMPONENT.equals(deploymentRequest.getType()) ?
                                DelegationModelShortName.COMPONENT_ACCESS_TAGS :
                                DelegationModelShortName.POLICY_ACCESS_TAGS,
                        devEntity,
                        false);
                if (devEntity.getActiveWorkflowRequest() != null){
                    devEntity.getActiveWorkflowRequest().setStatus(EntityWorkflowRequestStatus.DEPLOYED);
                }
            }
        }

        boolean push = false;
        List<DeploymentResponseDTO> deploymentResponses = new ArrayList<>();
        for (DeploymentRequestDTO deploymentRequest : deploymentRequests) {
            if (DevEntityType.COMPONENT.equals(deploymentRequest.getType())) {
                componentMgmtService.deploy(deploymentRequest);
            } else if (DevEntityType.XACML_POLICY.equals(deploymentRequest.getType())) {
                xacmlPolicyDeploy(deploymentRequest);
            } else {
                deploy(deploymentRequest);
            }
            push = push || deploymentRequest.isPush();
            deploymentResponses.add(new DeploymentResponseDTO(deploymentRequest.getId()));
        }

        if (push) {
            List<PushResultDTO> pushResults = dpsProxyService.schedulePush(new Date());
            deploymentResponses.forEach(deploymentResponse -> deploymentResponse.setPushResults(pushResults));
            if (log.isInfoEnabled()) {
                log.info("Policy update push requested, [Policy Ids:{}]",
                        deploymentResponses.stream()
                                .map(deploymentResponse -> String.valueOf(deploymentResponse.getId()))
                                .collect(Collectors.joining(",")));
            }
        }

        return deploymentResponses;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public DeploymentResponseDTO saveAndDeploy(PolicyDTO policyDTO) throws ConsoleException {

        PolicyDevelopmentEntity entity = null;
        if (policyDTO.getId() != null) {
            entity = modifyPolicy(policyDTO);
            log.info("Policy details updated, Policy Id:{}", entity.getId());
        } else {
            entity = savePolicy(policyDTO);
            policyDTO.setId(entity.getId());
            policyDTO.getDeploymentRequest().setId(entity.getId());
            log.info("New Policy details saved, Policy Id:{}", entity.getId());
        }
        List<DeploymentRequestDTO> deploymentRequests = new ArrayList<>();
        deploymentRequests.add(policyDTO.getDeploymentRequest());
        List<DeploymentResponseDTO> deploymentResponses = deploy(deploymentRequests, true);

        log.info("Policy deployment triggered, Policy Id:{}", entity.getId());

        return deploymentResponses.get(0);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Long saveAndDeploySubPolicy(PolicyDTO policyDTO)
            throws ConsoleException {
        PolicyDevelopmentEntity subPolicy = null;

        if (policyDTO.getId() == null) {
            subPolicy = savePolicy(policyDTO);
            policyDTO.getDeploymentRequest().setId(subPolicy.getId());
            PolicyDTO parent = findById(policyDTO.getParentId());
            parent.getSubPolicyRefs().add(subPolicy.getTitle());
            modifyPolicy(parent);
        } else {
            subPolicy = modifyPolicy(policyDTO);
        }
        
        List<DeploymentRequestDTO> deploymentRequests = new ArrayList<>();
        deploymentRequests.add(policyDTO.getDeploymentRequest());
        deploy(deploymentRequests, true);

        log.info("Sub policy deployment triggered, Policy Id:{}",
                subPolicy.getId());
        auditService.save(POLICY_MGMT.name(), "audit.deploy.sub.policy",
                policyDTO.getName());
        return subPolicy.getId();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<ValidationDetailDTO> validateAndDeploy(List<DeploymentRequestDTO> deploymentRequests)
            throws ConsoleException {
        List<ValidationDetailDTO> validateDTOList = new ArrayList<>();

        Map<Long, PolicyDTO> authorizedPolicies = new HashMap<>();
        for (DeploymentRequestDTO deploymentRequest : deploymentRequests) {
            PolicyDTO policy = findById(deploymentRequest.getId());
            if (policy != null) {
                accessControlService.authorizeByTags(ActionType.DEPLOY,
                        DelegationModelShortName.POLICY_ACCESS_TAGS,
                        policy,
                        false);
                authorizedPolicies.put(policy.getId(), policy);
            }
        }

        try {
            boolean push = false;
            for (DeploymentRequestDTO deploymentRequest : deploymentRequests) {
                PolicyDTO policy = authorizedPolicies.get(deploymentRequest.getId());
                ValidationDetailDTO validationDetail = validatorService
                        .validate(policy);
                Set<DeploymentRequestDTO> toBeDeployedSet = new TreeSet<>();
                if (validationDetail.isDeployable()) {
                    for (ValidationRecord record : validationDetail
                            .getDetails()) {
                        if (!record.isDeployed()) {
                            toBeDeployedSet.add(new DeploymentRequestDTO(record.getId(),
                                    DevEntityType.POLICY.name().equalsIgnoreCase(record.getType()) ?
                                            DevEntityType.POLICY : DevEntityType.COMPONENT,
                                    false,
                                    deploymentRequest.getDeploymentTime(), false)
                            );
                        }
                    }
                    toBeDeployedSet.add(new DeploymentRequestDTO(deploymentRequest.getId(),
                            DevEntityType.POLICY,
                            false,
                            deploymentRequest.getDeploymentTime(), false));
                    deploy(new ArrayList<>(toBeDeployedSet), false);
                    push = push || deploymentRequest.isPush();
                }
                validateDTOList.add(validationDetail);
            }

            if (push) {
                // Schedule time should be the server time.
                List<PushResultDTO> pushResults = dpsProxyService.schedulePush(new Date());
                validateDTOList.forEach(validationDetailDTO -> validationDetailDTO.setPushResults(pushResults));
            }

            if (log.isInfoEnabled()) {
                log.info(
                        "Policy and and its reference components have been validated and deployed{}successfully, [ Policy Ids: {}]",
                        push ? " (push) " : " ",
                        validateDTOList.stream()
                                .map(validationDetail -> String.valueOf(validationDetail.getId()))
                                .collect(Collectors.joining(",")));
            }
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in policy deployment", e);
        }
        return validateDTOList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void unDeploy(PolicyDevelopmentEntity devEntity) throws ConsoleException {
        try {
            //get references to undeploy
            Set<Long> referencesSet = new TreeSet<>();
            List<Long> referenceComponents = getReferencesToDeploy(devEntity.getId(), referencesSet);
            for (Long refId : referenceComponents) {
                PolicyDevelopmentEntity refDevEntity = devEntityMgmtService
                        .findById(refId);
				policyLifeCycleHandler.unDeployEntity(refDevEntity, OBSOLETE);
            }
            PolicyDTO snapshot = findById(devEntity.getId());
            populateComponentInfo(snapshot);
            policyLifeCycleHandler.unDeployEntity(devEntity, OBSOLETE);
            
            PolicyDTO policyDTO = findById(devEntity.getId());
            populateComponentInfo(policyDTO);
            policyDTO.setDeployed(false);
            policySearchService.reIndexPolicies(policyDTO);
            entityAuditLogDao.addEntityAuditLog(AuditAction.UNDEPLOY, 
            		DevEntityType.DELEGATION_POLICY.equals(policyDTO.getCategory()) ? AuditableEntity.DELEGATE_ADMIN.getCode() : AuditableEntity.POLICY.getCode(), 
            		policyDTO.getId(), snapshot.toAuditString(), 
            		policyDTO.toAuditString());
            auditService.save(POLICY_MGMT.name(), "audit.deactivated.policy",
                    devEntity.getNameFromTitle());

        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in Policy deployement", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void unDeploy(List<Long> policyIds) throws ConsoleException {
        List<PolicyDevelopmentEntity> authorizedDevEntities = new ArrayList<>();

        for (Long id : policyIds) {
            unDeploy(getSubPoliciesIdList(id));

            PolicyDevelopmentEntity devEntity = devEntityMgmtService.findById(id);
            if (devEntity != null) {
                accessControlService.authorizeByTags(ActionType.DEPLOY,
                        DelegationModelShortName.POLICY_ACCESS_TAGS,
                        devEntity,
                        false);
                authorizedDevEntities.add(devEntity);
            }
        }

        for (PolicyDevelopmentEntity devEntity : authorizedDevEntities) {
            unDeploy(devEntity);
        }
    }

    private void populateTags(PolicyDevelopmentEntity devEntity,
            PolicyDTO policyDTO) {
        policyDTO.getTags().clear();
        for (TagLabel tag : devEntity.getTags()) {
            policyDTO.getTags().add(TagDTO.getDTO(tag));
        }
    }

    private void addTags(PolicyDTO policyDTO, PolicyDevelopmentEntity devEntity)
            throws ConsoleException {
        devEntity.getTags().clear();
        for (TagDTO tagDTO : policyDTO.getTags()) {
            TagLabel tag = tagLabelService.findById(tagDTO.getId());
            if (tag != null) {
                tagDTO.setKey(tag.getKey());
                tagDTO.setLabel(tag.getLabel());
                devEntity.getTags().add(tag);
            }
        }
    }

    private void populateDeploymentTargets(PolicyDTO policyDTO) {
        if (!CollectionUtils.isEmpty(policyDTO.getDeploymentTargets())) {
            Map<Long, Agent> agents = agentSearchService.findByIds(
                    policyDTO.getDeploymentTargets().stream()
                            .map(AgentDTO::getId)
                            .collect(Collectors.toList()));
            if (MapUtils.isNotEmpty(agents)) {
                ListIterator<AgentDTO> iterator = policyDTO.getDeploymentTargets().listIterator();
                while (iterator.hasNext()) {
                    AgentDTO agentDTO = iterator.next();
                    Agent agent = agents.get(agentDTO.getId());
                    if (agent == null) {
                        iterator.remove();
                    } else {
                        agentDTO.setType(agent.getType());
                        agentDTO.setHost(agent.getHost());
                    }
                }
            } else {
                policyDTO.getDeploymentTargets().clear();
            }
        }
    }

    public String newPQL(Long id, String name) {
        EntityType type = EntityType.POLICY;
        StringBuffer res = new StringBuffer(128);
        res.append("ID ");
        res.append(id);
        res.append(" STATUS NEW ");
        res.append(type.emptyPql(name));
        return res.toString();
    }

    /**
     * Get all the sub-policies of a policy, given the id
     * 
     * @param id
     * @return List of sub-policy id(s)
     * @throws ConsoleException
     */
    private List<Long> getSubPoliciesIdList(Long id) throws ConsoleException {
        List<Long> subPoliciesIds = new ArrayList<>();
        try {

            policySearchRepository.findById(id)
                    .ifPresent(policyLite -> {
                        List<SubPolicyLite> subPolicies = policyLite.getSubPolicies();
                        for (SubPolicyLite tempSubPolicy : subPolicies) {
                            subPoliciesIds.add(tempSubPolicy.getId());
                        }
                    });
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in getting sub-policies from Policy ",
                    e);
        }

        return subPoliciesIds;
    }

    /**
     * Gets the sub-policies from policy PQL
     * 
     * @param pql
     * @param policyDTO
     * @throws ConsoleException
     */
	private void populateExistingSubPolicies(String title, String pql, PolicyDTO policyDTO, PolicyLite policyLite)
			throws ConsoleException {
		DomainObjectBuilder domBuilder = new DomainObjectBuilder(pql);
		policyDTO.getSubPolicyRefs().clear();
		try {
			IDPolicy idPolicy = domBuilder.processPolicy();
			String idPolicyName = idPolicy.getName();
			if (idPolicy.getPolicyExceptions() != null) {
				for (IPolicyReference reference : idPolicy.getPolicyExceptions().getPolicies()) {
					if (reference != null) {
						String referenceName = reference.getReferencedName();
						if (idPolicyName.equals(title)) {
							policyDTO.getSubPolicyRefs().add(reference.getReferencedName());
						} else {
							//policy renamed, update all existing sub-policies 
							updateExistingSubPolicies(title, policyDTO, policyLite, referenceName);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new ConsoleException("Error encountered in getting existing sub-policies", e);
		}
	}

	private void updateExistingSubPolicies(String title, PolicyDTO policyDTO, PolicyLite policyLite,
			String referenceName) throws ConsoleException {
		String subPolicyName = referenceName.substring(referenceName.lastIndexOf('/') + 1);
		String subPolicyRef = title + "/" + subPolicyName;
		policyDTO.getSubPolicyRefs().add(subPolicyRef);
		for (SubPolicyLite subPolicyLite : policyLite.getSubPolicies()) {
			if (subPolicyLite.getName().equals(subPolicyName)) {
				Long subPolicyId = subPolicyLite.getId();
				PolicyDTO subPolicyDTO = findById(subPolicyId);
				subPolicyDTO.setName(subPolicyName);
				subPolicyDTO.setFullName(subPolicyRef);
				modify(subPolicyDTO);
			}
		}
	}
	

    @Override
    public boolean isPolicyExists(String policyName) throws ConsoleException {
        try {
            checkPolicyNameIsUnique(policyName);
            return false;
        } catch (NotUniqueException e) {
            return true;
        }
    }

    @Override
    public PolicyLite enforceTBAC(PolicyLite lite) {
        lite = accessControlService.enforceTBAConPolicy(lite);

        // Add sub policy level access control
        for (SubPolicyLite subPolicyLite : lite.getSubPolicies()) {
            policySearchRepository.findById(subPolicyLite.getId())
                    .ifPresent(subPolicy -> {
                        accessControlService.enforceTBAConPolicy(subPolicy);
                        subPolicyLite.setAuthorities(subPolicy.getAuthorities());
                    });
        }

        // add parent access control
        ParentPolicyLite parentPolicyLite = lite.getParentPolicy();
        if (parentPolicyLite != null) {
            policySearchRepository.findById(parentPolicyLite.getId())
                    .ifPresent(parentPolicy -> {
                        accessControlService.enforceTBAConPolicy(parentPolicy);
                        parentPolicyLite.setAuthorities(parentPolicy.getAuthorities());
                    });
        }
        return lite;
    }

    /**
     * Checks if the given name is unique
     * 
     * @param policyName
     * 
     */
    private void checkPolicyNameIsUnique(String policyName) {
        List<PolicyLite> policies = getPolicyByName(policyName);
        if (!policies.isEmpty()) {
            throw new NotUniqueException(
                    msgBundle.getText("server.error.not.unique.code"),
                    msgBundle.getText("server.error.policy.name.not.unique",
                            policyName));
        }
    }
    
    private void checkPolicyNameIsUniqueOnUpdate(String policyName, Long policyId) {
        List<PolicyLite> policies = getPolicyByName(policyName);
        if (!policies.isEmpty()) {
        	for (PolicyLite policy : policies){
                if (policy.getId().compareTo(policyId) != 0) {
                    throw new NotUniqueException(
                            msgBundle.getText("server.error.not.unique.code"),
                            msgBundle.getText("server.error.policy.name.not.unique",
                                    policyName));
                	}
        	}
        }
    }

	public PolicyLite getPolicyByFullName(String policyName) throws ConsoleException {
        MatchAllQueryBuilder query = QueryBuilders.matchAllQuery();
        BoolQueryBuilder filter = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("policyFullName", policyName));

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(query).withFilter(filter)
                .withPageable(PageRequest.of(0, 1)).build();

        Page<PolicyLite> policyLitePage = policySearchRepository
                .search(searchQuery);

        List<PolicyLite> policies = policyLitePage.getContent();

        // Matching no policies is acceptable. More than one is a (strange) error

        if (policies.isEmpty()) {
            return null;
        } else if (policies.size() == 1) {
            return policies.get(0);
        }

        throw new ConsoleException("Multiple policies found with name " + policyName);
	}
    
    @Override
	public List<PolicyLite> getPolicyByName(String policyName) {
        MatchAllQueryBuilder query = QueryBuilders.matchAllQuery();
        BoolQueryBuilder filter = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("lowercase_name", policyName.toLowerCase()));

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(query).withFilter(filter)
                .withPageable(PageRequest.of(0, 1)).build();

        Page<PolicyLite> policyLitePage = policySearchRepository
                .search(searchQuery);

        return policyLitePage.getContent();
	}

    @Override
	public List<XacmlPolicyLite> getXacmlPolicyByName(String policyName) {
        MatchAllQueryBuilder query = QueryBuilders.matchAllQuery();
        BoolQueryBuilder filter = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("lowercaseName", policyName.toLowerCase()));

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(query).withFilter(filter)
                .withPageable(PageRequest.of(0, 1)).build();

        Page<XacmlPolicyLite> policyLitePage = xacmlPolicySearchRepository.search(searchQuery);

        return policyLitePage.getContent();
	}

    @Override
    public Set<DeploymentDependency> findDependencies(List<Long> ids) throws ConsoleException {
        Set<DeploymentDependency> dependencies = new HashSet<>();
        for (Long id : ids) {
            validatorService.findDependenciesOfPolicy(dependencies, id, true);
        }
        dependencies.forEach(dependency -> {
            if (ids.contains(dependency.getId())) {
                dependency.setProvided(true);
                dependency.setOptional(false);
            }
        });
        return dependencies;
    }

    public String getClonedName(String policyName) {
		String cloneTitle = policyName;
		int index = cloneTitle.indexOf('/');
		if (index != -1){
			String rootFolder = cloneTitle.substring(0, index);
			String titleName = cloneTitle.substring(index + 1);
			if (titleName.length() >= MAX_ALLOWED_LENGTH) {
				cloneTitle = getCloneNameForLongTitle(rootFolder, titleName);
			}
		}	
        while (true) {
            try {
                cloneTitle = JavaBeanCopier.clonedLabelSuffix(cloneTitle);
                String cloneName = getPolicyName(cloneTitle);
                checkPolicyNameIsUnique(cloneName);
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

    private String getPolicyName(String cloneTitle) {
        int index = cloneTitle.lastIndexOf('/');
        return cloneTitle.substring(index + 1);
    }

    private List<Long> getReferencesToDeploy(Long policyId,  
    		Set<Long> referencesSet ) throws ConsoleException {

        PolicyDTO policyDTO = findById(policyId);
        
		// get reference components
		getComponentReferences(referencesSet, policyDTO.getActionComponents());
		getComponentReferences(referencesSet, policyDTO.getSubjectComponents());
		getComponentReferences(referencesSet, policyDTO.
				getToSubjectComponents());
		getComponentReferences(referencesSet, policyDTO.
				getFromResourceComponents());
		getComponentReferences(referencesSet, policyDTO.
				getToResourceComponents());

		// get sub-policies
		List<Long> subPoliciesIds = getSubPoliciesIdList(policyId);

		// add sub-policies to references set
		for (Long subPolicyId : subPoliciesIds) {
			referencesSet.add(subPolicyId);
			
			// add undeployed components of the sub-policy to the references set
			getReferencesToDeploy(subPolicyId, referencesSet);
		}
		   
        return new ArrayList<>(referencesSet);
       
    }

    private void getComponentReferences(Set<Long> referencesSet,
            List<PolicyComponent> policyComponents) throws ConsoleException {
        for (PolicyComponent policyComponent : policyComponents) {
            List<ComponentDTO> components = policyComponent.getComponents();
            addUndeployedReferences(referencesSet, components);
        }
    }

    private void addUndeployedReferences(Set<Long> referencesSet,
            List<ComponentDTO> components) throws ConsoleException {
        for (ComponentDTO component : components) {
            ComponentDTO componentDTO = componentMgmtService.findById(component.getId());

            // add to set if undeployed
            componentSearchRepository.findById(component.getId())
                    .ifPresent(componentLite -> {
                        if (!componentLite.getStatus().equals(PolicyStatus.APPROVED.name()) && !componentLite.isDeployed()) {
                            referencesSet.add(componentDTO.getId());
                        }
                    });

            // check for sub components
            List<ComponentDTO> subComponents = new ArrayList<>();
            for (MemberCondition memberCondition : componentDTO.getMemberConditions()) {
                for (MemberDTO member : memberCondition.getMembers()) {
                    if (!ComponentPQLHelper.MEMBER_GROUP.equals(member.getType())) {
                        subComponents.add(new ComponentDTO(member.getId()));
                    }
                }
            }
            if (!subComponents.isEmpty()) {
                addUndeployedReferences(referencesSet, subComponents);
            }
        }
    }

    private void addTrueAllowAttributeOnlyIfAllowPolicy(PolicyDTO policyDTO) {
        if (PolicyEffect.allow.name().equalsIgnoreCase(policyDTO.getEffectType())
                && !policyDTO.isSkipAddingTrueAllowAttribute() && !isSkyDRMPolicy(policyDTO)) {
            policyDTO.getAttributes().add(PolicyDTO.POLICY_TRUE_ALLOW_ATTR);
        }
    }

    private boolean isSkyDRMPolicy(PolicyDTO policyDTO) {
        String skyDRMTagKey = msgBundle.getText("tag.skydrm.key");
        return policyDTO.getTags().stream()
                .anyMatch(tagDTO -> tagDTO.getKey().equalsIgnoreCase(skyDRMTagKey));
    }
    
    /**
     * Populate component's name and version for auditing purpose.
     * @param policyDTO PolicyDTO object for audit logging
     * @throws ConsoleException All kind of unhandled exceptions
     */
    private void populateComponentInfo(PolicyDTO policyDTO) 
    		throws ConsoleException {
    	if(policyDTO != null) {
    		if(policyDTO.getSubjectComponents() != null) {
    			populateRefComponents(policyDTO.getSubjectComponents());
    		}
    		
    		if(policyDTO.getToSubjectComponents() != null) {
    			populateRefComponents(policyDTO.getToSubjectComponents());
    		}
    		
    		if(policyDTO.getFromResourceComponents() != null) {
    			populateRefComponents(policyDTO.getFromResourceComponents());
    		}
    		
    		if(policyDTO.getToResourceComponents() != null) {
    			populateRefComponents(policyDTO.getToResourceComponents());
    		}
    		
    		if(policyDTO.getActionComponents() != null) {
    			populateRefComponents(policyDTO.getActionComponents());
    		}
    	}
    }

    /**
     * Move list of policies to another folder. Sub-policies are also moved with the parent policy.
     *
     * @param destinationFolderId destination folder id
     * @param ids                 list of policy ids
     * @throws ConsoleException if an error occurred
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void move(Long destinationFolderId, List<Long> ids) throws ConsoleException {
        List<PolicyDevelopmentEntity> policyDevelopmentEntities = new ArrayList<>();
        for (Long id : ids) {
            List<PolicyLite> policyLites = new ArrayList<>();
            findSubPolicies(policyLites, id);
            for (PolicyLite policyLite : policyLites) {
                PolicyDevelopmentEntity policyDevelopmentEntity = devEntityMgmtService.findById(policyLite.getId());
                accessControlService.authorizeByTags(ActionType.MOVE, DelegationModelShortName.POLICY_ACCESS_TAGS,
                        policyDevelopmentEntity, false);
                policyDevelopmentEntity.setFolderId(destinationFolderId);
                accessControlService.authorizeByTags(ActionType.INSERT, DelegationModelShortName.POLICY_ACCESS_TAGS,
                        policyDevelopmentEntity, false);
                policyDevelopmentEntities.add(policyDevelopmentEntity);
            }
        }
        for (PolicyDevelopmentEntity policyDevelopmentEntity : policyDevelopmentEntities) {
            PolicyDTO policyDTO = findById(policyDevelopmentEntity.getId());
            String policyJsonBefore = policyDTO == null ? "" : policyDTO.toAuditString();
            devEntityMgmtService.save(policyDevelopmentEntity);
            if (policyDTO != null) {
                if (destinationFolderId != null) {
                    folderRepository.findById(destinationFolderId)
                            .ifPresent(folder -> policyDTO.setFolderPath(folder.getFolderPath()));
                }
                entityAuditLogDao.addEntityAuditLog(AuditAction.MOVE,
                        AuditableEntity.POLICY.getCode(),
                        policyDTO.getId(), policyJsonBefore,
                        policyDTO.toAuditString());
            }
            policySearchService.reIndexPolicies(findById(policyDevelopmentEntity.getId()));
        }
    }

    private void findSubPolicies(List<PolicyLite> policyLites, long policyId) {
        policySearchRepository.findById(policyId).ifPresent(policyLite -> {
            policyLites.add(policyLite);
            for (SubPolicyLite subPolicyLite : policyLite.getSubPolicies()) {
                findSubPolicies(policyLites, subPolicyLite.getId());
            }
        });
    }

    private String getXPathValue(String xml, String xPathString) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        InputStream contentStream = new ByteArrayInputStream(xml.getBytes());
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newDefaultInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(contentStream);
        XPath xPath = XPathFactory.newInstance().newXPath();
        String value = (String) xPath.compile(xPathString).evaluate(xmlDocument, XPathConstants.STRING);
        return StringUtils.isNotBlank(value)? value: "";
    }

    @Override
    public boolean isSubPolicyNotApproved(Long parentPolicyId) throws ConsoleException {
        List<PolicyLite> subPolicies = policySearchService.findSubPolicy(parentPolicyId);
        for (PolicyLite policyLite: subPolicies) {
            String workflowStatus = policyLite.getActiveWorkflowRequestLevelStatus();
            if (workflowStatus != null && !workflowStatus.equals(WorkflowRequestLevelStatus.APPROVED.name())) {
                return true;
            }
            if (isSubPolicyNotApproved(policyLite.getId())) {
                return true;
            }
        }
        return false;
    }

    @Autowired
    public void setPolicyWorkflowProperties(PolicyWorkflowProperties policyWorkflowProperties) {
        this.policyWorkflowProperties = policyWorkflowProperties;
    }
}
