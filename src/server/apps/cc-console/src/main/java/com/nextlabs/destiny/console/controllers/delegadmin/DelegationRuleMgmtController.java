/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 25, 2016
 *
 */
package com.nextlabs.destiny.console.controllers.delegadmin;

import static com.nextlabs.destiny.console.enums.DevEntityType.DELEGATION_POLICY;
import static com.nextlabs.destiny.console.utils.SecurityContextUtil.getCurrentUser;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.swagger.annotations.ApiOperation;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.nextlabs.destiny.console.AuditLogger;
import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.controllers.AbstractRestController;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.delegadmin.DelegateRuleLite;
import com.nextlabs.destiny.console.dto.delegadmin.DelegationRuleDTO;
import com.nextlabs.destiny.console.dto.delegadmin.porting.DelegationPortingDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentConditionDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.exceptions.CircularReferenceException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.search.repositories.DelegateModelSearchRepository;
import com.nextlabs.destiny.console.services.delegadmin.DelegateRulePortingService;
import com.nextlabs.destiny.console.services.delegadmin.DelegationRuleMgmtService;

/**
 * Controller for the Delegation rule management
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("/delegationAdmin/rule/mgmt")
public class DelegationRuleMgmtController extends AbstractRestController {

    private static final Logger log = LoggerFactory
            .getLogger(DelegationRuleMgmtController.class);

    @Autowired
    private DelegationRuleMgmtService delegationRuleMgmtService;
    
    @Autowired
    private DelegateRulePortingService delegateRulePortingService;

    @Resource
    private DelegateModelSearchRepository delegateModelSearchRepository;

    @Resource
    private ApplicationUserSearchRepository applicationUserSearchRepository;

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/add")
    @ApiOperation(value = "Creates a new delegation rule",
		notes="Returns a success message along with the new delegation rule's id, when the delegation policy has been successfully saved.")
    public ConsoleResponseEntity<SimpleResponseDTO<Long>> createRule(
            @RequestBody DelegationRuleDTO delegationRuleDTO)
            throws ConsoleException {

        log.debug("Request came to add new delegation policy");

        validations.assertNotBlank(delegationRuleDTO.getName(), "Name");
        validations.assertNotBlank(delegationRuleDTO.getEffectType(), "Effect");
        validations.assertNotBlank(delegationRuleDTO.getStatus(), "Status");

        delegationRuleDTO.setCategory(DELEGATION_POLICY);
        DelegationRuleDTO delegationRule;
        SimpleResponseDTO<Long> response;
        
		try {
			delegationRule = delegationRuleMgmtService.save(delegationRuleDTO);
			response = SimpleResponseDTO.createWithType(
	                msgBundle.getText("success.data.saved.code"),
	                msgBundle.getText("success.data.saved"),
	                delegationRule.getId());
			
			log.info("New delegation policy saved successfully and response sent");
	        logAudit(delegationRuleDTO, "created");
		} catch (CircularReferenceException e) {
			response = SimpleResponseDTO.createWithType(msgBundle.getText(e.getStatusCode()),
                    msgBundle.getText(e.getStatusMsg()), -1L);
		}

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/modify")
    public ConsoleResponseEntity<ResponseDTO> modifyPolicy(
            @RequestBody DelegationRuleDTO delegationRuleDTO)
            throws ConsoleException {

        log.debug("Request came to modfiy delegation policy");

        validations.assertNotBlank(delegationRuleDTO.getName(), "Name");
        validations.assertNotBlank(delegationRuleDTO.getEffectType(), "Effect");
        validations.assertNotBlank(delegationRuleDTO.getStatus(), "Status");

        delegationRuleDTO.setCategory(DELEGATION_POLICY);
        DelegationRuleDTO delegationRule;
        ResponseDTO response;
		try {
			delegationRule = delegationRuleMgmtService.modify(delegationRuleDTO);
			response = SimpleResponseDTO.create(
	                msgBundle.getText("success.data.modified.code"),
	                msgBundle.getText("success.data.modified"),
	                delegationRule.getId());
			
			log.info("Delegation policy modified successfully and response sent");
	        logAudit(delegationRuleDTO, "modified");
		} catch (CircularReferenceException e) {
			response = ResponseDTO.create(msgBundle.getText(e.getStatusCode()), msgBundle.getText(e.getStatusMsg()));
		}
        
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}")
    public ConsoleResponseEntity<ResponseDTO> policyById(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to find Delegation policy by Id");

        validations.assertNotNull(id, "Id");
        DelegationRuleDTO delegationRuleDTO = delegationRuleMgmtService
                .findById(id);

        if (delegationRuleDTO == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), delegationRuleDTO);

        log.info("Requested Delegation policy details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/remove/{id}")
    public ConsoleResponseEntity<ResponseDTO> removeById(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to remove delegation policy by Id");

        validations.assertNotNull(id, "Id");
        DelegationRuleDTO delegationRuleDTO = delegationRuleMgmtService.findById(id);
        delegationRuleMgmtService.remove(id);

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.deleted.code"),
                msgBundle.getText("success.data.deleted"));

        log.info("Delegation rules removed and successfully and response sent");
        logAudit(delegationRuleDTO, "deleted");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/bulkDelete")
    public ConsoleResponseEntity<ResponseDTO> removeById(
            @RequestBody List<Long> ids) throws ConsoleException {

        log.debug("Request came to bulk delete delegation rules");

        validations.assertNotNull(ids, "Ids");
        
        List<DelegationRuleDTO> delegationRuleDTOs = new ArrayList<>();
        for(Long ruleId : ids) {
        	DelegationRuleDTO delegationRuleDTO = delegationRuleMgmtService.findById(ruleId);
        	if(delegationRuleDTO != null) {
        		delegationRuleDTOs.add(delegationRuleDTO);
        	}
        }
        delegationRuleMgmtService.remove(ids);

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.deleted.code"),
                msgBundle.getText("success.data.deleted"));
        
        log.info("Delegation rules removed successfully and response sent");
        for(DelegationRuleDTO delegationRuleDTO : delegationRuleDTOs) {
        	logAudit(delegationRuleDTO, "deleted");
        }

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }
    

    /**
     * <p>
     * Exports the given rules
     * </p>
     *
     * @param response
     * @param ruleIds
     * @return
     * @throws ConsoleException
     *
     */
    @PostMapping(value = "/export")
    public ConsoleResponseEntity<ResponseDTO> daRuleExport(
            @RequestBody List<Long> ruleIds, HttpServletResponse response)
            throws ConsoleException {
        log.debug("DA Rule export request came, Rule ids :{}", ruleIds);

        String filename = delegateRulePortingService.export(ruleIds);

        ResponseDTO responseDTO = SimpleResponseDTO.create(
                msgBundle.getText("success.file.export.code"),
                msgBundle.getText("success.file.export"), filename);
        log.info("Delagtion Rule details exported successfully, [Filename = {}]",
                filename);

        return ConsoleResponseEntity.get(responseDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/exportAll")
    public ConsoleResponseEntity<ResponseDTO> daRuleExportAll(
            @RequestBody List<Long> ruleIds, HttpServletResponse response)
            throws ConsoleException {
    	 log.debug("Request came to export all da rules");

         SearchCriteria criteria = new SearchCriteria();
         criteria.setPageNo(0);
         criteria.setPageSize(10000);
         Page<DelegateRuleLite> litePage = delegationRuleMgmtService
                 .findPolicyByCriteria(criteria);

         List<DelegateRuleLite> ruleLites = litePage.getContent();

         String filename = delegateRulePortingService.exportAll(ruleLites);

         ResponseDTO responseDTO = SimpleResponseDTO.create(
                 msgBundle.getText("success.file.export.code"),
                 msgBundle.getText("success.file.export"), filename);

         log.info("All Da Rules exported successfully, [Filename = {}]",
                 filename);

         return ConsoleResponseEntity.get(responseDTO, HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @ResponseBody
    @PostMapping(value = "/import")
    public ConsoleResponseEntity<ResponseDTO> importDARules(
            MultipartHttpServletRequest request) throws ConsoleException {
        log.info("Rule Upload request received");

        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = null;
        DelegationPortingDTO portDTO = null;

        boolean isSuccess = false;
        boolean isCircularRef = false;
        ResponseDTO response = null;
        JSONObject importResults = new JSONObject();
        
        while (itr.hasNext() && !isCircularRef) {
            try {
                mpf = request.getFile(itr.next());
                log.info("Import DA rules - [File name :{}, file size :{}]",
                        mpf.getOriginalFilename(), mpf.getSize());
                byte[] data = mpf.getBytes();
                portDTO = delegateRulePortingService.validateAndImport(data);
                isSuccess = true;
                isCircularRef = false;
            } catch (IOException e) {
                log.error("Error encountered during file import", e);
                isSuccess = false;
                isCircularRef = false;
                break;
            } catch (CircularReferenceException e) {
            	log.error("Circular reference: ", e);
            	response = ResponseDTO.create(msgBundle.getText(e.getStatusCode()), msgBundle.getText(e.getStatusMsg()));
            	isSuccess = false;
            	isCircularRef = true;
			}
        }

        if(!isCircularRef) {
        	if (isSuccess) {
                importResults.put("total_policy_models",
                        portDTO.getDelegateModels().size());
                importResults.put("total_components",
                        portDTO.getDelegateComponents().size());
                importResults.put("total_policies", portDTO.getDelegateRules().size());
            }
            
            response = SimpleResponseDTO.create(
                    msgBundle.getText("success.file.import.code"),
                    msgBundle.getText("success.file.import"), importResults);
        }
        
        log.info("Import policy status response");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }
    
    @Override
    protected Logger getLog() {
        return log;
    }
    
    protected void logAudit(DelegationRuleDTO ruleDTO, String actionName) {
    	if("deleted".equalsIgnoreCase(actionName)) {
            AuditLogger.log("Delegation rule {}, [name :{}] by {}",
        			actionName,
        			ruleDTO.getName(),
        			getCurrentUser().getDisplayName());
    	} else {
            AuditLogger.log("Delegation rule {}, [name :{}, effect :{}, condition :{}] by {}",
        			actionName,
        			ruleDTO.getName(),
        			ruleDTO.getEffectType(),
        			getConditionDisplayString(ruleDTO.getSubjectComponent()),
        			getCurrentUser().getDisplayName());
    	}
    }
    
    protected String getConditionDisplayString(ComponentDTO subjectComponent) {
    	StringBuilder displayString = new StringBuilder();
    	
    	if(!subjectComponent.getConditions().isEmpty()) {
	    	for(ComponentConditionDTO componentConditionDTO : subjectComponent.getConditions()) {
	    		displayString.append(displayString.length() > 0 ? 
	    				", " + componentConditionDTO.getAttribute() + " " + componentConditionDTO.getOperator() + " " + componentConditionDTO.getValue() 
	    				: componentConditionDTO.getAttribute() + " " + componentConditionDTO.getOperator() + " " + componentConditionDTO.getValue());
	    	}
    	} else {
    		displayString.append("-NONE-");
    	}
    	
    	return displayString.toString();
    }
}
