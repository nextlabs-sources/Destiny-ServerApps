/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 15, 2016
 *
 */
package com.nextlabs.destiny.console.controllers.policymgmt;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.annotations.Api;
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
import com.nextlabs.destiny.console.dto.policymgmt.PolicyModelDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SavedSearchDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SearchCriteriaDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SearchFieldsDTO;
import com.nextlabs.destiny.console.enums.DateOption;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.enums.PolicyStatus;
import com.nextlabs.destiny.console.enums.SavedSearchType;
import com.nextlabs.destiny.console.enums.SharedMode;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.model.SavedSearch;
import com.nextlabs.destiny.console.model.policy.PolicyModel;
import com.nextlabs.destiny.console.services.SavedSearchService;
import com.nextlabs.destiny.console.services.delegadmin.AccessControlService;
import com.nextlabs.destiny.console.services.policy.PolicyModelService;
import com.nextlabs.destiny.console.utils.SearchCriteriaBuilder;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * REST Controller for Policy model function
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("/policyModel/search")
@Api(tags = {"Policy Model/ Component Type Search Controller"})
public class PolicyModelSearchController extends AbstractRestController {

    private static final Logger log = LoggerFactory
            .getLogger(PolicyModelSearchController.class);

    @Autowired
    private PolicyModelService policyModelService;

    @Autowired
    private SavedSearchService savedSearchService;

    @Autowired
    private AccessControlService accessControlService;

    /**
     * Policy model criteria search handles by this method.
     * 
     * @param criteriaDTO
     * @return List of policies to display
     * @throws ConsoleException
     */
    @SuppressWarnings({ "rawtypes" })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    @ApiOperation(value="Searches for a list of component types based on a given search criteria.",
		notes="Returns a list of component types that match the given search criteria" +
                "\n<ul>" +
                "<li><strong>Allowable values for criteria.fields.field</strong>: Fields of <strong>PolicyModel</strong> model.</li>" +
                "<li><strong>Allowable values for criteria.fields.nestedField</strong>: tags.key, tags.label, tags.type, tags.status</li></ul>")
    public ConsoleResponseEntity<CollectionDataResponseDTO<PolicyModelDTO>> policyModelSearch(
            @RequestBody SearchCriteriaDTO criteriaDTO)
            throws ConsoleException {

        log.debug("Request came to policy model search");
        validations.assertNotNull(criteriaDTO.getCriteria(), "criteria");
        SearchCriteria criteria = criteriaDTO.getCriteria();

        long startTime = System.currentTimeMillis();
        Page<PolicyModel> policyModelPage = policyModelService
                .findByCriteria(criteria, false);
        long processingTime = System.currentTimeMillis() - startTime;

        List<PolicyModelDTO> policyModelList = populatePolicyModels(
                policyModelPage);
        policyModelList = accessControlService
                .enforceTBAConPolicyModels(policyModelList);

        CollectionDataResponseDTO<PolicyModelDTO> response = createPolicyModelListResponse(
                criteria, policyModelPage, policyModelList);

        log.info(
                "Policy model search has been completed, Search handled in {} milis, Total no of records : {}",
                processingTime, policyModelPage.getNumberOfElements());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * This will return the list of policies according to the given policy ids.
     * 
     * @param List
     *            of policy ids
     * @return List of policies to display
     * @throws ConsoleException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/ids")
    public ConsoleResponseEntity<CollectionDataResponseDTO<PolicyModelDTO>> findPolicyModelsByIds(
            @RequestBody SearchCriteriaDTO criteriaDTO)
            throws ConsoleException {

        log.debug("Request came to search policy models by given ids");
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
        Page<PolicyModel> policyModelPage = policyModelService.findByIds(ids,
                criteria.getSortFields(),
                PageRequest.of(criteria.getPageNo(), criteria.getPageSize()));

        long processingTime = System.currentTimeMillis() - startTime;

        List<PolicyModelDTO> policyModelList = populatePolicyModels(
                policyModelPage);

        CollectionDataResponseDTO<PolicyModelDTO> response = createPolicyModelListResponse(
                criteria, policyModelPage, policyModelList);

        log.info(
                "Policy model search by ids has been completed, Search handled in {} milis, Total no of records : {}",
                processingTime, policyModelPage.getNumberOfElements());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    private List<PolicyModelDTO> populatePolicyModels(
            Page<PolicyModel> policyModelPage) {
        List<PolicyModelDTO> policyModelList = new ArrayList<>();

        if (policyModelPage.getContent().isEmpty()) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.for.criteria.code"),
                    msgBundle.getText("no.data.found.for.criteria"));
        } else {
            for (PolicyModel model : policyModelPage.getContent()) {
                policyModelList.add(PolicyModelDTO.getLiteDTO(model));
            }
        }

        return policyModelList;
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

        log.debug("Request came to policy model facet search");
        validations.assertNotNull(criteriaDTO.getCriteria(), "criteria");
        SearchCriteria criteria = criteriaDTO.getCriteria();

        long startTime = System.currentTimeMillis();
        FacetResult facetResult = policyModelService
                .findFacetByCriteria(criteria);

        long processingTime = System.currentTimeMillis() - startTime;

        SimpleResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"), facetResult);

        log.info(
                "Policy model facet search has been completed, Search handled in {} milis, Total no of facet records : {}",
                processingTime, facetResult.getTerms().size());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Facet search will return the {@link PolicyModel} data with for a given
     * type
     *
     * @param type {@link PolicyModelType}
     * @return List of policy models to display}
     * @throws ConsoleException
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/listNames/{type}")
    public ConsoleResponseEntity<CollectionDataResponseDTO<LiteDTO>> findNamesByType(
            @PathVariable("type") String type,
            @RequestParam(value = "id", defaultValue = "-1", required = false) long id,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws ConsoleException {

        log.debug("Request came to find lite policy models");
        validations.assertNotBlank(type, "Type");
        long startTime = System.currentTimeMillis();

        List<PolicyModel> policyModels = new ArrayList<>();
        if (id > 0) {
            PolicyModel policyModel = policyModelService.findActivePolicyModelById(id);
            if (policyModel != null) {
                policyModels.add(policyModel);
            }
        } else {
            SearchCriteriaBuilder searchCriteriaBuilder = SearchCriteriaBuilder.create();
            searchCriteriaBuilder.addSingleExactMatchField("type", type);
            SearchCriteria criteria = searchCriteriaBuilder.getCriteria();
            criteria.setPageNo(pageNo);
            criteria.setPageSize(pageSize);
            policyModels = policyModelService.findByCriteria(criteria, true).getContent();
        }

        long processingTime = System.currentTimeMillis() - startTime;

        // Map policy model list to lite policy model list using only the mandatory fields.
        List<LiteDTO> liteDTOS = policyModels.stream()
                .map(policyModel -> new LiteDTO(policyModel.getId(), policyModel.getName())
                        .put(LiteDTO.SHORT_NAME, policyModel.getShortName())
                        .put(LiteDTO.TYPE, policyModel.getType())
                        .put(LiteDTO.LAST_UPDATED_DATE, policyModel.getLastUpdatedDate())
                        .put(LiteDTO.ATTRIBUTES, policyModel.getAttributes().stream()
                                .map(attributeConfig -> new LiteDTO(attributeConfig.getId(), attributeConfig.getName())
                                        .put(LiteDTO.SHORT_NAME, attributeConfig.getShortName())
                                        .put(LiteDTO.DATA_TYPE, attributeConfig.getDataType())
                                        .put(LiteDTO.REG_EX_PATTERN, attributeConfig.getRegExPattern())
                                        .put(LiteDTO.SORT_ORDER, attributeConfig.getSortOrder())
                                        .put(LiteDTO.OPERATOR_CONFIGS,
                                                attributeConfig.getOperatorConfigs().stream()
                                                        .map(operatorConfig -> new LiteDTO(operatorConfig.getId(),
                                                                operatorConfig.getLabel())
                                                                .put(LiteDTO.KEY, operatorConfig.getKey())
                                                                .put(LiteDTO.LABEL, operatorConfig.getLabel())
                                                                .put(LiteDTO.DATA_TYPE, operatorConfig.getDataType()))
                                                        .collect(Collectors.toList())))
                                .collect(Collectors.toList()))
                        .put(LiteDTO.OBLIGATIONS, policyModel.getObligations().stream()
                                .map(obligationConfig -> new LiteDTO(obligationConfig.getId(), obligationConfig.getName())
                                        .put(LiteDTO.SHORT_NAME, obligationConfig.getShortName())
                                        .put(LiteDTO.SORT_ORDER, obligationConfig.getSortOrder())
                                        .put(LiteDTO.PARAMETERS, obligationConfig.getParameters().stream()
                                                .map(parameterConfig -> new LiteDTO(parameterConfig.getId(),
                                                        parameterConfig.getName())
                                                        .put(LiteDTO.SHORT_NAME, parameterConfig.getShortName())
                                                        .put(LiteDTO.TYPE, parameterConfig.getType().name())
                                                        .put(LiteDTO.DEFAULT_VALUE, parameterConfig.getDefaultValue())
                                                        .put(LiteDTO.LIST_VALUES, parameterConfig.getListValues())
                                                        .put(LiteDTO.HIDDEN, parameterConfig.isHidden())
                                                        .put(LiteDTO.EDITABLE, parameterConfig.isEditable())
                                                        .put(LiteDTO.MANDATORY, parameterConfig.isMandatory())
                                                        .put(LiteDTO.SORT_ORDER, parameterConfig.getSortOrder()))
                                                .collect(Collectors.toList())))
                                .collect(Collectors.toList()))
                        .put(LiteDTO.ACTIONS, policyModel.getActions().stream()
                                .map(actionConfig -> new LiteDTO(actionConfig.getId(), actionConfig.getName())
                                        .put(LiteDTO.SHORT_NAME, actionConfig.getShortName())
                                        .put(LiteDTO.SORT_ORDER, actionConfig.getSortOrder()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());

        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"));
        response.setData(liteDTOS);
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setTotalPages(1);
        response.setTotalNoOfRecords(policyModels.size());

        log.debug("Policy model lite search completed, Search handled in {} milis, Total no of records : {}",
                processingTime, policyModels.size());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }
    
    /**
     * Facet search will return the {@link PolicyModel} detail with for a given
     * type
     * 
     * @param type
     *            {@link PolicyModelType}
     * @return List of policy models to display}
     * @throws ConsoleException
     */
    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/listDetails/{type}")
    public ConsoleResponseEntity<CollectionDataResponseDTO<PolicyModelDTO>> findDetailsByType(
            @PathVariable("type") String type,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws ConsoleException {

    	log.debug("Request came to find policy models by type ");
        validations.assertNotBlank(type, "Type");

        long startTime = System.currentTimeMillis();
        SearchCriteria criteria = SearchCriteriaBuilder.create()
                .addSingleExactMatchField("type", type).getCriteria();
        criteria.setPageNo(pageNo);
        criteria.setPageSize(pageSize);

        boolean skipDAFilter = type.equals(PolicyModelType.SUBJECT.name());
        Page<PolicyModel> policyModelPage = policyModelService
                .findByCriteria(criteria, skipDAFilter);

        List<PolicyModelDTO> policyModelDTOList = new ArrayList<>();
        for (PolicyModel policyModel : policyModelPage.getContent()) {
        	policyModelDTOList.add(PolicyModelDTO.getDTO(policyModel));
        }

        CollectionDataResponseDTO<PolicyModelDTO> response = CollectionDataResponseDTO.createWithType(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"));
        response.setData(policyModelDTOList);
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setTotalPages(policyModelPage.getTotalPages());
        response.setTotalNoOfRecords(policyModelPage.getTotalElements());

        long processingTime = System.currentTimeMillis() - startTime;
		log.info(
                "Policy model search by type has been completed, Search handled in {} milis, Total no of records : {}",
                processingTime , policyModelPage.getNumberOfElements());

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Save Policy search criteria
     * 
     * @param criteriaDTO
     * @return
     * @throws ConsoleException
     */
    @SuppressWarnings("rawtypes")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/add")
    @ApiOperation(value = "Saves a new component type search criteria and returns the ID in data field of the response.",
            notes = "<ul>" +
                    "<li><strong>Allowable values for criteria.fields.field</strong>: Fields of <strong>PolicyModel</strong> model.</li>" +
                    "<li><strong>Allowable values for criteria.fields.nestedField</strong>: tags.key, tags.label, tags.type, tags.status</li></ul>")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data saved successfully", response = SimpleResponseDTO.class)})
    public ConsoleResponseEntity<SimpleResponseDTO> savePolicySearch(
            @RequestBody SavedSearchDTO criteriaDTO) throws ConsoleException {

        log.debug("Request came to save search criteria");
        validations.assertNotBlank(criteriaDTO.getName(), "Name");
        validations.assertNotBlank(criteriaDTO.getDesc(), "Description");
        validations.assertNotBlank(criteriaDTO.getCriteriaJson(), "Criteria");
        validations.assertNotBlank(criteriaDTO.getType(), "Type");

        SavedSearch criteria = new SavedSearch(null, criteriaDTO.getName(),
                criteriaDTO.getDesc(), criteriaDTO.getCriteriaJson(),
                Status.get(criteriaDTO.getStatus()),
                SharedMode.get(criteriaDTO.getSharedMode()),
                criteriaDTO.getUserIds(),
                SavedSearchType.get(criteriaDTO.getType()));
        criteria.setLowercase_name(criteriaDTO.getName().toLowerCase());

        savedSearchService.saveCriteria(criteria);

        SimpleResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), criteria.getId());

        log.info(
                "New Policy model search criteria saved successfully and response sent, [Id: {}]",
                criteria.getId());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Modify saved policy search criteria
     * 
     * @param criteriaDTO
     * @return
     * @throws ConsoleException
     */
    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/modify")
    @ApiOperation(value = "Modifies an existing component type search criteria.",
            notes = "<ul>" +
                    "<li><strong>Allowable values for criteria.fields.field</strong>: Fields of <strong>PolicyModel</strong> model.</li>" +
                    "<li><strong>Allowable values for criteria.fields.nestedField</strong>: tags.key, tags.label, tags.type, tags.status</li></ul>")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data saved successfully", response = ResponseDTO.class)})
    public ConsoleResponseEntity<ResponseDTO> modifyCriteria(
            @RequestBody SavedSearchDTO criteriaDTO) throws ConsoleException {

        log.debug("Request came to modify the search criteria");
        validations.assertNotNull(criteriaDTO.getId(), "Id");
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
                "Policy model search criteria modifed successfully and response sent,  [Id: {}]",
                criteria.getId());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Remove saved policy search criteria
     * 
     * @param id
     * @return
     * @throws ConsoleException
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/remove/{id}")
    @ApiOperation(value="Deletes the saved search referenced by the ID.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data deleted successfully", response = ResponseDTO.class)})
    public ConsoleResponseEntity<ResponseDTO> removeCriteria(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to remove a policy model search criteria");
        validations.assertNotZero(id, "id");

        savedSearchService.removeCriteria(id);

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.deleted.code"),
                msgBundle.getText("success.data.deleted"));

        log.info(
                "Policy search criteria removed successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);

    }

    /**
     * Find saved policy search criteria by id
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

        log.debug("Request came find policy search criteria by id, [id: {}]",
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
                "Requested policy model search criteria details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * List saved policy search criteria by name
     * 
     * @param name
     * @return
     * @throws ConsoleException
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/savedlist/{type}/{name}")
    @ApiOperation(value="Returns the saved search having the passed type and name.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data found successfully")})
    public ConsoleResponseEntity<CollectionDataResponseDTO<SavedSearchDTO>> findByText(
            @PathVariable("type") String type,
            @PathVariable("name") String searchText,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws ConsoleException {

        log.debug(
                "Request came to find policy model saved criteria by name, [Name:{}]",
                searchText);

        validations.assertNotBlank(type, "type");
        validations.assertNotBlank(searchText, "name");

        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        Page<SavedSearch> criteriaPage = savedSearchService
                .findByNameOrDescriptionAndType(searchText,
                        SavedSearchType.get(type), pageable);

        if (criteriaPage.getNumberOfElements() == 0) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        List<SavedSearchDTO> criteriaDTOs = populateCriterias(criteriaPage);

        CollectionDataResponseDTO<SavedSearchDTO> response = createSaveSearchResponse(pageable,
                criteriaPage, criteriaDTOs);

        log.info(
                "Requested policy model search criteria details found and response sent, [No of records :{}]",
                criteriaDTOs.size());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * List all saved policy search criteria
     * 
     * @param name
     * @return
     * @throws ConsoleException
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/savedlist/{type}")
    @ApiOperation(value="Returns all the saved searches.",
            notes = "Given a type, this API returns a list of saved component type search. The type can be Policy_Model_Resource")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data found successfully")})
    public ConsoleResponseEntity<CollectionDataResponseDTO<SavedSearchDTO>> findByAll(
            @PathVariable("type") String type,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws ConsoleException {

        log.debug("Request came to find saved policy criterias");

        validations.assertNotBlank(type, "type");

        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        Page<SavedSearch> criteriaPage = savedSearchService
                .findByNameOrDescriptionAndType("", SavedSearchType.get(type),
                        pageable);

        if (criteriaPage.getNumberOfElements() == 0) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        List<SavedSearchDTO> criteriaDTOs = populateCriterias(criteriaPage);

        CollectionDataResponseDTO<SavedSearchDTO> response = createSaveSearchResponse(pageable,
                criteriaPage, criteriaDTOs);

        log.info(
                "Requested policy model search criteria details found and response sent, [No of records :{}]",
                criteriaDTOs.size());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    private List<SavedSearchDTO> populateCriterias(
            Page<SavedSearch> criteriaPage) throws ConsoleException {
        List<SavedSearchDTO> criteriaDTOs = new ArrayList<>(
                criteriaPage.getNumberOfElements());

        for (SavedSearch criteria : criteriaPage.getContent()) {
            SavedSearchDTO criteriaDTO = SavedSearchDTO.getDTO(criteria);
            criteriaDTOs.add(criteriaDTO);
        }
        return criteriaDTOs;
    }

    private CollectionDataResponseDTO<SavedSearchDTO> createSaveSearchResponse(
            PageRequest pageable, Page<SavedSearch> criteriaPage,
            List<SavedSearchDTO> criteriaDTOs) {
        CollectionDataResponseDTO<SavedSearchDTO> response = CollectionDataResponseDTO.createWithType(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"));
        response.setData(criteriaDTOs);
        response.setPageSize(pageable.getPageSize());
        response.setPageNo(pageable.getPageNumber());
        response.setTotalPages(criteriaPage.getTotalPages());
        response.setTotalNoOfRecords(criteriaPage.getTotalElements());
        return response;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private CollectionDataResponseDTO<PolicyModelDTO> createPolicyModelListResponse(
            SearchCriteria criteria, Page<PolicyModel> policyModelPage,
            List<PolicyModelDTO> policyModelList) {
        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"));
        response.setData(policyModelList);
        response.setPageNo(criteria.getPageNo());
        response.setPageSize(criteria.getPageSize());
        response.setTotalPages(policyModelPage.getTotalPages());
        response.setTotalNoOfRecords(policyModelPage.getTotalElements());
        return response;
    }

    /**
     * Load all search form fields with i18n support messages
     * 
     * @return
     */
    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/fields")
    public ConsoleResponseEntity<ResponseDTO> searchFields() {

        log.debug("Request came to load search fields");

        SearchFieldsDTO searchFields = new SearchFieldsDTO();

        // Policy model type
        searchFields.setType(SinglevalueFieldDTO.create("type",
                msgBundle.getText("policy.mgmt.search.fields.modelTypes")));
        searchFields.getTypeOptions().add(MultiFieldValuesDTO.create(
                PolicyModelType.RESOURCE.name(),
                msgBundle.getText(PolicyModelType.RESOURCE.getLabel())));
        searchFields.getTypeOptions()
                .add(MultiFieldValuesDTO.create(PolicyModelType.SUBJECT.name(),
                        msgBundle.getText(PolicyModelType.SUBJECT.getLabel())));

        // Policy status
        searchFields.setStatus(SinglevalueFieldDTO.create("status",
                msgBundle.getText("policy.mgmt.search.fields.status")));
        for (PolicyStatus status : PolicyStatus.values()) {
            searchFields.getStatusOptions().add(MultiFieldValuesDTO
                    .create(status.name(), msgBundle.getText(status.getKey())));
        }

        // Modified date field
        searchFields.setModifiedDate(SinglevalueFieldDTO.create(
                "lastUpdatedDate",
                msgBundle.getText("policy.mgmt.search.fields.modified.date")));
        for (DateOption dateOpt : DateOption.values()) {
            searchFields.getModifiedDateOptions()
                    .add(MultiFieldValuesDTO.create(dateOpt.name(),
                            msgBundle.getText(dateOpt.getKey())));
        }

        searchFields.setTags(SinglevalueFieldDTO.create("tags",
                msgBundle.getText("policy.mgmt.search.fields.tags")));

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
        
        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"), searchFields);

        log.info(
                "Policy model form fields and data populated successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @Override
    protected Logger getLog() {
        return log;
    }
}
