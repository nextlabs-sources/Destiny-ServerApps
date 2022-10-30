/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 12, 2015
 *
 */
package com.nextlabs.destiny.console.controllers.policymgmt;

import static com.nextlabs.destiny.console.enums.SavedSearchType.POLICY;

import java.util.ArrayList;
import java.util.List;

import com.nextlabs.destiny.console.enums.WorkflowRequestLevelStatus;
import io.swagger.annotations.*;
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
import com.nextlabs.destiny.console.dto.common.MultiFieldValuesDTO;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.common.SinglevalueFieldDTO;
import com.nextlabs.destiny.console.dto.common.StringFieldValue;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.dto.policymgmt.SavedSearchDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SearchCriteriaDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SearchFieldsDTO;
import com.nextlabs.destiny.console.enums.DateOption;
import com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus;
import com.nextlabs.destiny.console.enums.PolicyEffect;
import com.nextlabs.destiny.console.enums.PolicyStatus;
import com.nextlabs.destiny.console.enums.SharedMode;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.model.SavedSearch;
import com.nextlabs.destiny.console.services.SavedSearchService;
import com.nextlabs.destiny.console.services.policy.PolicyMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicySearchService;
import springfox.documentation.annotations.ApiIgnore;

/**
 * REST Controller for Policy search function
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("/policy/search")
@Api(tags = {"Policy Search Controller"})
@SwaggerDefinition(tags = { @Tag(name = "Policy Search Controller", description = "REST APIs for retrieving policies based on search criteria") })
public class PolicySearchController extends AbstractRestController {

    private static final Logger log = LoggerFactory
            .getLogger(PolicySearchController.class);

    @Autowired
    private PolicySearchService policySearchService;

    @Autowired
    private PolicyMgmtService policyMgmtService;

    @Autowired
    private SavedSearchService savedSearchService;

    /**
     * Policy List handles by this method. This will return the policy list
     * according to given policy search criteria
     * 
     * @param criteriaDTO
     * @return List of policies to display
     * @throws ConsoleException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    @ApiOperation(value="Searches for a list of policies based on a given search criteria.",
    	notes="Returns a list of policies that match the given search criteria" +
                "\n<ul>" +
                "<li><strong>Allowable values for criteria.fields.field</strong>: Fields of <strong>PolicyLite</strong> model.</li>" +
                "<li><strong>Allowable values for criteria.fields.nestedField</strong>: tags.key, tags.label, tags.type, tags.status</li></ul>")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data loaded successfully")})
    public ConsoleResponseEntity<CollectionDataResponseDTO<PolicyLite>> policyLiteSearch(
            @RequestBody SearchCriteriaDTO criteriaDTO)
            throws ConsoleException {

        log.debug("Request came to policy search");
        validations.assertNotNull(criteriaDTO.getCriteria(), "criteria");
        SearchCriteria criteria = criteriaDTO.getCriteria();

        long startTime = System.currentTimeMillis();
        Page<PolicyLite> policyLitePage = policySearchService
                .findPolicyByCriteria(criteria);
        long processingTime = System.currentTimeMillis() - startTime;

        for (PolicyLite policyLite : policyLitePage.getContent()) {
            policyMgmtService.enforceTBAC(policyLite);
        }

        CollectionDataResponseDTO<PolicyLite> response = CollectionDataResponseDTO.createWithType(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"));
        response.setData(policyLitePage.getContent());
        response.setPageNo(criteria.getPageNo());
        response.setPageSize(criteria.getPageSize());
        response.setTotalPages(policyLitePage.getTotalPages());
        response.setTotalNoOfRecords(policyLitePage.getTotalElements());

        log.info(
                "Policy search has been completed, Search handled in {} milis, Total no of records : {}",
                processingTime, policyLitePage.getNumberOfElements());
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
    public ConsoleResponseEntity<CollectionDataResponseDTO> findPoliciesByIds(
            @RequestBody SearchCriteriaDTO criteriaDTO)
            throws ConsoleException {

        log.debug("Request came to search policies by given ids");
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
        Page<PolicyLite> policyLitePage = policySearchService.findPolicyByIds(
                ids, criteria.getSortFields(),
                PageRequest.of(criteria.getPageNo(), criteria.getPageSize()));

        for (PolicyLite policyLite : policyLitePage.getContent()) {
            policyMgmtService.enforceTBAC(policyLite);
        }
        
        long processingTime = System.currentTimeMillis() - startTime;

        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"));
        response.setData(policyLitePage.getContent());
        response.setPageNo(criteria.getPageNo());
        response.setPageSize(criteria.getPageSize());
        response.setTotalPages(policyLitePage.getTotalPages());
        response.setTotalNoOfRecords(policyLitePage.getTotalElements());

        log.info(
                "Policy search by ids has been completed, Search handled in {} milis, Total no of records : {}",
                processingTime, policyLitePage.getNumberOfElements());
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
        FacetResult facetResult = policySearchService
                .findFacetByCriteria(criteria);

        long processingTime = System.currentTimeMillis() - startTime;

        SimpleResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"), facetResult);

        log.info(
                "Policy facet search has been completed, Search handled in {} milis, Total no of facet records : {}",
                processingTime, facetResult.getTerms().size());
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
    @ApiOperation(value="Saves a new policy search criteria and returns the ID in data field of the response.",
            notes = "<ul>" +
                    "<li><strong>Allowable values for criteria.fields.field</strong>: Fields of <strong>PolicyLite</strong> model.</li>" +
                    "<li><strong>Allowable values for criteria.fields.nestedField</strong>: tags.key, tags.label, tags.type, tags.status</li></ul>")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data saved successfully", response = SimpleResponseDTO.class)})
    public ConsoleResponseEntity<ResponseDTO> savePolicySearch(
            @RequestBody SavedSearchDTO criteriaDTO) throws ConsoleException {

        log.debug("Request came to save search criteria");
        validations.assertNotBlank(criteriaDTO.getName(), "Name");
        validations.assertNotBlank(criteriaDTO.getDesc(), "Description");
        validations.assertNotBlank(criteriaDTO.getCriteriaJson(), "Criteria");

        SavedSearch criteria = new SavedSearch(null, criteriaDTO.getName(),
                criteriaDTO.getDesc(), criteriaDTO.getCriteriaJson(),
                Status.get(criteriaDTO.getStatus()),
                SharedMode.get(criteriaDTO.getSharedMode()),
                criteriaDTO.getUserIds(), POLICY);
        criteria.setLowercase_name(criteriaDTO.getName().toLowerCase());

        savedSearchService.saveCriteria(criteria);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), criteria.getId());

        log.info(
                "New Policy search criteria saved successfully and response sent, [Id: {}]",
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
    @ApiOperation(value = "Modifies an existing policy search criteria.",
            notes = "<ul>" +
                    "<li><strong>Allowable values for criteria.fields.field</strong>: Fields of <strong>PolicyLite</strong> model.</li>" +
                    "<li><strong>Allowable values for criteria.fields.nestedField</strong>: tags.key, tags.label, tags.type, tags.status</li>" +
                    "</ul>")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data saved successfully", response = ResponseDTO.class)})
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
        criteria.setUserIds(criteriaDTO.getUserIds());
        criteria.setLowercase_name(criteriaDTO.getName().toLowerCase());

        savedSearchService.saveCriteria(criteria);

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.modified.code"),
                msgBundle.getText("success.data.modified"));

        log.info(
                "Policy search criteria modifed successfully and response sent,  [Id: {}]",
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

        log.debug("Request came to remove a criteria");
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
                "Requested policy search criteria details found and response sent");
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
    @GetMapping(value = "/savedlist/{name}")
    @ApiOperation(value="Returns the saved search having passed value as name or description.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data found successfully")})
    public ConsoleResponseEntity<CollectionDataResponseDTO<SavedSearchDTO>> findByText(
            @PathVariable("name") String searchText,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws ConsoleException {

        log.debug("Request came to find policy criteria by name, [Name:{}]",
                searchText);

        validations.assertNotBlank(searchText, "name");

        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        Page<SavedSearch> criteriaPage = savedSearchService
                .findByNameOrDescriptionAndType(searchText, POLICY, pageable);

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
                "Requested policy search criteria details found and response sent, [No of records :{}]",
                criteriaDTOs.size());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * List all saved policy search criterias
     * 
     * @param name
     * @return
     * @throws ConsoleException
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/savedlist")
    @ApiOperation(value="Returns all the saved searches.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data found successfully")})
    public ConsoleResponseEntity<CollectionDataResponseDTO<SavedSearchDTO>> findByAll(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws ConsoleException {

        log.debug("Request came to find saved policy criterias");

        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        Page<SavedSearch> criteriaPage = savedSearchService
                .findByNameOrDescriptionAndType("", POLICY, pageable);

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
                "Requested policy search criteria details found and response sent, [No of records :{}]",
                criteriaDTOs.size());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
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
    @ApiOperation(value = "Returns a list of available search fields for policy.",
            notes = "<ul>" +
                    "<li><strong>Allowable values for criteria.fields.field</strong>: Fields of <strong>PolicyLite</strong> model.</li>" +
                    "<li><strong>Allowable values for criteria.fields.nestedField</strong>: tags.key, tags.label, tags.type, tags.status</li>" +
                    "</ul>")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data loaded successfully")})
    public ConsoleResponseEntity<SimpleResponseDTO<SearchFieldsDTO>> searchFields() {

        log.debug("Request came to load search fields");

        SearchFieldsDTO searchFields = new SearchFieldsDTO();

        // Policy status
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

        // Policy status
        searchFields.setWorkflowStatus(SinglevalueFieldDTO.create("activeWorkflowRequestLevelStatus",
                msgBundle.getText("policy.mgmt.search.fields.workflow.status")));
        searchFields.getWorkflowStatusOptions()
                .add(MultiFieldValuesDTO.create(
                        WorkflowRequestLevelStatus.PENDING.name(), msgBundle
                                .getText(WorkflowRequestLevelStatus.PENDING.getKey())));
        searchFields.getWorkflowStatusOptions()
                .add(MultiFieldValuesDTO.create(WorkflowRequestLevelStatus.APPROVED
                        .name(), msgBundle
                                .getText(WorkflowRequestLevelStatus.APPROVED.getKey())));
        searchFields.getWorkflowStatusOptions()
                .add(MultiFieldValuesDTO.create(WorkflowRequestLevelStatus.REQUESTED_AMENDMENT
                        .name(), msgBundle
                                .getText(WorkflowRequestLevelStatus.REQUESTED_AMENDMENT.getKey())));

        // Policy effect
        searchFields.setPolicyEffect(SinglevalueFieldDTO.create("effectType",
                msgBundle.getText("policy.mgmt.search.fields.effect")));
        for (PolicyEffect effect : PolicyEffect.values()) {
            searchFields.getPolicyEffectOptions().add(MultiFieldValuesDTO
                    .create(effect.name(), msgBundle.getText(effect.getKey())));
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

        searchFields.setSubPolicySearch(SinglevalueFieldDTO.create(
                "hasSubPolicies",
                msgBundle.getText("policy.mgmt.search.fields.sub.policies")));

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

        log.info("Policy search form fields and data populated successfully and response sent");
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
