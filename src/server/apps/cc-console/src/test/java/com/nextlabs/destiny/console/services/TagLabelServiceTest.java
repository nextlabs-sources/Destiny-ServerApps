/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 20, 2015
 *
 */
package com.nextlabs.destiny.console.services;

import static com.nextlabs.destiny.console.enums.Status.ACTIVE;
import static com.nextlabs.destiny.console.enums.TagType.POLICY_TAG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

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
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.enums.TagType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.search.repositories.TagLabelSearchRepository;

//import static org.junit.Assert.assertNotEquals;

/**
 *
 * JUnit Test for TagLabelServiceImpl
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
public class TagLabelServiceTest {

    private static final Logger log = LoggerFactory
            .getLogger(TagLabelServiceTest.class);

    @Autowired
    private TagLabelService tagLevelService;

    @Resource
    private TagLabelSearchRepository tagLabelSearchRepository;

    @Test
    public void shouldSaveNewTag() throws Exception {

        log.info("Test --> should save a new tag ");

        TagLabel tagLabel = new TagLabel();
        tagLabel.setKey("tag_key_1");
        tagLabel.setLabel("ITAR-v1.1");
        tagLabel.setStatus(Status.ACTIVE);
        tagLabel.setType(TagType.POLICY_TAG);

        TagLabel saveTag = tagLevelService.saveTag(tagLabel);

        TagLabel savedTag = tagLevelService.findById(saveTag.getId());
        assertNotNull(savedTag);
        assertNotNull(savedTag.getId());
        assertEquals("ITAR-v1.1", savedTag.getLabel());
    }

    @Test(expected = ConsoleException.class)
    public void shouldNotSaveNullTag() throws Exception {

        log.info("Test --> should not save null tag ");

        TagLabel tagLabel = null;
        tagLevelService.saveTag(tagLabel);
    }

    @Test
    public void shouldUpdateExistingTag() throws Exception {

        log.info("Test --> should update an existing tag ");

        TagLabel tagLabel = new TagLabel();
        tagLabel.setKey("tag_key_2");
        tagLabel.setLabel("ITAR-v1.2.0");
        tagLabel.setStatus(Status.ACTIVE);
        tagLabel.setType(TagType.POLICY_TAG);

        TagLabel saveTag = tagLevelService.saveTag(tagLabel);

        TagLabel savedTag = tagLevelService.findById(saveTag.getId());
        savedTag.setLabel("ITAR-v1.2.1");

        TagLabel updateTag = tagLevelService.saveTag(savedTag);

        TagLabel updatedTag = tagLevelService.findById(updateTag.getId());
        assertNotNull(updatedTag);
        assertNotNull(updatedTag.getId());
        assertEquals("ITAR-v1.2.1", updatedTag.getLabel());
        //assertNotEquals("ITAR-v1.2.0", updatedTag.getLabel());
    }

    @Test
    public void shouldFindTagByType() throws Exception {

        log.info("Test --> should find tag by type ");

        TagLabel tagLabel = new TagLabel();
        tagLabel.setKey("tag_key_5");
        tagLabel.setLabel("ITAR-v1.5");
        tagLabel.setStatus(Status.ACTIVE);
        tagLabel.setType(TagType.POLICY_TAG);

        TagLabel saveTag = tagLevelService.saveTag(tagLabel);

        TagLabel savedTag = tagLevelService.findById(saveTag.getId());
        TagType type = savedTag.getType();
        String tagType = type.toString();

        PageRequest pageable = PageRequest.of(0, 10);
        Page<TagLabel> tagsList = tagLevelService.findByType(tagType, false,
                pageable);
        assertNotNull(tagsList);
        assertFalse("Tag Labels Result List should not be empty",
                tagsList.getContent().isEmpty());
        assertTrue("Should have at least one element",
                tagsList.getNumberOfElements() > 0);
    }

    @Test
    public void shouldFindTagByLabelAndType() throws Exception {

        log.info("Test --> should find tag by label and type ");

        TagLabel tagLabel = new TagLabel();
        tagLabel.setKey("tag_key_6");
        tagLabel.setLabel("ITAR-v1.6");
        tagLabel.setStatus(Status.ACTIVE);
        tagLabel.setType(TagType.POLICY_TAG);

        TagLabel saveTag = tagLevelService.saveTag(tagLabel);

        TagLabel savedTag = tagLevelService.findById(saveTag.getId());
        TagType type = savedTag.getType();
        String tagType = type.toString();
        String label = savedTag.getLabel();

        PageRequest pageable = PageRequest.of(0, 10);
        Page<TagLabel> tagsList = tagLevelService
                .findByLabelStartWithAndType(label, tagType, false, pageable);
        assertNotNull(tagsList);
        assertFalse("Tag Labels Result List should not be empty",
                tagsList.getContent().isEmpty());
        assertTrue("Should have at least one element",
                tagsList.getNumberOfElements() > 0);
    }

    @Test
    public void shouldRemoveTag() throws Exception {

        log.info("Test --> should remove an existing tag");

        TagLabel tagLabel = new TagLabel();
        tagLabel.setKey("tag_key_7");
        tagLabel.setLabel("ITAR-v1.7");
        tagLabel.setStatus(Status.ACTIVE);
        tagLabel.setType(TagType.POLICY_TAG);

        TagLabel saveTag = tagLevelService.saveTag(tagLabel);

        TagLabel savedTag = tagLevelService.findById(saveTag.getId());
        Long savedTagId = savedTag.getId();

        tagLevelService.removeTag(savedTagId);

        TagLabel removedTag = tagLevelService.findById(savedTagId);
        assertNull(removedTag);
    }

    @Test
    public void shouldReindexTags() throws Exception {

        log.info("Test --> should reindex tags");

        TagLabel tagLabel1 = new TagLabel("tag_key_8", "ITAR-v1.8", POLICY_TAG,
                ACTIVE);
        TagLabel tagLabel2 = new TagLabel("tag_key_10", "ITAR-v1.10",
                POLICY_TAG, ACTIVE);
        tagLevelService.saveTag(tagLabel1);
        tagLevelService.saveTag(tagLabel2);

        tagLevelService.reIndexAllTags();

        PageRequest pageable = PageRequest.of(0, 10);
        Page<TagLabel> tagLabels = tagLabelSearchRepository.findAll(pageable);

        assertNotNull(tagLabels);
        assertFalse("Tag Labels Result List should not be empty",
                tagLabels.getContent().isEmpty());
    }

    @Test(expected = ConsoleException.class)
    public void shouldNotRemoveNonExistingTag() throws Exception {

        log.info("Test --> should throw Console Exception ");

        Long tagId = 1111L;

        tagLevelService.removeTag(tagId);
    }

}
