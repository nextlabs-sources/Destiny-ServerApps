package com.nextlabs.destiny.console.controllers;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.CollectionDataResponseDTO;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.plugin.PDPPluginDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.InvalidInputParamException;
import com.nextlabs.destiny.console.exceptions.NotUniqueException;
import com.nextlabs.destiny.console.exceptions.ServerException;
import com.nextlabs.destiny.console.model.PDPPlugin;
import com.nextlabs.destiny.console.model.PDPPluginFile;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.PDPPluginService;
import com.nextlabs.destiny.console.utils.ValidationUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

/**
 *
 * REST Controller for PIP plugin management
 *
 * @author Chok Shah Neng
 * @since 2020.12
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("pdpPlugin")
@Api(tags = {"PDP Plugin Controller"})
@SwaggerDefinition(tags = { @Tag(name = "PDP Plugin Controller", description = "REST APIs to manage PDP plugins") })
public class PDPPluginController {

    private static final Logger logger = LoggerFactory.getLogger(PDPPluginController.class);

    @Autowired
    private ValidationUtils validations;

    @Autowired
    private MessageBundleService msgBundle;

    @Autowired
    private PDPPluginService pluginService;

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "Creates a new PDP plugin.")
    public ConsoleResponseEntity add(
                    @RequestPart("config") PDPPluginDTO pluginDto,
                    @RequestPart("mainJar") MultipartFile mainJar,
                    @RequestPart("properties") MultipartFile properties,
                    @RequestPart(value = "externalLibs", required = false) List<MultipartFile> externalLibs,
                    @RequestPart(value = "externalFiles", required = false) List<MultipartFile> externalFiles)
                    throws ConsoleException, ServerException {
        logger.debug("Request came to add new plugin");
        validations.assertNotBlank(pluginDto.getName(), "name");
        validations.assertNotNull(properties, "properties file");
        if(mainJar == null
            || mainJar.isEmpty()) {
            throw new InvalidInputParamException(
                            msgBundle.getText("invalid.input.file.empty.code"),
                            msgBundle.getText("invalid.input.file.empty", "main jar"));
        }
        if(properties == null
            || properties.isEmpty()) {
            throw new InvalidInputParamException(
                            msgBundle.getText("invalid.input.file.empty.code"),
                            msgBundle.getText("invalid.input.file.empty", "properties file"));
        }

        pluginDto.setMainJar(mainJar);
        pluginDto.setProperties(properties);
        if(externalLibs != null) {
            for(MultipartFile externalLib : externalLibs) {
                pluginDto.getExternalJars().add(externalLib);
            }
        }
        if(externalFiles != null) {
            for(MultipartFile externalFile : externalFiles) {
                pluginDto.getExternalFiles().add(externalFile);
            }
        }

        SimpleResponseDTO response;
        try {
            PDPPlugin plugin = pluginService.save(pluginDto);
            response = SimpleResponseDTO.create(msgBundle.getText("success.code"),
                            msgBundle.getText("success.data.saved"), plugin.getId());
        } catch(NotUniqueException err) {
            response = SimpleResponseDTO.create(err.getStatusCode(),
                            err.getStatusMsg(), null);
        }

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "list")
    @ApiOperation(value = "Returns the list of all PDP plugins.",
                    notes="This API returns all of the PDP plugins configured.")
    public ConsoleResponseEntity<CollectionDataResponseDTO> list()
                    throws ConsoleException, ServerException {
        logger.debug("Request came to plugins search");
        List<PDPPluginDTO> pluginDTOs = pluginService.findAll();

        CollectionDataResponseDTO response = CollectionDataResponseDTO
                        .create(msgBundle.getText("success.data.found.code"),
                                        msgBundle.getText("success.data.found"));
        response.setData(pluginDTOs);
        response.setPageSize(pluginDTOs.size());
        response.setPageNo(1);
        response.setTotalPages(1);
        response.setTotalNoOfRecords(pluginDTOs.size());

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "{id}")
    @ApiOperation(value = "Returns the details of a PDP plugin, given it's id.",
                    notes="Given the PDP plugin's id, this API returns the details of the PDP plugin.")
    public ConsoleResponseEntity<SimpleResponseDTO<PDPPluginDTO>> findById(@PathVariable("id") Long id)
                    throws ConsoleException, ServerException {
        logger.debug("Request came to find plugin by id");
        PDPPluginDTO pluginDTO = pluginService.findById(id);

        if (pluginDTO == null) {
            throw new ConsoleException(msgBundle.getText("no.data.found"));
        }

        SimpleResponseDTO<PDPPluginDTO> response = SimpleResponseDTO.createWithType(
                        msgBundle.getText("success.code"),
                        msgBundle.getText("success.data.loaded"), pluginDTO);

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "modifyWithAttachment", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "Modifies an existing PDP plugin.",
                    notes="Returns a success message along with the PDP plugin's Id, when the plugin has been successfully saved.")
    public ConsoleResponseEntity<SimpleResponseDTO> modifyWithAttachment(
                    @RequestPart("config") PDPPluginDTO pluginDto,
                    @RequestPart(value = "mainJar", required = false) MultipartFile mainJar,
                    @RequestPart(value = "properties", required = false) MultipartFile properties,
                    @RequestPart(value = "externalLibs", required = false) List<MultipartFile> externalLibs,
                    @RequestPart(value = "externalFiles", required = false) List<MultipartFile> externalFiles)
                    throws ServerException {
        logger.debug("Request came to modify a plugin");
        validations.assertNotZero(pluginDto.getId(), "id");
        validations.assertNotBlank(pluginDto.getName(), "name");

        if(mainJar != null
                        && !mainJar.isEmpty()) {
            pluginDto.setMainJar(mainJar);
        }

        if(properties != null
                        && !properties.isEmpty()) {
            pluginDto.setProperties(properties);
        }

        if(externalLibs != null
                        && !externalLibs.isEmpty()) {
            for(MultipartFile externalLib : externalLibs) {
                pluginDto.getExternalJars().add(externalLib);
            }
        }

        if(externalFiles != null
                && !externalFiles.isEmpty()) {
            for(MultipartFile externalFile : externalFiles) {
                pluginDto.getExternalFiles().add(externalFile);
            }
        }

        SimpleResponseDTO response;

        try {
            PDPPlugin plugin = pluginService.modify(pluginDto);
            response = SimpleResponseDTO.create(msgBundle.getText("success.code"),
                            msgBundle.getText("success.data.saved"), plugin.getId());
        } catch(NotUniqueException err) {
            response = SimpleResponseDTO.create(err.getStatusCode(),
                            err.getStatusMsg(), null);
        }

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "modify")
    @ApiOperation(value = "Modifies an existing PDP plugin.",
                    notes="Returns a success message along with the PDP plugin's Id, when the plugin has been successfully saved.")
    public ConsoleResponseEntity<SimpleResponseDTO> modify(
                    @RequestBody PDPPluginDTO pluginDto) throws ServerException {
        logger.debug("Request came to modify a plugin");
        validations.assertNotZero(pluginDto.getId(), "id");
        validations.assertNotBlank(pluginDto.getName(), "name");

        SimpleResponseDTO response;

        try {
            PDPPlugin plugin = pluginService.modify(pluginDto);
            response = SimpleResponseDTO.create(msgBundle.getText("success.code"),
                            msgBundle.getText("success.data.saved"), plugin.getId());
        } catch(NotUniqueException err) {
            response = SimpleResponseDTO.create(err.getStatusCode(),
                            err.getStatusMsg(), null);
        }

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "delete/{id}")
    @ApiOperation(value = "Delete an existing PDP plugin.")
    @ApiResponses({@ApiResponse(code = 200, message = "Data deleted successfully", response = ResponseDTO.class)})
    public ConsoleResponseEntity<ResponseDTO> delete(
                    @ApiParam(value = "The id of the plugin to be removed", required = true)
                    @PathVariable("id") Long id)
                    throws ConsoleException, ServerException {
        logger.debug("Request came to remove a plugin by id");
        validations.assertNotZero(id, "id");
        pluginService.delete(Collections.singletonList(id));

        ResponseDTO response = SimpleResponseDTO.create(
                        msgBundle.getText("success.code"),
                        msgBundle.getText("success.data.deleted"));

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "bulkDelete")
    @ApiOperation(value = "Delete collection of PDP plugin.")
    @ApiResponses({@ApiResponse(code = 200, message = "Data deleted successfully", response = ResponseDTO.class)})
    public ConsoleResponseEntity<ResponseDTO> bulkDelete(@RequestBody List<Long> ids)
                    throws ConsoleException, ServerException {
        logger.debug("Request came to remove multiple plugins by id");
        pluginService.delete(ids);

        ResponseDTO response = SimpleResponseDTO.create(
                        msgBundle.getText("success.code"),
                        msgBundle.getText("success.data.deleted"));
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "deploy/{id}")
    @ApiOperation(value = "Deploy an existing PDP plugin.",
                    notes="Returns a success message when the plugin has been successfully deployed.")
    public ConsoleResponseEntity<ResponseDTO> deploy(
                    @ApiParam(value = "The id of the plugin to be deployed", required = true)
                    @PathVariable("id") Long id) throws ConsoleException, ServerException {
        logger.debug("Request came to deploy a plugin");
        pluginService.deploy(Collections.singletonList(id));

        ResponseDTO response = SimpleResponseDTO.create(
                        msgBundle.getText("success.code"),
                        msgBundle.getText("success.data.deployed"));

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "bulkDeploy")
    @ApiOperation(value = "Deploy collection of PDP plugin.")
    @ApiResponses({@ApiResponse(code = 200, message = "Data deployed successfully", response = ResponseDTO.class)})
    public ConsoleResponseEntity<ResponseDTO> bulkDeploy(@RequestBody List<Long> ids)
                    throws ConsoleException, ServerException {
        logger.debug("Request came to deploy multiple plugins by id");
        pluginService.deploy(ids);

        ResponseDTO response = SimpleResponseDTO.create(
                        msgBundle.getText("success.code"),
                        msgBundle.getText("success.data.deployed"));

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "deactivate/{id}")
    @ApiOperation(value = "Deactivate an existing PDP plugin.",
                    notes="Returns a success message when the plugin has been successfully deactivated.")
    public ConsoleResponseEntity<ResponseDTO> deactivate(
                    @ApiParam(value = "The id of the plugin to be deactivated", required = true)
                    @PathVariable("id") Long id) throws ConsoleException, ServerException {
        logger.debug("Request came to modify a plugin");
        pluginService.deactivate(Collections.singletonList(id));

        ResponseDTO response = SimpleResponseDTO.create(
                        msgBundle.getText("success.code"),
                        msgBundle.getText("success.data.undeployed"));

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "bulkDeactivate")
    @ApiOperation(value = "Deactivate collection of PDP plugin.")
    @ApiResponses({@ApiResponse(code = 200, message = "Data deactivated successfully", response = ResponseDTO.class)})
    public ConsoleResponseEntity<ResponseDTO> bulkDeactivate(@RequestBody List<Long> ids)
                    throws ConsoleException, ServerException {
        logger.debug("Request came to deactivate multiple plugins by id");
        pluginService.deactivate(ids);

        ResponseDTO response = SimpleResponseDTO.create(
                        msgBundle.getText("success.code"),
                        msgBundle.getText("success.data.undeployed"));

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "downloadFile/{pluginId}/{fileId}")
    @ApiOperation(value = "Download file of a configured PDP plugin.",
                    notes = "Return the actual contents of the file")
    public ResponseEntity<ByteArrayResource> downloadFile(
                    @ApiParam(value = "The id of the plugin where the file belongs to", required = true)
                    @PathVariable("pluginId") Long pluginId,
                    @ApiParam(value = "The id of the plugin file to be downloaded", required = true)
                    @PathVariable("fileId") Long fileId)
                    throws ConsoleException, ServerException {
        logger.debug("Request came to download plugin file by id");
        PDPPluginFile pluginFile = pluginService.getFile(pluginId, fileId);

        return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("application/octet-stream"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pluginFile.getName() + "\"")
                        .header("Cache-Control", "no-cache, no-store, must-revalidate")
                        .header("Pragma", "no-cache")
                        .header("Expires", "0")
                        .body(new ByteArrayResource(pluginFile.getContent()));
    }
}
