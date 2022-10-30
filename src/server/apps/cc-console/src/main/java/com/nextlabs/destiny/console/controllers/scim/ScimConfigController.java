/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 21, 2020
 *
 */
package com.nextlabs.destiny.console.controllers.scim;

import com.bettercloud.scim2.common.GenericScimResource;
import com.bettercloud.scim2.common.exceptions.ScimException;
import com.bettercloud.scim2.common.messages.ListResponse;
import com.bettercloud.scim2.common.utils.ApiConstants;
import com.bettercloud.scim2.server.controller.discovery.ResourceTypesController;
import com.bettercloud.scim2.server.controller.discovery.SchemasController;
import com.bettercloud.scim2.server.controller.discovery.ServiceProviderConfigController;
import com.nextlabs.destiny.console.controllers.AbstractRestController;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scim/v2")
public class ScimConfigController extends AbstractRestController {

    private static final Logger log = LoggerFactory.getLogger(ScimConfigController.class);

    @Autowired
    private ServiceProviderConfigController serviceProviderConfigController;

    @Autowired
    private SchemasController schemasController;

    @Autowired
    private ResourceTypesController resourceTypesController;

    @GetMapping("/ServiceProviderConfig")
    public ConsoleResponseEntity<GenericScimResource> getServiceProviderConfig() throws ScimException {
        log.debug("in scim ServiceProviderConfig get");
        return ConsoleResponseEntity.get(serviceProviderConfigController.get(), HttpStatus.OK);
    }

    @GetMapping("/Schemas")
    public ConsoleResponseEntity<ListResponse<GenericScimResource>> getSchemas(@RequestParam(value = ApiConstants.QUERY_PARAMETER_FILTER, required = false) final String filterString)
            throws ScimException {
        log.debug("in scim Schemas get");
        return ConsoleResponseEntity.get(schemasController.search(filterString), HttpStatus.OK);
    }

    @GetMapping("/ResourceTypes")
    public ConsoleResponseEntity<ListResponse<GenericScimResource>> getResourceTypes(@RequestParam(value = ApiConstants.QUERY_PARAMETER_FILTER, required = false) final String filterString)
            throws ScimException {
        log.debug("in scim ResourceTypes get");
        return ConsoleResponseEntity.get(resourceTypesController.search(filterString), HttpStatus.OK)   ;
    }

    @Override
    protected Logger getLog() {
        return log;
    }
}
