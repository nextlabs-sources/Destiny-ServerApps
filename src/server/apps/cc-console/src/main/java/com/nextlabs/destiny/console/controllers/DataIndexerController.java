/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 31, 2016
 *
 */
package com.nextlabs.destiny.console.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.services.DataIndexerService;

/**
 *
 * Search data re-indexing controller.
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@RestController
@RequestMapping("/reIndexer")
public class DataIndexerController extends AbstractRestController {

    private static final Logger log = LoggerFactory
            .getLogger(DataIndexerController.class);

    @Autowired
    private DataIndexerService dataIndexerService;

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ConsoleResponseEntity<ResponseDTO> reIndexerAll()
            throws ConsoleException {

        log.debug("Request came to re-index search data");

        long startTime = System.currentTimeMillis();
        dataIndexerService.indexData();
        long processingTime = System.currentTimeMillis() - startTime;

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.reindexed.code"),
                msgBundle.getText("success.data.reindexed"));

        log.info(
                "Search data indexing has been completed, Data indexed in {} milis",
                processingTime);
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{indexName}")
    public ConsoleResponseEntity<ResponseDTO> reIndexerAll(
            @PathVariable("indexName") String indexName)
                    throws ConsoleException {

        log.debug("Request came to re-index, index : {}", indexName);

        long startTime = System.currentTimeMillis();
        dataIndexerService.indexByName(indexName);
        long processingTime = System.currentTimeMillis() - startTime;

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.reindexed.code"),
                msgBundle.getText("success.data.reindexed"));

        log.info(
                "Search data indexing has been completed, Data indexed in {} milis",
                processingTime);
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @Override
    protected Logger getLog() {
        return log;
    }

}
