/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 12, 2015
 *
 */
package com.nextlabs.destiny.console.controllers.policymgmt;

import static com.nextlabs.destiny.console.enums.DevEntityType.POLICY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.nextlabs.destiny.console.enums.ImportMechanism;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.ImmutableList;
import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.controllers.AbstractRestController;
import com.nextlabs.destiny.console.delegadmin.actions.DelegationModelActions;
import com.nextlabs.destiny.console.dto.common.CollectionDataResponseDTO;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.common.SinglevalueFieldDTO;
import com.nextlabs.destiny.console.dto.policymgmt.DeploymentDependency;
import com.nextlabs.destiny.console.dto.policymgmt.DeploymentRequestDTO;
import com.nextlabs.destiny.console.dto.policymgmt.DeploymentResponseDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ExportEntityDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDeploymentHistoryDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.dto.policymgmt.ValidationDetailDTO;
import com.nextlabs.destiny.console.dto.policymgmt.porting.PolicyExportOptionsDTO;
import com.nextlabs.destiny.console.dto.policymgmt.porting.PolicyPortingDTO;
import com.nextlabs.destiny.console.enums.ActionType;
import com.nextlabs.destiny.console.enums.AuthorizableType;
import com.nextlabs.destiny.console.enums.DelegationModelShortName;
import com.nextlabs.destiny.console.enums.PolicyEffect;
import com.nextlabs.destiny.console.enums.PolicyStatus;
import com.nextlabs.destiny.console.exceptions.CircularReferenceException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.ForbiddenException;
import com.nextlabs.destiny.console.exceptions.InvalidPolicyPortingRequestException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.exceptions.ServerException;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentEntity;
import com.nextlabs.destiny.console.policy.pql.helpers.ConditionPredicateHelper;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.services.delegadmin.AccessControlService;
import com.nextlabs.destiny.console.services.policy.PolicyMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyPortingService;
import com.nextlabs.destiny.console.services.policy.PolicySearchService;
import com.nextlabs.destiny.console.services.policy.ValidatorService;
import com.nextlabs.destiny.console.utils.PolicyPortingUtil;
import com.nextlabs.destiny.console.utils.PolicyPortingUtil.DataTransportationMode;
import springfox.documentation.annotations.ApiIgnore;

/**
 * REST Controller for Policy List and management function
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("/policy/mgmt")
@Api(tags = {"Policy Management Controller"})
@SwaggerDefinition(tags = { @Tag(name = "Policy Management Controller", description = "REST APIs related to managing policies") })
public class PolicyMgmtController extends AbstractRestController {

    private static final Logger log = LoggerFactory
            .getLogger(PolicyMgmtController.class);

    @Autowired
    private PolicyMgmtService policyMgmtService;

    @Autowired
    private PolicySearchService policySearchService;

    @Autowired
    private ValidatorService validatorService;

    @Autowired
    private PolicyPortingService policyPortingService;

    @Autowired
    private AccessControlService accessControlService;

    @Resource
    private ApplicationUserSearchRepository appUserSearchRepository;

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/add")
    @ApiOperation(value = "Creates and adds a new policy.",
		notes="Returns a success message along with the new policy's ID, after the policy is successfully saved.")
    @ApiResponses({@ApiResponse(code = 200, message = "Data saved successfully", response = SimpleResponseDTO.class)})
    public ConsoleResponseEntity<ResponseDTO> createPolicy(
           @ApiParam(value = "The new policy to be saved.", required = true) @RequestBody PolicyDTO policyDTO) throws ConsoleException {

        log.debug("Request came to add new Policy");
        String namePattern = msgBundle.getText("name.pattern");
        String nameExpectedFormatMsg = msgBundle.getText("name.pattern.format.message");

        validations.assertNotBlank(policyDTO.getName(), "Name");
        validations.assertNotBlank(policyDTO.getEffectType(), "Effect");
        validations.assertNotBlank(policyDTO.getStatus(), "Status");

        validations.assertMatches(policyDTO.getName(), Pattern.compile(namePattern),
                "Name", nameExpectedFormatMsg);

        policyDTO.setCategory(POLICY);
        policyMgmtService.save(policyDTO);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), policyDTO.getId());

        log.info("New policy saved successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/importXacmlPolicy")
    @ApiOperation(value = "Creates and adds a new Xacml policy.",
            notes = "Returns a success message along with the new policy's ID, when the policy has been successfully saved.")
    public ConsoleResponseEntity<ResponseDTO> importXacmlPolicy(
            @ApiParam(value = "The Xacml policy in XML format.", required = true) @RequestParam("xacmlFile") MultipartFile xacmlFile)
            throws ConsoleException {

        log.debug("Request came to add new Xacml Policy");
        PolicyDevelopmentEntity policyDevelopmentEntity = policyMgmtService.importXacmlPolicyAndDeploy(xacmlFile);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), policyDevelopmentEntity.getId());

        log.info("New Xacml policy saved successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/removeXacmlPolicy/{id}")
    @ApiOperation(value = "Removes a XACML policy.",
            notes="Given the XACML policy's ID, this API removes the policy and returns a success message.")
    public ConsoleResponseEntity<ResponseDTO> removeXacmlPolicyById(
            @ApiParam(value = "The id of the policy to be removed.", required = true)
            @PathVariable("id")
                    Long id) throws ConsoleException {

        log.debug("Request came to remove Xacml policy by Id");

        validations.assertNotNull(id, "Id");
        policyMgmtService.removeXacmlPolicy(ImmutableList.of(id));

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.deleted.code"),
                msgBundle.getText("success.data.deleted"));

        log.info("Xacml policy removed and successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/bulkDeleteXacmlPolicy")
    @ApiOperation(value = "Removes a list of policies.",
            notes="Given a list of policy ID, this API removes the policies and returns a success message.")
    public ConsoleResponseEntity<ResponseDTO> removeXacmlPolicyById(
            @RequestBody List<Long> ids) throws ConsoleException {

        log.debug("Request came to bulk delete Xacml policies");

        validations.assertNotNull(ids, "Ids");
        policyMgmtService.removeXacmlPolicy(ids);

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.deleted.code"),
                msgBundle.getText("success.data.deleted"));

        log.info("Xacml policies removed successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/modify")
    @ApiOperation(value="Modifies existing policy.", 
		notes="Returns a success message along with the policy's ID, after the changes to the policy are successfully saved.")
    @ApiResponses({@ApiResponse(code = 200, message = "Data saved successfully", response = SimpleResponseDTO.class)})
    public ConsoleResponseEntity<ResponseDTO> modifyPolicy(
                @ApiParam(value = "The modified policy.", required = true)
                @RequestBody PolicyDTO policyDTO) throws ConsoleException {

        log.debug("Request came to modify Policy");
        String namePattern = msgBundle.getText("name.pattern");
        String nameExpectedFormatMsg = msgBundle.getText("name.pattern.format.message");

        validations.assertNotBlank(policyDTO.getName(), "Name");
        validations.assertNotBlank(policyDTO.getEffectType(), "Effect");
        validations.assertNotBlank(policyDTO.getStatus(), "Status");

        validations.assertMatches(policyDTO.getName(), Pattern.compile(namePattern),
                "Name", nameExpectedFormatMsg);

        policyDTO.setCategory(POLICY);
        policyMgmtService.modify(policyDTO);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), policyDTO.getId());

        log.info("Policy modified successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/addSubPolicy")
    @ApiOperation(value = "Creates and adds a new sub policy.",
            notes="Returns a success message along with the new policy's ID, after the policy is successfully saved.")
    @ApiResponses({@ApiResponse(code = 200, message = "Data saved successfully", response = SimpleResponseDTO.class)})
    public ConsoleResponseEntity<ResponseDTO> addSubPolicy(
            @RequestBody PolicyDTO policyDTO) throws ConsoleException {

        log.debug("Request came to add sub policy");
        String namePattern = msgBundle.getText("name.pattern");
        String nameExpectedFormatMsg = msgBundle.getText("name.pattern.format.message");

        validations.assertNotBlank(policyDTO.getName(), "Name");
        validations.assertNotNull(policyDTO.getParentId(), "Parent Policy");
        validations.assertNotBlank(policyDTO.getEffectType(), "Effect");
        validations.assertNotBlank(policyDTO.getStatus(), "Status");

        validations.assertMatches(policyDTO.getName(), Pattern.compile(namePattern),
                "Name", nameExpectedFormatMsg);

        policyDTO.setCategory(POLICY);
        policyMgmtService.addSubPolicy(policyDTO);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), policyDTO.getId());

        log.info("Sub Policy saved successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/addAndDeploySubPolicy")
    public ConsoleResponseEntity<ResponseDTO> addAndDeploySubPolicy(
            @RequestBody PolicyDTO policyDTO) throws ConsoleException {

        log.debug("Request came to add and deploy sub policy");
        String namePattern = msgBundle.getText("name.pattern");
        String nameExpectedFormatMsg = msgBundle.getText("name.pattern.format.message");

        ResponseDTO response;
        ValidationDetailDTO validationDTO = null;

        validations.assertNull(policyDTO.getId(), "Id");
        validations.assertNotBlank(policyDTO.getName(), "Name");
        validations.assertNotNull(policyDTO.getParentId(), "Parent Policy");
        validations.assertNotBlank(policyDTO.getEffectType(), "Effect");
        validations.assertNotBlank(policyDTO.getStatus(), "Status");

        validations.assertMatches(policyDTO.getName(), Pattern.compile(namePattern),
                "Name", nameExpectedFormatMsg);

        policyDTO.setCategory(POLICY);

        if (!policyDTO.isSkipValidate()) {
            validationDTO = validatorService.validate(policyDTO);
        }

        if ((validationDTO != null && validationDTO.isDeployable())
                || policyDTO.isSkipValidate()) {
            policyDTO.setCategory(POLICY);
            Long policyId = policyMgmtService.saveAndDeploySubPolicy(policyDTO);
            response = SimpleResponseDTO.create(
                    msgBundle.getText("success.data.saved.code"),
                    msgBundle.getText("success.data.saved"), policyId);
        } else {
            response = SimpleResponseDTO.create(
                    msgBundle.getText("success.data.validated.code"),
                    msgBundle.getText("success.data.validated"), validationDTO);
        }

        log.info("Sub Policy added, deployment triggered and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/modifyAndDeploySubPolicy")
    public ConsoleResponseEntity<ResponseDTO> modifyAndDeploySubPolicy(
            @RequestBody PolicyDTO policyDTO) throws ConsoleException {

        log.debug("Request came to modify and deploy sub policy");
        String namePattern = msgBundle.getText("name.pattern");
        String nameExpectedFormatMsg = msgBundle.getText("name.pattern.format.message");

        ResponseDTO response;
        ValidationDetailDTO validationDTO = null;

        validations.assertNotNull(policyDTO.getId(), "Id");
        validations.assertNotBlank(policyDTO.getName(), "Name");
        validations.assertNotNull(policyDTO.getParentId(), "Parent Policy");
        validations.assertNotBlank(policyDTO.getEffectType(), "Effect");
        validations.assertNotBlank(policyDTO.getStatus(), "Status");

        validations.assertMatches(policyDTO.getName(), Pattern.compile(namePattern),
                "Name", nameExpectedFormatMsg);

        policyDTO.setCategory(POLICY);

        if (!policyDTO.isSkipValidate()) {
            validationDTO = validatorService.validate(policyDTO);
        }

        if ((validationDTO != null && validationDTO.isDeployable())
                || policyDTO.isSkipValidate()) {
            policyDTO.setCategory(POLICY);
            Long policyId = policyMgmtService.saveAndDeploySubPolicy(policyDTO);
            response = SimpleResponseDTO.create(
                    msgBundle.getText("success.data.saved.code"),
                    msgBundle.getText("success.data.saved"), policyId);
        } else {
            response = SimpleResponseDTO.create(
                    msgBundle.getText("success.data.validated.code"),
                    msgBundle.getText("success.data.validated"), validationDTO);
        }

        log.info("Sub Policy modified, deployment triggered and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/expressionValidate")
    @ApiOperation(value="Validates the given expression.",
	notes="This API evaluates a given expression and returns either true or false.")
    public ConsoleResponseEntity<ResponseDTO> validateExpression(
            @RequestBody String expression) {

		log.debug("Request came to validate expression");
		validations.assertNotBlank(expression, "Expression");

        boolean valid = ConditionPredicateHelper.validateExpression(expression);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.validated.code"),
                msgBundle.getText("success.data.validated"), valid);

        log.info(
                "Validation expression validated successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}")
    @ApiOperation(value="Fetches details of a policy.", 
		notes="Given the policy's ID, this API returns the details of the policy.")
    @ApiResponses({@ApiResponse(code = 200, message = "Data found successfully", response = PolicyDTO.class)})
    public ConsoleResponseEntity<SimpleResponseDTO<PolicyDTO>> policyById(
                    @ApiParam(value = "The ID of the policy to be retrieved.", required = true)
                    @PathVariable("id")
                    Long id) throws ConsoleException {

        log.debug("Request came to find Policy by Id");

        validations.assertNotNull(id, "Id");
        PolicyDTO policyDTO = policyMgmtService.findById(id);

        if (policyDTO == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        } else {
            PolicyLite lite = PolicyLite.getLite(policyDTO,
                    appUserSearchRepository);
            lite = policyMgmtService.enforceTBAC(lite);
            policyDTO.setAuthorities(lite.getAuthorities());
        }

        if (policyDTO.getAuthorities().isEmpty()
                || !policyDTO.getAuthorities()
                .contains(DelegationModelActions.getAuthority(DelegationModelActions.VIEW_POLICY))) {
            throw new ForbiddenException(ForbiddenException.UNAUTHORIZED_REQUEST,
                    DelegationModelShortName.POLICY_ACCESS_TAGS.toString(), ActionType.VIEW, AuthorizableType.POLICY);
        }

        SimpleResponseDTO<PolicyDTO> response = SimpleResponseDTO.createWithType(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), policyDTO);

        log.info("Requested Policy details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/active/{id}")
    @ApiOperation(value = "Returns the details of an active policy, given it's ID.",
		notes="Given the policy's ID, this API returns the details of the policy if it is not marked as deleted.")
    @ApiResponses({@ApiResponse(code = 200, message = "Data found successfully", response = PolicyDTO.class)})
    public ConsoleResponseEntity<SimpleResponseDTO<PolicyDTO>> activePolicyById(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to find Policy by Id");

        validations.assertNotNull(id, "Id");
        PolicyDTO policyDTO = policyMgmtService.findActiveById(id);

        if (policyDTO == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        } else {
            PolicyLite lite = PolicyLite.getLite(policyDTO,
                    appUserSearchRepository);
            lite = policyMgmtService.enforceTBAC(lite);
            policyDTO.setAuthorities(lite.getAuthorities());
        }

        if (policyDTO.getAuthorities().isEmpty()
                || !policyDTO.getAuthorities()
                .contains(DelegationModelActions.getAuthority(DelegationModelActions.VIEW_POLICY))) {
            throw new ForbiddenException(ForbiddenException.UNAUTHORIZED_REQUEST,
                    DelegationModelShortName.POLICY_ACCESS_TAGS.toString(), ActionType.VIEW, AuthorizableType.POLICY);
        }

        SimpleResponseDTO<PolicyDTO> response = SimpleResponseDTO.createWithType(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), policyDTO);

        log.info("Requested Policy details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/remove/{id}")
    @ApiOperation(value = "Removes a policy.",
		notes="Given the policy's ID, this API removes the policy and returns a success message.")
    @ApiResponses({@ApiResponse(code = 200, message = "Data deleted successfully", response = ResponseDTO.class)})
    public ConsoleResponseEntity<ResponseDTO> removeById(
                    @ApiParam(value = "The ID of the policy to be removed.", required = true)
                    @PathVariable("id")
                    Long id) throws ConsoleException {

        log.debug("Request came to remove policy by Id");

        validations.assertNotNull(id, "Id");
        policyMgmtService.remove(ImmutableList.of(id), true);

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.deleted.code"),
                msgBundle.getText("success.data.deleted"));

        log.info("Policy removed and successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/bulkDelete")
    @ApiOperation(value = "Removes a list of policies.",
		notes="Given a list of policy ID, this API removes the policies and returns a success message.")
    @ApiResponses({@ApiResponse(code = 200, message = "Data deleted successfully", response = ResponseDTO.class)})
    public ConsoleResponseEntity<ResponseDTO> removeById(
            @RequestBody List<Long> ids) throws ConsoleException {

        log.debug("Request came to bulk delete policies");

        validations.assertNotNull(ids, "Ids");
        policyMgmtService.remove(ids, true);

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.deleted.code"),
                msgBundle.getText("success.data.deleted"));

        log.info("Policies removed successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Duplicate(Clone) of Policy
     * 
     * @return {@link SimpleResponseDTO}
     * @throws ConsoleException
     */
    @SuppressWarnings("rawtypes")
    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/clone")
    public ConsoleResponseEntity<SimpleResponseDTO> clonePolicy(
            @RequestBody Long id) throws ConsoleException {

        log.debug("Request came to clone component by Id: {}", id);

        validations.assertNotNull(id, "Id");
        PolicyDTO policy = policyMgmtService.clone(id);

        if (policy == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        SimpleResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), policy.getId());

        log.info("Policy cloned successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/hierarchy/{id}")
    public ConsoleResponseEntity<ResponseDTO> policyHierarchy(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to find Policy hierarchy");

        validations.assertNotNull(id, "Id");
        PolicyLite lite = policySearchService.findPolicyTree(id);

        if (lite == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        } else {
            lite = policyMgmtService.enforceTBAC(lite);
        }

        if (lite.getAuthorities().isEmpty()
                || !lite.getAuthorities()
                .contains(DelegationModelActions.getAuthority(DelegationModelActions.VIEW_POLICY))) {
            throw new ForbiddenException(ForbiddenException.UNAUTHORIZED_REQUEST,
                    DelegationModelShortName.POLICY_ACCESS_TAGS.toString(), ActionType.VIEW, AuthorizableType.POLICY);
        }

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), lite);

        log.info("Requested Policy details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/deploy")
    @ApiOperation(value = "Deploys a list of policies",
		notes="This API deploys a list of policies passed to it. Returns the deployment details of policies.")
    @ApiResponses({@ApiResponse(code = 200, message = "Data deployed successfully")})
    public ConsoleResponseEntity<CollectionDataResponseDTO<DeploymentResponseDTO>> policyDeploy(
            @RequestBody List<DeploymentRequestDTO> deploymentRequests) throws ConsoleException {

        log.debug("Request came to deploy the policy/policies");

        validations.assertNotNull(deploymentRequests, "Deployment Requests");
        validations.assertNotZero(Long.valueOf(deploymentRequests.size()), "Deployment Requests");

        List<DeploymentResponseDTO> deploymentResponses = policyMgmtService.deploy(deploymentRequests, true);

        CollectionDataResponseDTO<DeploymentResponseDTO> response = CollectionDataResponseDTO.createWithType(
                msgBundle.getText("success.data.deployed.code"),
                msgBundle.getText("success.data.deployed"));
        response.setData(deploymentResponses);

        response.setPageNo(1);
        response.setPageSize(deploymentResponses.size());
        response.setTotalPages(deploymentResponses.size());
        response.setTotalNoOfRecords(deploymentResponses.size());

        log.info("Requested Policy/Policies deployed and report sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Add and deploy policy.
     *
     * @param policyDTO  policy to modify and deploy
     * @return  the response
     * @throws ConsoleException if and error occurred
     * @deprecated Instead of this method, the add and deploy methods should be used separately.
     */
    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/addAndDeploy")
    @ApiOperation(value = "Creates, adds and deploys a new policy.",
	notes="Returns a success message along with the new policy's details, when the policy have been successfully saved and deployed.")
    @Deprecated
    public ConsoleResponseEntity<ResponseDTO> addAndDeploy(
            @RequestBody PolicyDTO policyDTO) throws ConsoleException {

        log.debug("Request came to save and deploy a Policy");
        String namePattern = msgBundle.getText("name.pattern");
        String nameExpectedFormatMsg = msgBundle.getText("name.pattern.format.message");

        ResponseDTO response = null;
        ValidationDetailDTO validationDTO = null;

        validations.assertNotBlank(policyDTO.getName(), "Name");
        validations.assertNotBlank(policyDTO.getEffectType(), "Effect");
        validations.assertNotBlank(policyDTO.getStatus(), "Status");
        validations.assertNull(policyDTO.getId(), "Id");

        validations.assertMatches(policyDTO.getName(), Pattern.compile(namePattern),
                "Name", nameExpectedFormatMsg);

        if (!policyDTO.isSkipValidate()) {
            validationDTO = validatorService.validate(policyDTO);
        }

        if ((validationDTO != null && validationDTO.isDeployable())
                || policyDTO.isSkipValidate()) {
            policyDTO.setCategory(POLICY);
            DeploymentResponseDTO responseDTO = policyMgmtService.saveAndDeploy(policyDTO);
            response = SimpleResponseDTO.create(
                    msgBundle.getText("success.data.saved.code"),
                    msgBundle.getText("success.data.saved"), responseDTO);
        } else {
            response = SimpleResponseDTO.create(
                    msgBundle.getText("success.data.validated.code"),
                    msgBundle.getText("success.data.validated"), validationDTO);
        }

        log.info(
                "Policy saved and deployed triggered successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Modify and deploy policy.
     *
     * @param policyDTO  policy to modify and deploy
     * @return  the response
     * @throws ConsoleException if and error occurred
     * @deprecated Instead of this method, the modify and deploy methods should be used separately.
     */
    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/modifyAndDeploy")
    @ApiOperation(value = "Modifies and deploys a policy.",
	notes="Given a policy's details, this API will save the changes made to that policy and also deploys it.")
    @Deprecated
    public ConsoleResponseEntity<ResponseDTO> modifyAndDeploy(
            @RequestBody PolicyDTO policyDTO) throws ConsoleException {

        log.debug("Request came to save and deploy a Policy");
        String namePattern = msgBundle.getText("name.pattern");
        String nameExpectedFormatMsg = msgBundle.getText("name.pattern.format.message");

        ResponseDTO response = null;
        ValidationDetailDTO validationDTO = null;

        validations.assertNotBlank(policyDTO.getName(), "Name");
        validations.assertNotBlank(policyDTO.getEffectType(), "Effect");
        validations.assertNotBlank(policyDTO.getStatus(), "Status");
        validations.assertNotNull(policyDTO.getId(), "Id");

        validations.assertMatches(policyDTO.getName(), Pattern.compile(namePattern),
                "Name", nameExpectedFormatMsg);

        if (!policyDTO.isSkipValidate()) {
            validationDTO = validatorService.validate(policyDTO);
        }

        if ((validationDTO != null && validationDTO.isDeployable())
                || policyDTO.isSkipValidate()) {
            policyDTO.setCategory(POLICY);
            DeploymentResponseDTO responseDTO = policyMgmtService.saveAndDeploy(policyDTO);
            response = SimpleResponseDTO.create(
                    msgBundle.getText("success.data.saved.code"),
                    msgBundle.getText("success.data.saved"), responseDTO);
        } else {
            response = SimpleResponseDTO.create(
                    msgBundle.getText("success.data.validated.code"),
                    msgBundle.getText("success.data.validated"), validationDTO);
        }

        log.info("Policy saved and deployed triggered successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/unDeploy")
    @ApiOperation(value = "Undeploys a list of policies.",
		notes="This API undeploys a list of deployed policies passed to it.")
    public ConsoleResponseEntity<ResponseDTO> policyunDeploy(
            @RequestBody List<Long> ids) throws ConsoleException {

        log.debug("Request came to un-deploy the policy/policies");

        validations.assertNotNull(ids, "Ids");
        validations.assertNotZero(Long.valueOf(ids.size()), "Ids");

        policyMgmtService.unDeploy(ids);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.undeployed.code"),
                msgBundle.getText("success.data.undeployed"));

        log.info("Requested Policy/Policies undeployed and report sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/history/{id}")
    public ConsoleResponseEntity<CollectionDataResponseDTO> policyHistory(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to view the history of the policy");

        validations.assertNotNull(id, "id");
        List<PolicyDeploymentEntity> deploymentDetails = policyMgmtService
                .deploymentHistory(id);

        List<PolicyDeploymentHistoryDTO> historyDTO = new ArrayList<>(
                deploymentDetails.size());

        int revCount = deploymentDetails.size();
        for (PolicyDeploymentEntity deploymentEntity : deploymentDetails) {
            historyDTO.add(PolicyDeploymentHistoryDTO.getDTO(deploymentEntity,
                    String.valueOf(revCount--), appUserSearchRepository));
        }

        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"));
        response.setData(historyDTO);
        response.setPageNo(1);
        response.setPageSize(historyDTO.size());
        response.setTotalPages(historyDTO.size());
        response.setTotalNoOfRecords(historyDTO.size());

        log.info("Requested Policy history details and report sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/viewRevision/{revisionId}/{revisionNo}")
    public ConsoleResponseEntity<ResponseDTO> viewRevision(
            @PathVariable("revisionId") Long revisionId,
            @PathVariable("revisionNo") String revisionNo)
            throws ConsoleException {

        log.debug("Request came to view the revision");

        validations.assertNotNull(revisionId, "Revision Id");
        PolicyDeploymentHistoryDTO revisionDTO = policyMgmtService
                .viewRevision(revisionId);
        revisionDTO.setRevision(revisionNo);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), revisionDTO);

        log.info("Requested Policy revision loaded and report sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/revert")
    public ConsoleResponseEntity<ResponseDTO> revert(
            @RequestBody Long revisionId)
            throws ConsoleException {

        log.debug("Request came to revert current policy,[ Revision :{}]",
                revisionId);

        validations.assertNotNull(revisionId, "Revision Id");
        validations.assertNotZero(revisionId, "Revision Id");

        PolicyDTO dto = policyMgmtService.revertToVersion(revisionId);

        if (dto == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), dto.getId());

        log.info("Requested Policy reverted to given revision and report sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/findDependencies")
    public ConsoleResponseEntity<CollectionDataResponseDTO> findDependencies(
            @RequestBody List<Long> ids) throws ConsoleException {
        log.debug("Request received to find dependencies");

        validations.assertNotNull(ids, "Ids");

        Set<DeploymentDependency> dependencies = policyMgmtService.findDependencies(ids);

        CollectionDataResponseDTO<DeploymentDependency> response = CollectionDataResponseDTO.createWithType(
                msgBundle.getText("success.data.validated.code"),
                msgBundle.getText("success.data.validated"));
        response.setData(dependencies);

        log.debug("Dependencies have been found successfully and response sent");

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/validateAndDeploy")
    public ConsoleResponseEntity<CollectionDataResponseDTO> validateAndDeploy(
            @RequestBody List<DeploymentRequestDTO> deploymentRequests) throws ConsoleException {

        log.debug("Request came to validate and deploy policy");

        validations.assertNotNull(deploymentRequests, "Deployment Requests");
        validations.assertNotZero(Long.valueOf(deploymentRequests.size()), "Deployment Requests");

        List<ValidationDetailDTO> validateDTOList = policyMgmtService.validateAndDeploy(deploymentRequests);

        boolean allDeployed = true;
        for (ValidationDetailDTO validateDTO : validateDTOList) {
            if (validateDTO.getDetails().isEmpty()) {
                allDeployed &= true;
            } else {
                allDeployed &= false;
            }
        }

        CollectionDataResponseDTO response;
        if (allDeployed) {
            response = CollectionDataResponseDTO.create(
                    msgBundle.getText("success.data.deployed.code"),
                    msgBundle.getText("success.data.deployed"));
            response.setData(validateDTOList);
        } else {
            response = CollectionDataResponseDTO.create(
                    msgBundle.getText("success.data.validated.code"),
                    msgBundle.getText("success.data.validated"));
            response.setData(validateDTOList);
        }

        response.setPageNo(1);
        response.setPageSize(validateDTOList.size());
        response.setTotalPages(validateDTOList.size());
        response.setTotalNoOfRecords(validateDTOList.size());

        log.info(
                "Requested policy/policies validated/deployed and report sent");

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Load all available policy status types
     * 
     * @return
     * @throws ConsoleException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/statusTypes/list")
    public ConsoleResponseEntity<CollectionDataResponseDTO> loadStatusTypes()
            throws ConsoleException {

        log.debug("Request came to load policy status types");

        List<SinglevalueFieldDTO> statusFields = new ArrayList<>();
        for (PolicyStatus status : PolicyStatus.values()) {
            SinglevalueFieldDTO field = SinglevalueFieldDTO
                    .create(status.name(), msgBundle.getText(status.getKey()));
            statusFields.add(field);
        }

        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"));

        response.setData(statusFields);
        response.setPageNo(0);
        response.setPageSize(statusFields.size());
        response.setTotalNoOfRecords(statusFields.size());

        log.info("Policy Status types loaded to response");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Load all available policy effect types
     * 
     * @return
     * @throws ConsoleException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/effects/list")
    public ConsoleResponseEntity<CollectionDataResponseDTO> loadEffectTypes()
            throws ConsoleException {

        log.debug("Request came to load policy effect types");

        List<SinglevalueFieldDTO> effectFields = new ArrayList<>();
        for (PolicyEffect effects : PolicyEffect.values()) {
            SinglevalueFieldDTO field = SinglevalueFieldDTO.create(
                    effects.name(), msgBundle.getText(effects.getKey()));
            effectFields.add(field);
        }

        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"));

        response.setData(effectFields);
        response.setPageNo(0);
        response.setPageSize(effectFields.size());
        response.setTotalNoOfRecords(effectFields.size());

        log.info("Policy effetcs loaded to response");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @ResponseBody
    @PostMapping(value = "/import")
    @ApiOperation(value = "Import policies.",
            notes = "Import the policies provided in the attachment file. If the import file is encrypted, file extension should be ebin.\n" +
                    "For plain text file import, file extension should be bin.")
    @ApiResponses({@ApiResponse(code = 200, message = "File imported successfully", response = SimpleResponseDTO.class)})
    public ConsoleResponseEntity<ResponseDTO> importPolicies(
            @ApiParam(value = "List of policy export files to be imported.", required = true) @RequestParam("policyFiles") List<MultipartFile> policyFiles,
            @ApiParam(value = "Import mechanism. Value should be either PARTIAL or FULL. Default value is PARTIAL.") @RequestParam("importMechanism") String importMechanism,
            @ApiParam(value = "Option to clean up non-importing entity. Default value is false.")  @RequestParam("cleanup") boolean cleanup) throws ConsoleException {
        log.info("Upload request received");

        MultipartFile mpf;
        PolicyPortingDTO portDTO = null;

        boolean isSuccess = false;
        boolean isCircularRef = false;
        boolean isInvalidPortingRequest = false;
        ResponseDTO response = null;

        for(int i = 0; i < policyFiles.size(); i++) {
            try {
                mpf = policyFiles.get(i);
                log.info("Import policies - [File name :{}, file size :{}]",
                        mpf.getOriginalFilename(), mpf.getSize());
                byte[] data = mpf.getBytes();
                String importMode;
                if(mpf.getOriginalFilename().trim().endsWith(PolicyPortingUtil.FILE_EXTENSION_EBIN)) {
                	importMode = DataTransportationMode.SANDE.name();
                } else {
                	importMode = DataTransportationMode.PLAIN.name();
                }
                portDTO = policyPortingService.validateAndImport(data, importMode, ImportMechanism.getMechanism(importMechanism));

                if(cleanup) {
                    policyPortingService.cleanup(portDTO);
                }

                isSuccess = true;
                isCircularRef = false;
                isInvalidPortingRequest = false;
            } catch (IOException e) {
                log.error("Error encountered during file import", e);
                isSuccess = false;
                isCircularRef = false;
                isInvalidPortingRequest = false;
                break;
            } catch (CircularReferenceException e) {
            	isCircularRef = true;
            	isInvalidPortingRequest = false;
            	response = ResponseDTO.create(msgBundle.getText(e.getStatusCode()), msgBundle.getText(e.getStatusMsg()));
            	break;
			} catch (InvalidPolicyPortingRequestException e) {
				isSuccess = false;
                isCircularRef = false;
                isInvalidPortingRequest = true;
				response = ResponseDTO.create(e.getStatusCode(), e.getStatusMsg());
				break;
			} catch (NumberFormatException e) {
                log.error("Data formatting error during file import", e.getMessage());
                isSuccess = false;
                isCircularRef = false;
                isInvalidPortingRequest = true;
                response = ResponseDTO.create(msgBundle.getText("invalid.import.data.type.not.match.code"),
                        msgBundle.getText("invalid.import.data.type.not.match", e.getMessage()));
                break;
            }
        }

        if(!isCircularRef && !isInvalidPortingRequest) {
        	JSONObject importResults = new JSONObject();
            if (isSuccess) {
                importResults.put("total_policy_models",
                        portDTO.getPolicyModels().size());
                importResults.put("total_components",
                        portDTO.getComponents().size());
                importResults.put("total_policies", portDTO.getPolicyTree().size());
                importResults.put("non_blocking_error", portDTO.hasNonBlockingError());
            }

            response = SimpleResponseDTO.create(
                    msgBundle.getText("success.file.import.code"),
                    msgBundle.getText("success.file.import"), importResults);
        }

        log.info("Import policy status response");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * <p>
     * Export a given policies
     * </p>
     *
     * @param exportMode
     * @param exportEntityDTOS
     * @param response
     * @return
     * @throws ConsoleException
     *
     */
    @PostMapping(value = "/export")
    @ApiOperation(value = "Export the given list of policies.",
            notes="Returns a success message along with the exported policy file URL, after the policies are successfully exported." +
                    " The selected export mode must be allowed in the Control Centre configuration.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "exportMode", value = "Export Mode", dataType = "string", paramType = "query",
                    allowableValues = "PLAIN, SANDE")
    })
    @ApiResponses({@ApiResponse(code = 200, message = "File exported successfully", response = ResponseDTO.class)})
    public ConsoleResponseEntity<ResponseDTO> policyExport(
            @RequestParam(defaultValue = "PLAIN", required = false) String exportMode,
            @RequestBody List<ExportEntityDTO> exportEntityDTOS,
            HttpServletResponse response)
            throws ConsoleException {
        log.debug("Policy export request came, Policy ids :{}", exportEntityDTOS);

        ResponseDTO responseDTO = null;
        try {
	        String filename = policyPortingService.exportAsFile(exportEntityDTOS, exportMode);
	
	        responseDTO = SimpleResponseDTO.create(
	                msgBundle.getText("success.file.export.code"),
	                msgBundle.getText("success.file.export"), filename);
	        log.info("Policy details exported successfully, [Filename = {}]",
	                filename);
	
	        response.setHeader("Content-Disposition", "attachment");
        } catch (InvalidPolicyPortingRequestException e) {
			responseDTO = ResponseDTO.create(e.getStatusCode(), e.getStatusMsg());
        }

        return ConsoleResponseEntity.get(responseDTO, HttpStatus.OK);
    }

    /**
     * <p>
     * Export given xacml policies
     * </p>
     *
     * @param ids
     * @param response
     * @return
     * @throws ConsoleException
     *
     */
    @ApiIgnore
    @PostMapping(value = "/exportXacmlPolicy")
    @ApiOperation(value = "Export the xacml policies with given IDs.",
            notes="Returns a success message along with list exported policy file URL, when the policies has been successfully exported.")
    public ConsoleResponseEntity<ResponseDTO> xacmlPolicyExport(
            @RequestBody List<Long> ids, HttpServletResponse response)
            throws ConsoleException {
        log.debug("Xacml policy export request came");

        List<String> filenames = policyPortingService.exportXacmlPolicy(ids);
        response.setHeader("Content-Disposition", "attachment");

        ResponseDTO responseDTO = SimpleResponseDTO.create(
                msgBundle.getText("success.file.export.code"),
                msgBundle.getText("success.file.export"), filenames);
        log.info("Xacml policies details exported successfully");

        return ConsoleResponseEntity.get(responseDTO, HttpStatus.OK);
    }

    /**
     * <p>
     * Export a given policies
     * </p>
     *
     * @param response
     * @param exportMode
     * @return
     * @throws ConsoleException
     */
    @GetMapping(value = "/exportAll")
    @ApiOperation(value = "Export export all policies.",
            notes = "Returns a success message along with the exported policy file URL, after the policies are successfully exported." +
                    " The selected export mode must be allowed in the Control Centre configuration.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "exportMode", value = "Export Mode", dataType = "string", paramType = "query",
                    allowableValues = "PLAIN, SANDE")
    })
    @ApiResponses({@ApiResponse(code = 200, message = "File exported successfully", response = ResponseDTO.class)})
    public ConsoleResponseEntity<ResponseDTO> policyExportAll(
            HttpServletResponse response,
            @RequestParam(defaultValue = "PLAIN", required = false) String exportMode)
            throws ConsoleException {

        log.debug("Request came to export all policies");

        ResponseDTO responseDTO = null;
        try {
            String filename = policyPortingService.exportAll(getAllPolicyLite(), exportMode);
            responseDTO = SimpleResponseDTO.create(
                    msgBundle.getText("success.file.export.code"),
                    msgBundle.getText("success.file.export"), filename);

            log.info("All Policy details exported successfully, [Filename = {}]",
                    filename);

            response.setHeader("Content-Disposition", "attachment");
        } catch (InvalidPolicyPortingRequestException e) {
            responseDTO = ResponseDTO.create(e.getStatusCode(), e.getStatusMsg());
        }

        return ConsoleResponseEntity.get(responseDTO, HttpStatus.OK);
    }

    /**
     * <p>
     * Export given policies as pdf
     * 
     * @throws ConsoleException
     * @throws ServerException
     *
     */
    @ApiIgnore
    @PostMapping(value = "/generatePDF")
    public ConsoleResponseEntity<SimpleResponseDTO<String>> policyPDFExport(@RequestBody
            List<ExportEntityDTO> exportEntityDTOS, HttpServletResponse response)
            throws ConsoleException, ServerException {
        log.debug("Policy PDF generation request came, Policy ids :{}", exportEntityDTOS);

        String responseMessage;
        String filename = "";
        
        try {
            responseMessage = "success.file.export";
            filename = policyPortingService.generatePDF(exportEntityDTOS);
            response.setHeader("Content-Disposition", "attachment");
            log.info("Policy details exported successfully, [Filename = {}]", filename);
        } catch (InvalidPolicyPortingRequestException e) {
            responseMessage = e.getStatusMsg();
        }
        
        return ConsoleResponseEntity
                .get(SimpleResponseDTO.createWithType(msgBundle.getCode(responseMessage),
                        msgBundle.getText(responseMessage), filename), HttpStatus.OK);
    }


    /**
     * Export the given policies as xacml(-looking)
     * 
     * @throws ConsoleException
     * @throws ServerException
     * 
     */
    @ApiIgnore
    @PostMapping(value = "/generateXACML")
    public ConsoleResponseEntity<SimpleResponseDTO<String>> policyXacmlExport(@RequestBody
    List<ExportEntityDTO> exportEntityDTOS, HttpServletResponse response)
            throws ConsoleException, ServerException {
        log.debug("Policy XACML generation request came, Policy ids :{}", exportEntityDTOS);

        String responseMessage;
        String filename = "";
        try {
            responseMessage = "success.file.export";
            filename = policyPortingService.generateXACML(exportEntityDTOS);
            response.setHeader("Content-Disposition", "attachment");
            log.info("Policy details exported successfully, [Filename = {}]", filename);
        } catch (InvalidPolicyPortingRequestException e) {
            responseMessage = e.getStatusMsg();
        }
        
        return ConsoleResponseEntity
                .get(SimpleResponseDTO.createWithType(msgBundle.getCode(responseMessage),
                        msgBundle.getText(responseMessage), filename), HttpStatus.OK);
    }

    private List<PolicyLite> getAllPolicyLite() throws ConsoleException {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setPageNo(0);
        criteria.setPageSize(10000);
        Page<PolicyLite> policyLitePage = policySearchService
                .findPolicyByCriteria(criteria);

        return accessControlService.enforceTBAConPolicies(policyLitePage.getContent());
    }

    private ResponseEntity<ByteArrayResource> getFileDownloadResponse(byte[] exportedContent, String fileName) {
        ByteArrayResource resource = new ByteArrayResource(exportedContent);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(resource);
    }

    private ResponseEntity<ByteArrayResource> handleError(String exportMode){
        String response = String.format("Error: %s %s", msgBundle.getText("invalid.policy.export.mode.code"),
                msgBundle.getText("invalid.policy.export.mode", exportMode));
        return ResponseEntity.badRequest()
                .body(new ByteArrayResource(response.getBytes()));
    }

    @GetMapping(value = "/retrieveAllPolicies")
    @ApiOperation(value = "Export all policies.",
            notes = "Returns the actual contents of exported policies, after policies are successfully exported." +
                    " The selected export mode must be allowed in the Control Centre configuration.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "exportMode", value = "Export Mode", dataType = "string", paramType = "query",
                    allowableValues = "PLAIN, SANDE")
    })
    @ApiResponses({@ApiResponse(code = 200, message = "", response = PolicyPortingDTO.class)})
    public ResponseEntity<ByteArrayResource> retrieveAllPolicies(
            @RequestParam(defaultValue = "PLAIN", required = false) String exportMode)
            throws ConsoleException, IOException {
        log.debug("Request came to retrieve all policies export");
        byte [] exportedContent;
        try {
            exportedContent = policyPortingService.validateAndExport(getAllPolicyLite(), exportMode);
        } catch (InvalidPolicyPortingRequestException e) {
            log.error("Exception while retrieving all policies export {}", e);
            return handleError(exportMode);
        }

        String fileName = "Policy Export." + System.currentTimeMillis() + policyPortingService.getFileExtension(exportMode);
        log.info("All Policy details exported successfully, [Filename = {}]",
                fileName);
        return getFileDownloadResponse(exportedContent, fileName);
    }
    
    /**
     * <p>
     * Get policy export options
     * </p>
     *  
     * @return
     * @throws ConsoleException
     */
    @GetMapping(value = "/exportOptions")
    @ApiOperation(value = "Get policy export options.",
            notes = "Returns the available export options. Export options can be configured using the Control Centre" +
                    " console.")
    @ApiResponses({@ApiResponse(code = 200, message = "Data loaded successfully", response = ResponseDTO.class)})
    public ConsoleResponseEntity<ResponseDTO> getPolicyExportOptions() throws ConsoleException {
    	PolicyExportOptionsDTO exportOptionsDTO = policyPortingService.getExportOptions();    	
    	ResponseDTO responseDTO = SimpleResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"), exportOptionsDTO);    	 
    	return ConsoleResponseEntity.get(responseDTO, HttpStatus.OK);
    }

    @Override
    protected Logger getLog() {
        return log;
    }

}
