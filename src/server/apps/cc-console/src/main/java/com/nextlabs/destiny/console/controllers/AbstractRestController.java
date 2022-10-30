/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 6, 2015
 *
 */
package com.nextlabs.destiny.console.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.bettercloud.scim2.common.exceptions.ScimException;
import com.bettercloud.scim2.common.messages.ErrorResponse;
import com.nextlabs.destiny.console.dto.validation.ValidationErrorResponse;
import com.nextlabs.destiny.console.dto.validation.Violation;
import com.nextlabs.destiny.console.enums.LogMarker;
import com.nextlabs.destiny.console.exceptions.InvalidXacmlPolicyException;
import com.nextlabs.destiny.console.utils.SecurityContextUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.exceptions.ConnectionFailedException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.DirtyUpdateException;
import com.nextlabs.destiny.console.exceptions.EntityInUseException;
import com.nextlabs.destiny.console.exceptions.ForbiddenException;
import com.nextlabs.destiny.console.exceptions.InvalidInputParamException;
import com.nextlabs.destiny.console.exceptions.InvalidOperationException;
import com.nextlabs.destiny.console.exceptions.InvalidPasswordException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.exceptions.NotUniqueException;
import com.nextlabs.destiny.console.exceptions.ServerException;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.utils.ValidationUtils;

/**
 *
 * Abstract Rest Controller for handle generic functionality through out the
 * REST end points
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public abstract class AbstractRestController {

    @Autowired
    protected MessageBundleService msgBundle;

    @Autowired
    protected ValidationUtils validations;

    protected abstract Logger getLog();

    @ExceptionHandler(InvalidInputParamException.class)
    public ConsoleResponseEntity<ResponseDTO> handleInvalidInputParams(
            InvalidInputParamException e) {
        getLog().info(
                "Invalid request parameter, [ Error code :{}, Error messages:{}]",
                e.getStatusCode(), e.getStatusMsg());

        return ConsoleResponseEntity.get(
                ResponseDTO.create(e.getStatusCode(), e.getStatusMsg()),
                HttpStatus.OK);
    }

    @ExceptionHandler(NoDataFoundException.class)
    public ConsoleResponseEntity<ResponseDTO> handleNoModelFound(
            NoDataFoundException e) {
        getLog().info("No data model found, [ code :{}, messages:{}]",
                e.getStatusCode(), e.getStatusMsg());

        return ConsoleResponseEntity.get(
                ResponseDTO.create(e.getStatusCode(), e.getStatusMsg()),
                HttpStatus.OK);
    }
    
    @ExceptionHandler(DirtyUpdateException.class)
    public ConsoleResponseEntity<ResponseDTO> handleDirtyUpdates(
    		DirtyUpdateException e) {
        getLog().info("Dirty update, [ code :{}, messages:{}]",
                e.getStatusCode(), e.getStatusMsg());

        return ConsoleResponseEntity.get(
                ResponseDTO.create(e.getStatusCode(), e.getStatusMsg()),
                HttpStatus.OK);
    }
    
    @ExceptionHandler(NotUniqueException.class)
    public ConsoleResponseEntity<ResponseDTO> handleNameAlreadyExists(
            NotUniqueException e) {
        getLog().info("Name already exists, [ code :{}, messages:{}]",
                e.getStatusCode(), e.getStatusMsg());

        return ConsoleResponseEntity.get(
                ResponseDTO.create(e.getStatusCode(), e.getStatusMsg()),
                HttpStatus.OK);
    }

    @ExceptionHandler(InvalidXacmlPolicyException.class)
    public ConsoleResponseEntity<ResponseDTO> handleInvalidXacmlPolicy(
            InvalidXacmlPolicyException e) {
        getLog().info("Malformed Xacml Policy, [ code :{}, messages:{}]",
                e.getStatusCode(), e.getStatusMsg());

        return ConsoleResponseEntity.get(
                ResponseDTO.create(e.getStatusCode(), e.getStatusMsg()),
                HttpStatus.OK);
    }
    
    @ExceptionHandler(InvalidPasswordException.class)
    public ConsoleResponseEntity<ResponseDTO> handleInvalidPassword(
            InvalidPasswordException e) {
        getLog().info("Password is invalid, [ code :{}, messages:{}]",
                e.getStatusCode(), e.getStatusMsg());

        return ConsoleResponseEntity.get(
                ResponseDTO.create(e.getStatusCode(), e.getStatusMsg()),
                HttpStatus.OK);
    }

    @ExceptionHandler(ConnectionFailedException.class)
    public ConsoleResponseEntity<ResponseDTO> handleConnectionFailed(
            NotUniqueException e) {
        getLog().info("Connection to provider failed, [ code :{}, messages:{}]",
                e.getStatusCode(), e.getStatusMsg());

        return ConsoleResponseEntity.get(
                ResponseDTO.create(e.getStatusCode(), e.getStatusMsg()),
                HttpStatus.OK);
    }
    
    @ExceptionHandler(EntityInUseException.class)
    public ConsoleResponseEntity<ResponseDTO> handleEntityInUse(
            EntityInUseException e) {
        getLog().info("Entity in use, [ code :{}, messages:{}]",
                e.getStatusCode(), e.getStatusMsg());

        return ConsoleResponseEntity.get(
                ResponseDTO.create(e.getStatusCode(), e.getStatusMsg()),
                HttpStatus.OK);
    }
    
    @ExceptionHandler(ConsoleException.class)
    public ConsoleResponseEntity<ResponseDTO> errorHandler(
            ConsoleException exc) {
        getLog().error(exc.getMessage(), exc);
        getLog().error(LogMarker.SYSTEM, "Exception encountered. [message={}, userInfo={}]",
                exc.getMessage(), SecurityContextUtil.getUserInfo());
        return ConsoleResponseEntity.get(
                ResponseDTO.create(msgBundle.getText("server.error.code"),
                        msgBundle.getText("server.error", exc.getMessage())),
                HttpStatus.BAD_REQUEST);
    }

    @SuppressWarnings("rawtypes")
    @ExceptionHandler(ConstraintViolationException.class)
    public ConsoleResponseEntity<ResponseDTO> errorHandler(
            ConstraintViolationException exc) {
        getLog().error(exc.getMessage(), exc);
        getLog().error(LogMarker.SYSTEM, "Exception encountered. [message={}, userInfo={}]",
                exc.getMessage(), SecurityContextUtil.getUserInfo());
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (ConstraintViolation violation : exc.getConstraintViolations()) {
            error.getGlobalErrors().add(
                    new Violation(violation.getPropertyPath().toString(), violation.getMessage()));
        }
        return ConsoleResponseEntity.get(
                SimpleResponseDTO.create(msgBundle.getText("server.error.code"),
                        msgBundle.getText("server.error", "Validation failed"), error),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ConsoleResponseEntity<ResponseDTO> errorHandler(
            MethodArgumentNotValidException exc) {
        getLog().error(exc.getMessage(), exc);
        getLog().error(LogMarker.SYSTEM, "Exception encountered. [message={}, userInfo={}]",
                exc.getMessage(), SecurityContextUtil.getUserInfo());
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (FieldError fieldError : exc.getBindingResult().getFieldErrors()) {
            error.getFieldErrors().add(
                    new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        for (ObjectError globalError : exc.getBindingResult().getGlobalErrors()){
            error.getGlobalErrors().add(new Violation(globalError.getDefaultMessage()));
        }
        return ConsoleResponseEntity.get(
                SimpleResponseDTO.create(msgBundle.getText("server.error.code"),
                        msgBundle.getText("server.error", "Validation failed"), error),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ConsoleResponseEntity<ResponseDTO> handleForbiddenException(ForbiddenException e) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest servletRequest = servletRequestAttributes.getRequest();
            getLog().warn(LogMarker.SECURITY, "Not authorized access. [resource=[method={}, url={}], userInfo={}, remoteHost={}]",
                    servletRequest.getMethod(), servletRequest.getRequestURL(), SecurityContextUtil.getUserInfo(), servletRequest.getRemoteHost());
        }
        return ConsoleResponseEntity.get(
                SimpleResponseDTO.create(
                        msgBundle.getText(e.getErrorCode()),
                        msgBundle.getText("server.request.not.authorized"), e.getForbiddenResponse()),
                HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(InvalidOperationException.class)
    public ConsoleResponseEntity<ResponseDTO> handleInvalidOperationException(InvalidOperationException e) {
    	getLog().info("Invalid operation request, [ code :{}, messages:{}]", e.getStatusCode(), e.getStatusMsg());
    	
        return ConsoleResponseEntity.get(ResponseDTO.create(e.getStatusCode(), e.getStatusMsg()),
                HttpStatus.OK);
    }

    @ExceptionHandler(ScimException.class)
    public ConsoleResponseEntity<ErrorResponse> handleInvalidOperationException(ScimException e) {
    	getLog().info("Exception while performing SCIM operation, [ code :{}, messages:{}]", e.getScimError().getScimType(), e.getScimError().getDetail());

        return ConsoleResponseEntity.get(e.getScimError(), HttpStatus.resolve(e.getScimError().getStatus()));
    }
    
    @ExceptionHandler(ServerException.class)
    public ConsoleResponseEntity<ResponseDTO> errorHandler(ServerException exc) {
        getLog().error(exc.getMessage(), exc);
        getLog().error(LogMarker.SYSTEM, "Exception encountered. [message={}, userInfo={}]",
                exc.getMessage(), SecurityContextUtil.getUserInfo());
        return ConsoleResponseEntity.get(
                ResponseDTO.create(msgBundle.getText("server.error.code"),
                        msgBundle.getText("server.error", exc.getMessage())),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ConsoleResponseEntity<ResponseDTO> errorHandler(
            Exception ex) {
        getLog().error(ex.getMessage(), ex);
        getLog().error(LogMarker.SYSTEM, "Exception encountered. [message={}, userInfo={}]",
                ex.getMessage(), SecurityContextUtil.getUserInfo());
        return ConsoleResponseEntity.get(
                ResponseDTO.create(msgBundle.getText("server.error.code"),
                        msgBundle.getText("server.error", ex.getMessage())),
                HttpStatus.BAD_REQUEST);
    }
}
