/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 25, 2016
 *
 */
package com.nextlabs.destiny.console.controllers.delegadmin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
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
import com.nextlabs.destiny.console.controllers.AbstractRestController;
import com.nextlabs.destiny.console.dto.common.CollectionDataResponseDTO;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.FacetResult;
import com.nextlabs.destiny.console.dto.common.LiteDTO;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.common.StringFieldValue;
import com.nextlabs.destiny.console.dto.delegadmin.DelegationComponentLite;
import com.nextlabs.destiny.console.dto.policymgmt.SearchCriteriaDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.services.delegadmin.DelegationComponentSearchService;

/**
 *
 * REST Controller for Delegation components search function
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("/delegationAdmin/component/search")
public class DelegationComponentSearchController extends AbstractRestController {

	private static final Logger log = LoggerFactory.getLogger(DelegationComponentSearchController.class);

	@Autowired
	private DelegationComponentSearchService componentSearchService;

	/**
	 * Component List handles by this method. This will return the component
	 * list according to given component search criteria
	 * 
	 * @param criteriaDTO
	 * @return List of components to display
	 * @throws ConsoleException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping
	public ConsoleResponseEntity<CollectionDataResponseDTO> componentLiteSearch(
			@RequestBody SearchCriteriaDTO criteriaDTO) throws ConsoleException {

		log.debug("Request came to delegation component search");
		validations.assertNotNull(criteriaDTO.getCriteria(), "criteria");
		SearchCriteria criteria = criteriaDTO.getCriteria();

		long startTime = System.currentTimeMillis();
		Page<DelegationComponentLite> componentLitePage = componentSearchService.findByCriteria(criteria);

		long processingTime = System.currentTimeMillis() - startTime;

		CollectionDataResponseDTO response = CollectionDataResponseDTO
				.create(msgBundle.getText("success.data.loaded.code"), msgBundle.getText("success.data.loaded"));
		response.setPageNo(criteria.getPageNo());
		response.setPageSize(criteria.getPageSize());
		response.setTotalPages(componentLitePage.getTotalPages());
		response.setTotalNoOfRecords(componentLitePage.getTotalElements());
		response.setData(componentLitePage.getContent());

		log.info("DelegationComponent search has been completed, Search handled in {} milis, Total no of records : {}",
				processingTime, componentLitePage.getNumberOfElements());
		return ConsoleResponseEntity.get(response, HttpStatus.OK);
	}

	/**
	 * This will return the list of components according to the given delegation
	 * component ids.
	 * 
	 * @param criteriaDTO
	 *            of component ids
	 * @return List of policies to display
	 * @throws ConsoleException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value = "/ids")
	public ConsoleResponseEntity<CollectionDataResponseDTO> findComponenetsByIds(
			@RequestBody SearchCriteriaDTO criteriaDTO) throws ConsoleException {

		log.debug("Request came to search delegation components by given ids");
		validations.assertNotNull(criteriaDTO.getCriteria(), "criteria");
		validations.assertNotZero(Long.valueOf(criteriaDTO.getCriteria().getFields().size()), "ids");
		SearchCriteria criteria = criteriaDTO.getCriteria();

		List<Long> ids = new ArrayList<>();
		SearchField field = criteria.getFields().get(0);
		StringFieldValue fieldValues = (StringFieldValue) field.getValue();
		List<String> values = (List<String>) fieldValues.getValue();
		for (String value : values) {
			ids.add(Long.valueOf(value));
		}

		long startTime = System.currentTimeMillis();

		Page<DelegationComponentLite> componentLitePage = componentSearchService.findComponentsByIds(ids,
                criteria.getSortFields(), PageRequest.of(criteria.getPageNo(), criteria.getPageSize()));

		long processingTime = System.currentTimeMillis() - startTime;

		CollectionDataResponseDTO response = CollectionDataResponseDTO
				.create(msgBundle.getText("success.data.loaded.code"), msgBundle.getText("success.data.loaded"));
		response.setPageNo(criteria.getPageNo());
		response.setPageSize(criteria.getPageSize());
		response.setTotalPages(componentLitePage.getTotalPages());
		response.setTotalNoOfRecords(componentLitePage.getTotalElements());
		response.setData(componentLitePage.getContent());

		log.info(
				"Delegation component search by ids has been completed, Search handled in {} milis, Total no of records : {}",
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
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value = "/facet")
	public ConsoleResponseEntity<SimpleResponseDTO> facetSearch(@RequestBody SearchCriteriaDTO criteriaDTO)
			throws ConsoleException {

		log.debug("Request came to component facet search");
		validations.assertNotNull(criteriaDTO.getCriteria(), "criteria");
		SearchCriteria criteria = criteriaDTO.getCriteria();

		long startTime = System.currentTimeMillis();
		FacetResult facetResult = componentSearchService.findFacetByCriteria(criteria);

		long processingTime = System.currentTimeMillis() - startTime;

		SimpleResponseDTO response = SimpleResponseDTO.create(msgBundle.getText("success.data.loaded.code"),
				msgBundle.getText("success.data.loaded"), facetResult);

		log.info(
				"Delegation component facet search has been completed, Search handled in {} milis, Total no of facet records : {}",
				processingTime, facetResult.getTerms().size());
		return ConsoleResponseEntity.get(response, HttpStatus.OK);
	}

	/**
	 * Find the delegation components by group type.
	 * 
	 * @param group
	 *            group type
	 * @return List of components to display}
	 * @throws ConsoleException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = "/listNames/{group}")
	@ApiOperation(value = "Returns list of delegation components based on group type.", 
		notes = "Given a group type or a delegation component type, this API returns a list of delegation components.")
	public ConsoleResponseEntity<CollectionDataResponseDTO> findNamesByGroupType(@PathVariable("group") String group,
			@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
			throws ConsoleException {

		log.debug("Request came to delegation components by group type");
		validations.assertNotBlank(group, "Group Type");

        PageRequest pageable = PageRequest.of(pageNo, pageSize);

		long startTime = System.currentTimeMillis();
		Page<DelegationComponentLite> componentsPage = componentSearchService.findComponentsByGroupAndType(group, null,
				pageable);
		long processingTime = System.currentTimeMillis() - startTime;

		List<LiteDTO> liteDTOS = componentsPage.getContent().stream()
				.filter(component -> !"ACTION".equalsIgnoreCase(group) ||
						(!msgBundle.getText("module.pv.name").equals(component.getName())
								&& !msgBundle.getText("action.pv.name").equals(component.getName())))
				.map(component -> {
			LiteDTO liteDTO = new LiteDTO(component.getId(), component.getName())
					.put(LiteDTO.DATA, ImmutableMap.of(LiteDTO.POLICY_MODEL_ID, component.getModelId(),
							LiteDTO.POLICY_MODEL_NAME, component.getModelType()));
			updateDisplayName(liteDTO);
			if (component.getPredicateData() != null) {
				if(component.getPredicateData().getActions().size() == 1) {
					liteDTO.put(LiteDTO.SHORT_NAME, component.getPredicateData().getActions().get(0));
				}

				if (!component.getPredicateData().getActions().isEmpty()
						|| !component.getPredicateData().getAttributes().isEmpty()
						|| !component.getPredicateData().getReferenceIds().isEmpty()) {
					liteDTO.setEmpty(false);
				} else {
					liteDTO.setEmpty(true);
				}
			}

			if(liteDTO.getProperties().get(LiteDTO.SHORT_NAME) == null) {
				liteDTO.put(LiteDTO.SHORT_NAME, convertShortName(liteDTO.getName()));
			}

			return liteDTO;
		}).collect(Collectors.toList());

		Collections.sort(liteDTOS);
		CollectionDataResponseDTO response = CollectionDataResponseDTO
				.create(msgBundle.getText("success.data.loaded.code"), msgBundle.getText("success.data.loaded"));
		response.setData(liteDTOS);
		response.setPageNo(pageNo);
		response.setPageSize(pageSize);
		response.setTotalPages(componentsPage.getTotalPages());
		response.setTotalNoOfRecords(componentsPage.getTotalElements());

		log.info(
				"Delegation component search by group type has been completed, Search handled in {} milis, Total no of records : {}",
				processingTime, componentsPage.getContent().size());
		return ConsoleResponseEntity.get(response, HttpStatus.OK);
	}

	/**
	 * This will update the display names of the components
	 * 
	 * @param liteDTO
	 */
	private void updateDisplayName(LiteDTO liteDTO) {
		if (liteDTO.getName().equalsIgnoreCase("View Administrator")) {
			liteDTO.setName(msgBundle.getText("action.manage.system.settings"));
		} else if (liteDTO.getName().equalsIgnoreCase("View Reporter")) {
			liteDTO.setName(msgBundle.getText("action.view.reports"));
		} else if (liteDTO.getName().equalsIgnoreCase("Manage Reporter")) {
			liteDTO.setName(msgBundle.getText("action.manage.reports"));
		} else if (liteDTO.getName().equalsIgnoreCase("Administrator")) {
			liteDTO.setName(msgBundle.getText("module.sys.settings.name"));
		} else if (liteDTO.getName().equalsIgnoreCase("Reporter")) {
			liteDTO.setName(msgBundle.getText("module.reports.name"));
		}
	}

	private String convertShortName(String name) {
		if(name.indexOf(" - ") > -1) {
			return name.substring(name.indexOf(" - ") + 3).replaceAll(" ", "_").toUpperCase();
		}

		return name.replaceAll(" ", "_").toUpperCase();
	}

    /**
     * Find the delegation components by group type and model type.
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
    public ConsoleResponseEntity<CollectionDataResponseDTO> findNamesByGroupAndType(
            @PathVariable("group") String group,
            @PathVariable("type") String type,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws ConsoleException {

        log.debug("Request came to delegation components by group type");
        validations.assertNotBlank(group, "Group Type");
        validations.assertNotBlank(type, "Model Type");

        PageRequest pageable = PageRequest.of(pageNo, pageSize);

        long startTime = System.currentTimeMillis();
        Page<DelegationComponentLite> componentsPage = componentSearchService
                .findComponentsByGroupAndType(group, type, pageable);
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

		Collections.sort(liteDTOS);
		CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
				msgBundle.getText("success.data.loaded.code"),
				msgBundle.getText("success.data.loaded"));
		response.setData(liteDTOS);
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setTotalPages(componentsPage.getTotalPages());
        response.setTotalNoOfRecords(componentsPage.getTotalElements());

        log.info(
                "Delegation component search by group type and model type has been completed, "
                        + "Search handled in {} milis, Total no of records : {}",
                processingTime, componentsPage.getContent().size());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

	@Override
	protected Logger getLog() {
		return log;
	}

}
