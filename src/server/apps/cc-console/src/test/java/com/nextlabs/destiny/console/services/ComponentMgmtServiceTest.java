/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 4, 2016
 *
 */
package com.nextlabs.destiny.console.services;

import static com.bluejungle.framework.expressions.RelationOp.DOES_NOT_HAVE;
import static com.nextlabs.destiny.console.enums.PolicyModelType.RESOURCE;
import static org.junit.Assert.assertNotNull;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.bluejungle.framework.expressions.RelationOp;
import com.bluejungle.pf.destiny.lifecycle.DevelopmentStatus;
import com.nextlabs.destiny.console.config.TestConfiguration;
import com.nextlabs.destiny.console.config.TestElasticSearchConfig;
import com.nextlabs.destiny.console.config.root.MessageBundleResolveConfig;
import com.nextlabs.destiny.console.config.root.RootContextConfig;
import com.nextlabs.destiny.console.config.servlet.ServletContextConfig;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentConditionDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyModelDTO;
import com.nextlabs.destiny.console.services.policy.ComponentMgmtService;

/**
 *
 * JUnit Test for ComponentMgmtService
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
public class ComponentMgmtServiceTest {

    private static final Logger log = LoggerFactory
            .getLogger(ComponentMgmtServiceTest.class);

    @Autowired
    private ComponentMgmtService componentMgmtService;

    @Test
    @Transactional
    public void shouldSaveComponent() throws Exception {

        log.info("Test --> should save new component");

        PolicyModelDTO modelDTO = new PolicyModelDTO();
        modelDTO.setId(555L);
        modelDTO.setName("Test Document Type");
        modelDTO.setShortName("TDOC");
        modelDTO.setType(RESOURCE.name());

        ComponentConditionDTO condition1 = new ComponentConditionDTO(
                "fso.test.marker", DOES_NOT_HAVE.getName(), "1234");
        ComponentConditionDTO condition2 = new ComponentConditionDTO(
                "fso.test.size", RelationOp.GREATER_THAN.getName(), "300");
        ComponentConditionDTO condition3 = new ComponentConditionDTO(
                "fso.test.taglabel", RelationOp.INCLUDES.getName(),
                "Basic Doc");

        ComponentDTO component = new ComponentDTO();
        component.setName("Test Component -Resource");
        component.setDescription("Test Component -Resource description");
        component.setStatus("APPROVED");
        component.setPolicyModel(modelDTO);
        component.getConditions().add(condition1);
        component.getConditions().add(condition2);
        component.getConditions().add(condition3);
        component.getSubComponents().add(new ComponentDTO(123L, "Test 1",
                "Test 1 Desc", DevelopmentStatus.NEW.getName()));
        component.getSubComponents().add(new ComponentDTO(133L, "Test 2",
                "Test 2 Desc", DevelopmentStatus.NEW.getName()));
        component.getSubComponents().add(new ComponentDTO(143L, "Test 3",
                "Test 3 Desc", DevelopmentStatus.NEW.getName()));
        component.setType("CO");

        ComponentDTO savedDTO = componentMgmtService.save(component);

        assertNotNull(savedDTO);
        assertNotNull(savedDTO.getId());

    }

}
