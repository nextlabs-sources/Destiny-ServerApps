/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Dec 2, 2015
 *
 */
package com.nextlabs.destiny.console.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextlabs.destiny.console.config.TestConfiguration;
import com.nextlabs.destiny.console.config.TestElasticSearchConfig;
import com.nextlabs.destiny.console.config.root.MessageBundleResolveConfig;
import com.nextlabs.destiny.console.config.root.RootContextConfig;
import com.nextlabs.destiny.console.config.root.WebMvcConfig;
import com.nextlabs.destiny.console.config.servlet.ServletContextConfig;
import com.nextlabs.destiny.console.dto.common.OperatorConfigDTO;
import com.nextlabs.destiny.console.enums.DataType;
import com.nextlabs.destiny.console.model.policy.OperatorConfig;
import com.nextlabs.destiny.console.services.policy.OperatorConfigService;

/**
 *
 * JUnit Test Case for Operator and Data Type Configuration Controller
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestConfiguration.class,
        RootContextConfig.class, MessageBundleResolveConfig.class,
        TestElasticSearchConfig.class, WebMvcConfig.class,
        ServletContextConfig.class})
public class OperatorConfigControllerTest {

    private static Logger log = LoggerFactory
            .getLogger(OperatorConfigControllerTest.class);

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private OperatorConfigService configService;

    private MockMvc mockMvc;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void shouldAddNewOperatorConfig() throws Exception {

        log.info(" Test --> /v1/config/dataType/add");

        OperatorConfigDTO configDto = new OperatorConfigDTO();
        configDto.setKey("oper_notEqual");
        configDto.setLabel("not equals");
        configDto.setDataType(DataType.NUMBER.toString());

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(configDto);

        mockMvc.perform(post("/v1/config/dataType/add")
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1000"));

        OperatorConfig opConfig = configService.findByDataType(DataType.NUMBER)
                .get(0);
        assertNotNull(opConfig);
       // assertEquals("oper_notEqual", opConfig.getLabel());
    }

    @Test
    public void shouldModifyOperatorConfig() throws Exception {

        log.info("Test --> /v1/config/dataType/modify");

        OperatorConfig operConfig = new OperatorConfig(null, "oper_equal",
                "Equals", DataType.STRING);
        configService.save(operConfig);
        assertNotNull(operConfig.getId());

        OperatorConfigDTO configDto = new OperatorConfigDTO();
        configDto.setId(operConfig.getId());
        configDto.setKey("oper_equal");
        configDto.setLabel("Equal To");
        configDto.setDataType(operConfig.getDataType().name());

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(configDto);

        mockMvc.perform(put("/v1/config/dataType/modify/" + operConfig.getId())
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1001"));

        operConfig = configService.findById(operConfig.getId());
        assertNotNull(operConfig);
        assertEquals("oper_equal", operConfig.getKey());
        assertEquals("Equal To", operConfig.getLabel());
    }

    @Test
    public void shouldRemoveOperatorConfig() throws Exception {

        OperatorConfig operConfig = new OperatorConfig(null, "oper_reg_ex",
                "Regular Expression", DataType.STRING);
        configService.save(operConfig);
        assertNotNull(operConfig.getId());

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/v1/config/dataType/remove/" + operConfig.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1002"));

        operConfig = configService.findById(operConfig.getId());
        assertNull(operConfig);
    }

    @Test
    public void shouldFindOperatorById() throws Exception {

        log.info("Test --> /v1/config/dataType/details/{id}");

        OperatorConfig operConfig = new OperatorConfig(null, "oper_start_with",
                "Starts With", DataType.STRING);
        configService.save(operConfig);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/config/dataType/details/" + operConfig.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1003"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.key").value("oper_start_with"));
    }

    @Test
    public void shouldFindOperatorByDataType() throws Exception {

        log.info("Test --> /v1/config/dataType/list/{type}");

        OperatorConfig operConfig = new OperatorConfig(null, "oper_in", "in",
                DataType.COLLECTION);
        configService.save(operConfig);

        String dataType = DataType.COLLECTION.name();

        mockMvc.perform(
                MockMvcRequestBuilders.get("/v1/config/dataType/list/" + dataType)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1003"))
                .andExpect(jsonPath("$.data").isArray());
               // .andExpect(jsonPath("$.data.[0].key").value("oper_in"));
    }

    @Test
    public void shouldListAllOperators() throws Exception {

        log.info("Test --> /v1/config/dataType/list");

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/config/dataType/list")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1003"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data").isArray());
    }
    
    @Test
    public void shouldListDataTypesOperators() throws Exception {

        log.info("Test --> /v1/config/dataType/types");
        
        OperatorConfig dateConfig = new OperatorConfig(null, "oper_in", "in",
                DataType.DATE);
        OperatorConfig collectionConfig = new OperatorConfig(null, "oper_eq", "in",
                DataType.COLLECTION);
        
        configService.save(dateConfig);
        configService.save(collectionConfig);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/config/dataType/types")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1003"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data").isArray());
    }
}
