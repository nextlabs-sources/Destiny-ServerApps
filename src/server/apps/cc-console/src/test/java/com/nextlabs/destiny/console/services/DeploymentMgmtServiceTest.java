/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 22, 2016
 *
 */
package com.nextlabs.destiny.console.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.nextlabs.destiny.console.config.TestConfiguration;
import com.nextlabs.destiny.console.config.TestElasticSearchConfig;
import com.nextlabs.destiny.console.config.root.MessageBundleResolveConfig;
import com.nextlabs.destiny.console.config.root.RootContextConfig;
import com.nextlabs.destiny.console.config.servlet.ServletContextConfig;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentEntity;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentRecord;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.policy.handlers.PolicyLifeCycleHandler;
import com.nextlabs.destiny.console.services.policy.PolicyDeploymentEntityMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyDeploymentRecordMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyDevelopmentEntityMgmtService;

/**
 *
 *
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
public class DeploymentMgmtServiceTest {

    private static final Logger log = LoggerFactory
            .getLogger(DeploymentMgmtServiceTest.class);

    @Autowired
    private PolicyDevelopmentEntityMgmtService devEntityService;

    @Autowired
    private PolicyDeploymentEntityMgmtService deployEntityService;

    @Autowired
    private PolicyDeploymentRecordMgmtService deployRecordService;

    @Autowired
    private PolicyLifeCycleHandler lifeCyleHandler;

    String pql = "ID 5 STATUS APPROVED POLICY \"API-TEST/p0\" "
            + "ATTRIBUTE DOCUMENT_POLICY " + "FOR (TRUE AND TRUE) "
            + "ON (TRUE AND TRUE) " + "TO (TRUE AND TRUE) "
            + "BY ((TRUE AND (TRUE AND TRUE)) AND (TRUE AND TRUE) AND (TRUE AND TRUE)) "
            + "DO deny " + "BY DEFAULT DO allow ";

    @Test
    @Transactional
    public void shouldGetDeployedEntitiesByPolicyId() throws Exception {

        PolicyDeploymentRecord depRecord = createDeployRecord();
        PolicyDeploymentRecord savedRecord = deployRecordService
                .save(depRecord);
        assertNotNull(savedRecord);
        assertNotNull(savedRecord.getId());

        Long depRecordId = savedRecord.getId();
        PolicyDeploymentEntity depEnity = createDeployEntity(36L,
                "TEST/Test_Policy_1", "Policy PQL", 0, 9);
        depEnity.setDepRecordId(depRecordId);
        PolicyDeploymentEntity savedEntity = deployEntityService.save(depEnity);
        assertNotNull(savedEntity);
        assertNotNull(savedEntity.getId());

        long policyId = 36L;
        List<PolicyDeploymentEntity> entityList = deployEntityService
                .findByPolicyId(policyId);
        assertNotNull(entityList);
        assertFalse("Result List should not be empty", entityList.isEmpty());
        assertTrue("Should return 1 record", entityList.size() == 1);
    }

    @Test
    public void shouldDeployNewEntity() throws Exception {

        // create in record in DEVELOPMENT_ENTITIES
        PolicyDevelopmentEntity devEntity = createDevEntity("API-TEST/p0",
                "Test Policy p0 for API Test", pql);
        devEntity.setStatus("EM");
        PolicyDevelopmentEntity saveEntity = devEntityService.save(devEntity);
        assertNotNull(saveEntity);

        PolicyDevelopmentEntity savedEntity = devEntityService
                .findById(saveEntity.getId());
        assertNotNull(savedEntity);
        assertNotNull(savedEntity.getId());
        Long policyId = savedEntity.getId();
        log.info("Saved Entity Name = " + savedEntity.getTitle());
        log.info("New Entity saved successfully.. Id  = " + policyId);

        // deploy the policy
        devEntity.setStatus("AP");
        lifeCyleHandler.deployEntity(devEntity);

        // check if deployment entity has been created
        List<PolicyDeploymentEntity> entityList = deployEntityService
                .findByPolicyId(policyId);
        assertNotNull(entityList);
        assertFalse("Result List should not be empty", entityList.isEmpty());
        assertTrue("Should return only 1 record", entityList.size() == 1);

        PolicyDeploymentEntity deployedEntity = entityList.get(0);
        Long deployRecordId = deployedEntity.getDepRecordId();
        assertEquals("API-TEST/p0", savedEntity.getTitle());
        assertEquals("Test Policy p0 for API Test",
                savedEntity.getDescription());

        // check if the deployment record has been created
        PolicyDeploymentRecord deployedRecord = deployRecordService
                .findById(deployRecordId);
        assertNotNull(deployedRecord);
        assertEquals("DE", deployedRecord.getActionType());
        assertEquals("PR", deployedRecord.getDeploymentType());
    }

    @Test
    @Transactional
    public void shouldDeployUpdatedEntity() throws Exception {

        Long policyId = createAndDeployPolicy("API-TEST/p1",
                "Test Policy p1 for API Test");

        PolicyDevelopmentEntity devEntity = devEntityService.findById(policyId);

        // update the devEntity
        devEntity
                .setDescription("Updated desc for Test Policy p1 for API Test");
        PolicyDevelopmentEntity updatedEntity = updatePolicy(devEntity);
        assertEquals("Updated desc for Test Policy p1 for API Test",
                updatedEntity.getDescription());

        assertEquals(devEntity.getId(), updatedEntity.getId());

        // re-deploy the updated entity
        updatedEntity.setStatus("AP");
        lifeCyleHandler.deployEntity(updatedEntity);

        // check if deployment entity has been created
        List<PolicyDeploymentEntity> entities = deployEntityService
                .findByPolicyId(policyId);
        assertNotNull(entities);
        assertFalse("Result List should not be empty", entities.isEmpty());
        assertTrue("Should return 2 records", entities.size() == 2);

        // check if the deployment record has been created
        for (PolicyDeploymentEntity depEntity : entities) {
            Long deployRecordId = depEntity.getDepRecordId();
            PolicyDeploymentRecord deployedRecord = deployRecordService
                    .findById(deployRecordId);
            assertNotNull(deployedRecord);
        }
    }

    @Test
    public void shouldUnDeployEntity() throws Exception {

        Long policyId = createAndDeployPolicy("API-TEST/p2",
                "Test Policy p2 for API Test");

        PolicyDevelopmentEntity devEntity = devEntityService.findById(policyId);

        // deactivate the devEntity
        PolicyDevelopmentEntity deactivatedEntity = deactivatePolicy(devEntity);
        assertEquals(devEntity.getId(), deactivatedEntity.getId());

        // deploy the deactivated entity
        deactivatedEntity.setStatus("DE");
        lifeCyleHandler.deployEntity(deactivatedEntity);

        // check if deployment entity has been created
        List<PolicyDeploymentEntity> entities = deployEntityService
                .findByPolicyId(policyId);
        assertNotNull(entities);
        assertFalse("Result List should not be empty", entities.isEmpty());
        assertTrue("Should return 2 records", entities.size() == 2);

        // check if the deployment record has been created
        for (PolicyDeploymentEntity depEntity : entities) {
            Long deployRecordId = depEntity.getDepRecordId();
            PolicyDeploymentRecord deployedRecord = deployRecordService
                    .findById(deployRecordId);
            assertNotNull(deployedRecord);
        }
    }
   
    @Test
    public void shouldGetLastActiveRecordByPolicyId() throws Exception {

        // create and deploy a new policy
        Long policyId = createAndDeployPolicy("API-TEST/p3",
                "Test Policy p3 for API Test");
        PolicyDevelopmentEntity devEntity = devEntityService.findById(policyId);

        // update the policy
        devEntity.setDescription("Updated Test Policy p3 for API Test");
        PolicyDevelopmentEntity updatedEntity = updatePolicy(devEntity);
        assertEquals("Updated Test Policy p3 for API Test",
                updatedEntity.getDescription());

        assertEquals(devEntity.getId(), updatedEntity.getId());

        // deploy the updated policy
        updatedEntity.setStatus("AP");
        lifeCyleHandler.deployEntity(updatedEntity);

        // deactivate the policy
        
       // PolicyDevelopmentEntity deactivatedEntity = deactivatePolicy(updatedEntity);
        //assertEquals(updatedEntity.getId(), deactivatedEntity.getId());
        

        // deploy the deactivated policy
        //deployDeactivatedPolicy(deactivatedEntity);

        // get the last active record
        PolicyDeploymentEntity lastActiveRecord = deployEntityService
                .getLastActiveRecord(policyId);
        assertNotNull(lastActiveRecord);
        assertTrue("DevId should equal Policy ID",
                lastActiveRecord.getDevelopmentId() == policyId);
    }

    @Test
    public void shouldCheckIfEntityIsDeployed() throws Exception {
        
        Long policyId = createAndDeployPolicy("API-TEST/p4",
                "Test Policy p4 for API Test");

       /* PolicyDevelopmentEntity devEntity = devEntityService.findById(policyId);
        log.info("Id of policy API-TEST/p4 is = " + policyId);*/

        // check if the updated policy is deployed
        boolean isEntityDeployed = deployEntityService
                .isEntityDeployed(policyId);
        assertTrue(isEntityDeployed);
    }

    @Transactional
    private Long createAndDeployPolicy(String title, String desc)
            throws Exception {

        // create a record in DEVELOPMENT_ENTITIES
        PolicyDevelopmentEntity devEntity = createDevEntity("API-TEST/p1",
                "Test Policy p1 for API Test", pql);
        devEntity.setStatus("EM");
        PolicyDevelopmentEntity saveEntity = devEntityService.save(devEntity);
        assertNotNull(saveEntity);

        PolicyDevelopmentEntity savedEntity = devEntityService
                .findById(saveEntity.getId());
        assertNotNull(savedEntity);
        assertNotNull(savedEntity.getId());
        Long policyId = savedEntity.getId();

        log.info("New Entity saved successfully.. Id  = " + policyId);

        // deploy the policy
        devEntity.setStatus("AP");
        lifeCyleHandler.deployEntity(devEntity);

        // check if deployment entity has been created
        List<PolicyDeploymentEntity> entityList = deployEntityService
                .findByPolicyId(policyId);
        assertNotNull(entityList);
        assertFalse("Result List should not be empty", entityList.isEmpty());
        assertTrue("Should return only 1 record", entityList.size() == 1);
        PolicyDeploymentEntity deployedEntity = entityList.get(0);
        Long deployRecordId = deployedEntity.getDepRecordId();
        
        Date d1 = new Date(System.currentTimeMillis());
        log.info("Current Date derived from ms = "+d1);
        Date d2 = new Date(253402271999000L);
        log.info("Max Future Date derived from ms = "+d2);

        //verify data in DEPLOYMENT_ENTITIES
        assertEquals(policyId, deployedEntity.getDevelopmentId());
        Date d3 = new Date(deployedEntity.getActiveTo());
        log.info("Active_To Date derived from ms = "+d3);
        /*assertTrue("Active_to must be greater than current time",
                deployedEntity.getActiveTo() > System.currentTimeMillis());  */
        
        // check if the deployment record has been created
        PolicyDeploymentRecord deployedRecord = deployRecordService
                .findById(deployRecordId);
        assertNotNull(deployedRecord);
        assertEquals("DE", deployedRecord.getActionType());

        return policyId;
    }

    private PolicyDevelopmentEntity updatePolicy(
            PolicyDevelopmentEntity devEntity) throws Exception {

        PolicyDevelopmentEntity updateEntity = devEntityService.save(devEntity);
        assertNotNull(updateEntity);

        PolicyDevelopmentEntity updatedEntity = devEntityService
                .findById(updateEntity.getId());
        assertNotNull(updatedEntity);
        assertNotNull(updatedEntity.getId());

        return updatedEntity;
    }

    private PolicyDevelopmentEntity deactivatePolicy(
            PolicyDevelopmentEntity devEntity) throws Exception {

        devEntity.setStatus("OB");
        PolicyDevelopmentEntity updateEntity = devEntityService.save(devEntity);
        assertNotNull(updateEntity);

        PolicyDevelopmentEntity updatedEntity = devEntityService
                .findById(updateEntity.getId());
        assertNotNull(updatedEntity);
        assertNotNull(updatedEntity.getId());
        assertEquals("OB", updatedEntity.getStatus());

        return updatedEntity;
    }

    @Transactional
    private void deployDeactivatedPolicy(
            PolicyDevelopmentEntity deactivatedEntity) throws Exception {
        deactivatedEntity.setStatus("DE");

        lifeCyleHandler.deployEntity(deactivatedEntity);
    }

    private PolicyDevelopmentEntity createDevEntity(String title, String desc,
            String pql) {
        PolicyDevelopmentEntity devEntity = new PolicyDevelopmentEntity();

        devEntity.setOwner(0L);
        devEntity.setTitle(title);
        devEntity.setDescription(desc);
        devEntity.setPql(pql);
        devEntity.setApPql(pql);
        devEntity.setType("PO");
        devEntity.setModifiedBy(0L);
        devEntity.setHidden(false);
       /* devEntity.setHasDependencies('N');
        devEntity.setSubPolicy('N');
*/
        devEntity.setLastUpdatedDate(System.currentTimeMillis());
        return devEntity;
    }

    private PolicyDeploymentRecord createDeployRecord() {
        PolicyDeploymentRecord deployRecord = new PolicyDeploymentRecord();

        deployRecord.setActionType("DE");
        deployRecord.setDeploymentType("PR");
        deployRecord.setAsOf(System.currentTimeMillis());
        deployRecord.setWhenRequested(System.currentTimeMillis());
    //    deployRecord.setHidden('N');
        deployRecord.setDeployer(0L);

        return deployRecord;
    }

    private PolicyDeploymentEntity createDeployEntity(Long policyId,
            String name, String pql, int overrideCount, int originalVersion) {
        PolicyDeploymentEntity deployEntity = new PolicyDeploymentEntity();

        deployEntity.setDevelopmentId(policyId);
        deployEntity.setName(name);
        deployEntity.setPql(pql);
        deployEntity.setOverrideCount(overrideCount);
        //deployEntity.setHidden('N');
        deployEntity.setOriginalVersion(originalVersion);
        deployEntity.setModifier(0L);
        deployEntity.setSubmitter(0L);

        deployEntity.setActiveFrom(System.currentTimeMillis());
        deployEntity.setLastModified(System.currentTimeMillis());
        deployEntity.setSubmittedTime(System.currentTimeMillis());

        return deployEntity;
    }

}
