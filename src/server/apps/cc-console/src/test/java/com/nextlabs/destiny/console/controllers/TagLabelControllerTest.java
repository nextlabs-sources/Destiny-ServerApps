/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 17, 2015
 *
 */
package com.nextlabs.destiny.console.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.nextlabs.destiny.console.config.TestConfiguration;
import com.nextlabs.destiny.console.config.TestElasticSearchConfig;
import com.nextlabs.destiny.console.config.root.MessageBundleResolveConfig;
import com.nextlabs.destiny.console.config.root.RootContextConfig;
import com.nextlabs.destiny.console.config.root.WebMvcConfig;
import com.nextlabs.destiny.console.config.servlet.ServletContextConfig;
import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.enums.TagType;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.services.TagLabelService;

/**
 *
 * Tag Label Controller unit testing
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
public class TagLabelControllerTest {

    private static final Logger log = LoggerFactory
            .getLogger(TagLabelControllerTest.class);

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private TagLabelService tagLabelService;

    private MockMvc mockMvc;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void shouldAddNewTagLabel() throws Exception {

        log.info("Test --> /v1/config/tags/add ");

        TagDTO tagDTO = new TagDTO();
        tagDTO.setKey("tag_key");
        tagDTO.setLabel("ITAR-v1.0");
        tagDTO.setStatus(Status.ACTIVE.name());
        tagDTO.setType(TagType.COMPONENT_TAG.name());

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(tagDTO);

        mockMvc.perform(post("/v1/config/tags/add")
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1000"));

        PageRequest pageable = PageRequest.of(0, 10);
        Page<TagLabel> tagLabelPage = tagLabelService
                .findByLabelStartWithAndType("ITAR-v1.0",
                        TagType.COMPONENT_TAG.name(), false, pageable);
        assertNotNull(tagLabelPage);

        assertEquals("ITAR-v1.0", tagLabelPage.getContent().get(0).getLabel());
    }

    @Test
    public void shouldNotAddNewTagLabel() throws Exception {

        log.info(
                "Test (shoud not with empty key/ label)--> /v1/config/tags/add ");

        TagDTO tagDTO = new TagDTO();
        tagDTO.setKey("");
        tagDTO.setLabel("");
        tagDTO.setStatus(Status.ACTIVE.name());
        tagDTO.setType(TagType.COMPONENT_TAG.name());

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(tagDTO);

        mockMvc.perform(post("/v1/config/tags/add")
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("4002"));

    }

    @Test
    public void shouldModifyTagLabel() throws Exception {

        log.info("Test --> /v1/config/tags/modify ");

        TagLabel tagLabel = new TagLabel(null, "tag_key_2", "GM-02",
                TagType.POLICY_TAG, Status.ACTIVE);
        tagLabel = tagLabelService.saveTag(tagLabel);

        TagDTO tagDTO = TagDTO.getDTO(tagLabel);
        tagDTO.setKey("tag_key_2");
        tagDTO.setLabel("GM-v1.1");
        tagDTO.setStatus(Status.ACTIVE.name());
        tagDTO.setType(TagType.POLICY_TAG.name());

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(tagDTO);

        mockMvc.perform(put("/v1/config/tags/modify")
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1001"));

        PageRequest pageable = PageRequest.of(0, 10);
        Page<TagLabel> tagLabelPage = tagLabelService
                .findByLabelStartWithAndType("GM", TagType.POLICY_TAG.name(),
                        false, pageable);
        assertNotNull(tagLabelPage);

        assertEquals("GM-v1.1", tagLabelPage.getContent().get(0).getLabel());

    }

    @Test
    public void shouldRemoveTagLabel() throws Exception {

        log.info("Test --> /v1/config/tags/remove/{id} ");

        TagLabel tagLabel = new TagLabel(null, "tag_key_2", "GM-03",
                TagType.POLICY_TAG, Status.ACTIVE);
        tagLabel = tagLabelService.saveTag(tagLabel);

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/v1/config/tags/remove/" + tagLabel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1002"));

        PageRequest pageable = PageRequest.of(0, 10);
        Page<TagLabel> tagLabelPage = tagLabelService
                .findByLabelStartWithAndType("GM-03", TagType.POLICY_TAG.name(),
                        false, pageable);

        Assert.assertEquals(0, tagLabelPage.getNumberOfElements());
    }

}
