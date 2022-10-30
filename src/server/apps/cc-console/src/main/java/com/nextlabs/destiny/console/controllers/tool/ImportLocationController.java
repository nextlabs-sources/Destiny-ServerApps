/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 *
 */
package com.nextlabs.destiny.console.controllers.tool;

import static com.nextlabs.destiny.console.enums.SavedSearchType.LOCATION;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
import com.nextlabs.destiny.console.dto.tool.LocationDTO;
import com.nextlabs.destiny.console.enums.SharedMode;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.model.SavedSearch;
import com.nextlabs.destiny.console.services.SavedSearchService;
import com.nextlabs.destiny.console.services.tool.impl.ImportLocationServiceImpl;
import com.nextlabs.destiny.console.utils.SystemCodes;

@RestController
@ApiVersion(1)
@RequestMapping("/tools/location/")
public class ImportLocationController extends AbstractRestController {

    private static final Logger log = LoggerFactory.getLogger(ImportLocationController.class);

    @Autowired
    ImportLocationServiceImpl locationService;

    @Autowired
    private SavedSearchService savedSearchService;

    @ResponseBody
    @PostMapping("save")
    public ConsoleResponseEntity<ResponseDTO> saveLocation(@RequestBody
    LocationDTO location) {
        log.debug("Request came to save a location");
        ResponseDTO response;

        try {
            Long id = locationService.saveLocation(location);
            response = SimpleResponseDTO.create(msgBundle.getText("success.data.saved.code"),
                    msgBundle.getText("success.data.saved"), id);

        } catch (DuplicateElementException e) {
        	log.error(e.getMessage(), e);
            response = SimpleResponseDTO.create(msgBundle.getText("failed.data.saved.code"),
                    e.getMessage());

        } catch (ConsoleException | PQLException e) {
        	log.error(e.getMessage(), e);
            response = SimpleResponseDTO.create(msgBundle.getText("failed.data.saved.code"),
                    msgBundle.getText("failed.data.saved"));
        }

        return ConsoleResponseEntity.get(response, HttpStatus.OK);

    }

    @ResponseBody
    @PostMapping("update")
    public ConsoleResponseEntity<ResponseDTO> updateLocation(@RequestBody
    LocationDTO location) {
        log.debug("Request came to update a location");
        ResponseDTO response;
        try {
            locationService.updateLocation(location);
            response = SimpleResponseDTO.create(msgBundle.getText("success.data.saved.code"),
                    msgBundle.getText("success.data.saved"));
        } catch (ConsoleException | PQLException e) {
        	log.error(e.getMessage(), e);
            response = SimpleResponseDTO.create(msgBundle.getText("failed.data.saved.code"),
                    msgBundle.getText("failed.data.saved"));
        }
        return ConsoleResponseEntity.get(response, HttpStatus.OK);

    }

    @ResponseBody
    @DeleteMapping("delete")
    public ConsoleResponseEntity<ResponseDTO> deleteLocation(@RequestBody
    LocationDTO location) {
        log.debug("Request came to delete a location");
        ResponseDTO response;
        try {
            boolean deleted = locationService.deleteLocation(location);
            response = deleted
                    ? SimpleResponseDTO.create(msgBundle.getText("success.data.deleted.code"),
                            msgBundle.getText("success.data.deleted"))
                    : SimpleResponseDTO.create(msgBundle.getText("location.delete.referenced.code"),
                            msgBundle.getText("location.delete.referenced"));
        } catch (PQLException | ConsoleException e) {
        	log.error(e.getMessage(), e);
            response = SimpleResponseDTO.create(msgBundle.getText("task.delete.failed.code"),
                    msgBundle.getText("task.delete.failed"));
        }
        return ConsoleResponseEntity.get(response, HttpStatus.OK);

    }

    @ResponseBody
    @PostMapping("bulkDelete")
    public ConsoleResponseEntity<ResponseDTO> bulkDeleteLocation(@RequestBody
    List<LocationDTO> locList) {
        log.debug("Request came to bulk delete locations");
        ResponseDTO response;
        int deletedNum = 0;
        try {
            deletedNum = locationService.bulkDeleteLocation(locList);

            response = SimpleResponseDTO.create(msgBundle.getText("success.data.deleted.code"),
                    msgBundle.getText("success.data.deleted"), deletedNum);
        } catch (PQLException | ConsoleException e) {
        	log.error(e.getMessage(), e);
            response = SimpleResponseDTO.create(msgBundle.getText("task.delete.failed.code"),
                    msgBundle.getText("task.delete.failed"));
        }
        return ConsoleResponseEntity.get(response, HttpStatus.OK);

    }

    @ResponseBody
    @PostMapping("search")
    public ConsoleResponseEntity<CollectionDataResponseDTO> searchLocation(@RequestBody
    SearchCriteriaDTO searchCriteriaDTO) {

        Page<LocationDTO> locationList;
        CollectionDataResponseDTO response;
        SearchCriteria searchCriteria = searchCriteriaDTO.getCriteria();

        try {
            locationList = locationService.search(searchCriteria);
            response = CollectionDataResponseDTO.create(
                    msgBundle.getText(SystemCodes.DATA_FOUND_SUCCESS.getCode()),
                    msgBundle.getText(SystemCodes.DATA_FOUND_SUCCESS.getMessageKey()));
            response.setPageNo(searchCriteria.getPageNo());
            response.setPageSize(searchCriteria.getPageSize());
            response.setTotalPages(locationList.getTotalPages());
            response.setTotalNoOfRecords(locationList.getTotalElements());
            response.setData(locationList.getContent());
        } catch (PQLException e) {
            response = CollectionDataResponseDTO.create(msgBundle.getText("failed.data.saved.code"),
                    msgBundle.getText("failed.data.saved"));
        }

        return ConsoleResponseEntity.get(response, HttpStatus.OK);

    }

    @ResponseBody
    @PostMapping("search/add")
    public ConsoleResponseEntity<SimpleResponseDTO> saveLocationSearch(@RequestBody
    SavedSearchDTO criteriaDTO) throws ConsoleException {
        log.debug("Request came to save location search criteria");

        SavedSearch criteria = new SavedSearch(null, criteriaDTO.getName(), criteriaDTO.getDesc(),
                criteriaDTO.getCriteriaJson(), Status.get(criteriaDTO.getStatus()),
                SharedMode.get(criteriaDTO.getSharedMode()), criteriaDTO.getUserIds(), LOCATION);
        criteria.setLowercase_name(criteriaDTO.getName().toLowerCase());
        savedSearchService.saveCriteria(criteria);

        SimpleResponseDTO response =
                SimpleResponseDTO.create(msgBundle.getText("success.data.saved.code"),
                        msgBundle.getText("success.data.saved"), criteria.getId());

        log.info("New location search criteria saved successfully and response sent, [Id: {}]",
                criteria.getId());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("search/savedlist")
    public ConsoleResponseEntity<CollectionDataResponseDTO> findByAll(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false)
            int pageNo, @RequestParam(value = "pageSize", defaultValue = "10", required = false)
            int pageSize) throws ConsoleException {
        log.debug("Request came to find saved location criterias");

        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        Page<SavedSearch> criteriaPage =
                savedSearchService.findByNameOrDescriptionAndType("", LOCATION, pageable);

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
                "Requested location search criteria details found and response sent, [No of records :{}]",
                criteriaDTOs.size());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("search/savedlist/{name}")
    public ConsoleResponseEntity<CollectionDataResponseDTO> findByText(@PathVariable("name")
    String searchText, @RequestParam(value = "pageNo", defaultValue = "0", required = false)
    int pageNo, @RequestParam(value = "pageSize", defaultValue = "10", required = false)
    int pageSize) throws ConsoleException {
        log.debug("Request came to find saved location criterias");

        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        Page<SavedSearch> criteriaPage = savedSearchService.findByNameOrDescriptionAndType(
                searchText == null ? "" : searchText, LOCATION, pageable);

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
                "Requested location search criteria details found and response sent, [No of records :{}]",
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

        log.info("saved location search criteria removed successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);

    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "fields")
    public ConsoleResponseEntity<ResponseDTO> searchLocationFields() {

        SearchFieldsDTO searchFields = locationService.searchFields();

        SimpleResponseDTO<SearchFieldsDTO> response =
                SimpleResponseDTO.createWithType(msgBundle.getText("success.data.loaded.code"),
                        msgBundle.getText("success.data.loaded"), searchFields);

        return ConsoleResponseEntity.get(response, HttpStatus.OK);

    }

    @ResponseBody
    @PostMapping("validateImportFile")
    public ConsoleResponseEntity<ResponseDTO> validateImportFile(
            MultipartHttpServletRequest request) {
        log.debug("Request came to validate import file");

        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = null;
        ResponseDTO response = null;

        try {
            while (itr.hasNext()) {
                mpf = request.getFile(itr.next());
                byte[] data = mpf.getBytes();
                locationService.validateImportFile(data);
            }
            response = SimpleResponseDTO.create(msgBundle.getText("success.data.saved.code"),
                    msgBundle.getText("success.data.saved"));
        } catch (ParseException | IOException e) {
        	log.error(e.getMessage(), e);
            response =
                    SimpleResponseDTO.create(msgBundle.getText("location.file.parse.failed.code"),
                            msgBundle.getText("location.file.parse.failed", e.getMessage()));
        } catch (DuplicateElementException e) {
        	log.error(e.getMessage(), e);
            response = SimpleResponseDTO.create(msgBundle.getText("failed.data.saved.code"),
                    e.getMessage());

        }

        return ConsoleResponseEntity.get(response, HttpStatus.OK);

    }

    @ResponseBody
    @PostMapping("bulkImport")
    public ConsoleResponseEntity<ResponseDTO> bulkImportLocations(
            MultipartHttpServletRequest request) {
        log.debug("Request came to bulk import locations");

        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = null;
        ResponseDTO response = null;
        int successNum = 0;

        try {
            while (itr.hasNext()) {
                mpf = request.getFile(itr.next());
                byte[] data = mpf.getBytes();
                successNum = locationService.bulkImportLocation(data);
            }
            response = SimpleResponseDTO.create(msgBundle.getText("success.data.saved.code"),
                    msgBundle.getText("success.data.saved"), successNum);
        } catch (IOException e) {
        	log.error(e.getMessage(), e);
            response =
                    SimpleResponseDTO.create(msgBundle.getText("location.file.parse.failed.code"),
                            msgBundle.getText("location.file.parse.failed", e.getMessage()));
        } catch (DuplicateElementException e) {
        	log.error(e.getMessage(), e);
            response = SimpleResponseDTO.create(msgBundle.getText("failed.data.saved.code"),
                    e.getMessage());

        } catch (ConsoleException | PQLException e) {
        	log.error(e.getMessage(), e);
            response = SimpleResponseDTO.create(msgBundle.getText("location.save.failed.code"),
                    msgBundle.getText("location.save.failed"));
        }

        return ConsoleResponseEntity.get(response, HttpStatus.OK);

    }

    @Override
    protected Logger getLog() {
        return log;
    }
}
