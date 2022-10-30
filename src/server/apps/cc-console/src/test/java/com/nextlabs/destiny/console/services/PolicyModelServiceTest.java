/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 18, 2016
 *
 */
package com.nextlabs.destiny.console.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.nextlabs.destiny.console.config.TestConfiguration;
import com.nextlabs.destiny.console.config.TestElasticSearchConfig;
import com.nextlabs.destiny.console.config.root.MessageBundleResolveConfig;
import com.nextlabs.destiny.console.config.root.RootContextConfig;
import com.nextlabs.destiny.console.config.servlet.ServletContextConfig;
import com.nextlabs.destiny.console.dto.common.FacetResult;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.dto.common.SortField;
import com.nextlabs.destiny.console.dto.common.StringFieldValue;
import com.nextlabs.destiny.console.enums.DataType;
import com.nextlabs.destiny.console.enums.ObligationParameterDataType;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.enums.SearchFieldType;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.enums.TagType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.model.policy.ActionConfig;
import com.nextlabs.destiny.console.model.policy.AttributeConfig;
import com.nextlabs.destiny.console.model.policy.ObligationConfig;
import com.nextlabs.destiny.console.model.policy.OperatorConfig;
import com.nextlabs.destiny.console.model.policy.ParameterConfig;
import com.nextlabs.destiny.console.model.policy.PolicyModel;
import com.nextlabs.destiny.console.search.repositories.PolicyModelSearchRepository;
import com.nextlabs.destiny.console.services.policy.OperatorConfigService;
import com.nextlabs.destiny.console.services.policy.PolicyModelService;

/**
 *
 * JUnit Test for Policy Model Service
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestConfiguration.class,
        RootContextConfig.class, MessageBundleResolveConfig.class,
        TestElasticSearchConfig.class, ServletContextConfig.class })
public class PolicyModelServiceTest {

    private static final Logger log = LoggerFactory
            .getLogger(PolicyModelServiceTest.class);

    @Autowired
    private PolicyModelService policyModelService;

    @Resource
    private PolicyModelSearchRepository policyModelSearchRepository;

    @Autowired
    private OperatorConfigService operatorConfigService;

    @Autowired
    private TagLabelService tagLabelService;

    @Test
    @Transactional
    public void shouldSavePolicyModel() throws Exception {

        log.info("Test --> should save policy model");

        PolicyModel policyModel = createPolicyModel();

        policyModelService.save(policyModel, true);

        PolicyModel savedPM = policyModelService.findById(policyModel.getId());

        assertNotNull(savedPM);
        assertNotNull(savedPM.getId());
        assertEquals("KT Document", savedPM.getName());
        assertEquals(3, savedPM.getTags().size());
        assertEquals(2, savedPM.getAttributes().size());
        assertEquals(2, savedPM.getActions().size());
        assertEquals(1, savedPM.getObligations().size());
    }

    @Test
    @Transactional
    public void shouldModifyPolicyModel() throws Exception {

        log.info("Test --> should save policy model");

        PolicyModel policyModel = createPolicyModel();

        policyModel = policyModelService.save(policyModel, true);

        PolicyModel policyModel2 = policyModelService
                .findById(policyModel.getId());

        TagLabel tag1 = tagLabelService.saveTag(new TagLabel("owner",
                "Amila Silva", TagType.POLICY_MODEL_TAG, Status.ACTIVE));

        List<OperatorConfig> operatorConfigs = operatorConfigService.findAll();

        AttributeConfig attrib1 = new AttributeConfig();
        attrib1.setDataType(DataType.STRING);
        attrib1.setName("Content Type");
        attrib1.setShortName("content-type");

        for (OperatorConfig oper : operatorConfigs) {
            attrib1.getOperatorConfigs().add(oper);
        }

        ParameterConfig param1 = new ParameterConfig();
        param1.setName("Label2");
        param1.setType(ObligationParameterDataType.TEXT_SINGLE_ROW);
        param1.setEditable(false);
        param1.setHidden(false);
        param1.setDefaultValue("LK-3021");

        ObligationConfig oblig1 = new ObligationConfig();
        oblig1.setName("Oblig-2");
        oblig1.setRunAt(ObligationConfig.RUN_MODE_PEP);
        oblig1.getParameters().add(param1);

        ActionConfig action1 = new ActionConfig();
        action1.setName("Print");
        action1.setShortName("pr");

        policyModel2.getTags().add(tag1);
        policyModel2.getAttributes().add(attrib1);
        policyModel2.getActions().add(action1);
        policyModel2.getObligations().add(oblig1);

        policyModelService.save(policyModel2, false);

        PolicyModel savedPM = policyModelService.findById(policyModel2.getId());

        assertNotNull(savedPM);
        assertNotNull(savedPM.getId());
        assertEquals("KT Document", savedPM.getName());
        assertEquals(4, savedPM.getTags().size());
        assertEquals(3, savedPM.getAttributes().size());
        assertEquals(3, savedPM.getActions().size());
        assertEquals(2, savedPM.getObligations().size());

        // Clone test
        // PolicyModel clonedModel = policyModelService.clone(savedPM.getId());
        // assertNotNull(clonedModel);
        // assertNotNull(clonedModel.getId());
        // assertTrue(clonedModel.getId() != savedPM.getId());
    }

    /*
     * @Test
     * @Transactional public void shouldClonePolicyModel() throws Exception {
     * log.info("Test --> should save policy model"); PolicyModel policyModel =
     * createPolicyModel(); policyModelService.save(policyModel); PolicyModel
     * savedPM = policyModelService.findById(policyModel.getId());
     * assertNotNull(savedPM); assertNotNull(savedPM.getId()); assertEquals(
     * "KT Document", savedPM.getName()); assertEquals(3,
     * savedPM.getTags().size()); assertEquals(2,
     * savedPM.getAttributes().size()); assertEquals(2,
     * savedPM.getActions().size()); assertEquals(1,
     * savedPM.getObligations().size()); PolicyModel clone =
     * policyModelService.clone(savedPM.getId()); PolicyModel clonedPM =
     * policyModelService.findById(clone.getId()); assertNotNull(clonedPM);
     * assertNotNull(clonedPM.getId()); assertEquals("KT Document",
     * clonedPM.getName()); }
     */

    @Test
    @Transactional
    public void shoulFindByCriteria() throws Exception {

        log.info("Test --> should find by criteria");

        PolicyModel policyModel1 = createPolicyModel();
        PolicyModel policyModel2 = createPolicyModel();
        policyModel2.setName("SAP");
        PolicyModel policyModel3 = createPolicyModel();
        policyModel3.setName("KT - GM");

        policyModelService.save(policyModel1, true);
        policyModelService.save(policyModel2, true);
        policyModelService.save(policyModel3, true);

        SearchField field1 = new SearchField();
        field1.setField("name");
        field1.setType(SearchFieldType.SINGLE);
        field1.setValue(new StringFieldValue("KT"));

        SearchCriteria criteria = new SearchCriteria();
        criteria.getFields().add(field1);
        criteria.getSortFields().add(new SortField("name", SortField.ASC));

        Page<PolicyModel> policyModelPage = policyModelService
                .findByCriteria(criteria, false);

        assertNotNull(policyModelPage);
        assertEquals(2, policyModelPage.getContent().size());
        assertEquals("KT - GM", policyModelPage.getContent().get(0).getName());
    }

    @Test
    @Transactional
    public void shoulFindByIds() throws Exception {

        log.info("Test --> should find by Ids");

        PolicyModel policyModel1 = createPolicyModel();
        PolicyModel policyModel2 = createPolicyModel();
        policyModel2.setName("SAP");
        PolicyModel policyModel3 = createPolicyModel();
        policyModel3.setName("KT - GM");
        PolicyModel policyModel4 = createPolicyModel();
        policyModel4.setName("ITALINO");

        policyModelService.save(policyModel1, true);
        policyModelService.save(policyModel2, true);
        policyModelService.save(policyModel3, true);
        policyModelService.save(policyModel4, true);

        List<Long> ids = new ArrayList<>();
        ids.add(policyModel1.getId());
        ids.add(policyModel2.getId());
        ids.add(policyModel4.getId());

        List<SortField> sortFields = new ArrayList<>();
        sortFields.add(new SortField("name", SortField.ASC));

        Page<PolicyModel> policyModelPage = policyModelService.findByIds(ids,
                sortFields, PageRequest.of(0, 10));

        assertNotNull(policyModelPage);
        assertEquals(3, policyModelPage.getContent().size());
    }

    @Test
    @Transactional
    public void shoulFindFacetByCriteria() throws Exception {

        log.info("Test --> should find Facet By Criteria");

        PolicyModel policyModel1 = createPolicyModel();
        policyModel1.setType(PolicyModelType.RESOURCE);

        PolicyModel policyModel2 = createPolicyModel();
        policyModel2.setName("SAP - 1");
        policyModel2.setType(PolicyModelType.RESOURCE);
        PolicyModel policyModel3 = createPolicyModel();
        policyModel3.setName("KT2 - GM");
        policyModel3.setType(PolicyModelType.RESOURCE);
        PolicyModel policyModel4 = createPolicyModel();
        policyModel4.setName("ITALINO");
        policyModel4.setType(PolicyModelType.RESOURCE);
        PolicyModel policyModel5 = createPolicyModel();
        policyModel5.setType(PolicyModelType.SUBJECT);

        policyModelService.save(policyModel1, false);
        policyModelService.save(policyModel2, false);
        policyModelService.save(policyModel3, false);
        policyModelService.save(policyModel4, false);
        policyModelService.save(policyModel5, false);

        SearchCriteria criteria = new SearchCriteria();
        criteria.setFacetField("type");
        criteria.getSortFields().add(new SortField("name", SortField.ASC));

        FacetResult facetResults = policyModelService
                .findFacetByCriteria(criteria);

        assertNotNull(facetResults);
        assertEquals(2, facetResults.getTerms().size());
        // assertEquals(5, facetResults.getTerms().get(0).getCount());
        assertEquals(1, facetResults.getTerms().get(1).getCount());

        policyModelService.remove(policyModel5.getId());
        PolicyModel removedModel = policyModelService
                .findById(policyModel5.getId());
        assertNotNull(removedModel);
        assertEquals(Status.DELETED, removedModel.getStatus());
        removedModel = policyModelSearchRepository.findById(policyModel5.getId()).orElse(null);
        assertNull(removedModel);

        // bulk delete
        List<Long> ids = new ArrayList<>();
        ids.add(policyModel1.getId());
        ids.add(policyModel2.getId());
        ids.add(policyModel3.getId());
        ids.add(policyModel4.getId());
        ids.add(policyModel5.getId());
        policyModelService.remove(ids);

        criteria.setPageNo(0);
        criteria.setPageSize(10);
        Page<PolicyModel> policyModels = policyModelService
                .findByCriteria(criteria, false);
        assertNotNull(policyModels);
        assertEquals(3, policyModels.getContent().size());

    }

    @Test(expected = ConsoleException.class)
    public void errorInFindFacetByCriteria() throws Exception {
        policyModelService.findFacetByCriteria(null);
    }

    @Test(expected = ConsoleException.class)
    public void errorInFindByCriteria() throws Exception {
        policyModelService.findByCriteria(null, false);
    }

    @Test(expected = ConsoleException.class)
    public void errorInFindByIdsWithNullIds() throws Exception {
        policyModelService.findByIds(null, new ArrayList<SortField>(),
                PageRequest.of(0, 12));
    }

    @Test(expected = ConsoleException.class)
    public void errorInSave() throws Exception {
        policyModelService.save(null, false);
    }

    @Test(expected = NoDataFoundException.class)
    public void errorInRemoveWithNonExising() throws Exception {
        policyModelService.remove(10000L);
    }

    @Test(expected = ConsoleException.class)
    public void errorInBulkRemove() throws Exception {
        List<Long> ids = null;
        policyModelService.remove(ids);
    }

    @Test
    public void reIndexing() throws Exception {
        policyModelService.reIndexAllModels();

    }

    private PolicyModel createPolicyModel() throws ConsoleException {
        TagLabel tag1 = tagLabelService.saveTag(new TagLabel("ITAR", "ITAR",
                TagType.POLICY_MODEL_TAG, Status.ACTIVE));
        TagLabel tag2 = tagLabelService.saveTag(new TagLabel("AK-989", "AK-989",
                TagType.POLICY_MODEL_TAG, Status.ACTIVE));
        TagLabel tag3 = tagLabelService.saveTag(new TagLabel("KT-2", "KT-5",
                TagType.POLICY_MODEL_TAG, Status.ACTIVE));

        OperatorConfig oper1 = new OperatorConfig();
        oper1.setDataType(DataType.STRING);
        oper1.setKey("in");
        oper1.setLabel("in");

        OperatorConfig oper2 = new OperatorConfig();
        oper2.setDataType(DataType.STRING);
        oper2.setKey("like");
        oper2.setLabel("like");

        OperatorConfig oper3 = new OperatorConfig();
        oper3.setDataType(DataType.STRING);
        oper3.setKey("startsWith");
        oper3.setLabel("Starts with");

        oper1 = operatorConfigService.save(oper1);
        oper2 = operatorConfigService.save(oper2);
        oper3 = operatorConfigService.save(oper3);

        PolicyModel policyModel = new PolicyModel();
        policyModel.setName("KT Document");
        policyModel.setDescription("KT Document");
        policyModel.setShortName("KT_DOC");
        policyModel.setStatus(Status.ACTIVE);
        policyModel.getTags().add(tag1);
        policyModel.getTags().add(tag2);
        policyModel.getTags().add(tag3);

        AttributeConfig attrib1 = new AttributeConfig();
        attrib1.setDataType(DataType.STRING);
        attrib1.setName("File name");
        attrib1.setShortName("fileName");
        attrib1.getOperatorConfigs().add(oper1);
        attrib1.getOperatorConfigs().add(oper3);

        AttributeConfig attrib2 = new AttributeConfig();
        attrib2.setDataType(DataType.STRING);
        attrib2.setName("Document Rack");
        attrib2.setShortName("document_rack");
        attrib2.getOperatorConfigs().add(oper1);
        attrib2.getOperatorConfigs().add(oper2);
        attrib2.getOperatorConfigs().add(oper3);

        policyModel.getAttributes().add(attrib1);
        policyModel.getAttributes().add(attrib2);

        ActionConfig action1 = new ActionConfig();
        action1.setName("Copy");
        action1.setShortName("cp");

        ActionConfig action2 = new ActionConfig();
        action2.setName("Edit");
        action2.setShortName("ed");

        policyModel.getActions().add(action1);
        policyModel.getActions().add(action2);

        ParameterConfig param1 = new ParameterConfig();
        param1.setName("Label");
        param1.setType(ObligationParameterDataType.TEXT_SINGLE_ROW);
        param1.setEditable(false);
        param1.setHidden(false);
        param1.setDefaultValue("Amila Silva");

        ObligationConfig oblig1 = new ObligationConfig();
        oblig1.setName("Oblig-1");
        oblig1.setRunAt(ObligationConfig.RUN_MODE_PEP);
        oblig1.getParameters().add(param1);

        policyModel.getObligations().add(oblig1);
        return policyModel;
    }

}
