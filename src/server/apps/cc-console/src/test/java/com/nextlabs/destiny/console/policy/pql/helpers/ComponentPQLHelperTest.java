/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 15, 2016
 *
 */
package com.nextlabs.destiny.console.policy.pql.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.bluejungle.framework.search.RelationalOp;
import com.bluejungle.pf.domain.epicenter.common.SpecType;
import com.nextlabs.destiny.console.config.TestConfiguration;
import com.nextlabs.destiny.console.config.TestElasticSearchConfig;
import com.nextlabs.destiny.console.config.root.MessageBundleResolveConfig;
import com.nextlabs.destiny.console.config.root.RootContextConfig;
import com.nextlabs.destiny.console.config.servlet.ServletContextConfig;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentConditionDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.model.policy.PolicyModel;

/**
 *
 * Component PQL helper test
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
public class ComponentPQLHelperTest {

    static String resource_pql = "ID 78 STATUS DRAFT "
            + "COMPONENT \"RESOURCE/test resource 2-11-1\" = DESCRIPTION \"Feb 11th\" "
            + "((TRUE OR ID 79 OR ID 100) AND (TRUE AND resource.goodsn.\"attribute 1\" = \"1\" "
            + "AND resource.goodsn.\"attribute 2\" >= \"2\"))";

    static String action_pql = "ID 80 STATUS APPROVED "
            + "COMPONENT \"ACTION/test component - SUB ACTION\" = DESCRIPTION \"test component Feb 10th\" "
            + "(COPY OR DELETE)";

    static String subject_pql = "ID 97 STATUS DRAFT "
            + "COMPONENT \"SUBJECT/another subject component\" = DESCRIPTION \"description\""
            + "((TRUE OR ID 96 OR ID 108) AND (TRUE AND FALSE))";

    @Test
    public void shouldParseResourcePQL() throws Exception {
        ComponentDTO dto = ComponentPQLHelper.create().fromPQL(resource_pql);

        assertEquals(new Long(78), dto.getId());
        assertEquals("RESOURCE/test resource 2-11-1", dto.getName());
        assertEquals("DRAFT", dto.getStatus());
        assertEquals("Feb 11th", dto.getDescription());
        assertEquals(2, dto.getSubComponents().size());
        assertEquals(2, dto.getConditions().size());

    }

    @Test
    public void shouldParseActionPQL() throws Exception {
        ComponentDTO dto = ComponentPQLHelper.create().fromPQL(action_pql);

        assertEquals(new Long(80), dto.getId());
        assertEquals("ACTION/test component - SUB ACTION", dto.getName());
        assertEquals(2, dto.getActions().size());

    }

    @Test
    public void shouldParseSubjectPQL() throws Exception {
        ComponentDTO dto = ComponentPQLHelper.create().fromPQL(subject_pql);

        assertEquals(new Long(97), dto.getId());
        assertEquals("SUBJECT/another subject component", dto.getName());
        assertEquals("description", dto.getDescription());
        assertEquals(2, dto.getSubComponents().size());
    }

    @Test
    public void shouldGenerateResourcePQL() throws Exception {
        PolicyModel policyModel = new PolicyModel();
        policyModel.setShortName("acs");
        policyModel.setType(PolicyModelType.RESOURCE);

        ComponentDTO dto = new ComponentDTO(123L);
        dto.setStatus(PolicyDevelopmentStatus.DRAFT.name());
        dto.getConditions().add(new ComponentConditionDTO("test_attr1",
                RelationalOp.EQUALS.getName(), "abs"));
        dto.getConditions().add(new ComponentConditionDTO("test_attr2",
                RelationalOp.GREATER_THAN.getName(), "10"));
        dto.getConditions().add(new ComponentConditionDTO("test_attr3",
                RelationalOp.HAS.getName(), "amila"));

        dto.getSubComponents()
                .add(new ComponentDTO(100L, "Main-Resource 1", "", "NEW"));
        dto.getSubComponents()
                .add(new ComponentDTO(101L, "Main-Resource 2", "", "NEW"));

        String pql = ComponentPQLHelper.create().getPQL(123L,
                "RESOURCE/Test PQL 123", "123 Description", SpecType.RESOURCE,
                dto, policyModel, policyModel.getType());
        assertNotNull(pql);
        assertTrue(pql.contains("RESOURCE/Test PQL 123"));
        assertTrue(pql.contains("123 Description"));
        assertTrue(pql.contains("100"));
        assertTrue(pql.contains("101"));
        assertTrue(pql.contains("acs.test_attr1"));
        assertTrue(pql.contains("acs.test_attr2"));
        assertTrue(pql.contains("acs.test_attr3"));
        assertTrue(pql.contains("COMPONENT"));
        assertTrue(pql.contains("RESOURCE"));
    }

    @Test
    public void shouldGenerateActionPQL() throws Exception {
        PolicyModel policyModel = new PolicyModel();
        policyModel.setShortName("acs");

        ComponentDTO dto = new ComponentDTO(123L);
        dto.setStatus(PolicyDevelopmentStatus.DRAFT.name());
        dto.getActions().add("EDIT");
        dto.getActions().add("COPY");
        dto.getActions().add("MOVE");

        dto.getSubComponents()
                .add(new ComponentDTO(102L, "Main-Action 1", "", "NEW"));
        dto.getSubComponents()
                .add(new ComponentDTO(104L, "Main-Action 2", "", "NEW"));

        String pql = ComponentPQLHelper.create().getPQL(123L,
                "ACTION/Test ACTION 123", "123 Description", SpecType.ACTION,
                dto, policyModel, policyModel.getType());
        assertNotNull(pql);
        assertTrue(pql.contains("ACTION/Test ACTION 123"));
        assertTrue(pql.contains("123 Description"));
        assertTrue(pql.contains("102"));
        assertTrue(pql.contains("104"));
        assertTrue(pql.contains("EDIT"));
        assertTrue(pql.contains("COPY"));
        assertTrue(pql.contains("MOVE"));
        assertTrue(pql.contains("COMPONENT"));
        assertTrue(pql.contains("ACTION"));
    }

    @Test
    public void shouldGenerateSubjectPQL() throws Exception {
        PolicyModel policyModel = new PolicyModel();
        policyModel.setShortName("acs");
        policyModel.setType(PolicyModelType.SUBJECT);

        ComponentDTO dto = new ComponentDTO(123L);
        dto.setStatus(PolicyDevelopmentStatus.DRAFT.name());
        dto.getConditions().add(new ComponentConditionDTO("test_subject_attr1",
                RelationalOp.EQUALS.getName(), "abs"));
        dto.getConditions().add(new ComponentConditionDTO("test_subject_attr2",
                RelationalOp.GREATER_THAN.getName(), "10"));
        dto.getConditions().add(new ComponentConditionDTO("test_subject_attr3",
                RelationalOp.HAS.getName(), "amila"));

        dto.getSubComponents()
                .add(new ComponentDTO(102L, "Main-subject 1", "", "NEW"));
        dto.getSubComponents()
                .add(new ComponentDTO(104L, "Main-Action 2", "", "NEW"));

        String pql = ComponentPQLHelper.create().getPQL(123L,
                "SUBJECT/Test 123", "123 Description", SpecType.USER, dto,
                policyModel, policyModel.getType());
        assertNotNull(pql);
        assertTrue(pql.contains("SUBJECT/Test 123"));
        assertTrue(pql.contains("123 Description"));
        assertTrue(pql.contains("102"));
        assertTrue(pql.contains("104"));
        assertTrue(pql.contains("acs.test_subject_attr1"));
        assertTrue(pql.contains("acs.test_subject_attr2"));
        assertTrue(pql.contains("acs.test_subject_attr3"));
        assertTrue(pql.contains("COMPONENT"));
        assertTrue(pql.contains("SUBJECT"));
    }
}
