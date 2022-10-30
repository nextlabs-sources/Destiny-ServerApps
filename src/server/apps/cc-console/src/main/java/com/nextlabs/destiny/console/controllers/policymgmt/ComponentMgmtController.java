/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 3, 2016
 *
 */
package com.nextlabs.destiny.console.controllers.policymgmt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import com.nextlabs.destiny.console.dto.common.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

import com.bluejungle.pf.destiny.services.PolicyEditorException;
import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.controllers.AbstractRestController;
import com.nextlabs.destiny.console.delegadmin.actions.DelegationModelActions;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDeploymentHistoryDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentLite;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentPreviewDTO;
import com.nextlabs.destiny.console.dto.policymgmt.DeploymentDependency;
import com.nextlabs.destiny.console.dto.policymgmt.DeploymentRequestDTO;
import com.nextlabs.destiny.console.dto.policymgmt.DeploymentResponseDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDeploymentHistoryDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ValidationDetailDTO;
import com.nextlabs.destiny.console.enums.ActionType;
import com.nextlabs.destiny.console.enums.AuthorizableType;
import com.nextlabs.destiny.console.enums.DelegationModelShortName;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus;
import com.nextlabs.destiny.console.exceptions.CircularReferenceException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.ForbiddenException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentEntity;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.search.repositories.ComponentSearchRepository;
import com.nextlabs.destiny.console.services.policy.ComponentMgmtService;
import com.nextlabs.destiny.console.services.policy.ValidatorService;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 *
 * REST Controller for Component management functions
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("/component/mgmt")
@Api(tags = {"Component Management Controller"})
public class ComponentMgmtController extends AbstractRestController {

    private static final Logger log = LoggerFactory
            .getLogger(ComponentMgmtController.class);

    @Autowired
    private ComponentMgmtService componentMgmtService;

    @Autowired
    private ValidatorService validatorService;
    
    @Resource
    private ComponentSearchRepository componentSearchRepository;

    @Resource
    private ApplicationUserSearchRepository appUserSearchRepository;

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/add")
    @ApiOperation(value = "Creates and adds a new component.",
    		notes="Returns a success message along with the new component's ID, when the component has been successfully saved.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data saved successfully")})
    public ConsoleResponseEntity<ResponseDTO> createComponent(
            @RequestBody ComponentDTO componentDTO) throws ConsoleException {

        log.debug("Request came to add new Component");
        String namePattern = msgBundle.getText("name.pattern");
        String nameExpectedFormatMsg = msgBundle.getText("name.pattern.format.message");

        validations.assertNotBlank(componentDTO.getName(), "Name");
        validations.assertNotBlank(componentDTO.getType(), "Type");
        validations.assertNotBlank(componentDTO.getStatus(), "Status");

        validations.assertMatches(componentDTO.getName(), Pattern.compile(namePattern),
                "Name", nameExpectedFormatMsg);
        componentDTO.setCategory(DevEntityType.COMPONENT);
        ResponseDTO response;
        
        try {
			componentMgmtService.save(componentDTO);
			response = SimpleResponseDTO.create(
	                msgBundle.getText("success.data.saved.code"),
	                msgBundle.getText("success.data.saved"), componentDTO.getId());
			log.info("New Component saved successfully and response sent");
		} catch (CircularReferenceException e) {
			response = ResponseDTO.create(msgBundle.getText(e.getStatusCode()), msgBundle.getText(e.getStatusMsg()));
			log.error("New Component could not be saved due to circular dependency.");
		}
        
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/modify")
    @ApiOperation(value="Modifies existing component.", 
    	notes="Returns a success message along with the component's ID, when the changes to the component have been successfully saved.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data saved successfully")})
    public ConsoleResponseEntity<ResponseDTO> modifyComponent(
            @RequestBody ComponentDTO componentDTO) throws ConsoleException {

        log.debug("Request came to modify Component");

        String namePattern = msgBundle.getText("name.pattern");
        String nameExpectedFormatMsg = msgBundle.getText("name.pattern.format.message");

        validations.assertNotBlank(componentDTO.getName(), "Name");
        validations.assertNotBlank(componentDTO.getType(), "Type");
        validations.assertNotBlank(componentDTO.getStatus(), "Status");

        validations.assertMatches(componentDTO.getName(), Pattern.compile(namePattern),
                "Name", nameExpectedFormatMsg);

        componentDTO.setCategory(DevEntityType.COMPONENT);
        ResponseDTO response;
        
        try {
			componentMgmtService.modify(componentDTO);
			response = SimpleResponseDTO.create(
	                msgBundle.getText("success.data.saved.code"),
	                msgBundle.getText("success.data.saved"), componentDTO.getId());
			log.info("Component modified successfully and response sent");
		} catch (CircularReferenceException e) {
			response = ResponseDTO.create(msgBundle.getText(e.getStatusCode()), msgBundle.getText(e.getStatusMsg()));
			log.error("Component could not be modified due to circular dependency.");
		}

        
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/addSubComponent")
    @ApiOperation(value = "Creates and adds a new sub component.",
            notes="Returns a success message along with the new component's ID, when the component has been successfully saved.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data saved successfully")})
    public ConsoleResponseEntity<ResponseDTO> addSubComponent(
            @RequestBody ComponentDTO componentDTO) throws ConsoleException {

        log.debug("Request came to modify Component");
        String namePattern = msgBundle.getText("name.pattern");
        String nameExpectedFormatMsg = msgBundle.getText("name.pattern.format.message");

        validations.assertNotBlank(componentDTO.getName(), "Name");
        validations.assertNotBlank(componentDTO.getType(), "Type");
        validations.assertNotBlank(componentDTO.getStatus(), "Status");

        validations.assertMatches(componentDTO.getName(), Pattern.compile(namePattern),
                "Name", nameExpectedFormatMsg);

        componentDTO.setCategory(DevEntityType.COMPONENT);
        ResponseDTO response;
        try {
			componentMgmtService.addSubComponent(componentDTO);
			response = SimpleResponseDTO.create(
	                msgBundle.getText("success.data.saved.code"),
	                msgBundle.getText("success.data.saved"), componentDTO.getId());
			log.info("Component modified successfully and response sent");
		} catch (CircularReferenceException e) {
			response = ResponseDTO.create(msgBundle.getText(e.getStatusCode()), msgBundle.getText(e.getStatusMsg()));
			log.error("Component could not be modified due to circular dependency.");
		}
      
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/updateStatus")
    public ConsoleResponseEntity<ResponseDTO> updateStatus(
            @RequestBody ComponentDTO componentDTO) throws ConsoleException {

        log.debug("Request came to update the status of the Component");

        validations.assertNotNull(componentDTO.getId(), "Id");
        validations.assertNotBlank(componentDTO.getType(), "Type");
        validations.assertNotBlank(componentDTO.getStatus(), "Status");

        PolicyDevelopmentStatus devStatus = PolicyDevelopmentStatus
                .get(componentDTO.getStatus());
        validations.assertNotNull(devStatus, "Status");

        ComponentDTO component = componentMgmtService
                .updateStatus(componentDTO.getId(), devStatus);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.modified.code"),
                msgBundle.getText("success.data.modified"), component.getId());

        log.info("Component status updated successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}")
    @ApiOperation(value="Fetches details of a Component.", 
			notes="Given the Component's ID, this API returns the details of the component.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data found successfully")})
    public ConsoleResponseEntity<SimpleResponseDTO<ComponentDTO>> componentById(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to find Component by Id");

        validations.assertNotNull(id, "Id");
        ComponentDTO componentDTO = componentMgmtService.findById(id);

        if (componentDTO == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        } else {
            ComponentLite lite = ComponentLite.getLite(componentDTO,
                    appUserSearchRepository);
            lite = componentMgmtService.enforceTBAC(lite);
            componentDTO.setAuthorities(lite.getAuthorities());
        }

        if (componentDTO.getAuthorities().isEmpty()
                || !componentDTO.getAuthorities()
                .contains(DelegationModelActions.getAuthority(DelegationModelActions.VIEW_COMPONENT))) {
            throw new ForbiddenException(ForbiddenException.UNAUTHORIZED_REQUEST,
                    DelegationModelShortName.COMPONENT_ACCESS_TAGS.toString(), ActionType.VIEW, AuthorizableType.COMPONENT);
        }

        SimpleResponseDTO<ComponentDTO> response = SimpleResponseDTO.createWithType(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), componentDTO);

        log.info("Requested Component details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/active/{id}")
    @ApiOperation(value = "Returns the details of an active component, given it's id.",
			notes="Given the component's ID, this API returns the details of the component if it is not marked as deleted.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data found successfully")})
    public ConsoleResponseEntity<SimpleResponseDTO<ComponentDTO>> activeComponentById(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to find Component by Id");

        validations.assertNotNull(id, "Id");
        ComponentDTO componentDTO = componentMgmtService.findActiveById(id);

        if (componentDTO == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        } else {
            ComponentLite lite = ComponentLite.getLite(componentDTO,
                    appUserSearchRepository);
            lite = componentMgmtService.enforceTBAC(lite);
            componentDTO.setAuthorities(lite.getAuthorities());
        }

        if (componentDTO.getAuthorities().isEmpty()
                || !componentDTO.getAuthorities()
                .contains(DelegationModelActions.getAuthority(DelegationModelActions.VIEW_COMPONENT))) {
            throw new ForbiddenException(ForbiddenException.UNAUTHORIZED_REQUEST,
                    DelegationModelShortName.COMPONENT_ACCESS_TAGS.toString(), ActionType.VIEW, AuthorizableType.COMPONENT);
        }

        SimpleResponseDTO<ComponentDTO> response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), componentDTO);

        log.info("Requested Component details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/remove/{id}")
    @ApiOperation(value = "Removes a Component.",
			notes="Given the component's ID, this API removes the component and returns a success message.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data deleted successfully")})
    public ConsoleResponseEntity<ResponseDTO> removeById(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to remove Component by Id");

        validations.assertNotNull(id, "Id");
        componentMgmtService.remove(id);

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.deleted.code"),
                msgBundle.getText("success.data.deleted"));

        log.info("Component removed and successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/bulkDelete")
    @ApiOperation(value = "Removes a list of Components.",
			notes="Given a list of component IDs, this API removes the components and returns a success message.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data deleted successfully")})
    public ConsoleResponseEntity<ResponseDTO> removeById(
            @RequestBody List<Long> ids) throws ConsoleException {

        log.debug("Request came to bulk delete Components");

        validations.assertNotNull(ids, "Ids");
        componentMgmtService.remove(ids);

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.deleted.code"),
                msgBundle.getText("success.data.deleted"));

        log.info("Components removed successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Duplicate(Clone) of Component
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

        log.debug("Request came to clone component by Id: {}", id);

        validations.assertNotNull(id, "Id");
        ComponentDTO component = componentMgmtService.clone(id);

        if (component == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        SimpleResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), component.getId());

        log.info("Component cloned successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/hierarchy/{id}")
    public ConsoleResponseEntity<ResponseDTO> componentHierarchy(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to find Component's hierarchy");

        validations.assertNotNull(id, "Id");
        ComponentLite lite = componentSearchRepository.findById(id)
                .orElseThrow(() -> new NoDataFoundException(
                        msgBundle.getText("no.data.found.code"),
                        msgBundle.getText("no.data.found")));
        // Remove unnecessary data
        lite.setPredicateData(null);
        lite = componentMgmtService.enforceTBAC(lite);

        if (lite.getAuthorities().isEmpty()
                || !lite.getAuthorities()
                .contains(DelegationModelActions.getAuthority(DelegationModelActions.VIEW_COMPONENT))) {
            throw new ForbiddenException(ForbiddenException.UNAUTHORIZED_REQUEST,
                    DelegationModelShortName.COMPONENT_ACCESS_TAGS.toString(), ActionType.VIEW, AuthorizableType.COMPONENT);
        }

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), lite);

        log.info("Requested Component details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/deploy")
    @ApiOperation(value = "Deploys a component.",
			notes="Given a list of components, this API will deploy all the components mentioned in the list.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data deployed successfully")})
    public ConsoleResponseEntity<CollectionDataResponseDTO<DeploymentResponseDTO>> componentDeploy(
            @RequestBody List<DeploymentRequestDTO> deploymentRequests) throws ConsoleException {

        log.debug("Request came to deploy the component/components");

        validations.assertNotNull(deploymentRequests, "Deployment Requests");
        validations.assertNotZero(Long.valueOf(deploymentRequests.size()), "Deployment Requests");

        List<DeploymentResponseDTO> deploymentResponses = componentMgmtService.deploy(deploymentRequests);

        CollectionDataResponseDTO<DeploymentResponseDTO> response = CollectionDataResponseDTO.createWithType(
                msgBundle.getText("success.data.deployed.code"),
                msgBundle.getText("success.data.deployed"));
        response.setData(deploymentResponses);

        response.setPageNo(1);
        response.setPageSize(deploymentResponses.size());
        response.setTotalPages(deploymentResponses.size());
        response.setTotalNoOfRecords(deploymentResponses.size());

        log.info("Requested component/components deployed and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/unDeploy")
    @ApiOperation(value = "Undeploys a list of components.",
            notes="This API undeploys a list of deployed components passed to it.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Data un-deployed successfully")})
    public ConsoleResponseEntity<ResponseDTO> componentUnDeploy(
            @RequestBody List<Long> ids) throws ConsoleException {

        log.debug("Request came to un-deploy the component/components");

        validations.assertNotNull(ids, "Ids");
        validations.assertNotZero(Long.valueOf(ids.size()), "Ids");

        componentMgmtService.unDeploy(ids);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.undeployed.code"),
                msgBundle.getText("success.data.undeployed"));

        log.info("Requested component/components undeployed and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Add and deploy component.
     *
     * @param componentDTO  component to add and deploy
     * @return  the response
     * @throws ConsoleException if and error occurred
     * @deprecated Instead of this method, the add and deploy methods should be used separately.
     */
    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/addAndDeploy")
    @ApiOperation(value = "Creates, adds and deploys a new component.",
		notes="Returns a success message along with the new component's details, when the component have been successfully saved and deployed.")
    @Deprecated
    public ConsoleResponseEntity<ResponseDTO> addAndDeploy(
            @RequestBody ComponentDTO componentDTO) throws ConsoleException {

        log.debug("Request came to add and deploy a component");
        String namePattern = msgBundle.getText("name.pattern");
        String nameExpectedFormatMsg = msgBundle.getText("name.pattern.format.message");

        ResponseDTO response;
        ValidationDetailDTO validationDTO = null;

        validations.assertNull(componentDTO.getId(), "Id");
        validations.assertNotBlank(componentDTO.getName(), "Name");
        validations.assertNotBlank(componentDTO.getStatus(), "Status");
        validations.assertMatches(componentDTO.getName(), Pattern.compile(namePattern),
                "Name", nameExpectedFormatMsg);

        if (!componentDTO.isSkipValidate()) {
            validationDTO = validatorService.validate(componentDTO);
        }

        if (validationDTO != null && validationDTO.isDeployable()
                || componentDTO.isSkipValidate()) {
            componentDTO.setCategory(DevEntityType.COMPONENT);
            try {
                DeploymentResponseDTO responseDTO = componentMgmtService.saveAndDeploy(componentDTO);
                response = SimpleResponseDTO.create(
                        msgBundle.getText("success.data.saved.code"),
                        msgBundle.getText("success.data.saved"), responseDTO);
			} catch (CircularReferenceException e) {
				response = ResponseDTO.create(msgBundle.getText(e.getStatusCode()), msgBundle.getText(e.getStatusMsg()));
			}
        } else {
            response = SimpleResponseDTO.create(
                    msgBundle.getText("success.data.validated.code"),
                    msgBundle.getText("success.data.validated"), validationDTO);
        }
        log.info("Component added, deployment triggered and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Modify and deploy component.
     *
     * @param componentDTO  component to modify and deploy
     * @return  the response
     * @throws ConsoleException if and error occurred
     * @deprecated Instead of this method, the modify and deploy methods should be used separately.
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/modifyAndDeploy")
    @ApiOperation(value = "Modifies and deploys a component.",
	notes="Given a component's details, this API will save the changes made to that component and also deploys it.")
    @Deprecated
    public ConsoleResponseEntity<ResponseDTO> modifyAndDeploy(
            @RequestBody ComponentDTO componentDTO) throws ConsoleException {

        log.debug("Request came to modify and deploy a component");
        String namePattern = msgBundle.getText("name.pattern");
        String nameExpectedFormatMsg = msgBundle.getText("name.pattern.format.message");

        ResponseDTO response;
        ValidationDetailDTO validationDTO = null;

        validations.assertNotNull(componentDTO.getId(), "Id");
        validations.assertNotBlank(componentDTO.getName(), "Name");
        validations.assertNotBlank(componentDTO.getStatus(), "Status");

        validations.assertMatches(componentDTO.getName(), Pattern.compile(namePattern),
                "Name", nameExpectedFormatMsg);

        if (!componentDTO.isSkipValidate()) {
            validationDTO = validatorService.validate(componentDTO);
        }

        if (validationDTO != null && validationDTO.isDeployable()
                || componentDTO.isSkipValidate()) {
            componentDTO.setCategory(DevEntityType.COMPONENT);
            try {
                DeploymentResponseDTO responseDTO = componentMgmtService.saveAndDeploy(componentDTO);
                response = SimpleResponseDTO.create(
                        msgBundle.getText("success.data.saved.code"),
                        msgBundle.getText("success.data.saved"), responseDTO);
			} catch (CircularReferenceException e) {
				response = ResponseDTO.create(msgBundle.getText(e.getStatusCode()), msgBundle.getText(e.getStatusMsg()));
			}
        } else {
            response = SimpleResponseDTO.create(
                    msgBundle.getText("success.data.validated.code"),
                    msgBundle.getText("success.data.validated"), validationDTO);
        }
        log.info("Component modified, deployment triggered and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/history/{id}")
    public ConsoleResponseEntity<CollectionDataResponseDTO> policyHistory(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to view the history of the component");

        validations.assertNotNull(id, "id");
        List<PolicyDeploymentEntity> deploymentDetails = componentMgmtService
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

        log.info("Requested component history details and response sent");
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

        ComponentDeploymentHistoryDTO revisionDTO = componentMgmtService
                .viewRevision(revisionId);
        revisionDTO.setRevision(revisionNo);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), revisionDTO);

        log.info("Requested component revision loaded and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/revert")
    public ConsoleResponseEntity<ResponseDTO> revert( @RequestBody Long revisionId)
            throws ConsoleException {

        log.debug(
                "Request came to revert current component,[Revision :{}]",
                 revisionId);

        validations.assertNotNull(revisionId, "Revision Id");
        validations.assertNotZero(revisionId, "Revision Id");

        ComponentDTO componentDto = componentMgmtService
                .revertToVersion(revisionId);

        if (componentDto == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), componentDto);

        log.info(
                "Requested component reverted to given revision and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/validate/{componentId}")
    public ConsoleResponseEntity<ResponseDTO> checkCanDeploy(
            @PathVariable("componentId") Long componentId)
            throws ConsoleException {

        log.debug("Request came to validate component,[ Component Id :{}]",
                componentId);

        validations.assertNotNull(componentId, "Component Id");

        ComponentDTO dto = componentMgmtService.findById(componentId);
        ValidationDetailDTO validationDTO = validatorService.validate(dto);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.validated.code"),
                msgBundle.getText("success.data.validated"), validationDTO);

        log.info("Requested component validated and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/findDependencies")
    public ConsoleResponseEntity<CollectionDataResponseDTO> findDependencies(@RequestBody List<Long> ids)
            throws ConsoleException {
        log.debug("Request received to find dependencies");

        validations.assertNotNull(ids, "Ids");

        Set<DeploymentDependency> dependencies = componentMgmtService.findDependencies(ids);

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

        log.debug("Request came to validate and deploy components");

        validations.assertNotNull(deploymentRequests, "Deployment Requests");
        validations.assertNotZero(Long.valueOf(deploymentRequests.size()), "Deployment Requests");

        List<ValidationDetailDTO> validateDTOList = componentMgmtService
                .validateAndDeploy(deploymentRequests);

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
                "Requested component/components validated/deployed and report sent");

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ApiIgnore
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/validateUndeploy")
    public ConsoleResponseEntity<CollectionDataResponseDTO> checkCanUndeploy(
            @RequestBody List<Long> ids) throws ConsoleException {

        log.debug("Request came to checkCanUndeploy component/components");

        validations.assertNotNull(ids, "Ids");
        validations.assertNotZero(Long.valueOf(ids.size()), "Ids");

        List<ValidationDetailDTO> validationDTOs = validatorService
                .checkForReferences(ids);

        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                msgBundle.getText("success.data.validated.code"),
                msgBundle.getText("success.data.validated"));
        response.setData(validationDTOs);

        response.setPageNo(1);
        response.setPageSize(validationDTOs.size());
        response.setTotalPages(validationDTOs.size());
        response.setTotalNoOfRecords(validationDTOs.size());

        log.info("Requested component validated and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ApiIgnore
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value = "/preview")
	public ConsoleResponseEntity<ResponseDTO> preview(@RequestBody ComponentDTO componentDTO) throws ConsoleException {

		log.debug("Request came to preview a component");
		ResponseDTO response = null;
		validations.assertNotNull(componentDTO, "componentDTO");
		ComponentPreviewDTO componentPreviewDTO;
		try {
			componentPreviewDTO = componentMgmtService.getComponentPreview(componentDTO);

		} catch (PolicyEditorException e) {
			log.error("Error occured while preview ", e);
			componentPreviewDTO = new ComponentPreviewDTO();
			componentPreviewDTO.setTotalEnrolledSubjects(0);
			componentPreviewDTO.setEnrolledSubjects(new ArrayList<>());
		}

		response = SimpleResponseDTO.create(msgBundle.getText("success.data.found.code"),
				msgBundle.getText("success.data.found"), componentPreviewDTO);

		log.debug("Component Preview fetched.");
		return ConsoleResponseEntity.get(response, HttpStatus.OK);
	}

    @Override
    protected Logger getLog() {
        return log;
    }

}
