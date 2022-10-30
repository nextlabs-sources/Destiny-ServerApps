package com.nextlabs.destiny.console.services.policymigration;

import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.policymgmt.RemoteEnvironmentLite;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.remoteenv.RemoteEnvironment;
import org.springframework.data.domain.Page;

/**
 *
 * Remote Environment Search Service interface
 *
 * @author Mohammed Sainal Shah
 * @since 2020.08
 *
 */
public interface RemoteEnvironmentSearchService {

    /**
     * Find all the remote environments
     *
     * @throws ConsoleException
     */
    Page<RemoteEnvironmentLite> findAllRemoteEnvironments();

    /**
     * Re-Index all the policies
     *
     * @throws ConsoleException
     */
    void reIndexAllRemoteEnvironments() throws ConsoleException;

    /**
     * Find remote environments
     *
     * @throws ConsoleException
     */
    Page<RemoteEnvironmentLite> findRemoteEnvironmentByCriteria(SearchCriteria criteria) throws ConsoleException;

    /**
     * @param entity
     * @throws ConsoleException
     */
    void reIndexRemoteEnvironment(RemoteEnvironment entity) throws ConsoleException;
}
