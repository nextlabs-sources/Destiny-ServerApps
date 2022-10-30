package com.nextlabs.destiny.console.controllers.policyworkflow;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.policymgmt.policyworkflow.WorkflowRequestCommentDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.ServerException;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.policyworkflow.PolicyWorkflowService;
import com.nextlabs.destiny.console.utils.ValidationUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * REST Controller for Policy Workflow Management
 *
 * @author Mohammed Sainal Shah
 * @since 2020.10
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("policyWorkflow")
@Api(tags = {"Policy Workflow Controller"})
@SwaggerDefinition(tags = { @Tag(name = "Policy Workflow Controller", description = "REST APIs to manage policy workflow") })
public class PolicyWorkflowController {

    private static final Logger logger = LoggerFactory.getLogger(PolicyWorkflowController.class);

    private PolicyWorkflowService policyWorkflowService;
    private final MessageBundleService msgBundle;
    protected ValidationUtils validations;

    @Autowired
    public PolicyWorkflowController(MessageBundleService msgBundle) {
        this.msgBundle = msgBundle;
    }

    /**
     * Submits a policy workflow for review
     *
     * @param id
     * @return {@link SimpleResponseDTO}
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "submit/{id}")
    @ApiOperation(value = "Creates a new policy workflow.")
    public ConsoleResponseEntity<ResponseDTO> submitWorkflow(
            @ApiParam(value = "The ID of the policy.", required = true) @PathVariable("id") Long id) throws ConsoleException, ServerException {

        logger.debug("Request came to add new Policy Workflow");
        policyWorkflowService.submitWorkflowNested(id);
        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"));

        logger.info("Policy saved successfully and workflow created. Response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Approves a policy workflow
     *
     * @return {@link SimpleResponseDTO}
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "approve/{id}")
    @ApiOperation(value = "Approves a pending policy workflow.")
    public ConsoleResponseEntity<ResponseDTO> approveWorkflow(
            @ApiParam(value = "The ID of the policy.", required = true) @PathVariable("id") Long id) throws ConsoleException, ServerException {

        logger.debug("Request came to approve a policy workflow");

        policyWorkflowService.approveWorkflowRequestLevel(id);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"));

        logger.info("Policy Workflow approved. Response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Sets the policy workflow to REQUESTED_AMENDMENT status
     *
     * @return {@link SimpleResponseDTO}
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "return/{id}")
    @ApiOperation(value = "Returns a pending policy workflow to the author.")
    public ConsoleResponseEntity<ResponseDTO> returnWorkflow(
            @ApiParam(value = "The ID of the policy.", required = true) @PathVariable("id") Long id) throws ConsoleException, ServerException {

        logger.debug("Request came to return a policy workflow");

        policyWorkflowService.returnWorkflowRequestLevel(id);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"));

        logger.info("Policy Workflow returned. Response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Add comment to a workflow request
     *
     * @param workflowRequestCommentDto {@link WorkflowRequestCommentDTO}
     * @return {@link SimpleResponseDTO}
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "addComment")
    @ApiOperation(value = "Add comment to a workflow request.")
    public ConsoleResponseEntity<ResponseDTO> addComment(@ApiParam(value = "The policy workflow comment.", required = true)
                                                             @RequestBody WorkflowRequestCommentDTO workflowRequestCommentDto)
            throws ConsoleException, ServerException {

        logger.debug("Request came to approve a policy workflow");

        policyWorkflowService.addComment(workflowRequestCommentDto);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), workflowRequestCommentDto.getId());

        logger.info("Policy Workflow comment added. Response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @Autowired
    public void setValidations(ValidationUtils validations) {
        this.validations = validations;
    }

    @Autowired
    public void setPolicyWorkflowService(PolicyWorkflowService policyWorkflowService) {
        this.policyWorkflowService = policyWorkflowService;
    }
}
