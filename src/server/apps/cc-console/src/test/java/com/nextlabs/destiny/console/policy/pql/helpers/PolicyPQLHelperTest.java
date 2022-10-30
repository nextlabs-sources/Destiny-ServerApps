/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 15, 2016
 *
 */
package com.nextlabs.destiny.console.policy.pql.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluejungle.domain.agenttype.AgentTypeEnumType;
import com.bluejungle.pf.destiny.parser.PQLException;
import com.nextlabs.destiny.console.dto.common.AgentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;

/**
 * Policy PQL helper test
 *
 * @author Amila Silva
 * @since 8.0
 */
public class PolicyPQLHelperTest {

    private static final Logger log = LoggerFactory
            .getLogger(PolicyPQLHelperTest.class);

    static String pql = "ID 55 STATUS APPROVED "
            + "POLICY \"TEST_POLICIES/TEST_POLICY_1\""
            + "    ATTRIBUTE DOCUMENT_POLICY"
            + "    FOR (TRUE AND (FALSE OR ID 72))"
            + "    ON (TRUE AND (FALSE OR ID 85))"
            + "    TO (TRUE AND (FALSE OR ID 73))"
            + "    BY (((FALSE OR ID 64) AND NOT ((FALSE OR ID 68 OR ID 65 OR ID 67)) AND (FALSE OR ID 142 OR ID 147)) AND (TRUE AND TRUE) AND (TRUE AND (FALSE OR ID 56)))"
            + "    WHERE (TRUE AND (TRUE AND (user.citizenship = \"US\" AND user.locale = \"US\")))"
            + "    DO deny"
            + "    BY DEFAULT DO allow "
            + "    ON allow DO \"Strip Attachments - File Server Adapter\"(\"File Server\", \"SERVER_1\", \"Location\", \"Bottom\", \"Text\", \"The attachments [filename] to this message have been removed for security purpose and made available at the following location:[link].\", \"Link Format\", \"Long\")"
            + "    ON deny DO \"log\"(\"type\", \"case-1\", \"time\", \"current_timestamp\")";

    static String pql2 = "ID 80 STATUS APPROVED "
            + "POLICY \"TEST_POLICIES/TEST_POLICY_2\""
            + "    ATTRIBUTE COMMUNICATION_POLICY" + "    FOR (TRUE AND TRUE)"
            + "    ON (TRUE AND (FALSE OR ID 86))"
            + "    SENT_TO (TRUE AND (FALSE OR ID 64))"
            + "    BY (((FALSE OR ID 64) AND NOT ((FALSE OR ID 68 OR ID 145)) AND (FALSE OR ID 142)) AND (TRUE AND TRUE) AND (TRUE AND (FALSE OR ID 56)))"
            + "    WHERE (TRUE AND (TRUE AND (user.citizenship = \"US\" AND user.locale = \"US\")))"
            + "    DO allow" + "    ON deny DO log";

    @Test
    public void shouldParsePQL() throws Exception {
        PolicyDTO dto = PolicyPQLHelper.create().fromPQL(pql);

        assertEquals(new Long(55), dto.getId());
        assertEquals("TEST_POLICIES/TEST_POLICY_1", dto.getFullName());
        assertEquals("TEST_POLICY_1", dto.getName());
        assertEquals(4, dto.getSubjectComponents().size());
        assertEquals(1, dto.getActionComponents().size());
        assertEquals(1, dto.getFromResourceComponents().size());
        assertEquals(1, dto.getAllowObligations().size());
        assertEquals(1, dto.getDenyObligations().size());
        // assertEquals("user.citizenship = \"US\" AND user.locale = \"US\"",
        // dto.getExpression());

        log.info("Generated from PQL :{}", ToStringBuilder.reflectionToString(dto,
                ToStringStyle.MULTI_LINE_STYLE));

    }

    @Test
    public void shouldParsePQL2() throws Exception {
        PolicyDTO dto = PolicyPQLHelper.create().fromPQL(pql2);

        assertEquals(new Long(80), dto.getId());
        assertEquals("TEST_POLICY_2", dto.getName());
        assertEquals(4, dto.getSubjectComponents().size());
        assertEquals(1, dto.getToSubjectComponents().size());
        assertEquals(1, dto.getActionComponents().size());
        assertEquals(0, dto.getFromResourceComponents().size());

        log.info("Generated from PQL :{}", ToStringBuilder.reflectionToString(dto,
                ToStringStyle.MULTI_LINE_STYLE));

    }

    @Test
    public void shouldGeneratePQL() throws Exception {
        PolicyDTO dto = PolicyPQLHelper.create().fromPQL(pql2);
        String pql = PolicyPQLHelper.create().getPQL(123L, "GENERATE_PQL",
                "Test Description", dto);

        assertNotNull(pql);
        assertTrue(pql.contains("123"));
        assertTrue(pql.contains("GENERATE_PQL"));
        assertTrue(pql.contains("Test Description"));
    }

    @Test
    public void testManualDeploymentPQLCreation() throws ConsoleException {
        PolicyDTO policyDTO = createPolicyDTO(true);
        String pql = PolicyPQLHelper.create().getPQL(123l, "test_pql", "test_pql", policyDTO);
        String expectedDeploymentTargetPQL = "DEPLOYED TO (" +
                "((FALSE OR AGENT.ID = 12) WITH AGENT.TYPE = \"FILE_SERVER\") , " +
                "((FALSE OR AGENT.ID = 10 OR AGENT.ID = 11) WITH AGENT.TYPE = \"DESKTOP\") , " +
                "((FALSE OR AGENT.ID = 13) WITH AGENT.TYPE = \"PORTAL\") , " +
                "((FALSE OR AGENT.ID = 14) WITH AGENT.TYPE = \"ACTIVE_DIRECTORY\"))";
        assertTrue(pql.contains(expectedDeploymentTargetPQL));
    }

    public static PolicyDTO createPolicyDTO(boolean manualDeploy) {
        PolicyDTO policyDTO = new PolicyDTO();
        policyDTO.setId(123l);
        policyDTO.setName("test_policy");
        policyDTO.setStatus("DRAFT");
        policyDTO.setCategory(DevEntityType.POLICY);
        policyDTO.setEffectType("allow");
        if (manualDeploy) {
            policyDTO.setManualDeploy(true);
            policyDTO.getDeploymentTargets().add(new AgentDTO(10, "hostname1", AgentTypeEnumType.DESKTOP.getName()));
            policyDTO.getDeploymentTargets().add(new AgentDTO(11, "hostname2", AgentTypeEnumType.DESKTOP.getName()));
            policyDTO.getDeploymentTargets().add(new AgentDTO(12, "hostname3", AgentTypeEnumType.FILE_SERVER.getName()));
            policyDTO.getDeploymentTargets().add(new AgentDTO(13, "hostname4", AgentTypeEnumType.PORTAL.getName()));
        }
        return policyDTO;
    }

    @Test
    public void testAutomaticDeploymentPQLCreation() throws ConsoleException {
        PolicyDTO policyDTO = createPolicyDTO(false);
        String pql = PolicyPQLHelper.create().getPQL(123L, "test_pql", "test_pql", policyDTO);
        assertFalse(pql.contains("DEPLOYED TO"));
    }

    @Test
    public void testManualDeploymentPQLParse() throws ConsoleException, PQLException {
        PolicyDTO originalPolicyDTO = createPolicyDTO(true);
        String pql = PolicyPQLHelper.create().getPQL(123L, "test_pql", "test_pql"
                , originalPolicyDTO);
        PolicyDTO parsedPolicyDTO = PolicyPQLHelper.create().fromPQL(pql);
        originalPolicyDTO.getDeploymentTargets().forEach(deploymentTarget -> deploymentTarget.setHost(null));
        assertTrue(CollectionUtils.isEqualCollection(originalPolicyDTO.getDeploymentTargets()
                , parsedPolicyDTO.getDeploymentTargets()));
    }
}
