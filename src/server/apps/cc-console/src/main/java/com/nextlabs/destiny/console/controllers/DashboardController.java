/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 29, 2016
 *
 */
package com.nextlabs.destiny.console.controllers;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.services.DashboardService;

/**
 * REST Controller for Dashboard controller
 *
 * @author Amila Silva
 * @since 8.0
 */
@RestController
@ApiVersion(1)
@RequestMapping("/dashboard")
public class DashboardController extends AbstractRestController {

    private static final Logger log = LoggerFactory
            .getLogger(DashboardController.class);

    @Autowired
    private DashboardService dashboardService;

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/systemDetails")
    public ConsoleResponseEntity<ResponseDTO> systemDetails()
            throws ConsoleException {

        log.debug("Request came to load system details");

        JSONObject systemDetails = dashboardService.getSystemDetails();

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), systemDetails);

        log.info("Requested system details loaded and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/enrollmentDetails")
    public ConsoleResponseEntity<ResponseDTO> enrollmentDetails()
            throws ConsoleException {

        log.debug("Request came to load enrollment details");

        JSONObject enrollmentDetails = dashboardService.getEnrollmentDetails();

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), enrollmentDetails);

        log.info("Requested enrollment details loaded and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/systemConfigStatus")
    public ConsoleResponseEntity<ResponseDTO> systemConfigStatus()
            throws ConsoleException {

        log.debug("Request came toload system configuration status details");

        JSONObject sysConfig = dashboardService.getSystemConfigDetails();

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), sysConfig);

        log.info(
                "Requested system configuration status details loaded and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/pdpThroughput/{fromDate}/{toDate}/{groupBy}")
    public ConsoleResponseEntity<ResponseDTO> pdpThroughput(
            @PathVariable("fromDate") Long fromDate,
            @PathVariable("toDate") Long toDate,
            @PathVariable("groupBy") String groupBy) throws ConsoleException {

        log.debug("Request came to load PDP through put details");
        validations.assertNotZero(fromDate, "fromDate");
        validations.assertNotZero(toDate, "toDate");
        validations.assertNotBlank(groupBy, "groupBy");

        JSONObject pdpThroughput = dashboardService
                .getPDPThroughputDetails(fromDate, toDate, groupBy);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), pdpThroughput);

        log.info("Requested PDP throughput details loaded and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/alertsSummary/{fromDate}/{toDate}/{groupBy}")
    public ConsoleResponseEntity<ResponseDTO> alertSummart(
            @PathVariable("fromDate") Long fromDate,
            @PathVariable("toDate") Long toDate,
            @PathVariable("groupBy") String groupBy) throws ConsoleException {

        log.debug("Request came to load alert summary");
        validations.assertNotZero(fromDate, "fromDate");
        validations.assertNotZero(toDate, "toDate");
        validations.assertNotBlank(groupBy, "groupBy");

        JSONObject alertsSummary = dashboardService.getAlertSummary(fromDate,
                toDate, groupBy);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), alertsSummary);

        log.info("Requested alert summary details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/userActivities/{fromDate}/{toDate}/{decision}")
    public ConsoleResponseEntity<ResponseDTO> alertSummart(
            @PathVariable("fromDate") Long fromDate,
            @PathVariable("toDate") Long toDate,
            @PathVariable("decision") String decision,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws ConsoleException {

        log.debug("Request came to load user activity summary");
        validations.assertNotZero(fromDate, "fromDate");
        validations.assertNotZero(toDate, "toDate");
        validations.assertNotBlank(decision, "decision");

        JSONObject activitySummary = dashboardService
                .getActivitySummaryByUser(fromDate, toDate, decision, pageSize);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), activitySummary);

        log.info(
                "Requested user activity summary details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/activityByResources/{fromDate}/{toDate}")
    public ConsoleResponseEntity<ResponseDTO> activityByResources(
            @PathVariable("fromDate") Long fromDate,
            @PathVariable("toDate") Long toDate,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws ConsoleException {

        log.debug("Request came to load resource activity summary");
        validations.assertNotZero(fromDate, "fromDate");
        validations.assertNotZero(toDate, "toDate");

        JSONObject activitySummary = dashboardService
                .getActivitySummaryByResource(fromDate, toDate, pageSize);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), activitySummary);

        log.info(
                "Requested resource activity summary details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/activityByPolicies/{fromDate}/{toDate}")
    public ConsoleResponseEntity<ResponseDTO> activityByPolicies(
            @PathVariable("fromDate") Long fromDate,
            @PathVariable("toDate") Long toDate,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws ConsoleException {

        log.debug("Request came to load activities by policy summary");
        validations.assertNotZero(fromDate, "fromDate");
        validations.assertNotZero(toDate, "toDate");

        JSONObject activitySummary = dashboardService
                .getActivitySummaryByPolicy(fromDate, toDate, pageSize);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), activitySummary);

        log.info(
                "Requested policy activity summary details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/notMatchingPolicies/{fromDate}/{toDate}")
    public ConsoleResponseEntity<ResponseDTO> notMatchingPolicies(
            @PathVariable("fromDate") Long fromDate,
            @PathVariable("toDate") Long toDate,
            @RequestParam(value = "pageSize", defaultValue = "1000", required = false) int pageSize)
            throws ConsoleException {

        log.debug(
                "Request came to load non matching policies for given period");
        validations.assertNotZero(fromDate, "fromDate");
        validations.assertNotZero(toDate, "toDate");

        JSONObject activitySummary = dashboardService
                .getNotMatchingPolicies(fromDate, toDate, pageSize);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), activitySummary);

        log.info("Requested non matching policies loaded and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/policySummaryByStatus")
    public ConsoleResponseEntity<ResponseDTO> policySummaryByStatus()
            throws ConsoleException {

        log.debug("Request came to load policy summary by status");

        JSONObject policySummary = dashboardService.policySummaryByStatus();

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), policySummary);

        log.info("Requested policy summary details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/policySummaryByStatus/{fromDate}/{toDate}")
    public ConsoleResponseEntity<ResponseDTO> policySummaryByStatus(
            @PathVariable("fromDate") long fromDate,
            @PathVariable("toDate") long toDate) throws ConsoleException {

        log.debug(
                "Request came to load policy summary by status for a given period");

        JSONObject policySummary = dashboardService
                .policySummaryByStatus(fromDate, toDate);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), policySummary);

        log.info("Requested policy summary details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/policySummaryByTags")
    public ConsoleResponseEntity<ResponseDTO> policySummaryByTags(
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws ConsoleException {

        log.debug("Request came to load policies by tags");

        JSONObject policySummary = dashboardService
                .policySummaryByTags(pageSize);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), policySummary);

        log.info("Requested policies by tags found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/activityStream")
    public ConsoleResponseEntity<ResponseDTO> activityStream(
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws ConsoleException {

        log.debug("Request came to load activity stream");

        JSONObject activityStream = dashboardService
                .getActivityStream(pageSize);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), activityStream);

        log.info("Requested activity stream data found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @Override
    protected Logger getLog() {
        return log;
    }

}
