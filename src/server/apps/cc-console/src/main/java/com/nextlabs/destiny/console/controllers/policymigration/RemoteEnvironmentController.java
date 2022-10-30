package com.nextlabs.destiny.console.controllers.policymigration;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.policyworkflow.RemoteEnvironmentDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.ServerException;
import com.nextlabs.destiny.console.model.remoteenv.RemoteEnvironment;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.policymigration.RemoteEnvironmentService;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 *
 * REST Controller for Remote environment functions
 *
 * @author Mohammed Sainal Shah
 * @since 2020.08
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("remoteEnvironment/mgmt")
@Api(tags = {"Remote Environment Controller"})
@SwaggerDefinition(tags = { @Tag(name = "Remote Environment Controller", description = "REST APIs related to manage remote environments") })
public class RemoteEnvironmentController {

    private final RemoteEnvironmentService remoteEnvironmentService;

    private final MessageBundleService msgBundle;

    public RemoteEnvironmentController(RemoteEnvironmentService remoteEnvironmentService,
                                       MessageBundleService msgBundle) {
        this.remoteEnvironmentService = remoteEnvironmentService;
        this.msgBundle = msgBundle;
    }

    /**
     * Saves new Remote environment
     *
     * @param remoteEnvironmentDTO {@link RemoteEnvironmentDTO}
     * @return {@link SimpleResponseDTO}
     */
    @SuppressWarnings("rawtypes")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "add")
    @ApiOperation(value = "Creates and adds a new remote environment.",
            notes="Returns a success message along with the new remote environment's Id, when the remote environment has been successfully saved.")
    public ConsoleResponseEntity<SimpleResponseDTO> addRemoteEnvironment(
            @RequestBody RemoteEnvironmentDTO remoteEnvironmentDTO) throws ConsoleException {

        RemoteEnvironment remoteEnvironment = remoteEnvironmentService.create(remoteEnvironmentDTO);
        SimpleResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.code"),
                msgBundle.getText("success.data.saved"), remoteEnvironment.getId());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Modifies a Remote environment
     *
     * @param remoteEnvironmentDTO {@link RemoteEnvironmentDTO}
     * @return {@link SimpleResponseDTO}
     */
    @SuppressWarnings("rawtypes")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "modify")
    @ApiOperation(value = "Modifies an existing remote environment.",
            notes="Returns a success message along with the remote environment's Id, when the remote environment has been successfully saved.")
    public ConsoleResponseEntity<SimpleResponseDTO> modifyRemoteEnvironment(
            @RequestBody RemoteEnvironmentDTO remoteEnvironmentDTO) throws ConsoleException {

        RemoteEnvironment remoteEnvironment = remoteEnvironmentService.modify(remoteEnvironmentDTO);
        SimpleResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.code"),
                msgBundle.getText("success.data.saved"), remoteEnvironment.getId());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Deletes a Remote environment
     *
     * @param id {@link Long}
     * @return {@link SimpleResponseDTO}
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "remove/{id}")
    @ApiOperation(value = "Delete an existing remote environment.")
    @ApiResponses({@ApiResponse(code = 200, message = "Data deleted successfully", response = ResponseDTO.class)})
    public ConsoleResponseEntity<ResponseDTO> removeById(
            @ApiParam(value = "The id of the policy to be removed", required = true)
            @PathVariable("id") Long id) throws ConsoleException {

        remoteEnvironmentService.delete(Collections.singletonList(id));
        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.code"),
                msgBundle.getText("success.data.deleted"));
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Bulk deletes the list of Remote environments
     *
     * @param @RequestBody List<Long> ids {@link Long}
     * @return {@link SimpleResponseDTO}
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/bulkDelete")
    @ApiOperation(value = "Delete an existing remote environment.")
    @ApiResponses({@ApiResponse(code = 200, message = "Data deleted successfully", response = ResponseDTO.class)})
    public ConsoleResponseEntity<ResponseDTO> removeById(@RequestBody List<Long> ids) throws ConsoleException {

        remoteEnvironmentService.delete(ids);
        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.code"),
                msgBundle.getText("success.data.deleted"));
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Saves new Remote environment
     *
     * @param remoteEnvironmentDTO {@link RemoteEnvironmentDTO}
     * @return {@link SimpleResponseDTO}
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "validateConnection")
    @ApiOperation(value = "Validates connection to a remote environment.",
            notes="Returns a success message if the connection to remote host is successful.")
    public ConsoleResponseEntity<ResponseDTO> validateConnection(
            @RequestBody RemoteEnvironmentDTO remoteEnvironmentDTO) throws ServerException, ConsoleException {
        ResponseDTO response;
        remoteEnvironmentService.validateConnection(remoteEnvironmentDTO);
        response = SimpleResponseDTO.create(
                msgBundle.getText("success.code"),
                msgBundle.getText("success.remote.host.verify"));
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Returns active remote environment
     *
     * @return {@link SimpleResponseDTO}
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "active/{id}")
    @ApiOperation(value = "Returns the details of an active remote environment, given it's id.",
            notes="Given the remote environment's id, this API returns the details of the remote environment if it is not marked as deleted.")
    public ConsoleResponseEntity<SimpleResponseDTO<RemoteEnvironmentDTO>> activeRemoteEnvById(@PathVariable("id") Long id) throws ConsoleException {

        RemoteEnvironmentDTO remoteEnvironmentDTO = remoteEnvironmentService.findActiveById(id);
        if (remoteEnvironmentDTO == null) {
            throw new ConsoleException(msgBundle.getText("no.data.found"));
        }
        SimpleResponseDTO<RemoteEnvironmentDTO> response = SimpleResponseDTO.createWithType(
                msgBundle.getText("success.code"),
                msgBundle.getText("success.data.loaded"), remoteEnvironmentDTO);
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Returns a remote environment
     *
     * @return {@link SimpleResponseDTO}
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "{id}")
    @ApiOperation(value = "Returns the details of a remote environment, given it's id.",
            notes="Given the remote environment's id, this API returns the details of the remote environment.")
    public ConsoleResponseEntity<SimpleResponseDTO<RemoteEnvironmentDTO>> remoteEnvById(@PathVariable("id") Long id) throws ConsoleException {

        RemoteEnvironmentDTO remoteEnvironmentDTO = remoteEnvironmentService.findById(id);
        if (remoteEnvironmentDTO == null) {
            throw new ConsoleException(msgBundle.getText("no.data.found"));
        }
        SimpleResponseDTO<RemoteEnvironmentDTO> response = SimpleResponseDTO.createWithType(
                msgBundle.getText("success.code"),
                msgBundle.getText("success.data.loaded"), remoteEnvironmentDTO);
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }
}
