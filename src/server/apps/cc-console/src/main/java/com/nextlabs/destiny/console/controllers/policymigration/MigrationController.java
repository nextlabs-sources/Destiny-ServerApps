package com.nextlabs.destiny.console.controllers.policymigration;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.policymgmt.porting.PolicyPortingDTO;
import com.nextlabs.destiny.console.dto.policyworkflow.MigrationExportRequestDTO;
import com.nextlabs.destiny.console.dto.policyworkflow.MigrationImportRequestDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.ServerException;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.policymigration.MigrationService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("migration")
@Api(tags = {"Migration Controller"})
@SwaggerDefinition(tags = { @Tag(name = "Remote Environment Controller", description = "REST APIs related to manage remote environments") })
public class MigrationController {

    private static final Logger logger = LoggerFactory.getLogger(MigrationController.class);

    private final MigrationService migrationService;

    private final MessageBundleService msgBundle;

    @Autowired
    public MigrationController(MigrationService policyWorkflowService, MessageBundleService msgBundle) {
        this.migrationService = policyWorkflowService;
        this.msgBundle = msgBundle;
    }

    /**
     * Executes a migration export request
     *
     * @param migrationExportRequestDTO {@link MigrationExportRequestDTO}
     * @return {@link SimpleResponseDTO}
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "export")
    @ApiOperation(value = "Executes a migration export request.")
    public ConsoleResponseEntity<ResponseDTO> executeExportWorkflow(
            @RequestBody MigrationExportRequestDTO migrationExportRequestDTO) throws ConsoleException, ServerException {

        migrationService.executeExport(migrationExportRequestDTO);
        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"));
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    /**
     * Executes a migration import request
     *
     * @param migrationImportRequestDTO {@link MigrationImportRequestDTO}
     * @return {@link SimpleResponseDTO}
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "import")
    @ApiOperation(value = "Executes a migration import request.")
    public ConsoleResponseEntity<SimpleResponseDTO<PolicyPortingDTO>> executeImportWorkflow(
            @RequestBody MigrationImportRequestDTO migrationImportRequestDTO) throws ConsoleException {

        logger.debug("Request came to import policy migration");
        PolicyPortingDTO policyPortingDTO = migrationService.executeImport(migrationImportRequestDTO);
        SimpleResponseDTO<PolicyPortingDTO> response = SimpleResponseDTO.createWithType(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), policyPortingDTO);

        logger.debug("Import of policy migration successful");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }
}
