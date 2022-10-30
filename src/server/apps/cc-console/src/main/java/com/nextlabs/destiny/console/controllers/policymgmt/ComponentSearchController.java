/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 5, 2016
 *
 */
package com.nextlabs.destiny.console.controllers.policymgmt;

import static com.nextlabs.destiny.console.enums.SavedSearchType.COMPONENT;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;
import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.controllers.AbstractRestController;
import com.nextlabs.destiny.console.dto.common.CollectionDataResponseDTO;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.FacetResult;
import com.nextlabs.destiny.console.dto.common.LiteDTO;
import com.nextlabs.destiny.console.dto.common.MultiFieldValuesDTO;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.common.SinglevalueFieldDTO;
import com.nextlabs.destiny.console.dto.common.StringFieldValue;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentLite;
import com.nextlabs.destiny.console.dto.policymgmt.SavedSearchDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SearchCriteriaDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SearchFieldsDTO;
import com.nextlabs.destiny.console.enums.DateOption;
import com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus;
import com.nextlabs.destiny.console.enums.PolicyStatus;
import com.nextlabs.destiny.console.enums.SharedMode;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.model.SavedSearch;
import com.nextlabs.destiny.console.services.SavedSearchService;
import com.nextlabs.destiny.console.services.policy.ComponentMgmtService;
import com.nextlabs.destiny.console.services.policy.ComponentSearchService;

import springfox.documentation.annotations.ApiIgnore;

/**
 *
 * REST Controller for Component search function
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("/component/search")
@Api(tags = {"Component Search Controller"})
public class ComponentSearchController extends AbstractRestController {

    private static final Logger log = LoggerFactory
            .getLogger(ComponentSearchController.class);

    @Autowired
    private SavedSearchService savedSearchService;

    @Autowired
    private ComponentSearchService componentSearchService;

    @Autowired
    private ComponentMgmtService componentMgmtService;

    /**
     * Component List handles by this method. This will return the component
     * list according to given component search criteria
     * 
     * @param criteriaDTO
     * @return List of components to display
     * @throws ConsoleException
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping@ApiOperation(value="Searches for a list of components based on a given search criteria.",
            notes="Returns a list of components that match the given search criteria" +
                    "\n<ul>" +
                    "<li><strong>Allowable values for criteria.fields.field</strong>: Fields of <strong>ComponentLite</strong> model.</li>" +
                    " <li><strong>Allowable values for criteria.fields.nestedField</strong>: tags.key, tags.label, tags.type, tags.status</li></ul>")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data loaded successfully")})
    public ConsoleResponseEntity<CollectionDataResponseDTO<ComponentLite>> componentLiteSearch(
            @RequestBody SearchCriteriaDTO criteriaDTO)
            throws ConsoleException {

        log.debug("Request came to component search");
        validations.assertNotNull(criteriaDTO.getCriteria(), "criteria");
        SearchCriteria criteria = criteriaDTO.getCriteria();

        long startTime = System.currentTimeMillis();
        Page<ComponentLite> componentLitePage = componentSearchService
                .findByCriteria(criteria, false);
        long processingTime = System.currentTimeMillis() - startTime;

        for (ComponentLite componentLite : componentLitePage.getContent()) {
            componentMgmtService.enforceTBAC(componentLite);
        }

        CollectionDataResponseDTO<ComponentLite> response = CollectionDataResponseDTO.createWithType(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"));
        response.setData(componentLitePage.getContent());
        response.setPageNo(criteria.getPageNo());
        response.setPageSize(criteria.getPageSize());
        response.setTotalPages(componentLitePage.getTotalPages());
        response.setTotalNoOfRecords(componentLitePage.getTotalElements());

        log.info(
                "Component search has been completed, Search handled in {} milis, Total no of records : {}",
                processingTime, componentLitePage.getNumberOfElements());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * This will return the list of policies according to the given component
     * ids.
     * 
     * @param List
     *            of component ids
     * @return List of policies to display
     * @throws ConsoleException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/ids")
    public ConsoleResponseEntity<CollectionDataResponseDTO> findComponenetsByIds(
            @RequestBody SearchCriteriaDTO criteriaDTO)
            throws ConsoleException {

        log.debug("Request came to search components by given ids");
        validations.assertNotNull(criteriaDTO.getCriteria(), "criteria");
        validations.assertNotZero(
                Long.valueOf(criteriaDTO.getCriteria().getFields().size()),
                "ids");
        SearchCriteria criteria = criteriaDTO.getCriteria();

        List<Long> ids = new ArrayList<>();
        SearchField field = criteria.getFields().get(0);
        StringFieldValue fieldValues = (StringFieldValue) field.getValue();
        List<String> values = (List<String>) fieldValues.getValue();
        for (String value : values) {
            ids.add(Long.valueOf(value));
        }

        long startTime = System.currentTimeMillis();

        Page<ComponentLite> componentLitePage = componentSearchService
                .findComponentsByIds(ids, criteria.getSortFields(),
                        PageRequest.of(criteria.getPageNo(),
                                criteria.getPageSize()));
        
        long processingTime = System.currentTimeMillis() - startTime;
        
        for (ComponentLite componentLite : componentLitePage.getContent()) {
            componentMgmtService.enforceTBAC(componentLite);
        }

        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"));
        response.setPageNo(criteria.getPageNo());
        response.setPageSize(criteria.getPageSize());
        response.setTotalPages(componentLitePage.getTotalPages());
        response.setTotalNoOfRecords(componentLitePage.getTotalElements());
        response.setData(componentLitePage.getContent());

        log.info(
                "Component search by ids has been completed, Search handled in {} milis, Total no of records : {}",
                processingTime, componentLitePage.getNumberOfElements());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Facet search will return the {@link FacetResult} data with term and count
     * with proper faceted grouping
     * 
     * @param criteriaDTO
     * @return {@link FacetResult}
     * @throws ConsoleException
     */
    @SuppressWarnings({ "rawtypes" })
    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/facet")
    public ConsoleResponseEntity<SimpleResponseDTO> facetSearch(
            @RequestBody SearchCriteriaDTO criteriaDTO)
            throws ConsoleException {

        log.debug("Request came to component facet search");
        validations.assertNotNull(criteriaDTO.getCriteria(), "criteria");
        SearchCriteria criteria = criteriaDTO.getCriteria();

        long startTime = System.currentTimeMillis();
        FacetResult facetResult = componentSearchService
                .findFacetByCriteria(criteria);

        long processingTime = System.currentTimeMillis() - startTime;

        SimpleResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"), facetResult);

        log.info(
                "Component facet search has been completed, Search handled in {} milis, Total no of facet records : {}",
                processingTime, facetResult.getTerms().size());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Find the components by group type.
     * 
     * @param group
     *            group type
     * @return List of components to display}
     * @throws ConsoleException
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/listNames/{group}")
	@ApiOperation(value = "Returns list of components based on group.",
		notes = "Given a group type or a component type, this API returns a list of components. The group type can be RESOURCE, ACTION OR SUBJECT")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data loaded successfully")})
    public ConsoleResponseEntity<CollectionDataResponseDTO<LiteDTO>> findNamesByGroupType(
            @PathVariable("group") String group,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws ConsoleException {

        log.debug("Request came to components by group type");
        validations.assertNotBlank(group, "Group Type");

        PageRequest pageable = PageRequest.of(pageNo, pageSize);

        long startTime = System.currentTimeMillis();
        Page<ComponentLite> componentsPage = componentSearchService
                .findComponentsByGroupAndType(group, null, pageable, true);
        long processingTime = System.currentTimeMillis() - startTime;

        List<LiteDTO> dtos = componentsPage.getContent().stream()
                .map(component -> {
                    LiteDTO liteDTO = new LiteDTO(component.getId(), component.getName())
                            .put(LiteDTO.DATA, ImmutableMap.of(LiteDTO.POLICY_MODEL_ID, component.getModelId(),
                                    LiteDTO.POLICY_MODEL_NAME, component.getModelType()));
                    if (component.getPredicateData() != null) {
                        if (!component.getPredicateData().getActions().isEmpty()
                                || !component.getPredicateData().getAttributes().isEmpty()
                                || !component.getPredicateData().getReferenceIds().isEmpty()) {
                            liteDTO.setEmpty(false);
                        } else {
                            liteDTO.setEmpty(true);
                        }
                    }
                    return liteDTO;
                }).collect(Collectors.toList());

        CollectionDataResponseDTO<LiteDTO> response = CollectionDataResponseDTO.createWithType(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"));
        response.setData(dtos);
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setTotalPages(componentsPage.getTotalPages());
        response.setTotalNoOfRecords(componentsPage.getTotalElements());

        log.debug(
                "Component search by group type has been completed, Search handled in {} milis, Total no of records : {}",
                processingTime, componentsPage.getContent().size());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Find the components by group type and model type.
     * 
     * @param group
     *            group type
     * @return List of components to display}
     * @throws ConsoleException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/listNames/{group}/{type}")
    @ApiOperation(value = "Returns list of components based on group and component type.",
		notes = "Given a group type (component type) and a policy model type, this API returns a list of components.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data loaded successfully")})
    public ConsoleResponseEntity<CollectionDataResponseDTO<LiteDTO>> findNamesByGroupAndType(
            @PathVariable("group") String group,
            @PathVariable("type") String type,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws ConsoleException {

        log.debug("Request came to components by group type");
        validations.assertNotBlank(group, "Group Type");
        validations.assertNotBlank(type, "Model Type");

        PageRequest pageable = PageRequest.of(pageNo, pageSize);

        long startTime = System.currentTimeMillis();
        Page<ComponentLite> componentsPage = componentSearchService
                .findComponentsByGroupAndType(group, type, pageable, true);
        long processingTime = System.currentTimeMillis() - startTime;

        List<LiteDTO> liteDTOS = componentsPage.getContent().stream().map(component -> {
            LiteDTO liteDTO = new LiteDTO(component.getId(), component.getName())
                    .put(LiteDTO.DATA, ImmutableMap.of(LiteDTO.POLICY_MODEL_ID, component.getModelId()));
            if (component.getPredicateData() != null) {
                if (!component.getPredicateData().getActions().isEmpty()
                        || !component.getPredicateData().getAttributes().isEmpty()
                        || !component.getPredicateData().getReferenceIds().isEmpty()) {
                    liteDTO.setEmpty(false);
                } else {
                    liteDTO.setEmpty(true);
                }
            }
            return liteDTO;
        }).collect(Collectors.toList());

        CollectionDataResponseDTO<LiteDTO> response = CollectionDataResponseDTO.createWithType(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"));
        response.setData(liteDTOS);
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setTotalPages(componentsPage.getTotalPages());
        response.setTotalNoOfRecords(componentsPage.getTotalElements());

        log.debug(
                "Component search by group type and model type has been completed, "
                        + "Search handled in {} milis, Total no of records : {}",
                processingTime, componentsPage.getContent().size());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Save Component search criteria
     * 
     * @param criteriaDTO
     * @return
     * @throws ConsoleException
     */
    @SuppressWarnings("rawtypes")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/add")
    @ApiOperation(value = "Saves a new component search criteria and returns the id in data field of response.",
            notes = "<ul>" +
                    "<li><strong>Allowable values for criteria.fields.field</strong>: Fields of <strong>ComponentLite</strong> model.</li>" +
                    "<li><strong>Allowable values for criteria.fields.nestedField</strong>: tags.key, tags.label, tags.type, tags.status</li>" +
                    "</ul>")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data saved successfully")})
    public ConsoleResponseEntity<SimpleResponseDTO> saveComponentSearch(
            @RequestBody SavedSearchDTO criteriaDTO) throws ConsoleException {

        log.debug("Request came to save search criteria");
        validations.assertNotBlank(criteriaDTO.getName(), "Name");
        validations.assertNotBlank(criteriaDTO.getDesc(), "Description");
        validations.assertNotBlank(criteriaDTO.getCriteriaJson(), "Criteria");

        SavedSearch criteria = new SavedSearch(null, criteriaDTO.getName(),
                criteriaDTO.getDesc(), criteriaDTO.getCriteriaJson(),
                Status.get(criteriaDTO.getStatus()),
                SharedMode.get(criteriaDTO.getSharedMode()),
                criteriaDTO.getUserIds(), COMPONENT);
        criteria.setLowercase_name(criteriaDTO.getName().toLowerCase());
        savedSearchService.saveCriteria(criteria);

        SimpleResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), criteria.getId());

        log.info(
                "New Component search criteria saved successfully and response sent, [Id: {}]",
                criteria.getId());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Modify saved component search criteria
     * 
     * @param criteriaDTO
     * @return
     * @throws ConsoleException
     */
    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/modify")
    @ApiOperation(value = "Modifies an existing component search criteria.",
            notes = "<ul>" +
                    "<li><strong>Allowable values for criteria.fields.field</strong>: Fields of <strong>ComponentLite</strong> model.</li>" +
                    "<li><strong>Allowable values for criteria.fields.nestedField</strong>: tags.key, tags.label, tags.type, tags.status</li>" +
                    "</ul>")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data saved successfully")})
    public ConsoleResponseEntity<ResponseDTO> modifyCriteria(
            @RequestBody SavedSearchDTO criteriaDTO) throws ConsoleException {

        log.debug("Request came to modify the search criteria");
        validations.assertNotZero(criteriaDTO.getId(), "id");
        validations.assertNotBlank(criteriaDTO.getName(), "Name");
        validations.assertNotBlank(criteriaDTO.getDesc(), "Description");
        validations.assertNotBlank(criteriaDTO.getCriteriaJson(), "Criteria");

        SavedSearch criteria = savedSearchService.findById(criteriaDTO.getId());
        criteria.setName(criteriaDTO.getName());
        criteria.setDesc(criteriaDTO.getDesc());
        criteria.setCriteria(criteriaDTO.getCriteriaJson());
        criteria.setStatus(Status.get(criteriaDTO.getStatus()));
        criteria.setSharedMode(SharedMode.get(criteriaDTO.getSharedMode()));
        criteria.setLowercase_name(criteriaDTO.getName().toLowerCase());
        criteria.setUserIds(criteriaDTO.getUserIds());

        savedSearchService.saveCriteria(criteria);

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.modified.code"),
                msgBundle.getText("success.data.modified"));

        log.info(
                "saved search criteria modifed successfully and response sent,  [Id: {}]",
                criteria.getId());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Remove saved component search criteria
     * 
     * @param id
     * @return
     * @throws ConsoleException
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/remove/{id}")
    @ApiOperation(value="Deletes the saved search referenced by the ID.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data deleted successfully")})
    public ConsoleResponseEntity<ResponseDTO> removeCriteria(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to remove a criteria");
        validations.assertNotZero(id, "id");

        savedSearchService.removeCriteria(id);

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.deleted.code"),
                msgBundle.getText("success.data.deleted"));

        log.info(
                "saved component search criteria removed successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);

    }

    /**
     * Find saved component search criteria by id
     * 
     * @param id
     * @return
     * @throws ConsoleException
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/saved/{id}")
    @ApiOperation(value="Returns the saved search referenced by the ID.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data found successfully")})
    public ConsoleResponseEntity<SimpleResponseDTO<SavedSearchDTO>> getById(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came find saved search criteria by id, [id: {}]",
                id);
        validations.assertNotZero(id, "id");

        SavedSearch criteria = savedSearchService.findById(id);
        if (criteria == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        SavedSearchDTO criteriaDTO = SavedSearchDTO.getDTO(criteria);

        SimpleResponseDTO<SavedSearchDTO> response = SimpleResponseDTO.createWithType(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), criteriaDTO);

        log.info(
                "Requested saved search criteria details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * List saved component search criteria by name
     * 
     * @param name
     * @return
     * @throws ConsoleException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/savedlist/{name}")
    @ApiOperation(value="Returns the saved search having passed value as name or description.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data found successfully")})
    public ConsoleResponseEntity<CollectionDataResponseDTO<SavedSearchDTO>> findByText(
            @PathVariable("name") String searchText,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws ConsoleException {

        log.debug("Request came to find component criteria by name, [Name:{}]",
                searchText);

        validations.assertNotBlank(searchText, "name");

        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        Page<SavedSearch> criteriaPage = savedSearchService
                .findByNameOrDescriptionAndType(searchText, COMPONENT,
                        pageable);

        if (criteriaPage.getNumberOfElements() == 0) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        List<SavedSearchDTO> criteriaDTOs = new ArrayList<>(
                criteriaPage.getNumberOfElements());
        for (SavedSearch criteria : criteriaPage.getContent()) {
            SavedSearchDTO criteriaDTO = SavedSearchDTO.getDTO(criteria);
            criteriaDTOs.add(criteriaDTO);
        }

        CollectionDataResponseDTO<SavedSearchDTO> response = CollectionDataResponseDTO.createWithType(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"));
        response.setData(criteriaDTOs);
        response.setPageSize(pageable.getPageSize());
        response.setPageNo(pageable.getPageNumber());
        response.setTotalPages(criteriaPage.getTotalPages());
        response.setTotalNoOfRecords(criteriaPage.getTotalElements());

        log.info(
                "Requested saved search criteria details found and response sent, [No of records :{}]",
                criteriaDTOs.size());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * List all saved component search criteria
     * 
     * @param name
     * @return
     * @throws ConsoleException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/savedlist")
    @ApiOperation(value="Returns all the saved searches.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data found successfully")})
    public ConsoleResponseEntity<CollectionDataResponseDTO<SavedSearchDTO>> findByAll(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws ConsoleException {

        log.debug("Request came to find saved component criterias");

        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        Page<SavedSearch> criteriaPage = savedSearchService
                .findByNameOrDescriptionAndType("", COMPONENT, pageable);

        if (criteriaPage.getNumberOfElements() == 0) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        List<SavedSearchDTO> criteriaDTOs = new ArrayList<>(
                criteriaPage.getNumberOfElements());
        for (SavedSearch criteria : criteriaPage.getContent()) {
            SavedSearchDTO criteriaDTO = SavedSearchDTO.getDTO(criteria);
            criteriaDTOs.add(criteriaDTO);
        }

        CollectionDataResponseDTO<SavedSearchDTO> response = CollectionDataResponseDTO.createWithType(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"));
        response.setData(criteriaDTOs);
        response.setPageSize(pageable.getPageSize());
        response.setPageNo(pageable.getPageNumber());
        response.setTotalPages(criteriaPage.getTotalPages());
        response.setTotalNoOfRecords(criteriaPage.getTotalElements());

        log.info(
                "Requested component search criteria details found and response sent, [No of records :{}]",
                criteriaDTOs.size());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * TODO : need proper re-factoring
     * 
     * Load all search form fields with i18n support messages
     * 
     * @return
     */
    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/fields")
    @ApiOperation(value="Returns a list of available search fields for components.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data loaded successfully")})
    public ConsoleResponseEntity<SimpleResponseDTO<SearchFieldsDTO>> searchFields() {

        log.debug("Request came to load search fields");

        SearchFieldsDTO searchFields = new SearchFieldsDTO();

        // Component Type
        searchFields.setStatus(SinglevalueFieldDTO.create("modelType",
                msgBundle.getText("component.mgmt.search.fields.type")));

        // Component status
        searchFields.setStatus(SinglevalueFieldDTO.create("status",
                msgBundle.getText("policy.mgmt.search.fields.status")));
        searchFields.getStatusOptions()
                .add(MultiFieldValuesDTO.create(
                        PolicyDevelopmentStatus.DRAFT.name(), msgBundle
                                .getText(PolicyStatus.DRAFT.getKey())));
        searchFields.getStatusOptions()
                .add(MultiFieldValuesDTO.create(PolicyDevelopmentStatus.APPROVED
                        .name(), msgBundle
                                .getText(PolicyStatus.DEPLOYED.getKey())));
        searchFields.getStatusOptions()
                .add(MultiFieldValuesDTO.create(PolicyDevelopmentStatus.OBSOLETE
                        .name(), msgBundle
                                .getText(PolicyStatus.DE_ACTIVATED.getKey())));

        // Modified date field
        searchFields.setModifiedDate(SinglevalueFieldDTO.create("modifiedDate",
                msgBundle.getText("policy.mgmt.search.fields.modified.date")));
        for (DateOption dateOpt : DateOption.values()) {
            searchFields.getModifiedDateOptions()
                    .add(MultiFieldValuesDTO.create(dateOpt.name(),
                            msgBundle.getText(dateOpt.getKey())));
        }

        // sort by options
        searchFields.setSort(SinglevalueFieldDTO.create("sortBy",
                msgBundle.getText("policy.mgmt.search.fields.sortBy")));
        searchFields.getSortOptions().add(MultiFieldValuesDTO.create(
                "lastUpdatedDate",
                msgBundle.getText("policy.mgmt.search.fields.lastupdated"), 
                msgBundle.getText("policy.mgmt.search.fields.order.desc")));
        searchFields.getSortOptions().add(MultiFieldValuesDTO.create("name",
                msgBundle.getText("policy.mgmt.search.fields.name"), 
                msgBundle.getText("policy.mgmt.search.fields.order.asc")));
        searchFields.getSortOptions().add(MultiFieldValuesDTO.create("name",
                msgBundle.getText("policy.mgmt.search.fields.nameZtoA"), 
                msgBundle.getText("policy.mgmt.search.fields.order.desc")));

        SimpleResponseDTO<SearchFieldsDTO> response = SimpleResponseDTO.createWithType(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"), searchFields);

        log.info(
                "Component search form fields and data populated successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @Override
    protected Logger getLog() {
        return log;
    }

}
