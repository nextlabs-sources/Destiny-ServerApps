/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 17, 2016
 *
 */
package com.nextlabs.destiny.console.services.impl;

import javax.annotation.PostConstruct;

import com.nextlabs.destiny.console.services.notification.NotificationSearchService;
import com.nextlabs.destiny.console.services.policymigration.RemoteEnvironmentSearchService;
import com.nextlabs.destiny.console.services.policyworkflow.WorkflowRequestCommentSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nextlabs.destiny.console.dto.delegadmin.DelegateRuleLite;
import com.nextlabs.destiny.console.dto.delegadmin.DelegationComponentLite;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentLite;
import com.nextlabs.destiny.console.dto.policymgmt.FolderLite;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.SavedSearch;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.model.delegadmin.DelegateModel;
import com.nextlabs.destiny.console.model.policy.PolicyModel;
import com.nextlabs.destiny.console.services.ApplicationUserService;
import com.nextlabs.destiny.console.services.DataIndexerService;
import com.nextlabs.destiny.console.services.SavedSearchService;
import com.nextlabs.destiny.console.services.TagLabelService;
import com.nextlabs.destiny.console.services.delegadmin.DelegateModelService;
import com.nextlabs.destiny.console.services.delegadmin.DelegationComponentSearchService;
import com.nextlabs.destiny.console.services.delegadmin.DelegationRuleMgmtService;
import com.nextlabs.destiny.console.services.policy.ComponentSearchService;
import com.nextlabs.destiny.console.services.policy.FolderService;
import com.nextlabs.destiny.console.services.policy.PolicyModelService;
import com.nextlabs.destiny.console.services.policy.PolicySearchService;
import com.nextlabs.destiny.console.services.policy.XacmlPolicySearchService;

/**
 * Data indexer Service implementation
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class DataIndexerServiceImpl implements DataIndexerService {

    private static final Logger log = LoggerFactory
            .getLogger(DataIndexerServiceImpl.class);

    @Autowired
    private TagLabelService tagLabelService;

    @Autowired
    private PolicyModelService policyModelService;

    @Autowired
    private ComponentSearchService componentSearchService;

    @Autowired
    private PolicySearchService policySearchService;

    @Autowired
    private XacmlPolicySearchService xacmlPolicySearchService;

    @Autowired
    private SavedSearchService savedSearchService;

    @Autowired
    private ApplicationUserService applicationUserService;

    @Autowired
    private DelegateModelService delegateModelService;

    @Autowired
    private DelegationComponentSearchService delegationComponentSearchService;

    @Autowired
    private DelegationRuleMgmtService delegationRuleMgmtService;

    @Autowired
    private RemoteEnvironmentSearchService remoteEnvironmentSearchService;

    @Autowired
    private FolderService folderService;

    @Autowired
    private ElasticsearchRestTemplate esTemplate;

    private WorkflowRequestCommentSearchService workflowRequestCommentSearchService;
    private NotificationSearchService notificationSearchService;

    @Async
    @PostConstruct
    @Transactional
    public void initIndexes() {
        try {
            createAllIndexes();
            indexData();
        } catch (ConsoleException e) {
            // handle exception, otherwise console failed to start
            log.error("indexing problem while console starting", e);
        }
    }

    @Override
    public void createAllIndexes() {

        dropNCreateIndex(ApplicationUser.class);
        dropNCreateIndex(TagLabel.class);
        dropNCreateIndex(SavedSearch.class);
        dropNCreateIndex(PolicyModel.class);
        dropNCreateIndex(ComponentLite.class);
        dropNCreateIndex(PolicyLite.class);
        dropNCreateIndex(DelegateModel.class);
        dropNCreateIndex(DelegationComponentLite.class);
        dropNCreateIndex(DelegateRuleLite.class);
        dropNCreateIndex(FolderLite.class);
    }

    private <T> void dropNCreateIndex(Class<T> clazz) {
        if (esTemplate.indexExists(clazz)) {
            esTemplate.deleteIndex(clazz);
        }
        esTemplate.createIndex(clazz);
        esTemplate.putMapping(clazz);
        esTemplate.refresh(clazz);
        log.info("{} indexes created successfully", clazz.getName());
    }

    @Override
    @Transactional
    public void indexData() throws ConsoleException {
        delegateModelService.reIndexAllModels();
        applicationUserService.reIndexAllUsers();
        tagLabelService.reIndexAllTags();
        savedSearchService.reIndexAllCriteria();
        policyModelService.reIndexAllModels();
        componentSearchService.reIndexAllComponents();
        policySearchService.reIndexAllPolicies();
        xacmlPolicySearchService.reIndexAllXacmlPolicies();
        delegationComponentSearchService.reIndexAllComponents();
        delegationRuleMgmtService.reIndexAllRules();
        folderService.reIndexAllFolders();
        remoteEnvironmentSearchService.reIndexAllRemoteEnvironments();
        workflowRequestCommentSearchService.reIndexAllWorkflowRequestComments();
        notificationSearchService.reIndexAllNotifications();
        log.info("Data indexed successfully");
    }

    @Override
    public void indexByName(String indexName) throws ConsoleException {
        // TODO Auto-generated method stub

    }

    @Autowired
    public void setWorkflowRequestCommentSearchService(WorkflowRequestCommentSearchService workflowRequestCommentSearchService) {
        this.workflowRequestCommentSearchService = workflowRequestCommentSearchService;
    }

    @Autowired
    public void setNotificationSearchService(NotificationSearchService notificationSearchService) {
        this.notificationSearchService = notificationSearchService;
    }
}
