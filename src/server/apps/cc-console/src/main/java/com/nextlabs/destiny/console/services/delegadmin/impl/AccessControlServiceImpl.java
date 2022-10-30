/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on May 3, 2016
 *
 */
package com.nextlabs.destiny.console.services.delegadmin.impl;

import static com.bluejungle.pf.engine.destiny.EvaluationResult.ALLOW;
import static com.nextlabs.destiny.console.enums.ObligationTagType.DELETE_TAG_FILTERS;
import static com.nextlabs.destiny.console.enums.ObligationTagType.DEPLOY_TAG_FILTERS;
import static com.nextlabs.destiny.console.enums.ObligationTagType.EDIT_TAG_FILTERS;
import static com.nextlabs.destiny.console.enums.ObligationTagType.INSERT_TAG_FILTERS;
import static com.nextlabs.destiny.console.enums.ObligationTagType.MOVE_TAG_FILTERS;
import static com.nextlabs.destiny.console.enums.ObligationTagType.VIEW_TAG_FILTERS;
import static com.nextlabs.destiny.console.enums.Operator.IN;
import static com.nextlabs.destiny.console.enums.Operator.NOT;
import static com.nextlabs.destiny.console.utils.SecurityContextUtil.getCurrentUser;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.bluejungle.framework.expressions.Multivalue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.bluejungle.framework.expressions.EvalValue;
import com.bluejungle.framework.utils.DynamicAttributes;
import com.bluejungle.pf.domain.destiny.action.DAction;
import com.bluejungle.pf.domain.destiny.misc.EffectType;
import com.bluejungle.pf.domain.destiny.obligation.CustomObligation;
import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.bluejungle.pf.domain.destiny.subject.SubjectType;
import com.bluejungle.pf.domain.epicenter.misc.IObligation;
import com.bluejungle.pf.engine.destiny.EvaluationEngine;
import com.bluejungle.pf.engine.destiny.EvaluationRequest;
import com.bluejungle.pf.engine.destiny.EvaluationResult;
import com.bluejungle.pf.engine.destiny.PolicyEvaluationException;
import com.nextlabs.destiny.console.delegadmin.actions.DelegationModelActions;
import com.nextlabs.destiny.console.delegadmin.helpers.AppUserSubject;
import com.nextlabs.destiny.console.delegadmin.helpers.DelegationRuleReferenceResolver;
import com.nextlabs.destiny.console.delegadmin.helpers.DelegationTargetResolver;
import com.nextlabs.destiny.console.dto.Authorizable;
import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.dto.delegadmin.ObligationTagsFilter;
import com.nextlabs.destiny.console.dto.delegadmin.TagsFilter;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentLite;
import com.nextlabs.destiny.console.dto.policymgmt.FolderDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyModelDTO;
import com.nextlabs.destiny.console.enums.ActionType;
import com.nextlabs.destiny.console.enums.AuthorizableType;
import com.nextlabs.destiny.console.enums.DelegationModelShortName;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.enums.Operator;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.enums.TagType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.ForbiddenException;
import com.nextlabs.destiny.console.model.AppUserProperties;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.Tag;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.model.delegadmin.AccessibleTags;
import com.nextlabs.destiny.console.model.delegadmin.ApplicableTag;
import com.nextlabs.destiny.console.model.delegadmin.DelegateModel;
import com.nextlabs.destiny.console.model.delegadmin.ObligationTag;
import com.nextlabs.destiny.console.model.policy.ActionConfig;
import com.nextlabs.destiny.console.model.policy.Folder;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.search.repositories.DelegateModelSearchRepository;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.TagLabelService;
import com.nextlabs.destiny.console.services.delegadmin.AccessControlService;
import com.nextlabs.destiny.console.services.policy.FolderService;
import com.nextlabs.destiny.console.services.policy.PolicyDevelopmentEntityMgmtService;
import com.nextlabs.destiny.console.utils.SecurityContextUtil;

/**
 * Implementation for {@link AccessControlService}
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class AccessControlServiceImpl implements AccessControlService {

    private static final Logger log = LoggerFactory
            .getLogger(AccessControlServiceImpl.class);

    @Autowired
    private PolicyDevelopmentEntityMgmtService devEntityMgmtService;

    @Autowired
    private TagLabelService tagLabelService;

    @Autowired
    private FolderService folderService;

    @Resource
    private DelegateModelSearchRepository delegateModelSearchRepository;

    @Resource
    private ApplicationUserSearchRepository appUserSearchRepository;
    
    @Autowired
    private MessageBundleService msgBundle;

    @Override
    public ApplicationUser populateAllowedActionsAndTags(ApplicationUser user)
            throws ConsoleException {
        log.debug(
                "Start evaluating delegation rules to get access controls for the user, [User :{}]",
                user.getUsername());

        Set<String> allowedActions = new TreeSet<>();
        Map<Long, Collection<IObligation>> allowedObligations = new HashMap<>();
        Set<String> allActions = loadAllActions();
        List<IDPolicy> parsedRules = resolveRules();
        
        // grant all the permission to super user
        if (user.isSuperUser()) {
            allowedActions.addAll(allActions);
        } else {

            for (IDPolicy parsedRule : parsedRules) {
                EvaluationEngine engine = null;
                DelegationTargetResolver resolver = new DelegationTargetResolver(
                        parsedRule);
                // Subject with attributes
                AppUserSubject subject = getSubjectWithAttributes(user);

                for (String actionName : allActions) {
                    EvaluationRequest evalRequest = new EvaluationRequest();
                    evalRequest.setRequestId(System.nanoTime());
                    evalRequest.setAction(DAction.getAction(actionName));
                    evalRequest.setUser(subject);
                    // NOTE: Applicable for all the modules
                    // evalRequest.setFromResource(arg0);

                    engine = new EvaluationEngine(resolver);
                    evaluateRule(allowedActions, allowedObligations, resolver,
                            engine, actionName, evalRequest);
                }
            }
        }
        populateAccessibleTagsFromObligations(user, allowedObligations);
        user.setAllowedActions(allowedActions);
        return user;
    }

    @Override
    public PolicyLite enforceTBAConPolicy(PolicyLite policy) {
        List<PolicyLite> policyLites = new ArrayList<>();
        policyLites.add(policy);
        policyLites = enforceTBAConPolicies(policyLites);
        return policyLites.get(0);
    }

    @Override
    public List<PolicyLite> enforceTBAConPolicies(List<PolicyLite> policies) {
        log.debug("before enforce the Tag based access control on policies");
        long startTime = System.currentTimeMillis();
        ApplicationUser user = appUserSearchRepository.findById(getCurrentUser().getUserId()).orElse(null);
        if (user == null) {
            throw new AccessDeniedException("User not found");
        }
        AccessibleTags accessibleTags = user.getPolicyAccessibleTags();
        folderService.addIncludedSubFolderTags(accessibleTags);
        List<ObligationTag> obligationTags = (accessibleTags != null) ? accessibleTags.getTags() : new ArrayList<>();
        for (PolicyLite policyLite : policies) {
            List<TagDTO> assignedTags = policyLite.getTags();
            TagDTO folderTag = policyLite.getFolderId() > -1 ? new TagDTO(String.valueOf(policyLite.getFolderId()),
                    String.valueOf(policyLite.getFolderId()),
                    TagType.FOLDER_TAG.name()) : null;
            List<Boolean> hasViewAccessList = new ArrayList<>();
            List<Boolean> hasEditAccessList = new ArrayList<>();
            List<Boolean> hasDeleteAccessList = new ArrayList<>();
            List<Boolean> hasDeployAccessList = new ArrayList<>();
            List<Boolean> hasMoveAccessList = new ArrayList<>();

            List<Boolean> hasViewAccessByFoldersList = null;
            List<Boolean> hasEditAccessByFoldersList = null;
            List<Boolean> hasDeleteAccessByFoldersList = null;
            List<Boolean> hasDeployAccessByFoldersList = null;
            List<Boolean> hasMoveAccessByFoldersList = null;
            List<Boolean> hasInsertAccessByFoldersList = null;

            if (folderTag != null) {
                hasViewAccessByFoldersList = new ArrayList<>();
                hasEditAccessByFoldersList = new ArrayList<>();
                hasDeleteAccessByFoldersList = new ArrayList<>();
                hasDeployAccessByFoldersList = new ArrayList<>();
                hasMoveAccessByFoldersList = new ArrayList<>();
                hasInsertAccessByFoldersList = new ArrayList<>();
            }

            if (user.isSuperUser()) {
                policyLite.getAuthorities().add(new SimpleGrantedAuthority("VIEW_POLICY"));
                policyLite.getAuthorities().add(new SimpleGrantedAuthority("EDIT_POLICY"));
                policyLite.getAuthorities().add(new SimpleGrantedAuthority("DELETE_POLICY"));
                policyLite.getAuthorities().add(new SimpleGrantedAuthority("DEPLOY_POLICY"));
                policyLite.getAuthorities().add(new SimpleGrantedAuthority(DelegationModelActions.MOVE_POLICY));
                policyLite.getAuthorities().add(new SimpleGrantedAuthority(DelegationModelActions.CREATE_POLICY));
                continue;
            }

            for (ObligationTag obligationTag : obligationTags) {
                // View
                enforceTagBaseAccess(assignedTags, hasViewAccessList, obligationTag.getViewTags());

                // Edit
                enforceTagBaseAccess(assignedTags, hasEditAccessList, obligationTag.getEditTags());

                // delete
                enforceTagBaseAccess(assignedTags, hasDeleteAccessList, obligationTag.getDeleteTags());

                // deploy
                enforceTagBaseAccess(assignedTags, hasDeployAccessList, obligationTag.getDeployTags());

                // move
                enforceTagBaseAccess(assignedTags, hasMoveAccessList, obligationTag.getMoveTags());

                // create
                enforceTagBaseAccess(assignedTags, hasMoveAccessList, obligationTag.getInsertTags());

                if (folderTag != null) {
                    enforceFolderTagBasedAccess(folderTag, hasViewAccessByFoldersList, obligationTag.getViewTags());
                    enforceFolderTagBasedAccess(folderTag, hasEditAccessByFoldersList, obligationTag.getEditTags());
                    enforceFolderTagBasedAccess(folderTag, hasDeleteAccessByFoldersList, obligationTag.getDeleteTags());
                    enforceFolderTagBasedAccess(folderTag, hasDeployAccessByFoldersList, obligationTag.getDeployTags());
                    enforceFolderTagBasedAccess(folderTag, hasMoveAccessByFoldersList, obligationTag.getMoveTags());
                    enforceFolderTagBasedAccess(folderTag, hasInsertAccessByFoldersList, obligationTag.getInsertTags());
                }
            }

            GrantedAuthority viewAuthority = applyAuthority(hasViewAccessList, hasViewAccessByFoldersList, "VIEW_POLICY");
            if (viewAuthority != null) {
                policyLite.getAuthorities().add(viewAuthority);
            }
            GrantedAuthority editAuthority = applyAuthority(hasEditAccessList, hasEditAccessByFoldersList, "EDIT_POLICY");
            if (editAuthority != null) {
                policyLite.getAuthorities().add(editAuthority);
            }
            GrantedAuthority deleteAuthority = applyAuthority(hasDeleteAccessList, hasDeleteAccessByFoldersList, "DELETE_POLICY");
            if (deleteAuthority != null) {
                policyLite.getAuthorities().add(deleteAuthority);
            }
            GrantedAuthority deployAuthority = applyAuthority(hasDeployAccessList, hasDeployAccessByFoldersList, "DEPLOY_POLICY");
            if (deployAuthority != null) {
                policyLite.getAuthorities().add(deployAuthority);
            }
            GrantedAuthority moveAuthority = applyAuthority(hasMoveAccessList, hasMoveAccessByFoldersList, DelegationModelActions.MOVE_POLICY);
            if (moveAuthority != null) {
                policyLite.getAuthorities().add(moveAuthority);
            }
            GrantedAuthority insertAuthority = applyAuthority(hasMoveAccessList, hasMoveAccessByFoldersList, DelegationModelActions.CREATE_POLICY);
            if (insertAuthority != null) {
                policyLite.getAuthorities().add(insertAuthority);
            }
        }
        long endTime = System.currentTimeMillis();
        log.debug("Enforced the Tag based access control on given policies. [ No of records: {}, Time taken: {}ms]",
                policies.size(), (endTime - startTime));
        return policies;
    }

    @Override
    public ComponentLite enforceTBAConComponent(ComponentLite component) {
        List<ComponentLite> componentLites = new ArrayList<>();
        componentLites.add(component);
        componentLites = enforceTBAConComponents(componentLites);
        return componentLites.get(0);
    }

    @Override
    public List<ComponentLite> enforceTBAConComponents(
            List<ComponentLite> components) {
        log.debug("before enforce the Tag based access control on components");
        long startTime = System.currentTimeMillis();
        ApplicationUser user = appUserSearchRepository.findById(getCurrentUser().getUserId()).orElse(null);
        if (user == null) {
            throw new AccessDeniedException("User not found");
        }
        AccessibleTags accessibleTags = user.getComponentAccessibleTags();
        folderService.addIncludedSubFolderTags(accessibleTags);
        List<ObligationTag> obligationTags = (accessibleTags != null) ? accessibleTags.getTags() : new ArrayList<>();
        for (ComponentLite componentLite : components) {
            List<TagDTO> assignedTags = componentLite.getTags();
            TagDTO folderTag = componentLite.getFolderId() > -1 ? new TagDTO(String.valueOf(componentLite.getFolderId()),
                    String.valueOf(componentLite.getFolderId()),
                    TagType.FOLDER_TAG.name()) : null;
            List<Boolean> hasViewAccessList = new ArrayList<>();
            List<Boolean> hasEditAccessList = new ArrayList<>();
            List<Boolean> hasDeleteAccessList = new ArrayList<>();
            List<Boolean> hasDeployAccessList = new ArrayList<>();
            List<Boolean> hasMoveAccessList = new ArrayList<>();

            List<Boolean> hasViewAccessByFoldersList = null;
            List<Boolean> hasEditAccessByFoldersList = null;
            List<Boolean> hasDeleteAccessByFoldersList = null;
            List<Boolean> hasDeployAccessByFoldersList = null;
            List<Boolean> hasMoveAccessByFoldersList = null;
            List<Boolean> hasInsertAccessByFoldersList = null;

            if (folderTag != null) {
                hasViewAccessByFoldersList = new ArrayList<>();
                hasEditAccessByFoldersList = new ArrayList<>();
                hasDeleteAccessByFoldersList = new ArrayList<>();
                hasDeployAccessByFoldersList = new ArrayList<>();
                hasMoveAccessByFoldersList = new ArrayList<>();
                hasInsertAccessByFoldersList = new ArrayList<>();
            }

            if (user.isSuperUser()) {
                componentLite.getAuthorities().add(new SimpleGrantedAuthority("VIEW_COMPONENT"));
                componentLite.getAuthorities().add(new SimpleGrantedAuthority("EDIT_COMPONENT"));
                componentLite.getAuthorities().add(new SimpleGrantedAuthority("DELETE_COMPONENT"));
                componentLite.getAuthorities().add(new SimpleGrantedAuthority("DEPLOY_COMPONENT"));
                componentLite.getAuthorities().add(new SimpleGrantedAuthority(DelegationModelActions.MOVE_COMPONENT));
                componentLite.getAuthorities().add(new SimpleGrantedAuthority(DelegationModelActions.CREATE_COMPONENT));
                continue;
            }

            for (ObligationTag obligationTag : obligationTags) {
                // View
                enforceTagBaseAccess(assignedTags, hasViewAccessList, obligationTag.getViewTags());

                // Edit
                enforceTagBaseAccess(assignedTags, hasEditAccessList, obligationTag.getEditTags());

                // delete
                enforceTagBaseAccess(assignedTags, hasDeleteAccessList, obligationTag.getDeleteTags());

                // deploy
                enforceTagBaseAccess(assignedTags, hasDeployAccessList, obligationTag.getDeployTags());

                // move
                enforceTagBaseAccess(assignedTags, hasMoveAccessList, obligationTag.getMoveTags());

                // create
                enforceTagBaseAccess(assignedTags, hasMoveAccessList, obligationTag.getInsertTags());

                if (folderTag != null) {
                    enforceFolderTagBasedAccess(folderTag, hasViewAccessByFoldersList, obligationTag.getViewTags());
                    enforceFolderTagBasedAccess(folderTag, hasEditAccessByFoldersList, obligationTag.getEditTags());
                    enforceFolderTagBasedAccess(folderTag, hasDeleteAccessByFoldersList, obligationTag.getDeleteTags());
                    enforceFolderTagBasedAccess(folderTag, hasDeployAccessByFoldersList, obligationTag.getDeployTags());
                    enforceFolderTagBasedAccess(folderTag, hasMoveAccessByFoldersList, obligationTag.getMoveTags());
                    enforceFolderTagBasedAccess(folderTag, hasInsertAccessByFoldersList, obligationTag.getInsertTags());
                }
            }

            GrantedAuthority viewAuthority = applyAuthority(hasViewAccessList, hasViewAccessByFoldersList, "VIEW_COMPONENT");
            if (viewAuthority != null) {
                componentLite.getAuthorities().add(viewAuthority);
            }
            GrantedAuthority editAuthority = applyAuthority(hasEditAccessList, hasEditAccessByFoldersList, "EDIT_COMPONENT");
            if (editAuthority != null) {
                componentLite.getAuthorities().add(editAuthority);
            }
            GrantedAuthority deleteAuthority = applyAuthority(hasDeleteAccessList, hasDeleteAccessByFoldersList, "DELETE_COMPONENT");
            if (deleteAuthority != null) {
                componentLite.getAuthorities().add(deleteAuthority);
            }
            GrantedAuthority deployAuthority = applyAuthority(hasDeployAccessList, hasDeployAccessByFoldersList, "DEPLOY_COMPONENT");
            if (deployAuthority != null) {
                componentLite.getAuthorities().add(deployAuthority);
            }
            GrantedAuthority moveAuthority = applyAuthority(hasMoveAccessList, hasMoveAccessByFoldersList, DelegationModelActions.MOVE_COMPONENT);
            if (moveAuthority != null) {
                componentLite.getAuthorities().add(moveAuthority);
            }
            GrantedAuthority insertAuthority = applyAuthority(hasMoveAccessList, hasMoveAccessByFoldersList, DelegationModelActions.CREATE_COMPONENT);
            if (insertAuthority != null) {
                componentLite.getAuthorities().add(insertAuthority);
            }
        }
        long endTime = System.currentTimeMillis();
        log.debug("Enforced the Tag based access control on given components. [ No of records: {}, Time taken: {}ms]",
                components.size(), (endTime - startTime));
        return components;
    }

    @Override
    public PolicyModelDTO enforceTBAConPolicyModel(PolicyModelDTO policyModel) {
        List<PolicyModelDTO> policyModels = new ArrayList<>();
        policyModels.add(policyModel);
        policyModels = enforceTBAConPolicyModels(policyModels);
        return policyModels.get(0);
    }

    @Override
    public List<PolicyModelDTO> enforceTBAConPolicyModels(
            List<PolicyModelDTO> policyModels) {
        log.debug("before enforce the Tag based access control on policy models");
        long startTime = System.currentTimeMillis();
        ApplicationUser user = appUserSearchRepository.findById(getCurrentUser().getUserId()).orElse(null);
        if (user == null) {
            throw new AccessDeniedException("User not found");
        }
        AccessibleTags accessibleTags = user.getPolicyModelAccessibleTags();
        List<ObligationTag> obligationTags = (accessibleTags != null) ? accessibleTags.getTags() : new ArrayList<>();
        for (PolicyModelDTO policyModel : policyModels) {
            List<TagDTO> assignedTags = new ArrayList<>(policyModel.getTags());
            List<Boolean> hasViewAccessList = new ArrayList<>();
            List<Boolean> hasEditAccessList = new ArrayList<>();
            List<Boolean> hasDeleteAccessList = new ArrayList<>();
            List<Boolean> hasDeployAccessList = new ArrayList<>();

            if (user.isSuperUser()) {
                policyModel.getAuthorities().add(new SimpleGrantedAuthority("VIEW_POLICY_MODEL"));
                policyModel.getAuthorities().add(new SimpleGrantedAuthority("EDIT_POLICY_MODEL"));
                policyModel.getAuthorities().add(new SimpleGrantedAuthority("DELETE_POLICY_MODEL"));
                policyModel.getAuthorities().add(new SimpleGrantedAuthority("DEPLOY_POLICY_MODEL"));
                continue;
            }

            for (ObligationTag obligationTag : obligationTags) {
                // View
                enforceTagBaseAccess(assignedTags, hasViewAccessList, obligationTag.getViewTags());

                // Edit
                enforceTagBaseAccess(assignedTags, hasEditAccessList, obligationTag.getEditTags());

                // delete
                enforceTagBaseAccess(assignedTags, hasDeleteAccessList, obligationTag.getDeleteTags());

                // deploy
                enforceTagBaseAccess(assignedTags, hasDeployAccessList, obligationTag.getDeployTags());
            }

            GrantedAuthority viewAuthority = applyAuthority(hasViewAccessList, null,
                    "VIEW_POLICY_MODEL");
            if (viewAuthority != null) {
                policyModel.getAuthorities().add(viewAuthority);
            }
            GrantedAuthority editAuthority = applyAuthority(hasEditAccessList, null,
                    "EDIT_POLICY_MODEL");
            if (editAuthority != null) {
                policyModel.getAuthorities().add(editAuthority);
            }
            GrantedAuthority deleteAuthority = applyAuthority(hasDeleteAccessList, null,
                    "DELETE_POLICY_MODEL");
            if (deleteAuthority != null) {
                policyModel.getAuthorities().add(deleteAuthority);
            }
            GrantedAuthority deployAuthority = applyAuthority(hasDeployAccessList, null,
                    "DEPLOY_POLICY_MODEL");
            if (deployAuthority != null) {
                policyModel.getAuthorities().add(deployAuthority);
            }
        }
        long endTime = System.currentTimeMillis();
        log.debug(
                "Enforced the Tag based access control on given policy models. [ No of records: {}, Time taken: {}ms]",
                policyModels.size(), (endTime - startTime));
        return policyModels;
    }

    @Override
    public List<FolderDTO> enforceTBAConPolicyFolder(List<FolderDTO> folderDTOS) {
        long startTime = System.currentTimeMillis();
        ApplicationUser user = appUserSearchRepository.findById(getCurrentUser().getUserId()).orElse(null);
        if (user == null) {
            throw new AccessDeniedException("User not found");
        }
        AccessibleTags accessibleTags = user.getPolicyFolderAccessibleTags();
        folderService.addIncludedSubFolderTags(accessibleTags);
        List<ObligationTag> obligationTags = (accessibleTags != null) ? accessibleTags.getTags() : new ArrayList<>();
        AccessibleTags accessibleTagsForPolicies = user.getPolicyAccessibleTags();
        folderService.addIncludedSubFolderTags(accessibleTagsForPolicies);
        List<ObligationTag> obligationTagsForPolicies = (accessibleTagsForPolicies != null) ? accessibleTagsForPolicies.getTags() : new ArrayList<>();
        for (FolderDTO folderDTO : folderDTOS) {
            TagDTO folderTag = new TagDTO(String.valueOf(folderDTO.getId()), String.valueOf(folderDTO.getId()),
                    TagType.FOLDER_TAG.name());

            List<Boolean> hasViewAccessByFoldersList = new ArrayList<>();
            List<Boolean> hasInsertAccessByFoldersList = new ArrayList<>();
            List<Boolean> hasRenameAccessByFoldersList = new ArrayList<>();
            List<Boolean> hasMoveAccessByFoldersList = new ArrayList<>();
            List<Boolean> hasDeleteAccessByFoldersList = new ArrayList<>();
            List<Boolean> hasPolicyInsertAccessByFoldersList = new ArrayList<>();

            if (user.isSuperUser()) {
                folderDTO.getAuthorities().add(new SimpleGrantedAuthority(DelegationModelActions.VIEW_POLICY_FOLDER));
                folderDTO.getAuthorities().add(new SimpleGrantedAuthority(DelegationModelActions.CREATE_POLICY_FOLDER));
                folderDTO.getAuthorities().add(new SimpleGrantedAuthority(DelegationModelActions.RENAME_POLICY_FOLDER));
                folderDTO.getAuthorities().add(new SimpleGrantedAuthority(DelegationModelActions.MOVE_POLICY_FOLDER));
                folderDTO.getAuthorities().add(new SimpleGrantedAuthority(DelegationModelActions.DELETE_POLICY_FOLDER));
                folderDTO.getAuthorities().add(new SimpleGrantedAuthority(DelegationModelActions.CREATE_POLICY));
                continue;
            }

            for (ObligationTag obligationTag : obligationTags) {
                enforceFolderTagBasedAccess(folderTag, hasViewAccessByFoldersList, obligationTag.getViewTags());
                enforceFolderTagBasedAccess(folderTag, hasInsertAccessByFoldersList, obligationTag.getInsertTags());
                enforceFolderTagBasedAccess(folderTag, hasRenameAccessByFoldersList, obligationTag.getEditTags());
                enforceFolderTagBasedAccess(folderTag, hasMoveAccessByFoldersList, obligationTag.getMoveTags());
                enforceFolderTagBasedAccess(folderTag, hasDeleteAccessByFoldersList, obligationTag.getDeleteTags());
            }

            for (ObligationTag obligationTag : obligationTagsForPolicies) {
                enforceFolderTagBasedAccess(folderTag, hasPolicyInsertAccessByFoldersList, obligationTag.getInsertTags());
            }

            GrantedAuthority viewAuthority = applyAuthority(null, hasViewAccessByFoldersList, DelegationModelActions.VIEW_POLICY_FOLDER);
            if (viewAuthority != null) {
                folderDTO.getAuthorities().add(viewAuthority);
            }
            GrantedAuthority insertAuthority = applyAuthority(null, hasInsertAccessByFoldersList, DelegationModelActions.CREATE_POLICY_FOLDER);
            if (insertAuthority != null && user.getAllowedActions().contains(DelegationModelActions.CREATE_POLICY_FOLDER)) {
                folderDTO.getAuthorities().add(insertAuthority);
            }
            GrantedAuthority renameAuthority = applyAuthority(null, hasRenameAccessByFoldersList, DelegationModelActions.RENAME_POLICY_FOLDER);
            if (renameAuthority != null) {
                folderDTO.getAuthorities().add(renameAuthority);
            }
            GrantedAuthority moveAuthority = applyAuthority(null, hasMoveAccessByFoldersList, DelegationModelActions.MOVE_POLICY_FOLDER);
            if (moveAuthority != null) {
                folderDTO.getAuthorities().add(moveAuthority);
            }
            GrantedAuthority deleteAuthority = applyAuthority(null, hasDeleteAccessByFoldersList, DelegationModelActions.DELETE_POLICY_FOLDER);
            if (deleteAuthority != null) {
                folderDTO.getAuthorities().add(deleteAuthority);
            }
            GrantedAuthority insertPolicyAuthority = applyAuthority(null, hasPolicyInsertAccessByFoldersList, DelegationModelActions.CREATE_POLICY);
            if (insertPolicyAuthority != null && user.getAllowedActions().contains(DelegationModelActions.CREATE_POLICY)) {
                folderDTO.getAuthorities().add(insertPolicyAuthority);
            }
        }
        long endTime = System.currentTimeMillis();
        log.debug("Enforced the folder based access control on given folders. [ No of records: {}, Time taken: {}ms]",
                folderDTOS.size(), (endTime - startTime));
        return folderDTOS;
    }

    @Override
    public List<FolderDTO> enforceTBAConComponentFolder(List<FolderDTO> folderDTOS) {
        long startTime = System.currentTimeMillis();
        ApplicationUser user = appUserSearchRepository.findById(getCurrentUser().getUserId()).orElse(null);
        if (user == null) {
            throw new AccessDeniedException("User not found");
        }
        AccessibleTags accessibleTags = user.getComponentFolderAccessibleTags();
        folderService.addIncludedSubFolderTags(accessibleTags);
        List<ObligationTag> obligationTags = (accessibleTags != null) ? accessibleTags.getTags() : new ArrayList<>();
        AccessibleTags accessibleTagsForComponents = user.getComponentAccessibleTags();
        folderService.addIncludedSubFolderTags(accessibleTagsForComponents);
        List<ObligationTag> obligationTagsForComponents = (accessibleTagsForComponents != null) ? accessibleTagsForComponents.getTags() : new ArrayList<>();

        for (FolderDTO folderDTO : folderDTOS) {
            TagDTO folderTag = new TagDTO(String.valueOf(folderDTO.getId()), String.valueOf(folderDTO.getId()),
                    TagType.FOLDER_TAG.name());

            List<Boolean> hasViewAccessByFoldersList = new ArrayList<>();
            List<Boolean> hasInsertAccessByFoldersList = new ArrayList<>();
            List<Boolean> hasRenameAccessByFoldersList = new ArrayList<>();
            List<Boolean> hasMoveAccessByFoldersList = new ArrayList<>();
            List<Boolean> hasDeleteAccessByFoldersList = new ArrayList<>();
            List<Boolean> hasComponentInsertAccessByFoldersList = new ArrayList<>();

            if (user.isSuperUser()) {
                folderDTO.getAuthorities().add(new SimpleGrantedAuthority(DelegationModelActions.VIEW_COMPONENT_FOLDER));
                folderDTO.getAuthorities().add(new SimpleGrantedAuthority(DelegationModelActions.CREATE_COMPONENT_FOLDER));
                folderDTO.getAuthorities().add(new SimpleGrantedAuthority(DelegationModelActions.RENAME_COMPONENT_FOLDER));
                folderDTO.getAuthorities().add(new SimpleGrantedAuthority(DelegationModelActions.MOVE_COMPONENT_FOLDER));
                folderDTO.getAuthorities().add(new SimpleGrantedAuthority(DelegationModelActions.DELETE_COMPONENT_FOLDER));
                folderDTO.getAuthorities().add(new SimpleGrantedAuthority(DelegationModelActions.CREATE_COMPONENT));
                continue;
            }

            for (ObligationTag obligationTag : obligationTags) {
                enforceFolderTagBasedAccess(folderTag, hasViewAccessByFoldersList, obligationTag.getViewTags());
                enforceFolderTagBasedAccess(folderTag, hasInsertAccessByFoldersList, obligationTag.getInsertTags());
                enforceFolderTagBasedAccess(folderTag, hasRenameAccessByFoldersList, obligationTag.getEditTags());
                enforceFolderTagBasedAccess(folderTag, hasMoveAccessByFoldersList, obligationTag.getMoveTags());
                enforceFolderTagBasedAccess(folderTag, hasDeleteAccessByFoldersList, obligationTag.getDeleteTags());
            }

            for (ObligationTag obligationTag : obligationTagsForComponents) {
                enforceFolderTagBasedAccess(folderTag, hasComponentInsertAccessByFoldersList, obligationTag.getInsertTags());
            }

            GrantedAuthority viewAuthority = applyAuthority(null, hasViewAccessByFoldersList, DelegationModelActions.VIEW_COMPONENT_FOLDER);
            if (viewAuthority != null) {
                folderDTO.getAuthorities().add(viewAuthority);
            }
            GrantedAuthority createAuthority = applyAuthority(null, hasInsertAccessByFoldersList, DelegationModelActions.CREATE_COMPONENT_FOLDER);
            if (createAuthority != null) {
                folderDTO.getAuthorities().add(createAuthority);
            }
            GrantedAuthority renameAuthority = applyAuthority(null, hasRenameAccessByFoldersList, DelegationModelActions.RENAME_COMPONENT_FOLDER);
            if (renameAuthority != null) {
                folderDTO.getAuthorities().add(renameAuthority);
            }
            GrantedAuthority moveAuthority = applyAuthority(null, hasMoveAccessByFoldersList, DelegationModelActions.MOVE_COMPONENT_FOLDER);
            if (moveAuthority != null) {
                folderDTO.getAuthorities().add(moveAuthority);
            }
            GrantedAuthority deleteAuthority = applyAuthority(null, hasDeleteAccessByFoldersList, DelegationModelActions.DELETE_COMPONENT_FOLDER);
            if (deleteAuthority != null) {
                folderDTO.getAuthorities().add(deleteAuthority);
            }
            GrantedAuthority insertPolicyAuthority = applyAuthority(null, hasComponentInsertAccessByFoldersList, DelegationModelActions.CREATE_COMPONENT);
            if (insertPolicyAuthority != null && user.getAllowedActions().contains(DelegationModelActions.CREATE_COMPONENT)) {
                folderDTO.getAuthorities().add(insertPolicyAuthority);
            }
        }
        long endTime = System.currentTimeMillis();
        log.debug("Enforced the folder based access control on given folders. [ No of records: {}, Time taken: {}ms]",
                folderDTOS.size(), (endTime - startTime));
        return folderDTOS;
    }

    private GrantedAuthority applyAuthority(List<Boolean> hasAccessList, List<Boolean> hasAccessByFoldersList,
                                            String grantAuthority) {
        return (hasAccessList == null || hasAccessList.contains(true))
                && (hasAccessByFoldersList == null || hasAccessByFoldersList.contains(true)) ?
                new SimpleGrantedAuthority(grantAuthority) : null;
    }

    private void enforceTagBaseAccess(List<TagDTO> assignedTags,
            List<Boolean> hasAccessList, List<ApplicableTag> applicableTags) {
        for (ApplicableTag applicableTag : applicableTags) {
            boolean canAccess = false;
            if (Operator.IN.equals(applicableTag.getOperator())) {
                for (TagDTO tag : applicableTag.getTags()) {
                    if (TagType.FOLDER_TAG.name().equals(tag.getType())) {
                        continue;
                    }
                    if (TagLabel.ALL_TAGS_KEY.equalsIgnoreCase(tag.getKey())) {
                        canAccess = true;
                        break;
                    }
                    for (TagDTO localTag : assignedTags) {
                        if (localTag.getKey().equalsIgnoreCase(tag.getKey())) {
                            canAccess = true;
                        }
                    }
                }
            } else if (Operator.NOT.equals(applicableTag.getOperator())) {
                canAccess = true;
                notTags: for (TagDTO tag : applicableTag.getTags()) {
                    if (TagType.FOLDER_TAG.name().equals(tag.getType())) {
                        continue;
                    }
                    if (TagLabel.ALL_TAGS_KEY.equalsIgnoreCase(tag.getKey())) {
                        canAccess = false;
                        break;
                    }
                    for (TagDTO localTag : assignedTags) {
                        if (localTag.getKey().equalsIgnoreCase(tag.getKey())) {
                            canAccess = false;
                            break notTags;
                        }
                    }
                }
            }
            hasAccessList.add(canAccess);
        }
    }

    private void enforceFolderTagBasedAccess(TagDTO folderTag, List<Boolean> hasAccessList,
                                             List<ApplicableTag> applicableTags) {
        for (ApplicableTag applicableTag : applicableTags) {
            boolean canAccess = false;
            if (Operator.IN.equals(applicableTag.getOperator())) {
                for (TagDTO tag : applicableTag.getTags()) {
                    if (!TagType.FOLDER_TAG.name().equals(tag.getType())) {
                        continue;
                    }
                    if (TagLabel.ALL_FOLDERS_KEY.equalsIgnoreCase(tag.getKey())) {
                        canAccess = true;
                        break;
                    }
                    if (folderTag.getKey().equalsIgnoreCase(tag.getKey())) {
                        canAccess = true;
                    }
                }
            } else if (Operator.NOT.equals(applicableTag.getOperator())) {
                canAccess = true;
                for (TagDTO tag : applicableTag.getTags()) {
                    if (!TagType.FOLDER_TAG.name().equals(tag.getType())) {
                        continue;
                    }
                    if (TagLabel.ALL_FOLDERS_KEY.equalsIgnoreCase(tag.getKey())) {
                        canAccess = false;
                        break;
                    }
                    if (folderTag.getKey().equalsIgnoreCase(tag.getKey())) {
                        canAccess = false;
                        break;
                    }
                }
            }
            hasAccessList.add(canAccess);
        }
    }

    private AppUserSubject getSubjectWithAttributes(ApplicationUser user) {
        AppUserSubject subject = new AppUserSubject(user.getUsername(),
                user.getUsername(), user.getDisplayName(), user.getId(),
                SubjectType.USER, new DynamicAttributes());
        // add fixed user attributes for evaluation
        subject.setAttribute(StringUtils.lowerCase(msgBundle.getText("attr.firstName.key")),
                EvalValue.build(user.getFirstName()));
        subject.setAttribute(StringUtils.lowerCase(msgBundle.getText("attr.lastName.key")),
                EvalValue.build(user.getLastName()));
        subject.setAttribute(StringUtils.lowerCase(msgBundle.getText("attr.username.key")),  
        		EvalValue.build(user.getUsername()));
        subject.setAttribute(StringUtils.lowerCase(msgBundle.getText("attr.email.key")), 
        		EvalValue.build(user.getEmail()));
        subject.setAttribute(msgBundle.getText("attr.displayName.key"),
                EvalValue.build(user.getDisplayName().trim()));
        for (AppUserProperties userProp : user.getProperties()) {
        	if(userProp.getKey().toLowerCase().equals(StringUtils.lowerCase(msgBundle.getText("attr.displayName.key")))) {
                continue;
            }

            subject.setAttribute(StringUtils.lowerCase(userProp.getKey()),
                EvalValue.build(userProp.getValue()));
        }
        for (Map.Entry<String, Set<String>> userProp : user.getMultiValueProperties().entrySet()) {
            subject.setAttribute(StringUtils.lowerCase(userProp.getKey()),
                EvalValue.build(Multivalue.create(userProp.getValue())));
        }

        return subject;
    }

    private void populateAccessibleTagsFromObligations(ApplicationUser user,
            Map<Long, Collection<IObligation>> allowedObligations)
            throws ConsoleException {
        for (Map.Entry<Long, Collection<IObligation>> entry : allowedObligations
                .entrySet()) {
            Collection<IObligation> obligations = entry.getValue();
            for (IObligation obligation : obligations) {
                CustomObligation oblig = ((CustomObligation) obligation);

                String name = oblig.getCustomObligationName();
                DelegationModelShortName modelName = DelegationModelShortName
                        .get(name);
                AccessibleTags accessibleTags = user.getAccessibleTagsMap().get(name);
                if (null == accessibleTags) {
                    accessibleTags = new AccessibleTags(modelName);
                }
                populateAccessibleTags(oblig, accessibleTags);
                log.debug("Add {} accessible tags to user", modelName);
                user.getAccessibleTagsMap().put(name, accessibleTags);
            }
        }
    }

    private void evaluateRule(Set<String> allowedActions,
            Map<Long, Collection<IObligation>> allowedObligations,
            DelegationTargetResolver resolver, EvaluationEngine engine,
            String actionName, EvaluationRequest evalRequest) {

        try {
            EvaluationResult evalResult = engine.evaluate(evalRequest);
            if (EvaluationResult.ALLOW.equals(evalResult.getEffectName())) {
                allowedActions.add(actionName);
            }

            log.debug("DA rule evaluation result [ action:{}, effect: {}]",
                      actionName, evalResult.getEffectName());
            if (ALLOW.equals(evalResult.getEffectName())) {
                BitSet applicables = resolver.getApplicables();
                for (int i = applicables.nextSetBit(0); i >= 0; i = applicables
                             .nextSetBit(i + 1)) {
                    IDPolicy policy = resolver.getPolicies()[i];
                    allowedObligations.put(policy.getId(),
                                           policy.getObligations(EffectType.ALLOW));
                }
            }
        } catch (PolicyEvaluationException e) {
            log.info("Policy evaluation resulted in exception " + e);
        }
    }

    @SuppressWarnings("rawtypes")
    private void populateAccessibleTags(CustomObligation oblig,
            AccessibleTags accessibleTags) throws ConsoleException {
        Iterator itr = oblig.getCustomObligationArgs().iterator();

        ObligationTag obligationTag = null;
        while (itr.hasNext()) {
            String paramName = (String) itr.next();
            String paramValue = (itr.hasNext()) ? (String) itr.next() : null;
            if (paramValue == null) {
                continue;
            }

            if (obligationTag == null) {
                obligationTag = new ObligationTag();
                accessibleTags.getTags().add(obligationTag);
            }

            if (paramName.equals(VIEW_TAG_FILTERS.name())) {
                populateApplicableTags(paramValue, obligationTag.getViewTags());
            } else if (paramName.equals(EDIT_TAG_FILTERS.name())) {
                populateApplicableTags(paramValue, obligationTag.getEditTags());
            } else if (paramName.equals(DELETE_TAG_FILTERS.name())) {
                populateApplicableTags(paramValue,
                        obligationTag.getDeleteTags());
            } else if (paramName.equals(DEPLOY_TAG_FILTERS.name())) {
                populateApplicableTags(paramValue,
                        obligationTag.getDeployTags());
            } else if (paramName.equals(MOVE_TAG_FILTERS.name())) {
                populateApplicableTags(paramValue,
                        obligationTag.getMoveTags());
            } else if (paramName.equals(INSERT_TAG_FILTERS.name())) {
                populateApplicableTags(paramValue,
                        obligationTag.getInsertTags());
            }
        }
    }

    private void populateApplicableTags(String paramValue,
            List<ApplicableTag> applicableTags) throws ConsoleException {
        ObligationTagsFilter filters = ObligationTagsFilter
                .parsePQLFriendly(paramValue);
        for (TagsFilter tagFilter : filters.getTagsFilters()) {
            Operator operator = tagFilter.getOperator();
            Set<TagDTO> tagLabels = tagFilter.getTags();
            ApplicableTag tag = new ApplicableTag();
            if (IN.equals(operator)) {
                tag.setOperator(Operator.IN);
            } else if (NOT.equals(operator)) {
                tag.setOperator(Operator.NOT);
            }
            tag.setTags(new ArrayList<>(tagLabels));
            applicableTags.add(tag);
        }
    }

    private List<IDPolicy> resolveRules() throws ConsoleException {
        List<PolicyDevelopmentEntity> policies = devEntityMgmtService
                .findActiveEntitiesByType(
                        DevEntityType.DELEGATION_POLICY.getKey());
        List<PolicyDevelopmentEntity> components = devEntityMgmtService
                .findActiveEntitiesByType(
                        DevEntityType.DELEGATION_COMPONENT.getKey());

        DelegationRuleReferenceResolver resolver = DelegationRuleReferenceResolver
                .create(policies, components);
        return resolver.resolve();
    }

    private Set<String> loadAllActions() {
        Set<String> allActions = new TreeSet<>();

        Page<DelegateModel> delegateModelPage = delegateModelSearchRepository
                .findByTypeAndStatus(PolicyModelType.DA_RESOURCE.name(),
                        Status.ACTIVE, PageRequest.of(0, 1000));

        // Identify the allowed actions for all the resources
        for (DelegateModel delegateModel : delegateModelPage.getContent()) {
            for (ActionConfig actionConfig : delegateModel.getActions()) {
                allActions.add(actionConfig.getShortName());
            }
        }
        return allActions;
    }

    public void authorizeByTags(ActionType actionType,
                                DelegationModelShortName delegationModelShortName,
                                Authorizable authorizable,
                                boolean tagValidation) throws ConsoleException {
        ApplicationUser user = appUserSearchRepository.findById(getCurrentUser().getUserId()).orElse(null);
        if (user == null) {
            throw new AccessDeniedException("User not found");
        }
        AccessibleTags accessibleTags = user.getAccessibleTagsMap().get(delegationModelShortName.name());
        if (accessibleTags == null) {
            return;
        }
        folderService.addIncludedSubFolderTags(accessibleTags);
        List<ObligationTag> obligationTags = accessibleTags.getTags();
        if (obligationTags.isEmpty()) {
            return;
        }
        List<TagDTO> tagDTOList = new ArrayList<>();
        for (Tag tag : authorizable.getTags()) {
            if (tagValidation) {
                TagLabel tagLabel = tagLabelService.findById(tag.getId());
                if (tagLabel != null) {
                    tagDTOList.add(TagDTO.getDTO(tagLabel));
                }
            } else if (tag instanceof TagLabel) {
                TagLabel tagLabel = (TagLabel) tag;
                tagDTOList.add(TagDTO.getDTO(tagLabel));
            } else if (tag instanceof TagDTO) {
                tagDTOList.add((TagDTO) tag);
            }
        }
        String folderId = authorizable.getFolderId() == null ? "-1" : authorizable.getFolderId().toString();
        TagDTO folderTag = new TagDTO(folderId, folderId, TagType.FOLDER_TAG.name());

        List<Boolean> hasAccessList = new ArrayList<>();
        List<Boolean> hasAccessByFolderList = new ArrayList<>();
        for (ObligationTag obligationTag : obligationTags) {
            List<ApplicableTag> applicableTags = obligationTag.getTagsByActionType(actionType);
            if (applicableTags.isEmpty()) {
                continue;
            }
            enforceTagBaseAccess(tagDTOList, hasAccessList, applicableTags);
            enforceFolderTagBasedAccess(folderTag, hasAccessByFolderList, applicableTags);
        }
        if(authorizable instanceof Folder || authorizable instanceof FolderDTO) {
            hasAccessList.add(true);
        }

		if (!(hasAccessList.isEmpty() && (hasAccessByFolderList.isEmpty()))
				&& !(hasAccessList.contains(true) && (hasAccessByFolderList.contains(true)
						|| delegationModelShortName.equals(DelegationModelShortName.POLICY_MODEL_ACCESS_TAGS)))) {
			 String errorCode = ForbiddenException.UNAUTHORIZED_REQUEST;
            if (tagValidation) {
                switch (delegationModelShortName) {
                    case POLICY_ACCESS_TAGS: {
                        errorCode = ForbiddenException.MISSING_POLICY_TAGS_IN_REQUEST;
                        break;
                    }
                    case COMPONENT_ACCESS_TAGS: {
                        errorCode = ForbiddenException.MISSING_COMPONENT_TAGS_IN_REQUEST;
                        break;
                    }
                    case POLICY_MODEL_ACCESS_TAGS: {
                        errorCode = ForbiddenException.MISSING_POLICY_MODEL_TAGS_IN_REQUEST;
                        break;
                    }
                }
            }
            throw new ForbiddenException(errorCode, delegationModelShortName.toString(), actionType,
                    authorizable.getAuthorizableType(),
                    authorizable.getType() == null ? null : authorizable.getType().toString(),
                    authorizable.getName());
        }
    }

    public void checkAuthority(String authority, ActionType actionType, AuthorizableType authorizableType) {
        Authentication authentication = SecurityContextUtil.getCurrentAuth();
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (!CollectionUtils.isEmpty(authorities) && !authorities.contains(new SimpleGrantedAuthority(authority))) {
                throw new ForbiddenException(ForbiddenException.UNAUTHORIZED_REQUEST, authority, actionType, authorizableType);
            }
        }
    }
}
