/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 12, 2015
 *
 */
package com.nextlabs.destiny.console.controllers.policymigration;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.CollectionDataResponseDTO;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.policymgmt.RemoteEnvironmentLite;
import com.nextlabs.destiny.console.dto.policymgmt.SearchCriteriaDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.policymigration.RemoteEnvironmentSearchService;
import com.nextlabs.destiny.console.utils.ValidationUtils;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for remote environment search function
 *
 *
 * @author Mohammed Sainal Shah
 * @since 2020.08
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("remoteEnvironment/search")
@Api(tags = {"Remote Environment Search Controller"})
@SwaggerDefinition(tags = { @Tag(name = "Remote Environment Search Controller", description = "REST APIs for retrieving remote environments based on search criteria") })
public class RemoteEnvironmentSearchController {

    private static final Logger log = LoggerFactory
            .getLogger(RemoteEnvironmentSearchController.class);

    private RemoteEnvironmentSearchService remoteEnvironmentSearchService;

    private ValidationUtils validations;

    private MessageBundleService msgBundle;

    /**
     * Remote Env List handled by this method. This will return the remote environment list
     * according to given remote environment search criteria
     * 
     * @return List of remote environments to display
     * @throws ConsoleException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    @ApiOperation(value="Searches for a list of remote environments based on a given search criteria.",
            notes="Returns a list of remote environments that match the given search criteria")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data saved successfully")})
    public ConsoleResponseEntity<CollectionDataResponseDTO> policyLiteSearch(
            @RequestBody SearchCriteriaDTO criteriaDTO)
            throws ConsoleException {

        log.debug("Request came to remote environments search");
        validations.assertNotNull(criteriaDTO.getCriteria(), "criteria");
        SearchCriteria criteria = criteriaDTO.getCriteria();

        long startTime = System.currentTimeMillis();
        Page<RemoteEnvironmentLite> policyLitePage = remoteEnvironmentSearchService
                .findRemoteEnvironmentByCriteria(criteria);
        long processingTime = System.currentTimeMillis() - startTime;

        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"));
        response.setData(policyLitePage.getContent());
        response.setPageNo(criteria.getPageNo());
        response.setPageSize(criteria.getPageSize());
        response.setTotalPages(policyLitePage.getTotalPages());
        response.setTotalNoOfRecords(policyLitePage.getTotalElements());

        log.info(
                "Remote environment search has been completed, Search handled in {} milis, Total no of records : {}",
                processingTime, policyLitePage.getNumberOfElements());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @Autowired
    public void setRemoteEnvironmentSearchService(RemoteEnvironmentSearchService remoteEnvironmentSearchService) {
        this.remoteEnvironmentSearchService = remoteEnvironmentSearchService;
    }

    @Autowired
    public void setValidations(ValidationUtils validations) {
        this.validations = validations;
    }

    @Autowired
    public void setMsgBundle(MessageBundleService msgBundle) {
        this.msgBundle = msgBundle;
    }
}
