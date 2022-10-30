package com.nextlabs.destiny.console.services.policymigration;

import com.nextlabs.destiny.console.dto.policymgmt.porting.PolicyPortingDTO;
import com.nextlabs.destiny.console.dto.policyworkflow.MigrationExportRequestDTO;
import com.nextlabs.destiny.console.dto.policyworkflow.MigrationImportRequestDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.ServerException;

/**
 *
 * Service to manage Migration of entities.
 *
 * @author Mohammed Sainal Shah
 * @since 2020.08
 *
 */
public interface MigrationService {

    /**
     * Executes a migration export request
     *
     * @param exportRequestDTO{@link
     *            RemoteEnvironmentDTO}
     */
    void executeExport(MigrationExportRequestDTO exportRequestDTO) throws ConsoleException, ServerException;

    /**
     * Executes a import policy workflow
     *
     * @param migrationImportRequestDTO {@link
     *            MigrationImportRequestDTO}
     * @return PolicyPortingDTO
     */
    PolicyPortingDTO executeImport(MigrationImportRequestDTO migrationImportRequestDTO) throws ConsoleException;

}
