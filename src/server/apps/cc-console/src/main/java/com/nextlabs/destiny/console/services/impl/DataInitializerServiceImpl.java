/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 18, 2016
 *
 */
package com.nextlabs.destiny.console.services.impl;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nextlabs.destiny.console.dao.policy.OperatorConfigDao;
import com.nextlabs.destiny.console.enums.DataType;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.enums.TagType;
import com.nextlabs.destiny.console.exceptions.CircularReferenceException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.model.policy.OperatorConfig;
import com.nextlabs.destiny.console.services.DASeedDataService;
import com.nextlabs.destiny.console.services.DataInitializerService;
import com.nextlabs.destiny.console.services.HelpContentSearchService;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.TagLabelService;
import com.nextlabs.destiny.console.services.policy.OperatorConfigService;

/**
 *
 * Data Initializer Service Implementation
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@Service
public class DataInitializerServiceImpl implements DataInitializerService {

    private static final Logger log = LoggerFactory
            .getLogger(DataInitializerServiceImpl.class);

    @Autowired
    protected MessageBundleService msgBundle;

    @Autowired
    private OperatorConfigDao operatorConfigDao;

    @Autowired
    private OperatorConfigService operatorConfigService;

    @Autowired
    private HelpContentSearchService helpSearchService;

    @Autowired
    private DASeedDataService daSeedDataService;

    @Autowired
    private TagLabelService tagLabelService;

    // @Async
    @PostConstruct
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void initData() throws ConsoleException, CircularReferenceException {
        initializeOperatorConfig();
        createDASeedData();
        createHiddenTagLables();
        reloadHelpContent();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void initializeOperatorConfig() throws ConsoleException {

        // Check to see if we've done initialization by looking for operators we will add
        if (operatorConfigDao.findByKeyAndDataType(msgBundle.getText("oper.equals.key"),
                                                   DataType.STRING) == null) {
            log.info("Performing one-time OPERATOR_CONFIG initialization");
            List<OperatorConfig> operConfigList = getOpConfigInitializationData();
            for (OperatorConfig operConfig : operConfigList) {
                operatorConfigService.save(operConfig);
            }
            log.info("No of records inserted", operConfigList.size());
        }

    }

    @Override
    public void reloadHelpContent() throws ConsoleException {
        long startTime = System.currentTimeMillis();
        helpSearchService.uploadHelpContent();
        long processingTime = System.currentTimeMillis() - startTime;
        log.info("Help Content indexed successfully, Time taken = {} millis ",
                processingTime);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void createDASeedData() throws ConsoleException, CircularReferenceException {
        daSeedDataService.createDAModels();
        daSeedDataService.createDAComponents();
    }

    private List<OperatorConfig> getOpConfigInitializationData() {

        List<OperatorConfig> operConfigList = new ArrayList<>();

        OperatorConfig equalsStr = createNewOperatorConfig(
                msgBundle.getText("oper.equals.key"),
                msgBundle.getText("oper.equals.label"), DataType.STRING);
        operConfigList.add(equalsStr);
        OperatorConfig notEqualsStr = createNewOperatorConfig(
                msgBundle.getText("oper.notequals.key"),
                msgBundle.getText("oper.notequals.label"), DataType.STRING);
        operConfigList.add(notEqualsStr);

        OperatorConfig equals = createNewOperatorConfig(
                msgBundle.getText("oper.equals.key"),
                msgBundle.getText("oper.equals.key"), DataType.NUMBER);
        operConfigList.add(equals);
        OperatorConfig notEquals = createNewOperatorConfig(
                msgBundle.getText("oper.notequals.key"),
                msgBundle.getText("oper.notequals.key"), DataType.NUMBER);
        operConfigList.add(notEquals);
        OperatorConfig lessThan = createNewOperatorConfig(
                msgBundle.getText("oper.lt.key"),
                msgBundle.getText("oper.lt.key"), DataType.NUMBER);
        operConfigList.add(lessThan);
        OperatorConfig lessThanEquals = createNewOperatorConfig(
                msgBundle.getText("oper.lte.key"),
                msgBundle.getText("oper.lte.key"), DataType.NUMBER);
        operConfigList.add(lessThanEquals);
        OperatorConfig greaterThan = createNewOperatorConfig(
                msgBundle.getText("oper.gt.key"),
                msgBundle.getText("oper.gt.key"), DataType.NUMBER);
        operConfigList.add(greaterThan);
        OperatorConfig greaterThanEquals = createNewOperatorConfig(
                msgBundle.getText("oper.gte.key"),
                msgBundle.getText("oper.gte.key"), DataType.NUMBER);
        operConfigList.add(greaterThanEquals);

        OperatorConfig includes = createNewOperatorConfig(
                msgBundle.getText("oper.include.key"),
                msgBundle.getText("oper.include.label"), DataType.MULTIVAL);
        operConfigList.add(includes);
        OperatorConfig equalsUnordered = createNewOperatorConfig(
                msgBundle.getText("oper.equals.unordered.key"),
                msgBundle.getText("oper.equals.unordered.label"),
                DataType.MULTIVAL);
        operConfigList.add(equalsUnordered);
        OperatorConfig contains = createNewOperatorConfig(
                msgBundle.getText("oper.multival.equals.key"),
                msgBundle.getText("oper.multival.equals.label"), DataType.MULTIVAL);
        operConfigList.add(contains);
        OperatorConfig doesNotContain = createNewOperatorConfig(
                msgBundle.getText("oper.multival.does.not.equal.key"),
                msgBundle.getText("oper.multival.does.not.equal.label"), 
                DataType.MULTIVAL);
        operConfigList.add(doesNotContain);

        return operConfigList;
    }

    private OperatorConfig createNewOperatorConfig(String key, String label,
            DataType dataType) {

        OperatorConfig operConfig = new OperatorConfig();
        operConfig.setKey(key);
        operConfig.setLabel(label);
        operConfig.setDataType(dataType);

        return operConfig;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void createHiddenTagLables() throws ConsoleException {

        List<TagLabel> policyAllTagLabels = tagLabelService
                .findByKey(TagLabel.ALL_TAGS_KEY, TagType.POLICY_TAG);
        List<TagLabel> componentAllTagLabels = tagLabelService
                .findByKey(TagLabel.ALL_TAGS_KEY, TagType.COMPONENT_TAG);
        List<TagLabel> policyModelAllTagLabels = tagLabelService
                .findByKey(TagLabel.ALL_TAGS_KEY, TagType.POLICY_MODEL_TAG);

        if (policyModelAllTagLabels.isEmpty()) {
            TagLabel allPolicyModelTag = new TagLabel(TagLabel.ALL_TAGS_KEY,
                    msgBundle.getText("tag.hidden.all.tags"),
                    TagType.POLICY_MODEL_TAG, Status.ACTIVE);
            allPolicyModelTag.setHidden(true);
            tagLabelService.saveTag(allPolicyModelTag);
        }

        if (componentAllTagLabels.isEmpty()) {
            TagLabel allComponentTag = new TagLabel(TagLabel.ALL_TAGS_KEY,
                    msgBundle.getText("tag.hidden.all.tags"),
                    TagType.COMPONENT_TAG, Status.ACTIVE);
            allComponentTag.setHidden(true);
            tagLabelService.saveTag(allComponentTag);
        }

        if (policyAllTagLabels.isEmpty()) {
            TagLabel allPolicyTag = new TagLabel(TagLabel.ALL_TAGS_KEY,
                    msgBundle.getText("tag.hidden.all.tags"),
                    TagType.POLICY_TAG, Status.ACTIVE);
            allPolicyTag.setHidden(true);
            tagLabelService.saveTag(allPolicyTag);
        }
    }

}
