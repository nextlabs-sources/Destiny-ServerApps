/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 14, 2020
 *
 */
package com.nextlabs.destiny.console.controllers.tool;

import static com.nextlabs.destiny.console.enums.SavedSearchType.PROPERTY;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bluejungle.pf.destiny.parser.PQLException;
import com.mchange.util.DuplicateElementException;
import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.controllers.AbstractRestController;
import com.nextlabs.destiny.console.dto.common.CollectionDataResponseDTO;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SavedSearchDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SearchCriteriaDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SearchFieldsDTO;
import com.nextlabs.destiny.console.dto.tool.PropertyDTO;
import com.nextlabs.destiny.console.enums.SharedMode;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.InvalidOperationException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.model.SavedSearch;
import com.nextlabs.destiny.console.services.SavedSearchService;
import com.nextlabs.destiny.console.services.tool.impl.PropertyManagerServiceImpl;
import com.nextlabs.destiny.console.utils.SystemCodes;

@RestController
@ApiVersion(1)
@RequestMapping("/tools/property/")
public class PropertyManagerController extends AbstractRestController {
    private static final Logger log = LoggerFactory.getLogger(PropertyManagerController.class);

    @Autowired
    PropertyManagerServiceImpl propertyManagerService;

    @Autowired
    private SavedSearchService savedSearchService;

    @Override
    protected Logger getLog() {
        return log;
    }

    @ResponseBody
    @GetMapping("findAll")
    public ConsoleResponseEntity<CollectionDataResponseDTO> findAll() throws ConsoleException {
    	log.debug("Request came to fetch all properties");
    	
    	List<PropertyDTO> properties = propertyManagerService.findAll();
    	CollectionDataResponseDTO response =
                CollectionDataResponseDTO.create(msgBundle.getText("success.data.found.code"),
                        msgBundle.getText("success.data.found"));
        response.setData(properties);
    	 return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }
    
    @ResponseBody
    @PostMapping("save")
    public ConsoleResponseEntity<ResponseDTO> saveProperty(@RequestBody
    PropertyDTO property) throws ConsoleException {
        log.debug("Request came to save a property");
        validations.assertNotAttributeKeyword("Property display name", property.getLabel());
        validations.assertNotAttributeKeyword("Property logical name", property.getName());
        ResponseDTO response;

        try {
            Long id = propertyManagerService.save(property);
            response = SimpleResponseDTO.create(msgBundle.getText("success.data.saved.code"),
                    msgBundle.getText("success.data.saved"), id);

        } catch (DuplicateElementException e) {
        	log.error(e.getMessage(), e);
            response = SimpleResponseDTO.create(msgBundle.getText("failed.data.saved.code"),
                    e.getMessage());

        } catch (PQLException e) {
        	log.error(e.getMessage(), e);
            response = SimpleResponseDTO.create(msgBundle.getText("failed.data.saved.code"),
                    msgBundle.getText("failed.data.saved"));
        } catch (InvalidOperationException e) {
        	log.error(e.getMessage(), e);
            response = SimpleResponseDTO.create(e.getStatusCode(), e.getStatusMsg());
        }

        return ConsoleResponseEntity.get(response, HttpStatus.OK);

    }

    @ResponseBody
    @DeleteMapping("delete")
    public ConsoleResponseEntity<ResponseDTO> delete(@RequestBody
    PropertyDTO property) throws ConsoleException {
        log.debug("Request came to delete a property");
        ResponseDTO response;
        try {
            boolean deleted = propertyManagerService.delete(property);
            response = deleted
                    ? SimpleResponseDTO.create(msgBundle.getText("success.data.deleted.code"),
                            msgBundle.getText("success.data.deleted"))
                    : SimpleResponseDTO.create(msgBundle.getText("property.delete.failed.code"),
                            msgBundle.getText("property.delete.failed"));
        } catch (PQLException e) {
        	log.error(e.getMessage(), e);
            response = SimpleResponseDTO.create(msgBundle.getText("task.delete.failed.code"),
                    msgBundle.getText("task.delete.failed"));
        }
        return ConsoleResponseEntity.get(response, HttpStatus.OK);

    }

    @ResponseBody
    @PostMapping("bulkDelete")
    public ConsoleResponseEntity<ResponseDTO> bulkDelete(@RequestBody
    List<PropertyDTO> propertyList) throws ConsoleException {
        log.debug("Request came to bulk delete properties");
        ResponseDTO response;
        int deletedNum = 0;
        try {
            deletedNum = propertyManagerService.bulkDelete(propertyList);

            response = SimpleResponseDTO.create(msgBundle.getText("success.data.deleted.code"),
                    msgBundle.getText("success.data.deleted"), deletedNum);
        } catch (PQLException e) {
        	log.error(e.getMessage(), e);
            response = SimpleResponseDTO.create(msgBundle.getText("task.delete.failed.code"),
                    msgBundle.getText("task.delete.failed"));
        }
        return ConsoleResponseEntity.get(response, HttpStatus.OK);

    }

    @ResponseBody
    @PostMapping("search")
    public ConsoleResponseEntity<CollectionDataResponseDTO> search(@RequestBody
    SearchCriteriaDTO searchCriteriaDTO) {

        Page<PropertyDTO> propertyList;
        CollectionDataResponseDTO response;
        SearchCriteria searchCriteria = searchCriteriaDTO.getCriteria();

        propertyList = propertyManagerService.search(searchCriteria);
        response = CollectionDataResponseDTO.create(
                msgBundle.getText(SystemCodes.DATA_FOUND_SUCCESS.getCode()),
                msgBundle.getText(SystemCodes.DATA_FOUND_SUCCESS.getMessageKey()));
        response.setPageNo(searchCriteria.getPageNo());
        response.setPageSize(searchCriteria.getPageSize());
        response.setTotalPages(propertyList.getTotalPages());
        response.setTotalNoOfRecords(propertyList.getTotalElements());
        response.setData(propertyList.getContent());

        return ConsoleResponseEntity.get(response, HttpStatus.OK);

    }

    @ResponseBody
    @PostMapping("search/add")
    public ConsoleResponseEntity<SimpleResponseDTO> savePropertySearch(@RequestBody
    SavedSearchDTO criteriaDTO) throws ConsoleException {
        log.debug("Request came to save property search criteria");

        SavedSearch criteria = new SavedSearch(null, criteriaDTO.getName(), criteriaDTO.getDesc(),
                criteriaDTO.getCriteriaJson(), Status.get(criteriaDTO.getStatus()),
                SharedMode.get(criteriaDTO.getSharedMode()), criteriaDTO.getUserIds(), PROPERTY);
        criteria.setLowercase_name(criteriaDTO.getName().toLowerCase());
        savedSearchService.saveCriteria(criteria);

        SimpleResponseDTO response =
                SimpleResponseDTO.create(msgBundle.getText("success.data.saved.code"),
                        msgBundle.getText("success.data.saved"), criteria.getId());

        log.info("New property search criteria saved successfully and response sent, [Id: {}]",
                criteria.getId());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("search/savedlist")
    public ConsoleResponseEntity<CollectionDataResponseDTO> findByAll(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false)
            int pageNo, @RequestParam(value = "pageSize", defaultValue = "10", required = false)
            int pageSize) throws ConsoleException {
        log.debug("Request came to find saved property criterias");

        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        Page<SavedSearch> criteriaPage =
                savedSearchService.findByNameOrDescriptionAndType("", PROPERTY, pageable);

        if (criteriaPage.getNumberOfElements() == 0) {
            throw new NoDataFoundException(msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        List<SavedSearchDTO> criteriaDTOs = new ArrayList<>(criteriaPage.getNumberOfElements());
        for (SavedSearch criteria : criteriaPage.getContent()) {
            SavedSearchDTO criteriaDTO = SavedSearchDTO.getDTO(criteria);
            criteriaDTOs.add(criteriaDTO);
        }

        CollectionDataResponseDTO response =
                CollectionDataResponseDTO.create(msgBundle.getText("success.data.found.code"),
                        msgBundle.getText("success.data.found"));
        response.setData(criteriaDTOs);
        response.setPageSize(pageable.getPageSize());
        response.setPageNo(pageable.getPageNumber());
        response.setTotalPages(criteriaPage.getTotalPages());
        response.setTotalNoOfRecords(criteriaPage.getTotalElements());

        log.info(
                "Requested property search criteria details found and response sent, [No of records :{}]",
                criteriaDTOs.size());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("search/savedlist/{name}")
    public ConsoleResponseEntity<CollectionDataResponseDTO> findByText(@PathVariable("name")
    String searchText, @RequestParam(value = "pageNo", defaultValue = "0", required = false)
    int pageNo, @RequestParam(value = "pageSize", defaultValue = "10", required = false)
    int pageSize) throws ConsoleException {
        log.debug("Request came to find saved property criterias");

        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        Page<SavedSearch> criteriaPage = savedSearchService.findByNameOrDescriptionAndType(
                searchText == null ? "" : searchText, PROPERTY, pageable);

        if (criteriaPage.getNumberOfElements() == 0) {
            throw new NoDataFoundException(msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        List<SavedSearchDTO> criteriaDTOs = new ArrayList<>(criteriaPage.getNumberOfElements());
        for (SavedSearch criteria : criteriaPage.getContent()) {
            SavedSearchDTO criteriaDTO = SavedSearchDTO.getDTO(criteria);
            criteriaDTOs.add(criteriaDTO);
        }

        CollectionDataResponseDTO response =
                CollectionDataResponseDTO.create(msgBundle.getText("success.data.found.code"),
                        msgBundle.getText("success.data.found"));
        response.setData(criteriaDTOs);
        response.setPageSize(pageable.getPageSize());
        response.setPageNo(pageable.getPageNumber());
        response.setTotalPages(criteriaPage.getTotalPages());
        response.setTotalNoOfRecords(criteriaPage.getTotalElements());

        log.info(
                "Requested property search criteria details found and response sent, [No of records :{}]",
                criteriaDTOs.size());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @DeleteMapping(value = "search/remove/{id}")
    public ConsoleResponseEntity<ResponseDTO> removeCriteria(@PathVariable("id")
    Long id) throws ConsoleException {

        log.debug("Request came to remove a criteria");
        validations.assertNotZero(id, "id");

        savedSearchService.removeCriteria(id);

        ResponseDTO response = ResponseDTO.create(msgBundle.getText("success.data.deleted.code"),
                msgBundle.getText("success.data.deleted"));

        log.info("saved property search criteria removed successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);

    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "fields")
    public ConsoleResponseEntity<ResponseDTO> searchFields() {

        SimpleResponseDTO<SearchFieldsDTO> response;
        SearchFieldsDTO searchFields;
        try {
            searchFields = propertyManagerService.searchFields();
            response =
                    SimpleResponseDTO.createWithType(msgBundle.getText("success.data.loaded.code"),
                            msgBundle.getText("success.data.loaded"), searchFields);
        } catch (ConsoleException e) {
            response = SimpleResponseDTO.create(msgBundle.getText("failed.data.saved.code"),
                    msgBundle.getText("failed.data.saved"), e.getMessage());
        }

        return ConsoleResponseEntity.get(response, HttpStatus.OK);

    }


}
