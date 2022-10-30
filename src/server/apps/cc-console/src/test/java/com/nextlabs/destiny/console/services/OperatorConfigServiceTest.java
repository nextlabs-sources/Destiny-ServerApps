/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Dec 2, 2015
 *
 */
package com.nextlabs.destiny.console.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.nextlabs.destiny.console.config.TestConfiguration;
import com.nextlabs.destiny.console.config.TestElasticSearchConfig;
import com.nextlabs.destiny.console.config.root.MessageBundleResolveConfig;
import com.nextlabs.destiny.console.config.root.RootContextConfig;
import com.nextlabs.destiny.console.config.servlet.ServletContextConfig;
import com.nextlabs.destiny.console.enums.DataType;
import com.nextlabs.destiny.console.model.policy.OperatorConfig;
import com.nextlabs.destiny.console.services.policy.OperatorConfigService;

/**
 *
 * JUnit Test for Data Type and Operator Configuration Service
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
public class OperatorConfigServiceTest {

    @Autowired
    private OperatorConfigService configService;

    private static Logger log = LoggerFactory
            .getLogger(OperatorConfigServiceTest.class);

    @Test
    public void shouldSaveNewOperatorConfig() throws Exception {

        log.info("Test --> should save new operator configuration ");

        OperatorConfig opConfig = new OperatorConfig(null, "oper_equal",
                "equals", DataType.NUMBER);

        OperatorConfig saveOpConfig = configService.save(opConfig);

        OperatorConfig savedOpConfig = configService
                .findById(saveOpConfig.getId());
        assertNotNull(savedOpConfig);
        assertNotNull(savedOpConfig.getId());
        assertEquals("oper_equal", savedOpConfig.getKey());
        assertEquals(DataType.NUMBER, savedOpConfig.getDataType());
    }

    @Test
    public void shouldModifyOperatorConfig() throws Exception {

        log.info("Test --> should save new operator configuration ");

        OperatorConfig opConfig = new OperatorConfig(null, "oper_after",
                "After", DataType.DATE);

        OperatorConfig saveOpConfig = configService.save(opConfig);
        OperatorConfig savedOpConfig = configService
                .findById(saveOpConfig.getId());
        assertNotNull(savedOpConfig);
        Long opConfigId = savedOpConfig.getId();

        savedOpConfig.setLabel("On or After");

        configService.save(savedOpConfig);
        OperatorConfig updatedOpConfig = configService.findById(opConfigId);
        assertNotNull(updatedOpConfig);
        assertEquals(opConfigId, updatedOpConfig.getId());
        assertEquals("On or After", updatedOpConfig.getLabel());
        //assertNotEquals("After", updatedOpConfig.getLabel());
    }

    @Test
    public void shouldRemoveOperatorConfig() throws Exception {

        log.info("Test --> should remove operator configuration ");

        OperatorConfig opConfig = new OperatorConfig(null, "oper_in", "in",
                DataType.COLLECTION);

        OperatorConfig saveOpConfig = configService.save(opConfig);
        OperatorConfig savedOpConfig = configService
                .findById(saveOpConfig.getId());
        assertNotNull(savedOpConfig);
        Long opConfigId = savedOpConfig.getId();

        configService.removeOperatorConfig(opConfigId);
        OperatorConfig removedOpConfig = configService.findById(opConfigId);
        assertNull(removedOpConfig);
    }

    @Test
    public void shouldFindOperatorByDataType() throws Exception {

        log.info("Test --> should find operators by data type ");

        OperatorConfig opConfig1 = new OperatorConfig(null, "oper_is", "is",
                DataType.STRING);
        OperatorConfig opConfig2 = new OperatorConfig(null, "oper_isNot",
                "is not", DataType.STRING);

        configService.save(opConfig1);
        configService.save(opConfig2);

        List<OperatorConfig> operators = configService
                .findByDataType(DataType.STRING);
        //assertNotEquals("Result List should not be empty", operators.isEmpty());
        assertTrue("Should have 2 operators for STRING data type",
                operators.size() == 2);
        assertEquals("is", operators.get(0).getLabel());
        assertEquals("is not", operators.get(1).getLabel());
    }

    @Test
    public void shouldListAllOperators() throws Exception {

        log.info("Test --> should list all operators in the system ");

        OperatorConfig opConfig = new OperatorConfig(null, "oper_yes", "Yes",
                DataType.BOOLEAN);
        configService.save(opConfig);

        List<OperatorConfig> operators = configService.findAll();
        assertFalse("Result List should not be empty", operators.isEmpty());

        OperatorConfig foundConfig = null;

        for (OperatorConfig operator : operators) {
            if ("oper_yes".equalsIgnoreCase(operator.getKey())) {
                foundConfig = operator;
            }
        }

        assertNotNull(foundConfig);
        assertEquals("Yes", foundConfig.getLabel());
        assertEquals(DataType.BOOLEAN, foundConfig.getDataType());
    }

    @Test
    public void shouldFindAllDataTypesOperators() throws Exception {

        log.info(
                "Test --> should find all the data type operators in the system ");

        OperatorConfig booleanConfig = new OperatorConfig(null, "oper_yes",
                "Yes", DataType.BOOLEAN);
        OperatorConfig collectionConfig = new OperatorConfig(null, "oper_yes",
                "Yes", DataType.COLLECTION);
        OperatorConfig dateConfig = new OperatorConfig(null, "oper_yes", "Yes",
                DataType.DATE);
        OperatorConfig string1Config = new OperatorConfig(null, "oper_yes",
                "Yes", DataType.STRING);
        OperatorConfig string2Config = new OperatorConfig(null, "oper_yes",
                "Yes", DataType.STRING);
        OperatorConfig bool2Config = new OperatorConfig(null, "oper_yes", "Yes",
                DataType.BOOLEAN);
        OperatorConfig config = new OperatorConfig(null, "oper_yes", "Yes",
                DataType.NUMBER);

        configService.save(booleanConfig);
        configService.save(collectionConfig);
        configService.save(dateConfig);
        configService.save(string1Config);
        configService.save(string2Config);
        configService.save(bool2Config);
        configService.save(config);

        List<DataType> dataTypes = configService.findAllDataTypes();
        assertFalse("Result List should not be empty", dataTypes.isEmpty());

        assertNotNull(dataTypes);
        assertEquals(5, dataTypes.size());
    }
}
