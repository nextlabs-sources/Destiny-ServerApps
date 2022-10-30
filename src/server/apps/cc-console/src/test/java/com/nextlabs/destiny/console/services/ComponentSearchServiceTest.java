/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 6, 2016
 *
 */
package com.nextlabs.destiny.console.services;

import static com.nextlabs.destiny.console.enums.SavedSearchType.COMPONENT;
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
import com.nextlabs.destiny.console.dto.policymgmt.ComponentLite;
import com.nextlabs.destiny.console.enums.SearchFieldType;
import com.nextlabs.destiny.console.enums.SharedMode;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.SavedSearch;
import com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper;
import com.nextlabs.destiny.console.search.repositories.ComponentSearchRepository;
import com.nextlabs.destiny.console.services.policy.ComponentSearchService;

//import static org.junit.Assert.assertNotEquals;

/**
 *
 * JUnit Test for ComponentSearchService
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
public class ComponentSearchServiceTest {

    private static final Logger log = LoggerFactory
            .getLogger(PolicySearchServiceTest.class);

    @Autowired
    private ComponentSearchService componentSearchService;

    @Autowired
    private SavedSearchService savedSearchService;

    @Resource
    private ComponentSearchRepository componentSearchRepository;

    @Test
    public void shouldSaveNewCriteria() throws Exception {

        log.info("Test --> should save new search criteria ");

        List<String> usersList = new ArrayList<String>();
        usersList.add("amila");
        usersList.add("duan");

        SavedSearch criteria = new SavedSearch(null, "All User Components",
                "All User Components", "find all user components",
                Status.ACTIVE, SharedMode.PUBLIC, usersList, COMPONENT);

        SavedSearch saveCriteria = savedSearchService.saveCriteria(criteria);

        SavedSearch savedCriteria = savedSearchService
                .findById(saveCriteria.getId());
        assertNotNull(savedCriteria);
        assertNotNull(savedCriteria.getId());
        assertEquals("All User Components", savedCriteria.getDesc());
        assertEquals("All User Components", savedCriteria.getName());
        assertEquals("find all user components", savedCriteria.getCriteria());
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

        SavedSearch criteria = new SavedSearch(null, "search_criteria_name",
                "Desc of the Search Criteria", "search_criteria", Status.ACTIVE,
                SharedMode.PUBLIC, usersList, COMPONENT);

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

        SavedSearch criteria = new SavedSearch(null,
                "search_criteria_to_remove_name",
                "Desc of the Search Criteria to be removed",
                "search_criteria_to_be_removed", Status.ACTIVE,
                SharedMode.PUBLIC, usersList, COMPONENT);

        SavedSearch saveCriteria = savedSearchService.saveCriteria(criteria);

        SavedSearch savedCriteria = savedSearchService
                .findById(saveCriteria.getId());
        assertNotNull(savedCriteria);
        Long criteriaId = savedCriteria.getId();

        savedSearchService.removeCriteria(criteriaId);

        SavedSearch removedCriteria = savedSearchService.findById(criteriaId);
        assertNull(removedCriteria);
    }

    @Test
    public void shouldFindCriteriaByName() throws Exception {

        log.info("Test --> should search criteria by name ");

        SavedSearch criteria1 = new SavedSearch(null, "Component -A1",
                "Desc of Component -A1", "search_criteria_to_be_removed",
                Status.ACTIVE, SharedMode.PUBLIC, null, COMPONENT);

        savedSearchService.saveCriteria(criteria1);

        SavedSearch criteria2 = new SavedSearch(null, "Component -A2",
                "Desc of Component -A2", "search_criteria_to_be_removed",
                Status.ACTIVE, SharedMode.PUBLIC, null, COMPONENT);

        savedSearchService.saveCriteria(criteria2);

        SavedSearch criteria3 = new SavedSearch(null, "Com-A3",
                "Desc of Component -A3", "search_criteria_to_be_removed",
                Status.ACTIVE, SharedMode.PUBLIC, null, COMPONENT);

        savedSearchService.saveCriteria(criteria3);

        String criteriaName = "Com-A3";

        PageRequest pageable = PageRequest.of(0, 10);
        Page<SavedSearch> criteriaPage = savedSearchService
                .findByNameOrDescriptionAndType(criteriaName, COMPONENT,
                        pageable);

        assertNotNull(criteriaPage);
        assertTrue("Size of result list should be one ",
                criteriaPage.getContent().size() == 1);
        assertEquals("Com-A3", criteriaPage.getContent().get(0).getName());
    }

    @Test
    public void shouldReIndexComponentLite() throws Exception {

        log.info("Test --> component lite object re-indexing ");
        componentSearchService.reIndexAllComponents();

        PageRequest pageable = PageRequest.of(0, 10);
        Page<ComponentLite> componentLitePage = componentSearchRepository
                .findAll(pageable);

        assertNotNull(componentLitePage);
    }

    @Test
    public void shouldFindComponentsByIds() throws Exception {

        log.info("Test --> find components by given ids");

        ComponentLite c1 = createComponentLite(100L, "Test/Pol-1",
                "Pol-1 description", "deployed", "RESOURCE");
        ComponentLite c2 = createComponentLite(101L, "Test/Pol-2",
                "Pol-2 description", "deployed", "RESOURCE");
        ComponentLite c3 = createComponentLite(220L, "Test/Pol-3",
                "Pol-3 description", "deployed", "RESOURCE");
        ComponentLite c4 = createComponentLite(221L, "Test/Pol-4",
                "Pol-4 description", "deployed", "RESOURCE");
        ComponentLite c5 = createComponentLite(201L, "Test/Pol-5",
                "Pol-5 description", "deployed", "RESOURCE");

        componentSearchRepository.save(c1);
        componentSearchRepository.save(c2);
        componentSearchRepository.save(c3);
        componentSearchRepository.save(c4);
        componentSearchRepository.save(c5);

        PageRequest pageable = PageRequest.of(0, 10);
        Page<ComponentLite> componentLitePage = componentSearchRepository
                .findAll(pageable);

        List<Long> ids = new ArrayList<>();
        ids.add(100L);
        ids.add(201L);
        ids.add(220L);
        ids.add(221L);

        List<SortField> sortFields = new ArrayList<>();
        sortFields.add(new SortField("name", SortField.ASC));

        componentLitePage = componentSearchService.findComponentsByIds(ids,
                sortFields, pageable);

        assertNotNull(componentLitePage);
        assertTrue("Should find 4 matching policies",
                componentLitePage.getNumberOfElements() == 4);
    }

    @Test
    public void shouldFindFacetedComponents() throws Exception {

        log.info("Test --> find faceted components by group");

        ComponentLite c1 = createComponentLite(110L, "Test/Pol-1",
                "Pol-1 description", "deployed", "RESOURCE");
        ComponentLite c2 = createComponentLite(111L, "Test/Pol-2",
                "Pol-2 description", "deployed", "RESOURCE");
        ComponentLite c3 = createComponentLite(210L, "Test/Pol-3",
                "Pol-3 description", "deployed", "ACTION");
        ComponentLite c4 = createComponentLite(214L, "Test/Pol-4",
                "Pol-4 description", "deployed", "SUBJECT");
        ComponentLite c5 = createComponentLite(211L, "Test/Pol-5",
                "Pol-5 description", "deployed", "ACTION");

        componentSearchRepository.save(c1);
        componentSearchRepository.save(c2);
        componentSearchRepository.save(c3);
        componentSearchRepository.save(c4);
        componentSearchRepository.save(c5);

        SearchCriteria criteria = new SearchCriteria();
        criteria.setFacetField("group");

        FacetResult facetResult = componentSearchService
                .findFacetByCriteria(criteria);

        assertNotNull(facetResult);
        assertEquals("Should have 3 Groups", 3, facetResult.getTerms().size());
    }

    @Test
    public void shouldFindComponentsByGroup() throws Exception {
        
        List<TagDTO> tags = new ArrayList<TagDTO>();
        tags.add(new TagDTO("tag_key_1", "SENSITIVE", "POLICY_TAG"));
        tags.add(new TagDTO("tag_key_2", "CONFIDENTIAL", "POLICY_TAG"));

        ComponentLite c1 = createComponentLite(500L, "Test_Comp_1",
                "Comp-1 description", "deployed", "RESOURCE");
        c1.setFullName("Test_Comp_1");
        c1.setGroup(ComponentPQLHelper.ACTION_GROUP);
        c1.setTags(tags);

        componentSearchRepository.save(c1);
        
        ComponentLite saved = componentSearchRepository.save(c1);
        assertNotNull(saved.getId());
        assertEquals("ACTION",saved.getGroup());
        
        String groupType = "ACTION";

        PageRequest pageable = PageRequest.of(0, 10);
        Page<ComponentLite> compLitePage = componentSearchService
                .findComponentsByGroupAndType(groupType, "", pageable, false);

        assertNotNull(compLitePage);
       /* assertFalse("Components Result List should not be empty",
                compLitePage.getContent().isEmpty());
        assertTrue("Should find 1 matching component",
                compLitePage.getNumberOfElements() == 1);*/
        
    }
    
    @Test
    public void shouldFindComponentsByGroupAndType() throws Exception {
        
        List<TagDTO> tags = new ArrayList<TagDTO>();
        tags.add(new TagDTO("tag_key_1", "SENSITIVE", "POLICY_TAG"));
        tags.add(new TagDTO("tag_key_2", "CONFIDENTIAL", "POLICY_TAG"));

        ComponentLite c1 = createComponentLite(500L, "Test_Comp_1",
                "Comp-1 description", "deployed", "RESOURCE");
        c1.setFullName("Test_Comp_1");
        c1.setGroup(ComponentPQLHelper.RESOURCE_GROUP);
        c1.setTags(tags);
        c1.setModelType("MOD");

        ComponentLite saved = componentSearchRepository.save(c1);
        assertNotNull(saved.getId());
        assertEquals("RESOURCE",saved.getGroup());
        assertEquals("MOD",saved.getModelType());
        
        String groupType = "RESOURCE";
        String modelType = "MOD";

        PageRequest pageable = PageRequest.of(0, 10);
        Page<ComponentLite> compLitePage = componentSearchService
                .findComponentsByGroupAndType(groupType, modelType, pageable, false);

        assertNotNull(compLitePage);
        /*assertFalse("Components Result List should not be empty",
                compLitePage.getContent().isEmpty());
        assertTrue("Should find 1 matching component",
                compLitePage.getNumberOfElements() == 1);*/
    }
    
    
    @Test
    public void shouldFindComponentsByCriteria() throws Exception {

        List<TagDTO> tags = new ArrayList<TagDTO>();
        tags.add(new TagDTO("tag_key_1", "SENSITIVE", "POLICY_TAG"));
        tags.add(new TagDTO("tag_key_2", "CONFIDENTIAL", "POLICY_TAG"));

        ComponentLite c1 = createComponentLite(500L, "Test_Comp_1",
                "Comp-1 description", "deployed", "RESOURCE");
        c1.setFullName("Test_Comp_1");
        c1.setGroup(ComponentPQLHelper.RESOURCE_GROUP);
        c1.setTags(tags);

        ComponentLite c2 = createComponentLite(501L, "Test_Comp_2",
                "Comp-2 description", "deployed", "RESOURCE");
        c2.setFullName("Test_Comp_2");
        c2.setGroup(ComponentPQLHelper.RESOURCE_GROUP);
        c2.setTags(tags);

        ComponentLite c3 = createComponentLite(502L, "Test_Comp_3",
                "Comp-3 description", "deployed", "RESOURCE");
        c3.setFullName("Test_Comp_3");
        c3.setGroup(ComponentPQLHelper.RESOURCE_GROUP);
        c3.setTags(tags);

        componentSearchRepository.save(c1);
        componentSearchRepository.save(c2);
        componentSearchRepository.save(c3);

        SearchCriteria criteria = new SearchCriteria();
        criteria.getColumns().add("name");
        criteria.getColumns().add("fullName");
        criteria.getColumns().add("description");
        criteria.getColumns().add("group");
        criteria.getColumns().add("lastUpdatedDate");

        List<SortField> sortFields = new ArrayList<>();
        sortFields.add(new SortField("name", SortField.ASC));
        criteria.getSortFields().add(new SortField("name", SortField.ASC));

        SearchField field1 = new SearchField();
        field1.setField("name");
        field1.setType(SearchFieldType.SINGLE);
        field1.setValue(new StringFieldValue("Test_Comp"));
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
        field5.setField("name or fullName");
        field5.setType(SearchFieldType.TEXT);
        TextSearchValue textSearchVal = new TextSearchValue();
        textSearchVal.setFields(new String[] { "name", "fullName" });
        textSearchVal.setValue("Test_Comp");
        field5.setValue(textSearchVal);
        criteria.getFields().add(field5);

        criteria.setPageNo(0);
        criteria.setPageSize(10);

        Page<ComponentLite> compLitePage = componentSearchService
                .findByCriteria(criteria, false);

        assertNotNull(compLitePage);
        assertFalse("Components Result List should not be empty",
                compLitePage.getContent().isEmpty());
        assertTrue("Should find 3 matching components",
                compLitePage.getNumberOfElements() == 3);

    }

    private ComponentLite createComponentLite(Long id, String name, String desc,
            String status, String group) {
        ComponentLite component = new ComponentLite();

        component.setId(id);
        component.setStatus(status);
        component.setName(name);
        component.setDescription(desc);
        component.setGroup(group);

        return component;
    }

}
