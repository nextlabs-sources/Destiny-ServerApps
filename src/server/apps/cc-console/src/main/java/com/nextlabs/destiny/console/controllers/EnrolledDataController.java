package com.nextlabs.destiny.console.controllers;

import java.lang.reflect.InvocationTargetException;
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

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.CollectionDataResponseDTO;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.dictionary.ElementDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SavedSearchDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SearchCriteriaDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SearchFieldsDTO;
import com.nextlabs.destiny.console.enums.SavedSearchType;
import com.nextlabs.destiny.console.enums.SharedMode;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.model.SavedSearch;
import com.nextlabs.destiny.console.services.EnrolledDataService;
import com.nextlabs.destiny.console.services.SavedSearchService;
import com.nextlabs.destiny.console.utils.SystemCodes;

/**
 * REST controller to enrolled data.
 *
 * @author Sachindra Dasun
 */
@RestController
@ApiVersion(1)
@RequestMapping("enrolledData")
public class EnrolledDataController extends AbstractRestController {

    private static final Logger logger = LoggerFactory.getLogger(EnrolledDataController.class);
    @Autowired
    private EnrolledDataService enrolledDataService;
    @Autowired
    private SavedSearchService savedSearchService;

    @Override
    protected Logger getLog() {
        return logger;
    }

    @ResponseBody
    @PostMapping("search/add")
    public ConsoleResponseEntity<SimpleResponseDTO<Long>> savePropertySearch(
            @RequestBody SavedSearchDTO criteriaDTO) throws ConsoleException {
        SavedSearch criteria = new SavedSearch(null, criteriaDTO.getName(), criteriaDTO.getDesc(),
                criteriaDTO.getCriteriaJson(), Status.get(criteriaDTO.getStatus()),
                SharedMode.get(criteriaDTO.getSharedMode()), criteriaDTO.getUserIds(), SavedSearchType.ENROLLED_DATA);
        criteria.setLowercase_name(criteriaDTO.getName().toLowerCase());

        savedSearchService.saveCriteria(criteria);

        return ConsoleResponseEntity.get(SimpleResponseDTO.createWithType(msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), criteria.getId()), HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("search/savedlist")
    public ConsoleResponseEntity<CollectionDataResponseDTO<SavedSearchDTO>> findByAll(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false)
                    int pageNo, @RequestParam(value = "pageSize", defaultValue = "10", required = false)
                    int pageSize) throws ConsoleException {
        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        Page<SavedSearch> criteriaPage =
                savedSearchService.findByNameOrDescriptionAndType("", SavedSearchType.ENROLLED_DATA, pageable);
        if (criteriaPage.getNumberOfElements() == 0) {
            throw new NoDataFoundException(msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }
        List<SavedSearchDTO> criteriaDTOs = new ArrayList<>(criteriaPage.getNumberOfElements());
        for (SavedSearch criteria : criteriaPage.getContent()) {
            SavedSearchDTO criteriaDTO = SavedSearchDTO.getDTO(criteria);
            criteriaDTOs.add(criteriaDTO);
        }

        CollectionDataResponseDTO<SavedSearchDTO> response =
                CollectionDataResponseDTO.createWithType(msgBundle.getText("success.data.found.code"),
                        msgBundle.getText("success.data.found"));
        response.setData(criteriaDTOs);
        response.setPageSize(pageable.getPageSize());
        response.setPageNo(pageable.getPageNumber());
        response.setTotalPages(criteriaPage.getTotalPages());
        response.setTotalNoOfRecords(criteriaPage.getTotalElements());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("search/savedlist/{name}")
    public ConsoleResponseEntity<CollectionDataResponseDTO<SavedSearchDTO>> findByText(
            @PathVariable("name") String searchText,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws ConsoleException {

        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        Page<SavedSearch> criteriaPage = savedSearchService.findByNameOrDescriptionAndType(
                searchText == null ? "" : searchText, SavedSearchType.ENROLLED_DATA, pageable);

        if (criteriaPage.getNumberOfElements() == 0) {
            throw new NoDataFoundException(msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        List<SavedSearchDTO> criteriaDTOs = new ArrayList<>(criteriaPage.getNumberOfElements());
        for (SavedSearch criteria : criteriaPage.getContent()) {
            SavedSearchDTO criteriaDTO = SavedSearchDTO.getDTO(criteria);
            criteriaDTOs.add(criteriaDTO);
        }

        CollectionDataResponseDTO<SavedSearchDTO> response =
                CollectionDataResponseDTO.createWithType(msgBundle.getText("success.data.found.code"),
                        msgBundle.getText("success.data.found"));
        response.setData(criteriaDTOs);
        response.setPageSize(pageable.getPageSize());
        response.setPageNo(pageable.getPageNumber());
        response.setTotalPages(criteriaPage.getTotalPages());
        response.setTotalNoOfRecords(criteriaPage.getTotalElements());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @DeleteMapping(value = "search/remove/{id}")
    public ConsoleResponseEntity<ResponseDTO> removeCriteria(@PathVariable("id") Long id) throws ConsoleException {
        validations.assertNotZero(id, "id");
        savedSearchService.removeCriteria(id);
        ResponseDTO response = ResponseDTO.create(msgBundle.getText("success.data.deleted.code"),
                msgBundle.getText("success.data.deleted"));
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "search/fields")
    public ConsoleResponseEntity<ResponseDTO> searchFields() {
        SimpleResponseDTO<SearchFieldsDTO> response;
        SearchFieldsDTO searchFields;
        searchFields = enrolledDataService.searchFields();
        response = SimpleResponseDTO.createWithType(msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"), searchFields);
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping("search")
    public ConsoleResponseEntity<CollectionDataResponseDTO<ElementDTO>> search(
            @RequestBody SearchCriteriaDTO searchCriteriaDTO) {

        SearchCriteria searchCriteria = searchCriteriaDTO.getCriteria();

        Page<ElementDTO> enrolledObjects = enrolledDataService.search(searchCriteria);

        CollectionDataResponseDTO<ElementDTO> response = CollectionDataResponseDTO.createWithType(
                msgBundle.getText(SystemCodes.DATA_FOUND_SUCCESS.getCode()),
                msgBundle.getText(SystemCodes.DATA_FOUND_SUCCESS.getMessageKey()));
        response.setPageNo(searchCriteria.getPageNo());
        response.setPageSize(searchCriteria.getPageSize());
        response.setTotalPages(enrolledObjects.getTotalPages());
        response.setTotalNoOfRecords(enrolledObjects.getTotalElements());
        response.setData(enrolledObjects.getContent());

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("findById/{id}")
    public ConsoleResponseEntity<ElementDTO> findById(@PathVariable("id") Long id)
            throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        ElementDTO elementDTO = enrolledDataService.findById(id);

        if (elementDTO == null) {
            throw new NoDataFoundException(msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }
        return ConsoleResponseEntity.get(elementDTO, HttpStatus.OK);
    }

}
