/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 13, 2020
 *
 */
package com.nextlabs.destiny.console.controllers.scim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bluejungle.pf.destiny.services.PolicyEditorException;
import com.nextlabs.destiny.console.controllers.AbstractRestController;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.model.scim.BulkRequest;
import com.nextlabs.destiny.console.model.scim.BulkResponse;
import com.nextlabs.destiny.console.services.scim.ScimUserServiceImpl;

@RestController
@RequestMapping("/scim/v2/Bulk")
public class ScimBulkController  extends AbstractRestController {

    private static final Logger log = LoggerFactory.getLogger(ScimBulkController.class);

    @Autowired
    private ScimUserServiceImpl scimUserService;
    
    @PostMapping
    public ConsoleResponseEntity<BulkResponse> bulkRequest(@RequestBody
    BulkRequest br) throws PolicyEditorException {
        return ConsoleResponseEntity.get(new BulkResponse(scimUserService.bulkRequest(br)), HttpStatus.OK);
    }

    @Override
    protected Logger getLog() {
        return log;
    }
}
