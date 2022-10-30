/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 12, 2015
 *
 */
package com.nextlabs.destiny.console.controllers.notification;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.notification.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for notification management
 *
 *
 * @author Mohammed Sainal Shah
 * @since 2020.10
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("notification/mgmt")
@Api(tags = {"Notification Search Controller"})
@SwaggerDefinition(tags = { @Tag(name = "Notification Management Controller", description = "REST APIs for managing notification") })
public class NotificationMgmtController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationMgmtController.class);

    private MessageBundleService msgBundle;

    private NotificationService notificationService;


    /**
     * Marks a notification as read
     *
     * @return {@link SimpleResponseDTO}
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "markAsRead/{notificationId}")
    @ApiOperation(value = "Marks a notification as read.")
    public ConsoleResponseEntity<ResponseDTO> markAsRead(
            @ApiParam(value = "The ID of the notification.", required = true) @PathVariable("notificationId") Long notificationId) throws ConsoleException {

        logger.debug("Request came to mark notification as read");

        notificationService.markAsRead(notificationId);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"));

        logger.info("Notification marked as read. Response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @Autowired
    public void setMsgBundle(MessageBundleService msgBundle) {
        this.msgBundle = msgBundle;
    }

    @Autowired
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}
