/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 25, 2016
 *
 */
package com.nextlabs.destiny.console.controllers.delegadmin;

import static com.nextlabs.destiny.console.enums.DevEntityType.DELEGATION_COMPONENT;

import javax.annotation.Resource;

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
import com.nextlabs.destiny.console.controllers.AbstractRestController;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus;
import com.nextlabs.destiny.console.exceptions.CircularReferenceException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.search.repositories.DelegateModelSearchRepository;
import com.nextlabs.destiny.console.services.delegadmin.DelegationComponentSearchService;
import com.nextlabs.destiny.console.services.policy.ComponentMgmtService;

/**
 * Controller for handle and manage all delegation component CRUD operations
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("/delegationAdmin/component/mgmt")
public class DelegationComponentController extends AbstractRestController {

    private static final Logger log = LoggerFactory
            .getLogger(DelegationComponentController.class);

    @Autowired
    private ComponentMgmtService componentMgmtService;

    @Autowired
    private DelegationComponentSearchService delegationComponentSearchService;

    @Resource
    private DelegateModelSearchRepository delegateModelSearchRepository;

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/add")
    public ConsoleResponseEntity<ResponseDTO> createComponent(
            @RequestBody ComponentDTO componentDTO) throws ConsoleException {

        log.debug("Request came to add new Delegation component");

        validations.assertNotBlank(componentDTO.getName(), "Name");
        validations.assertNotBlank(componentDTO.getType(), "Type");
        validations.assertNotBlank(componentDTO.getStatus(), "Status");

        componentDTO.setCategory(DELEGATION_COMPONENT);
        componentDTO.setReIndexAllNow(false);
        ResponseDTO response;
        
        try {
			componentMgmtService.save(componentDTO);
			delegationComponentSearchService.reIndexComponent(componentDTO.getId());
			response = SimpleResponseDTO.create(
	                msgBundle.getText("success.data.saved.code"),
	                msgBundle.getText("success.data.saved"), componentDTO.getId());

	        log.info(
	                "New Delegation component saved successfully and response sent");
		} catch (CircularReferenceException e) {
			response = ResponseDTO.create(msgBundle.getText(e.getStatusCode()), msgBundle.getText(e.getStatusMsg()));
		}
  
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/modify")
    public ConsoleResponseEntity<ResponseDTO> modifyComponent(
            @RequestBody ComponentDTO componentDTO) throws ConsoleException {

        log.debug("Request came to modfiy Delegation component");

        validations.assertNotBlank(componentDTO.getName(), "Name");
        validations.assertNotBlank(componentDTO.getType(), "Type");
        validations.assertNotBlank(componentDTO.getStatus(), "Status");

        componentDTO.setCategory(DELEGATION_COMPONENT);
        componentDTO.setReIndexAllNow(false);
        ResponseDTO response;
        
        try {
			componentMgmtService.modify(componentDTO);
			delegationComponentSearchService.reIndexComponent(componentDTO.getId());
			response = SimpleResponseDTO.create(
	                msgBundle.getText("success.data.saved.code"),
	                msgBundle.getText("success.data.saved"), componentDTO.getId());
	        log.info(
	                "Delegation component modified successfully and response sent");
		} catch (CircularReferenceException e) {
			response = ResponseDTO.create(msgBundle.getText(e.getStatusCode()), msgBundle.getText(e.getStatusMsg()));
		}

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/updateStatus")
    public ConsoleResponseEntity<ResponseDTO> updateStatus(
            @RequestBody ComponentDTO componentDTO) throws ConsoleException {

        log.debug(
                "Request came to update the status of the delegation component");

        validations.assertNotNull(componentDTO.getId(), "Id");
        validations.assertNotBlank(componentDTO.getType(), "Type");
        validations.assertNotBlank(componentDTO.getStatus(), "Status");

        PolicyDevelopmentStatus devStatus = PolicyDevelopmentStatus
                .get(componentDTO.getStatus());
        validations.assertNotNull(devStatus, "Status");

        ComponentDTO component = componentMgmtService
                .updateStatus(componentDTO.getId(), devStatus);

        delegationComponentSearchService.reIndexComponent(componentDTO.getId());

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.modified.code"),
                msgBundle.getText("success.data.modified"), component.getId());

        log.info(
                "Delegation component status updated successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}")
    public ConsoleResponseEntity<ResponseDTO> componentById(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to find Component by Id");

        validations.assertNotNull(id, "Id");
        ComponentDTO componentDTO = componentMgmtService.findById(id);

        if (componentDTO == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), componentDTO);

        log.info("Requested Component details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/remove/{id}")
    public ConsoleResponseEntity<ResponseDTO> removeById(@PathVariable("id") Long id) {
        log.debug("Request came to remove delegation component by Id");

        validations.assertNotNull(id, "Id");

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.deleted.code"),
                msgBundle.getText("success.data.deleted"));

        log.info(
                "Delegation component removed and successfully and response sent");
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
