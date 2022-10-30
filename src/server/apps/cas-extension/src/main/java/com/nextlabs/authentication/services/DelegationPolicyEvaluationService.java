package com.nextlabs.authentication.services;

import java.util.Map;
import java.util.Set;

/**
 * Service interface for delegation policy evaluation service.
 *
 * @author Sachindra Dasun
 */
public interface DelegationPolicyEvaluationService {

    Set<String> getAllowedActions(Map<String, Set<String>> attributes);

}
