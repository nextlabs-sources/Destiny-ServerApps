package com.nextlabs.authentication.services;

import java.util.Map;
import java.util.Set;

import org.pac4j.core.profile.BasicUserProfile;

/**
 * Service interface for user permission service
 *
 * @author Sachindra Dasun
 */
public interface UserPermissionService {

    Set<String> getPermissions(Map<String, Set<String>> attributes);

}
