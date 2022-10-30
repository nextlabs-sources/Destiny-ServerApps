/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 19, 2016
 *
 */
package com.nextlabs.destiny.console.controllers.policymgmt;

import static com.nextlabs.destiny.console.enums.SavedSearchType.POLICY_MODEL_RESOURCE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.nextlabs.destiny.console.config.TestConfiguration;
import com.nextlabs.destiny.console.config.TestElasticSearchConfig;
import com.nextlabs.destiny.console.config.root.MessageBundleResolveConfig;
import com.nextlabs.destiny.console.config.root.RootContextConfig;
import com.nextlabs.destiny.console.config.root.WebMvcConfig;
import com.nextlabs.destiny.console.config.servlet.ServletContextConfig;
import com.nextlabs.destiny.console.dto.common.DateFieldValue;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.dto.common.SortField;
import com.nextlabs.destiny.console.dto.common.StringFieldValue;
import com.nextlabs.destiny.console.dto.policymgmt.SavedSearchDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SearchCriteriaDTO;
import com.nextlabs.destiny.console.enums.DataType;
import com.nextlabs.destiny.console.enums.DateOption;
import com.nextlabs.destiny.console.enums.ObligationParameterDataType;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.enums.SavedSearchType;
import com.nextlabs.destiny.console.enums.SearchFieldType;
import com.nextlabs.destiny.console.enums.SharedMode;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.enums.TagType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.SavedSearch;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.model.policy.ActionConfig;
import com.nextlabs.destiny.console.model.policy.AttributeConfig;
import com.nextlabs.destiny.console.model.policy.ObligationConfig;
import com.nextlabs.destiny.console.model.policy.OperatorConfig;
import com.nextlabs.destiny.console.model.policy.ParameterConfig;
import com.nextlabs.destiny.console.model.policy.PolicyModel;
import com.nextlabs.destiny.console.services.SavedSearchService;
import com.nextlabs.destiny.console.services.TagLabelService;
import com.nextlabs.destiny.console.services.policy.OperatorConfigService;
import com.nextlabs.destiny.console.services.policy.PolicyModelService;

/**
 *
 * Policy model search controller unit testing
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestConfiguration.class,
        RootContextConfig.class, MessageBundleResolveConfig.class,
        TestElasticSearchConfig.class, WebMvcConfig.class,
        ServletContextConfig.class})
public class PolicyModelSearchControllerTest {

    private static final Logger log = LoggerFactory
            .getLogger(PolicyModelSearchControllerTest.class);

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private SavedSearchService savedSearchService;

    @Autowired
    private OperatorConfigService operatorConfigService;

    @Autowired
    private TagLabelService tagLabelService;

    @Autowired
    private PolicyModelService policyModelService;

    private MockMvc mockMvc;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    @Transactional
    public void shouldFindPolicyModelByCriteria() throws Exception {
        log.info("Test --> /v1/policyModel/search");

        PolicyModel policyModel1 = createPolicyModel();
        policyModel1.setName("DAC Documents");
        policyModel1.setShortName("dac");

        PolicyModel policyModel2 = createPolicyModel();
        policyModel2.setName("ITAR Documents");
        policyModel2.setShortName("ITAR");

        PolicyModel policyModel3 = createPolicyModel();
        policyModel3.setName("WhatApp Data model");
        policyModel3.setShortName("whatsapp");

        PolicyModel policyModel4 = createPolicyModel();
        policyModel4.setName("Mobile data");
        policyModel4.setShortName("mobile");

		policyModelService.save(policyModel1, true);
        policyModelService.save(policyModel2, true);
        policyModelService.save(policyModel3, true);
        policyModelService.save(policyModel4, true);

        SearchCriteria criteria = new SearchCriteria();
        SearchField field1 = new SearchField();
        field1.setField("name");
        field1.setType(SearchFieldType.SINGLE);
        field1.setValue(new StringFieldValue("WhatApp"));

        SearchField field2 = new SearchField();
        field2.setField("lastUpdatedDate");
        field2.setType(SearchFieldType.DATE);

        DateFieldValue dateField = new DateFieldValue();
        dateField.setDateOption(DateOption.CUSTOM.name());
        dateField.setFromDate(LocalDate.now().minusDays(10).toDate().getTime());
        dateField.setToDate(LocalDate.now().plusDays(5).toDate().getTime());
        field2.setValue(dateField);

        criteria.getFields().add(field1);
        criteria.getFields().add(field2);
        criteria.setPageNo(0);
        criteria.setPageSize(15);

        SearchCriteriaDTO searchDTO = new SearchCriteriaDTO();
        searchDTO.setCriteria(criteria);

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(searchDTO);

        log.info("Policy Model search criteria: {}", content);

        mockMvc.perform(post("/v1/policyModel/search")
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1004"));
    }

    @Test
    @Transactional
    public void shouldFindPolicyModelByIds() throws Exception {
        log.info("Test --> /v1/policyModel/search/ids");

        PolicyModel policyModel1 = createPolicyModel();
        policyModel1.setName("DAC Documents");
        policyModel1.setShortName("dac");

        PolicyModel policyModel2 = createPolicyModel();
        policyModel2.setName("ITAR Documents");
        policyModel2.setShortName("ITAR");

        PolicyModel policyModel3 = createPolicyModel();
        policyModel3.setName("WhatApp Data model");
        policyModel3.setShortName("whatsapp");

        PolicyModel policyModel4 = createPolicyModel();
        policyModel4.setName("Mobile data");
        policyModel4.setShortName("mobile");

        policyModelService.save(policyModel1, true);
        policyModelService.save(policyModel2, true);
        policyModelService.save(policyModel3, true);
        policyModelService.save(policyModel4, true);

        SearchCriteria criteria = new SearchCriteria();

        SearchField field1 = new SearchField();
        field1.setField("id");
        field1.setType(SearchFieldType.MULTI);
        field1.setValue(new StringFieldValue(new String[] {
                "" + policyModel1.getId(), "" + policyModel2.getId(),
                "" + policyModel3.getId(), "" + policyModel4.getId() }));

        SortField sortField = new SortField("name", SortField.ASC);

        criteria.getFields().add(field1);
        criteria.getSortFields().add(sortField);
        criteria.setPageNo(0);
        criteria.setPageSize(15);

        SearchCriteriaDTO searchDTO = new SearchCriteriaDTO();
        searchDTO.setCriteria(criteria);
        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(searchDTO);
        log.info("Policy model search by ids, criteria :{}", content);

        mockMvc.perform(post("/v1/policyModel/search/ids")
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1004"));
    }

    @Test
    @Transactional
    public void shouldFindByFacets() throws Exception {
        log.info("Test --> /v1/policyModel/search/facet");

        PolicyModel policyModel1 = createPolicyModel();
        policyModel1.setName("DAC Documents");
        policyModel1.setShortName("dac");

        PolicyModel policyModel2 = createPolicyModel();
        policyModel2.setName("ITAR Documents");
        policyModel2.setShortName("ITAR");

        PolicyModel policyModel3 = createPolicyModel();
        policyModel3.setName("WhatApp Data model");
        policyModel3.setShortName("whatsapp");

        PolicyModel policyModel4 = createPolicyModel();
        policyModel4.setName("Mobile data");
        policyModel4.setShortName("mobile");

        policyModelService.save(policyModel1, true);
        policyModelService.save(policyModel2, true);
        policyModelService.save(policyModel3, true);
        policyModelService.save(policyModel4, true);

        SearchCriteria criteria = new SearchCriteria();
        criteria.setFacetField("shortName");
        SortField sortField = new SortField("name", SortField.ASC);
        criteria.getSortFields().add(sortField);
        criteria.setPageNo(0);
        criteria.setPageSize(15);

        SearchCriteriaDTO searchDTO = new SearchCriteriaDTO();
        searchDTO.setCriteria(criteria);
        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(searchDTO);

        log.info("Policy model search by facets, criteria :{}", content);

        mockMvc.perform(post("/v1/policyModel/search/facet")
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1004"));
    }

    @Test
    @Transactional
    public void shouldFindPolicyModelWithNoData() throws Exception {

        SearchCriteria criteria = new SearchCriteria();
        criteria.setFacetField("shortName");

        SearchField field1 = new SearchField();
        field1.setField("name");
        field1.setType(SearchFieldType.SINGLE);
        field1.setValue(new StringFieldValue("ZAP"));

        SortField sortField = new SortField("name", SortField.ASC);

        criteria.getFields().add(field1);
        criteria.getSortFields().add(sortField);
        criteria.setPageNo(0);
        criteria.setPageSize(15);

        SearchCriteriaDTO searchDTO = new SearchCriteriaDTO();
        searchDTO.setCriteria(criteria);
        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(searchDTO);
        log.info("Criteria :{}", content);

        mockMvc.perform(post("/v1/policyModel/search/facet")
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1004"));

        mockMvc.perform(post("/v1/policyModel/search/ids")
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("6000"));

        mockMvc.perform(post("/v1/policyModel/search")
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("5001"));
    }

    @Test
    @Transactional
    public void shouldLoadPolicyModelSearchFields() throws Exception {

        log.info("Test --> /v1/policyModel/search/fields ");

        mockMvc.perform(get("/v1/policyModel/search/fields")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1004"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    public void shouldPolicyModelSearch() throws Exception {

        log.info("Test --> /v1/policyModel/search/add ");

        SearchCriteria criteria = new SearchCriteria();
        criteria.getColumns().add("name");
        criteria.getColumns().add("shortName");
        criteria.getColumns().add("description");

        SearchField field1 = new SearchField();
        field1.setField("name");
        field1.setType(SearchFieldType.SINGLE);
        field1.setValue(new StringFieldValue("SAP DOCS"));

        SearchField field2 = new SearchField();
        field2.setField("lastUpdatedDate");
        field2.setType(SearchFieldType.DATE);

        DateFieldValue dateField = new DateFieldValue();
        dateField.setDateOption(DateOption.CUSTOM.name());
        dateField.setFromDate(LocalDate.now().minusDays(10).toDate().getTime());
        dateField.setToDate(LocalDate.now().plusDays(5).toDate().getTime());
        field2.setValue(dateField);

        criteria.getFields().add(field1);
        criteria.getFields().add(field2);
        criteria.setPageNo(0);
        criteria.setPageSize(15);

        SavedSearchDTO searchDTO = new SavedSearchDTO();
        searchDTO.setName("SAP Doc Models");
        searchDTO.setDesc("SAP Doc Models");
        searchDTO.setSharedMode(SharedMode.USERS.name());
        searchDTO.getUserIds().add("amilasilva88@gmail.com");
        searchDTO.getUserIds().add("test_console@gmail.com");
        searchDTO.setCriteria(criteria);
        searchDTO.setType(POLICY_MODEL_RESOURCE.name());

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(searchDTO);

        mockMvc.perform(post("/v1/policyModel/search/add")
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1000"));

        PageRequest pageable = PageRequest.of(0, 10);
        Page<SavedSearch> savedSearchPage = savedSearchService
                .findByNameOrDescriptionAndType("SAP Doc Models",
                        POLICY_MODEL_RESOURCE, pageable);

        assertNotNull(savedSearchPage);
        assertEquals(3, savedSearchPage.getContent().get(0).criteriaModel()
                .getColumns().size());
        assertEquals(2, savedSearchPage.getContent().get(0).criteriaModel()
                .getFields().size());
        assertEquals(2,
                savedSearchPage.getContent().get(0).getUserIds().size());
    }

    @Test
    public void shouldModifyPolicyModelCriteria() throws Exception {

        log.info("Test --> /v1/policyModel/search/modify ");

        SearchCriteria criteria = new SearchCriteria();
        criteria.getColumns().add("name");
        criteria.getColumns().add("shortName");
        criteria.getColumns().add("description");

        SearchField field1 = new SearchField();
        field1.setField("name");
        field1.setType(SearchFieldType.MULTI);
        field1.setValue(new StringFieldValue(new String[] { "ITAR", "AXA" }));

        SearchField field2 = new SearchField();
        field2.setField("lastUpdatedDate");
        field2.setType(SearchFieldType.DATE);

        DateFieldValue dateField = new DateFieldValue();
        dateField.setDateOption(DateOption.CUSTOM.name());
        dateField.setFromDate(LocalDate.now().minusDays(10).toDate().getTime());
        dateField.setToDate(LocalDate.now().plusDays(5).toDate().getTime());
        field2.setValue(dateField);

        criteria.getFields().add(field1);
        criteria.getFields().add(field2);
        criteria.setPageNo(0);
        criteria.setPageSize(15);

        ObjectMapper mapper = new ObjectMapper();
        String criteriaJson = mapper.writeValueAsString(criteria);

        SavedSearch search = new SavedSearch();
        search.setName("SAP Resource DOCS model");
        search.setDesc("SAP Resource DOCS model");
        search.setSharedMode(SharedMode.USERS);
        search.getUserIds().add("amilasilva88@gmail.com");
        search.getUserIds().add("test_console@gmail.com");
        search.setType(POLICY_MODEL_RESOURCE);
        search.setCriteria(criteriaJson);

        search = savedSearchService.saveCriteria(search);

        SavedSearchDTO searchDTO = SavedSearchDTO.getDTO(search);
        searchDTO.setName("SAP Docs model v1.1");
        searchDTO.setDesc("SAP Docs model v1.1");
        searchDTO.setSharedMode(SharedMode.ONLY_ME.name());
        searchDTO.getUserIds().clear();

        String content = mapper.writeValueAsString(searchDTO);

        mockMvc.perform(put("/v1/policyModel/search/modify")
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1001"));

        PageRequest pageable = PageRequest.of(0, 10);
        Page<SavedSearch> savedSearchPage = savedSearchService
                .findByNameOrDescriptionAndType("SAP Docs",
                        POLICY_MODEL_RESOURCE, pageable);

        assertNotNull(savedSearchPage);
        assertEquals(3, savedSearchPage.getContent().get(0).criteriaModel()
                .getColumns().size());
        assertEquals(2, savedSearchPage.getContent().get(0).criteriaModel()
                .getFields().size());
        assertEquals(0,
                savedSearchPage.getContent().get(0).getUserIds().size());
        assertEquals(SharedMode.ONLY_ME.name(),
                savedSearchPage.getContent().get(0).getSharedMode().name());
    }

    @Test
    public void shouldFindPolicyModelCriteria() throws Exception {

        log.info("Test --> /v1/policyModel/search/saved/:id ");

        SearchCriteria criteria = new SearchCriteria();
        criteria.getColumns().add("name");
        criteria.getColumns().add("shortName");
        criteria.getColumns().add("description");

        SearchField field1 = new SearchField();
        field1.setField("name");
        field1.setType(SearchFieldType.SINGLE);
        field1.setValue(new StringFieldValue("ITAR"));

        SearchField field2 = new SearchField();
        field2.setField("lastUpdatedDate");
        field2.setType(SearchFieldType.DATE);

        DateFieldValue dateField = new DateFieldValue();
        dateField.setDateOption(DateOption.CUSTOM.name());
        dateField.setFromDate(LocalDate.now().minusDays(10).toDate().getTime());
        dateField.setToDate(LocalDate.now().plusDays(5).toDate().getTime());
        field2.setValue(dateField);

        criteria.getFields().add(field1);
        criteria.getFields().add(field2);
        criteria.setPageNo(0);
        criteria.setPageSize(15);

        ObjectMapper mapper = new ObjectMapper();
        String criteriaJson = mapper.writeValueAsString(criteria);

        SavedSearch search = new SavedSearch();
        search.setName("SAP Documents model");
        search.setDesc("SAP Documents model");
        search.setSharedMode(SharedMode.USERS);
        search.setType(POLICY_MODEL_RESOURCE);
        search.getUserIds().add("amilasilva88@gmail.com");
        search.getUserIds().add("test_console@gmail.com");
        search.setCriteria(criteriaJson);

        search = savedSearchService.saveCriteria(search);

        mockMvc.perform(get("/v1/policyModel/search/saved/" + search.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1003"))
                .andExpect(jsonPath("$.data.name").value(search.getName()));

        log.info("Test --> /v1/policyModel/search/savedlist/:name ");

        mockMvc.perform(
                get("/v1/policyModel/search/savedlist/POLICY_MODEL_RESOURCE/SAP Documents")
                        .param("pageNo", "0").param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1003"))
                .andExpect(jsonPath("$.data").isNotEmpty());

        mockMvc.perform(
                get("/v1/policyModel/search/savedlist/POLICY_MODEL_SUBJECT/SAP")
                        .param("pageNo", "0").param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("5000"));

        mockMvc.perform(
                get("/v1/policyModel/search/savedlist/POLICY_MODEL_RESOURCE/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1003"));

        mockMvc.perform(
                delete("/v1/policyModel/search/remove/" + search.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1002"));

    }

    @Test
    public void shouldReturnErrroCode() throws Exception {

        mockMvc.perform(get("/v1/policyModel/search/savedlist/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().is4xxClientError());

        mockMvc.perform(get("/v1/policyModel/search/saved/1200")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("5000"));

        mockMvc.perform(get("/v1/policyModel/search/savedlist/"
                + SavedSearchType.POLICY_MODEL_SUBJECT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("5000"));

        mockMvc.perform(delete("/v1/policyModel/search/remove/-10")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("4001"));

        mockMvc.perform(delete("/v1/policyModel/search/remove/1000")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("5002"));
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
        policyModel.setType(PolicyModelType.RESOURCE);
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
