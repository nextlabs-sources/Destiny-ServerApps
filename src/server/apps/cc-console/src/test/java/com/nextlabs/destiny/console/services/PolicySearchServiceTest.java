/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 24, 2015
 *
 */
package com.nextlabs.destiny.console.services;

import static com.nextlabs.destiny.console.enums.SavedSearchType.POLICY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;
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
import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.dto.common.TextSearchValue;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.enums.SearchFieldType;
import com.nextlabs.destiny.console.enums.SharedMode;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.model.SavedSearch;
import com.nextlabs.destiny.console.search.repositories.PolicySearchRepository;
import com.nextlabs.destiny.console.services.policy.PolicySearchService;

//import static org.junit.Assert.assertNotEquals;

/**
 *
 * JUnit Test for PolicySearchService
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestConfiguration.class,
        RootContextConfig.class, MessageBundleResolveConfig.class,
        TestElasticSearchConfig.class, ServletContextConfig.class })
public class PolicySearchServiceTest {

    private static final Logger log = LoggerFactory
            .getLogger(PolicySearchServiceTest.class);

    @Autowired
    private PolicySearchService policySearchService;

    @Autowired
    private SavedSearchService savedSearchService;

    @Resource
    private PolicySearchRepository policySearchRepository;

    @Test
    public void shouldSaveNewCriteria() throws Exception {

        log.info("Test --> should save new search criteria ");

        List<String> usersList = new ArrayList<String>();
        usersList.add("amila");
        usersList.add("duan");

        SavedSearch criteria = new SavedSearch(1L, "search_by_action",
                "Search criteria to search for policies by Action attribute",
                "find_policy_by_action", Status.ACTIVE, SharedMode.USERS,
                usersList, POLICY);

        SavedSearch saveCriteria = savedSearchService.saveCriteria(criteria);

        SavedSearch savedCriteria = savedSearchService
                .findById(saveCriteria.getId());
        assertNotNull(savedCriteria);
        assertNotNull(savedCriteria.getId());
        assertEquals(
                "Search criteria to search for policies by Action attribute",
                savedCriteria.getDesc());
        assertEquals("search_by_action", savedCriteria.getName());
        assertEquals("find_policy_by_action", savedCriteria.getCriteria());
    }

    @Test(expected = ConsoleException.class)
    public void shouldNotSaveNullCriteria() throws Exception {

        log.info("Test --> throw Console Exception if criteria is null ");

        SavedSearch criteria = null;

        savedSearchService.saveCriteria(criteria);
    }

    @Test
    public void shouldUpdateExistingCriteria() throws Exception {

        log.info("Test --> should update existing search criteria ");

        List<String> usersList = new ArrayList<String>();
        usersList.add("amila");
        usersList.add("duan");

        SavedSearch criteria = new SavedSearch(2L, "search_criteria_name",
                "Desc of the Search Criteria", "search_criteria", Status.ACTIVE,
                SharedMode.PUBLIC, usersList, POLICY);

        SavedSearch saveCriteria = savedSearchService.saveCriteria(criteria);

        SavedSearch savedCriteria = savedSearchService
                .findById(saveCriteria.getId());
        assertNotNull(savedCriteria);
        Long criteriaId = savedCriteria.getId();
        savedCriteria.setCriteria("modified_search_criteria");

        SavedSearch updateCriteria = savedSearchService
                .saveCriteria(savedCriteria);

        SavedSearch updatedCriteria = savedSearchService
                .findById(updateCriteria.getId());
        assertNotNull(updatedCriteria);
        assertNotNull(updatedCriteria.getId());
        assertEquals(criteriaId, updatedCriteria.getId());
        assertEquals("search_criteria_name", updatedCriteria.getName());
        //assertNotEquals("search_criteria", updatedCriteria.getCriteria());
        assertEquals("modified_search_criteria", updatedCriteria.getCriteria());
    }

    @Test
    public void shouldRemoveCriteria() throws Exception {

        log.info("Test --> should remove a search criteria ");

        List<String> usersList = new ArrayList<String>();
        usersList.add("amila");
        usersList.add("duan");

        SavedSearch criteria = new SavedSearch(3L,
                "search_criteria_to_remove_name",
                "Desc of the Search Criteria to be removed",
                "search_criteria_to_be_removed", Status.ACTIVE,
                SharedMode.PUBLIC, usersList, POLICY);

        SavedSearch saveCriteria = savedSearchService.saveCriteria(criteria);

        SavedSearch savedCriteria = savedSearchService
                .findById(saveCriteria.getId());
        assertNotNull(savedCriteria);
        Long criteriaId = savedCriteria.getId();

        savedSearchService.removeCriteria(criteriaId);

        SavedSearch removedCriteria = savedSearchService.findById(criteriaId);
        assertNull(removedCriteria);
    }

    @Test(expected = NoDataFoundException.class)
    public void shouldNotRemoveNonExistingCriteria() throws Exception {

        log.info("Test --> should not remove non existing criteria ");

        Long randomCriteriaId = 1111L;
        savedSearchService.removeCriteria(randomCriteriaId);
    }

    @Test
    public void shouldFindCriteriaByName() throws Exception {

        log.info("Test --> should search criteria by name ");

        String criteriaName = "search_by_action";

        PageRequest pageable = PageRequest.of(0, 10);
        Page<SavedSearch> criteriaPage = savedSearchService
                .findByNameOrDescriptionAndType(criteriaName, POLICY, pageable);

        assertNotNull(criteriaPage);
        assertTrue("Size of result list should be one ",
                criteriaPage.getContent().size() == 1);
        assertTrue("Criteria Id should be 1L",
                criteriaPage.getContent().get(0).getId() == 1L);
    }

    @Test
    public void shouldReIndexPolicyLite() throws Exception {

        log.info("Test --> policy lite object re-indexing ");
        policySearchService.reIndexAllPolicies();

        PageRequest pageable = PageRequest.of(0, 10);
        Page<PolicyLite> policyLitePage = policySearchRepository
                .findAll(pageable);

        assertNotNull(policyLitePage);
    }

    @Test
    public void shouldFindPoliciesByIds() throws Exception {

        log.info("Test --> find polices by given ids");

        PolicyLite p1 = createPolicyLite(100L, "Test/Pol-1",
                "Pol-1 description", "allow", "deployed", false);
        PolicyLite p2 = createPolicyLite(101L, "Test/Pol-2",
                "Pol-2 description", "allow", "deployed", false);
        PolicyLite p3 = createPolicyLite(220L, "Test/Pol-3",
                "Pol-3 description", "allow", "deployed", false);
        PolicyLite p4 = createPolicyLite(221L, "Test/Pol-4",
                "Pol-4 description", "allow", "deployed", false);
        PolicyLite p5 = createPolicyLite(201L, "Test/Pol-5",
                "Pol-5 description", "allow", "deployed", false);

        policySearchRepository.save(p1);
        policySearchRepository.save(p2);
        policySearchRepository.save(p3);
        policySearchRepository.save(p4);
        policySearchRepository.save(p5);

        PageRequest pageable = PageRequest.of(0, 10);
        Page<PolicyLite> policyLitePage = policySearchRepository
                .findAll(pageable);

        List<Long> ids = new ArrayList<>();
        ids.add(100L);
        ids.add(201L);
        ids.add(220L);
        ids.add(221L);

        List<SortField> sortFields = new ArrayList<>();
        sortFields.add(new SortField("name", SortField.ASC));

        policyLitePage = policySearchService.findPolicyByIds(ids, sortFields,
                pageable);

        assertNotNull(policyLitePage);
        assertTrue("Should find 4 matching policies",
                policyLitePage.getNumberOfElements() == 4);
    }

    @Test
    public void shouldFindFacetedPolicies() throws Exception {

        log.info("Test --> find faceted polices effectType");

        PolicyLite p1 = createPolicyLite(110L, "Test/Pol-12",
                "Pol-1 description", "allow", "deployed", false);
        PolicyLite p2 = createPolicyLite(111L, "Test/Pol-12",
                "Pol-2 description", "deny", "deployed", false);
        PolicyLite p3 = createPolicyLite(210L, "Test/Pol-13",
                "Pol-3 description", "deny", "deployed", false);
        PolicyLite p4 = createPolicyLite(211L, "Test/Pol-14",
                "Pol-4 description", "allow", "deployed", false);
        PolicyLite p5 = createPolicyLite(211L, "Test/Pol-15",
                "Pol-5 description", "Deny", "deployed", false);

        policySearchRepository.save(p1);
        policySearchRepository.save(p2);
        policySearchRepository.save(p3);
        policySearchRepository.save(p4);
        policySearchRepository.save(p5);

        SearchCriteria criteria = new SearchCriteria();
        criteria.setFacetField("effectType");
        FacetResult facet = policySearchService.findFacetByCriteria(criteria);
        assertNotNull(facet);
        assertEquals("Should have 2 terms", 2, facet.getTerms().size());
    }

    @Test
    public void shouldFindPolicyByCriteria() throws Exception {

        List<TagDTO> tags = new ArrayList<TagDTO>();
        tags.add(new TagDTO("tag_key_1", "SENSITIVE", "POLICY_TAG"));
        tags.add(new TagDTO("tag_key_2", "CONFIDENTIAL", "POLICY_TAG"));

        PolicyLite p1 = createPolicyLite(500L, "test_policy_1", "test_policy_1",
                "allow", "active", false);
        p1.getTags().addAll(tags);

        PolicyLite p2 = createPolicyLite(501L, "test_policy_2", "test_policy_2",
                "allow", "active", false);
        p2.getTags().addAll(tags);

        PolicyLite p3 = createPolicyLite(502L, "test_policy_3", "test_policy_3",
                "deny", "active", false);
        p3.getTags().addAll(tags);

        policySearchRepository.save(p1);
        policySearchRepository.save(p2);
        policySearchRepository.save(p3);

        SearchCriteria criteria = new SearchCriteria();
        criteria.getColumns().add("name");
        criteria.getColumns().add("description");
        criteria.getColumns().add("lastUpdatedDate");

        List<SortField> sortFields = new ArrayList<>();
        sortFields.add(new SortField("name", SortField.ASC));
        criteria.getSortFields().add(new SortField("name", SortField.ASC));

        SearchField field1 = new SearchField();
        field1.setField("name");
        field1.setType(SearchFieldType.SINGLE);
        field1.setValue(new StringFieldValue("test_policy"));
        criteria.getFields().add(field1);

        SearchField field2 = new SearchField();
        field2.setField("id");
        field2.setType(SearchFieldType.MULTI);
        List<String> field2Values = new ArrayList<String>();
        field2Values.add("500");
        field2Values.add("501");
        field2Values.add("502");
        StringFieldValue fieldValue = new StringFieldValue();
        fieldValue.setValue(field2Values);
        field2.setValue(fieldValue);
        criteria.getFields().add(field2);

        SearchField field3 = new SearchField();
        field3.setType(SearchFieldType.NESTED);
        field3.setField("tags");
        field3.setNestedField("tags.status");
        field3.setValue(new StringFieldValue("ACTIVE"));
        criteria.getFields().add(field3);

        SearchField field4 = new SearchField();
        field4.setType(SearchFieldType.NESTED_MULTI);
        field4.setField("tags");
        field4.setNestedField("tags.label");
        List<String> field4Values = new ArrayList<String>();
        field4Values.add("SENSITIVE");
        field4Values.add("CONFIDENTIAL");
        StringFieldValue field4Value = new StringFieldValue();
        field4Value.setValue(field4Values);
        field4.setValue(field4Value);
        criteria.getFields().add(field4);

        SearchField field5 = new SearchField();
        field5.setField("name or description");
        field5.setType(SearchFieldType.TEXT);
        TextSearchValue textSearchVal = new TextSearchValue();
        textSearchVal.setFields(new String[] { "name", "description" });
        textSearchVal.setValue("test_policy");
        field5.setValue(textSearchVal);
        criteria.getFields().add(field5);

        criteria.setPageNo(0);
        criteria.setPageSize(10);

        Page<PolicyLite> policyLitePage = policySearchService
                .findPolicyByCriteria(criteria);
        assertNotNull(policyLitePage);
        assertFalse("Policy Result List should not be empty",
                policyLitePage.getContent().isEmpty());
        assertTrue("Should find 3 matching policies",
                policyLitePage.getNumberOfElements() == 3);
    }

    @Test
    public void shouldFindPolicyBySingleValuedFields() throws Exception {

        List<TagDTO> tags = new ArrayList<TagDTO>();
        tags.add(new TagDTO("tag_key_3", "CLASSIFIED", "POLICY_TAG"));

        PolicyLite p1 = createPolicyLite(510L, "test/policies/policy1",
                "test/policies/policy1", "deny", "active", false);
        p1.getTags().addAll(tags);

        policySearchRepository.save(p1);

        SearchCriteria criteria = new SearchCriteria();
        criteria.getColumns().add("name");
        criteria.getColumns().add("description");
        criteria.getColumns().add("lastUpdatedDate");

        List<SortField> sortFields = new ArrayList<>();
        sortFields.add(new SortField("name", SortField.DESC));

        SearchField field1 = new SearchField();
        field1.setField("name");
        field1.setType(SearchFieldType.SINGLE);
        field1.setValue(new StringFieldValue("test/policies/"));
        criteria.getFields().add(field1);

        SearchField field2 = new SearchField();
        field2.setField("id");
        field2.setType(SearchFieldType.MULTI);
        List<String> field2Values = new ArrayList<String>();
        field2Values.add("510");
        StringFieldValue fieldValue = new StringFieldValue();
        fieldValue.setValue(field2Values);
        field2.setValue(fieldValue);
        criteria.getFields().add(field2);

        SearchField field3 = new SearchField();
        field3.setType(SearchFieldType.NESTED);
        field3.setField("tags");
        field3.setNestedField("tags.status");
        field3.setValue(new StringFieldValue("ACTIVE"));
        criteria.getFields().add(field3);

        SearchField field4 = new SearchField();
        field4.setType(SearchFieldType.NESTED_MULTI);
        field4.setField("tags");
        field4.setNestedField("tags.label");
        List<String> field4Values = new ArrayList<String>();
        field4Values.add("CLASSIFIED");
        StringFieldValue field4Value = new StringFieldValue();
        field4Value.setValue(field4Values);
        field4.setValue(field4Value);
        criteria.getFields().add(field4);

        SearchField field5 = new SearchField();
        field5.setField("name or description");
        field5.setType(SearchFieldType.TEXT);
        TextSearchValue textSearchVal = new TextSearchValue();
        textSearchVal.setFields(new String[] { "title", "description" });
        textSearchVal.setValue("test/policies/");
        field5.setValue(textSearchVal);
        criteria.getFields().add(field5);

        criteria.setPageNo(0);
        criteria.setPageSize(10);

        Page<PolicyLite> policyLitePage = policySearchService
                .findPolicyByCriteria(criteria);
        assertNotNull(policyLitePage);
        assertFalse("Policy Search Criteria Result List should not be empty",
                policyLitePage.getContent().isEmpty());
        assertTrue("Should find 1 matching policy",
                policyLitePage.getNumberOfElements() == 1);
    }

    @Test
    public void shouldReIndexPolicyCriteria() throws Exception {

        log.info("Test --> should reindex policy search criteria");

        SavedSearch criteria1 = new SavedSearch(505L, "search_by_action",
                "Search criteria to search for policies by Action attribute",
                "find_policy_by_action", Status.ACTIVE, SharedMode.PUBLIC, null,
                POLICY);

        SavedSearch criteria2 = new SavedSearch(506L, "search_by_type",
                "Search criteria to search for policies by Type",
                "find_policy_by_type", Status.ACTIVE, SharedMode.PUBLIC, null,
                POLICY);

        savedSearchService.saveCriteria(criteria1);
        savedSearchService.saveCriteria(criteria2);

        savedSearchService.reIndexAllCriteria();

        PageRequest pageable = PageRequest.of(0, 10);
        Page<SavedSearch> savedSearchPage = savedSearchService
                .findByNameOrDescriptionAndType("search_by_", POLICY, pageable);

        assertNotNull(savedSearchPage);
        assertFalse("Policy Search Criteria Result List should not be empty",
                savedSearchPage.getContent().isEmpty());
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
