/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 12, 2015
 *
 */
package com.nextlabs.destiny.console.controllers.policyworkflow;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.CollectionDataResponseDTO;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.policymgmt.SearchCriteriaDTO;
import com.nextlabs.destiny.console.dto.policymgmt.policyworkflow.WorkflowRequestCommentLite;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.policyworkflow.WorkflowRequestCommentSearchService;
import com.nextlabs.destiny.console.utils.ValidationUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for workflow request comment search function
 *
 *
 * @author Mohammed Sainal Shah
 * @since 2020.10
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("policyWorkflowComment/search")
@Api(tags = {"Workflow Request Comment Search Controller"})
@SwaggerDefinition(tags = { @Tag(name = "Workflow Request Comment Search Controller", description = "REST APIs for retrieving workflow request comments based on search criteria") })
public class WorkflowRequestCommentSearchController {

    private static final Logger log = LoggerFactory
            .getLogger(WorkflowRequestCommentSearchController.class);

    private WorkflowRequestCommentSearchService workflowRequestCommentSearchService;

    private ValidationUtils validations;

    private MessageBundleService msgBundle;

    /**
     * Remote Env List handled by this method. This will return the workflow request comments list
     * according to given search criteria
     * 
     * @return List of workflow request comments
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    @ApiOperation(value="Searches for a list of workflow request comments based on a given search criteria.",
            notes="Returns a list of workflow request comments that match the given search criteria")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data saved successfully")})
    public ConsoleResponseEntity<CollectionDataResponseDTO> workflowRequestCommentSearch(
            @RequestBody SearchCriteriaDTO criteriaDTO)
            throws ConsoleException {

        log.debug("Request came to workflow request comments search");
        validations.assertNotNull(criteriaDTO.getCriteria(), "criteria");
        SearchCriteria criteria = criteriaDTO.getCriteria();

        long startTime = System.currentTimeMillis();
        Page<WorkflowRequestCommentLite> policyLitePage = workflowRequestCommentSearchService
                .findWorkflowRequestCommentsByCriteria(criteria);
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
                "Workflow Request Comment search has been completed, Search handled in {} milliseconds, Total no of records : {}",
                processingTime, policyLitePage.getNumberOfElements());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @Autowired
    public void setWorkflowRequestCommentSearchService(WorkflowRequestCommentSearchService workflowRequestCommentSearchService) {
        this.workflowRequestCommentSearchService = workflowRequestCommentSearchService;
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
