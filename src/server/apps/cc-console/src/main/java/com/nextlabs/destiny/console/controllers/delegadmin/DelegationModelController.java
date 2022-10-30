/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 21, 2016
 *
 */
package com.nextlabs.destiny.console.controllers.delegadmin;

import static com.nextlabs.destiny.console.enums.PolicyModelType.get;
import static com.nextlabs.destiny.console.enums.Status.ACTIVE;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.controllers.AbstractRestController;
import com.nextlabs.destiny.console.dto.common.CollectionDataResponseDTO;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.LiteDTO;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyModelDTO;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.model.delegadmin.DelegateModel;
import com.nextlabs.destiny.console.model.policy.ActionConfig;
import com.nextlabs.destiny.console.model.policy.AttributeConfig;
import com.nextlabs.destiny.console.model.policy.ObligationConfig;
import com.nextlabs.destiny.console.search.repositories.DelegateModelSearchRepository;
import com.nextlabs.destiny.console.services.delegadmin.DelegateModelService;

import io.swagger.annotations.ApiOperation;

/**
 * Controller for handle and manage all delegation model CRUD operations
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("/delegationAdmin/model/mgmt")
public class DelegationModelController extends AbstractRestController {

    private static final Logger log = LoggerFactory
            .getLogger(DelegationModelController.class);

    @Autowired
    private DelegateModelService delegateModelService;

    @Resource
    private DelegateModelSearchRepository delegateModelSearchRepository;

    private static final String SHORT_CODE_PATTERN = "^[0-9a-zA-Z_]+$";
    private static final String EXPECTED_FORMAT = "alphanumeric characters and/or underscore";

    /**
     * Save new {@link DelegateModel}
     * 
     * @return {@link SimpleResponseDTO}
     * @throws ConsoleException
     */
    @SuppressWarnings("rawtypes")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/add")
    public ConsoleResponseEntity<SimpleResponseDTO> addDelegateModel(
            @RequestBody PolicyModelDTO pmDTO) throws ConsoleException {

        log.debug("Request came to add new delegate model");

        validations.assertNotNull(pmDTO, "delegation model");
        validations.assertNotBlank(pmDTO.getName(), "name");
        validations.assertNotBlank(pmDTO.getShortName(), "shortName");
        validations.assertMatches(pmDTO.getShortName(),
                Pattern.compile(SHORT_CODE_PATTERN), "shortName",
                "Alphanumeric chars and underscore");
        validations.assertNotBlank(pmDTO.getType(), "type");

        for (AttributeConfig attrConfig : pmDTO.getAttributes()) {
            validations.assertMatches(attrConfig.getShortName(),
                    Pattern.compile(SHORT_CODE_PATTERN), "Attribute short name", EXPECTED_FORMAT);
        }
        for (ActionConfig actionConfig : pmDTO.getActions()) {
            validations.assertMatches(actionConfig.getShortName(),
                    Pattern.compile(SHORT_CODE_PATTERN), "Action short name", EXPECTED_FORMAT);
        }
        for (ObligationConfig oblConfig : pmDTO.getObligations()) {
            validations.assertMatches(oblConfig.getShortName(),
                    Pattern.compile(SHORT_CODE_PATTERN),
                    "Obligation short name", EXPECTED_FORMAT);
        }

        DelegateModel model = new DelegateModel(null, pmDTO.getName(),
                pmDTO.getShortName(), pmDTO.getDescription(),
                get(pmDTO.getType()), ACTIVE);
        model.setAttributes(pmDTO.getAttributes());
        model.setActions(pmDTO.getActions());
        model.setObligations(pmDTO.getObligations());

        delegateModelService.save(model);

        SimpleResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), model.getId());

        log.info("New Delegation model saved successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Modify {@link DelegateModel}
     * 
     * @return {@link SimpleResponseDTO}
     * @throws ConsoleException
     */
    @SuppressWarnings("rawtypes")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/modify")
    public ConsoleResponseEntity<SimpleResponseDTO> modifyDelegatePolicy(
            @RequestBody PolicyModelDTO pmDTO) throws ConsoleException {

        log.debug("Request came to modify delegation model");

        validations.assertNotNull(pmDTO, "policyModel");
        validations.assertNotBlank(pmDTO.getName(), "name");
        validations.assertNotBlank(pmDTO.getShortName(), "shortName");
        validations.assertNotBlank(pmDTO.getType(), "type");

        for (AttributeConfig attrConfig : pmDTO.getAttributes()) {
            validations.assertMatches(attrConfig.getShortName(),
                    Pattern.compile(SHORT_CODE_PATTERN), "Attribute short name", EXPECTED_FORMAT);
        }
        for (ActionConfig actionConfig : pmDTO.getActions()) {
            validations.assertMatches(actionConfig.getShortName(),
                    Pattern.compile(SHORT_CODE_PATTERN), "Action short name", EXPECTED_FORMAT);
        }
        for (ObligationConfig oblConfig : pmDTO.getObligations()) {
            validations.assertMatches(oblConfig.getShortName(),
                    Pattern.compile(SHORT_CODE_PATTERN),
                    "Obligation short name", EXPECTED_FORMAT);
        }

        DelegateModel model = delegateModelService.findById(pmDTO.getId());
        model.setName(pmDTO.getName());
        model.setShortName(pmDTO.getShortName());
        model.setDescription(pmDTO.getDescription());
        model.setStatus(ACTIVE);
        model.setAttributes(pmDTO.getAttributes());
        model.setActions(pmDTO.getActions());
        model.setObligations(pmDTO.getObligations());

        delegateModelService.save(model);

        SimpleResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.modified.code"),
                msgBundle.getText("success.data.modified"), model.getId());

        log.info("Delegation model modified successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * View {@link DelegateModel}
     * 
     * @return {@link SimpleResponseDTO}
     * @throws ConsoleException
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}")
    public ConsoleResponseEntity<ResponseDTO> findById(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to find delegation policy model by Id");

        validations.assertNotNull(id, "Id");
        DelegateModel model = delegateModelService.findById(id);

        if (model == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        PolicyModelDTO policyModelDTO = PolicyModelDTO.getDTO(model);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), policyModelDTO);

        log.info("Requested Delegation model details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Remove {@link DelegateModel}
     * 
     * @return {@link SimpleResponseDTO}
     * @throws ConsoleException
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/remove/{id}")
    public ConsoleResponseEntity<ResponseDTO> deleteDelegateModel(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to delete delegation model by Id");

        validations.assertNotNull(id, "Id");
        boolean isRemoved = delegateModelService.remove(id);

        ResponseDTO response;

        if (isRemoved) {
            response = ResponseDTO.create(
                    msgBundle.getText("success.data.deleted.code"),
                    msgBundle.getText("success.data.deleted"));
            log.info("Delegation model removed successfully and response sent");
        } else {
            response = ResponseDTO.create(
                    msgBundle.getText("server.error.delete.not.allowed.code"),
                    msgBundle.getText("server.error.delete.not.allowed",
                            "delegation Model"));
            log.info(
                    "Delegation model not removed as it is referenced by a component");
        }
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Facet search will return the {@link DelegateModel} data with for a given type
     *
     * @param type     {@link PolicyModelType}
     * @param pageNo
     * @param pageSize
     * @return List of delegation models to display}
     * @throws ConsoleException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/listNames/{type}")
    public ConsoleResponseEntity<CollectionDataResponseDTO> findNamesByType(
            @PathVariable("type") String type,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws ConsoleException {

        log.debug("Request came to find delegation models by type ");
        validations.assertNotBlank(type, "Type");

        PageRequest pageable = PageRequest.of(pageNo, pageSize);

        long startTime = System.currentTimeMillis();
        Page<DelegateModel> modelPage = delegateModelService
                .findByType(PolicyModelType.get(type), pageable);
        long processingTime = System.currentTimeMillis() - startTime;

        List<LiteDTO> liteDTOS = new ArrayList<>();
        for (DelegateModel model : modelPage.getContent()) {
            if (StringUtils.isNotEmpty(model.getShortName())) {
                liteDTOS.add(new LiteDTO(model.getId(), model.getName()));
            }
        }

        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"));
        response.setData(liteDTOS);
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setTotalPages(modelPage.getTotalPages());
        response.setTotalNoOfRecords(modelPage.getTotalElements());

        log.info(
                "Delegation model search by type has been completed, Search handled in {} milis, Total no of records : {}",
                processingTime, modelPage.getNumberOfElements());

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Search will return the {@link DelegateModel} full details for a given type
     *
     * @param type     {@link PolicyModelType}
     * @param pageNo
     * @param pageSize
     * @return List of delegation models to display}
     * @throws ConsoleException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/details/{type}")
    @ApiOperation(value="Fetches delegation models by type.", 
    	notes = "Returns a list of delegation models based on the given delegation model type.")
    public ConsoleResponseEntity<CollectionDataResponseDTO> findDetailsByType(
            @PathVariable("type") String type,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws ConsoleException {

        log.debug("Request came to find delegation models details by type ");
        validations.assertNotBlank(type, "Type");

        PageRequest pageable = PageRequest.of(pageNo, pageSize);

        long startTime = System.currentTimeMillis();
        Page<DelegateModel> modelPage = delegateModelService
                .findByType(PolicyModelType.get(type), pageable);
        long processingTime = System.currentTimeMillis() - startTime;

        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"));
        response.setData(modelPage.getContent());
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setTotalPages(modelPage.getTotalPages());
        response.setTotalNoOfRecords(modelPage.getTotalElements());

        log.info(
                "Delegation model search by type has been completed, Search handled in {} milis, Total no of records : {}",
                processingTime, modelPage.getNumberOfElements());

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @Override
    protected Logger getLog() {
        return log;
    }

}
