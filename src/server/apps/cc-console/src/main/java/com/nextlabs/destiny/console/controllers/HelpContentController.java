/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 6, 2016
 *
 */
package com.nextlabs.destiny.console.controllers;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.CollectionDataResponseDTO;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.HelpContent;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SearchCriteriaDTO;
import com.nextlabs.destiny.console.dto.policymgmt.porting.PolicyPortingDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NotUniqueException;
import com.nextlabs.destiny.console.init.ConfigurationDataLoader;
import com.nextlabs.destiny.console.services.HelpContentSearchService;
import com.nextlabs.destiny.console.services.policy.PolicyPortingService;

/**
 *
 * Help content controller to handle help text requests
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("/help")
public class HelpContentController extends AbstractRestController {

    private static final Logger log = LoggerFactory
            .getLogger(HelpContentController.class);

    @Autowired
    private HelpContentSearchService helpContentSearchService;
    
    @Autowired
    private PolicyPortingService policyPortingService;
    
    @Autowired
    private ConfigurationDataLoader configDataLoader;
    

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/reload")
    public ConsoleResponseEntity<ResponseDTO> reloadHelpContent()
            throws ConsoleException {
        log.debug("Request came to re-index help contents");

        long startTime = System.currentTimeMillis();
        helpContentSearchService.uploadHelpContent();
        long processingTime = System.currentTimeMillis() - startTime;

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.reindexed.code"),
                msgBundle.getText("success.data.reindexed"));

        log.info(
                "Help content re-indexing has been completed, Data re-indexed in {} milis",
                processingTime);
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/search")
    public ConsoleResponseEntity<CollectionDataResponseDTO> policyModelSearch(
            @RequestBody SearchCriteriaDTO criteriaDTO)
                    throws ConsoleException {
        log.debug("Request came to help content search");
        validations.assertNotNull(criteriaDTO.getCriteria(), "criteria");
        SearchCriteria criteria = criteriaDTO.getCriteria();

        long startTime = System.currentTimeMillis();
        Page<HelpContent> helpContentPage = helpContentSearchService
                .findHelpByCriteria(criteria);
        long processingTime = System.currentTimeMillis() - startTime;

        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"));
        response.setData(helpContentPage.getContent());
        response.setPageNo(criteria.getPageNo());
        response.setPageSize(criteria.getPageSize());
        response.setTotalPages(helpContentPage.getTotalPages());
        response.setTotalNoOfRecords(helpContentPage.getTotalElements());

        log.info(
                "Help content search has been completed, Search handled in {} milis, Total no of records : {}",
                processingTime, helpContentPage.getNumberOfElements());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    
    @SuppressWarnings("unchecked")
	@ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/importExamplePolicy")
    public ConsoleResponseEntity<ResponseDTO> importExamplePolicy()
            throws ConsoleException {
		log.debug("Request came to import example policy");
		final String SAMPLE_POLICY_BIN_PREFIX = "sample_policy";

		File helpContentDirectory = new File(configDataLoader.getHelpContentDirPath());
		if (!helpContentDirectory.isDirectory()) {
			throw new ConsoleException("Help content directory path is not a valid folder path, [ Given path:"
					+ helpContentDirectory.getPath() + "]");
		}
		File[] sampleFiles = helpContentDirectory.listFiles(
                        (dir, name) -> name.startsWith(SAMPLE_POLICY_BIN_PREFIX));
		
		PolicyPortingDTO portingDTO = null;
		boolean isSuccess = false;

		for (File file : sampleFiles) {
            try (FileInputStream fis = new FileInputStream(file)) {
				byte[] data = IOUtils.toByteArray(fis);
				portingDTO = policyPortingService.validateAndImport(data);
				isSuccess = true;
				log.info(
						"Example policy imported successfully, [ No of PM :{}, "
								+ "No of Components :{}, No of Policies :{}]",
						portingDTO.getPolicyModels().size(), portingDTO.getComponents().size(),
						portingDTO.getPolicyTree().size());
			} catch (NotUniqueException e) {
				throw e;
			} catch (Exception e) {
				throw new ConsoleException(
						"Failed to upload the help content file, [ Given path:" + helpContentDirectory.getPath() + "]",
						e);
			}
		}
		
		JSONObject importResults = new JSONObject();
        if (isSuccess) {
            importResults.put("total_policy_models",
            		portingDTO.getPolicyModels().size());
            importResults.put("total_components",
            		portingDTO.getComponents().size());
            importResults.put("total_policies", portingDTO.getPolicyTree().size());
            importResults.put("imported_policy_ids", portingDTO.getImportedPolicyIds());
        }

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.file.import.code"),
                msgBundle.getText("success.file.import"), importResults);
        log.info("Example policy details imported successfully");
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
