/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 17, 2015
 *
 */
package com.nextlabs.destiny.console.controllers.policymgmt;

import static com.nextlabs.destiny.console.enums.SavedSearchType.POLICY;
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

import javax.annotation.Resource;

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
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.dto.policymgmt.SavedSearchDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SearchCriteriaDTO;
import com.nextlabs.destiny.console.enums.DateOption;
import com.nextlabs.destiny.console.enums.SavedSearchType;
import com.nextlabs.destiny.console.enums.SearchFieldType;
import com.nextlabs.destiny.console.enums.SharedMode;
import com.nextlabs.destiny.console.model.SavedSearch;
import com.nextlabs.destiny.console.search.repositories.PolicySearchRepository;
import com.nextlabs.destiny.console.services.SavedSearchService;

/**
 *
 * Policy Search Controller unit testing
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
public class PolicySearchControllerTest {

    private static final Logger log = LoggerFactory
            .getLogger(PolicySearchControllerTest.class);

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private SavedSearchService savedSearchService;

    @Resource
    private PolicySearchRepository policySearchRepository;

    private MockMvc mockMvc;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void shouldSearchPolicyByCriteria() throws Exception {

        log.info("Test --> /v1/policy/search ");

        SearchCriteria criteria = new SearchCriteria();
        criteria.getColumns().add("title");
        criteria.getColumns().add("description");
        criteria.getColumns().add("createdBy");

        SearchField field1 = new SearchField();
        field1.setField("title");
        field1.setType(SearchFieldType.SINGLE);
        field1.setValue(new StringFieldValue("ITAR"));

        SearchField field2 = new SearchField();
        field2.setField("lastModified");
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

        mockMvc.perform(post("/v1/policy/search")
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1004"));

    }

    @Test
    public void shouldSearchPolicyByIds() throws Exception {

        log.info("Test --> /v1/policy/search/ids ");

        log.info("Test --> find polices by given ids");

        PolicyLite p1 = createPolicyLite(110L, "Test/Pol-110L",
                "Pol-1 description", "allow", "deployed", false);
        PolicyLite p2 = createPolicyLite(2012L, "Test/Pol-2012L",
                "Pol-2 description", "allow", "deployed", false);
        PolicyLite p3 = createPolicyLite(302L, "Test/Pol-302L",
                "Pol-3 description", "allow", "deployed", false);
        PolicyLite p4 = createPolicyLite(401L, "Test/Pol-401L",
                "Pol-4 description", "allow", "deployed", false);
        PolicyLite p5 = createPolicyLite(231L, "Test/Pol-231L",
                "Pol-5 description", "allow", "deployed", false);

        policySearchRepository.save(p1);
        policySearchRepository.save(p2);
        policySearchRepository.save(p3);
        policySearchRepository.save(p4);
        policySearchRepository.save(p5);

        SearchCriteria criteria = new SearchCriteria();

        SearchField field1 = new SearchField();
        field1.setField("id");
        field1.setType(SearchFieldType.MULTI);
        field1.setValue(new StringFieldValue(
                new String[] { "110", "2012", "302", "401" }));

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

        mockMvc.perform(post("/v1/policy/search/ids")
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1004"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    public void shouldSavePolicyCriteria() throws Exception {

        log.info("Test --> /v1/policy/search/add ");

        SearchCriteria criteria = new SearchCriteria();
        criteria.getColumns().add("title");
        criteria.getColumns().add("description");
        criteria.getColumns().add("createdBy");

        SearchField field1 = new SearchField();
        field1.setField("title");
        field1.setType(SearchFieldType.SINGLE);
        field1.setValue(new StringFieldValue("ITAR"));

        SearchField field2 = new SearchField();
        field2.setField("lastModified");
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
        searchDTO.setName("My Favorite 1");
        searchDTO.setDesc("My Favorite 1 desc");
        searchDTO.setSharedMode(SharedMode.USERS.name());
        searchDTO.getUserIds().add("amilasilva88@gmail.com");
        searchDTO.getUserIds().add("test_console@gmail.com");
        searchDTO.setCriteria(criteria);

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(searchDTO);

        mockMvc.perform(post("/v1/policy/search/add")
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1000"));

        PageRequest pageable = PageRequest.of(0, 10);
        Page<SavedSearch> savedSearchPage = savedSearchService
                .findByNameOrDescriptionAndType("My Favorite", POLICY,
                        pageable);

        assertNotNull(savedSearchPage);
        assertEquals(3, savedSearchPage.getContent().get(0).criteriaModel()
                .getColumns().size());
        assertEquals(2, savedSearchPage.getContent().get(0).criteriaModel()
                .getFields().size());
    }

    @Test
    public void shouldModifyPolicyCriteria() throws Exception {

        log.info("Test --> /v1/policy/search/modify ");

        SearchCriteria criteria = new SearchCriteria();
        criteria.getColumns().add("title");
        criteria.getColumns().add("description");
        criteria.getColumns().add("createdBy");

        SearchField field1 = new SearchField();
        field1.setField("title");
        field1.setType(SearchFieldType.MULTI);
        field1.setValue(new StringFieldValue(new String[] { "ITAR", "AXA" }));

        SearchField field2 = new SearchField();
        field2.setField("lastModified");
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
        search.setName("ITAR DOCS Allow Search");
        search.setDesc("ITAR DOCS Allow Search");
        search.setSharedMode(SharedMode.USERS);
        search.setType(SavedSearchType.POLICY);
        search.getUserIds().add("amilasilva88@gmail.com");
        search.getUserIds().add("test_console@gmail.com");
        search.setCriteria(criteriaJson);

        search = savedSearchService.saveCriteria(search);

        SavedSearchDTO searchDTO = SavedSearchDTO.getDTO(search);
        searchDTO.setName("ITAR HELLO v1.1");
        searchDTO.setDesc("ITAR HELLO v1.1");
        searchDTO.setSharedMode(SharedMode.PUBLIC.name());
        searchDTO.getUserIds().clear();

        String content = mapper.writeValueAsString(searchDTO);

        mockMvc.perform(put("/v1/policy/search/modify")
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1001"));

        PageRequest pageable = PageRequest.of(0, 10);
        Page<SavedSearch> savedSearchPage = savedSearchService
                .findByNameOrDescriptionAndType("ITAR HELLO", POLICY, pageable);

        assertNotNull(savedSearchPage);
        assertEquals(3, savedSearchPage.getContent().get(0).criteriaModel()
                .getColumns().size());
        assertEquals(2, savedSearchPage.getContent().get(0).criteriaModel()
                .getFields().size());
        assertEquals(0,
                savedSearchPage.getContent().get(0).getUserIds().size());
        assertEquals(SharedMode.PUBLIC.name(),
                savedSearchPage.getContent().get(0).getSharedMode().name());
    }

    @Test
    public void shouldFindPolicyCriteria() throws Exception {

        log.info("Test --> /v1/policy/search/saved/:id ");

        SearchCriteria criteria = new SearchCriteria();
        criteria.getColumns().add("title");
        criteria.getColumns().add("description");
        criteria.getColumns().add("createdBy");

        SearchField field1 = new SearchField();
        field1.setField("title");
        field1.setType(SearchFieldType.SINGLE);
        field1.setValue(new StringFieldValue("ITAR"));

        SearchField field2 = new SearchField();
        field2.setField("lastModified");
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
        search.setName("ITAR DOCS Allow Search");
        search.setDesc("ITAR DOCS Allow Search");
        search.setSharedMode(SharedMode.USERS);
        search.setType(POLICY);
        search.getUserIds().add("amilasilva88@gmail.com");
        search.getUserIds().add("test_console@gmail.com");
        search.setCriteria(criteriaJson);

        search = savedSearchService.saveCriteria(search);

        mockMvc.perform(get("/v1/policy/search/saved/" + search.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1003"))
                .andExpect(jsonPath("$.data.name").value(search.getName()));

        log.info("Test --> /v1/policy/search/savedlist/:name ");

        mockMvc.perform(get("/v1/policy/search/savedlist/ITAR DOCS")
                .param("pageNo", "0").param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1003"))
                .andExpect(jsonPath("$.data").isNotEmpty());

        mockMvc.perform(get("/v1/policy/search/savedlist/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1003"));
    }

    @Test
    public void shouldNotFindPolicyCriteria() throws Exception {

        log.info("Test --> /v1/policy/search/saved/:id ");

        mockMvc.perform(get("/v1/policy/search/saved/5005")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("5000"));

        mockMvc.perform(get("/v1/policy/search/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().is4xxClientError());

        log.info("Test --> /v1/policy/search/savedlist/:name ");

        mockMvc.perform(get("/v1/policy/search/savedlist/Nextlabs")
                .param("pageNo", "0").param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("5000"));

        log.info("Test --> /v1/policy/search/remove/:id ");

        mockMvc.perform(delete("/v1/policy/search/remove/5005")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("5002"));

        mockMvc.perform(delete("/v1/policy/search/remove/ ")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetSearchFormFields() throws Exception {

        log.info("Test --> /v1/policy/search/fields ");
        mockMvc.perform(get("/v1/policy/search/fields")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1004"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    public void shouldRemovePolicyCriteria() throws Exception {

        log.info("Test --> /v1/policy/search/remove ");

        SearchCriteria criteria = new SearchCriteria();
        criteria.getColumns().add("title");
        criteria.getColumns().add("description");
        criteria.getColumns().add("createdBy");

        SearchField field1 = new SearchField();
        field1.setField("title");
        field1.setType(SearchFieldType.SINGLE);
        field1.setValue(new StringFieldValue("ITAR"));

        SearchField field2 = new SearchField();
        field2.setField("lastModified");
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
        search.setName("ITAR KELLY");
        search.setDesc("ITAR KELLY");
        search.setSharedMode(SharedMode.USERS);
        search.getUserIds().add("amilasilva88@gmail.com");
        search.getUserIds().add("test_console@gmail.com");
        search.setCriteria(criteriaJson);

        search = savedSearchService.saveCriteria(search);

        mockMvc.perform(delete("/v1/policy/search/remove/" + search.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.statusCode").value("1002"));

        PageRequest pageable = PageRequest.of(0, 10);
        Page<SavedSearch> savedSearchPage = savedSearchService
                .findByNameOrDescriptionAndType("KELLY", POLICY, pageable);

        assertEquals(0, savedSearchPage.getNumberOfElements());
    }

    private PolicyLite createPolicyLite(Long id, String title, String desc,
            String effectType, String status, boolean hasParent) {
        PolicyLite policy = new PolicyLite();
        policy.setId(id);
        policy.setEffectType(effectType);
        policy.setStatus(status);
        policy.setName(title);
        policy.setDescription(desc);
        policy.setHasParent(hasParent);

        return policy;
    }

}
