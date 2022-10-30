/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 21, 2020
 *
 */
package com.nextlabs.destiny.console.controllers.scim;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

import com.bettercloud.scim2.common.exceptions.BadRequestException;
import com.bettercloud.scim2.common.messages.ErrorResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import com.bettercloud.scim2.common.exceptions.ScimException;
import com.bettercloud.scim2.common.messages.ListResponse;
import com.bettercloud.scim2.common.messages.PatchRequest;
import com.bettercloud.scim2.server.annotation.ScimResource;
import com.bluejungle.pf.destiny.services.PolicyEditorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nextlabs.destiny.console.controllers.AbstractRestController;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.model.scim.UserResource;
import com.nextlabs.destiny.console.model.scim.UserResourceExtension;
import com.nextlabs.destiny.console.services.scim.ScimUserServiceImpl;

@ScimResource(description = "Access User Resources", name = "User", schema = UserResource.class,
        requiredSchemaExtensions = UserResourceExtension.class)
@RestController
@RequestMapping("/scim/v2/Users")
public class ScimUserController extends AbstractRestController {

    private static final Logger log = LoggerFactory.getLogger(ScimUserController.class);

    @Autowired
    private ScimUserServiceImpl scimUserService;

    @GetMapping
    public ConsoleResponseEntity<ListResponse<UserResource>> getUsers()
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, URISyntaxException, ScimException {
        log.debug("in scim user get");
        return ConsoleResponseEntity.get(
                scimUserService.getUsers(),
                HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ConsoleResponseEntity<UserResource> getUser(@PathVariable("userId")
    Long userId) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, URISyntaxException, ScimException {
        log.debug("in scim user get");
        return ConsoleResponseEntity.get(
                scimUserService.getUser(userId),
                HttpStatus.OK);
    }

    @PostMapping
    public ConsoleResponseEntity<UserResource> addUser(@RequestBody
    UserResource user) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, URISyntaxException,
            ScimException, PolicyEditorException {
        return ConsoleResponseEntity.get(
                scimUserService.addUser(user),
                HttpStatus.CREATED);
    }

    @PutMapping
    public ConsoleResponseEntity<UserResource> replaceUser(@RequestBody
    UserResource user) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, URISyntaxException, ScimException, PolicyEditorException {
        if (user == null || StringUtils.isBlank(user.getId())){
            throw BadRequestException.invalidSyntax("id is a required attribute and is missing from the request");
        }
        return ConsoleResponseEntity.get(
                scimUserService.updateUser(user),
                HttpStatus.OK);
    }

    @PatchMapping("/{userId}")
    public ConsoleResponseEntity<UserResource> updateUser(@PathVariable("userId")
    Long userId, @RequestBody
    PatchRequest req) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, URISyntaxException, ScimException,
            JsonProcessingException, PolicyEditorException, NoSuchFieldException {
        return ConsoleResponseEntity.get(
                scimUserService.updateUser(userId, req),
                HttpStatus.OK);

    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId")
    Long userId, HttpServletResponse response) throws ScimException, PolicyEditorException {
        scimUserService.deleteUser(userId);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
    protected Logger getLog() {
        return log;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ConsoleResponseEntity<ErrorResponse> handleInvalidOperationException(HttpMessageNotReadableException e) {
        getLog().error(e.getMessage(), e);
        getLog().info("Exception while performing SCIM operation, [messages:{}]", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value());
        errorResponse.setDetail("Request is unparsable, syntactically incorrect, or violates schema");
        return ConsoleResponseEntity.get(errorResponse, HttpStatus.resolve(errorResponse.getStatus()));
    }

}
