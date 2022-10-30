package com.nextlabs.destiny.console.controllers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.MultiFieldValuesDTO;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.common.SinglevalueFieldDTO;
import com.nextlabs.destiny.console.dto.config.SysConfigDTO;
import com.nextlabs.destiny.console.dto.config.SysConfigGroupDTO;
import com.nextlabs.destiny.console.dto.config.SysConfigValueDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SearchCriteriaDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SysConfigSearchFieldsDTO;
import com.nextlabs.destiny.console.services.SysConfigService;

/**
 * REST controller to obtain and update system configurations.
 *
 * @author Sachindra Dasun
 */
@RestController
@ApiVersion(1)
@RequestMapping("sysconfig")
public class SysConfigController extends AbstractRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SysConfigController.class);

    @Autowired
    private SysConfigService sysConfigService;

	@GetMapping("get")
	public ConsoleResponseEntity<Map<String, List<SysConfigDTO>>> findByMainGroup(
			@RequestParam("mainGroup") String mainGroup,
			@RequestParam(value = "includeAdvanced", required = false, defaultValue = "false") boolean includeAdvanced) {
		validations.assertNotBlank(mainGroup, "mainGroup");
		Map<String, List<SysConfigDTO>> sysConfigDTOS = sysConfigService.findByMainGroup(mainGroup, includeAdvanced);
		return ConsoleResponseEntity.get(sysConfigDTOS, HttpStatus.OK);
	}

    @GetMapping("getUIConfigs")
    public ConsoleResponseEntity<Map<String, String>> findUiConfigs() {
        Map<String, String> uiConfigs = sysConfigService.findUiConfigs();
        return ConsoleResponseEntity.get(uiConfigs, HttpStatus.OK);
    }

    @GetMapping("reset")
    public ConsoleResponseEntity resetByMainGroup(@RequestParam("mainGroup") String mainGroup) {
        validations.assertNotBlank(mainGroup, "mainGroup");
        sysConfigService.resetByMainGroup(mainGroup);
        return ConsoleResponseEntity.get(HttpStatus.OK);
    }

    @PostMapping("search")
    public ConsoleResponseEntity<List<SysConfigGroupDTO>> search(@RequestBody SearchCriteriaDTO searchCriteriaDTO) {
        validations.assertNotNull(searchCriteriaDTO, "searchCriteriaDTO");
        List<SysConfigGroupDTO> sysConfigGroupDTOS = sysConfigService.search(searchCriteriaDTO.getCriteria());
        return ConsoleResponseEntity.get(sysConfigGroupDTOS, HttpStatus.OK);
    }

    @PostMapping("update")
    public ConsoleResponseEntity<SimpleResponseDTO<String>> updateValue(@RequestBody List<SysConfigValueDTO> sysConfigValueDTOS) {
        validations.assertNotNull(sysConfigValueDTOS, "sysConfigValueDTOS");
        Set<String> updatedApplications = sysConfigService.updateValue(sysConfigValueDTOS);
        sysConfigService.sendConfigRefreshRequest(updatedApplications);
        SimpleResponseDTO<String> response = SimpleResponseDTO.createWithType(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), msgBundle.getText("success.data.saved"));
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

	@GetMapping("fields")
    public ConsoleResponseEntity<SimpleResponseDTO<SysConfigSearchFieldsDTO>> searchFields() {
    	LOGGER.debug("Request came to load search fields");

    	List<String> mainGroups = sysConfigService.findMainGroups();
    	
    	SysConfigSearchFieldsDTO searchFields = new SysConfigSearchFieldsDTO();

        // Configuration main groups
    	searchFields.setGroup(SinglevalueFieldDTO.create("group",
                msgBundle.getText("config.search.fields.sys.group")));
        for (String mainGroup : mainGroups) {
            searchFields.getGroupOptions().add(MultiFieldValuesDTO
                    .create(mainGroup, msgBundle.getText(mainGroup)));
        }

        // sort by options
        searchFields.setSort(SinglevalueFieldDTO.create("sortBy",
                msgBundle.getText("policy.mgmt.search.fields.sortBy")));
        searchFields.getSortOptions().add(MultiFieldValuesDTO.create("mainGroup",
                msgBundle.getText("policy.mgmt.search.fields.name"), 
                msgBundle.getText("policy.mgmt.search.fields.order.asc")));
        searchFields.getSortOptions().add(MultiFieldValuesDTO.create("mainGroup",
                msgBundle.getText("policy.mgmt.search.fields.nameZtoA"), 
                msgBundle.getText("policy.mgmt.search.fields.order.desc")));
        searchFields.getSortOptions().add(MultiFieldValuesDTO.create(
                "lastModifiedOn",
                msgBundle.getText("policy.mgmt.search.fields.lastupdated"), 
                msgBundle.getText("policy.mgmt.search.fields.order.desc")));
        
        SimpleResponseDTO<SysConfigSearchFieldsDTO> response = SimpleResponseDTO.createWithType(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"), searchFields);

        LOGGER.info(
                "System Configuration form fields and data populated successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

	@GetMapping("refresh")
    public ConsoleResponseEntity<SimpleResponseDTO<String>> sendRefreshRequest(@RequestParam("applications") Set<String> applications) {
        sysConfigService.sendConfigRefreshRequest(applications);
        SimpleResponseDTO<String> response = SimpleResponseDTO.createWithType(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), msgBundle.getText("success.data.saved"));
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @Override
    protected Logger getLog() {
        return LOGGER;
    }

}
