package com.nextlabs.destiny.console.services.authentication;

import com.nextlabs.destiny.console.dto.authentication.OidcJwtToken;
import com.nextlabs.destiny.console.dto.policyworkflow.RemoteEnvironmentDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.ServerException;

/**
 *
 * Service to manage OIDC authentication to remote CC hosts
 *
 * @author Mohammed Sainal Shah
 * @since 2020.08
 *
 */
public interface OidcAuthenticationClientService {

    /**
     * Get ID token for a target CC host
     *
     * @param targetEnvId{@link Long}
     * @return String
     */
    String getIdToken(long targetEnvId) throws ConsoleException, ServerException;

    /**
     * Get ID token for a remote environment
     *
     * @param remoteEnvironmentDTO{@link RemoteEnvironmentDTO}
     * @return String
     */
    OidcJwtToken authenticate(RemoteEnvironmentDTO remoteEnvironmentDTO) throws ServerException;

    /**
     * Upon changes of environment setting, remove the environment token id from caching if any
     * @param targetEnvId{@link Long} The environment configuration id
     */
    void removeEnvironmentIdToken(long targetEnvId);
}
