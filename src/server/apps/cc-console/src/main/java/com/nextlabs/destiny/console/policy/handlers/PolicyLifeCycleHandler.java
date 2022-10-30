/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 19, 2016
 *
 */
package com.nextlabs.destiny.console.policy.handlers;

import static com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus.APPROVED;
import static com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus.OBSOLETE;
import static com.nextlabs.destiny.console.utils.SecurityContextUtil.getCurrentUser;
import static java.lang.System.currentTimeMillis;

import java.io.IOException;

import com.bluejungle.pf.destiny.parser.PQLException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bluejungle.pf.domain.epicenter.common.SpecType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyModelDTO;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.policy.ComponentExtDescription;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentEntity;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentRecord;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.model.policy.PolicyModel;
import com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper;
import com.nextlabs.destiny.console.policy.pql.helpers.PolicyPQLHelper;
import com.nextlabs.destiny.console.services.policy.PolicyDeploymentEntityMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyDeploymentRecordMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyDevelopmentEntityMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyModelService;

/**
 *
 * Policy or Component lifecycle manager to handle with all the states
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class PolicyLifeCycleHandler {

    private static final Logger log = LoggerFactory
            .getLogger(PolicyLifeCycleHandler.class);

    public static final long DATETIME_MAX_TICKS = 253402271999000L; // 9999-12-31
                                                                    // 23:59:59

    @Autowired
    private PolicyDevelopmentEntityMgmtService devEntityMgmtService;

    @Autowired
    private PolicyDeploymentEntityMgmtService depEntityMgmtService;

    @Autowired
    private PolicyDeploymentRecordMgmtService depRecordsMgmtService;

    @Autowired
    private PolicyModelService policyModelService;

    /**
     * Deploy a {@link PolicyDevelopmentEntity}
     * 
     * @param entity
     * @throws ConsoleException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deployEntity(PolicyDevelopmentEntity entity)
            throws ConsoleException {
        deployEntity(entity, -1);
    }

    /**
     * Deploy a {@link PolicyDevelopmentEntity} with deployment time.
     *
     * @param entity
     * @throws ConsoleException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deployEntity(PolicyDevelopmentEntity entity, long deploymentTime)
            throws ConsoleException {
        deployEntity(entity, APPROVED, deploymentTime);
    }

    /**
     * Deploy a {@link PolicyDevelopmentEntity}
     *
     * @param entity
     * @param status
     * @throws ConsoleException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deployEntity(PolicyDevelopmentEntity entity,
                             PolicyDevelopmentStatus status,
                             long deploymentTime) throws ConsoleException {

        try {
            String actionType = "DE";
            String deploymentType = "PR";

            PolicyDevelopmentEntity devEntity;
            // Update entity status
            if (entity.getType().equals(DevEntityType.XACML_POLICY.getKey())) {
                devEntity = updateDevEntity(entity, status);
            } else {
                devEntity = updateDevEntityPQL(entity, status);
            }

            deploymentTime = deploymentTime > 0 ? deploymentTime : System.currentTimeMillis();
            expireThePrvActiveDeployment(devEntity, deploymentTime);

            // create new deployed record and deployed entity
            PolicyDeploymentRecord depRecord = makeNewDeploymentRecord(deploymentTime, actionType, deploymentType);

            makeNewDeploymentEntity(entity, devEntity, deploymentTime, depRecord);

            log.info(
                    "Policy development entity deployed successfully, [Policy Id:{}]",
                    devEntity.getId());

        } catch (Exception e) {
            String message = StringUtils.isEmpty(e.getMessage()) ?
                    "Error encountered in policy deployment" :
                    e.getMessage();
            throw new ConsoleException(message, e);
        }
    }

    /**
     * un-deploy a {@link PolicyDevelopmentEntity}
     * 
     * @param entity
     * @throws ConsoleException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void unDeployEntity(PolicyDevelopmentEntity entity)
            throws ConsoleException {
        unDeployEntity(entity, OBSOLETE);
    }

    /**
     * un-deploy a {@link PolicyDevelopmentEntity}
     * 
     * @param entity
     * @param status
     * @throws ConsoleException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void unDeployEntity(PolicyDevelopmentEntity entity,
            PolicyDevelopmentStatus status) throws ConsoleException {
        try {
            String actionType = "UN";
            String deploymentType = "PR";
            Long currentTime = System.currentTimeMillis();

            PolicyDevelopmentEntity devEntity;
            // Update entity status
            if (entity.getType().equals(DevEntityType.XACML_POLICY.getKey())) {
                devEntity = updateDevEntity(entity, status);
            } else {
                devEntity = updateDevEntityPQL(entity, status);
            }

            // update last deployed entity records active period
            expireThePrvActiveDeployment(devEntity, currentTime);

            // create new deployed record and deployed entity
            PolicyDeploymentRecord depRecord = makeNewDeploymentRecord(
                    currentTime, actionType, deploymentType);
            makeNewDeploymentEntity(entity, devEntity, currentTime, depRecord);

            log.info(
                    "Policy development entity un-deployment successfully, [Policy Id:{}]",
                    devEntity.getId());

        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in un-deploy a policy development entity",
                    e);
        }
    }

    private void expireThePrvActiveDeployment(PolicyDevelopmentEntity devEntity,
            Long currentTime) throws ConsoleException {
        PolicyDeploymentEntity expireDepEntity = depEntityMgmtService
                .getLastActiveRecord(devEntity.getId());

        if (expireDepEntity != null) {
            expireDepEntity.setActiveTo(currentTime - 1000);
            depEntityMgmtService.save(expireDepEntity);
        }
    }

    private void makeNewDeploymentEntity(PolicyDevelopmentEntity entity,
            PolicyDevelopmentEntity devEntity, Long currentTime,
            PolicyDeploymentRecord depRecord) throws ConsoleException {
        PolicyDeploymentEntity deploymentEntity = new PolicyDeploymentEntity();
        deploymentEntity.setDevelopmentId(devEntity.getId());
        deploymentEntity.setDepRecordId(depRecord.getId());
        deploymentEntity.setName(entity.getTitle());
        deploymentEntity.setDescription(entity.getDescription());
        deploymentEntity.setActiveFrom(currentTime);
        deploymentEntity.setActiveTo(DATETIME_MAX_TICKS);
        deploymentEntity.setOverrideCount(0);
        if (depRecord.getActionType().equals("DE")) {
            deploymentEntity.setPql(entity.getPql());
        } else {
        	deploymentEntity.setPql(devEntity.getPql());
            deploymentEntity.setActiveTo(currentTime);
        }
        deploymentEntity.setOriginalVersion(1);
        deploymentEntity.setHidden(
                entity.getType().equalsIgnoreCase(DevEntityType.LOCATION.getKey())
                        ? entity.getHidden()
                        : Boolean.FALSE);
        deploymentEntity.setLastModified(entity.getLastModified());
        deploymentEntity.setModifier(entity.getModifiedBy());
        deploymentEntity.setSubmittedTime(currentTimeMillis());
        deploymentEntity.setSubmitter(getCurrentUser().getUserId());
        depEntityMgmtService.save(deploymentEntity);
    }

    private PolicyDeploymentRecord makeNewDeploymentRecord(Long currentTime,
            String actionType, String deploymentType) throws ConsoleException {
        PolicyDeploymentRecord depRecord = new PolicyDeploymentRecord();
        depRecord.setActionType(actionType);
        depRecord.setDeploymentType(deploymentType);
        depRecord.setAsOf(currentTime);
        depRecord.setWhenRequested(currentTime);
        depRecord.setHidden(Boolean.FALSE);
        depRecord.setDeployer(getCurrentUser().getUserId());
        depRecordsMgmtService.save(depRecord);
        return depRecord;
    }

    private PolicyDevelopmentEntity updateDevEntity(PolicyDevelopmentEntity entity, PolicyDevelopmentStatus status) throws ConsoleException {
        PolicyDevelopmentEntity devEntity = devEntityMgmtService
                .findById(entity.getId());

        devEntity.setLastUpdatedDate(System.currentTimeMillis());
        devEntity.setStatus(status.getKey());

        devEntity.setSubmittedTime(currentTimeMillis());
        devEntity.setSubmitter(getCurrentUser().getUserId());
        devEntityMgmtService.save(devEntity);
        return devEntity;

    }

    private PolicyDevelopmentEntity updateDevEntityPQL(
            PolicyDevelopmentEntity entity, PolicyDevelopmentStatus status) throws ConsoleException, IOException, PQLException {
        PolicyDevelopmentEntity devEntity = devEntityMgmtService
                .findById(entity.getId());

        // update or insert record in Deployment entities table
        devEntity.setLastUpdatedDate(System.currentTimeMillis());
        if(PolicyDevelopmentStatus.DELETED.equals(status)) {
            devEntity.setFolderId(null);
        }
        devEntity.setStatus(status.getKey());

        String updatedPql = " ";
        if (devEntity.getType().equalsIgnoreCase("PO")) {
            PolicyPQLHelper pqlHelper = PolicyPQLHelper.create();
            PolicyDTO policyDTO = pqlHelper.fromPQL(devEntity.getPql());
            policyDTO.setStatus(status.name());
            updatedPql = pqlHelper.getPQL(devEntity.getId(),
                    devEntity.getTitle(), devEntity.getDescription(),
                    policyDTO);
        } else if (devEntity.getType().equalsIgnoreCase(DevEntityType.LOCATION.toString())) {
            updatedPql = devEntity.getPql(); // TODO Is this correct?
        } else {
            ComponentPQLHelper pqlHelper = ComponentPQLHelper.create();
            ComponentDTO componentDTO = pqlHelper.fromPQL(devEntity.getPql());
            componentDTO.setStatus(status.name());
            populateExtendedDesc(devEntity.getExtendedDescription(),
                    componentDTO);

            PolicyModel policyModel = null;
            if (componentDTO.getPolicyModel() != null
                    && componentDTO.getPolicyModel().getId() != 0) {
                policyModel = policyModelService
                        .findById(componentDTO.getPolicyModel().getId());
                updatedPql = pqlHelper.getPQL(devEntity.getId(),
                        devEntity.getTitle(), devEntity.getDescription(),
                        SpecType.RESOURCE, componentDTO, policyModel,
                        policyModel.getType());
            } else {
                updatedPql = pqlHelper.getPQL(devEntity.getId(),
                        devEntity.getTitle(), devEntity.getDescription(),
                        SpecType.RESOURCE, componentDTO, policyModel,
                        PolicyModelType.RESOURCE);
            }

        }

        devEntity.setSubmittedTime(currentTimeMillis());
        devEntity.setSubmitter(getCurrentUser().getUserId());
        devEntity.setPql(updatedPql);
        devEntityMgmtService.save(devEntity);
        return devEntity;
    }

    private void populateExtendedDesc(String extendedDesc,
            ComponentDTO component) throws IOException {
        if (StringUtils.isNotEmpty(extendedDesc)) {
            ObjectMapper objectMapper = new ObjectMapper();
            ComponentExtDescription componentDesc = objectMapper
                    .readValue(extendedDesc, ComponentExtDescription.class);

            PolicyModelDTO policyModel = new PolicyModelDTO();
            policyModel.setId(componentDesc.getPolicyModelId());
            component.setPolicyModel(policyModel);
            component.setPreCreated(componentDesc.isPreCreated());
        }
    }

}
