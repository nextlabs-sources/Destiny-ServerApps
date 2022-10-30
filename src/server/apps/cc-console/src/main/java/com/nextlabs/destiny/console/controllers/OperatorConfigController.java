/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 30, 2015
 *
 */
package com.nextlabs.destiny.console.controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.CollectionDataResponseDTO;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.OperatorConfigDTO;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.enums.DataType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.model.policy.OperatorConfig;
import com.nextlabs.destiny.console.services.policy.OperatorConfigService;

import io.swagger.annotations.ApiOperation;

/**
 *
 * REST Controller for Data Type and Operator Configuration
 *
 * @author Amila Silva
 * @author Aishwarya
 * @since 8.0
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("/config/dataType")
public class OperatorConfigController extends AbstractRestController {

    private static final Logger log = LoggerFactory
            .getLogger(OperatorConfigController.class);

    @Autowired
    private OperatorConfigService configService;

    @SuppressWarnings("unchecked")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/add")
    public ConsoleResponseEntity<SimpleResponseDTO<Long>> addOperatorConfig(
            @RequestBody OperatorConfigDTO configDto) throws ConsoleException {

        log.debug("Request came to add new operator configuration");

        validations.assertNotBlank(configDto.getKey(), "key");
        validations.assertNotBlank(configDto.getLabel(), "label");
        validations.assertNotBlank(configDto.getDataType(), "dataType");

        OperatorConfig operatorConfig = new OperatorConfig(null,
                configDto.getKey(), configDto.getLabel(),
                DataType.get(configDto.getDataType()));

        OperatorConfig savedConfig = configService.save(operatorConfig);

        SimpleResponseDTO<Long> response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), savedConfig.getId());

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/modify/{id}")
    public ConsoleResponseEntity<ResponseDTO> modifyOperatorConfig(
            @RequestBody OperatorConfigDTO configDto) throws ConsoleException {

        log.debug("Request came to modify operator configuration");

        validations.assertNotBlank(configDto.getKey(), "key");
        validations.assertNotBlank(configDto.getLabel(), "label");
        validations.assertNotBlank(configDto.getDataType(), "dataType");

        OperatorConfig operatorConfig = configService
                .findById(configDto.getId());
        operatorConfig.setKey(configDto.getKey());
        operatorConfig.setLabel(configDto.getLabel());
        operatorConfig.setDataType(DataType.get(configDto.getDataType()));

        configService.save(operatorConfig);

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.modified.code"),
                msgBundle.getText("success.data.modified"));

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/remove/{id}")
    public ConsoleResponseEntity<ResponseDTO> removeOperatorConfig(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to remove operator config");
        validations.assertNotZero(id, "id");

        configService.removeOperatorConfig(id);

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.deleted.code"),
                msgBundle.getText("success.data.deleted"));

        log.info(
                "Operator configuration removed successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/details/{id}")
    public ConsoleResponseEntity<ResponseDTO> getById(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to get operator config by Id, [id : {}]", id);
        validations.assertNotZero(id, "id");

        OperatorConfig operatorConfig = configService.findById(id);

        if (operatorConfig == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        OperatorConfigDTO configDto = OperatorConfigDTO.getDTO(operatorConfig);
        configDto.setLabel(msgBundle.getText(configDto.getLabel()));

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), configDto);

        log.info("Requested operator details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/list")
    public ConsoleResponseEntity<CollectionDataResponseDTO> findAll()
            throws ConsoleException {

        log.debug("Request came to list all actions");
        List<OperatorConfig> operators = configService.findAll();

        if (operators.isEmpty()) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        List<OperatorConfigDTO> configDtos = new ArrayList<>();
        for (OperatorConfig operator : operators) {
            OperatorConfigDTO operatorDto = OperatorConfigDTO.getDTO(operator);
            operatorDto.setLabel(msgBundle.getText(operatorDto.getLabel()));
            configDtos.add(operatorDto);
        }

        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"));
        response.setData(configDtos);
        response.setPageSize(configDtos.size());
        response.setPageNo(0);
        response.setTotalNoOfRecords(configDtos.size());

        return ConsoleResponseEntity.get(response, HttpStatus.OK);

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/list/{dataType}")
    @ApiOperation(value="Fetches the operators based on given type.", 
    	notes="Returns a list of operators of the given data type.")
    public ConsoleResponseEntity<CollectionDataResponseDTO> findByType(
            @PathVariable("dataType") String dataType) throws ConsoleException {

        log.debug("Request came to list operators by Data Type, [Type : {} ",
                dataType);

        List<OperatorConfig> operators = configService
                .findByDataType(DataType.get(dataType));

        if (operators.isEmpty()) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        List<OperatorConfigDTO> operatorDtos = new ArrayList<>();
        for (OperatorConfig operator : operators) {
            OperatorConfigDTO operatorDto = OperatorConfigDTO.getDTO(operator);
            operatorDto.setLabel(msgBundle.getText(operatorDto.getLabel()));
            operatorDtos.add(operatorDto);
        }

        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"));
        response.setData(operatorDtos);
        response.setPageSize(operatorDtos.size());
        response.setPageNo(0);
        response.setTotalNoOfRecords(operatorDtos.size());

        log.info(
                "Operators for given data type found and response sent , [No of records :{}]",
                operatorDtos.size());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/types")
    public ConsoleResponseEntity<CollectionDataResponseDTO> loadAllDataType()
            throws ConsoleException {

        log.debug("Request came to load all data types");

        List<DataType> dataTypes = configService.findAllDataTypes();

        if (dataTypes.isEmpty()) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"));
        response.setData(dataTypes);
        response.setPageSize(dataTypes.size());
        response.setPageNo(0);
        response.setTotalNoOfRecords(dataTypes.size());

        log.info("Data types found and response sent , [No of records :{}]",
                dataTypes.size());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @Override
    protected Logger getLog() {
        return log;
    }

}
