package com.nextlabs.destiny.console.services.policymigration;

import com.nextlabs.destiny.console.dto.policyworkflow.RemoteEnvironmentDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.ServerException;
import com.nextlabs.destiny.console.model.remoteenv.RemoteEnvironment;

import java.util.List;

/**
 *
 * Service to manage the remote workflow environments
 *
 * @author Mohammed Sainal Shah
 * @since 2020.08
 *
 */
public interface RemoteEnvironmentService {

    /**
     * Returns the remote environment with the referenced id
     *
     * @return {@link RemoteEnvironmentDTO}
     */
    RemoteEnvironmentDTO findById(Long id);

    /**
     * Returns the active remote environment with the referenced id
     *
     * @return {@link List<RemoteEnvironmentDTO>}
     */
    RemoteEnvironmentDTO findActiveById(Long id);

    /**
     * Tests connection to the remote environment
     *
     * @param remoteEnvironmentDTO{@link
     *            RemoteEnvironmentDTO}
     */
    void validateConnection(RemoteEnvironmentDTO remoteEnvironmentDTO) throws ConsoleException, ServerException;

    /**
     * Saves a new remote workflow environment
     *
     * @param remoteEnvironmentDTO{@link
     *            RemoteEnvironmentDTO}
     * @return {@link RemoteEnvironment}
     */
    RemoteEnvironment create(RemoteEnvironmentDTO remoteEnvironmentDTO) throws ConsoleException;

    /**
     * Modifies an existing remote workflow environment
     *
     * @param remoteEnvironmentDTO{@link
     *            RemoteEnvironmentDTO}
     * @return {@link RemoteEnvironment}
     * @throws {@link ConsoleException}
     */
    RemoteEnvironment modify(RemoteEnvironmentDTO remoteEnvironmentDTO) throws ConsoleException;

    /**
     * Deletes remote workflow environment by id
     *
     * @param ids {@link Long}
     */
    void delete(List<Long> ids) throws ConsoleException;
}
