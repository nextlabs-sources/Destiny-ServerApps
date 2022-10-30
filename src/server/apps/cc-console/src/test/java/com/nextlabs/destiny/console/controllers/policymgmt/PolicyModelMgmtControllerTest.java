/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 19, 2016
 *
 */
package com.nextlabs.destiny.console.controllers.policymgmt;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextlabs.destiny.console.config.TestConfiguration;
import com.nextlabs.destiny.console.config.TestElasticSearchConfig;
import com.nextlabs.destiny.console.config.root.MessageBundleResolveConfig;
import com.nextlabs.destiny.console.config.root.RootContextConfig;
import com.nextlabs.destiny.console.config.root.WebMvcConfig;
import com.nextlabs.destiny.console.config.servlet.ServletContextConfig;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyModelDTO;
import com.nextlabs.destiny.console.enums.DataType;
import com.nextlabs.destiny.console.enums.ObligationParameterDataType;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.enums.TagType;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.model.policy.ActionConfig;
import com.nextlabs.destiny.console.model.policy.AttributeConfig;
import com.nextlabs.destiny.console.model.policy.ObligationConfig;
import com.nextlabs.destiny.console.model.policy.OperatorConfig;
import com.nextlabs.destiny.console.model.policy.ParameterConfig;
import com.nextlabs.destiny.console.model.policy.PolicyModel;
import com.nextlabs.destiny.console.services.TagLabelService;
import com.nextlabs.destiny.console.services.policy.OperatorConfigService;
import com.nextlabs.destiny.console.services.policy.PolicyModelService;

/**
 *
 * Policy model mgmt controller unit testing
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
        ServletContextConfig.class })
public class PolicyModelMgmtControllerTest {

    private static final Logger log = LoggerFactory
            .getLogger(PolicyModelMgmtControllerTest.class);

    @Autowired
    private WebApplicationContext wac;

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
    public void should1AddPolicyModel() throws Exception {
        log.info("Test --> /v1/policyModel/mgmt/add");

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

        PolicyModelDTO dto = new PolicyModelDTO();
        dto.setName("KT Document");
        dto.setDescription("KT Document");
        dto.setShortName("KT_DOC");
        dto.setStatus(Status.ACTIVE.name());
        dto.setType(PolicyModelType.RESOURCE.name());

        TagDTO tagD1 = TagDTO.getDTO(tag1);
        TagDTO tagD2 = TagDTO.getDTO(tag2);
        TagDTO tagD3 = TagDTO.getDTO(tag3);

        dto.getTags().add(tagD1);
        dto.getTags().add(tagD2);
        dto.getTags().add(tagD3);

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

        dto.getAttributes().add(attrib1);
        dto.getAttributes().add(attrib2);

        ActionConfig action1 = new ActionConfig();
        action1.setName("Copy");
        action1.setShortName("cp");

        ActionConfig action2 = new ActionConfig();
        action2.setName("Edit");
        action2.setShortName("ed");

        dto.getActions().add(action1);
        dto.getActions().add(action2);

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

        dto.getObligations().add(oblig1);

        ObjectMapper mapper = new ObjectMapper();
        String contentJson = mapper.writeValueAsString(dto);

        log.info("Save Policy model criteria : {}", contentJson);

        mockMvc.perform(post("/v1/policyModel/mgmt/add")
                .contentType(MediaType.APPLICATION_JSON).content(contentJson)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1000"));

        mockMvc.perform(post("/v1/policyModel/mgmt/add")
                .contentType(MediaType.APPLICATION_JSON).content(contentJson)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1000"));

        mockMvc.perform(post("/v1/policyModel/mgmt/add")
                .contentType(MediaType.APPLICATION_JSON).content(contentJson)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1000"));
    }

    @Test
    public void should2ModifyPolicyModel() throws Exception {
        log.info("Test --> /v1/policyModel/mgmt/modify");

        SearchCriteria criteria = new SearchCriteria();
        criteria.setPageNo(0);
        criteria.setPageSize(10);
        Page<PolicyModel> policyModelPage = policyModelService
                .findByCriteria(criteria, false);

        PolicyModel policyModel = policyModelPage.getContent().get(0);

        TagLabel tag1 = tagLabelService.saveTag(new TagLabel("AS-1", "AS-1",
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

        PolicyModelDTO dto = PolicyModelDTO.getDTO(policyModel);
        dto.setName("KT - Modified Document v2");

        TagDTO tagD1 = TagDTO.getDTO(tag1);
        dto.getTags().add(tagD1);

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
        attrib2.getOperatorConfigs().add(oper3);

        dto.getAttributes().add(attrib1);

        ActionConfig action1 = new ActionConfig();
        action1.setName("PRINT");
        action1.setShortName("pr");

        dto.getActions().add(action1);

        ParameterConfig param1 = new ParameterConfig();
        param1.setName("Label");
        param1.setType(ObligationParameterDataType.TEXT_SINGLE_ROW);
        param1.setEditable(false);
        param1.setHidden(false);
        param1.setDefaultValue("Amila Silva, @2016");

        ObligationConfig oblig1 = new ObligationConfig();
        oblig1.setName("Oblig-1");
        oblig1.setRunAt(ObligationConfig.RUN_MODE_PEP);
        oblig1.getParameters().add(param1);

        dto.getObligations().add(oblig1);

        ObjectMapper mapper = new ObjectMapper();
        String contentJson = mapper.writeValueAsString(dto);

        log.info("Modify Policy model criteria : {}", contentJson);

        mockMvc.perform(put("/v1/policyModel/mgmt/modify")
                .contentType(MediaType.APPLICATION_JSON).content(contentJson)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1001"));
    }
    
    @Test
    public void should3FindPolicyModel() throws Exception {
        log.info("Test --> /v1/policyModel/mgmt/{id}");

        SearchCriteria criteria = new SearchCriteria();
        criteria.setPageNo(0);
        criteria.setPageSize(10);
        Page<PolicyModel> policyModelPage = policyModelService
                .findByCriteria(criteria, false);

        PolicyModel policyModel = policyModelPage.getContent().get(0);

        mockMvc.perform(get("/v1/policyModel/mgmt/" + policyModel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1003"));
    }

    @Test
    public void should4RemovePolicyModel() throws Exception {
        log.info("Test --> Delete /v1/policyModel/mgmt/{id}");

        should1AddPolicyModel();

        SearchCriteria criteria = new SearchCriteria();
        criteria.setPageNo(0);
        criteria.setPageSize(10);
        Page<PolicyModel> policyModelPage = policyModelService
                .findByCriteria(criteria, false);

        assertEquals(3, policyModelPage.getContent().size());

        PolicyModel policyModel = policyModelPage.getContent().get(0);

        mockMvc.perform(delete("/v1/policyModel/mgmt/" + policyModel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1002"));

        policyModelPage = policyModelService.findByCriteria(criteria, false);

        assertEquals(2, policyModelPage.getContent().size());
    }

    @Test
    public void should5BulkDeletePolicyModel() throws Exception {
        log.info("Test --> Delete /v1/policyModel/mgmt/bulkDelete");

        SearchCriteria criteria = new SearchCriteria();
        criteria.setPageNo(0);
        criteria.setPageSize(10);
        Page<PolicyModel> policyModelPage = policyModelService
                .findByCriteria(criteria, false);

        List<Long> ids = new ArrayList<>();
        for (PolicyModel pm : policyModelPage.getContent()) {
            ids.add(pm.getId());
        }

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(ids);

        mockMvc.perform(delete("/v1/policyModel/mgmt/bulkDelete")
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1002"));

        policyModelPage = policyModelService.findByCriteria(criteria, false);

        assertEquals(0, policyModelPage.getContent().size());
    }

    // @Test
    // @Transactional
    // public void should6ModelClone() throws Exception {
    // log.info("Test --> GET /v1/policyModel/mgmt/clone/:id");
    //
    // SearchCriteria criteria = new SearchCriteria();
    // criteria.setPageNo(0);
    // criteria.setPageSize(10);
    // Page<PolicyModel> policyModelPage = policyModelService
    // .findByCriteria(criteria);
    //
    // Long id = policyModelPage.getContent().get(0).getId();
    //
    // mockMvc.perform(get("/v1/policyModel/mgmt/clone/" + id)
    // .contentType(MediaType.APPLICATION_JSON)
    // .accept(MediaType.APPLICATION_JSON)).andDo(print())
    // .andExpect(status().isOk())
    // .andExpect(
    // content().contentType("application/json;charset=UTF-8"))
    // .andExpect(jsonPath("$.statusCode").value("1000"));
    //
    // }

}
