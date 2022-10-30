/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 16, 2016
 *
 */
package com.nextlabs.destiny.console.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.nextlabs.destiny.console.config.TestConfiguration;
import com.nextlabs.destiny.console.config.TestElasticSearchConfig;
import com.nextlabs.destiny.console.config.root.MessageBundleResolveConfig;
import com.nextlabs.destiny.console.config.root.RootContextConfig;
import com.nextlabs.destiny.console.config.servlet.ServletContextConfig;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.services.policy.PolicyMgmtService;

/**
 *
 * JUnit Test for PolicyMgmtService
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
public class PolicyMgmtServiceTest {

    @Autowired
    private PolicyMgmtService policyMgmtService;

    @Test
    @Transactional
    public void shouldSavePolicy() throws Exception {

        PolicyDTO policyDto = new PolicyDTO();

        policyDto.setName("Test_policy_p0");
        policyDto.setFullName("Test_policy_p0");
        policyDto.setDescription("p0 description");
        policyDto.setStatus("APPROVED");
        policyDto.setEffectType("ALLOW");
        policyDto.setLastUpdatedDate(System.currentTimeMillis());
        policyDto.setCreatedDate(System.currentTimeMillis());

        PolicyDTO savePolicyDto = policyMgmtService.save(policyDto);
        assertNotNull(savePolicyDto);

        PolicyDTO savedPolicyDto = policyMgmtService
                .findById(savePolicyDto.getId());
        assertNotNull(savedPolicyDto);
        assertNotNull(savedPolicyDto.getId());

    }

    @Test
    @Transactional
    public void shouldModifyPolicy() throws Exception {

        PolicyDTO policyDto = new PolicyDTO();

        policyDto.setName("Test_policy_p1");
        policyDto.setFullName("Test_policy_p1");
        policyDto.setDescription("p1 description");
        policyDto.setStatus("APPROVED");
        policyDto.setEffectType("DENY");
        policyDto.setLastUpdatedDate(System.currentTimeMillis());
        policyDto.setCreatedDate(System.currentTimeMillis());

        PolicyDTO savePolicyDto = policyMgmtService.save(policyDto);
        assertNotNull(savePolicyDto);

        PolicyDTO savedPolicyDto = policyMgmtService
                .findById(savePolicyDto.getId());
        assertNotNull(savedPolicyDto);
        assertNotNull(savedPolicyDto.getId());

        savedPolicyDto.setFullName("Test_policy_p1_modified_name");

        PolicyDTO updatePolicyDto = policyMgmtService.save(savedPolicyDto);
        assertNotNull(updatePolicyDto);

        PolicyDTO updatedPolicyDto = policyMgmtService
                .findById(savePolicyDto.getId());
        assertNotNull(updatedPolicyDto);
        assertNotNull(updatedPolicyDto.getId());

        assertEquals(savedPolicyDto.getId(), updatedPolicyDto.getId());
        assertEquals("Test_policy_p1_modified_name",
                updatedPolicyDto.getFullName());
        //assertNotEquals("Test_policy_p1", updatedPolicyDto.getFullName());
    }

    @Test
    @Transactional
    public void shouldRemovePolicy() throws Exception {
        PolicyDTO policyDto = new PolicyDTO();

        policyDto.setName("Test_policy_p2");
        policyDto.setFullName("Test_policy_p2");
        policyDto.setDescription("p2 description");
        policyDto.setStatus("APPROVED");
        policyDto.setEffectType("ALLOW");
        policyDto.setCreatedDate(System.currentTimeMillis());

        PolicyDTO savePolicyDto = policyMgmtService.save(policyDto);
        assertNotNull(savePolicyDto);

        PolicyDTO savedPolicyDto = policyMgmtService
                .findById(savePolicyDto.getId());
        assertNotNull(savedPolicyDto);
        assertNotNull(savedPolicyDto.getId());
        Long policyId = savedPolicyDto.getId();

        policyMgmtService.remove(ImmutableList.of(policyId), true);

        PolicyDTO removedPolicyDto = policyMgmtService.findById(policyId);
        assertNotNull(removedPolicyDto);
        assertEquals("DE",removedPolicyDto.getStatus());
    }
    

    @Test
    @Transactional
    public void shouldClonePolicy() throws Exception {

        PolicyDTO policyDto = new PolicyDTO();

        policyDto.setName("Test_policy_p5");
        policyDto.setFullName("Test_policy_p5");
        policyDto.setDescription("p5 description");
        policyDto.setStatus("APPROVED");
        policyDto.setEffectType("ALLOW");
        policyDto.setCreatedDate(System.currentTimeMillis());

        PolicyDTO savedPolicyDto = policyMgmtService.save(policyDto);
        assertNotNull(savedPolicyDto);
        assertNotNull(savedPolicyDto.getId());

        PolicyDTO clonedPolicyDto = policyMgmtService
                .clone(savedPolicyDto.getId());
        assertNotNull(clonedPolicyDto);
        assertNotNull(clonedPolicyDto.getId());

        //assertNotEquals(savedPolicyDto.getId(), clonedPolicyDto.getId());
    }

    @Test
    @Transactional
    public void shouldRemovePolicies() throws Exception {

        PolicyDTO policyDto1 = new PolicyDTO();
        PolicyDTO policyDto2 = new PolicyDTO();
        PolicyDTO policyDto3 = new PolicyDTO();

        policyDto1.setName("Test_policy_p2");
        policyDto1.setFullName("Test_policy_p2");
        policyDto1.setDescription("p2 description");
        policyDto1.setStatus("APPROVED");
        policyDto1.setEffectType("ALLOW");
        policyDto1.setCreatedDate(System.currentTimeMillis());

        PolicyDTO savePolicyDto1 = policyMgmtService.save(policyDto1);
        assertNotNull(savePolicyDto1);

        PolicyDTO savedPolicyDto1 = policyMgmtService
                .findById(savePolicyDto1.getId());
        Long policyId1 = savedPolicyDto1.getId();

        policyDto2.setName("Test_policy_p2");
        policyDto2.setFullName("Test_policy_p2");
        policyDto2.setDescription("p2 description");
        policyDto2.setStatus("APPROVED");
        policyDto2.setEffectType("ALLOW");
        policyDto2.setCreatedDate(System.currentTimeMillis());

        PolicyDTO savePolicyDto2 = policyMgmtService.save(policyDto2);
        assertNotNull(savePolicyDto2);

        PolicyDTO savedPolicyDto2 = policyMgmtService
                .findById(savePolicyDto2.getId());
        Long policyId2 = savedPolicyDto2.getId();

        policyDto3.setName("Test_policy_p2");
        policyDto3.setFullName("Test_policy_p2");
        policyDto3.setDescription("p2 description");
        policyDto3.setStatus("APPROVED");
        policyDto3.setEffectType("ALLOW");
        policyDto3.setCreatedDate(System.currentTimeMillis());

        PolicyDTO savePolicyDto3 = policyMgmtService.save(policyDto3);
        assertNotNull(savePolicyDto3);

        PolicyDTO savedPolicyDto3 = policyMgmtService
                .findById(savePolicyDto3.getId());
        Long policyId3 = savedPolicyDto3.getId();

        List<Long> policyIds = new ArrayList<Long>();
        policyIds.add(policyId1);
        policyIds.add(policyId2);
        policyIds.add(policyId3);

        policyMgmtService.remove(policyIds, true);

        PolicyDTO removedPolicyDto1 = policyMgmtService.findById(policyId1);
        assertNotNull(removedPolicyDto1);
        assertEquals("DE", removedPolicyDto1.getStatus());

        PolicyDTO removedPolicyDto2 = policyMgmtService.findById(policyId2);
        assertNotNull(removedPolicyDto2);
        assertEquals("DE", removedPolicyDto2.getStatus());

        PolicyDTO removedPolicyDto3 = policyMgmtService.findById(policyId3);
        assertNotNull(removedPolicyDto3);
        assertEquals("DE", removedPolicyDto3.getStatus());

    }
}
