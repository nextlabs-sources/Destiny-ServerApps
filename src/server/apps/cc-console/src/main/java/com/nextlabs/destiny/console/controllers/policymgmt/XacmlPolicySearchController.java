/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 12, 2015
 *
 */
package com.nextlabs.destiny.console.controllers.policymgmt;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.controllers.AbstractRestController;
import com.nextlabs.destiny.console.dto.common.CollectionDataResponseDTO;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.policymgmt.XacmlPolicyLite;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.services.policy.XacmlPolicySearchService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Policy search function
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("/xacmlPolicy/search")
@Api(tags = {"Policy Search Controller"})
@SwaggerDefinition(tags = { @Tag(name = "Policy Search Controller", description = "REST APIs for retrieving policies based on search criteria") })
public class XacmlPolicySearchController extends AbstractRestController {

    private static final Logger log = LoggerFactory
            .getLogger(XacmlPolicySearchController.class);

    @Autowired
    private XacmlPolicySearchService xacmlPolicySearchService;

    /**
     * Policy List handles by this method. This will return the policy list
     * according to given policy search criteria
     * 
     * @return List of policies to display
     * @throws ConsoleException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    @ApiOperation(value="Returns all xacml policy")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data saved successfully")})
    public ConsoleResponseEntity<CollectionDataResponseDTO> policyLiteSearch() throws ConsoleException {

        log.debug("Request came to xacml policy search");
        long startTime = System.currentTimeMillis();
        Page<XacmlPolicyLite> xacmlPolicyLitePage;
        try{
            xacmlPolicyLitePage = xacmlPolicySearchService.findAllXacmlPolicies();
        } catch (Exception e){
            throw new ConsoleException(e);
        }
        long processingTime = System.currentTimeMillis() - startTime;

        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"));
        response.setData(xacmlPolicyLitePage.getContent());
        response.setPageNo(0);
        response.setPageSize(xacmlPolicyLitePage.getNumberOfElements());
        response.setTotalPages(xacmlPolicyLitePage.getTotalPages());
        response.setTotalNoOfRecords(xacmlPolicyLitePage.getTotalElements());

        log.info(
                "Policy search has been completed, Search handled in {} milis, Total no of records : {}",
                processingTime, xacmlPolicyLitePage.getNumberOfElements());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.nextlabs.destiny.console.controllers.AbstractRestController#getLog()
     */
    @Override
    protected Logger getLog() {
        return log;
    }

}
