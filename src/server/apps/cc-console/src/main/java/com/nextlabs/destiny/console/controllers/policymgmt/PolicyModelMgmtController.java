/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 19, 2016
 *
 */
package com.nextlabs.destiny.console.controllers.policymgmt;

import static com.nextlabs.destiny.console.enums.PolicyModelType.get;
import static com.nextlabs.destiny.console.enums.Status.ACTIVE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.bluejungle.pf.domain.epicenter.common.SpecType;
import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.controllers.AbstractRestController;
import com.nextlabs.destiny.console.delegadmin.actions.DelegationModelActions;
import com.nextlabs.destiny.console.dto.common.CollectionDataResponseDTO;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyModelDTO;
import com.nextlabs.destiny.console.enums.ActionType;
import com.nextlabs.destiny.console.enums.AuthorizableType;
import com.nextlabs.destiny.console.enums.DelegationModelShortName;
import com.nextlabs.destiny.console.exceptions.CircularReferenceException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.ForbiddenException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.model.policy.ActionConfig;
import com.nextlabs.destiny.console.model.policy.AttributeConfig;
import com.nextlabs.destiny.console.model.policy.ObligationConfig;
import com.nextlabs.destiny.console.model.policy.ParameterConfig;
import com.nextlabs.destiny.console.model.policy.PolicyModel;
import com.nextlabs.destiny.console.search.repositories.PolicySearchRepository;
import com.nextlabs.destiny.console.services.delegadmin.AccessControlService;
import com.nextlabs.destiny.console.services.policy.PolicyModelService;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Controller for handle and manage all Policy model CRUD operations
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("/policyModel/mgmt")
@Api(tags = {"Policy Model/ Component Type Management Controller"})
@SwaggerDefinition(tags = { @Tag(name = "Policy Model Management Controller", description = "REST APIs related to manage policy models/ component types.") })
public class PolicyModelMgmtController extends AbstractRestController {

    private static final Logger log = LoggerFactory
            .getLogger(PolicyModelMgmtController.class);

    @Autowired
    private PolicyModelService policyModelService;

    @Autowired
    private AccessControlService accessControlService;

    @Resource
    private PolicySearchRepository policySearchRepository;

    /**
     * Save new Policy model
     * 
     * @return {@link SimpleResponseDTO}
     * @throws ConsoleException
     */
    @SuppressWarnings("rawtypes")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/add")
    @ApiOperation(value = "Creates and adds a new policy model.",
		notes="Returns a success message along with the new policy model's ID, after the policy model is successfully saved.")
    public ConsoleResponseEntity<ResponseDTO> addPolicyModel(
            @RequestBody PolicyModelDTO pmDTO) throws ConsoleException {

        log.debug("Request came to add new policy model");

        String namePattern = msgBundle.getText("name.pattern");
        String nameExpectedFormatMsg = msgBundle.getText("name.pattern.format.message");
        String shortCodePattern = msgBundle.getText("short.code.pattern");
        String expectedFormat = msgBundle.getText("short.code.format.message");
        String attrOblShortNamePattern = msgBundle.getText("attr.obl.short.name.pattern");
        String attrOblExpectedFormatMsg = msgBundle.getText("attr.obl.short.name.format.message");

		validations.assertNotNull(pmDTO, "policyModel");
        validations.assertNull(pmDTO.getId(), "Id");
		validations.assertNotBlank(pmDTO.getName(), "name");
		validations.assertNotBlank(pmDTO.getShortName(), "shortName");
		validations.assertMatches(pmDTO.getShortName(), 
				Pattern.compile(shortCodePattern), "shortName", expectedFormat);
        validations.assertMatches(pmDTO.getName(), Pattern.compile(namePattern),
                "Name", nameExpectedFormatMsg);
        validations.assertNotKeyword("shortName", pmDTO.getShortName());
		validations.assertNotBlank(pmDTO.getType(), "type");

		for (AttributeConfig attrConfig : pmDTO.getAttributes()) {
			validations.assertMatches(attrConfig.getShortName(), Pattern.compile(attrOblShortNamePattern),
					"Attribute short name", attrOblExpectedFormatMsg);
			validations.assertNotKeyword("Attribute short name", attrConfig.getShortName());
			attrConfig.setShortName(attrConfig.getShortName().toLowerCase());
		}
		
		for (ActionConfig actionConfig : pmDTO.getActions()) {
			validations.assertMatches(actionConfig.getShortName(), Pattern.compile(shortCodePattern),
					"Action short name", expectedFormat);
			validations.assertNotKeyword("Action short name", actionConfig.getShortName());
			actionConfig.setShortName(actionConfig.getShortName().toUpperCase());
		}
		
		for (ObligationConfig oblConfig : pmDTO.getObligations()) {
			validations.assertMatches(oblConfig.getShortName(), Pattern.compile(attrOblShortNamePattern),
					"Obligation short name", attrOblExpectedFormatMsg);

			for (ParameterConfig paramConfig : oblConfig.getParameters()) {
				validations.assertMatches(paramConfig.getShortName(), Pattern.compile(attrOblShortNamePattern),
						oblConfig.getName() + " Parameter short name", attrOblExpectedFormatMsg);
			}
		}

        PolicyModel policyModel = new PolicyModel(null, pmDTO.getName(),
                pmDTO.getShortName().toLowerCase(), pmDTO.getDescription(),
                get(pmDTO.getType()), ACTIVE);
        policyModel.setAttributes(pmDTO.getAttributes());
        policyModel.setActions(pmDTO.getActions());
        policyModel.setObligations(pmDTO.getObligations());

        List<Long> tagIds = new ArrayList<>();
        for (TagDTO tagDTO : pmDTO.getTags()) {
            tagIds.add(tagDTO.getId());
        }

        ResponseDTO response;
        try {
			policyModelService.save(policyModel, tagIds, false);
			response = SimpleResponseDTO.create(
	                msgBundle.getText("success.data.saved.code"),
	                msgBundle.getText("success.data.saved"), policyModel.getId());
			log.info("New Policy model saved successfully and response sent");
		} catch (CircularReferenceException e) {
			response = SimpleResponseDTO.create(msgBundle.getText(e.getStatusCode()), msgBundle.getText(e.getStatusMsg()), policyModel.getId());
		}

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Modify Policy model
     * 
     * @return {@link SimpleResponseDTO}
     * @throws ConsoleException
     */
    @SuppressWarnings("rawtypes")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/modify")
    @ApiOperation(value="Modifies existing policy model.",
                    notes="<strong>Since v8.5.0</strong>\nReturns a success message along with the policy model's ID, after the changes to the policy model is successfully saved.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data modified successfully")})
    public ConsoleResponseEntity<ResponseDTO> modifyPolicyModel(
            @ApiParam(value = "The updated policy model to be saved", required = true) @RequestBody PolicyModelDTO policyModelDTO)
                    throws ConsoleException {

        log.debug("Request came to modify policy model");

        String namePattern = msgBundle.getText("name.pattern");
        String nameExpectedFormatMsg = msgBundle.getText("name.pattern.format.message");
        String shortCodePattern = msgBundle.getText("short.code.pattern");
        String expectedFormat = msgBundle.getText("short.code.format.message");
        String attrOblShortNamePattern = msgBundle.getText("attr.obl.short.name.pattern");
        String attrOblExpectedFormatMsg = msgBundle.getText("attr.obl.short.name.format.message");

        validations.assertNotNull(policyModelDTO, "policyModel");
        validations.assertNotNull(policyModelDTO.getId(), "Id");
        validations.assertNotBlank(policyModelDTO.getName(), "name");
        validations.assertMatches(policyModelDTO.getName(), Pattern.compile(namePattern),
                "Name", nameExpectedFormatMsg);
        validations.assertNotBlank(policyModelDTO.getShortName(), "shortName");
        validations.assertMatches(policyModelDTO.getShortName(),
                Pattern.compile(shortCodePattern), "shortName", expectedFormat);
        validations.assertNotBlank(policyModelDTO.getType(), "type");

        for (AttributeConfig attrConfig : policyModelDTO.getAttributes()) {
			validations.assertMatches(attrConfig.getShortName(), Pattern.compile(attrOblShortNamePattern),
					"Attribute short name", attrOblExpectedFormatMsg);
            validations.assertNotAttributeKeyword("Attribute short name", attrConfig.getShortName());
			attrConfig.setShortName(attrConfig.getShortName().toLowerCase());
		}
		
		for (ActionConfig actionConfig : policyModelDTO.getActions()) {
			validations.assertMatches(actionConfig.getShortName(), Pattern.compile(shortCodePattern),
					"Action short name", expectedFormat);
            validations.assertNotKeyword("Action short name", actionConfig.getShortName());
			actionConfig.setShortName(actionConfig.getShortName().toUpperCase());
		}
		
		for (ObligationConfig oblConfig : policyModelDTO.getObligations()) {
			validations.assertMatches(oblConfig.getShortName(), Pattern.compile(attrOblShortNamePattern),
					"Obligation short name", attrOblExpectedFormatMsg);

			for (ParameterConfig paramConfig : oblConfig.getParameters()) {
				validations.assertMatches(paramConfig.getShortName(), Pattern.compile(attrOblShortNamePattern),
						oblConfig.getName() + " Parameter short name", attrOblExpectedFormatMsg);
			}
		}

        PolicyModel policyModel = policyModelService.findById(policyModelDTO.getId());
        policyModel.setName(policyModelDTO.getName());
        policyModel.setShortName(policyModelDTO.getShortName().toLowerCase());
        policyModel.setDescription(policyModelDTO.getDescription());
        policyModel.setStatus(ACTIVE);
        policyModel.setAttributes(policyModelDTO.getAttributes());
        policyModel.setActions(policyModelDTO.getActions());
        policyModel.setObligations(policyModelDTO.getObligations());
        policyModel.setVersion(policyModelDTO.getVersion());

        List<Long> tagIds = new ArrayList<>();
        for (TagDTO tagDTO : policyModelDTO.getTags()) {
            tagIds.add(tagDTO.getId());
        }

        ResponseDTO response;

        try {
			policyModelService.save(policyModel, tagIds, false);
			response = SimpleResponseDTO.create(
	                msgBundle.getText("success.data.modified.code"),
	                msgBundle.getText("success.data.modified"),
	                policyModel.getId());
			log.info("Policy model modified successfully and response sent");
		} catch (CircularReferenceException e) {
			response = SimpleResponseDTO.create(msgBundle.getText(e.getStatusCode()), msgBundle.getText(e.getStatusMsg()), policyModel.getId());
			log.error("Policy model could not be modified");
		}

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * View Policy model
     * 
     * @return {@link SimpleResponseDTO}
     * @throws ConsoleException
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}")
    @ApiOperation(value="Fetches details of a policy model.", 
		notes="Given the policy model's ID, this API returns the details of the policy model.")
    @ApiResponses({@ApiResponse(code = 200, message = "Data found successfully", response = PolicyModelDTO.class)})
    public ConsoleResponseEntity<SimpleResponseDTO<PolicyModelDTO>> policyModelView(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to find policy model by Id");

        validations.assertNotNull(id, "Id");
        PolicyModel policyModel = policyModelService.findById(id);

        if (policyModel == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        PolicyModelDTO policyModelDTO = PolicyModelDTO.getDTO(policyModel);
        policyModelDTO = accessControlService
                .enforceTBAConPolicyModel(policyModelDTO);

        if (SpecType.USER.getName().equals(policyModel.getShortName())
                || SpecType.APPLICATION.getName().equals(policyModel.getShortName())
                || SpecType.HOST.getName().equals(policyModel.getShortName())) {
            policyModelDTO.getAuthorities().add(DelegationModelActions.getAuthority(DelegationModelActions.VIEW_POLICY_MODEL));
        }

        if (policyModelDTO.getAuthorities().isEmpty()
                || !policyModelDTO.getAuthorities()
                .contains(DelegationModelActions.getAuthority(DelegationModelActions.VIEW_POLICY_MODEL))) {
            throw new ForbiddenException(ForbiddenException.UNAUTHORIZED_REQUEST,
                    DelegationModelShortName.POLICY_MODEL_ACCESS_TAGS.toString(), ActionType.VIEW, AuthorizableType.POLICY_MODEL);
        }

        SimpleResponseDTO<PolicyModelDTO> response = SimpleResponseDTO.createWithType(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), policyModelDTO);

        log.info("Requested policy model details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * View Active Policy model
     * 
     * @return {@link SimpleResponseDTO}
     * @throws ConsoleException
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/active/{id}")
    @ApiOperation(value = "Returns the details of an active policy model, given it's ID.",
		notes="Given the policy model's ID, this API returns the details of the policy model if it is not marked as deleted.")
    @ApiResponses({@ApiResponse(code = 200, message = "Data found successfully", response = PolicyModelDTO.class)})
    public ConsoleResponseEntity<SimpleResponseDTO<PolicyModelDTO>> activePolicyModelView(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to find active policy model by Id");

        validations.assertNotNull(id, "Id");
        PolicyModel policyModel = policyModelService
                .findActivePolicyModelById(id);

        if (policyModel == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        PolicyModelDTO policyModelDTO = PolicyModelDTO.getDTO(policyModel);
        policyModelDTO = accessControlService
                .enforceTBAConPolicyModel(policyModelDTO);

        if (SpecType.USER.getName().equals(policyModel.getShortName())
                || SpecType.APPLICATION.getName().equals(policyModel.getShortName())
                || SpecType.HOST.getName().equals(policyModel.getShortName())) {
            policyModelDTO.getAuthorities().add(DelegationModelActions.getAuthority(DelegationModelActions.VIEW_POLICY_MODEL));
        }

        if (policyModelDTO.getAuthorities().isEmpty()
                || !policyModelDTO.getAuthorities()
                .contains(DelegationModelActions.getAuthority(DelegationModelActions.VIEW_POLICY_MODEL))) {
            throw new ForbiddenException(ForbiddenException.UNAUTHORIZED_REQUEST,
                    DelegationModelShortName.POLICY_MODEL_ACCESS_TAGS.toString(), ActionType.VIEW, AuthorizableType.POLICY_MODEL);
        }

        SimpleResponseDTO<PolicyModelDTO> response = SimpleResponseDTO.createWithType(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), policyModelDTO);

        log.info("Requested policy model details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * remove Policy model
     * 
     * @return {@link SimpleResponseDTO}
     * @throws ConsoleException
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/remove/{id}")
    @ApiOperation(value = "Removes a policy model.",
		notes="Given the policy model's ID, this API removes the policy model and returns a success message.")
    @ApiResponses({@ApiResponse(code = 200, message = "Data deleted successfully", response = ResponseDTO.class)})
    public ConsoleResponseEntity<ResponseDTO> deletePolicyModel(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to delete policy model by Id");

        validations.assertNotNull(id, "Id");
        boolean isRemoved = policyModelService.remove(id);

        ResponseDTO response;

        if (isRemoved) {
            response = ResponseDTO.create(
                    msgBundle.getText("success.data.deleted.code"),
                    msgBundle.getText("success.data.deleted"));
            log.info("Policy model removed successfully and response sent");
        } else {
            response = ResponseDTO.create(
                    msgBundle.getText("server.error.delete.not.allowed.code"),
                    msgBundle.getText("server.error.delete.not.allowed",
                            "Policy Model"));
            log.info(
                    "Policy model not removed as it is referenced by a component");
        }
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * bulk delete of Policy models
     * 
     * @return {@link SimpleResponseDTO}
     * @throws ConsoleException
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/bulkDelete")
    @ApiOperation(value = "Removes a list of policy models.",
		notes="Given a list of policy model IDs, this API removes the policy models and returns a success message.")
    @ApiResponses({@ApiResponse(code = 200, message = "Data deleted successfully", response = ResponseDTO.class)})
    public ConsoleResponseEntity<ResponseDTO> bulkDeletePolicyModel(
            @RequestBody List<Long> ids) throws ConsoleException {

        log.debug("Request came to bulk delete of policy models");

        validations.assertNotNull(ids, "Ids");
        List<String> modelsNotRemoved = policyModelService.remove(ids);
        ResponseDTO response;

        if (modelsNotRemoved.isEmpty()) {
            response = ResponseDTO.create(
                    msgBundle.getText("success.data.deleted.code"),
                    msgBundle.getText("success.data.deleted"));
            log.info("Policy models removed successfully and response sent");
        } else {
            StringBuilder modelNames = new StringBuilder("Policy Model ( ");
            for (String pmName : modelsNotRemoved) {
                modelNames.append(pmName + ",");
            }
            modelNames.deleteCharAt(modelNames.length() - 1);
            modelNames.append(" )");
            response = ResponseDTO.create(
                    msgBundle.getText("server.error.delete.not.allowed.code"),
                    msgBundle.getText("server.error.bulk.delete.not.allowed",
                            modelNames.toString()));
            log.info(
                    "Policy model not removed as it is referenced by a component");
        }

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Duplicate(Clone) of Policy models
     * 
     * @return {@link SimpleResponseDTO}
     * @throws ConsoleException
     */
    @SuppressWarnings("rawtypes")
    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/clone")
    public ConsoleResponseEntity<SimpleResponseDTO> clonePolicyModel(
            @RequestBody Long id) throws ConsoleException {

        log.debug("Request came to clone policy model by Id: {}", id);

        validations.assertNotNull(id, "Id");
		PolicyModel policyModel = null;
		SimpleResponseDTO response;
		try {
			policyModel = policyModelService.clone(id, true,
					true);

			response = SimpleResponseDTO.create(
	                msgBundle.getText("success.data.saved.code"),
	                msgBundle.getText("success.data.saved"), policyModel.getId());
			log.info("Policy models cloned successfully and response sent");

		} catch (CircularReferenceException e) {
			response = SimpleResponseDTO.create(msgBundle.getText(e.getStatusCode()), msgBundle.getText(e.getStatusMsg()), id);
			log.error("Policy models could not be cloned");
		}

        if (policyModel == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }


        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/extraSubjectAttribs/{type}")
    @ApiOperation(value="Given a policy model type, loads the enrolled subject attributes or properties.",
    	notes = "Returns a collection of subject attributes or properties of the policy model based on it's type.\n" +
                "Possible values for type: application, user, host")
    public ConsoleResponseEntity<CollectionDataResponseDTO> loadExtraSubjectAttributes(
            @PathVariable("type") String type) throws ConsoleException {

        log.debug("Request came to load extra subject attributes");

        validations.assertNotNull(type, "Type");
        Set<AttributeConfig> extraAttributes = policyModelService
                .loadExtraSubjectAttributes(type);

        CollectionDataResponseDTO<AttributeConfig> response = CollectionDataResponseDTO.createWithType(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"));
        response.setData(extraAttributes);
        response.setPageSize(extraAttributes.size());
        response.setPageNo(0);
        response.setTotalPages(1);
        response.setTotalNoOfRecords(extraAttributes.size());

        log.info("Requested extra subject attributes found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @Override
    protected Logger getLog() {
        return log;
    }
}
